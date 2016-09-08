package com.yongtrim.lib.activity.imagepicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nuums.nuums.R;
import com.yongtrim.lib.Config;
import com.yongtrim.lib.activity.ABaseFragmentAcitivty;
import com.yongtrim.lib.model.photo.Photo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by lao on 15/1/5.
 */
public class ImagePickerActivity extends ABaseFragmentAcitivty implements AdapterView.OnItemClickListener {

    static final String TAG = "PhotosPickerActivity";

    private static final int REQUEST_CODE_TAKEN_PHOTO_CAMERA = 0x01;

    Adapter adapter;

    DisplayImageOptions options;

    ArrayList<Photo> selected = new ArrayList<>();
    GridView gridView;

    int maxPhotoCnt;

    @Override
    protected void onCreate(Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);

        setContentView(R.layout.activity_imagepicker);

        contextHelper.getActivity().setupActionBar("사진 선택");

        contextHelper.getActivity().setTextButtonAndVisiable("선택");
        contextHelper.getActivity().setTextButtonEnable(false);

        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(100, true, false, false))
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();


        maxPhotoCnt = getIntent().getIntExtra("maxphotocnt", Config.MAX_NANUMPHOTO_CNT);

        gridView = (GridView) findViewById(R.id.photo_grid);
        gridView.setOnItemClickListener(this);

        adapter = new Adapter();
        gridView.setAdapter(adapter);
        loadThumbsFromGallery();
    }


    public void onButtonClicked(View v) {

        switch (v.getId()) {

            case R.id.actionbarTextButton:
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra("photos", selected);

                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
        }
    }

    private void loadThumbsFromGallery() {
        adapter.mContent.clear();
        Cursor imageCursor = null;
        try {
            final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_MODIFIED, MediaStore.Images.ImageColumns.ORIENTATION};
            final String orderBy = MediaStore.Images.Media.DATE_ADDED + " ASC";//" DESC/ASC";
            imageCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
            while (imageCursor.moveToNext()) {
                Uri uri = Uri.parse("file://" + imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                int orientation = imageCursor.getInt(imageCursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION));

                adapter.mContent.add(new Photo(uri.toString(), orientation));
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (imageCursor != null && !imageCursor.isClosed()) {
                imageCursor.close();
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object obj = adapter.getItem(position);
//        if (obj == adapter.CAMERA) {
//            final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//            startActivityForResult(intent, REQUEST_CODE_TAKEN_PHOTO_CAMERA);
//        } else

        if (obj instanceof Photo) {
            Photo photo = (Photo)obj;
            Photo photoRemove = null;

            for (Photo __photo : selected) {
                if(__photo.getUriOrg() == photo.getUriOrg()) {
                    photoRemove = __photo;
                    break;
                }
            }

            if(photoRemove != null) {
                selected.remove(photoRemove);
            } else {
                if(selected.size() == maxPhotoCnt) {
                    Toast.makeText(ImagePickerActivity.this, "최대 " + maxPhotoCnt + "장 까지 가능합니다.", Toast.LENGTH_LONG).show();
                } else {
                    selected.add((Photo) obj);
                }
            }

            adapter.notifyDataSetChanged();
            invalidateOptionsMenu();

            contextHelper.getActivity().setTextButtonEnable(selected.size() > 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_TAKEN_PHOTO_CAMERA: {
                    if (data != null) {
//                        final Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//                        new SaveImageAsyncTask(bitmap).execute();

                        gridView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loadThumbsFromGallery();
                            }
                        }, 500);
                        break;
                    }
                }
            }
        }
    }

    class Adapter extends BaseAdapter {

        //Object CAMERA = new Object();

        ArrayList<Photo> mContent = new ArrayList<>();

        @Override
        public int getCount() {
            return mContent.size();
        }

        @Override
        public Object getItem(int position) {
            if (position < mContent.size()) {
                return mContent.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Object obj = getItem(position);
//            if (obj == CAMERA) {
//                ImageView imageView = new ImageView(getContext());
//                int size = (DisplayUtils.getScreenWidth(getContext()) - DisplayUtils.dpToPixel(getContext(), 16)) / 3;
//                imageView.setLayoutParams(new
//                        AbsListView.LayoutParams(size, size));
//                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//                imageView.setImageResource(android.R.drawable.ic_menu_camera);
//                return imageView;
//            } else
            if (obj instanceof Photo) {

                Photo photo = (Photo) obj;

                String uri = photo.getUriOrg();

                FrameLayout view = (FrameLayout) (convertView == null || !"thumb".equals(convertView.getTag()) ? LayoutInflater.from(getContext()).inflate(R.layout.cell_gallery_thumbnail, null) : convertView);
                ImageView imageView = (ImageView) view.findViewById(R.id.thumb);
                if (view.getLayoutParams() == null) {
                    int size = (DisplayUtils.getScreenWidth(getContext()) - DisplayUtils.dpToPixel(getContext(), 16)) / 4;
                    view.setLayoutParams(new
                            AbsListView.LayoutParams(size, size));
                    imageView.setMaxWidth(size);
                    imageView.setMaxHeight(size);
                }
                view.setTag("thumb");


                ImageLoader.getInstance().displayImage(uri, imageView, options);

                int order = -1;
                for (int i = 0; i < selected.size(); i++) {
                    if (((Photo) obj).getUriOrg() == selected.get(i).getUriOrg()) {
                        order = i + 1;
                        break;
                    }
                }

                TextView orderText = (TextView) view.findViewById(R.id.order);

                if (order < 0) {
                    orderText.setBackground(null);
                    orderText.setText("");
                } else {
                    orderText.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.gallery_photo_selected));
                    orderText.setText(order + "");
                }


//                if(photo.getOrientation() == 90){
//                    Matrix matrix = new Matrix();
//                    imageView.setScaleType(ImageView.ScaleType.MATRIX);
//                    matrix.postRotate(90);
//                    imageView.setImageMatrix(matrix);
//                }


                return view;
            }

            return null;
        }
    }


//    private class SaveImageAsyncTask extends AsyncTask<Void, Void, String> implements MediaScannerConnection.MediaScannerConnectionClient {
//        private final String mPath;
//
//        private final String mName;
//
//        private final MediaScannerConnection mMediaScannerConnection;
//
//        private Bitmap bitmap;
//
//        public SaveImageAsyncTask(Bitmap bitmap) {
//            this.bitmap = bitmap;
//
//            this.mPath = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_PICTURES;
//            this.mName = bitmap.hashCode() + ".jpg";
//
//            this.mMediaScannerConnection = new MediaScannerConnection(ImagePickerActivity.this, this);
//        }
//
//        @Override
//        protected String doInBackground(final Void... pParams) {
//            File file = new File(mPath, mName);
//            storeImage(bitmap, file);
//
//            this.mMediaScannerConnection.connect();
//
//            return file.getAbsolutePath();
//        }
//
//        @Override
//        public void onMediaScannerConnected() {
//            this.mMediaScannerConnection.scanFile(this.mPath + "/" + this.mName, "*/*");
//        }
//
//        @Override
//        public void onScanCompleted(final String pPath, final Uri pUri) {
//            this.mMediaScannerConnection.disconnect();
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//
//            gridView.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    loadThumbsFromGallery();
//                }
//            }, 500);
//
//        }
//    }


//    public static void storeImage(Bitmap image, File pictureFile) {
//        if (pictureFile == null) {
//            Log.d(TAG, "Error creating media file, check storage permissions: ");
//            return;
//        }
//        try {
//            FileOutputStream fos = new FileOutputStream(pictureFile);
//            //image.compress(Bitmap.CompressFormat.PNG, 90, fos);
//            image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//
//            fos.close();
//        } catch (FileNotFoundException e) {
//            Log.d(TAG, "File not found: " + e.getMessage());
//        } catch (IOException e) {
//            Log.d(TAG, "Error accessing file: " + e.getMessage());
//        }
//    }

    protected Context getContext() {
        return this;
    }
}
