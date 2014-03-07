package com.android.pc.ioc.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.android.pc.ioc.util.ContextUtils;
/**
 * activity生命周期注解类
 * 
 * @author gdpancheng@gmail.com 2013-10-22 下午1:34:43
 */
@Target({ElementType.FIELD,ElementType.LOCAL_VARIABLE})
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectView {

	/**
	 * @return
	 */
	public int value() default ContextUtils.ID_NONE;

	public boolean isasy() default false;
	
	/**
	 * @return
	 */
	public InjectBinder[] binders() default {};
}