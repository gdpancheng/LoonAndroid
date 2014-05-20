package com.android.pc.ioc.invoker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import com.android.pc.ioc.app.ApplicationBean;
import com.android.pc.ioc.core.kernel.KernelReflect;
import com.android.pc.ioc.core.kernel.KernelLang.CauseRuntimeException;
import com.android.pc.ioc.util.InjectResouceSupply.InjectResouceType;

public class InjectResources extends InjectInvoker {

	private int id;
	private Field field;
	InjectResouceType<?> injectResouceType;
	private Class<?> inClass;
	private Field injectAllfield;

	public InjectResources(int id, Field field, InjectResouceType<?> injectResouceType, Class<?> inClass, Field field2) {
		this.id = id;
		this.field = field;
		this.injectResouceType = injectResouceType;
		this.inClass = inClass;
		this.injectAllfield = field2;
	}

	@Override
	public void invoke(Object beanObject, Object... args) {
		Object value = injectResouceType.getResouce(id, field.getName());
		if (value == null || !field.getType().isAssignableFrom(value.getClass())) {
			ApplicationBean.logger.e(beanObject.getClass().getSimpleName() + " 对象 " + field.getName() + "赋值不对 请检查\n");
			return;
		}

		try {
			if (injectAllfield == null) {
				field.setAccessible(true);
				field.set(beanObject, value);
				return;
			}
			injectAllfield.setAccessible(true);
			Object values = injectAllfield.get(beanObject);
			if (null == values) {
				if (inClass.getDeclaringClass() == null) {
					values = inClass.newInstance();
				} else {
					Constructor<?>[] c = inClass.getDeclaredConstructors();
					c[0].setAccessible(true);
					values = c[0].newInstance(beanObject);
				}
				KernelReflect.set(beanObject, injectAllfield, values);
			}
			field.setAccessible(true);
			field.set(values, value);
		} catch (Exception e) {
			e.printStackTrace();
			ApplicationBean.logger.e(beanObject.getClass().getSimpleName() + " 对象 " + field.getName() + "赋值不对 请检查\n");
			throw new CauseRuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return "InjectResources [id=" + id + "]";
	}

}
