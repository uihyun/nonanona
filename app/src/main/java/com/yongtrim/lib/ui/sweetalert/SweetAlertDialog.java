package com.yongtrim.lib.ui.sweetalert;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nuums.nuums.R;
import com.nuums.nuums.activity.BaseActivity;
import com.nuums.nuums.model.chat.Address;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.activity.ABaseFragmentAcitivty;
import com.yongtrim.lib.log.Logger;
import com.yongtrim.lib.model.misc.CodeName;
import com.yongtrim.lib.model.photo.Photo;
import com.yongtrim.lib.ui.UltraButton;
import com.yongtrim.lib.ui.UltraEditText;
import com.yongtrim.lib.util.DateUtil;
import com.yongtrim.lib.util.MiscUtil;
import com.yongtrim.lib.util.OptAnimationLoader;
import com.yongtrim.lib.util.PixelUtil;

import net.simonvt.widget.NumberPicker;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * hair / com.yongtrim.lib.ui.sweetalert
 * <p/>
 * Created by yongtrim.com on 15. 9. 17..
 */
public class SweetAlertDialog extends Dialog implements View.OnClickListener {

    final String TAG = "SweetAlertDialog";

    private View mDialogView;
    private AnimationSet mModalInAnim;
    private AnimationSet mModalOutAnim;

    private int mAlertType;

    private TextView mTitleTextView;
    private String mTitleText;


    private TextView mContentTextView;
    private String mContentText;
    private CharSequence mCharSequence;

    private String[] mArrayValues;
    private String[] mArrayRes;
    private ListView listView;
    ArrayList<ListItem> listItems;
    private OnSweetSelectListener mSelectListener;

    BaseAdapter mAdapter;

    private OnSweetMultiSelectListener mMultiSelectListener;
    private int[] mArrayIndices;

    private String mTipText;


    private EditText mEditText;
    private String mEditTextValue;
    private String mEditPlaceHolder;
    private int mEditType;
    private int mLimit;


    private boolean mShowCancel;
    private boolean mShowClose;
    private boolean mShowContent;
    private String mCancelText;
    private String mConfirmText;

    net.simonvt.widget.NumberPicker mNumberPicker;
    int mNumberValue;
    int mMaxValue;
    int mMinValue;
    String mUnit;

    ContextHelper contextHelper;


    private UltraButton mConfirmButton;
    private UltraButton mCancelButton;
    private ImageButton mCloseButton;
    private View viewCancel;


    private OnSweetClickListener mCancelClickListener;
    private OnSweetClickListener mConfirmClickListener;
    private boolean mCloseFromCancel;

    public static final int NORMAL_TYPE = 0;
    public static final int PROGRESS_TYPE = 1;
    public static final int SELECT_TYPE = 2;
    public static final int MULTISELECT_TYPE = 3;
    public static final int CUSTOMLIST_TYPE = 4;
    public static final int LIST_TYPE = 5;
    public static final int NUMBERPICKER_TYPE = 6;
    public static final int EDITTEXT_TYPE = 7;
    public static final int ADDRESS_TYPE = 8;
    public static final int DELIVERY_TYPE = 9;

    public static final int PHOTO_TYPE = 10;
    public static final int NORMAL2_TYPE = 11;
    public UltraEditText etName;
    public UltraEditText etNumber0;
    public UltraEditText etNumber1;
    public UltraEditText etNumber2;
    public UltraButton btnPostNumber;
    public UltraButton btnBasic;
    public EditText etDetail;
    public CheckBox cbRemember;
    Address address;


    public UltraButton btnDeliveryCompany;
    public EditText etDeliveryNumber;
    public CodeName codeNameDeliveryCompany;


    public Photo photo;

    private ImageView iconContent;

    public interface OnSweetClickListener {
        public void onClick(SweetAlertDialog sweetAlertDialog);
    }

    public interface OnSweetSelectListener {
        public void onSelected(SweetAlertDialog sweetAlertDialog, int index);
    }

    public interface OnSweetMultiSelectListener {
        public void onSelected(SweetAlertDialog sweetAlertDialog, int indices[]);
    }


    public SweetAlertDialog(Context context) {
        this(context, NORMAL_TYPE);
    }

    public SweetAlertDialog(Context context, int alertType) {
        super(context, R.style.dialog_sweet);
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        mAlertType = alertType;

        if (mAlertType == EDITTEXT_TYPE ||  mAlertType == ADDRESS_TYPE)
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        mModalInAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), R.anim.dialog_modal_in);
        mModalOutAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), R.anim.dialog_modal_out);
        mModalOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mDialogView.post(new Runnable() {
                    @Override
                    public void run() {
//                        try {
//                            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//
//                            if (mEditText != null)
//                                imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
//
//                            if (mEtValue1 != null)
//                                imm.hideSoftInputFromWindow(mEtValue1.getWindowToken(), 0);
//
//                            if (mEtValue0 != null)
//                                imm.hideSoftInputFromWindow(mEtValue0.getWindowToken(), 0);
//
//                            if (mEditTexts != null) {
//                                for (int i = 0; i < mEditTexts.length; i++) {
//                                    if (mEditTexts[i] != null)
//                                        imm.hideSoftInputFromWindow(mEditTexts[i].getWindowToken(), 0);
//
//                                }
//                            }
//                        } catch (Exception e) {
//
//                        }

                        if (mCloseFromCancel) {
                            SweetAlertDialog.super.cancel();
                        } else {
                            try {
                                if (SweetAlertDialog.super.isShowing())
                                    SweetAlertDialog.super.dismiss();
                            }catch (Exception e) {

                            }
                        }
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mAlertType == PROGRESS_TYPE) {
            setContentView(R.layout.dialog_sweet_progress);
            setCancelable(false);
            setCanceledOnTouchOutside(false);
            mDialogView = getWindow().getDecorView().findViewById(android.R.id.content);
            mContentTextView = (TextView)findViewById(R.id.tvContent);

            return;

        } else if(mAlertType == NORMAL_TYPE || mAlertType == NORMAL2_TYPE || mAlertType == EDITTEXT_TYPE) {
            setContentView(R.layout.dialog_sweet2);
            mDialogView = getWindow().getDecorView().findViewById(android.R.id.content);
        } else {
            setContentView(R.layout.dialog_sweet);
            mDialogView = getWindow().getDecorView().findViewById(android.R.id.content);
        }

        mTitleTextView = (TextView) findViewById(R.id.title_text);

        mConfirmButton = (UltraButton) findViewById(R.id.confirm_button);
        mCancelButton = (UltraButton) findViewById(R.id.cancel_button);
        viewCancel = findViewById(R.id.viewCancel);

        mCloseButton = (ImageButton) findViewById(R.id.btnClose);

        mConfirmButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);

        if(mCloseButton != null)
            mCloseButton.setOnClickListener(this);


        LinearLayout viewContent = (LinearLayout) findViewById(R.id.viewContent);
        LinearLayout viewFrame = (LinearLayout) findViewById(R.id.viewFrame);
        LinearLayout viewBottom = (LinearLayout) findViewById(R.id.viewBottom);


        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (mAlertType == NORMAL_TYPE) {
            View viewNormal = layoutInflater.inflate(R.layout.dialog_sweet_normal, viewContent);
            mContentTextView = (TextView) viewNormal.findViewById(R.id.textContent);
            mTitleTextView.setVisibility(View.GONE);
            iconContent = (ImageView)viewNormal.findViewById(R.id.iconContent);

        } else if (mAlertType == NORMAL2_TYPE) {
            View viewNormal = layoutInflater.inflate(R.layout.dialog_sweet_normal, viewContent);
            mContentTextView = (TextView) viewNormal.findViewById(R.id.textContent);
            mTitleTextView.setVisibility(View.GONE);
            iconContent = (ImageView)viewNormal.findViewById(R.id.iconContent);
            iconContent.setVisibility(View.GONE);
            mContentTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            mContentTextView.setGravity(Gravity.CENTER);
        } else if (mAlertType == EDITTEXT_TYPE) {
            View viewEditText = layoutInflater.inflate(R.layout.dialog_sweet_edittext, viewContent);
            mEditText = (EditText) viewEditText.findViewById(R.id.etValue);
            mEditText.setInputType(mEditType);
            if ((mEditType & InputType.TYPE_TEXT_FLAG_MULTI_LINE) == InputType.TYPE_TEXT_FLAG_MULTI_LINE) {
                mEditText.setSingleLine(false);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtil.dpToPx(getContext(), 100));
                mEditText.setLayoutParams(layoutParams);
                mEditText.setGravity(Gravity.TOP);
            }
            mEditText.setText(mEditTextValue);
            if (!TextUtils.isEmpty(mEditPlaceHolder))
                mEditText.setHint(mEditPlaceHolder);

            final TextView tvGuide = (TextView) findViewById(R.id.tvGuide);

            tvGuide.setText("" + mEditTextValue.length() + " / " + mLimit + "자이내");
            if (mLimit > 0) {
                tvGuide.setVisibility(View.VISIBLE);
                mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mLimit)});
                mEditText.addTextChangedListener(new TextWatcher() {
                    public void afterTextChanged(Editable s) {

                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                        tvGuide.setText("" + s.length() + " / " + mLimit + "자이내");
                    }
                });


            }
            mEditText.requestFocus();

        } else if (mAlertType == NUMBERPICKER_TYPE) {
            findViewById(R.id.viewPicker).setVisibility(View.VISIBLE);
            mNumberPicker = (net.simonvt.widget.NumberPicker)findViewById(R.id.numberPicker);
            mNumberPicker.setVisibility(View.VISIBLE);

            mNumberPicker.setMinValue(mMinValue);
            mNumberPicker.setMaxValue(mMaxValue);

            mNumberPicker.setValue(mNumberValue);
            mTitleTextView.setText(mTitleText);

            if(!TextUtils.isEmpty(mUnit)) {
                TextView tvUnit = (TextView)findViewById(R.id.tvUnit);
                tvUnit.setVisibility(View.VISIBLE);
                tvUnit.setText(mUnit);
            }

        } else if (mAlertType == SELECT_TYPE) {
            viewFrame.setPadding(0, 0, 0, 0);

            View viewListView = layoutInflater.inflate(R.layout.dialog_sweet_listview, viewContent);
            listView = (ListView) viewListView.findViewById(R.id.listView);


            ArrayList<ListItem> list = new ArrayList<>();

            for (int i = 0; i < mArrayValues.length; i++) {
                ListItem listItem = new ListItem(mArrayValues[i]);
                if(mArrayRes != null) {
                    listItem.setRes(mArrayRes[i]);
                }
                list.add(listItem);

            }

            ListAdapter adpater = new ListAdapter(false);
            adpater.setData(list);
            listView.setAdapter(adpater);
            listView.setOnItemClickListener(new ListView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mSelectListener.onSelected(SweetAlertDialog.this, position);
                }
            });

            if (mArrayValues.length > 7) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) listView.getLayoutParams();
                params.height = PixelUtil.dpToPx(getContext(), 300);
                listView.setLayoutParams(params);
            }

            viewBottom.setVisibility(View.GONE);

            mTitleTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            mTitleTextView.setBackgroundResource(R.color.green);


        } else if (mAlertType == LIST_TYPE) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewContent.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, 0);
            viewContent.setLayoutParams(layoutParams);

            View viewListView = layoutInflater.inflate(R.layout.dialog_sweet_listview, viewContent);
            listView = (ListView) viewListView.findViewById(R.id.listView);


            ArrayList<ListItem> list = new ArrayList<>();

            for (int i = 0; i < mArrayValues.length; i++) {
                list.add(new ListItem(mArrayValues[i]));
            }

            ListAdapter adpater = new ListAdapter(false);
            adpater.setData(list);
            listView.setAdapter(adpater);
            listView.setOnItemClickListener(new ListView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                }
            });

            if (mArrayValues.length > 7) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) listView.getLayoutParams();
                params.height = PixelUtil.dpToPx(getContext(), 300);
                listView.setLayoutParams(params);
            }
        } else if (mAlertType == MULTISELECT_TYPE) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewContent.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, 0);
            viewContent.setLayoutParams(layoutParams);

            View viewListView = layoutInflater.inflate(R.layout.dialog_sweet_listview, viewContent);
            listView = (ListView) viewListView.findViewById(R.id.listView);

            listItems = new ArrayList<>();

            for (int i = 0; i < mArrayValues.length; i++) {
                listItems.add(new ListItem(mArrayValues[i], MiscUtil.contain(mArrayIndices, i)));
            }


            final ListAdapter adpater = new ListAdapter(true);
            adpater.setData(listItems);
            listView.setAdapter(adpater);
            listView.setClickable(true);
            listView.setOnItemClickListener(new ListView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ListItem item = listItems.get(position);
                    item.isCheck = !item.isCheck;
                    adpater.notifyDataSetChanged();
                }
            });


            if (mArrayValues.length > 7) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) listView.getLayoutParams();
                params.height = PixelUtil.dpToPx(getContext(), 300);
                listView.setLayoutParams(params);
            }

        } else if (mAlertType == CUSTOMLIST_TYPE) {
            View viewListView = layoutInflater.inflate(R.layout.dialog_sweet_listview, viewContent);
            listView = (ListView) viewListView.findViewById(R.id.listView);
            listView.setSelector(R.color.trans);
            listView.setAdapter(mAdapter);

            if (mAdapter.getCount() > 8) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) listView.getLayoutParams();
                params.height = PixelUtil.dpToPx(getContext(), 300);
                listView.setLayoutParams(params);
            }

        } else if(mAlertType == ADDRESS_TYPE) {
            View viewAddress = layoutInflater.inflate(R.layout.dialog_sweet_address, viewContent);
            etName = (UltraEditText)viewAddress.findViewById(R.id.etName);
            etNumber0 = (UltraEditText)viewAddress.findViewById(R.id.etNumber0);
            etNumber1 = (UltraEditText)viewAddress.findViewById(R.id.etNumber1);
            etNumber2 = (UltraEditText)viewAddress.findViewById(R.id.etNumber2);
            etNumber1.setEditTextId(R.id.number1);
            etNumber2.setEditTextId(R.id.number2);


            btnPostNumber = (UltraButton)viewAddress.findViewById(R.id.btnPostNumber);
            btnBasic = (UltraButton)viewAddress.findViewById(R.id.btnBasic);
            etDetail = (EditText)viewAddress.findViewById(R.id.etDetail);
            cbRemember = (CheckBox)viewAddress.findViewById(R.id.cbRemember);

            etNumber0.setMaxCharacters(3);
            etNumber0.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            etNumber0.setNextFocusDownId(R.id.number1);

            etNumber1.setMaxCharacters(4);
            etNumber1.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            etNumber1.setNextFocusDownId(R.id.number2);

            etNumber2.setMaxCharacters(4);
            etNumber2.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            etNumber2.setNextFocusDownId(R.id.etDetail);



            btnPostNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getContext(), BaseActivity.class);
                    i.putExtra("activityCode", BaseActivity.ActivityCode.POSTCODE.ordinal());
                    contextHelper.getActivity().startActivityForResult(i, ABaseFragmentAcitivty.REQUEST_POSTCODE);
                }
            });

            btnBasic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getContext(), BaseActivity.class);
                    i.putExtra("activityCode", BaseActivity.ActivityCode.POSTCODE.ordinal());
                    contextHelper.getActivity().startActivityForResult(i, ABaseFragmentAcitivty.REQUEST_POSTCODE);
                }
            });

            if(this.address != null) {
                etName.setText(address.getName());
                etNumber0.setText(address.getNumber0());
                etNumber1.setText(address.getNumber1());
                etNumber2.setText(address.getNumber2());
                btnPostNumber.setText(address.getPostCode());
                btnBasic.setText(address.getAddressBasic());
                etDetail.setText(address.getAddressDetail());
            }
        } else if(mAlertType == DELIVERY_TYPE) {
            View viewDelivery = layoutInflater.inflate(R.layout.dialog_sweet_delivery, viewContent);
            btnDeliveryCompany = (UltraButton)viewDelivery.findViewById(R.id.btnDeliveryCompany);
            etDeliveryNumber = (EditText)viewDelivery.findViewById(R.id.etDeliveryNumber);


            btnDeliveryCompany.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Type collectionType = new TypeToken<List<CodeName>>() {
                    }.getType();

                    final List<CodeName> listDelivery = (new Gson()).fromJson(MiscUtil.getDataFromAsset(contextHelper, "delivery.json"), collectionType);

                    new SweetAlertDialog(getContext(), SweetAlertDialog.SELECT_TYPE)
                            .setTitleText("택배사 선택")
                            .showCloseButton(true)
                            .setArrayValue(CodeName.getNameList(listDelivery))
                            .setSelectListener(new SweetAlertDialog.OnSweetSelectListener() {
                                @Override
                                public void onSelected(SweetAlertDialog sweetAlertDialog, int index) {
                                    sweetAlertDialog.dismissWithAnimation();
                                    codeNameDeliveryCompany = listDelivery.get(index);
                                    btnDeliveryCompany.setText(listDelivery.get(index).getName());
                                }
                            })
                            .show();
                }
            });

        } else if(mAlertType == PHOTO_TYPE) {
            View viewPhoto = layoutInflater.inflate(R.layout.dialog_sweet_photo, viewContent);

            ImageView ivPhoto = (ImageView)viewPhoto.findViewById(R.id.ivPhoto);
            ivPhoto.setImageBitmap(photo.getBitmap());
        }


        setTitleText(mTitleText);
        setContentText(mContentText);
        setContentText(mCharSequence);

        if (mTipText != null) {
            TextView tvTip = (TextView) findViewById(R.id.tvTip);
            tvTip.setText(mTipText);
            tvTip.setVisibility(View.VISIBLE);
        }

        if (mCancelButton != null && mCancelText != null) {
            mCancelButton.setText(mCancelText);
        }

        if (mConfirmButton != null && mConfirmText != null) {
            mConfirmButton.setText(mConfirmText);
        }

        updateView();

    }

    public SweetAlertDialog setContextHelper(ContextHelper contextHelper) {
        this.contextHelper = contextHelper;
        return this;
    }

    public SweetAlertDialog setAddress(Address address) {
        this.address = address;
        return this;
    }

    private void updateView() {
        if (mCancelButton != null)
            mCancelButton.setVisibility(mShowCancel ? View.VISIBLE : View.GONE);

        if(viewCancel != null)
            viewCancel.setVisibility(mShowCancel ? View.VISIBLE : View.GONE);

        if (mCloseButton != null)
            mCloseButton.setVisibility(mShowClose ? View.VISIBLE : View.GONE);

    }


    public SweetAlertDialog setTitleText(String text) {
        if(TextUtils.isEmpty(text)) {
            mTitleText = "확인해 보세요";
        } else {
            mTitleText = text;
        }
        if (mTitleTextView != null && mTitleText != null) {
            mTitleTextView.setText(mTitleText);
        }
        return this;
    }

    public boolean isShowCancelButton() {
        return mShowCancel;
    }

    public SweetAlertDialog showCancelButton(boolean isShow) {
        mShowCancel = isShow;
        updateView();
        return this;
    }


    public SweetAlertDialog showCloseButton(boolean isShow) {
        mShowClose = isShow;
        updateView();
        return this;
    }


    public SweetAlertDialog showContentText(boolean isShow) {
        mShowContent = isShow;
        if (mContentTextView != null) {
            mContentTextView.setVisibility(mShowContent ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    public SweetAlertDialog setCancelText(String text) {
        mCancelText = text;

        return this;
    }


    public SweetAlertDialog setConfirmText(String text) {
        mConfirmText = text;
        return this;
    }

    public SweetAlertDialog setConfirmClickListener(OnSweetClickListener listener) {
        mConfirmClickListener = listener;
        return this;
    }

    protected void onStart() {
        mDialogView.startAnimation(mModalInAnim);
        //
        // start animation
        if (mAlertType == PROGRESS_TYPE) {
//            ImageView ivAniLoading = (ImageView) findViewById(R.id.ivAniLoading);
//            ivAniLoading.setBackgroundResource(R.drawable.frame_loading2);
//            AnimationDrawable frameAniLoading = (AnimationDrawable) ivAniLoading.getBackground();
//            frameAniLoading.start();
        }

    }

    /**
     * The real Dialog.cancel() will be invoked async-ly after the animation finishes.
     */
    @Override
    public void cancel() {
        dismissWithAnimation(true);
    }

    /**
     * The real Dialog.dismiss() will be invoked async-ly after the animation finishes.
     */
    public void dismissWithAnimation() {
        dismissWithAnimation(false);
    }

    private void dismissWithAnimation(boolean fromCancel) {
        mCloseFromCancel = fromCancel;
        mDialogView.startAnimation(mModalOutAnim);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cancel_button || v.getId() == R.id.btnClose) {
            if (mCancelClickListener != null) {
                mCancelClickListener.onClick(SweetAlertDialog.this);
            } else {
                dismissWithAnimation();
            }
        } else if (v.getId() == R.id.confirm_button) {
            if (mMultiSelectListener != null) {

                ArrayList<Integer> array = new ArrayList<>();

                for (int i = 0; i < listItems.size(); i++) {
                    ListItem listItem = listItems.get(i);
                    if (listItem.isCheck)
                        array.add(new Integer(i));
                }
                mMultiSelectListener.onSelected(SweetAlertDialog.this, MiscUtil.convert(array));
            }
            if (mConfirmClickListener != null) {
                mConfirmClickListener.onClick(SweetAlertDialog.this);
            } else {
                dismissWithAnimation();
            }
        }
    }

    public SweetAlertDialog setContentText(String text) {
        mContentText = text;
        if (mContentTextView != null && mContentText != null) {
            showContentText(true);
            mContentTextView.setText(mContentText);
        }
        return this;
    }


    public SweetAlertDialog setContentText(CharSequence text) {
        mCharSequence = text;
        if (mContentTextView != null && mCharSequence != null) {
            showContentText(true);
            mContentTextView.setText(mCharSequence);
        }
        return this;
    }


    public SweetAlertDialog setArrayValue(String[] values) {
        this.mArrayValues = values;
        return this;
    }

    public SweetAlertDialog setArrayRes(String[] reses) {
        mArrayRes = reses;
        return this;
    }

    public SweetAlertDialog setAdapter(BaseAdapter adapter) {
        this.mAdapter = adapter;
        return this;
    }

    public SweetAlertDialog setEditTextValue(String text, String placehoder, int type, int limit) {
        mEditTextValue = text;
        mEditType = type;
        mEditPlaceHolder = placehoder;
        mLimit = limit;
        return this;
    }

    public String getEditTextValue() {
        return mEditText.getText().toString();
    }


    public SweetAlertDialog setIndexValues(int[] indices) {
        this.mArrayIndices = indices;
        return this;
    }

    public SweetAlertDialog setSelectListener(OnSweetSelectListener listener) {
        mSelectListener = listener;
        return this;
    }

    public SweetAlertDialog setMultiSelectListener(OnSweetMultiSelectListener listener) {
        mMultiSelectListener = listener;
        return this;
    }


    public SweetAlertDialog setTip(String text) {
        mTipText = text;
        return this;
    }


    public int getNumberValue() {
        return mNumberPicker.getValue();
    }

    public SweetAlertDialog setNumber(int number, int min, int max, String unit) {
        mNumberValue = number;
        mMinValue = min;
        mMaxValue = max;
        mUnit = unit;
        return this;
    }

    public void setAddress(String postCode, String basicAddress) {
        btnPostNumber.setText(postCode);
        btnBasic.setText(basicAddress);
    }

    public SweetAlertDialog setPhoto(Photo photo) {
        this.photo = photo;
        return this;
    }

    class ListItem {
        public String title;
        public boolean isCheck;
        public String res;

        public ListItem(String title) {
            this.title = title;
        }

        public ListItem(String title, boolean isCheck) {
            this.title = title;
            this.isCheck = isCheck;
        }

        public void setRes(String res) {
            this.res = res;
        }
    }

    class ListAdapter extends BaseAdapter {
        private ArrayList<ListItem> listItems;
        boolean isMulti = false;

        public ListAdapter(boolean isMulti) {
            this.isMulti = isMulti;
        }

        public void setData(ArrayList<ListItem> listItems) {
            this.listItems = listItems;
        }

        public ArrayList<ListItem> getData() {
            return listItems;
        }

        @Override
        public int getCount() {
            return listItems.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return listItems.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final ListItem listItem = listItems.get(position);

            if (convertView == null) {
                LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.cell_item_simple, null);
            }

            TextView tvItem = (TextView) convertView.findViewById(R.id.tvItem);
            tvItem.setText(listItem.title);
//--            ImageView ivIcon = (ImageView)convertView.findViewById(R.id.ivIcon);
//--
//--            if(!TextUtils.isEmpty(listItem.res)) {
//--                ivIcon.setVisibility(View.VISIBLE);
//--                int resId = getContext().getResources().getIdentifier(listItem.res, "drawable", getContext().getPackageName());
//--
//--                Logger.debug(TAG, "resId = " + resId);
//--                Logger.debug(TAG, "listItem.res = " + listItem.res);
//--
//--                ivIcon.setImageResource(resId);
//--            } else {
//--                ivIcon.setVisibility(View.GONE);
//--            }

            final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
            if (isMulti) {
                checkBox.setVisibility(View.VISIBLE);
                checkBox.setChecked(listItem.isCheck);
                tvItem.setGravity(Gravity.LEFT);
                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBox _checkbox = (CheckBox) v;
                        listItem.isCheck = _checkbox.isChecked();
                    }
                });
            } else
                checkBox.setVisibility(View.GONE);


            return convertView;
        }
    }

}
