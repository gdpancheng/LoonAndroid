package com.android.pc.ioc.a.demo;

import com.android.pc.ioc.inject.InjectLayer;
import com.wash.activity.R;


@InjectLayer(R.layout.activity_main3)
public class ThirdActivity extends BaseActivity {
	@Override
    public void list() {
	    System.out.println("---------------------------");
    }
}
