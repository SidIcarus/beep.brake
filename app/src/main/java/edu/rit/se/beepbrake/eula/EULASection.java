package edu.rit.se.beepbrake.eula;

public class EULASection {

    // Making mID a String simplifies things...
    private String mID, mTitle, mContent;

    public EULASection() { }

    public String getContent() { return mContent; }

    public void setContent(String mContent) { this.mContent = mContent; }

    /* Getters */
    public String getID() { return mID; }

    // Pad singles w/ preceding 0
    public void setID(String mID) { this.mID = String.format("%02d", mID) + "."; }

    public String getTitle() { return mTitle; }

    public void setTitle(String mTitle) { this.mTitle = mTitle.toUpperCase() + "."; }

    /* Setters */
    public void setID(int mID) { this.setID(Integer.toString(mID)); }
}
