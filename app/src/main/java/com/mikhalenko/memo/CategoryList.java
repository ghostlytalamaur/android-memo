package com.mikhalenko.memo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

public class CategoryList extends ArrayList<Category> {

    public Category getByID(long aID) {
        for (Category category : this) {
            if (category.getID() == aID)
                return category;
        }
        return null;
    }

    public void deleteNotValid(Vector<Long> aValidIDs) {
        Iterator<Category> it = iterator();
        while (it.hasNext()) {
            Category category = it.next();
            if (!aValidIDs.contains(category.getID()))
                it.remove();
        }
    }
}
