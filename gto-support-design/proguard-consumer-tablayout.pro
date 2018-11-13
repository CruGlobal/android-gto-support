# keep the baseBackgroundDrawable member name so we can update the tab background image
-keepclassmembernames class com.google.android.material.tabs.TabLayout$TabView {
    android.graphics.drawable.Drawable baseBackgroundDrawable;
}
