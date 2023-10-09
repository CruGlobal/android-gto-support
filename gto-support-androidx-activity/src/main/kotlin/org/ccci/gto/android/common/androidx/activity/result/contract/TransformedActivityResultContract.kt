package org.ccci.gto.android.common.androidx.activity.result.contract

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

private class TransformedActivityResultContract<I, O, NEW_I, NEW_O>(
    private val baseContract: ActivityResultContract<I, O>,
    private val inputTransform: (NEW_I) -> I,
    private val outputTransform: (O) -> NEW_O,
) : ActivityResultContract<NEW_I, NEW_O>() {
    override fun createIntent(context: Context, input: NEW_I) =
        baseContract.createIntent(context, inputTransform(input))

    override fun parseResult(resultCode: Int, intent: Intent?) =
        outputTransform(baseContract.parseResult(resultCode, intent))
}

fun <I, O, NEW_I, NEW_O> ActivityResultContract<I, O>.transform(
    inputTransform: (NEW_I) -> I,
    outputTransform: (O) -> NEW_O,
): ActivityResultContract<NEW_I, NEW_O> = TransformedActivityResultContract(
    baseContract = this,
    inputTransform = inputTransform,
    outputTransform = outputTransform,
)

fun <I, O, NEW_I> ActivityResultContract<I, O>.transformInput(
    inputTransform: (NEW_I) -> I,
): ActivityResultContract<NEW_I, O> = TransformedActivityResultContract(
    baseContract = this,
    inputTransform = inputTransform,
    outputTransform = { it },
)

fun <I, O, NEW_O> ActivityResultContract<I, O>.transformOutput(
    outputTransform: (O) -> NEW_O,
): ActivityResultContract<I, NEW_O> = TransformedActivityResultContract(
    baseContract = this,
    inputTransform = { it },
    outputTransform = outputTransform,
)
