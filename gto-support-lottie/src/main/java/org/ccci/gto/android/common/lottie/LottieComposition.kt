package org.ccci.gto.android.common.lottie

import android.annotation.SuppressLint
import androidx.annotation.WorkerThread
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieResult
import com.airbnb.lottie.LottieTask
import com.airbnb.lottie.parser.moshi.JsonReader
import java.io.File
import okio.Okio.buffer
import okio.Okio.source

// TODO: utilize LottieCompositionFactory.cache() to leverage the cache
@SuppressLint("RestrictedApi")
fun File.loadLottieComposition(cacheKey: String? = "file_$path") = LottieTask { loadLottieCompositionSync(cacheKey) }

@WorkerThread
@SuppressLint("RestrictedApi")
fun File.loadLottieCompositionSync(cacheKey: String? = null): LottieResult<LottieComposition> =
    LottieCompositionFactory.fromJsonReaderSync(JsonReader.of(buffer(source(this))), cacheKey)
