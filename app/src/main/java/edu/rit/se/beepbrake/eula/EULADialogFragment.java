package edu.rit.se.beepbrake.eula;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.rit.se.beepbrake.R;
import edu.rit.se.beepbrake.utils.Utilities;

public class EULADialogFragment extends AppCompatDialogFragment {

    private OnEulaCompletedListener mListener;
    private RecyclerView mRecyclerView;
    private EULAAdapter mAdapter;

    // Holds the saved EULA result (default: false).
    private Boolean mSavedResult;

    private int positiveBtnID = R.id.action_agree, eulaFragmentLayoutID = R.layout.fragment_eula;

    // Empty constructor is required for DialogFragment
    // Make sure not to add arguments to the constructor
    // Use `newInstance` instead as shown below
    public EULADialogFragment() { }

    // May need to provide a content provider through the params here
    public static EULADialogFragment newInstance() { return new EULADialogFragment(); }

    // Pull the SharedPreference.mSavedResult saved value here?
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        setupToolbar();


        if (context instanceof OnEulaCompletedListener) {
            mListener = (OnEulaCompletedListener) context;

            // Pulls values from SharedPreference
            // TODO: set it to pull from SharedPreferences, default: false
            mSavedResult = false;
        } else {
            throw new RuntimeException(context.toString() + " must implement " +
                    "OnEulaCompletedListener");
        }
    }

    private void setupToolbar() {
        // Set the toolbar
        AppCompatActivity mActivity = (AppCompatActivity) this.getActivity();
        Toolbar toolbar = (Toolbar) mActivity.findViewById(R.id.toolbar);

        Utilities.toggleHideyBar(getActivity().getWindow().getDecorView());

        mActivity.setSupportActionBar(toolbar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(eulaFragmentLayoutID, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_eula);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        return view;
    }

    // Generates the eula sections from the values/strings_eula.xml resource file
    private ArrayList<EULASection> getSections() {
        EULASection section;
        int index;

        ArrayList<EULASection> sList = new ArrayList<>();
        Resources res = getResources();

        String[] sTitlesArr = res.getStringArray(R.array.eula_section_titles);
        int sTitlesLength = sTitlesArr.length - 1;

        String[] sContentsArr = res.getStringArray(R.array.eula_section_contents);
        int sContentsLength = sContentsArr.length - 1;

        int sLengthDiff = sTitlesLength - sContentsLength;

        // Equivalent number of section titles to contents
        if (sLengthDiff == 0) {
            for (index = 0; index < sContentsLength; index++) {
                section = new EULASection();

                section.setID(index);
                section.setTitle(sTitlesArr[index]);
                section.setContent(sContentsArr[index]);

                sList.add(section);
            }
            return sList;
        } else {
            String adjective = (sLengthDiff > 0) ? "more" : "less";
            String errorMsg = getContext().toString() +
                    ". eula_section_titles has " + Math.abs(sLengthDiff) + " " + adjective +
                    "index than eula_section_content.";

            throw new StringIndexOutOfBoundsException(errorMsg);
        }
    }

    @NonNull
    @Override
    public AppCompatDialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        List<EULASection> mSections = getSections();

        mAdapter = new EULAAdapter(mSections);
        mRecyclerView.setAdapter(mAdapter);

        Resources res = getResources();
        String appName = res.getString(R.string.app_name);
        String companyName = res.getString(R.string.company_name);

        String title = res.getString(R.string.eula_title);
        String notice = String.format(res.getString(R.string.eula_notice), appName);
        String disclaimer = String.format(res.getString(R.string.eula_disclaimer), companyName,
                appName, companyName);

        // TODO: Set the noticeView and disclaimerView text

        // TODO: Set the X and ACCEPT buttons in the toolbar


        // TODO: Verify if this is the way to set a title
        // TODO: Verify this is the way to return a AppCompatDialog
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_RIGHT_ICON);
        dialog.setTitle(title);
        return (AppCompatDialog) dialog;
    }

    private class EULASectionHolder extends RecyclerView.ViewHolder implements View
            .OnClickListener {

        private TextView mTitleTextView, mIDTextView, mContentTextView;

        private EULASection mSection;

        public EULASectionHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mIDTextView = (TextView) itemView.findViewById(R.id.eula_section_id);
            mTitleTextView = (TextView) itemView.findViewById(R.id.eula_section_title);
            mContentTextView = (TextView) itemView.findViewById(R.id.eula_section_content);
        }

        public void bindContent(EULASection section) {
            mSection = section;

            mIDTextView.setText(mSection.getID());
            mTitleTextView.setText(mSection.getTitle());
            mContentTextView.setText(mSection.getContent());
        }

        @Override
        public void onClick(View v) {
            // TODO: make expand / collapse or nothing
            Toast.makeText(getActivity(), "EULA section " + mSection.getID() + " clicked.", Toast
                    .LENGTH_SHORT).show();
        }
    }

    private class EULAAdapter extends RecyclerView.Adapter<EULASectionHolder> {

        private List<EULASection> mSections;

        public EULAAdapter(List<EULASection> sections) { mSections = sections; }

        @Override
        public EULASectionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_eula_section, parent, false);
            return new EULASectionHolder(view);
        }

        @Override
        public void onBindViewHolder(EULASectionHolder holder, int position) {
            EULASection section = mSections.get(position);
            holder.bindContent(section);
        }

        @Override
        public int getItemCount() { return mSections.size(); }
    }

    // Simply returns the boolean of there decision to accept the EULA
    public void onClick(View v) {
        if (mListener != null) {
            boolean newResult = v.getId() == positiveBtnID;

            // Only on resultant change does the SharedPreferences value get updated
            if (mSavedResult != newResult) {
                // Todo: Update SharedPreferences
            }

            String toastText = (newResult) ? "EULA Accepted!" : "Returning to Welcome Screen";

            Toast.makeText(getActivity(), toastText, Toast.LENGTH_SHORT).show();

            // On not accepted, go to Welcome screen (or previous screen if FirstRun)
            mListener.onEulaAccepted(newResult);
        }
    }

    // To return whether or not the user accepted the EULA
    public interface OnEulaCompletedListener {
        void onEulaAccepted(Boolean hasAccepted);
    }
}
