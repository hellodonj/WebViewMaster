package com.galaxyschool.app.wawaschool.helper;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.db.NewWatchWawaCourseResourceDao;
import com.galaxyschool.app.wawaschool.db.dto.NewWatchWawaCourseResourceDTO;
import com.galaxyschool.app.wawaschool.fragment.IntroductionSuperTaskFragment;
import com.galaxyschool.app.wawaschool.pojo.HomeworkListInfo;
import com.galaxyschool.app.wawaschool.pojo.ResourceInfoTag;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordHomeWorkLocalHelper {

    public static void updateToLocal(List<HomeworkListInfo> infos,
                                         String studentId,
                                         String taskId) {
        if (infos == null || infos.size() == 0){
            return;
        }
        List<ResourceInfoTag> resourceInfoTags = new ArrayList<>();
        for (int i = 0; i < infos.size(); i++){
            HomeworkListInfo info = infos.get(i);
            ResourceInfoTag tag = new ResourceInfoTag();
            tag.setHasRead(info.isStudentDoneTask());
            tag.setResId(info.getResId());
            resourceInfoTags.add(tag);
        }
        operationDatabase(resourceInfoTags,infos,studentId,taskId);
    }

    public static void operationDatabase(List<ResourceInfoTag> resourceInfoTagList,
                                         List<HomeworkListInfo> infos,
                                          String studentId,
                                          String taskId) {
        //先检查本地是否有数据
        boolean hasData = false;
        boolean readAll = false;
        NewWatchWawaCourseResourceDTO targetDto = getLocalResource(taskId,studentId);
        if (targetDto != null) {
            hasData = true;
            readAll = targetDto.isReadAll();
        }
        //有数据且没有完全阅读时才需要更新数据。
        if (hasData) {
            if (!readAll) {
                //映射本地数据库数据
                transferDtoToResourceInfoTagData(targetDto,infos);
            }
        } else {
            //没数据需要存储数据
            NewWatchWawaCourseResourceDTO resourceDTO = new NewWatchWawaCourseResourceDTO();
            resourceDTO.setTaskId(taskId);
            //学生id，用来区分学生。
            resourceDTO.setStudentId(studentId);
            String ids = getItemIds(resourceInfoTagList);
            resourceDTO.setIds(ids);
            resourceDTO.setReadAll(false);
            //保存到数据库
            saveResourceToLocal(resourceDTO);
        }
    }

    /**
     * 更新操作
     *
     */
    public static void updateLocalData(String taskId,
                                 String studentId,
                                 List<HomeworkListInfo> infos) {
        if (infos == null || infos.size() == 0){
            return;
        }
        List<ResourceInfoTag> resourceInfoTags = new ArrayList<>();
        for (int i = 0; i < infos.size(); i++){
            HomeworkListInfo info = infos.get(i);
            ResourceInfoTag tag = new ResourceInfoTag();
            tag.setHasRead(info.isStudentDoneTask());
            tag.setResId(info.getResId());
            resourceInfoTags.add(tag);
        }
        try {
            //设置已读
            NewWatchWawaCourseResourceDTO dto = new NewWatchWawaCourseResourceDTO();
            //ids
            String ids = getItemIds(resourceInfoTags);
            dto.setIds(ids);
            //read标识
            boolean readAll = checkReadAll(resourceInfoTags);
            dto.setReadAll(readAll);
            NewWatchWawaCourseResourceDao.getInstance(UIUtil.getContext()).updateResource(taskId,
                    studentId, dto);
            if (readAll) {
                //全部读取了，学生需要调取更新任务已读接口。
                updateReadState(taskId, studentId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新看微课、看课件、看作业已读
     *
     * @param taskId
     * @param memberId
     */
    private static void updateReadState(String taskId, String memberId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("TaskId", taskId);
        params.put("StudentId", memberId);
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestDataResultListener<DataModelResult>(
                        UIUtil.getContext(), DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        } else {
                            //删除本地数据
                            deleteLocalResource(taskId,memberId);
                        }
                    }
                };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(UIUtil.getContext(), ServerUrl.STUDENT_COMMIT_HOMEWORK_URL,
                params, listener);

    }

    /**
     * 删除资源
     */
    private static void deleteLocalResource(String taskId,String studentId) {
        try {
            NewWatchWawaCourseResourceDao.getInstance(UIUtil.getContext())
                    .deleteResource(taskId, studentId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否全部阅读
     *
     * @return
     */
    private static boolean checkReadAll( List<ResourceInfoTag> list) {
        if (list != null && list.size() > 0) {
            boolean readAll = true;
            for (ResourceInfoTag tag : list) {
                if (tag != null) {
                    boolean readItem = tag.isHasRead();
                    if (!readItem) {
                        readAll = false;
                        break;
                    }
                }
            }
            return readAll;
        }
        return false;
    }



    /**
     * 转换数据
     *
     * @param targetDto
     * @return
     */
    private static void transferDtoToResourceInfoTagData(NewWatchWawaCourseResourceDTO targetDto,
                                                         List<HomeworkListInfo> resourceInfoTagList) {
        if (targetDto != null) {
            String ids = targetDto.getIds();
            if (!TextUtils.isEmpty(ids)) {
                List<ResourceInfoTag> resultList = JSON.parseArray(ids, ResourceInfoTag.class);
                if (resultList != null && resultList.size() > 0) {
                    if (resourceInfoTagList != null && resourceInfoTagList.size() > 0) {
                        for (HomeworkListInfo tag : resourceInfoTagList) {
                            if (tag != null) {
                                for (ResourceInfoTag result : resultList) {
                                    if (result != null) {
                                        if (result.isHasRead()
                                                && TextUtils.equals(result.getResId(), tag.getResId())) {
                                            //是同一条记录：从数据库取数据赋值
                                            tag.setIsStudentDoneTask(true);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 判断本地数据是否已读
     */
    private static NewWatchWawaCourseResourceDTO getLocalResource(String taskId,String studentId) {
        try {
            NewWatchWawaCourseResourceDTO resultDto = NewWatchWawaCourseResourceDao
                    .getInstance(UIUtil.getContext()).queryResource(taskId, studentId);
            return resultDto;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取数据库存储json数据
     *
     * @return
     */
    private static String getItemIds(List<ResourceInfoTag> resourceInfoTagList) {
        if (resourceInfoTagList != null && resourceInfoTagList.size() > 0) {
            return JSON.toJSONString(resourceInfoTagList);
        }
        return null;
    }

    /**
     * 保存看课件资源到数据库
     *
     * @param resourceDTO
     */
    private static void saveResourceToLocal(NewWatchWawaCourseResourceDTO resourceDTO) {
        try {
            NewWatchWawaCourseResourceDao.getInstance(UIUtil.getContext()).addResource(resourceDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
