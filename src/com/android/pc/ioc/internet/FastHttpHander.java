package com.android.pc.ioc.internet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.android.pc.ioc.app.ApplicationBean;
import com.android.pc.ioc.internet.FastHttp.AjaxTask;
import com.android.pc.ioc.internet.FastHttp.TimeTask;
import com.android.pc.ioc.invoker.InjectInvoker;
import com.android.pc.ioc.util.ContextUtils;

public class FastHttpHander {
	/**
	 * 异步 post请求 无参数 默认下载配置器
	 * 
	 * @author gdpancheng@gmail.com 2013-5-20 下午2:49:57
	 * @param url
	 *            请求url
	 * @param object
	 *            回调函数
	 * @return void
	 */
	public static void ajax(String url, Object object) {
		ajax(url, null, InternetConfig.defaultConfig(), object);
	}

	/**
	 * 异步 post请求 无参数 自定义下载配置
	 * 
	 * @author gdpancheng@gmail.com 2013-5-20 下午2:50:48
	 * @param url
	 *            请求连接
	 * @param config
	 *            自定义下载配置
	 * @param object
	 *            回调函数
	 * @return void
	 */
	public static void ajax(String url, InternetConfig config, Object object) {
		ajax(url, null, config, object);
	}

	/**
	 * 异步 post请求 有参数 默认下载配置
	 * 
	 * @author gdpancheng@gmail.com 2013-5-20 下午2:51:48
	 * @param url
	 *            请求url
	 * @param params
	 *            请求参数
	 * @param object
	 *            回调函数
	 * @return void
	 */
	public static void ajax(String url, HashMap<String, String> params, Object object) {
		ajax(url, params, InternetConfig.defaultConfig(), object);
	}

	/**
	 * 异步 post异步获取 有参数 自定义下载配置
	 * 
	 * @author gdpancheng@gmail.com 2013-5-20 下午2:54:08
	 * @param url
	 *            请求连接
	 * @param params
	 *            请求参数
	 * @param config
	 *            请求配置
	 * @param object
	 *            回调函数
	 * @return void
	 */
	public static void ajax(String url, HashMap<String, String> params, final InternetConfig config, final Object object) {
		config.setRequest_type(InternetConfig.request_post);
		AjaxCallBack callBack = new AjaxCallBack() {
			@Override
			public void callBack(ResponseEntity status) {
				http_inject(status, object, config);
			}

			@Override
			public boolean stop() {
				return isDestory(object);
			}
		};
		new Thread(new AjaxTask(url, params, config, callBack)).start();
	}

	/**
	 * 异步post定时轮询
	 * 
	 * @author gdpancheng@gmail.com 2013-6-8 下午2:05:07
	 * @param url
	 * @param object
	 * @return void
	 */
	public static void ajax(String url, AjaxTimeCallBack object) {
		InternetConfig config = InternetConfig.defaultConfig();
		config.setRequest_type(InternetConfig.request_post);
		ajax(url, null, config, object);
	}

	/**
	 * 异步post定时轮询
	 * 
	 * @author gdpancheng@gmail.com 2013-6-8 下午2:06:54
	 * @param url
	 * @param params
	 * @param object
	 * @return void
	 */
	public static void ajax(String url, HashMap<String, String> params, AjaxTimeCallBack object) {
		InternetConfig config = InternetConfig.defaultConfig();
		config.setRequest_type(InternetConfig.request_post);
		ajax(url, params, config, object);
	}

	/**
	 * 异步 post异步获取 定时请求（轮询）有参数 自定义下载配置 回调函数
	 * 
	 * @author gdpancheng@gmail.com 2013-5-20 下午2:52:52
	 * @param url
	 *            请求连接
	 * @param params
	 *            请求参数
	 * @param config
	 *            请求配置
	 * @param object
	 *            回调函数
	 * @return void
	 */
	public static void ajax(String url, HashMap<String, String> params, InternetConfig config, AjaxTimeCallBack object) {
		config.setRequest_type(InternetConfig.request_post);
		new Thread(new TimeTask(url, params, config, object)).start();
	}

	/**
	 * 异步表单提交
	 * 
	 * @author gdpancheng@gmail.com 2013-6-8 下午2:03:10
	 * @param url
	 * @param object
	 * @return void
	 */
	public static void ajaxForm(String url, Object object) {
		ajaxForm(url, null, null, InternetConfig.defaultConfig(), object);
	}

	/**
	 * 异步表单提交 自定义配置
	 * 
	 * @author gdpancheng@gmail.com 2013-6-8 下午2:03:22
	 * @param url
	 * @param config
	 * @param object
	 * @return void
	 */
	public static void ajaxForm(String url, InternetConfig config, Object object) {
		ajaxForm(url, null, null, config, object);
	}

	/**
	 * 异步表单提交 有参数
	 * 
	 * @author gdpancheng@gmail.com 2013-6-8 下午2:03:37
	 * @param url
	 * @param params
	 * @param object
	 * @return void
	 */
	public static void ajaxForm(String url, HashMap<String, String> params, Object object) {
		ajaxForm(url, params, null, InternetConfig.defaultConfig(), object);
	}

	/**
	 * 自定义表单提交有参数 有文件
	 * 
	 * @author gdpancheng@gmail.com 2013-6-8 下午2:03:57
	 * @param url
	 * @param params
	 * @param files
	 * @param object
	 * @return void
	 */
	public static void ajaxForm(String url, HashMap<String, String> params, HashMap<String, File> files, Object object) {
		ajaxForm(url, params, files, InternetConfig.defaultConfig(), object);
	}

	/**
	 * 自定义表单提交有参数 有文件 自定义下载配置
	 * 
	 * @author gdpancheng@gmail.com 2013-6-8 下午2:04:38
	 * @param url
	 * @param params
	 * @param files
	 * @param config
	 * @param object
	 * @return void
	 */
	public static void ajaxForm(String url, HashMap<String, String> params, HashMap<String, File> files, final InternetConfig config, final Object object) {
		config.setRequest_type(InternetConfig.request_form);
		config.setFiles(files);
		AjaxCallBack callBack = new AjaxCallBack() {
			@Override
			public void callBack(ResponseEntity status) {
				http_inject(status, object, config);
			}

			@Override
			public boolean stop() {
				return isDestory(object);
			}
		};
		new Thread(new AjaxTask(url, params, config, callBack)).start();
	}

	/**
	 * 异步get获取
	 * 
	 * @author gdpancheng@gmail.com 2013-5-22 下午1:34:42
	 * @param url
	 *            请求路径
	 * @param object
	 *            回调函数
	 * @return void
	 */
	public static void ajaxGet(String url, Object object) {
		ajaxGet(url, null, InternetConfig.defaultConfig(), object);
	}

	/**
	 * 异步get获取
	 * 
	 * @author gdpancheng@gmail.com 2013-5-22 下午1:35:20
	 * @param url
	 *            请求路径
	 * @param config
	 *            配置文件
	 * @param object
	 *            回调函数
	 * @return void
	 */
	public static void ajaxGet(String url, InternetConfig config, Object object) {
		ajaxGet(url, null, config, object);
	}

	/**
	 * 异步get获取
	 * 
	 * @author gdpancheng@gmail.com 2013-5-22 下午1:36:13
	 * @param url
	 *            请求路径
	 * @param params
	 *            参数
	 * @param object
	 *            回调函数
	 * @return void
	 */
	public static void ajaxGet(String url, HashMap<String, String> params, Object object) {
		ajaxGet(url, params, InternetConfig.defaultConfig(), object);
	}

	/**
	 * get异步获取
	 * 
	 * @author gdpancheng@gmail.com 2013-5-22 下午1:36:39
	 * @param url
	 *            请求路径
	 * @param params
	 *            参数
	 * @param config
	 *            下载配置
	 * @param object
	 *            回调函数
	 * @return void
	 */
	public static void ajaxGet(String url, HashMap<String, String> params, final InternetConfig config, final Object object) {
		if (config == null) {
			ApplicationBean.logger.e(object.getClass().getSimpleName() + "  的网络请求配置不能为空\n");
			return;
		}
		config.setRequest_type(InternetConfig.request_get);

		AjaxCallBack callBack = new AjaxCallBack() {
			@Override
			public void callBack(ResponseEntity status) {
				http_inject(status, object, config);
			}

			@Override
			public boolean stop() {
				return isDestory(object);
			}
		};
		new Thread(new AjaxTask(url, params, config, callBack)).start();
	}

	/**
	 * get异步获取 定时请求（轮询）
	 * 
	 * @author gdpancheng@gmail.com 2013-5-22 下午1:38:32
	 * @param url
	 *            请求路径
	 * @param params
	 *            参数
	 * @param config
	 *            下载配置
	 * @param object
	 *            回调函数
	 * @return void
	 */
	public static void ajaxGet(String url, HashMap<String, String> params, InternetConfig config, AjaxTimeCallBack object) {
		if (config == null) {
			config = InternetConfig.defaultConfig();
		}
		config.setRequest_type(InternetConfig.request_get);
		new Thread(new TimeTask(url, params, config, object)).start();
	}

	/**
	 * 异步 post请求 无参数 默认下载配置器
	 * 
	 * @author gdpancheng@gmail.com 2013-5-20 下午2:49:57
	 * @param url
	 *            请求url
	 * @param object
	 *            回调函数
	 * @return void
	 */
	public static void ajaxWebServer(String url, String method, Object object) {
		InternetConfig config = new InternetConfig();
		config.setMethod(method);
		config.setRequest_type(InternetConfig.request_webserver);
		ajaxWebServer(url, method, null, config, object);
	}

	/**
	 * 异步 post请求 无参数 自定义下载配置
	 * 
	 * @author gdpancheng@gmail.com 2013-5-20 下午2:50:48
	 * @param url
	 *            请求连接
	 * @param config
	 *            自定义下载配置
	 * @param object
	 *            回调函数
	 * @return void
	 */
	public static void ajaxWebServer(String url, String method, InternetConfig config, Object object) {
		ajaxWebServer(url, method, null, config, object);
	}

	/**
	 * 异步 post请求 有参数 默认下载配置
	 * 
	 * @author gdpancheng@gmail.com 2013-5-20 下午2:51:48
	 * @param url
	 *            请求url
	 * @param params
	 *            请求参数
	 * @param object
	 *            回调函数
	 * @return void
	 */
	public static void ajaxWebServer(String url, String method, HashMap<String, String> params, Object object) {
		InternetConfig config = InternetConfig.defaultConfig();
		config.setMethod(method);
		config.setRequest_type(InternetConfig.request_webserver);
		ajaxWebServer(url, method, params, config, object);
	}

	/**
	 * 异步 post异步获取 有参数 自定义下载配置
	 * 
	 * @author gdpancheng@gmail.com 2013-5-20 下午2:54:08
	 * @param url
	 *            请求连接
	 * @param params
	 *            请求参数
	 * @param config
	 *            请求配置
	 * @param object
	 *            回调函数
	 * @return void
	 */
	public static void ajaxWebServer(String url, String method, HashMap<String, String> params, final InternetConfig config, final Object object) {
		if (config == null) {
			ApplicationBean.logger.e(object.getClass().getSimpleName() + " 的网络请求配置不能为空\n");
			return;
		}
		config.setMethod(method);
		config.setRequest_type(InternetConfig.request_webserver);
		AjaxCallBack callBack = new AjaxCallBack() {
			@Override
			public void callBack(ResponseEntity status) {
				http_inject(status, object, config);
			}

			@Override
			public boolean stop() {
				return isDestory(object);
			}
		};
		new Thread(new AjaxTask(url, params, config, callBack)).start();
	}

	/**
	 * 异步 post异步获取 定时请求（轮询）有参数 默认下载配置 回调函数
	 * 
	 * @author gdpancheng@gmail.com 2013-5-20 下午2:52:52
	 * @param url
	 *            请求连接
	 * @param params
	 *            请求参数
	 * @param config
	 *            请求配置
	 * @param object
	 *            回调函数
	 * @return void
	 */
	public static void ajaxWebServer(String url, String method, HashMap<String, String> params, AjaxTimeCallBack object) {
		InternetConfig config = InternetConfig.defaultConfig();
		config.setMethod(method);
		config.setRequest_type(InternetConfig.request_webserver);
		new Thread(new TimeTask(url, params, config, object)).start();
	}

	/**
	 * 异步 post异步获取 定时请求（轮询）有参数 自定义下载配置 回调函数
	 * 
	 * @author gdpancheng@gmail.com 2013-5-20 下午2:52:52
	 * @param url
	 *            请求连接
	 * @param params
	 *            请求参数
	 * @param config
	 *            请求配置
	 * @param object
	 *            回调函数
	 * @return void
	 */
	public static void ajaxWebServer(String url, String method, HashMap<String, String> params, InternetConfig config, AjaxTimeCallBack object) {
		if (config == null) {
			config = InternetConfig.defaultConfig();
		}
		config.setMethod(method);
		config.setRequest_type(InternetConfig.request_webserver);
		new Thread(new TimeTask(url, params, config, object)).start();
	}

	private static void http_inject(ResponseEntity entity, Object object, InternetConfig config) {
		ArrayList<InjectInvoker> ok = ContextUtils.getHttpOkInvokers(object.getClass(), config.getKey());
		if (ok == null) {
			ok = ContextUtils.getHttpOkInvokers(object.getClass(), ContextUtils.ID_NONE);
		}
		ArrayList<InjectInvoker> err = ContextUtils.getHttpErrInvokers(object.getClass(), config.getKey());
		if (err == null) {
			err = ContextUtils.getHttpErrInvokers(object.getClass(), ContextUtils.ID_NONE);
		}
		ArrayList<InjectInvoker> arrayList = ContextUtils.getHttpAllInvokers(object.getClass(), config.getKey());
		if (arrayList == null) {
			arrayList = ContextUtils.getHttpAllInvokers(object.getClass(), ContextUtils.ID_NONE);
		}

		if (entity.getStatus() == FastHttp.result_ok) {
			if (ok == null && arrayList == null) {
				ApplicationBean.logger.e(object.getClass().getSimpleName() + " 的网络请求没有增加回调方法注释 请检查\n");
			}
			if (ok == null) {
				if (arrayList != null) {
					for (InjectInvoker injectInvoker : arrayList) {
						injectInvoker.invoke(object, entity);
					}
				}
				return;
			}
			for (InjectInvoker injectInvoker : ok) {
				injectInvoker.invoke(object, entity);
			}
		} else {
			if (err == null && arrayList == null) {
				ApplicationBean.logger.e(object.getClass().getSimpleName() + " 的网络请求没有增加回调方法注释 请检查\n");
			}
			if (err == null) {
				if (arrayList != null) {
					for (InjectInvoker injectInvoker : arrayList) {
						injectInvoker.invoke(object, entity);
					}
				}
				return;
			}
			for (InjectInvoker injectInvoker : err) {
				injectInvoker.invoke(object, entity);
			}
		}
	}

	private static boolean isDestory(Object object) {
		if (Activity.class.isAssignableFrom(object.getClass())) {
			return ((Activity) object).isFinishing();
		}
		if (Fragment.class.isAssignableFrom(object.getClass())) {
			return ((Fragment) object).isDetached() || ((Fragment) object).isRemoving();
		}
		return false;
	}
}
