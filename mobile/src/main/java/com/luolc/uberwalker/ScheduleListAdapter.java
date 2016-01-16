package com.luolc.uberwalker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by LuoLiangchen on 16/1/17.
 */
public class ScheduleListAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<ScheduleItemEntity> mItems;
    private Context mContext;

    public ScheduleListAdapter(Context context, List<ScheduleItemEntity> items) {
        this.layoutInflater = LayoutInflater.from(context);
        this.mItems = items;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        if (mItems == null) {
            return 0;
        }
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        if (mItems == null || position >= mItems.size()) {
            return null;
        }
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_schedule_list, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.imWay = (ImageView) convertView.findViewById(R.id.im_travel_way);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ScheduleItemEntity item = mItems.get(position);
        viewHolder.imWay.setImageResource(item.getImWayRes());

        return convertView;
    }

    static class ViewHolder {
        ImageView imWay;
    }
}
