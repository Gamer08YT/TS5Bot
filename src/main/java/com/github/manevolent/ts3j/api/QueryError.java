package com.github.manevolent.ts3j.api;

/*
 * #%L
 * TeamSpeak 3 Java API
 * %%
 * Copyright (C) 2014 Bert De Geyter
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

public class QueryError extends Wrapper {

	private static final int ERROR_ID_OK = 0;
	private static final int ERROR_ID_EMPTY_RESULT_SET = 1281;

	public QueryError(Map<String, String> map) {
		super(map);
	}

	public int getId() {
		return getInt("id");
	}

	public String getMessage() {
		return get("msg");
	}

	public String getExtraMessage() {
		return get("extra_msg");
	}

	public int getFailedPermissionId() {
		return getInt("failed_permid");
	}

	public boolean isSuccessful() {
		final int id = getId();
		return (id == ERROR_ID_OK || id == ERROR_ID_EMPTY_RESULT_SET);
	}
}
