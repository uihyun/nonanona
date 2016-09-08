package com.nuums.nuums.fragment.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.nuums.nuums.R;
import com.nuums.nuums.adapter.AlarmAdapter;
import com.nuums.nuums.adapter.ApplyAdapter;
import com.nuums.nuums.model.alarm.AlarmList;
import com.nuums.nuums.model.alarm.AlarmListData;
import com.nuums.nuums.model.alarm.AlarmManager;
import com.nuums.nuums.model.apply.Apply;
import com.nuums.nuums.model.apply.ApplyList;
import com.nuums.nuums.model.apply.ApplyListData;
import com.nuums.nuums.model.apply.ApplyManager;
import com.nuums.nuums.view.HeaderView;
import com.nuums.nuums.view.MypageView;
import com.yongtrim.lib.fragment.ListFragment;
import com.yongtrim.lib.log.Logger;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.model.list.List;
import com.yongtrim.lib.ui.PagerSlidingTabStrip2;
import com.yongtrim.lib.ui.UltraListView;
import com.yongtrim.lib.util.PixelUtil;

/**
 * nuums / com.nuums.nuums.fragment.mypage
 * <p/>
 * Created by yongtrim.com on 16. 1. 19..
 */
public class ApplyListFragment extends ListFragment {
    private final String TAG = getClass().getSimpleName();

    ApplyAdapter applyAdapter;
    ApplyList applyList;

    PagerSlidingTabStrip2 tabbar;

    UltraListView listView;
    TextView tvTitleNodata;

    public MypageView mypageView;


    public static ApplyListFragment create() {
        ApplyListFragment fragment = new ApplyListFragment();
        return fragment;
    }

//    public ApplyListFragment setPager(ViewPager pager) {
//        this.pager = pager;
//        return this;
//    }


    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        View view = inflater.inflate(R.layout.fragment_applylist, container, false);

        listView = (UltraListView)view.findViewById(R.id.listView);
        applyAdapter = new ApplyAdapter(contextHelper);

        if(applyList == null) {
            applyList = new ApplyList();
        }
        applyAdapter.setData(applyList.getApplys());
        listView.setAdapter(applyAdapter);

        setupView(listView, view.findViewById(R.id.viewNodata), view.findViewById(R.id.swipeRefreshLayout), applyAdapter);
        setListInfo(applyList);
        setAddedLayer(view.findViewById(R.id.layerTop), view.findViewById(R.id.layerBottom));

        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

        mypageView = new MypageView(contextHelper);

        HeaderView headerView = new HeaderView(getContext());
        headerView.addView(mypageView);

        headerView.addView(PixelUtil.dpToPx(getContext(), 60+20));
        setHeader(headerView);
        tabbar = (PagerSlidingTabStrip2)view.findViewById(R.id.tabbar);
        tabbar.setContextHelper(contextHelper);
        tabbar.setVisibility(View.VISIBLE);
        tabbar.setViewPager(((MypageMainFragment)getParentFragment()).pager);


        tvTitleNodata = (TextView)view.findViewById(R.id.tvTitleNodata);

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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(mypageView != null)
            mypageView.onActivityResult(requestCode, resultCode, data);
    }



    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (!visible) {
            if(listView != null)
                listView.setSelection(0);
        }
    }

    public void onEvent(PushMessage pushMessage) {
        switch(pushMessage.getActionCode()) {
            case PushMessage.ACTIONCODE_CHANGE_ME:
                if(tabbar != null) {
                    tabbar.notifyDataSetChanged();
                    loadList(null);
                }

                if(mypageView != null)
                    mypageView.refresh();
                break;
            case PushMessage.ACTIONCODE_CHANGE_APPLY: {
                Apply apply = (Apply) pushMessage.getObject(contextHelper);

                java.util.List<Apply> applies = applyAdapter.getData();
                for (int i = 0; i < applies.size(); i++) {
                    if (applies.get(i).isSame(apply)) {
                        applies.set(i, apply);
                        break;
                    }
                }

                contextHelper.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        applyAdapter.notifyDataSetChanged();
                    }
                });
            }
            break;
        }
    }
    public void loadList(final List list) {
        super.loadList(list);

        String type = null;

        ApplyManager.getInstance(contextHelper).find(
                type, list == null ? 1 : list.getPages().getNext(),
                new Response.Listener<ApplyListData>() {
                    @Override
                    public void onResponse(ApplyListData response) {
                        Logger.debug(TAG, "find() | result = " + response.toString());

                        tvTitleNodata.setText("택배 접수하신 것이 없습니다.");
                        response.getApplyList().patch(contextHelper);

                        if (list == null) {
                            applyList.getApplys().clear();
                        }

                        applyList.getApplys().addAll(response.getApplyList().getApplys());
                        applyAdapter.setData(applyList.getApplys());
                        postLoad(response.getApplyList());
                        contextHelper.hideProgress();
                    }
                },
                null
        );
    }

    public void onButtonClicked(View v) {
        super.onButtonClicked(v);
        switch (v.getId()) {
        }
    }

}

