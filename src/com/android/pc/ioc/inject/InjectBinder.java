package com.android.pc.ioc.inject;

import com.android.pc.ioc.view.listener.OnListener;

/**
 * 用来绑定事件到组件上
 * @author gdpancheng@gmail.com 2013-10-22 下午1:26:27
 */
public @interface InjectBinder {

	/**
	 * 当前类里面所对应的点击事件
	 * @return
	 */
	String method();

	/**
	 * 当前类里面所对应的点击事件的类型
	 * @return
	 */
	Class<? extends OnListener>[] listeners();

}
