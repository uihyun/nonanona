package com.nuums.nuums.fragment.misc;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.nuums.nuums.R;
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.fragment.ABaseFragment;
import com.yongtrim.lib.log.Logger;
import com.yongtrim.lib.message.MessageManager;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.model.config.ConfigManager;
import com.yongtrim.lib.model.user.LoginManager;
import com.yongtrim.lib.model.user.UserData;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.sns.SNSLoginoutListener;
import com.yongtrim.lib.sns.facebook.FacebookManager;
import com.yongtrim.lib.sns.kakao.KakaoManager;
import com.yongtrim.lib.sns.twitter.TwitterManager;
import com.yongtrim.lib.ui.UltraButton;
import com.yongtrim.lib.ui.sweetalert.SweetAlertDialog;

import java.util.concurrent.CountDownLatch;

/**
 * nuums / com.nuums.nuums.fragment.misc
 * <p/>
 * Created by Uihyun on 16. 1. 23..
 */
public class AlarmFragment extends ABaseFragment {
    private final String TAG = getClass().getSimpleName();

    View mainView;

    UltraButton btnNanumComment;
    UltraButton btnNanumWon;
    UltraButton btnNanumWarn;
    UltraButton btnNanumKeyword;
    UltraButton btnNanumZzim;

    UltraButton btnDelivery;
    UltraButton btnTalk;

    UltraButton btnReviewComment;
    UltraButton btnReviewTag;

    UltraButton btnEventStart;
    UltraButton btnEventFinish;


    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        contextHelper.getActivity().setupActionBar("알람설정");
        contextHelper.getActivity().setBackButtonVisibility(false);
        contextHelper.getActivity().setImageButtonAndVisiable(R.drawable.del);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        final MessageManager.MessagePreference preference = MessageManager.getInstance(contextHelper).getPreference();

        mainView = inflater.inflate(R.layout.fragment_alarm, container, false);

        TextView tvHeader0 = (TextView)mainView.findViewById(R.id.viewHeader0).findViewById(R.id.tvTitle);
        tvHeader0.setText("나눔알림");

        TextView tvAttribute0_0 = (TextView)mainView.findViewById(R.id.viewAttribute0_0).findViewById(R.id.tvTitle);
        tvAttribute0_0.setText("나눔글에 댓글이 달렸을 때");
        btnNanumComment = (UltraButton)mainView.findViewById(R.id.viewAttribute0_0).findViewById(R.id.btnSet);
        btnNanumComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preference.putNanumComment(!preference.getIsNanumComment());
                refresh();
            }
        });

        TextView tvAttribute0_1 = (TextView)mainView.findViewById(R.id.viewAttribute0_1).findViewById(R.id.tvTitle);
        tvAttribute0_1.setText("나눔에 당첨되었을 때");
        btnNanumWon = (UltraButton)mainView.findViewById(R.id.viewAttribute0_1).findViewById(R.id.btnSet);
        btnNanumWon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preference.putNanumWon(!preference.getIsNanumWon());
                refresh();
            }
        });

        TextView tvAttribute0_2 = (TextView)mainView.findViewById(R.id.viewAttribute0_2).findViewById(R.id.tvTitle);
        tvAttribute0_2.setText("나눔 마감전 1시간 알림");
        btnNanumWarn = (UltraButton)mainView.findViewById(R.id.viewAttribute0_2).findViewById(R.id.btnSet);
        btnNanumWarn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preference.putNanumWarn(!preference.getIsNanumWarn());
                refresh();
            }
        });

        TextView tvAttribute0_3 = (TextView)mainView.findViewById(R.id.viewAttribute0_3).findViewById(R.id.tvTitle);
        tvAttribute0_3.setText("관심 키워드 나눔 알림");
        btnNanumKeyword = (UltraButton)mainView.findViewById(R.id.viewAttribute0_3).findViewById(R.id.btnSet);
        btnNanumKeyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preference.putNanumKeyword(!preference.getIsNanumKeyword());
                refresh();
            }
        });

        TextView tvAttribute0_4 = (TextView)mainView.findViewById(R.id.viewAttribute0_4).findViewById(R.id.tvTitle);
        tvAttribute0_4.setText("나눔이 찜 되었을 때");
        btnNanumZzim = (UltraButton)mainView.findViewById(R.id.viewAttribute0_4).findViewById(R.id.btnSet);
        btnNanumZzim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preference.putNanumZzim(!preference.getIsNanumZzim());
                refresh();
            }
        });

        TextView tvHeader1 = (TextView)mainView.findViewById(R.id.viewHeader1).findViewById(R.id.tvTitle);
        tvHeader1.setText("배송");
        TextView tvAttribute1_0 = (TextView)mainView.findViewById(R.id.viewAttribute1_0).findViewById(R.id.tvTitle);
        tvAttribute1_0.setText("배송 진행시");
        btnDelivery = (UltraButton)mainView.findViewById(R.id.viewAttribute1_0).findViewById(R.id.btnSet);
        btnDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preference.putDelivery(!preference.getIsDelivery());
                refresh();
            }
        });

        TextView tvHeader2 = (TextView)mainView.findViewById(R.id.viewHeader2).findViewById(R.id.tvTitle);
        tvHeader2.setText("메세지");
        TextView tvAttribute2_0 = (TextView)mainView.findViewById(R.id.viewAttribute2_0).findViewById(R.id.tvTitle);
        tvAttribute2_0.setText("톡 메시지를 받았을 때");
        btnTalk = (UltraButton)mainView.findViewById(R.id.viewAttribute2_0).findViewById(R.id.btnSet);
        btnTalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preference.putTalk(!preference.getIsTalk());
                refresh();
            }
        });


        TextView tvHeader3 = (TextView)mainView.findViewById(R.id.viewHeader3).findViewById(R.id.tvTitle);
        tvHeader3.setText("후기 / 보답하기");
        TextView tvAttribute3_0 = (TextView)mainView.findViewById(R.id.viewAttribute3_0).findViewById(R.id.tvTitle);
        tvAttribute3_0.setText("게시글에 댓글이 달렸을 때");
        btnReviewComment = (UltraButton)mainView.findViewById(R.id.viewAttribute3_0).findViewById(R.id.btnSet);
        btnReviewComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preference.putReviewComment(!preference.getIsReviewComment());
                refresh();
            }
        });

        TextView tvAttribute3_1 = (TextView)mainView.findViewById(R.id.viewAttribute3_1).findViewById(R.id.tvTitle);
        tvAttribute3_1.setText("게시글에 태그 되었을 때");
        btnReviewTag = (UltraButton)mainView.findViewById(R.id.viewAttribute3_1).findViewById(R.id.btnSet);
        btnReviewTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preference.putReviewTag(!preference.getIsReviewTag());
                refresh();
            }
        });

        TextView tvHeader4 = (TextView)mainView.findViewById(R.id.viewHeader4).findViewById(R.id.tvTitle);
        tvHeader4.setText("이벤트");

        TextView tvAttribute4_0 = (TextView)mainView.findViewById(R.id.viewAttribute4_0).findViewById(R.id.tvTitle);
        tvAttribute4_0.setText("이벤트 진행알림 정보");
        btnEventStart = (UltraButton)mainView.findViewById(R.id.viewAttribute4_0).findViewById(R.id.btnSet);
        btnEventStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preference.putEventStart(!preference.getIsEventStart());
                refresh();
            }
        });

        TextView tvAttribute4_1 = (TextView)mainView.findViewById(R.id.viewAttribute4_1).findViewById(R.id.tvTitle);
        tvAttribute4_1.setText("이벤트 결과 발표시");
        btnEventFinish = (UltraButton)mainView.findViewById(R.id.viewAttribute4_1).findViewById(R.id.btnSet);
        btnEventFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preference.putEventFinish(!preference.getIsEventFinish());
                refresh();
            }
        });

        refresh();
        return mainView;
    }

    void setButton(UltraButton button, boolean isOn) {
        if(isOn) {
            button.setIconResource(R.drawable.bellgreen);
        } else {
            button.setIconResource(R.drawable.bellgray);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    void refresh() {
        MessageManager.MessagePreference preference = MessageManager.getInstance(contextHelper).getPreference();

        setButton(btnNanumComment, preference.getIsNanumComment());
        setButton(btnNanumWon, preference.getIsNanumWon());
        setButton(btnNanumWarn, preference.getIsNanumWarn());
        setButton(btnNanumKeyword, preference.getIsNanumKeyword());
        setButton(btnNanumZzim, preference.getIsNanumZzim());
        setButton(btnDelivery, preference.getIsDelivery());
        setButton(btnTalk, preference.getIsTalk());
        setButton(btnReviewComment, preference.getIsReviewComment());
        setButton(btnReviewTag, preference.getIsReviewTag());

        setButton(btnEventStart, preference.getIsEventStart());
        setButton(btnEventFinish, preference.getIsEventFinish());
    }

    public void onButtonClicked(View v) {

        switch(v.getId()) {
            case R.id.actionbarImageButton:
                contextHelper.getActivity().finish();
                break;
        }
    }


    public void onEvent(PushMessage pushMessage) {
    }



}




