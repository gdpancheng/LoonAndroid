/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-5-2 下午2:29:35
 */
package com.android.pc.ioc.view.listener;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.android.pc.ioc.app.Ioc;

/**
 * @author absir
 * 
 */
public class OnCompoundChecked extends OnListener implements OnCheckedChangeListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.CompoundButton.OnCheckedChangeListener#onCheckedChanged
	 * (android.widget.CompoundButton, boolean)
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		invoke(buttonView, isChecked);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.android.view.listener.Listener#listener(android.view.View)
	 */
	@Override
	protected void listener(View view) {
		// TODO Auto-generated method stub
		if (view instanceof CompoundButton) {
			((CompoundButton) view).setOnCheckedChangeListener(this);
		}else {
			Ioc.getIoc().getLogger().e(view.getClass() +" 无法设置OnCompoundChecked 请检查InjectMethod的参数\n");
		}
	}
}
