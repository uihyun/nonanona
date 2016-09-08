package com.nuums.nuums.view;

import android.content.Context;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import com.nuums.nuums.R;

/**
 * nuums / com.nuums.nuums.view
 * <p/>
 * Created by yongtrim.com on 15. 12. 22..
 */
public class HeaderView extends LinearLayout {

    public HeaderView(Context context) {
        super(context);

        setOrientation(VERTICAL);

        AbsListView.LayoutParams containerParams = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        setLayoutParams(containerParams);
    }


    public void addView(View view) {
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(containerParams);
        super.addView(view);
    }

    public void addView(int height) {
        LinearLayout view = new LinearLayout(getContext());
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        containerParams.height = height;
        view.setLayoutParams(containerParams);
        view.setId(R.id.placeholder);
        super.addView(view);
    }
}


