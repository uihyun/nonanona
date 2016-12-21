package com.yongtrim.lib;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.kakao.kakaolink.AppActionBuilder;
import com.kakao.kakaolink.AppActionInfoBuilder;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.nuums.nuums.AppController;
import com.nuums.nuums.R;
import com.nuums.nuums.activity.Base2Activity;
import com.nuums.nuums.activity.BaseActivity;
import com.nuums.nuums.model.nanum.Nanum;
import com.nuums.nuums.model.report.Report;
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.activity.ABaseFragmentAcitivty;
import com.yongtrim.lib.log.Logger;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.model.photo.Photo;
import com.yongtrim.lib.model.user.LoginManager;
import com.yongtrim.lib.model.user.User;
import com.yongtrim.lib.model.user.UserData;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.ui.CustomNetworkImageView;
import com.yongtrim.lib.ui.sweetalert.SweetAlertDialog;
import com.yongtrim.lib.util.BasePreferenceUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;

/**
 * hair / com.yongtrim.lib
 * <p/>
 * Created by Uihyun on 15. 9. 4..
 */
public class ContextHelper {

    final static String TAG = "ContextHelper";

    private long lastClickTime = 0;

    private Context context;
    private ABaseFragmentAcitivty activity;
    private FragmentManager fragmentManager;
    private FragmentManager childManager;
    private ImageLoader imageLoader;
    private SweetAlertDialog progress;

    private ContextPreference preference;

    public ContextHelper(ABaseFragmentAcitivty activity) {
        this.context = activity;
        this.activity = activity;
        fragmentManager = activity.getSupportFragmentManager();
        imageLoader = AppController.getInstance().getImageLoader();

        preference = new ContextPreference(context);
    }


    public ContextHelper(Context context) {
        this.context = context;
    }


    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public Context getContext() {
        return context;
    }

    public ABaseFragmentAcitivty getActivity() {
        return activity;
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public boolean isDisabledClick() {
        if (SystemClock.elapsedRealtime() - lastClickTime < 500) {
            return true;
        }
        lastClickTime = SystemClock.elapsedRealtime();
        return false;
    }


    public void login(final String loginType,
                      final String username,
                      final String password,
                      final boolean isSignup,
                      final CountDownLatch latchWait, final CountDownLatch latchCount) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    if (latchWait != null)
                        latchWait.await();

                    Logger.debug(TAG, "login() called");
                    LoginManager.getInstance(ContextHelper.this).login(
                            loginType,
                            username,
                            password,
                            new Response.Listener<UserData>() {
                                @Override
                                public void onResponse(UserData response) {
                                    if (response.isSuccess()) {
                                        Logger.debug(TAG, "login() | user = " + response.user.toString());

                                        LoginManager.getInstance(ContextHelper.this).setLoginStatus(response.user);
                                        UserManager.getInstance(ContextHelper.this).setMe(response.user);

                                        if (!isSignup) {
                                            EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_CHANGE_ME).setObject(response.user));
                                            EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_CHANGE_LOGIN).setObject(response.user));
                                        }

                                        if (latchCount != null) {
                                            latchCount.countDown();
                                        } else {
                                            hideProgress();
                                        }

                                    } else {
                                        hideProgress();
                                        new SweetAlertDialog(getContext())
                                                .setContentText(response.getErrorMessage())
                                                .show();
                                    }
                                }
                            },
                            null
                    );

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void updateUser(final NsUser user, final CountDownLatch latchWait, final CountDownLatch latchCount) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    if (latchWait != null)
                        latchWait.await();

                    Logger.debug(TAG, "userUpdate() called");
                    UserManager.getInstance(ContextHelper.this).update(
                            user,
                            new Response.Listener<UserData>() {
                                @Override
                                public void onResponse(UserData response) {
                                    if (response.isSuccess()) {
                                        Logger.debug(TAG, "userUpdate() | user = " + response.user.toString());
                                        UserManager.getInstance(ContextHelper.this).setMe(response.user);
                                        latchCount.countDown();
                                    } else {
                                        hideProgress();
                                        new SweetAlertDialog(getContext())
                                                .setContentText(response.getErrorMessage())
                                                .show();
                                    }
                                }
                            },
                            null
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void createGuest(final CountDownLatch latchCount) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Logger.debug(TAG, "createGuest() called");

                    UserManager.getInstance(ContextHelper.this).create(
                            User.LOGINTYPE_GUEST,
                            null,
                            null,
                            null,
                            null,
                            null,
                            new Response.Listener<UserData>() {
                                @Override
                                public void onResponse(UserData response) {
                                    if (response.isSuccess()) {
                                        Logger.debug(TAG, "userInfo() | user = " + response.user.toString());
                                        login(User.LOGINTYPE_GUEST, response.user.getUsername(), null, false, null, latchCount);

                                    } else if (latchCount != null)
                                        latchCount.countDown();
                                }
                            },
                            null
                    );

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void userInfo(final CountDownLatch latchWait, final CountDownLatch latchCount) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    if (latchWait != null)
                        latchWait.await();

                    if (LoginManager.getInstance(ContextHelper.this).getLoginStatus().equals(LoginManager.LOGINSTATUS_NONE)) {
                        createGuest(latchCount);
                    } else {
                        Logger.debug(TAG, "userInfo() called " + LoginManager.getInstance(ContextHelper.this).getLoginStatus());
                        UserManager.getInstance(ContextHelper.this).read(
                                null,
                                new Response.Listener<UserData>() {
                                    @Override
                                    public void onResponse(UserData response) {
                                        if (response.isSuccess()) {
                                            UserManager.getInstance(ContextHelper.this).setMe(response.user);

                                            Logger.debug(TAG, "userInfo() | user = " + response.user.toString());
                                        }

                                        if (latchCount != null)
                                            latchCount.countDown();

                                    }
                                },
                                null
                        );
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void showProgress(String message) {
        if (progress != null) {
            progress.dismissWithAnimation();
        }
        progress = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        //progress.setContentText(message == null ? "로딩중..." : message);
        try {
            progress.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void hideProgress() {
        if (progress != null && progress.isShowing()) {
            progress.dismissWithAnimation();
        }
    }


    public void setProgressMessage(String message) {
        if (progress != null) {
            progress.setContentText(message);
        }
    }

//    public void setProfile(final NsUser user, CircularNetworkImageView ivAvatar) {
//
//        ivAvatar.setVisibility(View.VISIBLE);
//
//        ivAvatar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (user != null && !user.isMe(ContextHelper.this)) {
//                    Intent i = new Intent(getContext(), BaseActivity.class);
//                    i.putExtra("activityCode", BaseActivity.ActivityCode.PROFILE_VIEWER.ordinal());
//                    i.putExtra("user", user.toString());
//                    getContext().startActivity(i);
//                }
//            }
//        });
//
//        ivAvatar.setDefaultImageResId(R.drawable.image_prifile);
//        PhotoManager.getInstance(ContextHelper.this).setPhotoSmall(ivAvatar, user.getPhoto());
//    }


    public SpannableString getMention(String message) {
        SpannableString hashtagintitle = new SpannableString(message);
        //Matcher matcher = Pattern.compile("#([A-Za-z0-9_-]+)").matcher(hashtagintitle);
        Matcher matcher = Pattern.compile("@(\\w+|\\W+)").matcher(hashtagintitle);

        while (matcher.find()) {
            hashtagintitle.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.red)), matcher.start(), matcher.end(), 0);
        }

        return hashtagintitle;
    }

    public void showLogin() {
        new SweetAlertDialog(getContext())
                .setContentText("로그인하셔야 사용하실 수 있습니다.\n로그인이나 회원가입하시겠습니까?")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(final SweetAlertDialog sweetAlertDialog) {

                        sweetAlertDialog.dismissWithAnimation();

                        Intent i = new Intent(getContext(), Base2Activity.class);
                        i.putExtra("activityCode", Base2Activity.ActivityCode.SIGNIN.ordinal());
                        getContext().startActivity(i);
                    }
                })
                .show();
    }


//    public void showCertify() {
//        new SweetAlertDialog(getContext())
//                .setContentText("핸드폰 미인증 상태입니다. 인증하셔야 공개로 전환하실 수 있습니다. \n인증하시겠습니까?")
//                .showCancelButton(true)
//                .setCancelText("안함")
//                .setConfirmText("인증하기")
//                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                    @Override
//                    public void onClick(final SweetAlertDialog sweetAlertDialog) {
//
//                        sweetAlertDialog.dismissWithAnimation();
//
//                        Intent i = new Intent(getContext(), BaseActivity.class);
//                        i.putExtra("activityCode", BaseActivity.ActivityCode.CERTITY.ordinal());
//                        i.putExtra("isSinging", false);
//                        getContext().startActivity(i);
//                    }
//                })
//                .show();
//    }


    public void report(final String targetType, final String targetId) {
        final NsUser user = UserManager.getInstance(ContextHelper.this).getMe();

        if (!UserManager.getInstance(ContextHelper.this).getMe().isLogin()) {
            showLogin();
            return;
        }

//        new SweetAlertDialog(ContextHelper.this.getContext(), SweetAlertDialog.EDITTEXT_TYPE)
//                .setTitleText("신고하기")
//                .setTip("신고내용을 입력해주세요.")
//                .setEditTextValue("", null, InputType.TYPE_TEXT_FLAG_MULTI_LINE, 0)
//                .showCancelButton(true)
//                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                    @Override
//                    public void onClick(SweetAlertDialog sDialog) {
//                        sDialog.dismissWithAnimation();
//
//                        Report report = new Report();
//                        report.setReportType("REPORT");
//                        report.setTargetId(targetId);
//                        report.setTargetType(targetType);
//
//                        report.setEmail(user.getEmail());
//                        report.setName(user.getNicknameSafe());
//                        report.setPhoneNumber(user.getPhoneNumber());
//
//                        report.setMessage(sDialog.getEditTextValue());
//
//                        ContextHelper.this.showProgress(null);
//                        ReportManager.getInstance(ContextHelper.this).create(report,
//                                new Response.Listener<ReportData>() {
//                                    @Override
//                                    public void onResponse(ReportData response) {
//                                        ContextHelper.this.hideProgress();
//                                        if (response.isSuccess()) {
//                                            Toast.makeText(ContextHelper.this.getContext(), "접수하였습니다", Toast.LENGTH_SHORT).show();
//
//                                        } else {
//                                            new SweetAlertDialog(ContextHelper.this.getContext())
//                                                    .setContentText(response.getErrorMessage())
//                                                    .show();
//                                        }
//                                    }
//                                },
//                                null
//                        );
//                    }
//                })
//                .show();
    }


    public void saveImage(Photo photo) {

        showProgress(null);
        final CountDownLatch latchDown = new CountDownLatch(1);

        new DownloadImageTask(latchDown).execute(photo.getUrl());

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    latchDown.await();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ContextHelper.this.getContext(), "저장하였습니다", Toast.LENGTH_SHORT).show();
                            hideProgress();

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String savePhoto(Bitmap bmp) {
        //File imageFileFolder = new File(Environment.getExternalStorageDirectory(),"MyFolder"); //when you need to save the image inside your own folder in the SD Card
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
        ); //this is the default location inside SD Card - Pictures folder
        //imageFileFolder.mkdir(); //when you create your own folder, you use this line.
        FileOutputStream out = null;
        Calendar c = Calendar.getInstance();
        String date = fromInt(c.get(Calendar.MONTH))
                + fromInt(c.get(Calendar.DAY_OF_MONTH))
                + fromInt(c.get(Calendar.YEAR))
                + fromInt(c.get(Calendar.HOUR_OF_DAY))
                + fromInt(c.get(Calendar.MINUTE))
                + fromInt(c.get(Calendar.SECOND));
        File imageFileName = new File(path, date.toString() + ".jpg"); //imageFileFolder
        try {
            out = new FileOutputStream(imageFileName);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            scanPhoto(imageFileName.toString());
            out = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageFileName.toString();
    }

    private String fromInt(int val) {
        return String.valueOf(val);
    }

    /* invoke the system's media scanner to add your photo to the Media Provider's database,
    * making it available in the Android Gallery application and to other apps. */
    private void scanPhoto(String imageFileName) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imageFileName);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        //this.cordova.getContext().sendBroadcast(mediaScanIntent); //this is deprecated
        getActivity().sendBroadcast(mediaScanIntent);
    }

    public void restart() {
        Intent i = getActivity().getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getActivity().startActivity(i);
        EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_CHANGE_LOGOUT).setObject(null));

    }

    public void setUserAction(final NsUser user) {
        if (user.isMe(ContextHelper.this))
            return;

        new SweetAlertDialog(getContext(), SweetAlertDialog.SELECT_TYPE)
                .setTitleText(user.getNicknameSafe())
                .setArrayValue(new String[]{"연락하기", "후기쓰기", "신고하기"})
                .showCloseButton(true)
                .setSelectListener(new SweetAlertDialog.OnSweetSelectListener() {
                    @Override
                    public void onSelected(SweetAlertDialog sweetAlertDialog, int index) {
                        sweetAlertDialog.dismissWithAnimation();
                        switch (index) {
                            case 0: {
                                if (UserManager.getInstance(ContextHelper.this).getMe().isBlock(user)) {

                                    new SweetAlertDialog(getContext())
                                            .setContentText("차단한 회원입니다.\n차단 해제하시겠습니까?")
                                            .showCancelButton(true)
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(final SweetAlertDialog sweetAlertDialog) {
                                                    sweetAlertDialog.dismissWithAnimation();

                                                    ContextHelper.this.showProgress(null);

                                                    UserManager.getInstance(ContextHelper.this).block(
                                                            user, false,
                                                            new Response.Listener<UserData>() {
                                                                @Override
                                                                public void onResponse(UserData response) {
                                                                    if (response.isSuccess()) {
                                                                        UserManager.getInstance(ContextHelper.this).setMe(response.user);
                                                                        Toast.makeText(ContextHelper.this.getContext(), "차단이 해제되었습니다.", Toast.LENGTH_SHORT).show();

                                                                        Intent i = new Intent(getContext(), BaseActivity.class);
                                                                        i.putExtra("activityCode", BaseActivity.ActivityCode.CHAT.ordinal());
                                                                        i.putExtra("to", user.toString());
                                                                        getContext().startActivity(i);

                                                                    } else {
                                                                    }

                                                                    ContextHelper.this.hideProgress();
                                                                }
                                                            },
                                                            null
                                                    );

                                                }
                                            })
                                            .show();
                                } else {
                                    Intent i = new Intent(getContext(), BaseActivity.class);
                                    i.putExtra("activityCode", BaseActivity.ActivityCode.CHAT.ordinal());
                                    i.putExtra("to", user.toString());
                                    getContext().startActivity(i);
                                }

                            }
                            break;
                            case 1: {
                                Intent i = new Intent(getContext(), BaseActivity.class);
                                i.putExtra("activityCode", BaseActivity.ActivityCode.REVIEWEDIT.ordinal());
                                i.putExtra("tag", user.toString());

                                getContext().startActivity(i);
                            }
                            break;
                            case 2: {
                                Intent i = new Intent(getContext(), BaseActivity.class);
                                i.putExtra("activityCode", BaseActivity.ActivityCode.REPORT.ordinal());
                                i.putExtra("type", Report.REPORTTYPE_REPORT);
                                i.putExtra("target", user.toString());

                                getContext().startActivity(i);
                            }

                            break;
                        }
                    }
                })
                .show();
    }

    public void setUserAction2(final NsUser user) {
        if (user.isMe(ContextHelper.this))
            return;

        new SweetAlertDialog(getContext(), SweetAlertDialog.SELECT_TYPE)
                .setTitleText(user.getNicknameSafe())
                .setArrayValue(new String[]{"후기쓰기", "신고하기"})
                .showCloseButton(true)
                .setSelectListener(new SweetAlertDialog.OnSweetSelectListener() {
                    @Override
                    public void onSelected(SweetAlertDialog sweetAlertDialog, int index) {
                        sweetAlertDialog.dismissWithAnimation();
                        switch (index) {

                            case 0: {
                                Intent i = new Intent(getContext(), BaseActivity.class);
                                i.putExtra("activityCode", BaseActivity.ActivityCode.REVIEWEDIT.ordinal());
                                i.putExtra("tag", user.toString());

                                getContext().startActivity(i);
                            }
                            break;
                            case 1: {
                                Intent i = new Intent(getContext(), BaseActivity.class);
                                i.putExtra("activityCode", BaseActivity.ActivityCode.REPORT.ordinal());
                                i.putExtra("type", Report.REPORTTYPE_REPORT);
                                i.putExtra("target", user.toString());

                                getContext().startActivity(i);
                            }

                            break;
                        }
                    }
                })
                .show();
    }

    public void shareViaKakao(Nanum nanum) {
        try {
            final KakaoLink kakaoLink = KakaoLink.getKakaoLink(getContext());
            final KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();

            String message = "[NONANONA]\n";

            message += (nanum.getTitle() + "\n");

            if (nanum.getAddressFake() != null) {
                message += (nanum.getAddressFake() + "\n");
            }
            message += (nanum.getDescription());

            kakaoTalkLinkMessageBuilder.addText(message);

            Logger.debug(TAG, "url = " + nanum.getPhotoSafe().get(0).getMediumUrl());
            kakaoTalkLinkMessageBuilder.addImage(nanum.getPhotoSafe().get(0).getMediumUrl(), 300, 300);
            kakaoTalkLinkMessageBuilder.addAppButton("NONANONA로 이동",
                    new AppActionBuilder()
                            .addActionInfo(AppActionInfoBuilder
                                    .createAndroidActionInfoBuilder()
                                    .setExecuteParam("nanum_id=" + nanum.getId())
                                    .setMarketParam("referrer=kakaotalklink")
                                    .build())
                            .addActionInfo(AppActionInfoBuilder
                                    .createiOSActionInfoBuilder()
                                    .setExecuteParam("nanum_id=" + nanum.getId())
                                    .build())
                            .build());

            kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder.build(), getContext());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPhoto(CustomNetworkImageView ivPhoto, Photo photo) {
        if (photo == null || !photo.hasPhoto()) {
            ivPhoto.setImageUrl("", imageLoader);
            ivPhoto.setLocalImageBitmap(null);
        } else {

            if (TextUtils.isEmpty(photo.getId())) {
                if (photo.getBitmap() != null) {
                    ivPhoto.setLocalImageBitmap(photo.getBitmap());
                }
            } else {
                if (!TextUtils.isEmpty(photo.getMediumUrl()))
                    ivPhoto.setImageUrl(photo.getMediumUrl(), imageLoader);
                else
                    ivPhoto.setImageUrl("", imageLoader);
            }
        }

    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();

    }

    private class ContextPreference extends BasePreferenceUtil {
//        private static final String PROPERTY_SHOPMAIN_ADDRESS = "shop_main_address";

        public ContextPreference(Context context) {
            super(context);
        }

//        public void putShopMainAddress(HcAddress address) {
//            put(PROPERTY_SHOPMAIN_ADDRESS, address.toString());
//        }
//
//        public HcAddress getShopMainAddress() {
//            return HcAddress.getAddress(get(PROPERTY_SHOPMAIN_ADDRESS));
//        }

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        CountDownLatch latchDown;

        public DownloadImageTask(CountDownLatch latchDown) {
            this.latchDown = latchDown;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(final Bitmap result) {
            savePhoto(result);
            latchDown.countDown();
        }
    }


}
