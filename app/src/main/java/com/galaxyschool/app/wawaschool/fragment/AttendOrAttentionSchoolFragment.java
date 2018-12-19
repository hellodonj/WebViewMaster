package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.CaptureActivity;
import com.galaxyschool.app.wawaschool.NewBookStoreActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.SubscribeSchoolListActivity;
import com.galaxyschool.app.wawaschool.SubscribeSearchActivity;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.AddedSchoolInfoListResult;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.SubscribeUserInfo;
import com.galaxyschool.app.wawaschool.pojo.SubscribeUserListResult;
import com.galaxyschool.app.wawaschool.views.PopupMenu;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.sortlistview.ClearEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/12.
 */


public class AttendOrAttentionSchoolFragment extends ContactsListFragment {

    public static final String TAG = AttendOrAttentionSchoolFragment.class.getSimpleName();
    private TextView keywordView;
    private String keyword = "";
    private SubscribeUserListResult dataListResult;
    private int schoolType = Constants.SCHOOL_TYPE_ATTEND;
    public static final int MENU_ID_SCAN = 0;
    public static final int MENU_ID_ADD_AUTHORITY = 1;
    public static final String IS_FROM_HAPPY_STUDY = "isFromHappyStudy";

    public interface Constants {
        public static final String SCHOOL_TYPE = "school_type";
        public static final int SCHOOL_TYPE_ATTEND = 1;
        public static final int SCHOOL_TYPE_ATTENTION = 2;
    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_attend_or_attention_school, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getIntentData();
        initViews();
    }

    private void getIntentData() {
        schoolType = getActivity().getIntent().getIntExtra(Constants.SCHOOL_TYPE, Constants.SCHOOL_TYPE_ATTEND);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            loadViews();
        }
    }

    private void initViews() {
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setOnClickListener(this);
            if (schoolType == Constants.SCHOOL_TYPE_ATTEND) {
                textView.setText(R.string.attended_schools);
            } else {
                textView.setText(R.string.my_institutions);
            }
        }

        ImageView imageView = (ImageView) findViewById(R.id.contacts_header_right_ico);
        if (imageView != null) {
            if(isLogin()){
                imageView.setVisibility(View.VISIBLE);
            }else {
                imageView.setVisibility(View.INVISIBLE);
            }
            imageView.setImageResource(R.drawable.selector_icon_plus);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMoreMenu(v);
                }
            });
        }

        ClearEditText editText = (ClearEditText) findViewById(R.id.search_keyword);
        if (editText != null) {
            editText.setHint(getString(R.string.hint_school));
            editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        hideSoftKeyboard(getActivity());
                        loadSchoolData();
                        return true;
                    }
                    return false;
                }
            });
            editText.setOnClearClickListener(new ClearEditText.OnClearClickListener() {
                @Override
                public void onClearClick() {
                    keyword = "";
                    getCurrAdapterViewHelper().clearData();
                    getPageHelper().clear();
                    loadSchoolData();
                }
            });
            editText.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        }
        keywordView = editText;
        View view = findViewById(R.id.search_btn);
        if (view != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(getActivity());
                    loadSchoolData();
                }
            });
            view.setVisibility(View.VISIBLE);
        }
        view = findViewById(R.id.contacts_search_bar_layout);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }

        final PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);

        if (schoolType == Constants.SCHOOL_TYPE_ATTEND) {
        } else {
            setStopPullUpState(true);
        }

        setPullToRefreshView(pullToRefreshView);

        ListView listView = (ListView) findViewById(R.id.contacts_list_view);
        if (listView != null) {

            //添加头部
            if (listView.getHeaderViewsCount() <= 0 && schoolType != Constants.SCHOOL_TYPE_ATTEND) {

                View subscribedSchoolView = LayoutInflater.from(getActivity()).inflate(
                        R.layout.contacts_list_item, null);
                ImageView subscribedSchoolImageView = (ImageView) subscribedSchoolView.
                        findViewById(R.id.contacts_item_icon);
                subscribedSchoolImageView.setImageResource(R.drawable.icon_concerned_institutions);
                TextView subscribedSchoolTextView = (TextView) subscribedSchoolView.
                        findViewById(R.id.contacts_item_title);
                subscribedSchoolTextView.setText(R.string.has_subscribed_authority);
                listView.addHeaderView(subscribedSchoolView);
                subscribedSchoolView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        enterSubscribeSchoolListActivity();
                    }
                });
            }

            AdapterViewHelper listViewHelper = new AdapterViewHelper(getActivity(),
                    listView, R.layout.contacts_list_item) {
                @Override
                public void loadData() {
                    loadSchoolData();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    final SchoolInfo data =
                            (SchoolInfo) getDataAdapter().getItem(position);

                    if (data == null) {
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;
                    ImageView imageView = (ImageView) view.findViewById(
                            R.id.contacts_item_icon);
                    if (imageView != null) {
                        getThumbnailManager().displayUserIconWithDefault(AppSettings.getFileUrl(
                                data.getSchoolLogo()), imageView, R.drawable.default_school_icon);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //点头像进入机构名片
                                ActivityUtils.enterSchoolSpace(getActivity(),data.getSchoolId());
                            }
                        });
                    }
                    TextView textView = (TextView) view.findViewById(
                            R.id.contacts_item_title);
                    if (textView != null) {
                        textView.setText(data.getSchoolName());
                    }
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        return;
                    }
                    SchoolInfo data = (SchoolInfo) holder.data;
                    itemClickEvent(data !=null?data.getSchoolId():null);
                }
            };
            setCurrAdapterViewHelper(listView, listViewHelper);
        }
    }

    private void showMoreMenu(View view) {

        List<PopupMenu.PopupMenuData> items = new ArrayList();
        PopupMenu.PopupMenuData data = null;
        //扫一扫
        data = new PopupMenu.PopupMenuData(0, R.string.scan_me, MENU_ID_SCAN);
        items.add(data);

        //添加机构
        data = new PopupMenu.PopupMenuData(0, R.string.add_authority, MENU_ID_ADD_AUTHORITY);
        items.add(data);

        if (items.size() <= 0) {
            return;
        }

        AdapterView.OnItemClickListener itemClickListener =
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        if (view.getTag() == null) {
                            return;
                        }
                        PopupMenu.PopupMenuData data = (PopupMenu.PopupMenuData) view.getTag();
                        if (data.getId() == MENU_ID_SCAN) {
                            enterCaptureActivity();
                        } else if (data.getId() == MENU_ID_ADD_AUTHORITY) {
                            enterSubscribeSearch();
                        }
                    }
                };
        PopupMenu popupMenu = new PopupMenu(getActivity(), itemClickListener, items);
        popupMenu.showAsDropDown(view, view.getWidth(), 0);
    }

    private void enterCaptureActivity() {

        Intent intent = new Intent(getActivity(), CaptureActivity.class);
        startActivity(intent);
    }

    private void enterSubscribeSearch() {
        Intent intent = new Intent(getActivity(), SubscribeSearchActivity.class);
        intent.putExtra(SubscribeSearchActivity.EXTRA_SUBSCRIPE_SEARCH_TYPE,
                SubscribeSearchActivity.SUBSCRIPE_SEARCH_SCHOOL);
        //只有从快乐学习进入，点击才进入校本资源库，否则都进入机构名片。
        intent.putExtra(IS_FROM_HAPPY_STUDY,true);
        startActivity(intent);
    }

    private void enterSubscribeSchoolListActivity() {

        Intent intent = new Intent(getActivity(),SubscribeSchoolListActivity.class);
        startActivity(intent);
    }

    private void itemClickEvent(String schoolId) {
        Intent intent = new Intent(getActivity(), NewBookStoreActivity.class);
        Bundle args = new Bundle();
        args.putString(NewBookStoreActivity.SCHOOL_ID,schoolId);
        intent.putExtras(args);
        startActivity(intent);
    }

    private void loadViews() {
        getPageHelper().clear();
        loadSchoolData();
    }

    private void loadAttendSchoolData() {
        String keyword = this.keywordView.getText().toString().trim();
        if (!keyword.equals(this.keyword)) {
            getCurrAdapterViewHelper().clearData();
            getPageHelper().clear();
        }
        this.keyword = keyword;
        Map<String, Object> params = new HashMap();
        params.put("KeyWord", keyword);
        if(!TextUtils.isEmpty(getMemeberId())){
            params.put("MemberId",getMemeberId());
        }
        params.put("Type", 1);
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        DefaultPullToRefreshDataListener listener =
                new DefaultPullToRefreshDataListener<SubscribeUserListResult>(
                        SubscribeUserListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()
                                || getResult().getModel() == null) {
                            return;
                        }
                        updateAttendSchoolViews(getResult());
                    }
                };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.SUBSCRIBE_SEARCH_URL, params, listener);
    }

    private void loadAttentionSchoolData() {
        String keyword = this.keywordView.getText().toString().trim();
        if (!keyword.equals(this.keyword)) {
            getCurrAdapterViewHelper().clearData();
            getPageHelper().clear();
        }
        this.keyword = keyword;
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("SchoolName", keyword);
        //拉取已加入的机构
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_JOIN_SCHOOL_LIST_URL, params,
                new DefaultDataListener<AddedSchoolInfoListResult>(
                        AddedSchoolInfoListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        AddedSchoolInfoListResult result = getResult();
                        if (result == null || !result.isSuccess() || result.getModel() == null) {
                        } else {
                            updateAttentionSchoolView(result);
                        }
                    }
                });
    }


    private void updateAttentionSchoolView(AddedSchoolInfoListResult result) {
        List<SchoolInfo> list = result.getModel().getData();
        if (list == null || list.size() <= 0) {
            TipsHelper.showToast(getActivity(),
                    getString(R.string.no_data));
            if (getCurrAdapterViewHelper().hasData()) {
                getCurrAdapterViewHelper().clearData();
            }
            return;
        }
        getCurrAdapterViewHelper().setData(list);
    }

    private void loadSchoolData() {
        if (schoolType == Constants.SCHOOL_TYPE_ATTEND) {
            loadAttendSchoolData();
        } else {
            loadAttentionSchoolData();
        }
    }

    private void updateAttendSchoolViews(SubscribeUserListResult result) {
        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
            List<SubscribeUserInfo> list = result.getModel().getData();
            List<SchoolInfo> datas = datas = new ArrayList<SchoolInfo>();
            ;
            if (list != null && list.size() > 0) {
                for (SubscribeUserInfo info : list) {
                    if (info != null) {
                        SchoolInfo schoolInfo = new SchoolInfo();
                        schoolInfo.setSchoolId(info.getId());
                        schoolInfo.setSchoolName(info.getName());
                        schoolInfo.setSchoolLogo(info.getThumbnail());
                        datas.add(schoolInfo);
                    }
                }
            }
            if (datas == null || datas.size() <= 0) {
                if (getPageHelper().isFetchingFirstPage()) {
                    if(getCurrAdapterViewHelper().hasData()){
                        getCurrAdapterViewHelper().clearData();
                    }
                    TipsHelper.showToast(getActivity(),
                            getString(R.string.no_data));
                } else {
                    TipsHelper.showToast(getActivity(),
                            getString(R.string.no_more_data));
                }
                return;
            }
            if (getPageHelper().isFetchingFirstPage()) {
                if(getCurrAdapterViewHelper().hasData()){
                    getCurrAdapterViewHelper().clearData();
                }
            }
            getPageHelper().updateByPagerArgs(result.getModel().getPager());
            getPageHelper().setCurrPageIndex(
                    getPageHelper().getFetchingPageIndex());
            if (getCurrAdapterViewHelper().hasData()) {
                int position = getCurrAdapterViewHelper().getData().size();
                if (position > 0) {
                    position--;
                }
                getCurrAdapterViewHelper().getData().addAll(datas);
                getCurrAdapterView().setSelection(position);
                dataListResult.getModel().setData(getCurrAdapterViewHelper().getData());
            } else {
                getCurrAdapterViewHelper().setData(datas);
                dataListResult = result;
            }
        }
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

}
