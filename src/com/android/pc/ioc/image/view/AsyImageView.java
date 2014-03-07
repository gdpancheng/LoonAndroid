package com.android.pc.ioc.image.view;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.android.pc.ioc.image.ImageDownloader;
import com.android.pc.ioc.image.cache.FileCache;
import com.android.pc.ioc.image.cache.LoaderManager;
import com.android.pc.ioc.image.config.GlobalConfig;
import com.android.pc.ioc.image.config.SingleConfig;
import com.android.pc.ioc.image.config.Util;
import com.android.pc.util.Handler_Verify;

/**
 * 自定义view
 * @author gdpancheng@gmail.com 2013-7-8 下午4:48:20
 */
public class AsyImageView extends ImageView {

	/** 列表图片中列表的位置 如果为-1则表示不是列表 */
	private int postion = Util.ID_NONE;
	/** 图片下载链接 **/
	private String url;
	/** 单张图片的下载属性 如果没有则为空 **/
	private SingleConfig singleConfig;
	/** 只下载不显示 **/
	private boolean onlyLoader;
	private long length;
	private boolean destroy;

	public AsyImageView(Context context) {
		super(context);
	}

	public AsyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AsyImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	protected void onDraw(Canvas canvas) {
		Drawable drawable = getDrawable();
		if (drawable instanceof TransitionDrawable) {
			Bitmap bitmap = ((BitmapDrawable) ((TransitionDrawable) drawable).getDrawable(1)).getBitmap();
			if (bitmap == null) {
				super.onDraw(canvas);
				if(singleConfig!=null&&singleConfig.getDisplayer()!=null){
					singleConfig.getDisplayer().failLoader(this);
				}
				return;
			}
			Boolean isRecycled = bitmap.isRecycled();
			if (isRecycled) {
				ImageDownloader.download(url, this, singleConfig);
			}
		}
		super.onDraw(canvas);
	}

	protected void onDetachedFromWindow() {
		destroy = true;
		if (GlobalConfig.getInstance().getContext()==null) {
	        return;
        }
		LoaderManager loaderManager = GlobalConfig.getInstance().getLoaderManager();
		HashMap<String, Integer> map = ImageDownloader.getSize(this);
		int w = map.get("w");
		int h = map.get("h");
		loaderManager.recycle(FileCache.urlToFileName(url + "_" + h + "_" + w));
		/**
		 * 表示该imageview已经被销毁了，线程池有直接停止线程的方法 shutdown()方法在终止前允许执行以前提交的任务， 而 shutdownNow() 方法阻止等待任务的启动并试图停止当前正在执行的任务。 在终止后，执行程序没有任务在执行，也没有任务在等待执行，并且无法提交新任务 这两个方法都不满足我们的要求 原因: shutdown()以后之前提交的线程依然可以执行 shutdownNow() 以后 任何任务无法提交 我们必须新开一个线程池 shutdownNow() 后和新开线程池之前有时间差， 那么在你新开线程池之前万一有任务提交 则会出现异常 所以我们针对那些已经被销毁了的imageview 单独去停止其下载的线程
		 */
		super.onDetachedFromWindow();
		destroy = true;
		singleConfig = null;
	}

	public int getPostion() {
		return postion;
	}

	/**
	 * 设置当前view在listview中的postion
	 * @author gdpancheng@gmail.com 2014-2-13 下午2:46:45
	 * @param postion
	 * @return void
	 */
	public void setPostion(int postion) {
		this.postion = postion;
	}

	public String getUrl() {
		return url;
	}

	/**
	 * 设置下载的url
	 * @author gdpancheng@gmail.com 2014-2-13 下午2:46:35
	 * @param url
	 * @return void
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	public SingleConfig getSingleConfig() {
		return singleConfig;
	}

	/**
	 * 单张图片下载设置
	 * @author gdpancheng@gmail.com 2014-2-13 下午2:46:16
	 * @param singleConfig
	 * @return void
	 */
	public void setSingleConfig(SingleConfig singleConfig) {
		this.singleConfig = singleConfig;
	}

	/**
	 * 判断是否是网络请求 否则是本地图片
	 * TODO(这里用一句话描述这个方法的作用)
	 * @author gdpancheng@gmail.com 2014-2-13 下午2:43:59
	 * @return boolean
	 */
	public boolean isHttp() {
		return url.matches(Handler_Verify.http);
	}

	/**
	 * 判断是否销毁
	 * TODO(这里用一句话描述这个方法的作用)
	 * @author gdpancheng@gmail.com 2014-2-13 下午2:45:17
	 * @return
	 * @return boolean
	 */
	public boolean isDestroy() {
		return destroy;
	}

	/**
	 * 图片长度
	 * @author gdpancheng@gmail.com 2014-2-13 下午2:43:33
	 * @return
	 * @return long
	 */
	public long getLength() {
		return length;
	}

	/**
	 * 设置长度
	 * @author gdpancheng@gmail.com 2014-2-13 下午2:43:41
	 * @param length
	 * @return void
	 */
	public void setLength(long length) {
		this.length = length;
	}

	/**
	 * 设置这个参数 则对应assert里面的配置
	 * @author gdpancheng@gmail.com 2014-2-13 下午2:43:13
	 * @param temp
	 * @return void
	 */
	public void setTemplate(String temp){
		singleConfig = SingleConfig.getSingleConfig(temp);
	}
}
