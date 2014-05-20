package com.android.pc.ioc.a.demo;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.pc.ioc.inject.InjectInit;
import com.android.pc.ioc.inject.InjectView;
import com.android.pc.util.Handler_Inject;
import com.android.pc.util.Handler_Json;
import com.wash.activity.R;

/*
 * Author: pan Email:gdpancheng@gmail.com
 * Created Date:2014-1-21
 * Copyright @ 2014 BU
 * Description: 类描述
 *
 * History:
 */
public class SixthFragment extends BaseFragment {

	@InjectView
	TextView json, map, bean;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.inflater = inflater;
		View rootView = inflater.inflate(R.layout.fragment_main6, container, false);
		Handler_Inject.injectFragment(this, rootView);
		return rootView;
	}

	@InjectInit
	private void init() {

		// 模拟json字符串

		JSONObject parent_json = new JSONObject();
		try {
			parent_json.put("name", "潘城");
			//这个属性是父类里面的属性
			parent_json.put("common", "通用");
			parent_json.put("number", 1);
			parent_json.put("isTure", true);
			JSONArray stringArray = new JSONArray();
			stringArray.put("字符串一");
			stringArray.put("字符串二");
			parent_json.put("list_string", stringArray);
			JSONArray childrenArray = new JSONArray();
			JSONObject childMJson = new JSONObject();
			childMJson.put("name", "儿子");
			childMJson.put("age", 10);
			childMJson.put("isTure", false);
			JSONObject childWJson = new JSONObject();
			childWJson.put("name", "女儿");
			childWJson.put("age", 10);
			childWJson.put("isTure", true);
			childrenArray.put(childMJson);
			childrenArray.put(childWJson);
			parent_json.put("childrens", childrenArray);
			JSONObject ortherson = new JSONObject();
			ortherson.put("name", "干儿子");
			ortherson.put("age", 10);
			ortherson.put("isTure", false);
			parent_json.put("one", ortherson);
		} catch (Exception e) {
		}

		json.setText(parent_json.toString());
		Parent event = Handler_Json.JsonToBean(Parent.class, parent_json.toString());
		bean.setText(event.toString());
		HashMap<String, Object> object = (HashMap<String, Object>) Handler_Json.JsonToCollection(parent_json.toString());
		map.setText(object.toString());
	}
}
