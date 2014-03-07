package com.android.pc.ioc.a.demo;

import android.support.v4.app.Fragment;

/*
 * Author: pan Email:gdpancheng@gmail.com
 * Created Date:2014-1-22
 * Copyright @ 2014 BU
 * Description: 类描述
 *
 * History:
 */
public class FragmentEntity {
	
	@Override
    public String toString() {
	    return "FragmentEntity [fragment=" + fragment + "]";
    }

	public Fragment getFragment() {
		return fragment;
	}

	public void setFragment(Fragment fragment) {
		this.fragment = fragment;
	}

	Fragment fragment;
	
}
