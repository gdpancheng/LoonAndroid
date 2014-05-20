package com.android.pc.ioc.image;

import android.content.Context;

/*
 * Author: Administrator Email:gdpancheng@gmail.com
 * Created Date:2014-5-19
 * Copyright @ 2014 BU
 * Description: 类描述
 *
 * History:
 */
public class ImageLoadManager {

	private static final int MESSAGE_CLEAR = 0;
	private static final int MESSAGE_INIT_DISK_CACHE = 1;
	
	private ImageCache mImageCache;
	private ImageCache.ImageCacheParams mImageCacheParams;
	
	private static ImageLoadManager loadManager;
	
	public static ImageLoadManager instance(){
		if (loadManager == null) {
			loadManager = new ImageLoadManager();
        }
		return loadManager;
	}
	
	public ImageCache getmImageCache() {
		return mImageCache;
	}

	public void setmImageCache(ImageCache mImageCache) {
		this.mImageCache = mImageCache;
	}
	
	/**
	 * 给当前的{@link ImageWorker} 创建一个{@link ImageCache} 缓存
	 * 在此操作之前 磁盘缓存没有被初始化 则图片下载被锁 等待唤醒
	 * @param fragmentManager
	 * @param cacheParams 缓存参数
	 */
	public void addImageCache(ImageCache.ImageCacheParams cacheParams) {
		mImageCacheParams = cacheParams;
		mImageCache = ImageCache.getInstance(mImageCacheParams);
		new CacheAsyncTask().execute(MESSAGE_INIT_DISK_CACHE);
	}
	
	/**
	 * 给当前的{@link ImageWorker} 创建一个{@link ImageCache} 缓存
	 * 
	 * @param activity
	 * @param diskCacheDirectoryName
	 *            请见 {@link ImageCache.ImageCacheParams#ImageCacheParams(android.content.Context, String)}.
	 */
	public void addImageCache(Context activity, String diskCacheDirectoryName) {
		mImageCacheParams = new ImageCache.ImageCacheParams(activity, diskCacheDirectoryName);
		mImageCache = ImageCache.getInstance(mImageCacheParams);
		new CacheAsyncTask().execute(MESSAGE_INIT_DISK_CACHE);
	}
	
	/**
	 * 异步去处理缓存的清理
	 * @author gdpancheng@gmail.com 2014-5-13 下午3:18:12
	 */
	protected class CacheAsyncTask extends AsyncTask<Object, Void, Void> {

		@Override
		protected Void doInBackground(Object... params) {
			switch ((Integer) params[0]) {
			case MESSAGE_CLEAR:
				clearCacheInternal();
				break;
			case MESSAGE_INIT_DISK_CACHE:
				initDiskCacheInternal();
				break;
			}
			return null;
		}
	}

	protected void initDiskCacheInternal() {
		if (mImageCache != null) {
			mImageCache.initDiskCache();
		}
	}

	/**
	 * 清空缓存
	 * @author gdpancheng@gmail.com 2014-5-19 下午2:38:55
	 * @return void
	 */
	protected void clearCacheInternal() {
		if (mImageCache != null) {
			mImageCache.clearCache();
		}
	}


	/**
	 * 清空缓存
	 * @author gdpancheng@gmail.com 2014-5-13 下午3:15:39
	 * @return void
	 */
	public void clearCache() {
		new CacheAsyncTask().execute(MESSAGE_CLEAR);
	}
}
