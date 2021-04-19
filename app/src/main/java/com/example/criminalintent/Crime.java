package com.example.criminalintent;

import java.util.Date;
import java.util.UUID;

public class Crime {

    private Date mDate;
    private boolean mSolved;
    private String mTitle;
    private UUID mid;
    private String mSuspect = "";
    private String mSuspectPhone ;

    public Crime()
    {
        mid = UUID.randomUUID();
        mDate = new Date();
    }

    public Crime(UUID id)
    {
        mid = id;
        mDate = new Date();
    }

    public UUID getMid() {
        return mid;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public String getSuspectPhone() {
        return mSuspectPhone;
    }

    public void setSuspectPhone(String suspectPhone) {
        mSuspectPhone = suspectPhone;
    }

    public String getPhotoFilename()
    {
        return "IMG_" + getMid().toString() + ".jpg";
    }
}
