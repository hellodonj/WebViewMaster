package com.galaxyschool.app.wawaschool.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.SlideListViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.galaxyschool.app.wawaschool.pojo.CheckMarkInfo;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassMemberInfo;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassMemberListResult;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassRequestListResult;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.slidelistview.SlideListView;

import java.util.ArrayList;
import java.util.Map;
 /**
  * ================================================
  * 作    者：Blizzard-liu
  * 版    本：1.0
  * 创建日期：2017/11/27 15:45
  * 描    述：班级小组列表界面
  * 修订历史：
  * ================================================
  */
public class ContactsClassGroupFragment extends ContactsListFragment {

    public static final String TAG = ContactsClassGroupFragment.class.getSimpleName();

    public interface Constants {
        /**
         * isHeadTeacher: 是否为班主任
         * isTeacher : 是否为老师
         */
        public static final String EXTRA_ISHEADTEACHER = "isHeadTeacher";
        public static final String EXTRA_ISTEACHER = "isTeacher";
        /**
         * 班级成员列表
         */
        public static final String EXTRA_CLASSMAILLISTDETAILLIST = "ClassMailListDetailList";
        /**
         * 小组Id
         */
        public static final String EXTRA_GROUPID = "groupId";

        /**
         * 小组名
         */
        public static final String EXTRA_GROUPNAME = "groupName";
    }

    /**
     * isHeadTeacher: 是否为班主任
     * isTeacher : 是否为老师
     */
    private boolean isHeadTeacher,isTeacher;
    private String schoolID;
    private String classID;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contacts_class_request_list, container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadGroupData();
    }


    private void init() {
        if (getArguments() != null) {
            isHeadTeacher = getArguments().getBoolean(Constants.EXTRA_ISHEADTEACHER);
            isTeacher = getArguments().getBoolean(Constants.EXTRA_ISTEACHER);
            schoolID = getArguments().getString(GroupContactsListFragment.Constants.EXTRA_CONTACTS_SCHOOL_ID);
            classID = getArguments().getString(GroupContactsListFragment.Constants.EXTRA_CONTACTS_CLASS_ID);
        }
        initViews();
    }

    private void initViews() {
        //标题
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setText(R.string.str_class_group);
        }

        //右边btn
        textView = ((TextView) findViewById(R.id.contacts_header_right_btn));
        if (textView != null) {
            textView.setText(R.string.str_establish_group);
            textView.setTextColor(getResources().getColor(R.color.text_green));
            if (isHeadTeacher || isTeacher) {
                textView.setVisibility(View.VISIBLE);
            } else {
                textView.setVisibility(View.GONE);
            }
            textView.setOnClickListener(this);
        }

        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);

        SlideListView listView = (SlideListView) findViewById(R.id.contacts_list_view);
        if (listView != null) {
            listView.setBackgroundColor(getResources().getColor(R.color.white));
            if (!isTeacher && !isHeadTeacher) {
                //学生隐藏删除功能
                listView.setSlideMode(SlideListView.SlideMode.NONE);
            }

            SlideListViewHelper listViewHelper = new SlideListViewHelper(getActivity(),
                    listView, R.layout.contacts_class_group, 0,
                    R.layout.contacts_slide_list_item_delete) {
                @Override
                public void loadData() {
                    loadGroupData();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    final CheckMarkInfo.ModelBean data =
                            (CheckMarkInfo.ModelBean) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;

                    TextView textView = (TextView) view.findViewById(R.id.tv_groupName);

                    if (textView != null) {
                        textView.setText(data.getGroupName());
                    }

                    textView = (TextView) view.findViewById(R.id.contacts_item_delete);
                    textView.setText(R.string.str_dissolution_group);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            if (isHeadTeacher || getMemeberId().equalsIgnoreCase(data.getCreateId())) {
                                dissolveGroup(data);
                            } else {
                                TipMsgHelper.ShowLMsg(getMyApplication(), getString(R.string.str_group_delete));
                                return;
                            }

                            v.setEnabled(false);
                            v.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    v.setEnabled(true);
                                }
                            }, 1000);
                        }
                    });
                    textView.setTag(holder);
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        return;
                    }
                    CheckMarkInfo.ModelBean data = (CheckMarkInfo.ModelBean) holder.data;
                    if (data != null) {
                        //打开组详情
                        establishGroup(false,data.getGroupId()+"",data.getGroupName(),data.getCreateId());
                    }

                }
            };
            setCurrAdapterViewHelper(listView, listViewHelper);
        }
    }

    private void loadGroupData() {

        Map<String, Object> params = new ArrayMap<>();
        params.put("SchoolId", schoolID);
        params.put("ClassId", classID);
        params.put("CreateId", getMemeberId());

        DefaultPullToRefreshListener listener =
                new DefaultPullToRefreshListener<ContactsClassRequestListResult>(
                        ContactsClassRequestListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        CheckMarkInfo result = JSONObject.parseObject(jsonString,
                                CheckMarkInfo.class);
                        if (result.getErrorCode() != 0 || result.getModel() == null) {
                            return;
                        }

                        getCurrAdapterViewHelper().setData(result.getModel());
                    }
                };
        postRequest(ServerUrl.GET_LOADSTUDYGROUPBYCLASSID, params, listener);
    }


    private void deleteGroup(final CheckMarkInfo.ModelBean data) {
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new ArrayMap<>();
        params.put("UpdateId", getUserInfo().getMemberId());
        params.put("GroupId", data.getGroupId());
        DefaultListener listener =
                new DefaultListener<ModelResult>(ModelResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                JSONObject jsonObject = (JSONObject) JSONObject.parse(jsonString);
                int errorCode = jsonObject.getInteger("ErrorCode");
                if (errorCode != 0) {
                    TipMsgHelper.ShowLMsg(getMyApplication(), R.string.delete_failure);
                    return;
                }
                getCurrAdapterViewHelper().getData().remove(data);
                getCurrAdapterViewHelper().update();
            }
        };
        listener.setShowLoading(true);
        postRequest(ServerUrl.GET_REMOVESTUDYGROUP, params, listener);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_right_btn) {
            //创建分组
            establishGroup(true,null,null,getMemeberId());

        }else if (v.getId() == R.id.contacts_header_left_btn) {
            popStack();
        }
    }

    /**
     *创建分组
     */
    private void establishGroup(boolean isPick,String groupId,String groupName,String createId) {
        ArrayList<ContactsClassMemberInfo> list =  getArguments().getParcelableArrayList(Constants.EXTRA_CLASSMAILLISTDETAILLIST);
        for (ContactsClassMemberInfo info : list) {
            info.setIsSelect(false);
        }
        AddGroupMembersFragment fragment = AddGroupMembersFragment.newInstance(isPick,list,groupId,schoolID,classID,groupName,isTeacher,createId,false,isHeadTeacher);
        fragment.setLoadDataListener(new AddGroupMembersFragment.LoadDataListener() {
            @Override
            public void loadData() {
                loadGroupData();
            }
        });
        getFragmentManager().beginTransaction()
                .add(R.id.contacts_layout,fragment, AddGroupMembersFragment.TAG)
                .hide(ContactsClassGroupFragment.this)
                .addToBackStack(AddGroupMembersFragment.TAG)
                .commit();
    }

    /**
     *
     * @param isHeadTeacher 是否是班主任
     * @param isTeacher  是否是老师
     * @param classMemberListResult  班级成员列表
     * @param schoolId
     *@param classId @return
     */
    public static ContactsClassGroupFragment newInstance(boolean isHeadTeacher, boolean isTeacher,
                                                         ContactsClassMemberListResult classMemberListResult, String schoolId, String classId) {
        
        Bundle args = new Bundle();

        args.putBoolean(Constants.EXTRA_ISHEADTEACHER,isHeadTeacher);
        args.putBoolean(Constants.EXTRA_ISTEACHER,isTeacher);
        args.putString(GroupContactsListFragment.Constants.EXTRA_CONTACTS_SCHOOL_ID,schoolId);
        args.putString(GroupContactsListFragment.Constants.EXTRA_CONTACTS_CLASS_ID,classId);
        args.putParcelableArrayList(Constants.EXTRA_CLASSMAILLISTDETAILLIST, (ArrayList<? extends Parcelable>) classMemberListResult.getModel().getClassMailListDetailList());

        ContactsClassGroupFragment fragment = new ContactsClassGroupFragment();
        fragment.setArguments(args);
        return fragment;
    }

     /**
      * 解散分组弹框
      */
     private void dissolveGroup(final CheckMarkInfo.ModelBean data) {
         ContactsMessageDialog messageDialog = new ContactsMessageDialog(getActivity(), null,
                 getString(R.string.str_dissolution_group_tips)
                 ,getString(R.string.cancel) ,
                 null, getString(R.string.confirm), new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 dialog.dismiss();
                 deleteGroup(data);
             }
         });
         messageDialog.show();
     }
}
