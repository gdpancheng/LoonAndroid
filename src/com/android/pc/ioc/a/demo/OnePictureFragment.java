package com.android.pc.ioc.a.demo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.pc.ioc.image.ImageDownloader;
import com.android.pc.ioc.image.config.SingleConfig;
import com.android.pc.ioc.image.displayer.DisplayerLister;
import com.android.pc.ioc.image.displayer.FadeInAnimation;
import com.android.pc.ioc.image.view.AsyImageView;
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
	AsyImageView photo;
	@InjectView
	ImageView photo2;
	@InjectView
	PinProgressButton pin_progress_1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.inflater = inflater;
		View rootView = inflater.inflate(R.layout.fragment_onepicture, container, false);
		Handler_Inject.injectView(this, rootView);
		return rootView;
	}

	@InjectInit
	private void init() {
		Handler_Ui.startAnim(photo2, R.drawable.progress);
		SingleConfig config = new SingleConfig();
		config.setDisplayer(new DisplayerLister() {
			@Override
			public void startLoader(AsyImageView imageView) {
				System.out.println("下载进度开始");
			    super.startLoader(imageView);
			}
			@Override
			public Bitmap finishLoader(Bitmap bitmap, AsyImageView imageView) {
				pin_progress_1.setVisibility(View.GONE);
				System.out.println("下载进度结束");
				return bitmap;
			}
			
			@Override
			public void progressLoader(int progress, AsyImageView imageView) {
				System.out.println("下载进度:"+progress);
				pin_progress_1.setProgress(progress);
			    super.progressLoader(progress, imageView);
			}
		});
		config.setDisplayerAnimation(new FadeInAnimation());
		ImageDownloader.download("http://www.yjz9.com/uploadfile/2012/1231/20121231055637429.jpg",photo,config);
	}
}
