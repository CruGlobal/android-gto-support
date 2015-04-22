package org.ccci.gto.android.common.animation;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public abstract class SimpleAnimatorListener implements Animator.AnimatorListener {
    @Override
    public void onAnimationStart(Animator animation) {
        // do nothing
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        // do nothing
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        // do nothing
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
        // do nothing
    }
}
