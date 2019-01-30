package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.ClassResourceListActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.pojo.ReviewInfo;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.SubscribeClassInfo;
import com.galaxyschool.app.wawaschool.pojo.TeacherReviewInfoResult;
import com.galaxyschool.app.wawaschool.pojo.TeacherReviewStatisInfo;
import com.galaxyschool.app.wawaschool.views.DatePopupView;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作业完成状态列表
 */

public class CommentStatisticFragment extends ContactsListFragment {

    public static final String TAG = CommentStatisticFragment.class.getSimpleName();
    private GridView reviewGridView;
    private GridView unReviewGridView;
    private ImageView reviewArrowImageV;
    private ImageView unReviewArrowImageV;
    private TextView reviewAleadyTextV;
    private TextView unReviewTextV;
    private TextView startTimeTextV;
    private TextView endTimeTextV;
    private boolean isUnReviewLayoutExpand = true;
    private boolean isReviewLayoutExpand = true;
    private String startTime;
    private String endTime;
    private SubscribeClassInfo classInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comment_statistic, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadIntentData();
        initViews();
        initTimeData();
        initReviewGridViewHelper();
        initUnReviewGridViewHelper();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void loadIntentData() {
        Bundle args = getArguments();
        if (args != null) {
            classInfo = (SubscribeClassInfo) args.getSerializable(SubscribeClassInfo.class.getSimpleName());
        }
    }

    private void initViews() {
        //标题(点评统计)
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setText(getString(R.string.str_comment_statistic));
        }
        //开始时间
        startTimeTextV = (TextView) findViewById(R.id.study_task_start_date_text);
        startTimeTextV.setOnClickListener(v -> changeScreenTime(true));
        //结束时间
        endTimeTextV = (TextView) findViewById(R.id.study_task_end_date_text);
        endTimeTextV.setOnClickListener(v -> changeScreenTime(false));

        //已点评
        reviewAleadyTextV = (TextView) findViewById(R.id.tv_already_comment_num);
        reviewGridView = (GridView) findViewById(R.id.gv_review);
        reviewArrowImageV = (ImageView) findViewById(R.id.iv_already_item_arrow);
        findViewById(R.id.ll_review).setOnClickListener(this);
        //未点评
        unReviewTextV = (TextView) findViewById(R.id.tv_unalready_item_title);
        unReviewGridView = (GridView) findViewById(R.id.gv_unalready_grid_view);
        unReviewArrowImageV = (ImageView) findViewById(R.id.iv_unalready_item_arrow);
        findViewById(R.id.ll_unreview).setOnClickListener(this);
    }

    private void initTimeData() {
        endTime = DateUtils.getDateStr(new Date(), DateUtils.DATE_PATTERN_yyyy_MM_dd);
        startTime = DateUtils.getWeekDayString(0,endTime,DateUtils.DATE_PATTERN_yyyy_MM_dd);
        startTimeTextV.setText(startTime);
        endTimeTextV.setText(endTime);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_review) {
            //已完成
            isReviewLayoutExpand = !isReviewLayoutExpand;
            updateArrowLayout(isReviewLayoutExpand, reviewArrowImageV,reviewGridView);
        } else if (v.getId() == R.id.ll_unreview) {
            //未完成
            isUnReviewLayoutExpand = !isUnReviewLayoutExpand;
            updateArrowLayout(isUnReviewLayoutExpand, unReviewArrowImageV,unReviewGridView);
        } else {
            super.onClick(v);
        }
    }

    private void changeScreenTime(boolean isStartTime) {
        if (isStartTime) {
            if (TextUtils.isEmpty(startTime)) {
                return;
            }
        } else {
            if (TextUtils.isEmpty(endTime)) {
                return;
            }
        }
        UIUtils.hideSoftKeyboard(getActivity());
        DatePopupView stateDatePopView = new DatePopupView(getActivity(), isStartTime ?
                startTime : endTime, dateStr -> {
            if (isStartTime) {
                if (!TextUtils.equals(startTime, dateStr)) {
                    startTime = dateStr;
                    startTimeTextV.setText(startTime);
                }
            } else {
                if (!TextUtils.equals(startTime, dateStr)) {
                    endTime = dateStr;
                    endTimeTextV.setText(endTime);
                }
            }
            loadData();
        });
        stateDatePopView.showAtLocation(getView(), Gravity.BOTTOM, 0, 0);
    }

    private void loadData() {
        Map<String, Object> params = new HashMap<>();
        //TaskId，必填
        if (classInfo != null){
            params.put("ClassId", classInfo.getClassId());
        }
        params.put("TaskCreateId", getMemeberId());
        params.put("StartTimeBegin", startTime);
        params.put("StartTimeEnd", endTime);
        DefaultDataListener listener = new DefaultDataListener<TeacherReviewInfoResult>(TeacherReviewInfoResult.class) {
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
                TeacherReviewInfoResult result = getResult();
                if (result != null) {
                    updateView(result.getModel().getData());
                }
            }
        };
        postRequest(ServerUrl.GET_TEACHER_REVIEW_STATIS_BASE_URL, params, listener);
    }

    private void updateView(TeacherReviewStatisInfo dataInfo){
        if (dataInfo == null){
            return;
        }
        List<ReviewInfo> reviewInfoList = dataInfo.getHasReviewList();
        List<ReviewInfo> unReviewInfoList = dataInfo.getUnReviewList();
        int reviewNum = 0;
        if (reviewInfoList != null && reviewInfoList.size() > 0){
            reviewNum = reviewInfoList.size();
        }
        getAdapterViewHelper(String.valueOf(reviewGridView.getId())).setData(reviewInfoList);
        reviewAleadyTextV.setText(getString(R.string.str_already_comment,reviewNum));

        int unReviewNum = 0;
        if (unReviewInfoList != null && unReviewInfoList.size() > 0){
            unReviewNum = unReviewInfoList.size();
        }
        getAdapterViewHelper(String.valueOf(unReviewGridView.getId())).setData(unReviewInfoList);
        unReviewTextV.setText(getString(R.string.str_unAlready_comment,unReviewNum));
    }

    private void initReviewGridViewHelper(){
        AdapterViewHelper reViewGridViewHelper = new AdapterViewHelper(getActivity(),
                reviewGridView, R.layout.item_review_detail) {
            @Override
            public void loadData() {

            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                final ReviewInfo data = (ReviewInfo) getDataAdapter().getItem(position);
                if (data == null) {
                    return view;
                }
                //头像
                ImageView imageView = (ImageView) view.findViewById(R.id.icon_head);
                if (imageView != null) {
                    getThumbnailManager().displayThumbnailWithDefault(
                            AppSettings.getFileUrl(data.getHeadPicUrl()), imageView,
                            R.drawable.default_user_icon);
                }
                //标题
                TextView textView = (TextView) view.findViewById(R.id.title);
                if (textView != null) {
                    textView.setText(data.getStudentName());
                }

                //点评的数量
                TextView reviewNumView = (TextView) view.findViewById(R.id.tv_number);
                if (reviewNumView != null){
                    if (data.getTeacherReviewCount() > 0){
                        reviewNumView.setVisibility(View.VISIBLE);
                        reviewNumView.setText(String.valueOf(data.getTeacherReviewCount()));
                    } else {
                        reviewNumView.setVisibility(View.GONE);
                    }
                }

                ViewHolder holder = (ViewHolder) view.getTag();
                if (holder == null) {
                    holder = new ViewHolder();
                }
                holder.data = data;
                view.setTag(holder);
                return view;
            }

            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                ViewHolder holder = (ViewHolder) view.getTag();
                if (holder == null || holder.data == null) {
                    return;
                }
                openReviewStudentTaskDetail((ReviewInfo) holder.data);
            }
        };
        addAdapterViewHelper(String.valueOf(reviewGridView.getId()), reViewGridViewHelper);
    }

    private void initUnReviewGridViewHelper(){
        AdapterViewHelper unReviewGridViewHelper = new AdapterViewHelper(getActivity(),
                unReviewGridView, R.layout.item_review_detail) {
            @Override
            public void loadData() {
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                final ReviewInfo data = (ReviewInfo) getDataAdapter().getItem(position);
                if (data == null) {
                    return view;
                }
                //头像
                ImageView imageView = (ImageView) view.findViewById(R.id.icon_head);
                if (imageView != null) {
                    getThumbnailManager().displayThumbnailWithDefault(
                            AppSettings.getFileUrl(data.getHeadPicUrl()), imageView,
                            R.drawable.default_user_icon);
                }
                //标题
                TextView textView = (TextView) view.findViewById(R.id.title);
                if (textView != null) {
                    textView.setText(data.getStudentName());
                }

                ViewHolder holder = (ViewHolder) view.getTag();
                if (holder == null) {
                    holder = new ViewHolder();
                }
                holder.data = data;
                view.setTag(holder);
                return view;
            }

            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                ViewHolder holder = (ViewHolder) view.getTag();
                if (holder == null || holder.data == null) {
                    return;
                }
                openReviewStudentTaskDetail((ReviewInfo) holder.data);
            }
        };
        addAdapterViewHelper(String.valueOf(unReviewGridView.getId()), unReviewGridViewHelper);
    }

    private void openReviewStudentTaskDetail(ReviewInfo reviewInfo){
        if (classInfo == null){
            return;
        }
        Bundle args = new Bundle();
        args.putString(ClassResourceListActivity.EXTRA_CLASS_ID, classInfo.getClassId());
        args.putString(ClassResourceListActivity.EXTRA_SCHOOL_ID, classInfo.getSchoolId());
        args.putInt(ClassResourceListActivity.EXTRA_CHANNEL_TYPE, ClassResourceListActivity.CHANNEL_TYPE_HOMEWORK);
        args.putBoolean(ClassResourceListActivity.EXTRA_IS_TEACHER, classInfo.isTeacherByRoles());
//        args.putInt(ClassResourceListActivity.EXTRA_ROLE_TYPE, classInfo.getRoleType());
        args.putInt(ClassResourceListActivity.EXTRA_ROLE_TYPE, RoleType.ROLE_TYPE_TEACHER);
        args.putBoolean(ClassResourceListActivity.EXTRA_IS_HEAD_MASTER, classInfo.isHeadMaster());
        args.putBoolean(ClassResourceListActivity.EXTRA_IS_HISTORY, classInfo.isHistory());
        args.putSerializable(ReviewInfo.class.getSimpleName(),reviewInfo);
        Intent intent = new Intent(getActivity(), ClassResourceListActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }

    private void updateArrowLayout(boolean isReviewLayoutExpand,
                                   ImageView reviewImageV,
                                   GridView finishStatusGridView) {
        reviewImageV.setImageResource(isReviewLayoutExpand ? R.drawable.list_exp_up : R.drawable.list_exp_down);
        finishStatusGridView.setVisibility(isReviewLayoutExpand ? View.VISIBLE : View.GONE);
    }
}
