package com.lqwawa.intleducation.module.user.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
//import com.hyphenate.chat.EMClient;
//import com.hyphenate.chat.EMConversation;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseActivity;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.NoScrollGridView;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.ui.CustomDialog;
//import com.lqwawa.intleducation.module.chat.db.InviteMessgeDao;
//import com.lqwawa.intleducation.module.chat.ui.ChatActivity;
import com.lqwawa.intleducation.module.login.ui.LoginActivity;
import com.lqwawa.intleducation.module.user.adapter.ClassMemberAdapter;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.intleducation.module.user.vo.ContactVo;
import com.lqwawa.intleducation.module.user.vo.MyClassVo;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ClassMembersActivity extends MyBaseActivity implements View.OnClickListener {
    private static String TAG = "ClassMembersActivity";
    public static int Rc_ExitGroup = 1096;

    private TopBar topBar;
    private RelativeLayout loadFailedLayout;
    private Button btnReload;
    private PullToRefreshView pullToRefreshView;
    private NoScrollGridView gridViewTeacher;
    private NoScrollGridView gridViewStudent;
    private LinearLayout layTeacher;
    private LinearLayout layStudent;
    private ImageView igmArrowTeacher;
    private ImageView igmArrowStudent;
    private ScrollView scrollView;
    //发起群聊
    private Button buttonGroupChat;
    //退出班级
    private Button buttonExitClass;

    private ClassMemberAdapter teachersAdapter;
    private ClassMemberAdapter studentsAdapter;

    public static void start(Activity activity, String id, String name, String uuid, boolean isComeFromChat) {
        activity.startActivityForResult(new Intent(activity, ClassMembersActivity.class)
                .putExtra("id", id)
                .putExtra("name", name)
                .putExtra("uuid", uuid)
                .putExtra("isComeFromChat", isComeFromChat), Rc_ExitGroup);
    }

    public static void start(Activity activity, String uuid, boolean isComeFromChat) {
        activity.startActivityForResult(new Intent(activity, ClassMembersActivity.class)
                .putExtra("uuid", uuid)
                .putExtra("isComeFromChat", isComeFromChat), Rc_ExitGroup);
    }

    private String id;
    private String name;
    private String groupUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_members);

        topBar = (TopBar) findViewById(R.id.top_bar);
        loadFailedLayout = (RelativeLayout) findViewById(R.id.load_failed_layout);
        btnReload = (Button) findViewById(R.id.reload_bt);
        pullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        gridViewTeacher = (NoScrollGridView) findViewById(R.id.teacher_gv);
        gridViewStudent = (NoScrollGridView) findViewById(R.id.student_gv);
        layTeacher = (LinearLayout) findViewById(R.id.teacher_layout);
        layStudent = (LinearLayout) findViewById(R.id.student_layout);
        igmArrowTeacher = (ImageView) findViewById(R.id.teacher_sort_arrow);
        igmArrowStudent = (ImageView) findViewById(R.id.student_sort_arrow);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        buttonGroupChat = (Button) findViewById(R.id.group_chat_bt);
        buttonExitClass = (Button) findViewById(R.id.exit_class_bt);
        groupUuid = getIntent().getStringExtra("uuid");

        initViews();
    }

    private void initViews() {
        topBar.setBack(true);
        buttonGroupChat.setOnClickListener(this);
        buttonExitClass.setOnClickListener(this);
        layTeacher.setOnClickListener(this);
        layStudent.setOnClickListener(this);
        btnReload.setOnClickListener(this);
        topBar.setTitle(name);
        //初始化下拉刷新
        pullToRefreshView.setLoadMoreEnable(false);
        pullToRefreshView.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                getData();
            }
        });
        pullToRefreshView.setLastUpdated(new Date().toLocaleString());
        teachersAdapter = new ClassMemberAdapter(this);
        studentsAdapter = new ClassMemberAdapter(this);
        gridViewTeacher.setAdapter(teachersAdapter);
        gridViewStudent.setAdapter(studentsAdapter);

        gridViewStudent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactVo vo = (ContactVo) studentsAdapter.getItem(position);
                ContactDetailsActivity.start(activity, vo.getId() + "", false);
            }
        });
        gridViewTeacher.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactVo vo = (ContactVo) teachersAdapter.getItem(position);
                ContactDetailsActivity.start(activity, vo.getId() + "", false);
            }
        });

        id = getIntent().getStringExtra("id");
        if (id == null) {
            UserHelper.getGroupListByUuid(groupUuid, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String s) {
                    ResponseVo<List<MyClassVo>> result = JSON.parseObject(s,
                            new TypeReference<ResponseVo<List<MyClassVo>>>() {
                            });
                    if (result.getCode() == 0) {
                        List<MyClassVo> groupInfos = result.getData();
                        if (groupInfos != null && groupInfos.size() > 0) {
                            id = groupInfos.get(0).getId() + "";
                            name = groupInfos.get(0).getName();
                            topBar.setTitle(name);
                            pullToRefreshView.showRefresh();
                            getData();
                        }
                    } else {
                        pullToRefreshView.onHeaderRefreshComplete();
                        ToastUtil.showToast(activity, "获取组信息失败：" + result.getMessage());
                    }
                }

                @Override
                public void onError(Throwable throwable, boolean b) {
                    pullToRefreshView.onHeaderRefreshComplete();
                    ToastUtil.showToast(activity, getResources().getString(R.string.net_error_tip));
                }

                @Override
                public void onCancelled(CancelledException e) {

                }

                @Override
                public void onFinished() {

                }
            });
        } else {
            name = getIntent().getStringExtra("name");
            pullToRefreshView.showRefresh();
            getData();
        }
        if (getIntent().getBooleanExtra("isComeFromChat", false)) {
            buttonGroupChat.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.reload_bt) {
            pullToRefreshView.showRefresh();
            getData();
        }else if(view.getId() == R.id.teacher_layout) {
            if (gridViewTeacher.getVisibility() == View.VISIBLE) {
                gridViewTeacher.setVisibility(View.GONE);
                igmArrowTeacher.setImageDrawable(
                        getResources().getDrawable(R.drawable.arrow_down_gray_ico));
            } else {
                gridViewTeacher.setVisibility(View.VISIBLE);
                igmArrowTeacher.setImageDrawable(
                        getResources().getDrawable(R.drawable.arrow_up_gray_ico));
            }
            scrollView.invalidate();
        }else if (view.getId() == R.id.student_layout) {
            if (gridViewStudent.getVisibility() == View.VISIBLE) {
                gridViewStudent.setVisibility(View.GONE);
                igmArrowStudent.setImageDrawable(
                        getResources().getDrawable(R.drawable.arrow_down_gray_ico));
            } else {
                gridViewStudent.setVisibility(View.VISIBLE);
                igmArrowStudent.setImageDrawable(
                        getResources().getDrawable(R.drawable.arrow_up_gray_ico));
            }
            scrollView.invalidate();
        }else if(view.getId() == R.id.group_chat_bt) {//群聊
//            ChatActivity.start(activity, true, groupUuid, "", true);
        }else if(view.getId() == R.id.exit_class_bt) {
            exitClass();
        }
    }

    private void exitClass() {
        CustomDialog.Builder builder = new CustomDialog.Builder(activity);
        builder.setMessage(activity.getResources().getString(R.string.exit_class)
                + "?");
        builder.setTitle(activity.getResources().getString(R.string.tip));
        builder.setPositiveButton(activity.getResources().getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        doExitClass();
                    }
                });

        builder.setNegativeButton(activity.getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }

    private void doExitClass() {
        if (!UserHelper.isLogin()) {//未登录状态 跳转到登陆界面
            LoginActivity.loginForResult(this);
            return;
        }
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("groupId", id);
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.ExitGroup + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ResponseVo<String> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (result.getCode() == 0) {
                    ToastUtil.showToast(activity, activity.getResources().getString(R.string.exit_class)
                            + getResources().getString(R.string.success)
                            + "!");
//                    Map<String, EMConversation> newMsgs =
//                            EMClient.getInstance().chatManager().getAllConversations();
//                    for (String key : newMsgs.keySet()) {
//                        LogUtil.e("test", key + ":" + groupUuid);
//                        EMConversation conversation = newMsgs.get(key);
//                        if (key.equals(groupUuid)) {
//                            try {
//                                // delete conversation
//                                EMClient.getInstance().chatManager().deleteConversation(conversation.conversationId(), true);
//                                InviteMessgeDao dao = new InviteMessgeDao(activity);
//                                dao.deleteMessage(conversation.conversationId());
//                                sendBroadcast(new Intent().setAction(AppConfig.ServerUrl.ExitGroup));
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
                    setResult(Activity.RESULT_OK);
                    finish();
                } else {
                    ToastUtil.showToast(activity, activity.getResources().getString(R.string.exit_class)
                            + getResources().getString(R.string.failed)
                            + result.getMessage());
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "删除:" + throwable.getMessage());
                ToastUtil.showToast(activity, getResources().getString(R.string.net_error_tip));
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void getData() {
        loadFailedLayout.setVisibility(View.GONE);
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("classId", id);
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetClassMembersById
                        + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onHeaderRefreshComplete();
                ResponseVo<List<ContactVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<ContactVo>>>() {
                        });
                if (result.getCode() == 0) {
                    loadFailedLayout.setVisibility(View.GONE);
                    List<ContactVo> voList = result.getData();
                    studentsAdapter.setData(null);
                    teachersAdapter.setData(null);
                    if (voList != null && voList.size() > 0) {
                        for (int i = 0; i < voList.size(); i++) {
                            ContactVo vo = voList.get(i);
                            if (vo.getUserType() == 2) {
                                studentsAdapter.addData(vo);
                            } else {
                                teachersAdapter.addData(vo);
                            }
                        }
                        teachersAdapter.notifyDataSetChanged();
                        studentsAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "获取个人通讯录失败:" + throwable.getMessage());
                loadFailedLayout.setVisibility(View.VISIBLE);
                pullToRefreshView.onFooterRefreshComplete();
            }

            @Override
            public void onFinished() {
            }
        });
    }
}
