package com.android.pc.ioc.image;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.pc.ioc.app.ApplicationBean;
import com.android.pc.util.Handler_System;

/**
 * 图片下载类 可以设置图片高度宽度
 */
public class ImageDownloader extends ImageResizer {

	/**
	 * 限定高度宽度的
	 * 
	 * @param context
	 * @param imageWidth
	 * @param imageHeight
	 */
	public ImageDownloader(Context context, int imageWidth, int imageHeight) {
		super(context, imageWidth, imageHeight);
		init(context);
	}

	/**
	 * 限定高度宽度的
	 * 
	 * @param context
	 * @param imageWidth
	 * @param imageHeight
	 */
	public ImageDownloader(Context context, int imageSize) {
		super(context, imageSize);
		init(context);
	}
	
	public ImageDownloader(Context context) {
		super(context, Handler_System.getDisplayMetrics().get(Handler_System.systemWidth));
		init(context);
	}

	/**
	 * 初始化缓存 和 检查网络
	 * @author gdpancheng@gmail.com 2014-5-19 下午2:55:49
	 * @param context
	 * @return void
	 */
	private void init(Context context) {
		checkConnection(context);
	}


	/**
	 * 简单的网络判断
	 * 
	 * @param context
	 */
	private void checkConnection(Context context) {
		final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
			Toast.makeText(context, "无法链接到网络", Toast.LENGTH_LONG).show();
			ApplicationBean.logger.e("网络连接失败");
		}
	}

	/**
	 * 图片下载主要的处理方法 在后台线程ImageWorker中调用
	 * 
	 * @param data
	 *            普通的url链接
	 * @return 返回调整过大小的bitmap
	 */
	private Bitmap processBitmap(String data,ImageView imageView) {
		
		ApplicationBean.logger.d("图片下载开始 - " + data);
		File file = null;
		//网络图片 本地SD卡图片
		if (data.startsWith("http")) {
			final String key = ImageCache.hashKeyForDisk(data);
			file = ImageCache.getFromFileCache(key);
			start(imageView);
			downloadUrlToStream(data, file,imageView);
        }else {
        	file = new File(data);
		}
		
		Bitmap bitmap = null;
		if (file != null && file.exists()) {
			bitmap = decodeSampledBitmapFromFile(file.getPath(), mImageWidth, mImageHeight, getImageCache());
		}
		if (bitmap==null) {
			fail(imageView);
        }else {
        	finish(bitmap, imageView);
		}
		return bitmap;
	}

	@Override
	protected Bitmap processBitmap(Object data,ImageView imageView) {
		//本地sdcard和网络
		if (data.getClass() == String.class) {
			return processBitmap(String.valueOf(data),imageView);
        }
		//draw文件
		if (data.getClass() == Integer.class) {
			return super.processBitmap(Integer.valueOf(data.toString()),imageView);
        }
		return processBitmap(String.valueOf(data),imageView);
	}

	/**
	 * 从url下载一个bitmap 并写入文件
	 * 
	 * @param urlString
	 *            获取的url
	 * @return 成功则返回true 否则返回false
	 */
	public boolean downloadUrlToStream(String urlString, File file,ImageView imageView) {
		disableConnectionReuseIfNecessary();
		HttpURLConnection urlConnection = null;
		InputStream in = null;
		OutputStream out = null;
		try {
			final URL url = new URL(urlString);
			urlConnection = (HttpURLConnection) url.openConnection();
			out = new FileOutputStream(file);
			int length = urlConnection.getContentLength();
			in = urlConnection.getInputStream();

			int n = 0;
			final int buffer_size = 1024;
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = in.read(bytes, 0, buffer_size);
				if (count == -1) {
					break;
				}
				out.write(bytes, 0, count);
				n = n + count;
				if (length>0) {
					int percent = (int) (n * 100 / length);
					process(percent,imageView);
                }
			}
			return true;
		} catch (final IOException e) {
			ApplicationBean.logger.e("图片下载出错 - " + "Error in downloadBitmap - " + e);
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (final IOException e) {
			}
		}
		return false;
	}

	/**
	 * 解决了错误pre-Froyo, 详细请查看 http://android-developers.blogspot.com/2011/09/androids-http-clients.html
	 */
	public static void disableConnectionReuseIfNecessary() {
		// pre-froyo中的链接拒接的错误
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
			System.setProperty("http.keepAlive", "false");
		}
	}
}
