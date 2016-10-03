package com.nuums.nuums.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nuums.nuums.R;
import com.nuums.nuums.activity.BaseActivity;
import com.nuums.nuums.model.misc.Comment;
import com.nuums.nuums.model.nanum.Nanum;
import com.nuums.nuums.model.review.Review;
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.photo.PhotoManager;
import com.yongtrim.lib.ui.CircularNetworkImageView;
import com.yongtrim.lib.util.MiscUtil;

import java.util.List;

/**
 * nuums / com.nuums.nuums.adapter
 * <p/>
 * Created by Uihyun on 16. 1. 19..
 */
public class CommentInReviewAdapter extends BaseAdapter {

    Review review;
    private List<Comment> comments;
    private ContextHelper contextHelper;


    class Holder {

        TextView tvContent;

        public View set(View v) {
            v.setTag(this);

            tvContent = (TextView)v.findViewById(R.id.tvContent);
            return v;
        }
    }

    public CommentInReviewAdapter(ContextHelper contextHelper) {
        super();
        this.contextHelper = contextHelper;
    }


    public void setData(Review review, List<Comment> comments) {
        this.review = review;
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
            convertView = new Holder().set(mInflater.inflate(R.layout.cell_commentinreview, parent, false));
        }
        final Holder h = (Holder)convertView.getTag();

        Comment comment = comments.get(position);


        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(comment.getOwner().getNicknameSafe());

        stringBuilder.append(" " + comment.getMessage());


        SpannableString text = new SpannableString(stringBuilder.toString());
        setLink(text, 0, comment.getOwner().getNicknameSafe().length(), comment.getOwner());

        h.tvContent.setText(text);
        h.tvContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean ret = false;
                CharSequence text = ((TextView) v).getText();
                Spannable stext = Spannable.Factory.getInstance().newSpannable(text);
                TextView widget = (TextView) v;
                int action = event.getAction();

                if (action == MotionEvent.ACTION_UP ||
                        action == MotionEvent.ACTION_DOWN) {
                    int x = (int) event.getX();
                    int y = (int) event.getY();

                    x -= widget.getTotalPaddingLeft();
                    y -= widget.getTotalPaddingTop();

                    x += widget.getScrollX();
                    y += widget.getScrollY();

                    Layout layout = widget.getLayout();
                    int line = layout.getLineForVertical(y);
                    int off = layout.getOffsetForHorizontal(line, x);

                    ClickableSpan[] link = stext.getSpans(off, off, ClickableSpan.class);

                    if (link.length != 0) {
                        if (action == MotionEvent.ACTION_UP) {
                            link[0].onClick(widget);
                        }
                        ret = true;
                    }
                }
                return ret;
            }
        });
        return convertView;
    }


    private void setLink(SpannableString text, int offset, int length, NsUser user) {
        PolicyClickableSpan clickableSpan = new PolicyClickableSpan(user);
        text.setSpan(clickableSpan, offset, offset + length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    class PolicyClickableSpan extends ClickableSpan {
        private NsUser user;

        public PolicyClickableSpan(NsUser user) {
            this.user = user;
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(contextHelper.getContext(), BaseActivity.class);
            i.putExtra("activityCode", BaseActivity.ActivityCode.REVIEWLIST.ordinal());
            i.putExtra("owner", user.toString());
            contextHelper.getContext().startActivity(i);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.linkColor = ContextCompat.getColor(contextHelper.getContext(), R.color.green);
            ds.setColor(ds.linkColor);
            ds.setUnderlineText(false);
        }
    }
}


