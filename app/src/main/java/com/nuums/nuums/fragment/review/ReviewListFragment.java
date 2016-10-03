package com.nuums.nuums.fragment.review;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.nuums.nuums.R;
import com.nuums.nuums.activity.BaseActivity;
import com.nuums.nuums.adapter.ReviewAdapter;
import com.nuums.nuums.model.review.Review;
import com.nuums.nuums.model.review.ReviewList;
import com.nuums.nuums.model.review.ReviewListData;
import com.nuums.nuums.model.review.ReviewManager;
import com.nuums.nuums.model.user.NsUser;
import com.nuums.nuums.view.HeaderView;
import com.yongtrim.lib.fragment.ListFragment;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.model.list.List;
import com.yongtrim.lib.model.photo.PhotoManager;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.ui.CircularNetworkImageView;
import com.yongtrim.lib.ui.UltraButton;
import com.yongtrim.lib.ui.UltraListView;
import com.yongtrim.lib.util.PixelUtil;

/**
 * nuums / com.nuums.nuums.fragment.review
 * <p/>
 * Created by Uihyun on 15. 12. 20..
 */
public class ReviewListFragment extends ListFragment {
    private final String TAG = getClass().getSimpleName();

    ReviewAdapter reviewAdapter;
    ReviewList reviewList;

    View mainView;
    TextView tvTitleNodata;

    UltraButton btnAdd;

    NsUser owner;

    final String LISTTYPE_ALL = "ALL";
    final String LISTTYPE_MINE = "MINE";

    String type = LISTTYPE_ALL;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        if(contextHelper.getActivity().getIntent().hasExtra("owner")) {
            owner = NsUser.getUser(contextHelper.getActivity().getIntent().getStringExtra("owner"));
            type = LISTTYPE_MINE;
            contextHelper.getActivity().setupActionBar(owner.getNicknameSafe() + "님의 후기");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        mainView = inflater.inflate(R.layout.fragment_reviewlist, container, false);

        contextHelper.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        btnAdd = (UltraButton)mainView.findViewById(R.id.btnAdd);

        UltraListView listView = (UltraListView)mainView.findViewById(R.id.listView);
        reviewAdapter = new ReviewAdapter(contextHelper);

        if(reviewList == null) {
            reviewList = new ReviewList();
        }

        reviewAdapter.setData(reviewList.getReviews());
        listView.setAdapter(reviewAdapter);

        setupView(listView, mainView.findViewById(R.id.viewNodata), mainView.findViewById(R.id.swipeRefreshLayout), reviewAdapter);
        setListInfo(reviewList);
        setAddedLayer(mainView.findViewById(R.id.layerTop), mainView.findViewById(R.id.layerBottom));

        tvTitleNodata = (TextView)mainView.findViewById(R.id.tvTitleNodata);

        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Nanum nanumSelected = nanumList.getNanums().get((int) position);
//                Intent i = new Intent(getContext(), BaseActivity.class);
//                i.putExtra("activityCode", BaseActivity.ActivityCode.NANUMVIEWER.ordinal());
//                i.putExtra("nanum", nanumSelected.toString());
//                getContext().startActivity(i);
            }
        });

        HeaderView headerView = new HeaderView(getContext());
        headerView.addView(PixelUtil.dpToPx(getContext(), 40));
        setHeader(headerView);

        refreshTop();
        tvTitleNodata.setText("로딩중...");
        loadList(null);

        listView.setOnScrollListener(new AbsListView.OnScrollListener(){
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                try {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                    if(reviewAdapter.editTextFocus != null) {
                        reviewAdapter.editTextFocus.clearFocus();
                    }
                } catch(Exception e) {

                }
            }
        });

        if(type.equals(LISTTYPE_MINE)) {
            mainView.findViewById(R.id.viewLine).setVisibility(View.GONE);
        }
        return mainView;
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


    @Override
    public void onPause() {
        super.onPause();
    }

    public void refreshTop() {

        if(owner == null)
            owner = UserManager.getInstance(contextHelper).getMe();

        if(owner.isMe(contextHelper))
            owner = UserManager.getInstance(contextHelper).getMe(); // 게시물 갯수 변경때문에..

        CircularNetworkImageView ivAvatar = (CircularNetworkImageView)mainView.findViewById(R.id.ivAvatar);
        PhotoManager.getInstance(contextHelper).setPhotoSmall(ivAvatar, owner.getPhoto());

        TextView tvNickname = (TextView)mainView.findViewById(R.id.tvNickname);
        tvNickname.setText(owner.getNicknameSafe());

        if(type.equals(LISTTYPE_MINE)) {
            mainView.findViewById(R.id.btnAdd).setVisibility(View.GONE);

            mainView.findViewById(R.id.viewStatus).setVisibility(View.VISIBLE);

            TextView tvMyReviewCount = (TextView)mainView.findViewById(R.id.tvMyReviewCount);
            tvMyReviewCount.setText("" + owner.getReviews().size());
        }
    }


    public void onEvent(final PushMessage pushMessage) {
        switch(pushMessage.getActionCode()) {
            case PushMessage.ACTIONCODE_CHANGE_ME: {
                contextHelper.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        refreshTop();
                    }
                });
            }
            break;
            case PushMessage.ACTIONCODE_ADDED_REVIEW:
            case PushMessage.ACTIONCODE_DELETE_REVIEW:{
                try {
                    contextHelper.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadList(null);

                        }
                    });

                } catch(Exception e) {

                }
            }
            break;
            case PushMessage.ACTIONCODE_CHANGE_REVIEW: {
                Review review = (Review)pushMessage.getObject(contextHelper);

                java.util.List<Review> reviews = reviewAdapter.getData();
                for(int i = 0;i < reviews.size();i++) {
                    if(reviews.get(i).isSame(review)) {
                        reviews.set(i, review);
                        break;
                    }
                }
                contextHelper.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        reviewAdapter.notifyDataSetChanged();
                    }
                });
            }
            break;
        }
    }


    public void onButtonClicked(View v) {
        super.onButtonClicked(v);
        switch (v.getId()) {
            case R.id.btnAddInBottom:
            case R.id.btnAdd: {
                    Intent i = new Intent(getContext(), BaseActivity.class);
                    i.putExtra("activityCode", BaseActivity.ActivityCode.REVIEWEDIT.ordinal());
                    getContext().startActivity(i);
                }
                break;
            case R.id.viewProfile: {
                if(type.equals(LISTTYPE_ALL)) {
                    Intent i = new Intent(getContext(), BaseActivity.class);
                    i.putExtra("activityCode", BaseActivity.ActivityCode.REVIEWLIST.ordinal());
                    i.putExtra("owner", UserManager.getInstance(contextHelper).getMe().toString());
                    getContext().startActivity(i);
                }
            }
        }
    }

    public void loadList(final List list) {

        if(owner == null)
            owner = UserManager.getInstance(contextHelper).getMe();

        super.loadList(list);

        ReviewManager.getInstance(contextHelper).find(owner.getId(),
                type,
                list == null ? 1 : list.getPages().getNext(), 20, null,
                new Response.Listener<ReviewListData>() {
                    @Override
                    public void onResponse(ReviewListData response) {
                        tvTitleNodata.setText("후기가 없습니다.");
                        response.getReviewList().patch(contextHelper);

                        if (list == null) {
                            reviewList.getReviews().clear();
                        }
                        reviewList.getReviews().addAll(response.getReviewList().getReviews());
                        reviewAdapter.setData(reviewList.getReviews());
                        postLoad(response.getReviewList());
                        contextHelper.hideProgress();
                        refreshTop();
                    }
                },
                null
        );
    }


}


