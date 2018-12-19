package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.ClassDetailsActivity;
import com.galaxyschool.app.wawaschool.ContactsActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.Reporter;
import com.galaxyschool.app.wawaschool.pojo.ReporterListResult;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.SubscribeClassInfo;
import com.galaxyschool.app.wawaschool.pojo.SubscribeGradeInfo;
import com.galaxyschool.app.wawaschool.pojo.SubscribeGradeListResult;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReporterIdentityFragment extends ContactsListFragment implements View.OnClickListener{
    public static final String TAG=ReporterIdentityFragment.class.getSimpleName();
    public static final String ADD_REPORTER_PERMISSION="add_reporter_permission";
    public static final String REPORTER_LIST_TAG="reporter_list";
    private PullToRefreshView pullToRefreshView;
    private TextView headTitle,addPermission;
    private  boolean isAddReporterPermission;
    private SchoolInfo schoolInfo;
    private List<Reporter> reporterList=new ArrayList<>();
    private List<Reporter> addReporterTag=new ArrayList<>();
    private boolean isFirstLoad=true;
    public interface Constants{
        String SCHOOLINFO="schoolInfo";
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reporter_identity, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getIntent();
        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTypeData();
    }

    private void loadTypeData() {
        if (isAddReporterPermission){
            if (isFirstLoad){
                loadGrades();
                isFirstLoad=false;
            }
        }else {
            loadReporterList();
        }
    }

    private void getIntent(){
        isAddReporterPermission=getArguments().getBoolean(ADD_REPORTER_PERMISSION,false);
        if (isAddReporterPermission){
            //用来筛选已经记者已经存在于班级中
            if (addReporterTag.size()==0) {
                addReporterTag = getArguments().getParcelableArrayList(REPORTER_LIST_TAG);
            }
        }
        schoolInfo= (SchoolInfo) getArguments().getSerializable(Constants.SCHOOLINFO);
    }
    private void initViews(){
        //isAddReporterPermission用来区分显示小记者列表和增加记者权限
        headTitle= (TextView) findViewById(R.id.contacts_header_title);
        if (headTitle!=null){
            if (isAddReporterPermission){
                headTitle.setText(schoolInfo.getSchoolName());
            }else {
                headTitle.setText(getString(R.string.reporter_permission));
            }
        }
        addPermission= (TextView) findViewById(R.id.contacts_header_right_btn);
        if (addPermission!=null&&!isAddReporterPermission){
            addPermission.setVisibility(View.VISIBLE);
            addPermission.setText(getString(R.string.button_add));
            addPermission.setOnClickListener(this);
        }
        pullToRefreshView= (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        if (pullToRefreshView!=null){
            setPullToRefreshView(pullToRefreshView);
        }
        ListView listView= (ListView) findViewById(R.id.listview);
        //ListView长按时删除记者的权限
        if (!isAddReporterPermission) {
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    Reporter reporter = (Reporter) getCurrAdapterViewHelper().getData().get(position);
                    if (reporter != null) {
                        showDeleteDialog(reporter);
                    }
                    return false;
                }
            });
        }
        if (listView!=null){
            AdapterViewHelper helper = new AdapterViewHelper(getActivity(), listView, R.layout
                    .listview_common_item) {
                @Override
                public void loadData() {
                    loadTypeData();
                }
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    if (isAddReporterPermission){
                        final SubscribeClassInfo data= (SubscribeClassInfo) getData().get(position);
                        TextView tvName= (TextView)view.findViewById(R.id.contacts_item_title);
                        if (tvName!=null){
                            tvName.setText(data.getClassName());
                        }
                        ImageView imageView = (ImageView)view.findViewById(R.id.contacts_item_icon);
                        MyApplication.getThumbnailManager(getActivity()).displayThumbnailWithDefault(
                                AppSettings.getFileUrl(data.getHeadPicUrl()), imageView, R.drawable
                                        .default_group_icon);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(data.isClass()){
                                    ActivityUtils.enterClassDetialActivity(getActivity(),data
                                            .getClassId(), ClassDetailsActivity
                                            .FROM_TYPE_CLASS_HEAD_PIC,true,schoolInfo
                                            .getSchoolName(),data.getClassMailListId(),schoolInfo.getSchoolId
                                            (),data.getGroupId(), data.getIsHistory());
                                }else{
                                    ActivityUtils.enterClassDetialActivity(getActivity(),data
                                            .getClassId(), ClassDetailsActivity
                                            .FROM_TYPE_TEACHER_CONTACT,true,schoolInfo
                                            .getSchoolName(),data.getClassMailListId(),schoolInfo.getSchoolId
                                            (),data.getGroupId());
                                }
                            }
                        });
                        ViewHolder holder = (ViewHolder) view.getTag();
                        if (holder == null) {
                            holder = new ViewHolder();
                        }
                        holder.data = data;
                        view.setTag(holder);
                    }else {
                        if (getData() != null) {
                            Reporter data = (Reporter) getData().get(position);
                            TextView tvName = (TextView) view.findViewById(R.id.contacts_item_title);
                            if (tvName != null) {
                                String realName = data.getRealName();
                                tvName.setText(TextUtils.isEmpty(realName) ? data.getNickName() : realName);
                            }
                            TextView tvTime = (TextView) view.findViewById(R.id.contacts_item_status);
                            if (tvTime != null) {
                                tvTime.setText(getShowTime(data.getCreateTime()));
                            }
                            ImageView imageView = (ImageView) view.findViewById(R.id.contacts_item_icon);
                            MyApplication.getThumbnailManager(getActivity()).displayThumbnailWithDefault(
                                    AppSettings.getFileUrl(data.getHeadPicUrl()), imageView, R.drawable.default_user_icon);
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
                    if (holder == null || holder.data == null) {
                        return;
                    }
                    if (isAddReporterPermission){
                        enterGroupMembers((SubscribeClassInfo) holder.data);
                    }else {
//                        showDeleteDialog((Reporter) holder.data);
                    }
                }
            };
            setCurrAdapterViewHelper(listView, helper);
        }
    }
    private String  getShowTime(String createTime){
        int index=createTime.indexOf("T");
        createTime=createTime.substring(0,index);
        return  createTime;

    }
    private void showDeleteDialog(final Reporter data) {
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(getActivity(), getString
                (R.string.delete), getString(R.string.confirm_to_delete_reporter_id, data.getRealName()),
                getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }, getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                deleteReporterItem(data);
            }
        });
        messageDialog.show();
    }

    /**
     * 删除记者的权限
     * @param data
     */
    private void deleteReporterItem(final Reporter data) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("Id",data.getId());
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
                            getCurrAdapterViewHelper().getData().remove(data);
                            getCurrAdapterViewHelper().update();
                            TipsHelper.showToast(getActivity(), R.string.delete_success);
                        }
                    }
                };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.DELETE_REPORTER_PERMISSION_BASE_URL,
                params, listener);
    }

    private void enterGroupMembers(SubscribeClassInfo classInfo) {
        Bundle args = null;
        //筛选掉直播管理管自己的身份
        List<Reporter> rps=new ArrayList<>();
        Reporter reporter=new Reporter();
        UserInfo userInfo=getUserInfo();
        if (userInfo!=null){
            reporter.setMemberId(userInfo.getMemberId());
            rps.add(reporter);
        }
//        rps=addReporterTag;
//        //移除不是当前班级已存在的记者
//        if (addReporterTag!=null&&addReporterTag.size()>0) {
//            for (int i = 0; i < addReporterTag.size(); i++) {
//                String clsId = addReporterTag.get(i).getClassId();
//                if (!TextUtils.isEmpty(clsId)){
//                    if (clsId.equals(classInfo.getClassId())){
//                        rps.remove(i);
//                    }
//                }
//            }
//        }
        if (classInfo.isClass() || classInfo.isSchool()) {
            args = new Bundle();
            args.putInt(ContactsActivity.EXTRA_CONTACTS_TYPE, classInfo.getType());
            args.putString(ContactsActivity.EXTRA_CONTACTS_ID, classInfo.getClassMailListId());
            args.putString(ContactsActivity.EXTRA_CONTACTS_NAME, classInfo.getClassName());
            args.putString(ContactsActivity.EXTRA_CONTACTS_SCHOOL_ID, classInfo.getSchoolId());
            args.putString(ContactsActivity.EXTRA_CONTACTS_SCHOOL_NAME, schoolInfo.getSchoolName());
            args.putString(ContactsActivity.EXTRA_CONTACTS_CLASS_ID, classInfo.getClassId());
            args.putString(ContactsActivity.EXTRA_CONTACTS_CLASS_NAME, classInfo.getClassName());
            args.putString(ContactsActivity.EXTRA_CONTACTS_HXGROUP_ID, classInfo.getGroupId());
            args.putParcelableArrayList(ContactsActivity.EXTRA_CONTACTS_FOR_REPORTER, (ArrayList<? extends Parcelable>) rps);
//            args.putString("from", GroupExpandListFragment.TAG);
            args.putString("from", ReporterIdentityFragment.TAG);
        }
        if (classInfo.isClass()) {
            args.putInt(ClassContactsDetailsFragment.Constants.EXTRA_CLASS_STATUS,
                    classInfo.getIsHistory());
        }
        if (args != null) {
            Intent intent = new Intent(getActivity(), ContactsActivity.class);
            intent.putExtras(args);
                startActivityForResult(intent,
                        ClassContactsDetailsFragment.Constants.REQUEST_CODE_CLASS_DETAILS);
        }
    }
    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.contacts_header_right_btn){
            addReporterPermission();
        }else if (v.getId()==R.id.contacts_header_left_btn){
            if (isAddReporterPermission){
                popStack();
            }else {
                getActivity().finish();
            }
        }
    }

    private void addReporterPermission() {
        FragmentTransaction ft=getActivity().getSupportFragmentManager().beginTransaction();
        ReporterIdentityFragment fragment=new ReporterIdentityFragment();
        Bundle bundle=new Bundle();
        bundle.putBoolean(ADD_REPORTER_PERMISSION,true);
        bundle.putSerializable(Constants.SCHOOLINFO,schoolInfo);
        bundle.putParcelableArrayList(REPORTER_LIST_TAG, (ArrayList<? extends Parcelable>) reporterList);
        fragment.setArguments(bundle);
        ft.replace(R.id.activity_body,fragment,TAG);
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * 加载所有的班级
     */
    private void loadGrades() {
        if (schoolInfo== null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("SchoolId", schoolInfo.getSchoolId());
        DefaultPullToRefreshListener listener =
                new DefaultPullToRefreshListener<SubscribeGradeListResult>(
                        SubscribeGradeListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        SubscribeGradeListResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            return;
                        }
                        updateGrades(result);
                    }
                };
        postRequest(ServerUrl.SUBSCRIBE_CLASS_LIST_URL, params, listener);
    }
    private void updateGrades(SubscribeGradeListResult result) {
        List<SubscribeClassInfo> classList = new ArrayList();
        SubscribeClassInfo classInfo = result.getModel().getTeacherBook();
        if (classInfo!=null){
            classList.add(classInfo);
        }
        List<SubscribeGradeInfo> gradeList = result.getModel().getNaddedClassList();
        if (gradeList != null && gradeList.size() > 0) {
            for (int i=0;i<gradeList.size();i++){
                SubscribeGradeInfo gradeInfo=gradeList.get(i);
                List<SubscribeClassInfo> gCF=gradeInfo.getClassList();
                if (gCF!=null){
                    classList.addAll(gCF);
                }
            }
        }
        if (classList!=null){
            getCurrAdapterViewHelper().setData(classList);
        }else {
            TipMsgHelper.ShowLMsg(getActivity(),getString(R.string.no_data));
            return;
        }

    }

    private void loadReporterList() {
        if (schoolInfo == null) {
            return;
        }
        //每次加载拉取16数据
        pageHelper.setPageSize(16);
        Map<String, Object> params = new HashMap();
        params.put("SchoolId", schoolInfo.getSchoolId());
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        DefaultDataListener listener = new DefaultDataListener<ReporterListResult>(
                ReporterListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                ReporterListResult result = getResult();
                if (result == null || !result.isSuccess()
                        || result.getModel() == null) {
                    return;
                }
                upDateReporterList(result);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_CAMPUS_REPORTER_LIST_BASE_URL, params, listener);
    }
    private void upDateReporterList(ReporterListResult result){
        List<Reporter> reporters=result.getModel().getData();
        if (reporters==null||reporters.size()<=0){
            getCurrAdapterViewHelper().setData(reporters);
            TipMsgHelper.ShowLMsg(getActivity(),getString(R.string.no_data));
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
                getCurrAdapterViewHelper().getData().addAll(reporters);
                getCurrAdapterView().setSelection(position);
            } else {
                getCurrAdapterViewHelper().setData(reporters);
            }
            reporterList=getCurrAdapterViewHelper().getData();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode== Activity.RESULT_OK) {
            if (requestCode == ClassContactsDetailsFragment.Constants.REQUEST_CODE_CLASS_DETAILS) {
                popStack();
                pageHelper.setFetchingPageIndex(0);
            }
        }
    }
}
