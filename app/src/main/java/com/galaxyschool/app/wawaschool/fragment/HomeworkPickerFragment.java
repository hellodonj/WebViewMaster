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

import com.galaxyschool.app.wawaschool.ContactsPickerActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.ScreeningHomeworkResultActivity;
import com.galaxyschool.app.wawaschool.adapter.GridVewSelectorAdapter;
import com.galaxyschool.app.wawaschool.adapter.GridViewContactsSelectorAdapter;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.contacts.ContactsPickerListener.PersonalContactsPickerListener;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.HomeworkChildListResult;
import com.galaxyschool.app.wawaschool.pojo.PickerMode;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.StudentMemberInfo;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskTypeInfo;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.views.DatePickerPopupView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 筛选作业
 */
public class HomeworkPickerFragment extends ContactsPickerFragment
        implements DatePickerPopupView.OnDatePickerItemSelectedListener {

    public static final String TAG = HomeworkPickerFragment.class.getSimpleName();

    private int pickerMode;
    private PersonalContactsPickerListener pickerListener;
    private View selectAllView;
    private HomeworkChildListResult dataListResult;
    private TextView assign_start_date;
    private TextView assign_end_date;
    private TextView finish_start_date;
    private TextView finish_end_date;
    private View rootView;
    //GridView
    private GridView gridView;
    private List<StudentMemberInfo> teacherList = new ArrayList<>();
    private GridVewSelectorAdapter adapter;
    //老师选择布局
    private View layout_picker_grid_view;
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
    private int gridViewPickerMode;
    private int roleType = -1;
    private String classId;

    //作业类型ListView
    private ListView gridView_study_type;
    int studyTaskTypePickerMode=-1;
    private List<StudyTaskTypeInfo> studyTaskTypeInfoList = new ArrayList<>();
    private GridViewContactsSelectorAdapter adapter_study_type;
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
    private String schoolId;
    private String childId;
    private boolean isHeadMaster;
    private static boolean hasCommented;
    private String[] childIdArray;//孩子数组

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

    public void setPickerListener(PersonalContactsPickerListener listener) {
        this.pickerListener = listener;
    }

    private void init() {
        this.pickerMode = getArguments().getInt(ContactsPickerActivity.EXTRA_PICKER_MODE);
        this.gridViewPickerMode = PickerMode.PICKER_MODE_MULTIPLE;
        if (getArguments() != null) {
            roleType = getArguments().getInt("roleType");
            classId = getArguments().getString("classId");
            schoolId=getArguments().getString("schoolId");
            isHeadMaster = getArguments().getBoolean(HomeworkMainFragment.Constants
                    .EXTRA_IS_HEAD_MASTER);
            if (roleType==RoleType.ROLE_TYPE_PARENT){
                childId=getArguments().getString("childId");
                //获得孩子数组
                childIdArray = (String[]) getArguments().get(HomeworkMainFragment.Constants
                        .EXTRA_CHILD_ID_ARRAY);
            }
        }
        initGridView();
        initStudyTaskTypeListView();
        initViews();
    }

    private void initStudyTaskTypeListView() {
        studyTaskTypePickerMode=PickerMode.PICKER_MODE_MULTIPLE;
        //老师全选布局,仅对老师隐藏。
        layout_picker_grid_view_study_type = findViewById(R.id.layout_picker_grid_view_study_type);
        if (roleType == RoleType.ROLE_TYPE_TEACHER) {
            layout_picker_grid_view_study_type.setVisibility(View.GONE);
        } else {
            layout_picker_grid_view_study_type.setVisibility(View.VISIBLE);
        }
        //配置全选布局
        contacts_select_all_layout_study_type = findViewById(R.id.layout_select_all_layout_study_type);

        if (studyTaskTypePickerMode == PickerMode.PICKER_MODE_MULTIPLE) {
            contacts_select_all_layout_study_type.setVisibility(View.VISIBLE);
        } else if (studyTaskTypePickerMode == PickerMode.PICKER_MODE_SINGLE) {
            contacts_select_all_layout_study_type.setVisibility(View.GONE);
        }

        contacts_select_all_layout_study_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //全选/取消全选
                selectStudyTaskTypeAdapterViewAllItems(!selectAllImageView_study_type.isSelected());
            }
        });

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
        //老师全选布局,仅对老师隐藏。
        layout_picker_grid_view = findViewById(R.id.layout_picker_grid_view);
        if (roleType == RoleType.ROLE_TYPE_TEACHER) {
            //对班主任显示
            if (isHeadMaster){
                layout_picker_grid_view.setVisibility(View.VISIBLE);

            }else {
                //放开老师筛选选择老师的权限
                layout_picker_grid_view.setVisibility(View.VISIBLE);
            }

        } else {
            layout_picker_grid_view.setVisibility(View.VISIBLE);
        }
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
        contacts_select_item_title.setText(R.string.teacher);

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
        View view = findViewById(R.id.contacts_header_layout);
        if (view != null) {
            if (getArguments().getBoolean(ContactsPickerActivity.EXTRA_USE_EXTENDED_PICKER)) {
                view.setVisibility(View.GONE);
            } else {
                view.setVisibility(View.VISIBLE);
            }
        }

        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setText(R.string.screening);
        }

        //时间选择
        //今天
        String today = DateUtils.getDateYmdStr();
        //明天
        String tomorrow = DateUtils.getTomorrow();

        assign_start_date = (TextView) findViewById(R.id.assign_start_date);
        assign_start_date.setText(today);
        assign_start_date.setOnClickListener(this);

        assign_end_date = (TextView) findViewById(R.id.assign_end_date);
        assign_end_date.setText(today);
        assign_end_date.setOnClickListener(this);

        finish_start_date = (TextView) findViewById(R.id.finish_start_date);
        finish_start_date.setText(tomorrow);
        finish_start_date.setOnClickListener(this);

        finish_end_date = (TextView) findViewById(R.id.finish_end_date);
        finish_end_date.setText(tomorrow);
        finish_end_date.setOnClickListener(this);

        notifyPickerBar();

        //全选/取消全选布局
        view = findViewById(R.id.contacts_select_all);
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
            textView.setText(R.string.study_task_type);
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
//            initHomeworkStateType();
            initListViewHelper();
        }
    }

//    private void initHomeworkStateType() {
//        layout_homework_state_type=findViewById(R.id.layout_homework_state_type);
//        if (layout_homework_state_type!=null){
//
//        }
//    }

    private void initListViewHelper() {
        //ListView
        ListView listView = (ListView) findViewById(R.id.homework_list_view);
        if (listView != null) {
//            listView.setSlideMode(SlideListView.SlideMode.NONE);
            AdapterViewHelper listViewHelper = new AdapterViewHelper(getActivity(),
                    listView, R.layout.homework_list_item_with_selector) {
                @Override
                public void loadData() {
                    loadTeacherInfo();
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
                    MyViewHolder holder = (MyViewHolder) view.getTag();
//                    if (holder == null) {
                    holder = new MyViewHolder();
//                    }
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
//                            holder.selectorView.invalidate();
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
//                    holder.selectorView.invalidate();
                    getDataAdapter().notifyDataSetChanged();
                }
            };
            setCurrAdapterViewHelper(listView, listViewHelper);
//            addAdapterViewHelper("listView",listViewHelper);
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
            loadStudyTaskTypeInfo();
        }
    }

    /**
     * 拉取学习任务类型信息
     */
    private void loadStudyTaskTypeInfo() {

        List<StudyTaskTypeInfo> list = new ArrayList<>();
        //0-看微课,1-看课件,2看作业,3-交作业,4-讨论话题

        //看微课
        //新版看课件
        StudyTaskTypeInfo scanMiscoClass = new StudyTaskTypeInfo();
        scanMiscoClass.setType("0,9");
        //目前：名称改为看课件，类型不变。
        scanMiscoClass.setTypeName(getString(R.string.look_through_courseware));

        //看课件
        //目前：暂时隐藏。
        StudyTaskTypeInfo scanCourseWare = new StudyTaskTypeInfo();
        scanCourseWare.setType("1");
        scanCourseWare.setTypeName(getString(R.string.look_through_courseware));

        //看作业,目前看作业改为“作业”
        //目前：隐藏交作业，筛选“作业”其实就是筛选“看作业”和“交作业”。
        StudyTaskTypeInfo scanHomework = new StudyTaskTypeInfo();
//        scanHomework.setType("2");
        scanHomework.setType("2,3");
        scanHomework.setTypeName(getString(R.string.other));
//        scanHomework.setTypeName(getString(R.string.look_through_homework));

        //交作业
        StudyTaskTypeInfo commitHomework = new StudyTaskTypeInfo();
        commitHomework.setType("3");
        commitHomework.setTypeName(getString(R.string.commit_homework));

        //讨论话题
        StudyTaskTypeInfo discussionTopic = new StudyTaskTypeInfo();
        discussionTopic.setType("4");
        discussionTopic.setTypeName(getString(R.string.discuss_topic));

        //复述微课
        StudyTaskTypeInfo retellMicroCourse = new StudyTaskTypeInfo();
        retellMicroCourse.setType("5");
        retellMicroCourse.setTypeName(getString(R.string.retell_course));
        //导读
        StudyTaskTypeInfo introductionCourse=new StudyTaskTypeInfo();
        introductionCourse.setType("6");
        introductionCourse.setTypeName(getString(R.string.introduction));
        //英文写作
        StudyTaskTypeInfo englishWriting=new StudyTaskTypeInfo();
        englishWriting.setType("7");
        englishWriting.setTypeName(getString(R.string.english_writing));
        //做任务单
        StudyTaskTypeInfo taskOrder = new StudyTaskTypeInfo();
        taskOrder.setType("8");
        taskOrder.setTypeName(getString(R.string.do_task));
        //听说+读写
        StudyTaskTypeInfo listenReadAndWrite = new StudyTaskTypeInfo();
        listenReadAndWrite.setType("10");
        listenReadAndWrite.setTypeName(getString(R.string.str_listen_read_and_write));

        //综合任务
        StudyTaskTypeInfo superTask = new StudyTaskTypeInfo();
        superTask.setType("11");
        superTask.setTypeName(getString(R.string.str_super_task));
        //目前：隐藏看微课，看课件，交作业。
        //目前放开：看微课（“看微课”类型不变，名称改为“看课件”，原来的“看课件”类型暂时不动，仍然隐藏），交作业。
        //综合任务
        list.add(superTask);
        //做任务单
        list.add(taskOrder);
        //复述课件
        list.add(retellMicroCourse);
        //听说+读写
//        list.add(listenReadAndWrite);
        //看课件
        list.add(scanMiscoClass);
        //英文写作
        list.add(englishWriting);
        //导读
//        list.add(introductionCourse);
        //话题讨论
        list.add(discussionTopic);
        //其他
        list.add(scanHomework);
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

        //如果是学生或者家长，拉取老师信息。现在增加班主任也能筛选老师。
        if (roleType == RoleType.ROLE_TYPE_STUDENT || roleType == RoleType.ROLE_TYPE_PARENT
                || roleType == RoleType.ROLE_TYPE_TEACHER) {
            //放开老师筛选选择老师的权限
            loadHomeworkStateInfo();
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

    private void loadHomeworkStateInfo() {
        List<StudyTaskTypeInfo> list = new ArrayList<>();

        //已完成
        StudyTaskTypeInfo finishedTask = new StudyTaskTypeInfo();
        finishedTask.setType("1");
        finishedTask.setTypeName(getString(R.string.finished));

        //未完成
        StudyTaskTypeInfo unFinishedTask = new StudyTaskTypeInfo();
        unFinishedTask.setType("0");
        unFinishedTask.setTypeName(getString(R.string.unfinished));

        list.add(finishedTask);
        list.add(unFinishedTask);
        studyTaskTypeInfoList.clear();
        studyTaskTypeInfoList.addAll(list);
        adapter_study_type.notifyDataSetChanged();
        loadTeacherInfo();
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
//        notifyPickerBar(hasSelectedItems());
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
//                textView.setEnabled(selected);
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
        //如果啥都没选择，直接返回即可。
        Intent resultIntent = new Intent(getActivity(),ScreeningHomeworkResultActivity.class);
        int type=-1;
        if (!adapter.getSelectorHelper().hasSelectedItems()
                && isSelectedTimeLayoutEmpty()
                && !hasSelectedItems()
                &&!adapter_study_type.getSelectorHelper().hasSelectedItems()) {
            type=0;
        } else {
            type=1;
            String teacherIds=null;
            //老师列表，学生或家长角色才使用。现在班主任也可以了。
            //放开老师
            if (roleType == RoleType.ROLE_TYPE_STUDENT
                    || roleType == RoleType.ROLE_TYPE_PARENT
                    || roleType == RoleType.ROLE_TYPE_TEACHER) {
                List<StudentMemberInfo> studentMemberInfoList = adapter.getSelectorHelper()
                        .getSelectedItems();
                List<StudyTaskTypeInfo> studyTypeList=adapter_study_type.getSelectorHelper()
                        .getSelectedItems();

                if (studentMemberInfoList != null && studentMemberInfoList.size() > 0) {
                    teacherIds = createTeacherIdsParam(studentMemberInfoList);
                }

                //老师Id串
                if (!TextUtils.isEmpty(teacherIds)){
                    resultIntent.putExtra("TeacherIds",teacherIds);
                }

                //未完成和已完成，全选和不选不传参，单选传参。
                int studyTaskType=-1;
                if (studyTypeList!=null&&studyTypeList.size()>0){
                    if (studyTypeList.size()==2){

                    }else if (studyTypeList.size()==1){
                        studyTaskType= Integer.parseInt(studyTypeList.get(0).getType());
                        resultIntent.putExtra("TaskState",studyTaskType);
                    }
                }else {
                    resultIntent.putExtra("TaskState",-1);
                }
            }

            //学习任务类型
            String taskTypes=null;
            List<StudyTaskTypeInfo> studyTaskTypeInfoList=getSelectedItems();
            if (studyTaskTypeInfoList!=null&&studyTaskTypeInfoList.size()>0){
                taskTypes=createTaskTypesParam(studyTaskTypeInfoList);
            }

            //任务类型Id串
            if (!TextUtils.isEmpty(taskTypes)){
                resultIntent.putExtra("TaskTypes",taskTypes);
            }
        }

        //是否是班主任
        resultIntent.putExtra(HomeworkMainFragment.Constants.EXTRA_IS_HEAD_MASTER,isHeadMaster);

        //通过判断Intent的type值，可以判断是否选择了筛选条件。
        resultIntent.putExtra("type",type);
        //日期处理
        boolean isOk = checkDate();
        if (!isOk) {
            return;
        }
        //布置开始日期
        String assign_start_date_text=assign_start_date.getText().toString().trim();
        if (assign_start_date_text.length()>0){
            resultIntent.putExtra("StartTimeBegin",assign_start_date_text);
        }

        //布置结束日期
        String assign_end_date_text=assign_end_date.getText().toString().trim();
        if (assign_end_date_text.length()>0){
            resultIntent.putExtra("StartTimeEnd",assign_end_date_text);
        }

        //完成开始日期
        String finish_start_date_text=finish_start_date.getText().toString().trim();
        if (finish_start_date_text.length()>0){
            resultIntent.putExtra("EndTimeBegin",finish_start_date_text);
        }

        //完成结束日期
        String finish_end_date_text=finish_end_date.getText().toString().trim();
        if (finish_end_date_text.length()>0){
            resultIntent.putExtra("EndTimeEnd",finish_end_date_text);
        }

        resultIntent.putExtra("roleType",roleType);
        resultIntent.putExtra("schoolId",schoolId);
        resultIntent.putExtra("classId",classId);
        if (roleType==RoleType.ROLE_TYPE_PARENT){
            resultIntent.putExtra("childId",childId);
            //传递孩子数组
            resultIntent.putExtra(HomeworkMainFragment.Constants.EXTRA_CHILD_ID_ARRAY,childIdArray);
            //传递User信息
            if(getArguments() != null &&
                    getArguments().containsKey(UserInfo.class.getSimpleName())) {
                UserInfo userInfo = (UserInfo) getArguments().getSerializable
                        (UserInfo.class.getSimpleName());
                if(userInfo != null) {
                    resultIntent.putExtra(UserInfo.class.getSimpleName(), userInfo);
                }
            }
        }

        startActivityForResult(resultIntent,CampusPatrolPickerFragment.
                REQUEST_CODE_HOMEWORK_SCREENING_RESULT);
    }

    private boolean checkDate() {
        String assignStartTime = assign_start_date.getText().toString().trim();
        String assignEndTime = assign_end_date.getText().toString().trim();
        String finishStartTime = finish_start_date.getText().toString().trim();
        String finishEndTime = finish_end_date.getText().toString().trim();
        String startTime = null;
        String finishTime = null;
        if (!TextUtils.isEmpty(assignStartTime) && !TextUtils.isEmpty(assignEndTime)){
            int result = DateUtils.compareDate(assignStartTime, assignEndTime);
            if (result == 1){
                TipMsgHelper.ShowLMsg(getActivity(), R.string.str_assign_start_time_above_end_time);
                return false;
            }
            startTime = assignStartTime;
        } else if (!TextUtils.isEmpty(assignStartTime)){
            startTime = assignStartTime;
        } else if (!TextUtils.isEmpty(assignEndTime)){
            startTime = assignEndTime;
        }

        if (!TextUtils.isEmpty(finishStartTime) && !TextUtils.isEmpty(finishEndTime)){
            int result = DateUtils.compareDate(finishStartTime, finishEndTime);
            if (result == 1){
                TipMsgHelper.ShowLMsg(getActivity(), R.string.str_finish_start_time_above_end_time);
                return false;
            }
            finishTime = finishStartTime;
        } else if (!TextUtils.isEmpty(assignStartTime)){
            finishTime = assignStartTime;
        } else if (!TextUtils.isEmpty(assignEndTime)){
            finishTime = assignEndTime;
        }

        if (!TextUtils.isEmpty(startTime) && !TextUtils.isEmpty(finishTime)){
            int result = DateUtils.compareDate(startTime, finishTime);
            if (result == 1){
                TipMsgHelper.ShowLMsg(getActivity(), R.string.str_assign_time_above_finish_time);
                return false;
            }
        }
        return true;
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
            String dateStr = ((TextView)v).getText().toString();
            showPopupWindow(v,dateStr);
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

    private void showPopupWindow(View targetView,String dateStr) {
        String pattern = "yyyy-MM-dd";
        Date currentDate=new Date();
        Date date = DateUtils.parseDateStr(dateStr, pattern);
        DatePickerPopupView datePickerPopupView = new DatePickerPopupView(getActivity(), this, targetView);
        if(date != null) {
            datePickerPopupView.setCurrentYearMonthDay(date.getYear() + 1900, date.getMonth(), date.getDate());
        }else {
            datePickerPopupView.setCurrentYearMonthDay(currentDate.getYear() + 1900, currentDate.getMonth(), currentDate.getDate());
        }
        datePickerPopupView.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void onDatePickerItemSelected(String pickedResultStr, View targetView) {
        TextView obj = (TextView) targetView;
        if (pickedResultStr != null) {
            obj.setText(pickedResultStr);
        }
        //更新一下底部取消和确定的布局。
        notifyPickerBar();
    }

    public static void setHasCommented(boolean hasCommented) {
        HomeworkPickerFragment.hasCommented = hasCommented;
    }

    public static boolean hasCommented() {
        return hasCommented;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            if (requestCode == CampusPatrolPickerFragment
                    .REQUEST_CODE_HOMEWORK_SCREENING_RESULT) {
                //从筛选作业结果列表页面返回，是否要刷新页面。
                if (ScreeningHomeworkResultListFragment.hasCommented()) {
                    //通知之前的页面，需要刷新。
                    setHasCommented(true);
                    //reset value
                    ScreeningHomeworkResultListFragment.setHasCommented(false);
                }
            }
        }
    }
}
