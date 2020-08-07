@file:JvmName("Transformations2")

package org.ccci.gto.android.common.lifecycle

import androidx.lifecycle.LiveData
import org.ccci.gto.android.common.androidx.lifecycle.combineWith
import org.ccci.gto.android.common.androidx.lifecycle.sortedWith
import org.ccci.gto.android.common.androidx.lifecycle.switchCombineWith

@Deprecated(
    "Since v3.4.0, use the version in gto-support-androidx-lifecycle instead.",
    ReplaceWith(
        "switchCombineWith(other, mapFunction)",
        "org.ccci.gto.android.common.androidx.lifecycle.switchCombineWith"
    )
)
@JvmName("switchCombine")
fun <IN1, IN2, OUT> LiveData<IN1>.switchCombineWith(
    other: LiveData<IN2>,
    mapFunction: (IN1?, IN2?) -> LiveData<out OUT>
) = switchCombineWith(other, mapFunction)

@Deprecated(
    "Since v3.4.0, use the version in gto-support-androidx-lifecycle instead.",
    ReplaceWith(
        "switchCombineWith(other, other2, mapFunction)",
        "org.ccci.gto.android.common.androidx.lifecycle.switchCombineWith"
    )
)
@JvmName("switchCombine")
fun <IN1, IN2, IN3, OUT> LiveData<IN1>.switchCombineWith(
    other: LiveData<IN2>,
    other2: LiveData<IN3>,
    mapFunction: (IN1?, IN2?, IN3?) -> LiveData<out OUT>
) = switchCombineWith(other, other2, mapFunction)

@Deprecated(
    "Since v3.4.0, use the version in gto-support-androidx-lifecycle instead.",
    ReplaceWith(
        "switchCombineWith(other, other2, other3, mapFunction)",
        "org.ccci.gto.android.common.androidx.lifecycle.switchCombineWith"
    )
)
@JvmName("switchCombine")
fun <IN1, IN2, IN3, IN4, OUT> LiveData<IN1>.switchCombineWith(
    other: LiveData<IN2>,
    other2: LiveData<IN3>,
    other3: LiveData<IN4>,
    mapFunction: (IN1?, IN2?, IN3?, IN4?) -> LiveData<out OUT>
) = switchCombineWith(other, other2, other3, mapFunction)

@Deprecated(
    "Since v3.4.0, use the version in gto-support-androidx-lifecycle instead.",
    ReplaceWith("combineWith(other, mapFunction)", "org.ccci.gto.android.common.androidx.lifecycle.combineWith")
)
@JvmName("combine")
fun <IN1, IN2, OUT> LiveData<IN1>.combineWith(other: LiveData<IN2>, mapFunction: (IN1?, IN2?) -> OUT) =
    combineWith(other, mapFunction)

@Deprecated(
    "Since v3.4.0, use the version in gto-support-androidx-lifecycle instead.",
    ReplaceWith("sortedWith(comparator)", "org.ccci.gto.android.common.androidx.lifecycle.sortedWith")
)
fun <T> LiveData<out Iterable<T>>.sortedWith(comparator: Comparator<in T>) = sortedWith(comparator)
