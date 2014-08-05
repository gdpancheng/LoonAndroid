package com.android.pc.ioc.util;

import android.os.CountDownTimer;

import com.android.pc.ioc.event.EventBus;

/*
 * Author: Administrator Email:gdpancheng@gmail.com
 * Created Date:2014-6-19
 * Copyright @ 2014 BU
 * Description: 类描述
 *
 * History:
 */
public class MyCountDownTimer extends CountDownTimer {

	public MyCountDownTimer(long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
	}

	@Override
	public void onFinish() {
		System.out.println("倒计时停止");
	}

	@Override
	public void onTick(long arg0) {
		EventBus eventBus = EventBus.getDefault();
		eventBus.post(new TimeEntity());
	}
}
