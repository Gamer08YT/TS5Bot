package de.bytestore.commands;

import de.bytestore.core.CacheHandler;
import de.bytestore.lib.Bot.Events.RoomEvent;
import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.function.Consumer;

public class SearchCommand extends Command {

    @Override
    public void execute(RoomEvent eventIO, String[] argsIO) {
        if (argsIO.length > 1) {
            try {
                // StringBuilder for Name.
                StringBuilder nameIO = new StringBuilder();

                // Foreach Line in Args.
                for (String lineIO : argsIO) {
                    if (!lineIO.equals(argsIO[0])) {
                        // Append Spacer after Args.
                        nameIO.append(lineIO).append(" ");
                    }
                }

                // Send Debug Message.
                CacheHandler.clientIO.sendText(eventIO.getSender(), "Please wait ill search for " + nameIO.toString() + ", for you.", null);

                try (CloseableHttpClient httpIO = HttpClients.createDefault()) {
                    // Create new HttpGet Client.
                    HttpGet getIO = new HttpGet("https://community.teamspeak.com/search.json?q=" + URLEncoder.encode(nameIO.toString(), Charset.defaultCharset().toString()));

                    // Add Accept Header.
                    getIO.addHeader("accept", "application/json");
                    getIO.addHeader("discourse-logged-in", "false");
                    getIO.addHeader("discourse-present", "true");

                    // Create a custom response handler
                    ResponseHandler<String> handlerIO = responseIO -> {
                        int status = responseIO.getStatusLine().getStatusCode();

                        if (status >= 200 && status < 300) {
                            HttpEntity entity = responseIO.getEntity();
                            return entity != null ? EntityUtils.toString(entity) : null;
                        } else {
                            // Send Debug Message.
                            CacheHandler.clientIO.sendText(eventIO.getSender(), "Woom...\n The Forum blocked me with following Code " + status + " :sad:", null);

                            // Print Debug Message.
                            System.out.println("Got Code " + status + " from Webserver.");
                        }

                        return null;
                    };

                    // Get Body from Request.
                    String bodyIO = httpIO.execute(getIO, handlerIO);

                    // Check if Body starts and ends with JSON Identifier.
                    if (bodyIO.startsWith("{") && bodyIO.endsWith("}")) {
                        // Parse JSON of Forum Response.
                        JSONObject objectIO = new JSONObject(bodyIO);

                        // Check if Posts exits.
                        if (objectIO.has("posts")) {
                            // Store String Posts.
                            StringBuilder messageIO = new StringBuilder();

                            // Store posts of Search.
                            JSONArray postsIO = objectIO.getJSONArray("posts");

                            // Append Found Message.
                            messageIO.append("Look what i found in the Forum for you  :nerd_face:\n\n");


                            // Foreach post in Array.
                            postsIO.forEach(new Consumer<Object>() {
                                // Store Count for Posts.
                                int countIO = 0;

                                @Override
                                public void accept(Object objectIO) {
                                    if (countIO > CacheHandler.postsIO) {
                                        return;
                                    } else {
                                        if (objectIO instanceof JSONObject) {
                                            // Parse JSON object from Array.
                                            JSONObject postIO = (JSONObject) objectIO;

                                            // Check if Blurb and ID exits.
                                            if (postIO.has("blurb") && postIO.has("topic_id")) {
                                                // Send Forum Post.
                                                System.out.println("Sending message to Client.");
                                                messageIO.append("```" + postIO.getString("blurb") + "``` https://community.teamspeak.com/t/" + postIO.getInt("topic_id") + "\n\n");
                                            }
                                        }
                                    }

                                    countIO++;
                                }
                            });

                            // Send more Message.
                            messageIO.append("\nNot what you were :eyes: for? No problem in the forum are about " + (postsIO.length() - CacheHandler.postsIO) + " more posts with your search filter.");

                            // Send Message.
                            CacheHandler.clientIO.sendText(eventIO.getRoom_id(), messageIO.toString(), null);
                        }
                    }

                } catch (Exception exceptionIO) {
                    // Send Debug Message.
                    CacheHandler.clientIO.sendText(eventIO.getSender(), "Ouch...\n theres an Problem  " + exceptionIO.getMessage() + ", please try again later.", null);

                    // Print Debug Message.
                    System.out.println(exceptionIO.getMessage());
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else {
            try {
                CacheHandler.clientIO.sendText(eventIO.getRoom_id(), "You forgot your Question or Problem.", null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String usage() {
        return "search - Report a TeamSpeak Bug and ill search for you in the Forum.";
    }
}
