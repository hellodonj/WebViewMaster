package com.galaxyschool.app.wawaschool.chat.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.chat.domain.User;
import com.osastudio.common.utils.LQImageLoader;

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
    
}
