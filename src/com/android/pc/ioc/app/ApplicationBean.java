package com.android.pc.ioc.app;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Properties;

import android.app.Application;
import android.view.View;

import com.android.pc.ioc.core.kernel.KernelObject;
import com.android.pc.ioc.core.kernel.KernelReflect;
import com.android.pc.ioc.db.sqlite.DbUtils;
import com.android.pc.ioc.image.config.GlobalConfig;
import com.android.pc.ioc.util.ContextUtils;
import com.android.pc.ioc.util.InjectViewUtils;
import com.android.pc.util.Handler_Properties;
import com.android.pc.util.Logger;

public abstract class ApplicationBean extends Application {

	/**
	 * Application对象
	 */
	private static ApplicationBean Application;
	public static final Logger logger = Logger.getLogger("debug");
	/**
	 * 默认高度和宽度,所有的缩放比根据这个常数来获得
	 */
	private int mode_w = 480;
	private int mode_h = 800;
	private InstrumentationBean instrumentation;
	private DbUtils db;
	private String dbDirs;

	public static ApplicationBean getApplication() {
		return Application;
	}

	@Override
	public void onCreate() {
		long time = System.currentTimeMillis();
		// -------------------------------------------------------------------------------------------------
		Application = this;
		// 读取配置文件
		Properties properties = Handler_Properties.loadConfigAssets("mvc.properties");
		if (properties != null && properties.containsKey("standard_w")) {
			mode_w = Integer.valueOf(properties.get("standard_w").toString());
		}
		if (properties != null && properties.containsKey("standard_h")) {
			mode_h = Integer.valueOf(properties.get("standard_h").toString());
		}
		// --------------------------------------------------------------------------------------------------
		// 是否是deBug
		boolean isDebug = false;
		if (properties != null && properties.containsKey("is_debug")) {
			isDebug = Boolean.valueOf(properties.get("is_debug").toString());
		}
		Logger.setDebug(isDebug);
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
		InjectViewUtils.setApplication(Application);
		// 反射获取mMainThread
		// getBaseContext()返回的是ContextImpl对象 ContextImpl中包含ActivityThread mMainThread这个对象
		Object mainThread = KernelObject.declaredGet(getBaseContext(), "mMainThread");
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
		super.onCreate();
		// --------------------------------------------------------------------------------------------------
		//判断开启框架内的图片下载
		boolean isImageLoad = false;
		if (properties != null && properties.containsKey("imageload_open")) {
			isImageLoad = Boolean.valueOf(properties.get("imageload_open").toString()) ;
		}
		// --------------------------------------------------------------------------------------------------
		//开启框架内的图片下载控件
		if (isImageLoad) {
			// 初始化图片下载控件 全局配置
			GlobalConfig globalConfig = GlobalConfig.getInstance();
			if (properties != null && properties.containsKey("memory_size")) {
				globalConfig.setMemory_size(Integer.valueOf(properties.get("memory_size").toString()) * 1024 * 1024);
			}
			if (properties != null && properties.containsKey("maxWidth")) {
				globalConfig.setMaxWidth(Integer.valueOf(properties.get("maxWidth").toString()));
			}
			if (properties != null && properties.containsKey("maxHeight")) {
				globalConfig.setMaxHeight(Integer.valueOf(properties.get("maxHeight").toString()));
			}
			if (properties != null && properties.containsKey("def_drawable")) {
				Integer id = InjectViewUtils.getResouceId("drawable", properties.get("def_drawable").toString());
				if (id != null) {
					globalConfig.setDef_drawable(getResources().getDrawable(id));
				}
			}
			if (properties != null && properties.containsKey("failed_drawable")) {
				Integer id = InjectViewUtils.getResouceId("drawable", properties.get("failed_drawable").toString());
				if (id != null) {
					globalConfig.setFailed_drawable(getResources().getDrawable(id));
				}
			}
			//本地图片加载线程池
			if (properties != null && properties.containsKey("local_cpu")) {
				globalConfig.setLocal_cpu(Integer.valueOf(properties.get("local_cpu").toString()));
			}
			//网络图片加载线程池
			if (properties != null && properties.containsKey("internet_cpu")) {
				globalConfig.setInternet_cpu(Integer.valueOf(properties.get("internet_cpu").toString()));
			}
			globalConfig.init(this);
        }
		// --------------------------------------------------------------------------------------------------
		logger.d("appliaction 加载时间为:" + (System.currentTimeMillis() - time));
		init();
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

	public abstract void init();

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
		if (db != null) {
			return db;
		}
		if (dbDirs == null) {
			db = DbUtils.create(this, "db");
		} else {
			File file = new File(dbDirs);
			if (!file.exists()) {
				file.mkdirs();
            }
			db = DbUtils.create(this, dbDirs, "db");
		}
		db.configDebug(true);
		db.configAllowTransaction(true);
		return db;
	}

	/**
	 * 设置数据库在
	 * 
	 * @author gdpancheng@gmail.com 2014-2-15 上午12:30:21
	 * @param dbDirs
	 * @return void
	 */
	public void setDbDirs(String dbDirs) {
		this.dbDirs = dbDirs;
	}
}
