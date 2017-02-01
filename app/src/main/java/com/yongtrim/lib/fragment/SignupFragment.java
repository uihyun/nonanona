package com.yongtrim.lib.fragment;

import com.android.volley.Response;
import com.yongtrim.lib.logger.Logger;
import com.yongtrim.lib.model.user.UserData;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.ui.sweetalert.SweetAlertDialog;

import java.util.concurrent.CountDownLatch;

/**
 * hair / com.yongtrim.lib.fragment
 * <p/>
 * Created by Uihyun on 15. 9. 19..
 */
public class SignupFragment extends ABaseFragment {
    final String TAG = "SignupFragment";

    protected void createUser(
            final String loginType,
            final String role,
            final String username,
            final String password,
            final String email,
            final String nickname,
            final CountDownLatch latchCount) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Logger.debug(TAG, "createUser() called");
                    UserManager.getInstance(contextHelper).create(
                            loginType,
                            role,
                            username,
                            password,
                            email,
                            nickname,
                            new Response.Listener<UserData>() {
                                @Override
                                public void onResponse(UserData response) {
                                    if (response.isSuccess()) {
                                        Logger.debug(TAG, "createUser() | user = " + response.user.toString());
                                        contextHelper.login(loginType, username, password, true, null, latchCount);

                                    } else {
                                        contextHelper.hideProgress();
                                        new SweetAlertDialog(getContext())
                                                .setContentText(response.getErrorMessage())
                                                .show();
                                    }
                                }
                            },
                            null
                    );

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
