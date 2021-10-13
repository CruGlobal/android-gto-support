package com.airbnb.lottie

import org.ccci.gto.android.common.util.getDeclaredMethodOrNull

private val setCompositionTaskMethod by lazy {
    getDeclaredMethodOrNull<LottieAnimationView>("setCompositionTask", LottieTask::class.java)
}

internal fun LottieAnimationView.setCompositionTask(task: LottieTask<LottieComposition>) =
    setCompositionTaskMethod?.invoke(this, task)
