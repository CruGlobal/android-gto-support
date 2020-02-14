package org.ccci.gto.android.common.db.async

import android.database.sqlite.SQLiteOpenHelper
import org.ccci.gto.android.common.db.AbstractDao
import org.ccci.gto.android.common.db.AsyncDao

@Deprecated("Since v3.4.0, extend AbstractDao and implement AsyncDao instead.")
abstract class AbstractAsyncDao protected constructor(helper: SQLiteOpenHelper) : AbstractDao(helper), AsyncDao
