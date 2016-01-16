package com.luolc.uberwalker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LuoLiangchen on 16/1/16.
 */
public class MainFragment extends Fragment {

    public static final String KEY_TITLE = "key_title";
    private String mTitle;

    private ListView lvSchedule;
    private ScheduleListAdapter adapter;
    private List<ScheduleItemEntity> schedules = new ArrayList<>();

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

        // FAB Set
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "剩余预算：￥657；剩余卡路里：132Cal", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });

        for (int i = 0; i < 10; ++i) {
            ScheduleItemEntity item = new ScheduleItemEntity();
            if (i % 2 == 0) item.setImWayRes(R.drawable.way_walk);
            else item.setImWayRes(R.drawable.way_uber);
            schedules.add(item);
        }
        lvSchedule = (ListView) rootView.findViewById(R.id.lv_schedule);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new ScheduleListAdapter(getActivity(), schedules);
        lvSchedule.setAdapter(adapter);
    }
}
