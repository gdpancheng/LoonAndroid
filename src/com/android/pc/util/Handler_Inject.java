package com.android.pc.util;

import java.util.ArrayList;

import android.view.View;

import com.android.pc.ioc.app.ApplicationBean;
import com.android.pc.ioc.invoker.InjectInvoker;
import com.android.pc.ioc.util.ContextUtils;

/**
 * 第三方注解工具类
 * 供在fragment里面使用
 * @author gdpancheng@gmail.com 2013-10-22 下午12:57:05
 */
public class Handler_Inject {

	/**
	 * fragment里面使用 自动去注入组件
	 * @author gdpancheng@gmail.com 2013-10-22 下午12:59:07
	 * @param object
	 * @param view
	 * @return void
	 */
	public static void injectView(Object object, View view) {
		long time = System.currentTimeMillis();
		ArrayList<InjectInvoker> arrayList = ContextUtils.getViewInvokers(object.getClass(), view, null);
		for (InjectInvoker injectInvoker : arrayList) {
			injectInvoker.invoke(object);
		}
		ContextUtils.getCreateInvokers(object.getClass());
		ApplicationBean.logger.d(object.getClass() + " UI加载耗时 " + (System.currentTimeMillis() - time));
	}
}
