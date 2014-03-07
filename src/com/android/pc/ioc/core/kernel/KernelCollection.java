package com.android.pc.ioc.core.kernel;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class KernelCollection {

	/**
	 * @param list
	 * @param componentType
	 * @return
	 */
	public static <T> T[] toArray(Collection<? extends T> collection, Class<T> componentType) {
		if (collection == null) {
			return null;
		}

		T[] array = (T[]) Array.newInstance(componentType, collection.size());
		int i = 0;
		for (T el : collection) {
			array[i++] = el;
		}

		return array;
	}

	/**
	 * @param collection
	 * @param componentType
	 * @return
	 */
	public static <T> T[] castToArray(Collection collection, Class<T> componentType) {
		if (collection == null) {
			return null;
		}

		T[] array = (T[]) Array.newInstance(componentType, collection.size());
		int i = 0;
		for (Object el : collection) {
			array[i++] = KernelDyna.to(el, componentType);
		}

		return array;
	}

	/**
	 * @param list
	 * @param array
	 */
	public static <T> void addAll(Collection<T> list, T[] array) {
		for (T value : array) {
			list.add(value);
		}
	}

	/**
	 * @param collection
	 * @param to
	 */
	public static void copy(Collection collection, Collection to) {
		for (Object el : collection) {
			to.add(el);
		}
	}

	/**
	 * @param collection
	 * @return
	 */
	public static Map toMap(Collection collection) {
		if (collection == null) {
			return null;
		}

		Map map = new HashMap();
		Object key = null;
		for (Object el : collection) {
			if (key == null) {
				key = el;

			} else {
				map.put(key, el);
				key = null;
			}
		}

		return map;
	}
}
