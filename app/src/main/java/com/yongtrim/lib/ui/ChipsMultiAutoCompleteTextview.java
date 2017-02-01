package com.yongtrim.lib.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.android.volley.Response;
import com.nuums.nuums.R;
import com.nuums.nuums.adapter.UserAdapter;
import com.nuums.nuums.model.user.NsUser;
import com.nuums.nuums.model.user.NsUserListData;
import com.nuums.nuums.model.user.NsUserSearch;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.RequestManager;
import com.yongtrim.lib.model.user.UserManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * nuums / com.yongtrim.lib.ui
 * <p/>
 * http://kpbird.com/2013/02/android-chips-edittext-token-edittext.html
 * https://www.codeofaninja.com/2013/12/android-autocompletetextview-custom-arrayadapter-sqlite.html
 *
 * Created by Uihyun on 16. 1. 14..
 */
public class ChipsMultiAutoCompleteTextview extends MultiAutoCompleteTextView implements OnItemClickListener {

    private final String TAG = "ChipsMultiAutoCompleteTextview";
    ContextHelper contextHelper;

    NsUserSearch[] userSearchs;


    ArrayList<NsUserSearch> selected;
    boolean mBlockCompletion = false;

    int maxUserCount = 0;


    private OnChangeListener mChangeListener;
    public static interface OnChangeListener {
        public void onEditTextChanged(ChipsMultiAutoCompleteTextview chipsMultiAutoCompleteTextview);
    }


    public ChipsMultiAutoCompleteTextview(Context context) {
        super(context);
        init(context);
    }


    public ChipsMultiAutoCompleteTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    public ChipsMultiAutoCompleteTextview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }


    public void init(Context context){
        setOnItemClickListener(this);
        addTextChangedListener(textWather);

        selected = new ArrayList<>();
    }


    public void setMaxUserCount(int count) {
        maxUserCount = count;
    }


    public void setContextHelper(ContextHelper contextHelper) {
        this.contextHelper = contextHelper;
    }

    public void setChangeListener(OnChangeListener listener) {
        mChangeListener = listener;
    }


    /*TextWatcher, If user type any country name and press comma then following code will regenerate chips */
    private TextWatcher textWather = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if(mBlockCompletion) {
                mBlockCompletion = false;
                return;
            }

            if (isPerformingCompletion()) {
                // An item has been selected from the list. Ignore.
                return;
            }

            if(count >= 1) {
                String chips[] = getText().toString().trim().split(" ");

                if(chips.length > 0) {


                    RequestManager.getRequestQueue().cancelAll(UserManager.getInstance(contextHelper));
                    userSearchs = UserManager.getInstance(contextHelper).getMe().getSearchList(contextHelper, chips[chips.length - 1], null);
                    UserAdapter myAdapter = new UserAdapter(contextHelper.getContext(), R.layout.cell_searchuser, userSearchs);
                    myAdapter.setContextHelper(contextHelper);
                    setAdapter(myAdapter);

                    myAdapter.notifyDataSetChanged();

                    if (s.charAt(start) == ' ') {
                        mBlockCompletion = true;
                        setChips();
                    }

                    findNickname(chips[chips.length - 1]);
                }
            } else {
                String chips[] = getText().toString().trim().split(" ");

                if(s.toString().length() >= 1 && s.toString().charAt(s.toString().length() - 1) != ' ') {
                    String result = "";
                    for(int i = 0;i < chips.length - 1;i++) {
                        result += (chips[i] + " ");
                    }
                    mBlockCompletion = true;
                    setText(result);
                    setChips();
                }
            }
        }

        String beforeText;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,int after) {
            beforeText = s.toString();
        }
        @Override
        public void afterTextChanged(Editable s) {

            String chips[] = getText().toString().trim().split(" ");

            if(maxUserCount > 0 && maxUserCount < chips.length) {
                setText(beforeText);
                setChips();
                setSelection(beforeText.length());
            }
            if(mChangeListener != null)
                mChangeListener.onEditTextChanged(ChipsMultiAutoCompleteTextview.this);
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        RequestManager.getRequestQueue().cancelAll(UserManager.getInstance(contextHelper));
        NsUserSearch user = userSearchs[position];
        if (user.getUser() != null) {
            selected.add(userSearchs[position]);
        }
        setChips(); // call generate chips when user select any item from auto complete
    }

    NsUserSearch isIn(String nickname) {
        for(NsUserSearch userSearch : selected) {
            if(userSearch.toString().equals(nickname))
                return userSearch;
        }
        return null;
    }


    /*This function has whole logic for chips generate*/
    public void setChips(){
        if(getText().toString().contains(" ")) { // check comman in string
            // split string wich comma
            String chips[] = getText().toString().trim().split(" ");

            String result = "";

            for(int index = 0;index < chips.length;index++) {
                if(isIn(chips[index]) != null && isIn(chips[index]).getUser() != null) {
                    result += chips[index] + " ";
                }
            }

            chips = result.split(" ");

            SpannableStringBuilder ssb = new SpannableStringBuilder(result);

            int x = 0;
            // loop will generate ImageSpan for every country name separated by comma

            for (String c : chips) {

                // inflate chips_edittext layout
                LayoutInflater lf = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                TextView textView = (TextView) lf.inflate(R.layout.view_chips_edittext, null);
                textView.setText(c); // set text
                //setFlags(textView, c); // set flag image
                // capture bitmapt of genreated textview
                int spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                textView.measure(spec, spec);

                textView.layout(0, 0, textView.getMeasuredWidth(), textView.getMeasuredHeight());
                Bitmap b = Bitmap.createBitmap(textView.getWidth(), textView.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(b);
                canvas.translate(-textView.getScrollX(), -textView.getScrollY());
                textView.draw(canvas);
                textView.setDrawingCacheEnabled(true);
                Bitmap cacheBmp = textView.getDrawingCache();
                Bitmap viewBmp = cacheBmp.copy(Bitmap.Config.ARGB_8888, true);
                textView.destroyDrawingCache();  // destory drawable
                // create bitmap drawable for imagespan
                BitmapDrawable bmpDrawable = new BitmapDrawable(getContext().getResources(), viewBmp);
                bmpDrawable.setBounds(0, 0, bmpDrawable.getIntrinsicWidth(), bmpDrawable.getIntrinsicHeight());
                // create and set imagespan
                ssb.setSpan(new ImageSpan(bmpDrawable), x, x + c.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                x = x + c.length() + 1;
            }

            // set chips span
            setText(ssb);
            // move cursor to last
            setSelection(getText().length());
        }
    }



    public void findNickname(final String nickname) {

        JSONObject option = null;
        try {
            option = new JSONObject();
            option.put("nickname", nickname);
        } catch(Exception e) {
        }

        UserManager.getInstance(contextHelper).find(1, option,
                new Response.Listener<NsUserListData>() {
                    @Override
                    public void onResponse(final NsUserListData response) {


                        contextHelper.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                userSearchs = UserManager.getInstance(contextHelper).getMe().getSearchList(contextHelper, nickname, response.getUserList().getUsers());
                                UserAdapter myAdapter = new UserAdapter(contextHelper.getContext(), R.layout.cell_searchuser, userSearchs);
                                myAdapter.setContextHelper(contextHelper);
                                setAdapter(myAdapter);
                                myAdapter.notifyDataSetChanged();

                            }
                        });

//                        if(response.getUserList().getUsers().size() == 0) {
//                            new SweetAlertDialog(getContext()).setContentText("아이디가 없습니다.").show();
//                        } else {
//                            users = response.getUserList().getUsers();
//                            step = 1;
//                            refresh();
//                        }
                    }
                },
                null
        );

    }


    public List<NsUser> getTags() {

        List<NsUser> tags = new ArrayList<>();

        String chips[] = getText().toString().trim().split(" ");
        for(int index = 0;index < chips.length;index++) {
            if(isIn(chips[index]) != null && isIn(chips[index]).getUser() != null) {

                tags.add(isIn(chips[index]).getUser());
            }
        }

        return tags;
    }


    public void setTags(List<NsUser> users) {
        if(users == null || users.size() == 0)
            return;

        String result = "";
        for(NsUser user : users) {
            selected.add(new NsUserSearch(user.getNickname(), user));
            result += user.getNickname() + " ";
        }


        mBlockCompletion = true;
        setText(result);
        setChips();
    }

    public void setTag(NsUser user) {
        if(user == null)
            return;

        String result = "";
        selected.add(new NsUserSearch(user.getNickname(), user));
        result += user.getNickname() + " ";

        mBlockCompletion = true;
        setText(result);
        setChips();
    }

    /* this method set country flag image in textview's drawable component, this logic is not optimize, you need to change as per your requirement*/
//    public void setFlags(TextView textView,String country){
//        country = country.trim();
//        if(country.equals("India")){
//            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.india, 0);
//        }
//        else if(country.equals("United States")){
//            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.unitedstates, 0);
//        }
//        else if(country.equals("Canada")){
//            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.canada, 0);
//        }
//        else if(country.equals("Australia")){
//            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.australia, 0);
//        }
//        else if(country.equals("United Kingdom")){
//            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.unitedkingdom, 0);
//        }
//        else if(country.equals("Philippines")){
//            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.philippines, 0);
//        }
//        else if(country.equals("Japan")){
//            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.japan, 0);
//        }
//        else if(country.equals("Italy")){
//            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.italy, 0);
//        }
//        else if(country.equals("Germany")){
//            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.germany, 0);
//        }
//        else if(country.equals("Russia")){
//            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.russia, 0);
//        }
//        else if(country.equals("Malaysia")){
//            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.malaysia, 0);
//        }
//        else if(country.equals("France")){
//            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.france, 0);
//        }
//        else if(country.equals("Sweden")){
//            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sweden, 0);
//        }
//        else if(country.equals("New Zealand")){
//            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.newzealand, 0);
//        }
//        else if(country.equals("Singapore")){
//            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.singapore, 0);
//        }
//
//
//    }

    // this is how to disable AutoCompleteTextView filter
//    @Override
//    protected void performFiltering(final CharSequence text, final int keyCode) {
//        String filterText = "";
//        super.performFiltering(filterText, keyCode);
//    }
}
