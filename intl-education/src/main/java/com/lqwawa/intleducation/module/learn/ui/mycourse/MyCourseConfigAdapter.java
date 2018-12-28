package com.lqwawa.intleducation.module.learn.ui.mycourse;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.module.discovery.ui.subject.add.SubjectTagAdapter;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 二级列表Adapter
 */
public class MyCourseConfigAdapter extends BaseExpandableListAdapter {
    // 小语种的Id
    private static final int MINORITY_LANGUAGE_ID = 2004;

    private int[] colors = new int[]{
            R.color.basics_type_color_1,
            R.color.basics_type_color_2,
            R.color.basics_type_color_3,
            R.color.basics_type_color_4
    };

    private List<LQCourseConfigEntity> mGroupData;

    public MyCourseConfigAdapter(@NonNull List<LQCourseConfigEntity> groupData) {
        this.mGroupData = groupData;
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
            convertView = UIUtil.inflate(R.layout.item_course_config_expandable_group);
            groupHolder = new GroupViewHolder(convertView);
            convertView.setTag(groupHolder);
        }else{
            groupHolder = (GroupViewHolder) convertView.getTag();
        }

        LQCourseConfigEntity group = (LQCourseConfigEntity) getGroup(groupPosition);
        groupHolder.bind(groupPosition,group);

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
            convertView = UIUtil.inflate(R.layout.item_course_config_expandable_child);
            childHolder = new ChildViewHolder(convertView);
            convertView.setTag(childHolder);
        }else{
            childHolder = (ChildViewHolder) convertView.getTag();
        }

        LQCourseConfigEntity group = (LQCourseConfigEntity) getGroup(groupPosition);
        LQCourseConfigEntity child = (LQCourseConfigEntity) getChild(groupPosition, childPosition);
        if(group.getId() == MINORITY_LANGUAGE_ID){
            childHolder.bind(groupPosition,group);
        }else{
            childHolder.bind(childPosition,child);
        }
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
        // 如果是小语种课程,其子控件只有一个
        if(entity.getId() == MINORITY_LANGUAGE_ID) return 1;
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
        public void bind(int position,@NonNull LQCourseConfigEntity entity) {
            StringUtil.fillSafeTextView(mTvTitle,entity.getConfigValue());
        }
    }

    class ChildViewHolder implements Action{

        private TextView mTvConfigTitle;
        private RecyclerView mRecycler;
        // private RecyclerView mFlexRecycler;
        // private SubjectConfigAdapter mAdapter;

        private MyCourseChoiceAdapter mAdapter;

        public ChildViewHolder(View convertView) {
            mTvConfigTitle = (TextView) convertView.findViewById(R.id.tv_config_title);
            mRecycler = (RecyclerView) convertView.findViewById(R.id.recycler);
            GridLayoutManager layoutManager = new GridLayoutManager(UIUtil.getContext(),3);
            mRecycler.setLayoutManager(layoutManager);
        }

        @Override
        public void bind(int position,@NonNull LQCourseConfigEntity entity) {
            if(entity.getId() == MINORITY_LANGUAGE_ID){
                // 一级列表是小语种课程
                mTvConfigTitle.setVisibility(View.GONE);
            }else{
                mTvConfigTitle.setVisibility(View.VISIBLE);
                StringUtil.fillSafeTextView(mTvConfigTitle,entity.getConfigValue());
            }

            List<LQCourseConfigEntity> configEntities = entity.getChildList();
            mAdapter = new MyCourseChoiceAdapter(configEntities,UIUtil.getColor(colors[position % 4]));
            mRecycler.setAdapter(mAdapter);

            mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<LQCourseConfigEntity>() {
                @Override
                public void onItemClick(RecyclerAdapter.ViewHolder holder, LQCourseConfigEntity entity) {
                    super.onItemClick(holder, entity);
                    // 点击了某个Item

                }
            });
        }
    }

    interface Action{
        void bind(int position,@NonNull LQCourseConfigEntity entity);
    }

    /**
     * 获取data数据
     * @return List<LQCourseConfigEntity>
     */
    public List<LQCourseConfigEntity> getItems(){
        return mGroupData;
    }
}
