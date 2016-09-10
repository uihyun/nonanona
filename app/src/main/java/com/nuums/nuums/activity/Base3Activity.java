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
import com.yongtrim.lib.activity.ABaseFragmentAcitivty;
import com.yongtrim.lib.fragment.ABaseFragment;
import com.yongtrim.lib.message.PushMessage;

import de.greenrobot.event.EventBus;

/**
 * Created by YongTrim on 16. 6. 15. for nuums_ad
 */
public class Base3Activity extends ABaseFragmentAcitivty {
    ABaseFragment curFragment;
    ActivityCode curActivity;

    private EventBus bus = EventBus.getDefault();

    public static enum ActivityCode {
        TUTORIAL(0);

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

//        String shop_id = null;
//        String hair_id = null;
//        try {
//            if (getIntent() != null) {
//                Uri uri = getIntent().getData();
//                if (uri != null) {
//                    shop_id = uri.getQueryParameter("shop_id");
//                    hair_id = uri.getQueryParameter("hair_id");
//                }
//            }
//        } catch(Exception e) {
//
//        }
//
//        if(shop_id != null) {
//            activityCode = ActivityCode.SHOP_VIEWER;
//        } else if(hair_id != null) {
//            activityCode = ActivityCode.HAIR_VIEWER;
//        } else {
        activityCode = ActivityCode.valueOf(getIntent().getIntExtra("activityCode", 0));
//        }

        curActivity = activityCode;

        switch(activityCode) {
               case TUTORIAL:
                curFragment = new TutorialFragment();
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
