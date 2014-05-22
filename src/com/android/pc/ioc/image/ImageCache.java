/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.pc.ioc.image;

import java.io.File;
import java.lang.ref.SoftReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build.VERSION_CODES;
import android.os.Environment;
import android.os.Looper;
import android.os.StatFs;
import android.widget.Toast;

import com.android.pc.ioc.app.ApplicationBean;
import com.android.pc.ioc.image.ImageCache.ImageCacheParams;
import com.android.pc.util.LruCache;

/**
 * This class handles disk and memory caching of bitmaps in conjunction with the {@link ImageWorker} class and its subclasses. Use {@link ImageCache#getInstance(android.support.v4.app.FragmentManager, ImageCacheParams)} to get an instance of this class, although usually a cache should be added directly to an {@link ImageWorker} by calling {@link ImageWorker#addImageCache(android.support.v4.app.FragmentManager, ImageCacheParams)}.
 */
public class ImageCache {

	// Default memory cache size in kilobytes
	private static final int DEFAULT_MEM_CACHE_SIZE = 1024 * 5; // 5MB
	private static final int HTTP_CACHE_SIZE = 10 * 1024 * 1024; // 10MB
	// Default disk cache size in bytes
	private static final int DEFAULT_DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB

	// Compression settings when writing images to disk cache
	private static final CompressFormat DEFAULT_COMPRESS_FORMAT = CompressFormat.JPEG;
	private static final int DEFAULT_COMPRESS_QUALITY = 70;

	// Constants to easily toggle various caches
	private static final boolean DEFAULT_MEM_CACHE_ENABLED = true;
	private static final boolean DEFAULT_DISK_CACHE_ENABLED = true;
	private static final boolean DEFAULT_INIT_DISK_CACHE_ON_CREATE = false;

	// private DiskLruCache mDiskLruCache;
	private LruCache<String, BitmapDrawable> mMemoryCache;
	private ImageCacheParams mCacheParams;
	private final Object mDiskCacheLock = new Object();
	private boolean mDiskCacheStarting = true;

	private Set<SoftReference<Bitmap>> mReusableBitmaps;

	private static ImageCache imageCache;

	/**
	 * Create a new ImageCache object using the specified parameters. This should not be called directly by other classes, instead use {@link ImageCache#getInstance(android.support.v4.app.FragmentManager, ImageCacheParams)} to fetch an ImageCache instance.
	 * 
	 * @param cacheParams
	 *            The cache parameters to use to initialize the cache
	 */
	private ImageCache(ImageCacheParams cacheParams) {
		init(cacheParams);
	}

	/**
	 * Return an {@link ImageCache} instance. A {@link RetainFragment} is used to retain the ImageCache object across configuration changes such as a change in device orientation.
	 * 
	 * @param fragmentManager
	 *            The fragment manager to use when dealing with the retained fragment.
	 * @param cacheParams
	 *            The cache parameters to use if the ImageCache needs instantiation.
	 * @return An existing retained ImageCache object or a new one if one did not exist
	 */
	public static ImageCache getInstance(ImageCacheParams cacheParams) {
		if (imageCache == null) {
			imageCache = new ImageCache(cacheParams);
		}
		return imageCache;
	}

	/**
	 * Initialize the cache, providing all parameters.
	 * 
	 * @param cacheParams
	 *            The cache parameters to initialize the cache
	 */
	private void init(ImageCacheParams cacheParams) {

		mCacheParams = cacheParams;

		// Set up memory cache
		if (mCacheParams.memoryCacheEnabled) {
			ApplicationBean.logger.d("Memory cache created (size = " + mCacheParams.memCacheSize + ")");

			// If we're running on Honeycomb or newer, create a set of reusable bitmaps that can be
			// populated into the inBitmap field of BitmapFactory.Options. Note that the set is
			// of SoftReferences which will actually not be very effective due to the garbage
			// collector being aggressive clearing Soft/WeakReferences. A better approach
			// would be to use a strongly references bitmaps, however this would require some
			// balancing of memory usage between this set and the bitmap LruCache. It would also
			// require knowledge of the expected size of the bitmaps. From Honeycomb to JellyBean
			// the size would need to be precise, from KitKat onward the size would just need to
			// be the upper bound (due to changes in how inBitmap can re-use bitmaps).
			if (Utils.hasHoneycomb()) {
				mReusableBitmaps = Collections.synchronizedSet(new HashSet<SoftReference<Bitmap>>());
			}

			mMemoryCache = new LruCache<String, BitmapDrawable>(mCacheParams.memCacheSize) {
				/**
				 * Notify the removed entry that is no longer being cached
				 */
				@Override
				protected void entryRemoved(boolean evicted, String key, BitmapDrawable oldValue, BitmapDrawable newValue) {
					if (RecyclingBitmapDrawable.class.isInstance(oldValue)) {
						// The removed entry is a recycling drawable, so notify it
						// that it has been removed from the memory cache
						((RecyclingBitmapDrawable) oldValue).setIsCached(false);
					} else {
						// The removed entry is a standard BitmapDrawable
						if (Utils.hasHoneycomb()) {
							// We're running on Honeycomb or later, so add the bitmap
							// to a SoftReference set for possible use with inBitmap later
							mReusableBitmaps.add(new SoftReference<Bitmap>(oldValue.getBitmap()));
						}
					}
				}

				/**
				 * Measure item size in kilobytes rather than units which is more practical for a bitmap cache
				 */
				@Override
				protected int sizeOf(String key, BitmapDrawable value) {
					final int bitmapSize = getBitmapSize(value) / 1024;
					return bitmapSize == 0 ? 1 : bitmapSize;
				}
			};
		}

		// By default the disk cache is not initialized here as it should be initialized
		// on a separate thread due to disk access.
		if (cacheParams.initDiskCacheOnCreate) {
			initDiskCache();
		}
	}

	public ImageCacheParams getmCacheParams() {
		return mCacheParams;
	}

	public void initDiskCache() {
		synchronized (mDiskCacheLock) {
			// 初始化 创建目录
			File file = mCacheParams.diskCacheDir;
			if (!file.exists()) {
				file.mkdirs();
			}
			if (ImageCache.getUsableSpace(mCacheParams.diskCacheDir) < HTTP_CACHE_SIZE) {
				Looper.prepare();
				Toast.makeText(ApplicationBean.getApplication(), "存储空间不足,请检查", Toast.LENGTH_LONG).show();
				Looper.loop();
			}
			mDiskCacheStarting = false;
			mDiskCacheLock.notifyAll();
		}
	}

	/**
	 * 添加一个Bitmap到内存和物理缓存中
	 * 
	 * @param data
	 *            Unique identifier for the bitmap to store
	 * @param value
	 *            The bitmap drawable to store
	 */
	public void addBitmapToCache(String data, BitmapDrawable value) {
		if (data == null || value == null) {
			return;
		}

		// 添加内存缓存
		if (mMemoryCache != null) {
			if (RecyclingBitmapDrawable.class.isInstance(value)) {
				// 如果添加的是一个自定义的回收BitmapDrawable 则标记其状态 对其的计数器进行累加
				((RecyclingBitmapDrawable) value).setIsCached(true);
			}
			mMemoryCache.put(data, value);
		}
	}

	/**
	 * Get from memory cache.
	 * 
	 * @param data
	 *            Unique identifier for which item to get
	 * @return The bitmap drawable if found in cache, null otherwise
	 */
	public BitmapDrawable getBitmapFromMemCache(String data) {
		BitmapDrawable memValue = null;

		if (mMemoryCache != null) {
			memValue = mMemoryCache.get(data);
		}

		return memValue;
	}

	/**
	 * Get from disk cache.
	 * 
	 * @param data
	 *            Unique identifier for which item to get
	 * @return The bitmap if found in cache, null otherwise
	 */
	public Bitmap getBitmapFromDiskCache(String data, int w, int h) {
		final String key = hashKeyForDisk(data);

		synchronized (mDiskCacheLock) {
			while (mDiskCacheStarting) {
				try {
					mDiskCacheLock.wait();
				} catch (InterruptedException e) {
				}
			}
			File file = getFromFileCache(key);
			Bitmap bitmap = null;
			if (file != null && file.exists()) {
				bitmap = ImageResizer.decodeSampledBitmapFromFile(file.getPath(), w, h, this);
			}
			return bitmap;
		}
	}

	/**
	 * @param options
	 *            - BitmapFactory.Options with out* options populated
	 * @return Bitmap that case be used for inBitmap
	 */
	protected Bitmap getBitmapFromReusableSet(BitmapFactory.Options options) {
		// BEGIN_INCLUDE(get_bitmap_from_reusable_set)
		Bitmap bitmap = null;

		if (mReusableBitmaps != null && !mReusableBitmaps.isEmpty()) {
			synchronized (mReusableBitmaps) {
				final Iterator<SoftReference<Bitmap>> iterator = mReusableBitmaps.iterator();
				Bitmap item;

				while (iterator.hasNext()) {
					item = iterator.next().get();

					if (null != item && item.isMutable()) {
						// Check to see it the item can be used for inBitmap
						if (canUseForInBitmap(item, options)) {
							bitmap = item;

							// Remove from reusable set so it can't be used again
							iterator.remove();
							break;
						}
					} else {
						// Remove from the set if the reference has been cleared.
						iterator.remove();
					}
				}
			}
		}

		return bitmap;
		// END_INCLUDE(get_bitmap_from_reusable_set)
	}

	/**
	 * Clears both the memory and disk cache associated with this ImageCache object. Note that this includes disk access so this should not be executed on the main/UI thread.
	 */
	public void clearCache() {
		if (mMemoryCache != null) {
			mMemoryCache.evictAll();
			ApplicationBean.logger.d("缓存清理成功");
		}

		synchronized (mDiskCacheLock) {
			mDiskCacheStarting = true;

			File file = mCacheParams.diskCacheDir;
			if (file.exists()) {
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					files[i].delete();
					files[i].deleteOnExit();
				}
			}
			// 删除缓存
			ApplicationBean.logger.d("本地磁盘清理成功");
			initDiskCache();
		}
	}

	/**
	 * A holder class that contains cache parameters.
	 */
	public static class ImageCacheParams {
		public int memCacheSize = DEFAULT_MEM_CACHE_SIZE;
		public int diskCacheSize = DEFAULT_DISK_CACHE_SIZE;
		public File diskCacheDir;
		public CompressFormat compressFormat = DEFAULT_COMPRESS_FORMAT;
		public int compressQuality = DEFAULT_COMPRESS_QUALITY;
		public boolean memoryCacheEnabled = DEFAULT_MEM_CACHE_ENABLED;
		public boolean diskCacheEnabled = DEFAULT_DISK_CACHE_ENABLED;
		public boolean initDiskCacheOnCreate = DEFAULT_INIT_DISK_CACHE_ON_CREATE;

		/**
		 * 缓存目录
		 * 
		 * @param context
		 * @param diskCacheDirectoryName
		 */
		public ImageCacheParams(Context context, String diskCacheDirectoryName) {
			diskCacheDir = getDiskCacheDir(context, diskCacheDirectoryName);
		}

		/**
		 * 设置内存缓存大小根据应用可用虚拟内存的百分比 TODO(这里用一句话描述这个方法的作用)
		 * 
		 * @author gdpancheng@gmail.com 2014-5-19 下午4:03:21
		 * @param percent
		 * @return void
		 */
		public void setMemCacheSizePercent(float percent) {
			if (percent < 0.01f || percent > 0.8f) {
				throw new IllegalArgumentException("setMemCacheSizePercent - percent must be " + "between 0.01 and 0.8 (inclusive)");
			}
			memCacheSize = Math.round(percent * Runtime.getRuntime().maxMemory() / 1024);
		}
	}

	/**
	 * @param candidate
	 *            - Bitmap to check
	 * @param targetOptions
	 *            - Options that have the out* value populated
	 * @return true if <code>candidate</code> can be used for inBitmap re-use with <code>targetOptions</code>
	 */
	@TargetApi(VERSION_CODES.KITKAT)
	private static boolean canUseForInBitmap(Bitmap candidate, BitmapFactory.Options targetOptions) {
		// BEGIN_INCLUDE(can_use_for_inbitmap)
		if (!Utils.hasKitKat()) {
			// On earlier versions, the dimensions must match exactly and the inSampleSize must be 1
			return candidate.getWidth() == targetOptions.outWidth && candidate.getHeight() == targetOptions.outHeight && targetOptions.inSampleSize == 1;
		}

		// From Android 4.4 (KitKat) onward we can re-use if the byte size of the new bitmap
		// is smaller than the reusable bitmap candidate allocation byte count.
		int width = targetOptions.outWidth / targetOptions.inSampleSize;
		int height = targetOptions.outHeight / targetOptions.inSampleSize;
		int byteCount = width * height * getBytesPerPixel(candidate.getConfig());
		return byteCount <= candidate.getAllocationByteCount();
		// END_INCLUDE(can_use_for_inbitmap)
	}

	/**
	 * 返回每个像素所占的字节
	 * 
	 * @author gdpancheng@gmail.com 2014-5-19 下午4:05:08
	 * @param config
	 * @return int
	 */
	private static int getBytesPerPixel(Config config) {
		if (config == Config.ARGB_8888) {
			return 4;
		} else if (config == Config.RGB_565) {
			return 2;
		} else if (config == Config.ARGB_4444) {
			return 2;
		} else if (config == Config.ALPHA_8) {
			return 1;
		}
		return 1;
	}

	/**
	 * 获取缓存目录
	 * 
	 * @author gdpancheng@gmail.com 2014-5-19 下午4:05:28
	 * @param context
	 * @param uniqueName
	 * @return File
	 */
	public static File getDiskCacheDir(Context context, String uniqueName) {
		final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !isExternalStorageRemovable() ? getExternalCacheDir(context).getPath() : context.getCacheDir().getPath();

		return new File(cachePath + File.separator + uniqueName);
	}

	/**
	 * url转md5的key
	 * 
	 * @author gdpancheng@gmail.com 2014-5-19 下午4:05:50
	 * @param key
	 * @return String
	 */
	public static String hashKeyForDisk(String key) {
		String cacheKey;
		try {
			final MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(key.getBytes());
			cacheKey = bytesToHexString(mDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			cacheKey = String.valueOf(key.hashCode());
		}
		return cacheKey;
	}

	private static String bytesToHexString(byte[] bytes) {
		// http://stackoverflow.com/questions/332079
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}

	/**
	 * Get the size in bytes of a bitmap in a BitmapDrawable. Note that from Android 4.4 (KitKat) onward this returns the allocated memory size of the bitmap which can be larger than the actual bitmap data byte count (in the case it was re-used).
	 * 
	 * @param value
	 * @return size in bytes
	 */
	@TargetApi(VERSION_CODES.KITKAT)
	public static int getBitmapSize(BitmapDrawable value) {
		Bitmap bitmap = value.getBitmap();

		// From KitKat onward use getAllocationByteCount() as allocated bytes can potentially be
		// larger than bitmap byte count.
		if (Utils.hasKitKat()) {
			return bitmap.getAllocationByteCount();
		}

		if (Utils.hasHoneycombMR1()) {
			return bitmap.getByteCount();
		}

		// Pre HC-MR1
		return bitmap.getRowBytes() * bitmap.getHeight();
	}

	/**
	 * Check if external storage is built-in or removable.
	 * 
	 * @return True if external storage is removable (like an SD card), false otherwise.
	 */
	@TargetApi(VERSION_CODES.GINGERBREAD)
	public static boolean isExternalStorageRemovable() {
		if (Utils.hasGingerbread()) {
			return Environment.isExternalStorageRemovable();
		}
		return true;
	}

	/**
	 * Get the external app cache directory.
	 * 
	 * @param context
	 *            The context to use
	 * @return The external cache dir
	 */
	@TargetApi(VERSION_CODES.FROYO)
	public static File getExternalCacheDir(Context context) {
		if (Utils.hasFroyo()) {
			return context.getExternalCacheDir();
		}

		// Before Froyo we need to construct the external cache dir ourselves
		final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/images";
		return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
	}

	public static File getFromFileCache(String fileName) {
		File file = imageCache.getmCacheParams().diskCacheDir;
		if (!file.exists()) {
			file.mkdirs();
		}
		// 获取文件“file.txt”对应的“文件描述符”
		return new File(file, fileName);
	}

	/**
	 * Check how much usable space is available at a given path.
	 * 
	 * @param path
	 *            The path to check
	 * @return The space available in bytes
	 */
	@TargetApi(VERSION_CODES.GINGERBREAD)
	public static long getUsableSpace(File path) {
		if (Utils.hasGingerbread()) {
			return path.getUsableSpace();
		}
		final StatFs stats = new StatFs(path.getPath());
		return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
	}
}
