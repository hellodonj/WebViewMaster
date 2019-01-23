package com.lqwawa.intleducation.factory.helper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.Factory;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.LogUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.StringCallback;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.coursedetails.CourseDetailItemParams;
import com.lqwawa.intleducation.module.discovery.vo.CourseDetailsVo;
import com.lqwawa.intleducation.module.learn.vo.LqTaskCommitListVo;
import com.lqwawa.intleducation.module.learn.vo.SectionDetailsVo;
import com.lqwawa.lqbaselib.net.ErrorCodeUtil;

import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.Map;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 跟单元相关的网络请求帮助类
 * @date 2018/04/12 18:34
 * @history v1.0
 * **********************************
 */
public class LessonHelper {

    /**
     * 根据任务Id,获取到相应的已经提交的任务
     * @param taskId 任务Id
     * @param studentId userId,如果是家长身份,就不传;
     * @param callback 回调对象
     */
    public static void getCommittedTaskByTaskId(@NonNull String taskId,
                                             @Nullable String studentId,
                                             @NonNull final DataSource.Callback<LqTaskCommitListVo> callback) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("TaskId",taskId);
        jsonObject.put("StudentId",studentId);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetCommittedTask);
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toJSONString());
        params.setConnectTimeout(10000);
        x.http().post(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                TypeReference<Map<String,Object>> mapTypeReference = new TypeReference<Map<String,Object>>(){};
                Map<String,Object> result = JSON.parseObject(str, mapTypeReference);
                if((int)result.get("ErrorCode") == 0){
                    Map<String,Object> model = (Map<String, Object>) result.get("Model");
                    String dataStr = JSONObject.toJSONString(model.get("Data"));
                    LqTaskCommitListVo taskCommitListVo = JSONObject.parseObject(dataStr,LqTaskCommitListVo.class);
                    if(callback != null){
                        callback.onDataLoaded(taskCommitListVo);
                    }
                }else{
                    String ErrorMessage = (String) result.get("ErrorMessage");
                    Map<String, String> errorHashMap = ErrorCodeUtil.getInstance().getErrorCodeMap();
                    if (errorHashMap != null && errorHashMap.size() > 0 && !TextUtils.isEmpty(ErrorMessage)
                            && errorHashMap.containsKey(ErrorMessage)) {
                        UIUtil.showToastSafe(errorHashMap.get(ErrorMessage));
                    }
                }

                // 在使用FastJson 进行Model解析的时候出现问题
                /*TypeReference<LQwawaBaseEntity<LqTaskCommitListVo>> typeReference = new TypeReference<LQwawaBaseEntity<LqTaskCommitListVo>>(){};
                LQwawaBaseEntity<LqTaskCommitListVo> _result = JSON.parseObject(str, typeReference);
                if (_result.isSucceed()) {
                    LqTaskCommitListVo taskCommitListVo = _result.getMode().getData();
                    if(callback != null){
                        callback.onDataLoaded(taskCommitListVo);
                    }
                }else{
                    Map<String, String> errorHashMap = ErrorCodeUtil.getInstance().getErrorCodeMap();
                    if (errorHashMap != null && errorHashMap.size() > 0 && !TextUtils.isEmpty(_result.getErrorMessage())
                            && errorHashMap.containsKey(_result.getErrorMessage())) {
                        _result.setErrorMessage(errorHashMap.get(_result.getErrorMessage()));
                    }
                    UIUtil.showToastSafe(_result.getErrorMessage());
                }*/
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                if(null != callback){
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }


    /**
     * 根据任务Id,获取到相应的已经提交的任务,新接口
     * @param taskId 任务Id
     * @param studentId userId,如果是家长身份,就不传;
     * @param classId 班级学程传参
     * @param schoolId 机构学程传参
     * @param sortStudentId 如果填写就把该学生对应的任务排在前面,支持多个ID排序,多个ID时用逗号分隔传值
     * @param commitType 提交类型 1 复述提交列表 5 语音评测列表
     * @param callback 回调对象
     */
    public static void getNewCommittedTaskByTaskId(@NonNull String taskId,
                                                @Nullable String studentId,
                                                @NonNull String classId,
                                                @NonNull String schoolId,
                                                @NonNull String sortStudentId,
                                                int commitType,
                                                @NonNull final DataSource.Callback<LqTaskCommitListVo> callback) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("TaskId",taskId);
        jsonObject.put("StudentId",studentId);
        if(EmptyUtil.isNotEmpty(classId))
        jsonObject.put("ClassId",classId);
        if(EmptyUtil.isNotEmpty(schoolId))
        jsonObject.put("SchoolId",schoolId);
        if(EmptyUtil.isNotEmpty(sortStudentId))
        jsonObject.put("SortStudentId",sortStudentId);

        jsonObject.put("CommitType",commitType);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetNewCommittedTask);
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toJSONString());
        params.setConnectTimeout(10000);
        x.http().post(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                TypeReference<Map<String,Object>> mapTypeReference = new TypeReference<Map<String,Object>>(){};
                Map<String,Object> result = JSON.parseObject(str, mapTypeReference);
                if((int)result.get("ErrorCode") == 0){
                    Map<String,Object> model = (Map<String, Object>) result.get("Model");
                    String dataStr = JSONObject.toJSONString(model.get("Data"));
                    LqTaskCommitListVo taskCommitListVo = JSONObject.parseObject(dataStr,LqTaskCommitListVo.class);
                    if(callback != null){
                        callback.onDataLoaded(taskCommitListVo);
                    }
                }else{
                    String ErrorMessage = (String) result.get("ErrorMessage");
                    Map<String, String> errorHashMap = ErrorCodeUtil.getInstance().getErrorCodeMap();
                    if (errorHashMap != null && errorHashMap.size() > 0 && !TextUtils.isEmpty(ErrorMessage)
                            && errorHashMap.containsKey(ErrorMessage)) {
                        UIUtil.showToastSafe(errorHashMap.get(ErrorMessage));
                    }
                }

                // 在使用FastJson 进行Model解析的时候出现问题
                /*TypeReference<LQwawaBaseEntity<LqTaskCommitListVo>> typeReference = new TypeReference<LQwawaBaseEntity<LqTaskCommitListVo>>(){};
                LQwawaBaseEntity<LqTaskCommitListVo> _result = JSON.parseObject(str, typeReference);
                if (_result.isSucceed()) {
                    LqTaskCommitListVo taskCommitListVo = _result.getMode().getData();
                    if(callback != null){
                        callback.onDataLoaded(taskCommitListVo);
                    }
                }else{
                    Map<String, String> errorHashMap = ErrorCodeUtil.getInstance().getErrorCodeMap();
                    if (errorHashMap != null && errorHashMap.size() > 0 && !TextUtils.isEmpty(_result.getErrorMessage())
                            && errorHashMap.containsKey(_result.getErrorMessage())) {
                        _result.setErrorMessage(errorHashMap.get(_result.getErrorMessage()));
                    }
                    UIUtil.showToastSafe(_result.getErrorMessage());
                }*/
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                if(null != callback){
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 获取章小节的学习任务列表
     * @param token 家长传孩子的token
     * @param classId 如果是班级学程入口,并且是老师身份
     * @param courseId 课程Id
     * @param sectionId 小节Id
     * @param role 1老师 2学生
     */
    public static void requestChapterStudyTask(@NonNull String token,
                                               @Nullable String classId,
                                               @NonNull String courseId,
                                               @NonNull String sectionId,
                                               int role,
                                               @NonNull DataSource.Callback<SectionDetailsVo> callback){
        RequestVo requestVo = new RequestVo();
        if(EmptyUtil.isNotEmpty(token)){
            requestVo.addParams("token",token);
        }

        requestVo.addParams("courseId",courseId);
        requestVo.addParams("sectionId",sectionId);
        requestVo.addParams("role",role);
        if(role == 1 && EmptyUtil.isNotEmpty(classId)){
            requestVo.addParams("classId", classId);
        }
        RequestParams params = new RequestParams(AppConfig.ServerUrl.courseSectionDetail + requestVo.getParams());
        params.setConnectTimeout(10000);

        LogUtil.i(LessonHelper.class,"send request ==== " +params.getUri());
        x.http().get(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                LogUtil.i(LessonHelper.class,"request "+ params.getUri() + " result :"+str);
                ResponseVo<SectionDetailsVo> responseVo = JSON.parseObject(str,new TypeReference<ResponseVo<SectionDetailsVo>>() {});
                if (responseVo.isSucceed()) {
                    if(EmptyUtil.isNotEmpty(callback)){
                        callback.onDataLoaded(responseVo.getData());
                    }
                }else{
                    Factory.decodeRspCode(responseVo.getCode(),callback);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(LessonHelper.class,"request "+params.getUri()+" failed");
                if(!EmptyUtil.isEmpty(callback)){
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }


    /**
     * 看课件，听说课，读写单已读
     * @param taskType 1 看课件
     * @param cwareId 课件Id
     * @param resId 除了看课件,其它都需要传资源Id
     */
    public static void requestAddSourceFlag(int taskType,
                                               @NonNull String cwareId,
                                               @NonNull String resId,
                                               @NonNull DataSource.SucceedCallback<Void> callback){
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("cwareId",cwareId);
        if(taskType != 1){
            requestVo.addParams("resId",resId);
        }
        RequestParams params = new RequestParams(AppConfig.ServerUrl.setReaded + requestVo.getParams());
        params.setConnectTimeout(10000);

        LogUtil.i(LessonHelper.class,"send request ==== " +params.getUri());
        x.http().get(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                LogUtil.i(LessonHelper.class,"request "+ params.getUri() + " result :"+str);
                ResponseVo<Void> responseVo = JSON.parseObject(str,new TypeReference<ResponseVo<Void>>() {});
                if (responseVo.isSucceed()) {
                    if(EmptyUtil.isNotEmpty(callback)){
                        callback.onDataLoaded(responseVo.getData());
                    }
                }else{
                    UIUtil.showToastSafe(R.string.net_error_tip);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(LessonHelper.class,"request "+params.getUri()+" failed");
                if(!EmptyUtil.isEmpty(callback)){
                    UIUtil.showToastSafe(R.string.net_error_tip);
                }
            }
        });
    }

    /**
     * 点击听说课,读写单 分发任务
     * @param taskId 任务Id
     * @param studentId 学生Id memberId 其它身份不传
     * @param callback 回调对象
     */
    public static void DispatchTask(@NonNull String taskId,
                                   String studentId,

                                   final @NonNull DataSource.Callback<Void> callback){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("TaskId",taskId);
        jsonObject.put("StudentId",studentId);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.DispatchStudentTask);
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toJSONString());
        params.setConnectTimeout(10000);
        x.http().post(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                // 分发任务,不关成功与否,都是跳转
                if(null != callback){
                    callback.onDataLoaded(null);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                if(null != callback){
                    callback.onDataNotAvailable(R.string.net_error_tip);
                }
            }
        });
    }
}
