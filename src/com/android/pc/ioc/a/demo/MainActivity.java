package com.android.pc.ioc.a.demo;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBefore;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectInit;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.inject.InjectListener;
import com.android.pc.ioc.inject.InjectMethod;
import com.android.pc.ioc.inject.InjectResume;
import com.android.pc.ioc.view.listener.OnClick;
import com.android.pc.util.Handler_TextStyle;
import com.wash.activity.R;

/**
 * 
 * 
 * @author gdpancheng@gmail.com 2014-2-21 下午5:34:26
 */
@InjectLayer(value = R.layout.activity_main, parent = R.id.common)
public class MainActivity extends BaseActivity {
	
	@InjectAll(@InjectBinder(method = "click", listeners = { OnClick.class }))
	static class Views {
		static Button next, next3, next4;
		static Button next2;
		static TextView test;
		static String action_settings;
		static Drawable ic_launcher;
	}

	@InjectBefore
	void call() {
		MeApplication.logger.s("执行在oncreat之前");
	}

	// 这个注解是在所有组件自动绑定以后自动调用
	@InjectInit
	void init() {
		MeApplication.logger.s("子类的初始化");
		Views.test.setText("初始化完成，第一个页面");
	}

	// 支持由参数和无参数 即click(View view)或者click() 当然click名字必须对于变量注解中的method = "click"
	private void click(View view) {
		switch (view.getId()) {
		case R.id.next:
			startActivity(new Intent(this, ThirdActivity.class));
			break;
		case R.id.next2:
			startActivity(new Intent(this, SecondActivity.class));
			break;
		case R.id.next3:
			startActivity(new Intent(this, MyFragmentActivity.class));
			break;
		case R.id.next4:
			startActivity(new Intent(this, FourActivity.class));
			break;
		}
	}

	// 底部导航栏 子类覆盖父类
	@InjectMethod(@InjectListener(ids = { R.id.bottom }, listeners = { OnClick.class }))
	private void click3(View view) {
		Handler_TextStyle handler_TextStyle = new Handler_TextStyle();
		handler_TextStyle.setString("点击了底部按钮 子类覆盖了父类");
		handler_TextStyle.setBackgroundColor(Color.RED, 3, 5);
		Toast.makeText(this, handler_TextStyle.getSpannableString(), Toast.LENGTH_LONG).show();
	}

	@InjectResume
	private void resume() {
		System.out.println("activity生命周期会走这里哦");
	}

	@Override
    public void list() {
	    System.out.println("---------------------------");
    }
}
