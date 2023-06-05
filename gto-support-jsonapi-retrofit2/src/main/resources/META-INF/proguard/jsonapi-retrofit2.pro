-keep,allowobfuscation,allowshrinking class org.ccci.gto.android.common.jsonapi.retrofit2.BaseJsonApiParams
-keep,allowobfuscation,allowshrinking class org.ccci.gto.android.common.jsonapi.retrofit2.JsonApiParams

# When using Retrofit the JsonApiObject is likely to be used as a Response object generic.
# So let's just keep it to avoid potential proguard rule debugging.
-keep,allowobfuscation,allowshrinking class org.ccci.gto.android.common.jsonapi.model.JsonApiObject
