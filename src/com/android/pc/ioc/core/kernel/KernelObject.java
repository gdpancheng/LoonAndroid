package com.android.pc.ioc.core.kernel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.android.pc.ioc.core.kernel.KernelLang.BreakException;
import com.android.pc.ioc.core.kernel.KernelLang.CallbackBreak;

@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class KernelObject {

	/**
	 * 自动获取值
	 * 
	 * @author gdpancheng@gmail.com 2013-9-20 下午5:38:23
	 * @param value
	 * @param defaultValue
	 * @return T
	 */
	public static <T> T getValue(T value, T defaultValue) {
		return value == null ? defaultValue : value;
	}

	/**
	 * 自动注入set方法（私有）
	 * 
	 * @author gdpancheng@gmail.com 2013-9-20 下午5:39:49
	 * @param obj
	 * @param name
	 * @param value
	 * @return void
	 */
	public static void set(Object obj, String name, Object value) {
		declaredSet(obj, name, 0, false, value);
	}

	/**
	 * 自动注入set方法(公有方法)
	 * 
	 * @author gdpancheng@gmail.com 2013-9-20 下午5:40:11
	 * @param obj
	 * @param name
	 * @param value
	 * @return void
	 */
	public static void declaredSet(Object obj, String name, Object value) {
		declaredSet(obj, name, 0, true, value);
	}

	/**
	 * 自动注入set方法(公有方法)
	 * 
	 * @author gdpancheng@gmail.com 2013-9-20 下午5:40:42
	 * @param obj
	 *            对象
	 * @param name
	 *            名称
	 * @param ancest
	 *            父类的层数
	 * @param declared
	 *            是否公有
	 * @param value
	 *            值
	 * @return boolean 成功与否
	 */
	public static boolean declaredSet(Object obj, String name, int ancest, boolean declared, Object value) {
		Field field;
		if (obj instanceof Class) {
			field = KernelReflect.declaredField((Class) obj, name, ancest, declared);
			field = KernelReflect.memberStatic(field);
		} else {
			field = KernelReflect.declaredField(obj.getClass(), name, ancest, declared);
		}
		return KernelReflect.set(obj, field, value);
	}

	/**
	 * 注入get方法(私有)
	 * 
	 * @author gdpancheng@gmail.com 2013-9-20 下午5:42:37
	 * @param obj
	 * @param name
	 * @return Object
	 */
	public static Object get(Object obj, String name) {
		return declaredGet(obj, name, 0, false);
	}

	/**
	 * 注入get方法(公有)
	 * 
	 * @author gdpancheng@gmail.com 2013-9-20 下午5:43:01
	 * @param obj
	 * @param name
	 * @return
	 * @return Object
	 */
	public static Object declaredGet(Object obj, String name) {
		return declaredGet(obj, name, 0, true);
	}

	/**
	 * 自动注入get（私有）
	 * 
	 * @author gdpancheng@gmail.com 2013-9-20 下午5:43:30
	 * @param obj
	 * @param name
	 * @param ancest
	 * @param declared
	 * @return Object
	 */
	public static Object declaredGet(Object obj, String name, int ancest, boolean declared) {
		Field field;
		if (obj instanceof Class) {
			field = KernelReflect.declaredField((Class) obj, name, ancest, declared);
			field = KernelReflect.memberStatic(field);
		} else {
			field = KernelReflect.declaredField(obj.getClass(), name, ancest, declared);
		}
		return KernelReflect.get(obj, field);
	}

	/**
	 * 自动注入方法
	 * 
	 * @author gdpancheng@gmail.com 2013-9-20 下午5:44:13
	 * @param obj
	 * @param name
	 * @param args
	 * @return Object
	 */
	public static Object send(Object obj, String name, Object... args) {
		return send(obj, name, 0, true, true, KernelClass.parameterTypes(args), args);
	}

	/**
	 * 自动注入方法
	 * 
	 * @author gdpancheng@gmail.com 2013-9-20 下午5:45:17
	 * @param obj
	 * @param name
	 * @param parameterTypes
	 * @param args
	 * @return Object
	 */
	public static Object send(Object obj, String name, Class[] parameterTypes, Object... args) {
		return send(obj, name, 0, false, false, parameterTypes, args);
	}

	/**
	 * 自动注入方法
	 * 
	 * @author gdpancheng@gmail.com 2013-9-20 下午5:45:46
	 * @param obj
	 * @param name
	 * @param ancest
	 * @param assignable
	 * @param cacheable
	 * @param parameterTypes
	 * @param args
	 * @return Object
	 */
	public static Object send(Object obj, String name, int ancest, boolean assignable, boolean cacheable, Class[] parameterTypes, Object... args) {
		return declaredSend(obj, name, ancest, false, assignable, cacheable, parameterTypes, args);
	}

	/**
	 * @param obj
	 * @param name
	 * @param args
	 * @return
	 */
	public static Object declaredSend(Object obj, String name, Object... args) {
		return declaredSend(obj, name, 0, true, true, true, KernelClass.parameterTypes(args), args);
	}

	/**
	 * @param obj
	 * @param name
	 * @param parameterTypes
	 * @param args
	 * @return
	 */
	public static Object declaredSend(Object obj, String name, Class[] parameterTypes, Object... args) {
		return declaredSend(obj, name, 0, true, false, false, parameterTypes, args);
	}

	/**
	 * @param obj
	 * @param name
	 * @param ancest
	 * @param declared
	 * @param assignable
	 * @param cacheable
	 * @param parameterTypes
	 * @param args
	 * @return
	 */
	public static Object declaredSend(Object obj, String name, int ancest, boolean declared, boolean assignable, boolean cacheable, Class[] parameterTypes, Object... args) {
		Method method;
		if (obj instanceof Class) {
			method = KernelReflect.assignableMethod((Class) obj, name, ancest, declared, assignable, cacheable, parameterTypes);
			method = KernelReflect.memberStatic(method);

		} else {
			method = KernelReflect.assignableMethod(obj.getClass(), name, ancest, declared, assignable, cacheable, parameterTypes);
		}

		return KernelReflect.invoke(obj, method, args);
	}

	/**
	 * 注入set方法
	 * @author gdpancheng@gmail.com 2013-9-20 下午6:01:01
	 * @param obj
	 * @param field
	 * @param value
	 * @return boolean
	 */
	public static boolean setter(Object obj, Field field, Object value) {
		return setter(obj, field.getName(), field.getType(), value);
	}

	/**
	 * 注入set方法
	 * @author gdpancheng@gmail.com 2013-9-20 下午6:00:54
	 * @param obj
	 * @param field
	 * @param value
	 * @return boolean
	 */
	public static boolean setter(Object obj, String field, Object value) {
		return setter(obj, field, value.getClass(), value);
	}

	/**
	 * 注入set方法
	 * @author gdpancheng@gmail.com 2013-9-20 下午6:00:44
	 * @param obj
	 * @param field
	 * @param fieldType
	 * @param value
	 * @return boolean
	 */
	public static boolean setter(Object obj, String field, Class fieldType, Object value) {
		Method method = KernelClass.setter(obj.getClass(), field, fieldType);
		if (method != null) {
			if (KernelReflect.invoke(obj, false, method, value) == null) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 注入set方法 公有获取不到则私有
	 * @author gdpancheng@gmail.com 2013-9-20 下午6:00:18
	 * @param obj
	 * @param field
	 * @param value
	 * @return boolean
	 */
	public static boolean publicSetter(Object obj, Field field, Object value) {
		if (setter(obj, field, value)) {
			return true;
		}

		if (Modifier.isPublic(field.getModifiers())) {
			if (KernelReflect.set(obj, field, value)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 注入set方法 公有获取不到则私有
	 * 
	 * @author gdpancheng@gmail.com 2013-9-20 下午5:59:52
	 * @param obj
	 * @param field
	 * @param value
	 * @return boolean
	 */
	public static boolean publicSetter(Object obj, String field, Object value) {
		Method method = KernelClass.setter(obj.getClass(), field, value.getClass());
		if (method == null) {
			return setter(obj, field, value);
		} else {
			KernelReflect.invoke(obj, method, value);
			return true;
		}
	}

	/**
	 * 注入set方法 TODO(这里用一句话描述这个方法的作用)
	 * 
	 * @author gdpancheng@gmail.com 2013-9-20 下午5:59:36
	 * @param obj
	 * @param field
	 * @param value
	 * @return boolean
	 */
	public static boolean declaredSetter(Object obj, Field field, Object value) {
		if (setter(obj, field, value)) {
			return true;
		}

		if (KernelReflect.set(obj, field, value)) {
			return true;
		}

		return false;
	}

	/**
	 * 注入get方法
	 * 
	 * @author gdpancheng@gmail.com 2013-9-20 下午5:59:26
	 * @param obj
	 * @param field
	 * @return
	 * @return Object
	 */
	public static Object getter(Object obj, Field field) {
		return getter(obj, field.getName(), field.getType());
	}

	/**
	 * 注入get方法
	 * 
	 * @author gdpancheng@gmail.com 2013-9-20 下午5:59:03
	 * @param obj
	 * @param field
	 * @return Object
	 */
	public static Object getter(Object obj, String field) {
		return getter(obj, field, Object.class);
	}

	/**
	 * 注入get方法
	 * 
	 * @author gdpancheng@gmail.com 2013-9-20 下午5:58:22
	 * @param obj
	 * @param field
	 * @param fieldType
	 * @return Object
	 */
	public static Object getter(Object obj, String field, Class fieldType) {
		Method method = KernelClass.getter(obj.getClass(), field, fieldType);
		if (method != null) {
			return KernelReflect.invoke(obj, method);
		}
		return null;
	}

	/**
	 * 注入get方法 先获取公有的 如果不存在 则获取私有的
	 * 
	 * @author gdpancheng@gmail.com 2013-9-20 下午5:57:58
	 * @param obj
	 * @param field
	 * @return Object
	 */
	public static Object publicGetter(Object obj, Field field) {
		Method method = KernelClass.getter(obj.getClass(), field);
		if (method == null) {
			if (Modifier.isPublic(field.getModifiers())) {
				return KernelReflect.get(obj, field);
			}
		} else {
			return KernelReflect.invoke(obj, method);
		}
		return null;
	}

	/**
	 * 注入get方法 先获取公有的 如果不存在 则获取私有的
	 * 
	 * @author gdpancheng@gmail.com 2013-9-20 下午5:56:56
	 * @param obj
	 * @param field
	 * @return Object
	 */
	public static Object publicGetter(Object obj, String field) {
		Method method = KernelClass.getter(obj.getClass(), field);
		if (method == null) {
			return get(obj, field);
		} else {
			return KernelReflect.invoke(obj, method);
		}
	}

	/**
	 * 公有的get方法
	 * 
	 * @author gdpancheng@gmail.com 2013-9-20 下午5:55:37
	 * @param obj
	 * @param field
	 * @return Object
	 */
	public static Object declaredGetter(Object obj, Field field) {
		Method method = KernelClass.getter(obj.getClass(), field);
		if (method == null) {
			return KernelReflect.get(obj, field);

		} else {
			return KernelReflect.invoke(obj, method);
		}
	}

	/**
	 * 公有的get方法
	 * 
	 * @author gdpancheng@gmail.com 2013-9-20 下午5:55:20
	 * @param obj
	 * @param field
	 * @return Object
	 */
	public static Object declaredGetter(Object obj, String field) {
		Method method = KernelClass.getter(obj.getClass(), field);
		if (method == null) {
			return declaredGet(obj, field);
		} else {
			return KernelReflect.invoke(obj, method);
		}
	}

	/**
	 * @param obj
	 * @param target
	 * @return
	 */
	public static Object expressGetter(Object obj, String target) {
		if (KernelString.isEmpty(target)) {
			return obj;
		}

		String[] fields = target.split("\\.");
		for (String field : fields) {
			if (obj == null) {
				return null;
			}

			if (field.startsWith(":")) {
				Method method = KernelReflect.method(obj.getClass(), field.substring(1));
				if (method != null) {
					obj = KernelReflect.invoke(obj, method);
				}
			} else {
				obj = declaredGetter(obj, field);
			}
		}

		return obj;
	}

	/**
	 * 对象的转换
	 * 
	 * @author gdpancheng@gmail.com 2013-9-20 下午5:54:04
	 * @param obj
	 * @param toClass
	 * @return T
	 */
	public static <T> T cast(Object obj, Class<T> toClass) {
		if (obj != null && toClass.isAssignableFrom(obj.getClass())) {
			return (T) obj;
		}
		return null;
	}

	/**
	 * 对象的克隆
	 * 
	 * @author gdpancheng@gmail.com 2013-9-20 下午5:53:27
	 * @param obj
	 * @return T
	 */
	public static <T> T clone(T obj) {
		try {
			if (obj.getClass().isArray()) {
				return KernelArray.clone(obj);

			} else {
				T clone = (T) obj.getClass().newInstance();
				clone(obj, clone);
				return clone;
			}

		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}

		return null;
	}

	/**
	 * 对象的克隆
	 * 
	 * @author gdpancheng@gmail.com 2013-9-20 下午5:52:55
	 * @param obj
	 * @param clone
	 * @return void
	 */
	public static <T> void clone(final T obj, final T clone) {
		if (obj.getClass().isArray()) {
			KernelArray.copy(obj, clone);

		} else if (obj instanceof Collection) {
			KernelCollection.copy((Collection) obj, (Collection) clone);

		} else if (obj instanceof Map) {
			KernelMap.copy((Map<Object, Object>) obj, (Map<Object, Object>) clone);

		} else {
			KernelReflect.doWithDeclaredFields(obj.getClass(), new CallbackBreak<Field>() {

				@Override
				public void doWith(Field template) throws BreakException {
					// TODO Auto-generated method stub
					template.setAccessible(true);
					try {
						template.set(clone, template.get(obj));

					} catch (IllegalArgumentException e) {
					} catch (IllegalAccessException e) {
					}
				}
			});
		}
	}

	/**
	 * 对象的拷贝
	 * 
	 * @author gdpancheng@gmail.com 2013-9-20 下午5:52:18
	 * @param obj
	 * @param copy
	 * @return void
	 */
	public static void copy(final Object obj, final Object copy) {
		final Class cls = copy.getClass();
		if (obj.getClass().isArray()) {
			KernelArray.copy(obj, copy);

		} else if (obj instanceof Collection) {
			if (copy instanceof Collection) {
				KernelCollection.copy((Collection) obj, (Collection) copy);
			}

		} else if (obj instanceof Map) {
			if (copy instanceof Map) {
				KernelMap.copy((Map<Object, Object>) obj, (Map<Object, Object>) copy);
			}

		} else {
			KernelReflect.doWithDeclaredFields(obj.getClass(), new CallbackBreak<Field>() {

				@Override
				public void doWith(Field template) throws BreakException {
					// TODO Auto-generated method stub
					Field field = KernelReflect.declaredField(cls, template.getName());
					if (field != null && field.getType().isAssignableFrom(template.getType())) {
						template.setAccessible(true);
						try {
							field.set(copy, template.get(obj));

						} catch (IllegalArgumentException e) {
						} catch (IllegalAccessException e) {
						}
					}
				}
			});
		}
	}

	/**
	 * 序列化对象
	 * 
	 * @author gdpancheng@gmail.com 2013-9-20 下午5:51:58
	 * @param obj
	 * @return byte[]
	 */
	public static byte[] serialize(Object obj) {
		try {
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			ObjectOutputStream objectOut = new ObjectOutputStream(byteOut);
			objectOut.writeObject(obj);
			return byteOut.toByteArray();

		} catch (IOException e) {
		}

		return null;
	}

	/**
	 * 反序列化
	 * 
	 * @author gdpancheng@gmail.com 2013-9-20 下午5:51:46
	 * @param buf
	 * @return
	 * @return Object
	 */
	public static Object unserialize(byte[] buf) {
		try {
			ByteArrayInputStream byteInput = new ByteArrayInputStream(buf);
			ObjectInputStream objectInput = new ObjectInputStream(byteInput);
			Object obj = objectInput.readObject();
			return obj;
		} catch (IOException e) {

		} catch (ClassNotFoundException e) {
		}

		return null;
	}

	/**
	 * 序列化克隆
	 * 
	 * @author gdpancheng@gmail.com 2013-9-20 下午5:51:26
	 * @param obj
	 * @return T
	 */
	public static <T> T serializeClone(T obj) {
		byte[] buf = serialize(obj);
		if (buf == null) {
			return null;
		}
		return (T) unserialize(buf);
	}

	/**
	 * 获取对象的hashcode
	 * 
	 * @author gdpancheng@gmail.com 2013-9-20 下午5:49:46
	 * @param obj
	 * @return int
	 */
	public static int hashCode(Object obj) {
		return obj == null ? 1 : obj.hashCode();
	}

	/**
	 * 对比两个对象
	 * 
	 * @author gdpancheng@gmail.com 2013-9-20 下午5:49:32
	 * @param obj
	 * @param equal
	 * @return boolean
	 */
	public static boolean equals(Object obj, Object equal) {
		return obj == equal || (obj != null && obj.equals(equal));
	}

	/**
	 * 获取所有的公有属性和属性值
	 * 
	 * @author gdpancheng@gmail.com 2013-9-20 下午5:47:55
	 * @param obj
	 * @return Map<String,Object>
	 */
	public static Map<String, Object> getMap(final Object obj) {
		final Map<String, Object> map = new HashMap<String, Object>();
		KernelReflect.doWithDeclaredFields(obj.getClass(), new CallbackBreak<Field>() {

			@Override
			public void doWith(Field template) throws BreakException {
				Object value = publicGetter(obj, template);
				if (value != null) {
					map.put(template.getName(), value);
				}
			}
		});

		return map;
	}

	/**
	 * 批量注入
	 * 
	 * @author gdpancheng@gmail.com 2013-9-20 下午5:46:59
	 * @param obj
	 * @param map
	 * @return void
	 */
	public static void setMap(Object obj, Map<String, Object> map) {
		for (Entry<String, Object> entry : map.entrySet()) {
			publicSetter(obj, entry.getKey(), entry.getValue());
		}
	}
}
