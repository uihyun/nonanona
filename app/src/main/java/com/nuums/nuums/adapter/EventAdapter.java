package com.nuums.nuums.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.Response;
import com.nuums.nuums.R;
import com.nuums.nuums.model.apply.Apply;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.config.ConfigManager;
import com.yongtrim.lib.model.photo.PhotoManager;
import com.yongtrim.lib.model.post.Post;
import com.yongtrim.lib.model.post.PostData;
import com.yongtrim.lib.model.post.PostList;
import com.yongtrim.lib.model.post.PostManager;
import com.yongtrim.lib.ui.CustomNetworkImageView;
import com.yongtrim.lib.ui.autoscrollviewpager.ClickedCallback;
import com.yongtrim.lib.util.DateUtil;
import com.yongtrim.lib.util.UIUtil;

import java.util.List;

/**
 * nuums / com.nuums.nuums.adapter
 * <p/>
 * Created by Uihyun on 16. 1. 29..
 */
public class EventAdapter extends BaseAdapter {

    ContextHelper contextHelper;
    private List<Post> posts;
    class Holder {
        CustomNetworkImageView ivPhoto;
        View viewGap;

        public View set(View v) {
            v.setTag(this);
            ivPhoto = (CustomNetworkImageView) v.findViewById(R.id.ivPhoto);
            UIUtil.setRatio(ivPhoto, contextHelper.getContext(), 648, 202);
            viewGap = v.findViewById(R.id.viewGap);
            return v;
        }
    }

    public EventAdapter(ContextHelper contextHelper) {
        super();
        this.contextHelper = contextHelper;
        posts = ConfigManager.getInstance(contextHelper).getEvents().getPosts();
    }


    public List<Post> getData() {
        return this.posts;
    }

    @Override
    public int getCount() {
        if(posts == null)
            return 0;
        return posts.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return posts.get(position);
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)parent.getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = new Holder().set(mInflater.inflate(R.layout.cell_event, parent, false));
        }
        final Holder h = (Holder)convertView.getTag();

        final Post post = posts.get(position);

        if(post.getPhotos().size() > 0) {
            PhotoManager.getInstance(contextHelper).setPhotoSmall(h.ivPhoto, post.getPhotos().get(0));

            h.ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!TextUtils.isEmpty(posts.get(position).getUrl())) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(posts.get(position).getUrl()));
                        contextHelper.getActivity().startActivity(browserIntent);

                        PostManager.getInstance(contextHelper).count(
                                posts.get(position).getId(),
                                new Response.Listener<PostData>() {
                                    @Override
                                    public void onResponse(PostData response) {
                                    }
                                },
                                null
                        );
                    }
                }
            });
        }

        if(position == posts.size() - 1) {
            h.viewGap.setVisibility(View.GONE);
        } else {
            h.viewGap.setVisibility(View.VISIBLE);
        }
//        h.tvDate.setText(DateUtil.dateToString(apply.getTimeCreated(), "yyyy.MM.dd"));
//        h.tvTitle.setText(apply.getTitle());
//
//        switch(apply.getStatus()) {
//            case Apply.STATUS_ACCEPT:
//                h.tvStatus.setText("배송준비");
//                h.tvCompany.setText("-");
//                break;
//            case Apply.STATUS_DOING:
//                h.tvStatus.setText("배송중");
//                break;
//            case Apply.STATUS_DONE:
//                h.tvStatus.setText("배송완료");
//                break;
//        }



        return convertView;
    }
}



