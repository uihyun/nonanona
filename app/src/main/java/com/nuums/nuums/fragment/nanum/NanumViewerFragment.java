package com.nuums.nuums.fragment.nanum;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.nuums.nuums.R;
import com.nuums.nuums.activity.BaseActivity;
import com.nuums.nuums.adapter.CommentAdapter;
import com.nuums.nuums.holder.NanumHolder;
import com.nuums.nuums.model.misc.Comment;
import com.nuums.nuums.model.misc.CommentManager;
import com.nuums.nuums.model.nanum.Nanum;
import com.nuums.nuums.model.nanum.NanumData;
import com.nuums.nuums.model.nanum.NanumManager;
import com.yongtrim.lib.adapter.ImagePagerAdapter;
import com.yongtrim.lib.fragment.ABaseFragment;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.model.banner.Banner;
import com.yongtrim.lib.model.banner.BannerData;
import com.yongtrim.lib.model.banner.BannerManager;
import com.yongtrim.lib.model.config.ConfigManager;
import com.yongtrim.lib.model.photo.PhotoManager;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.ui.UltraButton;
import com.yongtrim.lib.ui.autoscrollviewpager.AutoScrollViewPager;
import com.yongtrim.lib.ui.autoscrollviewpager.ClickedCallback;
import com.yongtrim.lib.ui.sweetalert.SweetAlertDialog;
import com.yongtrim.lib.ui.viewpagerindicator.CirclePageIndicator;
import com.yongtrim.lib.util.MiscUtil;
import com.yongtrim.lib.util.UIUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import de.greenrobot.event.EventBus;

/**
 * nuums / com.nuums.nuums.fragment.nanum
 * <p/>
 * Created by Uihyun on 15. 12. 27..
 */
public class NanumViewerFragment extends ABaseFragment {
    private final String TAG = getClass().getSimpleName();

    Nanum nanum;

    ViewGroup viewMain;
    AutoScrollViewPager viewPicture;
    ImagePagerAdapter imagePagerAdapter;

    NanumHolder holder;

    EditText etInput;
    UltraButton btnEnter;

    ListView listView;
    CommentAdapter commentAdapter;

    Handler timerHandler;
    Runnable timerRunnable;

    boolean isLoading;

    boolean isSelectMode;

    String nanum_id;

    View viewBannerRoot;
    CirclePageIndicator bannerIndicator;
    AutoScrollViewPager viewBanner;


    public void setNanumId(String nanum_id) {
        this.nanum_id = nanum_id;
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        if (nanum_id != null) {
            nanum = new Nanum();
            contextHelper.getActivity().setupActionBar("로딩중");
            contextHelper.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        contextHelper.showProgress(null);
                        isLoading = true;
                        NanumManager.getInstance(contextHelper).read(
                                nanum_id,
                                new Response.Listener<NanumData>() {
                                    @Override
                                    public void onResponse(NanumData response) {
                                        if (response.isSuccess()) {
                                            nanum = response.nanum;
                                            nanum.patch(contextHelper);
                                            isLoading = false;
                                            contextHelper.getActivity().setupActionBar(nanum.getTitle());
                                            contextHelper.getActivity().setImageButtonAndVisiable(R.drawable.menu_3dotwhite);

                                            try {
                                                refresh();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            SweetAlertDialog dialog = new SweetAlertDialog(getContext());
                                            dialog.setContentText(response.getErrorMessage());
                                            dialog.setCancelable(false);
                                            dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    contextHelper.getActivity().finish();
                                                }
                                            });

                                            dialog.show();
                                        }
                                        contextHelper.hideProgress();
                                    }
                                },
                                null
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } else {
            nanum = Nanum.getNanum(contextHelper.getActivity().getIntent().getStringExtra("nanum"));
            contextHelper.getActivity().setupActionBar(nanum.getTitle());
            contextHelper.getActivity().setImageButtonAndVisiable(R.drawable.menu_3dotwhite);

        }

        timerHandler = new Handler();
        timerRunnable = new Runnable() {
            @Override
            public void run() {

                try {
                    refresh();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                timerHandler.postDelayed(this, 60000); //run every minute
            }
        };
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        viewMain = (ViewGroup) inflater.inflate(R.layout.fragment_nanumviewer, container, false);

        contextHelper.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        holder = new NanumHolder();
        holder.set(viewMain, getContext(), false);

        viewMain.findViewById(R.id.viewTop).setVisibility(View.VISIBLE);
        UIUtil.setRatio(viewMain.findViewById(R.id.viewTop), getContext(), 1, 1);
        viewPicture = (AutoScrollViewPager) viewMain.findViewById(R.id.viewPicture);

//        if (UserManager.getInstance(contextHelper).getMe().isSame(nanum.getOwner())) {
//            viewMain.findViewById(R.id.viewInput).setVisibility(View.GONE);
//        }

        etInput = (EditText) viewMain.findViewById(R.id.etInput);
        btnEnter = (UltraButton) viewMain.findViewById(R.id.btnEnter);
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComment(etInput.getText().toString());
            }
        });

        listView = (ListView) viewMain.findViewById(R.id.listView);
        commentAdapter = new CommentAdapter(contextHelper, listView);
        //--commentAdapter.setData(nanum.getComments());
        listView.setAdapter(commentAdapter);

        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final Comment comment = nanum.getComments().get(position);

                if (UserManager.getInstance(contextHelper).getMe().isSame(comment.getOwner())) {
                    new SweetAlertDialog(getContext(), SweetAlertDialog.SELECT_TYPE)
                            .setTitleText("선택해 주세요")
                            .setArrayValue(new String[]{"수정하기", "삭제하기"})
                            .showCloseButton(true)
                            .setSelectListener(new SweetAlertDialog.OnSweetSelectListener() {
                                @Override
                                public void onSelected(SweetAlertDialog sweetAlertDialog, int index) {
                                    sweetAlertDialog.dismissWithAnimation();

                                    switch (index) {
                                        case 0:
                                            new SweetAlertDialog(getContext(), SweetAlertDialog.EDITTEXT_TYPE)
                                                    .setTitleText("댓글 수정하기")
                                                    .setEditTextValue(comment.getMessage(), null, InputType.TYPE_TEXT_FLAG_MULTI_LINE, 0)
                                                    .showCancelButton(true)
                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sDialog) {
                                                            sDialog.dismissWithAnimation();
                                                            contextHelper.showProgress(null);

                                                            CommentManager.getInstance(contextHelper).updateInNanum(nanum, comment.getIndex(), sDialog.getEditTextValue(),
                                                                    new Response.Listener<NanumData>() {
                                                                        @Override
                                                                        public void onResponse(NanumData response) {
                                                                            contextHelper.hideProgress();
                                                                            if (response.isSuccess()) {
                                                                                nanum = response.nanum;
                                                                                nanum.patch(contextHelper);

                                                                                EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_CHANGE_NANUM).setObject(response.nanum));

                                                                                try {
                                                                                    refresh();
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }

                                                                            } else {
                                                                                new SweetAlertDialog(getContext())
                                                                                        .setContentText(response.getErrorMessage())
                                                                                        .show();
                                                                            }
                                                                        }
                                                                    },
                                                                    null
                                                            );

                                                        }
                                                    })
                                                    .show();
                                            break;
                                        case 1:

                                            if (nanum.getOwner().isMe(contextHelper)) {
                                                contextHelper.showProgress(null);
                                                CommentManager.getInstance(contextHelper).deleteInNanum(nanum, comment.getIndex(),
                                                        new Response.Listener<NanumData>() {
                                                            @Override
                                                            public void onResponse(NanumData response) {
                                                                contextHelper.hideProgress();
                                                                if (response.isSuccess()) {
                                                                    nanum = response.nanum;
                                                                    nanum.patch(contextHelper);

                                                                    EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_CHANGE_NANUM).setObject(response.nanum));

                                                                    if (response.user != null) {
                                                                        UserManager.getInstance(contextHelper).setMe(response.user);
                                                                        EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_CHANGE_ME).setObject(response.user));
                                                                    }


                                                                    try {
                                                                        refresh();
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                } else {
                                                                    new SweetAlertDialog(getContext())
                                                                            .setContentText(response.getErrorMessage())
                                                                            .show();
                                                                }
                                                            }
                                                        },
                                                        null
                                                );

                                            } else {
                                                new SweetAlertDialog(getContext())
                                                        .setContentText("해당나눔에는 다시 지원 할 수 없습니다 삭제하시겠습니까?")
                                                        .showCancelButton(true)
                                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                            @Override
                                                            public void onClick(final SweetAlertDialog sweetAlertDialog) {

                                                                sweetAlertDialog.dismissWithAnimation();

                                                                contextHelper.showProgress(null);
                                                                CommentManager.getInstance(contextHelper).deleteInNanum(nanum, comment.getIndex(),
                                                                        new Response.Listener<NanumData>() {
                                                                            @Override
                                                                            public void onResponse(NanumData response) {
                                                                                contextHelper.hideProgress();
                                                                                if (response.isSuccess()) {
                                                                                    nanum = response.nanum;
                                                                                    nanum.patch(contextHelper);

                                                                                    EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_CHANGE_NANUM).setObject(response.nanum));

                                                                                    try {
                                                                                        refresh();
                                                                                    } catch (Exception e) {
                                                                                        e.printStackTrace();
                                                                                    }

                                                                                } else {
                                                                                    new SweetAlertDialog(getContext())
                                                                                            .setContentText(response.getErrorMessage())
                                                                                            .show();
                                                                                }
                                                                            }
                                                                        },
                                                                        null
                                                                );


                                                            }
                                                        })
                                                        .show();
                                            }

                                            break;
                                        case 2:
                                            break;
                                    }
                                }
                            })
                            .show();
                } else {
                    if (isSelectMode) {
                        if (nanum.getSelectCount() == nanum.getAmount()) {
                            comment.isSelect = false;
                        } else {
                            comment.isSelect = !comment.isSelect;
                        }
                        try {
                            refresh();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });


        viewBannerRoot = viewMain.findViewById(R.id.viewBannerRoot);
        viewBanner = (AutoScrollViewPager) viewMain.findViewById(R.id.viewBanner);
        viewBanner.setAutoScrollDurationFactor(10.0);
        viewBanner.setInterval(5000);
        viewBanner.startAutoScroll();
        UIUtil.setRatio(viewMain.findViewById(R.id.viewBannerRoot), getContext(), 720, 90);

        bannerIndicator = (CirclePageIndicator) viewMain.findViewById(R.id.bannerIndicator);

        refresh();

        BannerManager.getInstance(contextHelper).find(
                new Response.Listener<BannerData>() {
                    @Override
                    public void onResponse(BannerData response) {
                        ConfigManager.getInstance(contextHelper).setBanner(response.banner);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshBanner();
                            }
                        });
                    }
                },
                null
        );

        return viewMain;
    }


    public void refreshBanner() {

        final Banner banner = ConfigManager.getInstance(contextHelper).getBanner();

        if (banner != null) {
            viewBannerRoot.setVisibility(View.VISIBLE);
            List<String> listUrl = new ArrayList<>();

            if (banner.getPhotos() != null && banner.getPhotos().size() > 0)
                listUrl.add(banner.getPhotos().get(0).getUrl());
            else {
                listUrl.add("");
            }

            ImagePagerAdapter bannerAdapter = new ImagePagerAdapter(getContext(), listUrl).setInfiniteLoop(false);
            bannerAdapter.setScaleType(ImageView.ScaleType.FIT_CENTER);

            viewBanner.setAdapter(bannerAdapter);

            viewBanner.setClickedCallback(new ClickedCallback() {
                @Override
                public void clicked(int position) {

                    if (!TextUtils.isEmpty(banner.getUrl())) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(banner.getUrl()));
                        startActivity(browserIntent);

                        BannerManager.getInstance(contextHelper).count(
                                banner.getId(),
                                new Response.Listener<BannerData>() {
                                    @Override
                                    public void onResponse(BannerData response) {
                                    }
                                },
                                null
                        );
                    }
                }
            });
            bannerIndicator.setViewPager(viewBanner);

        } else {
            viewBannerRoot.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        timerHandler.postDelayed(timerRunnable, 500);

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
        timerHandler.removeCallbacks(timerRunnable);
        super.onPause();
    }

    public void onButtonClicked(View v) {

        switch (v.getId()) {
            case R.id.btnSelect:
                if (nanum.getMethod().equals(Nanum.METHOD_SELECT) && nanum.getSelectCount() < nanum.getAmount()) {
                    new SweetAlertDialog(getContext()).setContentText("당첨자 수가 모자랍니다.").show();
                } else {

                    String message = "확정 후에는 당첨자를 변경할 수 없습니다. 진행 하시겠습니까?";
                    if (nanum.getMethod().equals(Nanum.METHOD_RANDOM)) {
                        message = "추첨을 시작하면 결과를 번복할 수 없습니다. 추첨을 시작하겠습니까?";
                        List<Comment> applys = nanum.getApplys();
                        Collections.shuffle(applys, new Random(System.nanoTime()));

                        if (applys.size() < nanum.getAmount()) {
                            new SweetAlertDialog(getContext()).setContentText("신청자 수가 모자랍니다.").show();
                            return;
                        }

                        for (int i = 0; i < nanum.getAmount(); i++) {
                            Comment apply = applys.get(i);
                            apply.isSelect = true;
                        }
                    }

                    new SweetAlertDialog(getContext())
                            .setContentText(message)
                            .showCancelButton(true)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(final SweetAlertDialog sweetAlertDialog) {

                                    sweetAlertDialog.dismissWithAnimation();

                                    contextHelper.showProgress(null);

                                    NanumManager.getInstance(contextHelper).won(
                                            nanum,
                                            new Response.Listener<NanumData>() {
                                                @Override
                                                public void onResponse(final NanumData response) {
                                                    contextHelper.hideProgress();
                                                    if (response.isSuccess()) {
                                                        nanum = response.nanum;
                                                        nanum.patch(contextHelper);
                                                        EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_CHANGE_NANUM).setObject(response.nanum));

                                                        try {
                                                            refresh();
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    } else {
                                                        new SweetAlertDialog(getContext())
                                                                .setContentText(response.getErrorMessage())
                                                                .show();
                                                    }
                                                }
                                            },
                                            null
                                    );


                                }
                            })
                            .show();

                    break;
                }
                break;
            case R.id.actionbarImageButton:
                if (UserManager.getInstance(contextHelper).getMe().isSame(nanum.getOwner())) {
                    new SweetAlertDialog(getContext(), SweetAlertDialog.SELECT_TYPE)
                            .setTitleText("선택해 주세요")
                            .showCloseButton(true)
                            .setArrayValue(new String[]{"수정하기", "공유하기", "삭제하기"})
                            .setSelectListener(new SweetAlertDialog.OnSweetSelectListener() {
                                @Override
                                public void onSelected(SweetAlertDialog sweetAlertDialog, int index) {
                                    sweetAlertDialog.dismissWithAnimation();

                                    switch (index) {
                                        case 0: {
                                            Intent i = new Intent(getContext(), BaseActivity.class);
                                            i.putExtra("activityCode", BaseActivity.ActivityCode.NANUMEDT.ordinal());
                                            i.putExtra("isModify", true);
                                            i.putExtra("nanum", nanum.toString());
                                            getContext().startActivity(i);
                                        }
                                        break;
                                        case 1:
                                            contextHelper.shareViaKakao(nanum);
                                            break;
                                        case 2:
                                            new SweetAlertDialog(getContext())
                                                    .setContentText("삭제 하시겠습니까?")
                                                    .showCancelButton(true)
                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(final SweetAlertDialog sweetAlertDialog) {

                                                            sweetAlertDialog.dismissWithAnimation();

                                                            contextHelper.showProgress(null);

                                                            NanumManager.getInstance(contextHelper).delete(
                                                                    nanum,
                                                                    new Response.Listener<NanumData>() {
                                                                        @Override
                                                                        public void onResponse(final NanumData response) {

                                                                            contextHelper.hideProgress();


                                                                            if (response.isSuccess()) {

                                                                                if (response.user != null) {
                                                                                    UserManager.getInstance(contextHelper).setMe(response.user);
                                                                                    EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_CHANGE_ME).setObject(response.user));
                                                                                }

                                                                                contextHelper.getActivity().runOnUiThread(new Runnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        deleteAndfinish(response.nanum);
                                                                                    }
                                                                                });

                                                                            } else {
                                                                                new SweetAlertDialog(getContext())
                                                                                        .setContentText(response.getErrorMessage())
                                                                                        .show();
                                                                            }
                                                                        }
                                                                    },
                                                                    null
                                                            );


                                                        }
                                                    })
                                                    .show();

                                            break;
                                    }
                                }
                            })
                            .show();
                } else {
                    new SweetAlertDialog(getContext(), SweetAlertDialog.SELECT_TYPE)
                            .setTitleText("선택해 주세요")
                            .setArrayValue(new String[]{"공유하기"})
                            .showCloseButton(true)
                            .setSelectListener(new SweetAlertDialog.OnSweetSelectListener() {
                                @Override
                                public void onSelected(SweetAlertDialog sweetAlertDialog, int index) {
                                    sweetAlertDialog.dismissWithAnimation();

                                    switch (index) {
                                        case 0:
                                            contextHelper.shareViaKakao(nanum);
                                            break;
                                    }
                                }
                            })
                            .show();
                }


                break;
        }
    }

    public void onEvent(PushMessage pushMessage) {
        switch (pushMessage.getActionCode()) {
            case PushMessage.ACTIONCODE_CHANGE_NANUM: {
                Nanum _nanum = (Nanum) pushMessage.getObject(contextHelper);
                if (nanum.isSame(_nanum)) {
                    nanum = _nanum;
                    contextHelper.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                refresh();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
            break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    void refresh() {

        if (isLoading)
            return;


        if (nanum.isAdmin()) {
            viewMain.findViewById(R.id.viewAddress).setVisibility(View.GONE);
        } else {
            viewMain.findViewById(R.id.viewAddress).setVisibility(View.VISIBLE);
        }


        PhotoManager.getInstance(contextHelper).setPhotoSmall(holder.ivAvatar, nanum.getOwner().getPhoto());


        holder.tvTitle.setText(nanum.getTitle());

        holder.tvNickname.setText(nanum.getOwner().getNicknameSafe());
        holder.tvNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contextHelper.setUserAction(nanum.getOwner());
            }
        });

        holder.ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contextHelper.setUserAction(nanum.getOwner());
            }
        });


        holder.tvAddress.setText(nanum.getAddressFake());
        holder.tvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(contextHelper.getContext(), BaseActivity.class);
                i.putExtra("activityCode", BaseActivity.ActivityCode.MAP.ordinal());
                i.putExtra("nanum", nanum.toString());
                contextHelper.getContext().startActivity(i);
            }
        });

        holder.tvDate.setText(MiscUtil.getPostTime(nanum.getTimeCreated()));

        holder.setMark(nanum, getContext());
        holder.setTimer(nanum, getContext());

        TextView tvAmountMethod = (TextView) viewMain.findViewById(R.id.tvAmountMethod);
        tvAmountMethod.setText("나눔수량 " + nanum.getAmount() + "개 | " + nanum.getMethodDesc());

        if (imagePagerAdapter == null) {
            imagePagerAdapter = new ImagePagerAdapter(getContext(), nanum.getMediumUrlArray()).setInfiniteLoop(false);
            viewPicture.setAdapter(imagePagerAdapter);
        }

        String urlTag = nanum.getUrlTag();
        if (!urlTag.equals(imagePagerAdapter.getUrlTag())) {
            imagePagerAdapter = new ImagePagerAdapter(getContext(), nanum.getMediumUrlArray()).setInfiniteLoop(false);
            viewPicture.setAdapter(imagePagerAdapter);
            viewPicture.setClickedCallback(new ClickedCallback() {
                @Override
                public void clicked(int position) {
//                    Intent galleryActivity = new Intent(contextHelper.getActivity(), GalleryActivity.class);
//                    galleryActivity.putExtra("position", position);
//                    galleryActivity.putExtra("hair", hair.toString());
//                    startActivity(galleryActivity);
                }
            });
        }
        imagePagerAdapter.setUrlTag(urlTag);

        CirclePageIndicator pageIndicator = (CirclePageIndicator) viewMain.findViewById(R.id.pageIndicator);
        pageIndicator.setViewPager(viewPicture);


        if (nanum.getStatus().equals(Nanum.STATUS_FINISH_TIME) || nanum.getStatus().equals(Nanum.STATUS_FINISH_SELECT)) {
            viewMain.findViewById(R.id.viewFinishBig).setVisibility(View.VISIBLE);
        } else {
            viewMain.findViewById(R.id.viewFinishBig).setVisibility(View.GONE);
        }

        TextView tvDiscription = (TextView) viewMain.findViewById(R.id.tvDiscription);
        tvDiscription.setText(nanum.getDescription());

        final UltraButton btnApply = (UltraButton) viewMain.findViewById(R.id.btnApply);
        btnApply.setText("" + nanum.getApplyCount());

        if (nanum.getComments().size() == 0) {
            viewMain.findViewById(R.id.viewSeparator).setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
        } else {
            viewMain.findViewById(R.id.viewSeparator).setVisibility(View.VISIBLE);
            listView.setVisibility(View.VISIBLE);
        }

        listView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            listView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            listView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                        contextHelper.setListViewHeightBasedOnChildren(listView);
                    }
                });


        final UltraButton btnBookmarkCnt = (UltraButton) viewMain.findViewById(R.id.btnBookmarkCnt);
        final UltraButton btnBookmark = (UltraButton) viewMain.findViewById(R.id.btnBookmark);


        btnBookmarkCnt.setText("" + nanum.getBookmarks().size());

        if (nanum.isBookmark()) {
            btnBookmark.setIconResource(R.drawable.big_like_on);
        } else {
            btnBookmark.setIconResource(R.drawable.big_like_off);
        }

        btnBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!UserManager.getInstance(contextHelper).getMe().isLogin()) {
                    contextHelper.showLogin();
                    return;
                }
                if (NanumManager.getInstance(contextHelper).isBookmarking())
                    return;
                NanumManager.getInstance(contextHelper).setIsBookmarking(true);

                //btnBookmark.setProgressBar(true);

                if (!nanum.isBookmark()) {
                    btnBookmark.setIconResource(R.drawable.big_like_on);
                } else {
                    btnBookmark.setIconResource(R.drawable.big_like_off);
                }


                NanumManager.getInstance(contextHelper).bookmark(
                        nanum, !nanum.isBookmark(),
                        new Response.Listener<NanumData>() {
                            @Override
                            public void onResponse(NanumData response) {
                                NanumManager.getInstance(contextHelper).setIsBookmarking(false);
                                //btnBookmark.setProgressBar(false);

                                if (response.isSuccess()) {
                                    nanum = response.nanum;
                                    nanum.patch(contextHelper);

                                    EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_CHANGE_NANUM).setObject(response.nanum));

                                    try {
                                        refresh();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        },
                        null
                );
            }
        });


        viewMain.findViewById(R.id.viewSelect).setVisibility(View.GONE);
        UltraButton btnSelect = (UltraButton) viewMain.findViewById(R.id.btnSelect);
        isSelectMode = false;

        if (UserManager.getInstance(contextHelper).getMe().isSame(nanum.getOwner())) {
            if ((nanum.getStatus().equals(Nanum.STATUS_ONGOING) || nanum.getStatus().equals(Nanum.STATUS_FINISH_TIME))
                    && nanum.getAmount() <= nanum.getApplyCount()) {

                viewMain.findViewById(R.id.viewSelect).setVisibility(View.VISIBLE);

                if (nanum.getMethod().equals(Nanum.METHOD_SELECT)) {
                    btnSelect.setText("나눔 선정하기 (" + nanum.getSelectCount() + "/" + nanum.getAmount() + ")");
                    isSelectMode = true;

                } else if (nanum.getMethod().equals(Nanum.METHOD_RANDOM)) {
                    btnSelect.setText("나눔 추첨하기");
                }
            }
        }

        commentAdapter.setIsSelectMode(isSelectMode);

        commentAdapter.setData(nanum, nanum.getComments());
        commentAdapter.notifyDataSetChanged();
    }

    void sendComment(String message) {

        if (TextUtils.isEmpty(message)) {
            return;
        }

        if (!UserManager.getInstance(contextHelper).getMe().isLogin()) {
            contextHelper.showLogin();
            return;
        }

        //btnEnter.setProgressBar(true);

        btnEnter.setEnabled(false, false);
        CommentManager.getInstance(contextHelper).createInNanum(nanum, message,
                new Response.Listener<NanumData>() {
                    @Override
                    public void onResponse(NanumData response) {
                        //btnEnter.setProgressBar(false);
                        btnEnter.setEnabled(true, false);

                        if (response.isSuccess()) {

                            nanum = response.nanum;
                            nanum.patch(contextHelper);

                            EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_CHANGE_NANUM).setObject(response.nanum));

                            if (response.user != null) {
                                UserManager.getInstance(contextHelper).setMe(response.user);
                                EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_CHANGE_ME).setObject(response.user));
                            }


                            try {
                                refresh();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            etInput.setText("");

                            try {
                                InputMethodManager im = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                im.hideSoftInputFromWindow(etInput.getWindowToken(), 0);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            new SweetAlertDialog(getContext())
                                    .setContentText(response.getErrorMessage())
                                    .show();
                        }
                    }
                },
                null
        );
    }

    void deleteAndfinish(Nanum nanum) {
        contextHelper.getActivity().finish();
        EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_DELETE_NANUM).setObject(nanum));
    }
}



