package com.android.pc.ioc.a.demo;

import com.android.pc.ioc.app.ApplicationBean;
import com.android.pc.ioc.image.ImageCache;
import com.android.pc.ioc.image.ImageLoadManager;

public class MeApplication extends ApplicationBean {
	
	public static MeApplication app;
	
	@Override
    public void init() {
		app = this;
		
		ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(this, "images");
		cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory
		ImageLoadManager.instance().addImageCache(cacheParams);
    }
}