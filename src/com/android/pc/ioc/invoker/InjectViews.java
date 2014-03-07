package com.android.pc.ioc.invoker;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.view.View;
import android.widget.ListView;

import com.android.pc.ioc.app.ApplicationBean;
import com.android.pc.ioc.image.config.GlobalConfig;
import com.android.pc.ioc.view.InjectExcutor;
import com.android.pc.ioc.view.listener.OnListener;

public class InjectViews extends InjectInvoker {

	private int id;
	private boolean isAsy;
	private ArrayList<Views> arrayList = new ArrayList<InjectViews.Views>();
	private InjectExcutor<Activity> injectExcutor;
	private Field field;

	public InjectViews(int id, InjectExcutor<Activity> injectExcutor, Field field, boolean isAsy) {
		this.id = id;
		this.injectExcutor = injectExcutor;
		this.field = field;
		this.isAsy = isAsy;
	}

	public void setViews(Views views) {
		arrayList.add(views);
	}

	public class Views {

		public String method;
		public Class[] listeners;

		public Views(String method, Class[] listeners) {
			this.method = method;
			this.listeners = listeners;
		}

		@Override
		public String toString() {
			return "Views [method=" + method + ", listeners=" + Arrays.toString(listeners) + "]";
		}
	}

	@Override
	public void invoke(Object beanObject, Object... args) {
		View view;
		if (injectExcutor.getObject() != null) {// 说明是view
			view = injectExcutor.findViewById(id);
		} else {
			view = injectExcutor.findViewById((Activity) beanObject, id);
		}
		if (view == null) {
			ApplicationBean.logger.e(beanObject.getClass().getSimpleName() + " 对象 " + field.getName() + " ID:" + id + "不对 无法查找到view 请检查\n");
			return;
		}
		if (isAsy &&ListView.class.isAssignableFrom(view.getClass())) {
			((ListView) view).setOnScrollListener(GlobalConfig.getInstance().getOnScrollLoaderListener());
			GlobalConfig config = GlobalConfig.getInstance();
			config.setOnScrollLoaderListener(null);
		}
//		if (isAsy && (view instanceof ListView)) {
//		}
		for (Views clazz : arrayList) {
			for (Class<? extends OnListener> listenerClass : clazz.listeners) {
				try {
					OnListener listener = listenerClass.newInstance();
					listener.listener(view, beanObject, clazz.method);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (view == null || !field.getType().isAssignableFrom(view.getClass())) {
			ApplicationBean.logger.e(beanObject.getClass().getSimpleName() + " 对象 " + field.getName() + "赋值不对 请检查\n");
			return;
		}
		try {
			field.setAccessible(true);
			field.set(beanObject, view);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "InjectViews [id=" + id + ", arrayList=" + arrayList + "]";
	}
}
