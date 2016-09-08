package com.nuums.nuums.fragment.nanum;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.nuums.nuums.R;
import com.nuums.nuums.activity.BaseActivity;
import com.nuums.nuums.adapter.PhotoPagerAdapter;
import com.nuums.nuums.model.nanum.Nanum;
import com.yongtrim.lib.activity.ABaseFragmentAcitivty;
import com.yongtrim.lib.fragment.ABaseFragment;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.model.config.ConfigManager;
import com.yongtrim.lib.model.photo.Photo;
import com.yongtrim.lib.model.photo.PhotoManager;
import com.yongtrim.lib.ui.LimitedEditText;
import com.yongtrim.lib.ui.autoscrollviewpager.AutoScrollViewPager;
import com.yongtrim.lib.ui.sweetalert.SweetAlertDialog;
import com.yongtrim.lib.util.UIUtil;

import java.util.ArrayList;

/**
 * nuums / com.nuums.nuums.fragment.nanum
 * <p/>
 * Created by yongtrim.com on 15. 12. 21..
 */
public class DiscriptionEditFragment extends ABaseFragment {
    private final String TAG = getClass().getSimpleName();

    Nanum nanum;

    EditText etDescription;
    TextView tvStatus;

    int MAX_CHARACTER = 1000;

    final String message = "나눔물건:\n" +
            "거래지역:\n" +
            "선정기준:\n" +
            "파손여부:";

    String orinalDescription;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        contextHelper.getActivity().setupActionBar("상세설명");
        contextHelper.getActivity().setImageButtonAndVisiable(R.drawable.tresh);
        nanum = Nanum.getNanum(contextHelper.getActivity().getIntent().getStringExtra("nanum"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_descriptionedit, container, false);

        tvStatus = (TextView)view.findViewById(R.id.tvStatus);

        etDescription = (EditText)view.findViewById(R.id.etDescription);
        etDescription.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_CHARACTER)});

        etDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                refresh();
            }
        });

        if(TextUtils.isEmpty(nanum.getDescription())) {
            etDescription.setText(message);
            orinalDescription = message;
        } else {
            etDescription.setText(nanum.getDescription());
            orinalDescription = nanum.getDescription();
        }

        TextView tvCaution = (TextView)view.findViewById(R.id.tvCaution);
        tvCaution.setText(ConfigManager.getInstance(contextHelper).getConfigHello().getParams().getNanumCaution());
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

    public void onButtonClicked(View v) {

        switch(v.getId()) {
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
                                etDescription.setText(message);
                                refresh();
                            }
                        })
                        .show();
                break;
            case R.id.btnSubmit:
                nanum.setDescription(etDescription.getText().toString());
                Intent intent = new Intent();
                intent.putExtra("nanum", nanum.toString());
                contextHelper.getActivity().setResult(Activity.RESULT_OK, intent);
                contextHelper.getActivity().finish();
                break;
        }
    }

    public void refresh() {
        tvStatus.setText(etDescription.getText().toString().length() + "/" + MAX_CHARACTER);
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            if(orinalDescription.equals(etDescription.getText().toString())) {
                return false;
            } else {
                new SweetAlertDialog(getContext())
                        .setContentText("입력한 정보가 모두 지워집니다.\n이전화면으로 돌아가시겠습니까?")
                        .showCancelButton(true)
                        .setConfirmText("예")
                        .setCancelText("아니오")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(final SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                                contextHelper.getActivity().finish();
                            }
                        })
                        .show();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}



