package com.android.pc.ioc.a.demo;

import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.view.listener.OnClick;

import android.graphics.drawable.Drawable;
import android.widget.Button;
import android.widget.TextView;

/*
 * Author: Administrator Email:gdpancheng@gmail.com
 * Created Date:2014-5-5
 * Copyright @ 2014 BU
 * Description: 类描述
 *
 * History:
 */
public class MainView {
	public Button next, next3, next4;
	public Button next2;
	@InjectBinder(method = "click", listeners = { OnClick.class })
	public TextView test;
	public String action_settings;
	public Drawable ic_launcher;
}
