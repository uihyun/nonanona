package com.nuums.nuums.fragment.membership;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.nuums.nuums.R;
import com.nuums.nuums.model.user.NsUser;
import com.nuums.nuums.model.user.NsUserListData;
import com.yongtrim.lib.fragment.ABaseFragment;
import com.yongtrim.lib.logger.Logger;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.model.user.UserData;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.ui.UltraButton;
import com.yongtrim.lib.ui.UltraEditText;
import com.yongtrim.lib.ui.sweetalert.SweetAlertDialog;

import org.json.JSONObject;

import me.grantland.widget.AutofitHelper;
import me.grantland.widget.AutofitTextView;

/**
 * nuums / com.nuums.nuums.fragment.membership
 * <p/>
 * Created by Uihyun on 15. 12. 13..
 */
public class FindAccountFragment extends ABaseFragment implements UltraEditText.OnChangeListener {

    public static final String TYPE_ID = "TYPE_ID";
    public static final String TYPE_PASSWORD = "TYPE_PASSWORD";

    private final String TAG = getClass().getSimpleName();

    String type;
    int step;

    TextView tvStep0;
    TextView tvStep1;
    TextView tvTitle;

    View viewResult;

    UltraEditText etFirst;
    UltraEditText etSecond;

    UltraButton btnConfirm;

    java.util.ArrayList<NsUser> users;
    NsUser user;


    boolean isGoodPassword = true;


    SweetAlertDialog dialogSender;


    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        type = contextHelper.getActivity().getIntent().getStringExtra("type");
        step = contextHelper.getActivity().getIntent().getIntExtra("step", 0);


        if(type.equals(TYPE_ID)) {
            contextHelper.getActivity().setupActionBar("아이디 찾기");
        } else {
            contextHelper.getActivity().setupActionBar("비밀번호 찾기");
        }


        contextHelper.getActivity().setBackButtonVisibility(false);
        contextHelper.getActivity().setImageButtonAndVisiable(R.drawable.del);

//
//        step = 1;
//        users = new ArrayList<NsUser>();
//
//        NsUser user = new NsUser();
//        user.setEmail("suppport@yongtrim.com");
//        users.add(user);
//
//        user = new NsUser();
//        user.setEmail("syyoon.koretea@yongtrim.com");
//        users.add(user);
//
//        user = new NsUser();
//        user.setEmail("1@yongtrim.com");
//        users.add(user);
//
//        user = new NsUser();
//        user.setEmail("12@yongtrim.com");
//        users.add(user);
//
//        user = new NsUser();
//        user.setEmail("123@yongtrim.com");
//        users.add(user);
//
//
//        user = new NsUser();
//        user.setEmail("1234@yongtrim.com");
//        users.add(user);
//
//        user = new NsUser();
//        user.setEmail("syyoon.koretea.fdsfa@yongtrim.com");
//        users.add(user);
//
//        user = new NsUser();
//        user.setEmail("syyoon.korea@yongtrim.com");
//        users.add(user);
//
//        user = new NsUser();
//        user.setEmail("12314cds.koretea@yongtrim.com");
//        users.add(user);
//
//        user = new NsUser();
//        user.setEmail("12314cds.koretea@yongtrim.com");
//        users.add(user);
//
//
//        user = new NsUser();
//        user.setEmail("12314cds.koretea@yongtrim.com");
//        users.add(user);
//        user = new NsUser();
//        user.setEmail("12314cds.koretea@yongtrim.com");
//        users.add(user);
//
//        user = new NsUser();
//        user.setEmail("12314cds.koretea@yongtrim.com");
//        users.add(user);
//
//        user = new NsUser();
//        user.setEmail("12314cds.koretea@yongtrim.com");
//        users.add(user);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_findaccount, container, false);

        tvStep0 = (TextView)view.findViewById(R.id.tvStep0);
        tvStep1 = (TextView)view.findViewById(R.id.tvStep1);
        tvTitle = (TextView)view.findViewById(R.id.tvTitle);

        etFirst = (UltraEditText)view.findViewById(R.id.etFirst);
        etSecond = (UltraEditText)view.findViewById(R.id.etSecond);

        btnConfirm = (UltraButton)view.findViewById(R.id.btnConfirm);

        viewResult = (View)view.findViewById(R.id.viewResult);

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

    void refresh() {
        if(type.equals(TYPE_ID)) {
            tvStep0.setText("01.본인 확인");
            tvStep1.setText("02.아이디 확인");

            if(step == 0) {
                tvStep0.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                tvStep1.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));
                tvTitle.setText("이름을 입력해주세요.");
                etFirst.setHint("이름(실명)");

                etSecond.setVisibility(View.GONE);
                btnConfirm.setText("다음");
            }
            else {
                tvStep0.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));
                tvStep1.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                tvTitle.setVisibility(View.GONE);

                etFirst.setVisibility(View.GONE);
                etSecond.setVisibility(View.GONE);

                viewResult.setVisibility(View.VISIBLE);

                ViewGroup viewContainer = (ViewGroup)viewResult.findViewById(R.id.viewContainer);
                viewContainer.removeAllViews();

                for(NsUser user : users) {
                    AutofitTextView textView = new AutofitTextView(getContext());
                    textView.setMaxLines(1);
                    Logger.debug(TAG, "dp = " + (int) getResources().getDimension(R.dimen.text_medium));
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) getResources().getDimension(R.dimen.text_xlarge));
                    textView.setText(filterEmail(user.getEmail()));
                    textView.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                    viewContainer.addView(textView);

                    AutofitHelper.create(textView);

                }

                btnConfirm.setText("확인");

            }
        } else {
            tvStep0.setText("01.본인 확인");
            tvStep1.setText("02.비밀번호 재설정");

            if(step == 0) {
                tvStep0.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                tvStep1.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));
                tvTitle.setText("정보를 입력해주세요.");
                etFirst.setHint("이름(실명)");
                etSecond.setHint("이메일 주소");
                etSecond.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | InputType.TYPE_CLASS_TEXT);

                btnConfirm.setText("다음");

            }
            else {
                tvStep0.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));
                tvStep1.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                tvTitle.setText("아이디: " + user.getUsername());

                etFirst.requestFocus();

                etFirst.setText("");
                etFirst.setHint("새 비밀번호(6자 이상)");
                etFirst.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                etFirst.setMaxCharacters(16);
                etFirst.setMinCharacters(6);

                etFirst.setChangeListener(this);

                etSecond.setText("");
                etSecond.setHint("새 비밀번호 확인");
                etSecond.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                etSecond.setMaxCharacters(16);
                etSecond.setMinCharacters(6);
                etSecond.setChangeListener(this);


                btnConfirm.setText("확인");

                verify();
            }
        }
    }

    String filterEmail(String input) {
        String[] array = input.split("@");
        String output = "";
        int offset = 2;

        if(array[0].length() > 3) {
            offset = 3;
        } else if(array[0].length() == 3) {
            offset = 2;
        } else if(array[0].length() == 2) {
            offset = 1;
        } else {
            offset = 0;
        }

        for(int i = 0; i < array[0].length(); i++) {

            if(i < offset) {
                output = output + array[0].charAt(i);;
            } else {
                output = output + "*";

            }
        }
        return output + "@" + array[1];
    }

    public void onButtonClicked(View v) {

        switch(v.getId()) {
            case R.id.actionbarImageButton:
                contextHelper.getActivity().finish();
                break;
            case R.id.btnConfirm: {
                if(type.equals(TYPE_ID)) {
                    if(step == 0) {
                        findId();
                    }
                    else {
                        contextHelper.getActivity().finish();
                    }

                } else {
                    if(step == 0) {
                        certifyEmail();
                    } else {

                        if(TextUtils.isEmpty(etFirst.getText()) || TextUtils.isEmpty(etSecond.getText())) {
                            new SweetAlertDialog(getContext()).setContentText("비빌번호를 입력해주세요.").show();
                            return;
                        } else if(!etSecond.validate() || !etFirst.validate()) {
                            new SweetAlertDialog(getContext()).setContentText("비빌번호는 6자리 이상이어야 합니다.").show();
                            return;
                        } else if(!isGoodPassword) {
                            new SweetAlertDialog(getContext()).setContentText("비빌번호가 동일하지 않습니다.").show();
                            return;
                        }

                        contextHelper.showProgress(null);

                        UserManager.getInstance(contextHelper).changePassword(user.getUsername(), etFirst.getText(),
                                new Response.Listener<UserData>() {
                                    @Override
                                    public void onResponse(UserData response) {
                                        contextHelper.getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(contextHelper.getContext(), "비밀번호가 바뀌었습니다.", Toast.LENGTH_SHORT).show();

                                                contextHelper.getActivity().finish();
                                                contextHelper.hideProgress();
                                            }
                                        });


                                    }
                                },
                                null
                        );
                    }
                }

            }
            break;
        }
    }

    public void findId() {

        if(TextUtils.isEmpty(etFirst.getText())) {
            new SweetAlertDialog(getContext()).setContentText("이름(실명)을 입력해주세요.").show();
            return;
        }

        JSONObject option = null;
        try {
            option = new JSONObject();
            option.put("realname", etFirst.getText());
        } catch(Exception e) {
        }

        contextHelper.showProgress(null);
        UserManager.getInstance(contextHelper).find(1, option,
                new Response.Listener<NsUserListData>() {
                    @Override
                    public void onResponse(NsUserListData response) {
                        contextHelper.hideProgress();
                        if(response.getUserList().getUsers().size() == 0) {
                            new SweetAlertDialog(getContext()).setContentText("아이디가 없습니다.").show();
                        } else {
                            users = response.getUserList().getUsers();
                            step = 1;
                            refresh();
                        }
                    }
                },
                null
        );

    }

    public void certifyEmail() {

        if(TextUtils.isEmpty(etFirst.getText())) {
            new SweetAlertDialog(getContext()).setContentText("이름(실명)을 입력해주세요.").show();
            return;
        }

        if(TextUtils.isEmpty(etSecond.getText())) {
            new SweetAlertDialog(getContext()).setContentText("이메일을 입력해주세요.").show();
            return;
        }

        contextHelper.showProgress("인증메일을 보내는중입니다.");
        UserManager.getInstance(contextHelper).certifyEmail(0,
                etSecond.getText().toString(),
                etFirst.getText().toString(),
                new Response.Listener<UserData>() {
                    @Override
                    public void onResponse(UserData response) {
                        if (response.isSuccess()) {
                            dialogSender = new SweetAlertDialog(getContext())
                                    .setContentText("인증 이메일을 발송하였습니다.\n인증완료 후 비밀번호를 변경해주세요.");


                            dialogSender.show();

                        } else {
                            new SweetAlertDialog(getContext())
                                    .setContentText(response.getErrorMessage())
                                    .show();
                        }
                        contextHelper.hideProgress();
                    }
                },
                null
        );
    }



    public void onEvent(final PushMessage pushMessage) {

        switch(pushMessage.getActionCode()) {
            case PushMessage.ACTIONCODE_CERTIFYEMAIL_PW: {
                contextHelper.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            if(dialogSender != null && dialogSender.isShowing())
                                dialogSender.dismissWithAnimation();


                            SweetAlertDialog dialog = new SweetAlertDialog(contextHelper.getContext())
                                    .setContentText("인증되었습니다.")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();

                                            user = (NsUser)pushMessage.getObject(contextHelper);
                                            step = 1;
                                            refresh();
                                        }
                                    });
                            dialog.setCancelable(false);
                            dialog.show();


                        } catch(Exception e) {

                        }
                    }
                });
            }
            break;
        }
    }


    public void verify() {
        boolean isValid = true;

        if((TextUtils.isEmpty(etFirst.getText()) || TextUtils.isEmpty(etSecond.getText()) || isGoodPassword == false))
            isValid = false;

    }


    public void onEditTextChanged(UltraEditText editText) {
        if (etSecond.validate()) {

            if (!etFirst.getText().toString().equals(etSecond.getText().toString())) {
                etSecond.setErrorMessage("동일 X");
                isGoodPassword = false;
            } else {
                etSecond.setInfoMessage("동일 O");
                isGoodPassword = true;
            }
        }

        verify();
    }

}




