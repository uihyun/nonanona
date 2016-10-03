package com.nuums.nuums.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.nuums.nuums.R;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.model.postcode.PostCode;
import com.yongtrim.lib.ui.CircularNetworkImageView;
import com.yongtrim.lib.ui.UltraButton;
import com.yongtrim.lib.ui.sweetalert.SweetAlertDialog;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * nuums / com.nuums.nuums.adapter
 * <p/>
 * Created by Uihyun on 15. 12. 20..
 */
public class PostcodeAdapter extends BaseAdapter {

    private List<PostCode> postCodes;

    class Holder {

        TextView tvCode;
        TextView tvOld;
        TextView tvNew;


        public View set(View v) {
            v.setTag(this);

            tvCode = (TextView)v.findViewById(R.id.tvCode);
            tvOld = (TextView)v.findViewById(R.id.tvOld);

            tvNew = (TextView)v.findViewById(R.id.tvNew);
            return v;
        }
    }

    public PostcodeAdapter() {
        super();
    }


    public void setData(List<PostCode> postCodes) {
        this.postCodes = postCodes;
    }



    public List<PostCode> getData() {
        return this.postCodes;
    }


    @Override
    public int getCount() {
        if(postCodes == null)
            return 0;
        return postCodes.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return postCodes.get(position);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)parent.getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = new Holder().set(mInflater.inflate(R.layout.cell_postcode, parent, false));
        }
        final Holder h = (Holder)convertView.getTag();

        final PostCode postCode = postCodes.get(position);

        h.tvCode.setText(postCode.code);
        h.tvOld.setText(postCode.oldAddress);
        h.tvNew.setText(postCode.newAddress);

        return convertView;
    }
}
