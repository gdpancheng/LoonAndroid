package com.android.pc.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

import android.content.Context;

import com.android.pc.ioc.app.Ioc;

/**
 * 配置文件工具类
 * 
 * @author gdpancheng@gmail.com 2013-10-22 下午1:08:52
 */
public class Handler_Properties {

	/**
	 * 根据文件名和文件路径 读取Properties文件
	 * 
	 * @author 潘城 gdpancheng@gmail.com 2012-6-27 下午12:46:05
	 * @param fileName
	 * @param dirName
	 * @return 设定文件
	 */
	public static Properties loadProperties(String fileName, String dirName) {
		Properties props = new Properties();
		try {
			int id = Ioc.getIoc().getApplication().getResources().getIdentifier(fileName, dirName, Ioc.getIoc().getApplication().getPackageName());
			props.load(Ioc.getIoc().getApplication().getResources().openRawResource(id));
		} catch (Exception e) {
			Ioc.getIoc().getLogger().e(e.toString());
		}
		return props;
	}

	/**
	 * 读取Properties文件(指定目录)
	 * 
	 * @author 潘城 gdpancheng@gmail.com 2012-6-27 下午12:46:51
	 * @param file
	 * @return 设定文件
	 */
	public static Properties loadConfig(String file) {
		Properties properties = new Properties();
		try {
			FileInputStream s = new FileInputStream(file);
			properties.load(s);
		} catch (Exception e) {
			Ioc.getIoc().getLogger().e(e.toString());
		}
		return properties;
	}

	/**
	 * 保存Properties(指定目录)
	 * 
	 * @author 潘城 gdpancheng@gmail.com 2012-6-27 下午12:48:57
	 * @param file
	 * @param properties
	 *            设定文件
	 */
	public static void saveConfig(String file, Properties properties) {
		try {
			FileOutputStream s = new FileOutputStream(file, false);
			properties.store(s, "");
		} catch (Exception e) {
			Ioc.getIoc().getLogger().e(e.toString());
		}
	}

	/**
	 * 读取文件 文件在/data/data/package_name/files下 无法指定位置
	 * 
	 * @author 潘城 gdpancheng@gmail.com 2012-6-27 下午12:49:08
	 * @param fileName
	 * @return 设定文件
	 */
	public static Properties loadConfigNoDirs(String fileName) {
		Properties properties = new Properties();
		try {
			FileInputStream s = Ioc.getIoc().getApplication().openFileInput(fileName);
			properties.load(s);
		} catch (Exception e) {
			Ioc.getIoc().getLogger().e(e.toString());
		}
		return properties;
	}

	/**
	 * 保存文件到/data/data/package_name/files下 无法指定位置
	 * 
	 * @author 潘城 gdpancheng@gmail.com 2012-6-27 下午12:49:55
	 * @param fileName
	 * @param properties
	 *            设定文件
	 */
	public static void saveConfigNoDirs(String fileName, Properties properties) {
		try {
			FileOutputStream s = Ioc.getIoc().getApplication().openFileOutput(fileName, Context.MODE_PRIVATE);
			properties.store(s, "");
		} catch (Exception e) {
			Ioc.getIoc().getLogger().e(e.toString());
		}
	}

	public static Properties loadConfigAssets(String fileName) {

		Properties properties = new Properties();
		try {
			InputStream is = Ioc.getIoc().getApplication().getAssets().open(fileName);
			properties.load(is);
		} catch (Exception e) {
			Ioc.getIoc().getLogger().e(e.toString());
		}
		return properties;
	}
}
