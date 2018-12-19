package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.Common;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.db.dto.LocalCourseDTO;
import com.galaxyschool.app.wawaschool.fragment.resource.ResourceBaseFragment;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.slide.CreateSlideHelper;
import com.galaxyschool.app.wawaschool.slide.SlideManagerHornForPhone;
import com.oosic.apps.iemaker.base.SlideManager;

import java.io.File;

/**
 * Created by Administrator on 2016/12/10.
 */

public class WawaCourseChoiceFragment extends ResourceBaseFragment {
    public static final String TAG = WawaCourseChoiceFragment.class.getSimpleName();
    public static String COURSEMODE = "course_mode";
    public static String PASS_TASK_TITLE = "pass_task_title";
    private int mode = 0;
    private int orientationType = 0;
    private int courseType = 0;
    private TextView courseTitle, landText, portText, whiteText, imageText, cameraText, readText,
            recordText;
    private ImageView courseBack, landImage, portImage, landIcon, portIcon, whiteIcon, imageIcon,
            cameraIcon, readIcon, recordIcon;
    private LinearLayout landLayout, portLayout, whiteLayout, imageLayout, caremaLayout, commitlayout,
            readCourseLayout, recordCourseLayout;
    private boolean isCreateIntroduction;//学习任务（true） lq云板（false）
    private UserInfo stuUserInfo;//保存孩子的信息
    private String makeTitle;

    //用来区分点读课件0和录音课件1
    public interface CourseMode {
        int READ = 0;
        int RECORD = 1;
    }

    //横屏与竖屏
    public interface Orientation {
        int LAND = 0;
        int PORT = 1;
    }

    //制作课件的类型
    public interface CousreType {
        int WHITEBOARD = 0;
        int IMAGE = 1;
        int CAMERA = 2;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wawa_course_choice, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    @Override
    public void importResource(String title, int type) {

    }

    @Override
    public UserInfo getUserInfo() {
        return stuUserInfo;
    }

    public void initViews() {
        if (getArguments() != null){
            //获得孩子信息
            stuUserInfo = (UserInfo) getArguments().getSerializable(UserInfo.class.getSimpleName());
            makeTitle = getArguments().getString(PASS_TASK_TITLE);
        }
        isCreateIntroduction = getArguments().getBoolean(SelectedReadingDetailFragment.Constants.INTORDUCTION_CREATE);
        courseTitle = (TextView) findViewById(R.id.course_title);
        if (courseTitle != null) {
            courseTitle.setText(getString(R.string.createspace));
        }
        courseBack = (ImageView) findViewById(R.id.course_left_button);
        courseBack.setOnClickListener(this);
        //横屏
        landLayout = (LinearLayout) findViewById(R.id.land_layout);
        landLayout.setOnClickListener(this);
        landImage = (ImageView) findViewById(R.id.land_image);
        landText = (TextView) findViewById(R.id.land_text);
        landIcon = (ImageView) findViewById(R.id.land_icon);
        //竖屏
        portLayout = (LinearLayout) findViewById(R.id.port_layout);
        portLayout.setOnClickListener(this);
        portImage = (ImageView) findViewById(R.id.port_image);
        portText = (TextView) findViewById(R.id.port_text);
        portIcon = (ImageView) findViewById(R.id.port_icon);
        //白版
        whiteLayout = (LinearLayout) findViewById(R.id.layout_whiteboard);
        whiteLayout.setOnClickListener(this);
        whiteText = (TextView) findViewById(R.id.whiteboard);
        whiteIcon = (ImageView) findViewById(R.id.whiteboard_icon);
        //相册
        imageLayout = (LinearLayout) findViewById(R.id.layout_image);
        imageLayout.setOnClickListener(this);
        imageText = (TextView) findViewById(R.id.image);
        imageIcon = (ImageView) findViewById(R.id.image_icon);
        //照相
        caremaLayout = (LinearLayout) findViewById(R.id.layout_camera);
        caremaLayout.setOnClickListener(this);
        cameraText = (TextView) findViewById(R.id.camera);
        cameraIcon = (ImageView) findViewById(R.id.camera_icon);
        //确定提交
        commitlayout = (LinearLayout) findViewById(R.id.commit_layout);
        commitlayout.setOnClickListener(this);
        //点读课件
        readCourseLayout = (LinearLayout) findViewById(R.id.layout_read);
        readCourseLayout.setOnClickListener(this);
        readIcon = (ImageView) findViewById(R.id.read_icon);
        readText = (TextView) findViewById(R.id.read);
        //录音课件
        recordCourseLayout = (LinearLayout) findViewById(R.id.layout_record);
        recordCourseLayout.setOnClickListener(this);
        recordIcon = (ImageView) findViewById(R.id.record_icon);
        recordText = (TextView) findViewById(R.id.record);
    }

    @Override
    public void onResume() {
        super.onResume();
        resetConfig();
    }

    public void resetConfig() {
        lqCourseType(CourseMode.READ);
        checkOrientation(Orientation.LAND);
        checkCourseType(CousreType.WHITEBOARD);
    }

    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.course_left_button) {
            if (isCreateIntroduction) {
                getActivity().setResult(Activity.RESULT_OK);
            }
            finish();
        } else if (v.getId() == R.id.layout_read) {
            lqCourseType(CourseMode.READ);
        } else if (v.getId() == R.id.layout_record) {
            lqCourseType(CourseMode.RECORD);
        } else if (v.getId() == R.id.land_layout) {
            checkOrientation(Orientation.LAND);
        } else if (v.getId() == R.id.port_layout) {
            checkOrientation(Orientation.PORT);
        } else if (v.getId() == R.id.layout_whiteboard) {
            checkCourseType(CousreType.WHITEBOARD);
        } else if (v.getId() == R.id.layout_image) {
            checkCourseType(CousreType.IMAGE);
        } else if (v.getId() == R.id.layout_camera) {
            checkCourseType(CousreType.CAMERA);
        } else if (v.getId() == R.id.commit_layout) {
            createCourseSlide();
        }
    }

    public void lqCourseType(int lqType) {
        if (lqType == CourseMode.READ) {
            if (mode != lqType) {
                mode = lqType;
                readIcon.setImageResource(R.drawable.select_green);
                readText.setTextColor(getResources().getColor(R.color.green));
                recordIcon.setImageResource(R.drawable.select_gray);
                recordText.setTextColor(getResources().getColor(R.color.text_black));
            }
        } else if (lqType == CourseMode.RECORD) {
            if (mode != lqType) {
                mode = lqType;
                readIcon.setImageResource(R.drawable.select_gray);
                readText.setTextColor(getResources().getColor(R.color.text_black));
                recordIcon.setImageResource(R.drawable.select_green);
                recordText.setTextColor(getResources().getColor(R.color.green));
            }
        }
    }

    public void checkOrientation(int orientation) {
        if (orientation == Orientation.LAND) {
            if (orientationType != orientation) {
                orientationType = orientation;
                landImage.setImageResource(R.drawable.create_course_dialog_checked_l);
                landText.setTextColor(getResources().getColor(R.color.green));
                landIcon.setImageResource(R.drawable.select_green);
                portImage.setImageResource(R.drawable.create_course_dialog_unchecked_p);
                portText.setTextColor(getResources().getColor(R.color.text_black));
                portIcon.setImageResource(R.drawable.select_gray);
            }
        } else if (orientation == Orientation.PORT) {
            if (orientationType != orientation)
                orientationType = orientation;
            landImage.setImageResource(R.drawable.create_course_dialog_unchecked_l);
            landText.setTextColor(getResources().getColor(R.color.text_black));
            landIcon.setImageResource(R.drawable.select_gray);
            portImage.setImageResource(R.drawable.create_course_dialog_checked_p);
            portText.setTextColor(getResources().getColor(R.color.green));
            portIcon.setImageResource(R.drawable.select_green);
        }
    }

    public void checkCourseType(int type) {
        if (type == CousreType.WHITEBOARD) {
            if (courseType != type) {
                courseType = type;
                whiteText.setTextColor(getResources().getColor(R.color.green));
                whiteIcon.setImageResource(R.drawable.select_green);
                imageText.setTextColor(getResources().getColor(R.color.text_black));
                imageIcon.setImageResource(R.drawable.select_gray);
                cameraText.setTextColor(getResources().getColor(R.color.text_black));
                cameraIcon.setImageResource(R.drawable.select_gray);
            }
        } else if (type == CousreType.IMAGE) {
            if (courseType != type) {
                courseType = type;
                whiteText.setTextColor(getResources().getColor(R.color.text_black));
                whiteIcon.setImageResource(R.drawable.select_gray);
                imageText.setTextColor(getResources().getColor(R.color.green));
                imageIcon.setImageResource(R.drawable.select_green);
                cameraText.setTextColor(getResources().getColor(R.color.text_black));
                cameraIcon.setImageResource(R.drawable.select_gray);
            }
        } else if (type == CousreType.CAMERA) {
            if (courseType != type) {
                courseType = type;
                whiteText.setTextColor(getResources().getColor(R.color.text_black));
                whiteIcon.setImageResource(R.drawable.select_gray);
                imageText.setTextColor(getResources().getColor(R.color.text_black));
                imageIcon.setImageResource(R.drawable.select_gray);
                cameraText.setTextColor(getResources().getColor(R.color.green));
                cameraIcon.setImageResource(R.drawable.select_green);
            }
        }
    }

    public void createCourseSlide() {
        if (mode == CourseMode.READ) {
            createReadSide();
        } else if (mode == CourseMode.RECORD) {
            int haveFree = Utils.checkStorageSpace(getActivity());
            if (haveFree == 0) {
                createRecordSide();
            }
        }
    }

    //创建点读课件
    public void createReadSide() {
        if (courseType == CousreType.WHITEBOARD) {
            createSlide(CreateSlideHelper.SLIDETYPE_WHITEBOARD, orientationType, CourseMode.READ);
        } else if (courseType == CousreType.IMAGE) {
            createSlide(CreateSlideHelper.SLIDETYPE_IMAGE, orientationType, CourseMode.READ);
        } else if (courseType == CousreType.CAMERA) {
            createSlide(CreateSlideHelper.SLIDETYPE_CAMERA, orientationType, CourseMode.READ);
        }
    }

    //创建录音课件
    public void createRecordSide() {
        if (courseType == CousreType.WHITEBOARD) {
            if (isCreateIntroduction) {
                createRecordCourse(orientationType, true, makeTitle);
            } else {
                createRecordCourse(orientationType, false, makeTitle);
            }
        } else if (courseType == CousreType.IMAGE) {
            createSlide(CreateSlideHelper.SLIDETYPE_IMAGE, orientationType, CourseMode.RECORD);
        } else if (courseType == CousreType.CAMERA) {
            createSlide(CreateSlideHelper.SLIDETYPE_CAMERA, orientationType, CourseMode.RECORD);
        }
    }

    public void createSlide(int slideType, int orientationType, int courseMode) {
        int haveFree = Utils.checkStorageSpace(getActivity());
        if (haveFree == 0) {
            CreateSlideHelper.CreateSlideParam param = new CreateSlideHelper.CreateSlideParam();
            param.mFragment = WawaCourseChoiceFragment.this;
            param.mEntryType = Common.LIST_TYPE_SHARE;
            param.mEditable = true;
            param.mSlideType = slideType;
            if (slideType == CreateSlideHelper.SLIDETYPE_IMAGE) {
                param.mIsPickOneImage = false;
            }
            //孩子信息
            UserInfo userInfo = stuUserInfo;
            if (userInfo == null) {
                userInfo = getUserInfo();
            }
            if (userInfo != null ) {
                param.mUserInfo = userInfo;
                //孩子信息
                if (!TextUtils.isEmpty(userInfo.getMemberId())) {
                    param.mMemberId = userInfo.getMemberId();
                }
            }
            param.mSlideSaveBtnParam = new CreateSlideHelper.SlideSaveBtnParam(true, true, true);
            param.mOrientation = orientationType;
            param.courseMode = courseMode;
            if (isCreateIntroduction) {
                param.isFromStudyTask = true;
                param.fromType = SlideManagerHornForPhone.FromWhereData.FROM_STUDY_TASK_COURSE;
                param.mTitle = makeTitle;
            } else {
                //发送选项控制的参数
                param.fromType = SlideManagerHornForPhone.FromWhereData.FROM_LQCLOUD_COURSE;
                param.isFromLqBoard = true;
            }
            CreateSlideHelper.createSlide(param);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //响应相册和拍照的回调方法
        super.onActivityResult(requestCode, resultCode,data);
        if (requestCode == CreateSlideHelper.SLIDETYPE_RECORD_COURSE || requestCode == SlideManager.REQUEST_CODE_PICK_IMAGE
                || requestCode == (Common.ACTIVITY_REQUEST_CAMERA_PATH_BASE + Common.LIST_TYPE_SHARE)) {
            CreateSlideHelper.processActivityResule(null, this, requestCode, resultCode, data);
            //点读课件的回调方法
        } else if (requestCode == Common.ACTIVITY_REQUEST_EDITTEMPLATES_BASE + Common.LIST_TYPE_SHARE) {
            if (isCreateIntroduction) {
                if (data != null) {
                    String slidePath = data.getStringExtra(SlideManager.EXTRA_SLIDE_PATH);
                    Intent intent = new Intent();
                    intent.putExtra("path", slidePath);
                    getActivity().setResult(Activity.RESULT_OK, intent);
                }
            }else {
                //保存和发送之后给一个toast提示
                if (data != null) {
                    String slidePath = data.getStringExtra(SlideManagerHornForPhone.SAVE_PATH);
                    if (!TextUtils.isEmpty(slidePath)){
                        TipMsgHelper.ShowLMsg(getActivity(), getActivity().getString(R.string.lqcourse_save_local));
                    }
                }
            }
            finish();
            //录音课件的回调
        } else if (requestCode == REQUEST_CODE_SLIDE) {
            if (data != null) {
                String slidePath = data.getStringExtra(com.oosic.apps.iemaker.base.SlideManager.EXTRA_SLIDE_PATH);
                String coursePath = data.getStringExtra(com.oosic.apps.iemaker.base.SlideManager.EXTRA_COURSE_PATH);
                if (!TextUtils.isEmpty(slidePath)) {
                    //只打开素材没有录制微课，此时slidePath不空，coursePath空值，此时删除素材
                    if (slidePath.endsWith(File.separator)) {
                        slidePath = slidePath.substring(0, slidePath.length() - 1);
                    }
                    if (!TextUtils.equals(slidePath,coursePath)) {
                        LocalCourseDTO.deleteLocalCourseByPath(getActivity(), getMemeberId(), slidePath, true);
                    }
                    if (isCreateIntroduction) {
                        Intent intent = new Intent();
                        intent.putExtra("path", coursePath);
                        getActivity().setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                    finish();
                }
            }else {
                //学习任务的保存 不需要更新数据
                if (isCreateIntroduction) {
                    finish();
                }
            }
        }
    }
}
