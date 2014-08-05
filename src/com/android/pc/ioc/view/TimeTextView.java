package com.android.pc.ioc.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.android.pc.ioc.event.EventBus;
import com.android.pc.ioc.util.TimeEntity;
import com.android.pc.util.Handler_Time;

/*
 * Author: Administrator Email:gdpancheng@gmail.com
 * Created Date:2014-6-19
 * Copyright @ 2014 BU
 * Description: 类描述
 *
 * History:
 */
public class TimeTextView extends TextView {


	EventBus eventBus = EventBus.getDefault();
	private long startTime = 0;
	private long limitTime = 0;

	public TimeTextView(Context context) {
		super(context);
	}

	public TimeTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TimeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		eventBus.register(this);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		eventBus.unregister(this);
	}

	public void setLimtTime(long startTime, long limitTime) {
		this.startTime = startTime;
		this.limitTime = limitTime;
		showTime();
	}
	
	public void setEndTime(long startTime, long endTime) {
		this.startTime = startTime;
		limitTime = endTime-startTime;
		showTime();
	}

	public void onEventMainThread(TimeEntity timeEntity) {
		showTime();
	}
	
	private void showTime(){
		if (startTime == 0 || limitTime == 0) {
			return;
		}
		long s = (startTime + limitTime) - System.currentTimeMillis();
		if (s<0) {
			if (finish!=null) {
				finish.finished(this);
            }
			setText("时间已到");
			return;
        }
		setText(Handler_Time.formatDuring(s));
	}
	
	public interface Finish{
		public  void  finished(TextView view);
	}
	
	public Finish getFinish() {
		return finish;
	}

	public void setFinish(Finish finish) {
		this.finish = finish;
	}
	
	private Finish finish;
}
