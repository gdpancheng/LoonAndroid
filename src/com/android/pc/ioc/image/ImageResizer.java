package com.android.pc.ioc.image;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.widget.ImageView;

import com.android.pc.ioc.app.ApplicationBean;
import com.android.pc.ioc.image.ImageLoadManager.Coding;
import com.android.pc.util.Handler_System;

/**
 * 其中实现了一系列图片加载的方法 <h1>第一条</h1> 实现了设置高宽的方法 <h1>第二条</h1> 实现了从本地文件或者从draw文件夹加载图片的犯法
 */
public class ImageResizer extends ImageWorker {
	protected int mImageWidth;
	protected int mImageHeight;

	/**
	 * 初始化的时候 需要设置大小
	 * 
	 * @param context
	 * @param imageWidth
	 * @param imageHeight
	 */
	public ImageResizer(Context context, int imageWidth, int imageHeight) {
		super(context);
		setImageSize(imageWidth, imageHeight);
	}

	/**
	 * 初始化的时候 需要设置大小
	 * 
	 * @param context
	 * @param imageSize
	 */
	public ImageResizer(Context context, int imageSize) {
		super(context);
		setImageSize(imageSize);
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
		ApplicationBean.logger.d("图片下载开始 - " + resId);
		return decodeSampledBitmapFromResource(mResources, resId, mImageWidth, mImageHeight, getImageCache());
	}

	@Override
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
	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight, ImageCache cache) {

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
	public static Bitmap decodeSampledBitmapFromFile(String filename, int reqWidth, int reqHeight, ImageCache cache) {

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filename, options);

		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		if (Utils.hasHoneycomb()) {
			addInBitmapOptions(options, cache);
		}

		options.inJustDecodeBounds = false;

		Coding coding = ImageLoadManager.instance().getCoding();
		if (coding == null) {
			return BitmapFactory.decodeFile(filename, options);
		}
		try {
			InputStream in = new FileInputStream(filename);
			byte[] buffer = coding.decodeJPG(in.available(), in);
			return BitmapFactory.decodeByteArray(buffer, 0, buffer.length, options);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Bitmap decodeSampledBitmapFromDescriptor(FileDescriptor fileDescriptor, int reqWidth, int reqHeight, ImageCache cache) {
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
	private static void addInBitmapOptions(BitmapFactory.Options options, ImageCache cache) {
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
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
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

	@Override
	protected int getW() {
		return mImageWidth;
	}

	@Override
	protected int getH() {
		return mImageHeight;
	}
}
