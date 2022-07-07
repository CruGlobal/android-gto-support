# flipper rules
-keep @com.facebook.proguard.annotations.DoNotStrip class com.facebook.flipper.** { *; }
-keepclasseswithmembers class com.facebook.flipper.** { @com.facebook.proguard.annotations.DoNotStrip *; }

# catch-all because jni references several other classes directly that don't have DoNotStrip annotations
-keep class com.facebook.flipper.** { *; }

# fbjni transitive dependency rules
-keep @com.facebook.jni.annotations.DoNotStrip class com.facebook.jni.** { *; }
-keepclasseswithmembers class com.facebook.jni.** { @com.facebook.jni.annotations.DoNotStrip *; }
