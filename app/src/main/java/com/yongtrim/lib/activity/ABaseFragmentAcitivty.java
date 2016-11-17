package com.yongtrim.lib.activity;

import android.app.ActionBar;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.nuums.nuums.R;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.fragment.ABaseFragment;
import com.yongtrim.lib.ui.UltraEditText;
import com.yongtrim.lib.util.PixelUtil;

/**
 * hair / com.yongtrim.lib.ui
 * <p/>
 * Created by Uihyun on 15. 8. 31..
 */
public class ABaseFragmentAcitivty extends FragmentActivity {
    //public static int REQUEST_TAKE_PICTURE  = 0b0100000000000000;
    //public static int REQUEST_ADDRESS       = 0b1100000000000000;
    //public static int REQUEST_AREA      = 0b0000100000000000;
    public static int REQUEST_PICK_IMAGE = 0b0000000000000000;
    public static int REQUEST_POSTCODE = 0b1000000000000000;
    public static int REQUEST_DESCRIPTION = 0b0100000000000000;
    public static int REQUEST_CROP_IMAGE = 0b1100000000000000;
    public static int REQUEST_ROTATE_IMAGE = 0b0010000000000000;

    final String TAG = "ABaseFragmentAcitivty";

    protected ContextHelper contextHelper;
    ImageButton btnBack;
    boolean isBackbuttonDisable = false;
    ViewGroup viewButton;
    View viewActionBar;
    UltraEditText etSearch;
    private Button buttonText;
    private ImageButton buttonImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 화면 캡쳐
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
//                WindowManager.LayoutParams.FLAG_SECURE);


        contextHelper = new ContextHelper(ABaseFragmentAcitivty.this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    public void setupActionBar(String title) {
        viewActionBar = getLayoutInflater().inflate(R.layout.actionbar_normal, null);
        buttonText = (Button) viewActionBar.findViewById(R.id.actionbarTextButton);
        buttonImage = (ImageButton) viewActionBar.findViewById(R.id.actionbarImageButton);
        viewButton = (ViewGroup) viewActionBar.findViewById(R.id.viewButton);

        ActionBar actionBar = getActionBar();

        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(viewActionBar);

        final int actionBarColor = ContextCompat.getColor(getBaseContext(), R.color.green);
        actionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));

        TextView tvTitle = (TextView) viewActionBar.findViewById(R.id.tvTitle);
        tvTitle.setText(title);

        btnBack = (ImageButton) viewActionBar.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contextHelper.isDisabledClick())
                    return;
                try {
                    onKeyDown(KeyEvent.KEYCODE_BACK, null);
                } catch (Exception e) {
                    finish();
                }
            }
        });

        buttonText.setVisibility(View.GONE);
        buttonText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClicked(v);
            }
        });

        if (buttonImage != null) {
            buttonImage.setVisibility(View.GONE);
            buttonImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onButtonClicked(v);
                }
            });
        }
    }


    public UltraEditText setupActionBarSearch() {
        viewActionBar = getLayoutInflater().inflate(R.layout.actionbar_search, null);
        buttonImage = (ImageButton) viewActionBar.findViewById(R.id.actionbarImageButton);
        viewButton = (ViewGroup) viewActionBar.findViewById(R.id.viewButton);

        ActionBar actionBar = getActionBar();

        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(viewActionBar);

        final int actionBarColor = ContextCompat.getColor(getBaseContext(), R.color.green);
        actionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));


        btnBack = (ImageButton) viewActionBar.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contextHelper.isDisabledClick())
                    return;
                try {
                    onKeyDown(KeyEvent.KEYCODE_BACK, null);
                } catch (Exception e) {
                    finish();
                }
            }
        });


        etSearch = (UltraEditText) viewActionBar.findViewById(R.id.etSearch);


        if (buttonImage != null) {
            buttonImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onButtonClicked(v);
                }
            });
        }

        return etSearch;
    }

    public void setBackButtonEnable(boolean enable) {
        if (enable) {
            btnBack.setVisibility(View.VISIBLE);
        } else {
            btnBack.setVisibility(View.INVISIBLE);
        }

        isBackbuttonDisable = !enable;
    }

    public void setBackButtonVisibility(boolean enable) {
        if (enable) {
            btnBack.setVisibility(View.VISIBLE);
        } else {
            btnBack.setVisibility(View.INVISIBLE);
        }
    }


    public Button setTextButtonAndVisiable(String title) {
        buttonText.setText(title);
        buttonText.setVisibility(View.VISIBLE);
        return buttonText;
    }


    public void setTextButtonEnable(boolean enable) {
        buttonText.setEnabled(enable);

        if (enable) {
            buttonText.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.white));
        } else {
            buttonText.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.gray));
        }
    }


    public ImageButton setBackButtonAndVisiable(int resid) {
        if (resid == 0) {
            btnBack.setVisibility(View.GONE);
            return btnBack;
        }
        btnBack.setImageResource(resid);
        btnBack.setVisibility(View.VISIBLE);
        return btnBack;
    }


    public void setImageButtonAndVisiable(int resid) {
        if (resid == 0) {
            buttonImage.setVisibility(View.GONE);
            return;
        }
        buttonImage.setImageResource(resid);
        buttonImage.setVisibility(View.VISIBLE);
    }

    public void setImageButtonVisibility(boolean enable) {
        if (buttonImage == null)
            return;
        if (enable) {
            buttonImage.setVisibility(View.VISIBLE);
        } else {
            buttonImage.setVisibility(View.INVISIBLE);
        }
    }


    public void setRightButtonMarginRight(int dp) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewButton.getLayoutParams();
        params.setMargins(0, 0, PixelUtil.dpToPx(contextHelper.getContext(), dp), 0);
        viewButton.setLayoutParams(params);

    }


    public void setLeftButtonMarginLeft(int dp) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btnBack.getLayoutParams();
        params.setMargins(PixelUtil.dpToPx(contextHelper.getContext(), dp), 0, 0, 0);
        btnBack.setLayoutParams(params);

    }


    public void setImageButtonEnable(boolean enable) {

    }


    public void backgroundColor(int color) {
        viewActionBar.setBackgroundColor(color);
    }


    protected ABaseFragment addFragment(ABaseFragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getFragments() != null && fragmentManager.getFragments().size() > 0)
            return (ABaseFragment) fragmentManager.getFragments().get(0);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
        return fragment;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isBackbuttonDisable && keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onButtonClicked(View v) {
    }
}
