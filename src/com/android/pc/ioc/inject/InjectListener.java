package com.android.pc.ioc.inject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.android.pc.ioc.view.listener.OnListener;

/**
 * 事件注解类
 * @author gdpancheng@gmail.com 2013-10-22 下午1:33:57
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectListener {

	/**
	 * @return
	 */
	int[] ids();

	/**
	 * @return
	 */
	Class<? extends OnListener>[] listeners();

}
