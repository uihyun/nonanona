package com.nuums.nuums.fragment.misc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.nuums.nuums.R;
import com.nuums.nuums.adapter.AskAdapter;
import com.nuums.nuums.model.ask.Ask;
import com.nuums.nuums.model.ask.AskData;
import com.nuums.nuums.model.ask.AskList;
import com.nuums.nuums.model.ask.AskListData;
import com.nuums.nuums.model.ask.AskManager;
import com.yongtrim.lib.fragment.ListFragment;
import com.yongtrim.lib.log.Logger;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.model.list.List;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.ui.UltraButton;
import com.yongtrim.lib.ui.UltraListView;
import com.yongtrim.lib.ui.sweetalert.SweetAlertDialog;

import java.util.concurrent.CountDownLatch;

import de.greenrobot.event.EventBus;

/**
 * Created by YongTrim on 16. 6. 15. for nuums_ad
 */
public class TutorialFragment extends ListFragment {
    final String TAG = "TutorialFragment";

    ImageView ivGuide0;
    ImageView ivGuide1;
    TextView tvContent;
    UltraButton btnNext;

    int page = 0;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tutorial, container, false);

        ivGuide0 = (ImageView)view.findViewById(R.id.ivGuide0);

        ivGuide1 = (ImageView)view.findViewById(R.id.ivGuide1);
        tvContent = (TextView)view.findViewById(R.id.tvContent);

        btnNext = (UltraButton)view.findViewById(R.id.btnNext);

        refresh();
        return view;
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

    @Override
    public void onPause() {
        super.onPause();
    }

    void refresh() {
        ivGuide0.setVisibility(View.GONE);
        ivGuide1.setVisibility(View.GONE);

        if(page == 0) {
            ivGuide0.setVisibility(View.VISIBLE);
            btnNext.setText("다음");



            String str0 = "홈";
            String str1 = "에서 다른 사람들의 나눔 물품을 확인 할 수 있어요";


            SpannableString text = new SpannableString(str0 + str1);

            int offset = 0;
            text.setSpan(new ForegroundColorSpan(ContextCompat.getColor(contextHelper.getContext(), R.color.yellow)), offset, offset + str0.length(), 0);

            tvContent.setText(text);
        } else {
            ivGuide1.setVisibility(View.VISIBLE);
            btnNext.setText("나눔 받으러 가기");


            String str0 = "등록 버튼";
            String str1 = "을 누르면 나눔 물품을 등록할 수 있어요";


            SpannableString text = new SpannableString(str0 + str1);

            int offset = 0;
            text.setSpan(new ForegroundColorSpan(ContextCompat.getColor(contextHelper.getContext(), R.color.yellow)), offset, offset + str0.length(), 0);

            tvContent.setText(text);
        }
    }

    public void onButtonClicked(View v) {

        switch (v.getId()) {
            case R.id.btnClose:
                getActivity().finish();
                break;
            case R.id.btnNext:
                page++;
                if(page > 1) {
                    getActivity().finish();
                    return;
                }
                refresh();
                break;
        }
    }
}


