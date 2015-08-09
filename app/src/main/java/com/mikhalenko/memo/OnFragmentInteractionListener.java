package com.mikhalenko.memo;


import android.support.v4.app.Fragment;

public interface OnFragmentInteractionListener {
    void onFragmentInteraction(String s);
    void replaceFragment(Fragment aFragment, boolean aNeedAddToBackStask);
}
