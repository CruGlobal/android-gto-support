package org.ccci.gto.android.common.kotlin.coroutines.flow

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

val EmptyStateFlow = MutableStateFlow(null).asStateFlow()
