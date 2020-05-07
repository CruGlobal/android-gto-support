package org.ccci.gto.android.common.db;

import android.database.sqlite.SQLiteConstraintException;

import org.ccci.gto.android.common.db.Contract.CompoundTable;
import org.ccci.gto.android.common.db.Contract.RootTable;
import org.ccci.gto.android.common.db.model.Compound;
import org.ccci.gto.android.common.db.model.Root;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import androidx.test.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class AbstractDaoIT {
    private TestDao getDao() {
        return TestDao.getInstance(InstrumentationRegistry.getContext());
    }

    @Test
    public void testGetTable() throws Exception {
        final TestDao dao = getDao();
        assertThat(dao.getTable(Root.class), is(RootTable.TABLE_NAME));
    }

    @Test
    public void testInsert() {
        final TestDao dao = getDao();

        dao.insert(new Root(1, "1"));

        Root foundRoot = dao.find(Root.class, 1);
        assertNotNull(foundRoot);
        assertThat(foundRoot.test, is("1"));
    }

    @Test
    public void testInsertPrimaryKeyConflictCompoundKey() throws Exception {
        final TestDao dao = getDao();

        // create object
        final Compound orig = new Compound("1", "2", "orig", "orig");
        dao.insert(orig);

        // test PK conflict
        final Compound conflict = new Compound("1", "2", "conflict", "conflict");
        try {
            dao.insert(conflict);
            fail("There should have been a PK conflict");
        } catch (final SQLiteConstraintException expected) {
            // expected conflict, should be original
            final Compound refresh = dao.refresh(conflict);
            assertNotNull(refresh);
            assertThat(refresh.id1, is(orig.id1));
            assertThat(refresh.id2, is(orig.id2));
            assertThat(refresh.data1, allOf(is(orig.data1), is(not(conflict.data1))));
            assertThat(refresh.data2, allOf(is(orig.data2), is(not(conflict.data2))));
        }
    }

    @Test
    public void verifyGetWithLimit() {
        final TestDao dao = getDao();

        dao.insert(new Root(1, "1"));
        dao.insert(new Root(2, "2"));
        dao.insert(new Root(3, "3"));

        final List<Root> objs = dao.get(Query.select(Root.class).orderBy(RootTable.COLUMN_ID).limit(1).offset(1));
        assertEquals(1, objs.size());
        assertEquals(2, objs.get(0).id);
    }

    @Test
    public void testUpdateCompoundKey() throws Exception {
        final TestDao dao = getDao();

        // create object
        final Compound orig = new Compound("1", "2", "orig", "orig");
        dao.insert(orig);

        // test update
        final Compound update = new Compound("1", "2", "update", "update");
        dao.update(update);
        final Compound refresh = dao.refresh(orig);
        assertNotNull(refresh);
        assertThat(refresh.id1, allOf(is(orig.id1), is(update.id1)));
        assertThat(refresh.id2, allOf(is(orig.id2), is(update.id2)));
        assertThat(refresh.data1, allOf(is(not(orig.data1)), is(update.data1)));
        assertThat(refresh.data2, allOf(is(not(orig.data2)), is(update.data2)));
    }

    @Test
    public void testUpdatePartialCompoundKey() throws Exception {
        final TestDao dao = getDao();

        // create object
        final Compound orig = new Compound("1", "2", "orig", "orig");
        dao.insert(orig);

        // test partial update
        final Compound update = new Compound("1", "2", "update", "update");
        dao.update(update, CompoundTable.COLUMN_DATA1);
        final Compound refresh = dao.refresh(orig);
        assertNotNull(refresh);
        assertThat(refresh.id1, allOf(is(orig.id1), is(update.id1)));
        assertThat(refresh.id2, allOf(is(orig.id2), is(update.id2)));
        assertThat(refresh.data1, allOf(is(not(orig.data1)), is(update.data1)));
        assertThat(refresh.data2, allOf(is(orig.data2), is(not(update.data2))));
    }

    @Test
    public void verifyUpdateWhere() throws Exception {
        final TestDao dao = getDao();

        // create some objects
        final Compound orig1 = new Compound("1", "1", "orig1", "d1");
        final Compound orig2 = new Compound("1", "2", "orig2", "d2");
        final Compound orig3 = new Compound("2", "1", "orig3", "d3");
        dao.insert(orig1);
        dao.insert(orig2);
        dao.insert(orig3);

        // verify initial values
        final Compound refresh11 = dao.refresh(orig1);
        final Compound refresh12 = dao.refresh(orig2);
        final Compound refresh13 = dao.refresh(orig3);
        assertNotNull(refresh11);
        assertNotNull(refresh12);
        assertNotNull(refresh13);
        assertThat(refresh11.data1, is(orig1.data1));
        assertThat(refresh11.data2, is(orig1.data2));
        assertThat(refresh12.data1, is(orig2.data1));
        assertThat(refresh12.data2, is(orig2.data2));
        assertThat(refresh13.data1, is(orig3.data1));
        assertThat(refresh13.data2, is(orig3.data2));

        // trigger update
        final Compound update = new Compound("", "", null, "newData");
        dao.update(update, CompoundTable.FIELD_ID1.eq("1"), CompoundTable.COLUMN_DATA2);

        // verify final values
        final Compound refresh21 = dao.refresh(orig1);
        final Compound refresh22 = dao.refresh(orig2);
        final Compound refresh23 = dao.refresh(orig3);
        assertNotNull(refresh21);
        assertNotNull(refresh22);
        assertNotNull(refresh23);
        assertThat(refresh21.data1, is(orig1.data1));
        assertThat(refresh21.data2, allOf(is(not(orig1.data2)), is(update.data2)));
        assertThat(refresh22.data1, is(orig2.data1));
        assertThat(refresh22.data2, allOf(is(not(orig2.data2)), is(update.data2)));
        assertThat(refresh23.data1, is(orig3.data1));
        assertThat(refresh23.data2, allOf(is(orig3.data2), is(not(update.data2))));
    }

    @Test
    public void verifyUpdateWhereAll() throws Exception {
        final TestDao dao = getDao();

        // create some objects
        final Compound orig1 = new Compound("1", "1", "orig1", "d1");
        final Compound orig2 = new Compound("1", "2", "orig2", "d2");
        final Compound orig3 = new Compound("2", "1", "orig3", "d3");
        dao.insert(orig1);
        dao.insert(orig2);
        dao.insert(orig3);

        // verify initial values
        final Compound refresh11 = dao.refresh(orig1);
        final Compound refresh12 = dao.refresh(orig2);
        final Compound refresh13 = dao.refresh(orig3);
        assertNotNull(refresh11);
        assertNotNull(refresh12);
        assertNotNull(refresh13);
        assertThat(refresh11.data1, is(orig1.data1));
        assertThat(refresh11.data2, is(orig1.data2));
        assertThat(refresh12.data1, is(orig2.data1));
        assertThat(refresh12.data2, is(orig2.data2));
        assertThat(refresh13.data1, is(orig3.data1));
        assertThat(refresh13.data2, is(orig3.data2));

        // trigger update
        final Compound update = new Compound("", "", null, "newData");
        dao.update(update, (Expression) null, CompoundTable.COLUMN_DATA2);

        // verify final values
        final Compound refresh21 = dao.refresh(orig1);
        final Compound refresh22 = dao.refresh(orig2);
        final Compound refresh23 = dao.refresh(orig3);
        assertNotNull(refresh21);
        assertNotNull(refresh22);
        assertNotNull(refresh23);
        assertThat(refresh21.data1, is(orig1.data1));
        assertThat(refresh21.data2, allOf(is(not(orig1.data2)), is(update.data2)));
        assertThat(refresh22.data1, is(orig2.data1));
        assertThat(refresh22.data2, allOf(is(not(orig2.data2)), is(update.data2)));
        assertThat(refresh23.data1, is(orig3.data1));
        assertThat(refresh23.data2, allOf(is(not(orig3.data2)), is(update.data2)));
    }

    @Test
    public void testDeleteCompoundKey() throws Exception {
        final TestDao dao = getDao();

        // create object
        final Compound orig = new Compound("1", "2", "orig", "orig");
        dao.insert(orig);
        Compound refresh = dao.refresh(orig);
        assertNotNull(refresh);

        // test deletion
        dao.delete(orig);
        refresh = dao.refresh(orig);
        assertNull(refresh);
    }

    @After
    public void reset() throws Exception {
        getDao().reset();
    }
}
