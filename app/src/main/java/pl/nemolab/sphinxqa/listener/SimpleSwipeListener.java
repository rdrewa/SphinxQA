package pl.nemolab.sphinxqa.listener;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by senator on 2015-11-29.
 */
public abstract class SimpleSwipeListener implements View.OnTouchListener {

    private static final int MIN_DISTANCE = 100;
    private static final int CLICK_ACTION_THRESHOLD = 100;
    private static final int LONG_CLICK_ACTION_THRESHOLD = 1000;
    private long lastTouchDown;
    private float downX, downY;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        long currentTouchDown = System.currentTimeMillis();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                downY = event.getY();
                lastTouchDown = currentTouchDown;
                return true;
            }
            case MotionEvent.ACTION_UP: {
                long deltaTouchDown = currentTouchDown - lastTouchDown;
                if (deltaTouchDown < CLICK_ACTION_THRESHOLD) {
                    onClick(v);
                    return true;
                }


                float deltaX = downX - event.getX();
                float deltaY = downY - event.getY();

                //HORIZONTAL SCROLL
                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    if (Math.abs(deltaX) > MIN_DISTANCE) {
                        // left or right
                        if (deltaX < 0) {
                            onSwipeRight();
                            return true;
                        }
                        if (deltaX > 0) {
                            onSwipeLeft();
                            return true;
                        }
                    } else {
                        //not long enough swipe...
                        if (deltaTouchDown < LONG_CLICK_ACTION_THRESHOLD) {
                            onLongClick(v);
                            return true;
                        }
                        return false;
                    }
                }
                //VERTICAL SCROLL
                else {
                    if (Math.abs(deltaY) > MIN_DISTANCE) {
                        // top or down
                        if (deltaY < 0) {
                            onSwipeBottom();
                            return true;
                        }
                        if (deltaY > 0) {
                            onSwipeTop();
                            return true;
                        }
                    } else {
                        if (deltaTouchDown < LONG_CLICK_ACTION_THRESHOLD) {
                            onLongClick(v);
                            return true;
                        }
                        //not long enough swipe...
                        return false;
                    }
                }

                return true;
            }
        }
        return false;
    }

    public abstract void onSwipeTop();

    public abstract void onSwipeBottom();

    public abstract void onSwipeLeft();

    public abstract void onSwipeRight();

    public abstract void onClick(View v);

    protected abstract void onLongClick(View v);
}
