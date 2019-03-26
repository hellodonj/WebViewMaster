package com.lqwawa.intleducation.module.discovery.ui.coin.donation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.base.widgets.adapter.TextWatcherAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.user.CoinEntity;
import com.lqwawa.intleducation.factory.data.entity.user.UserEntity;
import com.lqwawa.intleducation.factory.data.model.user.UserModel;
import com.lqwawa.intleducation.module.discovery.ui.coin.UserParams;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.pay.MemberChoiceAdapter;
import com.lqwawa.intleducation.module.learn.vo.ChildrenListVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 转赠蛙蛙币页面
 */
public class DonationCoinActivity extends PresenterActivity<DonationCoinContract.Presenter>
    implements DonationCoinContract.View, View.OnClickListener {

    public static final String KEY_RESULT_BALANCE_STATE = "KEY_RESULT_BALANCE_STATE";

    private TopBar mTopBar;

    private TextView mDialogDesc;
    private RecyclerView mRecycler;
    private MemberChoiceAdapter mMemberAdapter;

    private LinearLayout mMoreLayout;
    private LinearLayout mOtherLayout;
    private LinearLayout mChildContainer;
    private EditText mInputName;
    private Button mBtnWatchName;
    private TextView mQueryName;

    private EditText mInputMoney;

    private LinearLayout mBalanceContainer;
    // 余额
    private TextView mTvBalanceMoney;
    // 全部转赠
    private TextView mTvAllDonation;

    private CoinEntity mCoinEntity;

    //确认
    private Button mBtnConfirm;

    @Override
    protected DonationCoinContract.Presenter initPresenter() {
        return new DonationCoinPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_donation_coin;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setBack(true);
        mTopBar.setTitle(R.string.title_donation_coin);

        mDialogDesc = (TextView) findViewById(R.id.dialog_desc);
        mRecycler = (RecyclerView) findViewById(R.id.recycler);
        mMoreLayout = (LinearLayout) findViewById(R.id.more_layout);
        mOtherLayout = (LinearLayout) findViewById(R.id.other_layout);
        mChildContainer = (LinearLayout) findViewById(R.id.child_container);
        mInputName = (EditText) findViewById(R.id.et_nick_name);
        mBtnWatchName = (Button) findViewById(R.id.btn_watch_name);
        mQueryName = (TextView) findViewById(R.id.tv_query_name);

        mBalanceContainer = (LinearLayout) findViewById(R.id.balance_container);
        mInputMoney = (EditText) findViewById(R.id.et_input_money);
        mTvBalanceMoney = (TextView) findViewById(R.id.tv_balance_money);
        mTvAllDonation = (TextView) findViewById(R.id.tv_all_donation);

        mBtnConfirm = (Button) findViewById(R.id.btn_confirm);
        mBtnWatchName.setOnClickListener(this);
        mTvAllDonation.setOnClickListener(this);
        mBtnConfirm.setOnClickListener(this);

        // 界面显示
        LinearLayoutManager layoutManager = new LinearLayoutManager(UIUtil.getContext());
        mRecycler.setLayoutManager(layoutManager);

        mMemberAdapter = new MemberChoiceAdapter();
        mRecycler.setAdapter(mMemberAdapter);

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

                mBalanceContainer.postInvalidate();
                mMemberAdapter.notifyDataSetChanged();
            }
        });


        mInputMoney.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String editable = mInputMoney.getText().toString();
                if(editable.startsWith("0")){
                    StringUtil.fillSafeTextView(mInputMoney,"");
                }
            }
        });
    }


    @Override
    protected void initData() {
        super.initData();
        mPresenter.requestParentChildData();
        mPresenter.requestUserCoinCount();
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
        StringUtil.fillSafeTextView(mDialogDesc,getString(R.string.label_please_choice_donation_user));

        // 前面加上我自己
        // vos.add(0,ChildrenListVo.buildVo(UIUtil.getString(R.string.label_self_member),true));
        // 默认显示其他人模式
        if(EmptyUtil.isEmpty(vos)){
            // 没有孩子
            mOtherLayout.setVisibility(View.VISIBLE);
        }
        // 后面加上其它人
        vos.add(ChildrenListVo.buildVo(UIUtil.getString(R.string.label_other_member),EmptyUtil.isEmpty(vos)));

        mMemberAdapter.replace(vos);
    }

    @Override
    public void updateUserCoinCountView(@NonNull CoinEntity entity) {
        this.mCoinEntity = entity;
        int amount = entity.getAmount();
        // 填充剩余蛙蛙币
        StringUtil.fillSafeTextView(mTvBalanceMoney,String.format(UIUtil.getString(R.string.label_current_balance),amount));
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
        if(viewId == R.id.tv_all_donation){
            // 点击全部转赠
            final CoinEntity entity = mCoinEntity;
            if(EmptyUtil.isNotEmpty(entity)){
                int amount = entity.getAmount();
                if(amount != 0){
                    mInputMoney.setText(String.valueOf(amount));
                    mInputMoney.setSelection(String.valueOf(amount).length());
                }else{
                    mInputMoney.getText().clear();
                    mInputMoney.setSelection(0);
                }
            }

        }else if(viewId == R.id.btn_watch_name){
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
            // 确认转赠
            UserParams user = null;
            // 点击确定,就删Tag
            mChildContainer.setTag(null);

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

                                    String account = UserHelper.getAccount();
                                    String inputName = user.getNickName();
                                    if(inputName.equalsIgnoreCase(account)){
                                        UIUtil.showToastSafe(R.string.label_not_donation_self);
                                        return;
                                    }
                                }else{
                                    // 提示
                                    // UIUtil.showToastSafe(R.string.label_please_choice_donation_user);
                                    // return;

                                    // 此时请求
                                    String inputName = mInputName.getText().toString().trim();
                                    if(EmptyUtil.isEmpty(inputName)){
                                        UIUtil.showToastSafe(R.string.label_please_input_completed_right_account);
                                        return;
                                    }

                                    String account = UserHelper.getAccount();
                                    if(inputName.equalsIgnoreCase(account)){
                                        UIUtil.showToastSafe(R.string.label_not_donation_self);
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
                                                        balanceDonation(user);
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

            balanceDonation(user);

        }
    }

    /**
     * 转赠
     * @param user 转赠的对象
     */
    private void balanceDonation(@NonNull UserParams user){
        if(EmptyUtil.isEmpty(user)){
            // 提示
            UIUtil.showToastSafe(R.string.label_please_choice_donation_user);
            return;
        }

        // 转赠金额
        String inputMoney = mInputMoney.getText().toString().trim();
        if(EmptyUtil.isEmpty(inputMoney)){
            // 请输入转赠金额
            UIUtil.showToastSafe(R.string.label_please_input_donation_money);
            return;
        }

        int money = Integer.parseInt(inputMoney);
        final CoinEntity entity = mCoinEntity;
        if(money > entity.getAmount()){
            // 输入的转赠金额大于余额
            UIUtil.showToastSafe(R.string.tip_not_sufficient_funds);
            return;
        }

        String memberId = UserHelper.getUserId();
        String beneficiaryId = user.getMemberId();
        int amount = Integer.parseInt(inputMoney);
        showLoading();
        mBtnConfirm.setEnabled(false);
        mPresenter.requestBalanceDonation(memberId,beneficiaryId,amount);
    }

    @Override
    public void updateBalanceDonationView(boolean result) {
        if(result){
            // 余额赠送成功
            UIUtil.showToastSafe(R.string.tip_balance_succeed);
        }else{
            // 余额赠送失败
            UIUtil.showToastSafe(R.string.tip_balance_failed);
        }


        // 通知刷新
        mBtnConfirm.setEnabled(true);
        Intent resultIntent = new Intent();
        Bundle extras = new Bundle();
        extras.putBoolean(KEY_RESULT_BALANCE_STATE,result);
        resultIntent.putExtras(extras);
        setResult(Activity.RESULT_OK,resultIntent);
        finish();
    }

    @Override
    public void showError(int str) {
        super.showError(str);
        mBtnWatchName.setEnabled(true);
        mBtnConfirm.setEnabled(true);
    }

    /**
     * 蛙蛙币转赠页面的入口
     * @param activity 上下文对象
     * @param requestCode 请求码
     */
    public static void show(@NonNull Activity activity,int requestCode){
        Intent intent = new Intent(activity,DonationCoinActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        activity.startActivityForResult(intent,requestCode);
    }
}
