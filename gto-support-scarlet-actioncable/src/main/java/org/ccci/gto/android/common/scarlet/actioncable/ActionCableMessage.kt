package org.ccci.gto.android.common.scarlet.actioncable

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class ActionCableMessage(val channel: String)
