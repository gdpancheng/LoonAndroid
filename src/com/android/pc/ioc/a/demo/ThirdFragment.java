package com.android.pc.ioc.a.demo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectHttp;
import com.android.pc.ioc.inject.InjectHttpErr;
import com.android.pc.ioc.inject.InjectHttpOk;
import com.android.pc.ioc.inject.InjectInit;
import com.android.pc.ioc.internet.FastHttp;
import com.android.pc.ioc.internet.FastHttpHander;
import com.android.pc.ioc.internet.InternetConfig;
import com.android.pc.ioc.internet.ResponseEntity;
import com.android.pc.ioc.view.listener.OnClick;
import com.android.pc.util.Handler_Inject;
import com.wash.activity.R;

/**
 * 这个demo主要讲的是 自动注入网络回掉
 * 
 * @author gdpancheng@gmail.com 2014-1-22 下午10:08:12
 */
public class ThirdFragment extends BaseFragment {
	
	@InjectAll(@InjectBinder(method="click",listeners = OnClick.class))
	Views v;
	
	class Views{
		TextView result;
		ProgressBar progress;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.inflater = inflater;
		View rootView = inflater.inflate(R.layout.fragment_main3, container, false);
		Handler_Inject.injectFragment(this, rootView);
		return rootView;
	}

	/**
	 * 这里分如下几种情况 1 没有设置key 那么回自动去寻找@InjectHttp没有设置key的方法 2 如果设置key 自动寻找和它相等key的@InjectHttp 3 如果设置key 如果找不到和它相等key的@InjectHttp 则自动寻找没有设置key的方法
	 * 
	 * @author gdpancheng@gmail.com 2014-1-22 下午10:08:42
	 * @return void
	 */
	@InjectInit
	private void init() {
		FastHttpHander.ajaxGet("http://211.152.52.1119:8080/app/api.php?act=category", this);

		InternetConfig config = new InternetConfig();
		config.setKey(1);
		FastHttpHander.ajaxGet("http://211.152.52.119:8080/app/api.php?act=category", config, this);

		InternetConfig config2 = new InternetConfig();
		config2.setKey(2);
		FastHttpHander.ajaxGet("http://211.152.52.119:8080/app/api.php?act=category", config2, this);
	}
	
	
	void click(){
		System.out.println("点击");
	}
	
	// 手动区分返回状态
	@InjectHttp
	private void result(ResponseEntity r) {
		switch (r.getStatus()) {
		case FastHttp.result_ok:

			break;
		case FastHttp.result_net_err:

			break;
		}
		v.result.append("我是result 当前key为:" + r.getKey() + "回调了\n");
		v.result.setVisibility(View.VISIBLE);
		v.progress.setVisibility(View.GONE);
	}

	@InjectHttpOk(1)
	private void resultOk(ResponseEntity r) {
		v.result.append("我是resultOk 当前key为:" + r.getKey() + "回调了\n");
		v.result.setVisibility(View.VISIBLE);
		v.progress.setVisibility(View.GONE);
	}

	@InjectHttpErr(value = { 1, 2 })
	private void resultErr(ResponseEntity r) {
		v.result.append("我是resultErr 当前key为:" + r.getKey() + "回调了\n");
		v.result.setVisibility(View.VISIBLE);
		v.progress.setVisibility(View.GONE);
	}
}
