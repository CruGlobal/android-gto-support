# keep the mScroller member name so we can inject a custom scroller
-keepclassmembernames class androidx.viewpager.widget.ViewPager {
    android.widget.Scroller mScroller;
}
