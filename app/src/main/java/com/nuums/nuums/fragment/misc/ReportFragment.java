package com.nuums.nuums.fragment.misc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nuums.nuums.R;
import com.nuums.nuums.adapter.UserAdapter;
import com.nuums.nuums.model.nanum.Nanum;
import com.nuums.nuums.model.report.Report;
import com.nuums.nuums.model.report.ReportData;
import com.nuums.nuums.model.report.ReportManager;
import com.nuums.nuums.model.review.Review;
import com.nuums.nuums.model.review.ReviewData;
import com.nuums.nuums.model.review.ReviewManager;
import com.nuums.nuums.model.user.NsUser;
import com.nuums.nuums.model.user.NsUserSearch;
import com.yongtrim.lib.activity.ABaseFragmentAcitivty;
import com.yongtrim.lib.fragment.ABaseFragment;
import com.yongtrim.lib.message.MessageManager;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.model.config.ConfigManager;
import com.yongtrim.lib.model.photo.Photo;
import com.yongtrim.lib.model.photo.PhotoData;
import com.yongtrim.lib.model.photo.PhotoManager;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.ui.ChipsMultiAutoCompleteTextview;
import com.yongtrim.lib.ui.CustomNetworkImageView;
import com.yongtrim.lib.ui.UltraButton;
import com.yongtrim.lib.ui.UltraEditText;
import com.yongtrim.lib.ui.sweetalert.SweetAlertDialog;
import com.yongtrim.lib.util.PixelUtil;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import de.greenrobot.event.EventBus;

/**
 * nuums / com.nuums.nuums.fragment.misc
 * <p/>
 * Created by yongtrim.com on 16. 1. 27..
 */
public class ReportFragment extends ABaseFragment implements UltraEditText.OnChangeListener {
    private final String TAG = getClass().getSimpleName();

    View mainView;

    Report report;


    ChipsMultiAutoCompleteTextview etTarget;
    EditText etDescription;
    UltraEditText etTitle;
    UltraEditText etEmail;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("report", report.toString());
    }



    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        String type = contextHelper.getActivity().getIntent().getStringExtra("type");

        if(saveInstanceState != null) {
            report = Report.getReport(saveInstanceState.getString("report"));
        }

        //report = ConfigManager.getInstance(contextHelper).getPreference().getReportParam(type);

        if(type.equals(Report.REPORTTYPE_REPORT)) {
            contextHelper.getActivity().setupActionBar("신고하기");
            if (report == null) {
                report = new Report(Report.REPORTTYPE_REPORT);
            }
            if(contextHelper.getActivity().getIntent().hasExtra("target")) {
                NsUser user = NsUser.getUser(contextHelper.getActivity().getIntent().getStringExtra("target"));
                report.setUserTarger(user);
            }
        } else if(type.equals(Report.REPORTTYPE_AD)) {
            contextHelper.getActivity().setupActionBar("광고문의");
            if (report == null) {
                report = new Report(Report.REPORTTYPE_AD);
            }
        } else if(type.equals(Report.REPORTTYPE_INQUIRY)) {
            contextHelper.getActivity().setupActionBar("1:1문의");
            if (report == null) {
                report = new Report(Report.REPORTTYPE_INQUIRY);
            }
        }

        contextHelper.getActivity().setBackButtonVisibility(false);
        contextHelper.getActivity().setImageButtonAndVisiable(R.drawable.del);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_report, container, false);

        etTarget = (ChipsMultiAutoCompleteTextview) mainView.findViewById(R.id.etUser);
        etTarget.setContextHelper(contextHelper);
        NsUserSearch[] users = new NsUserSearch[0];
        UserAdapter myAdapter = new UserAdapter(contextHelper.getContext(), R.layout.cell_searchuser, users);
        myAdapter.setContextHelper(contextHelper);
        etTarget.setAdapter(myAdapter);
        etTarget.setTokenizer(new SpaceTokenizer());
        etTarget.setThreshold(1);
        etTarget.setMaxUserCount(1);
        etTarget.setChangeListener(new ChipsMultiAutoCompleteTextview.OnChangeListener() {
            public void onEditTextChanged(ChipsMultiAutoCompleteTextview chipsMultiAutoCompleteTextview) {
                List<NsUser> tags = chipsMultiAutoCompleteTextview.getTags();
                if(tags.size() > 0) {
                    report.setUserTarger(tags.get(0));
                } else {
                    report.setUserTarger(null);
                }

                //ConfigManager.getInstance(contextHelper).getPreference().putReportParam(report.getType(), report);
            }
        });

        etTarget.setTag(report.getUserTarger());

        etDescription = (EditText)mainView.findViewById(R.id.etDescription);
        etDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                report.setMessage(s.toString());
                //ConfigManager.getInstance(contextHelper).getPreference().putReportParam(report.getType(), report);
            }
        });
        etDescription.setText(report.getMessage());


        etTitle = (UltraEditText)mainView.findViewById(R.id.etTitle);
        etTitle.setText(report.getTitle());
        etTitle.setChangeListener(this);

        etEmail = (UltraEditText)mainView.findViewById(R.id.etEmail);
        etEmail.setText(report.getEmail());
        etEmail.setChangeListener(this);
        etEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | InputType.TYPE_CLASS_TEXT);
        etEmail.setText(UserManager.getInstance(contextHelper).getMe().getEmail());

        if(report.getType().equals(Report.REPORTTYPE_REPORT)) {
            mainView.findViewById(R.id.viewReport).setVisibility(View.VISIBLE);
            etDescription.setHint("신고할 내용을 적어주세요");

        } else {
            mainView.findViewById(R.id.viewInquiry).setVisibility(View.VISIBLE);
            etDescription.setMinHeight(PixelUtil.dpToPx(getContext(), 250));

            if(report.getType().equals(Report.REPORTTYPE_AD)) {
                etDescription.setHint("");
                if(TextUtils.isEmpty(report.getMessage())) {
                    etDescription.setText("1.회사명:\n2.담당자:\n3.연락처:\n4:광고상품:\n5:예산:\n6:기간:\n7:문의내용:");
                }

            }
            else
                etDescription.setHint("문의하실 내용을 적어주세요.");

        }
        return mainView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        for(Photo photo : report.getPhotos()) {
            if(photo != null) {
                if(photo.isLocalBitmap()) {
                    photo.setPath(null);
                    photo.makeBitmap(contextHelper);
                }
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (resultCode) {
            case Activity.RESULT_OK:
                if(requestCode >= ABaseFragmentAcitivty.REQUEST_PICK_IMAGE) {

                    String uri = PhotoManager.getInstance(contextHelper).getPickImageResultUri(data).toString();
                    int orientation = PhotoManager.getInstance(contextHelper).getExifRotation(data.getData());

                    Photo photo = new Photo(uri, orientation);
                    photo.setType(Photo.TYPE_PHOTO);


                    report.setPhoto(requestCode - ABaseFragmentAcitivty.REQUEST_PICK_IMAGE, photo);
                    //ConfigManager.getInstance(contextHelper).getPreference().putReportParam(report.getType(), report);
                }
                break;
            default:
                break;
        }

    }


    public void onEditTextChanged(UltraEditText editText) {

        if(editText.equals(etTitle)) {
            report.setTitle(editText.getText());
        }

        if(editText.equals(etEmail)) {
            report.setEmail(editText.getText());
        }

        //ConfigManager.getInstance(contextHelper).getPreference().putReportParam(report.getType(), report);
    }


    void refresh() {

        CheckBox cbReason0 = (CheckBox)mainView.findViewById(R.id.cbReason0);
        CheckBox cbReason1 = (CheckBox)mainView.findViewById(R.id.cbReason1);
        CheckBox cbReason2 = (CheckBox)mainView.findViewById(R.id.cbReason2);
        CheckBox cbReason3 = (CheckBox)mainView.findViewById(R.id.cbReason3);
        CheckBox cbReason4 = (CheckBox)mainView.findViewById(R.id.cbReason4);
        CheckBox cbReason5 = (CheckBox)mainView.findViewById(R.id.cbReason5);
        CheckBox cbReason6 = (CheckBox)mainView.findViewById(R.id.cbReason6);
        cbReason0.setChecked(false);
        cbReason1.setChecked(false);
        cbReason2.setChecked(false);
        cbReason3.setChecked(false);
        cbReason4.setChecked(false);
        cbReason5.setChecked(false);
        cbReason6.setChecked(false);

        if(report.getReportType() == 0)
            cbReason0.setChecked(true);
        else if(report.getReportType() == 1)
            cbReason1.setChecked(true);
        else if(report.getReportType() == 2)
            cbReason2.setChecked(true);
        else if(report.getReportType() == 3)
            cbReason3.setChecked(true);
        else if(report.getReportType() == 4)
            cbReason4.setChecked(true);
        else if(report.getReportType() == 5)
            cbReason5.setChecked(true);
        else if(report.getReportType() == 6)
            cbReason6.setChecked(true);


        CustomNetworkImageView ivThumbnail0 = (CustomNetworkImageView)mainView.findViewById(R.id.ivThumbnail0);
        CustomNetworkImageView ivThumbnail1 = (CustomNetworkImageView)mainView.findViewById(R.id.ivThumbnail1);
        CustomNetworkImageView ivThumbnail2 = (CustomNetworkImageView)mainView.findViewById(R.id.ivThumbnail2);

        ImageView ivAdd0 = (ImageView)mainView.findViewById(R.id.ivAdd0);
        ImageView ivAdd1 = (ImageView)mainView.findViewById(R.id.ivAdd1);
        ImageView ivAdd2 = (ImageView)mainView.findViewById(R.id.ivAdd2);



        if(report.getPhotos().get(0) == null || !report.getPhotos().get(0).hasPhoto()) {
            ivAdd0.setVisibility(View.VISIBLE);
        } else {
            ivAdd0.setVisibility(View.GONE);
        }
        contextHelper.setPhoto(ivThumbnail0, report.getPhotos().get(0));


        if(report.getPhotos().get(1) == null || !report.getPhotos().get(1).hasPhoto()) {
            ivAdd1.setVisibility(View.VISIBLE);
        } else {
            ivAdd1.setVisibility(View.GONE);
        }
        contextHelper.setPhoto(ivThumbnail1, report.getPhotos().get(1));


        if(report.getPhotos().get(2) == null || !report.getPhotos().get(2).hasPhoto()) {
            ivAdd2.setVisibility(View.VISIBLE);
        } else {
            ivAdd2.setVisibility(View.GONE);
        }
        contextHelper.setPhoto(ivThumbnail2, report.getPhotos().get(2));



        //ConfigManager.getInstance(contextHelper).getPreference().putReportParam(report.getType(), report);
    }

    public void onButtonClicked(View v) {

        switch(v.getId()) {
            case R.id.actionbarImageButton:
                contextHelper.getActivity().finish();
                break;
            case R.id.cbReason0:
            case R.id.viewReason0:
                report.setReportType(0);
                refresh();

                break;
            case R.id.cbReason1:
            case R.id.viewReason1:
                report.setReportType(1);
                refresh();
                break;
            case R.id.cbReason2:
            case R.id.viewReason2:
                report.setReportType(2);
                refresh();
                break;
            case R.id.cbReason3:
            case R.id.viewReason3:
                report.setReportType(3);
                refresh();
                break;
            case R.id.cbReason4:
            case R.id.viewReason4:
                report.setReportType(4);
                refresh();
                break;
            case R.id.cbReason5:
            case R.id.viewReason5:
                report.setReportType(5);
                refresh();
                break;
            case R.id.cbReason6:
            case R.id.viewReason6:
                report.setReportType(6);
                refresh();
                break;
            case R.id.viewThumbnail0:
                new SweetAlertDialog(getContext(), SweetAlertDialog.SELECT_TYPE)
                        .setTitleText("사진 선택하기")
                        .setArrayValue(new String[]{"사진 선택하기", "삭제하기"})
                        .setSelectListener(new SweetAlertDialog.OnSweetSelectListener() {
                            @Override
                            public void onSelected(SweetAlertDialog sweetAlertDialog, int index) {

                                sweetAlertDialog.dismissWithAnimation();

                                switch (index) {
                                    case 0:
                                        contextHelper.getActivity().startActivityForResult(
                                                PhotoManager.getInstance(contextHelper).getPickImageChooserIntent(1),
                                                ABaseFragmentAcitivty.REQUEST_PICK_IMAGE);
                                        break;
                                    case 1:
                                        report.setPhoto(0, null);
                                        //ConfigManager.getInstance(contextHelper).getPreference().putReportParam(report.getType(), report);
                                        refresh();
                                        break;
                                }
                            }
                        })
                        .show();


                break;
            case R.id.viewThumbnail1:
                new SweetAlertDialog(getContext(), SweetAlertDialog.SELECT_TYPE)
                        .setTitleText("사진 선택하기")
                        .setArrayValue(new String[]{"사진 선택하기", "삭제하기"})
                        .setSelectListener(new SweetAlertDialog.OnSweetSelectListener() {
                            @Override
                            public void onSelected(SweetAlertDialog sweetAlertDialog, int index) {

                                sweetAlertDialog.dismissWithAnimation();

                                switch (index) {
                                    case 0:
                                        contextHelper.getActivity().startActivityForResult(
                                                PhotoManager.getInstance(contextHelper).getPickImageChooserIntent(1),
                                                ABaseFragmentAcitivty.REQUEST_PICK_IMAGE + 1);
                                        break;
                                    case 1:
                                        report.setPhoto(1, null);
                                        //ConfigManager.getInstance(contextHelper).getPreference().putReportParam(report.getType(), report);
                                        refresh();
                                        break;
                                }
                            }
                        })
                        .show();
                break;
            case R.id.viewThumbnail2:
                new SweetAlertDialog(getContext(), SweetAlertDialog.SELECT_TYPE)
                        .setTitleText("사진 선택하기")
                        .setArrayValue(new String[]{"사진 선택하기", "삭제하기"})
                        .setSelectListener(new SweetAlertDialog.OnSweetSelectListener() {
                            @Override
                            public void onSelected(SweetAlertDialog sweetAlertDialog, int index) {

                                sweetAlertDialog.dismissWithAnimation();

                                switch (index) {
                                    case 0:
                                        contextHelper.getActivity().startActivityForResult(
                                                PhotoManager.getInstance(contextHelper).getPickImageChooserIntent(1),
                                                ABaseFragmentAcitivty.REQUEST_PICK_IMAGE + 2);
                                        break;
                                    case 1:
                                        report.setPhoto(2, null);
                                        //ConfigManager.getInstance(contextHelper).getPreference().putReportParam(report.getType(), report);
                                        refresh();
                                        break;
                                }
                            }
                        })
                        .show();
                break;

            case R.id.btnSubmit:
                submit();
                break;
        }
    }


    public void onEvent(PushMessage pushMessage) {
    }


    void submit() {

        if(report.getType().equals(Report.REPORTTYPE_REPORT)) {
            if (report.getUserTarger() == null) {
                new SweetAlertDialog(getContext()).setContentText("신고회원을 입력하세요.").show();
                return;
            }
        } else {
            if (TextUtils.isEmpty(report.getTitle())) {
                new SweetAlertDialog(getContext()).setContentText("제목을 입력하세요.").show();
                return;
            }
            if (TextUtils.isEmpty(report.getEmail())) {
                new SweetAlertDialog(getContext()).setContentText("이메일을 입력하세요.").show();
                return;
            }
        }


        if(TextUtils.isEmpty(report.getMessage())) {
            new SweetAlertDialog(getContext()).setContentText("상세설명을 입력하세요.").show();
            return;
        }


        contextHelper.showProgress("");

        if(report.getUserTarger() != null) {
            report.setUserTargetId(report.getUserTarger().getId());
        }


        final CountDownLatch latchPhoto = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PhotoManager.getInstance(contextHelper).uploadPhotos(report.getPhotos(), new PhotoManager.OnPhotoUploadedListener() {
                        public void onCompleted(List<Photo> photos) {
                            report.setPhotos(photos);
                            latchPhoto.countDown();
                        }
                    });
                } catch (Exception e) {
                    latchPhoto.countDown();
                }
            }
        }).start();


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    latchPhoto.await();
                    ReportManager.getInstance(contextHelper).create(report,
                            new Response.Listener<ReportData>() {
                                @Override
                                public void onResponse(ReportData response) {

                                    PhotoManager.getInstance(contextHelper).clearCache();
                                    if (response.isSuccess()) {
                                        //ConfigManager.getInstance(contextHelper).getPreference().putReportParam(report.getType(), report);

                                        contextHelper.getActivity().finish();
                                        Toast.makeText(contextHelper.getContext(), "접수하였습니다.", Toast.LENGTH_LONG).show();

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

}


