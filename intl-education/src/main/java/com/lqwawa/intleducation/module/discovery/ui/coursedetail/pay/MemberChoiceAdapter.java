package com.lqwawa.intleducation.module.discovery.ui.coursedetail.pay;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.module.learn.vo.ChildrenListVo;

/**
 * @author mrmedici
 * @desc 用户选择的Adapter
 */
public class MemberChoiceAdapter extends RecyclerAdapter<ChildrenListVo> {

    @Override
    protected int getItemViewType(int position, ChildrenListVo childrenListVo) {
        return R.layout.item_member_choice_layout;
    }

    @Override
    protected ViewHolder<ChildrenListVo> onCreateViewHolder(View root, int viewType) {
        return new MemberHolder(root);
    }

    /**
     * @author mrmedici
     * @desc 显示用户选择的Holder
     */
    private class MemberHolder extends ViewHolder<ChildrenListVo>{

       private CheckBox mChoiceBox;
       private TextView mRealName,mNickName;

       public MemberHolder(View itemView) {
            super(itemView);
           mChoiceBox = (CheckBox) itemView.findViewById(R.id.cb_choice);
           mRealName = (TextView) itemView.findViewById(R.id.tv_real_name);
           mNickName = (TextView) itemView.findViewById(R.id.tv_nick_name);
        }

        @Override
        protected void onBind(ChildrenListVo childrenListVo) {
            StringUtil.fillSafeTextView(mRealName,childrenListVo.getRealName());
            StringUtil.fillSafeTextView(mNickName,childrenListVo.getNickname());
            mChoiceBox.setChecked(childrenListVo.isChoice());
            if(childrenListVo.isOtherMember()){
                mNickName.setVisibility(View.GONE);
            }else{
                mRealName.setVisibility(View.VISIBLE);
            }
        }
    }
}
