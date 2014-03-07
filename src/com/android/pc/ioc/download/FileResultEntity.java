package com.android.pc.ioc.download;

import java.io.File;

/*
 * Author: pan Email:gdpancheng@gmail.com
 * Created Date:2014-2-17
 * Copyright @ 2014 BU
 * Description: 类描述
 *
 * History:
 */
public class FileResultEntity {

	public static final int status_loading = 0;
	public static final int status_start = 1;
	public static final int status_fail = 2;
	public static final int status_sucess = 3;

	private String url;
	private int progress;
	private int status;
	private File file;
	protected boolean range;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public boolean isRange() {
		return range;
	}

	public void setRange(boolean range) {
		this.range = range;
	}
	@Override
	public String toString() {
		return "FileResultEntity [url=" + url + ", progress=" + progress + ", status=" + status + "]";
	}
}
