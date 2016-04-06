package org.ccci.gto.android.common.db.util;

import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.ccci.gto.android.common.db.Contract.RootTable;
import org.ccci.gto.android.common.db.Query;
import org.ccci.gto.android.common.db.TestDao;
import org.ccci.gto.android.common.db.model.Root;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class CursorUtilsIT {
    private TestDao getDao() {
        return TestDao.getInstance(InstrumentationRegistry.getContext());
    }

    @Test
    public void testGetStringDefaultValue() throws Exception {
        final TestDao dao = getDao();
        final String defValue = "default";

        // create a couple objects to test
        dao.insert(new Root(1, null));
        dao.insert(new Root(2, ""));
        dao.insert(new Root(3, "3"));

        // test default when field isn't present
        Cursor c = dao.getCursor(Query.select(Root.class).projection(RootTable.COLUMN_ID).orderBy(RootTable.COLUMN_ID));
        c.moveToPosition(-1);
        while (c.moveToNext()) {
            assertThat(CursorUtils.getString(c, RootTable.COLUMN_TEST, defValue), is(defValue));
        }
        c.close();

        // test default when field is present
        c = dao.getCursor(Query.select(Root.class).orderBy(RootTable.COLUMN_ID));
        c.moveToPosition(0);
        assertThat(CursorUtils.getLong(c, RootTable.COLUMN_ID), is(1L));
        assertThat("null column value should return default value",
                   CursorUtils.getString(c, RootTable.COLUMN_TEST, defValue), is(defValue));
        c.moveToPosition(1);
        assertThat(CursorUtils.getLong(c, RootTable.COLUMN_ID), is(2L));
        assertThat(CursorUtils.getString(c, RootTable.COLUMN_TEST, defValue), is(not(defValue)));
        c.moveToPosition(2);
        assertThat(CursorUtils.getLong(c, RootTable.COLUMN_ID), is(3L));
        assertThat(CursorUtils.getString(c, RootTable.COLUMN_TEST, defValue), is(not(defValue)));
        c.close();
    }

    @Test
    public void verifyGetLongDefaultValue() throws Exception {
        final TestDao dao = getDao();

        // create a few objects
        dao.insert(new Root(1, null));
        dao.insert(new Root(2, "2"));

        // try a few different default values
        for (final Long defValue : new Long[] {null, 0L, 9L}) {
            // test default when field isn't present
            Cursor c = dao.getCursor(
                    Query.select(Root.class).projection(RootTable.COLUMN_ID).orderBy(RootTable.COLUMN_ID));
            c.moveToPosition(-1);
            while (c.moveToNext()) {
                assertThat(CursorUtils.getLong(c, RootTable.COLUMN_TEST, defValue), is(defValue));
            }
            c.close();

            // test default when field is present
            c = dao.getCursor(Query.select(Root.class).orderBy(RootTable.COLUMN_ID));
            c.moveToPosition(0);
            assertThat(CursorUtils.getLong(c, RootTable.COLUMN_ID), is(1L));
            assertThat("null column value should return default value",
                       CursorUtils.getLong(c, RootTable.COLUMN_TEST, defValue), is(defValue));
            c.moveToPosition(1);
            assertThat(CursorUtils.getLong(c, RootTable.COLUMN_ID), is(2L));
            assertThat(CursorUtils.getLong(c, RootTable.COLUMN_TEST, defValue), is(2L));
            c.close();
        }
    }

    // XXX: currently disabled because we don't support invalid values being returned from the database. We really
    // XXX: should support this at some point though.
    @Test
    @Ignore
    public void verifyGetLongInvalidValue() throws Exception {
        final TestDao dao = getDao();
        final long defValue = 9;

        // create a test object
        dao.insert(new Root(1, "a"));

        final Cursor c = dao.getCursor(Query.select(Root.class).orderBy(RootTable.COLUMN_ID));
        c.moveToPosition(0);
        assertThat(CursorUtils.getLong(c, RootTable.COLUMN_ID), is(1L));
        assertThat("invalid column value should return default value",
                   CursorUtils.getLong(c, RootTable.COLUMN_TEST, defValue), is(defValue));
        c.close();
    }

    @After
    public void reset() throws Exception {
        getDao().reset();
    }
}
