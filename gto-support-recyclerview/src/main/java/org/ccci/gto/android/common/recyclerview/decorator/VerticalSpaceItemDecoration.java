package org.ccci.gto.android.common.recyclerview.decorator;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
