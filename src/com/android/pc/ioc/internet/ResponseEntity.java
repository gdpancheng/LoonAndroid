package com.android.pc.ioc.internet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ResponseEntity implements Serializable {

	private static final long serialVersionUID = 4691805279783501287L;
	private int status = -1;
	private String url;
	private String content;
	private Map<String, String> cookies;
	private Map<String, String> params;
	private int key;
	private InternetConfig config;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getUrl() {
		return url;
	}

	public ResponseEntity setUrl(String url) {
		this.url = url;
		return this;
	}

	public String getContentAsString() {
		return content;
	}

	public void setContent(String content,boolean isSave) {
		this.content = content;
		if (isSave) {
	        //缓存数据
			for (String key : params.keySet()) {
				url = url+key+params.get(key);
            }
			HttpCache.setUrlCache(content, url);
        }
	}

	public Map<String, String> getCookies() {
		return cookies;
	}

	public ResponseEntity setCookies(Map<String, String> cookies) {
		this.cookies = cookies;
		return this;
	}

	public ResponseEntity cookie(String name, String value) {
		if (cookies == null) {
			cookies = new HashMap<String, String>();
		}
		cookies.put(name, value);
		return this;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public String makeCookie() {
		if (cookies == null || cookies.size() == 0)
			return null;
		Iterator<String> iter = cookies.keySet().iterator();

		StringBuilder sb = new StringBuilder();

		while (iter.hasNext()) {
			String key = iter.next();
			String value = cookies.get(key);
			sb.append(key);
			sb.append("=");
			sb.append(value);
			if (iter.hasNext()) {
				sb.append("; ");
			}
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return "ResponseEntity [status=" + status + ", url=" + url + ", content=" + content + ", cookies=" + cookies + ", params=" + params + ", key=" + key + "]";
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public InternetConfig getConfig() {
		return config;
	}

	public void setConfig(InternetConfig config) {
		this.config = config;
	}
}
