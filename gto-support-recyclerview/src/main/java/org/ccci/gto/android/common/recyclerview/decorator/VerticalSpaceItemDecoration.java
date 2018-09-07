package org.ccci.gto.android.common.recyclerview.decorator;

import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {
    @DimenRes
    private final int mSpace;

    public VerticalSpaceItemDecoration(@DimenRes final int space) {
        mSpace = space;
    }

    @Override
    public void getItemOffsets(@NonNull final Rect outRect, @NonNull final View view,
                               @NonNull final RecyclerView parent, final RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
            outRect.bottom = view.getResources().getDimensionPixelSize(mSpace);
        }
    }
}
