package org.ccci.gto.android.common.support.v4.widget;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

public final class ViewPagerFakeDragTouchListener implements View.OnTouchListener {
    private static final int INVALID_POINTER_ID = -1;

    @NonNull
    private final ViewPager mPager;
    private final int mTouchSlop;

    private boolean mIsBeingDragged = false;
    private int mPointerId = INVALID_POINTER_ID;
    private float mLastTouchX;
    private float mLastTouchY;

    public ViewPagerFakeDragTouchListener(@NonNull final ViewPager pager) {
        mPager = pager;
        mTouchSlop = ViewConfiguration.get(pager.getContext()).getScaledPagingTouchSlop();
    }

    @Override
    public boolean onTouch(final View v, @NonNull final MotionEvent event) {
        final int index = event.getActionIndex();
        final int pointer = event.getPointerId(index);
        final int action = event.getActionMasked();

        // short-circuit for events we don't care about (events for other pointers &
        // non-down events when not tracking a pointer)
        if ((mPointerId != INVALID_POINTER_ID && mPointerId != pointer) ||
                (mPointerId == INVALID_POINTER_ID && action != MotionEvent.ACTION_DOWN &&
                        action != MotionEvent.ACTION_POINTER_DOWN)) {
            return false;
        }

        // get position coords for current event & calculate distance from previous position
        final float x = event.getX(index);
        final float y = event.getY(index);
        final float dx = x - mLastTouchX;
        final float dy = y - mLastTouchY;

        // handle this event
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                // if the pager is already being dragged, short-circuit
                if (mPager.isFakeDragging() || !mPager.beginFakeDrag()) {
                    return false;
                }

                mPointerId = pointer;
                mPager.beginFakeDrag();

                // update the last touch values
                mLastTouchX = x;
                mLastTouchY = y;

                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsBeingDragged || !isTouchSlop(dx, dy)) {
                    mIsBeingDragged = true;
                    mPager.fakeDragBy(dx);

                    // update the last touch values
                    mLastTouchX = x;
                    mLastTouchY = y;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (mIsBeingDragged || !isTouchSlop(dx, dy)) {
                    mPager.fakeDragBy(dx);
                }

                // fall-through to cleanup after the drag is complete
            case MotionEvent.ACTION_CANCEL:
                mPointerId = INVALID_POINTER_ID;
                mPager.endFakeDrag();
                mIsBeingDragged = false;
                break;
            default:
        }

        return true;
    }

    private boolean isTouchSlop(final float dx, final float dy) {
        return Math.abs(dx) < mTouchSlop || Math.abs(dx) < Math.abs(dy);
    }
}
