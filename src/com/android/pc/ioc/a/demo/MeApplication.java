package com.android.pc.ioc.a.demo;

import java.io.InputStream;

import android.app.Application;

import com.android.pc.ioc.app.Ioc;
import com.android.pc.ioc.image.ImageCache;
import com.android.pc.ioc.image.ImageLoadManager;
import com.android.pc.ioc.image.ImageLoadManager.Coding;

public class MeApplication extends Application {
	
	public static Application app;
	
	@Override
	public void onCreate() {
		//整个框架的入口 最好在super之前执行
		Ioc.getIoc().init(this);
		
	    super.onCreate();
	    
	    app = this;
	    ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(this, "images");
		cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory
		ImageLoadManager.instance().addImageCache(cacheParams);
		ImageLoadManager.instance().setCoding(coding);
	}
	
	/**
	 * 图片解密专用
	 * 这个大家可以忽略 因为某个项目图片在服务端加密了
	 * 所以这里需要解密
	 */
	Coding coding = new Coding() {
		
		@Override
		public byte[] decodePNG(long size, InputStream in) {
			byte[] arr = new byte[(int) (size)];;
			try {
				byte[] buffer = new byte[1024];
				int n = 0;
				int data = 0;
				int number = 0;
				while ((data = in.read(buffer) )!= -1) {
					if (n == 0) {
						System.arraycopy(buffer, 0, arr, 0, 3);
						number = number+3;
						System.arraycopy("G".getBytes(), 0, arr, number, 1);
						number = number+1;
						System.arraycopy(buffer, 4, arr, number, buffer.length-4);
						number = number+buffer.length-4;
	                }
					if (n != 2&&n != 0) {
						System.arraycopy(buffer, 0, arr, number, data);
						number = number+data;
	                }
					n++;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return arr;
		}
		
		@Override
		public byte[] decodeJPG(long size, InputStream in) {
			byte[] arr = new byte[(int) (size)];;
			try {
				byte[] buffer = new byte[1024];
				int n = 0;
				int data = 0;
				int number = 0;
				while ((data = in.read(buffer) )!= -1) {
					if (n != 3&&n != 0) {
						System.arraycopy(buffer, 0, arr, number, data);
						number = number+data;
	                }
					n++;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return arr;
		}
	};
}