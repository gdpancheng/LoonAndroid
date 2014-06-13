package com.android.pc.ioc.a.demo;

import com.wash.activity.R;

import android.app.Activity;
import android.os.Bundle;


/**
 * 支持这种注解 单独对某个View进行注解
 * 记住 此种情况下 会和Activity本身的setContentView中的注解相冲突
 * 最好在其他类中使用
 * @author gdpancheng@gmail.com 2014-5-20 下午11:01:02
 */
public class FifthActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    ViewManager manager = new ViewManager();
	    setContentView(manager.getView());
	    //--------------------------------------------------------------------
	}
	
	
}
