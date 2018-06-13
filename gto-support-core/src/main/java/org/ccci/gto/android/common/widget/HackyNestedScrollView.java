package org.ccci.gto.android.common.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import org.ccci.gto.android.common.util.view.ViewUtils;

public class HackyNestedScrollView extends NestedScrollView {
    public HackyNestedScrollView(@NonNull final Context context) {
        super(context);
    }

    public HackyNestedScrollView(@NonNull final Context context,
                                 @Nullable final AttributeSet attrs) {
        super(context, attrs);
    }

    public HackyNestedScrollView(@NonNull final Context context, @Nullable final AttributeSet attrs,
                                 final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (final RuntimeException e) {
            return ViewUtils.handleOnTouchEventException(e);
        }
    }
}
