package com.android.pc.ioc.a.demo;

import com.android.pc.ioc.inject.InjectLayer;
import com.wash.activity.R;

@InjectLayer(value = R.layout.activity_main2, parent = R.id.common, isFull = true, isTitle = true)
public class SecondActivity extends BaseActivity {
	@Override
    public void list() {
	    System.out.println("---------------------------");
    }
}
