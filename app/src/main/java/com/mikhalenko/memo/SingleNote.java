package com.mikhalenko.memo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SingleNote implements Serializable {
    private String mTitle;
    private String mDescription;
    private long id;
    private long mDate;
    private boolean mIsCompleted;
    private String mSingleLineDescription;
    private String mSingleLineTitle;

    public static final String DATE_TIME_FORMAT = "HH:mm MM/dd/yyyy";

    SingleNote() {
        id = -1;
        mIsCompleted = false;
        mDate = new Date().getTime();
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mSingleLineTitle = title.replaceAll("\n", " ");
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mSingleLineDescription = description.replaceAll("\n", " ");
        mDescription = description;
    }

    public long getDate() {
        return mDate;
    }

    public void setDate(long date) {
        if (date != 0)
            mDate = date;
    }

    public long getId() {
        return id;
    }

    public String getSingleLineDescription() {
        return mSingleLineDescription;
    }

    public String getSingleLineTitle() {
        return mSingleLineTitle;
    }

    public void setId(long id) {
        this.id = id;
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
}
