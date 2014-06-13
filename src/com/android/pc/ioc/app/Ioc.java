package com.android.pc.ioc.app;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import android.app.Activity;
import android.app.Application;
import android.view.View;

import com.android.pc.ioc.core.kernel.KernelObject;
import com.android.pc.ioc.core.kernel.KernelReflect;
import com.android.pc.ioc.db.sqlite.DbUtils;
import com.android.pc.ioc.util.ContextUtils;
import com.android.pc.ioc.util.InjectViewUtils;
import com.android.pc.util.Handler_Properties;
import com.android.pc.util.Logger;

public class Ioc {
	/**
	 * Application对象
	 */
	private Application application;
	private static Ioc ioc;
	private  Logger logger = null;
	/**
	 * 默认高度和宽度,所有的缩放比根据这个常数来获得
	 */
	private int mode_w = 480;
	private int mode_h = 800;
	private InstrumentationBean instrumentation;
	private HashMap<String, DbUtils> dbMap = new HashMap<String, DbUtils>();
	private String dbName = "db";
	private List<Activity> activitys = new ArrayList<Activity>();

	public Application getApplication() {
		return application;
	}

	public Logger getLogger(){
		return logger;
	}
	
	public static Ioc getIoc() {
		if (ioc == null) {
			ioc = new Ioc();
		}
		return ioc;
	}

	public  void init(Application app) {
		
		long time = System.currentTimeMillis();
		// registerActivityLifecycleCallbacks(callbacks);
		application = app;
		logger = Logger.getLogger("debug");
		// 读取配置文件
		Properties properties = Handler_Properties.loadConfigAssets("mvc.properties");
		if (properties != null && properties.containsKey("standard_w")) {
			mode_w = Integer.valueOf(properties.get("standard_w").toString());
		}
		if (properties != null && properties.containsKey("standard_h")) {
			mode_h = Integer.valueOf(properties.get("standard_h").toString());
		}
		// --------------------------------------------------------------------------------------------------
		// 是否打开兼容模式
		boolean iscompatible = false;
		if (properties != null && properties.containsKey("iscompatible")) {
			iscompatible = Boolean.valueOf(properties.get("iscompatible").toString());
		}
		// --------------------------------------------------------------------------------------------------
		// 开启线程来提前遍历需要注入的activity
		initThread.start();
		// --------------------------------------------------------------------------------------------------
		// 整个框架的核心
		InjectViewUtils.setApplication(application);
		// 反射获取mMainThread
		// getBaseContext()返回的是ContextImpl对象 ContextImpl中包含ActivityThread mMainThread这个对象
		Object mainThread = KernelObject.declaredGet(application.getBaseContext(), "mMainThread");
		// 反射获取mInstrumentation的对象
		Field instrumentationField = KernelReflect.declaredField(mainThread.getClass(), "mInstrumentation");
		instrumentation = new InstrumentationBean();
		// 自定义一个Instrumentation的子类 并把所有的值给copy进去
		if (iscompatible) {
			KernelObject.copy(KernelReflect.get(mainThread, instrumentationField), instrumentation);
		}
		// 再把替换过的Instrumentation重新放进去
		KernelReflect.set(mainThread, instrumentationField, instrumentation);
		// --------------------------------------------------------------------------------------------------
		logger.d("appliaction 加载时间为:" + (System.currentTimeMillis() - time));
	}

	Thread initThread = new Thread() {
		public void run() {
			ContextUtils.getFactoryProvider();
		};
	};

	public int getMode_w() {
		return mode_w;
	}

	public void setMode_w(int mode_w) {
		this.mode_w = mode_w;
	}

	public int getMode_h() {
		return mode_h;
	}

	public void setMode_h(int mode_h) {
		this.mode_h = mode_h;
	}

	public void keypress(View view, final int key) {
		view.setFocusable(true);
		view.requestFocus();
		new Thread(new Runnable() {
			@Override
			public void run() {
				instrumentation.sendKeyDownUpSync(key);
			}
		}).start();
	}

	public DbUtils getDb() {
		return getDb(null, this.dbName);
	}

	public DbUtils getDb(String dbDirs, String dbName) {
		String key = dbDirs == null ? dbName : dbDirs + dbName;
		if (dbMap.containsKey(key)) {
			return dbMap.get(key);
		}

		DbUtils db;
		if (dbDirs == null) {
			db = DbUtils.create(application, dbName);
			dbMap.put(dbName, db);
		} else {
			File file = new File(dbDirs);
			if (!file.exists()) {
				file.mkdirs();
			}
			db = DbUtils.create(application, dbDirs, dbName);
			dbMap.put(dbDirs + dbName, db);
		}
		db.configDebug(true);
		db.configAllowTransaction(true);
		return db;
	}

	/**
	 * 避免由于InjectAll是静态的导致永远保留的是最后一次的
	 * 
	 * @author gdpancheng@gmail.com 2014-5-4 下午2:21:48
	 * @return HashMap<String,Object>
	 */
	public List<Activity> getActivity() {
		return activitys;
	}
}
