package com.android.pc.ioc.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.android.pc.ioc.view.listener.OnClick;
/**
 * 内部类的组件注解
 * @author gdpancheng@gmail.com 2013-10-22 下午1:34:43
 */
@Target({ElementType.TYPE,ElementType.FIELD,ElementType.LOCAL_VARIABLE})
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectAll {
	public InjectBinder value() default @InjectBinder(method = "",listeners = OnClick.class);
}