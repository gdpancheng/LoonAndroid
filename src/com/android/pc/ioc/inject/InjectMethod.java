package com.android.pc.ioc.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 方法注解类
 * @author gdpancheng@gmail.com 2013-10-22 下午1:34:16
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectMethod {

	/**
	 * @return
	 */
	InjectListener[] value();
}
