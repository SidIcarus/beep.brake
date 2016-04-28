package edu.rit.se.beepbrake.fragments;

import android.support.v4.app.FragmentManager;

import android.support.annotation.IntDef;
import android.util.SparseIntArray;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import edu.rit.se.beepbrake.R;

public class FragmentDirector {

    @IntDef({AppFragments.FIRST_RUN, AppFragments.EULA, AppFragments.CAMERA})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AppFragments {
        int FIRST_RUN = 0, EULA = 1, CAMERA = 2;

        // Keeping the relation of fragments to ID's in one place
        SparseIntArray fragmentID = new SparseIntArray() {{
            append(FIRST_RUN, 0);
            append(EULA, R.id.fragment_eula);
            append(CAMERA, 0);
        }};
    }

    private FragmentManager fManager;

    @AppFragments private int currentFragment;

    public FragmentDirector() { }

    public FragmentDirector newInstance(FragmentManager fManager) {
        this.fManager = fManager;

        this.currentFragment = AppFragments.FIRST_RUN;

        return new FragmentDirector();
    }

    protected void displayFragment(boolean hideFragment, @AppFragments Integer newFragment) {
        int fragmentID = AppFragments.fragmentID.get(newFragment);
    }

    public int getCurrentFragmentID() { return AppFragments.fragmentID.get(currentFragment); }

}
