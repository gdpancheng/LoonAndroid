
package com.android.pc.ioc.db.table;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import android.text.TextUtils;

import com.android.pc.ioc.app.Ioc;
import com.android.pc.ioc.db.annotation.Id;
import com.android.pc.ioc.db.annotation.Table;

public class TableUtils {

	private TableUtils() {
	}

	public static String getTableName(Class<?> entityType) {
		Table table = entityType.getAnnotation(Table.class);
		if (table == null || TextUtils.isEmpty(table.name())) {
			return entityType.getName().replace('.', '_');
		}
		return table.name();
	}

	/**
	 * key: entityType.canonicalName
	 */
	private static ConcurrentHashMap<String, HashMap<String, Column>> entityColumnsMap = new ConcurrentHashMap<String, HashMap<String, Column>>();

	/**
	 * @param entityType
	 * @return key: columnName
	 */
	public static synchronized HashMap<String, Column> getColumnMap(Class<?> entityType) {

		if (entityColumnsMap.containsKey(entityType.getCanonicalName())) {
			return entityColumnsMap.get(entityType.getCanonicalName());
		}

		HashMap<String, Column> columnMap = new HashMap<String, Column>();
		String primaryKeyFieldName = getPrimaryKeyFieldName(entityType);
		addColumns2Map(entityType, primaryKeyFieldName, columnMap);
		entityColumnsMap.put(entityType.getCanonicalName(), columnMap);

		return columnMap;
	}

	private static void addColumns2Map(Class<?> entityType, String primaryKeyFieldName, HashMap<String, Column> columnMap) {
		if (Object.class.equals(entityType))
			return;
		try {
			Field[] fields = entityType.getDeclaredFields();
			for (Field field : fields) {
				if (ColumnUtils.isTransient(field) || Modifier.isStatic(field.getModifiers())) {
					continue;
				}
				if (ColumnUtils.isSimpleColumnType(field)) {
					if (!field.getName().equals(primaryKeyFieldName)) {
						Column column = new Column(entityType, field);
						if (!columnMap.containsKey(column.getColumnName())) {
							columnMap.put(column.getColumnName(), column);
						}
					}
				} else if (ColumnUtils.isForeign(field)) {
					Foreign column = new Foreign(entityType, field);
					if (!columnMap.containsKey(column.getColumnName())) {
						columnMap.put(column.getColumnName(), column);
					}
				} else if (ColumnUtils.isFinder(field)) {
					Finder column = new Finder(entityType, field);
					if (!columnMap.containsKey(column.getColumnName())) {
						columnMap.put(column.getColumnName(), column);
					}
				}
			}

			if (!Object.class.equals(entityType.getSuperclass())) {
				addColumns2Map(entityType.getSuperclass(), primaryKeyFieldName, columnMap);
			}
		} catch (Exception e) {
			Ioc.getIoc().getLogger().e(e);
		}
	}

	public static Column getColumnOrId(Class<?> entityType, String columnName) {
		if (getPrimaryKeyColumnName(entityType).equals(columnName)) {
			return com.android.pc.ioc.db.table.Table.get(entityType).getId();
		}
		return getColumnMap(entityType).get(columnName);
	}

	public static Column getColumnOrId(Class<?> entityType, Field columnField) {
		String columnName = ColumnUtils.getColumnNameByField(columnField);
		if (getPrimaryKeyColumnName(entityType).equals(columnName)) {
			return com.android.pc.ioc.db.table.Table.get(entityType).getId();
		}
		return getColumnMap(entityType).get(columnName);
	}

	/**
	 * key: entityType.canonicalName
	 */
	private static ConcurrentHashMap<String, com.android.pc.ioc.db.table.Id> entityIdMap = new ConcurrentHashMap<String, com.android.pc.ioc.db.table.Id>();

	public static synchronized com.android.pc.ioc.db.table.Id getId(Class<?> entityType) {
		if (Object.class.equals(entityType)) {
			throw new RuntimeException("field 'id' not found");
		}

		if (entityIdMap.containsKey(entityType.getCanonicalName())) {
			return entityIdMap.get(entityType.getCanonicalName());
		}

		Field primaryKeyField = null;
		Field[] fields = entityType.getDeclaredFields();
		if (fields != null) {

			for (Field field : fields) {
				if (field.getAnnotation(Id.class) != null) {
					primaryKeyField = field;
					break;
				}
			}

			if (primaryKeyField == null) {
				for (Field field : fields) {
					if ("id".equals(field.getName()) || "_id".equals(field.getName())) {
						primaryKeyField = field;
						break;
					}
				}
			}
		}

		if (primaryKeyField == null) {
			return getId(entityType.getSuperclass());
		}

		com.android.pc.ioc.db.table.Id id = new com.android.pc.ioc.db.table.Id(entityType, primaryKeyField);
		entityIdMap.put(entityType.getCanonicalName(), id);
		return id;
	}

	private static String getPrimaryKeyFieldName(Class<?> entityType) {
		com.android.pc.ioc.db.table.Id id = getId(entityType);
		return id == null ? null : id.getColumnField().getName();
	}

	private static String getPrimaryKeyColumnName(Class<?> entityType) {
		com.android.pc.ioc.db.table.Id id = getId(entityType);
		return id == null ? null : id.getColumnName();
	}

	public static Object getIdValue(Object entity) {
		if (entity == null)
			return null;

		try {
			com.android.pc.ioc.db.table.Id id = getId(entity.getClass());
			if (id == null)
				return null;
			Object idValue = id.getColumnValue(entity);
			if (idValue != null && !idValue.equals(0) && idValue.toString().length() > 0) {
				return idValue;
			} else {
				return null;
			}
		} catch (Exception e) {
			Ioc.getIoc().getLogger().e(e);
		}
		return null;
	}
}
