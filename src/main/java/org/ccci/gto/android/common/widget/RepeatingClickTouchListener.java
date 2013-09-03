package org.ccci.gto.android.common.widget;

import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

public class RepeatingClickTouchListener implements View.OnTouchListener {
    private long initialDelay = 500;
    private long repeatingDelay = 100;

    private Pair<View, Runnable> task;
    private boolean fired = false;

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // reset any currently running task
                this.resetTask();

                // create a runnable task for the view
                this.task = this.createTask(v);
                this.fired = false;

                // Schedule the start of repetitions after the initial delay.
                v.postDelayed(this.task.second, this.initialDelay);

                return false;
            case MotionEvent.ACTION_UP:
                // should we consume this event?
                final boolean consumed = this.fired;

                // reset any currently running task
                this.resetTask();

                // did we consume the event?
                return consumed;
            case MotionEvent.ACTION_CANCEL:
                // reset any currently running task
                this.resetTask();

                // never consume cancel events
                return false;
        }

        // we didn't handle the event, so don't consume it
        return false;
    }

    private Pair<View,Runnable> createTask(final View v) {
        return Pair.create(v, (Runnable) new Runnable() {
            @Override
            public void run() {
                // Perform the present repetition of the click action provided by the user
                // in setOnClickListener().
                if(v.isPressed()) {
                    v.performClick();
                    fired = true;
                }

                // Schedule the next repetitions of the click action, using a faster repeat
                // interval than the initial repeat delay interval.
                v.postDelayed(this, repeatingDelay);
            }
        });
    }

    public void resetTask() {
        if(this.task != null) {
            //Cancel any repetition in progress.
            this.task.first.removeCallbacks(this.task.second);

            // clear the task
            this.task = null;
        }
    }
}
