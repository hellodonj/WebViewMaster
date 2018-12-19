package com.lqwawa.intleducation.module.discovery.ui.order;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.Common;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.PayChapterEntity;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.PayChapterAdapter;

/**
 * @desc Mooc订单页已经选择的章节列表
 * @author medici
 */
public class OrderChapterAdapter extends RecyclerAdapter<PayChapterEntity>{

    @Override
    protected int getItemViewType(int position, PayChapterEntity payChapterEntity) {
        return R.layout.item_order_chapter_layout;
    }

    @Override
    protected RecyclerAdapter.ViewHolder<PayChapterEntity> onCreateViewHolder(View root, int viewType) {
        return new ViewHolder(root);
    }

    private static class ViewHolder extends RecyclerAdapter.ViewHolder<PayChapterEntity>{
        private TextView mTvChapterName;
        private TextView mTvPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            mTvChapterName = (TextView) itemView.findViewById(R.id.tv_chapter_name);
            mTvPrice = (TextView) itemView.findViewById(R.id.tv_price);
        }

        @Override
        protected void onBind(PayChapterEntity payChapterEntity) {
            StringUtil.fillSafeTextView(mTvChapterName,payChapterEntity.getName());
            StringUtil.fillSafeTextView(mTvPrice, Common.Constance.MOOC_MONEY_MARK.concat(Long.toString(payChapterEntity.getPrice())));
        }
    }
}
