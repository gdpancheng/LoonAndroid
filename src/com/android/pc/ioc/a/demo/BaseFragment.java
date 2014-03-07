package com.android.pc.ioc.a.demo;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;

/*
 * Author: pan Email:gdpancheng@gmail.com
 * Created Date:2013-8-10
 * Copyright @ 2013 BU
 * Description: 类描述
 *
 * History:
 */
public class BaseFragment extends Fragment {

	protected LayoutInflater inflater;
	protected Activity activity;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
	}
}
