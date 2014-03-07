package com.android.pc.util;

import java.util.HashMap;
import java.util.Map;

import android.content.SharedPreferences.Editor;

public class ThreeMap {
	public static final String type      	= "t";
	public static final String value     	= "v";
	public static final String type_int  	= "i";
	public static final String type_long	= "l";
	public static final String type_string 	= "s";
	public static final String type_float 	= "f";
	public static final String type_boolean = "b";
	
	private static Map<String, Map<String, String>> threeMap = new HashMap<String, Map<String,String>>();;
	
	public ThreeMap(){};
	
	public ThreeMap(Map<String, Object> map) {
		for (Object key : map.keySet()) {
			Object object = map.get(key);
			if (object instanceof String) {
				setString(String.valueOf(key), String.valueOf(object));
			}else if (object instanceof Integer) {
				setInt(String.valueOf(key), Integer.parseInt(object.toString()));
			}else if (object instanceof Boolean) {
				setBoolean(String.valueOf(key), Boolean.parseBoolean(object.toString()));
			}else {
				setString(String.valueOf(key), String.valueOf(object));
			}
		}
	}
	
	public void setInt(String key,int number){
		Map<String, String> map = new HashMap<String, String>();
		map.put(type, type_int);
		map.put(value, String.valueOf(number));
		threeMap.put(key, map);
	}
	
	public void setLong(String key,long number){
		Map<String, String> map = new HashMap<String, String>();
		map.put(type, type_long);
		map.put(value, String.valueOf(number));
		threeMap.put(key, map);
	}
	
	public void setString(String key,String number){
		Map<String, String> map = new HashMap<String, String>();
		map.put(type, type_string);
		map.put(value, number);
		threeMap.put(key, map);
	}
	
	public void setFloat(String key,float number){
		Map<String, String> map = new HashMap<String, String>();
		map.put(type, type_float);
		map.put(value, String.valueOf(number));
		threeMap.put(key, map);
	}
	
	public void setBoolean(String key,boolean number){
		Map<String, String> map = new HashMap<String, String>();
		map.put(type, type_boolean);
		map.put(value, String.valueOf(number));
		threeMap.put(key, map);
	}
	
	public boolean addValueInEditor(Editor editor){
		if (editor == null || threeMap.size()==0) {
			return false;
		}
		for (String key : threeMap.keySet()) {
			String key_type = String.valueOf(threeMap.get(key).get(type));
			if (key_type.equals(type_int)) {
				editor.putInt(key, Integer.parseInt(threeMap.get(key).get(value)));
			} else if (key_type.equals(type_long)) {
				editor.putLong(key, Long.parseLong(threeMap.get(key).get(value)));
			} else if (key_type.equals(type_string)) {
				editor.putString(key, threeMap.get(key).get(value));
			} else if (key_type.equals(type_float)) {
				editor.putFloat(key, Float.parseFloat(threeMap.get(key).get(value)));
			} else if (key_type.equals(type_boolean)) {
				editor.putBoolean(key, Boolean.parseBoolean(threeMap.get(key).get(value)));
			}
		}
		return true;
	}
	
	public String toString(){
		return threeMap.toString();
	}
	
	public int getLength(){
		return threeMap.size();
	}
	
	public void remove(String key){
		threeMap.remove(key);
	}
}