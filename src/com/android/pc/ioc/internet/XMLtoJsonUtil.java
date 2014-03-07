package com.android.pc.ioc.internet;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class XMLtoJsonUtil {

	public static String XMLtoJson(String content, String method, String charest) {
		boolean isHas = false;
		String result = "";
		try {
			content = content.trim();
			ByteArrayInputStream bis = new ByteArrayInputStream(content.getBytes());
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(bis, charest);
			int event = parser.getEventType();// 产生第一个事件
			while (event != XmlPullParser.END_DOCUMENT) {
				switch (event) {
				case XmlPullParser.START_DOCUMENT:// 判断当前事件是否是文档开始事件
					break;
				case XmlPullParser.START_TAG:// 判断当前事件是否是标签元素开始事件
					if (isHas) {
						result = parser.nextText();
						return result;
					}
					if ((method + "Response").equals(parser.getName())) {
						isHas = true;
					}
					break;
				case XmlPullParser.END_TAG:// 判断当前事件是否是标签元素结束事件
					break;
				}
				event = parser.next();// 进入下一个元素并触发相应事件
			}// end while
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
