/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-6-21 下午2:45:55
 */
package com.android.pc.ioc.view;

import android.view.View;

import com.android.pc.ioc.core.kernel.KernelClass;

/**
 * @author absir
 * 
 */
public abstract class InjectExcutor<T> {

	/** type */
	protected Class<?> type;
	
	protected Object object = null;

	
	public InjectExcutor<T> setObject(Object object) {
		this.object = object;
		return this;
	}

	public Object getObject() {
		return object;
	}

	/**
	 * 
	 */
	public InjectExcutor() {
		type = KernelClass.componentClass(getClass().getGenericSuperclass());
	}

	/**
	 * @param object
	 * @param id
	 */
	public abstract void setContentView(T object, int id);

	/**
	 * @param object
	 * @param id
	 * @return
	 */
	public abstract View loadView(T object, int id);

	/**
	 * @param object
	 * @param id
	 * @return
	 */
	public abstract View findViewById(T object, int id);

	public abstract View findViewById(int id);
}
