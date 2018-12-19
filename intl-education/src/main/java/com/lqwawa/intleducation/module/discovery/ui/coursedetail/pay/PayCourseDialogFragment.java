package com.lqwawa.intleducation.module.discovery.ui.coursedetail.pay;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterDialogFragment;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.user.UserEntity;
import com.lqwawa.intleducation.factory.data.model.user.UserModel;
import com.lqwawa.intleducation.factory.helper.UserHelper;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.learn.vo.ChildrenListVo;

import java.util.ArrayList;
import java.util.List;

/**
 * @desc 启动购买页面的Fragment
 * @author mrmedici
 */
public class PayCourseDialogFragment extends PresenterDialogFragment<PayCourseContract.Presenter>
        implements PayCourseContract.View,View.OnClickListener{

    // 0 课程
    public static final int TYPE_COURSE = 0;
    // 3 课堂
    public static final int TYPE_CLASS = 3;

    // 是否是家长身份
    private static final String KEY_EXTRA_IS_PARENT = "KEY_EXTRA_IS_PARENT";
    // 是否直接帮孩子买课
    private static final String KEY_EXTRA_PARENT_ENTER = "KEY_EXTRA_PARENT_ENTER";
    // 孩子的memberId 此时确定从我的学程孩子的学程进来,帮孩子买课
    private static final String KEY_EXTRA_CHILD_MEMBER_ID = "KEY_EXTRA_CHILD_MEMBER_ID";
    // 课程或者课堂的Id
    private static final String KEY_EXTRA_COURSE_ID = "KEY_EXTRA_COURSE_ID";
    // 课程还是课堂 0课程 3在线课堂
    private static final String KEY_EXTRA_COURSE_TYPE = "KEY_EXTRA_COURSE_TYPE";
    // 购买课程的CourseVo
    private static final String KEY_EXTRA_COURSE_OBJECT = "KEY_EXTRA_COURSE_OBJECT";
    // 在线课堂老师的Ids
    private static final String KEY_EXTRA_CLASS_TEACHER_IDS = "KEY_EXTRA_CLASS_TEACHER_IDS";


    private View mRootView;
    private TextView mDialogDesc;
    private RecyclerView mRecycler;
    private MemberChoiceAdapter mMemberAdapter;

    private LinearLayout mChildLayout;
    private TextView mChildName,mChildAccount;
    private LinearLayout mMoreLayout;
    private LinearLayout mOtherLayout;
    private LinearLayout mChildContainer;
    private EditText mInputName;
    private Button mBtnWatchName;
    private TextView mQueryName;
    private CourseVo mCourseVo;
    private String mTeacherIds;

    // 确定
    private Button mBtnConfirm,mBtnCancel;

    private boolean mParent;
    private boolean mParentEnter;
    private String mChildMemberId;
    private int mCourseId;
    private int mType;

    private PayDialogNavigator mNavigator;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getContext(),R.style.AppTheme_Dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    protected PayCourseContract.Presenter initPresenter() {
        return new PayCoursePresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = mRootView = inflater.inflate(R.layout.dialog_pay_course,null);
        mDialogDesc = (TextView) view.findViewById(R.id.dialog_desc);
        mRecycler = (RecyclerView) view.findViewById(R.id.recycler);
        mChildLayout = (LinearLayout) view.findViewById(R.id.child_layout);
        mChildName = (TextView) view.findViewById(R.id.tv_child_name);
        mChildAccount = (TextView) view.findViewById(R.id.tv_child_account);
        mMoreLayout = (LinearLayout) view.findViewById(R.id.more_layout);
        mOtherLayout = (LinearLayout) view.findViewById(R.id.other_layout);
        mChildContainer = (LinearLayout) view.findViewById(R.id.child_container);
        mInputName = (EditText) view.findViewById(R.id.et_nick_name);
        mBtnWatchName = (Button) view.findViewById(R.id.btn_watch_name);
        mQueryName = (TextView) view.findViewById(R.id.tv_query_name);
        mBtnConfirm = (Button) view.findViewById(R.id.btn_confirm);
        mBtnCancel = (Button) view.findViewById(R.id.btn_cancel);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 界面显示
        LinearLayoutManager layoutManager = new LinearLayoutManager(UIUtil.getContext());
        mRecycler.setLayoutManager(layoutManager);

        mMemberAdapter = new MemberChoiceAdapter();
        mRecycler.setAdapter(mMemberAdapter);

        mBtnWatchName.setOnClickListener(this);
        mBtnConfirm.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);

        mMemberAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<ChildrenListVo>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, ChildrenListVo childrenListVo) {
                super.onItemClick(holder, childrenListVo);
                clearAll();
                childrenListVo.setChoice(!childrenListVo.isChoice());
                if(childrenListVo.isOtherMember() &&
                        TextUtils.equals(childrenListVo.getNickname(),UIUtil.getString(R.string.label_other_member))){
                    // 其它人
                    if(childrenListVo.isChoice()){
                        // 选中
                        mOtherLayout.setVisibility(View.VISIBLE);
                    }else{
                        // 未选中
                        mOtherLayout.setVisibility(View.GONE);
                    }
                }else{
                    // 未选中
                    mOtherLayout.setVisibility(View.GONE);
                }

                mMemberAdapter.notifyDataSetChanged();
            }
        });

        initData();

        if(mParent && mParentEnter){
            // 设置家长入口显示
            mChildLayout.setVisibility(View.VISIBLE);
            mMoreLayout.setVisibility(View.GONE);
            // 如果即是家长,又是从家长入口进入
            StringUtil.fillSafeTextView(mDialogDesc,UIUtil.getString(R.string.label_parent_pay_warning));
            // 请求指定的孩子身份
            final String userId = mChildMemberId;
            mPresenter.requestUserInfoWithUserId(userId);
        }else{
            // 设置家长入口隐藏
            mChildLayout.setVisibility(View.GONE);
            mMoreLayout.setVisibility(View.VISIBLE);
            // 请求所有的孩子
            mPresenter.requestParentChildData();
        }
    }

    private void initData(){
        Bundle arguments = getArguments();
        mParent = arguments.getBoolean(KEY_EXTRA_IS_PARENT);
        mParentEnter = arguments.getBoolean(KEY_EXTRA_PARENT_ENTER);
        mChildMemberId = arguments.getString(KEY_EXTRA_CHILD_MEMBER_ID);
        mCourseId = arguments.getInt(KEY_EXTRA_COURSE_ID);
        mType = arguments.getInt(KEY_EXTRA_COURSE_TYPE);
        mCourseVo = (CourseVo) arguments.getSerializable(KEY_EXTRA_COURSE_OBJECT);
        mTeacherIds = arguments.getString(KEY_EXTRA_CLASS_TEACHER_IDS);
    }

    /**
     * 清除所有的选择
     */
    private void clearAll(){
        List<ChildrenListVo> items = mMemberAdapter.getItems();
        if(EmptyUtil.isEmpty(items)) return;
        for (ChildrenListVo vo:items) {
            vo.setChoice(false);
        }
    }

    @Override
    public void updateParentChildView(List<ChildrenListVo> vos) {
        if(EmptyUtil.isEmpty(vos)){
            vos = new ArrayList<>();
        }

        if(EmptyUtil.isNotEmpty(vos)){
            // 不是从孩子课程入口进入
            if(!mParentEnter){
                StringUtil.fillSafeTextView(mDialogDesc,UIUtil.getString(R.string.label_parent_other_warning));
            }
        }else{
            StringUtil.fillSafeTextView(mDialogDesc,UIUtil.getString(R.string.label_pay_other_warning));
        }

        // 前面加上我自己
        vos.add(0,ChildrenListVo.buildVo(UIUtil.getString(R.string.label_self_member),true));
        // 后面加上其它人
        vos.add(ChildrenListVo.buildVo(UIUtil.getString(R.string.label_other_member),false));
        mMemberAdapter.replace(vos);
    }

    @Override
    public void updateUserInfoWithUserIdView(@NonNull UserEntity entity) {
        // 获取到对应孩子的信息
        StringUtil.fillSafeTextView(mChildName,entity.getRealName());
        StringUtil.fillSafeTextView(mChildAccount,entity.getNickName());
    }

    @Override
    public void updateUserInfoWithMembersView(List<UserEntity> entities) {
        mBtnWatchName.setEnabled(true);
        if(EmptyUtil.isNotEmpty(entities)){
            // 获取到用户信息
            UserEntity entity = entities.get(0);
            if(entity.isIsExist()){
                // 获取到对应实体的信息，就set进去
                mChildContainer.setTag(entity);
                StringUtil.fillSafeTextView(mQueryName,entity.getRealName());
                if(EmptyUtil.isEmpty(entity.getRealName())){
                    UIUtil.showToastSafe(R.string.label_not_have_realname);
                }
            }else{
                // 置空
                mChildContainer.setTag(null);
                StringUtil.fillSafeTextView(mQueryName,"");
            }
        }else{
            // 置空
            mChildContainer.setTag(null);
            StringUtil.fillSafeTextView(mQueryName,"");
        }
    }

    @Override
    public void updateCheckCourseBuy(@NonNull String memberId, boolean result) {
        if(result){
            // 可以购买
            // 检查是否是老师
            if(EmptyUtil.isNotEmpty(mCourseVo)){
                if(com.lqwawa.intleducation.module.user.tool.UserHelper.checkCourseAuthorWithUserId(memberId,mCourseVo)){
                    // 老师身份
                    UIUtil.showToastSafe(R.string.label_course_buy_warning);
                    return;
                }
            }

            if(EmptyUtil.isNotEmpty(mTeacherIds)){
                if(mTeacherIds.contains(memberId)){
                    // 老师身份
                    UIUtil.showToastSafe(R.string.label_course_buy_warning);
                    return;
                }
            }

            if(EmptyUtil.isNotEmpty(mNavigator)){
                mNavigator.onChoiceConfirm(memberId);
            }
            dismiss();
        }else{
            // 不可以购买,弹提示
            // UIUtil.showToastSafe(R.string.label_course_buy_warning);
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.btn_watch_name){
            // 点击查看姓名
            String inputName = mInputName.getText().toString().trim();
            if(EmptyUtil.isNotEmpty(inputName)){
                List<UserModel> members = new ArrayList<>();
                UserModel model = new UserModel(inputName);
                members.add(model);
                mBtnWatchName.setEnabled(false);
                mPresenter.requestUserInfoWithMembers(members);
            }
        }else if(viewId == R.id.btn_confirm){
            // 确定
            // 获取到已经选择的memberId
            String payMemberId = com.lqwawa.intleducation.module.user.tool.UserHelper.getUserId();
            if(mParent && mParentEnter){
                payMemberId = mChildMemberId;
            }else{
                // 选择购买
                List<ChildrenListVo> items = mMemberAdapter.getItems();
                for(ChildrenListVo vo:items){
                    if(vo.isChoice()){
                        if(vo.isOtherMember()){
                            // 自己或者其它人
                            if(TextUtils.equals(vo.getNickname(),UIUtil.getString(R.string.label_other_member))){
                                // 其它
                                if(EmptyUtil.isNotEmpty(mChildContainer)){
                                    if(EmptyUtil.isNotEmpty(mChildContainer.getTag())){
                                        UserEntity tag = (UserEntity) mChildContainer.getTag();
                                        payMemberId = tag.getMemberId();
                                    }else{
                                        // 提示
                                        UIUtil.showToastSafe(R.string.label_choice_other_null);
                                        return;
                                    }
                                }
                            }else if(TextUtils.equals(vo.getNickname(),UIUtil.getString(R.string.label_self_member))){
                                // 自己
                                payMemberId = com.lqwawa.intleducation.module.user.tool.UserHelper.getUserId();
                            }

                        }else{
                            // 找到选中
                            payMemberId = vo.getMemberId();
                        }
                        break;
                    }
                }
            }

            // 先网络请求,该课程该用户是否允许可以购买
            mPresenter.requestCheckCourseBuy(mCourseId,payMemberId,mType);
        }else if(viewId == R.id.btn_cancel){
            // 取消
            dismiss();
        }
    }

    @Override
    public void showError(int str) {
        super.showError(str);
        mBtnWatchName.setEnabled(true);
    }

    /**
     * 设置确定回调的监听
     * @param navigator 监听对象
     */
    public void setNavigator(@NonNull PayDialogNavigator navigator){
        this.mNavigator = navigator;
    }

    /**
     * 私有的show方法
     * @param manager Fragment管理器
     */
    public static void show(@NonNull FragmentManager manager,
                            @Nullable CourseVo courseVo,
                            @Nullable String teacherIds,
                            int courseId,int type,
                            @NonNull PayDialogNavigator navigator) {
        // 调用BottomSheetDialogFragment以及准备好的显示方法
        PayCourseDialogFragment fragment = new PayCourseDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_EXTRA_COURSE_OBJECT,courseVo);
        bundle.putString(KEY_EXTRA_CLASS_TEACHER_IDS,teacherIds);
        bundle.putBoolean(KEY_EXTRA_PARENT_ENTER,false);
        bundle.putInt(KEY_EXTRA_COURSE_ID,courseId);
        bundle.putInt(KEY_EXTRA_COURSE_TYPE,type);
        fragment.setArguments(bundle);
        fragment.setNavigator(navigator);
        fragment.show(manager,PayCourseDialogFragment.class.getName());
    }

    /**
     * 私有的show方法
     * @param manager Fragment管理器
     * @param isParent 是否是家长身份
     * @param childMemberId 从孩子的学程入口进来，帮孩子买课
     */
    public static void show(FragmentManager manager,
                            @Nullable CourseVo courseVo,
                            @Nullable String teacherIds,
                            boolean isParent,
                            @NonNull String childMemberId,
                            int courseId, int type,
                            @NonNull PayDialogNavigator navigator) {
        // 调用BottomSheetDialogFragment以及准备好的显示方法
        PayCourseDialogFragment fragment = new PayCourseDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_EXTRA_COURSE_OBJECT,courseVo);
        bundle.putString(KEY_EXTRA_CLASS_TEACHER_IDS,teacherIds);
        bundle.putBoolean(KEY_EXTRA_IS_PARENT,isParent);
        bundle.putBoolean(KEY_EXTRA_PARENT_ENTER,true);
        bundle.putString(KEY_EXTRA_CHILD_MEMBER_ID,childMemberId);
        bundle.putInt(KEY_EXTRA_COURSE_ID,courseId);
        bundle.putInt(KEY_EXTRA_COURSE_TYPE,type);
        fragment.setArguments(bundle);
        fragment.setNavigator(navigator);
        fragment.show(manager,PayCourseDialogFragment.class.getName());
    }
}
