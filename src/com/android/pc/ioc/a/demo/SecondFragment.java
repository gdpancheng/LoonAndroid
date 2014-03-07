package com.android.pc.ioc.a.demo;

import java.io.File;
import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.pc.ioc.event.EventBus;
import com.android.pc.ioc.image.ImageDownloader;
import com.android.pc.ioc.image.displayer.LoaderLister;
import com.android.pc.ioc.inject.InjectInit;
import com.android.pc.ioc.inject.InjectListener;
import com.android.pc.ioc.inject.InjectMethod;
import com.android.pc.ioc.inject.InjectView;
import com.android.pc.ioc.view.listener.OnClick;
import com.android.pc.util.Handler_Inject;
import com.wash.activity.R;

/**
 * 只要在ListView上加上了@InjectView(isasy=true)标签 isasy = true表示listview中的图片
 * 自动滑动停止加载
 * TODO(这里用一句话描述这个类的作用)
 * @author gdpancheng@gmail.com 2014-1-22 下午10:35:40
 */
public class SecondFragment extends BaseFragment {

	@InjectView(isasy=true)
	ListView listView;

	ArrayList<String> image = new ArrayList<String>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.inflater = inflater;
		View rootView = inflater.inflate(R.layout.fragment_main2, container, false);
		Handler_Inject.injectView(this, rootView);
		return rootView;
	}

	@InjectInit
	private void init() {
		for (int i = 0; i < 1000; i++) {
			image.add("http://panchengoyo.cf/test.jpg?b=a" + i);
		}
		listView.setAdapter(new ImageListAdapter(activity, image));
		
		//这里是图片后台下载
		ImageDownloader.download("http://www.yjz9.com/uploadfile/2012/1231/20121231055637429.jpg", new LoaderLister() {
			@Override
			public void finishLoader(String url, File file) {
				System.out.println("下载完成"+file.getPath());
			}
			@Override
			public void failLoader(String url) {
				System.out.println("下载失败");
			}
			@Override
			public void progressLoader(int progress) {
				System.out.println("下载进度:"+progress);
			}
		});
	}

	@InjectMethod(@InjectListener(ids = { R.id.next }, listeners = { OnClick.class }))
	private void click(View v) {
		EventBus eventBus = EventBus.getDefault();
		FragmentEntity fragmentEntity = new FragmentEntity();
		fragmentEntity.setFragment(new OnePictureFragment());
		eventBus.post(fragmentEntity);
	}
}
