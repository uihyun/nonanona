package com.yongtrim.lib.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nuums.nuums.AppController;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.yongtrim.lib.ui.salvage.RecyclingPagerAdapter;

import java.util.List;

/**
 * hair / com.yongtrim.lib.adapter
 * <p/>
 * Created by Uihyun on 15. 9. 15..
 */
public class ImagePagerAdapter extends RecyclingPagerAdapter {

    private Context context;
    private List<String> listUrl;

    private int           size;
    private boolean       isInfiniteLoop;

    String urlTag;

    ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER_CROP;


    public ImagePagerAdapter(Context context, List<String> listUrl) {
        this.context = context;
        this.listUrl = listUrl;
        this.size = listUrl.size();//ListUtils.getSize(imageIdList);
        isInfiniteLoop = false;
    }


//    public void setListUrl(List<String> listUrl) {
//        this.listUrl = listUrl;
//        this.size = listUrl.size();
//    }


    public void setUrlTag(String urlTag) {
        this.urlTag = urlTag;
    }

    public String getUrlTag() {
        return urlTag;
    }

    @Override
    public int getItemPosition(Object object){
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        // Infinite loop
        return isInfiniteLoop ? Integer.MAX_VALUE : listUrl.size();//ListUtils.getSize(imageIdList);
    }

    public void setScaleType(ImageView.ScaleType scaleType) {
        this.scaleType = scaleType;
    }

    /**
     * get really position
     *
     * @param position
     * @return
     */
    private int getPosition(int position) {
        return isInfiniteLoop ? position % size : position;
    }

    @Override
    public View getView(int position, View view, ViewGroup container) {
        ViewHolder holder;
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();

        if (view == null) {
            holder = new ViewHolder();
            view = holder.imageView = new NetworkImageView(context);
            holder.imageView.setScaleType(scaleType);
            view.setTag(holder);
            holder.imageView.setBackgroundColor(0x00000000);
        }
        holder = (ViewHolder)view.getTag();
        holder.imageView.setImageUrl(listUrl.get(getPosition(position)), imageLoader);
        return view;
    }

    private static class ViewHolder {

        NetworkImageView imageView;
    }

    /**
     * @return the isInfiniteLoop
     */
    public boolean isInfiniteLoop() {
        return isInfiniteLoop;
    }

    /**
     * @param isInfiniteLoop the isInfiniteLoop to set
     */
    public ImagePagerAdapter setInfiniteLoop(boolean isInfiniteLoop) {
        this.isInfiniteLoop = isInfiniteLoop;
        return this;
    }
}
