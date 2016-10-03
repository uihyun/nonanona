package com.nuums.nuums.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nuums.nuums.R;
import com.nuums.nuums.model.alarm.Alarm;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.photo.PhotoManager;
import com.yongtrim.lib.ui.CircularNetworkImageView;
import com.yongtrim.lib.util.MiscUtil;

import java.util.List;

/**
 * nuums / com.nuums.nuums.adapter
 * <p/>
 * Created by Uihyun on 16. 1. 20..
 */
public class AlarmAdapter extends BaseAdapter {

    private List<Alarm> alarms;
    private ContextHelper contextHelper;

    class Holder {
        CircularNetworkImageView ivAvatar;
        TextView tvMessage;
        TextView tvDate;
        View viewLine;

        public View set(View v) {
            v.setTag(this);
            ivAvatar = (CircularNetworkImageView) v.findViewById(R.id.ivAvatar);
            tvMessage = (TextView) v.findViewById(R.id.tvMessage);
            tvDate = (TextView) v.findViewById(R.id.tvDate);
            viewLine = (View)v.findViewById(R.id.viewLine);
            return v;
        }
    }

    public AlarmAdapter(ContextHelper contextHelper) {
        super();
        this.contextHelper = contextHelper;
    }

    public void setData(List<Alarm> alarms) {
        this.alarms = alarms;
    }

    public List<Alarm> getData() {
        return this.alarms;
    }

    @Override
    public int getCount() {
        if(alarms == null)
            return 0;
        return alarms.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return alarms.get(position);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)parent.getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = new Holder().set(mInflater.inflate(R.layout.cell_alarm, parent, false));
        }
        final Holder h = (Holder)convertView.getTag();

        final Alarm alarm = alarms.get(position);

        if(alarm.getActor() != null && alarm.getActor().getPhoto() != null && alarm.getActor().getPhoto().hasPhoto())
            PhotoManager.getInstance(contextHelper).setPhotoSmall(h.ivAvatar, alarm.getActor().getPhoto());
        else
            PhotoManager.getInstance(contextHelper).setPhotoSmall(h.ivAvatar, null);

        h.tvMessage.setText(alarm.getMessage());

        h.tvDate.setText(MiscUtil.getPostTime(alarm.getTimeCreated()));

        if(position == getCount() - 1) {
            h.viewLine.setVisibility(View.GONE);
        } else {
            h.viewLine.setVisibility(View.VISIBLE);
        }

        return convertView;
    }
}



