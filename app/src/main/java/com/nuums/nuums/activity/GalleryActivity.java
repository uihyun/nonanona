package com.nuums.nuums.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.nuums.nuums.R;
import com.yongtrim.lib.activity.ABaseFragmentAcitivty;
import com.yongtrim.lib.activity.gallery.GalleryWidget.BasePagerAdapter;
import com.yongtrim.lib.activity.gallery.GalleryWidget.GalleryViewPager;
import com.yongtrim.lib.activity.gallery.GalleryWidget.UrlPagerAdapter;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.model.photo.Photo;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.ui.CircularNetworkImageView;
import com.yongtrim.lib.ui.TintImageView;
import com.yongtrim.lib.ui.UltraButton;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;

/**
 * nuums / com.nuums.nuums.activity
 * <p/>
 * Created by yongtrim.com on 16. 1. 10..
 */
public class GalleryActivity extends ABaseFragmentAcitivty {

    GalleryViewPager viewPager;

    List<Photo> photos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);



        Photo photo = Photo.getPhoto(contextHelper.getActivity().getIntent().getStringExtra("photo"));

        List<String> items = new ArrayList<String>();
        try {
                items.add(photo.getUrl());
        } catch (Exception e) {
        }


        UrlPagerAdapter pagerAdapter = new UrlPagerAdapter(this, items);

        viewPager = (GalleryViewPager)findViewById(R.id.viewer);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);

        // close button click event
        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

}

