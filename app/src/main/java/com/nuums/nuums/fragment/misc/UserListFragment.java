package com.nuums.nuums.fragment.misc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.nuums.nuums.R;
import com.nuums.nuums.adapter.UserListAdapter;
import com.nuums.nuums.model.user.NsUserList;
import com.nuums.nuums.model.user.NsUserListData;
import com.yongtrim.lib.fragment.ListFragment;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.model.list.List;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.ui.UltraEditText;
import com.yongtrim.lib.ui.UltraListView;

import org.json.JSONObject;

/**
 * Created by YongTrim on 16. 6. 15. for nuums_ad
 */
public class UserListFragment extends ListFragment {
    private final String TAG = getClass().getSimpleName();

    UserListAdapter userListAdapter;
    NsUserList userList;
    View mainView;
    TextView tvTitleNodata;

    public static int LISTTYPE_SEARCH = 0;
    public static int LISTTYPE_BLOCK = 1;

    int listType = LISTTYPE_SEARCH;

    UltraEditText etSearch;
    String keyword;




    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        listType = getActivity().getIntent().getIntExtra("listType", LISTTYPE_SEARCH);

        if(listType == LISTTYPE_SEARCH) {
            etSearch = contextHelper.getActivity().setupActionBarSearch();
            etSearch.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            etSearch.setHint("닉네임");
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
        } else {
            contextHelper.getActivity().setupActionBar("차단목록 관리");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        mainView = inflater.inflate(R.layout.fragment_userlist, container, false);

        UltraListView listView = (UltraListView)mainView.findViewById(R.id.listView);
        userListAdapter = new UserListAdapter(contextHelper, listType);

        if(userList == null) {
            userList = new NsUserList();
        }

        userListAdapter.setData(userList.getUsers());
        listView.setAdapter(userListAdapter);

        setupView(listView, mainView.findViewById(R.id.viewNodata), mainView.findViewById(R.id.swipeRefreshLayout), userListAdapter);
        setListInfo(userList);
        setAddedLayer(mainView.findViewById(R.id.layerTop), mainView.findViewById(R.id.layerBottom));

        tvTitleNodata = (TextView)mainView.findViewById(R.id.tvTitleNodata);

        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

        if(listType == LISTTYPE_SEARCH) {
            tvTitleNodata.setText("닉네임으로 회원을 찾아보세요.");
        } else {
            tvTitleNodata.setText("로딩중...");
            loadList(null);
        }

        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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



    public void onEvent(final PushMessage pushMessage) {
        switch(pushMessage.getActionCode()) {
            case PushMessage.ACTIONCODE_CHANGE_ME:
                loadList(null);
                break;
        }
    }


    public void onButtonClicked(View v) {
        super.onButtonClicked(v);
        switch (v.getId()) {
            case R.id.actionbarImageButton:
                find(etSearch.getText().toString());
                break;

        }
    }

    public synchronized void loadList(final List list) {
        JSONObject option = new JSONObject();


        try {
            if(listType == LISTTYPE_SEARCH) {
                option.put("nickname", keyword);
            } else {
                option.put("block", true);
            }
        } catch(Exception e) {

        }

        UserManager.getInstance(contextHelper).find(list == null ? 1 : list.getPages().getNext(), option,
                new Response.Listener<NsUserListData>() {
                    @Override
                    public void onResponse(NsUserListData response) {

                        if(listType == LISTTYPE_SEARCH)
                            tvTitleNodata.setText("찾은 회원이 없습니다.");
                        else
                            tvTitleNodata.setText("차단한 회원이 없습니다.");
                        if (list == null) {
                            userList.getUsers().clear();
                        }
                        userList.getUsers().addAll(response.getUserList().getUsers());
                        userListAdapter.setData(userList.getUsers());
                        postLoad(response.getUserList());
                        contextHelper.hideProgress();

                        if(etSearch != null)
                            etSearch.setText("");
                    }
                },
                null
        );

    }
}


