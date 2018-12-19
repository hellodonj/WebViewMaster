package com.galaxyschool.app.wawaschool.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.AirClassInterAction;
import com.galaxyschool.app.wawaschool.pojo.AirClassInterActionListResult;
import com.galaxyschool.app.wawaschool.pojo.Emcee;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.lqwawa.apps.views.ContainsEmojiEditText;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by E450 on 2017/3/14.
 */

public class InteractiveFragment extends ContactsListFragment {
    public static String TAG = InteractiveFragment.class.getSimpleName();
    private LinearLayout bottomLayout;
    private ContainsEmojiEditText emojiEditText;
    private TextView sendBtn;
    private Emcee onlineRes;
    //第一次进入当前界面没有数据不给于toast提示
    private boolean isFirstIn=true;
    private boolean isNeedRefresh=true;
    public InteractiveFragment(Emcee onlineRes,LinearLayout bottomLayout) {
        this.onlineRes = onlineRes;
        this.bottomLayout=bottomLayout;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_interactive, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isNeedRefresh) {
            loadInteractionData();
            isNeedRefresh=false;
        }
    }

    private void initViews() {
        initNormalView();
        initListView();
    }

    private void initNormalView() {
        emojiEditText = (ContainsEmojiEditText) bottomLayout.findViewById(R.id.comment_edittext_send);
        sendBtn = (TextView) bottomLayout.findViewById(R.id.send_textview);
        sendBtn.setOnClickListener(this);

    }

    private void initListView() {
        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        if (pullToRefreshView != null) {
            setPullToRefreshView(pullToRefreshView);
        }
        ListView listView = (ListView) findViewById(R.id.listview);
        if (listView != null) {
            AdapterViewHelper gridViewHelper = new AdapterViewHelper(getActivity(),
                    listView, R.layout.interactive_item) {
                @Override
                public void loadData() {
                    loadInteractionData();
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {

                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    if (view == null) {
                        return view;
                    }
                   final AirClassInterAction data = (AirClassInterAction) getDataAdapter().getItem
                            (position);
                    if (data == null) {
                        return view;
                    }
                    TextView textView = null;
                    ImageView imageView = null;
                    imageView = (ImageView) view.findViewById(R.id.comment_sender_icon);
                    if (imageView != null) {
                        getThumbnailManager().displayThumbnailWithDefault(
                                AppSettings.getFileUrl(data.getHeadPicUrl()), imageView,
                                R.drawable.default_user_icon);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                ActivityUtils.enterPersonalSpace(getActivity(), String.valueOf(data.getMemberId()));
                            }
                        });
                    }
                    textView = (TextView) view.findViewById(R.id.comment_sender_name);
                    if (textView != null) {
                        if (!TextUtils.isEmpty(data.getRealName())){
                            textView.setText(data.getRealName());
                        }else if (!TextUtils.isEmpty(data.getCreateName())){
                            textView.setText(data.getCreateName());
                        }else if (!TextUtils.isEmpty(data.getNickName())){
                            textView.setText(data.getNickName());
                        }else {
                            textView.setText(R.string.anonym);
                        }
                    }
                    textView = (TextView) view.findViewById(R.id.comment_content);
                    if (textView != null) {
                        textView.setText(data.getContents());
                    }
                    textView = (TextView) view.findViewById(R.id.comment_date);
                    if (textView != null) {
                        textView.setText(data.getContentsTime());
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;
                    view.setTag(holder);
                    return view;
                }
            };
            setCurrAdapterViewHelper(listView, gridViewHelper);
        }
    }

    /**
     * 加载互动交流列表
     */
    public void loadInteractionData(){
        //每次加载拉取16数据
        pageHelper.setPageSize(16);
        Map<String, Object> params = new HashMap();
        //必填
        params.put("ExtId",onlineRes.getId());
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        DefaultDataListener listener = new DefaultDataListener<AirClassInterActionListResult>(
                AirClassInterActionListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                AirClassInterActionListResult result = getResult();
                if (result == null || !result.isSuccess()
                        || result.getModel() == null) {
                    return;
                }
                upDateInterationList(result);
            }

            @Override
            public void onError(NetroidError error) {
                super.onError(error);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        };
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.GET_AIRCLASSROOM_INTERACTION_LIT_BASE_URL, params, listener);
    }
    /**
     * 更新互动交流列表
     * @param result
     */
    private void upDateInterationList(AirClassInterActionListResult result){
        List<AirClassInterAction> interActionLists=result.getModel().getData();
        if (interActionLists==null||interActionLists.size()<=0){
            if (!isFirstIn) {
                TipMsgHelper.ShowLMsg(getActivity(), getString(R.string.no_more_data));
            }else {
                isFirstIn=false;
            }
            return;
        }
        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
            if (getPageHelper().isFetchingFirstPage()) {
                getCurrAdapterViewHelper().clearData();
            }
            getPageHelper().updateByPagerArgs(result.getModel().getPager());
            getPageHelper().setCurrPageIndex(getPageHelper().getFetchingPageIndex());
            if (getCurrAdapterViewHelper().hasData()) {
                int position = getCurrAdapterViewHelper().getData().size();
                if (position > 0) {
                    position--;
                }
                getCurrAdapterViewHelper().getData().addAll(interActionLists);
                getCurrAdapterView().setSelection(position);
            } else {
                getCurrAdapterViewHelper().setData(interActionLists);
            }
        }
    }
    /**
     * 新增互动交流的信息
     * @param
     */
    private void createMessageToInteraction(String content){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ExtId",onlineRes.getId());
        params.put("MemberId",getMemeberId());
        params.put("Contents",content);
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestDataResultListener<DataModelResult>(
                        getActivity(), DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        } else {
                            String errorMessage=getResult().getErrorMessage();
                            if (TextUtils.isEmpty(errorMessage)){
                                pageHelper.setFetchingPageIndex(0);
                                loadInteractionData();
                                TipsHelper.showToast(getActivity(),getString(R.string.send_success));
                            }else {
                                TipMsgHelper.ShowLMsg(getActivity(),errorMessage);
                            }

                        }
                    }
                    @Override
                    public void onFinish() {
                        super.onFinish();
                        dismissLoadingDialog();
                    }
                };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.ADD_AIRCLASSROOM_INTERACTION_BASE_URL, params, listener);
    }
    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.send_textview){
            sendContent();
        }else {
            super.onClick(v);
        }
    }

    /**
     * 发送互动交流的内容
     */
    private void sendContent(){
        String content=emojiEditText.getText().toString();
        if (TextUtils.isEmpty(content)){
            TipMsgHelper.ShowMsg(getActivity(),R.string.send_content_cannot_null);
            return;
        }
        createMessageToInteraction(content);
        emojiEditText.setText("");
        UIUtils.hideSoftKeyboard1(getActivity(),emojiEditText);
    }
    public void setCurrentPageIndex(){
        if (pageHelper!=null){
            pageHelper.setFetchingPageIndex(0);
        }
    }
}
