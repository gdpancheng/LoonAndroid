package com.android.pc.ioc.a.demo;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/*
 * Author: pan Email:gdpancheng@gmail.com
 * Created Date:2013-9-15
 * Copyright @ 2013 BU
 * Description: 类描述
 *
 * History:
 */
public abstract class CommonAdapter extends BaseAdapter {

	public ArrayList<String> data;
	public Context activity;

	public CommonAdapter() {
    }
	
	public CommonAdapter(Activity activity, ArrayList<String> data) {
	    this.activity = activity;
	    this.data = data;
    }
	
	@Override
	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return view(position, convertView, parent);
	}

	public abstract View view(int position, View convertView, ViewGroup parent);
}
