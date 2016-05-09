package edu.rit.se.beepbrake.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.CheckedTextView;

import edu.rit.se.beepbrake.R;

public class FirstRunFragment extends Fragment implements View.OnClickListener {
    private CheckedTextView ctv;

    public FirstRunFragment() { }

    public static FirstRunFragment newInstance() {

        return new FirstRunFragment();
    }

    // FL 1.0: Created
    // Called when a fragment is connected to an activity.
    // But Doesn't mean activity is fully initialized.
    @Override public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override public void onClick(View v) {

    }

    // FL 1.3: Created
    // Called soon after onCreateView() & ensures that the fragment's root view is non-null. Any
    // view setup should happen here. E.g., view object handles, attaching listeners.
    @Override public void onViewCreated(View view, Bundle savedInstanceState) {

        ctv = (CheckedTextView) getActivity().findViewById(R.id.firstrun_checkbox);

        ctv.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { ctv.setChecked(!ctv.isChecked()); }
        });
    }
}
