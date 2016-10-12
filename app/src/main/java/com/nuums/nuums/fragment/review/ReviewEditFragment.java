package com.nuums.nuums.fragment.review;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.kakao.auth.APIErrorResult;
import com.kakao.kakaostory.KakaoStoryHttpResponseHandler;
import com.kakao.kakaostory.KakaoStoryService;
import com.kakao.kakaostory.MyStoryInfo;
import com.kakao.kakaostory.PhotoKakaoStoryPostParamBuilder;
import com.kakao.util.KakaoParameterException;
import com.nuums.nuums.R;
import com.nuums.nuums.adapter.UserAdapter;
import com.nuums.nuums.model.review.Review;
import com.nuums.nuums.model.review.ReviewData;
import com.nuums.nuums.model.review.ReviewManager;
import com.nuums.nuums.model.user.NsUser;
import com.nuums.nuums.model.user.NsUserSearch;
import com.nuums.nuums.view.KakaoButton;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Privacy;
import com.sromku.simple.fb.listeners.OnPublishListener;
import com.yongtrim.lib.activity.ABaseFragmentAcitivty;
import com.yongtrim.lib.fragment.ABaseFragment;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.model.config.ConfigManager;
import com.yongtrim.lib.model.photo.Photo;
import com.yongtrim.lib.model.photo.PhotoManager;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.sns.SNSLoginListener;
import com.yongtrim.lib.sns.SNSLoginoutListener;
import com.yongtrim.lib.sns.SNSPostListner;
import com.yongtrim.lib.sns.facebook.FacebookManager;
import com.yongtrim.lib.sns.facebook.FacebookPreference;
import com.yongtrim.lib.sns.kakao.KakaoManager;
import com.yongtrim.lib.sns.kakao.KakaoPreference;
import com.yongtrim.lib.sns.twitter.TwitterManager;
import com.yongtrim.lib.sns.twitter.TwitterPreference;
import com.yongtrim.lib.ui.ChipsMultiAutoCompleteTextview;
import com.yongtrim.lib.ui.CustomNetworkImageView;
import com.yongtrim.lib.ui.UltraButton;
import com.yongtrim.lib.ui.UltraEditText;
import com.yongtrim.lib.ui.sweetalert.SweetAlertDialog;
import com.yongtrim.lib.util.FileUtil;
import com.yongtrim.lib.util.UIUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import de.greenrobot.event.EventBus;

/**
 * nuums / com.nuums.nuums.fragment.review
 * <p/>
 * Created by Uihyun on 16. 1. 18..
 */
public class ReviewEditFragment extends ABaseFragment implements UltraEditText.OnChangeListener {
    private final String TAG = getClass().getSimpleName();

    View viewMain;

    CustomNetworkImageView ivPhoto;
    ImageView ivAdd;
    Button btnDelete;
    Button btnModify;

    UltraEditText etContent;
    ChipsMultiAutoCompleteTextview etTags;

    Review review;

    boolean isModify;

    private SimpleFacebook mSimpleFacebook;

    boolean isPostFacebook = false;
    boolean isPostTwitter = false;
    boolean isPostKakao = false;
    boolean isPostInsta = false;

    public TwitterManager twitterManager;
    TwitterPreference twitterPreference;

    public FacebookManager facebookManager;
    FacebookPreference facebookPreference;

    public KakaoManager kakaoManager;
    KakaoPreference kakaoPreference;


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("review", review.toString());
    }


    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        isModify = contextHelper.getActivity().getIntent().getBooleanExtra("isModify", false);

        if(saveInstanceState != null) {
            review = Review.getReview(saveInstanceState.getString("review"));
        }

        if(isModify) {
            contextHelper.getActivity().setupActionBar("후기 수정하기");
            if(review == null)
                review = Review.getReview(contextHelper.getActivity().getIntent().getStringExtra("review"));
        } else {
            contextHelper.getActivity().setupActionBar("후기 등록하기");

            //review = ConfigManager.getInstance(contextHelper).getPreference().getReviewParam();
            if(review == null) {
                review = new Review();
            }

            if(contextHelper.getActivity().getIntent().hasExtra("tag")) {
                ArrayList<NsUser> tags = new ArrayList<>();
                tags.add(NsUser.getUser(contextHelper.getActivity().getIntent().getStringExtra("tag")));
                review.setTags(tags);
            }
        }

        mSimpleFacebook = SimpleFacebook.getInstance(contextHelper.getActivity());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        viewMain = inflater.inflate(R.layout.fragment_reviewedit, container, false);
        contextHelper.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        View viewTop = viewMain.findViewById(R.id.viewTop);
        UIUtil.setRatio(viewTop, getContext(), 320, 300);

        ivPhoto = (CustomNetworkImageView)viewMain.findViewById(R.id.ivPhoto);
        ivAdd = (ImageView)viewMain.findViewById(R.id.ivAdd);

        etContent = (UltraEditText)viewMain.findViewById(R.id.etContent);
        etContent.setText(review.getContent());
        etContent.setChangeListener(this);

        etTags = (ChipsMultiAutoCompleteTextview) viewMain.findViewById(R.id.etTags);
        etTags.setContextHelper(contextHelper);
        NsUserSearch[] users = new NsUserSearch[0];
        UserAdapter myAdapter = new UserAdapter(contextHelper.getContext(), R.layout.cell_searchuser, users);
        myAdapter.setContextHelper(contextHelper);
        etTags.setAdapter(myAdapter);
        etTags.setTokenizer(new SpaceTokenizer());
        etTags.setThreshold(1);
        etTags.setTags(review.getTags());

        btnDelete = (Button)viewMain.findViewById(R.id.btnDelete);
        btnDelete.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(contextHelper.getContext(), R.drawable.tresh_round), null, null);
        btnModify = (Button)viewMain.findViewById(R.id.btnModify);
        btnModify.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(contextHelper.getContext(), R.drawable.trim_round), null, null);

        twitterManager = TwitterManager.getInstance(contextHelper);
        twitterPreference = TwitterPreference.getInstance(contextHelper.getContext());
        facebookManager = FacebookManager.getInstance(contextHelper);
        facebookPreference = FacebookPreference.getInstance(contextHelper.getContext());

        kakaoPreference = KakaoPreference.getInstance(contextHelper.getContext());

//        isPostTwitter = twitterPreference.isPostable();
//        isPostFacebook = facebookPreference.isPostable();
//        isPostKakao = kakaoPreference.isPostable();
//        isPostInsta = ConfigManager.getInstance(contextHelper).getPreference().getIsShareInstagram();

        isPostTwitter = false;
        isPostFacebook = false;
        isPostKakao = false;
        isPostInsta = false;


        if(isModify) {
            UltraButton btnSubmit = (UltraButton)viewMain.findViewById(R.id.btnSubmit);
            btnSubmit.setText("수정하기");
            isPostFacebook = false;
            isPostTwitter = false;
            isPostKakao = false;
            isPostInsta = false;
            viewMain.findViewById(R.id.viewShare).setVisibility(View.GONE);
        }

        kakaoManager = KakaoManager.getInstance(contextHelper, new SNSLoginListener() {
            public void success(boolean isLogin, String snsId) {
                isPostKakao = kakaoPreference.isPostable();
                refresh();
            }

            public void fail() {

            }

            public void successAndNeedRegist() {
            }
        }, true);

        return viewMain;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSimpleFacebook = SimpleFacebook.getInstance(contextHelper.getActivity());

        Photo photo = review.getPhoto();
        if(photo != null) {
            if(photo.isLocalBitmap()) {

                if(photo.isCropped()) {
                    photo.setPath(PhotoManager.getInstance(contextHelper).getPathCropped());
                } else {
                    photo.setPath(null);
                }

                photo.makeBitmap(contextHelper, true);
            }
        }

        refresh();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    void refresh() {
        if(review.getPhoto() == null || !review.getPhoto().hasPhoto()) {
            ivAdd.setVisibility(View.VISIBLE);
            btnModify.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);

        } else {
            ivAdd.setVisibility(View.GONE);
            btnModify.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
        }
        contextHelper.setPhoto(ivPhoto, review.getPhoto());

        final ImageButton btnTweet = (ImageButton)viewMain.findViewById(R.id.btnTweet);

        if (isPostTwitter)
            btnTweet.setImageResource(R.drawable.twitter);
        else
            btnTweet.setImageResource(R.drawable.twitter_off);

        final ImageButton btnFacebook = (ImageButton)viewMain.findViewById(R.id.btnFacebook);
        if (isPostFacebook)
            btnFacebook.setImageResource(R.drawable.facebook);
        else
            btnFacebook.setImageResource(R.drawable.facebook_off);


        final KakaoButton btnKas = (KakaoButton)viewMain.findViewById(R.id.btnKas);
        final ImageButton btnKasOn = (ImageButton)viewMain.findViewById(R.id.btnKasOn);

        btnKas.setVisibility(View.GONE);
        btnKasOn.setVisibility(View.GONE);

        if (!kakaoPreference.isLogin())
            btnKas.setVisibility(View.VISIBLE);
        else {
            btnKasOn.setVisibility(View.VISIBLE);
            if (isPostKakao)
                btnKasOn.setImageResource(R.drawable.kakaostory);
            else
                btnKasOn.setImageResource(R.drawable.kakaostory_off);
        }

        final ImageButton btnInsta = (ImageButton)viewMain.findViewById(R.id.btnInsta);
        if (isPostInsta)
            btnInsta.setImageResource(R.drawable.instagram);
        else
            btnInsta.setImageResource(R.drawable.instagram_off);

        //ConfigManager.getInstance(contextHelper).getPreference().putReviewParam(review);
    }


    public void onButtonClicked(View v) {

        switch(v.getId()) {
            case R.id.btnKasOn: {
                isPostKakao = !isPostKakao;
                kakaoPreference.putIsPostable(isPostKakao);
                refresh();
            }
            break;
            case R.id.btnInsta: {
                isPostInsta = !isPostInsta;
                ConfigManager.getInstance(contextHelper).getPreference().putIsShareInstagram(isPostInsta);
                refresh();
            }
            break;
            case R.id.btnFacebook: {
                if(isPostFacebook == false && !facebookPreference.isLogin()) {
                    facebookManager.login(new SNSLoginListener() {
                        @Override
                        public void success(boolean isLogin, String snsId) {
                            isPostFacebook = facebookPreference.isPostable();
                            refresh();
                        }

                        @Override
                        public void fail() {
                        }

                        public void successAndNeedRegist() {

                        }
                    }, false);
                } else {
                    isPostFacebook = !isPostFacebook;
                    facebookPreference.putIsPostable(isPostFacebook);
                    refresh();
                }
            }
            break;

            case R.id.btnTweet: {
                if(isPostTwitter == false && !twitterPreference.isLogin()) {
                    twitterManager.login(new SNSLoginoutListener() {
                        @Override
                        public void success(boolean isLogin) {

                            contextHelper.getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    isPostTwitter = twitterPreference.isPostable();
                                    refresh();
                                }
                            });

                        }
                        @Override
                        public void fail() {
                        }
                    });
                } else {
                    isPostTwitter = !isPostTwitter;
                    twitterPreference.putIsPostable(isPostTwitter);
                    refresh();
                }
            }
            break;
            case R.id.btnSubmit: {
                varify();
            }
            break;
            case R.id.ivPhoto: {
                contextHelper.getActivity().startActivityForResult(
                        PhotoManager.getInstance(contextHelper).getPickImageChooserIntent(1),
                        ABaseFragmentAcitivty.REQUEST_PICK_IMAGE);
            }
                break;
            case R.id.btnDelete: {
                if(review.getPhoto() == null || !review.getPhoto().hasPhoto()) {
                    return;
                }

                new SweetAlertDialog(getContext())
                    .setContentText("등록한 사진을 삭제합니다.")
                    .showCancelButton(true)
                    .setConfirmText("예")
                    .setCancelText("아니오")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(final SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                            review.getPhoto().clear();
                            refresh();
                        }
                    })
                    .show();
            }
            break;
            case R.id.btnModify: {
                if(review.getPhoto() == null || !review.getPhoto().hasPhoto()) {
                    return;
                }

                Uri source = Uri.parse(review.getPhoto().getUriOrg());

                contextHelper.getActivity().startActivityForResult(
                        PhotoManager.getInstance(contextHelper).getCropImageIntent(source, false),
                        ABaseFragmentAcitivty.REQUEST_CROP_IMAGE);
            }
            break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(kakaoManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }

        switch (resultCode) {
            case Activity.RESULT_OK:
                if(requestCode == ABaseFragmentAcitivty.REQUEST_CROP_IMAGE) {
                    review.getPhoto().setIsCropped(true);

                }  else if(requestCode == ABaseFragmentAcitivty.REQUEST_PICK_IMAGE) {
                    Uri uri = PhotoManager.getInstance(contextHelper).getPickImageResultUri(data);
                    int orientation = PhotoManager.getInstance(contextHelper).getExifRotation(uri);

                    Photo photo = new Photo(uri.toString(), orientation);
                    photo.setType(Photo.TYPE_PHOTO);
                    photo.setIsCropped(false);
                    review.setPhoto(photo);
                }
                break;
            default:
                break;
        }

        mSimpleFacebook.onActivityResult(requestCode, resultCode, data);

    }



    public void onEvent(PushMessage pushMessage) {
    }


    public void onEditTextChanged(UltraEditText editText) {
        review.setContent(editText.getText().toString());
        //ConfigManager.getInstance(contextHelper).getPreference().putReviewParam(review);
    }

    void varify() {
        if(review.getPhoto() == null || !review.getPhoto().hasPhoto()) {
            new SweetAlertDialog(getContext()).setContentText("사진을 등록해야 합니다.").show();
            return;
        }
        if(TextUtils.isEmpty(review.getContent())) {
            new SweetAlertDialog(getContext()).setContentText("나눔받은 후기를 작성해주세요.").show();
            return;
        }
        if(isModify) {
            submit();
            submit();
        } else {
            new SweetAlertDialog(getContext())
                    .setContentText("등록하시겠습니까?")
                    .showCancelButton(true)
                    .setConfirmText("예")
                    .setCancelText("아니오")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(final SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                            submit();
                        }
                    })
                    .show();
        }
    }


    void submit() {
        contextHelper.showProgress("");
        final CountDownLatch latchCreate = new CountDownLatch(1);

        review.setTagids(etTags.getTags());

        //ConfigManager.getInstance(contextHelper).getPreference().putReviewParam(null);

        if(isModify) {
            latchCreate.countDown();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ReviewManager.getInstance(contextHelper).create(
                                new Response.Listener<ReviewData>() {
                                    @Override
                                    public void onResponse(ReviewData response) {
                                        if (response.isSuccess()) {
                                            review.setId(response.review.getId());
                                            review.setOwner(response.review.getOwner());
                                            if(response.user != null)
                                                UserManager.getInstance(contextHelper).setMe(response.user);


                                            latchCreate.countDown();

                                        } else {
                                            new SweetAlertDialog(getContext())
                                                    .setContentText(response.getErrorMessage())
                                                    .show();
                                            contextHelper.hideProgress();
                                        }
                                    }
                                },
                                null
                        );

                    } catch (Exception e) {

                    }
                }
            }).start();
        }


        final CountDownLatch latchKas = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (latchCreate != null)
                        latchCreate.await();
                    if(isPostKakao) {
                        Handler mHandler = new Handler(Looper.getMainLooper());
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 내용
                                updateKas(latchKas);
                            }
                        }, 0);
                    } else {
                        latchKas.countDown();
                    }
                } catch (Exception e) {
                }
            }
        }).start();


        final CountDownLatch latchFacebook = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (latchKas != null)
                        latchKas.await();
                    if(isPostFacebook) {
                        Handler mHandler = new Handler(Looper.getMainLooper());
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 내용
                                updateFacebook(latchFacebook);
                            }
                        }, 0);
                    } else {
                        latchFacebook.countDown();
                    }
                } catch (Exception e) {
                }
            }
        }).start();


        final CountDownLatch latchTwitter = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (latchFacebook != null)
                        latchFacebook.await();
                    if(isPostTwitter) {
                        Handler mHandler = new Handler(Looper.getMainLooper());
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 내용
                                updateTwitter(latchTwitter);
                            }
                        }, 0);
                    } else {
                        latchTwitter.countDown();
                    }
                } catch (Exception e) {
                }
            }
        }).start();


        final CountDownLatch latchInsta = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (latchTwitter != null)
                        latchTwitter.await();

                    if(isPostInsta) {
                        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                        shareIntent.setType("image/*");
                        shareIntent.putExtra(Intent.EXTRA_STREAM, getImageUri(contextHelper.getContext(), review.getPhoto().getBitmap()));
                        shareIntent.putExtra(Intent.EXTRA_TEXT, review.getContent());
                        shareIntent.setPackage("com.instagram.android");
                        contextHelper.getActivity().startActivity(shareIntent);
                    }
                    latchInsta.countDown();
                } catch (Exception e) {
                }
            }
        }).start();

        final CountDownLatch latchPhoto = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    latchInsta.await();
                    PhotoManager.getInstance(contextHelper).uploadPhoto(review.getPhoto(),
                            new Response.Listener<Photo>() {
                                @Override
                                public void onResponse(Photo photo) {
                                    review.setPhoto(photo);
                                    latchPhoto.countDown();

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
                                                new SweetAlertDialog(contextHelper.getContext()).setContentText(error.getMessage()).show();
                                            } catch (Exception e) {

                                            }
                                        }
                                    });
                                }
                            }
                    );

                } catch (Exception e) {
                    latchPhoto.countDown();
                }
            }
        }).start();


        final CountDownLatch latchUpdate = new CountDownLatch(1);

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    latchPhoto.await();
                    contextHelper.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            contextHelper.setProgressMessage("처리중...");

                        }
                    });

                    ReviewManager.getInstance(contextHelper).update(
                            review,
                            new Response.Listener<ReviewData>() {
                                @Override
                                public void onResponse(ReviewData response) {
                                    if (response.isSuccess()) {
                                        review = response.review;
                                        review.patch(contextHelper);

                                        latchUpdate.countDown();
                                    } else {
                                        new SweetAlertDialog(getContext())
                                                .setContentText(response.getErrorMessage())
                                                .show();
                                    }
                                }
                            },
                            null
                    );

                } catch (Exception e) {

                }
            }
        }).start();


        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    if (latchUpdate != null)
                        latchUpdate.await();

                    contextHelper.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                contextHelper.hideProgress();

                                PhotoManager.getInstance(contextHelper).clearCache();


                                if(isModify) {
                                    EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_CHANGE_REVIEW).setObject(review));
                                    contextHelper.getActivity().finish();
                                }
                                else {
                                    EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_ADDED_REVIEW).setObject(review));
                                    contextHelper.getActivity().finish();

                                    Toast.makeText(contextHelper.getContext(), "등록하였습니다.", Toast.LENGTH_LONG).show();
                                }

                            } catch (Exception e) {

                            }
                        }
                    });

                } catch (Exception e) {

                }
            }
        }).start();
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(
                inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }




    void updateFacebook(final CountDownLatch latch) {

        Privacy privacy = new Privacy.Builder()
                .setPrivacySettings(Privacy.PrivacySettings.ALL_FRIENDS)
                .build();

        StringBuilder messageData = new StringBuilder(review.getContent()).append('\n');
                //.append("NANOJO에서 올림");

        com.sromku.simple.fb.entities.Photo photoFB = new com.sromku.simple.fb.entities.Photo.Builder()
                .setImage(review.getPhoto().getBitmap())
                .setName(messageData.toString())
                //.setPlace("110619208966868")
                .setPrivacy(privacy)
                .build();

        SimpleFacebook.getInstance().publish(photoFB, false, new OnPublishListener() {
            @Override
            public void onException(Throwable throwable) {
                contextHelper.getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        latch.countDown();
                    }
                });
            }

            @Override
            public void onFail(String reason) {
                contextHelper.getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        latch.countDown();
                    }
                });
            }

            @Override
            public void onThinking() {
            }

            @Override
            public void onComplete(String response) {
                contextHelper.getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        latch.countDown();
                    }
                });
            }
        });
    }


    void updateTwitter(final CountDownLatch latch) {
        TwitterManager.getInstance(contextHelper).post(review.getPhoto(), review.getContent(), new SNSPostListner() {
            @Override
            public void success() {
                contextHelper.getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        latch.countDown();
                    }
                });
            }

            @Override
            public void fail() {
                contextHelper.getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        latch.countDown();
                    }
                });
            }
        });

    }

    private static class StoryResponseHandler<T> extends KakaoStoryHttpResponseHandler<T> {
        @Override
        public void onHttpSuccess(T resultObj) {
            //redirectLoginActivity(self);
        }

        @Override
        public void onHttpSessionClosedFailure(final APIErrorResult errorResult) {
            //redirectLoginActivity(self);
        }

        @Override
        public void onNotKakaoStoryUser() {
            //KakaoToast.makeToast(self, "not KakaoStory user", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailure(final APIErrorResult errorResult) {
//            final String message = "MyKakaoStoryHttpResponseHandler : failure : " + errorResult;
//            Logger.w(message);
//            KakaoToast.makeToast(self, message, Toast.LENGTH_LONG).show();
        }
    }



    private void updateKas(final CountDownLatch latchKas) {
        try {
            final List<File> files = new ArrayList<File>();

            File file = FileUtil.getInstance(contextHelper.getContext()).saveBitmap(review.getPhoto().getBitmap(), "photo");
            files.add(file);

            KakaoStoryService.requestMultiUpload(new StoryResponseHandler<String[]>() {
                @Override
                public void onHttpSuccess(final String[] imageURLs) {
                    try {
                        if (imageURLs != null && imageURLs.length != 0) {
                            final PhotoKakaoStoryPostParamBuilder postParamBuilder = new PhotoKakaoStoryPostParamBuilder(imageURLs);
                            postParamBuilder.setContent(review.getContent());

                            String execParam = contextHelper.getContext().getString(R.string.kakao_scheme) + "://kakaostory/review_id=" + review.getId();
                            String marketParam = "market://details?id=com.nuums.nuums&referrer=kakaostory";//ConfigManager.getInstance(contextHelper).getConfigHello().getParams().getAdUrl();//"market://details?id=com.kakao.sample.kakaostory&referrer=kakaostory";

                            // 앱이 설치되어 있는 경우 kakao<app_key>://kakaostory?place=1111 로 이동.
                            // 앱이 설치되어 있지 않은 경우 market://details?id=com.kakao.sample.kakaostory&referrer=kakaostory로 이동
                            postParamBuilder.setAndroidExecuteParam(execParam).setIOSExecuteParam(execParam).setAndroidMarketParam(marketParam).setIOSMarketParam(marketParam);
                            try {
                                final Bundle parameters = postParamBuilder.build();
                                KakaoStoryService.requestPost(KakaoStoryService.StoryType.PHOTO, new StoryResponseHandler<MyStoryInfo>() {
                                    @Override
                                    public void onHttpSuccess(final MyStoryInfo myStoryInfo) {
                                        latchKas.countDown();
                                    }
                                }, parameters);
                            } catch (KakaoParameterException e) {
                                //Logger.e(e);
                            }

                        } else {
                            //KakaoToast.makeToast(getApplicationContext(), "failed to upload image.\nkakaoStoryUpload=null", Toast.LENGTH_SHORT).show();
                        }
                    } finally {
                        deleteTempFiles();
                    }

                }

                @Override
                public void onHttpSessionClosedFailure(final APIErrorResult errorResult) {
                    try {
                        //Logger.w("onHttpSessionClosedFailure : %s", errorResult);
                        super.onHttpSessionClosedFailure(errorResult);
                    } finally {
                        deleteTempFiles();
                        latchKas.countDown();
                    }
                }

                @Override
                public void onNotKakaoStoryUser() {
                    try {
                        super.onNotKakaoStoryUser();
                    } finally {
                        deleteTempFiles();
                        latchKas.countDown();
                    }
                }

                @Override
                public void onFailure(final APIErrorResult errorResult) {
                    try {
                        //Logger.w("onFailure : %s", errorResult);
                        super.onFailure(errorResult);
                    } finally {
                        deleteTempFiles();
                        latchKas.countDown();
                    }
                }

                private void deleteTempFiles() {
                    for (File file : files) {
                        file.delete();
                    }
                }

            }, files);
        } catch (Exception e) {
            Toast.makeText(ReviewEditFragment.this.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }


}

