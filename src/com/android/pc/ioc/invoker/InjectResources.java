package com.android.pc.ioc.invoker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import com.android.pc.ioc.app.ApplicationBean;
import com.android.pc.ioc.core.kernel.KernelLang.CauseRuntimeException;
import com.android.pc.ioc.util.InjectResouceSupply.InjectResouceType;

public class InjectResources extends InjectInvoker {

	private int id;
	private Field field;
	InjectResouceType<?> injectResouceType;
	private Class<?> inClass;

	public InjectResources(int id, Field field, InjectResouceType<?> injectResouceType,Class<?> inClass) {
		this.id = id;
		this.field = field;
		this.injectResouceType = injectResouceType;
		this.inClass = inClass;
	}

	@Override
	public void invoke(Object beanObject,Object... args) {
		Object value = injectResouceType.getResouce(id, field.getName());
		if (value == null || !field.getType().isAssignableFrom(value.getClass())) {
			ApplicationBean.logger.e(beanObject.getClass().getSimpleName() + " 对象 " + field.getName() + "赋值不对 请检查\n");
			return;
		}
		try {
			field.setAccessible(true);
			if (inClass != null) {
				Constructor<?> c = inClass.getDeclaredConstructors()[0];
				c.setAccessible(true);
				Object object = c.newInstance();
				field.set(object, value);
			} else {
				field.set(beanObject, value);
			}
		} catch (Exception e) {
			ApplicationBean.logger.e(beanObject.getClass().getSimpleName() + " 对象 " + field.getName() + "赋值不对 请检查\n");
			throw new CauseRuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return "InjectResources [id=" + id + "]";
	}

}
