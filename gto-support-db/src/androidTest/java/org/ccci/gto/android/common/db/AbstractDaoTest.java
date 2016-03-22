package org.ccci.gto.android.common.db;

import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;

import org.ccci.gto.android.common.db.Contract.RootTable;
import org.ccci.gto.android.common.db.model.Root;
import org.ccci.gto.android.common.db.util.CursorUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class AbstractDaoTest extends InstrumentationTestCase {
    private TestDao getDao() {
        return TestDao.getInstance(getInstrumentation().getContext());
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

        assertThat(cursor.getColumnIndex(RootTable.COLUMN_ID), is(not(-1)));
        assertThat(cursor.getColumnIndex(RootTable.COLUMN_TEST), is(not(-1)));
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

        Cursor cursor = dao.getCursor(Query.select(Root.class).groupBy(RootTable.FIELD_TEST));

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
        Cursor cursor = dao.getCursor(Query.select(Root.class).groupBy(RootTable.FIELD_ID).having(max));

        assertThat(cursor.getCount(), is(1));

        cursor.moveToFirst();

        assertThat(CursorUtils.getString(cursor, RootTable.COLUMN_TEST), is("2"));
    }

    @Test
    public void testAddPrefixToSingleField() {
        final TestDao dao = getDao();
        String prefix = RootTable.TABLE_NAME + ".";

        String editedClause = dao.addPrefixToFields(RootTable.COLUMN_ID, prefix);

        assertThat(editedClause, is("root._id"));
    }

    @Test
    public void testAddPrefixToMultipleFields() {
        final TestDao dao = getDao();
        String prefix = RootTable.TABLE_NAME + ".";

        String editedClause = dao.addPrefixToFields(RootTable.COLUMN_ID + "," + RootTable.COLUMN_TEST,
                prefix);

        assertThat(editedClause, is("root._id,root.test"));
    }

    @Test
    public void testAddPrefixToMultipleFieldsSomePrefixed() {
        final TestDao dao = getDao();
        String prefix = RootTable.TABLE_NAME + ".";

        String editedClause = dao.addPrefixToFields(
                prefix + RootTable.COLUMN_ID + "," + RootTable.COLUMN_TEST,
                prefix);

        assertThat(editedClause, is("root._id,root.test"));
    }

    @Test
    public void testAddPrefixToMultipleFieldsAllPrefixed() {
        final TestDao dao = getDao();
        String prefix = RootTable.TABLE_NAME + ".";

        String editedClause = dao.addPrefixToFields(
                prefix + RootTable.COLUMN_ID + "," + prefix + RootTable.COLUMN_TEST,
                prefix);

        assertThat(editedClause, is("root._id,root.test"));
    }

    @Override
    protected void tearDown() throws Exception {
        final TestDao dao = getDao();
        dao.delete(Root.class, null);
        super.tearDown();
    }
}
