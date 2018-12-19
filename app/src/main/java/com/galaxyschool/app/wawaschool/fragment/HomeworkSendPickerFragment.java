package com.galaxyschool.app.wawaschool.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.galaxyschool.app.wawaschool.ContactsPickerActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.Note.MediaPaperActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.NoteHelper;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.contacts.ContactsPickerListener.PersonalContactsPickerListener;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.pojo.ContactsFriendListResult;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskTypeInfo;
import com.lqwawa.lqbaselib.net.library.DataResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.*;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseUploadResult;
import com.galaxyschool.app.wawaschool.views.DatePickerPopupView;
import java.util.*;

/**
 * 筛选作业
 */
public class HomeworkSendPickerFragment extends ContactsPickerFragment implements DatePickerPopupView.OnDatePickerItemSelectedListener {

    public static final String TAG = HomeworkPickerFragment.class.getSimpleName();

    private int pickerMode;
    private PersonalContactsPickerListener pickerListener;
    private View selectAllView;
    private ContactsFriendListResult dataListResult;
    private TextView assign_start_date;
    private TextView finish_start_date;
    private View rootView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_send_homework, null);
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
        this.pickerMode = PickerMode.PICKER_MODE_MULTIPLE;
        initViews();
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
            textView.setText(R.string.send);
        }

//        textView = (TextView) findViewById(R.id.contacts_header_right_btn);
//        if (textView != null) {
//            String text = getArguments().getString(
//                    ContactsPickerActivity.EXTRA_PICKER_CONFIRM_BUTTON_TEXT);
//            if (TextUtils.isEmpty(text)) {
//                text = getString(R.string.confirm);
//            }
//            textView.setText(text);
//            textView.setBackgroundResource(R.drawable.sel_nav_button_bg);
//            textView.setOnClickListener(this);
//            if (getArguments().getBoolean(ContactsPickerActivity.EXTRA_USE_EXTENDED_PICKER)) {
//                textView.setVisibility(View.INVISIBLE);
//            } else {
//                textView.setVisibility(View.VISIBLE);
//            }
//        }

        //时间选择

        //今天
        String today = DateUtils.getDateYmdStr();
        //明天
        String tomorrow = DateUtils.getTomorrow();

        assign_start_date = (TextView) findViewById(R.id.assign_date);
        assign_start_date.setText(today);
        assign_start_date.setOnClickListener(this);

        finish_start_date = (TextView) findViewById(R.id.finish_date);
        finish_start_date.setText(tomorrow);
        finish_start_date.setOnClickListener(this);
        notifyPickerBar();

        //全选/取消全选布局
        view = findViewById(R.id.contacts_select_all);
        if (this.pickerMode == ContactsPickerActivity.PICKER_MODE_MULTIPLE) {
            view.setVisibility(View.GONE);
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
            textView.setText(R.string.work_type);
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
        view = findViewById(R.id.homework_picker_bar_layout);
        //取消按钮
        if (view != null) {
            textView = (TextView) view.findViewById(R.id.contacts_picker_clear);
            if (textView != null) {
                textView.setText(R.string.clear);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectAllContacts(false);
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
//            if (getArguments().getBoolean(ContactsPickerActivity.EXTRA_USE_EXTENDED_PICKER)) {
//                view.setVisibility(View.VISIBLE);
//            } else {
//                view.setVisibility(View.GONE);
//            }
        }

//        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
//                R.id.contacts_pull_to_refresh);
//        setPullToRefreshView(pullToRefreshView);

        ListView listView = (ListView) findViewById(R.id.homework_list_view);
        if (listView != null) {
//            listView.setSlideMode(SlideListView.SlideMode.NONE);
            AdapterViewHelper listViewHelper = new AdapterViewHelper(getActivity(),
                    listView, R.layout.homework_list_item_with_selector) {
                @Override
                public void loadData() {
                    loadContacts();
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
        }
    }


    private void loadViews() {
        if (getCurrAdapterViewHelper().hasData()) {
            getCurrAdapterViewHelper().update();
        } else {
            loadContacts();
        }
    }

    private void clearDateSelectLayout() {
        assign_start_date.setText("");
        finish_start_date.setText("");
        notifyPickerBar();
    }

    private void loadContacts() {
        List<StudyTaskTypeInfo> list =new ArrayList<>();
        //作业类型，0，交作业 1,看作业。

        //交作业
        StudyTaskTypeInfo commitHomework=new StudyTaskTypeInfo();
        commitHomework.setType("0");
        commitHomework.setTypeName(getString(R.string.need_student_to_commit));

        //看作业,目前：看作业改为“作业”
        //目前：交作业和看作业都改为：“需要学生提交”，如果没勾选的话，默认是看作业，否则是交作业。
        StudyTaskTypeInfo scanHomework=new StudyTaskTypeInfo();
        scanHomework.setType("1");
        scanHomework.setTypeName(getString(R.string.homeworks));
        //隐藏交作业
//        scanHomework.setTypeName(getString(R.string.look_through_homework));

        list.add(commitHomework);
//        list.add(scanHomework);
        updateViews(list);
    }

    private void updateViews(List<StudyTaskTypeInfo> list) {
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
        boolean listViewHasSelectedItems = hasSelectedItems();
        boolean isTimeSelectLayoutEmpty = isTimeSelectLayoutEmpty();
        boolean hasSelectedItems = listViewHasSelectedItems || !isTimeSelectLayoutEmpty;
        notifyPickerBar(hasSelectedItems);
    }

    public boolean isTimeSelectLayoutEmpty() {
        boolean isEmpty;
        int startLength = assign_start_date.getText().toString().trim().length();
        int endLength = finish_start_date.getText().toString().trim().length();
        if (startLength == 0 && endLength == 0) {
            isEmpty = true;
        } else {
            isEmpty = false;
        }
        return isEmpty;
    }

    private void notifyPickerBar(boolean selected) {
        View view = findViewById(R.id.homework_picker_bar_layout);
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

    private void completePickContacts() {
        //提交的类型
        int homeworkType = -1;
        List<StudyTaskTypeInfo> items = getSelectedItems();
        if (items == null || items.size() <= 0) {
//            TipsHelper.showToast(getActivity(),
//                    R.string.work_type_empty);
//            return;
            //默认是选择看作业
            homeworkType = StudyTaskType.WATCH_HOMEWORK;
        }
        //提交的类型,单选，只有一个item。
        int  index = -1;
        if (items != null && items.size() >0) {
            index = Integer.parseInt(items.get(0).getType());
        }

        if (index!=-1){
            if (index==0){
                //交作业
                homeworkType = StudyTaskType.SUBMIT_HOMEWORK;
            }else if (index==1){
                //看作业
                homeworkType = StudyTaskType.WATCH_HOMEWORK;
            }
        }

        String assignText=assign_start_date.getText().toString().trim();
        String finishText=finish_start_date.getText().toString().trim();

        if (assignText.length() == 0 || finishText.length() == 0) {
            TipsHelper.showToast(getActivity(),
                    R.string.date_empty);
            return;
        }

        if(homeworkType > 0) {
            commitHomework(homeworkType);
        }

    }

    private void commitHomework(int homeworkType) {
        String startDateStr = assign_start_date.getText().toString();
        String endDateStr = finish_start_date.getText().toString();
        boolean isOk = checkDate(startDateStr, endDateStr);
        if (!isOk) {
            return;
        }
        final UploadParameter uploadParameter = (UploadParameter) getArguments().getSerializable(UploadParameter.class.getSimpleName());
        if (uploadParameter != null) {
            uploadParameter.setStartDate(startDateStr);
            uploadParameter.setEndDate(endDateStr);
            uploadParameter.setTaskType(homeworkType);
            if(uploadParameter.getSourceType() == MediaPaperActivity.SourceType.CLASS_SPACE_HOMEWORK) {
                showLoadingDialog();
                NoteHelper.uploadNote(getActivity(), uploadParameter, 0, new CallbackListener() {
                    @Override
                    public void onBack(Object result) {
                        dismissLoadingDialog();
                        CourseUploadResult uploadResult = (CourseUploadResult) result;
                        if (uploadResult != null && uploadResult.getCode() == 0) {
                            if (uploadResult.data == null || uploadResult.data.size() == 0) {
                                TipMsgHelper.ShowLMsg(getActivity(), R.string.update_failure);
                            } else {
                                if(uploadResult.data.size() > 0) {
                                    CourseData data = uploadResult.data.get(0);
                                    if(data != null) {
                                        NoteHelper.publishStudyTask(getActivity(), uploadParameter, data, true);
                                    }
                                }
                            }
                        }
                    }
                });
            } else {
                NoteInfo noteInfo = getArguments().getParcelable(NoteInfo.class.getSimpleName());
                NoteHelper.enterContactsPicker(getActivity(), uploadParameter, noteInfo,
                    uploadParameter.getFileName(), "", PublishResourceFragment.Constants.TYPE_CLASS_SPACE, true);

            }
        }
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

    @Override
    public void onClick(View v) {
        if (v == assign_start_date || v == finish_start_date) {
            String dateStr = ((TextView)v).getText().toString();
            showPopupWindow(v, dateStr);
        } else {
            super.onClick(v);
        }
    }

    private void showPopupWindow(View targetView, String dateStr) {
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
        notifyPickerBar();
    }

    public boolean isRightDate(String beginDate){
        boolean isRighDate=false;
        int result=DateUtils.compareDate(beginDate,DateUtils.getDateYmdStr());
        if (result<0){
            isRighDate=false;
        }else {
            isRighDate=true;
        }
        return isRighDate;
    }
}
