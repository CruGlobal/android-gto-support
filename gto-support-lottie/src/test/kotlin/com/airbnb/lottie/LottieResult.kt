package com.airbnb.lottie

fun LottieResult<LottieComposition>.wrapInLottieTask() = LottieTask({ this }, true)
