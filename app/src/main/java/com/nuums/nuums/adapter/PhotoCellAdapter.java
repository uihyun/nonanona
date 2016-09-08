package com.nuums.nuums.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.nuums.nuums.AppController;
import com.nuums.nuums.R;
import com.yongtrim.lib.model.photo.Photo;
import com.yongtrim.lib.ui.CustomNetworkImageView;
import com.yongtrim.lib.util.UIUtil;

import org.askerov.dynamicgrid.BaseDynamicGridAdapter;

import java.util.List;

/**
 * Created by YongTrim on 16. 6. 16. for nuums_ad
 */
public class PhotoCellAdapter extends BaseDynamicGridAdapter {
    private List<Photo> photos;
    protected ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    int selectedIndex;

    class Holder {
        CustomNetworkImageView ivThumbnail;
        ImageView ivAdd;
        View viewSelect;

        public View set(View v) {
            v.setTag(this);

            ivThumbnail = (CustomNetworkImageView)v.findViewById(R.id.ivThumbnail);
            ivAdd = (ImageView)v.findViewById(R.id.ivAdd);
            viewSelect = (View)v.findViewById(R.id.viewSelect);
            return v;
        }
    }

    public PhotoCellAdapter(Context context, List<?> items, int columnCount) {
        super(context, items, columnCount);
    }


    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public void setSelectedIndex(int index) {
        selectedIndex = index;
    }
//    @Override
//    public int getCount() {
//        if(photos == null)
//            return 0;
//        return photos.size();
//    }
//
//
//    @Override
//    public Object getItem(int position) {
//        if(photos == null)
//            return null;
//        return photos.get(position);
//    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)parent.getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = new Holder().set(mInflater.inflate(R.layout.cell_photo, parent, false));
        }

        final Holder h = (Holder)convertView.getTag();
        final Photo photo = photos.get(position);

        if (photo == null || !photo.hasPhoto()) {
            h.ivThumbnail.setVisibility(View.GONE);
            h.ivAdd.setVisibility(View.VISIBLE);

        } else {
            h.ivThumbnail.setVisibility(View.VISIBLE);
            h.ivAdd.setVisibility(View.GONE);

            if(TextUtils.isEmpty(photo.getId())) {
                if(photo.getBitmap() != null) {
                    h.ivThumbnail.setLocalImageBitmap(photo.getBitmap());
                }
            } else {
                if (!TextUtils.isEmpty(photo.getSmallUrl()))
                    h.ivThumbnail.setImageUrl(photo.getSmallUrl(), imageLoader);
                else
                    h.ivThumbnail.setImageUrl("", imageLoader);
            }
        }

        if(position == selectedIndex) {
            h.viewSelect.setVisibility(View.VISIBLE);
        } else {
            h.viewSelect.setVisibility(View.GONE);
        }

        return convertView;
    }



}

