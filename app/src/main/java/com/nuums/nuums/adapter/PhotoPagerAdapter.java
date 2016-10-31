package com.nuums.nuums.adapter;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.nuums.nuums.AppController;
import com.nuums.nuums.R;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.photo.Photo;
import com.yongtrim.lib.ui.CustomNetworkImageView;
import com.yongtrim.lib.ui.salvage.RecyclingPagerAdapter;

import java.util.List;

/**
 * nuums / com.nuums.nuums.adapter
 * <p/>
 * Created by Uihyun on 15. 12. 21..
 */
public class PhotoPagerAdapter extends RecyclingPagerAdapter {
    List<Photo> photos;
    OnPhotoPagerListener mOnPhotoPagerListener;
    private ContextHelper contextHelper;


    public PhotoPagerAdapter(ContextHelper contextHelper, List<Photo> photos) {
        this.contextHelper = contextHelper;
        this.photos = photos;
    }

    public void setOnPhotoPagerListener(OnPhotoPagerListener onPhotoPagerListener) {
        mOnPhotoPagerListener = onPhotoPagerListener;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        // Infinite loop
        return photos.size();
    }

    /**
     * get really position
     *
     * @param position
     * @return
     */
    private int getPosition(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) parent.getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = new Holder().set(mInflater.inflate(R.layout.cell_photobig, parent, false));
        }

        final Holder h = (Holder) convertView.getTag();

        final Photo photo = photos.get(position);

        if (photo.hasPhoto()) {
            h.ivAdd.setVisibility(View.GONE);
            h.btnAdd.setVisibility(View.GONE);
            h.btnDelete.setVisibility(View.VISIBLE);
            h.btnModify.setVisibility(View.VISIBLE);
            h.btnRotate.setVisibility(View.VISIBLE);
            h.ivPhoto.setVisibility(View.VISIBLE);

            if (TextUtils.isEmpty(photo.getId())) {
                if (photo.getBitmap() != null) {
                    h.ivPhoto.setLocalImageBitmap(photo.getBitmap());
                }
            } else {
                if (!TextUtils.isEmpty(photo.getMediumUrl()))
                    h.ivPhoto.setImageUrl(photo.getMediumUrl(), imageLoader);
                else
                    h.ivPhoto.setImageUrl("", imageLoader);
            }

        } else {
            h.ivAdd.setVisibility(View.VISIBLE);
            h.btnAdd.setVisibility(View.VISIBLE);
            h.ivPhoto.setVisibility(View.GONE);
            h.btnDelete.setVisibility(View.GONE);
            h.btnModify.setVisibility(View.GONE);
            h.btnRotate.setVisibility(View.GONE);
        }

        h.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnPhotoPagerListener.add(position);
            }
        });

        h.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnPhotoPagerListener.delete(position);
            }
        });

        h.btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnPhotoPagerListener.modify(position);
            }
        });

        h.btnRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnPhotoPagerListener.rotate(position);
            }
        });

        return convertView;
    }

    public interface OnPhotoPagerListener {
        public void modify(int index);

        public void rotate(int index);

        public void delete(int index);

        public void add(int index);
    }

    class Holder {
        CustomNetworkImageView ivPhoto;
        ImageView ivAdd;
        Button btnDelete;
        Button btnModify;
        Button btnRotate;
        Button btnAdd;

        public View set(View v) {
            v.setTag(this);

            ivPhoto = (CustomNetworkImageView) v.findViewById(R.id.ivPhoto);
            ivAdd = (ImageView) v.findViewById(R.id.ivAdd);
            btnDelete = (Button) v.findViewById(R.id.btnDelete);
            btnDelete.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(contextHelper.getContext(), R.drawable.tresh_round), null, null);
            btnModify = (Button) v.findViewById(R.id.btnModify);
            btnModify.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(contextHelper.getContext(), R.drawable.trim_round), null, null);
            btnRotate = (Button) v.findViewById(R.id.btnRotate);
            btnRotate.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(contextHelper.getContext(), R.drawable.rotation_round), null, null);
            btnAdd = (Button) v.findViewById(R.id.btnAdd);
            return v;
        }
    }
}

