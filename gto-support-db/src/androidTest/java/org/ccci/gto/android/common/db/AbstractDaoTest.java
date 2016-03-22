package org.ccci.gto.android.common.db;

import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;

import org.ccci.gto.android.common.db.TestDao.TestContract.RootTable;
import org.ccci.gto.android.common.db.model.Root;
import org.ccci.gto.android.common.db.util.CursorUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class AbstractDaoTest extends InstrumentationTestCase {
    private TestDao getDao() {
        return new TestDao(getInstrumentation().getContext());
    }

    @Test
    public void testGetTable() throws Exception {
        final TestDao dao = getDao();
        assertThat(dao.getTable(Root.class), is(RootTable.TABLE_NAME));
    }

    @Test
    public void testGetCursor() {
        final TestDao dao = getDao();
        final Cursor cursor = dao.getCursor(Query.select(Root.class));

        assertThat(cursor.getColumnName(0), is("_id"));
        assertThat(cursor.getColumnName(1), is("test"));
    }

    @Test
    public void testInsert() {
        final TestDao dao = getDao();

        insertRow(dao, 1, "1");

        Root foundRoot = dao.find(Root.class, 1);
        assertNotNull(foundRoot);
        assertThat(foundRoot.test, is("1"));
    }

    private void insertRow(TestDao dao, int id, String test) {
        Root testRoot = new Root();
        testRoot.id = id;
        testRoot.test = test;

        dao.insert(testRoot);
    }

    @Test
    public void testWhere() {
        final TestDao dao = getDao();

        insertRow(dao, 1, "1");
        insertRow(dao, 2, "2");

        Cursor cursor = dao.getCursor(
            Query.select(Root.class).where(RootTable.SQL_WHERE_PRIMARY_KEY.args(2)));
        cursor.moveToFirst();

        assertThat(CursorUtils.getString(cursor, RootTable.COLUMN_TEST), is("2"));
    }

    @Test
    public void testGroupBy() {
        final TestDao dao = getDao();

        insertRow(dao, 1, "1");
        insertRow(dao, 2, "2");
        insertRow(dao, 3, "2");
        insertRow(dao, 4, "2");
        insertRow(dao, 5, "3");

        Cursor cursor = dao.getCursor(
            Query.select(Root.class).groupBy(RootTable.COLUMN_TEST));

        assertThat(cursor.getCount(), is(3));

        cursor.moveToFirst();

        assertThat(CursorUtils.getString(cursor, RootTable.COLUMN_TEST), is("1"));

        cursor.moveToNext();

        assertThat(CursorUtils.getString(cursor, RootTable.COLUMN_TEST), is("2"));

        cursor.moveToNext();

        assertThat(CursorUtils.getString(cursor, RootTable.COLUMN_TEST), is("3"));
    }

    @Test
    public void testHaving() {
        final TestDao dao = getDao();

        insertRow(dao, 1, "1");
        insertRow(dao, 2, "2");
        insertRow(dao, 3, "2");
        insertRow(dao, 4, "2");
        insertRow(dao, 5, "3");

        Expression max = RootTable.FIELD_ID.max().eq(3);
        Cursor cursor = dao.getCursor(
                Query.select(Root.class).groupBy(RootTable.COLUMN_ID).having(max));

        assertThat(cursor.getCount(), is(1));

        cursor.moveToFirst();

        assertThat(CursorUtils.getString(cursor, RootTable.COLUMN_TEST), is("2"));
    }

    @Override
    protected void tearDown() throws Exception {
        final TestDao dao = getDao();
        dao.delete(Root.class, RootTable.SQL_WHERE_ANY);
        super.tearDown();
    }
}
