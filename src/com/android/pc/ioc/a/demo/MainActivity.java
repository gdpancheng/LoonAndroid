package com.android.pc.ioc.a.demo;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.pc.ioc.app.Ioc;
import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBefore;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectInit;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.inject.InjectListener;
import com.android.pc.ioc.inject.InjectMethod;
import com.android.pc.ioc.inject.InjectResource;
import com.android.pc.ioc.inject.InjectResume;
import com.android.pc.ioc.inject.InjectView;
import com.android.pc.ioc.view.listener.OnClick;
import com.android.pc.util.Handler_TextStyle;
import com.wash.activity.R;

/**
 * @author gdpancheng@gmail.com 2014-2-21 下午5:34:26
 */
@InjectLayer(value = R.layout.activity_main, parent = R.id.common)
public class MainActivity extends BaseActivity {

	// 四种写法任选一种
	// --------------------------------------------------------------------------------------
	// 第一种写法(内部类 全局定义点击事件)
	@InjectView(binders = @InjectBinder(method = "click", listeners = { OnClick.class }))
	Button next, next3, next4, next2;
	@InjectView
	TextView test;
	@InjectResource
	String action_settings;
	@InjectResource
	Drawable ic_launcher;
	// --------------------------------------------------------------------------------------
	// 第二种写法(内部类 全局定义点击事件)
	@InjectAll(@InjectBinder(method = "click", listeners = { OnClick.class }))
	Views v;

	class Views {
		Button next, next3, next4, next2, next5;
		TextView test;
		String action_settings;
		Drawable ic_launcher;
	}

	// --------------------------------------------------------------------------------------
	// injectAll第三种写法(内部类 单独设置点击事件)
	@InjectAll
	Views2 v2;

	class Views2 {
		@InjectBinder(method = "click", listeners = { OnClick.class })
		Button next, next3, next4, next2;
		TextView test;
		String action_settings;
		Drawable ic_launcher;
	}

	// --------------------------------------------------------------------------------------
	// injectAll第四种写法(外部类)
	// 如果需要单独设置
	@InjectAll(@InjectBinder(method = "click", listeners = { OnClick.class }))
	MainView v3;

	@InjectBefore
	void call() {
		Ioc.getIoc().getLogger().s("执行在oncreat之前");
	}

	// 这个注解是在所有组件自动绑定以后自动调用
	@InjectInit
	private void init() {
		Ioc.getIoc().getLogger().s("子类的初始化" + v2.ic_launcher);
		v.test.setText("初始化完成，第一个页面");
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
		case R.id.test:
			Toast.makeText(this, "文本点击事件", Toast.LENGTH_LONG).show();
		case R.id.next5:
			startActivity(new Intent(this, FifthActivity.class));
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
		System.out.println("activity生命周期会走这里哦" + super.v.tv_top);
	}
}
