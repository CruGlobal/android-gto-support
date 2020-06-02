package org.ccci.gto.android.common.scarlet.actioncable

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class ActionCableSerializeDataAsJson

internal val Array<Annotation>.actionCableSerializeDataAsJson
    get() = firstOrNull { it is ActionCableSerializeDataAsJson } as? ActionCableSerializeDataAsJson
