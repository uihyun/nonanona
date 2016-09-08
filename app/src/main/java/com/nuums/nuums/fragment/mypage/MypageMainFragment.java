package com.nuums.nuums.fragment.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.nuums.nuums.R;
import com.nuums.nuums.activity.BaseActivity;
import com.nuums.nuums.fragment.nanum.NanumListFragment;
import com.nuums.nuums.model.nanum.Nanum;
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.fragment.ABaseFragment;
import com.yongtrim.lib.log.Logger;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.model.post.PostData;
import com.yongtrim.lib.model.post.PostManager;
import com.yongtrim.lib.model.user.LoginManager;
import com.yongtrim.lib.model.user.UserData;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.sns.SNSLoginoutListener;
import com.yongtrim.lib.sns.facebook.FacebookManager;
import com.yongtrim.lib.sns.googleplus.GooglePlusManager;
import com.yongtrim.lib.sns.kakao.KakaoManager;
import com.yongtrim.lib.sns.twitter.TwitterManager;
import com.yongtrim.lib.ui.PagerSlidingTabStrip2;
import com.yongtrim.lib.ui.sweetalert.SweetAlertDialog;

import java.util.concurrent.CountDownLatch;

import de.greenrobot.event.EventBus;

/**
 * nuums / com.nuums.nuums.fragment.mypage
 * <p/>
 * Created by yongtrim.com on 15. 12. 13..
 */
public class MypageMainFragment extends ABaseFragment {
    private final String TAG = getClass().getSimpleName();

//    AlarmListFragment alarmListFragment;
//    NanumListFragment nanumListFragment;
//    NanumListFragment applyListFragment;
//    ApplyListFragment deliveryListFragment;
//    NanumListFragment wonListFragment;

    View mainView;

    public ViewPager pager;



    public void refreshMyPageMain() {

        TabsPagerAdapter adapter = (TabsPagerAdapter)pager.getAdapter();

        ((AlarmListFragment)adapter.getRegisteredFragment(0)).mypageView.refresh();
        ((NanumListFragment)adapter.getRegisteredFragment(1)).mypageView.refresh();
        ((NanumListFragment)adapter.getRegisteredFragment(2)).mypageView.refresh();
        ((ApplyListFragment)adapter.getRegisteredFragment(3)).mypageView.refresh();
        ((NanumListFragment)adapter.getRegisteredFragment(4)).mypageView.refresh();
    }



    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_mypagemain, container, false);

        pager = (ViewPager)mainView.findViewById(R.id.pager);
        pager.setAdapter(new TabsPagerAdapter(getChildFragmentManager()));
        pager.setOffscreenPageLimit(4);
        pager.setPageTransformer(false, new NoPageTransformer());



        refresh();
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

    void refresh() {
//        TextView tvNickname = (TextView)mainView.findViewById(R.id.tvNickname);
//        NsUser user = UserManager.getInstance(contextHelper).getMe();
//        tvNickname.setText(user.getNicknameSafe());
    }

    public void onButtonClicked(View v) {
        TabsPagerAdapter adapter = (TabsPagerAdapter)pager.getAdapter();

        adapter.getRegisteredFragment(pager.getCurrentItem()).onButtonClicked(v);

        switch(v.getId()) {
//            case R.id.btnLogout: {

//            }
//            break;
            case R.id.actionbarImageButton: {
                Intent i = new Intent(getContext(), BaseActivity.class);
                i.putExtra("activityCode", BaseActivity.ActivityCode.SETTING.ordinal());
                getContext().startActivity(i);
            }
            break;
        }
    }


    public void onEvent(PushMessage pushMessage) {
        TabsPagerAdapter adapter = (TabsPagerAdapter)pager.getAdapter();

        adapter.getRegisteredFragment(0).onEvent(pushMessage);
        adapter.getRegisteredFragment(1).onEvent(pushMessage);
        adapter.getRegisteredFragment(2).onEvent(pushMessage);
        adapter.getRegisteredFragment(3).onEvent(pushMessage);
        adapter.getRegisteredFragment(4).onEvent(pushMessage);

        switch(pushMessage.getActionCode()) {
            case PushMessage.ACTIONCODE_ADDED_ASK_OTHER:
            case PushMessage.ACTIONCODE_ADDED_POST_OTHER:
                UserManager.getInstance(contextHelper).read(UserManager.getInstance(contextHelper).getMe().getId(),
                        new Response.Listener<UserData>() {
                            @Override
                            public void onResponse(UserData response) {
                                if (response.isSuccess()) {
                                    UserManager.getInstance(contextHelper).setMe(response.user);
                                    EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_CHANGE_ME).setObject(response.user));
                                }
                            }
                        },
                        null
                );
                break;
        }
    }


    public void setPager(ViewPager pager) {
        this.pager = pager;
    }


    private class TabsPagerAdapter extends FragmentStatePagerAdapter implements PagerSlidingTabStrip2.CheckTabProvider {

        SparseArray<ABaseFragment> registeredFragments = new SparseArray<ABaseFragment>();

        public TabsPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0: return AlarmListFragment.create();
                case 1: return NanumListFragment.create(NanumListFragment.LISTTYPE_MINE);
                case 2: return NanumListFragment.create(NanumListFragment.LISTTYPE_APPLY);
                case 3: return ApplyListFragment.create();
                case 4: return NanumListFragment.create(NanumListFragment.LISTTYPE_WON);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public String getPageTitle(int position) {
            String[] titles = {"알림", "나의나눔", "나눔신청", "택배현황", "받은나눔"};
            return titles[position];
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ABaseFragment fragment = (ABaseFragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public ABaseFragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        TabsPagerAdapter adapter = (TabsPagerAdapter)pager.getAdapter();

        adapter.getRegisteredFragment(pager.getCurrentItem()).onActivityResult(requestCode, resultCode, data);

//        switch(pager.getCurrentItem()) {
//            case 0:
//                alarmListFragment.onActivityResult(requestCode, resultCode, data);
//                break;
//            case 1:
//                nanumListFragment.onActivityResult(requestCode, resultCode, data);
//                break;
//            case 2:
//                applyListFragment.onActivityResult(requestCode, resultCode, data);
//                break;
//            case 3:
//                deliveryListFragment.onActivityResult(requestCode, resultCode, data);
//                break;
//            case 4:
//                wonListFragment.onActivityResult(requestCode, resultCode, data);
//                break;
//        }
    }



    private static class NoPageTransformer implements ViewPager.PageTransformer {
        public void transformPage(View view, float position) {
            if (position < 0) {
                view.setScrollX((int)((float)(view.getWidth()) * position));
            } else if (position > 0) {
                view.setScrollX(-(int) ((float) (view.getWidth()) * -position));
            } else {
                view.setScrollX(0);
            }
        }
    }
}

