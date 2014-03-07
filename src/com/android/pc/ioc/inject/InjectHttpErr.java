package com.android.pc.ioc.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.android.pc.ioc.util.ContextUtils;

/**
 * 网络请求注解类 用在网络请求的回调方法上
 * @author gdpancheng@gmail.com 2013-9-21 下午1:43:35
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectHttpErr {
	int[] value() default ContextUtils.ID_NONE;
}
