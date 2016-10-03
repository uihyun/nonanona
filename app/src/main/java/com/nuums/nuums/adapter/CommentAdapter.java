package com.nuums.nuums.adapter;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nuums.nuums.R;
import com.nuums.nuums.model.misc.Comment;
import com.nuums.nuums.model.nanum.Nanum;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.photo.PhotoManager;
import com.yongtrim.lib.ui.CircularNetworkImageView;
import com.yongtrim.lib.util.MiscUtil;

import java.util.List;

/**
 * nuums / com.nuums.nuums.adapter
 * <p/>
 * Created by Uihyun on 15. 12. 28..
 */
public class CommentAdapter extends BaseAdapter {

    Nanum nanum;
    private List<Comment> comments;
    private ContextHelper contextHelper;

    private ListView listView;

    boolean isSelectMode;


    class Holder {

        CircularNetworkImageView ivAvatar;
        TextView tvMessage;
        TextView tvUsername;
        TextView tvNumber;
        TextView tvDate;
        View viewLine;
        ImageView ivModify;
        ImageView ivMark;
        CheckBox cbCheck;

        public View set(View v) {
            v.setTag(this);

            ivAvatar = (CircularNetworkImageView)v.findViewById(R.id.ivAvatar);
            tvUsername = (TextView)v.findViewById(R.id.tvUsername);
            tvNumber = (TextView)v.findViewById(R.id.tvNumber);

            tvMessage = (TextView)v.findViewById(R.id.tvMessage);
            tvDate = (TextView)v.findViewById(R.id.tvDate);

            viewLine = (View)v.findViewById(R.id.viewLine);

            ivModify = (ImageView)v.findViewById(R.id.ivModify);

            ivMark = (ImageView)v.findViewById(R.id.ivMark);
            cbCheck = (CheckBox)v.findViewById(R.id.cbCheck);
            return v;
        }
    }

    public CommentAdapter(ContextHelper contextHelper, ListView listView) {
        super();
        this.contextHelper = contextHelper;
        this.listView = listView;
    }


    public void setIsSelectMode(boolean isSelectMode) {
        this.isSelectMode = isSelectMode;
    }



    public void setData(Nanum nanum, List<Comment> comments) {
        this.nanum = nanum;

        this.comments = comments;
    }


    public List<Comment> getData() {
        return this.comments;
    }


    @Override
    public int getCount() {
        if(comments == null)
            return 0;
        return comments.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return comments.get(position);
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)parent.getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = new Holder().set(mInflater.inflate(R.layout.cell_comment, parent, false));
        }
        final Holder h = (Holder)convertView.getTag();

        Comment comment = comments.get(position);

        PhotoManager.getInstance(contextHelper).setPhotoSmall(h.ivAvatar, comment.getOwner().getPhoto());

        h.tvUsername.setText(comment.getOwner().getNicknameSafe());
        h.tvDate.setText(MiscUtil.getPostTime(comment.getTimeCreated()));
        h.tvMessage.setText(contextHelper.getMention(comment.getMessage()));

        if(position == comments.size() - 1) {
            h.viewLine.setVisibility(View.GONE);
        } else {
            h.viewLine.setVisibility(View.VISIBLE);
        }

        if(comment.getOwner().isMe(contextHelper)) {
            h.ivModify.setVisibility(View.VISIBLE);
        } else {
            h.ivModify.setVisibility(View.GONE);
        }

        h.ivMark.setVisibility(View.GONE);
        h.cbCheck.setVisibility(View.GONE);

        if(nanum.getOwner().isSame(comment.getOwner())) {
            h.tvNumber.setVisibility(View.GONE);
            h.tvUsername.setTextColor(ContextCompat.getColor(contextHelper.getContext(), R.color.green));
            h.tvMessage.setTextColor(ContextCompat.getColor(contextHelper.getContext(), R.color.green));
        } else {
            h.tvNumber.setVisibility(View.VISIBLE);
            h.tvNumber.setText("" + (comment.indexApply + 1));

            if(comment.isWon) {
                h.ivMark.setVisibility(View.VISIBLE);
            }

            h.tvUsername.setTextColor(ContextCompat.getColor(contextHelper.getContext(), R.color.black));
            h.tvMessage.setTextColor(ContextCompat.getColor(contextHelper.getContext(), R.color.gray));

            if(isSelectMode) {
                h.cbCheck.setVisibility(View.VISIBLE);
                h.cbCheck.setChecked(comment.isSelect);
            }
        }

        h.cbCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.performItemClick(listView, position, position);
            }
        });




        return convertView;
    }


}



