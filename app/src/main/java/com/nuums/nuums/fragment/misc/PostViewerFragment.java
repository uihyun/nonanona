package com.nuums.nuums.fragment.misc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.nuums.nuums.R;
import com.yongtrim.lib.adapter.ImagePagerAdapter;
import com.yongtrim.lib.fragment.ABaseFragment;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.model.post.Post;
import com.yongtrim.lib.model.post.PostData;
import com.yongtrim.lib.model.post.PostManager;
import com.yongtrim.lib.ui.autoscrollviewpager.AutoScrollViewPager;
import com.yongtrim.lib.ui.viewpagerindicator.CirclePageIndicator;
import com.yongtrim.lib.util.UIUtil;

/**
 * nuums / com.nuums.nuums.fragment.misc
 * <p/>
 * Created by Uihyun on 16. 1. 2..
 */
public class PostViewerFragment extends ABaseFragment {

    Post post;

    View viewTop;
    AutoScrollViewPager viewPhotoMain;
    CirclePageIndicator pageIndicator;

    ImagePagerAdapter imagePagerMainAdapter;
    TextView tvTitle;
    TextView tvContent;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        if (contextHelper.getActivity().getIntent().hasExtra("post")) {
            post = Post.getPost(contextHelper.getActivity().getIntent().getStringExtra("post"));
            contextHelper.getActivity().setupActionBar(post.getTitle());
        } else if (contextHelper.getActivity().getIntent().hasExtra("title")) {
            contextHelper.getActivity().setupActionBar(contextHelper.getActivity().getIntent().getStringExtra("title"));
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        View view = inflater.inflate(R.layout.fragment_postviewer, container, false);

        viewTop = view.findViewById(R.id.viewTop);
        UIUtil.setRatio(viewTop, getContext(), 1, 1);
        viewPhotoMain = (AutoScrollViewPager) view.findViewById(R.id.viewPhoto);
        pageIndicator = (CirclePageIndicator) view.findViewById(R.id.pageIndicator);

        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvContent = (TextView) view.findViewById(R.id.tvContent);


        return view;
    }

    public void refresh() {

        if (post.getPhotos().size() > 0) {
            viewTop.setVisibility(View.VISIBLE);
            if (imagePagerMainAdapter == null) {
                imagePagerMainAdapter = new ImagePagerAdapter(getContext(), post.getMediumUrlArray()).setInfiniteLoop(false);
                viewPhotoMain.setAdapter(imagePagerMainAdapter);
            }

            String urlTag = post.getUrlTag(true);
            if (!urlTag.equals(imagePagerMainAdapter.getUrlTag())) {
                imagePagerMainAdapter = new ImagePagerAdapter(getContext(), post.getMediumUrlArray()).setInfiniteLoop(false);
                viewPhotoMain.setAdapter(imagePagerMainAdapter);
            }
            pageIndicator.setViewPager(viewPhotoMain);

            imagePagerMainAdapter.setUrlTag(urlTag);
        } else {
            viewTop.setVisibility(View.GONE);
        }

        if (post.getTitle() != null) {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(post.getTitle());
        } else {
            tvTitle.setVisibility(View.GONE);

        }

        if (post.getContent() != null) {
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText(post.getContent());

//            tvContent.setText(Html.fromHtml(post.getContent()));
//            tvContent.setMovementMethod(LinkMovementMethod.getInstance());

        } else {
            tvContent.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (post != null) {
            refresh();
        } else if (contextHelper.getActivity().getIntent().hasExtra("type")) {
            contextHelper.showProgress(null);
            PostManager.getInstance(contextHelper).read(
                    contextHelper.getActivity().getIntent().getStringExtra("type"),
                    new Response.Listener<PostData>() {
                        @Override
                        public void onResponse(PostData response) {
                            if (response.isSuccess()) {
                                post = response.post;
                                contextHelper.getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            contextHelper.hideProgress();
                                            refresh();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }
                    },
                    null
            );
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onButtonClicked(View v) {
    }


    public void onEvent(PushMessage pushMessage) {
        switch (pushMessage.getActionCode()) {

        }
    }
}


