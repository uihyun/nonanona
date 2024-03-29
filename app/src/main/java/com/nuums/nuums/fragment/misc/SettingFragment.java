package com.nuums.nuums.fragment.misc;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.nuums.nuums.MarketVersionChecker;
import com.nuums.nuums.R;
import com.nuums.nuums.activity.BaseActivity;
import com.nuums.nuums.model.report.Report;
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.fragment.ABaseFragment;
import com.yongtrim.lib.logger.Logger;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.model.config.ConfigData;
import com.yongtrim.lib.model.config.ConfigManager;
import com.yongtrim.lib.model.post.Post;
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
 * <p>
 * Created by Uihyun on 16. 1. 21..
 */
public class SettingFragment extends ABaseFragment {
    private final String TAG = getClass().getSimpleName();

    View mainView;

    NsUser me;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        contextHelper.getActivity().setupActionBar("설정하기");
        contextHelper.getActivity().setBackButtonVisibility(false);
        contextHelper.getActivity().setImageButtonAndVisiable(R.drawable.del);
        //contextHelper.getActivity().backgroundColor(ContextCompat.getColor(contextHelper.getContext(), R.color.orange));

        me = UserManager.getInstance(contextHelper).getMe();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_setting, container, false);

        refresh();

        ConfigManager.getInstance(contextHelper).get(
                new Response.Listener<ConfigData>() {
                    @Override
                    public void onResponse(ConfigData response) {
                        if (response.isSuccess()) {
                            ConfigManager.getInstance(contextHelper).setConfigHello(response.config);
                            Logger.debug(TAG, "getConfig() | config = " + response.config.toString());

                            refresh();
                        }

                    }
                },
                null,
                "HELLO"
        );
        return mainView;
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
        TextView tvMyVersion = (TextView) mainView.findViewById(R.id.tvMyVersion);

        TextView tvNowVersion = (TextView) mainView.findViewById(R.id.tvNowVersion);
        UltraButton btnUpdate = (UltraButton) mainView.findViewById(R.id.btnUpdate);

        try {
//            String nowVersion = ConfigManager.getInstance(contextHelper).getConfigHello().getParams().getAdVersion();
            PackageInfo pinfo = contextHelper.getContext().getPackageManager().getPackageInfo(contextHelper.getContext().getPackageName(), 0);

            String nowVersion = MarketVersionChecker.getMarketVersion(pinfo.packageName);
            String[] nowVersions = nowVersion.split("[.]");

            String myVersion = pinfo.versionName;
            String[] myVersions = myVersion.split("[.]");

            int curValue = Integer.parseInt(nowVersions[0]) * 1000 + Integer.parseInt(nowVersions[1]) * 100 + Integer.parseInt(nowVersions[2]);
            int myValue = Integer.parseInt(myVersions[0]) * 1000 + Integer.parseInt(myVersions[1]) * 100 + Integer.parseInt(myVersions[2]);

            if (curValue > myValue) {
                btnUpdate.setVisibility(View.VISIBLE);
            } else {
                btnUpdate.setVisibility(View.GONE);
            }
            tvMyVersion.setText("현재버전 v" + myVersion);
            tvNowVersion.setText("최신버전 v" + nowVersion);
        } catch (Exception e) {
            e.printStackTrace();
        }

        NsUser me = UserManager.getInstance(contextHelper).getMe();

        UltraButton tvUnreadNotice = (UltraButton) mainView.findViewById(R.id.tvUnreadNotice);
        UltraButton tvUnreadFaq = (UltraButton) mainView.findViewById(R.id.tvUnreadFaq);

        if (me.getUnreadNotice() == 0) {
            tvUnreadNotice.setVisibility(View.GONE);
        } else {
            tvUnreadNotice.setVisibility(View.VISIBLE);
            tvUnreadNotice.setText("" + me.getUnreadNotice());
        }

        if (me.getUnreadFaq() == 0) {
            tvUnreadFaq.setVisibility(View.GONE);
        } else {
            tvUnreadFaq.setVisibility(View.VISIBLE);
            tvUnreadFaq.setText("" + me.getUnreadFaq());
        }
    }

    public void onButtonClicked(View v) {

        switch (v.getId()) {
            case R.id.viewAlarm: {
                Intent i = new Intent(getContext(), BaseActivity.class);
                i.putExtra("activityCode", BaseActivity.ActivityCode.ALARM.ordinal());
                getContext().startActivity(i);
            }
            break;
            case R.id.viewLogout:
                new SweetAlertDialog(getContext())
                        .setContentText("정말 로그아웃 하시겠습니까?")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(final SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                                logout(false);
                            }
                        })
                        .show();
                break;
            case R.id.viewFaq: {
                Intent i = new Intent(getContext(), BaseActivity.class);
                i.putExtra("activityCode", BaseActivity.ActivityCode.POSTLIST.ordinal());
                i.putExtra("type", Post.TYPE_FAQ);
                getContext().startActivity(i);
            }

            break;
            case R.id.viewNotice: {
                Intent i = new Intent(getContext(), BaseActivity.class);
                i.putExtra("activityCode", BaseActivity.ActivityCode.POSTLIST.ordinal());
                i.putExtra("type", Post.TYPE_NOTICE);
                getContext().startActivity(i);
            }
            break;
            case R.id.viewReport: {
                Intent i = new Intent(getContext(), BaseActivity.class);
                i.putExtra("activityCode", BaseActivity.ActivityCode.REPORT.ordinal());
                i.putExtra("type", Report.REPORTTYPE_REPORT);
                getContext().startActivity(i);
            }
            break;
            case R.id.viewInqury: {
                Intent i = new Intent(getContext(), BaseActivity.class);
                i.putExtra("activityCode", BaseActivity.ActivityCode.REPORT.ordinal());
                i.putExtra("type", Report.REPORTTYPE_INQUIRY);
                getContext().startActivity(i);
            }
            break;
            case R.id.viewInquryAd: {
                Intent i = new Intent(getContext(), BaseActivity.class);
                i.putExtra("activityCode", BaseActivity.ActivityCode.REPORT.ordinal());
                i.putExtra("type", Report.REPORTTYPE_AD);
                getContext().startActivity(i);
            }
            break;
            case R.id.viewWithdraw: {
                new SweetAlertDialog(getContext())
                        .setContentText("탈퇴후 재가입은 한달후에 가능합니다.\n정말 탈퇴하시겠습니까?")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(final SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                                logout(true);
                            }
                        })
                        .show();
            }
            break;
            case R.id.btnUpdate: {
                PackageInfo pinfo = null;
                try {
                    pinfo = contextHelper.getContext().getPackageManager().getPackageInfo(contextHelper.getContext().getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                contextHelper.getActivity().startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id="
                                + pinfo.packageName)));
            }
            break;
            case R.id.actionbarImageButton:
                contextHelper.getActivity().finish();
                break;
        }
    }


    public void onEvent(PushMessage pushMessage) {
        switch (pushMessage.getActionCode()) {
            case PushMessage.ACTIONCODE_CHANGE_ME:
                refresh();
                break;
        }
    }


    void logout(final boolean isWithdraw) {
        contextHelper.showProgress(null);
        LoginManager.getInstance(contextHelper).logout(
                new Response.Listener<UserData>() {
                    @Override
                    public void onResponse(UserData response) {
                        if (response.isSuccess()) {
                            LoginManager.getInstance(contextHelper).logout(new LoginManager.OnLogoutListener() {
                                @Override
                                public void onLogout(final NsUser userGuest) {

                                    contextHelper.getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            contextHelper.getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    logoutSns(isWithdraw);
                                                }
                                            });

                                            UserManager.getInstance(contextHelper).setMe(userGuest);

                                        }
                                    });

                                }
                            });

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
    }


    void logoutSns(final boolean isWithdraw) {
        // 내용
        final CountDownLatch latchFacebook = new CountDownLatch(1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    contextHelper.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            FacebookManager.getInstance(contextHelper).logout(new SNSLoginoutListener() {
                                @Override
                                public void success(boolean isLogin) {
                                    Logger.debug(TAG, "FacebookManager:success");
                                    latchFacebook.countDown();
                                }

                                @Override
                                public void fail() {
                                    Logger.debug(TAG, "FacebookManager:fail");
                                    latchFacebook.countDown();
                                }
                            });
                        }
                    });

                } catch (Exception e) {
                    latchFacebook.countDown();
                }
            }
        }).start();

        final CountDownLatch latchTweeter = new CountDownLatch(1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    latchFacebook.await();
                    contextHelper.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TwitterManager.getInstance(contextHelper).logout(new SNSLoginoutListener() {
                                @Override
                                public void success(boolean isLogin) {
                                    Logger.debug(TAG, "TwitterManager:success");

                                    latchTweeter.countDown();
                                }

                                @Override
                                public void fail() {
                                    Logger.debug(TAG, "TwitterManager:fail");

                                    latchTweeter.countDown();
                                }
                            });
                        }
                    });


                } catch (Exception e) {
                    Logger.debug(TAG, e.toString());
                    latchTweeter.countDown();
                }
            }
        }).start();


        final CountDownLatch latchKakao = new CountDownLatch(1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    latchTweeter.await();

                    contextHelper.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            KakaoManager.getInstance(contextHelper, null, false, false).logout(new SNSLoginoutListener() {
                                @Override
                                public void success(boolean isLogin) {
                                    Logger.debug(TAG, "KakaoManager:success");

                                    latchKakao.countDown();
                                }

                                @Override
                                public void fail() {
                                    Logger.debug(TAG, "KakaoManager:fail");

                                    latchKakao.countDown();
                                }
                            });
                        }
                    });

                } catch (Exception e) {
                    latchKakao.countDown();
                }
            }
        }).start();


        final CountDownLatch latchWithdraw = new CountDownLatch(1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    latchKakao.await();

                    if (isWithdraw) {
                        UserManager.getInstance(contextHelper).delete(me,
                                new Response.Listener<UserData>() {
                                    @Override
                                    public void onResponse(UserData response) {
                                        if (response.isSuccess()) {
                                            latchWithdraw.countDown();
                                        } else {
                                            new SweetAlertDialog(getContext())
                                                    .setContentText(response.getErrorMessage())
                                                    .show();
                                        }
                                    }
                                },
                                null
                        );
                    } else {
                        latchWithdraw.countDown();
                    }


                } catch (Exception e) {
                    latchKakao.countDown();
                }
            }
        }).start();


        new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    latchWithdraw.await();
                    contextHelper.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            contextHelper.hideProgress();

                            if (isWithdraw) {
                                Toast.makeText(contextHelper.getContext(), "탈퇴 하였습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(contextHelper.getContext(), "로그아웃 하였습니다.", Toast.LENGTH_SHORT).show();
                            }

                            contextHelper.restart();
                            contextHelper.getActivity().finish();

                        }
                    });

                } catch (Exception e) {

                }
            }
        }).start();

    }

}



