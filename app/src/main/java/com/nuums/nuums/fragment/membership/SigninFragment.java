package com.nuums.nuums.fragment.membership;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.nuums.nuums.R;
import com.nuums.nuums.activity.Base2Activity;
import com.nuums.nuums.activity.BaseActivity;
import com.nuums.nuums.activity.MainActivity;
import com.yongtrim.lib.fragment.LoginFragment;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.model.config.ConfigManager;
import com.yongtrim.lib.model.user.LoginManager;
import com.yongtrim.lib.model.user.User;
import com.yongtrim.lib.sns.facebook.FacebookManager;
import com.yongtrim.lib.ui.UltraEditText;
import com.yongtrim.lib.ui.sweetalert.SweetAlertDialog;
import com.yongtrim.lib.util.MiscUtil;

/**
 * nuums / com.nuums.nuums.fragment.membership
 * <p/>
 * Created by Uihyun on 15. 12. 11..
 */
public class SigninFragment extends LoginFragment {

    UltraEditText etEmail;
    UltraEditText etPassword;

    CheckBox cbAutoLogin;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        //facebookManager = FacebookManager.getInstance(contextHelper);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signin, container, false);
        etEmail = (UltraEditText)view.findViewById(R.id.etEmail);
        etEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | InputType.TYPE_CLASS_TEXT);
        etEmail.setText(LoginManager.getInstance(contextHelper).getRecentLoginUsername());

        etPassword = (UltraEditText)view.findViewById(R.id.etPassword);
        etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);

        cbAutoLogin = (CheckBox)view.findViewById(R.id.cbAutoLogin);
        cbAutoLogin.setChecked(ConfigManager.getInstance(contextHelper).getPreference().getAutoLogin());

        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //facebookManager.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onButtonClicked(View v) {
        switch(v.getId()) {
            case R.id.btnFindId:{
                Intent i = new Intent(getContext(), BaseActivity.class);
                i.putExtra("activityCode", BaseActivity.ActivityCode.FINDACCOUNT.ordinal());
                i.putExtra("type", FindAccountFragment.TYPE_ID);
                getContext().startActivity(i);
            }
            break;
            case R.id.btnFindPW:{
                Intent i = new Intent(getContext(), BaseActivity.class);
                i.putExtra("activityCode", BaseActivity.ActivityCode.FINDACCOUNT.ordinal());
                i.putExtra("type", FindAccountFragment.TYPE_PASSWORD);
                getContext().startActivity(i);
            }
            break;
            case R.id.btnSignin:
                login();
                break;
            case R.id.cbAutoLogin:
                ConfigManager.getInstance(contextHelper).getPreference().putAutoLogin(cbAutoLogin.isChecked());
                break;
            case R.id.btnSignup: {
                Intent i = new Intent(getContext(), BaseActivity.class);
                i.putExtra("activityCode", BaseActivity.ActivityCode.SIGNUP.ordinal());
                getContext().startActivity(i);
            }
            break;
        }
    }


    public void onEvent(PushMessage pushMessage) {
        switch(pushMessage.getActionCode()) {
            case PushMessage.ACTIONCODE_CHANGE_ME: {
                contextHelper.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (LoginManager.getInstance(contextHelper).getLoginStatus().equals(LoginManager.LOGINSTATUS_LOGIN)) {
                                Intent i = new Intent(contextHelper.getContext(), MainActivity.class);
                                getActivity().startActivity(i);
                                contextHelper.getActivity().finish();
                            }
                        } catch(Exception e) {

                        }
                    }
                });
            }
            break;
        }
    }

    public void login() {

        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        String message = null;
        if(TextUtils.isEmpty(email)) {
            message = "이메일을 입력해주세요.";
        } else if(!MiscUtil.isValidEmail(email)) {
            message = "이메일 형식이 맞지 않습니다.";
        } else if(TextUtils.isEmpty(password)) {
            message = "패스워드를 입력해주세요.";
        }

        if(message != null) {
            new SweetAlertDialog(getContext())
                    .setContentText(message)
                    .show();
        } else {
            contextHelper.showProgress("로그인 중입니다.");
            contextHelper.login(User.LOGINTYPE_EMAIL, email, password, false, null, null);
        }
    }

}
