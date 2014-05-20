package com.android.pc.ioc.image;

import java.io.File;

/**
 * 图片下载监听 只下在 不绑定imageview<br>
 * 如果需要 实现并重写其中的方法
 * @author gdpancheng@gmail.com 2014-2-28 下午10:59:44
 */
public abstract class LoaderLister {

	/**
	 * 开始下载图片 如需要请重写
	 * @author gdpancheng@gmail.com 2014-2-11 上午11:03:30
	 * @param imageView
	 * @return void
	 */
	public void startLoader(String url) {
	};

	/**
	 * 图片加载完成 回调的函数
	 * @author gdpancheng@gmail.com 2014-2-11 上午11:03:52
	 * @param bitmap
	 * @param imageView
	 * @return Bitmap
	 */
	public abstract void finishLoader(String url,File file);

	/**
	 * 下载进度 如果需要请重写
	 * @author gdpancheng@gmail.com 2014-2-11 上午11:31:44
	 * @param progress
	 * @return void
	 */
	public void progressLoader(int progress) {
	};
	
	/**
	 * 图片加载失败回调函数 如需要请重写
	 * @author gdpancheng@gmail.com 2014-2-11 上午11:03:22
	 * @param imageView
	 * @return void
	 */
	public void failLoader(String url) {
	};
}
