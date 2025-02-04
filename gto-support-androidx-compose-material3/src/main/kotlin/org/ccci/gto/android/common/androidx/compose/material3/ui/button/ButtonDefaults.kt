package org.ccci.gto.android.common.androidx.compose.material3.ui.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.unit.dp

val ButtonDefaults.ButtonHorizontalPadding get() = 24.dp
val ButtonDefaults.ButtonVerticalPadding get() = 8.dp
val ButtonDefaults.ButtonWithTrailingIconHorizontalEndPadding get() = 16.dp

val ButtonDefaults.ButtonWithTrailingIconContentPadding get() = PaddingValues(
    start = ButtonHorizontalPadding,
    top = ButtonVerticalPadding,
    end = ButtonWithTrailingIconHorizontalEndPadding,
    bottom = ButtonVerticalPadding,
)
