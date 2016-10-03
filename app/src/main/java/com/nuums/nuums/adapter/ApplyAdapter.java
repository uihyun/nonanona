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
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nuums.nuums.R;
import com.nuums.nuums.activity.BaseActivity;
import com.nuums.nuums.model.alarm.Alarm;
import com.nuums.nuums.model.apply.Apply;
import com.nuums.nuums.model.apply.ApplyData;
import com.nuums.nuums.model.apply.ApplyManager;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.model.misc.CodeName;
import com.yongtrim.lib.model.photo.PhotoManager;
import com.yongtrim.lib.model.post.Post;
import com.yongtrim.lib.model.post.PostData;
import com.yongtrim.lib.model.post.PostManager;
import com.yongtrim.lib.ui.CircularNetworkImageView;
import com.yongtrim.lib.ui.UltraButton;
import com.yongtrim.lib.ui.sweetalert.SweetAlertDialog;
import com.yongtrim.lib.util.DateUtil;
import com.yongtrim.lib.util.MiscUtil;

import java.lang.reflect.Type;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * nuums / com.nuums.nuums.adapter
 * <p/>
 * Created by Uihyun on 16. 1. 21..
 */
public class ApplyAdapter extends BaseAdapter {

    private List<Apply> applys;
    private ContextHelper contextHelper;

    class Holder {
        TextView tvDate;
        TextView tvTitle;
        TextView tvCompany;
        TextView tvStatus;
        UltraButton btnConfirm;

        View viewLine;

        public View set(View v) {
            v.setTag(this);
            tvDate = (TextView) v.findViewById(R.id.tvDate);
            tvTitle = (TextView) v.findViewById(R.id.tvTitle);
            tvCompany = (TextView) v.findViewById(R.id.tvCompany);
            tvStatus = (TextView) v.findViewById(R.id.tvStatus);
            btnConfirm = (UltraButton)v.findViewById(R.id.btnConfirm);
            viewLine = v.findViewById(R.id.viewLine);

            return v;
        }
    }

    public ApplyAdapter(ContextHelper contextHelper) {
        super();
        this.contextHelper = contextHelper;
    }

    public void setData(List<Apply> applys) {
        this.applys = applys;
    }

    public List<Apply> getData() {
        return this.applys;
    }

    @Override
    public int getCount() {
        if(applys == null)
            return 0;
        return applys.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return applys.get(position);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)parent.getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = new Holder().set(mInflater.inflate(R.layout.cell_apply, parent, false));
        }
        final Holder h = (Holder)convertView.getTag();

        final Apply apply = applys.get(position);

        h.tvDate.setText(DateUtil.dateToString(apply.getTimeCreated(), "yyyy.MM.dd"));

        h.tvTitle.setText(apply.getTitle());

        h.btnConfirm.setVisibility(View.GONE);
        h.tvStatus.setVisibility(View.GONE);


        if(apply.getCompany() != null) {
            h.tvCompany.setText(apply.getCompany().getName());
        } else {
            h.tvCompany.setText("-");
        }
        switch(apply.getStatus()) {
            case Apply.STATUS_ACCEPT:
                h.tvStatus.setVisibility(View.VISIBLE);
                h.tvStatus.setText("배송준비");

                break;
            case Apply.STATUS_DOING: {
                h.tvStatus.setVisibility(View.VISIBLE);

                SpannableString text = new SpannableString("배송중");
                setLink(text, 0, 3, apply);
                h.tvStatus.setText(text);
                h.tvStatus.setMovementMethod(LinkMovementMethod.getInstance());


            }
                break;
            case Apply.STATUS_DONE:
                h.btnConfirm.setVisibility(View.VISIBLE);

                h.btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contextHelper.showProgress(null);
                        apply.setStatus(Apply.STATUS_CONFIRM);

                        ApplyManager.getInstance(contextHelper).update(
                                apply,
                                new Response.Listener<ApplyData>() {
                                    @Override
                                    public void onResponse(ApplyData response) {
                                        if (response.isSuccess()) {

                                            contextHelper.hideProgress();

                                            response.apply.patch(contextHelper);
                                            EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_CHANGE_APPLY).setObject(response.apply));

                                            Toast.makeText(contextHelper.getActivity(), "감사합니다.", Toast.LENGTH_SHORT).show();

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
                });


                break;
            case Apply.STATUS_CONFIRM:
                h.tvStatus.setVisibility(View.VISIBLE);
                h.tvStatus.setText("배송확정");

                break;
        }

        if(position == getCount() - 1) {
            h.viewLine.setVisibility(View.GONE);
        } else {
            h.viewLine.setVisibility(View.VISIBLE);
        }

        return convertView;
    }


    private void setLink(SpannableString text, int offset, int length, Apply apply) {
        //text.setSpan(new StyleSpan(Typeface.BOLD), offset, offset + length, 0);
        PolicyClickableSpan clickableSpan = new PolicyClickableSpan(apply);
        text.setSpan(clickableSpan, offset, offset + length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }


    class PolicyClickableSpan extends ClickableSpan {
        Apply apply;

        public PolicyClickableSpan(Apply apply) {
            this.apply = apply;
        }

        @Override
        public void onClick(View view) {

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(apply.getCompany().getUrl() + apply.getNumber()));
            contextHelper.getActivity().startActivity(browserIntent);

        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.linkColor = ContextCompat.getColor(contextHelper.getContext(), R.color.pink);
            ds.setColor(ds.linkColor);
            //ds.setUnderlineText(false);
        }
    }
}


