package com.nuums.nuums.fragment.talk;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.nuums.nuums.R;
import com.nuums.nuums.activity.BaseActivity;
import com.nuums.nuums.activity.MainActivity;
import com.nuums.nuums.activity.SplashActivity;
import com.nuums.nuums.adapter.ChatAdapter;
import com.nuums.nuums.model.chat.Address;
import com.nuums.nuums.model.chat.Chat;
import com.nuums.nuums.model.chat.ChatData;
import com.nuums.nuums.model.chat.ChatDate;
import com.nuums.nuums.model.chat.ChatList;
import com.nuums.nuums.model.chat.ChatListData;
import com.nuums.nuums.model.chat.ChatManager;
import com.nuums.nuums.model.chat.Delivery;
import com.nuums.nuums.model.chat.Talk;
import com.nuums.nuums.model.chat.TalkData;
import com.nuums.nuums.model.chat.TalkManager;
import com.nuums.nuums.model.chat.UserInfo;
import com.nuums.nuums.model.misc.Comment;
import com.nuums.nuums.model.nanum.Nanum;
import com.nuums.nuums.model.nanum.NanumData;
import com.nuums.nuums.model.nanum.NanumManager;
import com.nuums.nuums.model.report.Report;
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.Application;
import com.yongtrim.lib.Config;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.activity.ABaseFragmentAcitivty;
import com.yongtrim.lib.fragment.ListFragment;
import com.yongtrim.lib.log.Logger;
import com.yongtrim.lib.message.GcmIntentService;
import com.yongtrim.lib.message.MessageManager;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.model.ACommonData;
import com.yongtrim.lib.model.config.ConfigManager;
import com.yongtrim.lib.model.list.List;
import com.yongtrim.lib.model.photo.Photo;
import com.yongtrim.lib.model.photo.PhotoManager;
import com.yongtrim.lib.model.user.UserData;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.ui.UltraButton;
import com.yongtrim.lib.ui.UltraListView;
import com.yongtrim.lib.ui.sweetalert.SweetAlertDialog;
import com.yongtrim.lib.util.DateUtil;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Random;
import java.util.UUID;


import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;


import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;

import de.greenrobot.event.EventBus;
import hirondelle.date4j.DateTime;

/**
 * nuums / com.nuums.nuums.fragment.talk
 * <p/>
 * Created by yongtrim.com on 16. 1. 6..
 */
public class ChatFragment extends ListFragment {

    final static String TAG = "ChatFragment";

    private UltraListView listView;
    private ChatList chatUIList;
    private ArrayList<Chat> chats;

    private ChatAdapter chatAdapter;

    TextView tvTitleNodata;

    NsUser user;
    NsUser to;

    EditText etMessage;
    UltraButton btnSend;

    boolean toConnected;
    boolean isLoading;
    String roomName;

    Talk talk;

    SweetAlertDialog dialogAddress;

    private Socket socket;
    {
        try{
            socket = IO.socket(Config.url);
        }catch(URISyntaxException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        if(contextHelper.getActivity().getIntent().hasExtra("to")) {
            to = NsUser.getUser(contextHelper.getActivity().getIntent().getStringExtra("to"));
        }

        contextHelper.getActivity().setupActionBar(to.getNickname());
        contextHelper.getActivity().setImageButtonAndVisiable(R.drawable.menu_3dotwhite);

        user = UserManager.getInstance(contextHelper).getMe();

        socket.connect();

        socket.on("message", handleIncomingMessages);
        socket.on("update", handleUpdate);

        ArrayList arrayList = new ArrayList();
        arrayList.add(user.getId());
        arrayList.add(to.getId());
        Collections.sort(arrayList);
        roomName = arrayList.get(0) + "-" + arrayList.get(1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        contextHelper.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        listView = (UltraListView)view.findViewById(R.id.listView);

        tvTitleNodata = (TextView)view.findViewById(R.id.tvTitleNodata);

        chatAdapter = new ChatAdapter(contextHelper, listView);

        chatUIList = new ChatList();
        chats = new ArrayList<>();

        chatAdapter.setData(chatUIList.getChats());
        listView.setAdapter(chatAdapter);

        setupView(listView, view.findViewById(R.id.viewNodata), null, chatAdapter);

        etMessage = (EditText)view.findViewById(R.id.etMessage);
        etMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendMessage(v.getText().toString());
                    return true;
                }
                return false;
            }
        });

        btnSend = (UltraButton)view.findViewById(R.id.ibSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(etMessage.getText().toString());
            }
        });

        setListInfo(chatUIList);


//        if(!MessageManager.getInstance(contextHelper).getPreference().getMainActivieyIsActive()) {
//            Intent i = new Intent(contextHelper.getContext(), SplashActivity.class);
//            i.putExtra("to", contextHelper.getActivity().getIntent().getStringExtra("to"));
//            contextHelper.getContext().startActivity(i);
//
//            contextHelper.getActivity().finish();
//        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        MessageManager.getInstance(contextHelper).getPreference().putCurChatter(to);
        super.onResume();
        loadList(null);
        socket.emit("join", user.toString(), to.toString());
    }


    @Override
    public void onPause() {
        MessageManager.getInstance(contextHelper).getPreference().putCurChatter(null);
        super.onPause();
        socket.emit("disjoin");

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        socket.disconnect();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case Activity.RESULT_OK:
                if(requestCode == ABaseFragmentAcitivty.REQUEST_POSTCODE) {
                    String postcode = data.getStringExtra("postcode");
                    String basicaddress = data.getStringExtra("basicaddress");
                    dialogAddress.setAddress(postcode, basicaddress);
                } else if(requestCode == ABaseFragmentAcitivty.REQUEST_PICK_IMAGE) {

                    String uri = PhotoManager.getInstance(contextHelper).getPickImageResultUri(data).toString();
                    int orientation = PhotoManager.getInstance(contextHelper).getExifRotation(data.getData());

                    final Photo photo = new Photo(uri, orientation);
                    photo.setType(Photo.TYPE_PHOTO);

                    photo.setPath(null);
                    photo.makeBitmap(contextHelper);

                    new SweetAlertDialog(getContext(), SweetAlertDialog.PHOTO_TYPE)
                            .setTitleText("사진전송")
                            .showCancelButton(false)
                            .showCloseButton(true)
                            .setConfirmText("전송하기")
                            .setPhoto(photo)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {


                                    sDialog.dismissWithAnimation();
                                    sendPhoto(photo);

                                }
                            })
                            .show();

                }
                break;

            default:
                break;
        }
    }


    void sendPhoto(final Photo photo) {

        contextHelper.showProgress(null);
        ArrayList<Photo> photos = new ArrayList<>();

        photo.setType("CHAT");
        photos.add(photo);

        PhotoManager.getInstance(contextHelper).uploadPhotos(photos, new PhotoManager.OnPhotoUploadedListener() {
            public void onCompleted(final java.util.List<Photo> photos) {

                contextHelper.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        contextHelper.hideProgress();

                        Chat chat = new Chat();
                        chat.setFrom(user);
                        chat.setMessage(user.getNicknameSafe() + "님이 사진을 전송하였습니다.");
                        chat.setTo(to);
                        chat.setType(Chat.TYPE_PHOTO);
                        chat.setPhoto(photos.get(0));

                        chat.setTimeCreated(new Date());
                        chat.setIsRead(toConnected);
                        addChat(chat);

                        socket.emit("message", chat.toString());


                        PhotoManager.getInstance(contextHelper).clearCache();
                    }
                });


            }
        });
    }

    public void onButtonClicked(View v) {
        if(isLoading)
            return;

        switch (v.getId()) {
            case R.id.btnAddress: //주소입력

                dialogAddress = new SweetAlertDialog(getContext(), SweetAlertDialog.ADDRESS_TYPE);
                dialogAddress.setTitleText("주소 입력")
                        .showCancelButton(false)
                        .showCloseButton(true)
                        .setConfirmText("접수하기")
                        .setAddress(ConfigManager.getInstance(contextHelper).getPreference().getMyAddress())
                        .setContextHelper(contextHelper)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                String errMessage = "";

                                if(TextUtils.isEmpty(sDialog.etName.getText().toString())) {
                                    errMessage = "보내는 사람을 입력해 주세요.";
                                }
                                else if(TextUtils.isEmpty(sDialog.etNumber0.getText().toString()) ||
                                        TextUtils.isEmpty(sDialog.etNumber1.getText().toString()) ||
                                        TextUtils.isEmpty(sDialog.etNumber2.getText().toString())
                                        ) {
                                    errMessage = "연락처를 입력해 주세요.";
                                } else if(TextUtils.isEmpty(sDialog.btnPostNumber.getText())) {
                                    errMessage = "우편번호를 입력해 주세요.";
                                } else if(TextUtils.isEmpty(sDialog.btnBasic.getText())) {
                                    errMessage = "기본주소를 입력해 주세요.";
                                } else if(TextUtils.isEmpty(sDialog.etDetail.getText().toString())) {
                                    errMessage = "상세주소를 입력해 주세요.";
                                }

                                if(!TextUtils.isEmpty(errMessage)) {
                                    new SweetAlertDialog(getContext()).setContentText(errMessage).show();
                                } else {
                                    sDialog.dismissWithAnimation();
                                    dialogAddress = null;


                                    Address address = new Address();
                                    address.setName(sDialog.etName.getText().toString());

                                    address.setNumber0(sDialog.etNumber0.getText().toString());
                                    address.setNumber1(sDialog.etNumber1.getText().toString());
                                    address.setNumber2(sDialog.etNumber2.getText().toString());

                                    address.setPostCode(sDialog.btnPostNumber.getText());
                                    address.setAddressBasic(sDialog.btnBasic.getText());
                                    address.setAddressDetail(sDialog.etDetail.getText().toString());

                                    Chat chat = new Chat();
                                    chat.setFrom(user);
                                    chat.setMessage(user.getNicknameSafe() + "님이 주소를 입력하였습니다.");
                                    chat.setTo(to);
                                    chat.setType(Chat.TYPE_ADDRESS);
                                    chat.setAddress(address);

                                    chat.setTimeCreated(new Date());
                                    chat.setIsRead(toConnected);
                                    addChat(chat);

                                    socket.emit("message", chat.toString());

                                    if(sDialog.cbRemember.isChecked()) {
                                        ConfigManager.getInstance(contextHelper).getPreference().putMyAddress(address);
                                    } else {
                                        ConfigManager.getInstance(contextHelper).getPreference().putMyAddress(null);
                                    }

                                }
                            }
                        })
                        .show();
                break;
            case R.id.btnAsk: //배송신청
            {
                Intent i = new Intent(contextHelper.getContext(), BaseActivity.class);
                i.putExtra("activityCode", BaseActivity.ActivityCode.APPLY.ordinal());
                contextHelper.getActivity().startActivity(i);
            }
                break;

            case R.id.btnSearch: //배송조회

                new SweetAlertDialog(getContext(), SweetAlertDialog.DELIVERY_TYPE)
                        .setTitleText("배송조회")
                        .showCancelButton(false)
                        .showCloseButton(true)
                        .setConfirmText("조회하기")
                        .setContextHelper(contextHelper)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                String errMessage = "";

                                if(sDialog.codeNameDeliveryCompany == null) {
                                    errMessage = "택배사를 선택해 주세요.";
                                } else if(TextUtils.isEmpty(sDialog.etDeliveryNumber.getText())) {
                                    errMessage = "운송장 번호를 입력해 주세요.";
                                }

                                if(!TextUtils.isEmpty(errMessage)) {
                                    new SweetAlertDialog(getContext()).setContentText(errMessage).show();
                                } else {
                                    sDialog.dismissWithAnimation();

                                    Delivery delivery = new Delivery();

                                    delivery.setCompany(sDialog.codeNameDeliveryCompany);
                                    delivery.setNumber(sDialog.etDeliveryNumber.getText().toString());

                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(delivery.getCompany().getUrl() + delivery.getNumber()));
                                    contextHelper.getActivity().startActivity(browserIntent);
                                }
                            }
                        })
                        .show();


                break;

            case R.id.btnEnter: //운송장입력

                new SweetAlertDialog(getContext(), SweetAlertDialog.DELIVERY_TYPE)
                        .setTitleText("송장번호 입력")
                        .showCancelButton(false)
                        .showCloseButton(true)
                        .setConfirmText("접수하기")
                        .setContextHelper(contextHelper)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                String errMessage = "";

                                if(sDialog.codeNameDeliveryCompany == null) {
                                    errMessage = "택배사를 선택해 주세요.";
                                } else if(TextUtils.isEmpty(sDialog.etDeliveryNumber.getText())) {
                                    errMessage = "운송장 번호를 입력해 주세요.";
                                }

                                if(!TextUtils.isEmpty(errMessage)) {
                                    new SweetAlertDialog(getContext()).setContentText(errMessage).show();
                                } else {
                                    sDialog.dismissWithAnimation();

                                    Delivery delivery = new Delivery();

                                    delivery.setCompany(sDialog.codeNameDeliveryCompany);
                                    delivery.setNumber(sDialog.etDeliveryNumber.getText().toString());

                                    Chat chat = new Chat();
                                    chat.setFrom(user);
                                    chat.setMessage(user.getNicknameSafe() + "님이 운송장번호를 입력하였습니다.");
                                    chat.setTo(to);
                                    chat.setType(Chat.TYPE_DELIVERY);
                                    chat.setDelivery(delivery);

                                    chat.setTimeCreated(new Date());
                                    chat.setIsRead(toConnected);
                                    addChat(chat);

                                    socket.emit("message", chat.toString());
                                }
                            }
                        })
                        .show();
                break;

            case R.id.btnAddPhoto: //사진첨부
                contextHelper.getActivity().startActivityForResult(
                        PhotoManager.getInstance(contextHelper).getPickImageChooserIntent(1),
                        ABaseFragmentAcitivty.REQUEST_PICK_IMAGE);
                break;

            case R.id.actionbarImageButton:

                MessageManager.MessagePreference preference = MessageManager.getInstance(contextHelper).getPreference();
                final boolean isAlarmOn = preference.getIsAlarmOn(user, to);

                new SweetAlertDialog(getContext(), SweetAlertDialog.SELECT_TYPE)
                        .setTitleText("선택해 주세요")
                        .setArrayValue(new String[]{"차단하기", "신고하기", isAlarmOn ? "알람끄기" : "알람켜기"})
                        .showCloseButton(true)
                        .setSelectListener(new SweetAlertDialog.OnSweetSelectListener() {
                            @Override
                            public void onSelected(SweetAlertDialog sweetAlertDialog, int index) {
                                sweetAlertDialog.dismissWithAnimation();

                                switch (index) {
                                    case 0: {
                                        new SweetAlertDialog(getContext())
                                                .setContentText("차단하시면 대화내용 및 채팅방 정보가 모두 삭제됩니다.\n" +
                                                        "차단 하시겠습니까?")
                                                .showCancelButton(true)
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(final SweetAlertDialog sweetAlertDialog) {
                                                        sweetAlertDialog.dismissWithAnimation();

                                                        contextHelper.showProgress(null);

                                                        UserManager.getInstance(contextHelper).block(
                                                                to, true,
                                                                new Response.Listener<UserData>() {
                                                                    @Override
                                                                    public void onResponse(UserData response) {
                                                                        if (response.isSuccess()) {
                                                                            UserManager.getInstance(contextHelper).setMe(response.user);

                                                                            TalkManager.getInstance(contextHelper).out(talk.getId(),
                                                                                    new Response.Listener<TalkData>() {
                                                                                        @Override
                                                                                        public void onResponse(TalkData response) {
                                                                                            contextHelper.hideProgress();

                                                                                            contextHelper.getActivity().runOnUiThread(new Runnable() {
                                                                                                @Override
                                                                                                public void run() {

                                                                                                    Toast.makeText(contextHelper.getContext(), "차단하였습니다.", Toast.LENGTH_SHORT).show();


                                                                                                    EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_DELETETALK));

                                                                                                    contextHelper.getActivity().finish();
                                                                                                }
                                                                                            });

                                                                                        }
                                                                                    },
                                                                                    null
                                                                            );


                                                                        } else {
                                                                        }
                                                                    }
                                                                },
                                                                null
                                                        );

                                                    }
                                                })
                                                .show();
                                    }
                                    break;
                                    case 1: // 신고하기
                                    {
                                        Intent i = new Intent(getContext(), BaseActivity.class);
                                        i.putExtra("activityCode", BaseActivity.ActivityCode.REPORT.ordinal());
                                        i.putExtra("type", Report.REPORTTYPE_REPORT);
                                        i.putExtra("target", to.toString());

                                        getContext().startActivity(i);
                                    }
                                        break;
                                    case 2: {
                                        MessageManager.MessagePreference preference = MessageManager.getInstance(contextHelper).getPreference();
                                        preference.putAlarm(user, to, !isAlarmOn);

                                        if (isAlarmOn) {
                                            Toast.makeText(contextHelper.getActivity(), "알람을 껏습니다.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(contextHelper.getActivity(), "알람을 켰습니다.", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                    break;
                                }
                            }
                        })
                        .show();
                break;
        }
    }

    public void onEvent(PushMessage pushMessage) {
        switch(pushMessage.getActionCode()) {
        }
    }


    public void loadList(final List list) {
        super.loadList(list);

        isLoading = true;
        ChatManager.getInstance(contextHelper).find(user.getId(), to.getId(),
                list == null ? 1 : list.getPages().getNext(),
                new Response.Listener<ChatListData>() {
                    @Override
                    public void onResponse(ChatListData response) {
                        if (response.getChatList() != null) {
                            chats = response.getChatList().getChats();
                            makeList();
                            postLoad(response.getChatList());
                            scrollToBottom();
                        }

                        talk = response.talk;
                        isLoading = false;
                        tvTitleNodata.setText("");

                        EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_CHANGETALK).setObject(response.talk));
                    }
                },
                null
        );
    }

    void sendMessage(String message) {
        if(isLoading)
            return;

        if(!TextUtils.isEmpty(message)) {

            Chat chat = new Chat();
            chat.setFrom(user);
            chat.setMessage(message);
            chat.setTo(to);
            chat.setType(Chat.TYPE_MESSAGE);
            chat.setTimeCreated(new Date());

            chat.setIsRead(toConnected);

            addChat(chat);

            etMessage.setText("");
            socket.emit("message", chat.toString());
        }
    }


    void addChat(Chat chat) {
        chats.add(chat);
        makeList();
        chatAdapter.notifyDataSetChanged();
        scrollToBottom();


        Talk talk = new Talk();
        talk.setTimeUpdated(chat.getTimeCreated());
        talk.setMessageRecent(chat.getMessage());
        talk.setRoomName(roomName);

        EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_CHANGETALK).setObject(talk));
    }


    void makeList() {
        chatUIList.getChats().clear();
        Date dateCur = null;
        for (Chat chat : chats) {

            if (dateCur == null || !DateUtil.isSameDay(dateCur, chat.getTimeCreated())) {
                dateCur = chat.getTimeCreated();

                ChatDate chatDate = new ChatDate();
                chatDate.date = dateCur;

                chatUIList.getChats().add(chatDate);
            }
            chatUIList.getChats().add(chat);
        }
        chatAdapter.setData(chatUIList.getChats());
    }

    void toAllread() {
        for (Chat chat : chats) {
            if (chat.getTo().isSame(to)) {
                chat.setIsRead(true);
            }
        }
        makeList();
        chatAdapter.notifyDataSetChanged();
        scrollToBottom();
    }


    private Emitter.Listener handleIncomingMessages = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            contextHelper.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Chat chat = Chat.getChat((String)args[0]);

                    addChat(chat);
                }
            });
        }
    };
    private Emitter.Listener handleUpdate = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            contextHelper.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String username = (String) args[0];
                    String status = (String) args[1];

                    if(status.equals("connecting")) {
                        toConnected = true;
                        toAllread();
                    }
                    else
                        toConnected = false;
                    //Logger.debug(TAG, "handleUpdate() " + username + " is " + status);
                }
            });
        }
    };
}


