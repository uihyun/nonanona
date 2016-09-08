package com.nuums.nuums.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.nuums.nuums.R;
import com.nuums.nuums.fragment.membership.FindAccountFragment;
import com.nuums.nuums.fragment.membership.NsSignupFragment;
import com.nuums.nuums.fragment.membership.SigninFragment;
import com.nuums.nuums.fragment.misc.AlarmFragment;
import com.nuums.nuums.fragment.misc.ApplyFragment;
import com.nuums.nuums.fragment.misc.AskFragment;
import com.nuums.nuums.fragment.misc.PostListFragment;
import com.nuums.nuums.fragment.misc.PostViewerFragment;
import com.nuums.nuums.fragment.misc.PostcodeFragment;
import com.nuums.nuums.fragment.misc.ReportFragment;
import com.nuums.nuums.fragment.misc.SettingFragment;
import com.nuums.nuums.fragment.misc.TutorialFragment;
import com.nuums.nuums.fragment.misc.UserListFragment;
import com.nuums.nuums.fragment.misc.YongdalFragment;
import com.nuums.nuums.fragment.nanum.DiscriptionEditFragment;
import com.nuums.nuums.fragment.nanum.MapFragment;
import com.nuums.nuums.fragment.nanum.NanumEditFragment;
import com.nuums.nuums.fragment.nanum.NanumListFragment;
import com.nuums.nuums.fragment.nanum.NanumViewerFragment;
import com.nuums.nuums.fragment.review.ReviewEditFragment;
import com.nuums.nuums.fragment.review.ReviewListFragment;
import com.nuums.nuums.fragment.talk.ChatFragment;
import com.nuums.nuums.fragment.test.ChipsEdittextFragment;
import com.yongtrim.lib.fragment.ABaseFragment;
import com.yongtrim.lib.activity.ABaseFragmentAcitivty;
import com.yongtrim.lib.fragment.SignupFragment;
import com.yongtrim.lib.message.PushMessage;

import de.greenrobot.event.EventBus;

/**
 * nuums / com.nuums.nuums
 * <p/>
 * Created by yongtrim.com on 15.12. 7..
 */
public class BaseActivity extends ABaseFragmentAcitivty {
    ABaseFragment curFragment;
    ActivityCode curActivity;

    private EventBus bus = EventBus.getDefault();

    public static enum ActivityCode {
        SIGNUP(0),
        FINDACCOUNT(1),
        POSTCODE(2),
        DESCRIPTIONEDIT(3),
        NANUMVIEWER(4),
        NANUMEDT(5),
        POST_VIEWER(6),
        CHAT(7),
        APPLY(8),
        TEXT_CHIPSEDITTEXT(9),
        REVIEWEDIT(10),
        REVIEWLIST(11),
        SETTING(12),
        NANUMASK(13),
        ALARM(14),
        REPORT(15),
        POSTLIST(16),
        YONGDAL(17),
        NANUMLIST(18),
        MAP(19),
        USERLIST(20);

        private final int mActivityCode;
        private ActivityCode(int value) {
            mActivityCode = value;
        }

        public static ActivityCode valueOf(int value) {
            for (ActivityCode status : values()) {
                if (status.mActivityCode == value) {
                    return status;
                }
            }
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin);

        bus.register(this);


        ActivityCode activityCode;

        String nanum_id = null;
        try {
            if (getIntent() != null) {
                Uri uri = getIntent().getData();
                if (uri != null) {
                    nanum_id = uri.getQueryParameter("nanum_id");
                }
            }
        } catch(Exception e) {

        }


        if(getIntent().hasExtra("nanum_id")) {
            nanum_id = getIntent().getStringExtra("nanum_id");
        }

        if(nanum_id != null) {
            activityCode = ActivityCode.NANUMVIEWER;
        } else {
            activityCode = ActivityCode.valueOf(getIntent().getIntExtra("activityCode", 0));
        }

        curActivity = activityCode;

        switch(activityCode) {
            case SIGNUP:
                curFragment = new NsSignupFragment();
                curFragment = addFragment(curFragment);
                break;
            case FINDACCOUNT:
                curFragment = new FindAccountFragment();
                curFragment = addFragment(curFragment);
                break;
            case POSTCODE:
                curFragment = new PostcodeFragment();
                curFragment = addFragment(curFragment);
                break;
            case DESCRIPTIONEDIT:
                curFragment = new DiscriptionEditFragment();
                curFragment = addFragment(curFragment);
                break;
            case NANUMVIEWER:
                curFragment = new NanumViewerFragment();
                if(nanum_id != null) {
                    ((NanumViewerFragment) curFragment).setNanumId(nanum_id);
                }
                curFragment = addFragment(curFragment);
                break;
            case NANUMEDT:
                curFragment = new NanumEditFragment();
                curFragment = addFragment(curFragment);
                break;
            case POST_VIEWER:
                curFragment = new PostViewerFragment();
                curFragment = addFragment(curFragment);
                break;
            case CHAT:
                curFragment = new ChatFragment();
                curFragment = addFragment(curFragment);
                break;
            case APPLY:
                curFragment = new ApplyFragment();
                addFragment(curFragment);
                break;
            case TEXT_CHIPSEDITTEXT:
                curFragment = new ChipsEdittextFragment();
                curFragment = addFragment(curFragment);
                break;
            case REVIEWEDIT:
                curFragment = new ReviewEditFragment();
                curFragment = addFragment(curFragment);
                break;
            case REVIEWLIST:
                curFragment = new ReviewListFragment();
                curFragment = addFragment(curFragment);
                break;
            case SETTING:
                curFragment = new SettingFragment();
                curFragment = addFragment(curFragment);
                break;
            case NANUMASK:
                curFragment = new AskFragment();
                curFragment = addFragment(curFragment);
                break;
            case ALARM:
                curFragment = new AlarmFragment();
                curFragment = addFragment(curFragment);
                break;
            case REPORT:
                curFragment = new ReportFragment();
                curFragment = addFragment(curFragment);
                break;
            case POSTLIST:
                curFragment = new PostListFragment();
                curFragment = addFragment(curFragment);
                break;
            case YONGDAL:
                curFragment = new YongdalFragment();
                curFragment = addFragment(curFragment);
                break;
            case NANUMLIST:
                curFragment = new NanumListFragment();
                curFragment = addFragment(curFragment);
                break;
            case MAP:
                curFragment = new MapFragment();
                curFragment = addFragment(curFragment);
                break;
            case USERLIST:
                curFragment = new UserListFragment();
                curFragment = addFragment(curFragment);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        curFragment.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onDestroy(){
        bus.unregister(this);
        super.onDestroy();
    }


    public void onEvent(PushMessage pushMessage) {
        if(curFragment != null) {
            curFragment.onEvent(pushMessage);
        }
    }


    public void onButtonClicked(View v) {
        curFragment.onButtonClicked(v);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(curFragment.onKeyDown(keyCode, event)) {
            return true;
        }

        if(event != null)
            return super.onKeyDown(keyCode, event);
        else {
            finish();
            return true;
        }
    }

}

