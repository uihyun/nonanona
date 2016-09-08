package com.nuums.nuums.holder;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.nuums.nuums.AppController;
import com.nuums.nuums.R;
import com.nuums.nuums.model.nanum.Nanum;
import com.yongtrim.lib.Config;
import com.yongtrim.lib.log.Logger;
import com.yongtrim.lib.ui.CircularNetworkImageView;
import com.yongtrim.lib.ui.CustomNetworkImageView;
import com.yongtrim.lib.ui.UltraButton;
import com.yongtrim.lib.util.MiscUtil;
import com.yongtrim.lib.util.PixelUtil;
import com.yongtrim.lib.util.UIUtil;

import java.util.Date;

/**
 * nuums / com.nuums.nuums.holder
 * <p/>
 * Created by yongtrim.com on 15. 12. 28..
 */
public class NanumHolder {

    final String TAG = "NanumHolder";

    public UltraButton btnBookmark;
    public CircularNetworkImageView ivAvatar;
    public TextView tvNickname;
    public TextView tvTitle;
    public TextView tvAddress;
    public TextView tvDate;
    public View viewAddress;

    public View padding0;
    public View padding1;
    public View padding2;

    public View viewMarkLeft;
    public View viewMarkRight;

    public View viewTimer;
    public TextView tvTimerHour;
    public TextView tvTimerMinute;

    public View viewFinish;

    public CustomNetworkImageView ivThumb[] = new CustomNetworkImageView[3];

    Context context;

    public View set(View v, Context context, boolean isCell) {
        this.context = context;
        v.setTag(this);

        btnBookmark = (UltraButton)v.findViewById(R.id.btnBookmark);

        ivAvatar = (CircularNetworkImageView)v.findViewById(R.id.ivAvatar);
        tvNickname = (TextView)v.findViewById(R.id.tvNickname);
        tvTitle = (TextView)v.findViewById(R.id.tvTitle);
        tvAddress = (TextView)v.findViewById(R.id.tvAddress);
        tvDate = (TextView)v.findViewById(R.id.tvDate);
        viewAddress = v.findViewById(R.id.viewAddress);

        ivThumb[0] = (CustomNetworkImageView)v.findViewById(R.id.ivThumb0);
        ivThumb[1] = (CustomNetworkImageView)v.findViewById(R.id.ivThumb1);
        ivThumb[2] = (CustomNetworkImageView)v.findViewById(R.id.ivThumb2);
        UIUtil.setDivided(ivThumb, v.getContext());

        padding0 = v.findViewById(R.id.padding0);
        padding1 = v.findViewById(R.id.padding1);
        padding2 = v.findViewById(R.id.padding2);

        viewMarkLeft = v.findViewById(R.id.viewMarkLeft);
        viewMarkRight = v.findViewById(R.id.viewMarkRight);


        viewFinish = v.findViewById(R.id.viewFinish);
        if(isCell) {
            tvTimerHour = (TextView)v.findViewById(R.id.tvTimerHour);
            tvTimerMinute = (TextView)v.findViewById(R.id.tvTimerMinute);

            viewTimer = v.findViewById(R.id.viewTimer);

            v.findViewById(R.id.viewList).setVisibility(View.VISIBLE);
            v.findViewById(R.id.viewViewer).setVisibility(View.GONE);
        } else {
            viewTimer = v.findViewById(R.id.viewTimerBig);

            tvTimerHour = (TextView)viewTimer.findViewById(R.id.tvTimerHour);
            tvTimerMinute = (TextView)viewTimer.findViewById(R.id.tvTimerMinute);

            tvTimerHour.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            tvTimerMinute.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);

            TextView tvHourTag = (TextView)viewTimer.findViewById(R.id.tvHourTag);
            TextView tvMinuteTag = (TextView)viewTimer.findViewById(R.id.tvMinuteTag);

            tvHourTag.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 8);
            tvMinuteTag.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 8);

            v.findViewById(R.id.viewList).setVisibility(View.GONE);
            v.findViewById(R.id.viewViewer).setVisibility(View.VISIBLE);

        }
        return v;
    }


    public void setMark(Nanum nanum, Context context) {
        viewMarkLeft.setVisibility(View.VISIBLE);
        viewMarkRight.setVisibility(View.VISIBLE);

        if(nanum.getOwner().getRole() != null && nanum.getOwner().getRole().equals("ADMIN")) {
            ViewGroup.LayoutParams param = viewMarkLeft.getLayoutParams();
            param.height = ViewGroup.LayoutParams.MATCH_PARENT;
            viewMarkLeft.setLayoutParams(param);
            viewMarkLeft.setBackgroundColor(ContextCompat.getColor(context, R.color.green));

            param = viewMarkRight.getLayoutParams();
            param.height = ViewGroup.LayoutParams.MATCH_PARENT;
            viewMarkRight.setLayoutParams(param);
            viewMarkRight.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
            viewMarkRight.setVisibility(View.VISIBLE);
        } else {
            ViewGroup.LayoutParams param = viewMarkLeft.getLayoutParams();
            param.height = PixelUtil.dpToPx(context, 5);
            viewMarkLeft.setLayoutParams(param);
            viewMarkLeft.setBackgroundColor(ContextCompat.getColor(context, R.color.green));

            viewMarkRight.setVisibility(View.GONE);
            switch(nanum.getMethod()) {
                case Nanum.METHOD_ARRIVAL:
                    viewMarkLeft.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
                    break;
                case Nanum.METHOD_SELECT:
                    viewMarkLeft.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
                    break;
                case Nanum. METHOD_RANDOM:
                    viewMarkLeft.setBackgroundColor(ContextCompat.getColor(context, R.color.black));
                    break;
            }
        }
    }

    public void setTimer(Nanum nanum, Context context) {
        if(!nanum.getStatus().equals(Nanum.STATUS_ONGOING)) {
            viewTimer.setVisibility(View.GONE);
            return;
        }

        Date date = new Date();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long different = hoursInMilli* Config.NANUM_HOUR - (date.getTime() - nanum.getTimeOngoing().getTime());
        //long different = minutesInMilli*2  - (date.getTime() - nanum.getTimeOngoing().getTime());
        long diff = different;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        if(diff > 0) {
            tvTimerHour.setText(String.format("%02d", elapsedHours));
            tvTimerMinute.setText(String.format("%02d", elapsedMinutes));
            viewTimer.setVisibility(View.VISIBLE);
        } else {
            viewTimer.setVisibility(View.GONE);
        }
    }
}
