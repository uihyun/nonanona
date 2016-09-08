package com.nuums.nuums.adapter;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nuums.nuums.R;
import com.nuums.nuums.model.chat.Talk;
import com.nuums.nuums.model.chat.UserInfo;
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.photo.PhotoManager;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.ui.CircularNetworkImageView;
import com.yongtrim.lib.ui.UltraButton;
import com.yongtrim.lib.util.MiscUtil;

/**
 * nuums / com.nuums.nuums.adapter
 * <p/>
 * Created by yongtrim.com on 16. 1. 6..
 */
public class TalkAdapter extends BaseAdapter {

    private ListView listView;
    private ContextHelper contextHelper;

    private java.util.ArrayList<Talk> talks;

    boolean isOutMode;

    class Holder {

        CircularNetworkImageView ivAvatar;
        TextView tvNickname;
        TextView tvDate;
        TextView tvMessage;
        UltraButton tvUnread;
        CheckBox cbCheck;

        public View set(View v) {
            v.setTag(this);
            ivAvatar = (CircularNetworkImageView)v.findViewById(R.id.ivAvatar);

            tvNickname = (TextView) v.findViewById(R.id.tvNickname);
            tvDate = (TextView) v.findViewById(R.id.tvDate);
            tvMessage = (TextView) v.findViewById(R.id.tvMessage);
            tvUnread = (UltraButton) v.findViewById(R.id.tvUnread);

            cbCheck = (CheckBox) v.findViewById(R.id.cbCheck);
            return v;
        }
    }


    public TalkAdapter(ContextHelper contextHelper, ListView listView) {
        super();
        this.listView = listView;
        this.contextHelper = contextHelper;
    }


    public void setMode(boolean isOutMode) {
        this.isOutMode = isOutMode;
        TalkAdapter.this.notifyDataSetChanged();
    }

    public boolean isOutMode() {
        return this.isOutMode;
    }

    public void setData(java.util.ArrayList<Talk> talks) {
        this.talks = talks;
    }


    @Override
    public int getCount() {
        if(talks == null)
            return 0;
        return talks.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return talks.get(position);
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Talk talk = talks.get(position);

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)parent.getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = new Holder().set(mInflater.inflate(R.layout.cell_talk, parent, false));
        }

        final Holder h = (Holder) convertView.getTag();

        NsUser me = UserManager.getInstance(contextHelper).getMe();
        NsUser to = talk.getOther(contextHelper);

        UserInfo myInfo = talk.getMyInfo(contextHelper);


        if(myInfo != null && myInfo.getUnreadCnt() > 0) {
            h.tvUnread.setVisibility(View.VISIBLE);
            h.tvUnread.setText("" + myInfo.getUnreadCnt());
        } else {
            h.tvUnread.setVisibility(View.GONE);
        }

        h.tvNickname.setText(to.getNicknameSafe());
        PhotoManager.getInstance(contextHelper).setPhotoSmall(h.ivAvatar, to.getPhoto());

        h.tvMessage.setText(talk.getMessageRecent());
        h.tvDate.setText(MiscUtil.getPostTime(talk.getTimeUpdated()));


        if(isOutMode) {
            h.cbCheck.setVisibility(View.VISIBLE);
            Boolean select = (Boolean)talk.getTag();
            if(select != null && select.booleanValue()) {
                h.cbCheck.setChecked(true);
            } else {
                h.cbCheck.setChecked(false);
            }

            h.cbCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listView.performItemClick(listView, position, position);
                }
            });

        } else {
            h.cbCheck.setVisibility(View.GONE);
        }

        return convertView;
    }

}


