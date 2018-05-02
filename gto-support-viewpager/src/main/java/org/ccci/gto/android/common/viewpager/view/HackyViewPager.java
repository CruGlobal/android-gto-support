package org.ccci.gto.android.common.viewpager.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import org.ccci.gto.android.common.viewpager.util.ViewPagerUtils;

public class HackyViewPager extends ViewPager {
    public HackyViewPager(@NonNull final Context context) {
        super(context);
    }

    public HackyViewPager(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(final MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (final RuntimeException e) {
            return ViewPagerUtils.handleOnInterceptTouchEventException(e);
        }
    }
}
