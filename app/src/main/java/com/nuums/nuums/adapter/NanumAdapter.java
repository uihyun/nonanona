package com.nuums.nuums.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.nuums.nuums.AppController;
import com.nuums.nuums.R;
import com.nuums.nuums.activity.BaseActivity;
import com.nuums.nuums.holder.NanumHolder;
import com.nuums.nuums.model.nanum.Nanum;
import com.nuums.nuums.model.nanum.NanumData;
import com.nuums.nuums.model.nanum.NanumManager;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.model.photo.PhotoManager;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.util.MiscUtil;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * nuums / com.nuums.nuums.adapter
 * <p/>
 * Created by Uihyun on 15. 12. 22..
 */
public class NanumAdapter extends BaseAdapter {

    private List<Nanum> nanums;
    private ContextHelper contextHelper;


    public NanumAdapter(ContextHelper contextHelper) {
        super();
        this.contextHelper = contextHelper;
    }

    public void setKeyword(String keyword) {

    }

    public List<Nanum> getData() {
        return this.nanums;
    }

    public void setData(List<Nanum> nanums) {
        this.nanums = nanums;
    }

    @Override
    public int getCount() {
        if (nanums == null)
            return 0;
        return nanums.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return nanums.get(position);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) parent.getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = new NanumHolder().set(mInflater.inflate(R.layout.cell_nanum, parent, false), parent.getContext(), true);
        }
        final NanumHolder h = (NanumHolder) convertView.getTag();

        final Nanum nanum = nanums.get(position);


        PhotoManager.getInstance(contextHelper).setPhotoSmall(h.ivAvatar, nanum.getOwner().getPhoto());

        h.tvTitle.setText(nanum.getTitle());

        h.tvNickname.setText(nanum.getOwner().getNicknameSafe());
        h.tvNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contextHelper.setUserAction(nanum.getOwner());
            }
        });

        h.ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contextHelper.setUserAction(nanum.getOwner());
            }
        });


        if (nanum.isAdmin()) {
            h.viewAddress.setVisibility(View.GONE);
        } else {
            h.viewAddress.setVisibility(View.VISIBLE);
        }

        h.tvAddress.setText(nanum.getAddressFake());
        h.tvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(contextHelper.getContext(), BaseActivity.class);
                i.putExtra("activityCode", BaseActivity.ActivityCode.MAP.ordinal());
                i.putExtra("nanum", nanum.toString());
                contextHelper.getContext().startActivity(i);
            }
        });

        h.tvDate.setText(MiscUtil.getPostTime(nanum.getTimeCreated()));

        for (int i = 0; i < 3; i++) {
            if (i < nanum.getPhotoSafe().size()) {
                h.ivThumb[i].setImageUrl(nanum.getPhotoSafe().get(i).getSmallUrl(), imageLoader);
            }
        }

        if (position == 0) {
            h.padding0.setVisibility(View.GONE);
            h.padding1.setVisibility(View.GONE);
            h.padding2.setVisibility(View.GONE);
        } else {
            h.padding0.setVisibility(View.VISIBLE);
            h.padding1.setVisibility(View.VISIBLE);
            h.padding2.setVisibility(View.VISIBLE);
        }

        h.setMark(nanum, parent.getContext());
        h.setTimer(nanum, parent.getContext());

        if (nanum.getStatus().equals(Nanum.STATUS_FINISH_TIME) || nanum.getStatus().equals(Nanum.STATUS_FINISH_SELECT)) {
            h.viewFinish.setVisibility(View.VISIBLE);
        } else {
            h.viewFinish.setVisibility(View.GONE);
        }

        if (nanum.isBookmark()) {
            h.btnBookmark.setIconResource(R.drawable.big_like_on);
        } else {
            h.btnBookmark.setIconResource(R.drawable.big_like_off);
        }

        h.btnBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!UserManager.getInstance(contextHelper).getMe().isLogin()) {
                    contextHelper.showLogin();
                    return;
                }

                if (NanumManager.getInstance(contextHelper).isBookmarking())
                    return;

                NanumManager.getInstance(contextHelper).setIsBookmarking(true);

                //h.btnBookmark.setProgressBar(true);
                if (!nanum.isBookmark()) {
                    h.btnBookmark.setIconResource(R.drawable.big_like_on);
                } else {
                    h.btnBookmark.setIconResource(R.drawable.big_like_off);
                }

                NanumManager.getInstance(contextHelper).bookmark(
                        nanum, !nanum.isBookmark(),
                        new Response.Listener<NanumData>() {
                            @Override
                            public void onResponse(NanumData response) {
                                NanumManager.getInstance(contextHelper).setIsBookmarking(false);

                                if (response.isSuccess()) {
                                    response.nanum.patch(contextHelper);
                                    EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_CHANGE_NANUM).setObject(response.nanum));
                                    //h.btnBookmark.setProgressBar(false);
                                    NanumAdapter.this.notifyDataSetChanged();
                                } else {
                                }
                            }
                        },
                        null
                );
            }
        });


        return convertView;
    }


}

