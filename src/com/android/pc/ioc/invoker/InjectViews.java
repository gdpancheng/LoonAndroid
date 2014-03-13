package com.android.pc.ioc.invoker;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.pc.ioc.app.ApplicationBean;
import com.android.pc.ioc.image.config.GlobalConfig;
import com.android.pc.ioc.inject.InjectView;
import com.android.pc.ioc.util.InjectExcutor;
import com.android.pc.ioc.view.PullToRefreshView;
import com.android.pc.ioc.view.PullToRefreshView.OnFooterRefreshListener;
import com.android.pc.ioc.view.PullToRefreshView.OnHeaderRefreshListener;
import com.android.pc.ioc.view.listener.OnListener;

public class InjectViews extends InjectInvoker implements OnHeaderRefreshListener, OnFooterRefreshListener {

	private int id;
	private boolean isAsy;
	private boolean pull;
	private boolean down;
	private ArrayList<Views> arrayList = new ArrayList<InjectViews.Views>();
	private InjectExcutor<Activity> injectExcutor;
	private Field field;
	Method method;
	private Object object;
	private PullToRefreshView mPullToRefreshView;

	public InjectViews(int id, InjectExcutor<Activity> injectExcutor, Field field, boolean isAsy, boolean pull, boolean down) {
		this.id = id;
		this.injectExcutor = injectExcutor;
		this.field = field;
		this.isAsy = isAsy;
		this.pull = pull;
		this.down = down;
	}

	public void setViews(Views views) {
		arrayList.add(views);
	}

	public void setMethod(Method method) {
		this.method = method;
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
		this.object = beanObject;
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
		if (isAsy && ListView.class.isAssignableFrom(view.getClass())) {
			((ListView) view).setOnScrollListener(GlobalConfig.getInstance().getOnScrollLoaderListener());
			GlobalConfig config = GlobalConfig.getInstance();
			config.setOnScrollLoaderListener(null);
		}
		if ((down || pull) && ListView.class.isAssignableFrom(view.getClass())) {
			applyTo((ListView) view);
		}
		// if (isAsy && (view instanceof ListView)) {
		// }
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

	private void applyTo(View target) {
		LayoutParams lp = target.getLayoutParams();
		mPullToRefreshView = new PullToRefreshView(target.getContext());
		ViewGroup group = (ViewGroup) target.getParent();
		int index = group.indexOfChild(target);
		group.removeView(target);
		target.setLayoutParams(new LinearLayout.LayoutParams(lp.width, lp.height));
		mPullToRefreshView.addView(target, 1);
		mPullToRefreshView.onFooter();
		group.addView(mPullToRefreshView, index, lp);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		mPullToRefreshView.setFooter(pull);
		mPullToRefreshView.setHeader(down);
	}

	@Override
	public String toString() {
		return "InjectViews [id=" + id + ", arrayList=" + arrayList + "]";
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		if (this.method == null) {
			return;
		}
		method.setAccessible(true);
		try {
			method.invoke(object, InjectView.PULL);
		} catch (Exception e) {
			if (e.getMessage() != null && e.getMessage().indexOf("wrong number of arguments") != -1) {
				ApplicationBean.logger.e(object.getClass().getSimpleName() + " 方法 " + method + "参数不对 请检查\n");
			} else if (e instanceof InvocationTargetException) {
				ApplicationBean.logger.e(object.getClass().getSimpleName() + " 方法 " + method + "里面出错了 请检查\n");
				e.getCause().printStackTrace();
			}
		}
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		if (this.method == null) {
			return;
		}
		method.setAccessible(true);
		try {
			method.invoke(object, InjectView.DOWN);
		} catch (Exception e) {
			if (e.getMessage() != null && e.getMessage().indexOf("wrong number of arguments") != -1) {
				ApplicationBean.logger.e(object.getClass().getSimpleName() + " 方法 " + method + "参数不对 请检查\n");
			} else if (e instanceof InvocationTargetException) {
				ApplicationBean.logger.e(object.getClass().getSimpleName() + " 方法 " + method + "里面出错了 请检查\n");
				e.getCause().printStackTrace();
			}
		}
	}
}
