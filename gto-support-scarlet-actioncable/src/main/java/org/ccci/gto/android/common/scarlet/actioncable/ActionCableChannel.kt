package org.ccci.gto.android.common.scarlet.actioncable

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class ActionCableChannel(val channel: String)

internal val Array<Annotation>.actionCableChannel
    get() = firstOrNull { it is ActionCableChannel } as? ActionCableChannel
