package org.ccci.gto.android.common.widget;

import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;

public final class ConsumeAllTouchListener implements View.OnTouchListener {
    @Override
    public boolean onTouch(@NonNull final View v, @NonNull final MotionEvent event) {
        return true;
    }
}
