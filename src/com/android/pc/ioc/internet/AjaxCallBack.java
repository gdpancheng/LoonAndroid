package com.android.pc.ioc.internet;

/**
 * 异步请求回调类
 * 
 * @author gdpancheng@gmail.com 2012-12-9 下午11:44:42
 */
public interface AjaxCallBack extends CallBack {

	/**
	 * 可以用来取消回调
	 * @author gdpancheng@gmail.com 2013-8-18 下午10:07:31
	 * @return
	 * @return boolean
	 */
	public abstract boolean stop();

}
