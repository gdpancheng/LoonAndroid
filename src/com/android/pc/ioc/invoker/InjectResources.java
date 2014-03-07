package com.android.pc.ioc.invoker;

import java.lang.reflect.Field;

import com.android.pc.ioc.app.ApplicationBean;
import com.android.pc.ioc.core.kernel.KernelLang.CauseRuntimeException;
import com.android.pc.ioc.util.InjectResouceSupply.InjectResouceType;

public class InjectResources extends InjectInvoker {

	private int id;
	private Field field;
	InjectResouceType injectResouceType;

	public InjectResources(int id, Field field, InjectResouceType<?> injectResouceType) {
		this.id = id;
		this.field = field;
		this.injectResouceType = injectResouceType;
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
			field.set(beanObject, value);
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
