package com.nuums.nuums.fragment.talk;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.nuums.nuums.R;
import com.nuums.nuums.activity.BaseActivity;
import com.nuums.nuums.activity.MainActivity;
import com.nuums.nuums.adapter.TalkAdapter;
import com.nuums.nuums.fragment.misc.UserListFragment;
import com.nuums.nuums.model.chat.Talk;
import com.nuums.nuums.model.chat.TalkData;
import com.nuums.nuums.model.chat.TalkList;
import com.nuums.nuums.model.chat.TalkListData;
import com.nuums.nuums.model.chat.TalkManager;
import com.nuums.nuums.model.nanum.Nanum;
import com.nuums.nuums.model.report.Report;
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.Application;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.fragment.ABaseFragment;
import com.yongtrim.lib.fragment.ListFragment;
import com.yongtrim.lib.log.Logger;
import com.yongtrim.lib.message.GcmIntentService;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.model.ACommonData;
import com.yongtrim.lib.model.list.List;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.ui.UltraListView;
import com.yongtrim.lib.ui.sweetalert.SweetAlertDialog;

import de.greenrobot.event.EventBus;

/**
 * nuums / com.nuums.nuums.fragment.talk
 * <p/>
 * Created by yongtrim.com on 15. 12. 20..
 */
public class TalkListFragment extends ListFragment {

    final static String TAG = "TalkListFragment";

    private UltraListView listView;

    public TalkList talkList;
    private TalkAdapter talkAdapter;

    public static TalkListFragment create() {
        TalkListFragment fragment = new TalkListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_talklist, container, false);

        listView = (UltraListView)view.findViewById(R.id.listView);

        talkAdapter = new TalkAdapter(contextHelper, listView);

        if(talkList == null) {
            talkList = new TalkList();
        }

        talkAdapter.setData(talkList.getTalks());
        listView.setAdapter(talkAdapter);

        setupView(listView, view.findViewById(R.id.viewNodata), view.findViewById(R.id.swipeRefreshLayout), talkAdapter);

        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Talk talkSelected = talkList.getTalks().get((int) position);

                if(talkAdapter.isOutMode()) {
                    Boolean select = (Boolean)talkSelected.getTag();

                    if(select != null && select.booleanValue()) {
                        talkSelected.setTag(new Boolean(false));
                    } else {
                        for(Talk talk : talkList.getTalks()) {
                            talk.setTag(new Boolean(false));
                        }
                        talkSelected.setTag(new Boolean(true));
                    }

                    talkAdapter.notifyDataSetChanged();
                } else {
                    Intent i = new Intent(getContext(), BaseActivity.class);
                    i.putExtra("activityCode", BaseActivity.ActivityCode.CHAT.ordinal());
                    i.putExtra("to", talkSelected.getOther(contextHelper).toString());
                    getContext().startActivity(i);
                }
            }
        });


        loadList(null);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    public void onButtonClicked(View v) {

        switch(v.getId()) {
            case R.id.btnSearchUser:
                Intent i = new Intent(getContext(), BaseActivity.class);
                i.putExtra("activityCode", BaseActivity.ActivityCode.USERLIST.ordinal());
                i.putExtra("listType", UserListFragment.LISTTYPE_SEARCH);

                getContext().startActivity(i);
                break;
            case R.id.actionbarImageButton:
                if(talkAdapter.isOutMode()) {
                    Talk talkChecked = null;

                    for(Talk talk : talkList.getTalks()) {

                        Boolean select = (Boolean)talk.getTag();

                        if(select != null && select.booleanValue()) {
                            talkChecked = talk;
                            break;
                        }
                    }

                    if(talkChecked != null) {

                        final Talk talkCheckedfinal = talkChecked;
                        new SweetAlertDialog(getContext())
                                .setContentText("채팅방에서 나가시면 대화내용 및 채팅방 정보가 모두 삭제됩니다.\n채팅방에서 나가시겠습니까?")
                                .showCancelButton(true)
                                .setConfirmText("예")
                                .setCancelText("아니오")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(final SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();

                                        contextHelper.showProgress(null);
                                        TalkManager.getInstance(contextHelper).out(talkCheckedfinal.getId(),
                                                new Response.Listener<TalkData>() {
                                                    @Override
                                                    public void onResponse(TalkData response) {
                                                        contextHelper.hideProgress();

                                                        Application.setBadge(contextHelper.getContext(), response.user.getUnreadCnt());
                                                        EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_CHANGEUNREADCOUNT).setObject(new Integer(response.user.getUnreadCnt())));


                                                        loadList(null);
                                                    }
                                                },
                                                null
                                        );

                                        talkAdapter.setMode(false);
                                        contextHelper.getActivity().setImageButtonAndVisiable(R.drawable.setwhite);

                                    }
                                })
                                .show();
                    } else {
                        talkAdapter.setMode(false);
                        contextHelper.getActivity().setImageButtonAndVisiable(R.drawable.setwhite);

                    }

                } else {
                    new SweetAlertDialog(getContext(), SweetAlertDialog.SELECT_TYPE)
                            .setTitleText("선택해 주세요")
                            .setArrayValue(new String[]{"대화 나가기", "차단목록 관리"})
                            .showCloseButton(true)
                            .setSelectListener(new SweetAlertDialog.OnSweetSelectListener() {
                                @Override
                                public void onSelected(SweetAlertDialog sweetAlertDialog, int index) {
                                    sweetAlertDialog.dismissWithAnimation();
                                    switch (index) {

                                        case 0: {
                                            if(talkList.getTalks().size() == 0) {
                                                new SweetAlertDialog(getContext()).setContentText("대화 목록이 없습니다.").show();
                                            } else {
                                                contextHelper.getActivity().setImageButtonAndVisiable(R.drawable.roomout);
                                                talkAdapter.setMode(true);
                                            }
                                        }
                                        break;
                                        case 1:
                                            Intent i = new Intent(getContext(), BaseActivity.class);
                                            i.putExtra("activityCode", BaseActivity.ActivityCode.USERLIST.ordinal());
                                            i.putExtra("listType", UserListFragment.LISTTYPE_BLOCK);
                                            getContext().startActivity(i);

                                            break;
                                    }
                                }
                            })
                            .show();
                }

                break;
        }
    }

    public void onEvent(PushMessage pushMessage) {
        switch(pushMessage.getActionCode()) {
            case PushMessage.ACTIONCODE_DELETETALK:
                loadList(null);
                break;
            case PushMessage.ACTIONCODE_CHANGETALK: {
                final Talk talk = (Talk)pushMessage.getObject(contextHelper);

                if(talk.getMe(contextHelper) != null) {
                    Application.setBadge(contextHelper.getContext(), talk.getMe(contextHelper).getUnreadCnt());
                }

                contextHelper.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boolean has = false;
                        java.util.List<Talk> talks = talkList.getTalks();
                        for(int i = 0;i < talks.size();i++) {
                            if(talk.getId() != null && talks.get(i).isSame(talk)) {
                                talks.set(i, talk);
                                has = true;
                                break;
                            }
                            else if(talks.get(i).getRoomName().equals(talk.getRoomName())) {
                                Talk _talk = talks.get(i);
                                _talk.setTimeUpdated(talk.getTimeUpdated());
                                _talk.setMessageRecent(talk.getMessageRecent());
                                has = true;
                                break;
                            }
                        }
                        if(has) {
                            talkAdapter.notifyDataSetChanged();
                        } else {
                            loadList(null);
                        }
                    }
                });

            }
            break;
        }
    }

    public void loadList(final List list) {
        super.loadList(list);

        TalkManager.getInstance(contextHelper).find(
                list == null ? 1 : list.getPages().getNext(),
                new Response.Listener<TalkListData>() {
                    @Override
                    public void onResponse(final TalkListData response) {

                        contextHelper.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (list == null) {
                                    talkList.getTalks().clear();
                                }

                                talkList.getTalks().addAll(response.getTalkList().getTalks());
                                talkAdapter.setData(talkList.getTalks());

                                postLoad(response.getTalkList());
                                contextHelper.hideProgress();

                                EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_CHANGETALKLIST).setObject(talkList.getTalks()));
                            }
                        });

                    }
                },
                null
        );
    }



//    public static boolean hasNew(ContextHelper contextHelper) {
//        boolean hasNew = false;
//        int unreadCnt = 0;
//        if(talkList == null)
//            return hasNew;
//
//        NsUser me = UserManager.getInstance(contextHelper).getMe();
//
//        for(Talk talk : talkList.getTalks()) {
//            if(inquiry.getGuest().isSame(me)) {
//                if(inquiry.getGuestUnreadCnt() > 0) {
//                    hasNew = true;
//                    unreadCnt += inquiry.getGuestUnreadCnt();
//                }
//            } else {
//                if(inquiry.getHostUnreadCnt() > 0) {
//                    hasNew = true;
//                    unreadCnt += inquiry.getHostUnreadCnt();
//                }
//            }
//        }
//
//        GcmIntentService.setBadge(contextHelper.getContext(), unreadCnt);
//
//        return hasNew;
//
//    }
}


