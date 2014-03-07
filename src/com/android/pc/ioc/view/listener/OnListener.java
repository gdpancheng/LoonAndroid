/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-5-2 上午9:39:01
 */
package com.android.pc.ioc.view.listener;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.view.View;

import com.android.pc.ioc.app.ApplicationBean;
import com.android.pc.ioc.core.kernel.KernelClass;
import com.android.pc.ioc.core.kernel.KernelReflect;
import com.android.pc.ioc.core.kernel.KernelString;

/**
 * @author absir
 * 
 */
public abstract class OnListener {

	/** target */
	Object target;

	/** method */
	String method;

	/** targetMethod */
	private Method targetMethod;

	private boolean noArgs;

	/**
	 * @param view
	 * @param target
	 * @param method
	 */
	public final void listener(View view, Object target, String method) {
		if (!KernelString.isEmpty(method)) {
			this.target = target;
			this.method = method;
			listener(view);
		}
	}

	/**
	 * @param args
	 * @throws RuntimeException
	 */
	public Object invoke(Object... args) {
		try {
			if (args == null) {
				return null;
			}
			if (method != null) {
				targetMethod = KernelReflect.assignableMethod(target.getClass(), method, 0, true, KernelClass.parameterTypes(args));
				if (targetMethod == null) {
					noArgs = true;
					targetMethod = KernelReflect.declaredMethod(target.getClass(), method);
				}
				method = null;
			}

			if (targetMethod != null) {

				targetMethod.setAccessible(true);
				if (noArgs) {
					targetMethod.invoke(target);
				} else {
					targetMethod.invoke(target, args);
				}
			}
		} catch (Exception e) {
			if (e instanceof InvocationTargetException) {
				StringWriter buf = new StringWriter();
				PrintWriter w = new PrintWriter(buf);
				e.getCause().printStackTrace(w);
				ApplicationBean.logger.e(target.getClass().getSimpleName() + " 方法 " + targetMethod + "里面出错了 请检查\n" + buf.toString());
				return null;
			} else {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * @param view
	 */
	protected abstract void listener(View view);

	@Override
	public String toString() {
		return "OnListener [target=" + target + ", method=" + method + ", targetMethod=" + targetMethod + ", noArgs=" + noArgs + "]";
	}
}
