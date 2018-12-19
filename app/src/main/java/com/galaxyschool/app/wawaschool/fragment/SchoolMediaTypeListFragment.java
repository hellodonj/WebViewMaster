package com.galaxyschool.app.wawaschool.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.medias.fragment.SchoolResourceContainerFragment;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;
import com.lqwawa.client.pojo.MediaType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: wangchao
 * @date: 2017/06/15 13:32
 */

public class SchoolMediaTypeListFragment extends MediaTypeListFragment {

    public static final String TAG = SchoolMediaTypeListFragment.class.getSimpleName();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ToolbarTopView toolbarTopView = (ToolbarTopView) findViewById(R.id.toolbar_top_view);
        if (toolbarTopView != null) {
            toolbarTopView.getBackView().setVisibility(View.VISIBLE);
            if (isFromChoiceLib){
                toolbarTopView.getTitleView().setText(R.string.choice_books);
            }else {
                toolbarTopView.getTitleView().setText(R.string.public_course);
            }
            toolbarTopView.getBackView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popStack();
                }
            });
        }
    }

    protected void loadTypeList() {
        List<MediaItem> mediaItems = new ArrayList<MediaItem>();
        MediaItem item;

        //lq课件
        if (isShowMediaType(MediaType.SCHOOL_COURSEWARE)) {
            item = new MediaItem();
            item.icon = R.drawable.icon_lq_course;
            item.type = MediaType.SCHOOL_COURSEWARE;
            item.title = R.string.microcourse;
            mediaItems.add(item);
        }

        //任务单
        if (isShowMediaType(MediaType.SCHOOL_TASKORDER)) {
            item = new MediaItem();
            item.icon = R.drawable.task_order_icon;
            item.type = MediaType.SCHOOL_TASKORDER;
            item.title = R.string.task_order;
            mediaItems.add(item);
        }

        if (isShowMediaType(MediaType.SCHOOL_PICTURE)) {
            item = new MediaItem();
            item.icon = R.drawable.resource_pic_ico;
            item.type = MediaType.SCHOOL_PICTURE;
            item.title = R.string.pictures;
            mediaItems.add(item);
        }

        if (isShowMediaType(MediaType.SCHOOL_AUDIO)) {
            item = new MediaItem();
            item.icon = R.drawable.resource_audio_ico;
            item.type = MediaType.SCHOOL_AUDIO;
            item.title = R.string.audios;
            mediaItems.add(item);
        }

        if (isShowMediaType(MediaType.SCHOOL_VIDEO)) {
            item = new MediaItem();
            item.icon = R.drawable.resource_video_ico;
            item.type = MediaType.SCHOOL_VIDEO;
            item.title = R.string.videos;
            mediaItems.add(item);
        }

        //PDF
        if (isShowMediaType(MediaType.SCHOOL_PDF)) {
            item = new MediaItem();
            item.icon = R.drawable.icon_personal_resource_pdf;
            item.type = MediaType.SCHOOL_PDF;
            item.title = R.string.pdf_file;
            mediaItems.add(item);
        }

        //PPT
        if (isShowMediaType(MediaType.SCHOOL_PPT)) {
            item = new MediaItem();
            item.icon = R.drawable.icon_ppt;
            item.type = MediaType.SCHOOL_PPT;
            item.title = R.string.ppt_file;
            mediaItems.add(item);
        }

        //DOC
        if (isShowMediaType(MediaType.SCHOOL_DOC)) {
            item = new MediaItem();
            item.icon = R.drawable.icon_doc;
            item.type = MediaType.SCHOOL_DOC;
            item.title = R.string.DOC;
            mediaItems.add(item);
        }

        getCurrAdapterViewHelper().setData(mediaItems);
    }

    protected void onItemClick(MediaItem data) {
        if (data != null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment fragment;
            Bundle args = getArguments();
            args.putBoolean(ActivityUtils.EXTRA_IS_PICK, true);
            args.putInt(SchoolResourceContainerFragment.Constants.CURRENTINDEX,
                    transferTypeToIndex(data.type));
            args.putBoolean(SchoolResourceContainerFragment.Constants.KEY_ISHIDETAB,
                    true);
            fragment = new SchoolResourceContainerFragment();
            fragment.setArguments(args);
            ft.replace(R.id.activity_body, fragment, SchoolResourceContainerFragment.TAG);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    private int transferTypeToIndex(int type) {
        int index;
        switch (type) {
            case MediaType.SCHOOL_LESSON_BOOK:
                index = SchoolResourceContainerFragment.Constants.LESSON_BOOK;
                break;
            case MediaType.SCHOOL_PICTURE:
                index = SchoolResourceContainerFragment.Constants.PICTURE;
                break;
            case MediaType.SCHOOL_AUDIO:
                index = SchoolResourceContainerFragment.Constants.AUDIO;
                break;
            case MediaType.SCHOOL_VIDEO:
                index = SchoolResourceContainerFragment.Constants.VIDEO;
                break;
            case MediaType.SCHOOL_PDF:
                index = SchoolResourceContainerFragment.Constants.PDF;
                break;
            case MediaType.SCHOOL_PPT:
                index = SchoolResourceContainerFragment.Constants.PPT;
                break;
            case MediaType.SCHOOL_COURSEWARE:
                index = SchoolResourceContainerFragment.Constants.LQ_COURSE;
                break;
            case MediaType.SCHOOL_TASKORDER:
                index = SchoolResourceContainerFragment.Constants.TASK_ORDER;
                break;
            case MediaType.SCHOOL_DOC:
                index = SchoolResourceContainerFragment.Constants.DOC;
                break;
            case MediaType.SCHOOL_LESSON:
                index = SchoolResourceContainerFragment.Constants.LESSON;
                break;

            default:
                index = SchoolResourceContainerFragment.Constants.LQ_COURSE;
                break;
        }
        return index;
    }
}
