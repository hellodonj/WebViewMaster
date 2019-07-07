package com.lqwawa.mooc.select;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterFragment;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.factory.helper.LQCourseHelper;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.coursedetails.CourseDetailItemParams;
import com.lqwawa.intleducation.module.discovery.vo.ChapterVo;
import com.lqwawa.intleducation.module.discovery.vo.CourseDetailsVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.mooc.adapter.SelectMoreAdapter;
import com.lqwawa.mooc.view.CustomExpandableListView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lqwawa.intleducation.base.ui.MyBaseFragment.FRAGMENT_BUNDLE_OBJECT;

/**
 * 描述: 播放列表的fragment
 * 作者|时间: djj on 2019/6/26 0026 下午 3:45
 */
public class PlayListViewFragment extends AdapterFragment implements SelectMoreAdapter.CheckInterface {

    private View mRootView;
    private TopBar mTopBar;
    private CustomExpandableListView mExpandableListView;
    // 习课程详情Tab参数
    private CourseDetailItemParams mDetailItemParams;
    private int totalCount = 0;//所选项目的数量
    private SelectMoreAdapter mMoreAdapter;
    private CourseDetailsVo courseDetailsVo;
    private List<ChapterVo> tempChapterList = new ArrayList<>();
    private List<ChapterVo> chapterList;
    private List<ChapterVo> children;
    private Map<String, List<ChapterVo>> childMap = new HashMap<String, List<ChapterVo>>();// 子元素数据列表

    private List<Map<String, Object>> playListVo;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = mRootView = inflater.inflate(R.layout.fragment_play_list_view, null);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadIntentData();
        loadViews();
        loadData();
    }

    /**
     * 获取上页传过来的数据
     */
    private void loadIntentData() {
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(FRAGMENT_BUNDLE_OBJECT)) {
            mDetailItemParams = (CourseDetailItemParams) arguments.getSerializable(FRAGMENT_BUNDLE_OBJECT);
        }
    }

    /**
     * 初始化
     */
    private void loadViews() {
        mTopBar = (TopBar) mRootView.findViewById(R.id.top_bar);
        mTopBar.setBack(true);
        mTopBar.setTitle(R.string.label_picker_chapter);
        // 选择章节确认
        mTopBar.setRightFunctionText1(R.string.label_confirm, view -> confirm());
        mExpandableListView = (CustomExpandableListView) mRootView.findViewById(R.id.expandable_list_view);
    }

    /**
     * 获取数据
     */
    private void loadData() {
        String token = UserHelper.getUserId();
        if (mDetailItemParams.isParentRole()) {
            // 家长身份
            token = mDetailItemParams.getMemberId();
        }
        String courseId = mDetailItemParams.getCourseId();
        String schoolIds = null;
        if (UserHelper.isLogin()
                && UserHelper.isTeacher(UserHelper.getUserInfo().getRoles())) {
            //仅在登陆用户是教师身份的情况下才传SchoolIds 以便server用于判断是否显示联合备课内容
            schoolIds = UserHelper.getUserInfo().getSchoolIds();
        }

        showLoadingDialog();
        LQCourseHelper.requestChapterByCourseId(token, courseId, schoolIds, new Callback());
    }

    /**
     * @author mrmedici
     * @desc 获取课程详情以及课程大纲的统一回调处理
     */
    private class Callback implements DataSource.Callback<CourseDetailsVo> {

        @Override
        public void onDataLoaded(CourseDetailsVo courseDetailsVo) {
            dismissLoadingDialog();
            PlayListViewFragment.this.courseDetailsVo = courseDetailsVo;
            chapterList = courseDetailsVo.getChapters();
            if (EmptyUtil.isNotEmpty(chapterList)) {
                for (int i = 0; i < chapterList.size(); i++) {
                    ChapterVo chapterVo = chapterList.get(i);
                    if (chapterVo.isBuyed()){
                        tempChapterList.add(chapterVo);
                        children = chapterList.get(i).getChildren();
                        childMap.put(chapterList.get(i).getId(), children);
                    }
                }
            }
            updateList();
        }

        @Override
        public void onDataNotAvailable(int strRes) {
            UIUtil.showToastSafe(strRes);
        }
    }

    //更新UI
    private void updateList() {
        mMoreAdapter = new SelectMoreAdapter(getActivity(), tempChapterList, childMap);
        mMoreAdapter.setCheckInterface(PlayListViewFragment.this);
        mExpandableListView.setAdapter(mMoreAdapter);
        for (int j = 0; j < mMoreAdapter.getGroupCount(); j++) {
            mExpandableListView.expandGroup(j);
        }
    }

    @Override
    public void checkGroup(int groupPosition, boolean isChecked) {
        ChapterVo group = tempChapterList.get(groupPosition);
        List<ChapterVo> childs = childMap.get(group.getId());
        for (int i = 0; i < childs.size(); i++) {
            childs.get(i).setChoosed(isChecked);
        }
        mMoreAdapter.notifyDataSetChanged();
        calculate();
    }

    @Override
    public void checkChild(int groupPosition, int childPosition, boolean isChecked) {
        boolean allChildSameState = true;// 判断该组下面的所有子元素是否是同一种状态
        ChapterVo group = tempChapterList.get(groupPosition);
        List<ChapterVo> childs = childMap.get(group.getId());
        for (int i = 0; i < childs.size(); i++) {
            // 不全选中
            if (childs.get(i).isChoosed() != isChecked) {
                allChildSameState = false;
                break;
            }
        }
        //获取该时间段的选中项目状态
        if (allChildSameState) {
            group.setChoosed(isChecked);// 如果所有子元素状态相同，那么对应的组元素被设为这种统一状态
        } else {
            group.setChoosed(false);// 否则，组元素一律设置为未选中状态
        }
        mMoreAdapter.notifyDataSetChanged();
        calculate();
    }


    /**
     * 统计操作<br>
     * 1.先清空全局计数器<br>
     * 2.遍历所有子元素，只要是被选中状态的，就进行相关的计算操作<br>
     */
    private void calculate() {
        totalCount = 0;
        for (int i = 0; i < tempChapterList.size(); i++) {
            ChapterVo group = tempChapterList.get(i);
            List<ChapterVo> childs = childMap.get(group.getId());
            for (int j = 0; j < childs.size(); j++) {
                ChapterVo project = childs.get(j);
                if (project.isChoosed()) {
                    totalCount++;
                }
            }
        }
    }

    /**
     * 确定按钮操作
     */
    private void confirm() {
        String jsonString = getChapterIds();
        LQwawaHelper.requestResourceListByChapterIds(jsonString, new DataSource.Callback<ResponseVo>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(ResponseVo responseVo) {
                if (responseVo.isSucceed()) {
                    playListVo = (List<Map<String, Object>>) responseVo.getData();
                    List<String> list = new ArrayList<String>();
                    if (EmptyUtil.isNotEmpty(playListVo)) {
                        for (int i = 0; i < playListVo.size(); i++) {
                            Map<String, Object> map = playListVo.get(i);
                            int id1 = (int) map.get("resId");
                            StringBuffer sb = new StringBuffer();
                            sb.append(id1 + "-").append(19);
                            list.add(sb.toString());
                        }
                        // 通过EventBus通知
                        EventBus.getDefault().post(new EventWrapper(list, EventConstant.GENERATE_PLAY_LIST_EVENT));
                        getActivity().finish();
                    }else {
                        UIUtil.showToastSafe("无播放文件！");
                    }
                }
            }
        });
    }

    private String getChapterIds() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < tempChapterList.size(); i++) {
            ChapterVo group = tempChapterList.get(i);
            List<ChapterVo> childs = childMap.get(group.getId());
            for (int j = 0; j < childs.size(); j++) {
                ChapterVo project = childs.get(j);
                if (project.isChoosed()) {
                    if (builder.length() == 0) {
                        builder.append(project.getId());
                    } else {
                        builder.append(",").append(project.getId());
                    }
                }
            }
        }
        return builder.toString();
    }


}
