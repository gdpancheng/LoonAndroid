package com.android.pc.ioc.core.kernel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("rawtypes")
public class KernelCallerO extends KernelCaller {

	/** parameterObjects */
	protected Object[] parameterObjects;

	/**
	 * @param target
	 * @param method
	 */
	public KernelCallerO(Object target, Method method) {
		super(target, method);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param target
	 * @param methodName
	 * @param parameterTypes
	 */
	public KernelCallerO(Object target, String methodName, Class[] parameterTypes) {
		super(target, methodName, parameterTypes);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param target
	 * @param methodName
	 * @param assignable
	 * @param parameterTypes
	 */
	public KernelCallerO(Object target, String methodName, boolean assignable, Class[] parameterTypes) {
		super(target, methodName, assignable, parameterTypes);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param target
	 * @param methodName
	 * @param assignable
	 * @param parameterTypes
	 * @param args
	 */
	public KernelCallerO(Object target, String methodName, boolean assignable, Class[] parameterTypes, Object... args) {
		super(target, methodName, assignable, parameterTypes);
		setParameterObjects(args);
	}

	/**
	 * @return
	 */
	public Object[] getParameterObjects() {
		return parameterObjects;
	}

	/**
	 * @param args
	 */
	public void setParameterObjects(Object... args) {
		if (parameterObjects == null) {
			parameterObjects = new Object[method == null ? 0 : method.getParameterTypes().length];
		}

		int length = parameterObjects.length;
		int start = length - args.length;
		if (start < 0) {
			start = 0;
		}

		for (length--; length > start; length--) {
			parameterObjects[length] = args[length - start];
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.appserv.kernel.KernelUtil.CallBack#invoke(java.lang.Object[])
	 */
	@Override
	public Object invoke(Object... objects) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (parameterObjects == null) {
			parameterObjects = new Object[method == null ? 0 : method.getParameterTypes().length];
		}

		int length = parameterObjects.length;
		int end = objects.length;
		if (end > length) {
			end = length;
		}

		for (length = 0; length < end; length++) {
			if (objects[length] != KernelLang.NULL_OBJECT) {
				parameterObjects[length] = objects[length];
			}
		}

		return super.invoke(parameterObjects);
	}
}
