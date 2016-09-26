package com.nuums.nuums.fragment.nanum;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.nuums.nuums.R;
import com.nuums.nuums.activity.BaseActivity;
import com.nuums.nuums.adapter.PhotoPagerAdapter;
import com.nuums.nuums.model.nanum.Nanum;
import com.nuums.nuums.model.nanum.NanumData;
import com.nuums.nuums.model.nanum.NanumManager;
import com.nuums.nuums.view.PhotoScrollView;
import com.yongtrim.lib.Config;
import com.yongtrim.lib.activity.ABaseFragmentAcitivty;
import com.yongtrim.lib.fragment.ABaseFragment;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.model.config.ConfigManager;
import com.yongtrim.lib.model.photo.Photo;
import com.yongtrim.lib.model.photo.PhotoManager;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.ui.UltraButton;
import com.yongtrim.lib.ui.UltraEditText;
import com.yongtrim.lib.ui.autoscrollviewpager.AutoScrollViewPager;
import com.yongtrim.lib.ui.sweetalert.SweetAlertDialog;
import com.yongtrim.lib.util.StringFilter;
import com.yongtrim.lib.util.UIUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import de.greenrobot.event.EventBus;

/**
 * nuums / com.nuums.nuums.fragment.nanum
 * <p/>
 * Created by Uihyun on 15. 12. 20..
 */
public class NanumEditFragment extends ABaseFragment implements UltraEditText.OnChangeListener {
    private final String TAG = getClass().getSimpleName();

    View viewTop;
    AutoScrollViewPager viewPhotoMain;
    PhotoPagerAdapter photoPagerAdapter;

    PhotoScrollView photoScrollView;
    UltraButton btnDescription;
    UltraButton btnLocation;
    UltraButton btnAmount;

    UltraEditText etTitle;

    View viewArrival;
    CheckBox cbArrival;
    TextView tvArrival;

    View viewSelect;
    CheckBox cbSelect;
    TextView tvSelect;

    View viewRandom;
    CheckBox cbRandom;
    TextView tvRandom;

    Nanum nanum;

    boolean isModify;
    boolean isAsker;

    List<Photo> photosBuffer;


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("nanum", nanum.toString());
    }


    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        isModify = contextHelper.getActivity().getIntent().getBooleanExtra("isModify", false);

        if (saveInstanceState != null) {
            nanum = Nanum.getNanum(saveInstanceState.getString("nanum"));
        }

        if (isModify) {
            contextHelper.getActivity().setupActionBar("수정하기");
            if (nanum == null)
                nanum = Nanum.getNanum(contextHelper.getActivity().getIntent().getStringExtra("nanum"));
        } else if (contextHelper.getActivity().getIntent().hasExtra("asker")) {
            contextHelper.getActivity().setupActionBar("나눔등록");
            isAsker = true;

            if (nanum == null)
                nanum = new Nanum();
        } else {
            if (nanum == null)
                nanum = ConfigManager.getInstance(contextHelper).getPreference().getNanumParam();
            if (nanum == null) {
                nanum = new Nanum();
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nanumedit, container, false);

        viewTop = view.findViewById(R.id.viewTop);
        UIUtil.setRatio(viewTop, getContext(), 320, 250);//원래 200

        viewPhotoMain = (AutoScrollViewPager) view.findViewById(R.id.viewPhoto);
        viewPhotoMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                photoScrollView.setSelectIndex(position);
                photoScrollView.refresh(contextHelper);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        photoScrollView = (PhotoScrollView) view.findViewById(R.id.photoScrollView);
        photoScrollView.setPhotos((ArrayList<Photo>) nanum.getPhotos());
        photoScrollView.setMaxPhotoCount(6);
        photoScrollView.setPhotoChangeListener(new PhotoScrollView.OnPhotoChangeListener() {
            @Override
            public void onChanged(List<Photo> photos) {
                nanum.setPhotos(photos);
                refresh();
            }

            @Override
            public void onSelected(int index) {
                photoScrollView.setSelectIndex(index);
                viewPhotoMain.setCurrentItem(index);
                if (!nanum.getPhotos().get(index).hasPhoto()) {
                    contextHelper.getActivity().startActivityForResult(
                            PhotoManager.getInstance(contextHelper).getPickImageChooserIntent(Config.MAX_NANUMPHOTO_CNT - index),
                            ABaseFragmentAcitivty.REQUEST_PICK_IMAGE + index);
                }

                photoScrollView.refresh(contextHelper);
            }
        });


        btnDescription = (UltraButton) view.findViewById(R.id.btnDescription);
        btnLocation = (UltraButton) view.findViewById(R.id.btnLocation);
        btnAmount = (UltraButton) view.findViewById(R.id.btnAmount);

        viewArrival = view.findViewById(R.id.viewArrival);
        cbArrival = (CheckBox) view.findViewById(R.id.cbArrival);
        tvArrival = (TextView) view.findViewById(R.id.tvArrival);

        viewSelect = view.findViewById(R.id.viewSelect);
        cbSelect = (CheckBox) view.findViewById(R.id.cbSelect);
        tvSelect = (TextView) view.findViewById(R.id.tvSelect);

        viewRandom = view.findViewById(R.id.viewRandom);
        cbRandom = (CheckBox) view.findViewById(R.id.cbRandom);
        tvRandom = (TextView) view.findViewById(R.id.tvRandom);

        etTitle = (UltraEditText) view.findViewById(R.id.etTitle);
        etTitle.setText(nanum.getTitle());
        etTitle.setChangeListener(this);

        InputFilter[] allowAlphanumericHangul = new InputFilter[1];
        StringFilter stringFilter = new StringFilter(contextHelper.getContext());
        allowAlphanumericHangul[0] = stringFilter.allowAlphanumericHangul;

        etTitle.setFilters(allowAlphanumericHangul);

        if (isModify) {
            UltraButton btnSubmit = (UltraButton) view.findViewById(R.id.btnSubmit);
            btnSubmit.setText("수정하기");

            cbArrival.setEnabled(false);
            cbSelect.setEnabled(false);
            cbRandom.setEnabled(false);

            view.findViewById(R.id.viewLine).setVisibility(View.GONE);
        }


        if (UserManager.getInstance(contextHelper).getMe().getRole() != null && UserManager.getInstance(contextHelper).getMe().getRole().equals("ADMIN")) {
            btnLocation.setVisibility(View.GONE);
        }

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        photoScrollView.loadPhotos(contextHelper, 0, nanum.getPhotos());
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

    void clear() {
        ConfigManager.getInstance(contextHelper).getPreference().putNanumParam(null);
        nanum = new Nanum();

        etTitle.setText(null);
        photoPagerAdapter = null;
        photoScrollView.setPhotos(nanum.getPhotos());
        refresh();
    }

    void refresh() {
        if (photoPagerAdapter == null) {
            photoPagerAdapter = new PhotoPagerAdapter(contextHelper, nanum.getPhotos());
            viewPhotoMain.setAdapter(photoPagerAdapter);
            photoPagerAdapter.setOnPhotoPagerListener(new PhotoPagerAdapter.OnPhotoPagerListener() {
                @Override
                public void modify(int index) {

                    Uri source = Uri.parse(nanum.getPhotos().get(index).getUriOrg());

                    contextHelper.getActivity().startActivityForResult(
                            PhotoManager.getInstance(contextHelper).getCropImageIntent(source, false),
                            ABaseFragmentAcitivty.REQUEST_CROP_IMAGE + index);
                }

                @Override
                public void delete(final int index) {
                    new SweetAlertDialog(getContext())
                            .setContentText("등록한 사진을 삭제합니다.")
                            .showCancelButton(true)
                            .setConfirmText("예")
                            .setCancelText("아니오")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(final SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                    nanum.getPhotos().get(index).clear();
                                    refresh();
                                }
                            })
                            .show();

                }

                @Override
                public void add(int index) {
                    contextHelper.getActivity().startActivityForResult(
                            PhotoManager.getInstance(contextHelper).getPickImageChooserIntent(Config.MAX_NANUMPHOTO_CNT - index),
                            ABaseFragmentAcitivty.REQUEST_PICK_IMAGE + index);
                }
            });

        } else {
            photoPagerAdapter.notifyDataSetChanged();
        }

        photoScrollView.refresh(contextHelper);

        if (TextUtils.isEmpty(nanum.getDescription())) {
            btnDescription.setText("상세설명");
        } else {
            btnDescription.setText(nanum.getDescription());
        }

        if (TextUtils.isEmpty(nanum.getAddressFake())) {
            btnLocation.setText("위치");
        } else {
            btnLocation.setText(nanum.getAddressFakeShorter());
        }

        if (nanum.getAmount() > 0) {
            btnAmount.setText(nanum.getAmount() + "개");
        } else {
            btnAmount.setText("수량");
        }


        cbArrival.setChecked(false);
        cbSelect.setChecked(false);
        cbRandom.setChecked(false);

        tvArrival.setTextColor(ContextCompat.getColor(contextHelper.getContext(), R.color.gray));
        tvSelect.setTextColor(ContextCompat.getColor(contextHelper.getContext(), R.color.gray));
        tvRandom.setTextColor(ContextCompat.getColor(contextHelper.getContext(), R.color.gray));

        if (nanum.getMethod().equals(Nanum.METHOD_ARRIVAL)) {
            cbArrival.setChecked(true);
            tvArrival.setTextColor(ContextCompat.getColor(contextHelper.getContext(), R.color.green));

        } else if (nanum.getMethod().equals(Nanum.METHOD_SELECT)) {
            cbSelect.setChecked(true);
            tvSelect.setTextColor(ContextCompat.getColor(contextHelper.getContext(), R.color.green));

        } else if (nanum.getMethod().equals(Nanum.METHOD_RANDOM)) {
            cbRandom.setChecked(true);
            tvRandom.setTextColor(ContextCompat.getColor(contextHelper.getContext(), R.color.green));
        }

        if (!isModify && !isAsker)
            ConfigManager.getInstance(contextHelper).getPreference().putNanumParam(nanum);
    }


    public void onButtonClicked(View v) {

        switch (v.getId()) {
            case R.id.btnDescription: {
                photosBuffer = nanum.getPhotos();

                Intent i = new Intent(getContext(), BaseActivity.class);
                i.putExtra("activityCode", BaseActivity.ActivityCode.DESCRIPTIONEDIT.ordinal());
                i.putExtra("nanum", nanum.toString());
                contextHelper.getActivity().startActivityForResult(i, ABaseFragmentAcitivty.REQUEST_DESCRIPTION);
            }
            break;
            case R.id.btnLocation: {
                photosBuffer = nanum.getPhotos();

                Intent i = new Intent(getContext(), BaseActivity.class);
                i.putExtra("activityCode", BaseActivity.ActivityCode.POSTCODE.ordinal());
                i.putExtra("nanum", nanum.toString());
                contextHelper.getActivity().startActivityForResult(i, ABaseFragmentAcitivty.REQUEST_POSTCODE);
            }
            break;
            case R.id.btnAmount: {
                if (isModify) {
                    new SweetAlertDialog(getContext()).setContentText("수정하실 수 없습니다.").show();
                } else {
                    new SweetAlertDialog(getContext(), SweetAlertDialog.NUMBERPICKER_TYPE)
                            .setTitleText("수량")
                            .setNumber(nanum.getAmount() > 0 ? nanum.getAmount() : 1, 1, 100, "개")
                            .showCancelButton(true)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    nanum.setAmount(sDialog.getNumberValue());
                                    sDialog.dismissWithAnimation();
                                    refresh();
                                }
                            })
                            .show();
                }
            }
            break;
            case R.id.btnSubmit: {
                varify();
            }
            break;
            case R.id.viewArrival:
            case R.id.cbArrival: {
                if (isModify) {
                    new SweetAlertDialog(getContext()).setContentText("수정하실 수 없습니다.").show();
                } else {
                    nanum.setMethod(Nanum.METHOD_ARRIVAL);
                    refresh();
                }
            }
            break;
            case R.id.viewSelect:
            case R.id.cbSelect: {
                if (isModify) {
                    new SweetAlertDialog(getContext()).setContentText("수정하실 수 없습니다.").show();
                } else {
                    nanum.setMethod(Nanum.METHOD_SELECT);
                    refresh();
                }
            }
            break;
            case R.id.viewRandom:
            case R.id.cbRandom: {
                if (isModify) {
                    new SweetAlertDialog(getContext()).setContentText("수정하실 수 없습니다.").show();
                } else {
                    nanum.setMethod(Nanum.METHOD_RANDOM);
                    refresh();
                }
            }
            break;
            case R.id.actionbarImageButton:
                new SweetAlertDialog(getContext())
                        .setContentText("입력한 내용을 모두 지웁니다.")
                        .showCancelButton(true)
                        .setConfirmText("예")
                        .setCancelText("아니오")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(final SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                                clear();
                                refresh();
                            }
                        })
                        .show();
                break;
        }
    }


    public void onEvent(PushMessage pushMessage) {
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case Activity.RESULT_OK:
                if (requestCode >= ABaseFragmentAcitivty.REQUEST_CROP_IMAGE) {

                    nanum.getPhotos().get(requestCode - ABaseFragmentAcitivty.REQUEST_CROP_IMAGE).setPath(PhotoManager.getInstance(contextHelper).getPathCropped());
                    nanum.getPhotos().get(requestCode - ABaseFragmentAcitivty.REQUEST_CROP_IMAGE).makeBitmap(contextHelper);
                    refresh();

                } else if (requestCode == ABaseFragmentAcitivty.REQUEST_DESCRIPTION) {
                    if (data != null) {
                        nanum = Nanum.getNanum(data.getStringExtra("nanum"));
                        nanum.setPhotos(photosBuffer);
                    }

                    refresh();
                } else if (requestCode == ABaseFragmentAcitivty.REQUEST_POSTCODE) {
                    if (data != null) {
                        nanum = Nanum.getNanum(data.getStringExtra("nanum"));
                        nanum.setPhotos(photosBuffer);
                    }

                    refresh();
                } else if (requestCode >= ABaseFragmentAcitivty.REQUEST_PICK_IMAGE) {

                    if (data != null && data.getParcelableArrayListExtra("photos") != null) {
                        List<Photo> photos = data.getParcelableArrayListExtra("photos");

                        int index = requestCode - ABaseFragmentAcitivty.REQUEST_PICK_IMAGE;

                        for (Photo _photo : photos) {
                            _photo.setPath(null);
                            _photo.makeBitmap(contextHelper);

                            nanum.getPhotos().set(index++, _photo);
                        }
                    } else {
                        Uri uri = PhotoManager.getInstance(contextHelper).getPickImageResultUri(data);
                        int orientation = PhotoManager.getInstance(contextHelper).getExifRotation(uri);

                        Photo photo = new Photo(uri.toString(), orientation);
                        photo.setType(Photo.TYPE_PHOTO);
                        photo.setPath(null);
                        photo.makeBitmap(contextHelper);

                        nanum.getPhotos().set(requestCode - ABaseFragmentAcitivty.REQUEST_PICK_IMAGE, photo);
                    }

                    if (!isModify && !isAsker)
                        ConfigManager.getInstance(contextHelper).getPreference().putNanumParam(nanum);
                }
        break;

        default:
        break;
    }

}


    public void onEditTextChanged(UltraEditText editText) {
        nanum.setTitle(editText.getText().toString());
        if (!isModify && !isAsker)
            ConfigManager.getInstance(contextHelper).getPreference().putNanumParam(nanum);
    }


    void varify() {
        if (nanum.getPhotoCnt() < 3) {
            new SweetAlertDialog(getContext()).setContentText("사진은 3장 이상 등록해야 합니다.").show();
            return;
        }

        if (TextUtils.isEmpty(nanum.getTitle())) {
            new SweetAlertDialog(getContext()).setContentText("나눔명을 입력하세요.").show();
            return;
        }

        if (UserManager.getInstance(contextHelper).getMe().getRole() != null &&
                !UserManager.getInstance(contextHelper).getMe().getRole().equals("ADMIN")) {
            if (TextUtils.isEmpty(nanum.getAddressFake())) {
                new SweetAlertDialog(getContext()).setContentText("위치를 입력하세요.").show();
                return;
            }
        }

        if (nanum.getAmount() == 0) {
            new SweetAlertDialog(getContext()).setContentText("수량을 입력하세요.").show();
            return;
        }

        if (TextUtils.isEmpty(nanum.getDescription())) {
            new SweetAlertDialog(getContext()).setContentText("상세설명을 입력하세요.").show();
            return;
        }


        if (isModify) {
            submit();
        } else {

            String str0 = "등록 후에는 ";
            String str1 = "수량";
            String str2 = " 및 ";
            String str3 = "당첨방식";
            String str4 = "을 수정할 수 없습니다.\n등록 하시겠습니까?";

            SpannableString text = new SpannableString(str0 + str1 + str2 + str3 + str4);

            int offset = str0.length();
            text.setSpan(new ForegroundColorSpan(ContextCompat.getColor(contextHelper.getContext(), R.color.red)), offset, offset + str1.length(), 0);

            offset += str1.length();
            offset += str2.length();

            text.setSpan(new ForegroundColorSpan(ContextCompat.getColor(contextHelper.getContext(), R.color.red)), offset, offset + str3.length(), 0);


            new SweetAlertDialog(getContext())
                    .setContentText(text)
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

        if (isModify) {
            latchCreate.countDown();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        NanumManager.getInstance(contextHelper).create(
                                new Response.Listener<NanumData>() {
                                    @Override
                                    public void onResponse(NanumData response) {
                                        if (response.isSuccess()) {
                                            //response.shop.patch(contextHelper);
                                            if (response.user != null) {
                                                UserManager.getInstance(contextHelper).setMe(response.user);

                                            }

                                            nanum.setId(response.nanum.getId());
                                            nanum.setOwner(response.nanum.getOwner());
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

        final CountDownLatch latchPhoto = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    latchCreate.await();
                    PhotoManager.getInstance(contextHelper).uploadPhotos(nanum.getPhotos(), new PhotoManager.OnPhotoUploadedListener() {
                        public void onCompleted(List<Photo> photos) {
                            nanum.setPhotos(photos);
                            latchPhoto.countDown();
                        }
                    });
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

                    NanumManager.getInstance(contextHelper).update(
                            nanum,
                            new Response.Listener<NanumData>() {
                                @Override
                                public void onResponse(NanumData response) {
                                    if (response.isSuccess()) {
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

                                if (isModify) {
                                    EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_CHANGE_NANUM).setObject(nanum));
                                    contextHelper.getActivity().finish();
                                    Toast.makeText(contextHelper.getContext(), "수정하였습니다.", Toast.LENGTH_SHORT).show();
                                } else if (isAsker) {
                                    EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_ADDED_NANUM).setObject(nanum));
                                    contextHelper.getActivity().finish();
                                    Toast.makeText(contextHelper.getContext(), "등록하였습니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_ADDED_NANUM).setObject(nanum));

                                    EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_CHANGE_ME).setObject(null));

                                    clear();
                                    refresh();
                                    new SweetAlertDialog(getContext())
                                            .setContentText("등록하였습니다.")
                                            .show();
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
}


