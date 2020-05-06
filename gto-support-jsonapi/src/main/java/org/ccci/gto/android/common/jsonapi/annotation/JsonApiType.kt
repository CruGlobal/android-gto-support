package org.ccci.gto.android.common.jsonapi.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class JsonApiType(val value: String, val aliases: Array<String> = [])
