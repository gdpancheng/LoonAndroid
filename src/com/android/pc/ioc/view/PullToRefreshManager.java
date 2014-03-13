package com.android.pc.ioc.view;

import com.android.pc.ioc.event.EventBus;
import com.android.pc.ioc.inject.InjectView;

/**
 * 上拉加载和下拉刷新管理类 TODO(这里用一句话描述这个类的作用)
 * 
 * @author gdpancheng@gmail.com 2014-3-11 下午11:28:02
 */
public class PullToRefreshManager {

	EventBus eventBus = EventBus.getDefault();
	
	private String pull_label = "下拉刷新";
	private String release_label = "松开后刷新";
	private String footer_pull_label = "上拉加载更多";
	private String footer_refreshing_label = "加载中...";

	private String refreshing_label = "刷新中...";

	private String updateTime = "刚刚刷新";
	
	private int limit = 0;

	private static PullToRefreshManager manager;

	public static PullToRefreshManager getInstance() {
		if (manager == null) {
			manager = new PullToRefreshManager();
		}
		return manager;
	}

	public String getPull_label() {
		return pull_label;
	}

	public void setPull_label(String pull_label) {
		this.pull_label = pull_label;
	}

	public String getRelease_label() {
		return release_label;
	}

	public void setRelease_label(String release_label) {
		this.release_label = release_label;
	}

	public String getFooter_pull_label() {
		return footer_pull_label;
	}

	public void setFooter_pull_label(String footer_pull_label) {
		this.footer_pull_label = footer_pull_label;
	}

	public String getFooter_refreshing_label() {
		return footer_refreshing_label;
	}

	public void setFooter_refreshing_label(String footer_refreshing_label) {
		this.footer_refreshing_label = footer_refreshing_label;
	}

	public String getRefreshing_label() {
		return refreshing_label;
	}

	public void setRefreshing_label(String refreshing_label) {
		this.refreshing_label = refreshing_label;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public void onFooterRefreshComplete() {
		RefershEntity event = new RefershEntity();
		event.setType(InjectView.PULL);
		eventBus.post(event);
	};

	/**
	 * 任务完成
	 * @author gdpancheng@gmail.com 2014-3-13 上午11:44:10
	 * @return void
	 */
	public void onHeaderRefreshComplete() {
		RefershEntity event = new RefershEntity();
		event.setType(InjectView.DOWN);
		eventBus.post(event);
	};
	
	/**
	 * 打开底部
	 * @author gdpancheng@gmail.com 2014-3-13 上午11:44:04
	 * @return void
	 */
	public void footerEnable() {
		RefershEntity event = new RefershEntity();
		event.setType(InjectView.PULL_OPEN);
		eventBus.post(event);
	};

	/**
	 * 打开顶部
	 * @author gdpancheng@gmail.com 2014-3-13 上午11:43:55
	 * @return void
	 */
	public void headerEnable() {
		RefershEntity event = new RefershEntity();
		event.setType(InjectView.DOWN_OPEN);
		eventBus.post(event);
	};
	
	/**
	 * 关闭底部 
	 * @author gdpancheng@gmail.com 2014-3-13 上午11:43:45
	 * @return void
	 */
	public void footerUnable() {
		RefershEntity event = new RefershEntity();
		event.setType(InjectView.PULL_CLOSE);
		eventBus.post(event);
	};

	/**
	 * 关闭顶部
	 * @author gdpancheng@gmail.com 2014-3-13 上午11:43:26
	 * @return void
	 */
	public void headerUnable() {
		RefershEntity event = new RefershEntity();
		event.setType(InjectView.DOWN_CLOSE);
		eventBus.post(event);
	};

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}
}