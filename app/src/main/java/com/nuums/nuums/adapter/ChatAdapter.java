package com.nuums.nuums.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nuums.nuums.AppController;
import com.nuums.nuums.R;
import com.nuums.nuums.activity.BaseActivity;
import com.nuums.nuums.activity.GalleryActivity;
import com.nuums.nuums.model.chat.Address;
import com.nuums.nuums.model.chat.Chat;
import com.nuums.nuums.model.chat.ChatDate;
import com.nuums.nuums.model.chat.Delivery;
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.photo.PhotoManager;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.ui.CircularNetworkImageView;
import com.yongtrim.lib.ui.CustomNetworkImageView;
import com.yongtrim.lib.ui.UltraButton;
import com.yongtrim.lib.util.DateUtil;

/**
 * nuums / com.nuums.nuums.adapter
 * <p/>
 * Created by Uihyun on 16. 1. 6..
 */
public class ChatAdapter extends BaseAdapter {

    private ListView listView;
    private ContextHelper contextHelper;

    private java.util.ArrayList<Chat> chats;

    class Holder {

        CircularNetworkImageView ivAvatar;
        TextView tvNickname;
        TextView tvTime;
        TextView tvMessage;
        TextView tvUnread;

        View viewAddress;
        TextView tvAddressTitle;
        TextView tvAddressName;
        TextView tvAddressPhoneNumber;
        TextView tvAddressAddress;
        UltraButton btnAddressConfirm;

        View viewDelivery;
        TextView tvDeliveryTitle;
        TextView tvDeliveryCompany;
        TextView tvDeliveryNumber;
        UltraButton btnDeliveryConfirm;

        CustomNetworkImageView ivPhoto;


        public View set(View v) {
            v.setTag(this);
            ivAvatar = (CircularNetworkImageView)v.findViewById(R.id.ivAvatar);

            tvNickname = (TextView) v.findViewById(R.id.tvNickname);
            tvTime = (TextView) v.findViewById(R.id.tvTime);
            tvMessage = (TextView) v.findViewById(R.id.tvMessage);
            tvUnread = (TextView) v.findViewById(R.id.tvUnread);

            viewAddress = v.findViewById(R.id.viewAddress);
            tvAddressTitle = (TextView)v.findViewById(R.id.tvAddressTitle);
            tvAddressName = (TextView)v.findViewById(R.id.tvAddressName);
            tvAddressPhoneNumber = (TextView)v.findViewById(R.id.tvAddressPhoneNumber);
            tvAddressAddress = (TextView)v.findViewById(R.id.tvAddressAddress);
            btnAddressConfirm = (UltraButton)v.findViewById(R.id.btnAddressConfirm);

            viewDelivery = v.findViewById(R.id.viewDelivery);
            tvDeliveryTitle = (TextView)v.findViewById(R.id.tvDeliveryTitle);
            tvDeliveryCompany = (TextView)v.findViewById(R.id.tvDeliveryCompany);
            tvDeliveryNumber = (TextView)v.findViewById(R.id.tvDeliveryNumber);
            btnDeliveryConfirm = (UltraButton)v.findViewById(R.id.btnDeliveryConfirm);

            ivPhoto = (CustomNetworkImageView)v.findViewById(R.id.ivPhoto);

            return v;
        }
    }


    class HolderDate {

        UltraButton tvDate;

        public View set(View v) {
            v.setTag(this);

            tvDate = (UltraButton) v.findViewById(R.id.tvDate);
            return v;
        }
    }




    public ChatAdapter(ContextHelper contextHelper, ListView listView) {
        super();
        this.listView = listView;
        this.contextHelper = contextHelper;
    }


    public void setData(java.util.ArrayList<Chat> chats) {
        this.chats = chats;
    }


    @Override
    public int getCount() {
        if(chats == null)
            return 0;
        return chats.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return chats.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        Chat chat = chats.get(position);
        NsUser from = chat.getFrom();

        if(chat instanceof ChatDate) {
            return 2;
        }
        else if(UserManager.getInstance(contextHelper).isMe(from))
            return 0;
        else
            return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Chat chat = chats.get(position);

        int celltype = getItemViewType(position);


        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)parent.getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            switch(celltype) {
                case 0:
                    convertView = new Holder().set(mInflater.inflate(R.layout.cell_chat_me, parent, false));
                    break;
                case 1:
                    convertView = new Holder().set(mInflater.inflate(R.layout.cell_chat_you, parent, false));
                    break;
                case 2:
                    convertView = new HolderDate().set(mInflater.inflate(R.layout.cell_chat_date, parent, false));
                    break;
            }
        }


        if(celltype == 0 || celltype == 1) {
            final Holder h = (Holder) convertView.getTag();
            final NsUser from = chat.getFrom();

            if(celltype == 1) {
                h.tvNickname.setText(from.getNicknameSafe());
                PhotoManager.getInstance(contextHelper).setPhotoSmall(h.ivAvatar, from.getPhoto());

            } else {

                if(chat.isRead()) {
                    h.tvUnread.setVisibility(View.GONE);
                } else {
                    h.tvUnread.setVisibility(View.VISIBLE);
                }
            }
            h.tvTime.setText(DateUtil.dateToString(chat.getTimeCreated(), "a hh:mm"));

            h.tvMessage.setVisibility(View.GONE);
            h.viewAddress.setVisibility(View.GONE);
            h.viewDelivery.setVisibility(View.GONE);
            h.ivPhoto.setVisibility(View.GONE);

            switch(chat.getType()) {
                case Chat.TYPE_MESSAGE:
                    h.tvMessage.setVisibility(View.VISIBLE);
                    h.tvMessage.setText(chat.getMessage());

                    break;

                case Chat.TYPE_PHOTO:
                    h.ivPhoto.setVisibility(View.VISIBLE);
                    h.ivPhoto.setImageUrl(chat.getPhoto().getUrl(), AppController.getInstance().getImageLoader());
                    h.ivPhoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent galleryActivity = new Intent(contextHelper.getActivity(), GalleryActivity.class);
                            galleryActivity.putExtra("photo", chat.getPhoto().toString());
                            contextHelper.getActivity().startActivity(galleryActivity);
                        }
                    });
                    break;
                case Chat.TYPE_ADDRESS: {
                    h.viewAddress.setVisibility(View.VISIBLE);

                    String str0 = from.getNicknameSafe();
                    String str1 = " 님이 주소를 입력하였습니다.";
                    SpannableString text = new SpannableString(str0 + str1);
                    text.setSpan(new ForegroundColorSpan(ContextCompat.getColor(contextHelper.getContext(), R.color.green)), 0, str0.length(), 0);
                    text.setSpan(new RelativeSizeSpan(1.3f), 0, str0.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    h.tvAddressTitle.setText(text);


                    final Address address = chat.getAddress();

                    h.tvAddressName.setText(address.getName());
                    h.tvAddressPhoneNumber.setText(address.getNumber0() + "-" + address.getNumber1() + "-" + address.getNumber2());
                    h.tvAddressAddress.setText("(" + address.getPostCode() + ")" + address.getAddressBasic() + " " + address.getAddressDetail());

                    if (UserManager.getInstance(contextHelper).isMe(from)) {
                        h.btnAddressConfirm.setVisibility(View.GONE);
                        h.viewAddress.setBackgroundResource(R.drawable.talkgreen);
                    } else {
                        h.btnAddressConfirm.setVisibility(View.VISIBLE);
                        h.viewAddress.setBackgroundResource(R.drawable.talkwhite);

                        h.btnAddressConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(contextHelper.getContext(), BaseActivity.class);
                                i.putExtra("activityCode", BaseActivity.ActivityCode.APPLY.ordinal());
                                i.putExtra("receiver", address.toString());
                                contextHelper.getActivity().startActivity(i);
                            }
                        });
                    }
                }
                break;
                case Chat.TYPE_DELIVERY: {

                    h.viewDelivery.setVisibility(View.VISIBLE);

                    String str0 = from.getNicknameSafe();
                    String str1 = " 님이 운송장을 입력하였습니다.";
                    SpannableString text = new SpannableString(str0 + str1);
                    text.setSpan(new ForegroundColorSpan(ContextCompat.getColor(contextHelper.getContext(), R.color.green)), 0, str0.length(), 0);
                    text.setSpan(new RelativeSizeSpan(1.3f), 0, str0.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    h.tvDeliveryTitle.setText(text);

                    final Delivery delivery = chat.getDelivery();

                    h.tvDeliveryCompany.setText(delivery.getCompany().getName());
                    h.tvDeliveryNumber.setText(delivery.getNumber());

                    if (UserManager.getInstance(contextHelper).isMe(from)) {
                        h.viewAddress.setBackgroundResource(R.drawable.talkgreen);
                        h.viewDelivery.setBackgroundResource(R.drawable.talkgreen);
                    } else {
                        h.viewAddress.setBackgroundResource(R.drawable.talkwhite);
                        h.viewDelivery.setBackgroundResource(R.drawable.talkwhite);
                    }

                    h.btnDeliveryConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(delivery.getCompany().getUrl() + delivery.getNumber()));
                            contextHelper.getActivity().startActivity(browserIntent);
                        }
                    });
                }
                break;

            }


        } else if(celltype == 2) {
            ChatDate chatDate = (ChatDate)chat;

            final HolderDate h = (HolderDate) convertView.getTag();

            h.tvDate.setText(DateUtil.dateToString(chatDate.date, "yyyy.MM.dd.E요일"));
        }

        return convertView;
    }

}



