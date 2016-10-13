package com.nuums.nuums.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.nuums.nuums.R;
import com.nuums.nuums.fragment.mypage.MypageMainFragment;
import com.nuums.nuums.fragment.nanum.NanumEditFragment;
import com.nuums.nuums.fragment.nanum.NanumListFragment;
import com.nuums.nuums.fragment.review.ReviewListFragment;
import com.nuums.nuums.fragment.talk.TalkListFragment;
import com.nuums.nuums.model.chat.Talk;
import com.nuums.nuums.view.SlideMenuView;
import com.yongtrim.lib.activity.ABaseFragmentAcitivty;
import com.yongtrim.lib.fragment.ABaseFragment;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.ui.PagerSlidingTabStrip;

import de.greenrobot.event.EventBus;
import me.tangke.slidemenu.SlideMenu;

/**
 * nuums / com.nuums.nuums
 * <p>
 * Created by Uihyun on 15.12. 7..
 */
public class MainActivity extends ABaseFragmentAcitivty {
    private final String TAG = getClass().getSimpleName();

    private final int MY_PERMISSION_REQUEST_LOCATION = 2;
    public boolean isKeywordEditMode = true;
    SlideMenuExt slideMenu;
    SlideMenuView slideMenuView;
    PagerSlidingTabStrip tabbar;
    ViewPager pager;
    java.util.ArrayList<Talk> talks;
    private EventBus bus = EventBus.getDefault();
    private View viewDim;
    private BackPressCloseHandler backPressCloseHandler;

    void setLeftDrawer() {
        ImageButton btnBack = contextHelper.getActivity().setBackButtonAndVisiable(R.drawable.leftdr_button);
        contextHelper.getActivity().setLeftButtonMarginLeft(12);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSlideMenu();
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        slideMenu = new SlideMenuExt(MainActivity.this);

        slideMenuView = new SlideMenuView(MainActivity.this, contextHelper);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        slideMenu.addView(slideMenuView,
                new SlideMenu.LayoutParams((int) ((float) metrics.widthPixels * 0.9f),
                        SlideMenu.LayoutParams.MATCH_PARENT, SlideMenu.LayoutParams.ROLE_PRIMARY_MENU));
        slideMenu.setPrimaryShadowWidth(0);

        setContentView(slideMenu);

        View contentView = getLayoutInflater().inflate(R.layout.activity_main, null);
        slideMenu.addView(contentView, new SlideMenu.LayoutParams(
                SlideMenu.LayoutParams.MATCH_PARENT, SlideMenu.LayoutParams.MATCH_PARENT,
                SlideMenu.LayoutParams.ROLE_CONTENT));

        viewDim = contentView.findViewById(R.id.viewDim);

        // TAB
        tabbar = (PagerSlidingTabStrip) findViewById(R.id.tabbar);
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new TabsPagerAdapter(getSupportFragmentManager()));
        pager.setOffscreenPageLimit(4);
        pager.setCurrentItem(0);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        contextHelper.getActivity().setupActionBar(""/*"나눔목록"*/);
                        contextHelper.getActivity().backgroundColor(ContextCompat.getColor(contextHelper.getContext(), R.color.green));

                        setLeftDrawer();
                        contextHelper.getActivity().setImageButtonAndVisiable(R.drawable.search);
                        contextHelper.getActivity().setRightButtonMarginRight(10);


                        break;
                    case 1:
                        contextHelper.getActivity().setupActionBar(""/*"대화하기"*/);
                        contextHelper.getActivity().backgroundColor(ContextCompat.getColor(contextHelper.getContext(), R.color.green));


                        setLeftDrawer();

                        contextHelper.getActivity().setImageButtonAndVisiable(R.drawable.setwhite);
                        contextHelper.getActivity().setRightButtonMarginRight(10);

                        //contextHelper.getActivity().setImageButtonVisibility(talks != null && talks.size() > 0);

                        break;
                    case 2:
                        contextHelper.getActivity().setupActionBar(""/*"나눔등록"*/);
                        contextHelper.getActivity().backgroundColor(ContextCompat.getColor(contextHelper.getContext(), R.color.green));


                        setLeftDrawer();

                        contextHelper.getActivity().setImageButtonAndVisiable(R.drawable.tresh);
                        contextHelper.getActivity().setRightButtonMarginRight(10);
                        break;
                    case 3:
                        contextHelper.getActivity().setupActionBar(""/*"후기"*/);
                        contextHelper.getActivity().backgroundColor(ContextCompat.getColor(contextHelper.getContext(), R.color.green));


                        setLeftDrawer();

                        contextHelper.getActivity().setImageButtonAndVisiable(0);
                        contextHelper.getActivity().setRightButtonMarginRight(0);
                        break;
                    case 4:
                        contextHelper.getActivity().setupActionBar(""/*"마이페이지"*/);
                        contextHelper.getActivity().backgroundColor(ContextCompat.getColor(contextHelper.getContext(), R.color.green));

                        setLeftDrawer();
                        contextHelper.getActivity().setImageButtonAndVisiable(0);
                        contextHelper.getActivity().setRightButtonMarginRight(0);

                        //contextHelper.getActivity().setImageButtonAndVisiable(R.drawable.setwhite);
                        //contextHelper.getActivity().setRightButtonMarginRight(10);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabbar.setViewPager(pager);

        bus.register(this);

        backPressCloseHandler = new BackPressCloseHandler(this);

        contextHelper.getActivity().setupActionBar(""/*"나눔목록"*/);
        setLeftDrawer();
        contextHelper.getActivity().setImageButtonAndVisiable(R.drawable.search);
        contextHelper.getActivity().setRightButtonMarginRight(10);

        tabbar.setChatBadge(UserManager.getInstance(contextHelper).getMe().getUnreadCnt());

        if (!UserManager.getInstance(contextHelper).getMe().isLogin()) {
            finish();
            Intent i = new Intent(contextHelper.getContext(), SplashActivity.class);
            contextHelper.getContext().startActivity(i);
        }

        if (contextHelper.getActivity().getIntent().hasExtra("to")) {
            Intent i = new Intent(contextHelper.getContext(), BaseActivity.class);
            i.putExtra("activityCode", BaseActivity.ActivityCode.CHAT.ordinal());
            i.putExtra("to", contextHelper.getActivity().getIntent().getStringExtra("to"));
            contextHelper.getContext().startActivity(i);
        }

        if (contextHelper.getActivity().getIntent().hasExtra("nanum_id")) {
            Intent i = new Intent(contextHelper.getContext(), BaseActivity.class);
            i.putExtra("activityCode", BaseActivity.ActivityCode.NANUMVIEWER.ordinal());
            i.putExtra("nanum_id", contextHelper.getActivity().getIntent().getStringExtra("nanum_id"));
            contextHelper.getContext().startActivity(i);
        }


        if (!UserManager.getInstance(contextHelper).isReadTutorial()) {
            Intent i = new Intent(this, Base3Activity.class);
            i.putExtra("activityCode", Base3Activity.ActivityCode.TUTORIAL.ordinal());
            startActivity(i);
            UserManager.getInstance(contextHelper).readTutorial();
        }

        checkPermission();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.hasExtra("to")) {
            Intent i = new Intent(contextHelper.getContext(), BaseActivity.class);
            i.putExtra("activityCode", BaseActivity.ActivityCode.CHAT.ordinal());
            i.putExtra("to", intent.getStringExtra("to"));
            contextHelper.getContext().startActivity(i);
        }

        if (intent.hasExtra("nanum_id")) {
            Intent i = new Intent(contextHelper.getContext(), BaseActivity.class);
            i.putExtra("activityCode", BaseActivity.ActivityCode.NANUMVIEWER.ordinal());
            i.putExtra("nanum_id", intent.getStringExtra("nanum_id"));
            contextHelper.getContext().startActivity(i);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission() {
        // mashmellow 이하 no check.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        // 권한이 없는 경우
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            // 최초 거부를 선택하면 두번째부터 이벤트 발생 & 권한 획득이 필요한 이융를 설명
            // Explain to the user why we need to write the permission.
//            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                Toast.makeText(this, "Need Access READ_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show();
//            }
//            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                Toast.makeText(this, "Need Access WRITE_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show();
//            }
//            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
//                Toast.makeText(this, "Need Access CAMERA", Toast.LENGTH_SHORT).show();
//            }
//            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
//                Toast.makeText(this, "Need Access ACCESS_FINE_LOCATION", Toast.LENGTH_SHORT).show();
//            }
//            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
//                Toast.makeText(this, "Need Access ACCESS_COARSE_LOCATION", Toast.LENGTH_SHORT).show();
//            }
//            if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
//                Toast.makeText(this, "Need Access CALL_PHONE", Toast.LENGTH_SHORT).show();
//            }
//            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
//                Toast.makeText(this, "Need Access READ_PHONE_STATE", Toast.LENGTH_SHORT).show();
//            }

            // 요청 팝업 팝업 선택시 onRequestPermissionsResult 이동
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE}, MY_PERMISSION_REQUEST_LOCATION);
        } else {
//            Toast.makeText(this, "Successfully Access", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        bus.unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onButtonClicked(View v) {
        TabsPagerAdapter adapter = (TabsPagerAdapter) pager.getAdapter();
        ((ABaseFragment) adapter.getRegisteredFragment(pager.getCurrentItem())).onButtonClicked(v);

        if (slideMenuView.onButtonClicked(v)) {
            //slideMenu.close(false);
        }


//        switch(pager.getCurrentItem()) {
//            case 0:
//                if(nanumListFragment != null)
//                    nanumListFragment.onButtonClicked(v);
//                break;
//            case 1:
//                if(nanumEditFragment != null)
//                    nanumEditFragment.onButtonClicked(v);
//                break;
//            case 2:
//                if(talkListFragment != null)
//                    talkListFragment.onButtonClicked(v);
//                break;
//            case 3:
//                if(reviewListFragment != null)
//                    reviewListFragment.onButtonClicked(v);
//                break;
//            case 4:
//                if(mypageMainFragment != null)
//                    mypageMainFragment.onButtonClicked(v);
//                break;
//        }
    }

    public void refreshMain() {
        TabsPagerAdapter adapter = (TabsPagerAdapter) pager.getAdapter();
        ((MypageMainFragment) adapter.getRegisteredFragment(4)).refreshMyPageMain();
    }


    public void onEvent(final PushMessage pushMessage) {
        slideMenuView.onEvent(pushMessage);
        try {
            FragmentManager fm = getSupportFragmentManager();
            for (Fragment f : fm.getFragments()) { // to loop through fragments and checking their type
                ((ABaseFragment) f).onEvent(pushMessage);
            }
        } catch (Exception e) {
        }

        switch (pushMessage.getActionCode()) {
            case PushMessage.ACTIONCODE_CHANGE_LOGOUT: {
                contextHelper.getActivity().finish();
            }
            break;
            case PushMessage.ACTIONCODE_ADDED_NANUM: {
                pager.setCurrentItem(0);
            }
            break;
            case PushMessage.ACTIONCODE_CHANGETALKLIST: {
                talks = (java.util.ArrayList<Talk>) pushMessage.getObject(contextHelper);
                //ViewPager pager = (ViewPager) findViewById(R.id.pager);

//                if(pager.getCurrentItem() == 2) {
//                    contextHelper.getActivity().setImageButtonVisibility(talks != null && talks.size() > 0);
//                }

            }
            break;
            case PushMessage.ACTIONCODE_CHANGETALK: {
                contextHelper.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final Talk talk = (Talk) pushMessage.getObject(contextHelper);

                        if (talk.getMe(contextHelper) != null) {
                            tabbar.setChatBadge(talk.getMe(contextHelper).getUnreadCnt());
                        }
                    }
                });

            }
            break;
            case PushMessage.ACTIONCODE_CHANGEUNREADCOUNT: {
                contextHelper.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final Integer count = (Integer) pushMessage.getObject(contextHelper);
                        tabbar.setChatBadge(count.intValue());
                    }
                });

            }
            break;


        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        TabsPagerAdapter adapter = (TabsPagerAdapter) pager.getAdapter();
        ((ABaseFragment) adapter.getRegisteredFragment(pager.getCurrentItem())).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    public void toggleSlideMenu() {
        if (slideMenu.isOpen()) {
            slideMenu.close(true);
        } else {
            slideMenu.open(false, true);
        }
    }

    private class TabsPagerAdapter extends FragmentStatePagerAdapter implements PagerSlidingTabStrip.IconTabProvider {
        SparseArray<ABaseFragment> registeredFragments = new SparseArray<ABaseFragment>();

        public TabsPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }


        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new NanumListFragment();
                case 1:
                    return new TalkListFragment();
                case 2:
                    return new NanumEditFragment();
                case 3:
                    return new ReviewListFragment();
                case 4:
                    return new MypageMainFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public int getPageIconResId(int position) {
            return 0;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // return the title of the tab
            return "";
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

    public class BackPressCloseHandler {

        private long backKeyPressedTime = 0;
        private Toast toast;

        private Activity activity;

        public BackPressCloseHandler(Activity context) {
            this.activity = context;
        }

        public void onBackPressed() {
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                showGuide();
                return;
            }
            if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                activity.finish();
                toast.cancel();
            }
        }

        private void showGuide() {
            toast = Toast.makeText(activity, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.",
                    Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    class SlideMenuExt extends me.tangke.slidemenu.SlideMenu {
        public SlideMenuExt(Context context) {
            super(context);
        }

        @Override
        protected void setCurrentState(int currentState) {
            super.setCurrentState(currentState);
            if (currentState == STATE_CLOSE) {
                if (viewDim != null)
                    viewDim.setVisibility(View.GONE);
            } else {
                if (viewDim != null)
                    viewDim.setVisibility(View.VISIBLE);
            }
        }
    }
}
