package com.yongtrim.lib.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
//import android.os.Parcel;
//import android.os.Parcelable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nuums.nuums.R;
import com.yongtrim.lib.log.Logger;
import com.yongtrim.lib.util.PixelUtil;

/**
 * hair / com.yongtrim.lib.ui
 * <p/>
 * Created by yongtrim.com on 15. 9. 3..
 */
public class PagerSlidingTabStrip extends HorizontalScrollView {
    private final String TAG = getClass().getSimpleName();

    private int currentPageSelected = 0;
    private LinearLayout tabsContainer;
    private ViewPager pager;
    private final PageListener pageListener = new PageListener();

    private ViewPager.OnPageChangeListener delegatePageListener;

    //
    // Custom
    private int indicatorHeight;
    private final int scrollOffset = 52;

    private int scrollXLast = 0;
    private int layout_width;

    private int tabCount = 0;

    private int currentPosition = 0;
    private float currentPositionOffset = 0f;
    private LinearLayout.LayoutParams tabLayoutParams;
    private int indicatorColor;
    private int backgroundColor;

    private Paint paint;

    TextView tvChatBadge;

    public PagerSlidingTabStrip(Context context) {
        this(context, null);
    }

    public PagerSlidingTabStrip(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagerSlidingTabStrip(Context context, AttributeSet attrs, int defStye) {
        super(context, attrs, defStye);

        setFillViewport(true);
        setWillNotDraw(false);

        //
        // color setting
        indicatorColor = ContextCompat.getColor(context, R.color.green);
        backgroundColor = 0xffffffff;//0xb4a7a9ab;//ContextCompat.getColor(context, R.color.green);

        //
        // tab size
        tabLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);


        tabsContainer = new LinearLayout(context);
        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(tabsContainer);


        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);

        int[] ATTRS = new int[] {
                android.R.attr.layout_width,
        };

        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);

        try {
            layout_width = a.getDimensionPixelSize(0, ViewGroup.LayoutParams.MATCH_PARENT);
        } catch (Exception e) {

        }

        a.recycle();
        a = context.obtainStyledAttributes(attrs, R.styleable.PagerSlidingTabStrip);

        indicatorHeight = 0;

        a.recycle();

    }


    private void scrollToChild(int position, int offset) {
        int scrollXNew = tabsContainer.getChildAt(position).getLeft() + offset;

        if (position > 0 || offset > 0) {
            scrollXNew -= scrollOffset;
        }

        if (scrollXNew != scrollXLast) {
            scrollXLast = scrollXNew;
            scrollTo(scrollXNew, 0);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (isInEditMode())
            return;

        final int height = getHeight();

        //
        // draw indicator line
        paint.setColor(indicatorColor);
        View currentTab = tabsContainer.getChildAt(currentPosition);
        float lineLeft = currentTab.getLeft();
        float lineRight = currentTab.getRight();

        // If there is an offset, start interpolating left and right coordinates between current and next tab
        if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {
            View tabNext = tabsContainer.getChildAt(currentPosition + 1);
            final float tabNextLeft = tabNext.getLeft();
            final float tabNextRight = tabNext.getRight();

            lineLeft = (currentPositionOffset * tabNextLeft + (1f - currentPositionOffset) * lineLeft);
            lineRight = (currentPositionOffset * tabNextRight + (1f - currentPositionOffset) * lineRight);
        }


        canvas.drawRect(lineLeft, height - indicatorHeight, lineRight, height, paint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }


    public void setViewPager(ViewPager pager) {
        this.pager = pager;

        if (pager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adpater instance.");
        }

        //--pager.clearOnPageChangeListeners();
        pager.addOnPageChangeListener(pageListener);

        notifyDataSetChanged();
    }


    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        this.delegatePageListener = listener;
    }

    public void notifyDataSetChanged() {
        tabsContainer.removeAllViews();

        tabCount = pager.getAdapter().getCount();

        for (int i = 0; i < tabCount; i++) {
            if (pager.getAdapter() instanceof IconTabProvider) {
                addIconTab(i);
            }
        }


        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                currentPosition = pager.getCurrentItem();

//                tabsContainer.getChildAt(currentPosition).setSelected(true);
//                scrollToChild(currentPosition, 0);
                refresh();
            }
        });
    }


    void refresh() {
        for(int i = 0;i < 5;i ++) {
            ViewGroup tabView = (ViewGroup) tabsContainer.getChildAt(i);

            TextView tvText = (TextView)tabView.findViewById(R.id.tvText);
            ImageView ivIcon = (ImageView)tabView.findViewById(R.id.ivIcon);

            int colorSelected = ContextCompat.getColor(getContext(), R.color.green);
            int colorDeselected = ContextCompat.getColor(getContext(), R.color.gray_70);

            switch(i) {
                case 0:
                    tvText.setTextColor(currentPosition == 0 ? colorSelected : colorDeselected);
                    ivIcon.setImageResource(currentPosition == 0 ? R.drawable.main_home_on : R.drawable.main_home_off);
                    break;
                case 1:
                    tvText.setTextColor(currentPosition == 1 ? colorSelected : colorDeselected);
                    ivIcon.setImageResource(currentPosition == 1 ? R.drawable.main_talk_on : R.drawable.main_talk_off);
                    break;
                case 2:
                    tvText.setTextColor(currentPosition == 2 ? colorSelected : colorDeselected);
                    ivIcon.setImageResource(currentPosition == 2 ? R.drawable.main_camera_on : R.drawable.main_camera_off);
                    break;
                case 3:
                    tvText.setTextColor(currentPosition == 3 ? colorSelected : colorDeselected);
                    ivIcon.setImageResource(currentPosition == 3 ? R.drawable.main_heart_on : R.drawable.main_heart_off);

                    break;
                case 4:
                    tvText.setTextColor(currentPosition == 4 ? colorSelected : colorDeselected);
                    ivIcon.setImageResource(currentPosition == 4 ? R.drawable.main_my_page_on : R.drawable.main_my_page_off);
                    break;
            }

        }

    }

    private void addIconTab(final int position) {
        if(layout_width == 0) {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            layout_width = metrics.widthPixels;
        }
        tabLayoutParams.width = layout_width / tabCount;

        LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout tabView = (RelativeLayout)li.inflate(R.layout.view_maintab, null, false);

        TextView tvText = (TextView)tabView.findViewById(R.id.tvText);
        TextView tvBadge = (TextView)tabView.findViewById(R.id.tvBadge);

        switch (position) {
            case 0:
                tvText.setText("홈");
                tvBadge.setVisibility(View.GONE);
                break;
            case 1:
                tvText.setText("대화");

                GradientDrawable shape =  new GradientDrawable();
                shape.setCornerRadius(PixelUtil.dpToPx(getContext(), 9));
                shape.setColor(ContextCompat.getColor(getContext(), R.color.pink));

                tvBadge.setBackground(shape);
                tvBadge.setPadding(PixelUtil.dpToPx(getContext(), 5), PixelUtil.dpToPx(getContext(), 2), PixelUtil.dpToPx(getContext(), 5), PixelUtil.dpToPx(getContext(), 2));
                tvChatBadge = tvBadge;
                break;
            case 2:
                tvText.setText("등록");
                tvBadge.setVisibility(View.GONE);
                break;
            case 3:
                tvText.setText("나눔후기");
                tvBadge.setVisibility(View.GONE);
                break;
            case 4:
                tvText.setText("내정보");
                tvBadge.setVisibility(View.GONE);
                break;
        }

        tabView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(position);
            }
        });
        tabsContainer.addView(tabView, position, tabLayoutParams);
    }


    public void setChatBadge(int count) {
        if(count > 0) {
            tvChatBadge.setText("" + count);
            tvChatBadge.setVisibility(View.VISIBLE);
        } else {
            tvChatBadge.setVisibility(View.GONE);
        }
    }

    public interface IconTabProvider {
        public int getPageIconResId(int position);
    }

    private class PageListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            currentPosition = position;
            currentPositionOffset = positionOffset;

            scrollToChild(position, (int) (positionOffset * tabsContainer.getChildAt(position).getWidth()));

            invalidate();

            if (delegatePageListener != null) {
                delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                scrollToChild(pager.getCurrentItem(), 0);
            }

            if (delegatePageListener != null) {
                delegatePageListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {

//            if(currentPageSelected == 2) {
//                ibChat.setSelected(false);
//            } else {
//                tabsContainer.getChildAt(currentPageSelected).setSelected(false);
//            }

            currentPageSelected = position;
            currentPosition = position;

//            if(currentPageSelected == 2) {
//                ibChat.setSelected(true);
//            } else {
//                tabsContainer.getChildAt(currentPageSelected).setSelected(true);
//            }
            refresh();
            if (delegatePageListener != null) {
                delegatePageListener.onPageSelected(position);
            }

        }
    }


//    @Override
//    public void onRestoreInstanceState(Parcelable state) {
//        SavedState savedState = (SavedState)state;
//        super.onRestoreInstanceState(savedState.getSuperState());
//        currentPosition = savedState.currentPosition;
//
//        requestLayout();
//    }
//
//    @Override
//    public Parcelable onSaveInstanceState() {
//        Parcelable superState = super.onSaveInstanceState();
//        SavedState savedState = new SavedState(superState);
//        savedState.currentPosition = currentPosition;
//        return savedState;
//    }
//
//    static class SavedState extends BaseSavedState {
//        int currentPosition;
//
//        public SavedState(Parcelable superState) {
//            super(superState);
//        }
//
//        private SavedState(Parcel in) {
//            super(in);
//            currentPosition = in.readInt();
//        }
//
//        @Override
//        public void writeToParcel(Parcel dest, int flags) {
//            super.writeToParcel(dest, flags);
//            dest.writeInt(currentPosition);
//        }
//
//        public static final Parcelable.Creator<SavedState> CRATE = new Parcelable.Creator<SavedState>() {
//            @Override
//            public SavedState createFromParcel(Parcel in) {
//                return new SavedState(in);
//            }
//
//            @Override
//            public SavedState[] newArray(int size) {
//                return new SavedState[size];
//            }
//        };
//    }

    public int getCurrentPageSelectedIndex() {
        return currentPageSelected;
    }

}
