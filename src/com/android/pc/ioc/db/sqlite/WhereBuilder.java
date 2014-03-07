
package com.android.pc.ioc.db.sqlite;

import android.text.TextUtils;

import com.android.pc.ioc.db.table.ColumnUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: wyouflf
 * Date: 13-7-29
 * Time: 上午9:35
 */
public class WhereBuilder {

    private List<String> whereItems;

    private WhereBuilder() {
        this.whereItems = new ArrayList<String>();
    }

    /**
     * create new instance
     *
     * @return
     */
    public static WhereBuilder b() {
        return new WhereBuilder();
    }

    /**
     * create new instance
     *
     * @param columnName
     * @param op         operator: "=","<","LIKE"...
     * @param value
     * @return
     */
    public static WhereBuilder b(String columnName, String op, Object value) {
        WhereBuilder result = new WhereBuilder();
        result.appendCondition(null, columnName, op, value);
        return result;
    }

    /**
     * add AND condition
     *
     * @param columnName
     * @param op         operator: "=","<","LIKE"...
     * @param value
     * @return
     */
    public WhereBuilder append(String columnName, String op, Object value) {
        appendCondition(whereItems.size() == 0 ? null : "AND", columnName, op, value);
        return this;
    }

    /**
     * add OR condition
     *
     * @param columnName
     * @param op         operator: "=","<","LIKE"...
     * @param value
     * @return
     */
    public WhereBuilder appendOR(String columnName, String op, Object value) {
        appendCondition(whereItems.size() == 0 ? null : "OR", columnName, op, value);
        return this;
    }

    @Override
    public String toString() {
        if (whereItems == null || whereItems.size() < 1) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String item : whereItems) {
            sb.append(item);
        }
        return sb.toString();
    }

    private void appendCondition(String conj, String columnName, String op, Object value) {
        StringBuilder sqlSb = new StringBuilder();
        if (!TextUtils.isEmpty(conj)) {
            sqlSb.append(" " + conj + " ");
        }
        if ("!=".equals(op)) {
            op = "<>";
        } else if ("==".equals(op)) {
            op = "=";
        }
        if (value == null) {
            if ("=".equals(op)) {
                sqlSb.append(columnName).append(" IS NULL");
            } else if ("<>".equals(op)) {
                sqlSb.append(columnName).append(" IS NOT NULL");
            } else {
                sqlSb.append(columnName).append(" " + op + " NULL");
            }
        } else {
            sqlSb.append(columnName).append(" " + op + " ");
            value = ColumnUtils.convert2DbColumnValueIfNeeded(value);
            if ("TEXT".equals(ColumnUtils.fieldType2DbType(value.getClass()))) {
                String valueStr = value.toString();
                if (valueStr.indexOf('\'') != -1) { // convert single quotations
                    valueStr = valueStr.replace("'", "''");
                }
                sqlSb.append("'" + valueStr + "'");
            } else {
                sqlSb.append(value);
            }
        }
        whereItems.add(sqlSb.toString());
    }
}
