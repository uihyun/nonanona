package com.nuums.nuums.view;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.StateListDrawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nuums.nuums.R;
import com.nuums.nuums.adapter.PhotoCellAdapter;
import com.yongtrim.lib.Config;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.activity.ABaseFragmentAcitivty;
import com.yongtrim.lib.log.Logger;
import com.yongtrim.lib.model.photo.Photo;
import com.yongtrim.lib.util.PixelUtil;

import org.askerov.dynamicgrid.DynamicGridView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * nuums / com.nuums.nuums.view
 * <p/>
 * Created by yongtrim.com on 15. 12. 20..
 */
public class PhotoScrollView extends LinearLayout {
    final String TAG = "PhotoScrollView";

    List<Photo> photos;
    int maxPhotoCount = Config.MAX_NANUMPHOTO_CNT;
    int tag = 0;

    DynamicGridView gridView;
    PhotoCellAdapter photoCellAdapter;


    public interface OnPhotoChangeListener {
        /**
         * Called when the list reaches the last item (the last item is visible
         * to the user)
         */
        public void onChanged(List<Photo> photos);
        public void onSelected(int index);
    }

    OnPhotoChangeListener mOnPhotoChangeListener;

    public void setPhotoChangeListener(OnPhotoChangeListener photoChangeListener) {
        mOnPhotoChangeListener = photoChangeListener;
    }


    public PhotoScrollView(Context context) {
        super(context);
        initView();
    }


    public PhotoScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public void initView() {
        this.setGravity(Gravity.CENTER);

        int cellWidth = 52;
        int cellHeight = 52;
        int padding = 6;
        int columns = 6;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(PixelUtil.dpToPx(getContext(), cellWidth*columns + 5*padding),
                PixelUtil.dpToPx(getContext(), cellHeight + padding*2));
        gridView = new DynamicGridView(getContext());
        gridView.setNumColumns(6);
        gridView.setHorizontalSpacing(PixelUtil.dpToPx(getContext(), 6));
        gridView.setColumnWidth(PixelUtil.dpToPx(getContext(), cellWidth));
        gridView.setStretchMode(GridView.NO_STRETCH);

        gridView.setPadding(0, PixelUtil.dpToPx(getContext(), 6), 0, PixelUtil.dpToPx(getContext(), 6));
        addView(gridView, params);



    }


    public void setupGridView() {
        if(photoCellAdapter != null)
            return;

        photoCellAdapter = new PhotoCellAdapter(getContext(), photos, 6);
        gridView.setAdapter(photoCellAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mOnPhotoChangeListener.onSelected(position);
            }
        });
        gridView.setOnDragListener(new DynamicGridView.OnDragListener() {
            @Override
            public void onDragStarted(int position) {
            }

            @Override
            public void onDragPositionsChanged(int oldPosition, int newPosition) {

                if (oldPosition != newPosition) {
                    Photo photo = photos.remove(oldPosition);
                    photos.add(newPosition, photo);

                    mOnPhotoChangeListener.onChanged(photos);
                }

            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                gridView.startEditMode(position);
                return true;
            }
        });
        gridView.setOnDropListener(new DynamicGridView.OnDropListener()
        {
            @Override
            public void onActionDrop()
            {
                gridView.stopEditMode();
            }
        });
    }

    public int getMaxPhotoCount() {
        return maxPhotoCount;
    }

    public void setMaxPhotoCount(int count) {
        maxPhotoCount = count;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getCurrentPhotoCount() {
        if(photos == null)
            return 0;
        return photos.size();
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
        setupGridView();
        photoCellAdapter.setPhotos(photos);
        photoCellAdapter.notifyDataSetChanged();
    }

    public void setSelectIndex(int selectIndex) {
        photoCellAdapter.setSelectedIndex(selectIndex);
        photoCellAdapter.notifyDataSetChanged();
    }

    public void refresh(final ContextHelper contextHelper) {
        photoCellAdapter.notifyDataSetChanged();
    }

    public void loadPhotos(final ContextHelper contextHelper, int startIndex, List<Photo> photoNews) {
        for(int i = 0;i < photoNews.size();i++) {
            Photo photo = photoNews.get(i);

            if(photo.isLocalBitmap() && !photo.hasPhoto()) {
                photo.makeBitmap(contextHelper);
                photos.set(startIndex + i, photo);
            }
        }

        mOnPhotoChangeListener.onChanged(photos);
    }

}

