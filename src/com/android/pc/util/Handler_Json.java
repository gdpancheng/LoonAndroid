package com.android.pc.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.android.pc.ioc.app.Ioc;

/**
 * json帮助类 可以解析为集合或者对象
 * 
 * @author gdpancheng@gmail.com 2013-10-22 上午11:40:02
 */
public class Handler_Json {

	private static Map<Class<?>, ArrayList<fieldEntity>> method_map = new HashMap<Class<?>, ArrayList<fieldEntity>>();

	/**
	 * json字符串转化为集合
	 * 
	 * @author gdpancheng@gmail.com 2013-10-22 上午11:39:47
	 * @param str
	 * @return Object
	 */
	private static Object JsonToHashMap(String str) {
		LinkedHashMap<String, Object> json = new LinkedHashMap<String, Object>();
		try {
			Object object = new JSONTokener(str).nextValue();
			if (object instanceof JSONArray) {
				JSONArray root = new JSONArray(str);
				ArrayList<Object> list = new ArrayList<Object>();
				if (root.length() > 0) {
					for (int i = 0; i < root.length(); i++) {
						list.add(JsonToCollection(root.getString(i)));
					}
					return list;
				}
				return list.add(str);
			} else if (object instanceof JSONObject) {
				JSONObject root = new JSONObject(str);
				if (root.length() > 0) {
					@SuppressWarnings("unchecked")
					Iterator<String> rootName = root.keys();
					String name;
					while (rootName.hasNext()) {
						name = rootName.next();
						json.put(name, JsonToCollection(root.getString(name)));
					}
				}
				return json;
			} else {
				return str;
			}
		} catch (JSONException e) {
			Ioc.getIoc().getLogger().d("错误字符串：" + str);
			return str;
		}
	}

	/**
	 * json转为对象
	 * 
	 * @author gdpancheng@gmail.com 2014-2-28 下午9:24:37
	 * @param str
	 * @param entity
	 * @return Object
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Object JsonToBean(String str, Object entity) {
		try {
			Object object = new JSONTokener(str).nextValue();
			if (object instanceof JSONArray) {
				JSONArray root = new JSONArray(str);
				if (root.length() > 0) {
					ArrayList<Object> list = new ArrayList<Object>();
					for (int i = 0; i < root.length(); i++) {
						Object value = new JSONTokener(root.getString(i)).nextValue();
						if (classes.contains(value.getClass())) {
							list.add(root.getString(i));
						} else {
							list.add(JsonToBean(root.getString(i), entity.getClass().newInstance()));
						}
					}
					return list;
				}
				Ioc.getIoc().getLogger().e("数组" + entity + "解析出错");
				return null;
			} else if (object instanceof JSONObject) {
				JSONObject root = new JSONObject(str);
				if (root.length() > 0) {
					Iterator<String> rootName = root.keys();
					String name;
					while (rootName.hasNext()) {
						name = rootName.next();
						boolean isHas = false;
						Class template = entity.getClass();
						while (template != null && !classes.contains(template)) {
							ArrayList<fieldEntity> arrayList = method_map.get(template);
							for (fieldEntity fieldEntity : arrayList) {
								fieldEntity.field.setAccessible(true);
								Object obj = null;
								if (name.equals(fieldEntity.field.getName())) {
									isHas = true;
									if (fieldEntity.clazz == null) {
										Class clazz = fieldEntity.field.getType();
										if (clazz == String.class) {
											obj = root.getString(name);
										}
										if (clazz == int.class) {
											obj = root.getInt(name);
										}
										if (clazz == boolean.class) {
											obj = root.getBoolean(name);
										}
									} else {
										Object obj2 = new JSONTokener(root.getString(name)).nextValue();
										Class value_class = fieldEntity.field.getType();
										if (classes.contains(value_class)) {
											JSONArray array = (JSONArray) obj2;
											ArrayList<Object> list = new ArrayList<Object>();
											for (int i = 0; i < array.length(); i++) {
												if (fieldEntity.clazz == String.class) {
													obj = array.get(i).toString();
												}
												if (fieldEntity.clazz == int.class) {
													obj = Integer.valueOf(array.get(i).toString());
												}
												if (fieldEntity.clazz == boolean.class) {
													obj = Boolean.valueOf(array.get(i).toString());
												}
												list.add(obj);
											}
											obj = list;
										} else {
											try {
												obj = JsonToBean(root.getString(name), fieldEntity.clazz.newInstance());
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}
									try {
										fieldEntity.field.set(entity, obj);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
							template = template.getSuperclass();
						}

						if (!isHas) {
							Ioc.getIoc().getLogger().e("字段" + name + "在实体类" + entity + "不存在");
						}
					}
				} else {
					Ioc.getIoc().getLogger().e("数据长度不对 解析出错");
				}
				return entity;
			} else {
				return entity;
			}
		} catch (Exception e) {
			Ioc.getIoc().getLogger().d("错误字符串：" + str);
			return entity;
		}
	}

	@SuppressWarnings({ "rawtypes", "serial" })
	static HashSet<Class> classes = new HashSet<Class>() {
		{
			add(Object.class);
			add(Double.class);
			add(Float.class);
			add(Integer.class);
			add(Long.class);
			add(String.class);
			add(int.class);
			add(boolean.class);
		}
	};

	private static void getMethod(Class<?> clazz) {

		Class<?> template = clazz;
		while (template != null && template != Object.class) {
			if (method_map.get(template) != null && method_map.get(template).size() > 0) {
				return;
			}
			template = template.getSuperclass();
		}
		template = clazz;
		while (template != null && !classes.contains(template)) {
			// -----------------------------------解析变量------------------------------------------------
			ArrayList<fieldEntity> entities = new ArrayList<fieldEntity>();
			for (Field m : template.getDeclaredFields()) {
				Type type = m.getGenericType();
				int modifiers = m.getModifiers();
				if (Modifier.isStatic(modifiers)) {
					continue;
				}
				if (type instanceof ParameterizedType) {
					Type[] types = ((ParameterizedType) type).getActualTypeArguments();
					for (Type type2 : types) {
						if (!classes.contains(m.getType())) {
							getMethod((Class<?>) type2);
							entities.add(new fieldEntity(m, (Class<?>) type2));
						} else {
							entities.add(new fieldEntity(m, null));
						}
						break;
					}
					continue;
				}
				if (!classes.contains(m.getType())) {
					getMethod((Class<?>) type);
					entities.add(new fieldEntity(m, (Class<?>) type));
				} else {
					entities.add(new fieldEntity(m, null));
				}
			}
			method_map.put(template, entities);
			// -----------------------------------解析完毕------------------------------------------------
			template = template.getSuperclass();
		}
	}


	/**
	 * json字符串转换为bean
	 * 
	 * @author gdpancheng@gmail.com 2013-10-22 下午1:01:32
	 * @param clazz
	 * @param json
	 * @return T
	 */
	@SuppressWarnings("unchecked")
	public static <T> T JsonToBean(Class<?> clazz, String json) {
		getMethod(clazz);
		T object = null;
		try {
			object = (T) JsonToBean(json, clazz.newInstance());
		} catch (Exception e) {
		}
		return object;
	}

	@SuppressWarnings("unchecked")
    public static <T> T  JsonToCollection(String str) {
		T object = null;
		try {
			object = (T) JsonToHashMap(str);
        } catch (Exception e) {
        }
		return object;
	}
	
	/**
	 * 解析内部类
	 * 
	 * @author gdpancheng@gmail.com 2013-10-12 下午5:12:20
	 */
	public static class fieldEntity {
		public Field field;
		public Class<?> clazz;

		public fieldEntity(Field field, Class<?> clazz) {
			this.field = field;
			this.clazz = clazz;
		}

		@Override
		public String toString() {
			return "fieldEntity [field=" + field.getName() + ", clazz=" + clazz + "]";
		}

	}
}
