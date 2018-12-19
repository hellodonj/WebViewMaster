package com.lqwawa.intleducation.factory.helper;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.LogUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.StringCallback;
import com.lqwawa.intleducation.factory.data.entity.LQwawaBaseResponse;
import com.lqwawa.intleducation.factory.data.entity.user.UserEntity;
import com.lqwawa.intleducation.factory.data.model.user.UserModel;
import com.lqwawa.lqbaselib.net.ErrorCodeUtil;

import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;
import java.util.Map;

/**
 * @author mrmedici
 * @desc 用户相关的网络请求工具类
 */
public class UserHelper {


    /**
     * 根据用户名查询真实姓名
     * @param needCreate needCreate
     * @param members 用户名
     */
    public static void requestRealNameWithNick(boolean needCreate,
                                               @NonNull List<UserModel> members,
                                               @NonNull final DataSource.Callback<List<UserEntity>> callback){
        // 准备数据
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("NeedCreate",needCreate);
        jsonObject.put("Members",members);

        RequestParams params = new RequestParams(AppConfig.ServerUrl.PostRealNameWithNickUrl);
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toJSONString());
        params.setConnectTimeout(10000);

        LogUtil.i(UserHelper.class,"send request ==== " +params.getUri());
        x.http().post(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                LogUtil.i(LiveHelper.class,"request "+params.getUri()+" result :"+str);
                TypeReference<LQwawaBaseResponse<List<UserEntity>>> mapTypeReference = new TypeReference<LQwawaBaseResponse<List<UserEntity>>>(){};
                LQwawaBaseResponse<List<UserEntity>> response = JSON.parseObject(str, mapTypeReference);
                if(!response.isHasError()){
                    List<UserEntity> model = response.getModel();
                    if(!EmptyUtil.isEmpty(callback)){
                        callback.onDataLoaded(model);
                    }
                }else{
                    String ErrorMessage = (String) response.getErrorMessage();
                    Map<String, String> errorHashMap = ErrorCodeUtil.getInstance().getErrorCodeMap();
                    if (errorHashMap != null && errorHashMap.size() > 0 && !TextUtils.isEmpty(ErrorMessage)
                            && errorHashMap.containsKey(ErrorMessage)) {
                        UIUtil.showToastSafe(errorHashMap.get(ErrorMessage));
                    }
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(UserHelper.class,"request "+params.getUri()+" failed");
                if(!EmptyUtil.isEmpty(callback)){
                    callback.onDataNotAvailable(com.lqwawa.intleducation.R.string.net_error_tip);
                }
            }
        });
    }


    /**
     * 根据用户名查询真实姓名
     * @param userId 用户Id
     * @param callback 接口回调对象
     */
    public static void requestUserInfoWithUserId(@NonNull String userId,
                                                 @NonNull final DataSource.Callback<UserEntity> callback){
        // 准备数据
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("UserId",userId);

        RequestParams params = new RequestParams(AppConfig.ServerUrl.PostLoadUserInfoWithUserId);
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toJSONString());
        params.setConnectTimeout(10000);

        LogUtil.i(UserHelper.class,"send request ==== " +params.getUri());
        x.http().post(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                LogUtil.i(LiveHelper.class,"request "+params.getUri()+" result :"+str);
                TypeReference<LQwawaBaseResponse<UserEntity>> mapTypeReference = new TypeReference<LQwawaBaseResponse<UserEntity>>(){};
                LQwawaBaseResponse<UserEntity> response = JSON.parseObject(str, mapTypeReference);
                if(!response.isHasError()){
                    UserEntity entity = response.getModel();
                    if(!EmptyUtil.isEmpty(callback)){
                        callback.onDataLoaded(entity);
                    }
                }else{
                    String ErrorMessage = (String) response.getErrorMessage();
                    Map<String, String> errorHashMap = ErrorCodeUtil.getInstance().getErrorCodeMap();
                    if (errorHashMap != null && errorHashMap.size() > 0 && !TextUtils.isEmpty(ErrorMessage)
                            && errorHashMap.containsKey(ErrorMessage)) {
                        UIUtil.showToastSafe(errorHashMap.get(ErrorMessage));
                    }
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.w(UserHelper.class,"request "+params.getUri()+" failed");
                if(!EmptyUtil.isEmpty(callback)){
                    callback.onDataNotAvailable(com.lqwawa.intleducation.R.string.net_error_tip);
                }
            }
        });
    }

}
