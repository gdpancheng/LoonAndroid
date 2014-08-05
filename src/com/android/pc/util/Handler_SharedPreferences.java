package com.android.pc.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;

import com.android.pc.ioc.app.Ioc;

public class Handler_SharedPreferences {
	public static final int STRING = 0;
	public static final int INT = 1;
	public static final int BOOLEAN = 2;

	public static void WriteSharedPreferences(String dataBasesName, ThreeMap map) {
		if (map == null) {
			return;
		}
		SharedPreferences user = Ioc.getIoc().getApplication().getSharedPreferences(dataBasesName, 0);
		Editor editor = user.edit();
		map.addValueInEditor(editor);
		editor.commit();
	}

	public static void ClearSharedPreferences(String dataBasesName) {
		SharedPreferences user = Ioc.getIoc().getApplication().getSharedPreferences(dataBasesName, 0);
		Editor editor = user.edit();
		editor.clear();
		editor.commit();
	}

	public static void removeSharedPreferences(String dataBasesName, String key) {
		SharedPreferences user = Ioc.getIoc().getApplication().getSharedPreferences(dataBasesName, 0);
		Editor editor = user.edit();
		editor.remove(key);
		editor.commit();
	}

	public static SharedPreferences ReadSharedPreferences(String dataBasesName) {
		SharedPreferences user = Ioc.getIoc().getApplication().getSharedPreferences(dataBasesName, 0);
		return user;
	}

	@SuppressWarnings("unchecked")
	public static HashMap<String, Object> getAllByBasesName(String dataBasesName) {
		SharedPreferences user = Ioc.getIoc().getApplication().getSharedPreferences(dataBasesName, 0);
		HashMap<String, Object> map = (HashMap<String, Object>) user.getAll();
		return map;
	}

	public static <T> T getValueByName(String dataBasesName, String key, int type) {
		SharedPreferences user = Ioc.getIoc().getApplication().getSharedPreferences(dataBasesName, 0);
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
		return (T) value;
	}

	public static void WriteSharedPreferences(String dataBasesName, String name, Object value) {
		if (name == null || value == null) {
			return;
		}
		SharedPreferences user = Ioc.getIoc().getApplication().getSharedPreferences(dataBasesName, 0);
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

	public static void saveObject(String data, String key, Object object) {
		SharedPreferences preferences = Ioc.getIoc().getApplication().getSharedPreferences(data, Context.MODE_PRIVATE);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			String oAuth_Base64 = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
			Editor editor = preferences.edit();
			editor.putString(key, oAuth_Base64);
			editor.commit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static <T> T readObject(String data, String key) {
		SharedPreferences preferences = Ioc.getIoc().getApplication().getSharedPreferences(data, Context.MODE_PRIVATE);
		return readObject(preferences, key);
	}

	public static <T> T readObject(SharedPreferences preferences, String key) {
		Object object = null;
		String string = preferences.getString(key, "");
		if (string == "") {
			return null;
		}
		byte[] base64 = Base64.decode(string.getBytes(), Base64.DEFAULT);
		ByteArrayInputStream bais = new ByteArrayInputStream(base64);
		try {
			ObjectInputStream bis = new ObjectInputStream(bais);
			object = bis.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (T) object;
	}

	public static <T> HashMap<String, T> readObject(String data) {
		SharedPreferences preferences = Ioc.getIoc().getApplication().getSharedPreferences(data, Context.MODE_PRIVATE);
		return readObject(preferences);
	}

	public static <T> HashMap<String, T> readObject(SharedPreferences preferences) {
		Object object = null;
		HashMap<String, Object> map = (HashMap<String, Object>) preferences.getAll();
		HashMap<String, T> datas = new HashMap<String, T>();
		for (String key : map.keySet()) {
			byte[] base64 = Base64.decode(((String) map.get(key)).getBytes(), Base64.DEFAULT);
			ByteArrayInputStream bais = new ByteArrayInputStream(base64);
			try {
				ObjectInputStream bis = new ObjectInputStream(bais);
				object = bis.readObject();
				datas.put(key, (T) object);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return datas;
	}
}
