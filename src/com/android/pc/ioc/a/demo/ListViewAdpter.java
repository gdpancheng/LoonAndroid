package com.android.pc.ioc.a.demo;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/*
 * Author: pan Email:gdpancheng@gmail.com
 * Created Date:2014-1-22
 * Copyright @ 2014 BU
 * Description: 类描述
 *
 * History:
 */
public class ListViewAdpter extends CommonAdapter {

	public ListViewAdpter(Activity activity, ArrayList<String> data) {
	    this.activity = activity;
	    this.data = data;
    }
	
	@Override
	public View view(int position, View convertView, ViewGroup parent) {
		TextView textView = new TextView(activity);
		textView.setPadding(10, 10, 10, 10);
		textView.setText(data.get(position));
		textView.setTextColor(Color.BLACK);
		return textView;
	}

}
