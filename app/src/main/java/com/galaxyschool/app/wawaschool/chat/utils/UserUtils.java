package com.galaxyschool.app.wawaschool.chat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.DefaultRetryPolicy;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.BasicUserInfoActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.chat.domain.User;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.DialogHelper;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.lqwawa.lqbaselib.net.NetErrorResult;
import com.lqwawa.lqbaselib.net.PostByMapParamsModelRequest;
import com.lqwawa.mooc.common.MOOCHelper;
import com.osastudio.common.utils.LQImageLoader;

import java.util.HashMap;
import java.util.Map;

public class UserUtils {
    /**
     * 根据username获取相应user，由于demo没有真实的用户数据，这里给的模拟的数据；
     * @param username
     * @return
     */
    public static User getUserInfo(String username){
        User user = DemoApplication.getInstance().getContactList().get(username);
        if(user == null){
            user = new User(username);
        }
            
        if(user != null){
            //demo没有这些数据，临时填充
            user.setNick(username);
//            user.setAvatar("http://downloads.easemob.com/downloads/57.png");
        }
        return user;
    }
    
    /**
     * 设置用户头像
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView) {
        User user = getUserInfo(username);
        if(user != null){
            setUserAvatarUrl(context, user.getAvatar(), imageView);
        }
    }

    public static void setUserAvatarUrl(Context context, String userAvatar, ImageView imageView) {
        if(!TextUtils.isEmpty(userAvatar)){
            LQImageLoader.displayImage(userAvatar, imageView, R.drawable.default_avatar);
//            Picasso.with(context).load(userAvatar).placeholder(R.drawable.default_avatar).into(imageView);
        }else{
            imageView.setImageResource(R.drawable.default_avatar);
//            Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
        }
    }

    public static void modifyBasicUserInfo(Activity activity,
                                     final UserInfo userInfo,
                                     CallbackListener listener) {
        DialogHelper.LoadingDialog dialog = DialogHelper.getIt(activity).GetLoadingDialog(0);
        String url = ServerUrl.SAVE_USERINFO_URL;
        if (!TextUtils.isEmpty(url)) {
            Map<String, String> mParams = new HashMap<String, String>();
            mParams.put("UserId", userInfo.getMemberId());
            mParams.put("NickName", userInfo.getNickName());
            if (!TextUtils.isEmpty(userInfo.getRealName())) {
                mParams.put("RealName", userInfo.getRealName());
            } else {
                mParams.put("RealName", "");
            }
            if (!TextUtils.isEmpty(userInfo.getSex())) {
                mParams.put("Sex", userInfo.getSex());
            } else {
                mParams.put("Sex", "");
            }
            if (!TextUtils.isEmpty(userInfo.getBindMobile())) {
                mParams.put("BindMobile", userInfo.getBindMobile());
            }
            if (!TextUtils.isEmpty(userInfo.getMobile())) {
                mParams.put("Mobile", userInfo.getMobile());
            }
            if (!TextUtils.isEmpty(userInfo.getEmail())) {
                mParams.put("Email", userInfo.getEmail());
            }
            if (!TextUtils.isEmpty(userInfo.getBirthday())) {
                mParams.put("Birthday", userInfo.getBirthday());
            }
            if (!TextUtils.isEmpty(userInfo.getPIntroduces())) {
                mParams.put("PIntroduces", userInfo.getPIntroduces());
            }
            if (!TextUtils.isEmpty(userInfo.getLocation())) {
                mParams.put("Location", userInfo.getLocation());
            }
            PostByMapParamsModelRequest request = new PostByMapParamsModelRequest(
                    url, mParams, new Listener<String>() {
                @Override
                public void onSuccess(String json) {
                    try {
                        Log.i("", "Login:onSuccess " + json);
                        UserInfo basicUserInfo = JSON.parseObject(json, UserInfo.class);
                        if (basicUserInfo != null) {
                            MOOCHelper.init(userInfo);
                            if (listener != null){
                                listener.onBack(true);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(NetroidError error) {
                    super.onError(error);
                    String es = error.getMessage();
                    try {
                        NetErrorResult result = JSON.parseObject(es, NetErrorResult.class);
                        if (result != null) {
                            if (result.isHasError()) {
                                TipMsgHelper.ShowMsg(activity, result.getErrorMessage());
                            }
                        }
                    } catch (Exception e) {
                        TipMsgHelper.ShowLMsg(activity, activity.getString(R.string.network_error));
                    }
                }

                @Override
                public void onFinish() {
                    // TODO Auto-generated method stub
                    super.onFinish();
                    dialog.dismiss();
                }
            });
            request.addHeader("Accept-Encoding", "*");
            request.setRetryPolicy(new DefaultRetryPolicy(60000, 0, DefaultRetryPolicy
                    .DEFAULT_BACKOFF_MULT));
            request.start(activity);
        }
    }
}
