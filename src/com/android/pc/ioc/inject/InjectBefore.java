package com.android.pc.ioc.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 在注入组件之前调用的方法
 * @author gdpancheng@gmail.com 2013-10-22 下午1:26:27
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectBefore {
}
