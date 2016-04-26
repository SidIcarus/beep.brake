package edu.rit.se.beepbrake.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import edu.rit.se.beepbrake.R;
import edu.rit.se.beepbrake.utils.Utilities;
import edu.rit.se.beepbrake.fragments.FragmentDirector;

public class MainActivity2 extends AppCompatActivity {

    public FragmentDirector fDirector;
    public static Utilities utilities;
    boolean showFAB = true;

    private int mDayNightMode = AppCompatDelegate.MODE_NIGHT_AUTO;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fDirector = new FragmentDirector().newInstance(getSupportFragmentManager());

        Utilities.hideStatusBar(getWindow());

        setContentView(R.layout.activity_main);

        initBottomSheet();
    }

    @Override protected void onResume() {
        super.onResume();

        Utilities.hideStatusBar(getWindow());
        //        utilities.resumeAnimatable();
        //        utilities.resumeNightMode(getResources());
    }

    public void initBottomSheet() {
        // Bottom Sheet

        // To handle FAB animation upon entrance and exit
        final Animation growAnimation = AnimationUtils.loadAnimation(this, R.anim.simple_grow);
        final Animation shrinkAnimation = AnimationUtils.loadAnimation(this, R.anim.simple_shrink);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.gmail_fab);

        fab.setVisibility(View.VISIBLE);
        fab.startAnimation(growAnimation);

        shrinkAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) { }

            @Override public void onAnimationEnd(Animation animation) {
                fab.setVisibility(View.GONE);
            }

            @Override public void onAnimationRepeat(Animation animation) { }
        });

        CoordinatorLayout coordinatorLayout =
                (CoordinatorLayout) findViewById(R.id.grand_poobah);
        View bottomSheet = coordinatorLayout.findViewById(R.id.g_bottom_sheet);

        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);

        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override public void onStateChanged(@NonNull View bottomSheet, int newState) {

                switch (newState) {

                    case BottomSheetBehavior.STATE_DRAGGING:
                        if (showFAB) fab.startAnimation(shrinkAnimation);
                        break;

                    case BottomSheetBehavior.STATE_COLLAPSED:
                        showFAB = true;
                        fab.setVisibility(View.VISIBLE);
                        fab.startAnimation(growAnimation);
                        break;

                    case BottomSheetBehavior.STATE_EXPANDED:
                        showFAB = false;
                        break;
                }
            }

            @Override public void onSlide(View bottomSheet, float slideOffset) { }
        });
    }

        //
        //    @Override
        //    public boolean onCreateOptionsMenu(Menu menu) {
        //        getMenuInflater().inflate(R.menu.main, menu);
        //        return true;
        //    }

        //    @Override
        //    public boolean onOptionsItemSelected(MenuItem item) {
        //        int id = item.getItemId();
        //        if (id == R.id.action_day_night_yes) {
        //            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        //            recreate();
        //            return true;
        //        } else if (id == R.id.action_day_night_no) {
        //            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //            recreate();
        //            return true;
        //        } else {
        //            if (id == R.id.action_bottom_sheet_dialog) {
        //                BottomSheetDialogView.show(this, mDayNightMode);
        //                return true;
        //            }
        //        }
        //        return super.onOptionsItemSelected(item);
        //    }
        }
