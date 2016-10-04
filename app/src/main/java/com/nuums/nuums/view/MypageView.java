package com.nuums.nuums.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nuums.nuums.R;
import com.nuums.nuums.activity.BaseActivity;
import com.nuums.nuums.activity.MainActivity;
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.activity.ABaseFragmentAcitivty;
import com.yongtrim.lib.model.photo.Photo;
import com.yongtrim.lib.model.photo.PhotoManager;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.ui.CircularNetworkImageView;
import com.yongtrim.lib.ui.UltraButton;
import com.yongtrim.lib.ui.sweetalert.SweetAlertDialog;
import com.yongtrim.lib.util.PixelUtil;

import java.util.concurrent.CountDownLatch;

/**
 * nuums / com.nuums.nuums.view
 * <p/>
 * Created by Uihyun on 16. 1. 20..
 */
public class MypageView extends LinearLayout {

    final String TAG = "MypageView";

    View mainView;
    ContextHelper contextHelper;
    CircularNetworkImageView ivAvatar;
    UltraButton btnKeyword[];

    public MypageView(ContextHelper contextHelper) {
        super(contextHelper.getContext());

        this.contextHelper = contextHelper;

        init();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refresh();
            }
        }, 2000);

    }




    private void init() {

        LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainView = li.inflate(R.layout.view_mypage, this, true);
//        setupProfile();

        btnKeyword = new UltraButton[9];

        btnKeyword[0] = (UltraButton)mainView.findViewById(R.id.btnKeyword0);
        btnKeyword[1] = (UltraButton)mainView.findViewById(R.id.btnKeyword1);
        btnKeyword[2] = (UltraButton)mainView.findViewById(R.id.btnKeyword2);
        btnKeyword[3] = (UltraButton)mainView.findViewById(R.id.btnKeyword3);
        btnKeyword[4] = (UltraButton)mainView.findViewById(R.id.btnKeyword4);
        btnKeyword[5] = (UltraButton)mainView.findViewById(R.id.btnKeyword5);
        btnKeyword[6] = (UltraButton)mainView.findViewById(R.id.btnKeyword6);
        btnKeyword[7] = (UltraButton)mainView.findViewById(R.id.btnKeyword7);
        btnKeyword[8] = (UltraButton)mainView.findViewById(R.id.btnKeyword8);


        ivAvatar = (CircularNetworkImageView)mainView.findViewById(R.id.ivAvatar);
        ivAvatar.setStrokeColorAndWidth(ContextCompat.getColor(getContext(), R.color.white), 0);

        ivAvatar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(getContext(), SweetAlertDialog.SELECT_TYPE)
                        .setTitleText("사진 선택하기")
                        .setArrayValue(new String[]{"사진 선택하기", "삭제하기"})
                        .setSelectListener(new SweetAlertDialog.OnSweetSelectListener() {
                            @Override
                            public void onSelected(SweetAlertDialog sweetAlertDialog, int index) {

                                sweetAlertDialog.dismissWithAnimation();

                                final NsUser user = UserManager.getInstance(contextHelper).getMe();

                                switch (index) {
                                    case 0:
                                        contextHelper.getActivity().startActivityForResult(
                                                PhotoManager.getInstance(contextHelper).getPickImageChooserIntent(0),
                                                ABaseFragmentAcitivty.REQUEST_PICK_IMAGE);
                                        break;
                                    case 1:
                                        if(user.getPhoto() != null && user.getPhoto().hasPhoto()) {
                                            contextHelper.showProgress(null);
                                            user.setPhoto(null);
                                            patchUser(null);
                                        }
                                        break;
                                }
                            }
                        })
                        .show();
            }
        });

        mainView.findViewById(R.id.btnWhatis).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                String str0 = " 키워드를 등록하면 ";
                String str1 = "해당 키워드";
                String str2 = "로 나눔 글이 올라왔을 때 실시간으로 알림을 받을 수 있습니다.\n필요한 물건을 키워드로 등록해 보세요.\n\n예) ";
                String str3 = "의자";
                String str4 = "를 키워드로 등록 시 \'";
                String str5 = "의자";
                String str6 = "나눔\' 이라는 제목의 나눔 글이 올라왔을 때 알람이 울립니다.";


                SpannableString text = new SpannableString(str0 + str1 + str2 + str3 + str4 + str5 + str6);

                int offset = str0.length();
                text.setSpan(new ForegroundColorSpan(ContextCompat.getColor(contextHelper.getContext(), R.color.green)), offset, offset + str1.length(), 0);
                offset += str1.length();
                offset += str2.length();
                text.setSpan(new ForegroundColorSpan(ContextCompat.getColor(contextHelper.getContext(), R.color.green)), offset, offset + str3.length(), 0);

                offset += str3.length();
                offset += str4.length();
                text.setSpan(new ForegroundColorSpan(ContextCompat.getColor(contextHelper.getContext(), R.color.green)), offset, offset + str5.length(), 0);

                new SweetAlertDialog(getContext(), SweetAlertDialog.NORMAL2_TYPE)
                        .setContentText(text)
                        .showCancelButton(false)
                        .setConfirmText("확인")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(final SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        })
                        .show();

//                MainActivity activity = (MainActivity)contextHelper.getActivity();
//                activity.isKeywordEditMode = !activity.isKeywordEditMode;
//                activity.refreshMain();
            }
        });

        mainView.findViewById(R.id.btnApplyNanum).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), BaseActivity.class);
                i.putExtra("activityCode", BaseActivity.ActivityCode.NANUMASK.ordinal());
                getContext().startActivity(i);
            }
        });

    }

    public void refresh() {
        final NsUser user = UserManager.getInstance(contextHelper).getMe();

        TextView tvNickname = (TextView)mainView.findViewById(R.id.tvNickname);
        tvNickname.setText(user.getNicknameSafe());
        tvNickname.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(contextHelper.getContext(), SweetAlertDialog.EDITTEXT_TYPE)
                        .setTitleText("닉네임 변경하기")
                        .setEditTextValue(user.getNickname(), null, InputType.TYPE_TEXT_FLAG_MULTI_LINE, 8)
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();

                                if(TextUtils.isEmpty(sDialog.getEditTextValue()) || sDialog.getEditTextValue().length() < 2) {
                                    new SweetAlertDialog(contextHelper.getContext()).setContentText("2자이상 입력해주세요.").show();
                                } else {
                                    user.setNickname(sDialog.getEditTextValue());
                                    contextHelper.showProgress(null);
                                    patchUser(null);
                                }
                            }
                        })
                        .show();
            }
        });


        PhotoManager.getInstance(contextHelper).setPhotoMedium(ivAvatar, user.getPhoto());

        MainActivity activity = (MainActivity)contextHelper.getActivity();


        for(int i = 0;i < 9;i++) {
            if(user.getKeywords() != null && user.getKeywords().size() > i && !TextUtils.isEmpty(user.getKeywords().get(i))) {
                btnKeyword[i].setText(user.getKeywords().get(i));
                //if(activity.isKeywordEditMode) {
                btnKeyword[i].setIconResource(R.drawable.keyword_minus);
                btnKeyword[i].setTextSize(PixelUtil.dpToPx(getContext(), 15));
                btnKeyword[i].setTextColor(ContextCompat.getColor(getContext(), R.color.green));

                final int index = i;

                btnKeyword[index].setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new SweetAlertDialog(getContext())
                                .setContentText("관심 키워드를 삭제합니다.")
                                .showCancelButton(true)
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(final SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();
                                        user.getKeywords().set(index, "");
                                        contextHelper.showProgress(null);
                                        patchUser(null);
                                    }
                                })
                                .show();
                    }
                });

                btnKeyword[i].setEnabled(true, false);
                btnKeyword[i].setGravity(true);
//                } else {
//                    btnKeyword[i].setIconResource(0);
//                    btnKeyword[i].setOnClickListener(null);
//                    btnKeyword[i].setEnabled(false, false);
//                    btnKeyword[i].setGravity(true);
//
//                }
            } else {
                btnKeyword[i].setText("키워드 등록");
                btnKeyword[i].setTextSize(PixelUtil.dpToPx(getContext(), 11));
                btnKeyword[i].setTextColor(ContextCompat.getColor(getContext(), R.color.gray));

                //if(activity.isKeywordEditMode) {
                    //btnKeyword[i].setIconResource(R.drawable.keyword_plus);
                btnKeyword[i].setIconResource(0);
                final int index = i;

                btnKeyword[index].setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new SweetAlertDialog(getContext(), SweetAlertDialog.EDITTEXT_TYPE)
                                .setTitleText("관심 키워드")
                                .setEditTextValue("", null, InputType.TYPE_CLASS_TEXT, 5)
                                .showCancelButton(true)
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                        user.getKeywords().set(index, sDialog.getEditTextValue());
                                        contextHelper.showProgress(null);
                                        patchUser(null);
                                    }
                                })
                                .show();
                    }
                });
                btnKeyword[i].setEnabled(true, false);
                btnKeyword[i].setGravity(true);
//                } else {
//                    btnKeyword[i].setIconResource(0);
//                    btnKeyword[i].setOnClickListener(null);
//                    btnKeyword[i].setEnabled(false, false);
//                    btnKeyword[i].setGravity(true);
//                }
            }
        }

        UltraButton tvUnreadNanum = (UltraButton)mainView.findViewById(R.id.tvUnreadNanum);
        if(user.getUnreadAsk() > 0) {
            tvUnreadNanum.setVisibility(View.VISIBLE);
            tvUnreadNanum.setText("" + user.getUnreadAsk());
        } else {
            tvUnreadNanum.setVisibility(View.GONE);
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final NsUser user = UserManager.getInstance(contextHelper).getMe();

        if(requestCode == ABaseFragmentAcitivty.REQUEST_PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {

                Uri source = PhotoManager.getInstance(contextHelper).getPickImageResultUri(data);

                contextHelper.getActivity().startActivityForResult(
                        PhotoManager.getInstance(contextHelper).getCropImageIntent(source, true),
                        ABaseFragmentAcitivty.REQUEST_CROP_IMAGE);
            }
        } else if(requestCode == ABaseFragmentAcitivty.REQUEST_CROP_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {

                contextHelper.showProgress(null);
                contextHelper.setProgressMessage("사진을 업로드 중입니다.");

                Photo photo = new Photo();
                photo.setPath(PhotoManager.getInstance(contextHelper).getPathCropped());
                photo.setType(Photo.TYPE_AVATAR);

                PhotoManager.getInstance(contextHelper).uploadPhoto(photo,
                        new Response.Listener<Photo>() {
                            @Override
                            public void onResponse(Photo photo) {
                                user.setPhoto(photo);
                                PhotoManager.getInstance(contextHelper).clearCache();
                                patchUser(null);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(final VolleyError error) {
                                contextHelper.getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            contextHelper.hideProgress();
                                            PhotoManager.getInstance(contextHelper).clearCache();
                                            new SweetAlertDialog(contextHelper.getContext()).setContentText(error.getMessage()).show();
                                        } catch (Exception e) {

                                        }
                                    }
                                });
                            }
                        }
                );
            }
        }
    }


    void patchUser(final CountDownLatch latchWait) {
        final NsUser user = UserManager.getInstance(contextHelper).getMe();

        final MainActivity activity = (MainActivity)contextHelper.getActivity();

        final CountDownLatch latchUpdate = new CountDownLatch(1);
        contextHelper.updateUser(user, latchWait, latchUpdate);
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    if (latchUpdate != null)
                        latchUpdate.await();

                    NsUser user = UserManager.getInstance(contextHelper).getMe();

                    //EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_CHANGE_ME).setObject(user));
                    contextHelper.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                contextHelper.hideProgress();
                                activity.refreshMain();
                            } catch (Exception e) {

                            }
                        }
                    });

                } catch (Exception e) {

                }
            }
        }).start();
    }

}
