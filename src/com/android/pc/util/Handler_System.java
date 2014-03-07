package com.android.pc.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings.Secure;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.WindowManager;

import com.android.pc.ioc.app.ApplicationBean;

/**
 * 获取相关系统信息
 * 
 * @author gdpancheng@gmail.com 2013-10-12 下午2:26:34
 */
public class Handler_System {

	public static String UA = Build.MODEL;
	private static String mIMEI;
	private static String mSIM;
	private static String mMobileVersion;
	private static String mNetwrokIso;
	private static String mNetType;
	private static String mDeviceID;
	private static List<NeighboringCellInfo> mCellinfos;

	public static final String systemWidth = "width";
	public static final String systemHeight = "height";
	private static HashMap<String, Integer> map;

	static{
		init();
	}
	/**
	 * 获取应用程序名称
	 * 
	 * @author gdpancheng@gmail.com 2013-10-12 下午2:30:56
	 * @return
	 * @return String
	 */
	public static String getAppName() {
		return getAppName(null);
	}

	/**
	 * 获取应用程序名称
	 * 
	 * @author gdpancheng@gmail.com 2013-10-12 下午2:30:43
	 * @param packageName
	 * @return
	 * @return String
	 */
	public static String getAppName(String packageName) {
		String applicationName;

		if (packageName == null) {
			packageName = ApplicationBean.getApplication().getPackageName();
		}

		try {
			PackageManager packageManager = ApplicationBean.getApplication().getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
			applicationName = ApplicationBean.getApplication().getString(packageInfo.applicationInfo.labelRes);
		} catch (Exception e) {
			ApplicationBean.logger.e(e);
			applicationName = "";
		}

		return applicationName;
	}

	/**
	 * 获取版本名称
	 * 
	 * @author gdpancheng@gmail.com 2013-10-12 下午2:30:37
	 * @return
	 * @return String
	 */
	public static String getAppVersionNumber() {
		return getAppVersionNumber(null);
	}

	/**
	 * 获取版本名称
	 * 
	 * @author gdpancheng@gmail.com 2013-10-12 下午2:30:27
	 * @param packageName
	 * @return
	 * @return String
	 */
	public static String getAppVersionNumber(String packageName) {
		String versionName;

		if (packageName == null) {
			packageName = ApplicationBean.getApplication().getPackageName();
		}

		try {
			PackageManager packageManager = ApplicationBean.getApplication().getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
			versionName = packageInfo.versionName;
		} catch (Exception e) {
			ApplicationBean.logger.e(e);
			versionName = "";
		}

		return versionName;
	}

	/**
	 * 获取应用程序的版本号
	 * 
	 * @author gdpancheng@gmail.com 2013-10-12 下午2:30:12
	 * @return
	 * @return String
	 */
	public static String getAppVersionCode() {
		return getAppVersionCode(null);
	}

	/**
	 * 获取指定应用程序的版本号
	 * 
	 * @author gdpancheng@gmail.com 2013-10-12 下午2:29:51
	 * @param packageName
	 * @return
	 * @return String
	 */
	public static String getAppVersionCode(String packageName) {
		String versionCode;

		if (packageName == null) {
			packageName = ApplicationBean.getApplication().getPackageName();
		}

		try {
			PackageManager packageManager = ApplicationBean.getApplication().getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
			versionCode = Integer.toString(packageInfo.versionCode);
		} catch (Exception e) {
			ApplicationBean.logger.e(e);
			versionCode = "";
		}

		return versionCode;
	}

	/**
	 * 获取SDK版本
	 * 
	 * @author gdpancheng@gmail.com 2013-10-12 下午2:29:13
	 * @return
	 * @return int
	 */
	public static int getSdkVersion() {
		try {
			return Build.VERSION.class.getField("SDK_INT").getInt(null);
		} catch (Exception e) {
			ApplicationBean.logger.e(e);
			return 3;
		}
	}

	/*
	 * 判断是否是该签名打包
	 */
	public static boolean isRelease(String signatureString) {
		final String releaseSignatureString = signatureString;
		if (releaseSignatureString == null || releaseSignatureString.length() == 0) {
			throw new RuntimeException("Release signature string is null or missing.");
		}

		final Signature releaseSignature = new Signature(releaseSignatureString);
		try {
			PackageManager pm = ApplicationBean.getApplication().getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ApplicationBean.getApplication().getPackageName(), PackageManager.GET_SIGNATURES);
			for (Signature sig : pi.signatures) {
				if (sig.equals(releaseSignature)) {
					return true;
				}
			}
		} catch (Exception e) {
			ApplicationBean.logger.e(e);
			return true;
		}
		return false;
	}

	/**
	 * 判断是否是模拟器
	 * 
	 * @author gdpancheng@gmail.com 2013-10-12 下午2:28:40
	 * @return
	 * @return boolean
	 */
	public static boolean isEmulator() {
		return Build.MODEL.equals("sdk") || Build.MODEL.equals("google_sdk");
	}

	/**
	 * @author 潘城 gdpancheng@gmail.com 2012-5-7 下午1:21:48
	 * @Title: getMobileInfo
	 * @Description: 获取手机的硬件信息
	 * @param @return 设定文件
	 * @return String 返回类型
	 */
	public static String getMobileInfo() {
		StringBuffer sb = new StringBuffer();
		/**
		 * 通过反射获取系统的硬件信息 获取私有的信息
		 */
		try {
			Field[] fields = Build.class.getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				String name = field.getName();
				String value = field.get(null).toString();
				sb.append(name + "=" + value);
				sb.append("\n");
			}
		} catch (Exception e) {
			ApplicationBean.logger.e(e);
		}
		return sb.toString();
	}

	/**
	 * @author 潘城 gdpancheng@gmail.com 2012-5-7 下午5:18:34
	 * @Title: getDisplayMetrics
	 * @Description: 获取屏幕的分辨率
	 * @param @param cx
	 * @param @return 设定文件
	 * @return HashMap<String,Integer> 返回类型
	 */
	public static HashMap<String, Integer> getDisplayMetrics() {
		if (map == null) {
			map = new HashMap<String, Integer>();
			Display display = ((WindowManager) ApplicationBean.getApplication().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			int screenWidth = display.getWidth();
			int screenHeight = display.getHeight();
			map.put(systemWidth, screenWidth);
			map.put(systemHeight, screenHeight);
		}
		return map;
	}

	/**
	 * 获取屏幕宽度缩放率
	 * @author gdpancheng@gmail.com 2013-10-22 下午1:10:47
	 * @param width
	 * @return float
	 */
	public static float getWidthRoate(int width) {
		if (map == null) {
			map = new HashMap<String, Integer>();
			Display display = ((WindowManager) ApplicationBean.getApplication().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			int screenWidth = display.getWidth();
			int screenHeight = display.getHeight();
			map.put(systemWidth, screenWidth);
			map.put(systemHeight, screenHeight);
		}
		return (map.get(systemWidth) * 1.00f) / width;
	}
	
	public static float getRoate(int width,int height) {
		if (map == null) {
			map = new HashMap<String, Integer>();
			Display display = ((WindowManager) ApplicationBean.getApplication().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			int screenWidth = display.getWidth();
			int screenHeight = display.getHeight();
			map.put(systemWidth, screenWidth);
			map.put(systemHeight, screenHeight);
		} 
		float w = (map.get(systemWidth) * 1.00f) / width;
		float h = (map.get(systemHeight) * 1.00f) / height;
		return w>h?w:h;
	}

	/**
	 * 获取屏幕高度缩放率
	 * @author gdpancheng@gmail.com 2013-10-22 下午1:11:08
	 * @param height
	 * @return float
	 */
	public static float getHeightRoate(int height) {
		if (map == null) {
			map = new HashMap<String, Integer>();
			Display display = ((WindowManager) ApplicationBean.getApplication().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			int screenWidth = display.getWidth();
			int screenHeight = display.getHeight();
			map.put(systemWidth, screenWidth);
			map.put(systemHeight, screenHeight);
		} 
		return (map.get(systemHeight) * 1.00f) / height;
	}

	/**
	 * dp转px
	 * @author gdpancheng@gmail.com 2013-10-22 下午1:11:48
	 * @param dipValue
	 * @return int
	 */
	public static int dip2px(float dipValue) {
		final float scale = ApplicationBean.getApplication().getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * px转dip
	 * TODO(这里用一句话描述这个方法的作用)
	 * @author gdpancheng@gmail.com 2013-10-22 下午1:12:09
	 * @param pxValue
	 * @return
	 * @return int
	 */
	public static int px2dip(float pxValue) {
		final float scale = ApplicationBean.getApplication().getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 获取通知栏高度
	 * @author gdpancheng@gmail.com 2012-2-12 下午07:37:13
	 * @Title: getBarHeight
	 * @param @param context
	 * @param @return 设定文件
	 * @return int 返回类型
	 */
	public static int getBarHeight() {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, sbar = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			sbar = ApplicationBean.getApplication().getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			ApplicationBean.logger.e(e1);
		}
		return sbar;
	}

	public static boolean hasFroyo() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}

	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
	}

	public static boolean hasGingerbreadMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1;
	}

	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	public static boolean hasHoneycombMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
	}

	public static boolean hasICS() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
	}

	public static boolean hasJellyBean() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
	}

	public static boolean hasJellyBeanMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
	}

	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	public static boolean isHoneycombTablet(Context context) {
		return hasHoneycomb() && isTablet(context);
	}

	public static boolean isGingerbread() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1;
	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (activeNetwork == null || !activeNetwork.isConnected()) {
			return false;
		}
		return true;
	}

	public static final int DEFAULT_THREAD_POOL_SIZE = getDefaultThreadPoolSize();

	/**
	 * get recommend default thread pool size
	 * 
	 * @return if 2 * availableProcessors + 1 less than 8, return it, else return 8;
	 * @see {@link #getDefaultThreadPoolSize(int)} max is 8
	 */
	public static int getDefaultThreadPoolSize() {
		return getDefaultThreadPoolSize(8);
	}

	/**
	 * get recommend default thread pool size
	 * 
	 * @param max
	 * @return if 2 * availableProcessors + 1 less than max, return it, else return max;
	 */
	public static int getDefaultThreadPoolSize(int max) {
		int availableProcessors = 2 * Runtime.getRuntime().availableProcessors() + 1;
		return availableProcessors > max ? max : availableProcessors;
	}

	/**
	 * 
	 * 设置手机立刻震动
	 * */
	public static void Vibrate(long milliseconds) {
		Vibrator vib = (Vibrator) ApplicationBean.getApplication().getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(milliseconds);
	}

	static TelephonyManager mTm = null;

	/**
	 * 在获取系统信息前初始化
	 * @author gdpancheng@gmail.com 2013-10-22 下午1:14:12
	 * @return void
	 */
	public static void init() {
		mTm = (TelephonyManager) ApplicationBean.getApplication().getSystemService(Context.TELEPHONY_SERVICE);
		mIMEI = mTm.getDeviceId();
		mMobileVersion = mTm.getDeviceSoftwareVersion();
		mCellinfos = mTm.getNeighboringCellInfo();
		mNetwrokIso = mTm.getNetworkCountryIso();
		mSIM = mTm.getSimSerialNumber();
		mDeviceID = getDeviceId();
		try {
			ConnectivityManager cm = (ConnectivityManager) ApplicationBean.getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cm.getActiveNetworkInfo();
			if (info!=null) {
				mNetType = info.getTypeName();
            }
		} catch (Exception ex) {
		}
	}

	/**
	 * 获得android设备-唯一标识，android2.2 之前无法稳定运行.
	 * */
	public static String getDeviceId(Context mCm) {
		return Secure.getString(mCm.getContentResolver(), Secure.ANDROID_ID);
	}

	/**
	 * 获取设备号 TODO(这里用一句话描述这个方法的作用)
	 * 
	 * @author gdpancheng@gmail.com 2013-10-15 下午10:31:48
	 * @return
	 * @return String
	 */
	private static String getDeviceId() {
		return Secure.getString(ApplicationBean.getApplication().getContentResolver(), Secure.ANDROID_ID);
	}

	public static String getImei() {
		return mIMEI;
	}

	public static String getSIM() {
		return mSIM;
	}

	public static String getUA() {
		return UA;
	}

	/**
	 * 获取设备信息 以字符串的形式
	 * @author gdpancheng@gmail.com 2013-10-22 下午1:14:30
	 * @return String
	 */
	public static String getDeviceInfo() {
		StringBuffer info = new StringBuffer();
		info.append("IMEI:").append(getImei());
		info.append("\n");
		info.append("SIM:").append(getSIM());
		info.append("\n");
		info.append("UA:").append(getUA());
		info.append("\n");
		info.append("MobileVersion:").append(mMobileVersion);

		info.append("\n");
		info.append("SDK: ").append(android.os.Build.VERSION.SDK);
		info.append("\n");
		info.append(getCallState());
		info.append("\n");
		info.append("SIM_STATE: ").append(getSimState());
		info.append("\n");
		info.append("SIM: ").append(getSIM());
		info.append("\n");
		info.append(getSimOpertorName());
		info.append("\n");
		info.append(getPhoneType());
		info.append("\n");
		info.append(getPhoneSettings());
		info.append("\n");
		return info.toString();
	}

	/**
	 * 检查sim的状态
	 * @author gdpancheng@gmail.com 2013-10-22 下午1:15:25
	 * @return String
	 */
	public static String getSimState() {
		switch (mTm.getSimState()) {
		case android.telephony.TelephonyManager.SIM_STATE_UNKNOWN:
			return "未知SIM状态_" + android.telephony.TelephonyManager.SIM_STATE_UNKNOWN;
		case android.telephony.TelephonyManager.SIM_STATE_ABSENT:
			return "没插SIM卡_" + android.telephony.TelephonyManager.SIM_STATE_ABSENT;
		case android.telephony.TelephonyManager.SIM_STATE_PIN_REQUIRED:
			return "锁定SIM状态_需要用户的PIN码解锁_" + android.telephony.TelephonyManager.SIM_STATE_PIN_REQUIRED;
		case android.telephony.TelephonyManager.SIM_STATE_PUK_REQUIRED:
			return "锁定SIM状态_需要用户的PUK码解锁_" + android.telephony.TelephonyManager.SIM_STATE_PUK_REQUIRED;
		case android.telephony.TelephonyManager.SIM_STATE_NETWORK_LOCKED:
			return "锁定SIM状态_需要网络的PIN码解锁_" + android.telephony.TelephonyManager.SIM_STATE_NETWORK_LOCKED;
		case android.telephony.TelephonyManager.SIM_STATE_READY:
			return "就绪SIM状态_" + android.telephony.TelephonyManager.SIM_STATE_READY;
		default:
			return "未知SIM状态_" + android.telephony.TelephonyManager.SIM_STATE_UNKNOWN;

		}
	}

	/**
	 * 获取手机信号状态
	 * @author gdpancheng@gmail.com 2013-10-22 下午1:15:37
	 * @return String
	 */
	public static String getPhoneType() {
		switch (mTm.getPhoneType()) {
		case android.telephony.TelephonyManager.PHONE_TYPE_NONE:
			return "PhoneType: 无信号_" + android.telephony.TelephonyManager.PHONE_TYPE_NONE;
		case android.telephony.TelephonyManager.PHONE_TYPE_GSM:
			return "PhoneType: GSM信号_" + android.telephony.TelephonyManager.PHONE_TYPE_GSM;
		case android.telephony.TelephonyManager.PHONE_TYPE_CDMA:
			return "PhoneType: CDMA信号_" + android.telephony.TelephonyManager.PHONE_TYPE_CDMA;
		default:
			return "PhoneType: 无信号_" + android.telephony.TelephonyManager.PHONE_TYPE_NONE;
		}
	}

	/**
	 * 服务商名称：例如：中国移动、联通 　　 SIM卡的状态必须是 SIM_STATE_READY(使用getSimState()判断). 　　
	 */
	public static String getSimOpertorName() {
		if (mTm.getSimState() == android.telephony.TelephonyManager.SIM_STATE_READY) {
			StringBuffer sb = new StringBuffer();
			sb.append("SimOperatorName: ").append(mTm.getSimOperatorName());
			sb.append("\n");
			sb.append("SimOperator: ").append(mTm.getSimOperator());
			sb.append("\n");
			sb.append("Phone:").append(mTm.getLine1Number());
			return sb.toString();
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append("SimOperatorName: ").append("未知");
			sb.append("\n");
			sb.append("SimOperator: ").append("未知");
			return sb.toString();
		}
	}

	/**
	 * 获取手机设置状态 比如蓝牙开启与否 
	 * @author gdpancheng@gmail.com 2013-10-22 下午1:16:02
	 * @return String
	 */
	public static String getPhoneSettings() {
		StringBuffer buf = new StringBuffer();
		String str = Secure.getString(ApplicationBean.getApplication().getContentResolver(), Secure.BLUETOOTH_ON);
		buf.append("蓝牙:");
		if (str.equals("0")) {
			buf.append("禁用");
		} else {
			buf.append("开启");
		}
		//
		str = Secure.getString(ApplicationBean.getApplication().getContentResolver(), Secure.BLUETOOTH_ON);
		buf.append("WIFI:");
		buf.append(str);

		str = Secure.getString(ApplicationBean.getApplication().getContentResolver(), Secure.INSTALL_NON_MARKET_APPS);
		buf.append("APP位置来源:");
		buf.append(str);

		return buf.toString();
	}

	/**
	 * 获取电话状态
	 * @author gdpancheng@gmail.com 2013-10-22 下午1:16:37
	 * @return String
	 */
	public static String getCallState() {
		switch (mTm.getCallState()) {
		case android.telephony.TelephonyManager.CALL_STATE_IDLE:
			return "电话状态[CallState]: 无活动";
		case android.telephony.TelephonyManager.CALL_STATE_OFFHOOK:
			return "电话状态[CallState]: 无活动";
		case android.telephony.TelephonyManager.CALL_STATE_RINGING:
			return "电话状态[CallState]: 无活动";
		default:
			return "电话状态[CallState]: 未知";
		}
	}

	public static String getNetwrokIso() {
		return mNetwrokIso;
	}

	/**
	 * @return the mDeviceID
	 */
	public String getmDeviceID() {
		return mDeviceID;
	}

	/**
	 * @return the mNetType
	 */
	public String getNetType() {
		return mNetType;
	}

}
