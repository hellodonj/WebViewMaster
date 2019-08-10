package com.galaxyschool.app.wawaschool.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.LearningStatisticActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.ReMarkTaskListActivity;
import com.galaxyschool.app.wawaschool.StudentMemberListActivity;
import com.galaxyschool.app.wawaschool.adapter.StatisticDetailAdapter;
import com.galaxyschool.app.wawaschool.adapter.StudentListAdapter;
import com.galaxyschool.app.wawaschool.helper.StatisticNetHelper;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.StatisticBean;
import com.galaxyschool.app.wawaschool.pojo.StatisticBeanListResult;
import com.lqwawa.apps.views.charts.PieHelper;
import com.lqwawa.apps.views.charts.PieView;
import com.lqwawa.intleducation.module.discovery.ui.ArrangementWorkActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * ======================================================
 * Describe:学习统计
 * ======================================================
 */
public class LearningStatisticFragment extends ContactsListFragment {

    public static LearningStatisticFragment newInstance(Bundle args) {
        LearningStatisticFragment fragment = new LearningStatisticFragment();
        if (args != null) {
            fragment.setArguments(args);
        }
        return fragment;
    }

    private ScrollView scrollView;
    private PieView pieView;
    private TextView markDetailTextV;
    private TextView completeDetailView;
    private RecyclerView recyclerView;
    private RecyclerView studentListRV;
    private StatisticDetailAdapter detailAdapter;
    private StudentListAdapter adapter;
    private String[] itemDataArray = null;
    private List<StatisticBean> beanList;
    private List<Integer> colors = null;
    private int statisticType;
    private int learnType;
    private StatisticBean taskInfo;
    private String classId;
    private int courseId;
    private int roleType = -1;
    private String studentId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_learning_statistics, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadIntentData();
        initViews();
        initColors();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void loadIntentData() {
        Bundle args = getArguments();
        if (args != null) {
            statisticType = args.getInt(LearningStatisticActivity.Constants.STATISTIC_TYPE);
            learnType = args.getInt(LearningStatisticActivity.Constants.LEARNING_TYPE);
            classId = args.getString(LearningStatisticActivity.Constants.CLASS_ID);
            courseId = args.getInt(LearningStatisticActivity.Constants.COURSE_ID);
            roleType = args.getInt(LearningStatisticActivity.Constants.ROLE_TYPE, -1);
            studentId = args.getString(LearningStatisticActivity.Constants.STUDENT_ID);
        }
        if (statisticType == LearningStatisticActivity.STATISTIC_TYPE.LEARNING_TYPE) {
            itemDataArray = getResources().getStringArray(R.array.array_grade_detail);
        } else if (statisticType == LearningStatisticActivity.STATISTIC_TYPE.COURSE_TYPE) {
            if (learnType == LearningStatisticActivity.LEARNING_TYPE.HOMEWORK_INTRO_RATE) {
                itemDataArray = getResources().getStringArray(R.array.array_intro_detail);
            } else if (learnType == LearningStatisticActivity.LEARNING_TYPE.HOMEWORK_MARK_RATE) {
                itemDataArray = getResources().getStringArray(R.array.array_mark_detail);
            } else if (learnType == LearningStatisticActivity.LEARNING_TYPE.HOMEWORK_COMPLETE_RATE) {
                itemDataArray = getResources().getStringArray(R.array.array_complete_detail);
            }
        }
    }

    /**
     * @return 老师查看学生统计
     */
    private boolean isTeacherLook() {
        boolean isTeacherLook = false;
        if (roleType == RoleType.ROLE_TYPE_TEACHER
                && TextUtils.isEmpty(studentId)
                && statisticType == LearningStatisticActivity.STATISTIC_TYPE.LEARNING_TYPE) {
            isTeacherLook = true;
        }
        return isTeacherLook;
    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        pieView = (PieView) findViewById(R.id.pie_view);
        markDetailTextV = (TextView) findViewById(R.id.tv_mark_detail);
        completeDetailView = (TextView) findViewById(R.id.tv_complete_detail);
        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        studentListRV = (RecyclerView) findViewById(R.id.rv_student_list);
        if (isTeacherLook()) {
            scrollView.setVisibility(View.GONE);
            studentListRV.setVisibility(View.VISIBLE);
        }
    }

    private void initColors() {
        colors = StatisticNetHelper.getColorsList();
    }

    private void loadData() {
        if (statisticType == LearningStatisticActivity.STATISTIC_TYPE.LEARNING_TYPE) {
            loadLearningData();
        } else if (statisticType == LearningStatisticActivity.STATISTIC_TYPE.COURSE_TYPE) {
            if (learnType == LearningStatisticActivity.LEARNING_TYPE.HOMEWORK_INTRO_RATE) {
                loadHomeWorkIntroRate();
            } else if (learnType == LearningStatisticActivity.LEARNING_TYPE.HOMEWORK_MARK_RATE) {
                loadMarkRate();
            } else if (learnType == LearningStatisticActivity.LEARNING_TYPE.HOMEWORK_COMPLETE_RATE) {
                loadCompleteRate();
            }
        }
    }

    private void loadHomeWorkIntroRate() {
        StatisticNetHelper netHelper = new StatisticNetHelper();
        netHelper.setCallListener(this::updateViewData).getCourseResSetRate(courseId, classId);
    }

    private void loadMarkRate() {
        StatisticNetHelper netHelper = new StatisticNetHelper();
        netHelper.setCallListener(this::updateViewData).getTaskCorrectRate(courseId, classId);
    }

    private void loadCompleteRate() {
        StatisticNetHelper netHelper = new StatisticNetHelper();
        netHelper.setCallListener(this::updateViewData).getTaskCompleteRate(courseId, classId);
    }

    private void loadLearningData() {
        StatisticNetHelper netHelper = new StatisticNetHelper();
        netHelper.setCallListener(this::updateViewData).getTaskDetailStatistic(courseId, classId,
                learnType + 1, isTeacherLook() ? "" : studentId);
    }

    private void initListData() {
        if (beanList == null) {
            beanList = new ArrayList<>();
        } else {
            beanList.clear();
        }
        if (itemDataArray != null && itemDataArray.length > 0) {
            for (int i = 0; i < itemDataArray.length; i++) {
                StatisticBean bean = new StatisticBean();
                if (statisticType == LearningStatisticActivity.STATISTIC_TYPE.LEARNING_TYPE) {
                    bean.setColor(colors.get(i));
                    if (i == 0) {
                        bean.setNumber(taskInfo.getExcellentNum());
                    } else if (i == 1) {
                        bean.setNumber(taskInfo.getGoodNum());
                    } else if (i == 2) {
                        bean.setNumber(taskInfo.getFairNum());
                    } else if (i == 3) {
                        bean.setNumber(taskInfo.getFailNum());
                    }
                } else if (statisticType == LearningStatisticActivity.STATISTIC_TYPE.COURSE_TYPE) {
                    if (i == 0) {
                        bean.setColor(colors.get(0));
                        bean.setNumber(taskInfo.getImportantNum());
                        if (learnType == LearningStatisticActivity.LEARNING_TYPE.HOMEWORK_INTRO_RATE) {
                            bean.setShowRightArrow(true);
                        } else {
                            bean.setShowRightArrow(false);
                        }
                    } else {
                        bean.setNumber(taskInfo.getUnImportantNum());
                        bean.setShowRightArrow(true);
                        bean.setColor(colors.get(3));
                    }
                }
                bean.setStatisticType(statisticType);
                bean.setTitle(itemDataArray[i]);
                bean.setLearningType(learnType);
                beanList.add(bean);
            }
        }
    }

    private void updateViewData(Object result) {
        if (result == null) {
            return;
        }
        if (statisticType == LearningStatisticActivity.STATISTIC_TYPE.LEARNING_TYPE) {
            updateLearnViewData((StatisticBeanListResult) result);
        } else {
            taskInfo = (StatisticBean) result;
            updateChartData();
            initListData();
            updateAdapter();
        }
    }

    private void updateLearnViewData(StatisticBeanListResult listResult) {
        beanList = listResult.getModel().getData();
        if (beanList == null || beanList.size() == 0) {
            return;
        }
        if (isTeacherLook()) {
            updateStudentListAdapter();
        } else {
            taskInfo = beanList.get(0);
            updateChartData();
            initListData();
            updateAdapter();
            //更新任务title的数据
            if (getActivity() != null && getActivity() instanceof LearningStatisticActivity) {
                ((LearningStatisticActivity) getActivity()).updateHomeWorkRecordView(taskInfo);
            }
        }
    }

    private void updateStudentListAdapter() {
        if (adapter == null) {
            adapter = new StudentListAdapter(beanList);
            studentListRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            studentListRV.addItemDecoration(new DividerItemDecoration(getActivity(),
                    DividerItemDecoration.VERTICAL));
            studentListRV.setAdapter(adapter);
            adapter.setOnItemClick(position -> {
                LearningStatisticActivity.start(getActivity(), courseId, beanList.get(position).getStudentName(),
                        classId, roleType, beanList.get(position).getStudentId());
            });
        }
    }

    private void updateChartData() {
        if (statisticType == LearningStatisticActivity.STATISTIC_TYPE.LEARNING_TYPE) {
            markDetailTextV.setText(getString(R.string.str_total_homework_mark, taskInfo.getTotalMarkNum()));
        } else if (statisticType == LearningStatisticActivity.STATISTIC_TYPE.COURSE_TYPE) {
            if (learnType == LearningStatisticActivity.LEARNING_TYPE.HOMEWORK_INTRO_RATE) {
                markDetailTextV.setText(getString(R.string.str_total_res,
                        taskInfo.getTotalNum()));
            } else if (learnType == LearningStatisticActivity.LEARNING_TYPE.HOMEWORK_MARK_RATE) {
                markDetailTextV.setText(getString(R.string.str_need_mark_homework_count,
                        taskInfo.getTotalNum()));
            } else if (learnType == LearningStatisticActivity.LEARNING_TYPE.HOMEWORK_COMPLETE_RATE) {
                markDetailTextV.setText(getString(R.string.str_need_commit_homework_count,
                        taskInfo.getTotalNum()));
                completeDetailView.setText(getString(R.string.str_complete_rate_detail,
                        taskInfo.getAlreadySetTaskNum(), taskInfo.getClassSize()));
                completeDetailView.setVisibility(View.VISIBLE);
            }
        }

        //显示图表数据
        ArrayList<PieHelper> pieHelpers = new ArrayList<>();
        if (statisticType == LearningStatisticActivity.STATISTIC_TYPE.LEARNING_TYPE) {
            int totalNum = taskInfo.getTotalMarkNum();
            if (totalNum > 0) {
                List<StatisticBean> percentBeans = new ArrayList<>();
                StatisticBean perBean = null;
                int excell = taskInfo.getExcellentNum() * 100 / totalNum;
                if (excell > 0) {
                    perBean = new StatisticBean();
                    perBean.setPercent(excell);
                    perBean.setColor(colors.get(0));
                    percentBeans.add(perBean);
                }
                int good = taskInfo.getGoodNum() * 100 / totalNum;
                if (good > 0) {
                    perBean = new StatisticBean();
                    perBean.setPercent(good);
                    perBean.setColor(colors.get(1));
                    percentBeans.add(perBean);
                }
                int fair = taskInfo.getFairNum() * 100 / totalNum;
                if (fair > 0) {
                    perBean = new StatisticBean();
                    perBean.setPercent(fair);
                    perBean.setColor(colors.get(2));
                    percentBeans.add(perBean);
                }

                int fail = taskInfo.getFailNum() * 100 / totalNum;
                if (fail > 0) {
                    perBean = new StatisticBean();
                    perBean.setPercent(fail);
                    perBean.setColor(colors.get(3));
                    percentBeans.add(perBean);
                }

                if (percentBeans.size() > 1) {
                    int totalPercent = 0;
                    for (int i = 0; i < percentBeans.size(); i++) {
                        totalPercent = totalPercent + percentBeans.get(i).getPercent();
                    }

                    if (totalPercent < 100) {
                        StatisticBean lastBean = percentBeans.get(percentBeans.size() - 1);
                        lastBean.setPercent(lastBean.getPercent() + (100 - totalPercent));
                    }
                }

                if (percentBeans.size() > 0) {
                    for (int i = 0; i < percentBeans.size(); i++) {
                        pieHelpers.add(new PieHelper(percentBeans.get(i).getPercent(), " ",
                                percentBeans.get(i).getColor()));
                    }
                }
            } else {
                //没有数据
                pieHelpers.add(new PieHelper(100, " ", Color.parseColor(
                        "#bcbcbc")));
            }
        } else if (statisticType == LearningStatisticActivity.STATISTIC_TYPE.COURSE_TYPE) {
            int importantNum = taskInfo.getImportantNum();
            int unImportantNum = taskInfo.getUnImportantNum();
            int totalNum = importantNum + unImportantNum;
            int importantPercent = 0;
            if (totalNum > 0) {
                //有数据
                importantPercent = importantNum * 100 / totalNum;
                pieHelpers.add(new PieHelper(importantPercent, " ", colors.get(0)));
                pieHelpers.add(new PieHelper(100 - importantNum, " ", colors.get(3)));
            } else {
                //没有数据
                pieHelpers.add(new PieHelper(100, " ", Color.parseColor(
                        "#bcbcbc")));
            }
        }
        pieView.setDate(pieHelpers);
    }

    private void updateAdapter() {
        detailAdapter = new StatisticDetailAdapter(beanList);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity()){
            @Override
            public boolean canScrollVertically() {
                return super.canScrollVertically();
            }
        };
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(detailAdapter);
        detailAdapter.setOnItemClick(this::onItemClick);
    }

    private void onItemClick(int position) {
        if (statisticType == LearningStatisticActivity.STATISTIC_TYPE.COURSE_TYPE) {
            //课程统计
            if (learnType == LearningStatisticActivity.LEARNING_TYPE.HOMEWORK_INTRO_RATE) {
                //作业布置率
                enterIntroDetailActivity(position);
            } else if (learnType == LearningStatisticActivity.LEARNING_TYPE.HOMEWORK_MARK_RATE) {
                //作业批阅率
                enterMarkDetailActivity(position);
            } else if (learnType == LearningStatisticActivity.LEARNING_TYPE.HOMEWORK_COMPLETE_RATE) {
                //作业完成率
                enterCompleteDetailActivity(position);
            }
        }
    }

    private void enterIntroDetailActivity(int position) {
        Bundle bundle = getArguments();
        if (position == 0) {
            //已布置
            ArrangementWorkActivity.start(getActivity(),String.valueOf(courseId),classId,bundle);
        } else if (position == 1) {
            //未布置
            ArrangementWorkActivity.start(getActivity(),String.valueOf(courseId),classId,bundle);
        }
    }

    private void enterMarkDetailActivity(int position) {
        if (position == 1){
            ReMarkTaskListActivity.start(getActivity());
        }
    }

    private void enterCompleteDetailActivity(int position) {
        if (position == 1) {
            StudentMemberListActivity.start(getActivity(), courseId,
                    StudentMemberListActivity.FROM_TYPE.FROM_COURSE_STATISTIC);
        }
    }
}
