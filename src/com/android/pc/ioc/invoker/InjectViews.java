package com.android.pc.ioc.invoker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.pc.ioc.app.Ioc;
import com.android.pc.ioc.core.kernel.KernelReflect;
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
	private Field injectAllfield;
	Method method;
	private Object object;
	private PullToRefreshView mPullToRefreshView;
	private Class<?> inClass;

	public InjectViews(int id, InjectExcutor<Activity> injectExcutor, Field field, boolean isAsy, boolean pull, boolean down, Class<?> inClass, Field field2) {
		this.id = id;
		this.injectExcutor = injectExcutor;
		this.field = field;
		this.isAsy = isAsy;
		this.pull = pull;
		this.down = down;
		this.inClass = inClass;
		this.injectAllfield = field2;
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
			Ioc.getIoc().getLogger().e(beanObject.getClass().getSimpleName() + " 对象 " + field.getName() + " ID:" + id + "不对 无法查找到view 请检查\n");
			return;
		}
		if (isAsy && ListView.class.isAssignableFrom(view.getClass())) {
//			((ListView) view).setOnScrollListener(GlobalConfig.getInstance().getOnScrollLoaderListener());
//			GlobalConfig config = GlobalConfig.getInstance();
//			config.setOnScrollLoaderListener(null);
		}
		if ((down || pull) && ListView.class.isAssignableFrom(view.getClass())) {
			applyTo((ListView) view);
		}
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
			Ioc.getIoc().getLogger().e(beanObject.getClass().getSimpleName() + " 对象 " + field.getName() + "赋值不对 请检查\n");
			return;
		}
		try {

			if (injectAllfield==null) {
				field.setAccessible(true);
				field.set(beanObject, view);
	            return;
            }
			injectAllfield.setAccessible(true);
			Object values = injectAllfield.get(this.object);
			if (null==values) {
				if (inClass.getDeclaringClass() == null) {
					values =inClass.newInstance();
                }else {
                	Constructor<?>[] c = inClass.getDeclaredConstructors();
                	c[0].setAccessible(true);
                	values = c[0].newInstance(this.object);
				}
				KernelReflect.set(this.object, injectAllfield, values);
            }
			field.setAccessible(true);
			field.set(values, view);
			
			// field.setAccessible(true);
			// if (inClass != null) {
			// Constructor<?> c = inClass.getDeclaredConstructors()[0];
			// c.setAccessible(true);
			// Object object = c.newInstance();
			// field.set(object, view);
			// } else {
			// field.set(beanObject, view);
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Map<String, String> readClassAttr(Object tb) throws Exception {
		Field[] fields = tb.getClass().getDeclaredFields();
		String keyList = "";
		String valueList = "";
		for (Field field : fields) {
			field.setAccessible(true);
			if (field.get(tb) != null && !"".equals(field.get(tb).toString())) {
				keyList += "," + field.getName();
				if ("a".equals(field.getName())) {
					valueList += "," + "特殊格式哦";
				} else {
					valueList += "," + field.get(tb);
				}
			}
		}
		Map<String, String> maps = new HashMap<String, String>();
		maps.put("keys", keyList);
		maps.put("values", valueList);
		return maps;
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
				Ioc.getIoc().getLogger().e(object.getClass().getSimpleName() + " 方法 " + method + "参数不对 请检查\n");
			} else if (e instanceof InvocationTargetException) {
				Ioc.getIoc().getLogger().e(object.getClass().getSimpleName() + " 方法 " + method + "里面出错了 请检查\n");
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
				Ioc.getIoc().getLogger().e(object.getClass().getSimpleName() + " 方法 " + method + "参数不对 请检查\n");
			} else if (e instanceof InvocationTargetException) {
				Ioc.getIoc().getLogger().e(object.getClass().getSimpleName() + " 方法 " + method + "里面出错了 请检查\n");
				e.getCause().printStackTrace();
			}
		}
	}
}
