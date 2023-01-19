# Make sure we keep annotations, fields, and methods for any jsonapi model
-keepattributes *Annotation*
-keep class org.ccci.gto.android.common.jsonapi.annotation.*
-keepclassmembers @org.ccci.gto.android.common.jsonapi.annotation.JsonApiType class ** {
  <fields>;
  @org.ccci.gto.android.common.jsonapi.annotation.JsonApiPostCreate <methods>;
}
