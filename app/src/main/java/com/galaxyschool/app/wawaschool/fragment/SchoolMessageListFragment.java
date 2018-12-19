package com.galaxyschool.app.wawaschool.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.Note.MediaPaperActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.NewResourceDeleteHelper;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.NewResourceAdapterViewHelper;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.CourseInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoListResult;
import com.galaxyschool.app.wawaschool.pojo.weike.NoteOpenParams;
import com.lqwawa.libs.mediapaper.MediaPaper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SchoolMessageListFragment extends SchoolResourceListBaseFragment {

    public static final String TAG = SchoolMessageListFragment.class.getSimpleName();
    private String [] onlineMessageType;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_school_resource_list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews() {
        onlineMessageType = getResources().getStringArray(R.array.str_online_school_message_type);
        if (isOnlineSchoolMessage){
            updateTitleView(getString(R.string.str_online_school_message), resourceCountStr);
        } else {
            updateTitleView(getString(R.string.school_message), resourceCountStr);
        }
        GridView listView = (GridView) findViewById(R.id.resource_list_view);
        if (listView != null) {
            AdapterViewHelper adapterViewHelper = new NewResourceAdapterViewHelper(
                    getActivity(), listView) {
                @Override
                public void loadData() {
                    loadResourceList();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    NewResourceInfo data = (NewResourceInfo) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    TextView textView = (TextView) view.findViewById(R.id.resource_type);
                    if (textView != null) {
                        String messageType = null;
                        if (isOnlineSchoolMessage){
                            if (data.getType() == NewResourceInfo.TYPE_SCHOOL_CAMPUS_COMMUNITY){
                                messageType = onlineMessageType[0];
                            } else if (data.getType() == NewResourceInfo.TYPE_SCHOOL_RESOURCE_SHARING){
                                messageType = onlineMessageType[1];
                            } else if (data.getType() == NewResourceInfo.TYPE_SCHOOL_RESEARCH_AND_DISCUSSION){
                                messageType = onlineMessageType[2];
                            } else if (data.getType() == NewResourceInfo.TYPE_SCHOOL_LEISURE_TIME){
                                messageType = onlineMessageType[3];
                            }
                        } else {
                            messageType = NewResourceInfo.getSchoolResourceTypeString(getContext(), data.getType())
                                    + NewResourceInfo.getResourceStateString(getContext(), data.getState());
                        }
                        textView.setText(messageType);
                        textView.setVisibility(View.VISIBLE);
                    }
                    //红点未读提醒
                    ImageView imageView = (ImageView) view.findViewById(R.id.resource_indicator);
                    if (imageView != null) {
                        //校园巡查要隐藏小红点
                        if (isCampusPatrolTag){
                            imageView.setVisibility(View.GONE);
                        }
                    }
                    imageView = (ImageView) view.findViewById(R.id.resource_delete);
                    NewResourceDeleteHelper helper = new NewResourceDeleteHelper(
                            getActivity(),
                            getCurrAdapterViewHelper(),
                            NewResourceDeleteHelper
                            .SCHOOL_MESSAGE_COURSE,
                            data,
                            imageView,
                            NewResourceInfo.TYPE_CLASS_SCHOOL_MOVEMENT);
                    if (isOnlineSchoolMessage){
                        boolean hasDeletePermission = false;
                        if (isTeacher || TextUtils.equals(getMemeberId(),data.getAuthorId())){
                            hasDeletePermission = true;
                        }
                        helper.initImageViewEvent(getMemeberId(), getString(R.string.str_delete_online_school_message),hasDeletePermission);
                    } else {
                        helper.initImageViewEvent(getMemeberId(), getString(R.string.delete_school_message));
                    }
                    //校园巡查需要隐藏删除按钮
                    if (isCampusPatrolTag){
                        imageView.setVisibility(View.GONE);
                    }

                    //优先选择更新的时间
                    TextView  time = (TextView) view.findViewById(R.id.resource_time);
                    if (time != null) {
                        if (TextUtils.isEmpty(data.getUpdatedTime())){
                            time.setText(DateUtils.getStringToString(data.getCreatedTime(),DateUtils.DATE_PATTERN_yyyy_MM_dd_HH_MM));
                        }else {
                            time.setText(DateUtils.getStringToString(data.getUpdatedTime(),DateUtils.DATE_PATTERN_yyyy_MM_dd_HH_MM));
                        }
                    }
                    TextView commentTextV = (TextView) view.findViewById(R.id.resource_comment_count);
                    TextView praiseTextV = (TextView) view.findViewById(R.id.resource_applaud_count);
                    if (data.getType() == NewResourceInfo.TYPE_SCHOOL_NEWS){
                        commentTextV.setVisibility(View.INVISIBLE);
                        praiseTextV.setVisibility(View.INVISIBLE);
                    } else {
                        commentTextV.setVisibility(View.VISIBLE);
                        praiseTextV.setVisibility(View.VISIBLE);
                    }
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        return;
                    }
                    NewResourceInfo data = (NewResourceInfo) holder.data;
                    if (data != null) {
                        //记录当前点击的条目Id
                        itemId = data.getId();
                        if (data.getType() == NewResourceInfo.TYPE_SCHOOL_SIGN_ACTIVITY
                                || data.getType() == NewResourceInfo.TYPE_SCHOOL_VOTE_ACTIVITY) {
                            TipsHelper.showToast(getContext(), R.string.resource_type_not_supported);
                            return;
                        }
                        if (data.getType() == NewResourceInfo.TYPE_SCHOOL_ACTIVITY
                                || data.getType() == NewResourceInfo.TYPE_SCHOOL_CAMPUS_COMMUNITY
                                || data.getType() == NewResourceInfo.TYPE_SCHOOL_LEISURE_TIME
                                || data.getType() == NewResourceInfo.TYPE_SCHOOL_RESEARCH_AND_DISCUSSION
                                || data.getType() == NewResourceInfo.TYPE_SCHOOL_RESOURCE_SHARING) {
                            //用于控制note详情页面编辑按钮的显示和隐藏
                            CourseInfo courseInfo = data.getCourseInfo();
                            if (courseInfo != null){
                                courseInfo.setCampusPatrolTag(isCampusPatrolTag);
                                courseInfo.setIsOnlineSchool(isOnlineSchoolMessage);
                                courseInfo.setIsTeacher(isTeacher);
                            }
                            ActivityUtils.openOnlineNote(getActivity(), courseInfo, false, false);
                        } else if (data.getType() == NewResourceInfo.TYPE_SCHOOL_NEWS) {
                            StringBuilder builder = new StringBuilder();
                            builder.append(ServerUrl.WEB_VIEW_NEW_URL);
                            builder.append("?Id=");
                            builder.append(data.getId());
                            builder.append("&MemberId=");
                            builder.append(getUserInfo().getMemberId());
                            builder.append("&SchoolId=");
                            builder.append(schoolId);
                            builder.append("&Type=1");
                            ActivityUtils.openNews(getActivity(), builder.toString(),
                                    data.getTitle());
                            //更新阅读数
                            updateReaderNumber(data.getId());
                        }
                    }
                }
            };
            setCurrAdapterViewHelper(listView, adapterViewHelper);
        }
    }

    @Override
    protected void loadResourceList(String keyword) {
        keyword = keyword.trim();
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
        params.put("SchoolId", schoolId);
        params.put("KeyWord", keyword);
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        DefaultPullToRefreshDataListener listener =
                new DefaultPullToRefreshDataListener<NewResourceInfoListResult>(
                        NewResourceInfoListResult.class) {
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
                        updateResourceListView(getResult());
                    }
                };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_SCHOOL_MESSAGE_LIST_URL, params, listener);
    }

    @Override
    protected void createNewResource() {
        long dateTime = System.currentTimeMillis();
        File noteFile = new File(Utils.NOTE_FOLDER, String.valueOf(dateTime));
        String dateTimeStr = DateUtils.transferLongToDate("yyyy-MM-dd HH:mm:ss", dateTime);
        NoteOpenParams params = new NoteOpenParams(noteFile.getPath(), dateTimeStr,
                MediaPaperActivity.OPEN_TYPE_EDIT, MediaPaper.PAPER_TYPE_TIEBA, null,
                MediaPaperActivity.SourceType.SCHOOL_MESSAGE, false);
        params.schoolId = schoolId;
        params.isOnlineSchool = isOnlineSchoolMessage;
        params.isTeacher = isTeacher;
        ActivityUtils.openLocalNote(getActivity(), params,
                CampusPatrolPickerFragment.CREATE_NEW_RESOURCE_REQUEST_CODE);
    }

}
