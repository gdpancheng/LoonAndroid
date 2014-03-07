package com.android.pc.ioc.image.cache;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Bundle;
import android.os.Message;

import com.android.pc.ioc.image.ImageDownloader;
import com.android.pc.ioc.image.config.Util;
import com.android.pc.ioc.image.displayer.DisplayerLister;
import com.android.pc.ioc.image.displayer.LoaderLister;
import com.android.pc.ioc.image.view.AsyImageView;

/**
 * <h2>图片缓存<h2/>
 * 
 * @author pancheng 2012-10-23 下午1:38:12
 */

public class FileCache {

	String cacheDir;

	public FileCache(String cacheDir) {
		this.cacheDir = cacheDir;
	}

	/**
	 * 添加文件到本地物理缓存中
	 * 
	 * @author gdpancheng@gmail.com 2013-7-9 下午6:02:10
	 * @param url
	 * @param inputStream
	 * @return void
	 */
	public void addImageToCache(String url, AsyImageView imageView, InputStream inputStream) {
		OutputStream outputStream = null;
		File file = null;
		try {
			file = getFromFileCache(url);
			outputStream = new FileOutputStream(file);
			copyStreamImage(imageView, inputStream, outputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 后台下载图片文件
	 * @author gdpancheng@gmail.com 2014-2-12 下午10:53:59
	 * @param url
	 * @param length
	 * @param lister
	 * @param inputStream
	 * @return void
	 */
	public void addFileToCache(String url,int length,LoaderLister lister, InputStream inputStream) {
		OutputStream outputStream = null;
		File file = null;
		try {
			file = getFromFileCache(url);
			outputStream = new FileOutputStream(file);
			copyStreamFile(url,length,lister, inputStream, outputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 从缓存中获取文件
	 * 
	 * @author pancheng 2012-10-23 下午1:41:24
	 * @param url
	 *            下载连接
	 * @return File 图片文件
	 */
	public File getFromFileCache(String url) {
		File file = new File(cacheDir);
		if (!file.exists()) {
			file.mkdirs();
		}
		String fileName = urlToFileName(url);
		return new File(cacheDir, fileName);
	}

	/**
	 * 清理缓存
	 * 
	 * @author pancheng 2012-10-23 下午1:41:30
	 * @return void
	 */
	public void clearCache() {
		File file = new File(cacheDir);
		File[] files = file.listFiles();
		if (files == null)
			return;
		for (File f : files)
			f.delete();
	}

	public static String urlToFileName(String url) {
		return String.valueOf(url.hashCode());
	}

	private void copyStreamImage(AsyImageView imageView, InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			int n = 0;
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1) {
					break;
				}
				n = n + count;
				int percent = (int) (n * 100 / imageView.getLength());
				if (imageView.getSingleConfig() != null) {
					DisplayerLister lister = imageView.getSingleConfig().getDisplayer();
					if (lister != null) {
						Message msg = ImageDownloader.getHandler().obtainMessage();
						msg.what = Util.ID_PROCESS;
						msg.arg1 = percent;
						msg.obj = imageView;
						ImageDownloader.getHandler().sendMessage(msg);
					}
				}
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void copyStreamFile(String url,int length, LoaderLister lister, InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			int n = 0;
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1) {
					break;
				}
				n = n + count;
				int percent = (int) (n * 100 / length);
				if (lister != null) {
					
					Message message = Message.obtain();
					message.obj = lister;
					message.what = Util.ID_NONE;
					Bundle bundle = new Bundle();
					bundle.putString("url", url);
					bundle.putInt("percent", percent);
					message.setData(bundle);
					ImageDownloader.getHandler().sendMessage(message);
					
					lister.progressLoader(percent);
				}
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
