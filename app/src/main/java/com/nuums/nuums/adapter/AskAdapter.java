package com.nuums.nuums.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nuums.nuums.R;
import com.nuums.nuums.activity.BaseActivity;
import com.nuums.nuums.model.apply.Apply;
import com.nuums.nuums.model.ask.Ask;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.photo.PhotoManager;
import com.yongtrim.lib.ui.CircularNetworkImageView;
import com.yongtrim.lib.ui.UltraButton;
import com.yongtrim.lib.util.DateUtil;

import java.util.List;

/**
 * nuums / com.nuums.nuums.adapter
 * <p/>
 * Created by yongtrim.com on 16. 1. 22..
 */
public class AskAdapter extends BaseAdapter {

    private List<Ask> asks;
    private ContextHelper contextHelper;

    class Holder {
        CircularNetworkImageView ivAvatar;
        TextView tvMessage;
        UltraButton btnGo;

        public View set(View v) {
            v.setTag(this);
            ivAvatar = (CircularNetworkImageView) v.findViewById(R.id.ivAvatar);
            tvMessage = (TextView) v.findViewById(R.id.tvMessage);
            btnGo = (UltraButton) v.findViewById(R.id.btnGo);

            return v;
        }
    }

    public AskAdapter(ContextHelper contextHelper) {
        super();
        this.contextHelper = contextHelper;
    }

    public void setData(List<Ask> asks) {
        this.asks = asks;
    }

    public List<Ask> getData() {
        return this.asks;
    }

    @Override
    public int getCount() {
        if(asks == null)
            return 0;
        return asks.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return asks.get(position);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)parent.getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = new Holder().set(mInflater.inflate(R.layout.cell_ask, parent, false));
        }
        final Holder h = (Holder)convertView.getTag();

        final Ask ask = asks.get(position);

        PhotoManager.getInstance(contextHelper).setPhotoSmall(h.ivAvatar, ask.getOwner().getPhoto());

        h.tvMessage.setText(ask.getMessage());


        h.btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(contextHelper.getContext(), BaseActivity.class);
                i.putExtra("activityCode", BaseActivity.ActivityCode.NANUMEDT.ordinal());
                i.putExtra("asker", ask.getOwner().toString());

                contextHelper.getContext().startActivity(i);
            }
        });

        return convertView;
    }
}



