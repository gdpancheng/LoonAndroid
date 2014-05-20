package com.android.pc.ioc.a.demo;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.pc.ioc.a.demo.ImageListAdapter.ViewHolder;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectView;
import com.android.pc.ioc.view.listener.OnClick;
import com.android.pc.util.Handler_Inject;
import com.wash.activity.R;


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
