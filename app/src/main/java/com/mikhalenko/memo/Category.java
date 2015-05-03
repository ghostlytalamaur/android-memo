package com.mikhalenko.memo;

import java.io.Serializable;

/**
 * Created by crow on 02.05.15.
 */
public class Category implements Serializable {
    private long mId;
    private String mName;

    public Category(long id, String name) {
        mId = id;
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }
}
