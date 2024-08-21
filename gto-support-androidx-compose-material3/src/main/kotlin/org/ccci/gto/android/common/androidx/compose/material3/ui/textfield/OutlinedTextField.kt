package org.ccci.gto.android.common.androidx.compose.material3.ui.textfield

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics

@Composable
fun OutlinedTextField(
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors()
) = OutlinedTextField(
    value = value,
    onValueChange = {},
    modifier = modifier.clickableTextField(onClick),
    readOnly = true,
    label = label,
    supportingText = supportingText,
    isError = isError,
    colors = colors
)

private fun Modifier.clickableTextField(onClick: () -> Unit) = this
    .pointerInput(onClick) {
        awaitEachGesture {
            // Must be PointerEventPass.Initial to observe events before the text field consumes them
            // in the Main pass
            awaitFirstDown(pass = PointerEventPass.Initial)
            val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
            if (upEvent != null) onClick()
        }
    }
    .semantics {
        onClick {
            onClick()
            true
        }
    }
