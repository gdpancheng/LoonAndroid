package com.android.pc.ioc.image.config;

import java.util.HashMap;
import java.util.Properties;

import android.graphics.drawable.Drawable;

import com.android.pc.ioc.app.ApplicationBean;
import com.android.pc.ioc.image.displayer.DisplayerAnimation;
import com.android.pc.ioc.image.displayer.DisplayerLister;
import com.android.pc.ioc.image.displayer.LoaderLister;
import com.android.pc.ioc.util.InjectViewUtils;
import com.android.pc.util.Handler_Properties;

/**
 * 针对每一个图片的下载配置
 * 
 * @author gdpancheng@gmail.com 2014-2-6 下午3:47:00
 */
public class SingleConfig {

	public static HashMap<String, SingleConfig> hashMap = new HashMap<String, SingleConfig>();

	/**
	 * 当我们针对每张图片设置下载属性 那么就用
	 */
	private SingleConfig defaultConfig;
	/** 默认图片 */
	private Drawable defDrawable;
	/** 默认图片 */
	private Drawable failedDrawable;
	/** 图片最大宽度 */
	private int max_width;
	/** 图片最大高度 */
	private int max_height;
	/** 图片显示控制器 */
	private DisplayerLister displayer;
	/** 加载动画 **/
	private DisplayerAnimation displayerAnimation;
	/** 下载进度 **/
	private LoaderLister loader;

	public static SingleConfig getSingleConfig(String key) {

		if (hashMap.containsKey(key)) {
			return hashMap.get(key);
		}

		Properties properties = Handler_Properties.loadConfigAssets("mvc.properties");
		String name = properties.getProperty("config_name");
		if (name == null || name.trim().length() == 0) {
			return null;
		}

		String names[] = name.split("_");
		for (int i = 0; i < names.length; i++) {
			SingleConfig singleConfig = null;
			if (properties != null && properties.containsKey(names[i] + "_maxWidth")) {
				singleConfig = new SingleConfig();
				singleConfig.setMax_width(Integer.valueOf(properties.get(names[i] + "_maxWidth").toString()));
			}
			if (properties != null && properties.containsKey(names[i] + "_maxHeight")) {
				singleConfig = singleConfig == null ? new SingleConfig() : singleConfig;
				singleConfig.setMax_height(Integer.valueOf(properties.get(names[i] + "_maxHeight").toString()));
			}
			if (properties != null && properties.containsKey(names[i] + "_def_drawable")) {
				Integer id = InjectViewUtils.getResouceId("drawable", properties.get(names[i] + "_def_drawable").toString());
				if (id != null) {
					singleConfig = singleConfig == null ? new SingleConfig() : singleConfig;
					singleConfig.setDefDrawable(ApplicationBean.getApplication().getResources().getDrawable(id));
				}
			}
			if (properties != null && properties.containsKey(names[i] + "_failed_drawable")) {
				Integer id = InjectViewUtils.getResouceId("drawable", properties.get(names[i] + "_failed_drawable").toString());
				if (id != null) {
					singleConfig = singleConfig == null ? new SingleConfig() : singleConfig;
					singleConfig.setFailedDrawable(ApplicationBean.getApplication().getResources().getDrawable(id));
				}
			}
			if (singleConfig != null) {
				hashMap.put(names[i], singleConfig);
			}
		}
		return hashMap.get(key);
	}

	public Drawable getDefDrawable() {
		return defDrawable;
	}

	public void setDefDrawable(Drawable defDrawable) {
		this.defDrawable = defDrawable;
	}

	public Drawable getFailedDrawable() {
		return failedDrawable;
	}

	public void setFailedDrawable(Drawable failedDrawable) {
		this.failedDrawable = failedDrawable;
	}

	public void setDefDrawable_id(int defDrawable_id) {
		this.defDrawable = GlobalConfig.getInstance().getContext().getResources().getDrawable(defDrawable_id);
	}

	public int getMax_width() {
		return max_width;
	}

	public void setMax_width(int max_width) {
		this.max_width = max_width;
	}

	public int getMax_height() {
		return max_height;
	}

	public void setMax_height(int max_height) {
		this.max_height = max_height;
	}

	public DisplayerLister getDisplayer() {
		return displayer;
	}

	public void setDisplayer(DisplayerLister displayer) {
		this.displayer = displayer;
	}

	public DisplayerAnimation getDisplayerAnimation() {
		return displayerAnimation;
	}

	public void setDisplayerAnimation(DisplayerAnimation displayerAnimation) {
		this.displayerAnimation = displayerAnimation;
	}

	public LoaderLister getLoader() {
		return loader;
	}

	public void setLoader(LoaderLister loader) {
		this.loader = loader;
	}

	@Override
	public String toString() {
		return "SingleConfig [defaultConfig=" + defaultConfig + ", defDrawable=" + defDrawable + ", failedDrawable=" + failedDrawable + ", max_width=" + max_width + ", max_height=" + max_height + ", displayer=" + displayer + ", displayerAnimation=" + displayerAnimation + "]";
	}
}
