package org.ccci.gto.android.common.widget;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

public class ChainedTouchListener implements View.OnTouchListener {
    @NonNull
    private final View.OnTouchListener[] mListeners;

    public ChainedTouchListener(@NonNull final View.OnTouchListener... listeners) {
        mListeners = listeners;
    }

    @Override
    public boolean onTouch(@NonNull final View v, @NonNull final MotionEvent event) {
        for (final View.OnTouchListener listener : mListeners) {
            if (listener.onTouch(v, event)) {
                return true;
            }
        }
        return false;
    }
}
