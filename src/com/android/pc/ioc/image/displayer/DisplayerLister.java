package com.android.pc.ioc.image.displayer;

import android.graphics.Bitmap;

import com.android.pc.ioc.image.view.AsyImageView;

public abstract class DisplayerLister {

	/**
	 * 开始下载图片 如需要请重写
	 * @author gdpancheng@gmail.com 2014-2-11 上午11:03:30
	 * @param imageView
	 * @return void
	 */
	public void startLoader(AsyImageView imageView) {
	};

	/**
	 * 图片加载完成 回调的函数
	 * @author gdpancheng@gmail.com 2014-2-11 上午11:03:52
	 * @param bitmap
	 * @param imageView
	 * @return Bitmap
	 */
	public abstract Bitmap finishLoader(Bitmap bitmap, AsyImageView imageView);

	/**
	 * 下载进度 如果需要请重写
	 * @author gdpancheng@gmail.com 2014-2-11 上午11:31:44
	 * @param progress
	 * @return void
	 */
	public void progressLoader(int progress, AsyImageView imageView) {
	};
	
	/**
	 * 图片加载失败回调函数 如需要请重写
	 * @author gdpancheng@gmail.com 2014-2-11 上午11:03:22
	 * @param imageView
	 * @return void
	 */
	public void failLoader(AsyImageView imageView) {

	};

}
