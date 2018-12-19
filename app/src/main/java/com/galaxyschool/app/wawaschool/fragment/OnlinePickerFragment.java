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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.AirClassroomActivity;
import com.galaxyschool.app.wawaschool.ContactsPickerActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.adapter.GridVewSelectorAdapter;
import com.galaxyschool.app.wawaschool.adapter.GridViewContactsSelectorAdapter;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.HomeworkChildListResult;
import com.galaxyschool.app.wawaschool.pojo.PickerMode;
import com.galaxyschool.app.wawaschool.pojo.StudentMemberInfo;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskTypeInfo;
import com.galaxyschool.app.wawaschool.views.DatePopupView;
import com.lqwawa.intleducation.module.learn.tool.LiveFilter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 筛选直播数据
 */
public class OnlinePickerFragment extends ContactsPickerFragment {

    public static final String TAG = OnlinePickerFragment.class.getSimpleName();
    private View selectAllView;
    private TextView assign_start_date;
    private TextView assign_end_date;
    private TextView finish_start_date;
    private TextView finish_end_date;
    private View rootView;
    //GridView
    private GridView gridView;
    //全选布局
    private View contacts_select_all_layout;
    //全选文字
    private TextView contacts_select_all_title;
    //标题
    private TextView contacts_select_item_title;
    //全选图片
    private ImageView contacts_select_all_icon;
    //只是标识是否全选了
    private View selectAllImageView;
    //老师选择布局
    private View layout_picker_grid_view_study_type;
    //全选布局
    private View contacts_select_all_layout_study_type;
    //全选文字
    private TextView contacts_select_all_title_study_type;
    //标题
    private TextView contacts_select_item_title_study_type;
    //全选图片
    private ImageView contacts_select_all_icon_study_type;
    //只是标识是否全选了
    private View selectAllImageView_study_type;
    private int gridViewPickerMode;
    private int roleType = -1;
    private String classId;
    //作业类型ListView
    private ListView gridView_study_type;
    //直播选择的方式（多选或者单选）
    int onlineTypePickerMode = -1;
    private List<StudyTaskTypeInfo> studyTaskTypeInfoList = new ArrayList<>();
    private GridViewContactsSelectorAdapter adapter_study_type;
    private List<StudentMemberInfo> teacherList = new ArrayList<>();
    private GridVewSelectorAdapter adapter;
    //筛选的选择模式
    private int pickerMode = PickerModel.MULTIPLE_MODEL;
    //直播的来源
    private int fromType = FromType.FROM_AIRCLASS;

    public interface FromType{
        //空中课堂的直播
        int FROM_AIRCLASS = 0;
        //慕课的直播
        int FROM_MOOC_LIVE = 1;
    }
    public interface PickerModel{
        //单选
        int SINGLE_MODEL = 0;
        //多选
        int MULTIPLE_MODEL = 1;
    }
    public interface OnlineType{
        //视频直播
        int VIDEO_TYPE = 0;
        //板书直播
        int E_BLACKBOARD_TYPE = 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_screening_homework, null);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            loadViews();
        }
    }

    private void init() {

        this.gridViewPickerMode = PickerMode.PICKER_MODE_MULTIPLE;
        Bundle bundle = getArguments();
        if (bundle != null) {
            roleType = bundle.getInt(AirClassroomActivity.EXTRA_ROLE_TYPE);
            classId = bundle.getString(AirClassroomActivity.EXTRA_CONTACTS_CLASS_ID);
            fromType = bundle.getInt("fromType");
            pickerMode = getArguments().getInt("picker_model");
        }
        initGridView();
        initStudyTaskTypeListView();
        initViews();
    }

    private void initStudyTaskTypeListView() {
        onlineTypePickerMode=PickerMode.PICKER_MODE_MULTIPLE;
        //老师全选布局,仅对老师隐藏。
        layout_picker_grid_view_study_type = findViewById(R.id.layout_picker_grid_view_study_type);
        if (layout_picker_grid_view_study_type != null){
            //隐藏作业完成状态的布局
            layout_picker_grid_view_study_type.setVisibility(View.GONE);
        }
        //配置全选布局
        contacts_select_all_layout_study_type = findViewById(R.id.layout_select_all_layout_study_type);

        if (onlineTypePickerMode == PickerMode.PICKER_MODE_MULTIPLE) {
            contacts_select_all_layout_study_type.setVisibility(View.VISIBLE);
        } else if (onlineTypePickerMode == PickerMode.PICKER_MODE_SINGLE) {
            contacts_select_all_layout_study_type.setVisibility(View.GONE);
        }

        contacts_select_all_layout_study_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //全选/取消全选
                selectStudyTaskTypeAdapterViewAllItems(!selectAllImageView_study_type.isSelected());
            }
        });

        //作业任务完成的状态（忽略<已完成/未完成>）
        contacts_select_item_title_study_type = (TextView) findViewById(R.id
                .layout_select_all_layout_item_title_study_type);
        contacts_select_item_title_study_type.setText(R.string.homework_state_type);

        contacts_select_all_title_study_type = (TextView) findViewById(R.id
                .layout_select_all_layout_select_all_title_study_type);
        contacts_select_all_title_study_type.setText(R.string.select_all);

        contacts_select_all_icon_study_type = (ImageView) findViewById(R.id
                .layout_select_all_layout_select_all_icon_study_type);
        contacts_select_all_icon_study_type.setVisibility(View.GONE);
        //配置全选显示的View
        selectAllImageView_study_type = contacts_select_all_icon_study_type;

        //加载ListView
        gridView_study_type = (ListView) findViewById(R.id.homework_list_view_study_type);
        //设置适配器
        adapter_study_type = new GridViewContactsSelectorAdapter(getActivity(),
                studyTaskTypeInfoList, gridViewPickerMode);
        gridView_study_type.setAdapter(adapter_study_type);
        //设置item点击事件
        gridView_study_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                //多选
                if (gridViewPickerMode == PickerMode.PICKER_MODE_MULTIPLE) {
                    //选中/非选中
                    boolean isItemSelected = adapter_study_type.getSelectorHelper()
                            .isItemSelected(position);
                    //选中/非选中条目,注意，多选的时候点击是反选。
                    adapter_study_type.getSelectorHelper().selectItem(position, !isItemSelected);
                    //更新一下全选布局
                    boolean isAllItemsSelected = adapter_study_type.getSelectorHelper()
                            .isAllItemsSelected();
                    selectAllImageView_study_type.setSelected(isAllItemsSelected);
                    //更新一下全选/取消全选的文字
                    updateStudyTaskTypeSelectAllText(isAllItemsSelected);
                }
                //单选
                else if (gridViewPickerMode == PickerMode.PICKER_MODE_SINGLE) {
                    //根据位置刷新单选布局
                    adapter_study_type.updateSinglePickerLayout(position);
                }
                //更新一下底部取消和确定按钮状态
                notifyPickerBar();
                //更新数据,刷新布局。
                adapter_study_type.notifyDataSetChanged();
            }
        });

    }

    private void initGridView() {
        //隐藏当前主持人布局
        findViewById(R.id.layout_picker_grid_view).setVisibility(View.GONE);
        //配置全选布局
        contacts_select_all_layout = findViewById(R.id.layout_select_all_layout);

        if (gridViewPickerMode == PickerMode.PICKER_MODE_MULTIPLE) {
            contacts_select_all_layout.setVisibility(View.VISIBLE);
        } else if (gridViewPickerMode == PickerMode.PICKER_MODE_SINGLE) {
            contacts_select_all_layout.setVisibility(View.GONE);
        }

        contacts_select_all_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //全选/取消全选
                selectAdapterViewAllItems(!selectAllImageView.isSelected());
            }
        });

        contacts_select_item_title = (TextView) findViewById(R.id.layout_select_all_layout_item_title);
        contacts_select_item_title.setText(R.string.online_host);

        contacts_select_all_title = (TextView) findViewById(R.id.layout_select_all_layout_select_all_title);
        contacts_select_all_title.setText(R.string.select_all);

        contacts_select_all_icon = (ImageView) findViewById(R.id.layout_select_all_layout_select_all_icon);
        contacts_select_all_icon.setVisibility(View.GONE);
        //配置全选显示的View
        selectAllImageView = contacts_select_all_icon;

        //加载GridView
        gridView = (GridView) findViewById(R.id.homework_grid_view);
        //设置适配器
        adapter = new GridVewSelectorAdapter(getActivity(), teacherList, gridViewPickerMode);
        gridView.setAdapter(adapter);
        //设置item点击事件
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                //多选
                if (gridViewPickerMode == PickerMode.PICKER_MODE_MULTIPLE) {
                    //选中/非选中
                    boolean isItemSelected = adapter.getSelectorHelper().isItemSelected(position);
                    //选中/非选中条目,注意，多选的时候点击是反选。
                    adapter.getSelectorHelper().selectItem(position, !isItemSelected);
                    //更新一下全选布局
                    boolean isAllItemsSelected = adapter.getSelectorHelper().isAllItemsSelected();
                    selectAllImageView.setSelected(isAllItemsSelected);
                    //更新一下全选/取消全选的文字
                    updateSelectAllText(isAllItemsSelected);
                }
                //单选
                else if (gridViewPickerMode == PickerMode.PICKER_MODE_SINGLE) {
                    //根据位置刷新单选布局
                    adapter.updateSinglePickerLayout(position);
                }
                //更新一下底部取消和确定按钮状态
                notifyPickerBar();
                //更新数据,刷新布局。
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 更新全选/取消全选文字
     *
     * @param isAllItemsSelected
     */
    private void updateSelectAllText(boolean isAllItemsSelected) {
        if (isAllItemsSelected) {
            contacts_select_all_title.setText(R.string.cancel_to_select_all);
        } else {
            contacts_select_all_title.setText(R.string.select_all);
        }
    }

    /**
     * 更新全选/取消全选文字
     *
     * @param isAllItemsSelected
     */
    private void updateStudyTaskTypeSelectAllText(boolean isAllItemsSelected) {
        if (isAllItemsSelected) {
            contacts_select_all_title_study_type.setText(R.string.cancel_to_select_all);
        } else {
            contacts_select_all_title_study_type.setText(R.string.select_all);
        }
    }

    private void initViews() {
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setText(R.string.screening);
        }
        //直播时间
        textView = (TextView) findViewById(R.id.tv_show_time_title_type);
        if (textView != null){
            textView.setText(getString(R.string.str_online_time));
        }
        //开始时间
        textView = (TextView) findViewById(R.id.tv_start_time);
        if (textView != null){
            textView.setText(getString(R.string.str_start_time));
        }
        //结束时间
        textView = (TextView) findViewById(R.id.tv_end_time);
        if(textView != null){
            textView.setText(getString(R.string.str_finish_time));
        }
        //时间选择
        //今天
        String todayTime = DateUtils.getDateStr(new Date(),DateUtils.DATE_PATTERN_yyyy_MM_dd);
        String startTime = changTimeFormat(todayTime,true);
        String finishTime = changTimeFormat(todayTime,false);

        assign_start_date = (TextView) findViewById(R.id.assign_start_date);
        assign_start_date.setText(startTime);
        assign_start_date.setOnClickListener(this);

        assign_end_date = (TextView) findViewById(R.id.assign_end_date);
        assign_end_date.setText(finishTime);
        assign_end_date.setOnClickListener(this);

        finish_start_date = (TextView) findViewById(R.id.finish_start_date);
        finish_start_date.setText(startTime);
        finish_start_date.setOnClickListener(this);

        finish_end_date = (TextView) findViewById(R.id.finish_end_date);
        finish_end_date.setText(finishTime);
        finish_end_date.setOnClickListener(this);

        notifyPickerBar();

        //全选/取消全选布局
        View view = findViewById(R.id.contacts_select_all);
        if (this.pickerMode == ContactsPickerActivity.PICKER_MODE_MULTIPLE) {
            view.setVisibility(View.VISIBLE);
            this.selectAllView = findViewById(R.id.contacts_select_all_icon);
            //默认非全选状态
            this.selectAllView.setSelected(false);

            //多选的时候，设置点击事件。
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectAllContacts(!selectAllView.isSelected());
                }
            });
        } else {
            view.setVisibility(View.VISIBLE);
        }
        //全选/取消全选布局的左侧标题
        textView = (TextView) findViewById(R.id.contacts_item_title);
        if (textView != null) {
            textView.setText(R.string.str_online_type_no_point);
        }

        //全选/取消全选的文字
        textView = (TextView) findViewById(R.id.contacts_select_all_title);
        if (textView != null) {
            if (this.pickerMode == ContactsPickerActivity.PICKER_MODE_MULTIPLE) {
                //多选的时候，显示，其他不显示。
                textView.setVisibility(View.VISIBLE);
            } else {
                textView.setVisibility(View.GONE);
            }
        }

        //全选/取消全选的图片
        ImageView imageView = (ImageView) findViewById(R.id.contacts_select_all_icon);
        if (imageView != null) {
            imageView.setVisibility(View.GONE);
        }

        //确定和取消按钮布局
        view = findViewById(R.id.contacts_picker_bar_layout);
        //取消按钮
        if (view != null) {
            textView = (TextView) view.findViewById(R.id.contacts_picker_clear);
            if (textView != null) {
                textView.setText(R.string.clear_screening_case);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectAdapterViewAllItems(false);
                        selectAllContacts(false);
                        selectStudyTaskTypeAdapterViewAllItems(false);
                        clearDateSelectLayout();
                    }
                });
            }
            //确定按钮
            textView = (TextView) view.findViewById(R.id.contacts_picker_confirm);
            if (textView != null) {
                textView.setText(R.string.ok);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        completePickContacts();
                    }
                });
            }
            initListViewHelper();
        }
    }

    private void initListViewHelper() {
        //ListView
        ListView listView = (ListView) findViewById(R.id.homework_list_view);
        if (listView != null) {
            AdapterViewHelper listViewHelper = new AdapterViewHelper(getActivity(),
                    listView, R.layout.homework_list_item_with_selector) {
                @Override
                public void loadData() {
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    //设置View的Padding是10dp
                    int padding = (int) (MyApplication.getDensity() * 10);
                    view.setPadding(padding, padding, padding, padding);
                    StudyTaskTypeInfo data =
                            (StudyTaskTypeInfo) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    MyViewHolder holder = new MyViewHolder();
                    holder.data = data;
                    holder.position = position;
                    //头像
                    ImageView imageView = (ImageView) view.findViewById(R.id.contacts_item_icon);
                    if (imageView != null) {
                        getThumbnailManager().displayUserIcon(
                                AppSettings.getFileUrl(data.getTypeImageUrl()), imageView);
                        imageView.setVisibility(View.GONE);
                    }

                    //item的标题
                    TextView textView = (TextView) view.findViewById(R.id.contacts_item_title);
                    if (textView != null) {
                        textView.setTextAppearance(getActivity(), R.style.txt_wawa_normal_black);
                        textView.setText(data.getTypeName());
                    }

                    //“全选”标题
                    textView = (TextView) view.findViewById(R.id.contacts_select_all_title);
                    if (textView != null) {
                        textView.setVisibility(View.GONE);
                    }

                    //选择的图片
                    imageView = (ImageView) view.findViewById(R.id.contacts_item_selector);
                    if (imageView != null) {
                        holder.selectorView = imageView;
                        imageView.setSelected(isItemSelected(position));
                    }
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    MyViewHolder holder = (MyViewHolder) getCurrViewHolder();
                    if (holder != null) {
                        if (pickerMode == ContactsPickerActivity.PICKER_MODE_SINGLE) {
                            selectItem(holder.position, false);
                            holder.selectorView.setSelected(false);
                            notifyPickerBar();
                        }
                    }

                    holder = (MyViewHolder) view.getTag();
                    if (holder == null) {
                        return;
                    }
                    if (pickerMode == ContactsPickerActivity.PICKER_MODE_SINGLE) {
                        selectItem(position, true);
                        setCurrViewHolder(holder);
                        holder.selectorView.setSelected(true);
                        notifyPickerBar();
                    } else if (pickerMode == ContactsPickerActivity.PICKER_MODE_MULTIPLE) {
                        boolean selected = !isItemSelected(position);
                        selectItem(position, selected);
                        holder.selectorView.setSelected(selected);
                        if (selectAllView != null) {
                            selectAllView.setSelected(isAllItemsSelected());
                        }
                        notifyPickerBar();
                    }
                    getDataAdapter().notifyDataSetChanged();
                }
            };
            setCurrAdapterViewHelper(listView, listViewHelper);
        }
    }

    private void clearDateSelectLayout() {
        assign_start_date.setText("");
        assign_end_date.setText("");
        finish_start_date.setText("");
        finish_end_date.setText("");
        notifyPickerBar();
    }

    /**
     * 判断日期选择布局是否为空，是否选择了时间。
     *
     * @return
     */
    private boolean isSelectedTimeLayoutEmpty() {
        boolean isEmtpy = false;
        int length_assign_start_date = assign_start_date.getText().toString().trim().length();
        int length_assign_end_date = assign_end_date.getText().toString().trim().length();
        int length_finish_start_date = finish_start_date.getText().toString().trim().length();
        int length_finish_end_date = finish_end_date.getText().toString().trim().length();
        if (length_assign_start_date == 0 && length_assign_end_date == 0 && length_finish_start_date == 0 && length_finish_end_date == 0) {
            //都为空才为空，否则非空。
            isEmtpy = true;
        } else {
            isEmtpy = false;
        }
        return isEmtpy;
    }

    private void loadViews() {
        if (getCurrAdapterViewHelper().hasData()) {
            getCurrAdapterViewHelper().update();
        } else {
            loadOnlineType();
        }
    }

    /**
     * 拉取学习任务类型信息
     */
    private void loadOnlineType() {

        List<StudyTaskTypeInfo> list = new ArrayList<>();
        //视频
        StudyTaskTypeInfo videoType = new StudyTaskTypeInfo();
        videoType.setType(OnlineType.VIDEO_TYPE + "");
        videoType.setTypeName(getString(R.string.live_type_video));
        list.add(videoType);
        //板书
        StudyTaskTypeInfo blackBoardType = new StudyTaskTypeInfo();
        blackBoardType.setType(OnlineType.E_BLACKBOARD_TYPE + "");
        blackBoardType.setTypeName(getString(R.string.live_type_blackboard));
        list.add(blackBoardType);
        updateLayout(list);
    }

    private void updateLayout(List<StudyTaskTypeInfo> list) {
        if (list == null && list.size() <= 0) {
            return;
        }
        getCurrAdapterViewHelper().setData(list);

        if (this.selectAllView != null && getCurrAdapterViewHelper().hasData()) {
            selectAllContacts(this.selectAllView.isSelected());
        } else {
            if (this.selectAllView != null) {
                this.selectAllView.setSelected(false);
            }
            notifyPickerBar();
        }
    }

    /**
     * 加载老师列表信息
     */
    private void loadTeacherInfo() {
        Map<String, Object> params = new HashMap();
        params.put("ClassId", classId);
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_TEACHERT_BY_CLASSID_URL, params,
                new DefaultDataListener<HomeworkChildListResult>(
                        HomeworkChildListResult.class) {
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
                        updateViews(getResult());
                    }
                });
    }

    private void updateViews(HomeworkChildListResult result) {
        List<StudentMemberInfo> list = result.getModel().getData();
        if (list == null && list.size() <= 0) {
            return;
        }
        teacherList.clear();
        teacherList.addAll(list);

        LinearLayout.LayoutParams lp= (LinearLayout.LayoutParams) gridView.getLayoutParams();
        gridView.measure(0,0);
        lp.height=gridView.getMeasuredHeight();
        gridView.setLayoutParams(lp);
        adapter.notifyDataSetChanged();
    }
    private void notifyPickerBar() {
        //全选/取消全选文字
        TextView textView = (TextView) findViewById(R.id.contacts_select_all_title);
        if (textView != null) {
            if (!isAllItemsSelected()) {
                textView.setText(getString(R.string.select_all));
            } else {
                textView.setText(getString(R.string.cancel_to_select_all));
            }
        }
        boolean gridViewHasSelectedItems = adapter.getSelectorHelper().hasSelectedItems();
        boolean studyTaskTypeGridViewHasSelectedItems=adapter_study_type.getSelectorHelper()
                .hasSelectedItems();
        boolean timeSelectedLayoutHasSelectedItems = isSelectedTimeLayoutEmpty();
        boolean listViewHasSelectedItems = hasSelectedItems();
        //是否有条目被选中,只要有一个被选中，都算选中。
        boolean hasSelectedItems = gridViewHasSelectedItems || !timeSelectedLayoutHasSelectedItems
                || listViewHasSelectedItems||studyTaskTypeGridViewHasSelectedItems;
        notifyPickerBar(hasSelectedItems);
    }

    private void notifyPickerBar(boolean selected) {
        View view = findViewById(R.id.contacts_picker_bar_layout);
        if (view != null) {
            TextView textView = (TextView) view.findViewById(
                    R.id.contacts_picker_clear);
            if (textView != null) {
                textView.setEnabled(selected);
            }
            textView = (TextView) view.findViewById(
                    R.id.contacts_picker_confirm);
            if (textView != null) {
                textView.setEnabled(true);
            }
        }
    }

    private void selectAllContacts(boolean selected) {
        //全选/取消全选按钮
        ImageView imageView = (ImageView) findViewById(
                R.id.contacts_select_all_icon);
        if (imageView != null) {
            imageView.setSelected(selected);
        }
        selectAllItems(selected);
        notifyPickerBar();
        getCurrAdapterViewHelper().update();
    }

    private boolean checkDate(String startDateStr, String endDateStr) {
        boolean isOk = true;
        String curDateStr = DateUtils.getDateStr(new Date(), "yyyy-MM-dd");
        int result = DateUtils.compareDate(startDateStr, curDateStr);
        if (result < 0) {
            isOk = false;
            TipMsgHelper.ShowLMsg(getActivity(), R.string.date_above_cur_date);
            return isOk;
        }
        result = DateUtils.compareDate(endDateStr, curDateStr);
        if (result < 0) {
            isOk = false;
            TipMsgHelper.ShowLMsg(getActivity(), R.string.date_above_cur_date);
            return isOk;
        }
        result = DateUtils.compareDate(endDateStr, startDateStr);
        if (result < 0) {
            isOk = false;
            TipMsgHelper.ShowLMsg(getActivity(), R.string.end_date_above_start_date);
            return isOk;
        }
        return isOk;
    }

    private void completePickContacts() {
        //主持人的集合
        List<StudentMemberInfo> studentMemberInfoList = adapter.getSelectorHelper()
                .getSelectedItems();
        //直播类型选取的集合
        List<StudyTaskTypeInfo> onlineTypeList = getSelectedItems();
        if (studentMemberInfoList != null && studentMemberInfoList.size() > 0) {
            String reporterIds = createTeacherIdsParam(studentMemberInfoList);
        }
        Intent intent = new Intent();
        //布置开始日期
        String assign_start_date_text=assign_start_date.getText().toString().trim();
        //布置结束日期
        String assign_end_date_text=assign_end_date.getText().toString().trim();
        //完成开始日期
        String finish_start_date_text=finish_start_date.getText().toString().trim();
        //完成结束日期
        String finish_end_date_text=finish_end_date.getText().toString().trim();
        if (fromType == FromType.FROM_AIRCLASS){
            //空中课堂的跳转
            Bundle bundle = getArguments();
            bundle.putString(AirClassroomActivity.EXTRA_FILTER_START_TIME_BEGIN, assign_start_date_text);
            bundle.putString(AirClassroomActivity.EXTRA_FILTER_START_TIME_END, assign_end_date_text);
            bundle.putString(AirClassroomActivity.EXTRA_FILTER_FINISH_TIME_BEGIN, finish_start_date_text);
            bundle.putString(AirClassroomActivity.EXTRA_FILTER_FINISH_TIME_END, finish_end_date_text);
            if (onlineTypeList != null && onlineTypeList.size() == 1) {
                bundle.putInt(AirClassroomActivity.EXTRA_FILTER_ONLINE_TYPE, Integer
                        .valueOf(onlineTypeList.get(0).getType()));
            } else {
                bundle.putInt(AirClassroomActivity.EXTRA_FILTER_ONLINE_TYPE, -1);
            }
            bundle.putBoolean(AirClassroomActivity.EXTRA_FROM_ONLINE_FILTER,true);
            intent.setClass(getActivity(),AirClassroomActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (fromType == FromType.FROM_MOOC_LIVE){
            int liveType = -1;
            if (onlineTypeList != null && onlineTypeList.size() > 0){
                if (onlineTypeList.size() == 1){
                    liveType = Integer.valueOf(onlineTypeList.get(0).getType());
                }else if(onlineTypeList.size() == 2){
                    liveType = 2;
                }
            }
            //慕课直播的跳转
            LiveFilter.jumpToFilterLiveList(getActivity(),
                    getArguments(),
                    assign_start_date_text,
                    assign_end_date_text,
                    finish_start_date_text,
                    finish_end_date_text,
                    liveType);
        }

    }

    private String createTeacherIdsParam(List<StudentMemberInfo> list) {
        StringBuilder sb = new StringBuilder();
        String result=null;
        if (list == null || list.size() <= 0) {
            result="";
        }else {
            for (int i = 0; i < list.size(); i++) {
                StudentMemberInfo info = list.get(i);
                if (info != null) {
                    if (i < list.size() - 1) {
                        sb.append(info.getMemberId()).append(",");
                    } else {
                        sb.append(info.getMemberId());
                    }
                }
            }
            result=sb.toString();
        }
        return result;
    }

    private String changTimeFormat(String timeStr, boolean isStartTime) {
        StringBuilder builder = new StringBuilder();
        builder.append(timeStr).append(" ");
        if (isStartTime) {
            builder.append("00:00");
        } else {
            builder.append("23:59");
        }
        return builder.toString();
    }
    private String createTaskTypesParam(List<StudyTaskTypeInfo> list) {
        StringBuilder sb = new StringBuilder();
        String result=null;
        if (list == null || list.size() <= 0) {
            result="";
        }else {
            for (int i = 0; i < list.size(); i++) {
                StudyTaskTypeInfo info = list.get(i);
                if (info != null) {
                    if (i < list.size() - 1) {
                        sb.append(info.getType()).append(",");
                    } else {
                        sb.append(info.getType());
                    }
                }
            }
            result=sb.toString();
        }
        return result;
    }
    @Override
    public void onClick(View v) {
        if (v == assign_start_date || v == assign_end_date || v == finish_start_date || v == finish_end_date) {
            showPopupWindow((TextView) v);
        } else {
            super.onClick(v);
        }
    }
    /**
     * 全选/取消全选
     *
     * @param selected
     */
    public void selectAdapterViewAllItems(boolean selected) {
        //全选图片要设置是否选中
        selectAllImageView.setSelected(selected);
        adapter.getSelectorHelper().selectAllItems(selected);
        //更新一下全选/取消全选的文字
        updateSelectAllText(adapter.getSelectorHelper().isAllItemsSelected());
        //更新一下底部取消和确定按钮状态
        notifyPickerBar();
        //更新一下数据
        adapter.notifyDataSetChanged();
    }

    /**
     * 全选/取消全选
     *
     * @param selected
     */
    public void selectStudyTaskTypeAdapterViewAllItems(boolean selected) {
        //全选图片要设置是否选中
        selectAllImageView_study_type.setSelected(selected);
        adapter_study_type.getSelectorHelper().selectAllItems(selected);
        //更新一下全选/取消全选的文字
        updateStudyTaskTypeSelectAllText(adapter_study_type.getSelectorHelper().isAllItemsSelected());
        //更新一下底部取消和确定按钮状态
        notifyPickerBar();
        //更新一下数据
        adapter_study_type.notifyDataSetChanged();
    }

    private void showPopupWindow(final TextView textView) {
        String dateString = textView.getText().toString().trim();
        if (TextUtils.isEmpty(dateString)){
           dateString = DateUtils.getDateStr(new Date());
        }
        DatePopupView stateDatePopView = new DatePopupView(getActivity(), dateString, true, new DatePopupView
                .OnDateChangeListener() {
            @Override
            public void onDateChange(String dateStr) {
                textView.setText(dateStr);
            }
        });
        stateDatePopView.showAtLocation(getView(), Gravity.BOTTOM, 0, 0);
    }
}
