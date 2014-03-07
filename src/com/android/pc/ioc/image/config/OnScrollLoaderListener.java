package com.android.pc.ioc.image.config;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public abstract class OnScrollLoaderListener implements OnScrollListener {

	private boolean scroll_stats = false;
	private OnStop onStop;
	private boolean fling_stats = false;

	/**
	 * listview中判断是否可以下载
	 * 
	 * @author gdpancheng@gmail.com 2013-7-9 下午4:33:01
	 * @return
	 * @return boolean
	 */
	public boolean isLoader() {
		// 既滚动 又飞行 不可以下载
		if (scroll_stats && fling_stats) {
			return false;
		}
		return true;
	}

	public interface OnStop {
		public void refer(int first, int count);
	}

	private int firstVisibleItem, visibleItemCount;

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		scroll_stats = true;
		this.firstVisibleItem = firstVisibleItem;
		this.visibleItemCount = visibleItemCount;
		onScrollListener(view, firstVisibleItem, visibleItemCount, totalItemCount);
	}

	public abstract void onScrollListener(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);

	public abstract void onScrollStateChange(AbsListView view, int scrollState);

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		onScrollStateChange(view, scrollState);
		switch (scrollState) {
		case SCROLL_STATE_IDLE:
			scroll_stats = false;
			fling_stats = false;
			if (onStop!=null) {
				onStop.refer(firstVisibleItem, visibleItemCount);
            }
			break;
		case SCROLL_STATE_FLING:
			fling_stats = true;
			break;
		case SCROLL_STATE_TOUCH_SCROLL:
			scroll_stats = true;
			fling_stats = false;
			break;
		default:
			break;
		}
	}

	public OnStop getOnStop() {
		return onStop;
	}

	public void setOnStop(OnStop onStop) {
		this.onStop = onStop;
	}

}