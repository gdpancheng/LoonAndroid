package com.android.pc.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import android.view.View;

import com.android.pc.ioc.app.Ioc;
import com.android.pc.ioc.inject.InjectBefore;
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
	public static void injectFragment(Object object, View view) {
		long time = System.currentTimeMillis();
		ContextUtils.getCreateInvokers(object.getClass());

		//-------------------------------------------------------------------------------------------------
		//因为fragment有些参数可能要在组件绑定之前进行初始化
		ArrayList<InjectInvoker> jArrayList = ContextUtils.getContextInvokers(object.getClass(), InjectBefore.class);
		if (jArrayList != null) {
			try {
				for (InjectInvoker injectInvoker : jArrayList) {
					injectInvoker.invoke(object);
				}
			} catch (Exception e) {
				StringWriter buf = new StringWriter();
				PrintWriter w = new PrintWriter(buf);
				e.printStackTrace(w);
				Ioc.getIoc().getLogger().e(object.getClass().getSimpleName() + "  里面出错了 请检查\n" + buf.toString());
			}
		}
		//-------------------------------------------------------------------------------------------------
		
		ArrayList<InjectInvoker> arrayList = ContextUtils.getViewInvokers(object.getClass(), view, null);
		for (InjectInvoker injectInvoker : arrayList) {
			injectInvoker.invoke(object);
		}
		Ioc.getIoc().getLogger().d(object.getClass() + " UI加载耗时 " + (System.currentTimeMillis() - time));
	}
	
	public static void injectOrther(Object object, View view) {
		long time = System.currentTimeMillis();
		ArrayList<InjectInvoker> arrayList = ContextUtils.getViewInvokers(object.getClass(), view, null);
		for (InjectInvoker injectInvoker : arrayList) {
			injectInvoker.invoke(object);
		}
		Ioc.getIoc().getLogger().d(object.getClass() + " UI加载耗时 " + (System.currentTimeMillis() - time));
	}
}
