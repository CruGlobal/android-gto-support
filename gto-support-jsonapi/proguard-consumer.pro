# Make sure we keep annotations and fields for any jsonapi model
-keepattributes *Annotation*
-keepclassmembers @org.ccci.gto.android.common.jsonapi.annotation.JsonApiType class ** {
  <fields>;
}
