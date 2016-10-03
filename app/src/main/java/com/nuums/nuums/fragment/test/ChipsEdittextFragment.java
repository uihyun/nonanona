package com.nuums.nuums.fragment.test;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.nuums.nuums.R;
import com.nuums.nuums.adapter.UserAdapter;
import com.nuums.nuums.model.user.NsUser;
import com.nuums.nuums.model.user.NsUserSearch;
import com.yongtrim.lib.fragment.ABaseFragment;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.ui.ChipsMultiAutoCompleteTextview;

import java.util.ArrayList;
import java.util.List;

/**
 * nuums / com.nuums.nuums.fragment.test
 * <p/>
 * Created by Uihyun on 16. 1. 14..
 */
public class ChipsEdittextFragment extends ABaseFragment {
    private final String TAG = getClass().getSimpleName();

    ChipsMultiAutoCompleteTextview mu;
    TextView tv;
    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text_chipedittext, container, false);
        refresh();

        mu = (ChipsMultiAutoCompleteTextview) view.findViewById(R.id.multiAutoCompleteTextView1);
        mu.setContextHelper(contextHelper);

        tv = (TextView) view.findViewById(R.id.textView1);

        NsUserSearch[] users = new NsUserSearch[0];
        UserAdapter myAdapter = new UserAdapter(contextHelper.getContext(), R.layout.cell_searchuser, users);
        myAdapter.setContextHelper(contextHelper);

        mu.setAdapter(myAdapter);

        mu.setTokenizer(new SpaceTokenizer());
        mu.setThreshold(1);

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

    void refresh() {

    }

    public void onButtonClicked(View v) {
        tv.setText(mu.getText().toString());
//
//        switch(v.getId()) {
//            case R.id.btnLogin: {
////                Intent i = new Intent(contextHelper.getContext(), IntroActivity.class);
////                i.putExtra("isIntro", false);
////                contextHelper.getActivity().startActivity(i);
//                contextHelper.restart();
//            }
//            break;
//            case R.id.btnSetting: {
//                Intent i = new Intent(contextHelper.getContext(), BaseActivity.class);
//                i.putExtra("activityCode", BaseActivity.ActivityCode.SETTING.ordinal());
//                contextHelper.getActivity().startActivity(i);
//            }
//            break;
//            case R.id.actionbarImageButton:
//                new SweetAlertDialog(getContext(), SweetAlertDialog.SELECT_TYPE)
//                        .setTitleText("선택해 주세요.")
//                        .setArrayValue(new String[]{"신고하기"})
//                        .setSelectListener(new SweetAlertDialog.OnSweetSelectListener() {
//                            @Override
//                            public void onSelected(SweetAlertDialog sweetAlertDialog, int index) {
//
//                                sweetAlertDialog.dismissWithAnimation();
//
//                                switch (index) {
//                                    case 0:
//                                        contextHelper.report("USER", user.getId());
//                                        break;
//                                }
//                            }
//                        })
//                        .show();
//
//                break;
//        }
    }


    public void onEvent(PushMessage pushMessage) {
//
//        try {
//            if(mineListFragment != null)
//                mineListFragment.onEvent(pushMessage);
//            if(bookmarkListFragment != null)
//                bookmarkListFragment.onEvent(pushMessage);
//            if(scrapListFragment != null)
//                scrapListFragment.onEvent(pushMessage);
//            if(shopListFragment != null)
//                shopListFragment.onEvent(pushMessage);
//            if(eventCouponListFragment != null)
//                eventCouponListFragment.onEvent(pushMessage);
//        } catch (Exception e) {
//
//        }
//        switch(pushMessage.getActionCode()) {
//            case PushMessage.ACTIONCODE_CHANGE_LOGIN: {
//                contextHelper.getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            if(type == PROFILETYYPE_MINE) {
//                                user = UserManager.getInstance(contextHelper).getMe();
//                                refresh();
//                            }
//                        } catch (Exception e) {
//                        }
//                    }
//                });
//            }
//            break;
//        }
    }


    public class SpaceTokenizer implements MultiAutoCompleteTextView.Tokenizer {

        public int findTokenStart(CharSequence text, int cursor) {
            int i = cursor;

            while (i > 0 && text.charAt(i - 1) != ' ') {
                i--;
            }
            while (i < cursor && text.charAt(i) == ' ') {
                i++;
            }

            return i;
        }

        public int findTokenEnd(CharSequence text, int cursor) {
            int i = cursor;
            int len = text.length();

            while (i < len) {
                if (text.charAt(i) == ' ') {
                    return i;
                } else {
                    i++;
                }
            }

            return len;
        }

        public CharSequence terminateToken(CharSequence text) {
            int i = text.length();

            while (i > 0 && text.charAt(i - 1) == ' ') {
                i--;
            }

            if (i > 0 && text.charAt(i - 1) == ' ') {
                return text;
            } else {
                if (text instanceof Spanned) {
                    SpannableString sp = new SpannableString(text + " ");
                    TextUtils.copySpansFrom((Spanned) text, 0, text.length(),
                            Object.class, sp, 0);
                    return sp;
                } else {
                    return text + " ";
                }
            }
        }
    }




}


