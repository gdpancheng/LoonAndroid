package com.android.pc.ioc.a.demo;

import com.android.pc.ioc.app.ApplicationBean;
import com.bluemobi.exception.ExceptionHandler;

public class MeApplication extends ApplicationBean {
	
	public static MeApplication app;
	
	@Override
    public void init() {
		app = this;
		//自定义数据库的路径
		setDbDirs("/sdcard/db");
		
		//用来获取错误报告
		ExceptionHandler handler = ExceptionHandler.getInstance(this);
		Thread.setDefaultUncaughtExceptionHandler(handler);
    }
}