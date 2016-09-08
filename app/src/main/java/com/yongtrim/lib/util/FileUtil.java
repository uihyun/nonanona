package com.yongtrim.lib.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.yongtrim.lib.Config;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * hair / com.yongtrim.lib.util
 * <p/>
 * Created by yongtrim.com on 15. 9. 2..
 */
public class FileUtil {

    private final String TAG = getClass().getSimpleName();
    private static FileUtil instance;

    private File cacheDir;

    public static FileUtil getInstance(Context context) {
        if (instance == null) {
            instance = new FileUtil();

            if (android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                instance.cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), Config.APPTAG);
            } else {
                instance.cacheDir = context.getCacheDir();
            }

            if (!instance.cacheDir.exists()) {
                instance.cacheDir.mkdirs();
            }
        }

        return instance;
    }


    public File getFile(String filename) {
        return new File(cacheDir, filename);
    }


    public File saveBitmap(Bitmap bitmap, String filename) {

        File file = getFile(filename);

        try {
            //Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            //bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.close();

        } catch (Exception e) {
            return null;
        }
        return file;
    }

}

