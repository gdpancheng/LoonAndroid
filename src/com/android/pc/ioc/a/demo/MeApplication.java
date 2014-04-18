package com.android.pc.ioc.a.demo;

import com.android.pc.ioc.app.ApplicationBean;

public class MeApplication extends ApplicationBean {
	
	public static MeApplication app;
	
	@Override
    public void init() {
		app = this;
		//自定义数据库的路径
		setDbDirs("/sdcard/db");
    }
}