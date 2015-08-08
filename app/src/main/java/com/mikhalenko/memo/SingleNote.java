package com.mikhalenko.memo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SingleNote implements Serializable {
    private String mTitle;
    private String mDescription;
    private long mId;
    private long mDate;
    private boolean mIsCompleted;

    private int mUserSortIndex;
    private int mAutoSortIndex;

    public int getAutoSortIndex() {
        return mAutoSortIndex;
    }

    public void setAutoSortIndex(int mAutoSortIndex) {
        this.mAutoSortIndex = mAutoSortIndex;
    }

    public int getUserSortIndex() {

        return mUserSortIndex;
    }

    public void setUserSortIndex(int mUserSortIndex) {
        this.mUserSortIndex = mUserSortIndex;
    }

    private long mCategoryID;

    public static final String DATE_TIME_FORMAT = "HH:mm MM/dd/yyyy";

    SingleNote() {
        mId = -1;
        mCategoryID = 1; // Unknown category
        mIsCompleted = false;
        mDate = new Date().getTime();
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public long getDate() {
        return mDate;
    }

    public void setDate(long date) {
        if (date != 0)
            mDate = date;
    }

    public long getID() {
        return mId;
    }

    public String getSingleLineDescription() {
        return mDescription.replaceAll("\n", " ");
    }

    public String getSingleLineTitle() {
        return mTitle.replaceAll("\n", " ");
    }

    public void setId(long id) {
        this.mId = id;
    }

    public String getStringDate() {
        return new SimpleDateFormat(DATE_TIME_FORMAT).format(mDate);
    }

    public boolean isEmpty() {
        return mTitle.isEmpty() && mDescription.isEmpty();
    }

    public boolean isCompleted() {
        return mIsCompleted;
    }

    public void setCompleted(boolean isCompleted) {
        mIsCompleted = isCompleted;
    }

    public long getCategoryID() {
        return mCategoryID;
    }

    public void setCategoryID(long categoryID) {
        mCategoryID = categoryID;
    }

    public void copyFrom(SingleNote aNote) {
        if (aNote == null)
            return;;
        mIsCompleted = aNote.isCompleted();
        mTitle = aNote.getTitle();
        mDate = aNote.getDate();
        mDescription = aNote.getDescription();
        mAutoSortIndex = aNote.getAutoSortIndex();
        mUserSortIndex = aNote.getUserSortIndex();
    }
}
