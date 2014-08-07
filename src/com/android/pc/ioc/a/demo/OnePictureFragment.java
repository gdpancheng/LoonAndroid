package com.android.pc.ioc.a.demo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.pc.ioc.image.DisplayerLister;
import com.android.pc.ioc.image.ImageDownloader;
import com.android.pc.ioc.image.ImageLoadManager;
import com.android.pc.ioc.image.RecyclingImageView;
import com.android.pc.ioc.inject.InjectInit;
import com.android.pc.ioc.inject.InjectView;
import com.android.pc.util.Handler_Inject;
import com.android.pc.util.Handler_Ui;
import com.wash.activity.R;

/*
 * Author: pan Email:gdpancheng@gmail.com
 * Created Date:2014-1-21
 * Copyright @ 2014 BU
 * Description: 类描述
 *
 * History:
 */
public class OnePictureFragment extends BaseFragment {

	@InjectView
	RecyclingImageView photo;
	@InjectView
	PinProgressButton pin_progress_1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.inflater = inflater;
		View rootView = inflater.inflate(R.layout.fragment_onepicture, container, false);
		Handler_Inject.injectFragment(this, rootView);
		return rootView;
	}

	@InjectInit
	private void init() {
		ImageDownloader mImageFetcher = new ImageDownloader(getActivity(),800);
		mImageFetcher.setLoadingImage(R.drawable.ic_launcher);
		mImageFetcher.loadImage("http://pic11.nipic.com/20101110/3320946_160215810000_2.jpg" , photo,new DisplayerLister() {
			@Override
			public void startLoader(ImageView imageView) {
				System.out.println("开始下载:"+imageView);
				pin_progress_1.setVisibility(View.VISIBLE);
			    super.startLoader(imageView);
			}
			
			@Override
			public void finishLoader(Bitmap bitmap, ImageView imageView) {
				System.out.println("下载完成:"+imageView);
				pin_progress_1.setVisibility(View.GONE);
			}
			
			@Override
			public void failLoader(ImageView imageView) {
				System.out.println("下载失败:"+imageView);
				pin_progress_1.setVisibility(View.GONE);
			}
			
			@Override
			public void progressLoader(int progress, ImageView imageView) {
				pin_progress_1.setProgress(progress);
			}
		});
		//清空缓存
//		ImageLoadManager.instance().clearCache();
	}
	
}
