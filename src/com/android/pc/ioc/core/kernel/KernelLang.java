package com.android.pc.ioc.core.kernel;

import java.util.HashSet;
import java.util.Set;

public class KernelLang {

	/** NULL_OBJECT */
	public static final Object NULL_OBJECT = new Object();

	/** NULL_OBJECTS */
	public static final Object[] NULL_OBJECTS = new Object[] {};

	/** NULL_Strings */
	public static final String[] NULL_STRINGS = new String[] {};

	/**
	 * @param one
	 * @param two
	 * @param three
	 * @return
	 */
	public static int min(int one, int two, int three) {
		return (one = one < two ? one : two) < three ? one : three;
	}

	/**
	 * @author absir
	 * 
	 */
	@SuppressWarnings("serial")
	public static class BreakException extends Exception {

	}

	@SuppressWarnings("serial")
	public static class CauseRuntimeException extends RuntimeException {

		/**
		 * @param cause
		 */
		public CauseRuntimeException(Throwable cause) {
			super(cause);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Throwable#getCause()
		 */
		@Override
		public Throwable getCause() {
			Throwable cause = super.getCause();
			while (cause != null && cause != this) {
				if (cause instanceof CauseRuntimeException) {
					cause = cause.getCause();
				}
			}

			return cause;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Throwable#printStackTrace()
		 */
		@Override
		public void printStackTrace() {
			Throwable cause = getCause();
			if (cause != null) {
				cause.printStackTrace();
			}

			super.printStackTrace();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Throwable#getMessage()
		 */
		@Override
		public String getMessage() {
			Throwable cause = getCause();
			return cause == null ? super.getMessage() : cause.getMessage();
		}
	}

	/**
	 * @author absir
	 * 
	 * @param <T>
	 */
	public static class ObjectTemplate<T> {

		/** object */
		public T object;

		/**
		 * 
		 */
		public ObjectTemplate() {

		}

		/**
		 * @param object
		 */
		public ObjectTemplate(T object) {
			this.object = object;
		}
	}

	/**
	 * @author absir
	 * 
	 * @param <T>
	 */
	public static interface CloneTemplate<T> extends Cloneable {

		/**
		 * @return
		 */
		public T clone();

	}

	/**
	 * @author absir
	 * 
	 * @param <T>
	 */
	public static interface CallbackTemplate<T> {
		void doWith(T template);
	}

	/**
	 * @author absir
	 * 
	 * @param <T>
	 */
	public static interface CallbackBreak<T> {
		void doWith(T template) throws BreakException;
	}

	/**
	 * @author absir
	 * 
	 * @param <T>
	 */
	public static interface FilterTemplate<T> {
		boolean doWith(T template) throws BreakException;
	}

	/**
	 * 
	 * @author absir
	 * 
	 *         not safe in thread
	 */
	public static class PropertyFilter {

		/** includes */
		private Set<String> includes;

		/** excludes */
		private Set<String> excludes;

		/** propertyPath */
		private String propertyPath = "";

		/**
		 * @return
		 */
		public PropertyFilter newly() {
			PropertyFilter filter = new PropertyFilter();
			filter.includes = includes;
			filter.excludes = excludes;

			return filter;
		}

		/**
		 * 
		 */
		public void begin() {
			propertyPath = "";
		}

		/**
		 * @param properties
		 * @return
		 */
		public PropertyFilter inlcude(String... properties) {
			if (includes == null) {
				includes = new HashSet<String>();
			}

			KernelArray.copy(properties, includes);
			return this;
		}

		/**
		 * @param properties
		 * @return
		 */
		public PropertyFilter exlcude(String... properties) {
			if (excludes == null) {
				excludes = new HashSet<String>();
			}

			KernelArray.copy(properties, excludes);
			return this;
		}

		/**
		 * @return
		 */
		public boolean isNonePath() {
			return includes == null && excludes == null;
		}

		/**
		 * @return
		 */
		public boolean isMatch() {
			return true;
		}

		/**
		 * @param propertyName
		 * @return
		 */
		public boolean isMatch(String propertyName) {
			if (!KernelString.isEmpty(propertyName)) {
				if (KernelString.isEmpty(propertyPath)) {
					propertyPath = propertyName;

				} else {
					propertyPath = propertyPath + "." + propertyName;
				}
			}

			return isMatch();
		}

		/**
		 * @param propertyPath
		 * @return
		 */
		public boolean isMatchPath(String propertyPath) {
			this.propertyPath = propertyPath;
			return isMatch();
		}

		/**
		 * @param propertyPath
		 * @return
		 */
		public boolean isMatchPath(String propertyPath, String propertyName) {
			setPropertyPath(propertyPath);
			return isMatch(propertyName);
		}

		/**
		 * @return the propertyPath
		 */
		public String getPropertyPath() {
			return propertyPath;
		}

		/**
		 * @param propertyPath
		 *            the propertyPath to set
		 */
		public void setPropertyPath(String propertyPath) {
			this.propertyPath = propertyPath;
		}
	}
}
