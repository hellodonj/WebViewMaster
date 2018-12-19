package com.galaxyschool.app.wawaschool.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.CompletedListInfo;
import com.galaxyschool.app.wawaschool.pojo.DiscussPersonList;
import com.galaxyschool.app.wawaschool.pojo.HomeworkFinishStatusObjectResult;
import com.galaxyschool.app.wawaschool.pojo.UnCompletedListInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作业完成状态列表
 */

public class ShowTopicDiscussionPeopleFragment extends ContactsListFragment {

    public static final String TAG = ShowTopicDiscussionPeopleFragment.class.getSimpleName();

    private View rootView;
    private int TaskId;
    private String title;
    //已完成
    private View finishLayout;
    private TextView finishText;
    private ImageView finishImage;
    private GridView finishGridView;
    private boolean isFinishLayoutExpand=true;
    private String finishGridViewTag;
    //未完成
    private View unFinishLayout;
    private TextView unFinishText;
    private ImageView unFinishImage;
    private GridView unFinishGridView;
    private boolean isUnFinishLayoutExpand=true;
    private String unFinishGridViewTag;
    private List<DiscussPersonList> discussPersonList=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.topic_discussion_join_people_list, null);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            loadViews();
        }
    }

    void initViews() {
        if (getArguments() != null) {
            title = getArguments().getString("title");
            List<DiscussPersonList> list= (List<DiscussPersonList>) getArguments().
                    getSerializable("list");
            if (list!=null&&list.size()>0){
                discussPersonList.addAll(list);
            }
        }


        //头布局
        View view = findViewById(R.id.contacts_header_layout);

        //标题
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setText(title);
        }

        //右侧的布局
        textView = (TextView) findViewById(R.id.contacts_header_right_btn);
        if (textView != null) {
            textView.setVisibility(View.GONE);
            textView.setOnClickListener(this);
            textView.setTextAppearance(getActivity(), R.style.txt_wawa_big_green);
            textView.setText("");
        }


        //下拉刷新
//        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
//                R.id.pull_to_refresh);
//        setPullToRefreshView(pullToRefreshView);

        //已完成布局
        finishLayout=findViewById(R.id.finish_status_contacts_list_item_layout);
        //目前不显示老师布局
        finishLayout.setVisibility(View.GONE);
        finishLayout.setOnClickListener(this);

        finishText= (TextView) findViewById(R.id.finish_status_contacts_item_title);
        finishText.setText(getString(R.string.n_person_join,0));

        finishImage= (ImageView) findViewById(R.id.finish_status_contacts_item_arrow);

        //未完成布局
        unFinishLayout=findViewById(R.id.unfinish_status_contacts_list_item_layout);
        unFinishLayout.setOnClickListener(this);

        unFinishText= (TextView) findViewById(R.id.unfinish_status_contacts_item_title);
        if (discussPersonList==null||discussPersonList.size()<=0){
            unFinishText.setText(getString(R.string.n_person_join,0));
        }else {
            unFinishText.setText(getString(R.string.n_person_join,discussPersonList.size()));
        }


        unFinishImage= (ImageView) findViewById(R.id.unfinish_status_contacts_item_arrow);

        initFinishGridViewHelper();
        initUnFinishGridViewHelper();
    }

    private void initFinishGridViewHelper() {
        finishGridView= (GridView) findViewById(R.id.finish_status_grid_view);
        if (finishGridView != null) {
            AdapterViewHelper finishGridViewHelper = new AdapterViewHelper(getActivity(),
                    finishGridView, R.layout.item_finish_status_gridview) {
                @Override
                public void loadData() {
                    loadCommonData();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    final DiscussPersonList data = (DiscussPersonList) getDataAdapter().getItem
                            (position);
                    if (data == null) {
                        return view;
                    }
                    //头像
                    ImageView imageView = (ImageView) view.findViewById(R.id.icon_head);
                    if (imageView != null) {
                        getThumbnailManager().displayThumbnailWithDefault(
                                AppSettings.getFileUrl(data.getHeadPicUrl()), imageView,
                                R.drawable.default_user_icon);
                        //点击头像进入个人详情
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //游客之类的memberId为空的不给点击。
                                if (!TextUtils.isEmpty(data.getId())) {
                                    ActivityUtils.enterPersonalSpace(getActivity(), data.getId());
                                }
                            }
                        });
                    }
                    //标题
                    TextView textView = (TextView) view.findViewById(R.id.title);
                    if (textView != null) {
                        textView.setText(data.getName());
                    }

                    //时间
                    textView = (TextView) view.findViewById(R.id.time);
                    if (textView != null) {
                        textView.setVisibility(View.INVISIBLE);
                    }

                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null || holder.data == null) {
                        return;
                    }
                    DiscussPersonList data = (DiscussPersonList) holder.data;
                    if (data!=null){

                    }
                }
            };
            //根据tag来区分不同的数据源
            this.finishGridViewTag = String.valueOf(finishGridView.getId());
            addAdapterViewHelper(this.finishGridViewTag, finishGridViewHelper);
        }

    }

    private void initUnFinishGridViewHelper() {
        unFinishGridView= (GridView) findViewById(R.id.unfinish_status_grid_view);
        if (unFinishGridView != null) {
            AdapterViewHelper unFinishGridViewHelper = new AdapterViewHelper(getActivity(),
                    unFinishGridView, R.layout.item_finish_status_gridview) {
                @Override
                public void loadData() {
                    loadCommonData();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    final DiscussPersonList data = (DiscussPersonList) getDataAdapter()
                            .getItem(position);
                    if (data == null) {
                        return view;
                    }
                    //头像
                    ImageView imageView = (ImageView) view.findViewById(R.id.icon_head);
                    if (imageView != null) {
                        getThumbnailManager().displayThumbnailWithDefault(
                                AppSettings.getFileUrl(data.getHeadPicUrl()), imageView,
                                R.drawable.default_user_icon);
                        //点击头像进入个人详情
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //游客之类的memberId为空的不给点击。
                                if (!TextUtils.isEmpty(data.getId())) {
                                    ActivityUtils.enterPersonalSpace(getActivity(), data.getId());
                                }
                            }
                        });
                    }
                    //标题
                    TextView textView = (TextView) view.findViewById(R.id.title);
                    if (textView != null) {
                        textView.setText(data.getName());
                    }

                    //时间
                    textView = (TextView) view.findViewById(R.id.time);
                    if (textView != null) {
//                        textView.setText(data.getCommitTime());
                        textView.setVisibility(View.INVISIBLE);
                    }

                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null || holder.data == null) {
                        return;
                    }
                    DiscussPersonList data = (DiscussPersonList) holder.data;
                    if (data!=null){

                    }
                }
            };
            //根据tag来区分不同的数据源
            this.unFinishGridViewTag = String.valueOf(unFinishGridView.getId());
            addAdapterViewHelper(this.unFinishGridViewTag, unFinishGridViewHelper);
        }

    }

    private void loadViews() {
        loadCommonData();
    }

    /**
     * 模拟数据
     */
    private void loadCommonData() {
        //老师
        if (discussPersonList==null||discussPersonList.size()<=0){
        }else {
//            finishText.setText(getString(R.string.teacher));
            getAdapterViewHelper(finishGridViewTag).setData(discussPersonList);
        }


        //学生
        if (discussPersonList==null||discussPersonList.size()<=0){
        }else {
//            unFinishText.setText(getString(R.string.student));
            getAdapterViewHelper(unFinishGridViewTag).setData(discussPersonList);
        }

    }


    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.finish_status_contacts_list_item_layout){
            //已完成
            isFinishLayoutExpand=!isFinishLayoutExpand;
            updateFinishStatusLayout(isFinishLayoutExpand,finishImage,finishGridView);

        }else if (v.getId()==R.id.unfinish_status_contacts_list_item_layout){
            //未完成
            isUnFinishLayoutExpand=!isUnFinishLayoutExpand;
            updateFinishStatusLayout(isUnFinishLayoutExpand,unFinishImage,unFinishGridView);

        }else {
            super.onClick(v);
        }
    }

    private void updateFinishStatusLayout(boolean isFinishStatusLayoutExpand, ImageView
            finishStatusImage, GridView finishStatusGridView) {
        finishStatusImage.setImageResource(isFinishStatusLayoutExpand? R.drawable.list_exp_up :
                R.drawable.list_exp_down);
        finishStatusGridView.setVisibility(isFinishStatusLayoutExpand?View.VISIBLE:View.GONE);
    }

}
