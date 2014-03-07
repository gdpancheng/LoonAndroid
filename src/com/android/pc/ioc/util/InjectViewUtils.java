package com.android.pc.ioc.util;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;

import com.android.pc.ioc.app.ApplicationBean;
import com.android.pc.ioc.core.kernel.KernelClass;
import com.android.pc.ioc.core.kernel.KernelObject;
import com.android.pc.ioc.core.kernel.KernelString;
import com.android.pc.ioc.view.InjectExcutor;

public class InjectViewUtils {

	/** ID_NONE */
	public final static int ID_NONE = -1;

	/** ID_ZERO */
	public final static int ID_ZERO = 0;

	/** 资源文件类 */
	private static Class<?> R_Resouce_Class = null;

	/** Type_Map_Resouce_Class */
	private static final Map<String, Object> Type_Map_Resouce_Class = new HashMap<String, Object>();

	/**
	 * @param application
	 */
	public static void setApplication(Application application) {
		R_Resouce_Class = KernelClass.forName(application.getPackageName() + "." + "R");
	}

	/**
	 * @param type
	 * @param name
	 * @return
	 */
	public static Integer getResouceId(String type, String name) {
		Object resouce = Type_Map_Resouce_Class.get(type);
		if (resouce == null) {
			synchronized (type) {
				resouce = Type_Map_Resouce_Class.get(type);
				if (resouce == null) {
					resouce = KernelClass.forName(R_Resouce_Class.getName() + "$" + KernelString.capitalize(type));
				}
				if (resouce != null) {
					Type_Map_Resouce_Class.put(type, resouce);
				}
			}
		}
		return resouce == null ? null : (Integer) KernelObject.get(resouce, name);
	}

	/**
	 * @param id
	 * @param type
	 * @param name
	 * @return
	 */
	public static int getResouceId(int id, String type, String name) {
		if (id == ID_NONE) {
			Integer integer = getResouceId(type, name);
			id = integer == null ? ID_ZERO : integer;
		}

		return id;
	}

	/** Inject_Excutors */
	@SuppressWarnings("rawtypes")
	public static InjectExcutor[] Inject_Excutors = new InjectExcutor[] {

	new InjectExcutor<Activity>() {

		@Override
		public void setContentView(Activity object, int id) {
			try {
				object.setContentView(id);
			} catch (Exception e) {
				ApplicationBean.logger.e(object.getClass().getSimpleName() + " setContentView() 出错 请检查InjectLayer的布局\n");
				e.printStackTrace();
			}
		}

		@Override
		public View loadView(Activity object, int id) {
			return object.getLayoutInflater().inflate(id, null);
		}

		@Override
		public View findViewById(Activity object, int id) {
			try {
				return object.findViewById(id);
			} catch (Exception e) {
				ApplicationBean.logger.e(object.getClass().getSimpleName() + " findViewById() 出错 请检查InjectView的参数\n");
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public View findViewById(int id) {
			return object != null ? ((View) object).findViewById(id) : null;
		}

	},

	new InjectExcutor<View>() {

		@Override
		public void setContentView(View object, int id) {
		}

		@Override
		public View loadView(View object, int id) {
			return LayoutInflater.from(object.getContext()).inflate(id, null);
		}

		@Override
		public View findViewById(View object, int id) {
			return object.findViewById(id);
		}

		@Override
		public View findViewById(int id) {
			return object != null ? ((View) object).findViewById(id) : null;
		}

	},

	new InjectExcutor<Dialog>() {
		@Override
		public void setContentView(Dialog object, int id) {
		}

		@Override
		public View loadView(Dialog object, int id) {
			return object.getLayoutInflater().inflate(id, null);
		}

		@Override
		public View findViewById(Dialog object, int id) {
			return object.findViewById(id);
		}

		@Override
		public View findViewById(int id) {
			return object != null ? ((View) object).findViewById(id) : null;
		}
	}, };
}
