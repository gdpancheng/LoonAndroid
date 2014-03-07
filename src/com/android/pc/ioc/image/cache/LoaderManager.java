package com.android.pc.ioc.image.cache;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ListIterator;
import java.util.Map;

import android.graphics.Bitmap;

import com.android.pc.ioc.app.ApplicationBean;
import com.android.pc.ioc.image.config.GlobalConfig;
import com.android.pc.ioc.image.view.AsyImageView;

/**
 * <h1>图片缓存类</h1><br>
 * 每一个bitmap可能被多个imageview引用 <br>
 * 每引用一次 计数器进行+1<br>
 * 每销毁一次 计数器-1<br>
 * 如果当前的引用从一个Bitmap变成另一个Bitmap
 * 则之前的引用 计数器-1<br>
 * 一旦计数器为0 则开始销毁<br>
 * 这种情况下最大程度保证了 不会内存溢出 设置缓存Xmb就肯定不会超过这个内存
 * 
 * @author gdpancheng@gmail.com 2013-7-9 下午6:01:54
 */
public class LoaderManager {

	private LruCache<String, Bitmap> sHardBitmapCache;//内存存储了缓存的图片
	private LinkedHashMap<String, SoftReference<Bitmap>> sSoftBitmapCache;//软引用 
	private LinkedHashMap<String, Integer> count = new LinkedHashMap<String, Integer>();//计数器
	private HashMap<String, String> hascodes = new HashMap<String, String>();//这个存储的时一个imageview对应一个url 一旦 imageview更换了 url 那么之前的url对应的引用即-1
	private GlobalConfig globalConfig = GlobalConfig.getInstance();

	private boolean clearing = false;

	public LoaderManager() {
		sSoftBitmapCache = new LinkedHashMap<String, SoftReference<Bitmap>>(LoaderManager.this.globalConfig.getQueue_cache_size(), 0.75f, true) {

			private static final long serialVersionUID = 1L;

			@Override
			public SoftReference<Bitmap> put(String key, SoftReference<Bitmap> value) {
				return super.put(key, value);
			}

			@Override
			protected boolean removeEldestEntry(LinkedHashMap.Entry<String, SoftReference<Bitmap>> eldest) {
				try {
					String key = eldest.getKey();
					if (count.containsKey(key) && count.get(key) <= 0) {
						recycle(key);
						count.remove(key);
					}
					if (!clearing) {
						clears();
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
				return false;
			}
		};

		sHardBitmapCache = new LruCache<String, Bitmap>(globalConfig.getMemory_size()) {
			@Override
			public int sizeOf(String key, Bitmap value) {
				return LoaderManager.size(value);
			}

			@Override
			protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
				// 硬引用缓存区满，将一个最不经常使用的oldvalue推入到软引用缓存
				synchronized (sSoftBitmapCache) {
					sSoftBitmapCache.put(key, new SoftReference<Bitmap>(oldValue));
				}
			}
		};
	}

	private void clears() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				clearing = true;
				synchronized (count) {
					ApplicationBean.logger.d("******************Remove unused images****************** ");
					ListIterator<Map.Entry<String, Integer>> i = new ArrayList<Map.Entry<String, Integer>>(count.entrySet()).listIterator(count.size());
					while (i.hasPrevious()) {
						Map.Entry<String, Integer> entry = i.previous();
						if (count.size() <= globalConfig.getQueue_cache_min_size()) {
							break;
						}
						if (entry.getValue() == 0) {
							recycle(entry.getKey());
						}
					}
                }
				try {
	                Thread.sleep(3*1000);
                } catch (InterruptedException e) {
	                e.printStackTrace();
                }
				clearing = false;
			}
		}).start();
	}

	// 缓存bitmap
	public boolean putBitmap(String key, Bitmap bitmap) {
		if (key == null || bitmap == null) {
			return false;
		}
		synchronized (sHardBitmapCache) {
			sHardBitmapCache.put(key, bitmap);
		}
		return true;
	}

	/**
	 * 从缓存中获取bitmap effective用来标记当次获取的bitmap是否有用于显示 如果用于显示则计数器累加 否则不累加
	 * 
	 * @author gdpancheng@gmail.com 2013-8-21 下午2:14:00
	 * @param key
	 * @param effective
	 * @return
	 * @return Bitmap
	 */
	public Bitmap getBitmap(String key, AsyImageView imageView) {
		String code = Integer.toHexString(imageView.hashCode());
		synchronized (sHardBitmapCache) {
			final Bitmap bitmap = sHardBitmapCache.get(key);
			if (bitmap != null) {
				String pre_key = "";
				if (hascodes.containsKey(code)) {
					pre_key = hascodes.get(code);
					if (count.containsKey(pre_key)) {
						count.put(pre_key, count.get(pre_key) - 1);
					}
				}
				hascodes.put(code, key);
				if (!count.containsKey(key)) {
					count.put(key, 0);
				}
				count.put(key, count.get(key) + 1);
				return bitmap;
			}
		}
		// 硬引用缓存区间中读取失败，从软引用缓存区间读取
		synchronized (sSoftBitmapCache) {
			SoftReference<Bitmap> bitmapReference = sSoftBitmapCache.get(key);
			if (bitmapReference != null) {
				final Bitmap bitmap2 = bitmapReference.get();
				if (bitmap2 != null && !bitmap2.isRecycled()) {
					String pre_key = "";
					if (hascodes.containsKey(code)) {
						pre_key = hascodes.get(code);
						if (count.containsKey(pre_key)) {
							count.put(pre_key, count.get(pre_key) - 1);
						}
					}
					hascodes.put(code, key);
					if (!count.containsKey(key)) {
						count.put(key, 0);
					}
					count.put(key, count.get(key) + 1);
					return bitmap2;
				} else {
					// 图片已经被回收
					sSoftBitmapCache.remove(key);
				}
			}
		}
		count.remove(key);
		return null;
	}

	private static int size(Bitmap mBitmap) {
		int weight = 4;
		if (mBitmap.getConfig() != null) {
			switch (mBitmap.getConfig()) {
			case ALPHA_8:
				weight = 1;
				break;
			case ARGB_4444:
				weight = 2;
				break;
			case ARGB_8888:
				weight = 4;
				break;
			case RGB_565:
				weight = 2;
				break;
			default:
				weight = 1;
				break;
			}
		}
		return (mBitmap.getWidth() * mBitmap.getHeight() * weight);
	}

	public void recycle(String key) {
		if (!count.containsKey(key)) {
			Bitmap bitmap = null;
			synchronized (sHardBitmapCache) {
				bitmap = sHardBitmapCache.remove(key);
			}
			if (bitmap != null) {
				ApplicationBean.logger.d("Because the picture is removed, so destroyed");
				bitmap.recycle();
			}
			if (!sSoftBitmapCache.containsKey(key)) {
				return;
			}
			bitmap = sSoftBitmapCache.get(key).get();
			if (bitmap != null) {
				ApplicationBean.logger.d("Because the picture is removed, so destroyed");
				bitmap.recycle();
			}
			synchronized (sSoftBitmapCache) {
				sSoftBitmapCache.remove(key);
			}
			return;
		}
		count.put(key, count.get(key) - 1);

		if (count.get(key) <= 0) {
			Bitmap bitmap = null;
			synchronized (sHardBitmapCache) {
				bitmap = sHardBitmapCache.remove(key);
			}
			if (bitmap != null) {
				ApplicationBean.logger.d("Because the picture is removed, so destroyed");
				bitmap.recycle();
			}
			bitmap = null;
			SoftReference<Bitmap> reference = null;
			synchronized (sSoftBitmapCache) {
				reference = sSoftBitmapCache.remove(key);
			}
			count.remove(key);
			if (reference == null) {
				return;
			}
			bitmap = reference.get();
			if (bitmap == null) {
				return;
			}
			ApplicationBean.logger.d("Because the picture is removed, so destroyed");
			bitmap.recycle();
		}
	}
}
