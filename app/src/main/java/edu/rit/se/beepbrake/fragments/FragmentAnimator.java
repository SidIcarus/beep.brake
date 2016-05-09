package edu.rit.se.beepbrake.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.res.Resources;
import android.support.annotation.IntDef;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import edu.rit.se.beepbrake.R;

public class FragmentAnimator {

    /** The flag indicating whether content is loaded (text shown) or not (loading spinner shown) */
    private boolean mContentLoaded;
    /** The view (or view group) containing the content. This is one of two overlapping views. */
    private View mNewView;
    /** */
    private View mCurrentView;
    /** The view containing the loading indicator. This is the other of two overlapping views. */
    private View mLoadingView;
    /**
     * The system "short" animation time duration, in milliseconds. This duration is ideal for
     * subtle animations or animations that occur very frequently.
     */
    private int mShortAnimationDuration, mMediumAnimationDuration, mLongAnimationDuration,
            mAnimationDuration;

    public FragmentAnimator() {}

    /** Cross-fades between {@link #mNewView} and {@link #mLoadingView}. */
    public void crossfadeFragments(View newView, View currentView) {
        final View showView = newView;
        final View hideView = currentView;

        // Set the "show" view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        showView.setAlpha(0f);
        showView.setVisibility(View.VISIBLE);

        // Animate the "show" view to 100% opacity, and clear any animation listener set on
        // the view. Remember that listeners are not limited to the specific animation
        // describes in the chained method calls. Listeners are set on the
        // ViewPropertyAnimator object for the view, which persists across several
        // animations.
        showView.animate().alpha(1f).setDuration(mShortAnimationDuration).setListener(null);

        // Animate the "hide" view to 0% opacity. After the animation ends, set its visibility
        // to GONE as an optimization step (it won't participate in layout passes, etc.)
        hideView.animate().alpha(0f).setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override public void onAnimationEnd(Animator animation) {
                        hideView.setVisibility(View.GONE);
                    }
                });
    }

    public FragmentAnimator newInstance(Resources res) {

        // Retrieve and cache the system's default "short", "medium", & "long" animation times.
        this.mShortAnimationDuration = res.getInteger(android.R.integer.config_shortAnimTime);
        this.mMediumAnimationDuration = res.getInteger(android.R.integer.config_mediumAnimTime);
        this.mLongAnimationDuration = res.getInteger(android.R.integer.config_longAnimTime);

        this.mAnimationDuration = mShortAnimationDuration;

        return new FragmentAnimator();
    }

    private void setCurrentView(View currentView) { this.mCurrentView = currentView; }

    @IntDef(flag = true, value = {
            AppAnimations.CROSS_FADE, AppAnimations.SLIDE, AppAnimations.ZOOM,
            AppAnimations.CARD_FLIP, AppAnimations.LAYOUT_CHANGE})
    @Retention(RetentionPolicy.SOURCE) public @interface AppAnimations {
        int CROSS_FADE = 0, SLIDE = 1, ZOOM = 2, CARD_FLIP = 3, LAYOUT_CHANGE = 4;
        int LEFT = 5, RIGHT = 6, IN = 7, OUT = 8;
    }

    @IntDef({CrossfadeSpeed.SHORT, CrossfadeSpeed.MEDIUM, CrossfadeSpeed.LONG})
    @Retention(RetentionPolicy.SOURCE) public @interface CrossfadeSpeed {
        int SHORT = 0, MEDIUM = 1, LONG = 2;
    }
}
