
package com.android.pc.ioc.db.sqlite;

import android.database.Cursor;

import java.io.Closeable;
import java.io.IOException;

/**
 * Author: wyouflf
 * Date: 13-8-26
 * Time: 下午6:02
 */
public class IOUtils {

    private IOUtils() {
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
            }
        }
    }

    public static void closeQuietly(Cursor cursor) {
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Exception e) {
            }
        }
    }
}
