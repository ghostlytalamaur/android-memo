package com.mikhalenko.memo;

import java.io.Serializable;

/**
 * Created by crow on 02.05.15.
 */
public class Category implements Serializable {
    private long mId;
    private String mName;

    private Notes mNotes;

    public Category(long id, String name) {
        mId = id;
        mName = name;
        mNotes = new Notes();
    }

    public void copyFrom(Category aCategory) {
        mName = aCategory.getName();
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public long getID() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public Notes getNotes() {
        return mNotes;
    }
}
