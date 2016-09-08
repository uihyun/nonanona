package com.yongtrim.lib.model.photo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.Application;
import com.yongtrim.lib.Config;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.activity.ABaseFragmentAcitivty;
import com.yongtrim.lib.activity.imagepicker.ImagePickerActivity;
import com.yongtrim.lib.log.Logger;
import com.yongtrim.lib.model.GsonBodyRequest;
import com.yongtrim.lib.model.RequestManager;
import com.yongtrim.lib.model.user.User;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.ui.CustomNetworkImageView;
import com.yongtrim.lib.ui.sweetalert.SweetAlertDialog;
import com.yongtrim.lib.util.FileUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * hair / com.yongtrim.lib.model.photo
 * <p/>
 * Created by yongtrim.com on 15. 9. 4..
 */
public class PhotoManager {
    private final String fileNameTemp = Config.APPTAG + "img.jpg";

    private final String TAG = getClass().getSimpleName();

    private static PhotoManager instance;

    private ContextHelper contextHelper;

    public interface OnPhotoUploadedListener {
        public void onCompleted(List<Photo> photos);
    }


    public static PhotoManager getInstance(ContextHelper contextHelper) {
        if(instance == null) {
            instance = new PhotoManager();
        }
        instance.contextHelper = contextHelper;
        return instance;
    }


    public void uploadPhoto(final Photo photo,
                            final Response.Listener<Photo> listener,
                            final Response.ErrorListener errorListener
                            ) {

        final UserManager userManager = UserManager.getInstance(contextHelper);

        new AsyncTask<String, Void, String>() {
            protected String doInBackground(String... paths) {
                HashMap config = new HashMap();


                Log.i(TAG, ">>> path = " + paths[0]);

                config.put("cloud_name", Config.cloudinary_cloud_name);
                config.put("api_key", Config.cloudinary_api_key);
                config.put("api_secret", Config.cloudinary_api_secret);

                Cloudinary cloudinary = new Cloudinary(config);

                try {
                    org.cloudinary.json.JSONObject uploadResult= new org.cloudinary.json.JSONObject(cloudinary.uploader().upload(paths[0], ObjectUtils.asMap("folder", userManager.getMe().getId())));
                    photo.setPubliId(uploadResult.getString("public_id"));
                    photo.setUrl(uploadResult.getString("url"));
                    photo.setId(uploadResult.getString("public_id"));


                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            protected void onPostExecute(String error) {
                if (error == null) {
                    listener.onResponse(photo);
                } else {
                    errorListener.onErrorResponse(new VolleyError("사진 업로드를 실패했습니다."));
                }
            }
        }.execute(photo.getPath());
    }



    class PhotoInfo {
        Photo photo;
        int index;
        public PhotoInfo(Photo photo, int index) {
            this.photo = photo;
            this.index = index;
        }
    }

    public void uploadPhotos(final List<Photo> photos, final OnPhotoUploadedListener onPhotoUploadedListener) {
        ArrayList<PhotoInfo> arrShouldUpload = new ArrayList<>();

        for(int i = 0;i < photos.size();i++) {
            Photo photo = photos.get(i);
            if (photo != null && TextUtils.isEmpty(photo.getId()) && photo.hasPhoto()) {
                arrShouldUpload.add(new PhotoInfo(photo, i));
            }
        }
        //final SweetAlertDialog progress = new SweetAlertDialog(contextHelper.getContext(), SweetAlertDialog.PROGRESS_TYPE);

        if(arrShouldUpload.size() > 0) {
            //progress.setContentText("사진을 서버에 전송중입니다. (1/" + arrShouldUpload.size() + ")");
            Logger.debug(TAG, "사진을 서버에 전송중입니다. (1/" + arrShouldUpload.size() + ")");

            //progress.show();
            CountDownLatch beforelatchUpdatePhoto = null;
            for(int i = 0;i < arrShouldUpload.size();i++) {
                final PhotoInfo photoInfo = arrShouldUpload.get(i);

                CountDownLatch latchUpdatePhoto = new CountDownLatch(1);
                patchPhoto(photos, photoInfo, beforelatchUpdatePhoto, latchUpdatePhoto,
                        null, i, arrShouldUpload.size());
                beforelatchUpdatePhoto = latchUpdatePhoto;
            }

            final CountDownLatch _beforelatchUpdatePhoto = beforelatchUpdatePhoto;

            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        if (_beforelatchUpdatePhoto != null)
                            _beforelatchUpdatePhoto.await();

                        //progress.dismissWithAnimation();

                        onPhotoUploadedListener.onCompleted(photos);


                    } catch (Exception e) {
                        onPhotoUploadedListener.onCompleted(null);
                    }
                }
            }).start();

        } else {
            onPhotoUploadedListener.onCompleted(photos);

        }
    }


    void patchPhoto(final List<Photo> photos, final PhotoInfo photoInfo, final CountDownLatch latchWait, final CountDownLatch latchCount,
                    final SweetAlertDialog progress, final int index, final int total) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    if (latchWait != null)
                        latchWait.await();


                    contextHelper.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            contextHelper.setProgressMessage("사진을 등록 중입니다. (" + (index + 1) + "/" + total + ")");

                        }
                    });


                    PhotoManager.getInstance(contextHelper).uploadPhoto(photoInfo.photo,
                            new Response.Listener<Photo>() {
                                @Override
                                public void onResponse(Photo photo) {
                                    photos.set(photoInfo.index, photo);
                                    latchCount.countDown();
                                    //progress.setContentText("사진을 서버에 전송중입니다. (" + (index + 1) + "/" + total + ")");
                                    Logger.debug(TAG, "사진을 서버에 전송중입니다. (" + (index + 1) + "/" + total + ")");

                                    //final int __size = arrShouldUpload.size();


                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    latchCount.countDown();
                                }
                            }
                    );


                } catch (Exception e) {

                }
            }
        }).start();


    }



    public Intent getCropImageIntent(Uri source, boolean isSmall) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        List<ResolveInfo> list = contextHelper.getActivity().getPackageManager().queryIntentActivities(intent, 0);
        int size = list.size();
        if (size == 0) {
            Toast.makeText(contextHelper.getActivity(), "편집할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return null;
        } else {
            intent.setData(source);

            if(isSmall) {
                intent.putExtra("outputX", Config.SMALL_SIZE);
                intent.putExtra("outputY", Config.SMALL_SIZE);
            } else {
                intent.putExtra("outputX", Config.MEDIUM_SIZE);
                intent.putExtra("outputY", Config.MEDIUM_SIZE);
            }

            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);

            String path = genderatePath();
            setPathCropped(path);

            File f = new File(path);
            Uri uri = Uri.fromFile(f);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

            Intent i = new Intent(intent);
            ResolveInfo res = list.get(0);
            i.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            return i;
        }
    }

//    public Intent getTakePictureIntent() {
//
//        Uri outputFileUri =  getCaptureImageOutputUri();
//
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
//
//        if (outputFileUri != null) {
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
//        }
//
//        return intent;
//    }

    /**
     * Create a chooser intent to select the  source to get image from.<br/>
     * The source can be camera's  (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br/>
     * All possible sources are added to the  intent chooser.
     */
    public Intent getPickImageChooserIntent(int maxphotocnt) {

        // Determine Uri of camera image to  save.
        Uri outputFileUri =  getCaptureImageOutputUri();

        ArrayList<Intent> allIntents = new  ArrayList<>();
        PackageManager packageManager = contextHelper.getContext().getPackageManager();


        Intent intentMulti = new Intent(Intent.ACTION_GET_CONTENT);
        intentMulti.setClass(contextHelper.getContext(), ImagePickerActivity.class);
        intentMulti.setType("image/*");
        intentMulti.putExtra("maxphotocnt", maxphotocnt);
        intentMulti.addCategory(Intent.CATEGORY_OPENABLE);


        Intent intentCamera = null;
        Intent intentGallery = null;

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new  Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            if(res.activityInfo.packageName.equals("com.sec.android.app.camera")) {
                intentCamera = intent;
            } else {
                allIntents.add(intent);

            }
            //Logger.debug(TAG, "res.activityInfo.packageName = " + res.activityInfo.packageName);

        }

        // collect all gallery intents
        Intent galleryIntent = new  Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery =  packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new  Intent(galleryIntent);
            intent.setComponent(new  ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            if(res.activityInfo.packageName.equals("com.sec.android.gallery3d")) {
                intentGallery = intent;
            } else {
                allIntents.add(intent);
            }
            Logger.debug(TAG, "res.activityInfo.packageName = " + res.activityInfo.packageName);
        }

        // the main intent is the last in the  list (fucking android) so pickup the useless one
        Intent mainIntent =  allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if  (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity"))  {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        if(intentGallery != null) {
            allIntents.add(0, intentGallery);
        }


        if(maxphotocnt > 1)
            allIntents.add(0, intentMulti);

        if(intentCamera != null) {
            allIntents.add(0, intentCamera);
        }


        // Create a chooser from the main  intent
        Intent chooserIntent =  Intent.createChooser(mainIntent, "선택하세요.");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    /**
     * Get URI to image received from capture  by camera.
     */
    private Uri getCaptureImageOutputUri() {
//        Uri outputFileUri = null;
//        File getImage = contextHelper.getContext().getExternalCacheDir();
//        if (getImage != null) {
//            outputFileUri = Uri.fromFile(new File(getImage.getPath(), fileNameTemp));
//        }
//        return outputFileUri;
        return null;
    }


    public Uri getPickImageResultUri(Intent  data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null  && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    public void setPhotoLarge(CustomNetworkImageView imageView, Photo photo) {
        if(photo == null) {
            imageView.setImageUrl("", contextHelper.getImageLoader());
            imageView.setImageDrawable(null);


        } else {
            if(photo.getBitmap() != null)
                imageView.setLocalImageBitmap(photo.getBitmap());
            else
                imageView.setImageUrl(photo.getUrl(), contextHelper.getImageLoader());
        }
    }

    public void setPhotoMedium(CustomNetworkImageView imageView, Photo photo) {
        if(photo == null) {
            imageView.setImageUrl("", contextHelper.getImageLoader());
            imageView.setImageDrawable(null);


        } else {
            if(photo.getBitmap() != null)
                imageView.setLocalImageBitmap(photo.getBitmap());
            else
                imageView.setImageUrl(photo.getMediumUrl(), contextHelper.getImageLoader());
        }
    }

    public void setPhotoSmall(CustomNetworkImageView imageView, Photo photo) {
        if(photo == null) {
            imageView.setImageUrl("", contextHelper.getImageLoader());
            imageView.setImageDrawable(null);

        } else {
            if(photo.getBitmap() != null)
                imageView.setLocalImageBitmap(photo.getBitmap());
            else {
                imageView.setImageUrl(photo.getSmallUrl(), contextHelper.getImageLoader());
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA};
        Cursor cursor = contextHelper.getActivity().managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }



    public int getExifRotation(Uri uri) {
        try {
            ExifInterface exif = new ExifInterface(getPath(uri));
            String rotationAmount = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            if (!TextUtils.isEmpty(rotationAmount)) {
                int rotationParam = Integer.parseInt(rotationAmount);
                switch (rotationParam) {
                    case ExifInterface.ORIENTATION_NORMAL:
                        return 0;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        return 90;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        return 180;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        return 270;
                    default:
                        return 0;
                }
            }
            else {
                return 0;
            }
        }
        catch (Exception ex)
        {
            return 0;
        }
    }

    int seedOfPath = 0;
    public String genderatePath() {
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        File dir = new File(Environment.getExternalStorageDirectory() + "/NONANONA/");
        if(!dir.exists()) {
            dir.mkdirs();
        }

        String path = Environment.getExternalStorageDirectory() + "/NONANONA/photo"+ts + "_" + (seedOfPath++);
        return path;
    }

    String pathCropped;
    public void setPathCropped(String path) {
        pathCropped = path;
    }

    public String getPathCropped() {
        return pathCropped;
    }

    public void clearCache() {
        try {
            File cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "/NONANONA/");
            String[] children = cacheDir.list();
            for (int i = 0; i < children.length; i++) {
                File file = new File(cacheDir, children[i]);
                file.delete();

            }
        } catch (Exception e) {
            Log.i(TAG, "error " + e.toString());
        }
    }

//    public Intent getMultipleImage(ContextHelper contextHelper, ArrayList<Photo> photos) {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        // who know how to resolve that?
//        // If I remove this line, the Google+ Photos will be opened prior.
//        // It works well after I uninstalled Google+, but disable Google+ doesn't work.
//        // So it seems like a trick made by Google+ teams.
//        intent.setClass(contextHelper.getContext(), ImagePickerActivity.class);
//        intent.setType("image/*");
//        intent.putParcelableArrayListExtra("photos", photos);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//
//        //--startActivityForResult(intent, REQUEST_CODE_TAKEN_PHOTO_GALLERY);
//
//        return intent;
//    }
}
