package com.android.pc.ioc.db.table;

import com.android.pc.ioc.db.annotation.NoAutoIncrement;

import java.lang.reflect.Field;

public class Id extends Column {

	protected Id(Class entityType, Field field) {
		super(entityType, field);
	}

	public boolean isAutoIncrement() {
		if (this.getColumnField().getAnnotation(NoAutoIncrement.class) != null) {
			return false;
		}
		Class idType = this.getColumnField().getType();
		return idType.equals(int.class) || idType.equals(Integer.class) || idType.equals(long.class) || idType.equals(Long.class);
	}
}
