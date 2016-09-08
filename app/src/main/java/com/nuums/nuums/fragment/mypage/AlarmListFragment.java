package com.nuums.nuums.fragment.mypage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nuums.nuums.R;
import com.nuums.nuums.activity.BaseActivity;
import com.nuums.nuums.adapter.AlarmAdapter;
import com.nuums.nuums.model.alarm.Alarm;
import com.nuums.nuums.model.alarm.AlarmList;
import com.nuums.nuums.model.alarm.AlarmListData;
import com.nuums.nuums.model.alarm.AlarmManager;
import com.nuums.nuums.model.nanum.Nanum;
import com.nuums.nuums.model.user.NsUser;
import com.nuums.nuums.view.HeaderView;
import com.nuums.nuums.view.MypageView;
import com.yongtrim.lib.activity.ABaseFragmentAcitivty;
import com.yongtrim.lib.fragment.ListFragment;
import com.yongtrim.lib.log.Logger;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.model.list.List;
import com.yongtrim.lib.model.photo.Photo;
import com.yongtrim.lib.model.photo.PhotoData;
import com.yongtrim.lib.model.photo.PhotoManager;
import com.yongtrim.lib.model.post.PostData;
import com.yongtrim.lib.model.post.PostManager;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.ui.PagerSlidingTabStrip2;
import com.yongtrim.lib.ui.UltraListView;
import com.yongtrim.lib.util.PixelUtil;

/**
 * nuums / com.nuums.nuums.fragment.mypage
 * <p/>
 * Created by yongtrim.com on 16. 1. 19..
 */
public class AlarmListFragment extends ListFragment {
    private final String TAG = getClass().getSimpleName();

    AlarmAdapter alarmAdapter;
    AlarmList alarmList;

    //ViewPager pager;
    PagerSlidingTabStrip2 tabbar;

    UltraListView listView;
    TextView tvTitleNodata;

    public MypageView mypageView;


    public static AlarmListFragment create() {
        AlarmListFragment fragment = new AlarmListFragment();
        return fragment;
    }

//    public AlarmListFragment setPager(ViewPager pager) {
//        this.pager = pager;
//        return this;
//    }


    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        View view = inflater.inflate(R.layout.fragment_alarmlist, container, false);

        listView = (UltraListView)view.findViewById(R.id.listView);
        alarmAdapter = new AlarmAdapter(contextHelper);

        if(alarmList == null) {
            alarmList = new AlarmList();
        }
        alarmAdapter.setData(alarmList.getAlarms());
        listView.setAdapter(alarmAdapter);

        setupView(listView, view.findViewById(R.id.viewNodata), view.findViewById(R.id.swipeRefreshLayout), alarmAdapter);
        setListInfo(alarmList);
        setAddedLayer(view.findViewById(R.id.layerTop), view.findViewById(R.id.layerBottom));

        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(id < 0 || id >= alarmList.getAlarms().size())
                    return;

                Alarm alarmSelected = alarmList.getAlarms().get((int) id);

                if(alarmSelected.getType().equals("APPLY") ||
                        alarmSelected.getType().equals("WON") ||
                        alarmSelected.getType().equals("WARNING") ||
                        alarmSelected.getType().equals("KEYWORD") ||
                        alarmSelected.getType().equals("ZZIM")) {
                    Intent i = new Intent(getContext(), BaseActivity.class);
                    i.putExtra("activityCode", BaseActivity.ActivityCode.NANUMVIEWER.ordinal());
                    i.putExtra("nanum_id", alarmSelected.getAlarmParam().nanum_id);
                    getContext().startActivity(i);
                } else if(alarmSelected.getType().equals("EVENT_START") ||
                        alarmSelected.getType().equals("EVENT_END")
                        ) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(alarmSelected.getAlarmParam().getUrl()));
                    contextHelper.getActivity().startActivity(browserIntent);
                }
            }
        });

        mypageView = new MypageView(contextHelper);

        HeaderView headerView = new HeaderView(getContext());
        headerView.addView(mypageView);

        headerView.addView(PixelUtil.dpToPx(getContext(), 60));
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

        }
    }
    public void loadList(final List list) {
        super.loadList(list);

        String type = null;

        AlarmManager.getInstance(contextHelper).find(
                type, list == null ? 1 : list.getPages().getNext(),
                new Response.Listener<AlarmListData>() {
                    @Override
                    public void onResponse(AlarmListData response) {
                        Logger.debug(TAG, "find() | result = " + response.toString());

                        tvTitleNodata.setText("알림이 없습니다.");

                        if (list == null) {
                            alarmList.getAlarms().clear();
                        }

                        alarmList.getAlarms().addAll(response.getAlarmList().getAlarms());
                        alarmAdapter.setData(alarmList.getAlarms());
                        postLoad(response.getAlarmList());
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
