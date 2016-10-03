package com.nuums.nuums.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.nuums.nuums.AppController;
import com.nuums.nuums.R;
import com.nuums.nuums.activity.BaseActivity;
import com.nuums.nuums.holder.NanumHolder;
import com.nuums.nuums.model.misc.CommentManager;
import com.nuums.nuums.model.nanum.Nanum;
import com.nuums.nuums.model.nanum.NanumData;
import com.nuums.nuums.model.nanum.NanumManager;
import com.nuums.nuums.model.report.Report;
import com.nuums.nuums.model.review.Review;
import com.nuums.nuums.model.review.ReviewData;
import com.nuums.nuums.model.review.ReviewManager;
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.Config;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.model.ACommonData;
import com.yongtrim.lib.model.photo.PhotoManager;
import com.yongtrim.lib.model.post.Post;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.ui.CircularNetworkImageView;
import com.yongtrim.lib.ui.CustomNetworkImageView;
import com.yongtrim.lib.ui.UltraButton;
import com.yongtrim.lib.ui.sweetalert.SweetAlertDialog;
import com.yongtrim.lib.util.MiscUtil;
import com.yongtrim.lib.util.PixelUtil;
import com.yongtrim.lib.util.UIUtil;

import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * nuums / com.nuums.nuums.adapter
 * <p/>
 * Created by Uihyun on 16. 1. 18..
 */
public class ReviewAdapter extends BaseAdapter {

    private List<Review> reviews;
    private ContextHelper contextHelper;

    public EditText editTextFocus;

    class Holder {

        //public View viewLine;

        public TextView tvNickname;
        public TextView tvDate;
        public CustomNetworkImageView ivPhoto;

        public UltraButton btnStar;
        public TextView tvStarCnt;

        //public UltraButton btnComment;
        public UltraButton btnMore;
        public TextView tvContent;

        Context context;

        public ListView listViewComment;
        CommentInReviewAdapter commentAdapter;

        EditText etComment;
        UltraButton btnEnter;


        public View set(View v, Context context) {
            this.context = context;
            v.setTag(this);

            //viewLine = v.findViewById(R.id.viewLine);

            tvNickname = (TextView)v.findViewById(R.id.tvNickname);
            tvDate = (TextView)v.findViewById(R.id.tvDate);

            ivPhoto = (CustomNetworkImageView)v.findViewById(R.id.ivPhoto);
            UIUtil.setRatio(ivPhoto, context, 320, 300);

            btnStar = (UltraButton)v.findViewById(R.id.btnStar);
            tvStarCnt = (TextView)v.findViewById(R.id.tvStarCnt);

            //btnComment = (UltraButton)v.findViewById(R.id.btnComment);
            btnMore = (UltraButton)v.findViewById(R.id.btnMore);
            tvDate = (TextView)v.findViewById(R.id.tvDate);
            tvContent = (TextView)v.findViewById(R.id.tvContent);

            listViewComment = (ListView)v.findViewById(R.id.listViewComment);
            commentAdapter = new CommentInReviewAdapter(contextHelper);
            listViewComment.setAdapter(commentAdapter);

            etComment = (EditText)v.findViewById(R.id.etComment);
            btnEnter = (UltraButton)v.findViewById(R.id.btnEnter);


            etComment.addTextChangedListener(new MyWatcher(etComment));


//            commentAdapter = new CommentAdapter(contextHelper, listViewComment);
//            //--commentAdapter.setData(nanum.getComments());
//            listView.setAdapter(commentAdapter);



            return v;
        }

    }


    public ReviewAdapter(ContextHelper contextHelper) {
        super();
        this.contextHelper = contextHelper;
    }


    public void setData(List<Review> reviews) {
        this.reviews = reviews;
    }



    public List<Review> getData() {
        return this.reviews;
    }


    @Override
    public int getCount() {
        if(reviews == null)
            return 0;
        return reviews.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return reviews.get(position);
    }


    void refreshStartCount(TextView tvStarCnt, int count) {
        if(count > 0) {
            tvStarCnt.setVisibility(View.VISIBLE);
        } else {
            tvStarCnt.setVisibility(View.GONE);
        }
        tvStarCnt.setText("" + count);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)parent.getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = new Holder().set(mInflater.inflate(R.layout.cell_review, parent, false), parent.getContext());

        }
        final Holder h = (Holder)convertView.getTag();

        final Review review = reviews.get(position);


//        if(position == 0) {
//            h.viewLine.setVisibility(View.GONE);
//        } else {
//
//            h.viewLine.setVisibility(View.VISIBLE);
//        }

        h.tvNickname.setText(review.getOwner().getNicknameSafe());

        h.tvNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(contextHelper.getContext(), BaseActivity.class);
                i.putExtra("activityCode", BaseActivity.ActivityCode.REVIEWLIST.ordinal());
                i.putExtra("owner", review.getOwner().toString());
                contextHelper.getContext().startActivity(i);
            }
        });
        h.tvDate.setText(MiscUtil.getPostTime(review.getTimeCreated()));

        contextHelper.setPhoto(h.ivPhoto, review.getPhoto());
        refreshStartCount(h.tvStarCnt, (review.getStars().size() + review.startDummy));

        if(review.isStarted()) {
            h.btnStar.setIconResource(R.drawable.grayheart_on);
        } else {
            h.btnStar.setIconResource(R.drawable.grayheart);
        }
        h.btnStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!UserManager.getInstance(contextHelper).getMe().isLogin()) {
                    contextHelper.showLogin();
                    return;
                }

                if (ReviewManager.getInstance(contextHelper).isStaring())
                    return;

                ReviewManager.getInstance(contextHelper).setIsStaring(true);

                review.setStarted(!review.isStarted());

                if(review.isStarted()) {
                    h.btnStar.setIconResource(R.drawable.grayheart_on);
                    review.startDummy = 1;
                    refreshStartCount(h.tvStarCnt, (review.getStars().size() + review.startDummy));
                } else {
                    h.btnStar.setIconResource(R.drawable.grayheart);
                    review.startDummy = -1;
                }
                h.tvStarCnt.setText("" + (review.getStars().size()+review.startDummy));
                ReviewManager.getInstance(contextHelper).star(
                        review, review.isStarted(),
                        new Response.Listener<ReviewData>() {
                            @Override
                            public void onResponse(ReviewData response) {
                                ReviewManager.getInstance(contextHelper).setIsStaring(false);
                                review.startDummy = 0;

                                if (response.isSuccess()) {
                                    response.review.patch(contextHelper);
                                    EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_CHANGE_REVIEW).setObject(response.review));
                                    ReviewAdapter.this.notifyDataSetChanged();
                                } else {
                                }
                            }
                        },
                        null
                );
            }
        });


        h.etComment.setTag(review);
        h.etComment.setText((String)review.getTag());

        h.etComment.setImeOptions(EditorInfo.IME_ACTION_SEND);
        h.etComment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendComment(review, v.getText().toString());
                    h.etComment.setText("");
                    return true;
                }
                return false;
            }
        });

        h.etComment.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    editTextFocus = h.etComment;
                    h.etComment.setGravity(Gravity.LEFT);
                } else if(TextUtils.isEmpty(h.etComment.getText().toString())) {
                    h.etComment.setGravity(Gravity.RIGHT);
                    editTextFocus = null;
                }
            }
         });

        h.btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComment(review, h.etComment.getText().toString());
                h.etComment.setText("");

            }
        });





        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(review.getContent());
        if(review.getTags() != null) {
            for(NsUser user : review.getTags()) {
                stringBuilder.append(" @" + user.getNicknameSafe());
            }
        }

        int offset = 0;
        SpannableString text = new SpannableString(stringBuilder.toString());
        offset += review.getContent().length();
        if(review.getTags() != null) {
            for(NsUser user : review.getTags()) {
                offset++;
                setLink(text, offset,  user.getNicknameSafe().length() + 1, user);
                offset += user.getNicknameSafe().length() + 1;

                stringBuilder.append(" @" + user.getNicknameSafe());
            }
        }

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



        h.commentAdapter.setData(review, review.getComments());
        h.commentAdapter.notifyDataSetChanged();

        h.listViewComment.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            h.listViewComment.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            h.listViewComment.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                        contextHelper.setListViewHeightBasedOnChildren(h.listViewComment);
                    }
                });



        h.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserManager.getInstance(contextHelper).getMe().isSame(review.getOwner())) {
                    new SweetAlertDialog(contextHelper.getContext(), SweetAlertDialog.SELECT_TYPE)
                            .setTitleText("선택해 주세요")
                            .showCloseButton(true)
                            .setArrayValue(new String[]{"수정하기", "삭제하기"})
                            .setSelectListener(new SweetAlertDialog.OnSweetSelectListener() {
                                @Override
                                public void onSelected(SweetAlertDialog sweetAlertDialog, int index) {
                                    sweetAlertDialog.dismissWithAnimation();

                                    switch (index) {
                                        case 0: {
                                            Intent i = new Intent(contextHelper.getContext(), BaseActivity.class);
                                            i.putExtra("activityCode", BaseActivity.ActivityCode.REVIEWEDIT.ordinal());
                                            i.putExtra("isModify", true);
                                            i.putExtra("review", review.toString());
                                            contextHelper.getContext().startActivity(i);
                                        }
                                        break;
                                        case 1:
                                            new SweetAlertDialog(contextHelper.getContext())
                                                    .setContentText("삭제 하시겠습니까?")
                                                    .showCancelButton(true)
                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(final SweetAlertDialog sweetAlertDialog) {

                                                            sweetAlertDialog.dismissWithAnimation();

                                                            contextHelper.showProgress(null);

                                                            ReviewManager.getInstance(contextHelper).delete(
                                                                    review,
                                                                    new Response.Listener<ReviewData>() {
                                                                        @Override
                                                                        public void onResponse(final ReviewData response) {

                                                                            contextHelper.hideProgress();
                                                                            if (response.isSuccess()) {
                                                                                if(response.user != null)
                                                                                    UserManager.getInstance(contextHelper).setMe(response.user);
                                                                                
                                                                                response.review.patch(contextHelper);
                                                                                EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_DELETE_REVIEW).setObject(response.review));

                                                                            } else {
                                                                                new SweetAlertDialog(contextHelper.getContext())
                                                                                        .setContentText(response.getErrorMessage())
                                                                                        .show();
                                                                            }
                                                                        }
                                                                    },
                                                                    null
                                                            );


                                                        }
                                                    })
                                                    .show();

                                            break;
                                    }
                                }
                            })
                            .show();
                } else {
                    new SweetAlertDialog(contextHelper.getContext(), SweetAlertDialog.SELECT_TYPE)
                            .setTitleText("선택해 주세요")
                            .setArrayValue(new String[]{"신고하기"})
                            .showCloseButton(true)
                            .setSelectListener(new SweetAlertDialog.OnSweetSelectListener() {
                                @Override
                                public void onSelected(SweetAlertDialog sweetAlertDialog, int index) {
                                    sweetAlertDialog.dismissWithAnimation();

                                    switch (index) {
                                        case 0: {
                                            Intent i = new Intent(contextHelper.getContext(), BaseActivity.class);
                                            i.putExtra("activityCode", BaseActivity.ActivityCode.REPORT.ordinal());
                                            i.putExtra("type", Report.REPORTTYPE_REPORT);
                                            i.putExtra("target", review.getOwner().toString());

                                            contextHelper.getContext().startActivity(i);
                                        }
                                            break;
                                    }
                                }
                            })
                            .show();
                }

            }
        });

        return convertView;
    }


    private void setLink(SpannableString text, int offset, int length, NsUser user) {
        //text.setSpan(new StyleSpan(Typeface.BOLD), offset, offset + length, 0);
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

    class MyWatcher implements TextWatcher {

        private EditText edit;
        private Review review;
        public MyWatcher(EditText edit) {
            this.edit = edit;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //Log.d("TAG", "onTextChanged: " + s);
            this.review = (Review)edit.getTag();
            if (review != null) {
                review.setTag(s.toString());
            }
            if (s.length() > 0){
                // position the text type in the left top corner
                edit.setGravity(Gravity.LEFT);
            } else {
                // no text entered. Center the hint text.
                edit.setGravity(Gravity.RIGHT);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }


    void sendComment(Review review, String comment) {

        if(TextUtils.isEmpty(comment))
            return;

        contextHelper.showProgress(null);

        CommentManager.getInstance(contextHelper).createInReview(review, comment,
                new Response.Listener<ReviewData>() {
                    @Override
                    public void onResponse(ReviewData response) {
                        contextHelper.hideProgress();
                        if (response.isSuccess()) {
                            response.review.patch(contextHelper);
                            EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_CHANGE_REVIEW).setObject(response.review));

                        } else {
                            new SweetAlertDialog(contextHelper.getContext())
                                    .setContentText(response.getErrorMessage())
                                    .show();
                        }
                    }
                },
                null
        );
    }



}


