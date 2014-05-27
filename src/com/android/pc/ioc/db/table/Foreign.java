package com.android.pc.ioc.db.table;

import java.lang.reflect.Field;
import java.util.List;

import com.android.pc.ioc.app.Ioc;
import com.android.pc.ioc.db.sqlite.DbUtils;
import com.android.pc.ioc.db.sqlite.ForeignLazyLoader;

public class Foreign extends Column {

	public DbUtils db;

	private String foreignColumnName;

	protected Foreign(Class entityType, Field field) {
		super(entityType, field);
		foreignColumnName = ColumnUtils.getForeignColumnNameByField(field);
	}

	public String getForeignColumnName() {
		return foreignColumnName;
	}

	public Class<?> getForeignEntityType() {
		return ColumnUtils.getForeignEntityType(this);
	}

	public Class<?> getForeignColumnType() {
		return TableUtils.getColumnOrId(getForeignEntityType(), foreignColumnName).columnField.getType();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setValue2Entity(Object entity, String valueStr) {
		Object value = null;
		if (valueStr != null) {
			Class columnType = columnField.getType();
			Object columnValue = ColumnUtils.valueStr2SimpleTypeFieldValue(getForeignColumnType(), valueStr);
			if (columnType.equals(ForeignLazyLoader.class)) {
				value = new ForeignLazyLoader(this, columnValue);
			} else if (columnType.equals(List.class)) {
				try {
					value = new ForeignLazyLoader(this, columnValue).getAllFromDb();
				} catch (Exception e) {
					Ioc.getIoc().getLogger().e(e);
				}
			} else {
				try {
					value = new ForeignLazyLoader(this, columnValue).getFirstFromDb();
				} catch (Exception e) {
					Ioc.getIoc().getLogger().e(e);
				}
			}
		}

		if (setMethod != null) {
			try {
				setMethod.invoke(entity, value);
			} catch (Exception e) {
				Ioc.getIoc().getLogger().e(e);
			}
		} else {
			try {
				this.columnField.setAccessible(true);
				this.columnField.set(entity, value);
			} catch (Exception e) {
				Ioc.getIoc().getLogger().e(e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getColumnValue(Object entity) {
		Object valueObj = getFieldValue(entity);

		if (valueObj != null) {
			Class columnType = columnField.getType();
			if (columnType.equals(ForeignLazyLoader.class)) {
				valueObj = ((ForeignLazyLoader) valueObj).getColumnValue();
			} else if (columnType.equals(List.class)) {
				try {
					List foreignEntities = (List) valueObj;
					if (foreignEntities.size() > 0) {

						if (this.db != null) {
							this.db.saveOrUpdateAll(foreignEntities);
						}

						Class foreignEntityType = ColumnUtils.getForeignEntityType(this);
						Column column = TableUtils.getColumnOrId(foreignEntityType, foreignColumnName);
						valueObj = column.getColumnValue(foreignEntities.get(0));
					}
				} catch (Exception e) {
					valueObj = null;
					Ioc.getIoc().getLogger().e(e);
				}
			} else {
				try {
					if (this.db != null) {
						try {
							this.db.saveOrUpdate(valueObj);
						} catch (Exception e) {
							Ioc.getIoc().getLogger().e(e);
						}
					}
					Column column = TableUtils.getColumnOrId(columnType, foreignColumnName);
					valueObj = column.getColumnValue(valueObj);
				} catch (Exception e) {
					valueObj = null;
					Ioc.getIoc().getLogger().e(e);
				}
			}
		}

		return ColumnUtils.convert2DbColumnValueIfNeeded(valueObj);
	}

	public Object getFieldValue(Object entity) {
		Object valueObj = null;
		if (entity != null) {
			if (getMethod != null) {
				try {
					valueObj = getMethod.invoke(entity);
				} catch (Exception e) {
					Ioc.getIoc().getLogger().e(e);
				}
			} else {
				try {
					this.columnField.setAccessible(true);
					valueObj = this.columnField.get(entity);
				} catch (Exception e) {
					Ioc.getIoc().getLogger().e(e);
				}
			}
		}
		return valueObj;
	}

	@Override
	public String getColumnDbType() {
		return ColumnUtils.fieldType2DbType(getForeignColumnType());
	}

	/**
	 * It always return null.
	 * 
	 * @return null
	 */
	@Override
	public Object getDefaultValue() {
		return null;
	}
}
