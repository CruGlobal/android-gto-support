# keep the client member name so we can modify the OkHttpClient
-keepclassmembernames class com.snowplowanalytics.snowplow.tracker.Emitter {
    okhttp3.OkHttpClient client;
}
