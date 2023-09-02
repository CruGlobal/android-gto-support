# JsonApi does reflection on type, method, and field annotations.
-keepattributes RuntimeVisibleAnnotations

# Keep annotation default values (e.g., JsonApiAttribute.serialize).
-keepattributes AnnotationDefault

# Keep all the jsonapi annotations
-keep class org.ccci.gto.android.common.jsonapi.annotation.*

# Keep any JsonApiType object
-keep @org.ccci.gto.android.common.jsonapi.annotation.JsonApiType class **
-keep @org.ccci.gto.android.common.jsonapi.annotation.JsonApiType class ** {
  !transient <fields>;
  @org.ccci.gto.android.common.jsonapi.annotation.JsonApiPostCreate <methods>;
}
