package com.yongtrim.lib.fragment;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;

import com.android.volley.Response;
import com.nuums.nuums.R;
import com.yongtrim.lib.log.Logger;
import com.yongtrim.lib.model.config.Config;
import com.yongtrim.lib.model.config.ConfigData;
import com.yongtrim.lib.model.config.ConfigManager;
import com.yongtrim.lib.ui.sweetalert.SweetAlertDialog;

import java.util.concurrent.CountDownLatch;

/**
 * hair / com.yongtrim.lib.fragment
 * <p/>
 * Created by Uihyun on 15. 9. 14..
 */
public class SplashFragment extends ABaseFragment {
    public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final String TAG = getClass().getSimpleName();

    protected void getConfig(final CountDownLatch latchWait, final CountDownLatch latchCount) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    if (latchWait != null)
                        latchWait.await();

                    Logger.debug(TAG, "getConfig() called");
                    ConfigManager.getInstance(contextHelper).get(
                            new Response.Listener<ConfigData>() {
                                @Override
                                public void onResponse(ConfigData response) {
                                    if (response.isSuccess()) {
                                        ConfigManager.getInstance(contextHelper).setConfigHello(response.config);
                                        Logger.debug(TAG, "getConfig() | config = " + response.config.toString());

                                        if (checkVersion(response.config)) {
                                            latchCount.countDown();
                                        }
                                    }

                                }
                            },
                            null,
                            "HELLO"
                    );

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private boolean checkVersion(final Config config) {
        try {
            String[] curVersion = config.getParams().getAdVersion().split("[.]");

            PackageInfo pinfo = contextHelper.getContext().getPackageManager().getPackageInfo(contextHelper.getContext().getPackageName(), 0);
            String[] myVersion = pinfo.versionName.split("[.]");

            int curValue = Integer.parseInt(curVersion[0]) * 1000 + Integer.parseInt(curVersion[1]) * 100 + Integer.parseInt(curVersion[2]);
            int myValue = Integer.parseInt(myVersion[0]) * 1000 + Integer.parseInt(myVersion[1]) * 100 + Integer.parseInt(myVersion[2]);

            if (curValue > myValue) {
                String message = getString(R.string.msg_version, config.getParams().getAdVersion(), pinfo.versionName);

                SweetAlertDialog alert = new SweetAlertDialog(contextHelper.getContext(), SweetAlertDialog.NORMAL_TYPE)
                        .setContentText(message)
                        .setConfirmText("최신버전 받기")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                contextHelper.getActivity().startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse(config.getParams().getAdUrl())));
                            }
                        });
                alert.setCancelable(false);
                alert.show();

                return false;
            }
        } catch (Exception e) {
            e.toString();
        }

        return true;
    }
}
