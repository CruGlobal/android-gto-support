package org.ccci.gto.android.common.widget;

import static org.ccci.gto.android.common.widget.AccordionView.Adapter.POSITION_NONE;
import static org.ccci.gto.android.common.widget.AccordionView.Adapter.POSITION_UNCHANGED;

import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public class AccordionView extends LinearLayout {
    static final int STATE_CLOSED = 0;
    static final int STATE_OPENING = 1;
    static final int STATE_CLOSING = 2;
    static final int STATE_OPEN = 3;

    static final int HEIGHT_UNKNOWN = -1;

    @IntDef({STATE_CLOSED, STATE_OPENING, STATE_CLOSING, STATE_OPEN})
    @Retention(RetentionPolicy.SOURCE)
    private @interface State {
    }

    @Nullable
    private Adapter<ViewHolder> mAdapter;
    @NonNull
    private final AccordionObserver mObserver = new AccordionObserver();
    @NonNull
    private final AccordionHeaderOnClickListener mHeaderOnClick = new AccordionHeaderOnClickListener();
    @NonNull
    private ViewHolder[] mSections = new ViewHolder[0];
    @Nullable
    private ViewHolder mActiveSection;

    public AccordionView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    @Override
    protected boolean checkLayoutParams(final ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @NonNull
    @Override
    public LayoutParams generateLayoutParams(final AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @NonNull
    @Override
    protected LayoutParams generateLayoutParams(@NonNull final ViewGroup.LayoutParams p) {
        if (p instanceof LinearLayout.LayoutParams) {
            return new LayoutParams((LinearLayout.LayoutParams) p);
        } else if (p instanceof MarginLayoutParams) {
            return new LayoutParams((MarginLayoutParams) p);
        } else {
            return new LayoutParams(p);
        }
    }

    @NonNull
    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        final int orientation = getOrientation();
        if (orientation == VERTICAL) {
            return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        } else {
            throw new IllegalStateException("Orientation for AccordionView can only be VERTICAL");
        }
    }

    @NonNull
    private LayoutParams upgradeLayoutParams(@Nullable final ViewGroup.LayoutParams p) {
        if (p instanceof LayoutParams) {
            return (LayoutParams) p;
        } else if (p != null) {
            return generateLayoutParams(p);
        } else {
            return generateDefaultLayoutParams();
        }

    }

    @SuppressWarnings("unchecked")
    public void setAdapter(@Nullable final Adapter<? extends ViewHolder> adapter) {
        final Adapter<ViewHolder> old = mAdapter;
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mObserver);
        }
        mAdapter = (Adapter<ViewHolder>) adapter;
        if (mAdapter != null) {
            mAdapter.registerDataSetObserver(mObserver);
        }

        // update the AccordionView because the dataset changed
        dataSetChanged(old);
    }

    /**
     * Refresh the view because the backing data has changed
     */
    void dataSetChanged(@Nullable Adapter<ViewHolder> oldAdapter) {
        // if there isn't an old adapter, use the current adapter
        if (oldAdapter == null) {
            oldAdapter = mAdapter;
        }

        // create new sections array
        final ViewHolder[] oldSections = mSections;
        mSections = new ViewHolder[mAdapter != null ? mAdapter.getCount() : 0];

        // attempt to reuse any existing sections
        for (int i = 0; i < oldSections.length; i++) {
            int position =
                    mAdapter != null && mAdapter == oldAdapter ? mAdapter.getPosition(oldSections[i]) : POSITION_NONE;
            switch (position) {
                case POSITION_NONE:
                    // are we removing the active section?
                    if (oldSections[i] == mActiveSection) {
                        mActiveSection = null;
                    }

                    if (oldAdapter != null) {
                        oldAdapter.onDestroyViewHolder(oldSections[i]);
                    }

                    break;
                case POSITION_UNCHANGED:
                    position = i;
                default:
                    mSections[position] = oldSections[i];
                    break;
            }

            // remove the views for this section, we will re-add them soon
            removeView(oldSections[i].mHeader);
            removeView(oldSections[i].mContentOuter);
        }

        // bind active sections
        for (int i = 0; i < mSections.length; i++) {
            assert mAdapter != null : "if there are sections there has to be an Adapter";

            // create sections that don't exist
            if (mSections[i] == null) {
                mSections[i] = createViewHolder(i);
            }

            // rebind this section
            mAdapter.onBindViewHolder(mSections[i], i);
            addView(mSections[i].mHeader);
            addView(mSections[i].mContentOuter);
        }

        // reset active section if needed
        if (mActiveSection == null && mSections.length > 0) {
            showSection(mSections[0]);
        }

        // request a fresh layout
        requestLayout();
    }

    @NonNull
    private ViewHolder createViewHolder(final int position) {
        assert mAdapter != null : "createViewHolder should only be called when we have a valid Adapter";

        // generate header and content containers
        final FrameLayout header = new FrameLayout(getContext());
        header.setLayoutParams(generateDefaultLayoutParams());
        final FrameLayout contentOuter = new FrameLayout(getContext());
        contentOuter.setLayoutParams(generateDefaultLayoutParams());
        final FrameLayout contentInner = new FrameLayout(getContext());
        contentOuter.addView(contentInner);
        contentInner.getLayoutParams().height = LayoutParams.MATCH_PARENT;
        contentInner.getLayoutParams().width = LayoutParams.MATCH_PARENT;

        // create new ViewHolder
        final ViewHolder holder = mAdapter.onCreateViewHolder(header, contentInner, position);
        holder.mHeader = header;
        holder.mContentOuter = contentOuter;
        holder.mContentInner = contentInner;

        // update header and content container LayoutParams
        final LayoutParams headerLp = upgradeLayoutParams(holder.mHeader.getLayoutParams());
        headerLp.mHolder = holder;
        holder.mHeader.setLayoutParams(headerLp);
        final LayoutParams contentLp = upgradeLayoutParams(holder.mContentOuter.getLayoutParams());
        contentLp.mHolder = holder;
        contentLp.height = 0;
        contentLp.weight = 1;
        holder.mContentOuter.setLayoutParams(contentLp);

        // set header OnClick handler
        holder.mHeader.setOnClickListener(mHeaderOnClick);

        // close section by default
        holder.mState = STATE_CLOSED;
        holder.mContentOuter.setVisibility(GONE);

        // return new ViewHolder
        return holder;
    }

    void showSection(@NonNull final ViewHolder section) {
        // short-circuit if the specified section is already active
        if (mActiveSection == section) {
            return;
        }

        // start closing the previous active section
        if (mActiveSection != null) {
            // update inner height if the section was OPEN
            if (mActiveSection.mState == STATE_OPEN) {
                mActiveSection.mContentInner.getLayoutParams().height = mActiveSection.mContentOuter.getHeight();
            }

            // mark this section as closing
            mActiveSection.mState = STATE_CLOSING;
            mActiveSection = null;
        }

        // mark the specified section as opening
        mActiveSection = section;
        mActiveSection.mState = STATE_OPENING;
        mActiveSection.mContentOuter.setVisibility(VISIBLE);

        // generate a list of all closing sections
        final List<ViewHolder> closing = new ArrayList<>();
        for (final ViewHolder holder : mSections) {
            if (holder.mState == STATE_CLOSING) {
                closing.add(holder);
            }
        }

        // update state (no animation)
        mActiveSection.mState = STATE_OPEN;
        for (final ViewHolder holder : closing) {
            holder.mContentOuter.setVisibility(GONE);
            holder.mContentInner.getLayoutParams().height = LayoutParams.MATCH_PARENT;
            holder.mState = STATE_CLOSED;
        }
    }

    private class AccordionObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            dataSetChanged(null);
        }

        @Override
        public void onInvalidated() {
            dataSetChanged(null);
        }
    }

    public static class LayoutParams extends LinearLayout.LayoutParams {
        @Nullable
        ViewHolder mHolder;

        public LayoutParams(@NonNull final LinearLayout.LayoutParams source) {
//            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            super((MarginLayoutParams) source);
            this.weight = source.weight;
            this.gravity = source.gravity;
//            } else {
//                super(source);
//            }
        }

        public LayoutParams(@NonNull final MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(@NonNull final ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(int width, int height, float weight) {
            super(width, height, weight);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }
    }

    public abstract static class Adapter<VH extends ViewHolder> {
        public static final int POSITION_NONE = -1;
        public static final int POSITION_UNCHANGED = -2;

        private final DataSetObservable mObservable = new DataSetObservable();

        /**
         * Returns the number of sections for the accordion
         *
         * @return number of sections for the accordion
         */
        public abstract int getCount();

        public int getPosition(@NonNull final VH holder) {
            return POSITION_NONE;
        }

        public boolean hasStableHeight(@NonNull final VH holder) {
            return false;
        }

        @NonNull
        public abstract VH onCreateViewHolder(@NonNull ViewGroup headerParent, @NonNull ViewGroup contentParent,
                                              int position);

        public void onBindViewHolder(@NonNull final VH holder, final int position) {
        }

        public void onDestroyViewHolder(@NonNull final VH holder) {
        }

        /**
         * This method should be called by the application if the data backing this adapter has changed
         * and associated views should update.
         */
        public void notifyDataSetChanged() {
            mObservable.notifyChanged();
        }

        /**
         * Register an observer to receive callbacks related to the adapter's data changing.
         *
         * @param observer The {@link DataSetObserver} which will receive callbacks.
         */
        public void registerDataSetObserver(@NonNull final DataSetObserver observer) {
            mObservable.registerObserver(observer);
        }

        /**
         * Unregister an observer from callbacks related to the adapter's data changing.
         *
         * @param observer The {@link DataSetObserver} which will be unregistered.
         */
        public void unregisterDataSetObserver(@NonNull final DataSetObserver observer) {
            mObservable.unregisterObserver(observer);
        }
    }

    public static class ViewHolder {
        @NonNull
        /* final */ View mHeader;
        @NonNull
        /* final */ ViewGroup mContentOuter;
        @NonNull
        /* final */ ViewGroup mContentInner;
        @State
        int mState = STATE_CLOSED;
        int mHeight = HEIGHT_UNKNOWN;
    }

    private class AccordionHeaderOnClickListener implements OnClickListener {
        @Override
        public void onClick(final View v) {
            final ViewGroup.LayoutParams lp = v.getLayoutParams();
            if (lp instanceof LayoutParams) {
                final ViewHolder section = ((LayoutParams) lp).mHolder;
                if (section != null) {
                    showSection(section);
                }
            }
        }
    }
}
