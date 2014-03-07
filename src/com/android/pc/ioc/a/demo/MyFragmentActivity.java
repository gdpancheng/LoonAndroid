package com.android.pc.ioc.a.demo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import com.android.pc.ioc.event.EventBus;
import com.android.pc.ioc.inject.InjectInit;
import com.android.pc.ioc.inject.InjectLayer;
import com.slidingmenu.lib.SlidingMenu;
import com.wash.activity.R;

@InjectLayer(R.layout.activity_fragment_main)
public class MyFragmentActivity extends FragmentActivity {

	public SlidingMenu menu;

	EventBus eventBus = EventBus.getDefault();
	
	@InjectInit
	private void init() {
		// ------------------------------------------------------------------------
		// 左右滑动空间
		menu = new SlidingMenu(this);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.setMode(SlidingMenu.LEFT);
		getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, new LeftFragment()).commit();
		menu.setMenu(R.layout.menu_frame);
		menu.setSecondaryShadowDrawable(R.drawable.shadow);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		// ------------------------------------------------------------------------
		startFragmentAdd(new HomeFragment());
		// ------------------------------------------------------------------------
		eventBus.register(this);
		// eventBus.register(this, "onGet");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		eventBus.unregister(this);
	}
	
	private void startFragmentAdd(Fragment fragment) {
		// ------------------------------------------------------------------------
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		Fragment to_fragment = fragmentManager.findFragmentByTag(fragment.getClass().getName());
		if (to_fragment != null) {
			for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
				BackStackEntry entry = fragmentManager.getBackStackEntryAt(i);
				if (fragment.getClass().getName().equals(entry.getName())) {
					fragmentManager.popBackStack(entry.getName(), 1);
				}
			}
		}
		fragmentTransaction.addToBackStack(fragment.getClass().getName());
		fragmentTransaction.replace(R.id.content_frame, fragment, fragment.getClass().getName()).commitAllowingStateLoss();
		// ------------------------------------------------------------------------
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) { // 按下的如果是BACK，同时没有重复
			Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
			if (fragment.getClass().getName().equals(HomeFragment.class.getName())) {
				finish();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	// 主线程的监听（另外四种方式如下）
	public void onEventMainThread(FragmentEntity fragmentEntity) {
		System.out.println("收到通知:"+fragmentEntity);
		startFragmentAdd(fragmentEntity.getFragment());
		if (menu.isMenuShowing()) {
			menu.toggle();
		}
	}

	// // 自定义的监听
	// public void onGet(FragmentEntity fragmentEntity) {
	// System.out.println("onGet:+收到监听信息了");
	// }
	//
	// // 异步的监听
	// public void onEventAsync(FragmentEntity fragmentEntity) {
	// System.out.println("收到监听信息了1");
	// }
	//
	// // 后台监听
	// public void onEventBackgroundThread(FragmentEntity fragmentEntity) {
	// System.out.println("收到监听信息了3");
	// }
	//
	// // 也是主线程的监听
	// public void onEvent(FragmentEntity fragmentEntity) {
	// System.out.println("收到监听信息了4");
	// }
}
