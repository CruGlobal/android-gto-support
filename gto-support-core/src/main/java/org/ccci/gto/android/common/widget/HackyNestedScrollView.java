package org.ccci.gto.android.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import org.ccci.gto.android.common.util.view.ViewUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

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
