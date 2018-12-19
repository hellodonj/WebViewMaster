package com.lqwawa.intleducation.module.discovery.adapter;
import android.app.Activity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.base.widgets.ExpandableTextView;
import com.lqwawa.intleducation.module.discovery.vo.CourseIntroduceVo;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2016/11/14.
 * email:man0fchina@foxmail.com
 */

public class CourseIntroduceAdapter extends MyBaseAdapter
        implements ExpandableTextView.OnExpandListener{
    private Activity activity;private
    SparseArray<Integer> mPositionsAndStates = new SparseArray<>();
    private List<CourseIntroduceVo> list;
    private LayoutInflater inflater;
    private static final int[] introductionStringResId = new int[]{
            R.string.introduction_str0,
            R.string.introduction_str1,
            R.string.introduction_str2,
            R.string.label_course_scoring_criteria};
    private static final int[] introductionLogoResId = new int[]{
            R.drawable.ic_introduction,
            R.drawable.ic_suit_obj,
            R.drawable.ic_study_goal,
            R.drawable.ic_course_scoring_criteria};

    public CourseIntroduceAdapter(Activity activity) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<CourseIntroduceVo>();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        CourseIntroduceVo vo = list.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.mod_course_introduction_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        int index = vo.getType() - 1;
        holder.logoIv.setImageDrawable(
                activity.getResources().getDrawable(introductionLogoResId[index]));
        holder.nameTv.setText(activity.getString(introductionStringResId[index]));
        holder.contentTv.setText(vo.getInfo());
        holder.contentTv.setTag(position);
        holder.contentTv.setExpandListener(this);

        return convertView;
    }

    @Override
    public void onExpand(ExpandableTextView view) {
        Object obj = view.getTag();
        if(obj != null && obj instanceof Integer){
            mPositionsAndStates.put((Integer)obj, view.getExpandState());
        }
    }

    @Override
    public void onShrink(ExpandableTextView view) {
        Object obj = view.getTag();
        if(obj != null && obj instanceof Integer){
            mPositionsAndStates.put((Integer)obj, view.getExpandState());
        }
    }

    private class ViewHolder {
        ImageView logoIv;
        TextView nameTv;
        ExpandableTextView contentTv;

        public ViewHolder(View parent) {
            logoIv = (ImageView) parent.findViewById(R.id.logo_iv);
            nameTv = (TextView) parent.findViewById(R.id.name_tv);
            contentTv = (ExpandableTextView) parent.findViewById(R.id.content_tv);
        }
    }

    /**
     * 下拉刷新设置数据
     *
     * @param list
     */
    public void setData(List<CourseIntroduceVo> list) {
        if (list != null) {
            this.list.clear();
            this.list = new ArrayList<CourseIntroduceVo>(list);
        } else {
            this.list.clear();
        }
    }

    /**
     * 上拉加载更多，新增数据
     *
     * @param list
     */
    public void addData(List<CourseIntroduceVo> list) {
        this.list.addAll(list);
    }
}
