package com.android.pc.ioc.core.kernel;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public abstract class KernelMap {

	/**
	 * @param map
	 * @return
	 */
	public static <K, V> K key(Map<K, V> map) {
		if (map != null) {
			Iterator<Entry<K, V>> iterator = map.entrySet().iterator();
			if (iterator.hasNext()) {
				return iterator.next().getKey();
			}
		}

		return null;
	}

	/**
	 * @param map
	 * @return
	 */
	public static <K, V> V value(Map<K, V> map) {
		if (map != null) {
			Iterator<Entry<K, V>> iterator = map.entrySet().iterator();
			if (iterator.hasNext()) {
				return iterator.next().getValue();
			}
		}

		return null;
	}

	/**
	 * @param map
	 * @return
	 */
	public static <K, V> Entry<K, V> entry(Map<K, V> map) {
		if (map != null) {
			Iterator<Entry<K, V>> iterator = map.entrySet().iterator();
			if (iterator.hasNext()) {
				return iterator.next();
			}
		}

		return null;
	}

	/**
	 * @param map
	 * @param key
	 * @return
	 */
	public static <V> V get(Map<?, V> map, Object key) {
		if (key == null) {
			return null;
		}

		return map.get(key);
	}

	/**
	 * @param map
	 * @param to
	 */
	public static void copy(Map<Object, Object> map, Map<Object, Object> to) {
		for (Entry<Object, Object> entry : map.entrySet()) {
			to.put(entry.getKey(), entry.getValue());
		}
	}
}
