package com.android.pc.ioc.a.demo;

/*
 * Author: pan Email:gdpancheng@gmail.com
 * Created Date:2013-9-27
 * Copyright @ 2013 BU
 * Description: 类描述
 *
 * History:
 */
public class Children {

	private String name;
	private int age;
	private boolean isTure;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public boolean isTure() {
		return isTure;
	}

	public void setTure(boolean isTure) {
		this.isTure = isTure;
	}
	@Override
	public String toString() {
		return "Children [name=" + name + ", age=" + age + ", isTure=" + isTure + "]";
	}
}
