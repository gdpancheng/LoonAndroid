package com.android.pc.ioc.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.pc.ioc.app.ApplicationBean;
import com.android.pc.ioc.image.cache.FileCache;
import com.android.pc.ioc.image.cache.LoaderManager;
import com.android.pc.ioc.image.config.GlobalConfig;
import com.android.pc.ioc.image.config.SingleConfig;
import com.android.pc.ioc.image.config.Util;
import com.android.pc.ioc.image.displayer.DisplayerAnimation;
import com.android.pc.ioc.image.displayer.DisplayerLister;
import com.android.pc.ioc.image.displayer.LoaderLister;
import com.android.pc.ioc.image.view.AsyImageView;
import com.android.pc.util.Handler_Verify;

public class ImageDownloader {

	private static final Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			if (msg.what == Util.ID_NONE) {
				LoaderLister loader = (LoaderLister) msg.obj;
				Bundle bundle = msg.getData();
				String url = bundle.getString("url");
				if (bundle.containsKey("percent")) {
					int percent = bundle.getInt("percent");
					loader.progressLoader(percent);
					return;
				}

				if (loader != null) {
					File file = GlobalConfig.getInstance().getFileCache().getFromFileCache(url);
					if (file.exists()) {
						loader.finishLoader(url, file);
						return;
					}
					loader.failLoader(url);
					return;
				}
				return;
			}

			AsyImageView imageView = (AsyImageView) msg.obj;
			SingleConfig singleConfig = imageView.getSingleConfig();
			switch (msg.what) {
			case Util.ID_START:
				if (singleConfig != null && singleConfig.getDisplayer() != null) {
					singleConfig.getDisplayer().startLoader(imageView);
				}
				return;
			case Util.ID_PROCESS:
				if (singleConfig != null && singleConfig.getDisplayer() != null) {
					singleConfig.getDisplayer().progressLoader(msg.arg1, imageView);
				}
				return;
			case Util.ID_FAIL:
				if (singleConfig != null && singleConfig.getDisplayer() != null) {
					singleConfig.getDisplayer().failLoader(imageView);
				}
				return;
			}
			GlobalConfig globalConfig = GlobalConfig.getInstance();
			LoaderManager loaderManager = globalConfig.getLoaderManager();

			// 下载回调
			DisplayerLister displayer = singleConfig == null ? null : singleConfig.getDisplayer();
			displayer = displayer == null ? globalConfig.getDisplayer() : displayer;
			// 显示动画
			DisplayerAnimation animation = singleConfig == null ? null : singleConfig.getDisplayerAnimation();
			animation = animation == null ? globalConfig.getDisplayerAnimation() : animation;

			HashMap<String, Integer> map = getSize(imageView);
			int w = map.get("w");
			int h = map.get("h");

			Bitmap bitmap = loaderManager.getBitmap(FileCache.urlToFileName(imageView.getUrl() + "_" + h + "_" + w), imageView);

			if (bitmap == null) {
				// 失败的图片
				Drawable drawable = singleConfig == null ? null : singleConfig.getDefDrawable();
				drawable = drawable == null ? globalConfig.getDef_drawable() : drawable;

				if (displayer != null) {
					displayer.failLoader(imageView);
				}
				imageView.setImageDrawable(drawable);
				return;
			}

			// 首先是加载过程然后是加载动画
			if (displayer != null) {
				bitmap = displayer.finishLoader(bitmap, imageView);
				if (animation != null) {
					animation.show(imageView, bitmap);
					return;
				}
				imageView.setImageBitmap(bitmap);
			} else {
				if (animation != null) {
					animation.show(imageView, bitmap);
					return;
				}
				imageView.setImageBitmap(bitmap);
			}
		};
	};

	public static Handler getHandler() {
		return handler;
	}

	/**
	 * 图片下载 不需要绑定imageview TODO(这里用一句话描述这个方法的作用)
	 * 
	 * @author gdpancheng@gmail.com 2014-2-28 下午10:59:08
	 * @param url
	 * @param loader
	 * @return void
	 */
	public static void download(final String url, final LoaderLister loader) {
		GlobalConfig globalConfig = GlobalConfig.getInstance();
		if (globalConfig.getContext() == null) {
			ApplicationBean.logger.e("无法使用图片下载框架,请先在mvc.properties配置");
			return;
		}
		if (loader == null) {
			return;
		}
		loader.startLoader(url);
		File file = null;
		if (url.matches(Handler_Verify.http)) {
			// 开始下载
			file = globalConfig.getFileCache().getFromFileCache(url);
		} else {
			Log.d("file from sdcard", url);
			file = new File(url);
		}
		if (file.exists()) {
			loader.finishLoader(url, file);
			return;
		}

		globalConfig.getPool().execute(new Runnable() {
			public void run() {
				downloadFile(url, loader);
				Message message = Message.obtain();
				message.obj = loader;
				message.what = Util.ID_NONE;
				Bundle bundle = new Bundle();
				bundle.putString("url", url);
				message.setData(bundle);
				handler.sendMessage(message);
			}
		});

	}

	/**
	 * <h1>加载网络图片</h1><br>
	 * 1 如果需要单独设置每张图片的尺寸,则请AsyImageView.setSingleConfig或者调用下面的download方法}<br>
	 * 或者直接在xml布局文件中定死高宽，程序自动判断<br>
	 * 2 如果是在listview中请设置AsyImageView.setPostion设置当前view在listview中的postion<br>
	 * 否则无法实现滑动到哪从哪开始加载<br>
	 * 3 如果希望统一配置 则在mvc.properties中进行配置 然后调用 AsyImageView.setTemplate 具体参考demo<br>
	 * 也可以对全局配置GlobalConfig globalConfig = GlobalConfig.getInstance();以后再对globalConfig设置属性
	 * 
	 * @author gdpancheng@gmail.com 2014-2-28 下午11:01:30
	 * @param url
	 * @param imageView
	 * @return void
	 */
	public static void download(String url, AsyImageView imageView) {
		if (imageView == null) {
			return;
		}
		imageView.setUrl(url);
		download(url, imageView, null);
	}

	/**
	 * <h1>加载网络图片</h1><br>
	 * 1 如果需要单独设置每张图片的尺寸,则请在SingleConfig设置<br>
	 * 或者直接在xml布局文件中定死高宽，程序自动判断<br>
	 * 2 如果是在listview中请设置AsyImageView.setPostion设置当前view在listview中的postion<br>
	 * 否则无法实现滑动到哪从哪开始加载<br>
	 * 3 如果希望统一配置 则在mvc.properties中进行配置 然后调用 AsyImageView.setTemplate 具体参考demo<br>
	 * 也可以对全局配置GlobalConfig globalConfig = GlobalConfig.getInstance();以后再对globalConfig设置属性
	 * 
	 * TODO(这里用一句话描述这个方法的作用)
	 * 
	 * @author gdpancheng@gmail.com 2014-3-4 下午11:28:14
	 * @param url
	 * @param imageView
	 * @param config
	 * @return void
	 */
	public static void download(String url, AsyImageView imageView, SingleConfig config) {
		// 获取全局的系统配置
		GlobalConfig globalConfig = GlobalConfig.getInstance();
		if (globalConfig.getContext() == null) {
			ApplicationBean.logger.e("无法使用图片下载框架,请现在mvc.properties配置");
			return;
		}
		if (imageView == null) {
			return;
		}

		if (config != null) {
			imageView.setSingleConfig(config);
		}
		imageView.setUrl(url);

		// 获取图片缓存对象
		LoaderManager bitMapLru = globalConfig.getLoaderManager();

		// 下载回调
		DisplayerLister displayer = config == null ? null : config.getDisplayer();
		displayer = displayer == null ? globalConfig.getDisplayer() : displayer;
		// 显示动画
		DisplayerAnimation animation = config == null ? null : config.getDisplayerAnimation();
		animation = animation == null ? globalConfig.getDisplayerAnimation() : animation;

		if (displayer != null) {
			displayer.startLoader(imageView);
		}

		HashMap<String, Integer> map = getSize(imageView);
		int w = map.get("w");
		int h = map.get("h");
		Bitmap bitmap = bitMapLru.getBitmap(FileCache.urlToFileName(url + "_" + h + "_" + w), imageView);

		// -----------------------------------------------------------------------------------------
		// 设置默认图片
		if (config != null && config.getDefDrawable() != null) {
			// 单个配置优先显示
			imageView.setImageDrawable(config.getDefDrawable());
		} else if (globalConfig.getDef_drawable() != null) {
			// 单个配置不存在默认图片的时候 显示全局设置中的默认图片
			imageView.setImageDrawable(globalConfig.getDef_drawable());
		} else {
			imageView.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
		}

		if (bitmap == null) {
			// 说明是listview
			if (imageView.getPostion() != Util.ID_NONE && !globalConfig.getOnScrollLoaderListener().isLoader()) {
				globalConfig.getList_data().put(imageView.getPostion(), imageView);
				return;
			}
			forceDownload(imageView);
		} else {
			// 首先是加载过程然后是加载动画
			if (displayer != null) {
				bitmap = displayer.finishLoader(bitmap, imageView);
				if (animation != null) {
					animation.show(imageView, bitmap);
					return;
				}
				imageView.setImageBitmap(bitmap);
			} else {
				if (animation != null) {
					animation.show(imageView, bitmap);
					return;
				}
				imageView.setImageBitmap(bitmap);
			}
		}
	}

	private static void forceDownload(final AsyImageView imageView) {

		final GlobalConfig globalConfig = GlobalConfig.getInstance();
		final LoaderManager bitMapLru = globalConfig.getLoaderManager();
		// 获取本地图片线程池 如果本地图片和网络图片用一个线程池
		// 如果网络不好 那么本地图片加载就会受影响
		globalConfig.getPool_Local().execute(new Runnable() {

			@Override
			public void run() {
				Bitmap bitmap = getBitmapFromFile(imageView);
				HashMap<String, Integer> map = getSize(imageView);
				int w = map.get("w");
				int h = map.get("h");
				bitMapLru.putBitmap(FileCache.urlToFileName(imageView.getUrl() + "_" + h + "_" + w), bitmap);
				if (bitmap != null) {
					Message message = Message.obtain();
					message.obj = imageView;
					message.what = Util.ID_ZEO;
					// 通知更新
					handler.sendMessage(message);
					return;
				}
				if (!imageView.isHttp()) {
					return;
				}
				// 获取网络图片线程池
				globalConfig.getPool().execute(new Runnable() {
					public void run() {
						// 获取到图片以后 放到缓存
						HashMap<String, Integer> map = getSize(imageView);
						int w = map.get("w");
						int h = map.get("h");
						bitMapLru.putBitmap(FileCache.urlToFileName(imageView.getUrl() + "_" + h + "_" + w), downloadBitmap(imageView));
						Message message = Message.obtain();
						message.obj = imageView;
						message.what = Util.ID_ZEO;
						// 通知更新
						handler.sendMessage(message);
					}
				});
			}
		});
	}

	/**
	 * 下载图片
	 * 
	 * @author gdpancheng@gmail.com 2014-2-28 下午11:04:49
	 * @param asyImageView
	 * @return
	 * @return Bitmap
	 */
	private static Bitmap downloadBitmap(AsyImageView asyImageView) {

		GlobalConfig globalConfig = GlobalConfig.getInstance();
		InputStream inputStream = null;
		HttpURLConnection conn = null;
		try {
			// 下载
			conn = getConnect(asyImageView.getUrl());
			if (200 == conn.getResponseCode()) {
				asyImageView.setLength(conn.getContentLength());
				inputStream = conn.getInputStream();
				globalConfig.getFileCache().addImageToCache(asyImageView.getUrl(), asyImageView, inputStream);
				File file = globalConfig.getFileCache().getFromFileCache(asyImageView.getUrl());
				if (file.exists()) {
					return getBitmapFromFile(file, globalConfig, asyImageView);
				}
			}
		} catch (Exception e) {
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				if (conn != null) {
					conn.disconnect();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private static File downloadFile(String url, LoaderLister lister) {

		GlobalConfig globalConfig = GlobalConfig.getInstance();
		InputStream inputStream = null;
		HttpURLConnection conn = null;
		try {
			conn = getConnect(url);
			if (200 == conn.getResponseCode()) {
				int length = conn.getContentLength();
				inputStream = conn.getInputStream();
				globalConfig.getFileCache().addFileToCache(url, length, lister, inputStream);
				File file = globalConfig.getFileCache().getFromFileCache(url);
				if (file.exists()) {
					return file;
				}
			}
		} catch (Exception e) {
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				if (conn != null) {
					conn.disconnect();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private static HttpURLConnection getConnect(String urls) throws IOException {
		URL url = new URL(urls);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestProperty("Accept-Encoding", "identity");
		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.connect();
		return conn;
	}

	private static Bitmap getBitmapFromFile(AsyImageView imageView) {
		GlobalConfig config = GlobalConfig.getInstance();
		File file = null;
		if (imageView.isHttp()) {
			// 开始下载
			file = config.getFileCache().getFromFileCache(imageView.getUrl());
		} else {
			Log.d("loadBitmap from sdcard", imageView.getUrl());
			file = new File(imageView.getUrl());
		}
		Bitmap bitmap = null;
		if (file != null && file.exists()) {
			try {
				bitmap = getBitmapFromFile(file, config, imageView);
			} catch (FileNotFoundException e1) {
				bitmap = null;
				if (file.exists()) {
					file.delete();
				}
			}
		}
		return bitmap;
	}

	private static Bitmap getBitmapFromFile(File file, GlobalConfig globalConfig, AsyImageView imageView) throws FileNotFoundException {

		Options options = getOptions(file.getPath());

		HashMap<String, Integer> map = getSize(imageView);
		int w = map.get("w");
		int h = map.get("h");

		int inSample = computeSampleSize(options, w > h ? h : w, w * h);
		options.inJustDecodeBounds = false;
		options.inSampleSize = inSample;
		options.inPreferredConfig = Bitmap.Config.ARGB_4444;
		return BitmapFactory.decodeStream(new FileInputStream(file), null, options);
	}

	public static HashMap<String, Integer> getSize(AsyImageView imageView) {
		GlobalConfig globalConfig = GlobalConfig.getInstance();
		SingleConfig configuration = imageView.getSingleConfig();
		int w = 0, h = 0;
		if (configuration != null && configuration.getMax_width() != 0) {
			w = configuration.getMax_width();
			h = configuration.getMax_height();
		}

		if (w <= 1 || h <= 1) {
			h = imageView.getLayoutParams().height;
			w = imageView.getLayoutParams().width;
		}

		if (w <= 1 || h <= 1) {
			w = globalConfig.getMaxWidth();
			h = globalConfig.getMaxHeight();
		}
		HashMap<String, Integer> date = new HashMap<String, Integer>();
		date.put("w", w);
		date.put("h", h);
		return date;
	}

	private static Options getOptions(String path) {
		Options options = new Options();
		options.inJustDecodeBounds = true;// 只描边，不读取数据
		BitmapFactory.decodeFile(path, options);
		return options;
	}

	private static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}
}
