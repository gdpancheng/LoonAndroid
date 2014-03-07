package com.android.pc.ioc.util;

import android.graphics.drawable.Drawable;

import com.android.pc.ioc.app.ApplicationBean;
import com.android.pc.ioc.core.kernel.KernelClass;

public class InjectResouceSupply {

	/**
	 * @author absir
	 * 
	 * @param <T>
	 */
	public static abstract class InjectResouceType<T> {

		/** type */
		private Class<?> type;

		/**
		 * 
		 */
		public InjectResouceType() {
			type = KernelClass.componentClass(this.getClass());
		}

		/**
		 * @param id
		 * @param name
		 * @return
		 */
		public T getResouce(int id, String name) {
			id = InjectViewUtils.getResouceId(id, type.getSimpleName(), name);
			if (id == InjectViewUtils.ID_ZERO) {
				return null;
			}

			return getResouce(id);
		}

		/**
		 * @param id
		 * @return
		 */
		protected abstract T getResouce(int id);
	}

	/** injectResouceTypes */
	public static InjectResouceType<?>[] injectResouceTypes = new InjectResouceType<?>[] {

	new InjectResouceType<String>() {

		@Override
		protected String getResouce(int id) {
			return ApplicationBean.getApplication().getResources().getString(id);
		}
	},

	new InjectResouceType<String[]>() {

		@Override
		protected String[] getResouce(int id) {
			return ApplicationBean.getApplication().getResources().getStringArray(id);
		}
	},

	new InjectResouceType<Drawable>() {

		@Override
		protected Drawable getResouce(int id) {
			return ApplicationBean.getApplication().getResources().getDrawable(id);
		}
	},
	};
}
