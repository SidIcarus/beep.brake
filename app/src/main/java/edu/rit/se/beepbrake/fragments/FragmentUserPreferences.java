package edu.rit.se.beepbrake.fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import edu.rit.se.beepbrake.R;

public class FragmentUserPreferences extends PreferenceFragment2 implements SharedPreferences.OnSharedPreferenceChangeListener {
    View rootView = null;
    Preference letterSizePref;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Access the main layout file
        rootView = inflater.inflate(R.layout.preferences_with_toolbar, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.preferences_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_call_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                dismiss();
            }
        });
        toolbar.inflateMenu(R.menu.dialog_toolbar_empty);
        toolbar.setTitle("Preferences");
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(Color.WHITE);
        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);
        LinearLayout prefLayout = (LinearLayout) rootView.findViewById(R.id.preferences_dialog);
        prefLayout.addView(view);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        letterSizePref = (Preference) findPreference("letterSize");

        // From here this is your default user settings stuff
        // ...
        return rootView;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }
}
// etc.
