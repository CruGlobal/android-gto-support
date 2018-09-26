package org.ccci.gto.android.common.support.v4.widget;

import android.view.View;
import android.widget.HorizontalScrollView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

/**
 * Link a HorizontalScrollView to a ViewPager to create a Parallax effect. Logic based off of
 * http://stackoverflow.com/a/38543133
 */
public class ParallaxScrollListener extends ViewPager.SimpleOnPageChangeListener {
    @NonNull
    private final ViewPager mPager;
    @NonNull
    private final HorizontalScrollView mParallax;

    public ParallaxScrollListener(@NonNull final ViewPager pager,
                                  @NonNull final HorizontalScrollView parallaxContainer) {
        mPager = pager;
        mParallax = parallaxContainer;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        int x = (int) ((getPageWidth() * position + positionOffsetPixels) * computeFactor());
        mParallax.scrollTo(x, 0);
    }

    private int getPageWidth() {
        return mPager.getWidth() - mPager.getPaddingLeft() - mPager.getPaddingRight() + mPager.getPageMargin();
    }

    private int getScrollRange() {
        int scrollRange = 0;
        if (mParallax.getChildCount() > 0) {
            final View child = mParallax.getChildAt(0);
            scrollRange = Math.max(0, child.getWidth() -
                    (mParallax.getWidth() - mParallax.getPaddingLeft() - mParallax.getPaddingRight()));
        }
        return scrollRange;
    }

    private float computeFactor() {
        final int pages = mPager.getAdapter().getCount();
        if (pages > 1) {
            return getScrollRange() / (float) (getPageWidth() * (pages - 1));
        }
        return 0;
    }
}
