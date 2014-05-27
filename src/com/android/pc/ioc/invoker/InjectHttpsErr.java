package com.android.pc.ioc.invoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.android.pc.ioc.app.Ioc;

public class InjectHttpsErr extends InjectInvoker {

	Method method;

	public InjectHttpsErr(Method method) {
		this.method = method;
	}

	@Override
	public void invoke(Object beanObject, Object... args) {
		if (beanObject == null) {
			Ioc.getIoc().getLogger().e("接口传进来的 activity为空 , 请检查");
			return;
		}
		try {
			method.setAccessible(true);
			if (args != null && args.length > 0) {
				method.invoke(beanObject, args);
			} else {
				method.invoke(beanObject);
			}
		} catch (Exception e) {
			if (e.getMessage() != null && e.getMessage().indexOf("wrong number of arguments") != -1) {
				Ioc.getIoc().getLogger().e(beanObject.getClass().getSimpleName() + " 方法 " + method + "参数不对 请检查\n");
			} else if (e instanceof InvocationTargetException) {
				Ioc.getIoc().getLogger().e(beanObject.getClass().getSimpleName() + " 方法 " + method + "里面出错了 请检查\n");
				e.getCause().printStackTrace();
			}else {
				Ioc.getIoc().getLogger().e(beanObject.getClass().getSimpleName() + " 方法 " + method + "里面出错了 请检查\n");
				if (e.getCause()!=null) {
					e.getCause().printStackTrace();
                }else {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public String toString() {
		return "InjectHttps [method=" + method + "]";
	}

}
