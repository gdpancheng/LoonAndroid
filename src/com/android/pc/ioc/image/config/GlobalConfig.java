package com.android.pc.ioc.image.config;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.WindowManager;
import android.widget.AbsListView;

import com.android.pc.ioc.image.ImageDownloader;
import com.android.pc.ioc.image.cache.FileCache;
import com.android.pc.ioc.image.cache.LoaderManager;
import com.android.pc.ioc.image.config.OnScrollLoaderListener.OnStop;
import com.android.pc.ioc.image.displayer.DisplayerAnimation;
import com.android.pc.ioc.image.displayer.DisplayerLister;
import com.android.pc.ioc.image.view.AsyImageView;
import com.android.pc.util.Handler_File;

/**
 * 图片下载工具
 * 
 * @author gdpancheng@gmail.com 2013-7-8 上午11:25:32
 */
@SuppressLint("UseSparseArrays")
public class GlobalConfig {

	private static final int DEFAULT_MAX_SIZE = (int) (1024 * 1024 * 10); // 默认缓存池10M
	private static final int DEFAULT_QUEUE_MAX_SIZE = 20; // fInCacheQue队列默认大小
	private static final int DEFAULT_QUEUE_MIN_SIZE = 10; // fInCacheQue队列默认大小
	private static final int DEFAULT_CPU_SIZE = 3;

	public static final int image_png = 1;
	public static final int image_jpg = 2;
	public static final int image_gif = 3;

	/**
	 * 图片最大宽度 如果不设置则默认是屏幕的宽度
	 */
	private int maxWidth;
	/**
	 * 图片最大高度 如果不设置则默认是屏幕的高度
	 */
	private int maxHeight;

	/**
	 * 最大内存
	 */
	private int memory_size;
	/**
	 * 默认图片
	 */
	private Drawable def_drawable;
	/**
	 * 下载失败
	 */
	private Drawable failed_drawable;
	/**
	 * 最大队列缓存
	 */
	private int queue_cache_size;
	/**
	 * 最小队列缓存
	 */
	private int queue_cache_min_size;
	/**
	 * 最大网络线程池数目
	 */
	private int internet_cpu;
	/**
	 * 最大线程池数目
	 */
	private int local_cpu;
	/**
	 * 缓存路径
	 */
	private String cache_path;
	private int type_image;
	/**
	 * 显示控制器
	 */
	private DisplayerLister displayer;

	private volatile static GlobalConfig instance;
	/**
	 * 线程池
	 */
	private ExecutorService pool_Internet;
	private ExecutorService pool_Local;
	/**
	 * 文件缓存
	 */
	private FileCache fileCache;
	/**
	 * 文件夹名称
	 */
	private String image_dirs;
	/**
	 * 缓存算法类
	 */
	private LoaderManager loaderManager;
	/**
	 * 列表滚动事件
	 */
	private OnScrollLoaderListener onScrollLoaderListener;
	
	private OnScrollLoaderListener scrollLoaderListener;
	
	/**
	 * 图片下载器
	 */
	private ImageDownloader downloader;
	/**
	 * 计数器 用来控制图片的销毁
	 */
	private HashMap<Integer, AsyImageView> list_data = new HashMap<Integer, AsyImageView>();
	private Context context;
	/** 加载动画 **/
	private DisplayerAnimation displayerAnimation;

	public GlobalConfig() {
		downloader = new ImageDownloader();
	}

	public static GlobalConfig getInstance() {
		if (instance == null) {
			synchronized (GlobalConfig.class) {
				if (instance == null) {
					instance = new GlobalConfig();
				}
			}
		}
		return instance;
	}

	public static void clear() {
		instance = null;
	}

	public void init(Context context) {
		this.context = context;

		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int screenWidth = display.getWidth();
		int screenHeight = display.getHeight();

		if (maxWidth == 0) {
			maxWidth = screenWidth;
		}
		if (maxHeight == 0) {
			maxHeight = screenHeight;
		}
		if (memory_size == 0) {
			memory_size = DEFAULT_MAX_SIZE;
		}
		if (queue_cache_size == 0) {
			queue_cache_size = DEFAULT_QUEUE_MAX_SIZE;
		}

		if (queue_cache_min_size == 0) {
			queue_cache_min_size = DEFAULT_QUEUE_MIN_SIZE;
		}

		if (local_cpu == 0) {
			local_cpu = 1;
		}

		if (internet_cpu == 0) {
			internet_cpu = DEFAULT_CPU_SIZE;
		}
		if (type_image == 0) {
			type_image = image_png;
		}
		if (pool_Internet == null) {
			pool_Internet = Executors.newFixedThreadPool(internet_cpu); // 固定线程
			pool_Local = Executors.newFixedThreadPool(local_cpu); // 固定线程
		}
		if (image_dirs == null) {
			image_dirs = "imageCaches";
		}
		if (cache_path == null) {
			File cacheDir = null;
			if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
				// cacheDir = new File(android.os.Environment.getExternalStorageDirectory() + "/android/data/" + context.getPackageName() + "/cache", image_dirs);
				cacheDir = Handler_File.getExternalCacheDir(context, image_dirs);
			} else {
				cacheDir = context.getCacheDir();
			}
			if (cacheDir != null && !cacheDir.exists()) {
				cacheDir.mkdirs();
			}
			cache_path = cacheDir.getPath();
		}
		if (fileCache == null) {
			fileCache = new FileCache(cache_path);
		}
		if (loaderManager == null) {
			loaderManager = new LoaderManager();
		}

		onScrollLoaderListener = new OnScrollLoaderListener() {

			@Override
			public void onScrollStateChange(AbsListView view, int scrollState) {
			}

			@Override
			public void onScrollListener(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			}
		};
		onScrollLoaderListener.setOnStop(onStop);
		
		scrollLoaderListener = onScrollLoaderListener;
	}

	// private File getExternalCacheDir(Context context) {
	// File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
	// File appCacheDir = new File(new File(new File(dataDir, context.getPackageName()), "cache"), image_dirs);
	// if (!appCacheDir.exists()) {
	// try {
	// new File(dataDir, ".nomedia").createNewFile();
	// } catch (IOException e) {
	// Log.w("图片下载", "Can't create \".nomedia\" file in application external cache directory", e);
	// }
	// if (!appCacheDir.mkdirs()) {
	// Log.w("图片下载", "Unable to create external cache directory");
	// return null;
	// }
	// }
	// return appCacheDir;
	// }

	public OnScrollLoaderListener getOnScrollLoaderListener() {
		return scrollLoaderListener;
	}

	public void setOnScrollLoaderListener(OnScrollLoaderListener onScrollLoaderListener) {
		if (onScrollLoaderListener == null) {
			scrollLoaderListener = this.onScrollLoaderListener;
        }
		this.scrollLoaderListener = onScrollLoaderListener;
		this.scrollLoaderListener.setOnStop(onStop);
	}

	private OnStop onStop = new OnStop() {
		@Override
		public void refer(int first, int count) {
			for (int i = 0; i < count; i++) {
				if (list_data.containsKey(first + i)) {
					AsyImageView view = list_data.get(first + i);
					view.setPostion(Util.ID_NONE);
					ImageDownloader.download(view.getUrl(), view, view.getSingleConfig());
				}
			}
			list_data.clear();
		}
	};

	public LoaderManager getLoaderManager() {
		return loaderManager;
	}

	public FileCache getFileCache() {
		return fileCache;
	}

	public ExecutorService getPool() {
		return pool_Internet;
	}

	public Drawable getFailed_drawable() {
		return failed_drawable;
	}

	public GlobalConfig setFailed_drawable(Drawable failed_drawable) {
		this.failed_drawable = failed_drawable;
		return this;
	}

	public ExecutorService getPool_Local() {
		return pool_Local;
	}

	public String getCache_path() {
		return cache_path;
	}

	public GlobalConfig setCache_path(String cache_path) {
		this.cache_path = cache_path;
		return this;
	}

	public int getMaxWidth() {
		return maxWidth;
	}

	public GlobalConfig setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
		return this;
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public int getQueue_cache_size() {
		return queue_cache_size;
	}

	public GlobalConfig setQueue_cache_size(int queue_cache_size) {
		this.queue_cache_size = queue_cache_size;
		return this;
	}

	public GlobalConfig setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
		return this;
	}

	public int getMemory_size() {
		return memory_size;
	}

	public GlobalConfig setMemory_size(int memory_size) {
		this.memory_size = memory_size;
		return this;
	}

	public Drawable getDef_drawable() {
		return def_drawable;
	}

	public GlobalConfig setDef_drawable(Drawable def_drawable) {
		this.def_drawable = def_drawable;
		return this;
	}

	public DisplayerLister getDisplayer() {
		return displayer;
	}

	public GlobalConfig setDisplayer(DisplayerLister displayer) {
		this.displayer = displayer;
		return this;
	}

	public HashMap<Integer, AsyImageView> getList_data() {
		return list_data;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public ImageDownloader getInDownloader() {
		return getInDownloader();
	}

	public DisplayerAnimation getDisplayerAnimation() {
		return displayerAnimation;
	}

	public void setDisplayerAnimation(DisplayerAnimation displayerAnimation) {
		this.displayerAnimation = displayerAnimation;
	}

	public int getInternet_cpu() {
		return internet_cpu;
	}

	public void setInternet_cpu(int internet_cpu) {
		this.internet_cpu = internet_cpu;
	}

	public int getLocal_cpu() {
		return local_cpu;
	}

	public void setLocal_cpu(int local_cpu) {
		this.local_cpu = local_cpu;
	}

	public int getQueue_cache_min_size() {
		return queue_cache_min_size;
	}

	public void setQueue_cache_min_size(int queue_cache_min_size) {
		this.queue_cache_min_size = queue_cache_min_size;
	}
}
