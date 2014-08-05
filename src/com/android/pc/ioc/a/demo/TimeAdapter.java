package com.android.pc.ioc.a.demo;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.pc.ioc.view.TimeTextView;
import com.wash.activity.R;

public class TimeAdapter extends BaseAdapter {
	Context mContext;
	int count = 0;
	ArrayList<HashMap<String, Long>> data;

	public TimeAdapter(Context context,ArrayList<HashMap<String, Long>> data) {
		mContext = context;
		this.data = data;
	}

	@Override
	public int getCount() {
		return this.data.size();
	}
	
	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.grid_item, null);
		}
		
		TimeTextView limit = (TimeTextView)convertView.findViewById(R.id.limit);
		
		HashMap<String, Long> map = data.get(position);
		
		if (map.containsKey("limit")) {
			limit.setLimtTime(map.get("start"), map.get("limit"));
        }
		
		if (map.containsKey("end")) {
			limit.setEndTime(map.get("start"), map.get("end"));
        }
		
		TextView tv=(TextView)convertView.findViewById(R.id.tv);
		tv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(mContext, ((TextView)v).getText(), Toast.LENGTH_SHORT).show();
			}
		});
		return convertView;
	}
	
	@Override
	public void notifyDataSetChanged() {
	    super.notifyDataSetChanged();
	    System.out.println("刷新");
	}
}