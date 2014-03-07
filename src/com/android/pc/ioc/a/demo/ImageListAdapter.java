package com.android.pc.ioc.a.demo;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.pc.ioc.image.ImageDownloader;
import com.android.pc.ioc.image.view.AsyImageView;
import com.wash.activity.R;

public class ImageListAdapter extends CommonAdapter {

	LayoutInflater inflater;

	public ImageListAdapter(Context context, ArrayList<String> list) {
		this.activity = context;
		this.data = list;
		inflater = ((Activity) context).getLayoutInflater();
	}

	static class ViewHolder {
		AsyImageView image;
	}

	private void getDrawable(String url, AsyImageView imageView, int position) {
		imageView.setPostion(position);
		//设置这个参数 则对应assert里面的one配置
//		imageView.setTemplate("one");
		ImageDownloader.download(url, imageView);
	}

	@Override
	public View view(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		final String info = data.get(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item, null);
			holder = new ViewHolder();
			holder.image = (AsyImageView) convertView.findViewById(R.id.image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		getDrawable(info, holder.image, position);
		return convertView;
	}
}