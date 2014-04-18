package com.android.pc.ioc.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.android.pc.ioc.app.ApplicationBean;
import com.android.pc.ioc.core.kernel.KernelString;
import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBefore;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectHttp;
import com.android.pc.ioc.inject.InjectHttpErr;
import com.android.pc.ioc.inject.InjectHttpOk;
import com.android.pc.ioc.inject.InjectInit;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.inject.InjectListener;
import com.android.pc.ioc.inject.InjectMethod;
import com.android.pc.ioc.inject.InjectOnNewIntent;
import com.android.pc.ioc.inject.InjectPLayer;
import com.android.pc.ioc.inject.InjectPause;
import com.android.pc.ioc.inject.InjectPullRefresh;
import com.android.pc.ioc.inject.InjectResource;
import com.android.pc.ioc.inject.InjectRestart;
import com.android.pc.ioc.inject.InjectResume;
import com.android.pc.ioc.inject.InjectStart;
import com.android.pc.ioc.inject.InjectStop;
import com.android.pc.ioc.inject.InjectView;
import com.android.pc.ioc.invoker.InjectHttps;
import com.android.pc.ioc.invoker.InjectInvoker;
import com.android.pc.ioc.invoker.InjectLayers;
import com.android.pc.ioc.invoker.InjectMethods;
import com.android.pc.ioc.invoker.InjectPLayers;
import com.android.pc.ioc.invoker.InjectResources;
import com.android.pc.ioc.invoker.InjectViews;
import com.android.pc.ioc.invoker.InjectViews.Views;

public class ContextUtils {

	/** ID_NONE */
	public final static int ID_NONE = -1;

	/** ID_ZERO */
	public final static int ID_ZERO = 0;

	/** 包括了 InjectLayer InjectView InjectBinder InjectResource InjectMethod InjectInit 的集合 */
	private static final Map<Class<?>, InjectInvoker> all_inject_layers = new HashMap<Class<?>, InjectInvoker>();
	private static final Map<Class<?>, ArrayList<InjectInvoker>> all_inject_views = new HashMap<Class<?>, ArrayList<InjectInvoker>>();
	private static final Map<Class<?>, ArrayList<InjectInvoker>> orther_inject_invokes = new HashMap<Class<?>, ArrayList<InjectInvoker>>();

	/** 包括了 有关activity生命周期 的 的集合 */
	private static final Map<Class<?>, HashMap<Class<?>, ArrayList<InjectInvoker>>> lift_InjectInvokes = new HashMap<Class<?>, HashMap<Class<?>, ArrayList<InjectInvoker>>>();
	/** 包括了网络请求的 的集合 */
	private static final Map<Class<?>, HashMap<Integer, ArrayList<InjectInvoker>>> http_InjectInvokes = new HashMap<Class<?>, HashMap<Integer, ArrayList<InjectInvoker>>>();
	private static final Map<Class<?>, HashMap<Integer, ArrayList<InjectInvoker>>> http_InjectInvokes_OK = new HashMap<Class<?>, HashMap<Integer, ArrayList<InjectInvoker>>>();
	private static final Map<Class<?>, HashMap<Integer, ArrayList<InjectInvoker>>> http_InjectInvokes_Err = new HashMap<Class<?>, HashMap<Integer, ArrayList<InjectInvoker>>>();

	private static HashSet<Class<?>> classes = new HashSet<Class<?>>() {
		private static final long serialVersionUID = -2816879839908314497L;
		{
			add(Drawable.class);
			add(String.class);
		}
	};

	public static ArrayList<InjectInvoker> getContextInvokers(Class<?> contextType, Class<?> method) {
		return lift_InjectInvokes.get(contextType).get(method);
	}

	public static ArrayList<InjectInvoker> getHttpAllInvokers(Class<?> contextType, int key) {
		if (!http_InjectInvokes.containsKey(contextType)) {
			return null;
		}
		HashMap<Integer, ArrayList<InjectInvoker>> hashMap = http_InjectInvokes.get(contextType);
		if (!hashMap.containsKey(key)) {
			return null;
		}
		return hashMap.get(key);
	}

	public static ArrayList<InjectInvoker> getHttpOkInvokers(Class<?> contextType, int key) {
		if (!http_InjectInvokes_OK.containsKey(contextType)) {
			return null;
		}
		HashMap<Integer, ArrayList<InjectInvoker>> hashMap = http_InjectInvokes_OK.get(contextType);
		if (!hashMap.containsKey(key)) {
			return null;
		}
		return hashMap.get(key);
	}

	public static ArrayList<InjectInvoker> getHttpErrInvokers(Class<?> contextType, int key) {
		if (!http_InjectInvokes_Err.containsKey(contextType)) {
			return null;
		}
		HashMap<Integer, ArrayList<InjectInvoker>> hashMap = http_InjectInvokes_Err.get(contextType);
		if (!hashMap.containsKey(key)) {
			return null;
		}
		return hashMap.get(key);
	}

	/**
	 * 根据contextType 获取AfterCreate和BeforeCreate 等等 activity生命周期的反射
	 * 
	 * @author gdpancheng@gmail.com 2013-11-1 下午1:10:02
	 * @param contextType
	 * @return
	 * @return InjectInvoker[][]
	 */
	public static void getCreateInvokers(final Class<?> contextType) {
		HashMap<Class<?>, ArrayList<InjectInvoker>> injectInvokers = lift_InjectInvokes.get(contextType);
		if (injectInvokers != null) {
			return;
		}

		lift_InjectInvokes.put(contextType, new HashMap<Class<?>, ArrayList<InjectInvoker>>());

		http_InjectInvokes.put(contextType, new HashMap<Integer, ArrayList<InjectInvoker>>());

		http_InjectInvokes_OK.put(contextType, new HashMap<Integer, ArrayList<InjectInvoker>>());

		http_InjectInvokes_Err.put(contextType, new HashMap<Integer, ArrayList<InjectInvoker>>());

		Class<?> template = contextType;
		while (template != null && template != Object.class) {
			// 过滤掉基类 因为基类是不包含注解的
			if (template.getName().equals("android.app.Activity") || template.getName().equals("android.support.v4.app.FragmentActivity") || template.getName().equals("android.support.v4.app.Fragment") || template.getName().equals("android.app.Fragment")) {
				break;
			}

			// ---------------------------------------------------------------------------------------------
			HashMap<Class<?>, ArrayList<InjectInvoker>> aInvokerLists = lift_InjectInvokes.get(contextType);
			HashMap<Integer, ArrayList<InjectInvoker>> https = http_InjectInvokes.get(contextType);
			HashMap<Integer, ArrayList<InjectInvoker>> https_ok = http_InjectInvokes_OK.get(contextType);
			HashMap<Integer, ArrayList<InjectInvoker>> https_err = http_InjectInvokes_Err.get(contextType);

			Method[] methods = template.getDeclaredMethods();
			for (int j = 0; j < methods.length; j++) {
				Method method = methods[j];
				if (method.getAnnotation(InjectBefore.class) != null) {
					if (!aInvokerLists.containsKey(InjectBefore.class)) {
						aInvokerLists.put(InjectBefore.class, new ArrayList<InjectInvoker>());
					}
					aInvokerLists.get(InjectBefore.class).add(new InjectMethods(method, null, null, null));
				} else if (method.getAnnotation(InjectOnNewIntent.class) != null) {
					if (!aInvokerLists.containsKey(InjectOnNewIntent.class)) {
						aInvokerLists.put(InjectOnNewIntent.class, new ArrayList<InjectInvoker>());
					}
					aInvokerLists.get(InjectOnNewIntent.class).add(new InjectMethods(method, null, null, null));
				} else if (method.getAnnotation(InjectPause.class) != null) {
					if (!aInvokerLists.containsKey(InjectPause.class)) {
						aInvokerLists.put(InjectPause.class, new ArrayList<InjectInvoker>());
					}
					aInvokerLists.get(InjectPause.class).add(new InjectMethods(method, null, null, null));
				} else if (method.getAnnotation(InjectResume.class) != null) {
					if (!aInvokerLists.containsKey(InjectResume.class)) {
						aInvokerLists.put(InjectResume.class, new ArrayList<InjectInvoker>());
					}
					aInvokerLists.get(InjectResume.class).add(new InjectMethods(method, null, null, null));
				} else if (method.getAnnotation(InjectRestart.class) != null) {
					if (!aInvokerLists.containsKey(InjectRestart.class)) {
						aInvokerLists.put(InjectRestart.class, new ArrayList<InjectInvoker>());
					}
					aInvokerLists.get(InjectRestart.class).add(new InjectMethods(method, null, null, null));
				} else if (method.getAnnotation(InjectStart.class) != null) {
					if (!aInvokerLists.containsKey(InjectStart.class)) {
						aInvokerLists.put(InjectStart.class, new ArrayList<InjectInvoker>());
					}
					aInvokerLists.get(InjectStart.class).add(new InjectMethods(method, null, null, null));
				} else if (method.getAnnotation(InjectStop.class) != null) {
					if (!aInvokerLists.containsKey(InjectStop.class)) {
						aInvokerLists.put(InjectStop.class, new ArrayList<InjectInvoker>());
					}
					aInvokerLists.get(InjectStop.class).add(new InjectMethods(method, null, null, null));
				} else if (method.getAnnotation(InjectHttp.class) != null) {
					InjectHttp injectHttp = method.getAnnotation(InjectHttp.class);
					int[] keys = injectHttp.value();
					for (int i = 0; i < keys.length; i++) {
						if (!https.containsKey(keys[i])) {
							https.put(keys[i], new ArrayList<InjectInvoker>());
						}
						https.get(keys[i]).add(new InjectHttps(method));
					}
				} else if (method.getAnnotation(InjectHttpOk.class) != null) {
					InjectHttpOk injectHttp = method.getAnnotation(InjectHttpOk.class);
					int[] keys = injectHttp.value();
					for (int i = 0; i < keys.length; i++) {
						if (!https_ok.containsKey(keys[i])) {
							https_ok.put(keys[i], new ArrayList<InjectInvoker>());
						}
						https_ok.get(keys[i]).add(new InjectHttps(method));
					}
				} else if (method.getAnnotation(InjectHttpErr.class) != null) {
					InjectHttpErr injectHttp = method.getAnnotation(InjectHttpErr.class);
					int[] keys = injectHttp.value();
					for (int i = 0; i < keys.length; i++) {
						if (!https_err.containsKey(keys[i])) {
							https_err.put(keys[i], new ArrayList<InjectInvoker>());
						}
						https_err.get(keys[i]).add(new InjectHttps(method));
					}
				}
			}
			// ---------------------------------------------------------------------------------------------
			template = template.getSuperclass();
		}
	}

	/**
	 * 根据class获取当前类的所有注解
	 * 
	 * @author gdpancheng@gmail.com 2014-1-21 上午11:42:27
	 * @param clazz
	 * @param obj
	 * @param superClass
	 * @return
	 * @return ArrayList<InjectInvoker>
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<InjectInvoker> getViewInvokers(final Class<?> clazz, final Object obj, final Class<?> superClass) {
		// 集合里面有这个类的集合 那么就直接返回了
		ArrayList<InjectInvoker> all_list = new ArrayList<InjectInvoker>();

		ArrayList<InjectInvoker> layers_list = new ArrayList<InjectInvoker>();
		ArrayList<InjectInvoker> views_list = new ArrayList<InjectInvoker>();
		ArrayList<InjectInvoker> orther_list = new ArrayList<InjectInvoker>();
		// ----------------------------------------------------------------------------------------------------------
		Class<?> template = clazz;
		if (all_inject_layers.containsKey(clazz) && orther_inject_invokes.containsKey(clazz)) {
			while (template != null && template != Object.class && template != superClass) {
				if (all_inject_layers.containsKey(template) && Activity.class.isAssignableFrom(template)) {
					layers_list.add(0, all_inject_layers.get(template));
				}
				if (all_inject_views.containsKey(template) && Activity.class.isAssignableFrom(template)) {
					views_list.addAll(0, all_inject_views.get(template));
				}
				if (orther_inject_invokes.containsKey(template) && Activity.class.isAssignableFrom(template)) {
					orther_list.addAll(0, orther_inject_invokes.get(template));
				}
				template = template.getSuperclass();
			}
			all_list.addAll(layers_list);
			all_list.addAll(views_list);
			all_list.addAll(orther_list);
			if (all_list.size() > 0) {
				return all_list;
			}
		}
		// ----------------------------------------------------------------------------------------------------------
		template = clazz;
		// 为了记录存在下拉刷新的组件,也就是说一个页面只支持一个下拉刷新
		InjectViews pull_down;

		while (template != null && template != Object.class && template != superClass) {
			// 过滤掉基类 因为基类是不包含注解的
			if (template.getName().equals("android.app.Activity") || template.getName().equals("android.support.v4.app.FragmentActivity") || template.getName().equals("android.support.v4.app.Fragment")) {
				break;
			}
			// 重置为空
			pull_down = null;
			// 用来存储每一个类的注解
			ArrayList<InjectInvoker> local_list = new ArrayList<InjectInvoker>();
			ArrayList<InjectInvoker> localview_list = new ArrayList<InjectInvoker>();
			// 此变量是用来标注当前类是否是Activity的子类，因为只有Activity才需要setContentView
			// 另一种就是Fragment 这种情况下是不需要setContentView 所以要单独区分开来
			boolean isActivity = false;
			if (Activity.class.isAssignableFrom(clazz)) {
				isActivity = true;
			}
			InjectLayer injectLayer = template.getAnnotation(InjectLayer.class);
			InjectLayers local_layers = null;
			// 如果是Activity 则把获取到的layout添加到集合中
			if (isActivity && injectLayer != null && injectLayer.value() != ContextUtils.ID_ZERO) {
				local_layers = new InjectLayers(injectLayer.value(), injectLayer.isFull(), injectLayer.isTitle(), injectLayer.parent(), InjectViewUtils.Inject_Excutors[0]);
			}
			// 如果不是Activity 则是fragment 那么我们需要把当前的Object对象保持到集合中 因为它不是
			// activity 所以不能通过Context.findbyid而只能通过 view.findbyid 当不是Activity的时候activity为fragment里面的view
			if (!isActivity) {
				local_layers = new InjectLayers(ContextUtils.ID_NONE, false, false, ContextUtils.ID_NONE, InjectViewUtils.Inject_Excutors[1].setObject(obj));
			}

			InjectPLayer injectPLayer = template.getAnnotation(InjectPLayer.class);

			if (injectPLayer != null && layers_list.size() > 0) {
				InjectLayers injectLayers = (InjectLayers) layers_list.get(0);
				if (injectLayers.getParent() != ID_NONE) {
					InjectPLayers injectPLayers = new InjectPLayers(injectPLayer.value(), injectPLayer.isFull(), injectPLayer.isTitle(), InjectViewUtils.Inject_Excutors[0]);
					injectLayers.setInjectPLayers(injectPLayers);
				}
			}

			// 如果是Activity InjectLayer没有设置 则提示错误
			if (isActivity && injectLayer == null && injectPLayer == null) {
				if (clazz == template) {
					ApplicationBean.logger.d(template + " 无法获取到对应layout的ID 请检查injectLayer或者injectPLayer是否设置\n");
				}
			}

			// 获得当前类的所有的字段
			Field[] fields = template.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				// 获取view注解
				InjectView injectView = field.getAnnotation(InjectView.class);
				if (injectView != null) {
					int id = injectView.value();
					// 如果注解为空 那么我们就根据名字取获取其id
					if (id == ContextUtils.ID_NONE) {
						try {
							id = InjectViewUtils.getResouceId("id", field.getName());
						} catch (Exception e) {
							ApplicationBean.logger.e(template + " 变量" + field.getName() + "无法获取到对应的ID 请检查InjectView的参数\n");
							e.printStackTrace();
						}
					}
					// 判断listview 是否添加图片滑动停止加载 如果InjectView含有此参数 则表示使用
					boolean isAsy = injectView.isasy();
					boolean pull = injectView.pull();
					boolean down = injectView.down();
					InjectViews injectViews = new InjectViews(id, isActivity ? InjectViewUtils.Inject_Excutors[0] : InjectViewUtils.Inject_Excutors[1].setObject(obj), field, isAsy, injectView.pull(), injectView.down(), null);
					if (pull || down) {
						pull_down = injectViews;
					}
					InjectBinder[] injectBinders = injectView.binders();
					if (injectBinders != null) {
						for (InjectBinder injectBinder : injectBinders) {
							String method = injectBinder.method();
							Class<?>[] classes = injectBinder.listeners();
							Views views = injectViews.new Views(method, classes);
							injectViews.setViews(views);
						}
						// 这里是要保证父activity或者fragment的view的注入必须在子类之前
						if (clazz != template) {
							localview_list.add(0, injectViews);
						} else {
							localview_list.add(injectViews);
						}
						continue;
					}
				}
				// 资源文件变量的绑定 目前只支持图片和字符串
				InjectResource Inject = field.getAnnotation(InjectResource.class);
				if (Inject != null) {
					if (!classes.contains(field.getType())) {
						break;
					}
					int id = Inject.value();
					if (id == ContextUtils.ID_NONE) {
						try {
							id = InjectViewUtils.getResouceId(KernelString.capitalize(field.getType().getSimpleName()), field.getName());
						} catch (Exception e) {
							ApplicationBean.logger.e(template + " 变量" + field.getName() + "无法获取到对应的ID 请检查InjectView的参数\n");
							e.printStackTrace();
						}
					}
					if (field.getType() == Drawable.class) {
						InjectResources injectResources = new InjectResources(id, field, InjectResouceSupply.injectResouceTypes[2], null);
						local_list.add(injectResources);
					}
					if (field.getType() == String.class) {
						InjectResources injectResources = new InjectResources(id, field, InjectResouceSupply.injectResouceTypes[0], null);
						local_list.add(injectResources);
					}
				}
			}

			Class<?>[] classes = template.getDeclaredClasses();
			if (classes != null) {
				int id = 0;
				for (int i = 0; i < classes.length; i++) {
					InjectAll allInject = classes[i].getAnnotation(InjectAll.class);
					if (allInject == null) {
						continue;
					}
					InjectBinder inBinder = allInject.value();
					Field[] allFields = classes[i].getDeclaredFields();
					for (int j = 0; j < allFields.length; j++) {
						if (View.class.isAssignableFrom(allFields[j].getType())) {
							try {
								id = 0;
								id = InjectViewUtils.getResouceId("id", allFields[j].getName());
							} catch (Exception e) {
								ApplicationBean.logger.e("内部类 " + template + " 变量 " + allFields[j].getName() + " 无法获取到对应的ID 请检查InjectView的参数\n");
								e.printStackTrace();
							}
							InjectViews injectViews = new InjectViews(id, isActivity ? InjectViewUtils.Inject_Excutors[0] : InjectViewUtils.Inject_Excutors[1].setObject(obj), allFields[j], false, false, false, classes[i]);

							InjectBinder injectBinder = allFields[j].getAnnotation(InjectBinder.class);
							if (injectBinder != null) {
								Views views = injectViews.new Views(injectBinder.method(), injectBinder.listeners());
								injectViews.setViews(views);
							}else if (inBinder.method().length()>0) {
								Views views = injectViews.new Views(inBinder.method(), inBinder.listeners());
								injectViews.setViews(views);
							}
							if (clazz != template) {
								localview_list.add(0, injectViews);
							} else {
								localview_list.add(injectViews);
							}
							continue;
						}
						
						try {
							id = InjectViewUtils.getResouceId(KernelString.capitalize(allFields[j].getType().getSimpleName()), allFields[j].getName());
						} catch (Exception e) {
							ApplicationBean.logger.e(template + " 变量" + allFields[j].getName() + "无法获取到对应的ID 请检查InjectView的参数\n");
							e.printStackTrace();
						}
						if (allFields[j].getType() == Drawable.class) {
							InjectResources injectResources = new InjectResources(id, allFields[j], InjectResouceSupply.injectResouceTypes[2], classes[i]);
							local_list.add(injectResources);
						}
						if (allFields[j].getType() == String.class) {
							InjectResources injectResources = new InjectResources(id, allFields[j], InjectResouceSupply.injectResouceTypes[0], classes[i]);
							local_list.add(injectResources);
						}
					}
				}
			}

			Method[] methods = template.getDeclaredMethods();
			for (int i = 0; i < methods.length; i++) {
				Method method = methods[i];
				if (pull_down != null) {
					InjectPullRefresh injectPullRefresh = method.getAnnotation(InjectPullRefresh.class);
					if (injectPullRefresh != null) {
						pull_down.setMethod(method);
					}
				}
				InjectMethod injectMethod = method.getAnnotation(InjectMethod.class);
				if (injectMethod != null) {
					InjectListener[] injectListener = injectMethod.value();
					if (injectListener != null) {
						for (InjectListener injectInvoker : injectListener) {
							InjectMethods injectMethods = new InjectMethods(method, injectInvoker.ids(), injectInvoker.listeners(), isActivity ? InjectViewUtils.Inject_Excutors[0] : InjectViewUtils.Inject_Excutors[1].setObject(obj));
							local_list.add(injectMethods);
						}
					}
				}
				InjectInit Inject = method.getAnnotation(InjectInit.class);
				if (Inject == null) {
					continue;
				}
				InjectMethods injectMethods = new InjectMethods(method, null, null, null);
				local_list.add(injectMethods);
			}

			if (local_layers != null) {
				layers_list.add(0, local_layers);
				all_inject_layers.put(template, local_layers);
			}
			views_list.addAll(localview_list);
			all_inject_views.put(template, localview_list);
			orther_list.addAll(0, local_list);
			orther_inject_invokes.put(template, local_list);
			template = template.getSuperclass();
		}
		all_list.addAll(layers_list);
		all_list.addAll(views_list);
		all_list.addAll(orther_list);
		return all_list;
	}

	public static void getFactoryProvider() {
		Class<?>[] classes = null;
		PackageManager pManager = ApplicationBean.getApplication().getPackageManager();
		try {
			PackageInfo packageInfo = pManager.getPackageInfo(ApplicationBean.getApplication().getPackageName(), PackageManager.GET_ACTIVITIES);
			ActivityInfo[] activityInfos = packageInfo.activities;
			classes = new Class[activityInfos.length];
			for (int i = 0; i < activityInfos.length; i++) {
				ActivityInfo activityInfo = activityInfos[i];
				try {
					classes[i] = Class.forName(activityInfo.name);
				} catch (ClassNotFoundException e) {
					continue;
				}
			}
		} catch (NameNotFoundException e) {
		}

		if (classes == null) {
			return;
		}

		for (int i = 0; i < classes.length; i++) {
			Class<?> clazz = classes[i];
			ContextUtils.getViewInvokers(clazz, null, Activity.class);
			ContextUtils.getCreateInvokers(clazz);
		}
	}
}
