
package com.plymouthsoftware.android.commons.os;

import android.os.CountDownTimer;

public abstract class ResumableCountDownTimer {

    private long startTime;
    private long splitTime;
    private long millisRemaining;
    private long elapsed;
    private long splitElapsed;
    private long updateEveryMillis;

    private boolean inProgress;
    private boolean isPaused;

    private CountDownTimer countDownTimer;

    public ResumableCountDownTimer(long updateEveryMillis) {
        inProgress = false;
        isPaused = false;
        
        this.updateEveryMillis = updateEveryMillis;
    }

    public void start(long millis, boolean restart) {
        if (inProgress && !isPaused)
            return;

        splitElapsed = 0;

        if (restart) {
            startTime = System.currentTimeMillis();
            splitTime = startTime;
            elapsed = 0;
        }
        else
            splitTime = System.currentTimeMillis();

        countDownTimer = new CountDownTimer(millis, updateEveryMillis) { // Update every second

            @Override
            public void onTick(long millisUntilFinished) {
                millisRemaining = millisUntilFinished;

                long now = System.currentTimeMillis();
                splitElapsed = now - splitTime;

                ResumableCountDownTimer.this.onTick(elapsed + splitElapsed, millisUntilFinished);
            }

            @Override
            public void onFinish() {
                ResumableCountDownTimer.this.onFinish();
                inProgress = false;
            }
        };

        countDownTimer.start();
        inProgress = true;
    }

    public void pause() {
        if (!inProgress || countDownTimer == null)
            return;

        countDownTimer.cancel();
        countDownTimer = null;

        elapsed += splitElapsed;

        isPaused = true;
    }

    public void resume() {

        if (millisRemaining <= 1) {
            isPaused = false;
            return;
        }

        splitTime = System.currentTimeMillis();
        splitElapsed = 0;

        start(millisRemaining, false);

        isPaused = false;

    }

    public abstract void onTick(long millisElapsed, long millisRemaining);

    public abstract void onFinish();

    public void toggle() {
        if (isPaused)
            resume();
        else
            pause();
    }

    public boolean inProgress() {
        return inProgress;
    }
}
