package com.refugeye.widget;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.support.annotation.Nullable;

import com.refugeye.R;
import com.refugeye.helper.DimensionHelper;

public class SwipeView extends LinearLayout {

    public interface OnOpenedChangeListener {
        void onOpenedChange(boolean opened);
    }

    private boolean opened = true;
    private ImageView toggle;

    @Nullable
    private OnOpenedChangeListener onOpenedChangeListener;

    public void setOnOpenedChangeListener(@Nullable OnOpenedChangeListener listener) {
        onOpenedChangeListener = listener;
    }

    public SwipeView(Context context) {
        super(context);
    }

    public SwipeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        toggle = findViewById(R.id.toggle_drawer);
        toggle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });
    }

    public void toggle() {
        opened = !opened;
        if (!opened) {
            this.animateOpening(true, true);
        } else {
            this.animateOpening(false, true);
        }
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
        if (!opened) {
            this.animateOpening(true, false);
        } else {
            this.animateOpening(false, false);
        }
    }

    /**
     * Animate opening/closing
     *
     * @param opening boolean
     * @param animate boolean
     */
    private void animateOpening(boolean opening, boolean animate) {

        int duration = animate ? 300 : 0;

        int translate = DimensionHelper.dpToPx(getContext(), opening ? 220 : 0);
        int handleRotation = opening ? 180 : 0;

        int orientation = getOrientation();
        ViewPropertyAnimator animator = animate();
        switch (orientation) {
            case HORIZONTAL:
                animator = animate().translationX(translate).setDuration(duration).setInterpolator(new AccelerateDecelerateInterpolator());
                toggle.animate().rotation(handleRotation).setDuration(duration).start();
                break;
            case VERTICAL:
                handleRotation += 90;
                animator = animate().translationY(translate).setDuration(duration).setInterpolator(new AccelerateDecelerateInterpolator());
                toggle.animate().rotation(handleRotation).setDuration(duration).start();
                break;
        }
        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}
            @Override
            public void onAnimationCancel(Animator animator) {}
            @Override
            public void onAnimationRepeat(Animator animator) {}
            @Override
            public void onAnimationEnd(Animator animator) {
                if (onOpenedChangeListener != null) {
                    onOpenedChangeListener.onOpenedChange(opened);
                }
            }
        }).start();
    }
}
