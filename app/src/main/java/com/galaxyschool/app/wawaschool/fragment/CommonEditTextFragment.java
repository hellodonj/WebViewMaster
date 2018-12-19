package com.galaxyschool.app.wawaschool.fragment;

import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;
import com.lqwawa.apps.views.ContainsEmojiEditText;

import java.util.Map;

 /**
  * ================================================
  * 作    者：Blizzard-liu
  * 版    本：1.0
  * 创建日期：2017/11/27 11:28
  * 描    述：修改小组名称  也可用于其他名字修改
  * 修订历史：
  * ================================================
  */
public class CommonEditTextFragment extends ContactsListFragment implements TextWatcher {

    public static final String TAG = CommonEditTextFragment.class.getSimpleName();

    public interface Constants {
        /**
         * 标题
         */
        public static final String EXTRA_TITLE = "title";
    }

    private ContainsEmojiEditText editText;
    private ToolbarTopView toolbarTopView;

    /**
     * 学校 班级 小组Id
     */
    private String schoolID;
    private String classID;
    /**
     * groupID: 小组Id
     * groupName : 小组名
     */
    private String groupID;
    private String groupName;
    private String title;
    private int maxLen = 20;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.common_edit_text_fragment, container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();
        initViews();
    }


    private void init() {
        if (getArguments() != null) {
            groupID = getArguments().getString(ContactsClassGroupFragment.Constants.EXTRA_GROUPID);
            groupName = getArguments().getString(ContactsClassGroupFragment.Constants.EXTRA_GROUPNAME);
            schoolID = getArguments().getString(GroupContactsListFragment.Constants.EXTRA_CONTACTS_SCHOOL_ID);
            classID = getArguments().getString(GroupContactsListFragment.Constants.EXTRA_CONTACTS_CLASS_ID);
            title = getArguments().getString(Constants.EXTRA_TITLE);
        }

    }

    private void initViews() {
        toolbarTopView = (ToolbarTopView) findViewById(R.id.toolbartopview);
        toolbarTopView.getBackView().setVisibility(View.VISIBLE);
        toolbarTopView.getCommitView().setVisibility(View.VISIBLE);
        toolbarTopView.getCommitView().setText(R.string.save);
        toolbarTopView.getCommitView().setTextColor(getResources().getColor(R.color.text_green));
        toolbarTopView.getTitleView().setText(title);
        toolbarTopView.getBackView().setOnClickListener(this);
        toolbarTopView.getCommitView().setOnClickListener(this);

        editText = (ContainsEmojiEditText) findViewById(R.id.common_content_edittext);
        editText.setText(groupName);
        editText.setSelection(groupName.length());
        //至多20个字符
        editText.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Editable editable = editText.getText();
        int len = editable.length();

        if (maxLen > 0 & len > maxLen) {
            int selEndIndex = Selection.getSelectionEnd(editable);
            String str = editable.toString();
            //截取新字符串
            String newStr = str.substring(0, maxLen);
            editText.setText(newStr);
            editable = editText.getText();

            //新字符串的长度
            int newLen = editable.length();
            //旧光标位置超过字符串长度
            if (selEndIndex > newLen) {
                selEndIndex = editable.length();
            }
            //设置新光标所在的位置
            Selection.setSelection(editable, selEndIndex);
            TipMsgHelper.ShowLMsg(getActivity(),
                    getString(R.string.max_input_character_length,maxLen));
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }




    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.toolbar_top_back_btn) {
            //返回
            hideSoftKeyboard(getActivity());
           popStack();

        }else if (v.getId() == R.id.toolbar_top_commit_btn) {
            groupRename(editText.getText().toString());
        }
    }

    /**
     * 修改小组名称
     */
    private void groupRename(final String groupName) {
        if (TextUtils.isEmpty(groupName)) {
            TipMsgHelper.ShowMsg(getMyApplication(), getString(R.string.pls_input_groupname));
            return;
        }
        if (this.groupName.equalsIgnoreCase(groupName)) {
            TipMsgHelper.ShowMsg(getMyApplication(), getString(R.string.str_rename_group_error_tips));
            return;
        }

        Map<String, Object> params = new ArrayMap<>();
        params.put("SchoolId",schoolID );
        params.put("ClassId",classID);
        params.put("UpdateId",getMemeberId());
        params.put("GroupName", groupName);
        params.put("GroupId",groupID);

        DefaultListener listener=
                new DefaultListener<ModelResult>(
                        ModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        com.alibaba.fastjson.JSONObject jsonObject = (com.alibaba.fastjson.JSONObject) com.alibaba.fastjson.JSONObject.parse(jsonString);
                        int errorCode = jsonObject.getInteger("ErrorCode");
                        if (errorCode == 0) {
                            //更新组名 标题栏
                            CommonEditTextFragment.this.groupName = groupName;
                            if (mLoadDataListener != null) {
                                mLoadDataListener.loadData(groupName);
                            }
                            TipMsgHelper.ShowLMsg(getMyApplication(), R.string.modify_success);
                            popStack();
                        } else {
                            TipMsgHelper.ShowLMsg(getMyApplication(), getResult().getErrorMessage());
                        }
                    }
                };
        listener.setShowLoading(true);
        postRequest(ServerUrl.GET_CHANGESTUDYGROUPNAME, params, listener);

    }



    /**
     *
     * @param schoolId
     *@param classId @return
     */
    public static CommonEditTextFragment newInstance( String schoolId, String classId, String groupId,String groupName,String title) {
        
        Bundle args = new Bundle();

        args.putString(GroupContactsListFragment.Constants.EXTRA_CONTACTS_SCHOOL_ID,schoolId);
        args.putString(GroupContactsListFragment.Constants.EXTRA_CONTACTS_CLASS_ID,classId);
        args.putString(ContactsClassGroupFragment.Constants.EXTRA_GROUPID,groupId);
        args.putString(ContactsClassGroupFragment.Constants.EXTRA_GROUPNAME,groupName);
        args.putString(Constants.EXTRA_TITLE,title);

        CommonEditTextFragment fragment = new CommonEditTextFragment();
        fragment.setArguments(args);
        return fragment;
    }

    interface LoadDataListener {
        void loadData(String groupName);
    }

    private LoadDataListener mLoadDataListener;

    public void setLoadDataListener(LoadDataListener listener) {
        mLoadDataListener = listener;
    }
}
