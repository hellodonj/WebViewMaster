package com.lqwawa.intleducation.module.discovery.ui.coin;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
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
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.pay.MemberChoiceAdapter;
import com.lqwawa.intleducation.module.learn.vo.ChildrenListVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 货币转让选择赠送的人Dialog
 */
public class JavaCoinTransferDialogFragment extends PresenterDialogFragment<JavaCoinTransferContract.Presenter>
    implements JavaCoinTransferContract.View,View.OnClickListener {

    private JavaCoinTransferNavigator mNavigator;
    private View mRootView;

    private TextView mDialogDesc;
    private RecyclerView mRecycler;
    private MemberChoiceAdapter mMemberAdapter;

    private LinearLayout mMoreLayout;
    private LinearLayout mOtherLayout;
    private LinearLayout mChildContainer;
    private EditText mInputName;
    private Button mBtnWatchName;
    private TextView mQueryName;

    // 确定
    private Button mBtnConfirm,mBtnCancel;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getContext(), R.style.AppTheme_Dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = mRootView = inflater.inflate(R.layout.dialog_coin_transfer,null);
        mDialogDesc = (TextView) view.findViewById(R.id.dialog_desc);
        mRecycler = (RecyclerView) view.findViewById(R.id.recycler);
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
    protected JavaCoinTransferContract.Presenter initPresenter() {
        return new JavaCoinTransferPresenter(this);
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

        // 请求所有的孩子
        mPresenter.requestParentChildData();
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
        // 设置标题
        StringUtil.fillSafeTextView(mDialogDesc,getString(R.string.label_give_money_tip_title));

        // 前面加上我自己
        vos.add(0,ChildrenListVo.buildVo(UIUtil.getString(R.string.label_self_member),true));
        // 后面加上其它人
        vos.add(ChildrenListVo.buildVo(UIUtil.getString(R.string.label_other_member),false));
        mMemberAdapter.replace(vos);
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
                UIUtil.showToastSafe(R.string.label_this_account_not_available);
            }
        }else{
            // 置空
            mChildContainer.setTag(null);
            StringUtil.fillSafeTextView(mQueryName,"");
            UIUtil.showToastSafe(R.string.label_this_account_not_available);
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
            }else{
                UIUtil.showToastSafe(R.string.label_please_input_completed_right_account);
            }
        }else if(viewId == R.id.btn_confirm){
            // 确定
            UserParams user = null;

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
                                    user = UserParams.buildUser(tag);
                                }else{
                                    // 提示
                                    // UIUtil.showToastSafe(R.string.label_give_money_tip_title);
                                    // return;

                                    // 此时请求
                                    String inputName = mInputName.getText().toString().trim();
                                    if(EmptyUtil.isEmpty(inputName)){
                                        UIUtil.showToastSafe(R.string.label_please_input_completed_right_account);
                                        return;
                                    }

                                    List<UserModel> members = new ArrayList<>();
                                    UserModel model = new UserModel(inputName);
                                    members.add(model);

                                    mBtnConfirm.setEnabled(false);
                                    com.lqwawa.intleducation.factory.helper.UserHelper.requestRealNameWithNick(false, members, new DataSource.Callback<List<UserEntity>>() {
                                        @Override
                                        public void onDataNotAvailable(int strRes) {
                                            UIUtil.showToastSafe(strRes);
                                            mBtnConfirm.setEnabled(true);
                                        }


                                        @Override
                                        public void onDataLoaded(List<UserEntity> entities) {
                                            if(EmptyUtil.isNotEmpty(entities)){
                                                mBtnConfirm.setEnabled(true);
                                                if(EmptyUtil.isNotEmpty(entities)){
                                                    // 获取到用户信息
                                                    UserEntity entity = entities.get(0);
                                                    if(entity.isIsExist()){
                                                        UserParams user = UserParams.buildUser(entity);
                                                        if(EmptyUtil.isNotEmpty(mNavigator)){
                                                            mNavigator.onChoiceConfirm(user);
                                                            dismiss();
                                                        }
                                                    }else{
                                                        UIUtil.showToastSafe(R.string.label_this_account_not_available);
                                                    }
                                                }else{
                                                    UIUtil.showToastSafe(R.string.label_this_account_not_available);
                                                }
                                            }
                                        }
                                    });
                                    // 不走下面的逻辑
                                    return;
                                }
                            }
                        }else if(TextUtils.equals(vo.getNickname(),UIUtil.getString(R.string.label_self_member))){
                            // 自己
                            user = UserParams.buildUser(UserHelper.getUserInfo());
                        }

                    }else{
                        // 找到选中
                        user = UserParams.buildUser(vo);
                    }
                    break;
                }
            }

            if(EmptyUtil.isNotEmpty(mNavigator)){
                mNavigator.onChoiceConfirm(user);
                dismiss();
            }
        }else if(viewId == R.id.btn_cancel){
            // 取消
            dismiss();
        }
    }



    public void setNavigator(@NonNull JavaCoinTransferNavigator navigator){
        this.mNavigator = navigator;
    }

    public static void show(@NonNull FragmentManager manager,@NonNull JavaCoinTransferNavigator navigator){
        JavaCoinTransferDialogFragment fragment = new JavaCoinTransferDialogFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        fragment.setNavigator(navigator);
        fragment.show(manager,JavaCoinTransferDialogFragment.class.getSimpleName());
    }
}
