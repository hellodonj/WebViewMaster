package com.lqwawa.intleducation.module.discovery.ui.subject;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.module.discovery.ui.subject.add.SubjectConfigAdapter;
import com.lqwawa.intleducation.module.discovery.ui.subject.add.SubjectTagAdapter;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 二级列表Adapter
 */
public class SubjectExpandableAdapter extends BaseExpandableListAdapter {

    private List<LQCourseConfigEntity> mGroupData;
    private boolean mViewer;

    public SubjectExpandableAdapter(boolean viewer) {
        this(viewer,null);
    }

    public SubjectExpandableAdapter(boolean viewer,@NonNull List<LQCourseConfigEntity> groupData) {
        this.mViewer = viewer;
        this.mGroupData = mGroupData;
        if(mGroupData == null){
            mGroupData = new ArrayList<>();
        }
    }

    @Override
    public int getGroupCount() {
        return mGroupData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        LQCourseConfigEntity groupEntity = mGroupData.get(groupPosition);
        return getChildrenSize(groupEntity);
    }

    @Override
    public Object getGroup(int groupPosition) {
        LQCourseConfigEntity groupEntity = mGroupData.get(groupPosition);
        return groupEntity;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        LQCourseConfigEntity groupEntity = mGroupData.get(groupPosition);
        List<LQCourseConfigEntity> childList = groupEntity.getChildList();
        if(EmptyUtil.isEmpty(childList)) return null;
        return childList.get(childPosition);
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
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder groupHolder = null;
        if(convertView == null){
            convertView = UIUtil.inflate(R.layout.item_subject_expandable_group);
            groupHolder = new GroupViewHolder(convertView);
            convertView.setTag(groupHolder);
        }else{
            groupHolder = (GroupViewHolder) convertView.getTag();
        }

        LQCourseConfigEntity group = (LQCourseConfigEntity) getGroup(groupPosition);
        groupHolder.bind(group);

        if(isExpanded){
            // 展开
            groupHolder.mIvArrow.setImageResource(R.drawable.ic_arrow_up);
        }else{
            // 关闭
            groupHolder.mIvArrow.setImageResource(R.drawable.ic_arrow_down);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder childHolder = null;
        if(convertView == null){
            convertView = UIUtil.inflate(R.layout.item_subject_expandable_child);
            childHolder = new ChildViewHolder(convertView);
            convertView.setTag(childHolder);
        }else{
            childHolder = (ChildViewHolder) convertView.getTag();
        }

        LQCourseConfigEntity child = (LQCourseConfigEntity) getChild(groupPosition, childPosition);
        childHolder.bind(child);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public void setData(@NonNull List<LQCourseConfigEntity> groupData){
        mGroupData.clear();
        if(EmptyUtil.isNotEmpty(groupData)){
            mGroupData.addAll(groupData);
            notifyDataSetChanged();
        }
    }

    /**
     * 获取二级列表的数目
     * @param entity 一级实体
     * @return count
     */
    public int getChildrenSize(@NonNull LQCourseConfigEntity entity){
        int count = 0;
        if(EmptyUtil.isEmpty(entity.getChildList())) return count;
        return entity.getChildList().size();
    }

    class GroupViewHolder implements Action{
        ImageView mIvArrow;
        TextView mTvTitle;

        public GroupViewHolder(View convertView) {
            mIvArrow = (ImageView) convertView.findViewById(R.id.iv_arrow);
            mTvTitle = (TextView) convertView.findViewById(R.id.tv_group_title);
        }

        @Override
        public void bind(@NonNull LQCourseConfigEntity entity) {
            StringUtil.fillSafeTextView(mTvTitle,entity.getConfigValue());
        }
    }

    class ChildViewHolder implements Action{

        private TextView mTvConfigTitle;
        private TagFlowLayout mFlowLayout;
        // private RecyclerView mFlexRecycler;
        // private SubjectConfigAdapter mAdapter;

        private SubjectTagAdapter mAdapter;

        public ChildViewHolder(View convertView) {
            mTvConfigTitle = (TextView) convertView.findViewById(R.id.tv_config_title);
            mFlowLayout = (TagFlowLayout) convertView.findViewById(R.id.flow_layout);
            // mFlexRecycler = (RecyclerView) convertView.findViewById(R.id.recycler);
            /*FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(UIUtil.getContext());
            // 主轴为水平方向，起点在左端。
            layoutManager.setFlexDirection(FlexDirection.ROW);
            // 换行排列
            layoutManager.setFlexWrap(FlexWrap.WRAP);
            // 项目在主轴上的对齐方式 交叉轴的起点对齐。
            layoutManager.setJustifyContent(JustifyContent.FLEX_END);*/

            /*GridLayoutManager layoutManager = new GridLayoutManager(UIUtil.getContext(),3);
            mFlexRecycler.setLayoutManager(layoutManager);*/
        }

        @Override
        public void bind(@NonNull LQCourseConfigEntity entity) {
            StringUtil.fillSafeTextView(mTvConfigTitle,entity.getConfigValue());
            List<LQCourseConfigEntity> configEntities = entity.getChildList();
            mAdapter = new SubjectTagAdapter(mViewer,configEntities);
            mFlowLayout.setAdapter(mAdapter);

            if(!mViewer){
                // 不是观看模式
                mFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
                    @Override
                    public boolean onTagClick(View view, int position, FlowLayout parent) {
                        LQCourseConfigEntity item = mAdapter.getItem(position);
                        item.setSelected(!item.isSelected());
                        mAdapter.notifyDataChanged();
                        return true;
                    }
                });
            }
        }
    }

    interface Action{
        void bind(@NonNull LQCourseConfigEntity entity);
    }

    /**
     * 获取data数据
     * @return List<LQCourseConfigEntity>
     */
    public List<LQCourseConfigEntity> getItems(){
        return mGroupData;
    }
}
