package de.bytestore;

import com.github.manevolent.ts3j.api.ChannelInfo;
import com.github.manevolent.ts3j.protocol.NetworkPacket;
import com.github.manevolent.ts3j.protocol.Packet;
import com.github.manevolent.ts3j.protocol.socket.client.AbstractTeamspeakClientSocket;
import com.github.manevolent.ts3j.protocol.socket.client.LocalTeamspeakClientSocket;
import com.github.manevolent.ts3j.protocol.socket.client.TeamspeakClientSocket;

import javax.xml.crypto.Data;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class Proxy {
    static final int CONNECTION_TIMEOUT = 60000;
    static HashMap<InetSocketAddress, ChannelInfo> channelMap = new HashMap<InetSocketAddress, ChannelInfo>();
    static InetSocketAddress serverAddress = new InetSocketAddress("85.214.126.187", 9987);
    static int proxyPort = 9987;

    public static void main(String[] args) throws Exception {
        runServer();
    }

    public static void runServer() throws Exception {
        System.out.println("Listening on " + proxyPort + ", forwarding to " + serverAddress);

        ByteBuffer buff = ByteBuffer.allocate(500);
        Selector selector = Selector.open();
        DatagramChannel proxyChannel = addChannel(selector, proxyPort);
        long connectionTestTime = System.currentTimeMillis();

        while (true) {
            try {
                selector.selectNow();
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();

                    if (key.isReadable()) {
                        DatagramChannel currentChannel = (DatagramChannel) key.channel();
                        InetSocketAddress localAddress = (InetSocketAddress) currentChannel.socket().getLocalSocketAddress();
                        buff.clear();
                        InetSocketAddress remoteAddress = (InetSocketAddress) currentChannel.receive(buff);

                        buff.flip();

                        if (!fromServer(remoteAddress)) {
                            ChannelInfo info = channelMap.get(remoteAddress);
                            if (info == null) {

                                DatagramChannel tempChannel = addChannel(selector, serverAddress);
                                InetSocketAddress tempAddress = (InetSocketAddress) tempChannel.socket().getLocalSocketAddress();
                                info = new ChannelInfo(tempChannel, remoteAddress);
                                channelMap.put(remoteAddress, info);
                                System.out.println("Added key = " + remoteAddress.toString());
                                channelMap.put(tempAddress, info);
                                System.out.println("Added key = " + tempAddress.toString());
                            }

                            System.out.println(new String(buff.array(), buff.arrayOffset(), buff.remaining() + 1));
                            LocalTeamspeakClientSocket socketIO = new LocalTeamspeakClientSocket();

                            // Create new Datagram Packet from Buffer.
                            DatagramPacket packetIO = new DatagramPacket(buff.array(), buff.arrayOffset()+ 1, buff.remaining());
                            packetIO.setAddress(remoteAddress.getAddress());
                            packetIO.setPort(remoteAddress.getPort());


                            try {
                                // Parse Datagram Paket.
                                NetworkPacket networkIO = socketIO.readNetworkPacket(packetIO, 1000);

                                // Parse Packet from Network Packet.
                                Packet tsIO = socketIO.readPacket(networkIO);

                                System.out.println("Header:");
                                System.out.println(tsIO.getBody());
                            } catch (Exception exceptionIO) {
                                System.out.println(exceptionIO.getMessage());
                            }

                            info.rxTime = System.currentTimeMillis();
                            info.channel.send(buff, serverAddress);
                        } else {
                            ChannelInfo info = channelMap.get(localAddress);
                            if (info != null) {
                                proxyChannel.send(buff, info.remoteAddress);
                            }
                        }
                    }
                }

                //Test & remove old connections
                if (System.currentTimeMillis() - connectionTestTime >= CONNECTION_TIMEOUT) {
                    connectionTestTime = System.currentTimeMillis();
                    Iterator<Map.Entry<InetSocketAddress, ChannelInfo>> entryIterator = channelMap.entrySet().iterator();
                    while (entryIterator.hasNext()) {
                        Map.Entry<InetSocketAddress, ChannelInfo> entry = entryIterator.next();
                        InetSocketAddress address = entry.getKey();
                        ChannelInfo info = entry.getValue();
                        if (connectionTestTime - info.rxTime >= CONNECTION_TIMEOUT) {
                            info.channel.close();
                            entryIterator.remove();
                            System.out.println("Removed key = " + address.toString());
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static boolean fromServer(InetSocketAddress address) {
        return address.getAddress().equals(serverAddress.getAddress());
    }

    public static DatagramChannel addChannel(Selector selector, int bindPort) throws Exception {
        DatagramChannel channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.socket().bind(new InetSocketAddress(bindPort));
        channel.register(selector, SelectionKey.OP_READ);
        return channel;
    }

    public static DatagramChannel addChannel(Selector selector, InetSocketAddress address) throws Exception {
        DatagramChannel channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.connect(address);
        channel.register(selector, SelectionKey.OP_READ);
        return channel;
    }

    public static class ChannelInfo {
        public ChannelInfo() {
        }

        public ChannelInfo(DatagramChannel channel, InetSocketAddress remoteAddress) {
            this.channel = channel;
            this.localAddress = (InetSocketAddress) channel.socket().getLocalSocketAddress();
            this.remoteAddress = remoteAddress;
        }

        DatagramChannel channel;
        InetSocketAddress localAddress;
        InetSocketAddress remoteAddress;
        long rxTime;
    }
}
