package com.nuums.nuums.activity;

import android.os.Bundle;
import android.view.View;

import com.nuums.nuums.R;
import com.yongtrim.lib.activity.ABaseFragmentAcitivty;
import com.yongtrim.lib.activity.gallery.GalleryWidget.GalleryViewPager;
import com.yongtrim.lib.activity.gallery.GalleryWidget.UrlPagerAdapter;
import com.yongtrim.lib.model.photo.Photo;

import java.util.ArrayList;
import java.util.List;

/**
 * nuums / com.nuums.nuums.activity
 * <p/>
 * Created by Uihyun on 16. 1. 10..
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

