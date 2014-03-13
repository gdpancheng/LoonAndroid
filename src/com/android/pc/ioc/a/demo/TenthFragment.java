package com.android.pc.ioc.a.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.pc.ioc.inject.InjectHttp;
import com.android.pc.ioc.inject.InjectInit;
import com.android.pc.ioc.inject.InjectPullRefresh;
import com.android.pc.ioc.inject.InjectView;
import com.android.pc.ioc.internet.FastHttpHander;
import com.android.pc.ioc.internet.ResponseEntity;
import com.android.pc.ioc.view.PullToRefreshManager;
import com.android.pc.util.Handler_Inject;
import com.wash.activity.R;

/**
 * <h1>切记：</h1>
 * assets中必须放两张图片,一张up,一张down<br>
 * ************************************<br>
 * 自动注入下拉刷新 上拉加载更多<br>
 * 很清晰的一个类<br>
 * 你需要做的只有两个:<br>
 * 一个是你的adapter<br>
 * 一个就是获取网络数据
 * @author gdpancheng@gmail.com 2014-1-22 下午10:37:26
 */
public class TenthFragment extends BaseFragment {

	//pull上拉加载更多，down下拉刷新，默认false，默认关闭
	@InjectView(pull = true,down = true)
	ListView list;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.test_listview, container, false);
		Handler_Inject.injectView(this, rootView);
		return rootView;
	}
	
	@InjectInit
	private void init(){
		//这里是设置里面的文字 可以不设置 默认有
		PullToRefreshManager.getInstance().setRelease_label("松开后刷新");
		list.setAdapter(new DataAdapter(activity));
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
	
	@InjectHttp
	private void result(ResponseEntity entity){
		MeApplication.logger.s(entity.getContentAsString());
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
