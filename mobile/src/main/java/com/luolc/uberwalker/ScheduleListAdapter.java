package com.luolc.uberwalker;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_schedule_list, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.imWay = (ImageView) convertView.findViewById(R.id.im_travel_way);
            viewHolder.tvContentTest = (TextView) convertView.findViewById(R.id.tv_content_test);
            viewHolder.layoutContent = (RelativeLayout) convertView.findViewById(R.id.layout_content);
            viewHolder.tvLabel = (TextView) convertView.findViewById(R.id.tv_label);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ScheduleItemEntity item = mItems.get(position);

        if (item.getLabel() != null) {
            viewHolder.tvLabel.setText(item.getLabel());
            viewHolder.tvLabel.setVisibility(View.VISIBLE);
            viewHolder.layoutContent.setVisibility(View.GONE);
        } else {
            viewHolder.imWay.setImageResource(item.getImWayRes());
            viewHolder.tvContentTest.setText(item.getContentTest());
            viewHolder.tvTime.setText(item.getTime());
            viewHolder.tvLabel.setVisibility(View.GONE);
            viewHolder.layoutContent.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView imWay;
        TextView tvContentTest;
        RelativeLayout layoutContent;
        TextView tvLabel;
        TextView tvTime;
    }
}
