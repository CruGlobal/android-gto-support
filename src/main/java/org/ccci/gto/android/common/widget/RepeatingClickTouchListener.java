package org.ccci.gto.android.common.widget;

import android.view.MotionEvent;
import android.view.View;

public class RepeatingClickTouchListener implements View.OnTouchListener {
    private long initialDelay = 500;
    private long repeatingDelay = 100;

    private Runnable task;

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        int action = event.getAction();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //Just to be sure that we removed all callbacks,
                // which should have occurred in the ACTION_UP
                v.removeCallbacks(this.task);

                //Perform the default click action.
                v.performClick();

                // create a runnable task for the view
                this.createTask(v);

                //Schedule the start of repetitions after the initial delay.
                v.postDelayed(this.task, this.initialDelay);

                return true;
            case MotionEvent.ACTION_UP:
                //Cancel any repetition in progress.
                v.removeCallbacks(this.task);

                // reset task
                this.task = null;

                return true;
        }

        // we didn't consume the event, so let other handlers possible take it
        return false;
    }

    private void createTask(final View v) {
        this.task = new Runnable() {
            @Override
            public void run() {
                // Perform the present repetition of the click action provided by the user
                // in setOnClickListener().
                v.performClick();

                // Schedule the next repetitions of the click action, using a faster repeat
                // interval than the initial repeat delay interval.
                v.postDelayed(task, repeatingDelay);
            }
        };
    }
}
