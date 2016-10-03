package com.yongtrim.lib.sns.googleplus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ShareCompat;
import android.util.Log;
import android.widget.Toast;



import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;

import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.photo.Photo;
import com.yongtrim.lib.model.user.SnsInfo;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.sns.SNSLoginoutListener;
import com.yongtrim.lib.util.FileUtil;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * Hair / com.yongtrim.lib.sns.googleplus
 * <p/>
 * Created by Uihyun on 15. 9. 14..
 */
public class GooglePlusManager implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, PlusClient.OnPeopleLoadedListener{
    private final String TAG = getClass().getSimpleName();
    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

    private static GooglePlusManager mInstance;

    private PlusClient mPlusClient;
    private ConnectionResult mConnectionResult;

    ContextHelper mHelper;
    private Context mContext;
    private Activity mActivity;

    SNSLoginoutListener mListener;

    protected GooglePlusPreference googlePlusPreference;

    public static GooglePlusManager getInstance(ContextHelper helper){
        if(mInstance == null) {
            mInstance = new GooglePlusManager();

        }

        mInstance.mHelper = helper;
        mInstance.mContext = helper.getContext();
        mInstance.mActivity = helper.getActivity();
        mInstance.googlePlusPreference = GooglePlusPreference.getInstance(helper.getContext());

        return mInstance;
    }

    public void login(SNSLoginoutListener listener) {
        mListener = listener;
        mPlusClient = new PlusClient.Builder(mContext, this, this)
                .setActions("http://schemas.google.com/AddActivity",
                        "http://schemas.google.com/BuyActivity")
                .build();
        mPlusClient.connect();
    }


    public void logout(SNSLoginoutListener listener) {
        disconnect();
        googlePlusPreference.putIsLogin(false);
        googlePlusPreference.putIsPostable(false);

        listener.success(false);

    }


    public void connect() {
        if(mPlusClient != null) {
            mPlusClient.connect();
            mConnectionResult = null;
        }
    }


    public void disconnect() {
        if(mPlusClient != null) {
            mPlusClient.disconnect();
        }
    }


    public boolean onActiveResult(int requestCode, int responseCode) {
        if (requestCode == REQUEST_CODE_RESOLVE_ERR && responseCode == Activity.RESULT_OK) {
            connect();
            return true;
        }
        return false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (result.hasResolution()) {
            try {
                result.startResolutionForResult(mActivity, REQUEST_CODE_RESOLVE_ERR);
            } catch (IntentSender.SendIntentException e) {
                mPlusClient.connect();
            }
        }
        // 결과를 저장하고 사용자가 클릭할 때 연결 실패를 해결합니다.
        mConnectionResult = result;
    }


    @Override
    public void onConnected(Bundle var1) {
        String accountName = mPlusClient.getAccountName();
        //mPlusClient.getCurrentPerson().getId();
        Toast.makeText(mContext, "구글 계정 " + accountName + "이 연결되었습니다.", Toast.LENGTH_LONG).show();

        mPlusClient.loadVisiblePeople(this, null);
    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "disconnected");
    }

    @Override
    public void onPeopleLoaded(ConnectionResult status,
                               PersonBuffer personBuffer, String nextPageToken) {
//        switch (status.getErrorCode()) {
//            case ConnectionResult.SUCCESS:
//                try {
//                    final NsUser user = UserManager.getInstance(mHelper).getMe();
//                    SnsInfo info = user.getGoogleInfo();
//
//                    // TODO
//                    int count = personBuffer.getCount();
//
//                    ArrayList<String> arrFrends = new ArrayList<>();
//
//                    for (int i = 0; i < count; i++) {
//                        Person person = personBuffer.get(i);
//                        arrFrends.add(person.getId());
//                    }
//                    info.setSnsFriends(arrFrends);
//                    info.setSnsId(mPlusClient.getCurrentPerson().getId());
//                    final CountDownLatch latchUpdate = new CountDownLatch(1);
//                    mHelper.updateUser(user, null, latchUpdate);
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                latchUpdate.await();
//
//                                mHelper.getActivity().runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//
//                                        UserManager.getInstance(mHelper).setMe(user);
//                                        googlePlusPreference.putIsLogin(true);
//                                        googlePlusPreference.putIsPostable(true);
//                                        mListener.success(true);
//                                    }
//                                });
//                            } catch (Exception e) {
//                            }
//                        }
//                    }).start();
//
//
//                } finally {
//                    personBuffer.close();
//                }
//
//                break;
//
//            case ConnectionResult.SIGN_IN_REQUIRED:
//                mPlusClient.disconnect();
//                mPlusClient.connect();
//
//                googlePlusPreference.putIsLogin(false);
//                googlePlusPreference.putIsPostable(false);
//                mListener.fail();
//
//                break;
//
//            default:
//                Log.e(TAG, "Error when listing people: " + status);
//
//                googlePlusPreference.putIsLogin(false);
//                googlePlusPreference.putIsPostable(false);
//                mListener.fail();
//                break;
//        }
    }


    public boolean post(Photo photo, String message) {

        File file = FileUtil.getInstance(mContext).saveBitmap(photo.getBitmap(), "photo0");
        try {
            final String photoUri = MediaStore.Images.Media.insertImage(
                    mActivity.getContentResolver(), file.getAbsolutePath(), null, null);

            Intent shareIntent = ShareCompat.IntentBuilder.from(mActivity)
                    .setText(message)
                    .setType("image/jpeg")
                    .setStream(Uri.parse(photoUri))
                    .getIntent()
                    .setPackage("com.google.android.apps.plus");
            mActivity.startActivity(shareIntent);

            file.delete();
        } catch(Exception e) {
        }

        return true;

    }
}
