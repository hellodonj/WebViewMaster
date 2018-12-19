package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.alibaba.fastjson.JSONObject;
import com.galaxyschool.app.wawaschool.CommonFragmentActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.Emcee;
import com.galaxyschool.app.wawaschool.pojo.EmceeList;
import com.galaxyschool.app.wawaschool.pojo.OnlineNumber;
import com.galaxyschool.app.wawaschool.pojo.PublishClass;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PublishObjectFragment extends ContactsListFragment {
    private List<PublishClass> publishClasses;
    private Emcee onlineRes;
    private boolean isBelong;
    private boolean isReporter;
    private String classId;
    private int choosePosition = 0;
    private List<EmceeList> reporterBelongClass = new ArrayList<>();
    private boolean isHistoryClass;

    public PublishObjectFragment() {

    }

    public void setOnlineRes(Emcee onlineRes){
        this.onlineRes = onlineRes;
        this.publishClasses = onlineRes.getPublishClassList();
        sortData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.publish_object_fragment, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadIntentData();
        initViews();
    }

    private void loadIntentData(){
        Bundle bundle = getArguments();
        if (bundle != null){
            classId = bundle.getString(AirClassroomFragment.Constants.EXTRA_CONTACTS_CLASS_ID);
            isHistoryClass = bundle.getBoolean(ActivityUtils.EXTRA_IS_HISTORY_CLASS,false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initViews() {
        //获取是不是主持人或者记者
        isReporter = isReporter();
        GridView gridView = (GridView) findViewById(R.id.resource_list_view);
        if (gridView != null) {
            AdapterViewHelper adapterViewHelper = new AdapterViewHelper(getActivity(), gridView, R
                    .layout.item_school_class_name) {
                @Override
                public void loadData() {
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    if (view != null) {
                        final PublishClass data = (PublishClass) getData().get(position);
                        if (data != null) {
                            TextView textView = (TextView) view.findViewById(R.id.tv_school_class_name);
                            if (textView != null) {
                                textView.setText(data.getSchoolName() + data.getClassName());
                                if (isReporter) {
                                    //如果小记者在当前班就显示
                                    changePublishClassReporterIcon(textView, data);
                                }
                            }
                            //显示右箭头
                            ImageView rightIcon = (ImageView) view.findViewById(R.id.iv_right_icon);
                            if (rightIcon != null) {
                                rightIcon.setVisibility(View.VISIBLE);
                            }
                            ViewHolder holder = (ViewHolder) view.getTag();
                            if (holder == null) {
                                holder = new ViewHolder();
                            }
                            holder.data = data;
                            view.setTag(holder);
                        }
                    }
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null || holder.data == null) return;
                    choosePosition = position;
                    if (onlineRes.getState() == 1) {
                        loadOnlineNumberListData((PublishClass) holder.data);
                    } else {
                        enterShowOnlineNumberActivity((PublishClass) holder.data, null);
                    }

                }
            };
            setCurrAdapterViewHelper(gridView, adapterViewHelper);
        }
        getCurrAdapterViewHelper().setData(publishClasses);
    }

    private void changePublishClassReporterIcon(TextView reporterIcon, PublishClass data) {
        if (reporterBelongClass != null && reporterBelongClass.size() > 0) {
            boolean flag = false;
            //如果是创建者直接返回true
            if (TextUtils.equals(getMemeberId(), onlineRes.getAcCreateId())) {
                flag = true;
            } else {
                //比较小编的classId
                for (int i = 0, len = reporterBelongClass.size(); i < len; i++) {
                    EmceeList member = reporterBelongClass.get(i);
                    if (TextUtils.equals(member.getClassIds(), data.getClassId())) {
                        flag = true;
                        break;
                    }
                }
            }
            //按类型显示小图标
            Drawable drawable = getActivity().getResources().getDrawable(R.drawable.publish_class_reporter_icon);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            if (flag) {
                reporterIcon.setCompoundDrawablePadding(5);
                reporterIcon.setCompoundDrawables(null, null, drawable, null);
            } else {
                reporterIcon.setCompoundDrawables(null, null, null, null);
            }
        }
    }


    private void sortData() {
        if (publishClasses == null) {
            return;
        }
        PublishClass publishClass;
        for (int i = 0; i < publishClasses.size(); i++) {
            PublishClass pubClass = publishClasses.get(i);
            if (TextUtils.equals(pubClass.getClassId(),onlineRes.getClassId())) {
                publishClass = pubClass;
                publishClass.setIsCanDelete(true);
                publishClasses.remove(pubClass);
                publishClasses.add(0, publishClass);
                break;
            }
        }
    }

    private boolean isReporter() {
        boolean flag = false;
        List<EmceeList> emceeList = onlineRes.getEmceeList();
        for (int i = 0; i < emceeList.size(); i++) {
            EmceeList emceeMember = emceeList.get(i);
            String classIds = emceeMember.getClassIds();
            String schoolIds = emceeMember.getSchoolIds();
            if (TextUtils.equals(getMemeberId(), emceeMember.getMemberId())
                    && !TextUtils.isEmpty(classIds)) {
                EmceeList tempData = null;
                if (classIds.contains(",")) {
                    String[] splitClassArray = classIds.split(",");
                    String[] splitSchoolArray = schoolIds.split(",");
                    for (int j = 0; j < splitClassArray.length; j++) {
                        tempData = new EmceeList();
                        tempData.setMemberId(emceeMember.getMemberId());
                        tempData.setSchoolIds(splitSchoolArray[j]);
                        tempData.setClassIds(splitClassArray[j]);
                        reporterBelongClass.add(tempData);
                    }
                } else {
                    tempData = new EmceeList();
                    tempData.setMemberId(emceeMember.getMemberId());
                    tempData.setSchoolIds(emceeMember.getSchoolIds());
                    tempData.setClassIds(emceeMember.getClassIds());
                    reporterBelongClass.add(tempData);
                }
                flag = true;
                break;
            }
        }
        return flag;
    }

    private void loadOnlineNumberListData(final PublishClass data) {
        Map<String, Object> params = new HashMap<>();
        params.put("ExtId", onlineRes.getId());
        params.put("ClassId", classId);
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestDataResultListener<DataModelResult>(
                        getActivity(), DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        super.onSuccess(jsonString);
                        DataModelResult result = getResult();
                        if (result != null && result.isSuccess()) {
                            //成功
                            List<OnlineNumber> onlineNumbers = JSONObject.parseArray(result
                                    .getModel().getData().toString(), OnlineNumber.class);

                            if (onlineNumbers != null && onlineNumbers.size() > 0) {
                                Fragment fragment = getParentFragment();
                                if (fragment != null && fragment instanceof AirClassroomDetailFragment) {
                                    AirClassroomDetailFragment detailFragment = (AirClassroomDetailFragment) fragment;
                                    onlineRes.setOnlineNum(onlineNumbers.size());
                                    detailFragment.refreshShowOnlineNum();
                                }
                            }
                            enterShowOnlineNumberActivity(data, onlineNumbers);
                        }
                    }
                };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.GET_ONLINE_NUMBER_LIST_BASE_URL, params, listener);
    }

    private void enterShowOnlineNumberActivity(final PublishClass data, List<OnlineNumber> onlineNumbers) {
        Intent intent = new Intent(getActivity(), CommonFragmentActivity.class);
        Bundle bundle = new Bundle();
        if (onlineNumbers != null) {
            bundle.putSerializable(ClassMemberListFragment.Constant.ONLINE_NUMBER_LIST, (Serializable)
                    onlineNumbers);
        }
        //传值删除按钮的可见性
        if (TextUtils.equals(getMemeberId(),onlineRes.getAcCreateId())) {
            bundle.putSerializable(ClassMemberListFragment.Constant.SHOW_DELETE_BTN, true);
        }
        bundle.putSerializable(ClassMemberListFragment.Constant.CURRENT_CLASS_INFO, data);
        bundle.putSerializable(CommonFragmentActivity.EXTRA_CLASS_OBJECT, ClassMemberListFragment.class);
        bundle.putBoolean(ActivityUtils.EXTRA_IS_HISTORY_CLASS,isHistoryClass);
        intent.putExtras(bundle);
        startActivityForResult(intent, ActivityUtils.REQUEST_CODE_RETURN_REFRESH);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivityUtils.REQUEST_CODE_RETURN_REFRESH && data != null) {
            PublishClass returnData = (PublishClass) data.getSerializableExtra(ClassMemberListFragment.Constant
                    .CURRENT_CLASS_INFO);
            if (returnData != null) {
                sendBroadcastToUpdateLiveList();
                if (TextUtils.equals(classId, returnData.getClassId())) {
                    getActivity().finish();
                } else {
                    publishClasses.remove(choosePosition);
                    getCurrAdapterViewHelper().update();
                }
            }
        }
    }

    /**
     * 如果来自mooc 发广播通知刷新列表界面
     */
    private void sendBroadcastToUpdateLiveList() {
        Intent broadIntent = new Intent();
        broadIntent.setAction("LIVE_STATUS_CHANGED");
        getActivity().sendBroadcast(broadIntent);
    }
}
