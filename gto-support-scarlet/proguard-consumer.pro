# Retain service method parameters when optimizing.
# Copied & modified from: https://github.com/square/retrofit/blob/master/retrofit/src/main/resources/META-INF/proguard/retrofit2.pro#L11-L14
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @com.tinder.scarlet.ws.* <methods>;
}
