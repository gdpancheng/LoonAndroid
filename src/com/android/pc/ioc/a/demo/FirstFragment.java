package com.android.pc.ioc.a.demo;

import java.util.ArrayList;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectInit;
import com.android.pc.ioc.inject.InjectListener;
import com.android.pc.ioc.inject.InjectMethod;
import com.android.pc.ioc.inject.InjectView;
import com.android.pc.ioc.view.listener.OnClick;
import com.android.pc.ioc.view.listener.OnCompoundChecked;
import com.android.pc.ioc.view.listener.OnItemClick;
import com.android.pc.ioc.view.listener.OnItemLongClick;
import com.android.pc.ioc.view.listener.OnItemSelected;
import com.android.pc.ioc.view.listener.OnLongClick;
import com.android.pc.ioc.view.listener.OnRadioChecked;
import com.android.pc.ioc.view.listener.OnTextChanged;
import com.android.pc.ioc.view.listener.OnTouch;
import com.android.pc.util.Handler_Inject;
import com.wash.activity.R;

/*
 * Author: pan Email:gdpancheng@gmail.com
 * Created Date:2014-1-21
 * Copyright @ 2014 BU
 * Description: 类描述
 *
 * History:
 */
public class FirstFragment extends BaseFragment {

	@InjectView(binders = { @InjectBinder(method = "clicks", listeners = { OnItemClick.class }), @InjectBinder(method = "itemLongclick", listeners = { OnItemLongClick.class }) })
	ListView list;

	@InjectView(binders = { @InjectBinder(method = "itemSelected", listeners = { OnItemSelected.class }) })
	Spinner spinner;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.inflater = inflater;
		View rootView = inflater.inflate(R.layout.fragment_main1, container, false);
		// fragment注解必须走这里
		Handler_Inject.injectView(this, rootView);
		return rootView;
	}

	@InjectInit
	private void init() {
		ArrayList<String> key = new ArrayList<String>();
		key.add("第一个");
		key.add("第二个");
		key.add("第三个");
		key.add("第四个");
		list.setAdapter(new ListViewAdpter(activity, key));
		key.add("第四个");
		key.add("第四个");
		key.add("第四个");
		key.add("第四个");
		key.add("第四个");
		key.add("第四个");
		key.add("第四个");
		spinner.setAdapter(new ListViewAdpter(activity, key));
	}

	public void clicks(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		MakeToast("列表单击");
	}

	public boolean itemLongclick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		MakeToast("列表长按");
		return true;
	}

	public void itemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		MakeToast("列表选择");
	}

	// -----------------------------------------------------------------------------------------------------------
	// 以下是各个事件的注入 这种注入是在你不需要声明该组建对象的时候使用
	// 你也可以在声明变量的时候 把事件绑定上去如上
	@InjectMethod(@InjectListener(ids = { R.id.onclick }, listeners = { OnClick.class }))
	private void click(View v) {
		MakeToast("单击了");
	}

	@InjectMethod(@InjectListener(ids = { R.id.checkbox1, R.id.checkbox2, R.id.checkbox3, R.id.checkbox4 }, listeners = { OnCompoundChecked.class }))
	private void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		MakeToast("多选了");
	}

	@InjectMethod(@InjectListener(ids = { R.id.onlongclick }, listeners = { OnLongClick.class }))
	private boolean onLongClick(View v) {
		MakeToast("长按了");
		return true;
	}

	@InjectMethod(@InjectListener(ids = { R.id.radio }, listeners = { OnRadioChecked.class }))
	private void onCheckedChanged(RadioGroup group, int checkedId) {
		MakeToast("单选");
	}

	@InjectMethod(@InjectListener(ids = { R.id.edit }, listeners = { OnTextChanged.class }))
	private void beforeTextChanged(CharSequence s, int start, int count, int after) {
		MakeToast("文字改变之前：" + s);
	}

	@InjectMethod(@InjectListener(ids = { R.id.edit }, listeners = { OnTextChanged.class }))
	private void afterTextChanged(Editable s) {
		MakeToast("文字改变之后：" + s);
	}

	@InjectMethod(@InjectListener(ids = { R.id.edit }, listeners = { OnTextChanged.class }))
	private void onTextChanged(CharSequence s, int start, int before, int count) {
		MakeToast("文字改变：" + s);
	}

	@InjectMethod(@InjectListener(ids = { R.id.touch }, listeners = { OnTouch.class }))
	public boolean onTouch(View v, MotionEvent event) {
		MakeToast("触摸");
		return true;
	}

	private void MakeToast(String title) {
		Toast.makeText(activity, title, Toast.LENGTH_LONG).show();
	}
}
