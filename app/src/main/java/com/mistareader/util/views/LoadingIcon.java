package com.mistareader.util.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.MenuItem;

public class LoadingIcon extends AnimatorListenerAdapter {
    private ObjectAnimator loadingAnimation;
    private boolean        showProgress;
    private MenuItem       menuItem;

    public LoadingIcon() {
    }

    public void init(MenuItem menuReload, boolean show) {
        menuItem = menuReload;
        showProgress = show;
        loadingAnimation = ObjectAnimator.ofInt(menuItem.getIcon(), "level", 0, 10000);
        loadingAnimation.setDuration(500);
        loadingAnimation.setRepeatCount(ValueAnimator.INFINITE);
        loadingAnimation.addListener(this);
        if (showProgress) {
            loadingAnimation.start();
        }
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
        if (!showProgress) {
            loadingAnimation.setRepeatCount(0);
        }
    }

    public void showProgress() {
        if (menuItem != null) {
            menuItem.setEnabled(false);
        }

        showProgress = true;
        if (loadingAnimation != null) {
            loadingAnimation.setRepeatCount(ValueAnimator.INFINITE);
            loadingAnimation.start();
        }
    }

    public void hideProgress() {
        if (menuItem != null) {
            menuItem.setEnabled(true);
        }
        showProgress = false;
    }
}
