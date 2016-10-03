package com.nuums.nuums.fragment.misc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.android.gms.maps.model.LatLng;
import com.nuums.nuums.R;
import com.nuums.nuums.adapter.PostcodeAdapter;
import com.nuums.nuums.model.chat.TalkData;
import com.nuums.nuums.model.chat.TalkManager;
import com.nuums.nuums.model.nanum.Nanum;
import com.nuums.nuums.model.user.NsUser;
import com.nuums.nuums.model.yongdal.Yongdal;
import com.yongtrim.lib.fragment.ListFragment;
import com.yongtrim.lib.log.Logger;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.model.config.ConfigManager;
import com.yongtrim.lib.model.misc.LocationToAddressTask;
import com.yongtrim.lib.model.postcode.PostCode;
import com.yongtrim.lib.model.postcode.PostCodeList;
import com.yongtrim.lib.model.postcode.PostCodeTask;
import com.yongtrim.lib.model.user.LocationTask;
import com.yongtrim.lib.model.user.UserData;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.ui.UltraButton;
import com.yongtrim.lib.ui.UltraListView;
import com.yongtrim.lib.ui.sweetalert.SweetAlertDialog;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import de.greenrobot.event.EventBus;

/**
 * nuums / com.nuums.nuums.fragment.misc
 * <p/>
 * Created by Uihyun on 15. 12. 20..
 */
public class PostcodeFragment extends ListFragment {
    final String TAG = "PostcodeFragment";

    PostcodeAdapter postcodeAdapter;
    PostCodeList postCodeList;

    EditText etSearch;
    TextView tvTitleNodata;

    String keyword;

    Nanum nanum;

    Yongdal yongdal;
    boolean isStart;
    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        contextHelper.getActivity().setupActionBar("지역검색");

        if (contextHelper.getActivity().getIntent().hasExtra("nanum")) {
            nanum = Nanum.getNanum(contextHelper.getActivity().getIntent().getStringExtra("nanum"));
        } else if (contextHelper.getActivity().getIntent().hasExtra("yongdal")) {
            yongdal = Yongdal.getYongdal(contextHelper.getActivity().getIntent().getStringExtra("yongdal"));
            isStart = contextHelper.getActivity().getIntent().getBooleanExtra("isStart", false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        View view = inflater.inflate(R.layout.fragment_postcode, container, false);

        UltraListView listView = (UltraListView)view.findViewById(R.id.listView);
        postcodeAdapter = new PostcodeAdapter();

        if(postCodeList == null) {
            postCodeList = new PostCodeList();
        }

        postcodeAdapter.setData(postCodeList.getPostCodes());
        listView.setAdapter(postcodeAdapter);

        setupView(listView, view.findViewById(R.id.viewNodata), view.findViewById(R.id.swipeRefreshLayout), postcodeAdapter);
        setAddedLayer(null, null);
        setListInfo(postCodeList);

        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final PostCode postCode = postCodeList.getPostCodes().get((int) position);

                if(nanum != null) {
                    new SweetAlertDialog(getContext())
                            .setContentText("\'" + postCode.oldAddress + "\'(을)를 거래지역으로 등록합니다.")
                            .showCancelButton(true)
                            .setConfirmText("예")
                            .setCancelText("아니오")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(final SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();

                                    nanum.setAddressNew(postCode.newAddress);
                                    nanum.setAddressOld(postCode.oldAddress);
                                    nanum.setAddressPostcode(postCode.code);
                                    processLocation(postCode);

                                }
                            })
                            .show();
                } else if(yongdal != null) {
                    if(isStart)
                        yongdal.setAddressStart(postCode.code + "/" + postCode.newAddress);
                    else
                        yongdal.setAddressEnd(postCode.code + "/" + postCode.newAddress);
                    processLocation(postCode);
                } else {
                    String newAddress = "도로명 주소" + "\n" +
                            "(" + postCode.code + ")" + postCode.newAddress;

                    String oldAddress = "지번 주소" + "\n" +
                            "(" + postCode.code + ")" + postCode.oldAddress;

                    new SweetAlertDialog(getContext(), SweetAlertDialog.SELECT_TYPE)
                            .setTitleText("선택해 주세요")
                            .showCloseButton(true)
                            .setArrayValue(new String[]{newAddress, oldAddress})
                            .setSelectListener(new SweetAlertDialog.OnSweetSelectListener() {
                                @Override
                                public void onSelected(SweetAlertDialog sweetAlertDialog, int index) {
                                    sweetAlertDialog.dismissWithAnimation();

                                    Intent intent = new Intent();

                                    intent.putExtra("postcode", postCode.code);
                                    intent.putExtra("basicaddress", index == 0 ? postCode.newAddress : postCode.oldAddress);
                                    contextHelper.getActivity().setResult(Activity.RESULT_OK, intent);
                                    contextHelper.getActivity().finish();

                                }
                            })
                            .show();
                }
            }
        });

        tvTitleNodata = (TextView) view.findViewById(R.id.tvTitleNodata);
        etSearch = (EditText) view.findViewById(R.id.etSearch);
        etSearch.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    find(v.getText().toString());
                    return true;
                }
                return false;
            }
        });

        UltraButton btnEnter = (UltraButton) view.findViewById(R.id.btnEnter);
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                find(etSearch.getText().toString());

            }
        });

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

    @Override
    public void onPause() {
        super.onPause();
    }


    public void onButtonClicked(View v) {

        switch (v.getId()) {

        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    public void onEvent(PushMessage pushMessage) {

    }

    void find(String keyword) {
        if(TextUtils.isEmpty(keyword))
            return;

        try {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        } catch(Exception e) {

        }
        this.keyword = keyword;

        loadList(null);
    }

    public void loadList(final com.yongtrim.lib.model.list.List list) {
        super.loadList(list);

        PostCodeTask postCodeTask = new PostCodeTask(keyword, list == null ? 1 : list.getPages().getNext(), new PostCodeTask.AServiceCallback() {
            @Override
            public void success(PostCodeList result) {

                tvTitleNodata.setText("검색결과가 없습니다.");

                if (list == null) {
                    postCodeList.getPostCodes().clear();
                }

                postCodeList.getPostCodes().addAll(result.getPostCodes());

                postcodeAdapter.setData(postCodeList.getPostCodes());

                postLoad(result);

            }

            @Override
            public void failure(int errorCode, String message) {
                contextHelper.hideProgress();
                //helper.checkServerError(errorCode, message);
            }
        });
        postCodeTask.execute();
    }


    void processLocation(PostCode postCode) {

        contextHelper.showProgress(null);
        final CountDownLatch latchLocation = new CountDownLatch(1);

        LocationTask location = new LocationTask(postCode.oldAddress, new LocationTask.AServiceCallback() {
            @Override
            public void success(Object object) {
                Map<String, Object> map = (HashMap<String, Object>) object;
                LatLng location = (LatLng) map.get("location");
                if(nanum != null)
                    nanum.setLocation(location);
                else if(yongdal != null) {
                    yongdal.setLocation(location, isStart);
                }
                latchLocation.countDown();
            }

            @Override
            public void failure(int errorCode, String message) {
                contextHelper.hideProgress();
                new SweetAlertDialog(contextHelper.getContext()).setContentText("실패하였습니다.").show();
            }
        });

        location.execute();

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    latchLocation.await();

                    LatLng location = null;

                    if(nanum != null) {
                        location = nanum.getLocation();
                    } else if(yongdal != null) {
                        location = yongdal.getLocation(isStart);
                    }

                    LocationToAddressTask locationToAddressTask = new LocationToAddressTask(location.latitude, location.longitude, new LocationToAddressTask.AServiceCallback() {
                        @Override
                        public void success(Object object) {
                            Map<String, Object> map = (HashMap<String, Object>) object;

                            String seqAddress = (String)map.get("addressShort");

                            if(nanum != null)
                                nanum.setAddressFake(seqAddress);
                            else if(yongdal != null) {
                                if(isStart)
                                    yongdal.setAddressStartFake(seqAddress);
                                else
                                    yongdal.setAddressEndFake(seqAddress);
                            }
                            contextHelper.getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    contextHelper.hideProgress();
                                    Intent intent = new Intent();

                                    if(nanum != null)
                                        intent.putExtra("nanum", nanum.toString());

                                    else if(yongdal != null) {
                                        intent.putExtra("yongdal", yongdal.toString());
                                    }


                                    contextHelper.getActivity().setResult(Activity.RESULT_OK, intent);
                                    contextHelper.getActivity().finish();
                                }
                            });
                        }

                        @Override
                        public void failure(int errorCode, String message) {

                        }
                    });

                    locationToAddressTask.execute();

                } catch (Exception e) {

                }
            }
        }).start();
    }
}


