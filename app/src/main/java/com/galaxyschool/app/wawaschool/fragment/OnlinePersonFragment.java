package com.galaxyschool.app.wawaschool.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.galaxyschool.app.wawaschool.R;
public class OnlinePersonFragment extends ContactsListFragment {
    public static String TAG = OnlinePersonFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_online_person, null);
    }
}
