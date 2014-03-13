package com.android.pc.ioc.invoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import android.app.Activity;
import android.view.View;

import com.android.pc.ioc.app.ApplicationBean;
import com.android.pc.ioc.util.InjectExcutor;
import com.android.pc.ioc.view.listener.OnListener;

public class InjectMethods extends InjectInvoker {

	Method method;
	int[] ids;
	Class[] clazz;
	InjectExcutor<?> injectExcutor;

	public InjectMethods(Method method, int[] ids, Class[] clazz, InjectExcutor<?> injectExcutor) {
		this.method = method;
		this.clazz = clazz;
		this.ids = ids;
		this.injectExcutor = injectExcutor;
	}

	@Override
	public void invoke(Object beanObject, Object... args) {
		try {
			if (clazz == null || ids == null) {
				method.setAccessible(true);
				try {
					if (args != null && args.length > 0) {
						method.invoke(beanObject, args);
					} else {
						method.invoke(beanObject);
					}
				} catch (Exception e) {
					if (e.getMessage() != null && e.getMessage().indexOf("wrong number of arguments") != -1) {
						ApplicationBean.logger.e(beanObject.getClass().getSimpleName() + " 方法 " + method + "参数不对 请检查\n");
					} else if (e instanceof InvocationTargetException) {
						ApplicationBean.logger.e(beanObject.getClass().getSimpleName() + " 方法 " + method + "里面出错了 请检查\n");
						e.getCause().printStackTrace();
					}
				}
				return;
			}

			for (int i = 0; i < ids.length; i++) {
				int id = ids[i];
				View view;
				if (injectExcutor.getObject() != null) {// 说明是view
					view = injectExcutor.findViewById(id);
				} else {
					InjectExcutor<Activity> inject = (InjectExcutor<Activity>) injectExcutor;
					view = inject.findViewById((Activity) beanObject, id);
				}
				if (view == null) {
					ApplicationBean.logger.e(beanObject.getClass().getSimpleName() + " 方法 " + method + " 对应的ids出错\n");
					continue;
				}
				for (int j = 0; j < clazz.length; j++) {
					Class<? extends OnListener> listenerClass = clazz[j];
					OnListener listener = listenerClass.newInstance();
					listener.listener(view, beanObject, method.getName());
                }
			}

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getCause() != null) {
				e.getCause().printStackTrace();
			}
		}
	}

	@Override
	public String toString() {
		return "InjectMethods [method=" + method + ", ids=" + Arrays.toString(ids) + ", clazz=" + Arrays.toString(clazz) + "]";
	}
}
