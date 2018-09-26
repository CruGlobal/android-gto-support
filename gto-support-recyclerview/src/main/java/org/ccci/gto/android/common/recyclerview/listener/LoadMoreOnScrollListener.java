package org.ccci.gto.android.common.recyclerview.listener;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;

public abstract class LoadMoreOnScrollListener extends RecyclerView.OnScrollListener {
    private boolean mLoading = false;
    private int mThreshold = 5;

    public void setThreshold(final int threshold) {
        mThreshold = threshold;
    }

    @Override
    public void onScrolled(@NonNull final RecyclerView view, final int dx, final int dy) {
        // short-circuit if we are currently loading more data
        if (mLoading) {
            return;
        }

        // short-circuit if there isn't a valid layout manager
        final LayoutManager layoutManager = view.getLayoutManager();
        if (layoutManager == null) {
            return;
        }

        // determine the last visible item
        final int lastVisible;
        if (layoutManager instanceof LinearLayoutManager) {
            lastVisible = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else {
            lastVisible = getLastVisiblePosition(layoutManager);
        }

        // have we passed the threshold to load more data?
        if (lastVisible + mThreshold > layoutManager.getItemCount() - 1) {
            mLoading = true;
            onLoadMore();
        }
    }

    protected int getLastVisiblePosition(@NonNull final LayoutManager layoutManager) {
        return NO_POSITION;
    }

    protected abstract void onLoadMore();

    public final void doneLoading() {
        mLoading = false;
    }
}
