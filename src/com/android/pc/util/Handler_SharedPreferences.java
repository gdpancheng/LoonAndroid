package com.android.pc.util;

import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Handler_SharedPreferences {
	public static final int STRING = 0;
	public static final int INT = 1;
	public static final int BOOLEAN = 2;

	public static void WriteSharedPreferences(Context context, String dataBasesName, ThreeMap map) {
		if (map == null) {
			return;
		}
		SharedPreferences user = context.getSharedPreferences(dataBasesName, 0);
		Editor editor = user.edit();
		map.addValueInEditor(editor);
		editor.commit();
	}

	public static void ClearSharedPreferences(Context context, String dataBasesName) {
		SharedPreferences user = context.getSharedPreferences(dataBasesName, 0);
		Editor editor = user.edit();
		editor.clear();
		editor.commit();
	}

	public static void removeSharedPreferences(Context context, String dataBasesName, String key) {
		SharedPreferences user = context.getSharedPreferences(dataBasesName, 0);
		Editor editor = user.edit();
		editor.remove(key);
		editor.commit();
	}

	public static SharedPreferences ReadSharedPreferences(Context context, String dataBasesName) {
		SharedPreferences user = context.getSharedPreferences(dataBasesName, 0);
		return user;
	}

	@SuppressWarnings("unchecked")
	public static HashMap<String, Object> getAllByBasesName(Context context, String dataBasesName) {
		SharedPreferences user = context.getSharedPreferences(dataBasesName, 0);
		HashMap<String, Object> map = (HashMap<String, Object>) user.getAll();
		return map;
	}

	public static <T> T getValueByName(Context context, String dataBasesName, String key, int type) {
		SharedPreferences user = context.getSharedPreferences(dataBasesName, 0);
		Object value = null;
		switch (type) {
		case STRING:
			value = user.getString(key, "");
			break;
		case INT:
			value = user.getInt(key, 0);
			break;
		case BOOLEAN:
			value = user.getBoolean(key, false);
			break;
		}
		return (T)value;
	}

	public static void WriteSharedPreferences(Context context, String dataBasesName, String name, Object value) {
		if (name == null || value == null) {
			return;
		}
		SharedPreferences user = context.getSharedPreferences(dataBasesName, 0);
		Editor editor = user.edit();
		if (value instanceof Integer) {
			editor.putInt(name, Integer.parseInt(value.toString()));
		} else if (value instanceof Long) {
			editor.putLong(name, Long.parseLong(value.toString()));
		} else if (value instanceof Boolean) {
			editor.putBoolean(name, Boolean.parseBoolean(value.toString()));
		} else if (value instanceof String) {
			editor.putString(name, value.toString());
		} else if (value instanceof Float) {
			editor.putFloat(name, Float.parseFloat(value.toString()));
		}
		editor.commit();
	}
}
