package com.galaxyschool.app.wawaschool.fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterFragment;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.client.pojo.MediaType;

import java.util.ArrayList;
import java.util.List;
public class NocCoursePickerFragment extends AdapterFragment {

    public static final String TAG = NocCoursePickerFragment.class.getSimpleName();
    static final int TAB_CLOUD_COURSE = 0;
    static final int TAB_VIDEO_COURSE = 1;
    LqCourseSelectFragment lqCourseSelectFragment;
    LqCourseSelectFragment videoCourseSelectFragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_personal_course_picker, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity()!=null){
            hideSoftKeyboard(getActivity());
        }
        initViews();
    }
    @Override
    public void onResume() {
        super.onResume();
        loadViews();
    }

    private void loadViews() {
        if (getCurrAdapterViewHelper().hasData()) {
            getCurrAdapterViewHelper().update();
        } else {
            loadEntries();
        }
    }

   public void initViews(){
       TextView cancelBtn= (TextView) findViewById(R.id.btn_bottom_cancel);
       if (cancelBtn != null){
           cancelBtn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   getActivity().finish();
               }
           });
       }
        ListView listView = (ListView) findViewById(R.id.listview);
        if (listView != null) {
            listView.setDivider(new ColorDrawable(getResources().getColor(R.color.main_bg_color)));
            listView.setDividerHeight(1);
            AdapterViewHelper helper = new AdapterViewHelper(getActivity(), listView, R.layout
                    .media_type_list_item_model) {
                @Override
                public void loadData() {
                    loadEntries();
                }
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    HomeTypeEntry data= (HomeTypeEntry) getDataAdapter().getData().get(position);
                    if (data == null) {
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;
                    ImageView thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
                    TextView name = (TextView) view.findViewById(R.id.name);
                    thumbnail.setImageResource(data.icon);
                    name.setText(data.typeName);
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null || holder.data == null) {
                        return;
                    }
                    HomeTypeEntry data = (HomeTypeEntry) holder.data;
                    if (data != null) {
                        enterEntries(data);
                    }

                }
            };
            setCurrAdapterViewHelper(listView, helper);
        }
    }

    /**
     * 点击ListView的item进入具体的详情页面
     * @param data
     */
    public void enterEntries(HomeTypeEntry data){
        FragmentTransaction ft=getActivity().getSupportFragmentManager().beginTransaction();
        Bundle  args=getArguments();
        if (args==null){
            args=new Bundle();
        }
        if (data.type==TAB_CLOUD_COURSE){
                if (this.lqCourseSelectFragment == null) {
                    this.lqCourseSelectFragment = new LqCourseSelectFragment();
                }
                args.putInt(LqCourseSelectFragment.FROM_MEDIA_TYPE, MediaType.MICROCOURSE);
            args.putString(ActivityUtils.EXTRA_HEADER_TITLE,getString(R.string.microcourse));
                lqCourseSelectFragment.setArguments(args);
                ft.add(R.id.activity_body, lqCourseSelectFragment, LqCourseSelectFragment.TAG);
        }else if (data.type==TAB_VIDEO_COURSE){
            if(this.videoCourseSelectFragment == null) {
                this.videoCourseSelectFragment = new LqCourseSelectFragment();
            }
            args.putInt(LqCourseSelectFragment.FROM_MEDIA_TYPE,MediaType.VIDEO);
            args.putString(ActivityUtils.EXTRA_HEADER_TITLE,getString(R.string.videos));
            videoCourseSelectFragment.setArguments(args);
            ft.add(R.id.activity_body,videoCourseSelectFragment,LqCourseSelectFragment.TAG);
        }
        ft.addToBackStack(null);
        ft.commit();
    }
    private void loadEntries() {
        List<HomeTypeEntry> list=new ArrayList<>();
        HomeTypeEntry  item=new HomeTypeEntry();
        //lq课件
        item=new HomeTypeEntry();
        item.icon=R.drawable.icon_lq_course;
        item.typeName=R.string.microcourse;
        item.type=TAB_CLOUD_COURSE;
        list.add(item);

        //视频
        item=new HomeTypeEntry();
        item.icon=R.drawable.resource_video_ico;
        item.typeName=R.string.videos;
        item.type=TAB_VIDEO_COURSE;
        list.add(item);

        getCurrAdapterViewHelper().setData(list);
    }

    class HomeTypeEntry{
        int icon;
        int typeName;
        int type;
    }
}
