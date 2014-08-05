package com.android.pc.ioc.a.demo;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.pc.ioc.app.Ioc;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectHttp;
import com.android.pc.ioc.inject.InjectInit;
import com.android.pc.ioc.inject.InjectPullRefresh;
import com.android.pc.ioc.inject.InjectView;
import com.android.pc.ioc.internet.FastHttpHander;
import com.android.pc.ioc.internet.ResponseEntity;
import com.android.pc.ioc.view.PullToRefreshManager;
import com.android.pc.ioc.view.listener.OnClick;
import com.android.pc.util.Handler_Inject;
import com.android.pc.util.Handler_Time;
import com.wash.activity.R;

/**
 * 倒计时
 * @author gdpancheng@gmail.com 2014-1-22 下午10:37:26
 */
public class ElevenFragment extends BaseFragment {

	//pull上拉加载更多，down下拉刷新，默认false，默认关闭
	@InjectView(pull = true,down = true)
	ListView list;

	@InjectView(binders = @InjectBinder(method = "click", listeners = { OnClick.class }))
	TextView emtpy;
	
	TimeAdapter dataAdapter;
	
	ArrayList<HashMap<String, Long>> data = new ArrayList<HashMap<String,Long>>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.test_listview, container, false);
		Handler_Inject.injectFragment(this, rootView);
		return rootView;
	}
	
	@InjectInit
	private void init(){
		
		//分钟
		for (int i = 0; i < 100; i++) {
			Handler_Time time = Handler_Time.getInstance();
			Handler_Time end = Handler_Time.getInstance("2014-07-10 08:10");
			HashMap<String, Long> object = new HashMap<String, Long>();
			object.put("start", time.getTimeInMillis());
			if (i>10) {
				object.put("end", end.getTimeInMillis());
            }else {
            	object.put("limit", ((long)(i+1)*60*1000));
			}
			data.add(object);
        }
		
		//这里是设置里面的文字 可以不设置 默认有
		PullToRefreshManager.getInstance().setRelease_label("松开后刷新");
		dataAdapter = new TimeAdapter(activity,data);
		list.setAdapter(dataAdapter);
		list.setEmptyView(emtpy);
	}
	
	/**
	 * 方法名随意，但是确保参数要有int类型的，用来区分下拉和上拉
	 * @author gdpancheng@gmail.com 2014-3-12 上午9:35:15
	 * @param type
	 * @return void
	 */
	@InjectPullRefresh
	private void call(int type){
		//这里的type来判断是否是下拉还是上拉
		switch (type) {
		case InjectView.PULL:
			FastHttpHander.ajaxGet("http://211.152.52.119:8080/app/api.php?act=category",this);
			break;
		case InjectView.DOWN:
			FastHttpHander.ajaxGet("http://211.152.52.119:8080/app/api.php?act=category",this);
			break;
		}
	}
	
	private void click(View v){
		dataAdapter.notifyDataSetChanged();
	}
	
	@InjectHttp
	private void result(ResponseEntity entity){
		Ioc.getIoc().getLogger().s(entity.getContentAsString());
		//完成 加载更多
		PullToRefreshManager.getInstance().onFooterRefreshComplete();
		//完成 刷新
		PullToRefreshManager.getInstance().onHeaderRefreshComplete();
		
		//关闭顶部的下拉
		PullToRefreshManager.getInstance().headerUnable();
		//打开顶部的下拉
		PullToRefreshManager.getInstance().headerEnable();
		//关闭底部的加载
		PullToRefreshManager.getInstance().footerUnable();
		//打开顶部的加载
		PullToRefreshManager.getInstance().footerEnable();
	}
}
