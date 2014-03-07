package com.android.pc.ioc.core.kernel;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings({ "rawtypes" })
public abstract class KernelString {

	/**
	 * @param object
	 * @return null or String
	 */
	public static String valueOf(Object object) {
		if (object == null) {
			return null;

		} else {
			return String.valueOf(object);
		}
	}

	/**
	 * @param string
	 * @return
	 */
	public static boolean empty(String string) {
		return "".equals(string);
	}

	/**
	 * @param string
	 * @return
	 */
	public static boolean isEmpty(String string) {
		return string == null || empty(string);
	}

	/**
	 * @param string
	 * @return
	 */
	public static String capitalize(String string) {
		char[] data = string.toCharArray();
		data[0] = Character.toLowerCase(data[0]);
		return String.valueOf(data);
	}

	/**
	 * @param string
	 * @return
	 */
	public static String uncapitalize(String string) {
		char[] data = string.toCharArray();
		if (data.length < 2 || data[1] > 'Z' || data[1] < 'A') {
			data[0] = Character.toUpperCase(data[0]);
		}

		return String.valueOf(data);
	}

	/**
	 * @param str
	 * @param ch
	 * @return
	 */
	public static String lastString(String str, char ch) {
		int index = str.lastIndexOf(ch);
		if (index >= 0) {
			str = str.substring(index + 1);
		}

		return str;
	}

	/**
	 * @param str
	 * @param ch
	 * @return
	 */
	public static String lastSubString(String str, char ch) {
		int index = str.lastIndexOf(ch);
		if (index >= 0) {
			str = str.substring(0, index - 1);
		}

		return str;
	}

	/**
	 * @param string
	 * @param length
	 * @return
	 */
	public static String leftString(String string, int length) {
		return string.substring(0, length);
	}

	/**
	 * @param string
	 * @param length
	 * @return
	 */
	public static String leftSubString(String string, int length) {
		return string.substring(length);
	}

	/**
	 * @param string
	 * @param length
	 * @return
	 */
	public static String rightString(String string, int length) {
		return string.substring(string.length() - length);
	}

	/**
	 * @param string
	 * @param length
	 * @return
	 */
	public static String rightSubString(String string, int length) {
		return string.substring(0, string.length() - length);
	}

	/**
	 * @param str
	 * @param strs
	 * @return
	 */
	public static boolean matchStrings(String str, String[] strs) {
		if (strs == null) {
			return false;
		}

		for (String string : strs) {
			if (str.indexOf(string) >= 0) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @param string
	 * @param target
	 * @param replacement
	 * @return
	 */
	public static String replaceLast(String string, String target, String replacement) {
		if (isEmpty(string) || isEmpty(target)) {
			return string;
		}

		int index = string.lastIndexOf(target);
		return string.substring(0, index) + replacement + string.substring(index + target.length());
	}

	/** SEQUENCE_SIZE */
	public static final int SEQUENCE_SIZE = 'z' - 'a' + 1;

	/**
	 * @param sequence
	 * @return
	 */
	public static String getSequenceString(int sequence) {
		StringBuilder stringBuilder = new StringBuilder();
		while ((sequence -= SEQUENCE_SIZE) > SEQUENCE_SIZE) {
			stringBuilder.append('z');
		}

		if (sequence >= 0) {
			stringBuilder.append((char) sequence);
		}

		return stringBuilder.toString();
	}

	/**
	 * @param sequence
	 * @return
	 */
	public static String nextSequenceString(String sequence) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(rightSubString(sequence, 1));
		char chr = (char) (sequence.charAt(sequence.length() - 1) + 1);
		if (chr <= 'z') {
			stringBuilder.append(chr);

		} else {
			stringBuilder.append("za");
		}

		return stringBuilder.toString();
	}

	/**
	 * @param str
	 * @param to
	 * @return
	 */
	public static int compare(String str, String to) {
		return compare(str, to, str.length(), to.length());
	}

	/**
	 * @param str
	 * @param to
	 * @param m
	 * @param n
	 * @return
	 */
	public static int compare(String str, String to, int m, int n) {
		if (m == 0) {
			return n;
		}

		if (n == 0) {
			return m;
		}

		int matrix[][];
		matrix = new int[m + 1][n + 1];
		for (int i = 0; i <= m; i++) {
			matrix[i][0] = i;
		}

		for (int j = 0; j <= n; j++) {
			matrix[0][j] = j;
		}

		for (int i = 0; i < m; i++) {
			char chr = str.charAt(i);
			for (int j = 0; j < n; j++) {
				matrix[i + 1][j + 1] = KernelLang.min(matrix[i][j + 1] + 1, matrix[i + 1][j] + 1, matrix[i][j] + (chr == to.charAt(j) ? 0 : 1));
			}
		}

		return matrix[m][n];
	}

	/**
	 * @param str
	 * @param to
	 * @return
	 */
	public static float similar(String str, String to) {
		if (str == to) {
			return 1;
		}

		if (str == null) {
			return 0;
		}

		int m = str.length();
		int n = to.length();
		if (m == 0 && n == 0) {
			return 1;
		}

		return 1.0f - (float) compare(str, to, m, n) / Math.max(str.length(), to.length());
	}

	/**
	 * @author absir
	 * 
	 */
	public static interface ImplodeBuilder {

		/**
		 * @param index
		 * @param glue
		 * @param value
		 * @param target
		 * @return
		 */
		public Object glue(String glue, int index, Object value, Object target);
	}

	/**
	 * @param array
	 * @param glues
	 * @return
	 */
	public static String implode(Object[] array, String... glues) {
		return implode(array, null, null, glues);
	}

	/**
	 * @param array
	 * @param imploder
	 * @param target
	 * @param glues
	 * @return
	 */
	public static String implode(Object[] array, ImplodeBuilder imploder, Object target, String... glues) {
		StringBuilder builder = new StringBuilder();
		String glue = null;
		int index = 0;
		int length = glues.length;
		for (Object value : array) {
			if (imploder == null) {
				if (glue != null) {
					builder.append(glue);
				}

				builder.append(value);

			} else {
				implode(builder, imploder, glue, index, value, target);
			}

			if (++index >= length) {
				index = 0;
			}

			glue = glues[index];
		}

		return builder.toString();
	}

	/**
	 * @param collection
	 * @param glues
	 * @return
	 */
	public static String implode(Collection collection, String... glues) {
		return implode(collection, null, null, glues);
	}

	/**
	 * @param collection
	 * @param imploder
	 * @param target
	 * @param glues
	 * @return
	 */
	public static String implode(Collection collection, ImplodeBuilder imploder, Object target, String... glues) {
		StringBuilder builder = new StringBuilder();
		String glue = null;
		int index = 0;
		int length = glues.length;
		for (Object value : collection) {
			if (imploder == null) {
				if (glue != null) {
					builder.append(glue);
				}

				builder.append(value);

			} else {
				implode(builder, imploder, glue, index, value, target);
			}

			if (++index >= length) {
				index = 0;
			}

			glue = glues[index];
		}

		return builder.toString();
	}

	/**
	 * @param map
	 * @param glues
	 * @return
	 */
	public static String implode(Map map, String... glues) {
		return implode(map, null, null, glues);
	}

	/**
	 * @param map
	 * @param imploder
	 * @param target
	 * @param glues
	 * @return
	 */
	public static String implode(Map<?, ?> map, ImplodeBuilder imploder, Object target, String... glues) {
		StringBuilder builder = new StringBuilder();
		String glue = null;
		int index = 0;
		int length = glues.length;
		for (Entry entry : map.entrySet()) {
			if (imploder == null) {
				if (glue != null) {
					builder.append(glue);
				}

				builder.append(entry.getKey());

			} else {
				implode(builder, imploder, glue, index, entry.getKey(), target);
			}

			if (++index >= length) {
				index = 0;
			}

			glue = glues[index];
			if (imploder == null) {
				if (glue != null) {
					builder.append(glue);
				}

				builder.append(entry.getValue());

			} else {
				implode(builder, imploder, glue, index, entry.getValue(), target);
			}

			if (++index >= length) {
				index = 0;
			}

			glue = glues[index];
		}

		return builder.toString();
	}

	/**
	 * @param imploder
	 * @param builder
	 * @param glue
	 * @param index
	 * @param value
	 * @param target
	 */
	protected static void implode(StringBuilder builder, ImplodeBuilder imploder, Object glue, int index, Object value, Object target) {
		Object result = imploder.glue((String) glue, index, value, target);
		if (result instanceof Object[]) {
			Object[] res = (Object[]) result;
			if (res.length > 1) {
				glue = res[0];
				value = res[1];
			}

			result = null;
		}

		if (result == null) {
			if (glue != null) {
				builder.append(glue);
			}

			if (value != null) {
				builder.append(value);
			}

		} else {
			builder.append(result);
		}
	}
}
