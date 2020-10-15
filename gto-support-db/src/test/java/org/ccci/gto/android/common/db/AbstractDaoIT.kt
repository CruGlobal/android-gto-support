package org.ccci.gto.android.common.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.ccci.gto.android.common.db.AbstractDaoIT.Contract.CompoundTable
import org.ccci.gto.android.common.db.AbstractDaoIT.Contract.RootTable
import org.ccci.gto.android.common.db.CommonTables.LastSyncTable
import org.ccci.gto.android.common.db.Expression.Companion.bind
import org.ccci.gto.android.common.db.model.Compound
import org.ccci.gto.android.common.db.model.Root
import org.ccci.gto.android.common.util.database.getLong
import org.ccci.gto.android.common.util.database.getString
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AbstractDaoIT {
    private lateinit var db: TestDatabase
    private lateinit var dao: TestDao

    @Before
    fun setup() {
        db = TestDatabase(ApplicationProvider.getApplicationContext())
        dao = TestDao(db)
    }

    @After
    fun cleanup() {
        db.close()
    }

    @Test
    fun verifyInsert() {
        dao.insert(Root(1, "1"))
        val foundRoot = dao.find(Root::class.java, 1)
        assertNotNull(foundRoot)
        assertEquals("1", foundRoot!!.test)
    }

    @Test
    fun verifyInsertPrimaryKeyConflictCompoundKey() {
        // create object
        val orig = Compound("1", "2", "orig", "orig")
        dao.insert(orig)

        // test PK conflict
        val conflict = Compound("1", "2", "conflict", "conflict")
        try {
            dao.insert(conflict)
            fail("There should have been a PK conflict")
        } catch (expected: SQLiteConstraintException) {
            // expected conflict, should be original
            val refresh = dao.refresh(conflict)
            assertNotNull(refresh)
            assertEquals(orig.id1, refresh!!.id1)
            assertEquals(orig.id2, refresh.id2)
            assertEquals(orig.data1, refresh.data1)
            assertEquals(orig.data2, refresh.data2)
            assertNotEquals(conflict.data1, refresh.data1)
            assertNotEquals(conflict.data2, refresh.data2)
        }
    }

    @Test
    fun verifyGetWithLimit() {
        dao.insert(Root(1, "1"))
        dao.insert(Root(2, "2"))
        dao.insert(Root(3, "3"))
        val objs = Query.select<Root>().orderBy(RootTable.COLUMN_ID).limit(1).offset(1).get(dao)
        assertEquals(1, objs.size)
        assertEquals(2, objs[0].id)
    }

    @Test
    fun verifyUpdateCompoundKey() {
        // create object
        val orig = Compound("1", "2", "orig", "orig")
        dao.insert(orig)

        // test update
        val update = Compound("1", "2", "update", "update")
        dao.update(update)
        val refresh = dao.refresh(orig)!!
        assertThat(refresh.id1, allOf(equalTo(orig.id1), equalTo(update.id1)))
        assertThat(refresh.id2, allOf(equalTo(orig.id2), equalTo(update.id2)))
        assertThat(refresh.data1, allOf(not(equalTo(orig.data1)), equalTo(update.data1)))
        assertThat(refresh.data2, allOf(not(equalTo(orig.data2)), equalTo(update.data2)))
    }

    @Test
    fun verifyUpdatePartialCompoundKey() {
        // create object
        val orig = Compound("1", "2", "orig", "orig")
        dao.insert(orig)

        // test partial update
        val update = Compound("1", "2", "update", "update")
        dao.update(update, CompoundTable.COLUMN_DATA1)
        val refresh = dao.refresh(orig)!!
        assertThat(refresh.id1, allOf(equalTo(orig.id1), equalTo(update.id1)))
        assertThat(refresh.id2, allOf(equalTo(orig.id2), equalTo(update.id2)))
        assertThat(refresh.data1, allOf(not(equalTo(orig.data1)), equalTo(update.data1)))
        assertThat(refresh.data2, allOf(equalTo(orig.data2), not(equalTo(update.data2))))
    }

    @Test
    fun verifyUpdateWhere() {
        // create some objects
        val orig1 = Compound("1", "1", "orig1", "d1")
        val orig2 = Compound("1", "2", "orig2", "d2")
        val orig3 = Compound("2", "1", "orig3", "d3")
        dao.insert(orig1)
        dao.insert(orig2)
        dao.insert(orig3)

        // verify initial values
        val refresh11 = dao.refresh(orig1)!!
        val refresh12 = dao.refresh(orig2)!!
        val refresh13 = dao.refresh(orig3)!!
        assertEquals(orig1.data1, refresh11.data1)
        assertEquals(orig1.data2, refresh11.data2)
        assertEquals(orig2.data1, refresh12.data1)
        assertEquals(orig2.data2, refresh12.data2)
        assertEquals(orig3.data1, refresh13.data1)
        assertEquals(orig3.data2, refresh13.data2)

        // trigger update
        val update = Compound("", "", null, "newData")
        dao.update(update, CompoundTable.FIELD_ID1.eq("1"), CompoundTable.COLUMN_DATA2)

        // verify final values
        val refresh21 = dao.refresh(orig1)!!
        val refresh22 = dao.refresh(orig2)!!
        val refresh23 = dao.refresh(orig3)!!
        assertEquals(orig1.data1, refresh21.data1)
        assertThat(refresh21.data2, allOf(not(equalTo(orig1.data2)), equalTo(update.data2)))
        assertEquals(orig2.data1, refresh22.data1)
        assertThat(refresh22.data2, allOf(not(equalTo(orig2.data2)), equalTo(update.data2)))
        assertEquals(orig3.data1, refresh23.data1)
        assertThat(refresh23.data2, allOf(equalTo(orig3.data2), not(equalTo(update.data2))))
    }

    @Test
    fun verifyUpdateWhereAll() {
        // create some objects
        val orig1 = Compound("1", "1", "orig1", "d1")
        val orig2 = Compound("1", "2", "orig2", "d2")
        val orig3 = Compound("2", "1", "orig3", "d3")
        dao.insert(orig1)
        dao.insert(orig2)
        dao.insert(orig3)

        // verify initial values
        val refresh11 = dao.refresh(orig1)!!
        val refresh12 = dao.refresh(orig2)!!
        val refresh13 = dao.refresh(orig3)!!
        assertEquals(orig1.data1, refresh11.data1)
        assertEquals(orig1.data2, refresh11.data2)
        assertEquals(orig2.data1, refresh12.data1)
        assertEquals(orig2.data2, refresh12.data2)
        assertEquals(orig3.data1, refresh13.data1)
        assertEquals(orig3.data2, refresh13.data2)

        // trigger update
        val update = Compound("", "", null, "newData")
        dao.update(update, null, CompoundTable.COLUMN_DATA2)

        // verify final values
        val refresh21 = dao.refresh(orig1)!!
        val refresh22 = dao.refresh(orig2)!!
        val refresh23 = dao.refresh(orig3)!!
        assertEquals(orig1.data1, refresh21.data1)
        assertThat(refresh21.data2, allOf(not(equalTo(orig1.data2)), equalTo(update.data2)))
        assertEquals(orig2.data1, refresh22.data1)
        assertThat(refresh22.data2, allOf(not(equalTo(orig2.data2)), equalTo(update.data2)))
        assertEquals(orig3.data1, refresh23.data1)
        assertThat(refresh23.data2, allOf(not(equalTo(orig3.data2)), equalTo(update.data2)))
    }

    @Test
    fun verifyDeleteCompoundKey() {
        // create object
        val orig = Compound("1", "2", "orig", "orig")
        dao.insert(orig)
        assertNotNull(dao.refresh(orig))

        // test deletion
        dao.delete(orig)
        assertNull(dao.refresh(orig))
    }

    class Contract : BaseContract() {
        object RootTable : Base {
            const val TABLE_NAME = "root"
            private val TABLE = Table.forClass<Root>()
            internal const val COLUMN_ID = BaseColumns._ID
            internal const val COLUMN_TEST = "test"
            private val FIELD_ID = TABLE.field(COLUMN_ID)
            val PROJECTION_ALL = arrayOf(COLUMN_ID, COLUMN_TEST)
            private const val SQL_COLUMN_ID = "$COLUMN_ID INTEGER PRIMARY KEY"
            private const val SQL_COLUMN_TEST = "$COLUMN_TEST TEXT"
            val SQL_WHERE_PRIMARY_KEY = FIELD_ID.eq(bind())
            val SQL_CREATE_TABLE = create(TABLE_NAME, SQL_COLUMN_ID, SQL_COLUMN_TEST)
            val SQL_DELETE_TABLE = drop(TABLE_NAME)
        }

        internal object CompoundTable : Base {
            const val TABLE_NAME = "compound"
            private val TABLE = Table.forClass<Compound>()
            internal const val COLUMN_ID1 = "id1"
            internal const val COLUMN_ID2 = "id2"
            internal const val COLUMN_DATA1 = "data1"
            internal const val COLUMN_DATA2 = "data2"
            internal val FIELD_ID1 = TABLE.field(COLUMN_ID1)
            private val FIELD_ID2 = TABLE.field(COLUMN_ID2)
            val PROJECTION_ALL = arrayOf(COLUMN_ID1, COLUMN_ID2, COLUMN_DATA1, COLUMN_DATA2)
            private const val SQL_COLUMN_ID1 = "$COLUMN_ID1 TEXT NOT NULL"
            private const val SQL_COLUMN_ID2 = "$COLUMN_ID2 TEXT NOT NULL"
            private const val SQL_COLUMN_DATA1 = "$COLUMN_DATA1 TEXT"
            private const val SQL_COLUMN_DATA2 = "$COLUMN_DATA2 TEXT"
            private const val SQL_PRIMARY_KEY = "UNIQUE($COLUMN_ID1,$COLUMN_ID2)"
            val SQL_WHERE_PRIMARY_KEY = FIELD_ID1.eq(bind()).and(FIELD_ID2.eq(bind()))
            val SQL_CREATE_TABLE = create(
                TABLE_NAME,
                Base.SQL_COLUMN_ROWID,
                SQL_COLUMN_ID1,
                SQL_COLUMN_ID2,
                SQL_COLUMN_DATA1,
                SQL_COLUMN_DATA2,
                SQL_PRIMARY_KEY
            )
            val SQL_DELETE_TABLE = drop(TABLE_NAME)
        }
    }

    internal object CompoundMapper : AbstractMapper<Compound>() {
        override fun mapField(values: ContentValues, field: String, obj: Compound) = when (field) {
            CompoundTable.COLUMN_ID1 -> values.put(field, obj.id1)
            CompoundTable.COLUMN_ID2 -> values.put(field, obj.id2)
            CompoundTable.COLUMN_DATA1 -> values.put(field, obj.data1)
            CompoundTable.COLUMN_DATA2 -> values.put(field, obj.data2)
            else -> super.mapField(values, field, obj)
        }

        override fun newObject(c: Cursor) =
            Compound(c.getString(CompoundTable.COLUMN_ID1).orEmpty(), c.getString(CompoundTable.COLUMN_ID2).orEmpty())

        override fun toObject(c: Cursor) = super.toObject(c).apply {
            data1 = c.getString(CompoundTable.COLUMN_DATA1)
            data2 = c.getString(CompoundTable.COLUMN_DATA2)
        }
    }
    object RootMapper : AbstractMapper<Root>() {
        override fun mapField(values: ContentValues, field: String, obj: Root) = when (field) {
            RootTable.COLUMN_ID -> values.put(field, obj.id)
            RootTable.COLUMN_TEST -> values.put(field, obj.test)
            else -> super.mapField(values, field, obj)
        }

        override fun newObject(c: Cursor) =
            Root(c.getLong(RootTable.COLUMN_ID) ?: 0L, c.getString(RootTable.COLUMN_TEST))
    }

    internal class TestDatabase(context: Context) : WalSQLiteOpenHelper(context, "test_db", null, 1) {
        init {
            resetDatabase(writableDatabase)
        }

        override fun onCreate(db: SQLiteDatabase) {
            db.apply {
                execSQL(RootTable.SQL_CREATE_TABLE)
                execSQL(CompoundTable.SQL_CREATE_TABLE)
                execSQL(LastSyncTable.SQL_CREATE_TABLE)
            }
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) =
            error("onUpgrade should not be triggered")

        private fun resetDatabase(db: SQLiteDatabase) {
            try {
                db.beginTransaction()
                db.execSQL(RootTable.SQL_DELETE_TABLE)
                db.execSQL(CompoundTable.SQL_DELETE_TABLE)
                db.execSQL(LastSyncTable.SQL_DELETE_TABLE)
                onCreate(db)
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }
    }

    class TestDao(helper: SQLiteOpenHelper) : AbstractDao(helper) {
        init {
            registerType(
                Root::class.java,
                RootTable.TABLE_NAME,
                RootTable.PROJECTION_ALL,
                RootMapper,
                RootTable.SQL_WHERE_PRIMARY_KEY
            )
            registerType(
                Compound::class.java,
                CompoundTable.TABLE_NAME,
                CompoundTable.PROJECTION_ALL,
                CompoundMapper,
                CompoundTable.SQL_WHERE_PRIMARY_KEY
            )
        }

        override fun getPrimaryKeyWhere(obj: Any) = when (obj) {
            is Root -> getPrimaryKeyWhere(Root::class.java, obj.id)
            is Compound -> getPrimaryKeyWhere(Compound::class.java, obj.id1, obj.id2)
            else -> super.getPrimaryKeyWhere(obj)
        }

        fun reset() {
            delete(Root::class.java, null)
            delete(Compound::class.java, null)
        }
    }
}
