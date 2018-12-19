package com.galaxyschool.app.wawaschool.imagebrowser;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CollectionHelper;
import com.galaxyschool.app.wawaschool.common.DoCourseHelper;
import com.galaxyschool.app.wawaschool.common.PrefsManager;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.fragment.resource.ResourceBaseFragment;
import com.galaxyschool.app.wawaschool.pojo.ExerciseAnswerCardParam;
import com.galaxyschool.app.wawaschool.pojo.ExerciseItem;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.MediaData;
import com.galaxyschool.app.wawaschool.views.OrientationSelectDialog;
import com.lecloud.xutils.cache.MD5FileNameGenerator;
import com.libs.gallery.ImageBrowserActivity;
import com.libs.gallery.ImageInfo;
import com.oosic.apps.iemaker.base.SlideManager;
import com.osastudio.common.popmenu.EntryBean;
import com.osastudio.common.popmenu.PopMenuAdapter;
import com.osastudio.common.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <br/>================================================
 * <br/> 作    者：Blizzard-liu
 * <br/> 版    本：1.0
 * <br/> 创建日期：2017/12/7 9:26
 * <br/> 描    述：图片浏览
 * <br/> 修订历史：
 * <br/>================================================
 */
public class GalleryActivity extends ImageBrowserActivity {

    private ExerciseAnswerCardParam cardParam;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadIntentData();
        initViews();
    }

    private void loadIntentData() {
        Bundle args = getIntent().getExtras();
        if (args != null) {
            cardParam = (ExerciseAnswerCardParam) args.getSerializable(ExerciseAnswerCardParam.class.getSimpleName());
        }
    }

    private void initViews() {
        if (cardParam != null) {
            if (cardParam.isOnlineReporter() || cardParam.isOnlineHost()){
                mIvMore.setVisibility(View.VISIBLE);
                mIvMore.setEnabled(true);
            } else if (cardParam.getRoleType() == RoleType.ROLE_TYPE_STUDENT){
                mIvMore.setVisibility(View.VISIBLE);
                mIvMore.setEnabled(true);
            } else {
                mIvMore.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void handleLogic(View contentView) {
        if (cardParam == null) {
            super.handleLogic(contentView);
        } else {
            EntryBean entryBean = new EntryBean();
            final List<EntryBean> list = new ArrayList<>();
            if (cardParam.isOnlineReporter() || cardParam.isOnlineHost()){
                entryBean.value = getString(R.string.read_over);
            } else if (cardParam.getRoleType() == RoleType.ROLE_TYPE_STUDENT){
                entryBean.value = getString(R.string.ask_question);
            }
            entryBean.id = KEY_COLLECT;
            list.add(entryBean);
            ListView listView = (ListView) contentView.findViewById(com.lqwawa.apps.R.id.pop_menu_list);
            PopMenuAdapter adapter = new PopMenuAdapter(this,list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if(mPopWindow != null){
                        mPopWindow.dissmiss();
                    }
                    ExerciseItem itemData = cardParam.getExerciseItem();
                    if (itemData == null) {
                        return;
                    }
                    List<MediaData> dataList = itemData.getDatas();
                    if (dataList != null && dataList.size() > 0) {
                        List<String> imageList = new ArrayList<>();
                        for (int m = 0; m < dataList.size(); m++) {
                            imageList.add(dataList.get(m).resourceurl);
                        }
                        String savePath = Utils.PIC_TEMP_FOLDER + new MD5FileNameGenerator()
                                .generate(dataList.get(0).resourceurl);
                        cardParam.setExerciseItem(itemData);
                        DoCourseHelper doCourseHelper = new DoCourseHelper(GalleryActivity.this);
                        doCourseHelper.doAnswerQuestionCheckMarkData(
                                cardParam,
                                savePath,
                                imageList,
                                cardParam.getCommitTaskTitle(),
                                cardParam.getScreenType(),
                                DoCourseHelper.FromType.Do_Answer_Card_Check_Course);
                    }
                }
            });
        }
    }


    /**
     * 我要做课件  我要加点读
     *
     * @param flag
     */
    @Override
    protected void selectOrientation(final int flag) {

        UserInfo userInfo = ((MyApplication) getApplication()).getUserInfo();
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            ActivityUtils.enterLogin(GalleryActivity.this);
            return;
        }

        OrientationSelectDialog dialog = new OrientationSelectDialog(this,
                new OrientationSelectDialog.SelectHandler() {
                    DoCourseHelper doCourseHelper = new DoCourseHelper(GalleryActivity.this);

                    @Override
                    public void orientationSelect(int orientation) {
                        if (flag == KEY_DO_COURSE) {//我要做课件

                            doCourseHelper.doRemoteLqCourseFromImage(getPathList(), getNewResourceInfoTag(), orientation, DoCourseHelper.FromType.DO_LQ_COURSE);

                        } else {//我要加点读
                            doCourseHelper.doRemoteLqCourseFromImage(getPathList(), getNewResourceInfoTag(), orientation, DoCourseHelper.FromType.Do_SLIDE_COURSE);

                        }

                    }
                });
        dialog.show();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        Window window = dialog.getWindow();

        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = null; // 获取对话框当前的参数值
        if (window != null) {
            p = window.getAttributes();
            window.setGravity(Gravity.CENTER);
            p.width = (int) (d.getWidth() * 0.75f);
            p.height = (int) (d.getHeight() * 0.3);
            window.setAttributes(p);
        }
    }

    /**
     * 点击收藏
     */
    @Override
    protected void handleCollectLogic() {
        ImageInfo newResourceInfoTag = getNewResourceInfoTag();

        int resourceType = newResourceInfoTag.getResourceType();
        String typeName = "";
        if (resourceType == ResType.RES_TYPE_IMG) {
            typeName = getString(R.string.pictures);
        } else if (resourceType == ResType.RES_TYPE_PPT) {
            typeName = getString(R.string.txt_ppt);
        } else if (resourceType == ResType.RES_TYPE_PDF) {
            typeName = getString(R.string.txt_pdf);
        } else if (resourceType == ResType.RES_TYPE_DOC) {
            typeName = getString(R.string.DOC);
        }
        String resourceId = newResourceInfoTag.getResourceId();
        if (!resourceId.contains("-")) {
            resourceId = resourceId + "-" + resourceType;
        }
        CollectionHelper collectionHelper = new CollectionHelper(this);
        collectionHelper.setIsPublicRes(newResourceInfoTag.isPublicRes());
        collectionHelper.setCollectSchoolId(newResourceInfoTag.getCollectionSchoolId());
        collectionHelper.setFromChoiceLib(newResourceInfoTag.isFromChoiceLib());
        collectionHelper.collectDifferentResource(
                resourceId,
                newResourceInfoTag.getTitle(),
                newResourceInfoTag.getAuthorId(),
                typeName);
    }


    public static void newInstance(Context context, List<ImageInfo> mediaInfos, boolean isPDF, int index, boolean isHideMoreBtn, boolean isShowCourseAndReading) {
        newInstance(context, mediaInfos, isPDF, index, isHideMoreBtn, isShowCourseAndReading, true);
    }

    public static void newInstance(Context context, List<ImageInfo> mediaInfos, boolean isPDF, int index, boolean isHideMoreBtn, boolean isShowCourseAndReading, boolean isShowCollect) {
        newInstance(context, mediaInfos, isPDF, index, isHideMoreBtn, isShowCourseAndReading, isShowCollect, false);
    }

    /**
     * @param context
     * @param mediaInfos
     * @param isPDF
     * @param index
     * @param isHideMoreBtn          true:屏蔽右上角更多按钮
     * @param isShowCourseAndReading 是否显示“我要做课件”，“我要加点读”
     * @param isShowCollect          true:显示收藏条目
     * @param isShowIndex            true:显示当前页码
     */
    public static void newInstance(Context context,
                                   List<ImageInfo> mediaInfos,
                                   boolean isPDF,
                                   int index,
                                   boolean isHideMoreBtn,
                                   boolean isShowCourseAndReading,
                                   boolean isShowCollect,
                                   boolean isShowIndex) {
        if (mediaInfos == null || mediaInfos.size() == 0) {
            return;
        }
        Intent intent = new Intent(context, GalleryActivity.class);
        if (mediaInfos.size() > 500) {
            //防止传的时候bundle值过大
            DemoApplication.
                    getInstance().
                    getPrefsManager().
                    setDataList(PrefsManager.PrefsItems.PICTURE_RESOURCE_DATA_LIST, mediaInfos);
            mediaInfos.subList(100, mediaInfos.size()).clear();
            intent.putExtra(ImageBrowserActivity.KEY_BUNDLE_VALUE_TOO_BIG, true);
        }
        intent.putParcelableArrayListExtra(ImageBrowserActivity.EXTRA_IMAGE_INFOS, (ArrayList<? extends Parcelable>) mediaInfos);
        intent.putExtra(ImageBrowserActivity.EXTRA_CURRENT_INDEX, index);
        intent.putExtra(ImageBrowserActivity.ISPDF, isPDF);
        intent.putExtra(ImageBrowserActivity.EXTRA_ISSHOWINDEX, isShowIndex);
        intent.putExtra(ImageBrowserActivity.KEY_ISHIDEMOREBTN, isHideMoreBtn);
        intent.putExtra(ImageBrowserActivity.KEY_ISSHOWCOLLECT, isShowCollect);
        intent.putExtra(ImageBrowserActivity.KEY_ISSHOWCOURSEANDREADING, isShowCourseAndReading);
        context.startActivity(intent);
    }


    public static void newInstance(Context context,
                                   List<ImageInfo> mediaInfos,
                                   ExerciseAnswerCardParam cardParam) {
        if (mediaInfos == null || mediaInfos.size() == 0) {
            return;
        }
        Intent intent = new Intent(context, GalleryActivity.class);
        intent.putParcelableArrayListExtra(ImageBrowserActivity.EXTRA_IMAGE_INFOS, (ArrayList<? extends Parcelable>) mediaInfos);
        intent.putExtra(ImageBrowserActivity.EXTRA_CURRENT_INDEX, 0);
        intent.putExtra(ImageBrowserActivity.ISPDF, true);
        intent.putExtra(ImageBrowserActivity.EXTRA_ISSHOWINDEX, false);
        intent.putExtra(ImageBrowserActivity.KEY_ISHIDEMOREBTN, false);
        intent.putExtra(ImageBrowserActivity.KEY_ISSHOWCOLLECT, false);
        intent.putExtra(ImageBrowserActivity.KEY_ISSHOWCOURSEANDREADING, false);
        intent.putExtra(ExerciseAnswerCardParam.class.getSimpleName(), cardParam);
        context.startActivity(intent);
    }

    public List<ImageInfo> getPreferencesData() {
        PrefsManager prefsManager = DemoApplication.getInstance().getPrefsManager();
        return prefsManager.getPictureDataList(PrefsManager.PrefsItems.PICTURE_RESOURCE_DATA_LIST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == ResourceBaseFragment.REQUEST_CODE_DO_SLIDE_TOAST) {
                //我要加点读
                TipMsgHelper.ShowLMsg(GalleryActivity.this, getString(R.string.lqcourse_save_local));
            } else if (requestCode == ResourceBaseFragment.REQUEST_CODE_SLIDE) {
                //我要做课件
                String coursePath = data.getStringExtra(SlideManager.EXTRA_COURSE_PATH);
                String title = data.getStringExtra(SlideManager.LOAD_FILE_TITLE);
                if (!TextUtils.isEmpty(coursePath) && !TextUtils.isEmpty(title)) {
                    if (FileUtils.isFileExists(coursePath)) {
                        TipMsgHelper.ShowLMsg(GalleryActivity.this, getString(R.string.lqcourse_save_local));
                    }
                }
            }
        }
    }
}