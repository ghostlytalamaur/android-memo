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

    private void sortByID(final boolean aComplitedAtEnd) {
        Collections.sort(this, new Comparator<SingleNote>() {
            @Override
            public int compare(SingleNote lhs, SingleNote rhs) {
                int res = Boolean.compare(lhs.isCompleted(), rhs.isCompleted());
                if (res == 0)
                    res = Long.compare(rhs.getID(), lhs.getID());
                return res;
            }
        });
        updateSortIndex(SortIndexType.sitAuto);
    }

    private void sortByDate(boolean aComplitedAtEnd) {
        Collections.sort(this, new Comparator<SingleNote>() {
            @Override
            public int compare(SingleNote lhs, SingleNote rhs) {
                int res = Boolean.compare(lhs.isCompleted(), rhs.isCompleted());
                if (res == 0)
                    res = Long.compare(lhs.getDate(), rhs.getDate());
                return res;
            }
        });
        updateSortIndex(SortIndexType.sitAuto);
    }

    public void sortList(SortType aType, boolean aComplitedAtEnd) {
        switch (aType) {
            case ID:
                sortByID(aComplitedAtEnd);
                break;
            case DATE:
                sortByDate(aComplitedAtEnd);
        }
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
