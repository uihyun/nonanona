package com.nuums.nuums.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nuums.nuums.R;
import com.nuums.nuums.model.user.NsUserSearch;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.photo.PhotoManager;
import com.yongtrim.lib.ui.CircularNetworkImageView;

/**
 * nuums / com.nuums.nuums.adapter
 * <p/>
 * Created by Uihyun on 16. 1. 15..
 */
public class UserAdapter extends ArrayAdapter<NsUserSearch> {
    Context mContext;
    int layoutResourceId;
    NsUserSearch data[] = null;
    ContextHelper contextHelper;

    public UserAdapter(Context mContext, int layoutResourceId, NsUserSearch[] data) {

        super(mContext, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }


    public void setContextHelper(ContextHelper contextHelper) {
        this.contextHelper = contextHelper;

    }

    @Override
    public boolean isEnabled(int position) {
        NsUserSearch user = data[position];

        if(user.getUser() != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        NsUserSearch user = data[position];

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(layoutResourceId, parent, false);
        }

        CircularNetworkImageView ivAvatar = (CircularNetworkImageView) convertView.findViewById(R.id.ivAvatar);
        ivAvatar.setVisibility(View.GONE);

        TextView tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);

        convertView.findViewById(R.id.progressBar).setVisibility(View.GONE);

        if(user.getUser() != null) {
            tvUsername.setText(user.toString());

            ivAvatar.setVisibility(View.VISIBLE);
            PhotoManager.getInstance(contextHelper).setPhotoSmall(ivAvatar, user.getUser().getPhoto());
        } else {
            tvUsername.setText("검색중...");
            convertView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        }

        return convertView;
    }
}
