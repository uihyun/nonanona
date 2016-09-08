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
import android.view.MotionEvent;
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
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.log.Logger;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.util.PixelUtil;

/**
 * hair / com.yongtrim.lib.ui
 * <p/>
 * Created by yongtrim.com on 15. 9. 3..
 */
public class PagerSlidingTabStrip2 extends HorizontalScrollView {
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


    ContextHelper contextHelper;
    public PagerSlidingTabStrip2(Context context) {
        this(context, null);
    }

    public PagerSlidingTabStrip2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagerSlidingTabStrip2(Context context, AttributeSet attrs, int defStye) {
        super(context, attrs, defStye);

        setFillViewport(true);
        setWillNotDraw(false);


        //
        // color setting
        indicatorColor = ContextCompat.getColor(context, R.color.green);
        backgroundColor = 0xb4a7a9ab;//ContextCompat.getColor(context, R.color.green);

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

        position = 4;
        offset = 0;

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
            if (pager.getAdapter() instanceof CheckTabProvider) {
                addCheckTab(i, ((CheckTabProvider) pager.getAdapter()).getPageTitle(i));
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
                tabsContainer.getChildAt(currentPosition).setSelected(true);
                scrollToChild(currentPosition, 0);
            }
        });
    }

    public void setContextHelper(ContextHelper contextHelper) {
        this.contextHelper = contextHelper;
    }

    private void addCheckTab(final int position, String title) {

        NsUser user = UserManager.getInstance(contextHelper).getMe();

        LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout linearLayout = (LinearLayout)li.inflate(R.layout.view_tab, null, false);

        TextView tvTitle = (TextView)linearLayout.findViewById(R.id.tvTitle);
        tvTitle.setText(title);

        TextView tvCount = (TextView)linearLayout.findViewById(R.id.tvCount);

        switch(position) {
            case 0:
                break;
            case 1:
                tvCount.setText("" + user.getNanums().size());
                break;
            case 2:
                tvCount.setText("" + user.getApplys().size());
                break;
            case 3:
                tvCount.setText("" + user.getApplyDeliverys().size());
                break;
            case 4:
                tvCount.setText("" + user.getWons().size());
                break;

        }

//        LinearLayout linearLayout = new LinearLayout(getContext());
//        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
//        linearLayout.setGravity(Gravity.CENTER);
//
//        linearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//        linearLayout.setOrientation(LinearLayout.VERTICAL);
//
////        ImageView imgCheck = new ImageView(getContext());
////        imgCheck.setTag("IMGCHECK");
////        imgCheck.setImageDrawable(getContext().getResources().getDrawable(R.drawable.button_tap_bar_on));
////        imgCheck.setPadding(0, 0, PixelUtil.dpToPx(getContext(), 5), 0);
////        linearLayout.addView(imgCheck);
//
//        TextView tab = new TextView(getContext());
//        tab.setTag("TABTEXT");
//        tab.setText(title);
//        tab.setSingleLine();
//        tab.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
//        tab.setTypeface(null, Typeface.BOLD);
//        tab.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
//        linearLayout.addView(tab);



        addTab(position, linearLayout);
    }


    private void addTab(final int position, View tab) {
        tab.setFocusable(true);
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(position);
            }
        });

        if(layout_width == 0) {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            layout_width = metrics.widthPixels;
        }

        tabLayoutParams.width = layout_width / 4;

        tabsContainer.addView(tab, position, tabLayoutParams);
    }


    public interface CheckTabProvider {
        public String getPageTitle(int position);
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


            //tabsContainer.getChildAt(currentPageSelected).setSelected(false);

            tabSelected(tabsContainer.getChildAt(currentPageSelected), false);


            currentPageSelected = position;

            tabSelected(tabsContainer.getChildAt(currentPageSelected), true);

            //tabsContainer.getChildAt(currentPageSelected).setSelected(true);
            if (delegatePageListener != null) {
                delegatePageListener.onPageSelected(position);
            }

        }
    }


    void tabSelected(View v, boolean isSelect) {

        //View imgView = v.findViewWithTag("IMGCHECK");
        TextView tvTitle = (TextView)v.findViewById(R.id.tvTitle);

        TextView tvCount = (TextView)v.findViewById(R.id.tvCount);
        TextView tvCountTag = (TextView)v.findViewById(R.id.tvCountTag);

        if(isSelect) {
            v.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.pink_80));
            tvTitle.setTypeface(null, Typeface.BOLD);
            tvTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            tvCount.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            tvCountTag.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        }
        else {
            v.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
            tvTitle.setTypeface(null, Typeface.NORMAL);
            tvTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            tvCount.setTextColor(ContextCompat.getColor(getContext(), R.color.pink));
            tvCountTag.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));
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

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                // if we can scroll pass the event to the superclass
//                if (mScrollable) return super.onTouchEvent(ev);
//                // only continue to handle the touch event if scrolling enabled
//                return mScrollable; // mScrollable is always false at this point
//            default:
//                return super.onTouchEvent(ev);
//        }
        return  false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Don't do anything with intercepted touch events if
        // we are not scrollable
//        if (!mScrollable) return false;
//        else return super.onInterceptTouchEvent(ev);
        return false;
    }

}
