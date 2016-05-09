package org.ccci.gto.android.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class StateAwareVideoView extends VideoView {
    interface PlaybackListener {
        void onStart(StateAwareVideoView view);

        void onPause(StateAwareVideoView view);

        void onResume(StateAwareVideoView view);

        void onSeek(StateAwareVideoView view, int msec);

        void onStop(StateAwareVideoView view);
    }

    private PlaybackListener mPlaybackListener = null;

    public StateAwareVideoView(final Context context) {
        super(context);
    }

    public StateAwareVideoView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public StateAwareVideoView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setPlaybackListener(final PlaybackListener listener) {
        mPlaybackListener = listener;
    }

    @Override
    public void start() {
        super.start();
        if (mPlaybackListener != null) {
            mPlaybackListener.onStart(this);
        }
    }

    @Override
    public void pause() {
        super.pause();
        if (mPlaybackListener != null) {
            mPlaybackListener.onPause(this);
        }
    }

    @Override
    public void resume() {
        super.resume();
        if (mPlaybackListener != null) {
            mPlaybackListener.onResume(this);
        }
    }

    @Override
    public void seekTo(final int msec) {
        super.seekTo(msec);
        if (mPlaybackListener != null) {
            mPlaybackListener.onSeek(this, msec);
        }
    }

    @Override
    public void stopPlayback() {
        super.stopPlayback();
        if (mPlaybackListener != null) {
            mPlaybackListener.onStop(this);
        }
    }

    public abstract static class SimplePlaybackListener implements PlaybackListener {
        @Override
        public void onStart(final StateAwareVideoView view) {
        }

        @Override
        public void onPause(final StateAwareVideoView view) {
        }

        @Override
        public void onResume(final StateAwareVideoView view) {
        }

        @Override
        public void onSeek(final StateAwareVideoView view, final int msec) {
        }

        @Override
        public void onStop(final StateAwareVideoView view) {
        }
    }
}
