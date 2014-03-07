package com.android.pc.ioc.db.table;

import java.lang.reflect.Field;
import java.util.List;

import com.android.pc.ioc.app.ApplicationBean;
import com.android.pc.ioc.db.sqlite.DbUtils;
import com.android.pc.ioc.db.sqlite.FinderLazyLoader;

/**
 * Author: wyouflf Date: 13-9-10 Time: 下午7:43
 */
public class Finder extends Column {

	public DbUtils db;

	private String valueColumnName;

	private String targetColumnName;

	protected Finder(Class entityType, Field field) {
		super(entityType, field);

		com.android.pc.ioc.db.annotation.Finder finder = field.getAnnotation(com.android.pc.ioc.db.annotation.Finder.class);
		this.valueColumnName = finder.valueColumn();
		this.targetColumnName = finder.targetColumn();
	}

	public Class<?> getTargetEntityType() {
		return ColumnUtils.getFinderTargetEntityType(this);
	}

	@Override
	public void setValue2Entity(Object entity, String valueStr) {
		Object value = null;
		Class columnType = columnField.getType();
		Object finderValue = TableUtils.getColumnOrId(entity.getClass(), this.valueColumnName).getColumnValue(entity);
		if (columnType.equals(FinderLazyLoader.class)) {
			value = new FinderLazyLoader(this, finderValue);
		} else if (columnType.equals(List.class)) {
			try {
				value = new FinderLazyLoader(this, finderValue).getAllFromDb();
			} catch (Exception e) {
				ApplicationBean.logger.e(e);
			}
		} else {
			try {
				value = new FinderLazyLoader(this, finderValue).getFirstFromDb();
			} catch (Exception e) {
				ApplicationBean.logger.e(e);
			}
		}

		if (setMethod != null) {
			try {
				setMethod.invoke(entity, value);
			} catch (Exception e) {
				ApplicationBean.logger.e(e);
			}
		} else {
			try {
				this.columnField.setAccessible(true);
				this.columnField.set(entity, value);
			} catch (Exception e) {
				ApplicationBean.logger.e(e);
			}
		}
	}

	public String getTargetColumnName() {
		return targetColumnName;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getColumnValue(Object entity) {
		return null;
	}

	public Object getFieldValue(Object entity) {
		Object valueObj = null;
		if (entity != null) {
			if (getMethod != null) {
				try {
					valueObj = getMethod.invoke(entity);
				} catch (Exception e) {
					ApplicationBean.logger.e(e);
				}
			} else {
				try {
					this.columnField.setAccessible(true);
					valueObj = this.columnField.get(entity);
				} catch (Exception e) {
					ApplicationBean.logger.e(e);
				}
			}
		}
		return valueObj;
	}

	@Override
	public Object getDefaultValue() {
		return null;
	}

	@Override
	public String getColumnDbType() {
		return "";
	}
}
