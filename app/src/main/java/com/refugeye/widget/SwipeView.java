package com.refugeye.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.refugeye.R;

public class SwipeView extends LinearLayout {

    private static final String TAG = "SwipeView";

    private static final String STATE_OPEN = "open";

    private boolean opened = true;
    private ImageView toggle;

    public SwipeView(Context context) {
        super(context);
    }

    public SwipeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void onPause() {
        SharedPreferences shared = getContext().getSharedPreferences(TAG, Context.MODE_PRIVATE);
        shared.edit().putBoolean(STATE_OPEN, opened).apply();
    }

    public void onResume() {
        SharedPreferences shared = getContext().getSharedPreferences(TAG, Context.MODE_PRIVATE);
        if (shared.getBoolean(STATE_OPEN, true)) {
            open(0);
        } else {
            close(0);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setSaveEnabled(true);
        toggle = findViewById(R.id.toggle_drawer);
        toggle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });
    }

    public void open() {
        open(300);
    }

    public void open(long duration) {
        opened = true;

        int orientation = getOrientation();
        switch (orientation) {
            case HORIZONTAL:
                animate().translationX(dpToPx(0)).setDuration(duration).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                toggle.animate().rotationBy(180).setDuration(duration).start();
                break;
            case VERTICAL:
                animate().translationY(dpToPx(0)).setDuration(duration).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                toggle.animate().rotationBy(180).setDuration(duration).start();
                break;
        }
    }

    public void close() {
        close(300);
    }

    public void close(long duration) {
        opened = false;

        int orientation = getOrientation();
        switch (orientation) {
            case HORIZONTAL:
                animate().translationX(dpToPx(220)).setDuration(duration).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                toggle.animate().rotationBy(180).setDuration(duration).start();
                break;
            case VERTICAL:
                animate().translationY(dpToPx(220)).setDuration(duration).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                toggle.animate().rotationBy(180).setDuration(duration).start();
                break;
        }
    }

    public void toggle() {
        if (opened) {
            close();
            return;
        }
        open();
    }


    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
