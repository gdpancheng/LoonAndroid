package com.android.pc.ioc.a.demo;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wash.activity.R;

public class ImageListAdapter extends CommonAdapter {

	LayoutInflater inflater;

	public ImageListAdapter(Context context, ArrayList<String> list) {
		this.activity = context;
		this.data = list;
		inflater = ((Activity) context).getLayoutInflater();
	}

	static class ViewHolder {
		ImageView image;
	}

	private void getDrawable(String url, ImageView imageView, int position) {
		SecondFragment.mImageFetcher.loadImage(url, imageView);
	}

	@Override
	public View view(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		final String info = data.get(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item, null);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView.findViewById(R.id.image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		getDrawable(info, holder.image, position);
		return convertView;
	}
}