package org.ccci.gto.android.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

import org.ccci.gto.android.common.R;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;

public class AffixingScrollView extends ScrollView {
    private static final int INVALID_RESOURCE = -1;

    @IdRes
    private int mAffixingId = INVALID_RESOURCE;
    @Nullable
    private View mAffixingView;
    @IdRes
    private int mAnchorId = INVALID_RESOURCE;
    @Nullable
    private View mAnchorView;

    public AffixingScrollView(final Context context) {
        super(context);
    }

    public AffixingScrollView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        this.init(attrs);
    }

    public AffixingScrollView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        this.init(attrs);
    }

    private void init(final AttributeSet attrs) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.AffixingScrollView);
        mAffixingId = a.getResourceId(R.styleable.AffixingScrollView_affixingView, INVALID_RESOURCE);
        mAnchorId = a.getResourceId(R.styleable.AffixingScrollView_anchorView, INVALID_RESOURCE);
        a.recycle();
    }

    /**
     * Attach the appropriate child view to managed during scrolling as the affixing view. This view MUST be an
     * existing child.
     *
     * @param v View to manage as the floating view
     */
    public void setAffixingView(final View v) {
        mAffixingView = v;
        updateAffixingView();
    }

    /**
     * Attach the appropriate child view to monitor during scrolling as the anchoring space for the floating view.
     * This view MUST be an existing child.
     *
     * @param v View to manage as the anchoring space
     */
    public void setAnchorView(final View v) {
        mAnchorView = v;
        updateAffixingView();
    }

    /* BEGIN lifecycle */

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mAnchorId > 0) {
            mAnchorView = this.findViewById(mAnchorId);
            mAnchorId = INVALID_RESOURCE;
        }
        if (mAffixingId > 0) {
            mAffixingView = this.findViewById(mAffixingId);
            mAffixingId = INVALID_RESOURCE;
        }
        updateAffixingView();
    }

    @Override
    protected void onLayout(final boolean changed, final int l, final int t, final int r, final int b) {
        super.onLayout(changed, l, t, r, b);
        updateAffixingView();
    }

    @Override
    protected void onScrollChanged(final int l, final int t, final int oldl, final int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        updateAffixingView();
    }

    /* END lifecycle */

    private void updateAffixingView() {
        if (mAnchorView == null || mAffixingView == null) {
            return;
        }

        // is the anchor view off the screen?
        if (mAnchorView.getTop() - getScrollY() < 0) {
            // Yes, move the affixing view to the top of the visible view
            mAffixingView.offsetTopAndBottom(getScrollY() - mAffixingView.getTop());
        } else {
            // No, move the affixing view to the anchor view
            mAffixingView.offsetTopAndBottom(mAnchorView.getTop() - mAffixingView.getTop());
        }
    }
}
