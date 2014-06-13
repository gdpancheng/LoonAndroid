package com.android.pc.ioc.a.demo;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.android.pc.ioc.event.EventBus;
import com.android.pc.ioc.image.ImageDownloader;
import com.android.pc.ioc.image.Utils;
import com.android.pc.ioc.inject.InjectBefore;
import com.android.pc.ioc.inject.InjectInit;
import com.android.pc.ioc.inject.InjectListener;
import com.android.pc.ioc.inject.InjectMethod;
import com.android.pc.ioc.inject.InjectPullRefresh;
import com.android.pc.ioc.inject.InjectView;
import com.android.pc.ioc.view.PullToRefreshManager;
import com.android.pc.ioc.view.listener.OnClick;
import com.android.pc.util.Handler_Inject;
import com.wash.activity.R;

/**
 * 只要在ListView上加上了@InjectView(isasy=true)标签 isasy = true表示listview中的图片 自动滑动停止加载 TODO(这里用一句话描述这个类的作用)
 * 
 * @author gdpancheng@gmail.com 2014-1-22 下午10:35:40
 */
public class SecondFragment extends BaseFragment {

	@InjectView(pull = true,down = true)
	ListView listView;

	ArrayList<String> image = new ArrayList<String>();
	public static ImageDownloader mImageFetcher = null;

	@InjectBefore
	void test() {
		System.out.println("before");
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.inflater = inflater;
		View rootView = inflater.inflate(R.layout.fragment_main2, container, false);
		Handler_Inject.injectFragment(this, rootView);
		return rootView;
	}
	
	@InjectInit
	private void init() {

		// The ImageFetcher takes care of loading images into our ImageView children asynchronously
		mImageFetcher = new ImageDownloader(getActivity(),300);
		mImageFetcher.setLoadingImage(R.drawable.ic_launcher);
		for (int i = 0; i < 1000; i++) {
			image.add("http://bcs.duapp.com/question-image/201212310556374291.jpg?b=a" + i);
		}
		listView.setAdapter(new ImageListAdapter(activity, image));
		listView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView absListView, int scrollState) {
				// Pause fetcher to ensure smoother scrolling when flinging
				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
					// Before Honeycomb pause image loading on scroll to help with performance
					if (!Utils.hasHoneycomb()) {
						mImageFetcher.setPauseWork(true);
					}
				} else {
					mImageFetcher.setPauseWork(false);
				}
			}

			@Override
			public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
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

	@InjectPullRefresh
	private void call(int type) {
		PullToRefreshManager.getInstance().onFooterRefreshComplete();
		//完成 刷新
		PullToRefreshManager.getInstance().onHeaderRefreshComplete();
	}
}
