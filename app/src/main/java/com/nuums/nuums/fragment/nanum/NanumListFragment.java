package com.nuums.nuums.fragment.nanum;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.google.android.gms.maps.model.LatLng;
import com.nuums.nuums.R;
import com.nuums.nuums.activity.BaseActivity;
import com.nuums.nuums.adapter.NanumAdapter;
import com.nuums.nuums.fragment.mypage.MypageMainFragment;
import com.nuums.nuums.model.nanum.Nanum;
import com.nuums.nuums.model.nanum.NanumList;
import com.nuums.nuums.model.nanum.NanumListData;
import com.nuums.nuums.model.nanum.NanumManager;
import com.nuums.nuums.model.user.NsUser;
import com.nuums.nuums.view.HeaderView;
import com.nuums.nuums.view.MypageView;
import com.yongtrim.lib.fragment.ListFragment;
import com.yongtrim.lib.log.Logger;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.model.config.ConfigManager;
import com.yongtrim.lib.model.list.List;
import com.yongtrim.lib.model.list.Page;
import com.yongtrim.lib.model.location.GPSTracker;
import com.yongtrim.lib.model.location.LocationManager;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.ui.PagerSlidingTabStrip2;
import com.yongtrim.lib.ui.UltraButton;
import com.yongtrim.lib.ui.UltraEditText;
import com.yongtrim.lib.ui.UltraListView;
import com.yongtrim.lib.ui.sweetalert.SweetAlertDialog;
import com.yongtrim.lib.util.PixelUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;


/**
 * nuums / com.nuums.nuums.fragment.nanum
 * <p/>
 * Created by Uihyun on 15. 12. 12..
 */
public class NanumListFragment extends ListFragment implements GPSTracker.LocationListener {
    private final String TAG = getClass().getSimpleName();
    private static final int REQUEST_CALL = 1;

    NanumAdapter nanumAdapter;
    NanumList nanumList;
    View mainView;
    TextView tvTitleNodata;

    Handler timerHandler;
    Runnable timerRunnable;

    final int SORT_TIME = 0;
    final int SORT_DISTANCE = 1;
    final int SORT_BOOKMARK = 2;

    int sort = SORT_TIME;

    LatLng curLocation;

    UltraButton btnNew;

    public static int LISTTYPE_ALL = 0;
    public static int LISTTYPE_MINE = 1;
    public static int LISTTYPE_APPLY = 2;
    public static int LISTTYPE_WON = 3;

    int listType = LISTTYPE_ALL;
    public MypageView mypageView;

    boolean isSearch;

    PagerSlidingTabStrip2 tabbar;

    UltraEditText etSearch;
    String keyword;


    public static NanumListFragment create(int listType) {
        NanumListFragment fragment = new NanumListFragment();
        fragment.listType = listType;
        return fragment;
    }



//    public NanumListFragment setPager(ViewPager pager) {
//        this.pager = pager;
//        return this;
//    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("listType", listType);
    }



    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        if(saveInstanceState != null) {
            listType = saveInstanceState.getInt("listType");
        }
        isSearch = contextHelper.getActivity().getIntent().getBooleanExtra("isSearch", false);

        timerHandler = new Handler();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if(nanumAdapter != null)
                    nanumAdapter.notifyDataSetChanged();
                timerHandler.postDelayed(this, 60000); //run every minute
            }
        };


        if(isSearch) {
            etSearch = contextHelper.getActivity().setupActionBarSearch();
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        mainView = inflater.inflate(R.layout.fragment_nanumlist, container, false);

        btnNew = (UltraButton)mainView.findViewById(R.id.btnNew);

        UltraListView listView = (UltraListView)mainView.findViewById(R.id.listView);
        nanumAdapter = new NanumAdapter(contextHelper);

        if(nanumList == null) {
            nanumList = new NanumList();
        }

        nanumAdapter.setData(nanumList.getNanums());
        listView.setAdapter(nanumAdapter);

        setupView(listView, mainView.findViewById(R.id.viewNodata), mainView.findViewById(R.id.swipeRefreshLayout), nanumAdapter);
        setListInfo(nanumList);
        setAddedLayer(mainView.findViewById(R.id.layerTop), mainView.findViewById(R.id.layerBottom));

        tvTitleNodata = (TextView)mainView.findViewById(R.id.tvTitleNodata);

        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(id < 0 || id >= nanumList.getNanums().size())
                    return;

                Nanum nanumSelected = nanumList.getNanums().get((int) id);
                Intent i = new Intent(getContext(), BaseActivity.class);
                i.putExtra("activityCode", BaseActivity.ActivityCode.NANUMVIEWER.ordinal());
                i.putExtra("nanum", nanumSelected.toString());

                getContext().startActivity(i);
            }
        });

        HeaderView headerView = new HeaderView(getContext());

        if(isSearch == true) {
            mainView.findViewById(R.id.layerBottom).setVisibility(View.GONE);
        }
        else if(listType == LISTTYPE_ALL) {
            mainView.findViewById(R.id.layerSort).setVisibility(View.VISIBLE);
        } else {
            mainView.findViewById(R.id.layerTabbar).setVisibility(View.VISIBLE);
            mainView.findViewById(R.id.layerBottom).setVisibility(View.GONE);
            mypageView = new MypageView(contextHelper);
            headerView.addView(mypageView);
            headerView.addView(PixelUtil.dpToPx(getContext(), 60));

            tabbar = (PagerSlidingTabStrip2)mainView.findViewById(R.id.tabbar);
            tabbar.setContextHelper(contextHelper);
            tabbar.setVisibility(View.VISIBLE);
            tabbar.setViewPager(((MypageMainFragment)getParentFragment()).pager);
        }

        setHeader(headerView);

        refreshTop();
        refreshBottom();


        if(isSearch) {
            tvTitleNodata.setText("검색어로 나눔을 찾아보세요.");

        } else {
            tvTitleNodata.setText("로딩중...");
            loadList(null);
        }
        curLocation = LocationManager.getInstance(contextHelper).getCurrentLocation(NanumListFragment.this);

        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(mypageView != null)
            mypageView.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onResume() {
        timerHandler.postDelayed(timerRunnable, 500);
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
        timerHandler.removeCallbacks(timerRunnable);
        super.onPause();
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (!visible) {
            if(listView != null)
                listView.setSelection(0);
        }
    }

    void find(String keyword) {
        if(TextUtils.isEmpty(keyword))
            return;

        try {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        } catch(Exception e) {

        }

        contextHelper.showProgress(null);
        this.keyword = keyword;

        loadList(null);
    }


    public void refreshTop() {
        if(listType != LISTTYPE_ALL)
            return;
        if(isSearch)
            return;

        UltraButton btnZim = (UltraButton)mainView.findViewById(R.id.btnZim);
        UltraButton btnDistance = (UltraButton)mainView.findViewById(R.id.btnDistance);
        UltraButton btnTime = (UltraButton)mainView.findViewById(R.id.btnTime);

        btnZim.setTextColor(ContextCompat.getColor(contextHelper.getContext(), R.color.white));
        btnZim.setTextStyleColor(Typeface.DEFAULT);

        btnDistance.setTextColor(ContextCompat.getColor(contextHelper.getContext(), R.color.white));
        btnDistance.setTextStyleColor(Typeface.DEFAULT);

        btnTime.setTextColor(ContextCompat.getColor(contextHelper.getContext(), R.color.white));
        btnTime.setTextStyleColor(Typeface.DEFAULT);


        if(sort == SORT_TIME) {
            btnTime.setTextColor(ContextCompat.getColor(contextHelper.getContext(), R.color.green));
            //btnTime.setTextStyleColor(Typeface.DEFAULT_BOLD);

        } else if(sort == SORT_DISTANCE) {
            btnDistance.setTextColor(ContextCompat.getColor(contextHelper.getContext(), R.color.green));
            //btnDistance.setTextStyleColor(Typeface.DEFAULT_BOLD);

        } else {
            btnZim.setTextColor(ContextCompat.getColor(contextHelper.getContext(), R.color.green));
            //btnBookmark.setTextStyleColor(Typeface.DEFAULT_BOLD);
        }
    }

    public void refreshBottom() {
    }


    public void onEvent(final PushMessage pushMessage) {
        switch(pushMessage.getActionCode()) {
            case PushMessage.ACTIONCODE_CHANGE_ME:
                if(tabbar != null) {
                    tabbar.notifyDataSetChanged();
                    loadList(null);
                }

                if(mypageView != null)
                    mypageView.refresh();
                break;
            case PushMessage.ACTIONCODE_ADDED_NANUM:
            case PushMessage.ACTIONCODE_DELETE_NANUM:{
                try {
                    contextHelper.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadList(null);

                        }
                    });

                } catch(Exception e) {

                }
            }
            break;
            case PushMessage.ACTIONCODE_ADDED_NANUM_OTHER:
                if(listType == LISTTYPE_ALL && sort == SORT_TIME) {
                    contextHelper.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            btnNew.setVisibility(View.VISIBLE);
                        }
                    });
                }
                break;
            case PushMessage.ACTIONCODE_CHANGE_NANUM: {
                Nanum nanum = (Nanum)pushMessage.getObject(contextHelper);

                java.util.List<Nanum> nanums = nanumAdapter.getData();
                for(int i = 0;i < nanums.size();i++) {
                    if(nanums.get(i).isSame(nanum)) {
                        nanums.set(i, nanum);
                        break;
                    }
                }

                contextHelper.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        nanumAdapter.notifyDataSetChanged();
                    }
                });
            }
            break;
            case PushMessage.ACTIONCODE_CHANGEGPS: {
                contextHelper.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(LocationManager.getInstance(contextHelper).isGpsOn()) {
                            curLocation = LocationManager.getInstance(contextHelper).getCurrentLocation(NanumListFragment.this);

                        } else {
                            curLocation = null;
                        }
                    }
                });

            }
            break;
//            break;
//            case PushMessage.ACTIONCODE_CHANGE_SHOPMAINADDRESS: {
//                if(listType == LISTTYPE_MAIN) {
//                    refreshTop();
//                    loadList(null);
//                }
//            }
//            break;
        }
    }


    public void onButtonClicked(View v) {
        super.onButtonClicked(v);
        switch (v.getId()) {
            case R.id.actionbarImageButton:
                if(listType == LISTTYPE_ALL) {
                    if(isSearch) {
                        find(etSearch.getText().toString());
                    } else {
                        Intent i = new Intent(getContext(), BaseActivity.class);
                        i.putExtra("activityCode", BaseActivity.ActivityCode.NANUMLIST.ordinal());
                        i.putExtra("isSearch", true);

                        getContext().startActivity(i);
                    }
                }
            break;
            case R.id.btnNew:

                listView.smoothScrollToPosition(0);
                loadList(null);

                break;
            case R.id.btnZim:
                sort = SORT_BOOKMARK;
                refreshTop();
                loadList(null);

                break;

            case R.id.btnDistance:
                if(!LocationManager.getInstance(contextHelper).isGpsOn()) {
                    new SweetAlertDialog(getContext())
                            .setContentText("위치 서비스 사용 설정이 꺼져있습니다.\n거리순을 보시려면 휴대폰 설정에서 위치 서비스 사용을 허용해주세요.")
                            .showCancelButton(true)
                            .setConfirmText("설정")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(final SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    contextHelper.getActivity().startActivity(intent);
                                }
                            })
                            .show();
                } else {
                    sort = SORT_DISTANCE;
                    refreshTop();
                    curLocation = LocationManager.getInstance(contextHelper).getCurrentLocation(NanumListFragment.this);
                    loadList(null);
                }
                break;

            case R.id.btnTime:
                sort = SORT_TIME;
                refreshTop();
                loadList(null);
                break;
            case R.id.btnDelivery: {
                Intent i = new Intent(getContext(), BaseActivity.class);
                i.putExtra("activityCode", BaseActivity.ActivityCode.APPLY.ordinal());
                getContext().startActivity(i);
            }
            break;
            case R.id.btnYondal: {
                Intent callIntent=new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ConfigManager.getInstance(contextHelper).getConfigHello().getParams().getYongdalPhone()));
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL);
                }else {
                    startActivity(callIntent);
                }
//                Intent i = new Intent(getContext(), BaseActivity.class);
//                i.putExtra("activityCode", BaseActivity.ActivityCode.YONGDAL.ordinal());
//                getContext().startActivity(i);
            }
                break;


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CALL:
            {
                Intent callIntent=new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ConfigManager.getInstance(contextHelper).getConfigHello().getParams().getYongdalPhone()));

                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    startActivity(callIntent);
                }else{
                    ////
                }
            }
        }
    }

    private void callPhone() {
        Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:010-1234-5678"));
        startActivity(i);
    }




    public void onLocationChanged(Location location) {
        if(curLocation == null) {
            curLocation = LocationManager.getInstance(contextHelper).getCurrentLocation(null);
            if (sort == SORT_DISTANCE) {
                loadList(null);
            }
        }
    }

    ArrayList<Nanum> adminOngoingList;
    ArrayList<Nanum> adminFinishList;
    ArrayList<Nanum> ongoingList;
    ArrayList<Nanum> finishList;
    Page adminOngoing;
    Page adminFinish;
    Page ongoing;
    Page finish;
    NanumList nanumListTemp;
    JSONObject option = null;

    public synchronized void loadList(final List list) {

        final int limit = 20;

        if(list == null) {
            adminOngoing = null;
            adminFinish = null;
            ongoing = null;
            finish = null;
        }

        btnNew.setVisibility(View.GONE);

        final NsUser user = UserManager.getInstance(contextHelper).getMe();

        if(isSearch) {
            JSONObject option = new JSONObject();
            try {
                option.put("keyword", keyword);
            } catch(Exception e) {

            }

            NanumManager.getInstance(contextHelper).find(user.getId(),
                    "KEYWORD",
                    list == null ? 1 : list.getPages().getNext(), limit, option,
                    new Response.Listener<NanumListData>() {
                        @Override
                        public void onResponse(NanumListData response) {
                            tvTitleNodata.setText("\'" + keyword + "\'(이)가 포함된 나눔이 없습니다.");
                            response.getNanumList().patch(contextHelper);
                            if (list == null) {
                                nanumList.getNanums().clear();
                            }
                            nanumList.getNanums().addAll(response.getNanumList().getNanums());
                            nanumAdapter.setData(nanumList.getNanums());
                            nanumAdapter.setKeyword(keyword);
                            postLoad(response.getNanumList());
                            contextHelper.hideProgress();
                            etSearch.setText("");
                        }
                    },
                    null
            );
            return;
        }
        else if(listType != LISTTYPE_ALL) {
            super.loadList(list);

            String type = "MINE";

            if(listType == LISTTYPE_MINE) {
                type = "MINE";
            } else if(listType == LISTTYPE_APPLY) {
                type = "APPLY";
            } else if(listType == LISTTYPE_WON) {
                type = "WON";
            }

            NanumManager.getInstance(contextHelper).find(user.getId(),
                    type,
                    list == null ? 1 : list.getPages().getNext(), limit, null,
                    new Response.Listener<NanumListData>() {
                        @Override
                        public void onResponse(NanumListData response) {

                            if(listType == LISTTYPE_MINE) {
                                tvTitleNodata.setText("나의 나눔이 없습니다.");
                            } else if(listType == LISTTYPE_APPLY) {
                                tvTitleNodata.setText("신청한 나눔이 없습니다.");
                            } else if(listType == LISTTYPE_WON) {
                                tvTitleNodata.setText("받은 나눔이 없습니다.");
                            }

                            response.getNanumList().patch(contextHelper);
                            if (list == null) {
                                nanumList.getNanums().clear();
                            }
                            nanumList.getNanums().addAll(response.getNanumList().getNanums());
                            nanumAdapter.setData(nanumList.getNanums());
                            postLoad(response.getNanumList());
                            contextHelper.hideProgress();
                        }
                    },
                    null
            );
            return;
        }

        if(sort == SORT_TIME || sort == SORT_BOOKMARK) {
            super.loadList(list);
            NanumManager.getInstance(contextHelper).find(user.getId(),
                    sort == SORT_TIME ? "TIME" : "BOOKMARK",
                    list == null ? 1 : list.getPages().getNext(), limit, null,
                    new Response.Listener<NanumListData>() {
                        @Override
                        public void onResponse(NanumListData response) {

                            tvTitleNodata.setText("나눔이 없습니다.");

                            response.getNanumList().patch(contextHelper);
                            if (list == null) {
                                nanumList.getNanums().clear();
                            }
                            nanumList.getNanums().addAll(response.getNanumList().getNanums());
                            nanumAdapter.setData(nanumList.getNanums());
                            postLoad(response.getNanumList());
                            contextHelper.hideProgress();
                        }
                    },
                    null
            );
            return;
        }


        if(!LocationManager.getInstance(contextHelper).isGpsOn()) {
            new SweetAlertDialog(getContext())
                    .setContentText("위치 서비스 사용 설정이 꺼져있습니다.\n거리순을 보시려면 휴대폰 설정에서 위치 서비스 사용을 허용해주세요.")
                    .showCancelButton(true)
                    .setConfirmText("설정")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(final SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            contextHelper.getActivity().startActivity(intent);
                        }
                    })
                    .show();
            this.refreshLayout.setRefreshing(false);
            return;
        }
        if(curLocation == null) {
            this.refreshLayout.setRefreshing(false);
            return;
        }

        super.loadList(list);


        try {
            option = new JSONObject();
            option.put("center_lng", curLocation.longitude);
            option.put("center_lat", curLocation.latitude);

        } catch(Exception e) {
        }


        final CountDownLatch latchAdminOngoing = new CountDownLatch(1);
        if(adminOngoing == null || adminOngoing.isHasNext()) {
            NanumManager.getInstance(contextHelper).find(user.getId(),
                    "ADMIN_ONGOING",
                    adminOngoing == null ? 1 : adminOngoing.getNext(), limit, null,
                    new Response.Listener<NanumListData>() {
                        @Override
                        public void onResponse(NanumListData response) {
                            response.getNanumList().patch(contextHelper);

                            adminOngoingList = response.getNanumList().getNanums();
                            adminOngoing = response.getNanumList().getPages();

                            nanumListTemp = response.getNanumList();

                            latchAdminOngoing.countDown();
                        }
                    },
                    null
            );
        } else {
            if(adminOngoingList != null)
                adminOngoingList.clear();
            latchAdminOngoing.countDown();
        }

        final CountDownLatch latchOngoing = new CountDownLatch(1);

        if(ongoing == null || ongoing.isHasNext()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        latchAdminOngoing.await();
                        NanumManager.getInstance(contextHelper).find(user.getId(),
                                "ONGOING", ongoing == null ? 1 : ongoing.getNext(), limit, option,
                                new Response.Listener<NanumListData>() {
                                    @Override
                                    public void onResponse(NanumListData response) {
                                        response.getNanumList().patch(contextHelper);

                                        ongoingList = response.getNanumList().getNanums();
                                        ongoing = response.getNanumList().getPages();

                                        nanumListTemp = response.getNanumList();

                                        latchOngoing.countDown();
                                    }
                                },
                                null
                        );

                    } catch (Exception e) {

                    }
                }
            }).start();
        } else {
            if(ongoingList != null)
                ongoingList.clear();
            latchOngoing.countDown();
        }



        final CountDownLatch latchAdminFinish = new CountDownLatch(1);
        if(adminFinish == null || adminFinish.isHasNext()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        latchOngoing.await();
                        NanumManager.getInstance(contextHelper).find(user.getId(),
                                "ADMIN_FINISH", adminFinish == null ? 1 : adminFinish.getNext(), limit, null,
                                new Response.Listener<NanumListData>() {
                                    @Override
                                    public void onResponse(NanumListData response) {
                                        response.getNanumList().patch(contextHelper);

                                        adminFinishList = response.getNanumList().getNanums();
                                        adminFinish = response.getNanumList().getPages();

                                        nanumListTemp = response.getNanumList();

                                        latchAdminFinish.countDown();
                                    }
                                },
                                null
                        );


                    } catch (Exception e) {

                    }
                }
            }).start();
        } else {
            if(adminFinishList != null)
                adminFinishList.clear();
            latchAdminFinish.countDown();
        }


        final CountDownLatch latchFinish = new CountDownLatch(1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    latchAdminFinish.await();
                    NanumManager.getInstance(contextHelper).find(user.getId(),
                            "FINISH", finish == null ? 1 : finish.getNext(), limit, option,
                            new Response.Listener<NanumListData>() {
                                @Override
                                public void onResponse(NanumListData response) {
                                    response.getNanumList().patch(contextHelper);

                                    finishList = response.getNanumList().getNanums();
                                    finish = response.getNanumList().getPages();

                                    nanumListTemp = response.getNanumList();

                                    latchFinish.countDown();

                                }
                            },
                            null
                    );

                } catch (Exception e) {

                    Logger.debug(TAG, e.toString());

                }
            }
        }).start();


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    latchFinish.await();

                    contextHelper.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            tvTitleNodata.setText("나눔이 없습니다.");

                            if (list == null) {
                                nanumList.getNanums().clear();
                            }
                            if (adminOngoingList != null)
                                nanumList.getNanums().addAll(adminOngoingList);

                            if (ongoingList != null)
                                nanumList.getNanums().addAll(ongoingList);

                            if (adminFinishList != null)
                                nanumList.getNanums().addAll(adminFinishList);

                            if (finishList != null)
                                nanumList.getNanums().addAll(finishList);

                            nanumAdapter.setData(nanumList.getNanums());
                            postLoad(nanumListTemp);

                            contextHelper.hideProgress();
                        }
                    });


                } catch (Exception e) {
                    Logger.debug(TAG, e.toString());

                }
            }
        }).start();

    }
}





