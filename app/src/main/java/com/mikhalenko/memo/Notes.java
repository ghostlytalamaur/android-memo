package com.mikhalenko.memo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;

public class Notes extends ArrayList<SingleNote> {

    private void updateSortIndex(SortIndexType aType) {
        for (int I = 0; I < size(); I++)
            if (aType == SortIndexType.sitAuto)
                get(I).setAutoSortIndex(I);
            else
                get(I).setUserSortIndex(I);
    }

    public void sortByID() {
        Collections.sort(this, new Comparator<SingleNote>() {
            @Override
            public int compare(SingleNote lhs, SingleNote rhs) {
                return (int) (lhs.getID() - rhs.getID());
            }
        });
        updateSortIndex(SortIndexType.sitAuto);
    }

    public  void sortByDate() {
        Collections.sort(this, new Comparator<SingleNote>() {
            @Override
            public int compare(SingleNote lhs, SingleNote rhs) {
                return (int) (lhs.getDate() - rhs.getDate());
            }
        });
        updateSortIndex(SortIndexType.sitAuto);
    }

    public SingleNote getByID(long aID) {
        for (SingleNote note : this) {
            if (note.getID() == aID)
                return note;
        }
        return null;
    }

    public void deleteNotValid(Vector<Long> aValidIDs) {
        Iterator<SingleNote> it = iterator();
        while (it.hasNext()) {
            SingleNote note = it.next();
            if (!aValidIDs.contains(note.getID()))
                it.remove();
        }
    }
}
