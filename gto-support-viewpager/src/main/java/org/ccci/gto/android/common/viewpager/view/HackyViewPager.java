package org.ccci.gto.android.common.viewpager.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import org.ccci.gto.android.common.util.view.ViewUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

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
            return ViewUtils.handleOnInterceptTouchEventException(e);
        }
    }
}
