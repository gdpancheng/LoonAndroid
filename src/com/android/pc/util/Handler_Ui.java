package com.android.pc.util;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.ResultReceiver;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

/**
 * UI工具类
 * 
 * @author gdpancheng@gmail.com 2013-10-31 上午10:47:02
 */
public class Handler_Ui {

	/**
	 * 设置选中某一些 下拉框
	 * 
	 * @author gdpancheng@gmail.com 2013-10-12 下午3:47:51
	 * @param spinner
	 * @param selection
	 * @return void
	 */
	public static void setSelection(Spinner spinner, Object selection) {
		setSelection(spinner, selection.toString());
	}

	/**
	 * 设置选中某一些 下拉框
	 * 
	 * @author gdpancheng@gmail.com 2013-10-12 下午3:47:34
	 * @param spinner
	 * @param selection
	 * @return void
	 */
	public static void setSelection(Spinner spinner, String selection) {
		final int count = spinner.getCount();
		for (int i = 0; i < count; i++) {
			String item = spinner.getItemAtPosition(i).toString();
			if (item.equalsIgnoreCase(selection)) {
				spinner.setSelection(i);
			}
		}
	}

	/**
	 * 隐藏软键盘
	 * 
	 * @author gdpancheng@gmail.com 2013-10-12 下午3:46:33
	 * @param view
	 * @return void
	 */
	public static void hideSoftKeyboard(View view) {
		InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	/**
	 * 显示软键盘
	 * 
	 * @author gdpancheng@gmail.com 2013-10-12 下午3:46:44
	 * @param view
	 * @return void
	 */
	public static void showSoftkeyboard(View view) {
		showSoftkeyboard(view, null);
	}

	/**
	 * 显示软键盘
	 * 
	 * @author gdpancheng@gmail.com 2013-10-12 下午3:47:19
	 * @param view
	 * @param resultReceiver
	 * @return void
	 */
	public static void showSoftkeyboard(View view, ResultReceiver resultReceiver) {
		Configuration config = view.getContext().getResources().getConfiguration();
		if (config.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
			InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

			if (resultReceiver != null) {
				imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT, resultReceiver);
			} else {
				imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
			}
		}
	}

	public static void imageLLViewReset(ImageView imageView,int bitmapW,int bitmapH){
		LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
		HashMap<String, Integer> data = Handler_System.getDisplayMetrics();
		int	width = data.get(Handler_System.systemWidth);
		int	height = data.get(Handler_System.systemHeight);
		if (width>height) {
			layoutParams.width = (int) (bitmapW*1.00f/bitmapH*height);
			layoutParams.height = height;
        }else {
        	layoutParams.width =width;
			layoutParams.height =  (int) (bitmapH*1.00f/bitmapW*width);
		}
		imageView.setLayoutParams(layoutParams);
	}
	
	public static void imageRLViewReset(ImageView imageView,int bitmapW,int bitmapH){
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
		HashMap<String, Integer> data = Handler_System.getDisplayMetrics();
		int	width = data.get(Handler_System.systemWidth);
		int	height = data.get(Handler_System.systemHeight);
		if (width>height) {
			layoutParams.width = (int) (bitmapW*1.00f/bitmapH*height);
			layoutParams.height = height;
        }else {
        	layoutParams.width =width;
			layoutParams.height =  (int) (bitmapH*1.00f/bitmapW*width);
		}
		imageView.setLayoutParams(layoutParams);
	}
	
	public static void imageFLViewReset(ImageView imageView,int bitmapW,int bitmapH){
		FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) imageView.getLayoutParams();
		HashMap<String, Integer> data = Handler_System.getDisplayMetrics();
		int	width = data.get(Handler_System.systemWidth);
		int	height = data.get(Handler_System.systemHeight);
		if (width>height) {
			layoutParams.width = (int) (bitmapW*1.00f/bitmapH*height);
			layoutParams.height = height;
        }else {
        	layoutParams.width =width;
			layoutParams.height =  (int) (bitmapH*1.00f/bitmapW*width);
		}
		imageView.setLayoutParams(layoutParams);
	}
	
	public static void resetRL(View... view) {
		float rote = Handler_System.getWidthRoate();
		if (view == null || rote == 1) {
			return;
		}
		for (View view2 : view) {
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view2.getLayoutParams();
			layoutParams.leftMargin = (int) (layoutParams.leftMargin * rote);
			layoutParams.rightMargin = (int) (layoutParams.rightMargin * rote);
			layoutParams.topMargin = (int) (layoutParams.topMargin * rote);
			layoutParams.bottomMargin = (int) (layoutParams.bottomMargin * rote);
			view2.setLayoutParams(layoutParams);
		}
	}

	/**
	 * 根据分辨率设置透明按钮的大小
	 * 
	 * @author gdpancheng@gmail.com 2013-7-29 下午5:12:27
	 * @param view
	 * @return void
	 */
	public static void resetRLBack(View... view) {
		float rote = Handler_System.getWidthRoate();
		if (view == null || rote == 1) {
			return;
		}
		for (View view2 : view) {
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view2.getLayoutParams();
			layoutParams.height = (int) (layoutParams.height * rote);
			layoutParams.width = (int) (layoutParams.width * rote);
			view2.setLayoutParams(layoutParams);
		}
	}

	public static void resetLL(View... view) {
		float rote = Handler_System.getWidthRoate();
		if (view == null || rote == 1) {
			return;
		}
		for (View view2 : view) {
			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view2.getLayoutParams();
			layoutParams.leftMargin = (int) (layoutParams.leftMargin * rote);
			layoutParams.rightMargin = (int) (layoutParams.rightMargin * rote);
			layoutParams.topMargin = (int) (layoutParams.topMargin * rote);
			layoutParams.bottomMargin = (int) (layoutParams.bottomMargin * rote);
			view2.setLayoutParams(layoutParams);
		}
	}

	/**
	 * 根据分辨率设置透明按钮的大小
	 * 
	 * @author gdpancheng@gmail.com 2013-7-29 下午5:12:27
	 * @param view
	 * @return void
	 */
	public static void resetLLBack(View... view) {
		float rote = Handler_System.getWidthRoate();
		if (view == null || rote == 1) {
			return;
		}
		for (View view2 : view) {
			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view2.getLayoutParams();
			layoutParams.height = (int) (layoutParams.height * rote);
			layoutParams.width = (int) (layoutParams.width * rote);
			view2.setLayoutParams(layoutParams);
		}
	}

	public static void resetFL(View... view) {
		float rote = Handler_System.getWidthRoate();
		if (view == null || rote == 1) {
			return;
		}
		for (View view2 : view) {
			FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view2.getLayoutParams();
			layoutParams.leftMargin = (int) (layoutParams.leftMargin * rote);
			layoutParams.rightMargin = (int) (layoutParams.rightMargin * rote);
			layoutParams.topMargin = (int) (layoutParams.topMargin * rote);
			layoutParams.bottomMargin = (int) (layoutParams.bottomMargin * rote);
			view2.setLayoutParams(layoutParams);
		}
	}

	/**
	 * 根据分辨率设置透明按钮的大小
	 * 
	 * @author gdpancheng@gmail.com 2013-7-29 下午5:12:27
	 * @param view
	 * @return void
	 */
	public static void resetFLBack(View... view) {
		float rote = Handler_System.getWidthRoate();
		if (view == null || rote == 1) {
			return;
		}
		for (View view2 : view) {
			FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view2.getLayoutParams();
			layoutParams.height = (int) (layoutParams.height * rote);
			layoutParams.width = (int) (layoutParams.width * rote);
			view2.setLayoutParams(layoutParams);
		}
	}

	/**
	 * 截屏
	 * @author gdpancheng@gmail.com 2013-10-26 下午2:39:01
	 * @param activity
	 * @return Bitmap
	 */
	public static Bitmap shot(Activity activity) {
		View view = activity.getWindow().getDecorView();
		Display display = activity.getWindowManager().getDefaultDisplay();
		view.layout(0, 0, display.getWidth(), display.getHeight());
		// 允许当前窗口保存缓存信息，这样getDrawingCache()方法才会返回一个Bitmap
		view.setDrawingCacheEnabled(true);
		Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache());
		view.setDrawingCacheEnabled(false);
		return bmp;
	}

	/**
	 * 代码实现旋转的菊花效果 
	 * @author gdpancheng@gmail.com 2014-2-21 下午5:09:58
	 * @param imageView 需要旋转的图片
	 * @param drawable 旋转菊花
	 * @return void
	 */
	public static void startAnim(ImageView imageView,int drawable) {
		try {
			imageView.setScaleType(ImageView.ScaleType.CENTER);
			imageView.setImageResource(drawable);
			AnimationSet animationSet = new AnimationSet(false);
			RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
			rotateAnimation.setDuration(2000);
			rotateAnimation.setInterpolator(new LinearInterpolator());
			rotateAnimation.setRepeatMode(Animation.RESTART);
			rotateAnimation.setRepeatCount(Animation.INFINITE);
			animationSet.addAnimation(rotateAnimation);
			imageView.setAnimation(animationSet);
		} catch (Exception e) {

		}
	}

	/**
	 * 停止自定义菊花的旋转
	 * @author gdpancheng@gmail.com 2014-2-21 下午5:10:40
	 * @param imageView
	 * @return void
	 */
	public static void stopAnim(ImageView imageView) {
		try {
			imageView.clearAnimation();
			imageView.setImageBitmap(null);
		} catch (Exception e) {
		}
	}
}
