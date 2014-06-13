package com.android.pc.ioc.internet;


import java.io.File;
import java.util.HashMap;

import com.android.pc.ioc.internet.FastHttp.Progress;

/*
 * Author: pan Email:gdpancheng@gmail.com
 * Created Date:2013-5-20
 * Copyright @ 2013 BU
 * Description: 类描述
 *
 * History:
 */
public class InternetConfig {

	final public static int request_post = 0;
	final public static int request_get = 1;
	final public static int request_file = 2;
	final public static int request_webserver = 3;
	final public static int request_form = 4;
	
	final public static int result_map = 0;
	final public static int result_entity = 1;
	final public static int result_String = 2;

	final public static String UA = "Mozilla/4.0 (compatible; MSIE 5.0; Windows XP; DigExt)";
	final public static String content_type_map = "application/x-www-form-urlencoded";
	final public static String content_type_json = "application/json;charset=utf-8";
	final public static String content_type_xml = "text/xml; charset=utf-8";

	/**
	 * 协议
	 */
	private String content_type_web = content_type_map;
	/**
	 * 判断是否是https
	 */
	private boolean isHttps = false;
	/**
	 * webservers 也就是.net服务器的接口 必须要设置这个
	 */
	private String method;
	/**
	 * 字符串的编码 以及服务器的编码
	 */
	private String charset;
	/**
	 * 定时请求的时间间隔
	 */
	private int time;
	/**
	 * 请求方式 post get webservers fom等
	 */
	private int request_type = request_post;
	/**
	 * .net服务器的接口 的参数 域名空间 默认为http://tempuri.org/
	 */
	private String name_space = "http://tempuri.org/";
	/**
	 * 链接超时
	 */
	private int timeout = 30000;
	/**
	 * 表单提交的时候 上传的文件集合
	 */
	private HashMap<String, File> files;
	/**
	 * 是否获取cookies
	 */
	private boolean isCookies = false;
	/**
	 * 有的接口 参数是放到http的请求头里面 则用这个
	 */
	private HashMap<String, Object> head;
	/**
	 * 这个用来标记请求 可以让多个请求公用同一个callback
	 */
	private int key;
	
	private long all_length = 0;
	/**
	 * 表单上传进度
	 */
	private Progress progress;
	/**
	 * 是否支持离线
	 */
	private boolean isSave = false;
	
	/**
	 * 缓存的时间(分钟)
	 */
	private int saveDate = -1;
	
	private static InternetConfig defaultConfig = new InternetConfig(){{
		setCharset("utf-8");
		setTime(30 * 1000);
		setRequest_type(request_post);
	}};

	public static InternetConfig defaultConfig() {
		return defaultConfig;
	}

	public boolean isCookies() {
		return isCookies;
	}

	public void setCookies(boolean isCookies) {
		this.isCookies = isCookies;
	}

	public HashMap<String, Object> getHead() {
		return head;
	}

	public void setHead(HashMap<String, Object> head) {
		this.head = head;
	}

	public String getCharset() {
		if (charset == null) {
			return defaultConfig().charset;
		}
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public int getTime() {
		if (time == 0) {
			return defaultConfig().time;
		}
		return time;
	}

	public void setTime(int time) {
		this.time = time * 1000;
	}

	public int getRequest_type() {
		return request_type;
	}

	public void setRequest_type(int request_type) {
		this.request_type = request_type;
	}

	public String getName_space() {
		return name_space;
	}

	public void setName_space(String name_space) {
		this.name_space = name_space;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getContent_type_web() {
		return content_type_web;
	}

	public void setContent_type_web(String content_type_web) {
		this.content_type_web = content_type_web;
	}

	@Override
	public String toString() {
		return "InternetConfig [content_type_web=" + content_type_web + ", isHttps=" + isHttps + ", method=" + method  + ", charset=" + charset + ", time=" + time  + ", request_type=" + request_type + ", name_space=" + name_space + ", timeout=" + timeout + ", files=" + files + ", isCookies=" + isCookies + ", key=" + key + "]";
	}

	public HashMap<String, File> getFiles() {
		return files;
	}

	public void setFiles(HashMap<String, File> files) {
		this.files = files;
	}

	public boolean isHttps() {
		return isHttps;
	}

	
	public void setHttps(boolean isHttps) {
		this.isHttps = isHttps;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public Progress getProgress() {
		return progress;
	}

	public void setProgress(Progress progress) {
		this.progress = progress;
	}

	public long getAll_length() {
		return all_length;
	}

	public void setAll_length(long all_length) {
		this.all_length = all_length;
	}

	public boolean isSave() {
		return isSave;
	}

	public void setSave(boolean isSave) {
		this.isSave = isSave;
	}

	public int getSaveDate() {
		return saveDate;
	}

	public void setSaveDate(int saveDate) {
		this.saveDate = saveDate;
	}
}
