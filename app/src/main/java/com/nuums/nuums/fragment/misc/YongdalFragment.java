package com.nuums.nuums.fragment.misc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.TextUtils;
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
import com.nuums.nuums.model.apply.Apply;
import com.nuums.nuums.model.apply.ApplyData;
import com.nuums.nuums.model.apply.ApplyManager;
import com.nuums.nuums.model.chat.Address;
import com.nuums.nuums.model.misc.CommentManager;
import com.nuums.nuums.model.nanum.Nanum;
import com.nuums.nuums.model.review.ReviewData;
import com.nuums.nuums.model.yongdal.Yongdal;
import com.nuums.nuums.model.yongdal.YongdalData;
import com.nuums.nuums.model.yongdal.YongdalManager;
import com.yongtrim.lib.activity.ABaseFragmentAcitivty;
import com.yongtrim.lib.fragment.ABaseFragment;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.model.config.ConfigManager;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.ui.UltraButton;
import com.yongtrim.lib.ui.UltraEditText;
import com.yongtrim.lib.ui.sweetalert.SweetAlertDialog;
import com.yongtrim.lib.util.MiscUtil;

import java.util.concurrent.CountDownLatch;

import de.greenrobot.event.EventBus;

/**
 * nuums / com.nuums.nuums.fragment.misc
 * <p/>
 * Created by yongtrim.com on 16. 1. 31..
 */
public class YongdalFragment extends ABaseFragment implements UltraEditText.OnChangeListener {
    private final String TAG = getClass().getSimpleName();

    UltraButton btnStart;
    UltraButton btnEnd;

    View viewTypeNormal;
    CheckBox cbTypeNormal;
    TextView tvTypeNormal;

    View viewTypeRapid;
    CheckBox cbTypeRapid;
    TextView tvTypeRapid;

    View viewMethodBike;
    CheckBox cbMethodBike;
    TextView tvMethodBike;

    View viewMethodSmallTruck;
    CheckBox cbMethodSmallTruck;
    TextView tvMethodSmallTruck;

    View viewMethod1TonTruck;
    CheckBox cbMethod1TonTruck;
    TextView tvMethod1TonTruck;


    UltraEditText etStuff;
    UltraEditText etWeight;
    UltraEditText etCount;


    UltraButton btnPrice;

    Yongdal yongdal;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        contextHelper.getActivity().setupActionBar("용달문의");
        contextHelper.getActivity().setBackButtonVisibility(false);
        contextHelper.getActivity().setImageButtonAndVisiable(R.drawable.del);

        yongdal = new Yongdal();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_yongdal, container, false);

        btnStart = (UltraButton)view.findViewById(R.id.btnStart);
        btnEnd = (UltraButton)view.findViewById(R.id.btnEnd);

        viewTypeNormal = view.findViewById(R.id.viewTypeNormal);
        cbTypeNormal = (CheckBox)view.findViewById(R.id.cbTypeNormal);
        tvTypeNormal = (TextView)view.findViewById(R.id.tvTypeNormal);


        viewTypeRapid = view.findViewById(R.id.viewTypeRapid);
        cbTypeRapid = (CheckBox)view.findViewById(R.id.cbTypeRapid);
        tvTypeRapid = (TextView)view.findViewById(R.id.tvTypeRapid);

        viewMethodBike = view.findViewById(R.id.viewMethodBike);
        cbMethodBike = (CheckBox)view.findViewById(R.id.cbMethodBike);
        tvMethodBike = (TextView)view.findViewById(R.id.tvMethodBike);

        viewMethodSmallTruck = view.findViewById(R.id.viewMethodSmallTruck);
        cbMethodSmallTruck = (CheckBox)view.findViewById(R.id.cbMethodSmallTruck);
        tvMethodSmallTruck = (TextView)view.findViewById(R.id.tvMethodSmallTruck);

        viewMethod1TonTruck = view.findViewById(R.id.viewMethod1TonTruck);
        cbMethod1TonTruck = (CheckBox)view.findViewById(R.id.cbMethod1TonTruck);
        tvMethod1TonTruck = (TextView)view.findViewById(R.id.tvMethod1TonTruck);


        etStuff = (UltraEditText)view.findViewById(R.id.etStuff);
        etWeight = (UltraEditText)view.findViewById(R.id.etWeight);
        etWeight.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);


        etCount = (UltraEditText)view.findViewById(R.id.etCount);
        etCount.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        btnPrice = (UltraButton)view.findViewById(R.id.btnPrice);
        btnPrice.setVisibility(View.INVISIBLE);


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
                    yongdal = Yongdal.getYongdal(data.getStringExtra("yongdal"));
                    refresh();
                }
                break;

            default:
                break;
        }
    }


    void refresh() {

        if(!TextUtils.isEmpty(yongdal.getAddressStartFake())) {
            btnStart.setText(yongdal.getAddressFakeShorter(true));
        }

        if(!TextUtils.isEmpty(yongdal.getAddressEndFake())) {
            btnEnd.setText(yongdal.getAddressFakeShorter(false));
        }

        cbTypeNormal.setChecked(false);
        cbTypeRapid.setChecked(false);
        tvTypeNormal.setTextColor(ContextCompat.getColor(contextHelper.getContext(), R.color.gray));
        tvTypeRapid.setTextColor(ContextCompat.getColor(contextHelper.getContext(), R.color.gray));

        if(yongdal.getType().equals(Yongdal.TYPE_NORMAL)) {
            cbTypeNormal.setChecked(true);
            tvTypeNormal.setTextColor(ContextCompat.getColor(contextHelper.getContext(), R.color.green));

        } else if(yongdal.getType().equals(Yongdal.TYPE_RAPID)) {
            cbTypeRapid.setChecked(true);
            tvTypeRapid.setTextColor(ContextCompat.getColor(contextHelper.getContext(), R.color.green));
        }

        cbMethodBike.setChecked(false);
        cbMethodSmallTruck.setChecked(false);
        cbMethod1TonTruck.setChecked(false);
        tvMethodBike.setTextColor(ContextCompat.getColor(contextHelper.getContext(), R.color.gray));
        tvMethodSmallTruck.setTextColor(ContextCompat.getColor(contextHelper.getContext(), R.color.gray));
        tvMethod1TonTruck.setTextColor(ContextCompat.getColor(contextHelper.getContext(), R.color.gray));

        if(yongdal.getMethod().equals(Yongdal.METHOD_BIKE)) {
            cbMethodBike.setChecked(true);
            tvMethodBike.setTextColor(ContextCompat.getColor(contextHelper.getContext(), R.color.green));

        } else if(yongdal.getMethod().equals(Yongdal.METHOD_SMALLSTRUCK)) {
            cbMethodSmallTruck.setChecked(true);
            tvMethodSmallTruck.setTextColor(ContextCompat.getColor(contextHelper.getContext(), R.color.green));
        } else if(yongdal.getMethod().equals(Yongdal.METHOD_1TONTRUCK)) {
            cbMethod1TonTruck.setChecked(true);
            tvMethod1TonTruck.setTextColor(ContextCompat.getColor(contextHelper.getContext(), R.color.green));
        }

    }

    public void onButtonClicked(View v) {

        switch(v.getId()) {
            case R.id.btnStart: {
                Intent i = new Intent(getContext(), BaseActivity.class);
                i.putExtra("activityCode", BaseActivity.ActivityCode.POSTCODE.ordinal());
                i.putExtra("yongdal", yongdal.toString());
                i.putExtra("isStart", true);

                contextHelper.getActivity().startActivityForResult(i, ABaseFragmentAcitivty.REQUEST_POSTCODE);
            }
            break;
            case R.id.btnEnd: {
                Intent i = new Intent(getContext(), BaseActivity.class);
                i.putExtra("activityCode", BaseActivity.ActivityCode.POSTCODE.ordinal());
                i.putExtra("yongdal", yongdal.toString());
                i.putExtra("isStart", false);
                contextHelper.getActivity().startActivityForResult(i, ABaseFragmentAcitivty.REQUEST_POSTCODE);
            }
                break;
            case R.id.btnInquiry: {
                inquiry();
            }
                break;
            case R.id.btnSubmit: {
                varify();
            }
            case R.id.viewTypeNormal:
            case R.id.cbTypeNormal:{
                yongdal.setType(Yongdal.TYPE_NORMAL);
                refresh();
            }
            break;
            case R.id.viewTypeRapid:
            case R.id.cbTypeRapid:{
                yongdal.setType(Yongdal.TYPE_RAPID);
                refresh();
            }
            break;

            case R.id.viewMethodBike:
            case R.id.cbMethodBike:{
                yongdal.setMethod(Yongdal.METHOD_BIKE);
                refresh();
            }
            break;
            case R.id.viewMethodSmallTruck:
            case R.id.cbMethodSmallTruck:{
                yongdal.setMethod(Yongdal.METHOD_SMALLSTRUCK);
                refresh();
            }
            break;
            case R.id.viewMethod1TonTruck:
            case R.id.cbMethod1TonTruck:{
                yongdal.setMethod(Yongdal.METHOD_1TONTRUCK);
                refresh();
            }
            break;

            case R.id.actionbarImageButton:
                contextHelper.getActivity().finish();
                break;
        }
    }


    public void onEvent(PushMessage pushMessage) {
    }



    public void onEditTextChanged(UltraEditText editText) {

    }

    void inquiry() {
        String errMessage = "";

        if(TextUtils.isEmpty(yongdal.getAddressStartFake())) {
            errMessage = "출발지역을 입력해 주세요.";
        } else if(TextUtils.isEmpty(yongdal.getAddressEndFake())) {
            errMessage = "도착지역을 선택해 주세요.";
        } else if(TextUtils.isEmpty(etWeight.getText().toString())) {
            errMessage = "무게를 입력해 주세요.";
        } else if(TextUtils.isEmpty(etCount.getText().toString())) {
            errMessage = "수량을 입력해 주세요.";
        } else if(TextUtils.isEmpty(etStuff.getText().toString())) {
            errMessage = "용달물품을 입력해 주세요.";
        }


        if(!TextUtils.isEmpty(errMessage)) {
            new SweetAlertDialog(getContext()).setContentText(errMessage).show();
        } else {
            yongdal.setWeight(etWeight.getText().toString());
            yongdal.setCount(etCount.getText().toString());
            yongdal.setDistance();

            contextHelper.showProgress(null);
            YongdalManager.getInstance(contextHelper).calPrice(
                    yongdal,
                    new Response.Listener<YongdalData>() {
                        @Override
                        public void onResponse(YongdalData response) {
                            if (response.isSuccess()) {
                                contextHelper.hideProgress();

                                btnPrice.setText(MiscUtil.toMoneyStyle("" + response.price));
                                btnPrice.setVisibility(View.VISIBLE);

                            } else {
                                new SweetAlertDialog(getContext())
                                        .setContentText(response.getErrorMessage())
                                        .show();
                            }
                        }
                    },
                    null
            );
        }
    }

    void varify() {
        String errMessage = "";

        if(TextUtils.isEmpty(yongdal.getAddressStartFake())) {
            errMessage = "출발지역을 입력해 주세요.";
        } else if(TextUtils.isEmpty(yongdal.getAddressEndFake())) {
            errMessage = "도착지역을 선택해 주세요.";
        } else if(TextUtils.isEmpty(etWeight.getText().toString())) {
            errMessage = "무게를 입력해 주세요.";
        } else if(TextUtils.isEmpty(etCount.getText().toString())) {
            errMessage = "수량을 입력해 주세요.";
        } else if(TextUtils.isEmpty(etStuff.getText().toString())) {
            errMessage = "용달물품을 입력해 주세요.";
        }

        if(!TextUtils.isEmpty(errMessage)) {
            new SweetAlertDialog(getContext()).setContentText(errMessage).show();
        } else {
            yongdal.setWeight(etWeight.getText().toString());
            yongdal.setCount(etCount.getText().toString());
            yongdal.setDistance();
            yongdal.setMessage(etStuff.getText().toString());

            new SweetAlertDialog(contextHelper.getContext(), SweetAlertDialog.EDITTEXT_TYPE)
                    .setTitleText("연락받으실 번호 입력")
                    .setEditTextValue("", null, InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL, 0)
                    .showCancelButton(true)
                    .setConfirmText("접수")
                    .setCancelText("닫기")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {

                            if(TextUtils.isEmpty(sDialog.getEditTextValue())) {
                                new SweetAlertDialog(getContext()).setContentText("연락받으실 번호를 입력해 주세요").show();
                            } else {
                                sDialog.dismissWithAnimation();

                                yongdal.setPhoneNumber(sDialog.getEditTextValue());
                                submit();
                            }
                        }
                    })
                    .show();
        }
    }


    void submit() {
        contextHelper.showProgress("");
        final CountDownLatch latchCreate = new CountDownLatch(1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    YongdalManager.getInstance(contextHelper).create(
                            new Response.Listener<YongdalData>() {
                                @Override
                                public void onResponse(YongdalData response) {
                                    if (response.isSuccess()) {

                                        yongdal.setId(response.yongdal.getId());
                                        yongdal.setOwner(response.yongdal.getOwner());
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

                    YongdalManager.getInstance(contextHelper).update(
                            yongdal,
                            new Response.Listener<YongdalData>() {
                                @Override
                                public void onResponse(YongdalData response) {
                                    if (response.isSuccess()) {

                                        contextHelper.hideProgress();

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


