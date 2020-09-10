package org.ccci.gto.android.common.facebook.flipper.plugins.databases

import android.content.Context
import com.facebook.flipper.plugins.databases.impl.DefaultSqliteDatabaseProvider

@Deprecated("Since v3.6.2, use DefaultSqliteDatabaseProvider from flipper directly")
class DefaultSqliteDatabaseProvider(context: Context) : DefaultSqliteDatabaseProvider(context)
