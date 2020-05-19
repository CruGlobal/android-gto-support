package org.ccci.gto.android.common.scarlet.actioncable

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class ActionCableMessage(val channel: String)

internal val Array<Annotation>.actionCableMessage
    get() = firstOrNull { it is ActionCableMessage } as? ActionCableMessage
