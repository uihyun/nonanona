package com.nuums.nuums.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.nuums.nuums.R;
import com.nuums.nuums.model.apply.Apply;
import com.nuums.nuums.model.apply.ApplyData;
import com.nuums.nuums.model.apply.ApplyManager;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.model.post.Post;
import com.yongtrim.lib.model.post.PostData;
import com.yongtrim.lib.model.post.PostManager;
import com.yongtrim.lib.ui.UltraButton;
import com.yongtrim.lib.ui.sweetalert.SweetAlertDialog;
import com.yongtrim.lib.util.DateUtil;
import com.yongtrim.lib.util.PixelUtil;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * nuums / com.nuums.nuums.adapter
 * <p/>
 * Created by Uihyun on 16. 1. 30..
 */
public class PostAdapter extends BaseAdapter {

    private List<Post> posts;
    private ContextHelper contextHelper;
    private ListView listView;

    class Holder {
        TextView tvDate;
        TextView tvTitle;
        View viewTitle;

        View viewMessage;

        TextView tvMessage;

        View viewLine;

        public View set(View v) {
            v.setTag(this);
            tvDate = (TextView) v.findViewById(R.id.tvDate);
            tvTitle = (TextView) v.findViewById(R.id.tvTitle);
            viewTitle = v.findViewById(R.id.viewTitle);
            viewMessage = v.findViewById(R.id.viewMessage);
            tvMessage = (TextView) v.findViewById(R.id.tvMessage);
            viewLine = v.findViewById(R.id.viewLine);

            return v;
        }
    }

    public PostAdapter(ContextHelper contextHelper, ListView listView) {
        super();
        this.contextHelper = contextHelper;
        this.listView = listView;
    }

    public void setData(List<Post> posts) {
        this.posts = posts;
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
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)parent.getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = new Holder().set(mInflater.inflate(R.layout.cell_post, parent, false));
        }
        final Holder h = (Holder)convertView.getTag();

        final Post post = posts.get(position);

        h.tvDate.setText(DateUtil.dateToString(post.getTimeCreated(), "yyyy.MM.dd"));

        if(post.getType().equals(Post.TYPE_FAQ)) {
            h.tvDate.setVisibility(View.GONE);
        }

        h.tvTitle.setText(post.getTitle());

        h.tvMessage.setText(post.getContent());

        if(position == getCount() - 1) {
            h.viewLine.setVisibility(View.GONE);
        } else {
            h.viewLine.setVisibility(View.VISIBLE);
        }

        Boolean isExpand = (Boolean)post.getTag();
        if(isExpand != null && isExpand.booleanValue()) {
            h.viewMessage.setVisibility(View.VISIBLE);

            h.tvTitle.setTextColor(ContextCompat.getColor(contextHelper.getContext(), R.color.green));
            h.tvDate.setTextColor(ContextCompat.getColor(contextHelper.getContext(), R.color.green));

        } else {
            h.viewMessage.setVisibility(View.GONE);
            h.tvTitle.setTextColor(ContextCompat.getColor(contextHelper.getContext(), R.color.black));
            h.tvDate.setTextColor(ContextCompat.getColor(contextHelper.getContext(), R.color.gray));

        }


        h.viewTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean isExpand = (Boolean)post.getTag();

                if(isExpand != null && isExpand.booleanValue()) {
                    post.setTag(new Boolean(false));
                } else {
                    post.setTag(new Boolean(true));

                    PostManager.getInstance(contextHelper).count(
                            post.getId(),
                            new Response.Listener<PostData>() {
                                @Override
                                public void onResponse(PostData response) {
                                }
                            },
                            null
                    );

                }

                PostAdapter.this.notifyDataSetChanged();
            }
        });

        return convertView;
    }


//    void expand(final View v) {
//        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        final int targetHeight = PixelUtil.dpToPx(contextHelper.getContext(), 120);//v.getMeasuredHeight();
//
//        v.getLayoutParams().height = 0;
//        v.setVisibility(View.VISIBLE);
//        Animation a = new Animation()
//        {
//            @Override
//            protected void applyTransformation(float interpolatedTime, Transformation t) {
//                v.getLayoutParams().height = interpolatedTime == 1
//                        ? ViewGroup.LayoutParams.WRAP_CONTENT
//                        : (int)(targetHeight * interpolatedTime);
//                v.requestLayout();
//            }
//
//            @Override
//            public boolean willChangeBounds() {
//                return true;
//            }
//        };
//
//        // 1dp/ms
//        a.setDuration(200);//(int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
//        v.startAnimation(a);
//    }
//
//    public void collapse(final View v) {
//        final int initialHeight = v.getMeasuredHeight();
//
//        Animation a = new Animation()
//        {
//            @Override
//            protected void applyTransformation(float interpolatedTime, Transformation t) {
//                if(interpolatedTime == 1){
//                    v.setVisibility(View.GONE);
//                }else{
//                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
//                    v.requestLayout();
//                }
//            }
//
//            @Override
//            public boolean willChangeBounds() {
//                return true;
//            }
//        };
//
//        // 1dp/ms
//        a.setDuration(200);//(int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
//        v.startAnimation(a);
//    }

}


