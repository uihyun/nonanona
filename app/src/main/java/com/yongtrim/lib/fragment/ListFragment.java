package com.yongtrim.lib.fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import com.nuums.nuums.R;
import com.yongtrim.lib.model.list.List;
import com.yongtrim.lib.ui.UltraListView;

/**
 * hair / com.yongtrim.lib.fragment
 * <p/>
 * Created by Uihyun on 15. 9. 15..
 */
public class ListFragment extends ABaseFragment {

    final String TAG = "ListFragment";
    final int STATE_ONSCREEN = 0;
    final int STATE_OFFSCREEN = 1;
    final int STATE_RETURNING = 2;
    protected UltraListView listView;
    protected SwipeRefreshLayout refreshLayout;
    View viewNodata;
    BaseAdapter listAdapter;
    List list;
    View mHeader;
    View mPlaceHolder;
    int mCachedVerticalScrollRange;
    View layerTop;
    int nTopHeight;
    View layerBottom;
    int nBottomHeight;
    int mScrollY;
    int mState_Bottom = STATE_ONSCREEN;
    int mMinRawY_Bottom = 0;
    int mState_Top = STATE_ONSCREEN;
    int mMinRawY_Top = 0;
    TranslateAnimation anim;

    protected void setupView(final UltraListView listView, View viewNoddate, View refreshLayout, BaseAdapter listAdapter) {
        this.viewNodata = viewNoddate;
        this.listAdapter = listAdapter;
        this.listView = listView;

        if (refreshLayout != null) {
            this.refreshLayout = (SwipeRefreshLayout) refreshLayout;
            listView.setSwipeRefreshLayout(this.refreshLayout);
        }

        listView.setOnLoadMoreListener(new UltraListView.OnUltraListener() {
            public void onLoadMore() {
                if (list != null && list.getPages().isHasNext()) {
                    loadList(list);
                } else {
                    listView.onLoadMoreComplete();
                }
            }

            public void onRefresh() {
                loadList(null);
            }
        });

        checkListZeroSize();

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @SuppressLint("NewApi")
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                mScrollY = 0;
                int translationY_Bottom = 0;
                int translationY_Top = 0;

                if (listView.scrollYIsComputed()) {
                    mScrollY = listView.getComputedScrollY();
                }

                if (layerBottom != null) {
                    int rawY_Bottom = mScrollY;
                    switch (mState_Bottom) {
                        case STATE_OFFSCREEN:
                            if (rawY_Bottom >= mMinRawY_Bottom) {
                                mMinRawY_Bottom = rawY_Bottom;
                            } else {
                                mState_Bottom = STATE_RETURNING;
                            }
                            translationY_Bottom = rawY_Bottom;
                            break;

                        case STATE_ONSCREEN:
                            if (rawY_Bottom > nBottomHeight) {
                                mState_Bottom = STATE_OFFSCREEN;
                                mMinRawY_Bottom = rawY_Bottom;
                            }
                            translationY_Bottom = rawY_Bottom;
                            break;

                        case STATE_RETURNING:

                            translationY_Bottom = (rawY_Bottom - mMinRawY_Bottom) + nBottomHeight;

                            if (translationY_Bottom < 0) {
                                translationY_Bottom = 0;
                                mMinRawY_Bottom = rawY_Bottom + nBottomHeight;
                            }

                            if (rawY_Bottom == 0) {
                                mState_Bottom = STATE_ONSCREEN;
                                translationY_Bottom = 0;
                            }

                            if (translationY_Bottom > nBottomHeight) {
                                mState_Bottom = STATE_OFFSCREEN;
                                mMinRawY_Bottom = rawY_Bottom;
                            }
                            break;
                    }


                    /** this can be used if the build is below honeycomb **/
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB) {
                        anim = new TranslateAnimation(0, 0, translationY_Bottom,
                                translationY_Bottom);
                        anim.setFillAfter(true);
                        anim.setDuration(0);
                        layerBottom.startAnimation(anim);
                    } else {
                        layerBottom.setTranslationY(translationY_Bottom);
                    }
                }


                if (layerTop != null) {

                    int placeHolderTopPos = 0;

                    if (mPlaceHolder != null)
                        placeHolderTopPos = mPlaceHolder.getTop();

                    int rawY_Top = placeHolderTopPos - Math.min(mCachedVerticalScrollRange - listView.getHeight(), mScrollY);

                    switch (mState_Top) {
                        case STATE_OFFSCREEN:
                            if (rawY_Top <= mMinRawY_Top) {
                                mMinRawY_Top = rawY_Top;
                            } else {
                                mState_Top = STATE_RETURNING;
                            }
                            translationY_Top = rawY_Top;
                            break;

                        case STATE_ONSCREEN:
                            if (rawY_Top < -nTopHeight) {
                                mState_Top = STATE_OFFSCREEN;
                                mMinRawY_Top = rawY_Top;
                            }
                            translationY_Top = rawY_Top;
                            break;

                        case STATE_RETURNING:
                            translationY_Top = (rawY_Top - mMinRawY_Top) - nTopHeight;
                            if (translationY_Top > 0) {
                                translationY_Top = 0;
                                mMinRawY_Top = rawY_Top - nTopHeight;
                            }

                            if (rawY_Top > 0) {
                                mState_Top = STATE_ONSCREEN;
                                translationY_Top = rawY_Top;
                            }

                            if (translationY_Top < -nTopHeight) {
                                mState_Top = STATE_OFFSCREEN;
                                mMinRawY_Top = rawY_Top;
                            }
                            break;
                    }


                    if (placeHolderTopPos > 0) {

                        if (translationY_Top > placeHolderTopPos)
                            translationY_Top = placeHolderTopPos;
                    } else if (translationY_Top > 0)
                        translationY_Top = 0;

                    /** this can be used if the build is below honeycomb **/
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB) {
                        anim = new TranslateAnimation(0, 0, translationY_Top,
                                translationY_Top);

                        anim.setFillAfter(true);
                        anim.setDuration(0);
                        layerTop.startAnimation(anim);
                    } else {
                        layerTop.setTranslationY(translationY_Top);
                    }
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
        });
    }


    private void checkListZeroSize() {
        if (viewNodata != null) {
            if (list != null && listAdapter.getCount() == 0) {
                viewNodata.setVisibility(View.VISIBLE);
            } else {
                viewNodata.setVisibility(View.GONE);
            }
        }
    }

    protected void setListInfo(List list) {
        this.list = list;
        checkListZeroSize();
        calculateListSize();
    }


    protected void loadList(List list) {
        if (list == null && this.refreshLayout != null) {
            this.refreshLayout.setRefreshing(true);
        }

        if (list == null)
            listView.addFooter();

        this.list = list;
    }

    protected void postLoad(List list) {
        this.list = list;

        listView.onLoadMoreComplete();

        checkListZeroSize();

        listAdapter.notifyDataSetChanged();
        calculateListSize();

        if (list.getPages() == null || !list.getPages().isHasNext())
            listView.removeFooter();
    }

    protected void setAddedLayer(View layerTop, View layerBottom) {
        this.layerTop = layerTop;
        this.layerBottom = layerBottom;
    }


    void calculateListSize() {
        listView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            listView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            listView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }

                        if (layerTop != null) {
                            nTopHeight = layerTop.getHeight();
                        }

                        if (layerBottom != null) {
                            nBottomHeight = layerBottom.getHeight();
                        }

                        listView.computeScrollY();
                        mCachedVerticalScrollRange = listView.getListHeight();

//                        ViewGroup layerNoData = (ViewGroup)rootView.findViewById(R.id.layerNoData);
//                        final ViewGroup.MarginLayoutParams lpt =(ViewGroup.MarginLayoutParams)layerNoData.getLayoutParams();
//                        int height = PixelUtil.dpToPx(helper.context, 92);
//                        if(isOwner)
//                            height = PixelUtil.dpToPx(helper.context, 54);
//
//                        lpt.setMargins(lpt.rightMargin, height + scrollNice.mPlaceHolder.getTop(), lpt.rightMargin, lpt.bottomMargin);
//
//                        if (dataListInfo.isReadyLoaded() == true) {
//                            if (dataListInfo.getListSize() == 0) {
//                                layerNoData.setVisibility(View.VISIBLE);
//                            }
//                        }


                    }
                });
    }

    protected void scrollToBottom() {
        contextHelper.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                listView.getViewTreeObserver().addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    listView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                } else {
                                    listView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                }
                                listView.setSelection(listAdapter.getCount() - 1);
                            }
                        });

            }
        });

    }

    protected void setHeader(View headerView) {
        this.mHeader = headerView;
        listView.addHeaderView(mHeader);

        mPlaceHolder = mHeader.findViewById(R.id.placeholder);
        listView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            listView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            listView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }

                        viewNodata.setTranslationY(mHeader.getHeight());

                    }
                });
    }
}
