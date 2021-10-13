package org.ccci.gto.android.common.lottie

import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.setCompositionTask
import java.io.File

fun LottieAnimationView.setAnimationFromFile(file: File) = setCompositionTask(file.loadLottieComposition())
