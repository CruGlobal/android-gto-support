package org.ccci.gto.android.common.scarlet

import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.internal.utils.defaultPlatform
import com.tinder.scarlet.platform

/**
 * Scarlet improperly detects desugared Android apps as the Java 8 runtime.
 * This method will force the default platform for Scarlet to be used.
 *
 * See:
 * https://github.com/Tinder/Scarlet/issues/235
 * https://issuetracker.google.com/issues/342419066
 */
fun Scarlet.Builder.forceDefaultPlatform() = apply { platform = defaultPlatform() }
