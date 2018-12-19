package com.galaxyschool.app.wawaschool;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.common.DialogHelper;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.lqwawa.lqbaselib.net.NetErrorResult;
import com.lqwawa.lqbaselib.net.Netroid;
import com.lqwawa.lqbaselib.net.PostByMapParamsModelRequest;
import com.galaxyschool.app.wawaschool.pojo.*;
import com.galaxyschool.app.wawaschool.views.SingleChoiceDialog;
import com.galaxyschool.app.wawaschool.views.SingleChoiceDialog.ChoiceItemData;
import com.galaxyschool.app.wawaschool.views.SingleChoiceDialog.ConfirmCallback;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoleInfoActivity extends BaseActivity implements View.OnClickListener {
    private ToolbarTopView toolbarTopView;
    private LinearLayout subLayout0, subLayout1;
    private TextView roleTxt, schoolTxt, gradeTxt, classTxt;
    private DialogHelper.LoadingDialog loadingDialog;

    private SingleChoiceDialog roleListDialog;
    private SingleChoiceDialog schoolListDialog;
    private SingleChoiceDialog gradeListDialog;
    private SingleChoiceDialog classListDialog;

    private String roleId;
    private int roleType;
    private RoleDetail roleDetail;
    private String memberId;

    private ArrayList<ChoiceItemData> roleItemDataList = new ArrayList<ChoiceItemData>();
    private ArrayList<ChoiceItemData> schoolItemDataList = new ArrayList<ChoiceItemData>();
    private ArrayList<ChoiceItemData> gradeItemDataList = new ArrayList<ChoiceItemData>();
    private ArrayList<ChoiceItemData> classItemDataList = new ArrayList<ChoiceItemData>();

    private ChoiceItemData roleItemData;
    private ChoiceItemData schoolItemData;
    private ChoiceItemData gradeItemData;
    private ChoiceItemData classItemData;

    private int roleSelectPosition = 0;
    private int schoolSelectPosition = 0;
    private int gradeSelectPosition = 0;
    private int classSelectPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roleinfo);
        initViews();
        initData();
        initRoleItemDataList();
    }

    private void initViews() {
        toolbarTopView = (ToolbarTopView) findViewById(R.id.toolbartopview);
        toolbarTopView.getBackView().setVisibility(View.VISIBLE);
        toolbarTopView.getCommitView().setVisibility(View.VISIBLE);
        toolbarTopView.getCommitView().setText(R.string.save);
        toolbarTopView.getTitleView().setText(R.string.role_info);
        toolbarTopView.getCommitView().setBackgroundResource(R.drawable.sel_nav_button_bg);
        toolbarTopView.getBackView().setOnClickListener(this);
        toolbarTopView.getCommitView().setOnClickListener(this);

        subLayout0 = (LinearLayout) findViewById(R.id.activity_roleinfo_sub_layout0);
        subLayout1 = (LinearLayout) findViewById(R.id.activity_roleinfo_sub_layout1);
        roleTxt = (TextView) findViewById(R.id.activity_roleinfo_role_txt);
        schoolTxt = (TextView) findViewById(R.id.activity_roleinfo_school_txt);
        gradeTxt = (TextView) findViewById(R.id.activity_roleinfo_grade_txt);
        classTxt = (TextView) findViewById(R.id.activity_roleinfo_class_txt);
        roleTxt.setOnClickListener(this);
        schoolTxt.setOnClickListener(this);
        gradeTxt.setOnClickListener(this);
        classTxt.setOnClickListener(this);
    }

    private void switchView(int roleType) {
        if (roleType >= 0) {
            subLayout0.setVisibility(View.VISIBLE);
            subLayout1.setVisibility(View.VISIBLE);
            switch (roleType) {
                case RoleType.ROLE_TYPE_TEACHER:
                    subLayout1.setVisibility(View.GONE);
                    roleTxt.setText(R.string.teacher);
                    break;
                case RoleType.ROLE_TYPE_STUDENT:
                    roleTxt.setText(R.string.student);
                    break;
                case RoleType.ROLE_TYPE_PARENT:
                    subLayout0.setVisibility(View.GONE);
                    roleTxt.setText(R.string.parent);
                    break;
            }
        }
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            roleId = bundle.getString("roleId");
            roleType = bundle.getInt("roleType");
            if (!TextUtils.isEmpty(roleId)) {
                roleTxt.setEnabled(false);
                schoolTxt.setEnabled(false);
                gradeTxt.setEnabled(false);
                classTxt.setEnabled(false);
                toolbarTopView.getCommitView().setVisibility(View.INVISIBLE);
                loadRoleInfo(roleType, roleId);
                switchView(roleType);
            } else {
                roleTxt.setEnabled(true);
                schoolTxt.setEnabled(true);
                gradeTxt.setEnabled(true);
                classTxt.setEnabled(true);
                toolbarTopView.getCommitView().setVisibility(View.VISIBLE);
                loadSchoolInfo();
            }
        }

        memberId = ((MyApplication)getApplication()).getMemberId();
    }

    private void initRoleItemDataList() {
        roleItemDataList.clear();
        ChoiceItemData choiceItemData = new ChoiceItemData();
        choiceItemData.setmText(getString(R.string.student));
        choiceItemData.setId("" + RoleType.ROLE_TYPE_STUDENT);
        roleItemDataList.add(choiceItemData);
        choiceItemData = new ChoiceItemData();
        choiceItemData.setmText(getString(R.string.teacher));
        choiceItemData.setId("" + RoleType.ROLE_TYPE_TEACHER);
        roleItemDataList.add(choiceItemData);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_top_back_btn:
                finish();
                break;
            case R.id.toolbar_top_commit_btn:
                saveRoleInfo();
                break;
            case R.id.activity_roleinfo_role_txt:
                showRoleListDialog();
                break;
            case R.id.activity_roleinfo_school_txt:
                showSchoolListDialog();
                break;
            case R.id.activity_roleinfo_grade_txt:
                if(schoolItemData != null && !TextUtils.isEmpty(schoolItemData.getId())) {
                    loadGradeInfo(schoolItemData.getId());
                } else {
                    TipMsgHelper.ShowMsg(RoleInfoActivity.this, getString(R.string.no_school));
                    return;
                }
                break;
            case R.id.activity_roleinfo_class_txt:
                if(schoolItemData!= null && !TextUtils.isEmpty(schoolItemData.getId()) && gradeItemData != null &&
                    !TextUtils.isEmpty(gradeItemData.getId())) {
                    loadClassInfo(schoolItemData.getId(), gradeItemData.getId());
                } else {
                    if(schoolItemData == null) {
                        TipMsgHelper.ShowMsg(RoleInfoActivity.this, getString(R.string.no_school));
                        return;
                    }
                    if(gradeItemData == null) {
                        TipMsgHelper.ShowMsg(RoleInfoActivity.this, getString(R.string.no_grade));
                        return;
                    }
                }
                break;
        }
    }

    private void loadRoleInfo(int roleType, String roleId) {
        if (roleType >= 0) {
            String url = null;
            if (roleType == RoleType.ROLE_TYPE_STUDENT) {
                url = ServerUrl.LOAD_STUDENT_ROLEINFO_URL;
            } else if (roleType == RoleType.ROLE_TYPE_TEACHER) {
                url = ServerUrl.LOAD_TEACHER_ROLEINFO_URL;
            }
            if (!TextUtils.isEmpty(url)) {
                Map<String, String> mParams = new HashMap<String, String>();
                mParams.put("Id", roleId);
                PostByMapParamsModelRequest request = new PostByMapParamsModelRequest(
                    url, mParams, new Listener<String>() {
                    @Override
                    public void onSuccess(String json) {
                        loadingDialog.dismiss();
                        try {
                            Log.i("", "Login:onSuccess " + json);
                            roleDetail = JSON.parseObject(json, RoleDetail.class);
                            if (roleDetail != null) {
                                updateRoleInfo();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(NetroidError error) {
                        super.onError(error);
                        loadingDialog.dismiss();
                        String es = error.getMessage();
                        try {
                            NetErrorResult result = JSON.parseObject(es, NetErrorResult.class);
                            if (result != null) {
                                if (result.isHasError()) {
                                    TipMsgHelper.ShowMsg(RoleInfoActivity.this, result.getErrorMessage());
                                }
                            }
                        } catch (Exception e) {
                            TipMsgHelper.ShowLMsg(RoleInfoActivity.this, getString(R.string.network_error));
                        }
                    }
                });
                request.addHeader("Accept-Encoding", "*");
                request.start(RoleInfoActivity.this);
                loadingDialog = DialogHelper.getIt(RoleInfoActivity.this).GetLoadingDialog(0);
            }
        }
    }

    private void updateRoleInfo() {
        if (roleDetail != null) {
            schoolTxt.setText(roleDetail.getSchoolName());
            gradeTxt.setText(roleDetail.getGradeName());
            classTxt.setText(roleDetail.getClassName());
        }
    }

    private void loadSchoolInfo() {
        String url = ServerUrl.LOAD_ALL_SCHOOLLIST_URL;
        if (!TextUtils.isEmpty(url)) {
            Map<String, String> mParams = new HashMap<String, String>();
            PostByMapParamsModelRequest request = new PostByMapParamsModelRequest(
                url, mParams, new Listener<String>() {
                @Override
                public void onSuccess(String json) {
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        if(jsonObject != null) {
                            JSONArray jsonArray = jsonObject.optJSONArray("SchoolList");
                            if(jsonArray != null) {
                                List<SchoolInfo> schoolInfoList = JSON.parseArray(jsonArray.toString(),SchoolInfo
                                        .class);
                                if(schoolInfoList != null && schoolInfoList.size() > 0) {
                                    schoolItemDataList.clear();
                                    for(SchoolInfo schoolInfo : schoolInfoList) {
                                        if(schoolInfo != null) {
                                            ChoiceItemData itemData = new ChoiceItemData();
                                            itemData.setId(schoolInfo.getSchoolId());
                                            itemData.setmText(schoolInfo.getSchoolName());
                                            if(!TextUtils.isEmpty(schoolInfo.getSchoolLogo())) {
                                                itemData.setmLogoUrl(schoolInfo.getSchoolLogo());
                                            } else {
                                                itemData.setmLogoUrl("schoolLogo");
                                            }
                                            schoolItemDataList.add(itemData);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(NetroidError error) {
                    super.onError(error);
                    String es = error.getMessage();
                    try {
                        NetErrorResult result = JSON.parseObject(es, NetErrorResult.class);
                        if (result != null) {
                            if (result.isHasError()) {
                                TipMsgHelper.ShowMsg(RoleInfoActivity.this, result.getErrorMessage());
                            }
                        }
                    } catch (Exception e) {
                        TipMsgHelper.ShowLMsg(RoleInfoActivity.this, getString(R.string.network_error));
                    }
                }
            });
            request.addHeader("Accept-Encoding", "*");
            request.start(RoleInfoActivity.this);
        }
    }

    private void loadGradeInfo(final String schoolId) {
        String url = ServerUrl.LOAD_GRADELIST_URL;
        if (!TextUtils.isEmpty(url)) {
            Map<String, String> mParams = new HashMap<String, String>();
            mParams.put("SchoolId", schoolId);
            PostByMapParamsModelRequest request = new PostByMapParamsModelRequest(
                url, mParams, new Listener<String>() {
                @Override
                public void onSuccess(String json) {
                    loadingDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        if(jsonObject != null) {
                            JSONArray jsonArray = jsonObject.optJSONArray("GradeList");
                            String tempSchooId = jsonObject.optString("SchoolId");
                            if(!TextUtils.isEmpty(tempSchooId) && tempSchooId.equals(schoolId)) {
                                if(jsonArray != null) {
                                    List<GradeInfo> gradeInfoList = JSON.parseArray(jsonArray.toString(),GradeInfo.class);
                                    if(gradeInfoList != null && gradeInfoList.size() > 0) {
                                        gradeItemDataList.clear();
                                        for(GradeInfo gradeInfo : gradeInfoList) {
                                            if(gradeInfo != null) {
                                                ChoiceItemData itemData = new ChoiceItemData();
                                                itemData.setId(gradeInfo.getId());
                                                itemData.setmText(gradeInfo.getGradeName());
                                                gradeItemDataList.add(itemData);
                                            }
                                        }
                                    }
                                    if(gradeItemDataList.size() > 0) {
                                        showGradeListDialog();
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(NetroidError error) {
                    super.onError(error);
                    loadingDialog.dismiss();
                    String es = error.getMessage();
                    try {
                        NetErrorResult result = JSON.parseObject(es, NetErrorResult.class);
                        if (result != null) {
                            if (result.isHasError()) {
                                TipMsgHelper.ShowMsg(RoleInfoActivity.this, result.getErrorMessage());
                            }
                        }
                    } catch (Exception e) {
                        TipMsgHelper.ShowLMsg(RoleInfoActivity.this, getString(R.string.network_error));
                    }
                }
            });
            request.addHeader("Accept-Encoding", "*");
            request.start(RoleInfoActivity.this);
            loadingDialog = DialogHelper.getIt(RoleInfoActivity.this).GetLoadingDialog(0);
        }
    }

    private void loadClassInfo(final String schoolId, final String gradeId) {
        String url = ServerUrl.LOAD_CLASSLIST_URL;
        if (!TextUtils.isEmpty(url)) {
            Map<String, String> mParams = new HashMap<String, String>();
            mParams.put("SchoolId", schoolId);
            mParams.put("GradeId", gradeId);
            PostByMapParamsModelRequest request = new PostByMapParamsModelRequest(
                url, mParams, new Listener<String>() {
                @Override
                public void onSuccess(String json) {
                    loadingDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        if(jsonObject != null) {
                            JSONArray jsonArray = jsonObject.optJSONArray("ClassList");
                            String tempSchoolId = jsonObject.optString("SchoolId");
                            String tempGradeId = jsonObject.optString("GradeId");
                            if(!TextUtils.isEmpty(tempSchoolId) && tempSchoolId.equals(schoolId) &&
                                !TextUtils.isEmpty(tempGradeId) && tempGradeId.equals(gradeId)) {
                                if(jsonArray != null) {
                                    List<ClassInfo> classInfoList = JSON.parseArray(jsonArray.toString(), ClassInfo
                                            .class);
                                    if(classInfoList != null && classInfoList.size() > 0) {
                                        classItemDataList.clear();
                                        for(ClassInfo classInfo : classInfoList) {
                                            if(classInfo != null) {
                                                ChoiceItemData itemData = new ChoiceItemData();
                                                itemData.setId(classInfo.getId());
                                                itemData.setmText(classInfo.getName());
                                                classItemDataList.add(itemData);
                                            }
                                        }
                                    }
                                    if(classItemDataList.size() > 0) {
                                        showClassListDialog();
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(NetroidError error) {
                    loadingDialog.dismiss();
                    super.onError(error);
                    String es = error.getMessage();
                    try {
                        NetErrorResult result = JSON.parseObject(es, NetErrorResult.class);
                        if (result != null) {
                            if (result.isHasError()) {
                                TipMsgHelper.ShowMsg(RoleInfoActivity.this, result.getErrorMessage());
                            }
                        }
                    } catch (Exception e) {
                        TipMsgHelper.ShowLMsg(RoleInfoActivity.this, getString(R.string.network_error));
                    }
                }
            });
            request.addHeader("Accept-Encoding", "*");
            request.start(RoleInfoActivity.this);
            loadingDialog = DialogHelper.getIt(RoleInfoActivity.this).GetLoadingDialog(0);
        }
    }

    private void showRoleListDialog() {
        if(roleListDialog != null) {
            roleListDialog.dismiss();
            roleListDialog = null;
        }
        if(roleListDialog == null) {
            roleListDialog = new SingleChoiceDialog(RoleInfoActivity.this, getString(R.string.role),
                roleItemDataList, roleSelectPosition, roleConfirmCallBack);
            roleListDialog.show();
        }
    }

    private void showSchoolListDialog() {
        if(schoolListDialog != null) {
            schoolListDialog.dismiss();
            schoolListDialog = null;
        }
        if(schoolListDialog == null) {
            schoolListDialog = new SingleChoiceDialog(RoleInfoActivity.this, getString(R.string.school),
                schoolItemDataList, schoolSelectPosition, schoolConfirmCallBack);
            schoolListDialog.show();
        }
    }

    private void showGradeListDialog() {
        if(gradeListDialog != null) {
            gradeListDialog.dismiss();
            gradeListDialog = null;
        }
        if(gradeListDialog == null) {
            gradeListDialog = new SingleChoiceDialog(RoleInfoActivity.this, getString(R.string.grade),
                gradeItemDataList, gradeSelectPosition, gradeConfirmCallBack);
            gradeListDialog.show();
        }
    }

    private void showClassListDialog() {
        if(classListDialog != null) {
            classListDialog.dismiss();
            classListDialog = null;
        }
        if(classListDialog == null) {
            classListDialog = new SingleChoiceDialog(RoleInfoActivity.this, getString(R.string.classes),
                classItemDataList, classSelectPosition, classConfirmCallBack);
            classListDialog.show();
        }
    }


    private ConfirmCallback roleConfirmCallBack = new ConfirmCallback() {
        @Override
        public void onConfirm(
            Dialog dialog, int selectPosition, ChoiceItemData selectData) {
            if(roleListDialog != null) {
                roleListDialog = null;
            }
            roleItemData = selectData;
            roleSelectPosition = selectPosition;
            if(roleItemData != null) {
                roleTxt.setText(roleItemData.getmText());
                roleType = Integer.parseInt(roleItemData.getId());
                if(roleType == RoleType.ROLE_TYPE_STUDENT) {
                    subLayout1.setVisibility(View.VISIBLE);
                    schoolTxt.setText("");
                    schoolSelectPosition = 0;
                    schoolItemData = null;
                    gradeTxt.setText("");
                    gradeSelectPosition = 0;
                    gradeItemData = null;
                    classTxt.setText("");
                    classSelectPosition = 0;
                    classItemData = null;
                } else {
                    subLayout1.setVisibility(View.GONE);
                    schoolTxt.setText("");
                    schoolSelectPosition = 0;
                    schoolItemData = null;
                }
            }
        }
    };

    private ConfirmCallback schoolConfirmCallBack = new ConfirmCallback() {
        @Override
        public void onConfirm(
            Dialog dialog, int selectPosition, ChoiceItemData selectData) {
            if(schoolListDialog != null) {
                schoolListDialog = null;
            }
            schoolItemData = selectData;
            schoolSelectPosition = selectPosition;
            if(schoolItemData != null) {
                schoolTxt.setText(schoolItemData.getmText());

                gradeTxt.setText("");
                gradeSelectPosition = 0;
                gradeItemData = null;
                classTxt.setText("");
                classSelectPosition = 0;
                classItemData = null;
            }
        }
    };

    private ConfirmCallback gradeConfirmCallBack = new ConfirmCallback() {
        @Override
        public void onConfirm(
            Dialog dialog, int selectPosition, ChoiceItemData selectData) {
            if(gradeListDialog != null) {
                gradeListDialog = null;
            }
            gradeItemData = selectData;
            gradeSelectPosition = selectPosition;
            if(gradeItemData != null) {
                gradeTxt.setText(gradeItemData.getmText());
                classTxt.setText("");
                classSelectPosition = 0;
                classItemData = null;
            }
        }
    };

    private ConfirmCallback classConfirmCallBack = new ConfirmCallback() {
        @Override
        public void onConfirm(
            Dialog dialog, int selectPosition, ChoiceItemData selectData) {
            if(classListDialog != null) {
                classListDialog = null;
            }
            classItemData = selectData;
            classSelectPosition = selectPosition;
            if(classItemData != null) {
                classTxt.setText(classItemData.getmText());
            }
        }
    };

    private void saveRoleInfo() {
        if(roleItemData == null) {
            TipMsgHelper.ShowMsg(RoleInfoActivity.this, getString(R.string.no_role));
            return;
        }


        if(schoolItemData == null) {
            TipMsgHelper.ShowMsg(RoleInfoActivity.this, getString(R.string.no_school));
            return;
        }

        if(roleType == RoleType.ROLE_TYPE_STUDENT) {
            if (gradeItemData == null) {
                TipMsgHelper.ShowMsg(RoleInfoActivity.this, getString(R.string.no_grade));
                return;
            }

            if (classItemData == null) {
                TipMsgHelper.ShowMsg(RoleInfoActivity.this, getString(R.string.no_class));
                return;
            }
        }

        saveRoleInfo(roleType);
    }

    private void saveRoleInfo(final int roleType) {
        String url;
        if (roleType == RoleType.ROLE_TYPE_STUDENT) {
            url = ServerUrl.SAVE_STUDENT_ROLEINFO_URL;
        } else {
            url = ServerUrl.SAVE_TEACHER_ROLEINFO_URL;
        }

        if (!TextUtils.isEmpty(url)) {
            Map<String, String> mParams = new HashMap<String, String>();
            mParams.put("MemberId", memberId);
            mParams.put("SchoolId", schoolItemData.getId());
            mParams.put("SchoolName", schoolItemData.getmText());
            if (roleType == RoleType.ROLE_TYPE_STUDENT) {
                mParams.put("GradeId", gradeItemData.getId());
                mParams.put("GradeName", gradeItemData.getmText());
                mParams.put("ClassId", classItemData.getId());
                mParams.put("ClassName", classItemData.getmText());
            }
            PostByMapParamsModelRequest request = new PostByMapParamsModelRequest(
                url, mParams, new Listener<String>() {
                @Override
                public void onSuccess(String json) {
                    loadingDialog.dismiss();
                    try {
                        RoleDetail roleDetail = JSON.parseObject(json, RoleDetail.class);
                        if(roleDetail != null) {
                            Intent intent = new Intent();
                            RoleInfo roleInfo = new RoleInfo();
                            roleInfo.setId(roleDetail.getId());
                            roleInfo.setApplyName(roleDetail.getSchoolName());
                            roleInfo.setState(roleDetail.getCheckState());
                            roleInfo.setRoleType(roleType);
                            if(roleType == RoleType.ROLE_TYPE_STUDENT) {
                                roleInfo.setRole(getString(R.string.student));
                            } else {
                                roleInfo.setRole(getString(R.string.teacher));
                            }
                            intent.putExtra("roleInfo", roleInfo);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(NetroidError error) {
                    super.onError(error);
                    loadingDialog.dismiss();
                    String es = error.getMessage();
                    try {
                        NetErrorResult result = JSON.parseObject(es, NetErrorResult.class);
                        if (result != null) {
                            if (result.isHasError()) {
                                TipMsgHelper.ShowMsg(RoleInfoActivity.this, result.getErrorMessage());
                            }
                        }
                    } catch (Exception e) {
                        TipMsgHelper.ShowLMsg(RoleInfoActivity.this, getString(R.string.network_error));
                    }
                }
            });
            request.addHeader("Accept-Encoding", "*");
            request.start(RoleInfoActivity.this);
            loadingDialog = DialogHelper.getIt(RoleInfoActivity.this).GetLoadingDialog(0);
        }
    }

}
