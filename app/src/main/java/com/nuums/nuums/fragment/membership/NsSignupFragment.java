package com.nuums.nuums.fragment.membership;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.nuums.nuums.R;
import com.nuums.nuums.activity.BaseActivity;
import com.nuums.nuums.model.misc.Sns;
import com.nuums.nuums.model.user.NsUser;
import com.sromku.simple.fb.SimpleFacebook;
import com.yongtrim.lib.fragment.SignupFragment;
import com.yongtrim.lib.log.Logger;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.model.ACommonData;
import com.yongtrim.lib.model.config.ConfigManager;
import com.yongtrim.lib.model.post.Post;
import com.yongtrim.lib.model.user.UserData;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.sns.SNSLoginListener;
import com.yongtrim.lib.sns.facebook.FacebookManager;
import com.yongtrim.lib.ui.UltraButton;
import com.yongtrim.lib.ui.UltraEditText;
import com.yongtrim.lib.ui.sweetalert.SweetAlertDialog;
import com.yongtrim.lib.util.MiscUtil;

import java.util.concurrent.CountDownLatch;

import de.greenrobot.event.EventBus;

/**
 * nuums / com.nuums.nuums.fragment.membership
 * <p/>
 * Created by Uihyun on 15. 12. 11..
 */
public class NsSignupFragment extends SignupFragment implements UltraEditText.OnChangeListener {

    final String TAG = getClass().getSimpleName();

    UltraEditText etEmail;
    boolean isGoodEmail = true;

    UltraEditText etNickName;
    boolean isGoodNickName = true;

    UltraEditText etRealName;
    boolean isGoodRealName = true;

    UltraEditText etPassword;
    UltraEditText etConfirm;
    boolean isGoodPassword = true;

    UltraButton btnSignup;

    NsUser user;

    SweetAlertDialog dialogSender;

    boolean isSignuping;

    LinearLayout emailSignup;
    LinearLayout emailConfirm;
    LinearLayout listSignup;

    FacebookManager facebookManager;
    private SimpleFacebook mSimpleFacebook;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        contextHelper.getActivity().setupActionBar("회원인증정보 입력");
        contextHelper.getActivity().setBackButtonVisibility(false);
        contextHelper.getActivity().setImageButtonAndVisiable(R.drawable.del);

        mSimpleFacebook = SimpleFacebook.getInstance(contextHelper.getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        user = ConfigManager.getInstance(contextHelper).getPreference().getSignupParam();
        if (user == null) {
            user = new NsUser();
            user.setLoginType("EMAIL");
        }

        facebookManager = FacebookManager.getInstance(contextHelper);

        setupUI(view);

        verify();

        return view;
    }


    public void setupUI(View view) {

        btnSignup = (UltraButton) view.findViewById(R.id.btnSignin);

        etEmail = (UltraEditText) view.findViewById(R.id.etEmail);
        etEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | InputType.TYPE_CLASS_TEXT);
        etEmail.setText(user.getEmail());

        etRealName = (UltraEditText) view.findViewById(R.id.etRealName);
        etRealName.setMaxCharacters(16);
        etRealName.setText(user.getRealname());

        etNickName = (UltraEditText) view.findViewById(R.id.etNickName);
        etNickName.setMaxCharacters(8);
        etNickName.setMinCharacters(2);
        etNickName.setText(user.getNickname());

        etPassword = (UltraEditText) view.findViewById(R.id.etPassword);
        etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
        etPassword.setMaxCharacters(16);
        etPassword.setMinCharacters(6);

        etConfirm = (UltraEditText) view.findViewById(R.id.etConfirm);
        etConfirm.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
        etConfirm.setMaxCharacters(16);
        etConfirm.setMinCharacters(6);


        etEmail.setChangeListener(this);
        etRealName.setChangeListener(this);
        etNickName.setChangeListener(this);
        etPassword.setChangeListener(this);
        etConfirm.setChangeListener(this);


        String str0 = "확인 버튼을 누르면 ";
        String str1 = "개인정보 수집";
        String str2 = " 및 ";
        String str3 = "이용";
        String str4 = "에 동의하게 됩니다.";

        SpannableString text = new SpannableString(str0 + str1 + str2 + str3 + str4);

        int offset = str0.length();
        setLink(text, offset, str1.length(), Post.TYPE_PRIVACY);
        offset += str1.length();
        offset += str2.length();

        setLink(text, offset, str3.length(), Post.TYPE_POLICY);

        TextView tvPolicy = (TextView) view.findViewById(R.id.tvPolicy);
        tvPolicy.setText(text);
        tvPolicy.setMovementMethod(LinkMovementMethod.getInstance());

        if (!TextUtils.isEmpty(user.getNickname())) {
            UserManager.getInstance(contextHelper).checkNickname(etNickName.getText(),
                    new Response.Listener<ACommonData>() {
                        @Override
                        public void onResponse(ACommonData response) {
                            if (response.isSuccess()) {
                                etNickName.setInfoMessage("등록 가능");
                                isGoodNickName = true;
                                verify();
                            } else {
                                etNickName.setErrorMessage("이미 등록된 닉네임입니다.");
                                isGoodNickName = false;
                                verify();
                            }
                        }
                    },
                    null
            );
        }

        emailSignup = (LinearLayout) view.findViewById(R.id.email_signup);
        emailSignup.setVisibility(View.GONE);
        emailConfirm = (LinearLayout) view.findViewById(R.id.email_confirm);
        emailConfirm.setVisibility(View.GONE);

        listSignup = (LinearLayout) view.findViewById(R.id.list_signup);
        listSignup.setVisibility(View.VISIBLE);
    }


    public void verify() {
        user.setUsername(etEmail.getText());
        user.setEmail(etEmail.getText());
        user.setRealname(etRealName.getText());
        user.setNickname(etNickName.getText());
        user.setPasswordChanged(etPassword.getText());

        ConfigManager.getInstance(contextHelper).getPreference().putSignupParam(user);
    }

    public void verifySns(Sns sns) {
        user.setUsername(sns.getEmail());
        user.setEmail(sns.getEmail());
        user.setRealname(sns.getName());
        user.setNickname(sns.getNickName());
        user.setPasswordChanged(sns.getId());

        ConfigManager.getInstance(contextHelper).getPreference().putSignupParam(user);
    }

    public void onEditTextChanged(UltraEditText editText) {
        if (editText == etEmail) {
            String email = etEmail.getText();
            if (!TextUtils.isEmpty(email)) {
                if (!MiscUtil.isValidEmail(email)) {
                    etEmail.setErrorMessage("이메일 형식이 아닙니다.");
                    isGoodEmail = false;
                } else {
                    etEmail.setErrorMessage("");
                    isGoodEmail = true;
                }
            }
        }

        if (!TextUtils.isEmpty(etPassword.getText()) && etConfirm.validate()) {

            if (!etPassword.getText().toString().equals(etConfirm.getText().toString())) {
                etConfirm.setErrorMessage("동일 X");
                isGoodPassword = false;
            } else {
                etConfirm.setInfoMessage("동일 O");
                isGoodPassword = true;
            }

        }

        if (editText == etNickName) {
            if (etNickName.getText().toString().contains(" ")) {
                etNickName.setErrorMessage("공백은 허용하지 않습니다.");
                isGoodNickName = false;
            } else if (etNickName.validate()) {
                UserManager.getInstance(contextHelper).checkNickname(etNickName.getText(),
                        new Response.Listener<ACommonData>() {
                            @Override
                            public void onResponse(ACommonData response) {
                                if (response.isSuccess()) {
                                    etNickName.setInfoMessage("등록 가능");
                                    isGoodNickName = true;
                                    verify();
                                } else {
                                    etNickName.setErrorMessage("이미 등록된 닉네임입니다.");
                                    isGoodNickName = false;
                                    verify();
                                }
                            }
                        },
                        null
                );
                etNickName.setErrorMessage("");

            }
        }

        if (editText == etRealName) {
            if (etRealName.getText().toString().contains(" ")) {
                etRealName.setErrorMessage("공백은 허용하지 않습니다.");
                isGoodRealName = false;
            } else if (etRealName.validate()) {
                etRealName.setErrorMessage("");
                isGoodRealName = true;
            }
        }

        verify();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void prevSignup() {

        String message = "";

        if (TextUtils.isEmpty(etEmail.getText())) {
            message = "이메일을 입력해 주세요.";
        } else if (!isGoodEmail) {
            message = "이메일 형식이 아닙니다.";
        } else if (TextUtils.isEmpty(etRealName.getText())) {
            message = "이름(실명)을 입력해 주세요.";
        } else if (!isGoodRealName) {
            message = "이름(실명)에서 공백은 허용하지 않습니다.";
        } else if (TextUtils.isEmpty(etNickName.getText())) {
            message = "닉네임을 입력해 주세요.";
        } else if (!etNickName.validate()) {
            message = "닉네임의 형식이 잘못 되었습니다.";
        } else if (!isGoodNickName) {
            message = "이미 등록된 닉네임입니다.";
        } else if (TextUtils.isEmpty(etPassword.getText()) || TextUtils.isEmpty(etConfirm.getText())) {
            message = "비밀번호를 입력해 주세요.";
        } else if (!etPassword.validate() || !etConfirm.validate() || !isGoodPassword) {
            message = "비밀번호 형식이 잘못 되었습니다.";
        }

        if (!TextUtils.isEmpty(message)) {
            new SweetAlertDialog(getContext()).setContentText(message).show();
            return;
        }


        final CountDownLatch latchCheckEmail = new CountDownLatch(1);

        contextHelper.showProgress("");
        UserManager.getInstance(contextHelper).checkEmail(etEmail.getText(),
                new Response.Listener<ACommonData>() {
                    @Override
                    public void onResponse(ACommonData response) {
                        if (response.isSuccess()) {
                            latchCheckEmail.countDown();
                        } else {
                            contextHelper.hideProgress();
                            new SweetAlertDialog(getContext()).setContentText(response.getErrorMessage()).show();
                        }
                    }
                },
                null
        );


        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    latchCheckEmail.await();

                    UserManager.getInstance(contextHelper).certifyEmail(1,
                            etEmail.getText().toString(),
                            null,
                            new Response.Listener<UserData>() {
                                @Override
                                public void onResponse(UserData response) {

                                    contextHelper.hideProgress();
                                    if (response.isSuccess()) {

                                        dialogSender = new SweetAlertDialog(getContext())
                                                .setContentText("인증 이메일을 발송하였습니다. 인증메일 확인후 잠시만 기다려주세요.");
                                        dialogSender.show();

                                    } else if (response.isCerified()) {
                                        contextHelper.getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                signup();
                                            }
                                        });
                                    } else {
                                        new SweetAlertDialog(getContext())
                                                .setContentText(response.getErrorMessage())
                                                .show();
                                    }
                                }
                            },
                            null
                    );


                } catch (Exception e) {
                    Logger.debug(TAG, e.toString());
                }
            }
        }).start();

    }


    public void signup() {

        isSignuping = true;

        contextHelper.showProgress("가입중입니다.");
        final CountDownLatch latchCreate = new CountDownLatch(1);

        createUser(user.getLoginType(), user.getRole(), user.getUsername(), user.getPasswordChanged(), user.getEmail(), user.getNickname(), latchCreate);

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    latchCreate.await();

                    NsUser createdUser = UserManager.getInstance(contextHelper).getMe();
                    user.setId(createdUser.getId());

                    final CountDownLatch latchUpdate = new CountDownLatch(1);
                    contextHelper.updateUser(user, null, latchUpdate);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                latchUpdate.await();

                                ConfigManager.getInstance(contextHelper).getPreference().putSignupParam(null);

                                user = UserManager.getInstance(contextHelper).getMe();
                                //UserManager.getInstance(contextHelper).setMe(user);
                                EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_CHANGE_ME).setObject(user));
                                contextHelper.getActivity().finish();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void snsSignup() {
        contextHelper.showProgress("");
        UserManager.getInstance(contextHelper).checkEmail(user.getEmail(),
                new Response.Listener<ACommonData>() {
                    @Override
                    public void onResponse(ACommonData response) {
                        if (response.isSuccess()) {
                            contextHelper.getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    signup();
                                }
                            });
                        } else {
                            contextHelper.hideProgress();
                            new SweetAlertDialog(getContext()).setContentText(response.getErrorMessage()).show();
                        }
                    }
                },
                null
        );

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mSimpleFacebook.onActivityResult(requestCode, resultCode, data);
    }

    public void onButtonClicked(View v) {
        switch (v.getId()) {
            case R.id.btnSignin:
                prevSignup();
                break;
            case R.id.actionbarImageButton:
                contextHelper.getActivity().finish();
                break;
            case R.id.btnSigninEmail: {
                emailSignup.setVisibility(View.VISIBLE);
                emailConfirm.setVisibility(View.VISIBLE);
                listSignup.setVisibility(View.GONE);
            }
            break;
            case R.id.btnSignupFacebook: {
                facebookManager.login(new SNSLoginListener() {
                    @Override
                    public void success(boolean isLogin, Sns sns) {
                        verifySns(sns);
                        snsSignup();
                    }

                    @Override
                    public void fail() {
                    }

                    public void successAndNeedRegist() {
                    }
                }, true);
            }
            break;
        }
    }

    private void setLink(SpannableString text, int offset, int length, String type) {
        //text.setSpan(new StyleSpan(Typeface.BOLD), offset, offset + length, 0);
        PolicyClickableSpan clickableSpan = new PolicyClickableSpan(type);
        text.setSpan(clickableSpan, offset, offset + length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public void onEvent(final PushMessage pushMessage) {

        switch (pushMessage.getActionCode()) {
            case PushMessage.ACTIONCODE_CERTIFYEMAIL_SIGNUP: {
                contextHelper.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            if (dialogSender != null && dialogSender.isShowing())
                                dialogSender.dismissWithAnimation();

                            if (!isSignuping) {
                                SweetAlertDialog dialog = new SweetAlertDialog(contextHelper.getContext())
                                        .setContentText("인증되었습니다.")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismissWithAnimation();
                                                signup();
                                            }
                                        });
                                dialog.setCancelable(false);
                                dialog.show();
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            break;
        }
    }

    class PolicyClickableSpan extends ClickableSpan {
        private String type;

        public PolicyClickableSpan(String type) {
            this.type = type;
        }

        @Override
        public void onClick(View view) {
            if (type.equals(Post.TYPE_POLICY)) {
                Intent i = new Intent(getContext(), BaseActivity.class);
                i.putExtra("activityCode", BaseActivity.ActivityCode.POST_VIEWER.ordinal());
                i.putExtra("title", "서비스 이용 약관");
                i.putExtra("type", Post.TYPE_POLICY);
                getContext().startActivity(i);

            } else {
                Intent i = new Intent(getContext(), BaseActivity.class);
                i.putExtra("activityCode", BaseActivity.ActivityCode.POST_VIEWER.ordinal());
                i.putExtra("title", "개인정보 수집정책");
                i.putExtra("type", Post.TYPE_PRIVACY);
                getContext().startActivity(i);
            }
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.linkColor = ContextCompat.getColor(getContext(), R.color.red);
            ds.setColor(ds.linkColor);
            //ds.setUnderlineText(false);
        }
    }
}

