package com.android.pc.ioc.a.demo;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.pc.ioc.event.EventBus;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectInit;
import com.android.pc.ioc.inject.InjectView;
import com.android.pc.ioc.view.listener.OnItemClick;
import com.android.pc.ioc.view.listener.OnItemLongClick;
import com.android.pc.util.Handler_Inject;
import com.wash.activity.R;

/*
 * Author: pan Email:gdpancheng@gmail.com
 * Created Date:2014-1-21
 * Copyright @ 2014 BU
 * Description: 类描述
 *
 * History:
 */
public class LeftFragment extends BaseFragment {

	// 单击和长按都添加了事件
	@InjectView(binders = @InjectBinder(method = "clicks", listeners = { OnItemClick.class, OnItemLongClick.class }))
	ListView home_list;
	@InjectView
	TextView msg;

	EventBus eventBus = EventBus.getDefault();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.inflater = inflater;
		View rootView = inflater.inflate(R.layout.activity_left, container, false);
		Handler_Inject.injectFragment(this, rootView);
		return rootView;
	}

	@InjectInit
	private void init() {
		ArrayList<String> menu = new ArrayList<String>();
		menu.add("自动绑定事件 Demo");
		menu.add("图片加载 Demo");
		menu.add("网络请求 Demo");
		menu.add("跨进程通信 Demo");
		menu.add("输入框验证 Demo");
		menu.add("JSON转bean和HashMap Demo");
		menu.add("数据库 Demo");
		menu.add("文件多线程断点下载");
		menu.add("版本更新");
		menu.add("下拉刷新上拉加载更多");
		home_list.setAdapter(new LeftAdapter(activity, menu));
		// ------------------------------------------------------------------------
		eventBus.register(this);
		// eventBus.register(this, "onGet");
	}

	public void clicks(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Fragment fragment = null;
		switch (arg2) {
		case 0:
			fragment = new FirstFragment();
			break;
		case 1:
			fragment = new SecondFragment();
			break;
		case 2:
			fragment = new ThirdFragment();
			break;
		case 3:
			fragment = new FourthFragment();
			break;
		case 4:
			fragment = new FifthFragment();
			break;
		case 5:
			fragment = new SixthFragment();
			break;
		case 6:
			fragment = new SeventFragment();
			break;
		case 7:
			fragment = new EighthFragment();
			break;
		case 8:
			fragment = new NinthFragment();
			break;
		case 9:
			fragment = new TenthFragment();
			break;
		}
		FragmentEntity entity = new FragmentEntity();
		entity.setFragment(fragment);
		eventBus.post(entity);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		eventBus.unregister(this);
	}

	int num;

	// 主线程的监听（另外四种方式如下）
	public void onEventMainThread(SendEntity sendEntity) {
		num++;
		msg.setText(num + "");
	}

	// // 自定义的监听
	// public void onGet(SendEntity sendEntity) {
	// System.out.println("onGet:+收到监听信息了");
	// }
	//
	// // 异步的监听
	// public void onEventAsync(SendEntity sendEntity) {
	// System.out.println("收到监听信息了1");
	// }
	//
	// // 后台监听
	// public void onEventBackgroundThread(SendEntity sendEntity) {
	// System.out.println("收到监听信息了3");
	// }
	//
	// // 也是主线程的监听
	// public void onEvent(SendEntity sendEntity) {
	// System.out.println("收到监听信息了4");
	// }
}
