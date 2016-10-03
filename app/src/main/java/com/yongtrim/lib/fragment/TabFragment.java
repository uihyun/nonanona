package com.yongtrim.lib.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.activity.ABaseFragmentAcitivty;
import com.yongtrim.lib.message.PushMessage;

/**
 * hair / com.yongtrim.lib.fragment
 * <p/>
 * Created by Uihyun on 15. 10. 11..
 */
public class TabFragment extends ABaseFragment {
    final static String TAG = "TabFragment";

    private LinearLayout[] layers;
    private int[] icons;
    private int count;
    private int current;


    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        return null;
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


    public void onButtonClicked(View v) {
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    public void onEvent(PushMessage pushMessage) {

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }


    protected void setup(int[] icons, LinearLayout[] layers) {
        this.layers = layers;
        this.icons = icons;
        count = layers.length;

    }

    public void setCurrent(int index) {
        current = index;

        for(int i = 0;i < count;i++) {
            layers[i].setVisibility(View.INVISIBLE);
        }

        layers[current].setVisibility(View.VISIBLE);
    }
}

