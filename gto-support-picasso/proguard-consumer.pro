# keep the context member name so we can inject a TintContextWrapper
-keepclassmembernames class com.squareup.picasso.Picasso$Builder {
    android.content.Context context;
}
