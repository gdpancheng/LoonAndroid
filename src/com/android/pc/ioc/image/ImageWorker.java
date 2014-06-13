package com.android.pc.ioc.image;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.android.pc.ioc.app.Ioc;
import com.android.pc.ioc.image.ImageLoadManager.Coding;
import com.android.pc.util.Handler_System;

/**
 * 图片下载工具类
 */
public  class ImageWorker {
	private static final int FADE_IN_TIME = 200;

	private static final int start = 0;
	private static final int process = 1;
	private static final int fail = 2;
	private static final int finish = 3;

	private ImageCache mImageCache;
	private Bitmap mLoadingBitmap;
	private boolean mFadeInBitmap = true;
	private boolean mExitTasksEarly = false;
	protected boolean mPauseWork = false;
	private final Object mPauseWorkLock = new Object();
	protected Resources mResources;
	protected DisplayerLister lister;
	private boolean isDecode = false;

	protected ImageWorker(Context context) {
		mResources = context.getResources();
	}
	
	protected int mImageWidth;
	protected int mImageHeight;

	/**
	 * 初始化的时候 需要设置大小
	 * 
	 * @param context
	 * @param imageWidth
	 * @param imageHeight
	 */
	public ImageWorker(Context context, int imageWidth, int imageHeight) {
		setImageSize(imageWidth, imageHeight);
		mResources = context.getResources();
	}

	/**
	 * 初始化的时候 需要设置大小
	 * 
	 * @param context
	 * @param imageSize
	 */
	public ImageWorker(Context context, int imageSize) {
		setImageSize(imageSize);
		mResources = context.getResources();
	}
	
	public void useDecode(boolean isDecode){
		this.isDecode = isDecode;
	}
	
	public boolean isDecode(){
		return isDecode;
	}

	/**
	 * 加载网络图片到ImageView上(当然也可以重写{@link ImageWorker#processBitmap(Object)}来实现图片获取过程)<br>
	 * 如果图片在内存和本地缓存ImageWorker中存在,则直接 <br>
	 * 否则将开启{@link AsyncTask} 下载图片
	 * 
	 * @param data
	 *            图片下载链接
	 * @param imageView
	 *            图片显示组件
	 */
	public void loadImage(Object data, ImageView imageView) {
		if (data == null) {
			return;
		}

		if (data.toString().indexOf("?") != -1) {
			data = data + "&w=" + getW() + "&h=" + getH();
		} else {
			data = data + "?w=" + getW() + "&h=" + getH();
		}
		BitmapDrawable value = null;
		mImageCache = ImageLoadManager.instance().getmImageCache();
		if (mImageCache != null) {
			value = mImageCache.getBitmapFromMemCache(String.valueOf(data));
		}
		// 如果图片为空
		if (value != null) {
			// 如果图片不为空 则直接设置
			if (value.getBitmap()!=null) {
				finish(value.getBitmap(), imageView, lister);
            }
			imageView.setImageDrawable(value);
		} else if (cancelPotentialWork(data, imageView)) {
			final BitmapWorkerTask task = new BitmapWorkerTask(data, imageView, lister);
			final AsyncDrawable asyncDrawable = new AsyncDrawable(mResources, mLoadingBitmap, task);
			imageView.setImageDrawable(asyncDrawable);
			// 通过线程池执行下载进程
			task.executeOnExecutor(AsyncTask.DUAL_THREAD_EXECUTOR);
		}
	}

	public void loadImage(Object data, ImageView imageView, DisplayerLister lister) {
		this.lister = lister;
		loadImage(data, imageView);
	}

	/**
	 * 设置loading的图片
	 * 
	 * @param bitmap
	 */
	public void setLoadingImage(Bitmap bitmap) {
		mLoadingBitmap = bitmap;
	}

	/**
	 * 设置loading的图片的ID
	 * 
	 * @param resId
	 */
	public void setLoadingImage(int resId) {
		mLoadingBitmap = BitmapFactory.decodeResource(mResources, resId);
	}

	/**
	 * 淡入淡出
	 */
	public void setImageFadeIn(boolean fadeIn) {
		mFadeInBitmap = fadeIn;
	}

	public void setExitTasksEarly(boolean exitTasksEarly) {
		mExitTasksEarly = exitTasksEarly;
		setPauseWork(false);
	}


	private class Entity {
		public int process;
		public ImageView imageView;
		public Bitmap bitmap;
		public DisplayerLister lister;
	}

	protected void process(int process, ImageView imageView, DisplayerLister lister) {
		if (lister == null) {
			return;
		}
		Message msg = handler.obtainMessage();
		Entity entity = new Entity();
		entity.process = process;
		entity.imageView = imageView;
		entity.lister = lister;
		msg.obj = entity;
		msg.what = process;
		msg.sendToTarget();
	};

	protected void finish(Bitmap bitmap, ImageView imageView, DisplayerLister lister) {
		if (lister == null) {
			return;
		}
		Message msg = handler.obtainMessage();
		Entity entity = new Entity();
		entity.bitmap = bitmap;
		entity.imageView = imageView;
		entity.lister = lister;
		msg.obj = entity;
		msg.what = finish;
		msg.sendToTarget();
	};

	protected void fail(ImageView imageView, DisplayerLister lister) {
		if (lister == null) {
			return;
		}
		Message msg = handler.obtainMessage();
		Entity entity = new Entity();
		entity.imageView = imageView;
		entity.lister = lister;
		msg.obj = entity;
		msg.what = fail;
		msg.sendToTarget();
	};

	protected void start(ImageView imageView, DisplayerLister lister) {
		if (lister == null) {
			return;
		}
		Message msg = handler.obtainMessage();
		Entity entity = new Entity();
		entity.imageView = imageView;
		entity.lister = lister;
		msg.obj = entity;
		msg.what = start;
		msg.sendToTarget();
	};

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Entity entity = (Entity) msg.obj;
			switch (msg.what) {
			case start:
				entity.lister.startLoader(entity.imageView);
				break;
			case process:
				entity.lister.progressLoader(entity.process, entity.imageView);
				break;
			case fail:
				entity.lister.failLoader(entity.imageView);
				break;
			case finish:
				entity.lister.finishLoader(entity.bitmap, entity.imageView);
				break;
			}
		};
	};

	/**
	 * @return 返回当前对象使用的缓存
	 */
	protected ImageCache getImageCache() {
		return mImageCache;
	}

	/**
	 * 取消这个imageview所对应的任务
	 * 
	 * @param imageView
	 */
	public static void cancelWork(ImageView imageView) {
		final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
		if (bitmapWorkerTask != null) {
			bitmapWorkerTask.cancel(true);
			final Object bitmapData = bitmapWorkerTask.mData;
			Ioc.getIoc().getLogger().d("cancelWork - cancelled work for " + bitmapData);
		}
	}

	/**
	 * <h1>第一步</h1> 根据imageView获取到AsyncDrawable<br>
	 * <h1>第二步</h1>从AsyncDrawable获得BitmapWorkerTask<br>
	 * <h1>第三步</h1>根据BitmapWorkerTask中的url判断当前下载的url和之前是否一样<br>
	 * 如果一样则返回false无需再下载<br>
	 * <h1>第四步</h1>如果不一样则说明 当前imageview需要显示的图片和之前图片不一样,取消之前下载进程 返回true
	 * 
	 */
	public static boolean cancelPotentialWork(Object data, ImageView imageView) {
		final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

		if (bitmapWorkerTask != null) {
			final Object bitmapData = bitmapWorkerTask.mData;
			// 判断当前的Url和之前的url是否一样
			if (bitmapData == null || !bitmapData.equals(data)) {
				bitmapWorkerTask.cancel(true);
				Ioc.getIoc().getLogger().d("cancelPotentialWork - cancelled work for " + data);
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param imageView
	 *            图片控件
	 * @return 返回和当前imageview有关的任务 如果不存在则返回null
	 */
	private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getBitmapWorkerTask();
			}
		}
		return null;
	}

	/**
	 * 异步下载图片 继承的一个自定义的异步下载进程
	 */
	private class BitmapWorkerTask extends AsyncTask<Void, Void, BitmapDrawable> {
		/** 图片的来源 **/
		private Object mData;
		// 弱引用 存储了视图
		private final WeakReference<ImageView> imageViewReference;
		protected final DisplayerLister lister;

		public BitmapWorkerTask(Object data, ImageView imageView, DisplayerLister lister) {
			mData = data;
			imageViewReference = new WeakReference<ImageView>(imageView);
			this.lister = lister;
		}

		/**
		 * 后台进程
		 */
		@Override
		protected BitmapDrawable doInBackground(Void... params) {
			Ioc.getIoc().getLogger().d("doInBackground - starting work");

			final String dataString = String.valueOf(mData);
			Bitmap bitmap = null;
			BitmapDrawable drawable = null;
			// 暂停下载的时候这里被锁住了 等待唤醒
			// 这里也可能被取消了
			synchronized (mPauseWorkLock) {
				while (mPauseWork && !isCancelled()) {
					try {
						mPauseWorkLock.wait();
					} catch (InterruptedException e) {
					}
				}
			}

			// 如果缓存不为空 没有被取消 当前显示的imageview不为空 则去从本地缓存中获取
			if (mImageCache != null && !isCancelled() && getAttachedImageView() != null && !mExitTasksEarly) {
				bitmap = mImageCache.getBitmapFromDiskCache(dataString, getW(), getH(),ImageWorker.this);
			}
			// 下载图片 通过自定义下载模块
			if (bitmap == null && !isCancelled() && getAttachedImageView() != null && !mExitTasksEarly) {
				bitmap = processBitmap(mData, imageViewReference.get());
			}

			// 如果图片不为空
			if (bitmap != null) {

				if (Utils.hasHoneycomb()) {
					// 如果版本大于3.0则把bitmap包裹到BitmapDrawable中
					drawable = new BitmapDrawable(mResources, bitmap);
				} else {
					// 小于3.0的情况下 自定义一个BitmapDrawable用来自动回收图片
					drawable = new RecyclingBitmapDrawable(mResources, bitmap);
				}

				if (mImageCache != null) {
					mImageCache.addBitmapToCache(dataString, drawable);
				}
			}

			Ioc.getIoc().getLogger().d("doInBackground - finished work");

			return drawable;
		}

		/**
		 * 图片下载完成以后，调用imageview进行绘制
		 */
		@Override
		protected void onPostExecute(BitmapDrawable value) {
			// 如果取消 或者 被标志位退出 则返回Null
			if (isCancelled() || mExitTasksEarly) {
				value = null;
			}

			final ImageView imageView = getAttachedImageView();
			if (value != null && imageView != null) {
				if (value.getBitmap()!=null) {
					finish(value.getBitmap(), imageView, lister);
                }
				Ioc.getIoc().getLogger().d("onPostExecute - setting bitmap");
				setImageDrawable(imageView, value);
			}
			if (value == null) {
				fail(imageView, lister);
			}
		}

		@Override
		protected void onCancelled(BitmapDrawable value) {
			super.onCancelled(value);
			synchronized (mPauseWorkLock) {
				mPauseWorkLock.notifyAll();
			}
		}

		/**
		 * 返回该任务指向的imageview 否则返回null
		 */
		private ImageView getAttachedImageView() {
			final ImageView imageView = imageViewReference.get();
			final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

			if (this == bitmapWorkerTask) {
				return imageView;
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
	}

	/**
	 * 用来显示Bitmap的载体， 包含了下载线程的引用
	 */
	private static class AsyncDrawable extends BitmapDrawable {
		private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

		public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
			super(res, bitmap);
			bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
		}

		public BitmapWorkerTask getBitmapWorkerTask() {
			return bitmapWorkerTaskReference.get();
		}
	}

	/**
	 * Called when the processing is complete and the final drawable should be set on the ImageView.
	 * 
	 * @param imageView
	 * @param drawable
	 */
	private void setImageDrawable(ImageView imageView, Drawable drawable) {
		if (mFadeInBitmap) {
			final TransitionDrawable td = new TransitionDrawable(new Drawable[] { new ColorDrawable(android.R.color.transparent), drawable });
			// imageView.setBackgroundDrawable(new BitmapDrawable(mResources, mLoadingBitmap));
			imageView.setImageDrawable(td);
			td.startTransition(FADE_IN_TIME);
		} else {
			imageView.setImageDrawable(drawable);
		}
	}

	/**
	 * 可以用来暂停图片的下载 一半用在列表 {@link android.widget.AbsListView.OnScrollListener}中来让滑动更为平滑
	 * <p>
	 * 为了让下载更加安全 请在你得fragment或者activity {@link android.app.Activity#onPause()}终止之前 请设置setPauseWork(false)
	 */
	public void setPauseWork(boolean pauseWork) {
		synchronized (mPauseWorkLock) {
			mPauseWork = pauseWork;
			if (!mPauseWork) {
				mPauseWorkLock.notifyAll();
			}
		}
	}

	/**
	 * Set the target image width and height.
	 * 
	 * @param width
	 * @param height
	 */
	public void setImageSize(int width, int height) {
		HashMap<String, Integer> data = Handler_System.getDisplayMetrics();
		if (width == 0) {
			width = data.get(Handler_System.systemWidth);
		}
		if (height == 0) {
			height = data.get(Handler_System.systemHeight);
		}
		mImageWidth = width;
		mImageHeight = height;
	}

	/**
	 * 设置图片的高宽 高宽一样
	 * 
	 * @param size
	 */
	public void setImageSize(int size) {
		setImageSize(size, size);
	}

	/**
	 * 图片解析的主方法<br>
	 * 参数为资源ID
	 * 
	 * @param resId
	 * @return
	 */
	private Bitmap processBitmap(int resId) {
		Ioc.getIoc().getLogger().d("图片下载开始 - " + resId);
		return decodeSampledBitmapFromResource(mResources, resId, mImageWidth, mImageHeight, getImageCache());
	}

	protected Bitmap processBitmap(Object data, ImageView imageView) {
		return processBitmap(Integer.parseInt(String.valueOf(data)));
	}

	/**
	 * 从资源文件加载图片
	 * 
	 * @author gdpancheng@gmail.com 2014-5-19 下午2:14:13
	 * @param res
	 * @param resId
	 * @param reqWidth
	 * @param reqHeight
	 * @param cache
	 * @return Bitmap
	 */
	public  Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight, ImageCache cache) {

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		if (Utils.hasHoneycomb()) {
			addInBitmapOptions(options, cache);
		}

		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	/**
	 * 从本地文件加载图片
	 * 
	 * @author gdpancheng@gmail.com 2014-5-19 下午2:15:23
	 * @param filename
	 * @param reqWidth
	 * @param reqHeight
	 * @param cache
	 * @return Bitmap
	 */
	public  Bitmap decodeSampledBitmapFromFile(String url,String filename, int reqWidth, int reqHeight, ImageCache cache) {

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Coding coding = ImageLoadManager.instance().getCoding();
		if (coding == null||isDecode == false) {
			BitmapFactory.decodeFile(filename, options);
        }else if (isDecode&&coding!=null) {
        	try {
	            InputStream in = new FileInputStream(filename);
	            byte[] buffer;
	            if (url.toLowerCase().indexOf(".jpg")!=-1) {
	            	buffer= coding.decodeJPG(in.available(), in);
                }else {
                	buffer= coding.decodePNG(in.available(), in);
				}
	            in.close();
	            BitmapFactory.decodeByteArray(buffer, 0, buffer.length, options);
	            buffer = null;
            } catch (Exception e) {
	            e.printStackTrace();
            } 
		}

		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		if (Utils.hasHoneycomb()) {
			addInBitmapOptions(options, cache);
		}

		options.inJustDecodeBounds = false;
		
		if (coding == null||isDecode == false) {
			return BitmapFactory.decodeFile(filename, options);
		}else if(isDecode && coding!=null) {
			try {
				InputStream in = new FileInputStream(filename);
				byte[] buffer;
				if (url.toLowerCase().indexOf(".jpg")!=-1) {
					buffer= coding.decodeJPG(in.available(), in);
				}else {
					buffer= coding.decodePNG(in.available(), in);
				}
				in.close();
				return BitmapFactory.decodeByteArray(buffer, 0, buffer.length, options);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public  Bitmap decodeSampledBitmapFromDescriptor(FileDescriptor fileDescriptor, int reqWidth, int reqHeight, ImageCache cache) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		options.inJustDecodeBounds = false;

		if (Utils.hasHoneycomb()) {
			addInBitmapOptions(options, cache);
		}

		return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private  void addInBitmapOptions(BitmapFactory.Options options, ImageCache cache) {
		options.inMutable = true;

		if (cache != null) {
			Bitmap inBitmap = cache.getBitmapFromReusableSet(options);

			if (inBitmap != null) {
				options.inBitmap = inBitmap;
			}
		}
	}

	/**
	 * 根据需要的高宽，对图片进行缩放 TODO(这里用一句话描述这个方法的作用)
	 * 
	 * @author gdpancheng@gmail.com 2014-5-19 下午2:19:56
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return int
	 */
	public  int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}

			long totalPixels = width * height / inSampleSize;

			final long totalReqPixelsCap = reqWidth * reqHeight * 2;

			while (totalPixels > totalReqPixelsCap) {
				inSampleSize *= 2;
				totalPixels /= 2;
			}
		}
		return inSampleSize;
	}

	protected int getW() {
		return mImageWidth;
	}

	protected int getH() {
		return mImageHeight;
	}
}
