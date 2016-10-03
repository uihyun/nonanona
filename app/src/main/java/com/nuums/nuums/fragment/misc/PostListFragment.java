package com.nuums.nuums.fragment.misc;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.nuums.nuums.R;
import com.nuums.nuums.adapter.ApplyAdapter;
import com.nuums.nuums.adapter.PostAdapter;
import com.nuums.nuums.fragment.mypage.MypageMainFragment;
import com.nuums.nuums.model.apply.Apply;
import com.nuums.nuums.model.apply.ApplyList;
import com.nuums.nuums.model.apply.ApplyListData;
import com.nuums.nuums.model.apply.ApplyManager;
import com.nuums.nuums.model.report.Report;
import com.nuums.nuums.view.HeaderView;
import com.nuums.nuums.view.MypageView;
import com.yongtrim.lib.fragment.ListFragment;
import com.yongtrim.lib.log.Logger;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.model.list.List;
import com.yongtrim.lib.model.post.Post;
import com.yongtrim.lib.model.post.PostList;
import com.yongtrim.lib.model.post.PostListData;
import com.yongtrim.lib.model.post.PostManager;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.ui.PagerSlidingTabStrip2;
import com.yongtrim.lib.ui.UltraListView;
import com.yongtrim.lib.util.PixelUtil;

import de.greenrobot.event.EventBus;

/**
 * nuums / com.nuums.nuums.fragment.misc
 * <p/>
 * Created by Uihyun on 16. 1. 30..
 */
public class PostListFragment extends ListFragment {
    private final String TAG = getClass().getSimpleName();

    PostAdapter postAdapter;
    PostList postList;

    UltraListView listView;
    TextView tvTitleNodata;

    String type;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        type = contextHelper.getActivity().getIntent().getStringExtra("type");

        if(type.equals(Post.TYPE_FAQ)) {
            contextHelper.getActivity().setupActionBar("자주묻는질문");
        } else if(type.equals(Post.TYPE_NOTICE)) {
            contextHelper.getActivity().setupActionBar("공지사항");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        View view = inflater.inflate(R.layout.fragment_postlist, container, false);

        listView = (UltraListView)view.findViewById(R.id.listView);
        postAdapter = new PostAdapter(contextHelper, listView);

        if(postList == null) {
            postList = new PostList();
        }
        postAdapter.setData(postList.getPosts());
        listView.setAdapter(postAdapter);

        setupView(listView, view.findViewById(R.id.viewNodata), view.findViewById(R.id.swipeRefreshLayout), postAdapter);
        setListInfo(postList);
        setAddedLayer(view.findViewById(R.id.layerTop), view.findViewById(R.id.layerBottom));

        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

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
    }


    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
    }

    public void onEvent(PushMessage pushMessage) {
        switch(pushMessage.getActionCode()) {
            case PushMessage.ACTIONCODE_ADDED_POST_OTHER:
                loadList(null);
                break;
        }
    }


    public void loadList(final List list) {
        super.loadList(list);

        PostManager.getInstance(contextHelper).find(
                type, list == null ? 1 : list.getPages().getNext(),
                new Response.Listener<PostListData>() {
                    @Override
                    public void onResponse(PostListData response) {
                        Logger.debug(TAG, "find() | result = " + response.toString());

                        if(type.equals(Post.TYPE_NOTICE))
                            tvTitleNodata.setText("공지사항이 없습니다.");
                        else if(type.equals(Post.TYPE_FAQ))
                            tvTitleNodata.setText("자주묻는질문이 없습니다.");

                        if (list == null) {
                            postList.getPosts().clear();
                        }

                        postList.getPosts().addAll(response.getPostList().getPosts());
                        postAdapter.setData(postList.getPosts());
                        postLoad(response.getPostList());
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

    public void onButtonClicked(View v) {
        super.onButtonClicked(v);
        switch (v.getId()) {
        }
    }

}

