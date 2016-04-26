package edu.rit.se.beepbrake.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.rit.se.beepbrake.R;

// Fragment Lifecycle = (FL)
public class SampleFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;

    // Empty constructor is required for DialogFragment
    // Make sure not to add arguments to the constructor
    // Use `newInstance` instead as shown below
    public SampleFragment() { }

    /**
     * @return A new instance of fragment SampleFragment.
     */
    public static SampleFragment newInstance(String title) {
        SampleFragment fragment = new SampleFragment();
        Bundle args = new Bundle();
        args.putString("title", title.toString());
        fragment.setArguments(args);
        return fragment;
    }

    // FL 1.0: Created
    // Called when a fragment is connected to an activity.
    // But Doesn't mean activity is fully initialized.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement " +
                    "OnFragmentInteractionListener");
        }
    }

    // FL 1.1: Created
    // Called to do initial creation and recreation of the fragment.
    // Doesn't require the activity to be fully created.
    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    // FL 1.2: Created
    // Called by Android once the Fragment should create its view object hierarchy, either
    // dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sample, container, false);
    }

    // FL 1.3: Created
    // Called soon after onCreateView() & ensures that the fragment's root view is non-null. Any
    // view setup should happen here. E.g., view object handles, attaching listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {    }

    // FL 1.5: Created
    // Called when host activity has completed its onCreate() method and only upon fragment creation
    // & reattachment, not for a restart.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    // FL 1.6: Created
    // Only called upon fragment creation and reattachment, not restart
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    // FL 2.0: Started
    // Called once the fragment is ready to be displayed on screen.
    @Override
    public void onStart() { super.onStart(); }

    // FL 3.0: Resumed
    // Allocate “expensive” resources such as registering for location, sensor updates, etc.
    @Override
    public void onResume() { super.onResume(); }

    // FL 4.0: Paused
    // Release “expensive” resources. Commit any changes.
    @Override
    public void onPause() { super.onPause(); }

    // FL 5.0: Paused
    // This method may be called at anytime before onDestroy()
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    // FL 6.0: Stopped
    @Override
    public void onStop() { super.onStop(); }

    // FL 7.0: Destroyed
    // Called when fragment's view is being destroyed, but the fragment is still kept around.
    @Override
    public void onDestroyView() { super.onDestroyView(); }

    // FL 7.1: Destroyed
    // Called when fragment is no longer in use. Either explicitly by the user or by the system when
    //  it requires memory and realizes this is not in use.
    @Override
    public void onDestroy() { super.onDestroy(); }

    // FL 7.2: Destroyed
    // Called when fragment is no longer connected to the activity.
    @Override
    public void onDetach() { // Will be used for EULA.
        super.onDetach();
        mListener = null;
    }

    // Simply returns the boolean of there decision to accept the EULA
    @Override
    public void onClick(View v) {
    }

    //
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Boolean hasAccepted);
    }
}
