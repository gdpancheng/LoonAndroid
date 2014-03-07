package com.android.pc.ioc.a.demo;

import java.util.ArrayList;

/*
 * Author: pan Email:gdpancheng@gmail.com
 * Created Date:2013-9-27
 * Copyright @ 2013 BU
 * Description: 类描述
 *
 * History:
 */
public class Parent extends Entity {

	private String name;

	private int number;

	private boolean isTure;

	private ArrayList<String> list_string;

	private ArrayList<Children> childrens;

	private Children one;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public boolean isTure() {
		return isTure;
	}

	public void setTure(boolean isTure) {
		this.isTure = isTure;
	}

	public ArrayList<String> getList_string() {
		return list_string;
	}

	public void setList_string(ArrayList<String> list_string) {
		this.list_string = list_string;
	}

	public ArrayList<Children> getChildrens() {
		return childrens;
	}

	public void setChildrens(ArrayList<Children> childrens) {
		this.childrens = childrens;
	}

	public Children getOne() {
		return one;
	}

	public void setOne(Children one) {
		this.one = one;
	}
	
	@Override
    public String toString() {
	    return "Parent [name=" + name + ", number=" + number + ", isTure=" + isTure + ", list_string=" + list_string + ", childrens=" + childrens + ", one=" + one + ", common=" + common + "]";
    }
}
