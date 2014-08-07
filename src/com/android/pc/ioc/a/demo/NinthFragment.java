package com.android.pc.ioc.a.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.pc.ioc.download.FileLoaderManager;
import com.android.pc.ioc.download.FileResultEntity;
import com.android.pc.ioc.download.NotfiEntity;
import com.android.pc.ioc.event.EventBus;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectInit;
import com.android.pc.ioc.inject.InjectView;
import com.android.pc.ioc.view.listener.OnClick;
import com.android.pc.util.Handler_Inject;
import com.wash.activity.R;

/**
 * 版本更新 和 多线程断点下载类似
 * 
 * @author gdpancheng@gmail.com 2014-2-20 下午2:51:01
 */
public class NinthFragment extends BaseFragment {

	@InjectView
	TextView content;
	// 支持断点下载的链接
	String url = "http://gd.yyjzt.com/KingPharmacist.apk ";

	EventBus eventBus = EventBus.getDefault();

	@InjectView(binders = @InjectBinder(method = "click", listeners = { OnClick.class }))
	Button down1, down2, down5, down6;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.inflater = inflater;
		View rootView = inflater.inflate(R.layout.fragment_main8, container, false);
		Handler_Inject.injectFragment(this, rootView);
		return rootView;
	}

	@InjectInit
	void init() {
		// 清楚所有下载历史记录
		// FileLoaderManager.clearHistory();
		eventBus.register(this);
	}

	private void click(View v) {

		// ---------------------------------------------------------------
		if (v.getTag() != null && v.getTag().toString().equals("start")) {
			down1.setEnabled(true);
			down2.setEnabled(true);
		} else if (v.getId() != R.id.down5 && v.getId() != R.id.down6) {
			down1.setEnabled(false);
			down2.setEnabled(false);
		}
		// ---------------------------------------------------------------

		switch (v.getId()) {
		case R.id.down1:

			if (v.getTag() == null || v.getTag().toString().equals("pause")) {
				// 构建一个下载通知栏对象 并填充资源
				NotfiEntity notfi = new NotfiEntity();
				notfi.setLayout_id(R.layout.updatehelper_notification_progress);
				notfi.setIcon_id(R.id.updatehelper_notification_progress_icon);
				notfi.setProgress_id(R.id.updatehelper_notification_progress_pb);
				notfi.setProgress_txt_id(R.id.updatehelper_notification_progress_tv);
				FileLoaderManager.downloadUpdate(url, 3, notfi);
				((Button) v).setText("更新停止");
				v.setTag("start");
			} else {
				FileLoaderManager.stop(url);
				v.setTag("pause");
				content.setText("更新停止......");
				((Button) v).setText(R.string.down7);
			}

			down1.setEnabled(true);
			break;
		case R.id.down2:

			if (v.getTag() == null || v.getTag().toString().equals("pause")) {
				// 构建一个下载通知栏对象 并填充资源
				FileLoaderManager.downloadUpdate(url, 3);
				((Button) v).setText("更新停止");
				v.setTag("start");
			} else {
				FileLoaderManager.stop(url);
				v.setTag("pause");
				content.setText("更新停止......");
				((Button) v).setText(R.string.down8);
			}

			down2.setEnabled(true);
			break;
		case R.id.down5:
			NotfiEntity notfi = new NotfiEntity();
			notfi.setLayout_id(R.layout.updatehelper_notification_progress);
			notfi.setIcon_id(R.id.updatehelper_notification_progress_icon);
			notfi.setProgress_id(R.id.updatehelper_notification_progress_pb);
			notfi.setProgress_txt_id(R.id.updatehelper_notification_progress_tv);
			FileLoaderManager.showNotif(url, notfi);
			break;
		case R.id.down6:
			FileLoaderManager.hideNotif(url);
			break;
		default:
			break;
		}
	}

	// 这里接收下载进度和状态广播 这个方法名固定和参数固定 只要有这个方法
	// 并且注册了eventBus.register(this); 那么任何文件下载 在该方法都可以监听到
	public void onEventMainThread(FileResultEntity entity) {
		System.out.println(entity);
		if (entity.getStatus() == FileResultEntity.status_sucess) {
			content.setText("成功下载......");
			down1.setEnabled(true);
			down2.setEnabled(true);
		} else if (entity.getStatus() == FileResultEntity.status_fail) {
			content.setText("停止下载......");
			down1.setEnabled(true);
			down2.setEnabled(true);
		} else if (entity.getStatus() == FileResultEntity.status_loading) {
			content.setText("下载进度......" + entity.getProgress());
			System.out.println(entity.getProgress());
		}
	}
}
