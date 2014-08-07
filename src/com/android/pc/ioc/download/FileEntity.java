package com.android.pc.ioc.download;

import java.util.List;

import com.android.pc.ioc.app.Ioc;
import com.android.pc.ioc.db.annotation.Finder;
import com.android.pc.ioc.db.annotation.Transient;
import com.android.pc.ioc.db.sqlite.Selector;
import com.android.pc.ioc.db.sqlite.WhereBuilder;
import com.android.pc.ioc.update.NotificationHelper;

/*
 * Author: pan Email:gdpancheng@gmail.com
 * Created Date:2014-2-14
 * Copyright @ 2014 BU
 * Description: 类描述
 *
 * History:
 */
public class FileEntity {

	protected int id;
	/** 下载的url **/
	protected String url;
	/** 本地存储路径 **/
	protected String path;
	/** 文件长度 **/
	protected long length;
	/** 下载进程 **/
	protected int threads;
	/** 是否支持断点续传 **/
	protected boolean range;
	/** 是否成功 **/
	protected boolean isSucess;
	/** 如果长度不一样则重新下载 **/
	@Transient
	private boolean again;
	@Transient
	private boolean isUpdate;
	@Transient
	private NotfiEntity notfi;
	@Transient
	private long loadedLength;
	@Transient
	private String real_url;
	@Finder(valueColumn = "id", targetColumn = "ThreadId")
	private List<ThreadEntity> threadsEntities;
	@Transient
	private NotificationHelper helper;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public int getThreads() {
		return threads;
	}

	public void setThreads(int threads) {
		this.threads = threads;
	}

	public boolean isRange() {
		return range;
	}

	public void setRange(boolean range) {
		this.range = range;
	}

	public boolean isSucess() {
		return isSucess;
	}

	public void setSucess(boolean isSucess) {
		this.isSucess = isSucess;
	}

	public List<ThreadEntity> getThreadsEntities() {
		return threadsEntities;
	}

	public void setThreadsEntities(List<ThreadEntity> threadsEntities) {
		this.threadsEntities = threadsEntities;
	}

	public boolean isAgain() {
		return again;
	}

	public void setAgain(boolean again) {
		this.again = again;
	}

	@Override
    public String toString() {
	    return "FileEntity [id=" + id + ", url=" + url + ", path=" + path + ", length=" + length + ", threads=" + threads + ", range=" + range + ", isSucess=" + isSucess + ", again=" + again + ", isUpdate=" + isUpdate + ", notfi=" + notfi + ", loadedLength=" + loadedLength + ", real_url=" + real_url + ", threadsEntities=" + threadsEntities + ", helper=" + helper + "]";
    }

	
	public long getLoadedLength() {
		return loadedLength;
	}

	public void setLoadedLength(long loadedLength) {
		this.loadedLength = loadedLength;
	}
	
	public static FileEntity getEntityByUrl(String url) {

		Selector selector = Selector.from(FileEntity.class);
		selector.select(" * ").where(WhereBuilder.b("url", "=", url));
		List<FileEntity> fileEntities = Ioc.getIoc().getDb().findAll(selector);
		if (fileEntities == null || fileEntities.size() == 0) {
			return null;
		}
		FileEntity entity = fileEntities.get(0);
		Selector selector2 = Selector.from(ThreadEntity.class);
		selector2.select(" * ").where(WhereBuilder.b("ThreadId", "=", entity.getId()));
		List<ThreadEntity> entities = Ioc.getIoc().getDb().findAll(selector2);
		entity.setThreadsEntities(entities);
		return entity;
	}

	public static List<FileEntity> getAllFinishEntity() {
		Selector selector = Selector.from(FileEntity.class);
		selector.select(" * ").where(WhereBuilder.b("isSucess", "=", true));
		List<FileEntity> fileEntities = Ioc.getIoc().getDb().findAll(selector);
		return fileEntities;
	}

	public static List<FileEntity> getAllFailureEntity() {
		Selector selector = Selector.from(FileEntity.class);
		selector.select(" * ").where(WhereBuilder.b("isSucess", "=", false));
		List<FileEntity> fileEntities = Ioc.getIoc().getDb().findAll(selector);
		return fileEntities;
	}

	public static List<FileEntity> getAllEntity() {
		Selector selector = Selector.from(FileEntity.class);
		List<FileEntity> fileEntities = Ioc.getIoc().getDb().findAll(selector);
		return fileEntities;
	}

	public void update() {
		Ioc.getIoc().getDb().update(this);
	}

	public String getReal_url() {
		return real_url;
	}

	public void setReal_url(String real_url) {
		this.real_url = real_url;
	}

	public boolean isUpdate() {
		return isUpdate;
	}

	public void setUpdate(boolean isUpdate) {
		this.isUpdate = isUpdate;
	}

	public NotfiEntity getNotfi() {
		return notfi;
	}

	public void setNotfi(NotfiEntity notfi) {
		this.notfi = notfi;
	}

	public NotificationHelper getHelper() {
		return helper;
	}

	public void setHelper(NotificationHelper helper) {
		this.helper = helper;
	}

}
