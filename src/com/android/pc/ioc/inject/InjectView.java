package com.android.pc.ioc.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.android.pc.ioc.util.ContextUtils;
/**
 * 组件的注解
 * @author gdpancheng@gmail.com 2013-10-22 下午1:34:43
 */
@Target({ElementType.FIELD,ElementType.LOCAL_VARIABLE})
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectView {

	public static final int PULL = 1;
	public static final int DOWN = 2;
	
	public static final int PULL_CLOSE = 3;
	public static final int DOWN_CLOSE = 4;
	
	public static final int PULL_OPEN = 5;
	public static final int DOWN_OPEN = 6;
	/**
	 * @return
	 */
	public int value() default ContextUtils.ID_NONE;

	/**
	 * 是否添加滑动停止 才加载图片的监听
	 * @author gdpancheng@gmail.com 2014-3-11 下午10:13:14
	 * @return boolean
	 */
	public boolean isasy() default false;
	
	/**
	 * 上拉加载更多
	 * @author gdpancheng@gmail.com 2014-3-11 下午10:14:24
	 * @return
	 * @return boolean
	 */
	public boolean pull() default false;
	
	/**
	 * 下拉刷新
	 * @author gdpancheng@gmail.com 2014-3-11 下午10:14:43
	 * @return boolean
	 */
	public boolean down() default false;
	/**
	 * @return
	 */
	public InjectBinder[] binders() default {};
}