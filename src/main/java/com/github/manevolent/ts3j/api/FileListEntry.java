package com.github.manevolent.ts3j.api;

/*
 * #%L
 * TeamSpeak 3 Java API
 * %%
 * Copyright (C) 2016 Bert De Geyter, Roger Baumgartner
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import java.util.Map;

public class FileListEntry extends FileInfo {

	public FileListEntry(Map<String, String> map) {
		super(map);
	}

	@Override
	public String getPath() {
		return getParentPath() + getName();
	}

	@Override
	public String getName() {
		return get("name");
	}

	@Override
	public String getParentPath() {
		return get("path");
	}

	@Override
	public long getFileSize() {
		// Present if still uploading and returns
		final long finishedSize = getLong("incompletesize");
		return (finishedSize > 0) ? finishedSize : super.getFileSize();
	}

	@Override
	public int getType() {
		return getInt("type");
	}

	@Override
	public boolean isFile() {
		return getType() == 1;
	}

	@Override
	public boolean isDirectory() {
		return getType() == 0;
	}

	/**
	 * Returns {@code true} if this file was actively being uploaded at the time
	 * this object was created. Note that this will return {@code false} if a
	 * client has paused an upload.
	 *
	 * @return whether this file is actively being uploaded
	 */
	public boolean isStillUploading() {
		return getLong("incompletesize") > 0;
	}

	/**
	 * If this file is still uploading, this method will return how many bytes have already
	 * been uploaded. Otherwise the normal file size is returned.
	 *
	 * @return how many bytes of this file have been uploaded
	 */
	public long getUploadedBytes() {
		return super.getFileSize();
	}
}
