package com.android.pc.util;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * 图片工具类
 * 
 * @author gdpancheng@gmail.com 2013-10-22 下午12:54:55
 */
public class Handler_Bitmap {
	public static final String textChangLine = "@";

	/**
	 * 缩放图片
	 * 
	 * @author gdpancheng@gmail.com 2013-10-12 下午3:56:16
	 * @param bm
	 * @param newWidth
	 * @param newHeight
	 * @return Bitmap
	 */
	public static Bitmap scaleImg(Bitmap bm, int newWidth, int newHeight) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		int newWidth1 = newWidth;
		int newHeight1 = newHeight;
		float scaleWidth = ((float) newWidth1) / width;
		float scaleHeight = ((float) newHeight1) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
		return newbm;
	}

	/**
	 * drawable 转为 Bitmap
	 * 
	 * @author gdpancheng@gmail.com 2013-10-12 下午3:56:25
	 * @param drawable
	 * @return Bitmap
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * 缩放Drawable
	 * 
	 * @author gdpancheng@gmail.com 2013-10-12 下午3:56:40
	 * @param drawable
	 * @param w
	 * @param h
	 * @return Drawable
	 */
	public static Drawable zoomDrawable(Drawable drawable, int w, int h) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap oldbmp = drawableToBitmap(drawable); // drawable转换成bitmap
		Matrix matrix = new Matrix(); // 创建操作图片用的Matrix对象
		float scaleWidth = ((float) w / width); // 计算缩放比例
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidth, scaleHeight); // 设置缩放比例
		Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true); // 建立新的bitmap，其内容是对原bitmap的缩放后的图
		return new BitmapDrawable(newbmp); // 把bitmap转换成drawable并返回
	}

	/**
	 * bitmap 转 Drawable
	 * 
	 * @author gdpancheng@gmail.com 2013-10-12 下午3:56:49
	 * @param bitmap
	 * @return Drawable
	 */
	public static Drawable bitmap2Drawable(Bitmap bitmap) {
		Drawable drawable = new BitmapDrawable(bitmap);
		return drawable;
	}

	/**
	 * 图片创建倒影 TODO(这里用一句话描述这个方法的作用)
	 * 
	 * @author gdpancheng@gmail.com 2013-10-12 下午3:58:39
	 * @param originalImage
	 * @param number
	 * @return Bitmap
	 */
	public static Bitmap createReflectedImage(Bitmap originalImage, int number) {
		final int reflectionGap = 0; // 倒影和原图片间的距离
		int width = originalImage.getWidth();
		int height = originalImage.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		double reflectHeight = number / 100.00;

		number = (int) (height * reflectHeight);
		// 倒影部分
		Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, number, width, number, matrix, false);
		// 要返回的倒影图片
		Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + number), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		// 画原来的图片
		canvas.drawBitmap(originalImage, 0, 0, null);

		// Paint defaultPaint = new Paint();
		// //倒影和原图片间的距离
		// canvas.drawRect(0, height, width, height + reflectionGap,
		// defaultPaint);
		// 画倒影部分
		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0, bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.MIRROR);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);
		return bitmapWithReflection;
	}

	/**
	 * 图片增加边框
	 * 
	 * @author gdpancheng@gmail.com 2013-10-12 下午3:58:28
	 * @param bitmap
	 * @param color
	 * @return Bitmap
	 */
	public static Bitmap addFrame(Bitmap bitmap, int color) {
		Bitmap bitmap2 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap2);
		Rect rect = canvas.getClipBounds();
		rect.bottom--;
		rect.right--;
		Paint recPaint = new Paint();
		recPaint.setColor(color);
		recPaint.setStyle(Paint.Style.STROKE);
		canvas.drawRect(rect, recPaint);
		canvas.drawBitmap(bitmap, 0, 0, null);
		return bitmap2;
	}

	/**
	 * 字节转图片
	 * 
	 * @author gdpancheng@gmail.com 2013-10-12 下午3:58:03
	 * @param data
	 * @return Bitmap
	 */
	public static Bitmap getBitmap(byte[] data) {
		return BitmapFactory.decodeByteArray(data, 0, data.length);
	}

	/**
	 * 图片转字节
	 * 
	 * @author gdpancheng@gmail.com 2013-10-12 下午3:57:54
	 * @param bitmap
	 * @return byte[]
	 */
	public static byte[] getBytes(Bitmap bitmap) {
		ByteArrayOutputStream baops = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 0, baops);
		return baops.toByteArray();
	}
}
