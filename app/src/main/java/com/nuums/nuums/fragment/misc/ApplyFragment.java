package com.nuums.nuums.fragment.misc;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.nuums.nuums.R;
import com.nuums.nuums.activity.BaseActivity;
import com.nuums.nuums.adapter.PhotoPagerAdapter;
import com.nuums.nuums.model.apply.Apply;
import com.nuums.nuums.model.apply.ApplyData;
import com.nuums.nuums.model.apply.ApplyManager;
import com.nuums.nuums.model.chat.Address;
import com.yongtrim.lib.Config;
import com.yongtrim.lib.activity.ABaseFragmentAcitivty;
import com.yongtrim.lib.fragment.ABaseFragment;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.model.config.ConfigManager;
import com.yongtrim.lib.model.photo.Photo;
import com.yongtrim.lib.model.photo.PhotoManager;
import com.yongtrim.lib.model.user.UserData;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.ui.UltraButton;
import com.yongtrim.lib.ui.UltraEditText;
import com.yongtrim.lib.ui.autoscrollviewpager.AutoScrollViewPager;
import com.yongtrim.lib.ui.sweetalert.SweetAlertDialog;
import com.yongtrim.lib.util.StringFilter;
import com.yongtrim.lib.util.UIUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import de.greenrobot.event.EventBus;

/**
 * nuums / com.nuums.nuums.fragment.misc
 * <p/>
 * Created by yongtrim.com on 16. 1. 10..
 */
public class ApplyFragment extends ABaseFragment implements UltraEditText.OnChangeListener {
    private final String TAG = getClass().getSimpleName();

    UltraEditText etTitle;
    UltraButton btnSize;
    UltraEditText etCaution;
    TextView tvCaution;

    UltraEditText etSenderName;
    UltraEditText etSenderNumber0;
    UltraEditText etSenderNumber1;
    UltraEditText etSenderNumber2;
    UltraButton btnSenderPostNumber;
    UltraButton btnSenderBasic;
    EditText etSenderDetail;
    CheckBox cbSenderRemember;

    UltraEditText etReceiverName;
    UltraEditText etReceiverNumber0;
    UltraEditText etReceiverNumber1;
    UltraEditText etReceiverNumber2;
    UltraButton btnReceiverPostNumber;
    UltraButton btnReceiverBasic;
    EditText etReceiverDetail;


    View viewCOD;
    CheckBox cbCOD;
    TextView tvCOD;

    View viewPrepaid;
    CheckBox cbPrepaid;
    TextView tvPrepaid;


    Apply apply;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);


        contextHelper.getActivity().setupActionBar("배송신청");
        contextHelper.getActivity().setBackButtonVisibility(false);
        contextHelper.getActivity().setImageButtonAndVisiable(R.drawable.del);

        apply = new Apply();

        if(ConfigManager.getInstance(contextHelper).getPreference().getMyAddress() != null) {
            apply.setAddressSender(ConfigManager.getInstance(contextHelper).getPreference().getMyAddress());
        }

        if(contextHelper.getActivity().getIntent().hasExtra("receiver")) {
            apply.setAddressReceiver(Address.getAddress(contextHelper.getActivity().getIntent().getStringExtra("receiver")));
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apply, container, false);

        etTitle = (UltraEditText)view.findViewById(R.id.etTitle);
        btnSize = (UltraButton)view.findViewById(R.id.btnSize);
        etCaution = (UltraEditText)view.findViewById(R.id.etCaution);
        tvCaution = (TextView)view.findViewById(R.id.tvCaution);

        tvCaution.setText(ConfigManager.getInstance(contextHelper).getConfigHello().getParams().getApplyCaution());

        View viewSender = (View)view.findViewById(R.id.viewSender);
        etSenderName = (UltraEditText)viewSender.findViewById(R.id.etName);
        etSenderNumber0 = (UltraEditText)viewSender.findViewById(R.id.etNumber0);
        etSenderNumber1 = (UltraEditText)viewSender.findViewById(R.id.etNumber1);
        etSenderNumber2 = (UltraEditText)viewSender.findViewById(R.id.etNumber2);
        etSenderNumber1.setEditTextId(R.id.number1);
        etSenderNumber2.setEditTextId(R.id.number2);

        etSenderNumber0.setMaxCharacters(3);
        etSenderNumber0.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etSenderNumber0.setNextFocusDownId(R.id.number1);

        etSenderNumber1.setMaxCharacters(4);
        etSenderNumber1.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etSenderNumber1.setNextFocusDownId(R.id.number2);

        etSenderNumber2.setMaxCharacters(4);
        etSenderNumber2.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        btnSenderPostNumber = (UltraButton)viewSender.findViewById(R.id.btnPostNumber);
        btnSenderBasic = (UltraButton)viewSender.findViewById(R.id.btnBasic);


        btnSenderPostNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), BaseActivity.class);
                i.putExtra("activityCode", BaseActivity.ActivityCode.POSTCODE.ordinal());
                contextHelper.getActivity().startActivityForResult(i, ABaseFragmentAcitivty.REQUEST_POSTCODE);
            }
        });

        btnSenderBasic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), BaseActivity.class);
                i.putExtra("activityCode", BaseActivity.ActivityCode.POSTCODE.ordinal());
                contextHelper.getActivity().startActivityForResult(i, ABaseFragmentAcitivty.REQUEST_POSTCODE);
            }
        });


        etSenderDetail = (EditText)viewSender.findViewById(R.id.etDetail);
        cbSenderRemember = (CheckBox)viewSender.findViewById(R.id.cbRemember);


        View viewReceiver = (View)view.findViewById(R.id.viewReceiver);
        etReceiverName = (UltraEditText)viewReceiver.findViewById(R.id.etName);
        etReceiverName.setHint("받는 사람");
        etReceiverNumber0 = (UltraEditText)viewReceiver.findViewById(R.id.etNumber0);
        etReceiverNumber1 = (UltraEditText)viewReceiver.findViewById(R.id.etNumber1);
        etReceiverNumber2 = (UltraEditText)viewReceiver.findViewById(R.id.etNumber2);

        etReceiverNumber1.setEditTextId(R.id.number1_1);
        etReceiverNumber2.setEditTextId(R.id.number1_2);

        etReceiverNumber0.setMaxCharacters(3);
        etReceiverNumber0.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etReceiverNumber0.setNextFocusDownId(R.id.number1_1);

        etReceiverNumber1.setMaxCharacters(4);
        etReceiverNumber1.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etReceiverNumber1.setNextFocusDownId(R.id.number1_2);

        etReceiverNumber2.setMaxCharacters(4);
        etReceiverNumber2.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        btnReceiverPostNumber = (UltraButton)viewReceiver.findViewById(R.id.btnPostNumber);
        btnReceiverBasic = (UltraButton)viewReceiver.findViewById(R.id.btnBasic);

        btnReceiverPostNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), BaseActivity.class);
                i.putExtra("activityCode", BaseActivity.ActivityCode.POSTCODE.ordinal());
                contextHelper.getActivity().startActivityForResult(i, ABaseFragmentAcitivty.REQUEST_POSTCODE + 1);
            }
        });

        btnReceiverBasic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), BaseActivity.class);
                i.putExtra("activityCode", BaseActivity.ActivityCode.POSTCODE.ordinal());
                contextHelper.getActivity().startActivityForResult(i, ABaseFragmentAcitivty.REQUEST_POSTCODE + 1);
            }
        });



        etReceiverDetail = (EditText)viewReceiver.findViewById(R.id.etDetail);
        viewReceiver.findViewById(R.id.viewRemember).setVisibility(View.GONE);

        viewCOD = view.findViewById(R.id.viewCOD);
        cbCOD = (CheckBox)view.findViewById(R.id.cbCOD);
        tvCOD = (TextView)view.findViewById(R.id.tvCOD);

        viewPrepaid = view.findViewById(R.id.viewPrepaid);
        cbPrepaid = (CheckBox)view.findViewById(R.id.cbPrepaid);
        tvPrepaid = (TextView)view.findViewById(R.id.tvPrepaid);


        Address addressSender = apply.getAddressSender();

        etSenderName.setText(addressSender.getName());
        etSenderNumber0.setText(addressSender.getNumber0());
        etSenderNumber1.setText(addressSender.getNumber1());
        etSenderNumber2.setText(addressSender.getNumber2());

        if(TextUtils.isEmpty(addressSender.getPostCode())) {
            btnSenderPostNumber.setText("우편번호");
        } else {
            btnSenderPostNumber.setText(addressSender.getPostCode());
        }

        if(TextUtils.isEmpty(addressSender.getAddressBasic())) {
            btnSenderBasic.setText("기본주소");
        } else {
            btnSenderBasic.setText(addressSender.getAddressBasic());
        }

        etSenderDetail.setText(addressSender.getAddressDetail());

        Address addressReceiver = apply.getAddressReceiver();

        etReceiverName.setText(addressReceiver.getName());
        etReceiverNumber0.setText(addressReceiver.getNumber0());
        etReceiverNumber1.setText(addressReceiver.getNumber1());
        etReceiverNumber2.setText(addressReceiver.getNumber2());

        if(TextUtils.isEmpty(addressReceiver.getPostCode())) {
            btnReceiverPostNumber.setText("우편번호");
        } else {
            btnReceiverPostNumber.setText(addressReceiver.getPostCode());
        }

        if(TextUtils.isEmpty(addressReceiver.getAddressBasic())) {
            btnReceiverBasic.setText("기본주소");
        } else {
            btnReceiverBasic.setText(addressReceiver.getAddressBasic());
        }
        etReceiverDetail.setText(addressReceiver.getAddressDetail());

        refresh();

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
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
                if(requestCode == ABaseFragmentAcitivty.REQUEST_POSTCODE) {
                    String postcode = data.getStringExtra("postcode");
                    String basicaddress = data.getStringExtra("basicaddress");

                    Address address = apply.getAddressSender();
                    address.setPostCode(postcode);
                    address.setAddressBasic(basicaddress);

                    btnSenderPostNumber.setText(address.getPostCode());
                    btnSenderBasic.setText(address.getAddressBasic());


                } else  if(requestCode == ABaseFragmentAcitivty.REQUEST_POSTCODE + 1) {
                    String postcode = data.getStringExtra("postcode");
                    String basicaddress = data.getStringExtra("basicaddress");

                    Address address = apply.getAddressReceiver();
                    address.setPostCode(postcode);
                    address.setAddressBasic(basicaddress);


                    btnReceiverPostNumber.setText(address.getPostCode());
                    btnReceiverBasic.setText(address.getAddressBasic());

                }
                break;

            default:
                break;
        }
    }


    void refresh() {
        if(TextUtils.isEmpty(apply.getSize())) {
            btnSize.setText("크기");
        } else {
            switch(apply.getSize()) {
                case Apply.SIZE_SMALL:
                    btnSize.setText("소");
                    break;
                case Apply.SIZE_MEDIUM:
                    btnSize.setText("중");
                    break;
                case Apply.SIZE_BIG:
                    btnSize.setText("대");
                    break;
            }
        }

        cbCOD.setChecked(false);
        cbPrepaid.setChecked(false);

        tvCOD.setTextColor(ContextCompat.getColor(contextHelper.getContext(), R.color.gray));
        tvPrepaid.setTextColor(ContextCompat.getColor(contextHelper.getContext(), R.color.gray));

        if(apply.getPay().equals(Apply.PAY_COD)) {
            cbCOD.setChecked(true);
            tvCOD.setTextColor(ContextCompat.getColor(contextHelper.getContext(), R.color.green));

        } else if(apply.getPay().equals(Apply.PAY_PREPAID)) {
            cbPrepaid.setChecked(true);
            tvPrepaid.setTextColor(ContextCompat.getColor(contextHelper.getContext(), R.color.green));
        }
    }

    public void onButtonClicked(View v) {

        switch(v.getId()) {
            case R.id.btnSubmit: {
                varify();
            }
            case R.id.viewCOD:
            case R.id.cbCOD:{
                apply.setPay(Apply.PAY_COD);
                refresh();
            }
            break;
            case R.id.viewPrepaid:
            case R.id.cbPrepaid:{
                apply.setPay(Apply.PAY_PREPAID);
                refresh();
            }
            break;

            case R.id.actionbarImageButton:
                contextHelper.getActivity().finish();
                break;
            case R.id.btnSize:
                new SweetAlertDialog(getContext(), SweetAlertDialog.SELECT_TYPE)
                        .setTitleText("크기 선택")
                        .setArrayValue(new String[]{"소 (크기 50cm/무게 10Kg이하)", "중 (크기 120cm/무게 15Kg이하)", "대 (크기 160cm/무게 25Kg이하)"})
                        .showCloseButton(true)
                        .setSelectListener(new SweetAlertDialog.OnSweetSelectListener() {
                            @Override
                            public void onSelected(SweetAlertDialog sweetAlertDialog, int index) {
                                sweetAlertDialog.dismissWithAnimation();

                                switch (index) {
                                    case 0:
                                        apply.setSize(Apply.SIZE_SMALL);
                                        refresh();
                                        break;
                                    case 1:
                                        apply.setSize(Apply.SIZE_MEDIUM);
                                        refresh();
                                        break;
                                    case 2:
                                        apply.setSize(Apply.SIZE_BIG);
                                        refresh();
                                        break;
                                }
                            }
                        })
                        .show();
                break;
        }
    }


    public void onEvent(PushMessage pushMessage) {
    }



    public void onEditTextChanged(UltraEditText editText) {

    }


    void varify() {
        String errMessage = "";

        if(TextUtils.isEmpty(etTitle.getText().toString())) {
            errMessage = "물품명을 입력해 주세요.";
        } else if(TextUtils.isEmpty(apply.getSize())) {
            errMessage = "크기를 선택해 주세요.";
        } else if(TextUtils.isEmpty(etSenderName.getText().toString())) {
            errMessage = "보내는 사람 이름을 입력해 주세요.";
        }else if(TextUtils.isEmpty(etSenderNumber0.getText().toString()) ||
                TextUtils.isEmpty(etSenderNumber1.getText().toString()) ||
                TextUtils.isEmpty(etSenderNumber2.getText().toString())
                ) {
            errMessage = "보내는 사람 연락처를 입력해 주세요.";
        } else if(TextUtils.isEmpty(btnSenderPostNumber.getText())) {
            errMessage = "보내는 사람 우편번호를 입력해 주세요.";
        } else if(TextUtils.isEmpty(btnSenderBasic.getText())) {
            errMessage = "보내는 사람 기본주소를 입력해 주세요.";
        } else if(TextUtils.isEmpty(etSenderDetail.getText().toString())) {
            errMessage = "보내는 사람 상세주소를 입력해 주세요.";
        } else if(TextUtils.isEmpty(etReceiverName.getText().toString())) {
            errMessage = "받는 사람 이름을 입력해 주세요.";
        }else if(TextUtils.isEmpty(etReceiverNumber0.getText().toString()) ||
                TextUtils.isEmpty(etReceiverNumber1.getText().toString()) ||
                TextUtils.isEmpty(etReceiverNumber2.getText().toString())
                ) {
            errMessage = "받는 사람 연락처를 입력해 주세요.";
        } else if(TextUtils.isEmpty(btnReceiverPostNumber.getText())) {
            errMessage = "받는 사람 우편번호를 입력해 주세요.";
        } else if(TextUtils.isEmpty(btnReceiverBasic.getText())) {
            errMessage = "받는 사람 기본주소를 입력해 주세요.";
        } else if(TextUtils.isEmpty(etReceiverDetail.getText().toString())) {
            errMessage = "받는 사람 상세주소를 입력해 주세요.";
        }

        if(!TextUtils.isEmpty(errMessage)) {
            new SweetAlertDialog(getContext()).setContentText(errMessage).show();
        } else {

            apply.setTitle(etTitle.getText().toString());
            apply.setCaution(etCaution.getText().toString());

            apply.getAddressSender().setName(etSenderName.getText().toString());

            apply.getAddressSender().setNumber0(etSenderNumber0.getText().toString());
            apply.getAddressSender().setNumber1(etSenderNumber1.getText().toString());
            apply.getAddressSender().setNumber2(etSenderNumber2.getText().toString());

            apply.getAddressSender().setPostCode(btnSenderPostNumber.getText());
            apply.getAddressSender().setAddressBasic(btnSenderBasic.getText());
            apply.getAddressSender().setAddressDetail(etSenderDetail.getText().toString());


            apply.getAddressReceiver().setName(etReceiverName.getText().toString());

            apply.getAddressReceiver().setNumber0(etReceiverNumber0.getText().toString());
            apply.getAddressReceiver().setNumber1(etReceiverNumber1.getText().toString());
            apply.getAddressReceiver().setNumber2(etReceiverNumber2.getText().toString());

            apply.getAddressReceiver().setPostCode(btnReceiverPostNumber.getText());
            apply.getAddressReceiver().setAddressBasic(btnReceiverBasic.getText());
            apply.getAddressReceiver().setAddressDetail(etReceiverDetail.getText().toString());

            if(cbSenderRemember.isChecked()) {
                ConfigManager.getInstance(contextHelper).getPreference().putMyAddress(apply.getAddressSender());
            } else {
                ConfigManager.getInstance(contextHelper).getPreference().putMyAddress(null);
            }

            submit();

        }

    }


    void submit() {
        contextHelper.showProgress("");
        final CountDownLatch latchCreate = new CountDownLatch(1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ApplyManager.getInstance(contextHelper).create(
                            new Response.Listener<ApplyData>() {
                                @Override
                                public void onResponse(ApplyData response) {
                                    if (response.isSuccess()) {
                                        //response.shop.patch(contextHelper);

                                        if(response.user != null) {
                                            UserManager.getInstance(contextHelper).setMe(response.user);
                                        }

                                        apply.setId(response.apply.getId());
                                        apply.setOwner(response.apply.getOwner());
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


        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    latchCreate.await();

                    ApplyManager.getInstance(contextHelper).update(
                            apply,
                            new Response.Listener<ApplyData>() {
                                @Override
                                public void onResponse(ApplyData response) {
                                    if (response.isSuccess()) {

                                        contextHelper.hideProgress();

                                        EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_CHANGE_ME).setObject(null));

                                        Toast.makeText(contextHelper.getActivity(), "접수 하였습니다.", Toast.LENGTH_SHORT).show();
                                        contextHelper.getActivity().finish();

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
    }
}



