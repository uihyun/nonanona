package com.nuums.nuums.fragment.misc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.android.gms.maps.model.LatLng;
import com.nuums.nuums.R;
import com.nuums.nuums.adapter.AskAdapter;
import com.nuums.nuums.adapter.PostcodeAdapter;
import com.nuums.nuums.model.apply.ApplyData;
import com.nuums.nuums.model.apply.ApplyListData;
import com.nuums.nuums.model.apply.ApplyManager;
import com.nuums.nuums.model.ask.Ask;
import com.nuums.nuums.model.ask.AskData;
import com.nuums.nuums.model.ask.AskList;
import com.nuums.nuums.model.ask.AskListData;
import com.nuums.nuums.model.ask.AskManager;
import com.nuums.nuums.model.nanum.Nanum;
import com.yongtrim.lib.fragment.ListFragment;
import com.yongtrim.lib.log.Logger;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.model.list.List;
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import de.greenrobot.event.EventBus;

/**
 * nuums / com.nuums.nuums.fragment.misc
 * <p/>
 * Created by yongtrim.com on 16. 1. 22..
 */
public class AskFragment extends ListFragment {
    final String TAG = "AskFragment";

    AskAdapter askAdapter;
    AskList askList;

    EditText etSearch;
    TextView tvTitleNodata;

    Ask ask;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        contextHelper.getActivity().setupActionBar("나눔요청");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ask, container, false);

        UltraListView listView = (UltraListView)view.findViewById(R.id.listView);
        askAdapter = new AskAdapter(contextHelper);

        if(askList == null) {
            askList = new AskList();
        }

        askAdapter.setData(askList.getAsks());
        listView.setAdapter(askAdapter);

        setupView(listView, view.findViewById(R.id.viewNodata), view.findViewById(R.id.swipeRefreshLayout), askAdapter);
        setAddedLayer(null, null);
        setListInfo(askList);

        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

        tvTitleNodata = (TextView) view.findViewById(R.id.tvTitleNodata);
        etSearch = (EditText) view.findViewById(R.id.etSearch);
        etSearch.setImeOptions(EditorInfo.IME_ACTION_SEND);
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    send(v.getText().toString());
                    return true;
                }
                return false;
            }
        });

        etSearch.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});

        UltraButton btnEnter = (UltraButton) view.findViewById(R.id.btnEnter);
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send(etSearch.getText().toString());

            }
        });


        tvTitleNodata.setText("로딩중...");

        loadList(null);

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
        switch(pushMessage.getActionCode()) {
            case PushMessage.ACTIONCODE_ADDED_ASK_OTHER:
                loadList(null);
                break;
        }
    }

    void send(final String message) {
        if(TextUtils.isEmpty(message))
            return;

        try {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        } catch(Exception e) {

        }

        contextHelper.showProgress("");
        final CountDownLatch latchCreate = new CountDownLatch(1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AskManager.getInstance(contextHelper).create(
                            new Response.Listener<AskData>() {
                                @Override
                                public void onResponse(AskData response) {
                                    if (response.isSuccess()) {
                                        ask = response.ask;
                                        ask.setMessage(message);
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

                    AskManager.getInstance(contextHelper).update(
                            ask,
                            new Response.Listener<AskData>() {
                                @Override
                                public void onResponse(AskData response) {
                                    if (response.isSuccess()) {
                                        Toast.makeText(contextHelper.getActivity(), "전송 하였습니다.", Toast.LENGTH_SHORT).show();
                                        loadList(null);
                                        etSearch.setText("");
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

    public void loadList(final List list) {
        super.loadList(list);

        AskManager.getInstance(contextHelper).find(
                list == null ? 1 : list.getPages().getNext(),
                new Response.Listener<AskListData>() {
                    @Override
                    public void onResponse(AskListData response) {
                        Logger.debug(TAG, "find() | result = " + response.toString());

                        tvTitleNodata.setText("나눔 요청이 없습니다.");

                        if (list == null) {
                            askList.getAsks().clear();
                        }
                        askList.getAsks().addAll(response.getAskList().getAsks());
                        askAdapter.setData(askList.getAsks());
                        postLoad(response.getAskList());
                        contextHelper.hideProgress();

                        if(response.user != null) {
                            UserManager.getInstance(contextHelper).setMe(response.user);
                            EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_CHANGE_ME).setObject(response.user));
                        }
                    }
                },
                null
        );
    }


}



