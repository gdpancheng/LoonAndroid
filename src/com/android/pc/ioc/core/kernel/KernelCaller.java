package com.android.pc.ioc.core.kernel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("rawtypes")
public class KernelCaller {

	/** target */
	protected Object target;

	/** method */
	protected Method method;

	/**
	 * @param target
	 * @param method
	 */
	public KernelCaller(Object target, Method method) {
		this.target = target;
		this.method = method;
	}

	/**
	 * @param target
	 * @param methodName
	 * @param parameterTypes
	 */
	public KernelCaller(Object target, String methodName, Class... parameterTypes) {
		this(target, methodName, false, parameterTypes);
	}

	/**
	 * @param target
	 * @param methodName
	 * @param assignable
	 * @param parameterTypes
	 */
	public KernelCaller(Object target, String methodName, boolean assignable, Class... parameterTypes) {
		this.target = target;
		Class targetClass = (target instanceof Class) ? (Class) target : target.getClass();
		this.method = KernelReflect.assignableMethod(targetClass, methodName, 0, false, assignable, false, parameterTypes);
		if (targetClass == target) {
			method = KernelReflect.memberStatic(method);
		}
	}

	/**
	 * @return the target
	 */
	public Object getTarget() {
		return target;
	}

	/**
	 * @return the method
	 */
	public Method getMethod() {
		return method;
	}

	/**
	 * @param args
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public Object invoke(Object... args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		return method.invoke(target, args);
	}
}
