package com.nuums.nuums.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.nuums.nuums.R;
import com.nuums.nuums.activity.BaseActivity;
import com.nuums.nuums.fragment.misc.UserListFragment;
import com.nuums.nuums.model.chat.Talk;
import com.nuums.nuums.model.chat.TalkData;
import com.nuums.nuums.model.chat.TalkManager;
import com.nuums.nuums.model.chat.UserInfo;
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.model.photo.PhotoManager;
import com.yongtrim.lib.model.user.User;
import com.yongtrim.lib.model.user.UserData;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.ui.CircularNetworkImageView;
import com.yongtrim.lib.ui.UltraButton;
import com.yongtrim.lib.ui.sweetalert.SweetAlertDialog;
import com.yongtrim.lib.util.MiscUtil;

import de.greenrobot.event.EventBus;

/**
 * Created by YongTrim on 16. 6. 15. for nuums_ad
 */
public class UserListAdapter extends BaseAdapter {

    private ContextHelper contextHelper;

    private java.util.ArrayList<NsUser> users;

    int listType;


    class Holder {

        CircularNetworkImageView ivAvatar;
        TextView tvNickname;
        UltraButton btnAction;

        public View set(View v) {
            v.setTag(this);
            ivAvatar = (CircularNetworkImageView)v.findViewById(R.id.ivAvatar);

            tvNickname = (TextView) v.findViewById(R.id.tvNickname);
            btnAction = (UltraButton) v.findViewById(R.id.btnAction);
            return v;
        }
    }


    public UserListAdapter(ContextHelper contextHelper, int listType) {
        super();
        this.contextHelper = contextHelper;
        this.listType = listType;
    }

    public void setData(java.util.ArrayList<NsUser> users) {
        this.users = users;
    }


    @Override
    public int getCount() {
        if(users == null)
            return 0;
        return users.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final NsUser user = users.get(position);

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)parent.getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = new Holder().set(mInflater.inflate(R.layout.cell_user, parent, false));
        }

        final Holder h = (Holder) convertView.getTag();

        h.tvNickname.setText(user.getNicknameSafe());
        PhotoManager.getInstance(contextHelper).setPhotoSmall(h.ivAvatar, user.getPhoto());

        h.ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contextHelper.setUserAction2(user);
            }
        });


        if(listType == UserListFragment.LISTTYPE_SEARCH) {
            h.btnAction.setText("대화하기");
            h.btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(UserManager.getInstance(contextHelper).getMe().isBlock(user)) {

                        new SweetAlertDialog(contextHelper.getContext())
                                .setContentText("차단한 회원입니다.\n차단 해제하시겠습니까?")
                                .showCancelButton(true)
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(final SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();

                                        contextHelper.showProgress(null);

                                        UserManager.getInstance(contextHelper).block(
                                                user, false,
                                                new Response.Listener<UserData>() {
                                                    @Override
                                                    public void onResponse(UserData response) {
                                                        if (response.isSuccess()) {
                                                            UserManager.getInstance(contextHelper).setMe(response.user);
                                                            Toast.makeText(contextHelper.getContext(), "차단이 해제되었습니다.", Toast.LENGTH_SHORT).show();

                                                            Intent i = new Intent(contextHelper.getContext(), BaseActivity.class);
                                                            i.putExtra("activityCode", BaseActivity.ActivityCode.CHAT.ordinal());
                                                            i.putExtra("to", user.toString());
                                                            contextHelper.getContext().startActivity(i);

                                                        } else {
                                                        }

                                                        contextHelper.hideProgress();
                                                    }
                                                },
                                                null
                                        );

                                    }
                                })
                                .show();
                    } else {
                        Intent i = new Intent(contextHelper.getContext(), BaseActivity.class);
                        i.putExtra("activityCode", BaseActivity.ActivityCode.CHAT.ordinal());
                        i.putExtra("to", user.toString());
                        contextHelper.getContext().startActivity(i);
                    }
                }
            });
        } else {
            h.btnAction.setText("해제");
            h.btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contextHelper.showProgress(null);

                    UserManager.getInstance(contextHelper).block(
                            user, false,
                            new Response.Listener<UserData>() {
                                @Override
                                public void onResponse(UserData response) {
                                    if (response.isSuccess()) {
                                        UserManager.getInstance(contextHelper).setMe(response.user);
                                        Toast.makeText(contextHelper.getActivity(), "차단이 해제되었습니다.", Toast.LENGTH_SHORT).show();
                                        EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_CHANGE_ME).setObject(null));

                                    } else {
                                    }

                                    contextHelper.hideProgress();
                                }
                            },
                            null
                    );
                }
            });
        }
       return convertView;
    }

}



