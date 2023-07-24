package org.ccci.gto.android.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import org.ccci.gto.android.common.R;

import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.UNSPECIFIED;
import static android.view.View.MeasureSpec.makeMeasureSpec;

public class RatioLayout extends FrameLayout {
    private float mAspectRatio = 0.0f;

    public RatioLayout(Context context) {
        super(context);
    }

    public RatioLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RatioLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public RatioLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(final AttributeSet attrs) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RatioLayout);
        final float width = a.getFloat(R.styleable.RatioLayout_aspectRatioWidth, 0.0f);
        final float height = a.getFloat(R.styleable.RatioLayout_aspectRatioHeight, 0.0f);
        if (height > 0.0f && width > 0.0f) {
            mAspectRatio = width / height;
        } else {
            mAspectRatio = a.getFloat(R.styleable.RatioLayout_aspectRatio, 0.0f);
        }
        a.recycle();
    }

    @Override
    @SuppressWarnings("checkstyle:RightCurly")
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        if (mAspectRatio > 0.0f) {
            // get the measurement spec components
            final int hMode = MeasureSpec.getMode(heightMeasureSpec);
            final int h = MeasureSpec.getSize(heightMeasureSpec);
            final int wMode = MeasureSpec.getMode(widthMeasureSpec);
            final int w = MeasureSpec.getSize(widthMeasureSpec);

            // calculate the scaled height and width based on the aspect ratio
            int hScaled = Math.max((int) (w / mAspectRatio), getSuggestedMinimumHeight());
            int wScaled = Math.max((int) (h * mAspectRatio), getSuggestedMinimumWidth());

            // scale based off static width, we still constrain measurements into the MeasureSpec
            if (wMode == EXACTLY && hMode != EXACTLY) {
                // unless this is unspecified we should constrain the scaled height
                if (hMode != UNSPECIFIED) {
                    hScaled = Math.min(hScaled, h);
                }

                super.onMeasure(widthMeasureSpec, makeMeasureSpec(hScaled, EXACTLY));
                return;
            }
            // scale based off static height
            else if (hMode == EXACTLY && wMode != EXACTLY) {
                // unless this is unspecified we should constrain the scaled width
                if (wMode != UNSPECIFIED) {
                    wScaled = Math.min(wScaled, w);
                }

                super.onMeasure(makeMeasureSpec(wScaled, EXACTLY), heightMeasureSpec);
                return;
            }
            // scale view to fit available space since no explicit sizes were specified
            else if (hMode == AT_MOST || wMode == AT_MOST) {
                // width is unspecified or larger than calculated width, use height as base
                if (wMode == UNSPECIFIED || w >= wScaled) {
                    super.onMeasure(makeMeasureSpec(wScaled, EXACTLY), makeMeasureSpec(h, EXACTLY));
                    return;
                }
                // height is unspecified or larger than calculated height, use width as base
                else if (hMode == UNSPECIFIED || h >= hScaled) {
                    super.onMeasure(makeMeasureSpec(w, EXACTLY), makeMeasureSpec(hScaled, EXACTLY));
                    return;
                }
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
