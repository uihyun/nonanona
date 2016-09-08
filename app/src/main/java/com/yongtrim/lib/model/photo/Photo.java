package com.yongtrim.lib.model.photo;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.nuums.nuums.AppController;
import com.yongtrim.lib.Config;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.Model;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * hair / com.yongtrim.lib.model.photo
 * <p/>
 * Created by yongtrim.com on 15. 9. 4..
 */
public class Photo extends Model implements Parcelable {

    public final static String TYPE_AVATAR = "AVATAR";
    public final static String TYPE_PHOTO = "PHOTO";

//    // parcel keys
    private static final String KEY_URI = "uriOrg";
    private static final String KEY_ORIENTATION = "orientation";

    private String uriOrg;

    private String url;
    private String url_s;
    private String url_m;
    private String type;
    int orientation;

    boolean isCropped;

    public void setIsCropped(boolean isCropped) {
        this.isCropped = isCropped;
    }

    public boolean isCropped() {
        return isCropped;
    }


    String public_id;


    public String getType() {
        return this.type;
    }
    public void setType(String type) {
        this.type = type;
    }

    private int index;
    private transient Bitmap bitmap;
    private String path;

    public Photo(String uriOrg, int orientation) {
        super();
        this.uriOrg = uriOrg;
        this.orientation = orientation;
    }

    public Photo(String uriOrg) {
        super();
        this.uriOrg = uriOrg;
    }

    public Photo() {
        super();
    }


    public String getUriOrg() {
        return uriOrg;
    }

    public int getOrientation() {
        return orientation;
    }

    public boolean isLocalBitmap() {
        return TextUtils.isEmpty(getId()) && !TextUtils.isEmpty(uriOrg);
    }


    public String getUrl() {
        if(public_id != null) {
            return url;
        }

        return Config.imageHosting + url;
    }


    public String getSmallUrl() {
        if(public_id != null) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("http://res.cloudinary.com/");
            stringBuffer.append(Config.cloudinary_cloud_name);
            stringBuffer.append("/image/upload/w_256,h_256,c_fill/");
            stringBuffer.append(public_id);
            stringBuffer.append(".jpg");

            return stringBuffer.toString();
        }

        if(!TextUtils.isEmpty(url_s))
            return Config.imageHosting + url_s;
        return getUrl();
    }

    public String getMediumUrl() {
        if(public_id != null) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("http://res.cloudinary.com/");
            stringBuffer.append(Config.cloudinary_cloud_name);
            stringBuffer.append("/image/upload/w_720,h_720,c_fill/");
            stringBuffer.append(public_id);
            stringBuffer.append(".jpg");
            return stringBuffer.toString();
        }


        if(!TextUtils.isEmpty(url_m))
            return Config.imageHosting + url_m;
        return getSmallUrl();
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }
    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public boolean hasPhoto() {
        boolean hasPhoto = false;
        if(!TextUtils.isEmpty(url))
            hasPhoto = true;

        if(bitmap != null)
            return true;

        return hasPhoto;
    }

    public void clear() {
        uriOrg = null;
        url = null;
        url_m = null;
        url_s = null;
        bitmap = null;
        setId(null);
    }



    public void setUrl(String url) {
        this.url = url;
    }

    public String getPublicId() {
        return public_id;
    }

    public void setPubliId(String publicId) {
        this.public_id = publicId;
    }


    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();

        if(uriOrg != null) {
            bundle.putString(KEY_URI, uriOrg.toString());
        }
        bundle.putInt(KEY_ORIENTATION, orientation);
        dest.writeBundle(bundle);
    }


    public static final Parcelable.Creator<Photo> CREATOR = new Creator<Photo>() {

        @Override
        public Photo createFromParcel(Parcel source) {
            Bundle bundle = source.readBundle();

            Photo photo = new Photo(bundle.getString(KEY_URI), bundle.getInt(KEY_ORIENTATION));
            return photo;
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };


    public void makeBitmap(ContextHelper contextHelper) {
        setId(null);

        if(path == null) {
            boolean isFile = false;

            if(getUriOrg().startsWith("file://")) {
                isFile = true;
            }

            Bitmap bitmap = null;

            if (isFile) {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(contextHelper.getContext().getContentResolver(), Uri.parse(getUriOrg()));
                } catch(Exception e) {

                }

            } else {
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor cursor = contextHelper.getContext().getContentResolver().query(Uri.parse(getUriOrg()), filePath, null, null, null);
                cursor.moveToFirst();
                String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                bitmap = BitmapFactory.decodeFile(imagePath, options);
            }
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, 720, 720);

            if(orientation == 90 || orientation == 270){
                Matrix matrix = new Matrix();
                matrix.postRotate(orientation);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(),
                        matrix, true);
            }

            setBitmap(bitmap);

            String path = PhotoManager.getInstance(contextHelper).genderatePath();

            try {
                FileOutputStream out = new FileOutputStream(path);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.close();

            } catch (Exception e) {

            }

            setPath(path);

        } else {
            File file = new File(path);
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

            setBitmap(bitmap);
        }
    }

    public static Photo getPhoto(String jsonString) {
        Gson gson = AppController.getInstance().getGson();
        Photo photo = gson.fromJson(jsonString, Photo.class);
        return photo;
    }
}


