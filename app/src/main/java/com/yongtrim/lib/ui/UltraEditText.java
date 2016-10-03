package com.yongtrim.lib.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nuums.nuums.AppController;
import com.nuums.nuums.R;
import com.yongtrim.lib.util.PixelUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * hair / com.yongtrim.lib.ui
 * <p/>
 * Created by Uihyun on 15. 9. 23..
 */
public class UltraEditText extends LinearLayout {

    EditText edittext;
    TextView tvMessage;
    ImageView ivIcon;
    //TextView tvCurrent;

    String hint;
    boolean isMultiline;


    private Drawable mIconResource = null;
    private int mStyle = 0; // 0: 버튼을 가장자리에, 1:버튼과 텍스트를 중앙 정렬, 2:왼쪽으로 몰리게,



    int maxCharacters = Integer.MAX_VALUE;
    int minCharacters = 0;
    boolean isValidate = true;

    private OnChangeListener mChangeListener;
    public static interface OnChangeListener {
        public void onEditTextChanged(UltraEditText editText);
    }

    public UltraEditText(Context context) {
        super(context);
        init(context);

    }

    public UltraEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray attrsArray = context.obtainStyledAttributes(attrs, R.styleable.UltraEditAttrs, 0, 0);
        initAttributs(attrsArray);
        attrsArray.recycle();

        init(context);
    }

    public void setEditTextId(@IdRes int id) {
        edittext.setId(id);
    }

    private void init(Context context) {
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        li.inflate(R.layout.view_edittext, this, true);
        View viewLine = (View)findViewById(R.id.viewLine);

        edittext = (EditText)findViewById(R.id.edittext);
        edittext.setHint(hint);
        if(isMultiline) {
            edittext.setGravity(Gravity.TOP);
        } else {
            edittext.setSingleLine(true);
            edittext.setMaxLines(1);
            edittext.setLines(1);
        }

        tvMessage = (TextView)findViewById(R.id.tvMessage);
        //tvCurrent = (TextView)findViewById(R.id.tvCurrent);
        ivIcon = (ImageView)findViewById(R.id.ivIcon);

        if(mIconResource == null) {
            ivIcon.setVisibility(View.GONE);
        } else {
            ivIcon.setImageDrawable(mIconResource);
        }

        if(mStyle == 1) {
            edittext.setHintTextColor(ContextCompat.getColor(context, R.color.gray));
            edittext.setTextColor(ContextCompat.getColor(context, R.color.gray));

            try {
                // https://github.com/android/platform_frameworks_base/blob/kitkat-release/core/java/android/widget/TextView.java#L562-564
                Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
                f.setAccessible(true);
                f.set(edittext, R.drawable.text_cursor2);
            } catch (Exception ignored) {
            }

            viewLine.setBackgroundColor(ContextCompat.getColor(context, R.color.gray));
        } else if(mStyle == 2) {
            edittext.setHintTextColor(ContextCompat.getColor(context, R.color.white));
            edittext.setTextColor(ContextCompat.getColor(context, R.color.white));


            edittext.setPadding(PixelUtil.dpToPx(getContext(), 10), PixelUtil.dpToPx(getContext(), 5), PixelUtil.dpToPx(getContext(), 10), PixelUtil.dpToPx(getContext(), 5));
            try {
                // https://github.com/android/platform_frameworks_base/blob/kitkat-release/core/java/android/widget/TextView.java#L562-564
                Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
                f.setAccessible(true);
                f.set(edittext, R.drawable.text_cursor);
            } catch (Exception ignored) {
            }

            viewLine.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }
    }


    private void initAttributs(TypedArray attrsArray) {
        mStyle = attrsArray.getInt(R.styleable.UltraEditAttrs_bgStyle, mStyle);

        hint = attrsArray.getString(R.styleable.UltraEditAttrs_hint);
        isMultiline = attrsArray.getBoolean(R.styleable.UltraEditAttrs_multiline, false);

        try {
            mIconResource = attrsArray.getDrawable(R.styleable.UltraEditAttrs_iconRes);
        } catch(Exception e) {
        }
    }


    public void setChangeListener(OnChangeListener listener) {
        mChangeListener = listener;
        edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                postInvalidate();

//                if(maxCharacters < Integer.MAX_VALUE) {
//                    tvCurrent.setText(getText().length() + " / " + maxCharacters + "자 이내");
//                }

                if(minCharacters > 0) {
                    if(s.toString().length() < minCharacters) {
                        setErrorMessage(minCharacters + "자 이상이어야 합니다.");
                        isValidate = false;
                    } else {
                        isValidate = true;
                        setErrorMessage("");
                    }
                } else if(s.toString().length() == 0) {
                    setErrorMessage("");
                    setInfoMessage("");
                }
                if (mChangeListener != null) {
                    mChangeListener.onEditTextChanged(UltraEditText.this);
                }
            }
        });

    }

    public void setInputType(int type) {
        edittext.setInputType(type);
    }


    public void setRawInputType(int type) {
        edittext.setRawInputType(type);
    }

    public void setNextFocusDownId(int nextFocusDownId) {
        edittext.setNextFocusDownId(nextFocusDownId);
    }


    public String getText() {
        return edittext.getText().toString();
    }

    public void setInfoMessage(String info) {
        tvMessage.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
        tvMessage.setText(info);
    }

    public void setErrorMessage(String message) {
        tvMessage.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        tvMessage.setText(message);
    }

    public void setMaxCharacters(int value) {
        maxCharacters = value;
        edittext.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxCharacters)});
        //tvCurrent.setText(getText().length() + " / " + maxCharacters + "자 이내");
    }

    public void setFilters(InputFilter[] filters) {
        edittext.setFilters(filters);
    }

    public void setMinCharacters(int value) {
        minCharacters = value;
    }


    public void setText(String string) {
        edittext.setText(string);
    }


    public void setFocusable(boolean focusable) {
        edittext.setFocusable(focusable);
    }


    public void setOnEditorActionListener(TextView.OnEditorActionListener l) {
        edittext.setOnEditorActionListener(l);

    }


    public boolean validate() {
        return isValidate;
    }

    public final void append(CharSequence text) {
        edittext.append(text);
    }

    public void setHint(String hint) {
        edittext.setHint(hint);

    }


    public void setImeOptions(int imeOptions) {
        edittext.setImeOptions(imeOptions);

    }

    public void addTextChangedListener(TextWatcher watcher) {
        edittext.addTextChangedListener(watcher);
    }

}
