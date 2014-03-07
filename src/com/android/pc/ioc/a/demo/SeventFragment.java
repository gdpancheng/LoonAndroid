package com.android.pc.ioc.a.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.pc.ioc.db.sqlite.Selector;
import com.android.pc.ioc.db.sqlite.WhereBuilder;
import com.android.pc.ioc.inject.InjectInit;
import com.android.pc.ioc.inject.InjectListener;
import com.android.pc.ioc.inject.InjectMethod;
import com.android.pc.ioc.inject.InjectView;
import com.android.pc.ioc.view.listener.OnClick;
import com.android.pc.ioc.view.listener.OnRadioChecked;
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
public class SeventFragment extends BaseFragment {

	String key = "";
	private ArrayList<String> user_lists = new ArrayList<String>();

	@InjectView
	ListView sql_list;
	@InjectView
	EditText input;
	LeftAdapter leftAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.inflater = inflater;
		View rootView = inflater.inflate(R.layout.fragment_sql, container, false);
		Handler_Inject.injectView(this, rootView);
		return rootView;
	}

	@InjectInit
	private void init() {
		leftAdapter = new LeftAdapter(activity, user_lists);
		sql_list.setAdapter(leftAdapter);
		select();
	}

	@InjectMethod(@InjectListener(ids = { R.id.add, R.id.delete, R.id.update, R.id.select }, listeners = { OnClick.class }))
	private void click(View v) {
		switch (v.getId()) {
		case R.id.add:
			add();
			break;
		case R.id.delete:
			delete();
			break;
		case R.id.update:
			break;
		case R.id.select:
			break;
		}
		select();
	}

	@InjectMethod(@InjectListener(ids = { R.id.keys }, listeners = { OnRadioChecked.class }))
	private void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.id:
			key = "id";
			break;
		case R.id.content:
			key = "content";
			break;
		case R.id.is_answer:
			key = "is_answer";
			break;
		case R.id.server_id:
			key = "server_id";
			break;
		}
	}

	private void delete() {
		String txt = input.getText().toString();
		if (key.equals("content")) {
			MeApplication.app.getDb().delete(User.class, WhereBuilder.b(key, "like", "%" + txt + "%"));
		} else if (key.equals("id") || key.equals("server_id")) {
			Pattern pattern = Pattern.compile("[0-9]*");
			if (!pattern.matcher(txt).matches()) {
				Toast.makeText(activity, "输入的和选择的不对", Toast.LENGTH_LONG).show();
				return;
			}
			MeApplication.app.getDb().delete(User.class, WhereBuilder.b(key, "=", Integer.valueOf(txt)));
		}
	}

	private void select() {
		Selector selector = Selector.from(User.class);
		selector.select(" * ");
		selector.limit(Integer.MAX_VALUE);
		List<User> users = MeApplication.app.getDb().findAll(selector);
		if (users != null) {
			user_lists.clear();
			for (User user : users) {
				user_lists.add(user.toString());
			}
		}
		leftAdapter.notifyDataSetChanged();
	}

	/**
	 * 一对多的插入
	 * 
	 * @author gdpancheng@gmail.com 2014-1-23 下午3:28:25
	 * @return void
	 */
	private void add() {
		int id = (int) (System.currentTimeMillis() % 1000);
		User user = new User();
		user.setContent("问题" + id + "XXXXXXXXXXXXXXXX");
		user.setCorrect(1);
		user.setServer_id(id);
		user.setDescribe("测试" + id);
		user.setIs_answer(false);
		user.setType("di");
		user.setTime(System.currentTimeMillis());

		ArrayList<User2> arrayList = new ArrayList<User2>();
		for (int i = 0; i < 4; i++) {
			User2 user2 = new User2();
			user2.setContent("答案" + i + "yyyyyyyyyyyyyyyyyyyyy");
			user2.setContent_des("答案二说明");
			user2.user = user;
			arrayList.add(user2);
		}

		MeApplication.app.getDb().saveBindingIdAll(arrayList);
	}
}
