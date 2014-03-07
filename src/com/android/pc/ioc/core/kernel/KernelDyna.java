package com.android.pc.ioc.core.kernel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class KernelDyna {

	/** BYTE_ZERO */
	public static final Byte BYTE_ZERO = Byte.valueOf((byte) 0);

	/** SHORT_ZERO */
	public static final Short SHORT_ZERO = Short.valueOf((short) 0);

	/** INTEGER_ZERO */
	public static final Integer INTEGER_ZERO = Integer.valueOf((int) 0);

	/** LONG_ZERO */
	public static final Long LONG_ZERO = Long.valueOf((long) 0);

	/** FLOAT_ZERO */
	public static final Float FLOAT_ZERO = Float.valueOf((float) 0);

	/** DOUBLE_ZERO */
	public static final Double DOUBLE_ZERO = Double.valueOf((double) 0);

	/** BOOLEAN_ZERO */
	public static final Boolean BOOLEAN_ZERO = Boolean.valueOf(false);

	/** CHARACTER_ZERO */
	public static final Character CHARACTER_ZERO = Character.valueOf((char) 0);

	/** DATE_ZERO */
	public static final Date DATE_ZERO = new Date((long) 0);

	/** DATE_FORMAT */
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/** DATE_FORMAT_DAY */
	public static final DateFormat DATE_FORMAT_DAY = new SimpleDateFormat("yyyy-MM-dd");

	/** DATE_FORMAT_TIME */
	public static final DateFormat DATE_FORMAT_TIME = new SimpleDateFormat("HH:mm:ss");

	/** DATE_FORMAT_ARRAY */
	public static final DateFormat[] DATE_FORMAT_ARRAY = new DateFormat[] { DATE_FORMAT, DATE_FORMAT_DAY, DATE_FORMAT_TIME, DateFormat.getDateTimeInstance(),
			DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG), DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM),
			new SimpleDateFormat("EEE MMM d hh:mm:ss a z yyyy"), new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy"), new SimpleDateFormat("MM/dd/yy hh:mm:ss a"), new SimpleDateFormat("MM/dd/yy") };

	/**
	 * @param obj
	 * @param toClass
	 * @return
	 */
	public static <T> T to(Object obj, Class<T> toClass) {
		if (obj == null) {
			return nullTo(toClass);
		}

		if (toClass.isAssignableFrom(obj.getClass())) {
			return (T) obj;
		}

		if (obj instanceof Number) {
			return numberTo((Number) obj, toClass);

		} else if (obj instanceof Date) {
			return dateTo((Date) obj, toClass);

		} else if (obj instanceof String) {
			return stringTo((String) obj, toClass);
		}

		return nullTo(toClass);
	}

	/**
	 * @param toClass
	 * @return
	 */
	public static <T> T nullTo(Class<T> toClass) {
		if (toClass == byte.class) {
			return (T) BYTE_ZERO;

		} else if (toClass == short.class) {
			return (T) SHORT_ZERO;

		} else if (toClass == int.class) {
			return (T) INTEGER_ZERO;

		} else if (toClass == long.class) {
			return (T) LONG_ZERO;

		} else if (toClass == float.class) {
			return (T) FLOAT_ZERO;

		} else if (toClass == double.class) {
			return (T) DOUBLE_ZERO;

		} else if (toClass == boolean.class) {
			return (T) BOOLEAN_ZERO;

		} else if (toClass == char.class) {
			return (T) CHARACTER_ZERO;
		}

		return null;
	}

	/**
	 * @param num
	 * @param toClass
	 * @return
	 */
	public static <T> T numberTo(Number num, Class<T> toClass) {
		if (toClass == Byte.class || toClass == byte.class) {
			return (T) (Object) num.byteValue();

		} else if (toClass == Short.class || toClass == short.class) {
			return (T) (Object) num.shortValue();

		} else if (toClass == Integer.class || toClass == int.class) {
			return (T) (Object) num.intValue();

		} else if (toClass == Long.class || toClass == long.class) {
			return (T) (Object) num.longValue();

		} else if (toClass == Float.class || toClass == float.class) {
			return (T) (Object) num.floatValue();

		} else if (toClass == Double.class || toClass == double.class) {
			return (T) (Object) num.doubleValue();

		} else if (toClass == Boolean.class || toClass == boolean.class) {
			return (T) (Object) (num.byteValue() != 0);

		} else if (toClass == Character.class || toClass == char.class) {
			return (T) (Object) (char) num.byteValue();

		} else if (toClass == Date.class) {
			return (T) new Date(num.longValue());

		} else if (toClass.isAssignableFrom(String.class)) {
			return (T) num.toString();
		}

		return null;
	}

	/**
	 * @param date
	 * @param toClass
	 * @return
	 */
	public static <T> T dateTo(Date date, Class<T> toClass) {
		if (toClass == Byte.class || toClass == byte.class) {
			return (T) (Object) ((Long) date.getTime()).byteValue();

		} else if (toClass == Short.class || toClass == short.class) {
			return (T) (Object) ((Long) date.getTime()).shortValue();

		} else if (toClass == Integer.class || toClass == int.class) {
			return (T) (Object) ((Long) date.getTime()).intValue();

		} else if (toClass == Long.class || toClass == long.class) {
			return (T) (Object) date.getTime();

		} else if (toClass == Float.class || toClass == float.class) {
			return (T) (Object) ((Long) date.getTime()).floatValue();

		} else if (toClass == Double.class || toClass == double.class) {
			return (T) (Object) ((Long) date.getTime()).doubleValue();

		} else if (toClass == Boolean.class || toClass == boolean.class) {
			return (T) (Object) (((Long) date.getTime()).byteValue() != 0);

		} else if (toClass == Character.class || toClass == char.class) {
			return (T) (Object) (char) ((Long) date.getTime()).byteValue();

		} else if (toClass.isAssignableFrom(String.class)) {
			return (T) toString(date);
		}

		return null;
	}

	/**
	 * @param date
	 * @return
	 */
	public static String toString(Date date) {
		return toString(date, 0);
	}

	/**
	 * @param date
	 * @param type
	 * @return
	 */
	public static String toString(Date date, int type) {
		try {
			return DATE_FORMAT_ARRAY[type].format(date);

		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @param str
	 * @param toClass
	 * @return
	 */
	public static <T> T stringTo(String str, Class<T> toClass) {
		if (KernelString.empty(str)) {
			return nullTo(toClass);
		}

		return stringTo(str, toClass, null);
	}

	/**
	 * @param str
	 * @param toClass
	 * @param dynas
	 * @return
	 */
	public static <T> T stringTo(String str, Class<T> toClass, boolean[] dynas) {
		if (toClass == byte.class) {
			return (T) toByte(str);

		} else if (toClass == Byte.class) {
			return (T) toByte(str, null);

		} else if (toClass == short.class) {
			return (T) toShort(str);

		} else if (toClass == Short.class) {
			return (T) toShort(str, null);

		} else if (toClass == int.class) {
			return (T) toInteger(str);

		} else if (toClass == Integer.class) {
			return (T) toInteger(str, null);

		} else if (toClass == long.class) {
			return (T) toLong(str);

		} else if (toClass == Long.class) {
			return (T) toLong(str, null);

		} else if (toClass == float.class) {
			return (T) toFloat(str);

		} else if (toClass == Float.class) {
			return (T) toFloat(str, null);

		} else if (toClass == double.class) {
			return (T) toDouble(str);

		} else if (toClass == Double.class) {
			return (T) toDouble(str, null);

		} else if (toClass == boolean.class) {
			return (T) toBoolean(str);

		} else if (toClass == Boolean.class) {
			return (T) toBoolean(str, null);

		} else if (toClass == char.class) {
			return (T) toCharacter(str);

		} else if (toClass == Character.class) {
			return (T) toBoolean(str, null);

		} else if (toClass == Date.class) {
			return (T) toDate(str, DATE_ZERO);

		} else if (toClass == Enum.class) {
			return (T) Enum.valueOf((Class) toClass, str);
		}

		if (dynas != null && dynas.length > 0) {
			dynas[0] = !dynas[0];
		}

		return null;
	}

	/**
	 * @param str
	 * @return
	 */
	public static Byte toByte(String str) {
		return toByte(str, BYTE_ZERO);
	}

	/**
	 * @param str
	 * @param defaultValue
	 * @return
	 */
	public static Byte toByte(String str, Byte defaultValue) {
		try {
			return Byte.valueOf(str);

		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * @param str
	 * @return
	 */
	public static Short toShort(String str) {
		return toShort(str, SHORT_ZERO);
	}

	/**
	 * @param str
	 * @param defaultValue
	 * @return
	 */
	public static Short toShort(String str, Short defaultValue) {
		try {
			return Short.valueOf(str);

		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * @param str
	 * @return
	 */
	public static Integer toInteger(String str) {
		return toInteger(str, INTEGER_ZERO);
	}

	/**
	 * @param str
	 * @param defaultValue
	 * @return
	 */
	public static Integer toInteger(String str, Integer defaultValue) {
		try {
			return Integer.valueOf(str);

		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * @param str
	 * @return
	 */
	public static Long toLong(String str) {
		return toLong(str, LONG_ZERO);
	}

	/**
	 * @param str
	 * @param defaultValue
	 * @return
	 */
	public static Long toLong(String str, Long defaultValue) {
		try {
			return Long.valueOf(str);

		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * @param str
	 * @return
	 */
	public static Float toFloat(String str) {
		return toFloat(str, FLOAT_ZERO);
	}

	/**
	 * @param str
	 * @param defaultValue
	 * @return
	 */
	public static Float toFloat(String str, Float defaultValue) {
		try {
			return Float.valueOf(str);

		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * @param str
	 * @return
	 */
	public static Double toDouble(String str) {
		return toDouble(str, DOUBLE_ZERO);
	}

	/**
	 * @param str
	 * @param defaultValue
	 * @return
	 */
	public static Double toDouble(String str, Double defaultValue) {
		try {
			return Double.valueOf(str);

		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * @param str
	 * @return
	 */
	public static Boolean toBoolean(String str) {
		return toBoolean(str, BOOLEAN_ZERO);
	}

	/**
	 * @param str
	 * @param defaultValue
	 * @return
	 */
	public static Boolean toBoolean(String str, Boolean defaultValue) {
		try {
			return Boolean.valueOf(str);

		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * @param str
	 * @return
	 */
	public static Character toCharacter(String str) {
		return toCharacter(str, CHARACTER_ZERO);
	}

	/**
	 * @param str
	 * @param defaultValue
	 * @return
	 */
	public static Character toCharacter(String str, Character defaultValue) {
		try {
			return Character.valueOf(str.charAt(0));

		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * @param obj
	 * @return
	 */
	public static Date toDate(Object obj) {
		return to(obj, Date.class);
	}

	/**
	 * @param str
	 * @return
	 */
	public static Date toDate(String str) {
		return toDate(str, DATE_ZERO);
	}

	/**
	 * @param str
	 * @param defaultValue
	 * @return
	 */
	public static Date toDate(String str, Date defaultValue) {
		for (DateFormat dateFormat : DATE_FORMAT_ARRAY) {
			try {
				return dateFormat.parse(str);

			} catch (Exception e) {
			}
		}

		return defaultValue;
	}

}
