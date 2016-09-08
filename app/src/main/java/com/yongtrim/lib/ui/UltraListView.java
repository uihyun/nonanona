package com.yongtrim.lib.ui;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.nuums.nuums.R;


/**
 * hair / com.yongtrim.lib.ui
 * <p/>
 * Created by yongtrim.com on 15. 9. 14..
 */
public class UltraListView extends ListView implements AbsListView.OnScrollListener{
    private static final String TAG = "UltraListview";

    private View mViewFooter;
    private OnScrollListener mScrollListener;
    private OnUltraListener mUltraListener;

    private boolean mIsLoadingMore;
    private int mCurrentScrollState;

    private View mViewRefresh;

    public UltraListView(Context context) {
        super(context);
        init(context);
    }

    public UltraListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public UltraListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mViewFooter = (View)inflater.inflate(R.layout.view_footer, this, false);

        addFooterView(mViewFooter);

        super.setOnScrollListener(this);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
    }


    @Override
    public void setOnScrollListener(AbsListView.OnScrollListener listener) {
        mScrollListener = listener;
    }


    //
    // AbsListView.OnScrollListener
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(mScrollListener != null) {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }


        if (mViewRefresh != null) {
           boolean enable = false;
            if (getChildCount() > 0) {
                // check if the first item of the list is visible
                boolean firstItemVisible = getFirstVisiblePosition() == 0;
                // check if the top of the first item is visible
                boolean topOfFirstItemVisible = getChildAt(0).getTop() == 0;
                // enabling or disabling the refresh layout
                enable = firstItemVisible && topOfFirstItemVisible;
            }
            mViewRefresh.setEnabled(enable);
        }


        if (visibleItemCount == totalItemCount) {
            mViewFooter.setVisibility(View.GONE);
            return;
        }

        boolean needLoadMore = firstVisibleItem + visibleItemCount >= totalItemCount;

        if(!mIsLoadingMore && needLoadMore && mCurrentScrollState != SCROLL_STATE_IDLE) {
            mViewFooter.setVisibility(View.VISIBLE);
            mIsLoadingMore = true;
            if (mUltraListener != null) {
                mUltraListener.onLoadMore();
            }
        }

    }


    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
            view.invalidateViews();
        }

        mCurrentScrollState = scrollState;

        if (mScrollListener != null) {
            mScrollListener.onScrollStateChanged(view ,scrollState);
        }
    }



    public interface OnUltraListener {
        public void onLoadMore();
        public void onRefresh();
    }

    public void setOnLoadMoreListener(OnUltraListener listener) {
        mUltraListener = listener;
    }


    public void setSwipeRefreshLayout(SwipeRefreshLayout viewRefresh) {
        mViewRefresh = (View)viewRefresh;
        viewRefresh.setColorSchemeResources(R.color.green, R.color.green, R.color.green);
        viewRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mUltraListener.onRefresh();
            }
        });
    }

    public void onLoadMoreComplete() {
        mIsLoadingMore = false;
        mViewFooter.setVisibility(View.GONE);

        if(mViewRefresh != null) {
            ((SwipeRefreshLayout)mViewRefresh).setRefreshing(false);
        }
    }



    private int mItemCount;
    private int mItemOffsetY[];
    private boolean scrollIsComputed = false;
    private int mHeight;

    public int getListHeight() {
        return mHeight;
    }

    public int computeScrollY() {
        mHeight = 0;
        mItemCount = getAdapter().getCount();
        //if (mItemOffsetY == null) {
        mItemOffsetY = new int[mItemCount];
        //}
        for (int i = 0; i < mItemCount; ++i) {
            View view = getAdapter().getView(i, null, this);
            view.measure(
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            mItemOffsetY[i] = mHeight;
            mHeight += view.getMeasuredHeight();
            //System.out.println(mHeight);
        }
        scrollIsComputed = true;
        return mHeight;
    }

    public boolean scrollYIsComputed() {
        return scrollIsComputed;
    }

    public int getComputedScrollY() {
        int pos, nScrollY = 0, nItemY;
        View view = null;
        pos = getFirstVisiblePosition();
        view = getChildAt(0);
        if(view != null) {
            nItemY = view.getTop();
            if(pos >= mItemCount)
                return 0;
            nScrollY = mItemOffsetY[pos] - nItemY;
        }
        return nScrollY;
    }


    public void removeFooter() {
        removeFooterView(mViewFooter);
    }

    public void addFooter() {
        if(this.getFooterViewsCount() > 0)
            return;
        addFooterView(mViewFooter);
    }
}
