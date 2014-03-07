package com.android.pc.ioc.core.kernel;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.android.pc.ioc.core.kernel.KernelLang.BreakException;
import com.android.pc.ioc.core.kernel.KernelLang.CallbackBreak;

@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class KernelClass {

	/**
	 * @param classname
	 * @return
	 */
	public static String parentName(Class cls) {
		return KernelString.rightSubString(cls.getName(), cls.getSimpleName().length() + 1);
	}

	/**
	 * @param cls
	 * @param types
	 * @return
	 */
	public static boolean isAssignableFrom(Class cls, Class[] types) {
		for (Class type : types) {
			if (cls.isAssignableFrom(type)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @param classes
	 * @param type
	 * @return
	 */
	public static boolean isAssignableFrom(Class[] classes, Class type) {
		for (Class cls : classes) {
			if (cls.isAssignableFrom(type)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @param classes
	 * @param types
	 * @return
	 */
	public static boolean isAssignableFrom(Class[] classes, Class[] types) {
		int length = classes.length;
		if (length == types.length) {
			for (int i = 0; i < length; i++) {
				if (!classes[i].isAssignableFrom(types[i])) {
					return false;
				}
			}

			return true;
		}

		return false;
	}

	/**
	 * @param cls
	 * @param T
	 * @return
	 */
	public static <T> Class<? extends T> classAssignable(Class cls, Class T) {
		return classAssignable(cls, null, T);
	}

	/**
	 * @param cls
	 * @param defaultValue
	 * @param T
	 * @return
	 */
	public static <T> Class<? extends T> classAssignable(Class cls, Class<? extends T> defaultValue, Class T) {
		if (cls == null || !T.isAssignableFrom(cls)) {
			return defaultValue;
		}

		return cls;
	}

	/**
	 * @param cls
	 * @param type
	 * @return
	 */
	public static boolean isMatchableFrom(Class cls, Class type) {
		if (cls.isAssignableFrom(type)) {
			return true;
		}

		if (cls == Object.class) {
			return true;

		} else if (cls == byte.class) {
			return type == Byte.class;

		} else if (cls == Byte.class) {
			return type == byte.class;

		} else if (cls == short.class) {
			return type == Short.class;

		} else if (cls == Short.class) {
			return type == short.class;

		} else if (cls == int.class) {
			return type == Integer.class;

		} else if (cls == Integer.class) {
			return type == int.class;

		} else if (cls == float.class) {
			return type == Float.class;

		} else if (cls == Float.class) {
			return type == float.class;

		} else if (cls == double.class) {
			return type == Double.class;

		} else if (cls == Double.class) {
			return type == double.class;

		} else if (cls == boolean.class) {
			return type == Boolean.class;

		} else if (cls == Boolean.class) {
			return type == boolean.class;

		} else if (cls == char.class) {
			return type == Character.class;

		} else if (cls == Character.class) {
			return type == char.class;

		} else if (cls == long.class) {
			return type == Long.class;

		} else if (cls == Long.class) {
			return type == long.class;

		} else if (cls.isArray() && type.isArray()) {
			cls.getComponentType().isAssignableFrom(type.getComponentType());
		}

		return false;
	}

	/**
	 * @param cls
	 * @param types
	 * @return
	 */
	public static boolean isMatchableFrom(Class cls, Class[] types) {
		for (Class type : types) {
			if (isMatchableFrom(cls, type)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @param classes
	 * @param types
	 * @return
	 */
	public static boolean isMatchableFrom(Class[] classes, Class[] types) {
		int length = classes.length;
		if (length == types.length) {
			for (int i = 0; i < length; i++) {
				if (!isMatchableFrom(classes[i], types[i])) {
					return false;
				}
			}

			return true;
		}

		return false;
	}

	/**
	 * @param args
	 * @return
	 */
	public static Class[] parameterTypes(Object... args) {
		int length = args.length;
		Class[] parameterTypes = new Class[length];
		for (int i = 0; i < length; i++) {
			parameterTypes[i] = args[i].getClass();
		}

		return parameterTypes;
	}

	/**
	 * @param type
	 * @return
	 */
	public static Class rawClass(Type type) {
		if (type instanceof ParameterizedType) {
			return (Class) ((ParameterizedType) type).getRawType();

		} else if (type instanceof GenericArrayType) {
			try {
				return Array.newInstance(rawClass(((GenericArrayType) type).getGenericComponentType()), 0).getClass();

			} catch (Exception e) {
			}

		} else if (type instanceof Class) {
			return (Class) type;
		}

		return Object.class;
	}

	/**
	 * @param type
	 * @return
	 */
	public static Type[] typeArguments(Type type) {
		if (type instanceof ParameterizedType) {
			return ((ParameterizedType) type).getActualTypeArguments();
		}

		return null;
	}

	/**
	 * @param types
	 * @return
	 */
	public static Class[] rawClasses(Type[] types) {
		int length = types.length;
		Class[] classes = new Class[length];
		for (int i = 0; i < length; i++) {
			classes[i] = rawClass(types[i]);
		}

		return classes;
	}

	/**
	 * @param cls
	 * @return
	 */
	public static Class componentClass(Class<?> cls) {
		if (cls.isArray()) {
			return cls.getComponentType();
		}

		Class superClass = componentClass(cls.getGenericSuperclass());
		return superClass == cls.getSuperclass() ? cls : superClass;
	}

	/**
	 * @param cls
	 * @return
	 */
	public static Class[] componentClasses(Class<?> cls) {
		if (cls.isArray()) {
			return new Class[] { cls.getComponentType() };
		}

		Class[] superClasses = componentClasses(cls.getGenericSuperclass());
		if (superClasses.length == 1 && superClasses[0] == cls.getSuperclass()) {
			superClasses[0] = cls;
		}

		return superClasses;
	}

	/**
	 * @param type
	 * @return
	 */
	public static Class componentClass(Type type) {
		Type componentType = type;
		while (type != null) {
			Type[] types = typeArguments(type);
			if (types == null || types.length <= 0) {
				Class cls = rawClass(type);
				if (cls.isArray()) {
					return cls.getComponentType();

				} else {
					type = cls.getGenericSuperclass();
					continue;
				}
			}

			return rawClass(types[0]);
		}

		return rawClass(componentType);
	}

	/**
	 * @param type
	 * @return
	 */
	public static Class[] componentClasses(Type type) {
		Type componentType = type;
		while (type != null) {
			Type[] types = typeArguments(type);
			if (types == null || types.length <= 0) {
				Class cls = rawClass(type);
				if (cls.isArray()) {
					return new Class[] { cls.getComponentType() };

				} else {
					type = cls.getGenericSuperclass();
					continue;
				}
			}

			return rawClasses(types);
		}

		return new Class[] { rawClass(componentType) };
	}

	/**
	 * @param cls
	 * @param type
	 * @return
	 */
	public static int similar(Class cls, Class type) {
		int similar = -1;
		while (cls != null) {
			if (similar < 0) {
				if (cls == type) {
					similar = 0;

				} else {
					for (Class iCls : cls.getInterfaces()) {
						if (iCls == type) {
							similar = 0;
							break;
						}
					}
				}

			} else {
				similar++;
			}

			cls = cls.getSuperclass();
		}

		return similar;
	}

	/**
	 * @param classes
	 * @param types
	 * @return
	 */
	public static int similar(Class[] classes, Class[] types) {
		int similar = -1;
		int length = classes.length;
		if (length == types.length) {
			for (int i = 0; i < length; i++) {
				similar += similar(classes[i], types[i]);
			}
		}

		return similar;
	}

	/**
	 * @param className
	 * @return
	 */
	public static Class forName(String className) {
		return forName(className, null);
	}

	/**
	 * @param className
	 * @param defaultValue
	 * @return
	 */
	public static Class forName(String className, Class defaultValue) {
		try {
			return Class.forName(className);

		} catch (ClassNotFoundException e) {
		}

		return defaultValue;
	}

	/**
	 * @param cls
	 * @param annotationType
	 * @return
	 */
	public static <T extends Annotation> T getAnnotation(Class<?> cls, Class<T> annotationType) {
		T annotation = cls.getAnnotation(annotationType);
		if (annotation == null && !Annotation.class.isAssignableFrom(cls)) {
			Annotation[] annotations = cls.getAnnotations();
			if (annotations != null) {
				for (Annotation obj : annotations) {
					annotation = getAnnotation(obj.annotationType(), annotationType);
					if (annotation != null) {
						break;
					}
				}
			}
		}

		return annotation;
	}

	/**
	 * @param cls
	 * @return
	 */
	public static <T> T newInstance(Class<T> cls) {
		return newInstance(null, cls);
	}

	/**
	 * @param defaultValue
	 * @param cls
	 * @return
	 */
	public static <T> T newInstance(T defaultValue, Class<T> cls) {
		try {
			return cls.newInstance();

		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}

		return defaultValue;
	}

	/**
	 * @param cls
	 * @param initargs
	 * @return
	 */
	public static <T> T newInstance(Class<T> cls, Object... initargs) {
		return newInstance(cls, null, initargs);
	}

	/**
	 * @param cls
	 * @param defaultValue
	 * @param initargs
	 * @return
	 */
	public static <T> T newInstance(Class<T> cls, T defaultValue, Object... initargs) {
		if (initargs.length == 0) {
			return newInstance(defaultValue, cls);
		}

		return newInstance(cls, defaultValue, KernelClass.parameterTypes(initargs), true, initargs);
	}

	/**
	 * @param cls
	 * @param defaultValue
	 * @param parameterTypes
	 * @param initargs
	 * @return
	 */
	public static <T> T newInstance(Class<T> cls, T defaultValue, Class[] parameterTypes, Object... initargs) {
		return newInstance(cls, defaultValue, parameterTypes, false, initargs);
	}

	/**
	 * @param cls
	 * @param defaultValue
	 * @param parameterTypes
	 * @param assignable
	 * @param initargs
	 * @return
	 */
	public static <T> T newInstance(Class<T> cls, T defaultValue, Class[] parameterTypes, boolean assignable, Object... initargs) {
		return declaredNew(cls, defaultValue, false, assignable, parameterTypes, initargs);
	}

	/**
	 * @param cls
	 * @param initargs
	 * @return
	 */
	public static <T> T declaredNew(Class<T> cls, Object... initargs) {
		return declaredNew(cls, null, KernelClass.parameterTypes(initargs), true, initargs);
	}

	/**
	 * @param cls
	 * @param defaultValue
	 * @param initargs
	 * @return
	 */
	public static <T> T declaredNew(Class<T> cls, T defaultValue, Object... initargs) {
		return declaredNew(cls, defaultValue, KernelClass.parameterTypes(initargs), true, initargs);
	}

	/**
	 * @param cls
	 * @param parameterTypes
	 * @param initargs
	 * @return
	 */
	public static <T> T declaredNew(Class<T> cls, Class[] parameterTypes, Object... initargs) {
		return declaredNew(cls, null, parameterTypes, initargs);
	}

	/**
	 * @param cls
	 * @param defaultValue
	 * @param parameterTypes
	 * @param initargs
	 * @return
	 */
	public static <T> T declaredNew(Class<T> cls, T defaultValue, Class[] parameterTypes, Object... initargs) {
		return declaredNew(cls, defaultValue, parameterTypes, false, initargs);
	}

	/**
	 * @param cls
	 * @param defaultValue
	 * @param parameterTypes
	 * @param assignable
	 * @param initargs
	 * @return
	 */
	public static <T> T declaredNew(Class<T> cls, T defaultValue, Class[] parameterTypes, boolean assignable, Object... initargs) {
		return declaredNew(cls, defaultValue, true, assignable, parameterTypes, initargs);
	}

	/**
	 * @param cls
	 * @param defaultValue
	 * @param declared
	 * @param assignable
	 * @param parameterTypes
	 * @param initargs
	 * @return
	 */
	public static <T> T declaredNew(Class<T> cls, T defaultValue, boolean declared, boolean assignable, Class[] parameterTypes, Object... initargs) {
		Constructor<T> constructor = KernelReflect.assignableConstructor(cls, declared, assignable, parameterTypes);
		return KernelReflect.newInstance(constructor, initargs);
	}

	/** Class_Map_Instance */
	static final Map<Class, Object> Class_Map_Instance = new HashMap<Class, Object>();

	/**
	 * @param cls
	 * @return
	 */
	public static <T> T getInstance(Class<T> cls) {
		T instance = (T) Class_Map_Instance.get(cls);
		if (instance == null) {
			synchronized (cls) {
				instance = (T) Class_Map_Instance.get(cls);
				if (instance == null) {
					instance = newInstance(cls);
					Class_Map_Instance.put(cls, instance);
				}
			}
		}

		return instance;
	}

	/**
	 * @param cls
	 * @param initargs
	 * @return
	 */
	public static <T> T getInstance(Class<T> cls, Object... initargs) {
		T instance = (T) Class_Map_Instance.get(cls);
		if (instance == null) {
			synchronized (cls) {
				instance = (T) Class_Map_Instance.get(cls);
				if (instance == null) {
					instance = newInstance(cls, initargs);
					Class_Map_Instance.put(cls, instance);
				}
			}
		}

		return instance;
	}

	/**
	 * @param cls
	 * @param name
	 * @return
	 */
	public static Object declaredGet(Class cls, String name) {
		Field field = KernelReflect.declaredField(cls.getClass(), name);
		return KernelReflect.get(cls, field);
	}

	/**
	 * @param cls
	 * @param name
	 * @param value
	 * @return
	 */
	public static boolean declaredSet(Class cls, String name, Object value) {
		Field field = KernelReflect.declaredField(cls.getClass(), name);
		return KernelReflect.set(cls, field, value);
	}

	/**
	 * @param cls
	 * @param name
	 * @param args
	 * @return
	 */
	public static Object declaredSend(Class cls, String name, Object... args) {
		Method method = KernelReflect.assignableMethod(cls, name, 0, true, parameterTypes(args));
		if (method != null) {
			return KernelReflect.invoke(cls, method, args);
		}

		return null;
	}

	/**
	 * @param field
	 * @return
	 */
	public static Method setter(Field field) {
		return setter(field.getDeclaringClass(), field);
	}

	/**
	 * @param cls
	 * @param field
	 * @return
	 */
	public static Method setter(Class cls, Field field) {
		return setter(cls, field.getName(), field.getType());
	}

	/**
	 * @param cls
	 * @param field
	 * @param fieldType
	 * @return
	 */
	public static Method setter(Class cls, String field, Class fieldType) {
		return KernelReflect.method(cls, "set" + KernelString.uncapitalize(field), fieldType);
	}

	/**
	 * @param field
	 * @return
	 */
	public static Method getter(Field field) {
		return getter(field.getDeclaringClass(), field);
	}

	/**
	 * @param cls
	 * @param field
	 * @return
	 */
	public static Method getter(Class cls, Field field) {
		return getter(cls, field.getName(), field.getType());
	}

	/**
	 * @param cls
	 * @param field
	 * @return
	 */
	public static Method getter(Class cls, String field) {
		field = KernelString.uncapitalize(field);
		Method method = KernelReflect.method(cls, "get" + field);
		if (method == null) {
			method = KernelReflect.method(cls, "is" + field);
		}

		return method;
	}

	/**
	 * @param cls
	 * @param field
	 * @param fieldType
	 * @return
	 */
	public static Method getter(Class cls, String field, Class fieldType) {
		field = KernelString.uncapitalize(field);
		Method method = KernelReflect.method(cls, "get" + field);
		if (method == null || !fieldType.isAssignableFrom(method.getReturnType())) {
			if (fieldType == boolean.class || fieldType == Boolean.class) {
				method = KernelReflect.method(cls, "is" + field);
			}

			if (method != null && !fieldType.isAssignableFrom(method.getReturnType())) {
				method = null;
			}
		}

		return method;
	}

	/**
	 * @param cls
	 * @param obj
	 * @return
	 */
	public static <T> T instanceOf(Class<T> cls, Object obj) {
		return instanceOf(cls, null, obj);
	}

	/**
	 * @param cls
	 * @param defaultValue
	 * @param obj
	 * @return
	 */
	public static <T> T instanceOf(Class<T> cls, T defaultValue, Object obj) {
		T value = newInstance(cls, obj);
		if (value == null) {
			value = valueOf(cls, obj);
		}

		if (value == null) {
			return defaultValue;

		} else {
			return value;
		}
	}

	/**
	 * @param cls
	 * @param obj
	 * @return
	 */
	public static <T> T valueOf(Class<T> cls, Object obj) {
		return valueOf(cls, null, obj);
	}

	/**
	 * @param cls
	 * @param defaultValue
	 * @param obj
	 * @return
	 */
	public static <T> T valueOf(Class<T> cls, T defaultValue, Object obj) {
		Method method = KernelReflect.assignableMethod(cls, "valueOf", obj.getClass());
		if (method != null && cls.isAssignableFrom(method.getReturnType())) {
			return (T) KernelReflect.invoke(cls, method, obj);
		}

		return defaultValue;
	}

	/**
	 * @param cls
	 * @param toClass
	 * @return
	 */
	public static <T> Class<? extends T> cast(Class cls, Class<T> toClass) {
		return cast(cls, null, toClass);
	}

	/**
	 * @param cls
	 * @param defaultValue
	 * @param toClass
	 * @return
	 */
	public static <T> Class<? extends T> cast(Class cls, Class<? extends T> defaultValue, Class<T> toClass) {
		if (cls != null && toClass.isAssignableFrom(cls)) {
			return cls;
		}

		return defaultValue;
	}

	/**
	 * @param cls
	 * @param callback
	 */
	public static void doWithSuperClass(Class cls, CallbackBreak<Class<?>> callback) {
		try {
			while (!(cls == null || cls == Object.class)) {
				callback.doWith(cls);
				cls = cls.getSuperclass();
			}

		} catch (BreakException e) {
			// TODO: handle exception
		}
	}

	/**
	 * @param cls
	 * @param callback
	 */
	public static void doWithAncestClass(Class cls, CallbackBreak<Class<?>> callback) {
		try {
			while (!(cls == null || cls == Object.class)) {
				callback.doWith(cls);
				for (Class c : cls.getInterfaces()) {
					callback.doWith(c);
				}

				cls = cls.getSuperclass();
			}

		} catch (BreakException e) {
			// TODO: handle exception
		}
	}
}
