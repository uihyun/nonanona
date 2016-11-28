package com.nuums.nuums.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.nuums.nuums.R;
import com.nuums.nuums.fragment.misc.TutorialFragment;
import com.yongtrim.lib.activity.ABaseFragmentAcitivty;
import com.yongtrim.lib.fragment.ABaseFragment;
import com.yongtrim.lib.message.PushMessage;

import de.greenrobot.event.EventBus;

/**
 * Created by Uihyun on 16. 6. 15. for nuums_ad
 */
public class Base3Activity extends ABaseFragmentAcitivty {
    ABaseFragment curFragment;
    ActivityCode curActivity;

    private EventBus bus = EventBus.getDefault();

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

        switch (activityCode) {
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
    protected void onDestroy() {
        bus.unregister(this);
        super.onDestroy();
    }

    public void onEvent(PushMessage pushMessage) {
        if (curFragment != null) {
            curFragment.onEvent(pushMessage);
        }
    }

    public void onButtonClicked(View v) {
        curFragment.onButtonClicked(v);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (curFragment.onKeyDown(keyCode, event)) {
            return true;
        }

        if (event != null)
            return super.onKeyDown(keyCode, event);
        else {
            finish();
            return true;
        }
    }

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

}

