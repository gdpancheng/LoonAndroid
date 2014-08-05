package com.android.pc.ioc.a.demo;

import java.util.ArrayList;
import java.util.HashMap;

import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.pc.ioc.image.ImageDownloader;
import com.android.pc.ioc.image.Utils;
import com.android.pc.ioc.inject.InjectInit;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.inject.InjectView;
import com.wash.activity.R;

@InjectLayer(value = R.layout.activity_main2, parent = R.id.common)
public class SecondActivity extends BaseActivity {

	@InjectView
	ListView lt_demo;
	ImageDownloader imageDownloader = null;
	ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();

	@InjectInit
	private void init() {

		for (int i = 0; i < 1000; i++) {
			HashMap<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("text1", "测试");
			hashMap.put("text2", "测试");
			hashMap.put("text3", "测试");
			hashMap.put("text4", "测试");
			hashMap.put("text5", "测试");
			hashMap.put("image", "http://www.yjz9.com/uploadfile/2012/1231/20121231055637429.jpg?s=" + i);
			dataList.add(hashMap);
		}

		setAdapter();
	};

	// 第一种简单方式
	private void setAdapter() {
		MyAdapter adapter = new MyAdapter(lt_demo, dataList, R.layout.list_item);
		lt_demo.setAdapter(adapter);
	}

	// 第二种需要自己传图片下载链接进去的 则需要进行以下设置
	private void setAdapter2() {
		// --------------------------------------------------------------------------------------------------
		// 如果不传ImageDownloader进去 则调用Adapter类的图片下载
		imageDownloader = new ImageDownloader(this, 200);
		MyAdapter adapter = new MyAdapter(lt_demo, dataList, R.layout.list_item){
			@Override
			public void download(ImageView view, String url) {
			    super.download(view, url);
			}
		};
		lt_demo.setAdapter(adapter);
		// 滑动停止才开始加载
		lt_demo.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView absListView, int scrollState) {
				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
					if (!Utils.hasHoneycomb()) {
						imageDownloader.setPauseWork(true);
					}
				} else {
					imageDownloader.setPauseWork(false);
				}
			}

			@Override
			public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			}
		});
	}
}
