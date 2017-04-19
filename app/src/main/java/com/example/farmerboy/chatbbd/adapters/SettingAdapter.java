package com.example.farmerboy.chatbbd.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.farmerboy.chatbbd.R;
import com.example.farmerboy.chatbbd.classes.ListViewGeneralItem;

import java.util.List;

/**
 *  Created by farmerboy on 4/18/2017.
 */
public class SettingAdapter extends ArrayAdapter<ListViewGeneralItem> {

    private List<ListViewGeneralItem> mSetting;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public SettingAdapter(Context context, List<ListViewGeneralItem> settings) {
        super(context, 0, settings);
        mContext = context;
        mSetting = settings;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        if (mSetting.isEmpty()) return 0;
        return mSetting.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ListViewGeneralItem setting = mSetting.get(position);
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_setting, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvSettingName = (TextView) convertView.findViewById(R.id.tvSettingName);
            viewHolder.ivSettingIcon = (ImageView) convertView.findViewById(R.id.ivSettingIcon);
            viewHolder.viewLine = (View) convertView.findViewById(R.id.viewLine);
            convertView.setTag(viewHolder);
        }
        else viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.tvSettingName.setText(setting.getName());
        viewHolder.ivSettingIcon.setImageResource(setting.getIcon());
        if (position == mSetting.size()-1) viewHolder.viewLine.setVisibility(View.GONE);
        return convertView;
    }

    private static class ViewHolder {
        TextView tvSettingName;
        ImageView ivSettingIcon;
        View viewLine;
    }
}
