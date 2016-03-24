package org.ccci.gto.android.common.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class SQLiteOpenHelperCompatIT {
    @Test
    public void verifyOnConfigureCalledOncePerOpen() throws Exception {
        final TestDbHelper helper = new TestDbHelper(InstrumentationRegistry.getContext());

        helper.getReadableDatabase();
        helper.getWritableDatabase();

        assertEquals(helper.mConfigure, helper.mOpen);
        assertThat(helper.mConfigure, is(greaterThan(0)));
        assertThat(helper.mOpen, is(greaterThan(0)));

        helper.close();
    }

    private static class TestDbHelper extends SQLiteOpenHelperCompat {
        int mConfigure = 0;
        int mOpen = 0;

        public TestDbHelper(Context context) {
            super(context, "SQLiteOpenHelperCompatIT", null, 1);
        }

        @Override
        public void onConfigure(SQLiteDatabase db) {
            super.onConfigure(db);
            mConfigure++;
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
            mOpen++;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {}

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
    }
}
