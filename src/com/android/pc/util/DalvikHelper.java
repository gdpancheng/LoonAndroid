package com.android.pc.util;

import java.io.IOException;
import java.util.Enumeration;

import android.content.Context;

import com.android.pc.ioc.core.kernel.KernelLang.BreakException;
import com.android.pc.ioc.core.kernel.KernelLang.CallbackBreak;
import com.android.pc.ioc.core.kernel.KernelLang.CallbackTemplate;
import com.android.pc.ioc.core.kernel.KernelLang.FilterTemplate;

import dalvik.system.DexFile;

/**
 * 虚拟机工具类
 * 
 * @author gdpancheng@gmail.com 2013-9-20 下午11:40:45
 */
public class DalvikHelper {

	/**
	 * 对虚拟机中的类名进行过滤
	 * 
	 * @author gdpancheng@gmail.com 2013-9-20 下午11:41:05
	 * @param context
	 * @param callback
	 * @param filter
	 * @return void
	 */
	public static void doScannerFilter(Context context, CallbackBreak<String> callback, FilterTemplate<String> filter) {
		try {
			// cont.getPackageCodePath() 获取自身APK的路径
			DexFile dexFile = new DexFile(context.getPackageCodePath());
			// Enumeration<String> entries ()迭代其中的类名
			Enumeration<String> it = dexFile.entries();
			while (it.hasMoreElements()) {
				String classname = it.nextElement();
				if (filter == null || filter.doWith(classname)) {
					callback.doWith(classname);
				}
			}
		} catch (BreakException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param context
	 * @param basepath
	 * @param callback
	 * @param filter
	 */
	public static void doScannerFilter(Context context, String basepath, CallbackTemplate<String> callback, FilterTemplate<String> filter) {
		try {
			DexFile dexFile = new DexFile(context.getPackageCodePath());
			Enumeration<String> it = dexFile.entries();
			while (it.hasMoreElements()) {
				String classname = it.nextElement();
				if (classname.startsWith(basepath) && (filter == null || filter.doWith(classname))) {
					callback.doWith(classname);
				}
			}
		} catch (BreakException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
