package com.lqwawa.mooc.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.module.discovery.vo.ChapterVo;

import java.util.List;
import java.util.Map;

/**
 * desc: 多选界面的Adapter
 * author: dj
 */

public class SelectMoreAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<ChapterVo> group;
    private Map<String, List<ChapterVo>> children;

    /**
     * 构造函数
     *
     * @param group    组元素列表
     * @param children 子元素列表
     * @param context
     */
    public SelectMoreAdapter(Context context, List<ChapterVo> group, Map<String, List<ChapterVo>> children) {
        mContext = context;
        this.group = group;
        this.children = children;
    }

    @Override
    public int getGroupCount() {
        if (group != null) {
            return group.size();
        }
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<ChapterVo> childs = children.get(group.get(groupPosition).getId());
        if (childs != null) {
            return childs.size();
        }
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return group.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        List<ChapterVo> childs = children.get(group.get(groupPosition).getId());
        return childs.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final GroupViewHolder gholder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.playlist_item_title, null);
            gholder = new GroupViewHolder(convertView);
            convertView.setTag(gholder);
        } else {
            gholder = (GroupViewHolder) convertView.getTag();
        }
        final ChapterVo group = (ChapterVo) getGroup(groupPosition);
        if (group != null) {
            gholder.mTvTitle.setText(group.getName());
            gholder.mCbTitleSelect.setChecked(group.isChoosed());
            gholder.mCbTitleSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //没锁并且有子选项
                    if (group.isUnlock()){
                        List<ChapterVo> chapterVos = group.getChildren();
                        for (int i = 0; i < chapterVos.size(); i++) {
                             if(!chapterVos.get(i).isUnlock()){
                                 gholder.mCbTitleSelect.setChecked(false);
                                 UIUtil.showToastSafe(com.lqwawa.intleducation.R.string.label_unlock_tip);
                                 return;
                             }
                        }
                        group.setChoosed(((CheckBox) v).isChecked());
                        // 暴露组选接口
                        checkInterface.checkGroup(groupPosition, ((CheckBox) v).isChecked());
                    }else {
                        gholder.mCbTitleSelect.setChecked(false);
                        UIUtil.showToastSafe(com.lqwawa.intleducation.R.string.label_unlock_tip);
                    }
                }
            });
        }
        notifyDataSetChanged();
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ChildViewHolder cholder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.playlist_item_content, null);
            cholder = new ChildViewHolder(convertView);
            convertView.setTag(cholder);
        } else {
            cholder = (ChildViewHolder) convertView.getTag();
        }

        final ChapterVo detailResponse = (ChapterVo) getChild(groupPosition, childPosition);
        if (detailResponse != null ) {
            cholder.mTvChildTitle.setText(detailResponse.getName());
            cholder.mCbChildSelect.setChecked(detailResponse.isChoosed());
            cholder.mCbChildSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (detailResponse.isUnlock()){
                        detailResponse.setChoosed(((CheckBox) v).isChecked());
                        cholder.mCbChildSelect.setChecked(((CheckBox) v).isChecked());
                        // 暴露子选接口
                        checkInterface.checkChild(groupPosition, childPosition, ((CheckBox) v).isChecked());
                    }else {
                        cholder.mCbChildSelect.setChecked(false);
                        UIUtil.showToastSafe(com.lqwawa.intleducation.R.string.label_unlock_tip);
                    }
                }
            });
        }
        notifyDataSetChanged();
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    /**
     * 组元素绑定器
     */
    public static class GroupViewHolder {
        private CheckBox mCbTitleSelect;
        private TextView mTvTitle;

        public GroupViewHolder(View view) {
            mCbTitleSelect = (CheckBox) view.findViewById(R.id.cb_title_select);
            mTvTitle = (TextView) view.findViewById(R.id.tv_title);
        }
    }

    /**
     * 子元素绑定器
     */
    public static class ChildViewHolder {
        private CheckBox mCbChildSelect;
        private TextView mTvChildTitle;

        public ChildViewHolder(View view) {
            mCbChildSelect = (CheckBox) view.findViewById(R.id.cb_child_select);
            mTvChildTitle = (TextView) view.findViewById(R.id.tv_child_title);
        }
    }

    private CheckInterface checkInterface;

    public void setCheckInterface(CheckInterface checkInterface) {
        this.checkInterface = checkInterface;
    }

    /**
     * 复选框接口
     */
    public interface CheckInterface {
        /**
         * 组选框状态改变触发的事件
         *
         * @param groupPosition 组元素位置
         * @param isChecked     组元素选中与否
         */
        void checkGroup(int groupPosition, boolean isChecked);

        /**
         * 子选框状态改变时触发的事件
         *
         * @param groupPosition 组元素位置
         * @param childPosition 子元素位置
         * @param isChecked     子元素选中与否
         */
        void checkChild(int groupPosition, int childPosition, boolean isChecked);

    }

}
