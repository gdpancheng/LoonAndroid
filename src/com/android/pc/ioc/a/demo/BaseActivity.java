package com.android.pc.ioc.a.demo;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectInit;
import com.android.pc.ioc.inject.InjectListener;
import com.android.pc.ioc.inject.InjectMethod;
import com.android.pc.ioc.inject.InjectPLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.android.pc.util.Handler_TextStyle;
import com.wash.activity.R;

@InjectPLayer(R.layout.activity_com)
public abstract class BaseActivity extends CommonActivity {
	
	@InjectAll
	static class Views{
		static TextView tv_top;
	}
	
	@InjectInit
	private void init() {
		MeApplication.logger.s("公共类的初始化");
		Views.tv_top.setText("父类对顶部文字进行填充");
		list();
	}
	
	public abstract void list();

	// 这里是第一种交互事件注入方式（单击）
	@InjectMethod(@InjectListener(ids = { R.id.top, R.id.bottom }, listeners = { OnClick.class }))
	private void click2(View view) {
		try {
	        list();
        } catch (Exception e) {
	        e.printStackTrace();
        }
		Handler_TextStyle handler_TextStyle = new Handler_TextStyle();
		switch (view.getId()) {
		case R.id.top:
			handler_TextStyle.setString("点击了顶部按钮(在基类中统一注册,也可以单独注册)");
			handler_TextStyle.setBackgroundColor(Color.RED, 3, 5);
			Toast.makeText(this, handler_TextStyle.getSpannableString(), Toast.LENGTH_LONG).show();
			break;
		case R.id.bottom:
			handler_TextStyle.setString("点击了底部按钮(在基类中统一注册,也可以单独注册)");
			handler_TextStyle.setBackgroundColor(Color.RED, 3, 5);
			Toast.makeText(this, handler_TextStyle.getSpannableString(), Toast.LENGTH_LONG).show();
			break;
		}
	}
}
