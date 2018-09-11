package android.arch.persistence.db.framework;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import java.lang.reflect.Field;

public class FrameworkSQLiteUtils {
    @Nullable
    public static SQLiteDatabase getFrameworkDb(@Nullable final SupportSQLiteDatabase db) {
        if (db instanceof FrameworkSQLiteDatabase) {
            try {
                final Field field = FrameworkSQLiteDatabase.class.getDeclaredField("mDelegate");
                field.setAccessible(true);
                return (SQLiteDatabase) field.get(db);
            } catch (NoSuchFieldException ignored) {
            } catch (IllegalAccessException ignored) {
            }
        }
        return null;
    }
}
