package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.BookListActivity;
import com.galaxyschool.app.wawaschool.MyTaskListActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.SchoolCourseCategorySelectorActivity;
import com.galaxyschool.app.wawaschool.chat.activity.ChatActivity;
import com.galaxyschool.app.wawaschool.chat.library.ConversationHelper;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.CampusPatrolUtils;
import com.galaxyschool.app.wawaschool.common.LogUtils;
import com.galaxyschool.app.wawaschool.common.MessageEventConstantUtils;
import com.galaxyschool.app.wawaschool.common.NoteHelper;
import com.galaxyschool.app.wawaschool.common.StudyTaskUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UploadCourseHelper;
import com.galaxyschool.app.wawaschool.common.UploadReourceHelper;
import com.galaxyschool.app.wawaschool.common.UploadUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.db.dto.LocalCourseDTO;
import com.galaxyschool.app.wawaschool.fragment.contacts.ContactItem;
import com.galaxyschool.app.wawaschool.fragment.contacts.ContactsPickerListener;
import com.galaxyschool.app.wawaschool.fragment.contacts.ContactsPickerListener.FamilyContactsPickerListener;
import com.galaxyschool.app.wawaschool.fragment.contacts.ContactsPickerListener.GroupMemberPickerListener;
import com.galaxyschool.app.wawaschool.fragment.contacts.ContactsPickerListener.GroupPickerListener;
import com.galaxyschool.app.wawaschool.fragment.contacts.ContactsPickerListener.PersonalContactsPickerListener;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.helper.LqIntroTaskHelper;
import com.lqwawa.client.pojo.ResourceInfo;
import com.lqwawa.intleducation.module.discovery.ui.lesson.detail.LessonSourceFragment;
import com.lqwawa.lqbaselib.net.NetResultListener;
import com.galaxyschool.app.wawaschool.net.course.UploadCourseManager;
import com.lqwawa.lqbaselib.net.library.DataResult;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.lqwawa.lqbaselib.net.library.Result;
import com.galaxyschool.app.wawaschool.pojo.LookResDto;
import com.galaxyschool.app.wawaschool.pojo.NoteInfo;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.ShortSchoolClassInfo;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.pojo.UploadCourseType;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.UploadSchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseUploadResult;
import com.hyphenate.EMCallBack;
import com.lqwawa.lqbaselib.pojo.MessageEvent;
import com.lqwawa.tools.FileZipHelper;
import com.oosic.apps.share.ShareType;
import com.oosic.apps.share.SharedResource;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ContactsPickerEntryFragment extends BaseFragment
    implements GroupPickerListener, GroupMemberPickerListener,
    PersonalContactsPickerListener, FamilyContactsPickerListener {

    public static final String TAG = ContactsPickerEntryFragment.class.getSimpleName();

    public interface Constants {
        public static final int REQUEST_CODE_PICK_CONTACTS = 5102;
        public static final String REQUEST_DATA_PICK_CONTACTS = "data";

        public static final String EXTRA_USE_EXTENDED_PICKER = "useExtended";
        public static final String EXTRA_PICKER_TYPE = "type";
        public static final String EXTRA_PICKER_MODE = "mode";
        public static final String EXTRA_GROUP_ID = "groupId";
        public static final String EXTRA_GROUP_TYPE = "groupType";
        public static final String EXTRA_GROUP_NAME = "groupName";
        public static final String EXTRA_MEMBER_TYPE = "memberType";
        public static final String EXTRA_SCHOOL_ID = "schoolId";
        public static final String EXTRA_CLASS_ID = "classId";
        public static final String EXTRA_PICKER_CONFIRM_BUTTON_TEXT = "confirmButtonText";
        public static final String EXTRA_PICKER_SUPERUSER = "superUser";
        public static final String EXTRA_PUBLISH_RESOURCE = "publishResource";
        public static final String EXTRA_PUBLISH_CHAT_RESOURCE = "publishChatResource";
        public static final String EXTRA_ROLE_TYPE = "roleType";
        public static final String EXTRA_UPLOAD_TYPE = "uploadType";

        public static final String EXTRA_MY_CLASS_ID = "my_class_id";
        public static final String EXTRA_ADD_STUDENT = "add_student";
        public static final String EXTRA_IS_ONLINE_CLASS = "is_online_class";

        public static final int PICKER_TYPE_GROUP = 1;
        public static final int PICKER_TYPE_MEMBER = 2;
        public static final int PICKER_TYPE_PERSONAL = 4;
        public static final int PICKER_TYPE_FAMILY = 8;

        public static final int PICKER_MODE_SINGLE = 0;
        public static final int PICKER_MODE_MULTIPLE = 1;

        public static final int MEMBER_TYPE_TEACHER = 0;
        public static final int MEMBER_TYPE_STUDENT = 1;
        public static final int MEMBER_TYPE_PARENT = 2;
        public static final int MEMBER_TYPE_ALL = 3;

        public static final int GROUP_TYPE_CLASS = 0;
        public static final int GROUP_TYPE_SCHOOL = 1;
        public static final int GROUP_TYPE_ALL = 2;

        public static final int ROLE_TYPE_ALL = 0;
        public static final int ROLE_TYPE_TEACHER = 1;
        public static final int ROLE_TYPE_STUDENT = 2;
        public static final int ROLE_TYPE_PARENT = 3;
    }

    ContactsPickerListener internalPickerListener;
    GroupPickerListener groupPickerListener;
    GroupMemberPickerListener groupMemberPickerListener;
    PersonalContactsPickerListener personalContactsPickerListener;
    FamilyContactsPickerListener familyContactsPickerListener;

    UploadParameter uploadParameter;
    NoteInfo noteInfo;
    CourseData courseData;
    int uploadType;

    DefaultUploadListener defaultListener;

    //此参数用来控制发送到多个学习任务的多个班级 是否发送成功 或者超时
    boolean isSendSuccess = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_contacts, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();
    }

    @Override
    public void finish() {
        getActivity().finish();
    }

    public void setGroupPickerListener(GroupPickerListener listener) {
        this.groupPickerListener = listener;
    }

    public void setGroupMemberPickerListener(
        GroupMemberPickerListener listener) {
        this.groupMemberPickerListener = listener;
    }

    public void setPersonalContactsPickerListener(
        PersonalContactsPickerListener listener) {
        this.personalContactsPickerListener = listener;
    }

    public void setFamilyContactsPickerListener(
        FamilyContactsPickerListener listener) {
        this.familyContactsPickerListener = listener;
    }

    protected void setInternalPickerListener(ContactsPickerListener listener) {
        this.internalPickerListener = listener;
    }

    private void init() {
        defaultListener = new DefaultUploadListener(ModelResult.class);
        initFragments();
    }

    private void initFragments() {
        Bundle args = getArguments();
        uploadParameter = (UploadParameter) args.getSerializable(UploadParameter.class.getSimpleName());
        noteInfo = getArguments().getParcelable(NoteInfo.class.getSimpleName());
        courseData = (CourseData) getArguments().getSerializable(CourseData.class.getSimpleName());
        uploadType = args.getInt(Constants.EXTRA_UPLOAD_TYPE);

        int type = args.getInt(Constants.EXTRA_PICKER_TYPE);
        if (type == Constants.PICKER_TYPE_GROUP) {
            enterGroupContacts();
        } else if (type == Constants.PICKER_TYPE_MEMBER) {
            String groupId = args.getString(Constants.EXTRA_GROUP_ID);
            if (!TextUtils.isEmpty(groupId)) {
                enterGroupMemberContacts();
            } else {
                enterGroupContacts();
            }
        } else if (type == Constants.PICKER_TYPE_PERSONAL) {
            enterPersonalContacts();
        } else if (type == Constants.PICKER_TYPE_FAMILY) {
            enterFamilyContacts();
        }
    }

    void enterGroupContacts() {
//        GroupPickerFragment fragment = new GroupPickerFragment();
        GroupExpandPickerFragment fragment = new GroupExpandPickerFragment();
        fragment.setArguments(getArguments());
        fragment.setPickerListener(this);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.contacts_layout, fragment, GroupExpandPickerFragment.TAG);
        ft.commit();
    }

    void enterGroupMemberContacts() {
        GroupMemberPickerFragment fragment = new GroupMemberPickerFragment();
        fragment.setPickerListener(this);
        fragment.setArguments(getArguments());
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.contacts_layout, fragment, GroupMemberPickerFragment.TAG);
        ft.commit();
    }

    void enterPersonalContacts() {
        PersonalContactsPickerFragment fragment = new PersonalContactsPickerFragment();
        fragment.setPickerListener(this);
        fragment.setArguments(getArguments());
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.contacts_layout, fragment, PersonalContactsPickerFragment.TAG);
        ft.commit();
    }

    void enterFamilyContacts() {
        FamilyContactsPickerFragment fragment = new FamilyContactsPickerFragment();
        fragment.setPickerListener(this);
        fragment.setArguments(getArguments());
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.contacts_layout, fragment, FamilyContactsPickerFragment.TAG);
        ft.commit();
    }

    @Override
    public void onGroupPicked(List<ContactItem> result) {
        if (result != null && result.size() > 0) {
            for (int i = 0; i < result.size(); i++) {
                ContactItem obj = result.get(i);
                LogUtils.log("Contacts Picker", "[" + i + "]: " + obj.getName()
                        + " id: " + obj.getId()
                        + " school: " + obj.getSchoolId()
                        + " class: " + obj.getClassId());
            }
        }

        if (this.internalPickerListener != null) {
            this.internalPickerListener.onContactsPicked(result);
            return;
        }

        boolean superUser = getArguments().getBoolean(
                Constants.EXTRA_PICKER_SUPERUSER, false);
        if (!superUser) {
            if (this.groupPickerListener != null) {
                this.groupPickerListener.onGroupPicked(result);
            } else {
                notifyPickedResult(result);
            }
        } else {
            superGroupWorks(result);
        }


    }

    @Override
    public void onGroupMemberPicked(List<ContactItem> result) {
        if (result != null && result.size() > 0) {
            for (int i = 0; i < result.size(); i++) {
                ContactItem obj = result.get(i);
                LogUtils.log("Contacts Picker", "[" + i + "]: " + obj.getName()
                    + " id: " + obj.getId()
                    + " school: " + obj.getSchoolId()
                    + " class: " + obj.getClassId());
            }
        }

        if (this.internalPickerListener != null) {
            this.internalPickerListener.onContactsPicked(result);
            return;
        }

        boolean superUser = getArguments().getBoolean(
            Constants.EXTRA_PICKER_SUPERUSER, false);
        if (!superUser) {
            if (this.groupMemberPickerListener != null) {
                this.groupMemberPickerListener.onGroupMemberPicked(result);
            } else {
                notifyPickedResult(result);
            }
        } else {
            superWorks(result);
        }
    }

    @Override
    public void onPersonalContactsPicked(List<ContactItem> result) {
        if (result != null && result.size() > 0) {
            for (int i = 0; i < result.size(); i++) {
                ContactItem obj = result.get(i);
                LogUtils.log("Contacts Picker", "[" + i + "]: " + obj.getName()
                    + " id: " + obj.getId());
            }
        }

        if (this.internalPickerListener != null) {
            this.internalPickerListener.onContactsPicked(result);
            return;
        }

        boolean superUser = getArguments().getBoolean(
            Constants.EXTRA_PICKER_SUPERUSER, false);
        if (!superUser) {
            if (this.personalContactsPickerListener != null) {
                this.personalContactsPickerListener.onPersonalContactsPicked(result);
            } else {
                notifyPickedResult(result);
            }
        } else {
            superWorks(result);
        }
    }

    @Override
    public void onFamilyContactsPicked(List<ContactItem> result) {
        if (result != null && result.size() > 0) {
            for (int i = 0; i < result.size(); i++) {
                ContactItem obj = result.get(i);
                LogUtils.log("Contacts Picker", "[" + i + "]: " + obj.getName()
                    + " id: " + obj.getId());
            }
        }

        if (this.internalPickerListener != null) {
            this.internalPickerListener.onContactsPicked(result);
            return;
        }

        boolean superUser = getArguments().getBoolean(
            Constants.EXTRA_PICKER_SUPERUSER, false);
        if (!superUser) {
            if (this.familyContactsPickerListener != null) {
                this.familyContactsPickerListener.onFamilyContactsPicked(result);
            } else {
                notifyPickedResult(result);
            }
        } else {
            superWorks(result);
        }
    }

    protected void notifyPickedResult(List<ContactItem> result) {
        ArrayList list = null;
        if (result instanceof ArrayList) {
            list = (ArrayList) result;
        } else {
            list = new ArrayList();
            for (ContactItem item : result) {
                list.add(item);
            }
        }
        Bundle args = new Bundle();
        args.putParcelableArrayList(Constants.REQUEST_DATA_PICK_CONTACTS, list);
        Intent intent = new Intent();
        intent.putExtras(args);
        getActivity().setResult(Activity.RESULT_OK, intent);
        finish();
    }

    protected void superGroupWorks(List<ContactItem> list) {
        if (getArguments().getBoolean(Constants.EXTRA_PUBLISH_RESOURCE, false)) {
            if (uploadParameter != null) {
                publishNote(list);
            }
        }
    }
    protected void superWorks(List<ContactItem> list) {
        if (getArguments().getBoolean(Constants.EXTRA_PUBLISH_RESOURCE, false)) {
            if (uploadParameter != null) {
                if(uploadType == UploadCourseType.STUDY_TASK) {
                    publishStudyTask(list);
                } else {
                    if (uploadParameter.getType() == ShareType.SHARE_TYPE_CLASSROOM
                            || uploadParameter.getType() == ShareType.SHARE_TYPE_PICTUREBOOK) {
                        publishCourse(list);
                    } else if (uploadParameter.getType() == ShareType.SHARE_TYPE_PUBLIC_COURSE) {
                        enterSyllabusSelection(list);
                    } else {
                        publishNote(list);
                    }
                }
            }

            return;
        } else if (getArguments().getBoolean(Constants.EXTRA_PUBLISH_CHAT_RESOURCE, false)) {
            publishChatResource(list);
            return;
        }
    }

    private String getStudentIds(List<ContactItem> list) {
        StringBuilder builder = new StringBuilder();
        if (list != null && list.size() > 0) {
            for (ContactItem item : list) {
                if (item != null && !TextUtils.isEmpty(item.getId())) {
                    builder.append(item.getId() + ",");
                }
            }
        }
        String result = builder.toString();
        if (result.endsWith(",")) {
            result = result.substring(0, result.length());
        }
        return result;
    }


    void publishChatResource(final List<ContactItem> list) {
        if (getArguments().containsKey(NoteInfo.class.getSimpleName())) {
            publishLocalCourseChatReource(list);
        } else {
            publishChatResource(list, null);
        }
    }

    void publishChatResource(final List<ContactItem> list, SharedResource resource) {
        if (resource == null) {
            resource = (SharedResource) getArguments().getSerializable(
                SharedResource.class.getSimpleName());
            if (resource == null) {
                defaultListener.onFinish();
                return;
            }
        }
        ContactItem target = list.get(0);
        int chatType = 0;
        if (target.getType() == ContactItem.CONTACT_TYPE_GROUP) {
            chatType = ChatActivity.CHATTYPE_GROUP;
        } else if (target.getType() == ContactItem.CONTACT_TYPE_PERSON) {
            chatType = ChatActivity.CHATTYPE_SINGLE;
            //发到班级通讯录里不加toUserId，发到个人通讯录里要加
            resource.setToUserId(target.getId());
        }
        if (getUserInfo() == null){
            return;
        }
        resource.setFromUserId(getUserInfo().getMemberId());
        resource.patchFields();
        resource.patchFieldShareUrlWithHideFooter();

        //对发送蛙蛙好友的资源进行区分加密
        String url = resource.getShareUrl();
        int type =resource.getResourceType();
        if (resource.isPublicRescourse()){
            //表示公开的资源
            if (Utils.isStudyCard(type)){
                url = Utils.encryptionVisitUrl(url,true,type,null);
            }
        }else {
            url = Utils.encryptionVisitUrl(url,false,type,resource.getParentId());
        }
        resource.setShareUrl(url);

        defaultListener.setShowErrorTips(false);
        defaultListener.onPreExecute();
        ConversationHelper.sendResource(getActivity(), chatType,
            target.getHxId(), target.getName(), target.getIcon(),
            resource, new EMCallBack() {
                @Override
                public void onSuccess() {
                    if (getActivity() == null) {
                        return;
                    }
                    getView().post(new Runnable() {
                        @Override
                        public void run() {
                            defaultListener.onFinish();
                            defaultListener.onSuccess(JSON.toJSONString(new Result(false, "")));
                        }
                    });
                }

                @Override
                public void onError(int i, String s) {
                    if(getActivity() == null) {
                        return;
                    }
                    getView().post(new Runnable() {
                        @Override
                        public void run() {
                            defaultListener.onFinish();
                            defaultListener.onError(null);
                        }
                    });
                }

                @Override
                public void onProgress(int i, String s) {

                }
            });
    }

    void publishLocalCourseChatReource(final List<ContactItem> list) {
        if (noteInfo != null) {
            final long dateTime = noteInfo.getDateTime();
            if (uploadParameter != null) {
                showLoadingDialog(getString(R.string.cs_loading_wait), true);
                NoteHelper.uploadNote(
                    getActivity(), uploadParameter, dateTime, new CallbackListener() {
                        @Override
                        public void onBack(Object result) {
                            dismissLoadingDialog();
                            CourseUploadResult uploadResult = (CourseUploadResult) result;
                            if (uploadResult != null && uploadResult.code == 0) {
                                if (uploadResult.data != null && uploadResult.data.size() > 0) {
                                    CourseData courseData = uploadResult.data.get(0);
                                    if (courseData != null) {
                                        publishChatResource(list, courseData.getSharedResource());
                                    }
                                }
                            } else {
                                TipsHelper.showToast(getActivity(), R.string.send_failure);
                            }
                        }
                    });
            }
        }
    }

    void publishNote(final List<ContactItem> list) {
        List<String> ids = new ArrayList();
        for (ContactItem item : list) {
            ids.add(item.getId());
        }
        if (uploadParameter != null) {
            showLoadingDialog();
            uploadParameter.setSchoolIds(list.get(0).schoolId);
            uploadParameter.setContactItem(list.get(0));
            uploadParameter.setStudentIds(ids);
            final String schoolId = list.get(0).schoolId;
            final String classId = list.get(0).classId;
           final  int shareType = uploadParameter.getShareType();
            //改成多选
            List<ShortSchoolClassInfo> shortSchoolClassInfos = new ArrayList<ShortSchoolClassInfo>();
            for (ContactItem item : list){
                ShortSchoolClassInfo shortSchoolClassInfo = new ShortSchoolClassInfo();
                shortSchoolClassInfo.setSchoolId(item.getSchoolId());
                shortSchoolClassInfo.setSchoolName("");
                shortSchoolClassInfo.setClassId(item.getClassId());
                shortSchoolClassInfo.setClassName("");
                shortSchoolClassInfo.setGroupId(item.getGroupId());
                shortSchoolClassInfos.add(shortSchoolClassInfo);
            }
            uploadParameter.setShortSchoolClassInfos(shortSchoolClassInfos);

            if (uploadParameter.getCourseData() != null) {
                CourseData courseData = uploadParameter.getCourseData();
                if (courseData != null) {
                    if (shareType == ShareType.SHARE_TYPE_SCHOOL_COURSE) {
//                        NoteHelper.updateNoteToSchoolCourse(getActivity(), getUserInfo(), courseData, shcoolId);
                        enterSchoolCourseCategorySelector(schoolId);
                    } else if (shareType == ShareType.SHARE_TYPE_SCHOOL_MOVEMENT) {
                        NoteHelper.updateNoteToSchoolMovement(getActivity(), getUserInfo(), courseData, schoolId);
                    } else if (shareType >= ShareType.SHARE_TYPE_NOTICE && shareType <= ShareType.SHARE_TYPE_COURSE) {
                        if(shareType != ShareType.SHARE_TYPE_HOMEWORK) {
                            int type = transferType(shareType);
                            if (type > 0) {
                                NoteHelper.updateNoteToClassSpace(getActivity(), getUserInfo(), courseData, schoolId, classId, type);
                            }
                        } else {
                            NoteHelper.publishStudyTask(getActivity(), uploadParameter, courseData, true);
                        }
                    }
                }
            } else {
                if(shareType == ShareType.SHARE_TYPE_SCHOOL_COURSE) {
                    enterSchoolCourseCategorySelector(schoolId);
                } else if(shareType == ShareType.SHARE_TYPE_CLASSROOM){
                    if(uploadParameter != null) {
                        showLoadingDialog();
                        UploadUtils.uploadResource(getActivity(), uploadParameter, new CallbackListener() {
                            @Override
                            public void onBack(Object result) {
                                final CourseUploadResult courseUploadResult = (CourseUploadResult) result;
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dismissLoadingDialog();
                                            if (courseUploadResult != null) {
                                                List<CourseData> courseDatas = courseUploadResult.data;
                                                if (courseDatas != null && courseDatas.size() > 0) {
                                                    CourseData data = courseDatas.get(0);
                                                    if (data != null) {
                                                        courseData = data;
                                                        int type = transferType(shareType);
                                                        if (type > 0) {
                                                            NoteHelper.updateNoteToClassSpace(getActivity(), getUserInfo(), courseData, schoolId, classId, type);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                } else {
                    if (noteInfo != null) {
                        final long dateTime = noteInfo.getDateTime();
                        showLoadingDialog(getString(R.string.cs_loading_wait), true);
                        NoteHelper.uploadNote(
                            getActivity(), uploadParameter, dateTime, new CallbackListener() {
                                @Override
                                public void onBack(Object result) {
                                    dismissLoadingDialog();
                                    CourseUploadResult uploadResult = (CourseUploadResult) result;
                                    if (uploadResult != null && uploadResult.code == 0) {
                                        if (uploadResult.data != null && uploadResult.data.size() > 0) {
                                            CourseData courseData = uploadResult.data.get(0);
                                            if (courseData != null) {
                                                int shareType = uploadParameter.getShareType();
                                                if (shareType == ShareType.SHARE_TYPE_SCHOOL_COURSE) {
                                                    NoteHelper.updateNoteToSchoolCourse(getActivity(), getUserInfo(), courseData, schoolId);
                                                } else if (shareType == ShareType.SHARE_TYPE_SCHOOL_MOVEMENT) {
                                                    NoteHelper.updateNoteToSchoolMovement(getActivity(), getUserInfo(), courseData, schoolId);
                                                } else if (shareType >= ShareType.SHARE_TYPE_NOTICE && shareType <= ShareType.SHARE_TYPE_COURSE) {
                                                    if(shareType != ShareType.SHARE_TYPE_HOMEWORK) {
                                                        int type = transferType(shareType);
                                                        if (type > 0) {
                                                            NoteHelper.updateNoteToClassSpace(getActivity(), getUserInfo(), courseData, schoolId, classId, type);
                                                        }
                                                    } else {
                                                        NoteHelper.publishStudyTask(getActivity(), uploadParameter, courseData, true);
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        TipsHelper.showToast(getActivity(), R.string.send_failure);
                                    }
                                }
                            });
                    }
                }
            }
        }
    }

    private void enterSchoolCourseCategorySelector(String schoolId) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), SchoolCourseCategorySelectorActivity.class);
        intent.putExtra(SchoolCourseCategorySelectorActivity.EXTRA_MODE, SchoolCourseCategorySelectorActivity.UPLOAD_MODE);
        intent.putExtra(SchoolCourseCategorySelectorActivity.EXTRA_SCHOOL_ID, schoolId);
        intent.putExtra(UploadParameter.class.getSimpleName(), uploadParameter);
        intent.putExtra(NoteInfo.class.getSimpleName(), noteInfo);
        startActivity(intent);
        finish();
    }

    private void publishMicroCourse(List<ContactItem> list) {
        List<String> ids = new ArrayList();
        for (ContactItem item : list) {
            ids.add(item.getId());
        }
        if (uploadParameter != null) {
            uploadParameter.setSchoolIds(list.get(0).schoolId);
            uploadParameter.setContactItem(list.get(0));
            uploadParameter.setStudentIds(ids);
            uploadParameter.setClassId(list.get(0).classId);
            CourseData courseData = uploadParameter.getCourseData();
            if (courseData != null) {
                UploadCourseManager.commitCourseToClassSpace
                        (getActivity(), courseData, uploadParameter, true);
            } else {
                final LocalCourseDTO data = uploadParameter.getLocalCourseDTO();
                showLoadingDialog(getString(R.string.upload_and_wait), false);
                FileZipHelper.ZipUnzipParam param = new FileZipHelper.ZipUnzipParam(
                        data.getmPath(), Utils.TEMP_FOLDER + Utils.getFileNameFromPath(data.getmPath())
                        + Utils.COURSE_SUFFIX);
                FileZipHelper.zip(param,
                        new FileZipHelper.ZipUnzipFileListener() {
                            @Override
                            public void onFinish(
                                    FileZipHelper.ZipUnzipResult result) {
                                if (result != null && result.mIsOk) {
                                    uploadParameter.setZipFilePath(result.mParam.mOutputPath);
                                    UploadUtils.uploadResource(getActivity(), uploadParameter, new CallbackListener() {
                                        @Override
                                        public void onBack(final Object result) {
                                            if (getActivity() != null) {
                                                getActivity().runOnUiThread(
                                                        new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                dismissLoadingDialog();
                                                                if (result != null) {
                                                                    CourseUploadResult uploadResult = (CourseUploadResult) result;
                                                                    if (uploadResult.code != 0) {
                                                                        TipMsgHelper.ShowLMsg(getActivity(), R.string
                                                                                .upload_file_failed);
                                                                        return;
                                                                    }
                                                                    if (uploadResult.data != null && uploadResult.data.size() > 0) {
                                                                        final CourseData courseData = uploadResult.data.get(0);
                                                                        if (courseData != null) {
                                                                            UploadCourseManager.commitCourseToClassSpace
                                                                                    (getActivity(), courseData,
                                                                                            uploadParameter, true);
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                );
                                            }
                                        }
                                    });
                                }
                            }
                        });
            }
        }
    }

    private int transferType(int shareType) {
        int type = -1;
        switch (shareType) {
        case ShareType.SHARE_TYPE_NOTICE:
            type = 2;
            break;
        case ShareType.SHARE_TYPE_HOMEWORK:
            type = 1;
            break;
        case ShareType.SHARE_TYPE_COMMENT:
            type = 3;
            break;
        case ShareType.SHARE_TYPE_COURSE:
            type = 4;
            break;
            case ShareType.SHARE_TYPE_CLASSROOM:
                type=5;
                break;

        }
        return type;
    }

    NetResultListener netResultListener = new NetResultListener() {
        @Override
        public void onSuccess(Object data) {
            if(getActivity() == null) {
                return;
            }
            finish();
        }

        @Override
        public void onError(String message) {
            if(getActivity() == null) {
                return;
            }

        }

        @Override
        public void onFinish() {
            dismissLoadingDialog();
        }
    };

    private class DefaultUploadListener<T extends ModelResult> extends DefaultListener<T> {

        public DefaultUploadListener(Class resultClass) {
            super(resultClass);
            setShowLoading(true);
        }

        @Override
        public void onSuccess(String jsonString) {
            if (getActivity() == null) {
                return;
            }
            super.onSuccess(jsonString);
            if (getResult() == null || !getResult().isSuccess()) {
                TipsHelper.showToast(getActivity(), R.string.upload_success);
                finish();
            } else {
                TipsHelper.showToast(getActivity(), R.string.upload_failure);
            }
        }

        @Override
        public void onError(NetroidError error) {
//            super.onError(error);
            if(getActivity() == null) {
                return;
            }
            TipsHelper.showToast(getActivity(), R.string.upload_failure);
        }
    }

    //复制平板的代码
    void publishCourse(final List<ContactItem> list) {
        List<String> ids = new ArrayList();
        for (ContactItem item : list) {
            ids.add(item.getId());
        }
        uploadParameter.setSchoolIds(list.get(0).schoolId);
        uploadParameter.setContactItem(list.get(0));
        uploadParameter.setStudentIds(ids);
        if(uploadParameter.getType() == ShareType.SHARE_TYPE_CLASSROOM) {
            uploadParameter.setClassId(list.get(0).classId);
            if(uploadParameter.getLocalCourseDTO() != null) {
                uploadTaskToClassroom();
            } else {
                new UploadCourseHelper(getActivity()).uploadResource(uploadParameter, ShareType.SHARE_TYPE_CLASSROOM);
                finish();
            }
        } else if(uploadParameter.getType() == ShareType.SHARE_TYPE_PICTUREBOOK) {
            ActivityUtils.gotoPicBookCategorySelector(getActivity(), uploadParameter);
            finish();
        }
    }

    void publishStudyTask(final List<ContactItem> list) {
        final List<ShortSchoolClassInfo> schoolClassInfos = new ArrayList<ShortSchoolClassInfo>();
        for (ContactItem item : list) {
            ShortSchoolClassInfo info = new ShortSchoolClassInfo();
            info.setSchoolId(item.schoolId);
            info.setSchoolName("");
            info.setClassId(item.classId);
            info.setClassName("");
            info.setGroupId(item.getGroupId());
            schoolClassInfos.add(info);
        }
        if(uploadParameter != null) {
            //判断startDate和endDate区分老师发布学习任务还是学生提交学习任务
            if(uploadParameter.getLocalCourseDTO() != null
                    || (uploadParameter.getCourseData() != null && TextUtils.isEmpty
                    (uploadParameter.getStartDate())) && TextUtils.isEmpty(uploadParameter.getEndDate())) {
                uploadParameter.setShortSchoolClassInfos(schoolClassInfos);
                Intent intent = new Intent();
                intent.setClass(getActivity(), MyTaskListActivity.class);
                Bundle args = new Bundle();
                args.putSerializable(UploadParameter.class.getSimpleName(), uploadParameter);
                args.putBoolean(MyTaskListFragment.EXTRA_IS_SCAN_TASK, uploadParameter.isScanTask());
                intent.putExtras(args);
                if(getActivity() != null) {
                    if (uploadParameter.isTempData()){
                        getActivity().startActivityForResult(intent,ActivityUtils.REQUEST_CODE_RETURN_REFRESH);
                    }else {
                        startActivity(intent);
                        getActivity().finish();
                    }
                }
                return;
            }
            //当布置任务为话题讨论和英文写作时直接进入当前的方法
            if(uploadParameter.getTaskType() == StudyTaskType.TOPIC_DISCUSSION || uploadParameter
                    .getTaskType()==StudyTaskType.ENGLISH_WRITING) {
                publishStudyTask(uploadParameter, null, schoolClassInfos, true);
            } else if (uploadParameter.getTaskType() == StudyTaskType.WATCH_WAWA_COURSE){
                //看课件多类型单独处理
                publishWatchWawaCourseStudyTask(uploadParameter,schoolClassInfos,true);
            } else if (uploadParameter.getTaskType() == StudyTaskType.LISTEN_READ_AND_WRITE){
                //听说+读写
                publishListenReadAndWriteStudyTask(uploadParameter,schoolClassInfos);
            } else if (uploadParameter.getTaskType() == StudyTaskType.SUPER_TASK){
                autoDistinguishStudyType(uploadParameter,schoolClassInfos);
            } else {
                if(uploadParameter.getCourseData() != null) {
                    publishStudyTask(uploadParameter, uploadParameter.getCourseData(), schoolClassInfos, true);
                } else {
                    showLoadingDialog(getString(R.string.upload_and_wait), false);
                    UploadReourceHelper.uploadResource(getActivity(), uploadParameter, new CallbackListener() {
                        @Override
                        public void onBack(final Object result) {
                            if(getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        CourseUploadResult courseUploadResult = (CourseUploadResult) result;
                                        if (courseUploadResult != null && courseUploadResult.code == 0) {
                                            List<CourseData> courseDatas = courseUploadResult.getData();
                                            if (courseDatas != null && courseDatas.size() > 0) {
                                                CourseData courseData = courseDatas.get(0);
                                                if (courseData != null) {
                                                    publishStudyTask(uploadParameter, courseData, schoolClassInfos, false);
                                                }
                                            }
                                        } else {
                                            dismissLoadingDialog();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        }
    }

    public void autoDistinguishStudyType(UploadParameter uploadParameter,
                                         List<ShortSchoolClassInfo> schoolClassInfos){
        List<UploadParameter> uploadParameters = uploadParameter.getUploadParameters();
        if (uploadParameters != null && uploadParameters.size() > 0){
            if (uploadParameters.size() == 1){
                //拆分学习任务具体的类型
                UploadParameter parameter = uploadParameters.get(0);
                parameter.setMemberId(uploadParameter.getMemberId());
                parameter.setCreateName(uploadParameter.getCreateName());
                parameter.setSubmitType(uploadParameter.getSubmitType());
                parameter.setViewOtherPermissionType(uploadParameter.getViewOtherPermissionType());
                int taskType = parameter.getTaskType();
                if (taskType == StudyTaskType.WATCH_WAWA_COURSE
                        || taskType == StudyTaskType.NEW_WATACH_WAWA_COURSE){
                    //看课件
                    publishWatchWawaCourseStudyTask(parameter,schoolClassInfos,true);
                } else {
                    List<LookResDto> dtos = parameter.getLookResDtoList();
                    if (dtos != null && dtos.size() > 1){
                        if (taskType == StudyTaskType.WATCH_HOMEWORK){
                            parameter.setTaskType(StudyTaskType.MULTIPLE_OTHER);
                        } else if (taskType == StudyTaskType.SUBMIT_HOMEWORK){
                            parameter.setTaskType(StudyTaskType.MULTIPLE_OTHER_SUBMIT);
                        }
                    }
                    //其他任务类型
                    publishStudyTask(parameter,parameter.getCourseData(),schoolClassInfos,true);
                }
            } else {
                publishSuperTask(uploadParameter, schoolClassInfos);
            }
        }
    }

    /**
     * 发送综合任务到班级和小组
     * @param uploadParameter
     * @param schoolClassInfos
     */
    private void publishSuperTask(UploadParameter uploadParameter,
                                  List<ShortSchoolClassInfo> schoolClassInfos){
        showLoadingDialog();
        JSONObject taskParams = new JSONObject();
        boolean isPickerGroup = false;
        if (uploadParameter != null) {
            //是否为选择小组模式
            if (!TextUtils.isEmpty(schoolClassInfos.get(0).getGroupId())) {
                //选择小组模式
                isPickerGroup = true;
            }
            try {
                taskParams.put("TaskCreateId", uploadParameter.getMemberId());
                taskParams.put("TaskCreateName", uploadParameter.getCreateName());
                StudyTaskUtils.handleSchoolClassData(taskParams,schoolClassInfos);
                taskParams.put("TaskTitle", uploadParameter.getFileName());
                taskParams.put("StartTime", uploadParameter.getStartDate());
                taskParams.put("EndTime", uploadParameter.getEndDate());
                //提交时间类型
                taskParams.put("SubmitType",uploadParameter.getSubmitType());
                //作业可不可查看
                taskParams.put("ViewOthersTaskPermisson", uploadParameter.getViewOtherPermissionType());
                JSONArray secondTaskList = new JSONArray();
                JSONObject secondObject = null;
                List<UploadParameter> data = uploadParameter.getUploadParameters();
                for (int i= 0,len  = data.size();i < len;i++){
                    UploadParameter parameter = data.get(i);
                    secondObject = new JSONObject();
                    secondObject.put("SecondTaskNum",i);
                    if (parameter.getTaskType() == StudyTaskType.WATCH_WAWA_COURSE){
                        secondObject.put("TaskType", StudyTaskType.NEW_WATACH_WAWA_COURSE);
                    } else {
                        secondObject.put("TaskType", parameter.getTaskType());
                    }
                    secondObject.put("TaskTitle",parameter.getFileName());
                    secondObject.put("StartTime",parameter.getStartDate());
                    secondObject.put("EndTime",parameter.getEndDate());
                    secondObject.put("DiscussContent",parameter.getDisContent());
                    //打分
                    if (parameter.NeedScore) {
                        secondObject.put("NeedScore", true);
                        secondObject.put("ScoringRule", parameter.ScoringRule);
                    }
                    if (parameter.getTaskType() == StudyTaskType.ENGLISH_WRITING){
                        secondObject.put("WritingRequire",parameter.getWritingRequire());
                        secondObject.put("MarkFormula",parameter.getMarkFormula());
                        secondObject.put("WordCountMin",parameter.getWordCountMin());
                        secondObject.put("WordCountMax",parameter.getWordCountMax());
                        CourseData englishData = parameter.getCourseData();
                        if (englishData != null) {
                            secondObject.put("ResId", englishData.getIdType());
                            secondObject.put("ResUrl", englishData.resourceurl);
                        }
                    }

                    JSONArray thirdTaskList = new JSONArray();
                    JSONObject thirdObject = null;
                    List<LookResDto> resDtos = parameter.getLookResDtoList();
                    if (resDtos != null && resDtos.size() > 0 && parameter.getTaskType() != StudyTaskType.ENGLISH_WRITING){
                        for (int j = 0;j < resDtos.size();j++){
                            thirdObject = new JSONObject();
                            LookResDto lookDto = resDtos.get(j);
                            thirdObject.put("ResTitle",lookDto.getResTitle() == null ? "" : lookDto.getResTitle());
                            String resUrl = lookDto.getResUrl();
                            String resId = lookDto.getResId();
                            String authorId = lookDto.getAuthor();
                            List<ResourceInfo> splitInfo = lookDto.getSplitInfoList();
                            int taskType = parameter.getTaskType();
                            if ((taskType == StudyTaskType.RETELL_WAWA_COURSE
                                    || taskType == StudyTaskType.TASK_ORDER)
                                    && splitInfo != null && splitInfo.size() > 0){
                                resUrl = StudyTaskUtils.getPicResourceData(splitInfo,true,
                                        false,false);
                                resId = StudyTaskUtils.getPicResourceData(splitInfo,false,
                                        false,true);
                                authorId = StudyTaskUtils.getPicResourceData(splitInfo,false,
                                        true,false);
                            }
                            thirdObject.put("ResUrl", resUrl);
                            thirdObject.put("ResId",resId);
                            thirdObject.put("Author", authorId == null ? "" : authorId);
                            //学程馆资源的id
                            thirdObject.put("ResCourseId",lookDto.getResCourseId());
                            thirdObject.put("ResPropType", lookDto.getResPropType());
                            if (taskType == StudyTaskType.RETELL_WAWA_COURSE) {
                                thirdObject.put("RepeatCourseCompletionMode", lookDto.getCompletionMode());
                            }
                            if (!TextUtils.isEmpty(lookDto.getPoint())) {
                                thirdObject.put("ScoringRule", StudyTaskUtils.getScoringRule(lookDto.getPoint()));
                            }
                            thirdTaskList.put(thirdObject);
                        }
                    }
                    secondObject.put("ThirdTaskList",thirdTaskList);
                    secondTaskList.put(secondObject);
                }
                taskParams.put("SecondTaskList",secondTaskList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        RequestHelper.RequestDataResultListener listener = new RequestHelper
                .RequestDataResultListener(getActivity(), DataResult.class) {
            @Override
            public void onSuccess(String json) {
                if(getActivity() == null) {
                    return;
                }
                if (TextUtils.isEmpty(json)) return;
                dismissLoadingDialog();
                try {
                    DataResult result = JSON.parseObject(json,DataResult.class);
                    if (result != null && result.isSuccess()) {
                        //布置完成刷新布置任务页面
                        CampusPatrolUtils.setHasStudyTaskAssigned(true);
                        LqIntroTaskHelper.getInstance().clearTaskList();
                        TipMsgHelper.ShowLMsg(getActivity(), R.string.publish_course_ok);
                        //发送广播刷新mooc的数据
                        getActivity().sendBroadcast(new Intent().setAction(LessonSourceFragment.LESSON_RESOURCE_CHOICE_PUBLISH_ACTION));
                        EventBus.getDefault().post(new MessageEvent(MessageEventConstantUtils.SEND_HOME_WORK_LIB_SUCCESS));
                        finish();
                    } else {
                        String errorMessage = getString(R.string.publish_course_error);
                        if (result != null && !TextUtils.isEmpty(result.getErrorMessage())){
                            errorMessage = result.getErrorMessage();
                        }
                        TipMsgHelper.ShowLMsg(getActivity(),errorMessage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFinish() {
                super.onFinish();
                dismissLoadingDialog();
                CampusPatrolUtils.setHasStudyTaskAssigned(true);
                finish();
            }

            @Override
            public void onError(NetroidError error) {
                super.onError(error);
                dismissLoadingDialog();
            }
        };
        listener.setShowLoading(true);
//        String serverUrl = isPickerGroup ? ServerUrl.ADD_TOGETHER_TASK_TOSTUDYGROUP_BASE_URL:ServerUrl
//                .ADD_TOGETHER_TASK_TOCLASS_BASE_URL;
        String serverUrl = ServerUrl.GET_TOGETHERTASK_TO_CLASS_AND_STUDYGROUP;
        RequestHelper.postRequest(getActivity(),serverUrl,taskParams.toString(), listener);
    }

    /**
     * 听说 + 读写资源的上传
     * @param uploadParameter
     * @param schoolClassInfos
     */
    private void publishListenReadAndWriteStudyTask(UploadParameter uploadParameter,
                                                    List<ShortSchoolClassInfo> schoolClassInfos){
        showLoadingDialog();
        JSONObject taskParams = new JSONObject();
        boolean isPickerGroup = false;
        if (uploadParameter != null) {
            //是否为选择小组模式
            if (!TextUtils.isEmpty(schoolClassInfos.get(0).getGroupId())) {
                //选择小组模式
                isPickerGroup = true;
            }
            try {
                taskParams.put("TaskCreateId", uploadParameter.getMemberId());
                taskParams.put("TaskCreateName", uploadParameter.getCreateName());
                if (isPickerGroup) {
                    //发送到小组
                    JSONArray groupArray = new JSONArray();
                    JSONObject groupObject = null;
                    if (schoolClassInfos != null && schoolClassInfos.size() > 0) {
                        for (int i = 0; i < schoolClassInfos.size(); i++) {
                            groupObject = new JSONObject();
                            ShortSchoolClassInfo info = schoolClassInfos.get(i);
                            groupObject.put("GroupId", info.getGroupId());
                            groupObject.put("SchoolName", info.getSchoolName());
                            groupObject.put("SchoolId", info.getSchoolId());
                            groupArray.put(groupObject);
                        }
                    }
                    taskParams.put("SchoolStudyGroupList", groupArray);
                } else {
                    //发送到班级
                    JSONArray schoolArray = new JSONArray();
                    JSONObject schoolObject = null;
                    if (schoolClassInfos != null && schoolClassInfos.size() > 0) {
                        for (int i = 0; i < schoolClassInfos.size(); i++) {
                            schoolObject = new JSONObject();
                            ShortSchoolClassInfo info = schoolClassInfos.get(i);
                            schoolObject.put("ClassName", info.getClassName());
                            schoolObject.put("ClassId", info.getClassId());
                            schoolObject.put("SchoolName", info.getSchoolName());
                            schoolObject.put("SchoolId", info.getSchoolId());
                            schoolArray.put(schoolObject);
                        }
                    }
                    taskParams.put("SchoolClassList", schoolArray);
                }

                taskParams.put("TaskTitle", uploadParameter.getFileName());
                taskParams.put("StartTime", uploadParameter.getStartDate());
                taskParams.put("EndTime", uploadParameter.getEndDate());
                taskParams.put("DiscussContent", uploadParameter.getDisContent());
                //打分
                if (uploadParameter.NeedScore) {
                    taskParams.put("NeedScore", true);
                    taskParams.put("ScoringRule", uploadParameter.ScoringRule);
                }
                List<LookResDto> lookResDtos = uploadParameter.getLookResDtoList();
                JSONArray lookResArray = new JSONArray();
                JSONObject lookObject = null;
                if (lookResDtos != null && lookResDtos.size() > 0){
                    for (int i = 0;i < lookResDtos.size();i++){
                        lookObject = new JSONObject();
                        LookResDto lookDto = lookResDtos.get(i);
                        lookObject.put("TaskType",lookDto.getTaskId());
                        lookObject.put("ResId",lookDto.getResId());
                        lookObject.put("ResUrl",lookDto.getResUrl());
                        lookObject.put("ResTitle",lookDto.getResTitle() == null ? "" : lookDto
                                .getResTitle());
                        lookObject.put("Author",lookDto.getAuthor() == null ? "" : lookDto.getAuthor());
                        lookResArray.put(lookObject);
                    }
                }
                taskParams.put("TSDXResList",lookResArray);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        RequestHelper.RequestDataResultListener listener = new RequestHelper
                .RequestDataResultListener(getActivity(), DataResult.class) {
            @Override
            public void onSuccess(String json) {
                if(getActivity() == null) {
                    return;
                }
                if (TextUtils.isEmpty(json)) return;
                dismissLoadingDialog();
                try {
                    DataResult result = JSON.parseObject(json,DataResult.class);
                    if (result != null && result.isSuccess()) {
                        //布置完成刷新布置任务页面
                        CampusPatrolUtils.setHasStudyTaskAssigned(true);
                        TipMsgHelper.ShowLMsg(getActivity(), R.string.publish_course_ok);
                        LqIntroTaskHelper.getInstance().clearTaskList();
                        getActivity().sendBroadcast(new Intent().setAction(LessonSourceFragment.LESSON_RESOURCE_CHOICE_PUBLISH_ACTION));
                        EventBus.getDefault().post(new MessageEvent(MessageEventConstantUtils.SEND_HOME_WORK_LIB_SUCCESS));
                        finish();
                    } else {
                        String errorMessage = getString(R.string.publish_course_error);
                        if (result != null && !TextUtils.isEmpty(result.getErrorMessage())){
                            errorMessage = result.getErrorMessage();
                        }
                        TipMsgHelper.ShowLMsg(getActivity(),errorMessage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFinish() {
                super.onFinish();
                dismissLoadingDialog();
                CampusPatrolUtils.setHasStudyTaskAssigned(true);
                finish();
            }

            @Override
            public void onError(NetroidError error) {
                super.onError(error);
                dismissLoadingDialog();
            }
        };
        listener.setShowLoading(true);
        String serverUrl = isPickerGroup ? ServerUrl.GET_ADD_STUDY_LISTEN_READANDWRITE_GROUP_TASK_BASE_URL:ServerUrl
                .GET_ADD_STUDY_LISTEN_READANDWRITE_TASK_BASE_URL;
        RequestHelper.postRequest(getActivity(),serverUrl,taskParams.toString(), listener);
    }
    /**
     * 看课件多类型上传
     * @param uploadParameter
     * @param schoolClassInfos
     * @param showLoading
     */
    private void publishWatchWawaCourseStudyTask(UploadParameter uploadParameter,
                                                 List<ShortSchoolClassInfo> schoolClassInfos, boolean showLoading) {
        showLoadingDialog();
        JSONObject taskParams = new JSONObject();
        boolean isPickerGroup = false;
        if (uploadParameter != null) {
            //是否为选择小组模式
            if (!TextUtils.isEmpty(schoolClassInfos.get(0).getGroupId())) {
                //选择小组模式
                isPickerGroup = true;
            }
            try {
                taskParams.put("TaskCreateId", uploadParameter.getMemberId());
                taskParams.put("TaskCreateName", uploadParameter.getCreateName());
                StudyTaskUtils.handleSchoolClassData(taskParams,schoolClassInfos);
//                if (isPickerGroup) {
//                    //发送到小组
//                    JSONArray groupArray = new JSONArray();
//                    JSONObject groupObject = null;
//                    if (schoolClassInfos != null && schoolClassInfos.size() > 0) {
//                        for (int i = 0; i < schoolClassInfos.size(); i++) {
//                            groupObject = new JSONObject();
//                            ShortSchoolClassInfo info = schoolClassInfos.get(i);
//                            groupObject.put("GroupId", info.getGroupId());
//                            groupObject.put("SchoolName", info.getSchoolName());
//                            groupObject.put("SchoolId", info.getSchoolId());
//                            groupArray.put(groupObject);
//                        }
//                    }
//                    taskParams.put("SchoolStudyGroupList", groupArray);
//                } else {
//                    //发送到班级
//                    JSONArray schoolArray = new JSONArray();
//                    JSONObject schoolObject = null;
//                    if (schoolClassInfos != null && schoolClassInfos.size() > 0) {
//                        for (int i = 0; i < schoolClassInfos.size(); i++) {
//                            schoolObject = new JSONObject();
//                            ShortSchoolClassInfo info = schoolClassInfos.get(i);
//                            schoolObject.put("ClassName", info.getClassName());
//                            schoolObject.put("ClassId", info.getClassId());
//                            schoolObject.put("SchoolName", info.getSchoolName());
//                            schoolObject.put("SchoolId", info.getSchoolId());
//                            schoolArray.put(schoolObject);
//                        }
//                    }
//                    taskParams.put("SchoolClassList", schoolArray);
//                }

                taskParams.put("TaskTitle", uploadParameter.getFileName());
                taskParams.put("StartTime", uploadParameter.getStartDate());
                taskParams.put("EndTime", uploadParameter.getEndDate());
                //提交时间类型
                taskParams.put("SubmitType",uploadParameter.getSubmitType());
                taskParams.put("DiscussContent", uploadParameter.getDisContent());
                List<LookResDto> lookResDtos = uploadParameter.getLookResDtoList();
                JSONArray lookResArray = new JSONArray();
                JSONObject lookObject = null;
                if (lookResDtos != null && lookResDtos.size() > 0){
                    for (int i = 0;i < lookResDtos.size();i++){
                        lookObject = new JSONObject();
                        LookResDto lookDto = lookResDtos.get(i);
                        lookObject.put("Id",lookDto.getId());
                        lookObject.put("TaskId",lookDto.getTaskId());
                        lookObject.put("ResId",lookDto.getResId());
                        lookObject.put("ResUrl",lookDto.getResUrl());
                        lookObject.put("ResTitle",lookDto.getResTitle() == null ? "" : lookDto
                                .getResTitle());
                        lookObject.put("CreateId",lookDto.getCreateId() == null ? "" : lookDto
                                .getCreateId());
                        lookObject.put("CreateName",lookDto.getCreateName() == null ? "" :
                                lookDto.getCreateName());
                        lookObject.put("CreateTime",lookDto.getCreateTime() == null ? "" :
                                lookDto.getCreateTime());
                        lookObject.put("UpdateId",lookDto.getUpdateId() == null ? "" : lookDto
                                .getUpdateName());
                        lookObject.put("UpdateName",lookDto.getCreateName() == null ? "" :
                                lookDto.getCreateName());
                        lookObject.put("Deleted",lookDto.isDeleted());
                        lookObject.put("Author",lookDto.getAuthor() == null ? "" : lookDto.getAuthor());
                        lookObject.put("ResCourseId",lookDto.getResCourseId());
                        lookResArray.put(lookObject);
                    }
                }
                taskParams.put("LookResList",lookResArray);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        RequestHelper.RequestDataResultListener listener = new RequestHelper
                .RequestDataResultListener(getActivity(), DataResult.class) {
            @Override
            public void onSuccess(String json) {
                if(getActivity() == null) {
                    return;
                }
                if (TextUtils.isEmpty(json)) return;
                dismissLoadingDialog();
                try {
                    DataResult result = JSON.parseObject(json,DataResult.class);
                    if (result != null && result.isSuccess()) {
                        //布置完成刷新布置任务页面
                        CampusPatrolUtils.setHasStudyTaskAssigned(true);
                        LqIntroTaskHelper.getInstance().clearTaskList();
                        getActivity().sendBroadcast(new Intent().setAction(LessonSourceFragment.LESSON_RESOURCE_CHOICE_PUBLISH_ACTION));
                        EventBus.getDefault().post(new MessageEvent(MessageEventConstantUtils.SEND_HOME_WORK_LIB_SUCCESS));
                        TipMsgHelper.ShowLMsg(getActivity(), R.string.publish_course_ok);
                        finish();
                    } else {
                        TipMsgHelper.ShowLMsg(getActivity(), R.string.publish_course_error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFinish() {
                super.onFinish();
                dismissLoadingDialog();
                CampusPatrolUtils.setHasStudyTaskAssigned(true);
                finish();
            }

            @Override
            public void onError(NetroidError error) {
                super.onError(error);
                dismissLoadingDialog();
            }
        };
//        String serverUrl = isPickerGroup ? ServerUrl.GET_ADD_STUDY_LOOK_TASK_GROUP:ServerUrl
//                .WATCH_WAWA_COURSE_PUBLISH_STUDY_TASK_URL;
        String serverUrl = ServerUrl.GET_LOOKSTUDYTASK_TO_CLASS_AND_STUDYGROUP;
        RequestHelper.postRequest(getActivity(),serverUrl,taskParams.toString(), listener);
    }

    private void publishStudyTask(final UploadParameter uploadParameter, CourseData courseData,
                                  List<ShortSchoolClassInfo> schoolClassInfos,boolean
                                          isShowDialog) {
        showLoadingDialog();
        JSONObject taskParams = new JSONObject();
        //是否为选择小组模式
        boolean isPickerGroup = false;
        if (!TextUtils.isEmpty(schoolClassInfos.get(0).getGroupId())) {
            //选择小组模式
            isPickerGroup = true;
        }
        if (uploadParameter != null) {
            try {
                taskParams.put("TaskType", uploadParameter.getTaskType());
                taskParams.put("TaskCreateId", uploadParameter.getMemberId());
                taskParams.put("TaskCreateName", uploadParameter.getCreateName());
                StudyTaskUtils.handleSchoolClassData(taskParams,schoolClassInfos);
                taskParams.put("TaskTitle", uploadParameter.getFileName());
                if (courseData != null) {
                    if ((uploadParameter.getTaskType() == StudyTaskType.RETELL_WAWA_COURSE
                    || uploadParameter.getTaskType() == StudyTaskType.TASK_ORDER)
                            && uploadParameter.getType() == ResType.RES_TYPE_IMG){
                        taskParams.put("ResAuthor", courseData.code);
                        taskParams.put("ResId", courseData.resId);
                    } else {
                        taskParams.put("ResId", courseData.getIdType());
                    }
                    taskParams.put("ResUrl", courseData.resourceurl);
                } else {
                    taskParams.put("ResId", "");
                    taskParams.put("ResUrl", "");
                }
                //学程馆资源的id
                if (uploadParameter.getTaskType() == StudyTaskType.RETELL_WAWA_COURSE
                        || uploadParameter.getTaskType() == StudyTaskType.TASK_ORDER
                        || uploadParameter.getTaskType() == StudyTaskType.Q_DUBBING){
                    taskParams.put("ResCourseId",uploadParameter.getResCourseId());
                }
                if (uploadParameter.getTaskType() == StudyTaskType.TASK_ORDER){
                    taskParams.put("ResPropType",uploadParameter.getResPropType());
                }
                if (uploadParameter.getWorkOrderId() != null) {
                    taskParams.put("WorkOrderId", uploadParameter.getWorkOrderId());
                }
                if (uploadParameter.getWorkOrderUrl() != null) {
                    taskParams.put("WorkOrderUrl", uploadParameter.getWorkOrderUrl());
                }
                taskParams.put("StartTime", uploadParameter.getStartDate());
                taskParams.put("EndTime", uploadParameter.getEndDate());
                //提交时间类型
                taskParams.put("SubmitType",uploadParameter.getSubmitType());
                taskParams.put("ViewOthersTaskPermisson", uploadParameter.getViewOtherPermissionType());
                if (uploadParameter.getTaskType() == StudyTaskType.INTRODUCTION_WAWA_COURSE) {
                    taskParams.put("DiscussContent", uploadParameter.getDisContent());
                } else {
                    taskParams.put("DiscussContent", uploadParameter.getDescription());
                }
                //布置任务之英文写作相关的字段
                //作文要求
                taskParams.put("WritingRequire", uploadParameter.getWritingRequire());
                //打分公式
                taskParams.put("MarkFormula", uploadParameter.getMarkFormula());
                //作业字数最小值
                taskParams.put("WordCountMin", uploadParameter.getWordCountMin());
                //作业字数最大值
                taskParams.put("WordCountMax", uploadParameter.getWordCountMax());

                //打分
                if (uploadParameter.NeedScore) {
                    taskParams.put("NeedScore", true);
                    taskParams.put("ScoringRule", uploadParameter.ScoringRule);
                }

                //判断是不是任务单和听说课的多选
                int taskType = uploadParameter.getTaskType();
                if (taskType == StudyTaskType.TASK_ORDER
                        || taskType == StudyTaskType.RETELL_WAWA_COURSE
                        || taskType == StudyTaskType.Q_DUBBING
                        || taskType == StudyTaskType.MULTIPLE_OTHER
                        || taskType == StudyTaskType.MULTIPLE_OTHER_SUBMIT){
                    List<LookResDto> lookResDtos = uploadParameter.getLookResDtoList();
                    if (lookResDtos != null){
                        if (lookResDtos.size() == 1){
                            String point = lookResDtos.get(0).getPoint();
                            if (uploadParameter.NeedScore && !TextUtils.isEmpty(point)) {
                                taskParams.put("ScoringRule", StudyTaskUtils.getScoringRule(point));
                            }
                            if (taskType == StudyTaskType.RETELL_WAWA_COURSE) {
                                //完成方式
                                taskParams.put("RepeatCourseCompletionMode", lookResDtos.get(0).getCompletionMode());
                            } else if (taskType == StudyTaskType.Q_DUBBING) {
                                taskParams.put("ResPropType",lookResDtos.get(0).getResPropType());
                            }
                        } else if (lookResDtos.size() > 1){
                            if (taskType == StudyTaskType.RETELL_WAWA_COURSE) {
                                taskParams.put("TaskType", StudyTaskType.MULTIPLE_RETELL_COURSE);
                            } else if (taskType == StudyTaskType.Q_DUBBING) {
                                taskParams.put("TaskType", StudyTaskType.MULTIPLE_Q_DUBBING);
                            } else if (taskType == StudyTaskType.TASK_ORDER){
                                taskParams.put("TaskType", StudyTaskType.MULTIPLE_TASK_ORDER);
                            }
                            StudyTaskUtils.addMultipleTaskParams(taskParams, lookResDtos);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        RequestHelper.RequestDataResultListener listener = new RequestHelper
                .RequestDataResultListener(getActivity(), DataResult.class) {
            @Override
            public void onSuccess(String json) {
                if (getActivity() == null) return;
                if (TextUtils.isEmpty(json)) return;
                dismissLoadingDialog();
                try {
                    DataResult result = JSON.parseObject(json, DataResult.class);
                    if (result != null && result.isSuccess()) {
                        //布置完成刷新布置任务页面
                        CampusPatrolUtils.setHasStudyTaskAssigned(true);
                        LqIntroTaskHelper.getInstance().clearTaskList();
                        getActivity().sendBroadcast(new Intent().setAction(LessonSourceFragment.LESSON_RESOURCE_CHOICE_PUBLISH_ACTION));
                        EventBus.getDefault().post(new MessageEvent(MessageEventConstantUtils.SEND_HOME_WORK_LIB_SUCCESS));
                        TipMsgHelper.ShowLMsg(getActivity(), R.string.publish_course_ok);
                        finish();
                    } else {
                        TipMsgHelper.ShowLMsg(getActivity(), R.string.publish_course_error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissLoadingDialog();
                CampusPatrolUtils.setHasStudyTaskAssigned(true);
                finish();
            }

            @Override
            public void onError(NetroidError error) {
                super.onError(error);
                dismissLoadingDialog();
            }
        };
//        RequestHelper.postRequest(getActivity(),isPickerGroup ? ServerUrl.GET_ADDSTUDYTASKTOSTUDYGROUP : ServerUrl.ADD_STUDY_TASK_URL,taskParams.toString(),
//                listener);
        RequestHelper.postRequest(getActivity(), ServerUrl.GET_STUDYTASK_TO_CLASS_AND_STUDYGROUP, taskParams.toString(),
                listener);
    }

    private void uploadTaskToClassroom() {
        if (uploadParameter != null) {
            final LocalCourseDTO data = uploadParameter.getLocalCourseDTO();
            showLoadingDialog(getString(R.string.upload_and_wait), false);
            FileZipHelper.ZipUnzipParam param = new FileZipHelper.ZipUnzipParam(
                    data.getmPath(), Utils.TEMP_FOLDER + Utils.getFileNameFromPath(data.getmPath()
            ) + Utils.COURSE_SUFFIX);
            FileZipHelper.zip(param,
                    new FileZipHelper.ZipUnzipFileListener() {
                        @Override
                        public void onFinish(
                                FileZipHelper.ZipUnzipResult result) {
                            if (result != null && result.mIsOk) {
                                uploadParameter.setZipFilePath(result.mParam.mOutputPath);
                                UploadUtils.uploadResource(getActivity(), uploadParameter, new CallbackListener() {
                                    @Override
                                    public void onBack(final Object result) {
                                        if(getActivity() != null) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    dismissLoadingDialog();
                                                    if (result != null) {
                                                        CourseUploadResult uploadResult = (CourseUploadResult) result;
                                                        if (uploadResult.code != 0) {
                                                            TipMsgHelper.ShowLMsg(getActivity(), R.string
                                                                    .upload_file_failed);
                                                            return;
                                                        }
                                                        if (uploadResult.data != null && uploadResult.data.size() > 0) {
                                                            final CourseData courseData = uploadResult.data.get(0);
                                                            if (courseData != null) {
                                                                UploadCourseManager.commitCourseToClassSpace
                                                                        (getActivity(), courseData,
                                                                                uploadParameter, true);
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });
        }

    }


    void enterSyllabusSelection(final List<ContactItem> list) {
        List<String> ids = new ArrayList();
        for (ContactItem item : list) {
            ids.add(item.getId());
        }
        String schoolId = list.get(0).schoolId;
        uploadParameter.setSchoolIds(schoolId);
        uploadParameter.setContactItem(list.get(0));
        uploadParameter.setStudentIds(ids);
        UploadSchoolInfo schoolInfo = new UploadSchoolInfo();
        schoolInfo.SchoolId = schoolId;
        uploadParameter.setUploadSchoolInfo(schoolInfo);
        Bundle args = getArguments();
        args.putString(BookListActivity.EXTRA_SCHOOL_ID, schoolId);
        args.putInt(BookListActivity.EXTRA_BOOK_SOURCE, BookListActivity.SCHOOL_BOOK);
        args.putInt(BookListActivity.EXTRA_MODE, BookListActivity.UPLOAD_MODE);
        args.putSerializable(UploadParameter.class.getSimpleName(), uploadParameter);
        Intent intent = new Intent(getActivity(), BookListActivity.class);
        intent.putExtras(args);
        if (uploadParameter.isLocal() && uploadParameter.isTempData()){
            getActivity().startActivityForResult(intent,ActivityUtils.REQUEST_CODE_RETURN_REFRESH);
        }else {
            startActivity(intent);
            getActivity().finish();
        }
    }
}
