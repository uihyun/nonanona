package com.nuums.nuums.fragment.misc;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.Response;
import com.nuums.nuums.R;
import com.nuums.nuums.activity.Base2Activity;
import com.nuums.nuums.activity.BaseActivity;
import com.nuums.nuums.activity.MainActivity;
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.fragment.SplashFragment;
import com.yongtrim.lib.log.Logger;
import com.yongtrim.lib.message.MessageManager;
import com.yongtrim.lib.model.banner.BannerData;
import com.yongtrim.lib.model.banner.BannerManager;
import com.yongtrim.lib.model.config.ConfigManager;
import com.yongtrim.lib.model.location.LocationManager;
import com.yongtrim.lib.model.post.PostListData;
import com.yongtrim.lib.model.post.PostManager;
import com.yongtrim.lib.model.user.UserManager;

import java.util.concurrent.CountDownLatch;

/**
 * nuums / com.nuums.nuums.fragment.misc
 * <p/>
 * Created by yongtrim.com on 15. 12. 8..
 */
public class NsSplashFragment extends SplashFragment {
    private final String TAG = getClass().getSimpleName();
    ImageView ivAniLoading;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        View view = inflater.inflate(R.layout.fragment_splash, container, false);

        ivAniLoading = (ImageView)view.findViewById(R.id.ivAnimation);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if(requestCode == PLAY_SERVICES_RESOLUTION_REQUEST) {
            doProcess();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        doProcess();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void doProcess() {

        final CountDownLatch latchMain = new CountDownLatch(2);

//        ivAniLoading.setBackgroundResource(R.drawable.frame_loading);
//        AnimationDrawable frameAniLoading = (AnimationDrawable) ivAniLoading.getBackground();
//        frameAniLoading.start();
        //
        // get gcm
        CountDownLatch latchGcm = new CountDownLatch(1);
        MessageManager.getInstance(contextHelper).initialize(null, latchGcm);

        //
        // get config
        CountDownLatch latchConfig = new CountDownLatch(1);
        getConfig(latchGcm, latchConfig);

        CountDownLatch latchUserInfo = new CountDownLatch(1);
        contextHelper.userInfo(latchConfig, latchUserInfo);

        getMisc(latchUserInfo, latchMain);


        MessageManager.getInstance(contextHelper).getPreference().putCurChatter(null);

        goMainAcitivty(latchMain, null);
    }

    private void goMainAcitivty(final CountDownLatch latchWait, final CountDownLatch latchCount) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    if (latchWait != null)
                        latchWait.await();

                    NsUser user = UserManager.getInstance(contextHelper).getMe();
                    if(user != null && UserManager.getInstance(contextHelper).getMe().isLogin()) {
                        if(ConfigManager.getInstance(contextHelper).getPreference().getAutoLogin()) {
                            Intent i = new Intent(contextHelper.getContext(), MainActivity.class);

                            if(contextHelper.getActivity().getIntent().hasExtra("to")) {
                                i.putExtra("to", contextHelper.getActivity().getIntent().getStringExtra("to"));
                            }

                            if(contextHelper.getActivity().getIntent().hasExtra("nanum_id")) {
                                i.putExtra("nanum_id", contextHelper.getActivity().getIntent().getStringExtra("nanum_id"));
                            }

                            getActivity().startActivity(i);
                            getActivity().finish();
                        } else {
                            Intent i = new Intent(getContext(), Base2Activity.class);
                            i.putExtra("activityCode", Base2Activity.ActivityCode.SIGNIN.ordinal());
                            getContext().startActivity(i);
                        }
                    } else {
                        Intent i = new Intent(getContext(), Base2Activity.class);
                        i.putExtra("activityCode", Base2Activity.ActivityCode.SIGNIN.ordinal());
                        getContext().startActivity(i);
                    }

                    getActivity().finish();

                } catch (Exception e) {

                }
            }
        }).start();
    }

    private void getMisc(final CountDownLatch latchWait, final CountDownLatch latchCount) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    if (latchWait != null)
                        latchWait.await();

                    BannerManager.getInstance(contextHelper).find(
                            new Response.Listener<BannerData>() {
                                @Override
                                public void onResponse(BannerData response) {
                                    ConfigManager.getInstance(contextHelper).setBanner(response.banner);
                                    if (latchCount != null)
                                        latchCount.countDown();
                                }
                            },
                            null
                    );

                    PostManager.getInstance(contextHelper).find("EVENT", 1,
                            new Response.Listener<PostListData>() {
                                @Override
                                public void onResponse(PostListData response) {
                                    ConfigManager.getInstance(contextHelper).setEvents(response.getPostList());
                                    if (latchCount != null)
                                        latchCount.countDown();
                                }
                            },
                            null
                    );



                } catch (Exception e) {

                }
            }
        }).start();
    }



}

