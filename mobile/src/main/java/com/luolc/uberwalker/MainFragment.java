package com.luolc.uberwalker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by LuoLiangchen on 16/1/16.
 */
public class MainFragment extends Fragment {

    public static final String KEY_TITLE = "key_title";
    private String mTitle;

    public static MainFragment newInstance(String title) {
        MainFragment fragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TITLE, title);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }
}
