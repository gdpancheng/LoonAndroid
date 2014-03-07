package com.android.pc.ioc.app;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.android.pc.ioc.inject.InjectBefore;
import com.android.pc.ioc.inject.InjectOnNewIntent;
import com.android.pc.ioc.inject.InjectPause;
import com.android.pc.ioc.inject.InjectRestart;
import com.android.pc.ioc.inject.InjectResume;
import com.android.pc.ioc.inject.InjectStart;
import com.android.pc.ioc.inject.InjectStop;
import com.android.pc.ioc.invoker.InjectInvoker;
import com.android.pc.ioc.invoker.InjectLayers;
import com.android.pc.ioc.util.ContextUtils;

/**
 * 替换掉系统类 TODO(这里用一句话描述这个类的作用)
 * 
 * @author gdpancheng@gmail.com 2014-2-25 下午11:13:31
 */
public class InstrumentationBean extends Instrumentation {

	@Override
	public void callActivityOnCreate(Activity activity, Bundle icicle) {
		try {
			long time = System.currentTimeMillis();
			ContextUtils.getCreateInvokers(activity.getClass());
			inject(activity, InjectBefore.class, null);
			ApplicationBean.logger.d(activity.getClass() + " 遍历生命周期和网络请求注解耗时 " + (System.currentTimeMillis() - time));
			time = System.currentTimeMillis();
			// 当前类和当前父类的注解
			ArrayList<InjectInvoker> all_inject = ContextUtils.getViewInvokers(activity.getClass(), activity, Activity.class);
			ApplicationBean.logger.d(activity.getClass() + " 遍历所有View注解耗时 " + (System.currentTimeMillis() - time));
			if (all_inject.size() > 0 && all_inject.get(0).getClass() == InjectLayers.class) {
				InjectLayers injectLayers = (InjectLayers) all_inject.get(0);
				if (injectLayers.isFull()) {// 全屏
					activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
				}
				if (injectLayers.isTile()) {// 没有标题栏
					activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
				}
			}
			super.callActivityOnCreate(activity, icicle);
			time = System.currentTimeMillis();
			int count = all_inject.size();
			for (int i = 0; i < count; i++) {
				InjectInvoker injectInvoker = all_inject.get(i);
				injectInvoker.invoke(activity);
			}
			ApplicationBean.logger.d(activity.getClass() + " 遍历UI绑定耗时 " + (System.currentTimeMillis() - time));
		} catch (Exception e) {
			StringWriter buf = new StringWriter();
			PrintWriter w = new PrintWriter(buf);
			e.printStackTrace(w);
			ApplicationBean.logger.e(activity.getClass().getSimpleName() + " 方法 OnCreate里面出错了 请检查\n" + buf.toString());
		}
	}

	private void inject(Activity activity, Class<?> clazz, Intent intent) {
		ArrayList<InjectInvoker> jArrayList = ContextUtils.getContextInvokers(activity.getClass(), clazz);
		if (jArrayList == null) {
			return;
		}
		try {
			for (InjectInvoker injectInvoker : jArrayList) {
				if (intent != null) {
					injectInvoker.invoke(activity, intent);
				} else {
					injectInvoker.invoke(activity);
				}
			}
		} catch (Exception e) {
			StringWriter buf = new StringWriter();
			PrintWriter w = new PrintWriter(buf);
			e.printStackTrace(w);
			ApplicationBean.logger.e(activity.getClass().getSimpleName() + "  里面出错了 请检查\n" + buf.toString());
		}
	}

	@Override
	public void callActivityOnNewIntent(Activity activity, Intent intent) {
		super.callActivityOnNewIntent(activity, intent);
		inject(activity, InjectOnNewIntent.class, intent);
	}

	@Override
	public void callActivityOnPause(Activity activity) {
		super.callActivityOnPause(activity);
		inject(activity, InjectPause.class, null);
	}

	@Override
	public void callActivityOnResume(Activity activity) {
		super.callActivityOnResume(activity);
		inject(activity, InjectResume.class, null);
	}

	@Override
	public void callActivityOnRestart(Activity activity) {
		super.callActivityOnRestart(activity);
		inject(activity, InjectRestart.class, null);
	}

	@Override
	public void callActivityOnStart(Activity activity) {
		super.callActivityOnStart(activity);
		inject(activity, InjectStart.class, null);
	}

	@Override
	public void callActivityOnStop(Activity activity) {
		super.callActivityOnStop(activity);
		inject(activity, InjectStop.class, null);
	}

	@Override
	public void callActivityOnDestroy(Activity activity) {
		super.callActivityOnDestroy(activity);
	}

}
