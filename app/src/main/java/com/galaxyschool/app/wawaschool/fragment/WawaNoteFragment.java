package com.galaxyschool.app.wawaschool.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.galaxyschool.app.wawaschool.Note.MediaPaperActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.*;
import com.galaxyschool.app.wawaschool.db.NoteDao;
import com.galaxyschool.app.wawaschool.db.dto.NoteDTO;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.pojo.NoteInfo;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseUploadResult;
import com.galaxyschool.app.wawaschool.pojo.weike.NoteOpenParams;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;
import com.lqwawa.libs.mediapaper.MediaPaper;
import com.oosic.apps.share.ShareInfo;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Author: wangchao
 * Time: 2015/10/24 10:04
 */
public class WawaNoteFragment extends ContactsListFragment {

    public static final String TAG = WawaNoteFragment.class.getSimpleName();

    public static final String EXTRA_NOTE_TYPE = "note_type";
    private NoteDao noteDao;
    private int noteType;
    private UploadParameter uploadParameter;
    private NoteInfo mNoteInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wawanote, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        noteDao = new NoteDao(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        loadViews();
    }

    private void loadViews() {
        loadNotes();
    }

    private void initViews() {
        noteType = getArguments().getInt(EXTRA_NOTE_TYPE);
        ToolbarTopView toolbarTopView = (ToolbarTopView) findViewById(R.id.toolbar_top_view);
        if (toolbarTopView != null) {
            toolbarTopView.getBackView().setVisibility(View.VISIBLE);
            toolbarTopView.getCommitView().setVisibility(View.VISIBLE);
            if (noteType == MediaPaper.PAPER_TYPE_TIEBA) {
                toolbarTopView.getTitleView().setText(R.string.cs_local_post_bar);
            } else if (noteType == MediaPaper.PAPER_TYPE_PREPARATION) {
                toolbarTopView.getTitleView().setText(R.string.cs_preparation);
            }
            toolbarTopView.getCommitView().setText(R.string.create);
            toolbarTopView.getCommitView().setBackgroundResource(R.drawable.sel_nav_button_bg);
            toolbarTopView.getBackView().setOnClickListener(this);
            toolbarTopView.getCommitView().setOnClickListener(this);
        }

        GridView gridView = (GridView) findViewById(R.id.gridview);
        if (gridView == null) {
            return;
        }
        gridView.setNumColumns(1);
        AdapterViewHelper gridViewHelper = new AdapterViewHelper(
            getActivity(),
            gridView, R.layout.note_grid_item) {
            @Override
            public void loadData() {
                loadNotes();
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final View view = super.getView(position, convertView, parent);
                final NoteInfo data = (NoteInfo) getDataAdapter().getItem(position);
                if (data == null) {
                    return view;
                }
                ViewHolder holder = (ViewHolder) view.getTag();
                if (holder == null) {
                    holder = new ViewHolder();
                }
                holder.data = data;
                ImageView noteUpdate = (ImageView)view.findViewById(R.id.item_update);
                ImageView noteDelete = (ImageView) view.findViewById(R.id.item_selector);
                TextView noteTitle = (TextView) view.findViewById(R.id.item_title);
                final TextView noteDate = (TextView) view.findViewById(R.id.item_date);
                ImageView noteThumbnail = (ImageView) view.findViewById(R.id.item_icon);
                final LinearLayout noteContent = (LinearLayout) view.findViewById(R.id.item_content);
                TextView notePublish = (TextView) view.findViewById(R.id.operations_publish);
                TextView noteShare = (TextView) view.findViewById(R.id.operations_share);
                TextView noteEdit = (TextView) view.findViewById(R.id.operations_edit);

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(data.getCreateTime());

                noteThumbnail.setVisibility(TextUtils.isEmpty(data.getThumbnail()) ? View.GONE : View.VISIBLE);
                getThumbnailManager().displayThumbnail(data.getThumbnail(), noteThumbnail);
                noteTitle.setText(data.getTitle());
                noteDate.setText(getString(R.string.cs_modify_time) + DateUtils.getDateStr(calendar.getTime()));

                noteUpdate.setTag(holder.data);
                noteUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateNote((NoteInfo)v.getTag());
                    }
                });

                noteDelete.setTag(holder.data);
                noteDelete.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            delete((NoteInfo) v.getTag(), getString(R.string.delete_video));
                        }
                    });
                noteContent.setTag(holder.data);
                noteContent.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            NoteInfo noteInfo = (NoteInfo) v.getTag();
                            File noteFile = new File(Utils.NOTE_FOLDER, String.valueOf(noteInfo.getDateTime()));
                            String dateTimeStr = DateUtils.transferLongToDate("yyyy-MM-dd " +
                                "HH:mm:ss", noteInfo.getDateTime());
                            NoteOpenParams params = new NoteOpenParams(noteFile.getPath(), dateTimeStr, MediaPaperActivity.OPEN_TYPE_EDIT,
                                noteType, noteInfo, -1, false);
                            ActivityUtils.openLocalNote(getActivity(), params, 0);
                        }
                    });

                notePublish.setTag(holder.data);
                notePublish.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            NoteInfo noteInfo = (NoteInfo) v.getTag();
                            if(noteInfo != null) {
                                try {
                                    NoteDTO noteDTO = noteDao.getNoteDTOByDateTime(noteInfo.getDateTime(), noteType);
                                    if (noteDTO != null) {
                                        if (noteDTO.getDateTime() == noteInfo.getDateTime()) {
                                            noteInfo.setNoteId(noteDTO.getNoteId());
                                            noteInfo.setIsUpdate(noteDTO.isUpdate());
                                        }
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (noteType == MediaPaper.PAPER_TYPE_TIEBA) {
                                NoteHelper.publishLocalPostBar(getActivity(), getUserInfo(), noteInfo);
                            } else {
                                publishPreparation(noteInfo);
                            }
                        }
                    });

                noteShare.setTag(holder.data);
                noteShare.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            NoteInfo noteInfo = (NoteInfo) v.getTag();
                            if (noteInfo != null) {
                                if (noteInfo.getNoteId() > 0 && !noteInfo.isUpdate()) {
                                    showLoadingDialog();
                                    WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(getActivity());
                                    wawaCourseUtils.loadCourseDetail(String.valueOf(noteInfo.getNoteId()));
                                    wawaCourseUtils.setOnCourseDetailFinishListener(
                                        new WawaCourseUtils.OnCourseDetailFinishListener() {
                                            @Override
                                            public void onCourseDetailFinish(CourseData data) {
                                                dismissLoadingDialog();
                                                if (data != null) {
                                                    ShareInfo shareInfo = data.getShareInfo(getActivity());
                                                    if (shareInfo != null) {
                                                        new ShareUtils(getActivity()).share(
                                                            WawaNoteFragment.this
                                                                .getView(), shareInfo);
                                                    }
                                                }
                                            }
                                        });
                                } else {
                                    UserInfo userInfo = getUserInfo();
                                    if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
                                        ActivityUtils.enterLogin(getActivity());
                                        return;
                                    }
                                    final long dateTime = noteInfo.getDateTime();
                                    UploadParameter uploadParameter = NoteHelper.getUploadParameter(
                                        userInfo, noteInfo, null, 0, noteType);
                                    uploadParameter.setNoteType(noteType);
                                    if (uploadParameter != null) {
                                        showLoadingDialog(getString(R.string.cs_loading_wait), true);
                                        NoteHelper.uploadNote(
                                            getActivity(), uploadParameter, dateTime, new CallbackListener() {
                                                @Override
                                                public void onBack(Object result) {
                                                    dismissLoadingDialog();
                                                    CourseUploadResult uploadResult = (CourseUploadResult) result;
                                                    if (uploadResult != null && uploadResult.getCode() == 0) {
                                                        if (uploadResult.data == null || uploadResult.data.size() == 0) {
                                                            return;
                                                        }
                                                        CourseData courseData = uploadResult.data.get(0);
                                                        if (courseData != null) {
                                                            new ShareUtils(getActivity()).share(
                                                                WawaNoteFragment.this.getView
                                                                    (), courseData.getShareInfo(getActivity()));
                                                        }
                                                    }
                                                }
                                            });
                                    }
                                }
                            }

                        }
                    });
                noteEdit.setTag(holder.data);
                noteEdit.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //TODO Open note for Editing
                            NoteInfo noteInfo = (NoteInfo) v.getTag();
                            if (noteInfo != null) {
                                long dateTime = noteInfo.getDateTime();
                                File paper = new File(Utils.NOTE_FOLDER, String.valueOf(dateTime));
                                String dateTimeStr = DateUtils.transferLongToDate("yyyy-MM-dd " +
                                    "HH:mm:ss", dateTime);
                                NoteOpenParams params = new NoteOpenParams(paper.getPath(), dateTimeStr, MediaPaperActivity.OPEN_TYPE_EDIT,
                                    noteType, noteInfo, -1, false);
                                ActivityUtils.openLocalNote(getActivity(), params, 0);
                            }
                        }
                    });

                view.setTag(holder);

                return view;
            }

            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {

            }
        };
        setCurrAdapterViewHelper(gridView, gridViewHelper);
    }

    private void loadNotes() {
        List<NoteInfo> noteInfos = null;
        try {
            List<NoteDTO> noteDTOs = noteDao.getNoteDTOs(noteType);
            if (noteDTOs != null && noteDTOs.size() > 0) {
                noteInfos = new ArrayList<NoteInfo>();
                for (NoteDTO dto : noteDTOs) {
                    if (dto != null) {
                        noteInfos.add(dto.toNoteInfo());
                    }
                }
            }
            getCurrAdapterViewHelper().setData(noteInfos);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void updateNote(NoteInfo noteInfo) {
        if(noteInfo == null || !noteInfo.isUpdate()) {
            TipMsgHelper.ShowLMsg(getActivity(), R.string.no_note_update);
            return;
        }
        UserInfo userInfo = getUserInfo();
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            ActivityUtils.enterLogin(getActivity());
            return;
        }
        final long dateTime = noteInfo.getDateTime();
        UploadParameter uploadParameter = NoteHelper.getUploadParameter(
            userInfo, noteInfo, null, 0, noteType);
        uploadParameter.setNoteType(noteType);
        if (uploadParameter != null) {
            showLoadingDialog(getString(R.string.cs_loading_wait), true);
            NoteHelper.uploadNote(
                getActivity(), uploadParameter, dateTime, new CallbackListener() {
                    @Override
                    public void onBack(Object result) {
                        dismissLoadingDialog();
                        CourseUploadResult uploadResult = (CourseUploadResult) result;
                        if (uploadResult != null && uploadResult.getCode() == 0) {
                            if (uploadResult.data == null || uploadResult.data.size() == 0) {
                                TipMsgHelper.ShowLMsg(getActivity(), R.string.update_failure);
                            } else {
                                TipMsgHelper.ShowLMsg(getActivity(), R.string.update_success);
                            }
                        }
                    }
                });
        }

    }

    private void delete(final NoteInfo noteInfo, String title) {
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                getActivity(), null, title,
                getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                },
                getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        delete(noteInfo);
                    }
                });
        messageDialog.show();
    }

    private void delete(NoteInfo noteInfo) {
        if (noteInfo == null) {
            return;
        }
        File file = new File(Utils.NOTE_FOLDER, String.valueOf(noteInfo.getDateTime()));
        if (file != null && file.exists()) {
            Utils.safeDeleteDirectory(file.getPath());
        }
        try {
            noteDao.deleteNoteDTOByDateTime(noteInfo.getDateTime(), noteType);
            loadNotes();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    void publishPreparation(final NoteInfo noteInfo) {
        if (noteInfo == null) {
            return;
        }
        if (noteInfo.getNoteId() > 0 && !noteInfo.isUpdate()) {
            showLoadingDialog();
            WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(getActivity());
            wawaCourseUtils.loadCourseDetail(String.valueOf(noteInfo.getNoteId()));
            wawaCourseUtils.setOnCourseDetailFinishListener(
                new WawaCourseUtils.OnCourseDetailFinishListener() {
                    @Override
                    public void onCourseDetailFinish(CourseData data) {
                        dismissLoadingDialog();
                        if (data != null) {
                            data.setNickname(noteInfo.getTitle());
                            uploadParameter = new UploadParameter();
                            uploadParameter.setResType(ResType.RES_TYPE_NOTE);
                            uploadParameter.setType(1);
                            if(data != null) {
                                uploadParameter.setCourseData(data);
                            }
                            NoteHelper.showPublishToContactsDialog(getActivity(), uploadParameter, noteInfo,
                                getString(R.string.send_to_sp, getString(R.string.pub_course)),
                                data.getNickname(), "", PublishResourceFragment.Constants.TYPE_COURSE, false);
                        }
                    }
                });
        } else {
            UserInfo userInfo = getUserInfo();
            if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
                ActivityUtils.enterLogin(getActivity());
                return;
            }
            if (noteInfo != null) {
                uploadParameter = NoteHelper.getUploadParameter(
                    userInfo, noteInfo, null, 1, noteType);
                mNoteInfo = noteInfo;
                NoteHelper.showPublishToContactsDialog(getActivity(), uploadParameter, noteInfo,
                    getString(
                        R.string.send_to_sp,
                        getString(R.string.pub_course)), noteInfo.getTitle(), "", PublishResourceFragment.Constants.TYPE_COURSE, false);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.toolbar_top_commit_btn) {
            long dateTime = System.currentTimeMillis();
            File noteFile = new File(Utils.NOTE_FOLDER, String.valueOf(dateTime));
            String dateTimeStr = DateUtils.transferLongToDate("yyyy-MM-dd HH:mm:ss", dateTime);
            NoteOpenParams params = new NoteOpenParams(noteFile.getPath(), dateTimeStr, MediaPaperActivity.OPEN_TYPE_EDIT,
                noteType, null, -1, false);
            ActivityUtils.openLocalNote(getActivity(), params, 0);
        } else if (v.getId() == R.id.toolbar_top_back_btn) {
            if (getActivity() != null) {
                getActivity().finish();
            }
        }
    }
}
