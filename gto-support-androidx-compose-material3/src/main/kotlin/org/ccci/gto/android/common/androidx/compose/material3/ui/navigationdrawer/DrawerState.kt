package org.ccci.gto.android.common.androidx.compose.material3.ui.navigationdrawer

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue

suspend fun DrawerState.toggle() = if (targetValue == DrawerValue.Open) close() else open()
