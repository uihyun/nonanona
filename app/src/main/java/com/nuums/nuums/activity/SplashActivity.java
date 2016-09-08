package com.nuums.nuums.activity;

import android.os.Bundle;

import com.nuums.nuums.R;

import com.nuums.nuums.fragment.misc.NsSplashFragment;
import com.yongtrim.lib.activity.ABaseFragmentAcitivty;

public class SplashActivity extends ABaseFragmentAcitivty {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin);
        addFragment(new NsSplashFragment());
    }

}
