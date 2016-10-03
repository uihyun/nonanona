package com.yongtrim.lib.sns.twitter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.CookieManager;
import android.widget.Toast;

import com.nuums.nuums.R;
import com.nuums.nuums.activity.BaseActivity;
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.model.photo.Photo;
import com.yongtrim.lib.model.user.SnsInfo;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.sns.SNSLoginoutListener;
import com.yongtrim.lib.sns.SNSPostListner;
import com.yongtrim.lib.sns.googleplus.GooglePlusPreference;
import com.yongtrim.lib.util.FileUtil;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import de.greenrobot.event.EventBus;
import twitter4j.IDs;
import twitter4j.StatusUpdate;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 * Hair / com.yongtrim.lib.sns.twitter
 * <p/>
 * Created by Uihyun on 15. 9. 14..
 */
public class TwitterManager {
    final String TAG = "TwittSharing";
    private static TwitterManager mInstance;

    private TwitterHandler mTwitter;
    ContextHelper mHelper;

    private String mMsssage;
    private Photo mPhoto;

    SNSLoginoutListener mLoginoutListner;
    SNSPostListner mPostListner;

    protected TwitterPreference twitterPreference;

    public static TwitterManager getInstance(ContextHelper helper) {
        if (mInstance == null) {
            mInstance = new TwitterManager();

        }
        mInstance.mHelper = helper;

        mInstance.mTwitter = new TwitterHandler(helper.getActivity(),
                helper.getContext().getString(R.string.twitter_key),
                helper.getContext().getString(R.string.twitter_secret));
        mInstance.twitterPreference = TwitterPreference.getInstance(helper.getContext());

        return mInstance;
    }

    public void login(SNSLoginoutListener listener) {
        mLoginoutListner = listener;

        mTwitter.setListener(new TwitterHandler.TwDialogListener() {
            @Override
            public void onError(String value) {
                mHelper.getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mTwitter.resetAccessToken();
                        twitterPreference.putIsLogin(false);
                        twitterPreference.putIsPostable(false);
                        mLoginoutListner.success(false);
                    }
                });
            }

            @Override
            public void onComplete(String value) {
                //new GetFriendTask().execute(mMsssage);
                processEnd();
            }
        });

        if (mTwitter.hasAccessToken()) {
            // this will post data in asyn background thread
            //new GetFriendTask().execute(mMsssage);
            processEnd();
        } else {
            mTwitter.authorize();
        }
    }


    public void logout(SNSLoginoutListener listener) {
        mTwitter.resetAccessToken();

        twitterPreference.putIsLogin(false);
        twitterPreference.putIsPostable(false);
        listener.success(false);
    }


    public boolean post(Photo photo, String message, SNSPostListner listener) {

        mPostListner = listener;

        boolean isPost = TwitterPreference.getInstance(mHelper.getContext()).isPostable();

        if(!isPost) {
            mPostListner.fail();
            return false;
        }
        this.mPhoto = photo;
        this.mMsssage = message;
        mTwitter.setListener(new TwitterHandler.TwDialogListener() {
            @Override
            public void onError(String value) {
                mHelper.getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //fail
                        mTwitter.resetAccessToken();

                        mPostListner.fail();
                    }
                });
            }

            @Override
            public void onComplete(String value) {
                new PostTwittTask().execute(mMsssage);
            }
        });

        if (mTwitter.hasAccessToken()) {
            // this will post data in asyn background thread
            new PostTwittTask().execute(mMsssage);
        } else {
            mTwitter.authorize();
        }
        return true;
    }

    class PostTwittTask extends AsyncTask<String, Void, String> {
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(mHelper.getActivity());
            pDialog.setMessage("Posting Twitt...");
            pDialog.setCancelable(false);
            pDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... twitt) {
            try {
                String Status = "";
                try {

                    File file = FileUtil.getInstance(mHelper.getContext()).saveBitmap(mPhoto.getBitmap(), "photo0");

                    StatusUpdate st = new StatusUpdate(mMsssage);
					st.setMedia(file);
					mTwitter.twitterObj.updateStatus(st);
					mHelper.getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            mPostListner.success();
                        }
					});
					Status = "success";
                } catch (TwitterException e) {
                    Status = "fail";
                    Log.v("log_tag", "Pic Upload error" + e);
                    mHelper.getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            mPostListner.fail();
                        }
                    });

                }
                return Status;

            } catch (Exception e) {
                if (e.getMessage().toString().contains("duplicate")) {
                    return "Posting Failed because of Duplicate message...";
                }
                e.printStackTrace();
                return "Posting Failed!!!";
            }

        }

        @Override
        protected void onPostExecute(final String result) {

            super.onPostExecute(result);
            pDialog.dismiss();
        }
    }


    void processEnd() {
        twitterPreference.putIsLogin(true);
        twitterPreference.putIsPostable(true);
        mLoginoutListner.success(true);
    }
//    class GetFriendTask extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected String doInBackground(String... twitt) {
//            try {
//                NsUser user = UserManager.getInstance(mHelper).getMe();
//                SnsInfo info = user.getTwitterInfo();
//
//                String Status = "";
//                try {
//
//                    long cursor = -1;
//                    IDs ids;
//
//                    ArrayList<String> arrFrends = new ArrayList<>();
//
//                    do {
//                        ids = mTwitter.twitterObj.getFollowersIDs(mTwitter.twitterObj.getId(), cursor);
//
//                        for (long id : ids.getIDs()) {
//                            arrFrends.add("" + id);
//                        }
//                    } while ((cursor = ids.getNextCursor()) != 0);
//
//                    info.setSnsFriends(arrFrends);
//                    info.setSnsId("" + mTwitter.twitterObj.getId());
//                    Status = "success";
//
//                } catch (TwitterException e) {
//                    Status = "fail";
//                    Log.v("log_tag", "Pic Upload error" + e);
//                    mHelper.getActivity().runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            twitterPreference.putIsLogin(false);
//                            twitterPreference.putIsPostable(false);
//                            mLoginoutListner.success(false);
//                        }
//                    });
//
//                }
//                return Status;
//
//            } catch (Exception e) {
//                if (e.getMessage().toString().contains("duplicate")) {
//                    return "Posting Failed because of Duplicate message...";
//                }
//                e.printStackTrace();
//                return "Posting Failed!!!";
//            }
//
//        }
//
//        @Override
//        protected void onPostExecute(final String result) {
//
//            super.onPostExecute(result);
//            //pDialog.dismiss();
//            if (null != result && result.equals("success")) {
//
//                final NsUser user = UserManager.getInstance(mHelper).getMe();
//                final CountDownLatch latchUpdate = new CountDownLatch(1);
//                mHelper.updateUser(user, null, latchUpdate);
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            latchUpdate.await();
//
//                            mHelper.getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//
//                                    UserManager.getInstance(mHelper).setMe(user);
//                                    twitterPreference.putIsLogin(true);
//                                    twitterPreference.putIsPostable(true);
//                                    mLoginoutListner.success(true);
//                                }
//                            });
//                        } catch (Exception e) {
//                        }
//                    }
//                }).start();
//            }
//        }
//    }
}
