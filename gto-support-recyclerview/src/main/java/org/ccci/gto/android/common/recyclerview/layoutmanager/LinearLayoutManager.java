package org.ccci.gto.android.common.recyclerview.layoutmanager;

import android.content.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class LinearLayoutManager extends android.support.v7.widget.LinearLayoutManager {
    private static final Logger LOG = LoggerFactory.getLogger(LinearLayoutManager.class);

    public LinearLayoutManager(final Context context) {
        super(context);
    }

    public LinearLayoutManager(final Context context, final int orientation, final boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    /**
     * Override getChildCount to work around a bug in ChildHelper where there are more hidden views than actual child
     * views. This bug is triggered when a RecyclerView loses all of it's children.
     *
     * @return adjusted child view count
     * @see <a href="http://stackoverflow.com/questions/29241676/android-recyclerview-last-item-remove-runtime-error">http://stackoverflow.com/questions/29241676/android-recyclerview-last-item-remove-runtime-error</a>
     */
    @Override
    public int getChildCount() {
        final int count = super.getChildCount();
        if (count < 0) {
            LOG.error("LinearLayoutManager.getChildCount() returned a negative count {}", count);
        }
        // XXX: workaround a bug in the LayoutManager where there are more hidden views than child views
        return count >= 0 ? count : 0;
    }
}
