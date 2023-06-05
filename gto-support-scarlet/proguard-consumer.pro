# These rules are based upon Retrofit rules found here:
# https://github.com/square/retrofit/blob/dcc890bc1e13895309c83bb7ca448cc885c41d6b/retrofit/src/main/resources/META-INF/proguard/retrofit2.pro

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @com.tinder.scarlet.ws.* <methods>;
}

# With R8 full mode, it sees no subtypes of Scarlet interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @com.tinder.scarlet.ws.* <methods>; }
-keep,allowobfuscation interface <1>

# R8 full mode strips generic signatures from return types if not kept.
-if interface * { @com.tinder.scarlet.ws.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>
