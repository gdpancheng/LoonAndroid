package com.android.pc.ioc.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.android.pc.ioc.util.ContextUtils;

/**
 * 用在acitivty上 标记了 当前activity的布局 来代替setcontentview
 * TODO(这里用一句话描述这个类的作用)
 * @author gdpancheng@gmail.com 2013-10-22 下午1:33:10
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectPLayer {

	/**
	 * layout
	 * TODO(这里用一句话描述这个方法的作用)
	 * @author gdpancheng@gmail.com 2014-1-21 下午1:03:13
	 * @return
	 * @return int
	 */
	public int value() default ContextUtils.ID_ZERO;
	
	/**
	 * 是否全屏 默认不是全屏
	 * @author gdpancheng@gmail.com 2014-1-21 下午1:03:01
	 * @return
	 * @return boolean
	 */
	public boolean isFull() default false;
	/**
	 * 是否有标题栏 默认有标题栏
	 * @author gdpancheng@gmail.com 2014-1-21 下午1:14:48
	 * @return
	 * @return boolean
	 */
	public boolean isTitle() default false;
}