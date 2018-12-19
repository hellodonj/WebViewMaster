package com.lqwawa.intleducation.module.discovery.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseFragment;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.SuperGridView;
import com.lqwawa.intleducation.common.interfaces.OnLoadStatusChangeListener;
import com.lqwawa.intleducation.module.discovery.adapter.ResourceFolderAdapter;
import com.lqwawa.intleducation.module.discovery.adapter.TeacherCourseAdapter;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.discovery.vo.ResourceFolderVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2016/11/16.
 * email:man0fchina@foxmail.com
 */

public class TeacherDetailsFragment extends MyBaseFragment implements View.OnClickListener {
    private static final String TAG = "TeacherDetailsFragment";
    private SuperGridView gridView;
    private SuperGridView gridViewResource;

    private int type = 2;//2课程/3资源
    private String teacherId;

    private List<CourseVo> courseList;
    TeacherCourseAdapter teacherCourseAdapter;
    ResourceFolderAdapter resourceFolderAdapter;
    OnLoadStatusChangeListener onLoadStatusChangeListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_details_item, container, false);
        gridView = (SuperGridView) view.findViewById(R.id.common_gridview);
        gridViewResource = (SuperGridView) view.findViewById(R.id.resource_gv);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = TeacherDetailsFragment.this.getActivity();
        type = getArguments().getInt("type", 2);
        teacherId = getArguments().getString("id");
        initViews();
    }

    public void setOnLoadStatusChangeListener(OnLoadStatusChangeListener listener) {
        onLoadStatusChangeListener = listener;
    }

    public void updateData() {
        getData();
    }

    private void initViews() {
        if (type == 2) {//课程列表
            gridView.setVisibility(View.VISIBLE);
            gridViewResource.setVisibility(View.GONE);
            courseList = new ArrayList<CourseVo>();
            teacherCourseAdapter = new TeacherCourseAdapter(activity);
            gridView.setAdapter(teacherCourseAdapter);
            gridView.setNumColumns(2);
            gridView.setOnItemClickListener(new SuperGridView.OnItemClickListener() {
                @Override
                public void onItemClick(LinearLayout parent, View view, int position) {
                    CourseVo vo = (CourseVo) teacherCourseAdapter.getItem(position);
                    if (type == 2) {//课程
                        CourseDetailsActivity.start(activity, vo.getId(),true, UserHelper.getUserId());
                    }
                }
            });
            getData();
        } else if (type == 3) {//资源列表
            gridView.setVisibility(View.GONE);
            gridViewResource.setVisibility(View.VISIBLE);
            gridViewResource.setNumColumns(3);
            List<ResourceFolderVo> resourceFolderList = new ArrayList<ResourceFolderVo>();
            ResourceFolderVo vo = new ResourceFolderVo();
            vo.name = getString(R.string.learn_micro_course);
            vo.icon = R.drawable.learn_micro_course;
            vo.type = 1;
            vo.teacherId = teacherId;
            resourceFolderList.add(vo);

            vo = new ResourceFolderVo();
            vo.name = getString(R.string.learn_video);
            vo.icon = R.drawable.learn_video;
            vo.type = 2;
            vo.teacherId = teacherId;
            resourceFolderList.add(vo);

            vo = new ResourceFolderVo();
            vo.name = getString(R.string.learn_audio);
            vo.icon = R.drawable.learn_audio;
            vo.type = 3;
            vo.teacherId = teacherId;
            resourceFolderList.add(vo);

            vo = new ResourceFolderVo();
            vo.name = getString(R.string.learn_image);
            vo.icon = R.drawable.learn_image;
            vo.type = 4;
            vo.teacherId = teacherId;
            resourceFolderList.add(vo);

            vo = new ResourceFolderVo();
            vo.name = getString(R.string.learn_ppt_or_pdf);
            vo.icon = R.drawable.learn_ppt_or_pdf;
            vo.type = 5;
            vo.teacherId = teacherId;

            resourceFolderList.add(vo);
            vo = new ResourceFolderVo();
            vo.name = getString(R.string.other_resource);
            vo.icon = R.drawable.learn_other;
            vo.type = 6;
            vo.teacherId = teacherId;
            resourceFolderList.add(vo);
            resourceFolderAdapter = new ResourceFolderAdapter(activity);
            resourceFolderAdapter.setData(resourceFolderList);
            gridViewResource.setAdapter(resourceFolderAdapter);
            gridViewResource.setOnItemClickListener(new SuperGridView.OnItemClickListener() {

                @Override
                public void onItemClick(LinearLayout parent, View view, int position) {
                    ResourceFolderVo vo = (ResourceFolderVo) resourceFolderAdapter.getItem(position);
                    MicroCourseActivity.start(activity, vo.type, teacherId, 0);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {

    }

    private int pageIndex = 0;

    //拉去数据 just for courseList
    private void getData() {
        pageIndex = 0;
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("pageIndex", pageIndex);
        requestVo.addParams("pageSize", AppConfig.PAGE_SIZE);
        requestVo.addParams("dataType", type);
        requestVo.addParams("teacherId", teacherId);
        requestVo.addParams("resType", 1);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetTeacherDetailsById + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                if (onLoadStatusChangeListener != null) {
                    onLoadStatusChangeListener.onLoadSuccess();
                }
                if (type == 2) {//课程
                    ResponseVo<List<CourseVo>> result = JSON.parseObject(s,
                            new TypeReference<ResponseVo<List<CourseVo>>>() {
                            });
                    if (result.getCode() == 0) {
                        courseList = result.getData();
                        teacherCourseAdapter.setData(courseList);
                        teacherCourseAdapter.notifyDataSetChanged();
                        if (onLoadStatusChangeListener != null) {
                            onLoadStatusChangeListener.onLoadFinish( courseList != null &&
                                    courseList.size() == AppConfig.PAGE_SIZE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "获取老师详情数据失败:" + throwable.getMessage());
                if (onLoadStatusChangeListener != null) {
                    onLoadStatusChangeListener.onLoadFlailed();
                }
            }

            @Override
            public void onFinished() {
            }
        });
    }

    //拉去数据 just for courseList
    public void getMore() {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("pageIndex", pageIndex + 1);
        requestVo.addParams("pageSize", AppConfig.PAGE_SIZE);
        requestVo.addParams("dataType", type);
        requestVo.addParams("teacherId", teacherId);
        requestVo.addParams("resType", 1);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetTeacherDetailsById + requestVo.getParams());
        pageIndex = 0;
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                if (onLoadStatusChangeListener != null) {
                    onLoadStatusChangeListener.onLoadSuccess();
                }
                if (type == 2) {//课程
                    ResponseVo<List<CourseVo>> result = JSON.parseObject(s,
                            new TypeReference<ResponseVo<List<CourseVo>>>() {
                            });
                    if (result.getCode() == 0) {
                        if (result.getData() != null && result.getData().size() > 0) {
                            courseList = result.getData();
                            pageIndex += 1;
                            teacherCourseAdapter.addData(courseList);
                            teacherCourseAdapter.notifyDataSetChanged();
                            if (onLoadStatusChangeListener != null) {
                                onLoadStatusChangeListener.onLoadFinish(
                                        courseList.size() == AppConfig.PAGE_SIZE);
                            }

                        }
                    }
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "获取老师详情数据失败:" + throwable.getMessage());
                if (onLoadStatusChangeListener != null) {
                    onLoadStatusChangeListener.onLoadFlailed();
                }
            }

            @Override
            public void onFinished() {
            }
        });
    }
}
