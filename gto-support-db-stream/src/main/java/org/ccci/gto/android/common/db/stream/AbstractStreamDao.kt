package org.ccci.gto.android.common.db.stream

import android.database.sqlite.SQLiteOpenHelper
import org.ccci.gto.android.common.db.AbstractDao
import org.ccci.gto.android.common.db.StreamDao

@Deprecated("Since v3.4.0, extend AbstractDao and directly implement StreamDao instead.")
abstract class AbstractStreamDao protected constructor(helper: SQLiteOpenHelper) : AbstractDao(helper), StreamDao
