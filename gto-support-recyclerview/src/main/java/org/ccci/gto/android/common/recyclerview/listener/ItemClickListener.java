package org.ccci.gto.android.common.recyclerview.listener;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class ItemClickListener implements RecyclerView.OnItemTouchListener {
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    @NonNull
    private final OnItemClickListener mListener;

    private final GestureDetector mGestureDetector;

    public ItemClickListener(@NonNull final Context context, @NonNull final OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(final RecyclerView view, final MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
            return true;
        }
        return false;
    }

    @Override
    public void onTouchEvent(final RecyclerView view, final MotionEvent motionEvent) {
    }
}
