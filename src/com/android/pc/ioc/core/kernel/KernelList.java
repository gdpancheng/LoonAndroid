package com.android.pc.ioc.core.kernel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class KernelList {

	/** EMPTY_LIST */
	public static final List EMPTY_LIST = new ArrayList();

	/**
	 * @param list
	 * @param index
	 * @return
	 */
	public static <T> T get(List<T> list, int index) {
		return get(list, null, index);
	}

	/**
	 * @param list
	 * @param defaultValue
	 * @param index
	 * @return
	 */
	public static <T> T get(List<T> list, T defaultValue, int index) {
		if (index >= 0 && index < list.size()) {
			return list.get(index);
		}

		return defaultValue;
	}

	/**
	 * @author absir
	 * 
	 */
	public interface Orderable {
		public int getOrder();
	}

	/**
	 * @param list
	 * @param element
	 */
	public static <T extends Orderable> void addOrder(List<T> list, T element) {
		int order = element.getOrder();
		int size = list.size();
		for (int i = 0; i < size; i++) {
			if (order < list.get(i).getOrder()) {
				list.add(i, element);
				return;
			}
		}

		list.add(element);
	}

	/**
	 * @param list
	 * @param element
	 */
	public static <T extends Orderable> void addOrderOnly(List<T> list, T element) {
		int order = element.getOrder();
		int size = list.size();
		for (int i = 0; i < size; i++) {
			Orderable orderable = list.get(i);
			if (orderable == element) {
				return;
			}

			if (order < orderable.getOrder()) {
				list.add(i, element);
				return;
			}
		}

		list.add(element);
	}

	/** COMPARATOR */
	public static final Comparator<Orderable> COMPARATOR = new Comparator<Orderable>() {

		@Override
		public int compare(Orderable lhs, Orderable rhs) {
			// TODO Auto-generated method stub
			return lhs.getOrder() - rhs.getOrder();
		}
	};

	/**
	 * @param list
	 */
	public static <T extends Orderable> void sortOrderable(List<T> list) {
		Collections.sort(list, COMPARATOR);
	}

	/** COMPARATOR_DESC */
	public static final Comparator<Orderable> COMPARATOR_DESC = new Comparator<Orderable>() {

		@Override
		public int compare(Orderable lhs, Orderable rhs) {
			// TODO Auto-generated method stub
			return rhs.getOrder() - lhs.getOrder();
		}
	};

	/**
	 * @param list
	 */
	public static <T extends Orderable> void sortOrderableDesc(List<T> list) {
		Collections.sort(list, COMPARATOR_DESC);
	}

	/**
	 * @param element
	 * @return
	 */
	public static int getOrder(Object element) {
		return element instanceof Orderable ? ((Orderable) element).getOrder() : 0;
	}

	/**
	 * @param list
	 * @param element
	 */
	public static void addOrderObject(List list, Object element) {
		int order = getOrder(element);
		int size = list.size();
		for (int i = 0; i < size; i++) {
			if (order < getOrder(list.get(i))) {
				list.add(i, element);
				return;
			}
		}

		list.add(element);
	}

	/** COMMON_COMPARATOR */
	public static final Comparator COMMON_COMPARATOR = new Comparator() {

		@Override
		public int compare(Object lhs, Object rhs) {
			// TODO Auto-generated method stub
			return getOrder(lhs) - getOrder(rhs);
		}
	};

	/**
	 * @param list
	 */
	public static void sortCommonObjects(List list) {
		Collections.sort(list, COMMON_COMPARATOR);
	}
}
