package com.lqwawa.intleducation.module.tutorial.marking.require;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.libs.gallery.ImageBrowserActivity;
import com.libs.gallery.ImageInfo;
import com.lqwawa.client.pojo.SourceFromType;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.ImageUtil;
import com.lqwawa.intleducation.common.utils.image.LQwawaImageUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.StringCallback;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TaskEntity;
import com.lqwawa.intleducation.factory.helper.LQCourseHelper;
import com.lqwawa.intleducation.factory.helper.LessonHelper;
import com.lqwawa.intleducation.module.discovery.lessontask.missionrequire.MissionRequireFragment;
import com.lqwawa.intleducation.module.discovery.ui.task.list.TaskCommitParams;
import com.lqwawa.intleducation.module.learn.tool.TaskSliderHelper;
import com.lqwawa.intleducation.module.learn.vo.CourseData;
import com.lqwawa.intleducation.module.learn.vo.LqTaskCommitListVo;
import com.lqwawa.intleducation.module.learn.vo.LqTaskInfoVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mrmedici
 * @desc 帮辅批阅页面，任务要求的页面
 */
public class TaskRequirementActivity extends PresenterActivity<TaskRequirementContract.Presenter>
        implements TaskRequirementContract.View, View.OnClickListener {

    private static final String KEY_EXTRA_TASK_ENTITY = "KEY_EXTRA_TASK_ENTITY";

    private TopBar mTopBar;
    private ImageView mIvResIcon;
    private TextView mTvResName;
    private TextView mTvAccessDetail;
    private LinearLayout mBodyLayout;

    private TaskEntity mTaskEntity;
    private LqTaskInfoVo mTaskInfoVo;

    private MissionRequireFragment mInstance;

    @Override
    protected TaskRequirementContract.Presenter initPresenter() {
        return new TaskRequirementPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_task_requirement;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mTaskEntity = (TaskEntity) bundle.getSerializable(KEY_EXTRA_TASK_ENTITY);
        if (EmptyUtil.isEmpty(mTaskEntity)) return false;
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setBack(true);
        mTopBar.setTitle(R.string.title_watch_task_requirement);

        mIvResIcon = (ImageView) findViewById(R.id.iv_res_icon);
        mTvResName = (TextView) findViewById(R.id.tv_res_name);
        mTvAccessDetail = (TextView) findViewById(R.id.tv_access_details);
        mBodyLayout = (LinearLayout) findViewById(R.id.body_layout);

        mIvResIcon.setOnClickListener(this);
        mTvAccessDetail.setOnClickListener(this);

        // mTvResName.setText(mTaskEntity.getTitle());
        // ImageUtil.fillNotificationView(mIvResIcon, mTaskEntity.getResThumbnailUrl());

        MissionRequireFragment instance = (MissionRequireFragment) MissionRequireFragment.getInstance(null);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.lay_content, instance)
                .commit();

        this.mInstance = instance;

        // 使用新接口,拉数据
        /*String userId = UserHelper.getUserId();
        LessonHelper.getNewCommittedTaskByTaskId(Integer.toString(mTaskEntity.getT_TaskId()),
                userId,
                null,
                null, null, TaskCommitParams.TYPE_ALL, new DataSource.Callback<LqTaskCommitListVo>() {
                    @Override
                    public void onDataNotAvailable(int strRes) {

                    }

                    @Override
                    public void onDataLoaded(LqTaskCommitListVo lqTaskCommitListVo) {
                        if(!EmptyUtil.isEmpty(lqTaskCommitListVo) && !EmptyUtil.isEmpty(lqTaskCommitListVo.getTaskInfo())){
                            LqTaskInfoVo vo = lqTaskCommitListVo.getTaskInfo();
                            if(!EmptyUtil.isEmpty(vo.getDiscussContent())){
                                // 有任务要求文本
                                mBodyLayout.setVisibility(View.VISIBLE);
                                instance.updateViews(lqTaskCommitListVo);
                            }else{
                                // 没有任务要求文本
                                mBodyLayout.setVisibility(View.GONE);
                            }
                        }
                    }
                });*/
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.requestTaskInfoByTaskId(Integer.toString(mTaskEntity.getT_TaskId()));
    }

    @Override
    public void updateTaskInfoByTaskIdView(@NonNull LqTaskInfoVo taskInfoVo) {
        this.mTaskInfoVo = taskInfoVo;
        mTvResName.setText(taskInfoVo.getTaskTitle());
        int resType = getResType(mTaskInfoVo.getResId());
        mTvAccessDetail.setVisibility(isShowCourseDetail(resType) ? View.VISIBLE : View.INVISIBLE);
        String thumbnailUrl;
        if (resType == 1) {
            thumbnailUrl = getThumbnailUrl(taskInfoVo.getResThumbnailUrl());
        } else {
            thumbnailUrl = getThumbnailUrlForDoc(taskInfoVo.getResUrl());
        }
        LQwawaImageUtil.loadCommonIcon(mTvResName.getContext(), mIvResIcon,
                thumbnailUrl, R.drawable.img_def);
        // ImageUtil.fillNotificationView(mIvResIcon, taskInfoVo.getResThumbnailUrl());
        if (EmptyUtil.isNotEmpty(mInstance)) {
            if (!EmptyUtil.isEmpty(taskInfoVo.getDiscussContent())) {
                // 有任务要求文本
                mBodyLayout.setVisibility(View.VISIBLE);
                mInstance.updateViews(taskInfoVo);
            } else {
                // 没有任务要求文本
                mBodyLayout.setVisibility(View.GONE);
            }
        }
    }


    private int getResType(String resId) {
        if (!TextUtils.isEmpty(resId)) {
            if (resId.contains(",")) {
                resId = resId.substring(0, resId.indexOf(","));
            }
            if (!TextUtils.isEmpty(resId) && resId.contains("-")) {
                String[] ids = resId.split("-");
                if (ids != null && ids.length == 2) {
                    if (!TextUtils.isEmpty(ids[1]) && TextUtils.isDigitsOnly(ids[1])) {
                        return Integer.parseInt(ids[1]);
                    }
                }
            }
        }
        return -1;
    }

    private String getThumbnailUrl(String resThumbnailUrl) {
        if (!TextUtils.isEmpty(resThumbnailUrl) && resThumbnailUrl.contains(",")) {
            String[] thumbnails = resThumbnailUrl.split(",");
            if (thumbnails != null && thumbnails.length > 0) {
                return thumbnails[0];
            }
        }
        return null;
    }

    private boolean isShowCourseDetail(int resType) {
        if (resType == 1 || resType == 6 || resType == 20 || resType == 24) {
            return false;
        }
        return true;
    }

    public static String getThumbnailUrlForDoc(String resourceUrl) {
        if (TextUtils.isEmpty(resourceUrl)) {
            return resourceUrl;
        }
        String flag = null;
        if (resourceUrl.endsWith(".pdf")) {
            flag = ".pdf";
        }
        if (resourceUrl.endsWith(".pdfx")) {
            flag = ".pdfx";
        }
        if (resourceUrl.endsWith(".ppt")) {
            flag = ".ppt";
        }
        if (resourceUrl.endsWith(".pptx")) {
            flag = ".pptx";
        }
        if (resourceUrl.endsWith(".doc")) {
            flag = ".doc";
        }
        if (resourceUrl.endsWith(".docx")) {
            flag = ".docx";
        }
        if (!TextUtils.isEmpty(flag)) {
            resourceUrl = resourceUrl.replace(flag, "_split/1.jpg");
        }
        return resourceUrl;
    }


    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (EmptyUtil.isEmpty(mTaskInfoVo)) return;
        if (viewId == R.id.tv_access_details) {
            if (TaskSliderHelper.onTaskSliderListener != null) {
                String id = mTaskInfoVo.getResId();
                if (EmptyUtil.isNotEmpty(id) && id.contains("-")) {
                    String[] strings = id.split("-");
                    String resId = strings[0];
                    String resType = strings[1];
                    String title = mTaskInfoVo.getTaskTitle();
                    String resUrl = mTaskInfoVo.getResUrl();
                    String resThumbnailUrl = mTaskInfoVo.getResThumbnailUrl();
                    TaskSliderHelper.onTutorialMarkingListener.openCourseWareDetails(
                            this, false,
                            resId, Integer.parseInt(resType),
                            title, 1,
                            resUrl, resThumbnailUrl);
                }
            }
        } else if (viewId == R.id.iv_res_icon) {
            // 打开课件
            if (TaskSliderHelper.onTaskSliderListener != null) {
                String id = mTaskInfoVo.getResId();
                int resType = getResType(id);
                if (getResType(id) == 1) {
                   openImageList(mTaskInfoVo.getResUrl());
                } else {
                    if (EmptyUtil.isNotEmpty(id) && id.contains("-")) {
                        String[] strings = id.split("-");
                        String resId = strings[0];
                        if (resType == 6 || resType == 20 || resType == 24) {
                            TaskSliderHelper.onTaskSliderListener.viewPdfOrPPT(this, resId,
                                    resType, mTaskInfoVo.getTaskTitle(),
                                    mTaskInfoVo.getTaskCreateId(), SourceFromType.STUDY_TASK);
                        } else {
                            TaskSliderHelper.onTaskSliderListener.viewCourse(this, resId, resType, "", SourceFromType.STUDY_TASK);
                        }
                    }
                }
            }
        }
    }

    private void openImageList(String resUrl) {
        if (TextUtils.isEmpty(resUrl)) {
            return;
        }
        List<ImageInfo> resourceInfoList = new ArrayList<>();
        if (resUrl.contains(",")) {
            String[] urls = resUrl.split(",");
            if (urls != null && urls.length > 0) {
                for (String url : urls) {
                    ImageInfo newResourceInfo = new ImageInfo();
                    newResourceInfo.setResourceUrl(url.trim());
                    newResourceInfo.setResourceType(1);
                    resourceInfoList.add(newResourceInfo);
                }
            }
        }
        Intent intent = new Intent();
        intent.setClassName(MainApplication.getInstance().getPackageName(), "com.galaxyschool.app.wawaschool.imagebrowser.GalleryActivity");
        intent.putParcelableArrayListExtra(ImageBrowserActivity.EXTRA_IMAGE_INFOS, (ArrayList<? extends Parcelable>) resourceInfoList);
        intent.putExtra(ImageBrowserActivity.EXTRA_CURRENT_INDEX, 0);
        intent.putExtra(ImageBrowserActivity.ISPDF, false);

        intent.putExtra(ImageBrowserActivity.KEY_ISHIDEMOREBTN, false);
        intent.putExtra(ImageBrowserActivity.KEY_ISSHOWCOURSEANDREADING, false);
        intent.putExtra(ImageBrowserActivity.KEY_ISSHOWCOLLECT, false);
        startActivity(intent);
    }

    /**
     * 帮辅批阅任务要求页面的入口
     *
     * @param context 上下文对象
     */
    public static void show(@NonNull Context context, @NonNull TaskEntity taskEntity) {
        Intent intent = new Intent(context, TaskRequirementActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_EXTRA_TASK_ENTITY, taskEntity);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
