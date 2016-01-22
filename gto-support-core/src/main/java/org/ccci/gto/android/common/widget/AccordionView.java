package org.ccci.gto.android.common.widget;

import static org.ccci.gto.android.common.widget.AccordionView.Adapter.POSITION_NONE;
import static org.ccci.gto.android.common.widget.AccordionView.Adapter.POSITION_UNCHANGED;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import org.ccci.gto.android.common.animation.SimpleAnimatorListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public class AccordionView extends LinearLayout {
    public static final int STATE_CLOSED = 0;
    static final int STATE_OPENING = 1;
    static final int STATE_CLOSING = 2;
    public static final int STATE_OPEN = 3;

    static final int HEIGHT_UNKNOWN = -1;

    static final long DEFAULT_DURATION = 300;

    static final int DEFAULT_INNER_HEIGHT = LayoutParams.MATCH_PARENT;
    static final int DEFAULT_INNER_WIDTH = LayoutParams.MATCH_PARENT;
    static final int DEFAULT_OUTER_HEIGHT = 0;
    static final float DEFAULT_OUTER_WEIGHT = 1;

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
    private boolean mAnimate = true;
    @Nullable
    private AnimationManager mAnimationManager = null;

    public AccordionView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setAnimationManager(new AnimatorAnimationManager());
        }
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

    public void setAnimationManager(@Nullable final AnimationManager manager) {
        mAnimationManager = manager;
    }

    @Nullable
    public AnimationManager getAnimationManager() {
        return mAnimationManager;
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
            showSection(mSections[0], false);
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
        contentInner.getLayoutParams().height = DEFAULT_INNER_HEIGHT;
        contentInner.getLayoutParams().width = DEFAULT_INNER_WIDTH;

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
        contentLp.height = DEFAULT_OUTER_HEIGHT;
        contentLp.weight = DEFAULT_OUTER_WEIGHT;
        holder.mContentOuter.setLayoutParams(contentLp);

        // set header OnClick handler
        holder.mHeader.setOnClickListener(mHeaderOnClick);

        // close section by default
        holder.changeState(STATE_CLOSED);

        // return new ViewHolder
        return holder;
    }

    void showSection(@NonNull final ViewHolder section, final boolean animate) {
        // short-circuit if the specified section is already active
        if (mActiveSection == section) {
            return;
        }

        // start closing the previous active section
        if (mActiveSection != null) {
            // update last known height if the section was OPEN
            if (mActiveSection.mState == STATE_OPEN) {
                mActiveSection.mLastKnownHeight = mActiveSection.mContentOuter.getHeight();
            }

            // mark this section as closing
            mActiveSection.changeState(STATE_CLOSING);
            mActiveSection = null;
        }

        // mark the specified section as opening
        section.changeState(STATE_OPENING);
        mActiveSection = section;

        // generate a list of all closing sections
        final List<ViewHolder> closing = new ArrayList<>();
        for (final ViewHolder holder : mSections) {
            if (holder.mState == STATE_CLOSING) {
                closing.add(holder);
            }
        }

        // handle animation
        boolean animating = false;
        if (animate && mAnimate && mAnimationManager != null) {
            // determine requested height of opening section
            if (mActiveSection.mLastKnownHeight == HEIGHT_UNKNOWN) {
                // backup the height of the opening section
                final int height = mActiveSection.mContentOuter.getLayoutParams().height;

                // set everything to it's final visibility state
                for (final ViewHolder holder : closing) {
                    holder.mContentOuter.setVisibility(GONE);
                }
                mActiveSection.mContentOuter.setVisibility(VISIBLE);
                mActiveSection.mContentOuter.getLayoutParams().height = DEFAULT_OUTER_HEIGHT;
                ((LinearLayout.LayoutParams) mActiveSection.mContentOuter.getLayoutParams()).weight =
                        DEFAULT_OUTER_WEIGHT;

                // trigger a measurement from this views parent (if possible, otherwise use this view)
                final ViewParent parent = getParent();
                final View view = parent instanceof View ? (View) parent : this;
                view.measure(MeasureSpec.makeMeasureSpec(view.getMeasuredWidth(), MeasureSpec.EXACTLY),
                             MeasureSpec.makeMeasureSpec(view.getMeasuredHeight(), MeasureSpec.EXACTLY));

                // initialize the last known height to the measured height
                mActiveSection.mLastKnownHeight = mActiveSection.mContentOuter.getMeasuredHeight();

                // reset visibility of closing sections
                for (final ViewHolder holder : closing) {
                    holder.mContentOuter.setVisibility(VISIBLE);
                }

                // reset the state of the opening section
                mActiveSection.mContentOuter.getLayoutParams().height = height;
                mActiveSection.changeState(STATE_OPENING);
            }

            // animate the state transition
            animating = mAnimationManager.animate(mActiveSection, closing.toArray(new ViewHolder[closing.size()]));
        }

        // just update state (not animating)
        if (!animating) {
            mActiveSection.changeState(STATE_OPEN);

            // mark all closing sections as closed
            for (final ViewHolder holder : closing) {
                holder.changeState(STATE_CLOSED);
            }
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
            super((MarginLayoutParams) source);
            this.weight = source.weight;
            this.gravity = source.gravity;
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
        int mLastKnownHeight = HEIGHT_UNKNOWN;

        void changeState(@State final int state) {
            final ViewGroup.LayoutParams innerLp = mContentInner.getLayoutParams();
            final LinearLayout.LayoutParams outerLp = (LinearLayout.LayoutParams) mContentOuter.getLayoutParams();
            switch (state) {
                case STATE_OPEN:
                    mContentOuter.setVisibility(VISIBLE);
                    innerLp.height = DEFAULT_INNER_HEIGHT;
                    outerLp.height = DEFAULT_OUTER_HEIGHT;
                    outerLp.weight = DEFAULT_OUTER_WEIGHT;
                    break;
                case STATE_CLOSING:
                    mContentOuter.setVisibility(VISIBLE);
                    innerLp.height = mLastKnownHeight;
                    switch (mState) {
                        case STATE_CLOSED:
                            throw new IllegalStateException(
                                    "Accordion section cannot transition from CLOSED to CLOSING");
                        case STATE_OPEN:
                            outerLp.height = mLastKnownHeight;
                            break;
                    }
                    outerLp.weight = 0;
                    break;
                case STATE_CLOSED:
                    mContentOuter.setVisibility(GONE);
                    innerLp.height = DEFAULT_INNER_HEIGHT;
                    outerLp.height = DEFAULT_OUTER_HEIGHT;
                    outerLp.weight = DEFAULT_OUTER_WEIGHT;
                    break;
                case STATE_OPENING:
                    mContentOuter.setVisibility(VISIBLE);
                    innerLp.height = mLastKnownHeight;
                    switch (mState) {
                        case STATE_OPEN:
                            throw new IllegalStateException("Accordion section cannot transition from OPEN to OPENING");
                        case STATE_CLOSED:
                            outerLp.height = 0;
                            break;
                    }
                    outerLp.weight = 0;
                    break;
            }

            // set updated LayoutParams, this triggers a fresh layout
            mContentInner.setLayoutParams(innerLp);
            mContentOuter.setLayoutParams(outerLp);

            // update the state
            mState = state;
        }
    }

    private class AccordionHeaderOnClickListener implements OnClickListener {
        @Override
        public void onClick(final View v) {
            final ViewGroup.LayoutParams lp = v.getLayoutParams();
            if (lp instanceof LayoutParams) {
                final ViewHolder section = ((LayoutParams) lp).mHolder;
                if (section != null) {
                    showSection(section, true);
                }
            }
        }
    }

    public interface AnimationManager {
        boolean animate(@NonNull ViewHolder opening, @NonNull ViewHolder... closing);

        void setDuration(long duration);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AnimatorAnimationManager implements AnimationManager {
        @Nullable
        private Animator mCurrentAnimation;

        @Nullable
        private TimeInterpolator mInterpolator = new DecelerateInterpolator();
        private long mDuration = DEFAULT_DURATION;

        @Override
        public boolean animate(@NonNull final ViewHolder opening, @NonNull final ViewHolder... closing) {
            if (mCurrentAnimation != null) {
                mCurrentAnimation.cancel();
            }

            // build the animation
            final Animator animation = buildAnimation(opening, closing);

            // listen for completion to reset the current animation
            animation.addListener(new SimpleAnimatorListener() {
                @Override
                public void onAnimationEnd(@NonNull final Animator animation) {
                    if (animation == mCurrentAnimation) {
                        mCurrentAnimation = null;
                    }
                }
            });
            mCurrentAnimation = animation;

            // start the animation
            animation.start();

            return true;
        }

        public final void setDuration(final long duration) {
            mDuration = duration;
        }

        public void setInterpolator(@Nullable final TimeInterpolator interpolator) {
            mInterpolator = interpolator;
        }

        @NonNull
        protected Animator buildAnimation(@NonNull final ViewHolder opening, @NonNull final ViewHolder... closing) {
            final AnimatorSet animation = new AnimatorSet();
            animation.play(getOpeningAnimator(opening));
            for (final ViewHolder holder : closing) {
                animation.play(getClosingAnimator(holder));
            }
            animation.setDuration(mDuration);
            animation.setInterpolator(mInterpolator);
            return animation;
        }

        @NonNull
        protected Animator getOpeningAnimator(@NonNull final ViewHolder holder) {
            final ValueAnimator animator =
                    ValueAnimator.ofInt(holder.mContentOuter.getLayoutParams().height, holder.mLastKnownHeight);
            final DefaultOpeningAnimatorListener listener = new DefaultOpeningAnimatorListener(holder);
            animator.addListener(listener);
            animator.addUpdateListener(listener);
            return animator;
        }

        @NonNull
        protected Animator getClosingAnimator(@NonNull final ViewHolder holder) {
            final ValueAnimator animator = ValueAnimator.ofInt(holder.mContentOuter.getLayoutParams().height, 0);
            final DefaultClosingAnimatorListener listener = new DefaultClosingAnimatorListener(holder);
            animator.addListener(listener);
            animator.addUpdateListener(listener);
            return animator;
        }

        public abstract static class AnimatorListener extends SimpleAnimatorListener {
            @NonNull
            protected final ViewHolder mHolder;
            @State
            private final int mTargetState;
            private boolean mCanceled = false;

            protected AnimatorListener(@NonNull final ViewHolder holder, @State final int targetState) {
                mHolder = holder;
                mTargetState = targetState;
            }

            @Override
            public void onAnimationCancel(@NonNull final Animator animation) {
                mCanceled = true;
            }

            @Override
            @SuppressLint("WrongConstant") // workaround https://code.google.com/p/android/issues/detail?id=182179
            public void onAnimationEnd(@NonNull final Animator animation) {
                if (!mCanceled) {
                    mHolder.changeState(mTargetState);
                }
            }
        }

        private static final class DefaultOpeningAnimatorListener extends AnimatorListener
                implements ValueAnimator.AnimatorUpdateListener {
            public DefaultOpeningAnimatorListener(@NonNull final ViewHolder holder) {
                super(holder, STATE_OPEN);
            }

            @Override
            public void onAnimationUpdate(@NonNull final ValueAnimator animation) {
                final Object value = animation.getAnimatedValue();
                if (value instanceof Integer) {
                    mHolder.mContentOuter.getLayoutParams().height = (Integer) value;
                    mHolder.mContentOuter.requestLayout();
                }
            }
        }

        private static final class DefaultClosingAnimatorListener extends AnimatorListener
                implements ValueAnimator.AnimatorUpdateListener {
            public DefaultClosingAnimatorListener(@NonNull final ViewHolder holder) {
                super(holder, STATE_CLOSED);
            }

            @Override
            public void onAnimationUpdate(@NonNull final ValueAnimator animation) {
                final Object value = animation.getAnimatedValue();
                if (value instanceof Integer) {
                    mHolder.mContentOuter.getLayoutParams().height = (Integer) value;
                    mHolder.mContentOuter.requestLayout();
                }
            }
        }
    }
}
