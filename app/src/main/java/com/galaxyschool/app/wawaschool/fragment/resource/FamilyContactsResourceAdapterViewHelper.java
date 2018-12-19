package com.galaxyschool.app.wawaschool.fragment.resource;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.StudyTaskOpenHelper;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.pojo.CommitTask;
import com.galaxyschool.app.wawaschool.pojo.FamilyMemberInfo;
import com.galaxyschool.app.wawaschool.pojo.RoleType;

/**
 * 家庭通讯录Helper
 *
 * @param <T>
 */
public abstract class FamilyContactsResourceAdapterViewHelper<T> extends AdapterViewHelper<T> {

    private Activity activity;
    private String memberId;
    private String roles;
    private String typeNameArray[];

    public FamilyContactsResourceAdapterViewHelper(Activity activity,
                                                   AdapterView adapterView,
                                                   String roles,String memberId) {
        this(activity, adapterView, roles,memberId,R.layout.item_family_info);
    }

    public FamilyContactsResourceAdapterViewHelper(Activity activity,
                                                   AdapterView adapterView, String roles,
                                                   String memberId,int itemViewLayout) {
        super(activity, adapterView, itemViewLayout);
        this.activity = activity;
        this.roles = roles;
        this.memberId = memberId;
        typeNameArray = new String[]{activity.getString(R.string.dad),activity.getString(R.string
        .mum),activity.getString(R.string.parent),activity.getString(R.string.header_teacher),
        activity.getString(R.string.student)};
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        final FamilyMemberInfo data = (FamilyMemberInfo) getDataAdapter().getItem(position);
        if (data == null) {
            return view;
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder();
        }
        holder.data = data;

        //头像
        ImageView imageView = (ImageView) view.findViewById(R.id.user_icon);
        if (imageView != null) {
            MyApplication.getThumbnailManager(this.activity).displayUserIconWithDefault(
                    AppSettings.getFileUrl(data.getHeadPicUrl()), imageView,
                    R.drawable.default_user_icon);
            //点击头像进入详情
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityUtils.enterPersonalSpace(activity,data.getMemberId());
                }
            });
        }

        //真实姓名
        TextView textView = (TextView) view.findViewById(R.id.user_real_name);
        if (textView != null) {
            textView.setText(data.getRealName());
        }

        //账号
        textView = (TextView) view.findViewById(R.id.user_account);
        if (textView != null) {
            textView.setText(data.getNickName());
        }

        //加好友
       TextView addFriendTextView = (TextView) view.findViewById(R.id.add_good_friend);

        //聊天
       TextView chatTextView = (TextView) view.findViewById(R.id.chat);

        if (addFriendTextView != null && chatTextView != null){

            //如果是自己的话，都不显示。
            if (data.getMemberId().equals(memberId)){
                addFriendTextView.setVisibility(View.GONE);
                chatTextView.setVisibility(View.GONE);
            }else {
                //如果是好友,显示“聊天”，隐藏“加好友”。
//                if (data.isFriend()){
                if (data.getIsFriend()){
//                    chatTextView.setVisibility(View.VISIBLE);
                    chatTextView.setVisibility(View.GONE);
                    addFriendTextView.setVisibility(View.GONE);
                }else {
                    addFriendTextView.setVisibility(View.VISIBLE);
                    chatTextView.setVisibility(View.GONE);
                }
            }
        }

        //关系
        //0父亲 1母亲  2家长 3班主任 4学生
        int relationType = -1;
        relationType = data.getRelationType();
        textView = (TextView) view.findViewById(R.id.relationship);
        if (textView != null) {
            if (relationType != -1){
                showRelationShipByType(textView,relationType,data);
            }
        }

        //联系电话
        textView = (TextView) view.findViewById(R.id.telephone_number);
        if (textView != null) {
            textView.setText(data.getTelephone());
        }

        //邮箱
        textView = (TextView) view.findViewById(R.id.email_address);
        if (textView != null) {
            textView.setText(data.getEmail());
        }

        //取消绑定
        View unbindLayout = view.findViewById(R.id.layout_unbind);
        //如果自己是家长角色，并且条目是自己绑定的孩子。
        if (unbindLayout != null) {
            if (!TextUtils.isEmpty(roles)){
                if (roles.contains(String.valueOf(RoleType.ROLE_TYPE_PARENT))){
                    if (relationType != -1){
                        if (relationType == 4){
                            //学生条目有解除绑定
                            unbindLayout.setVisibility(View.VISIBLE);
                        }else {
                            unbindLayout.setVisibility(View.GONE);
                        }
                    }

                }else {
                    unbindLayout.setVisibility(View.GONE);
                }
            }
        }

        view.setTag(holder);
        return view;

    }

    public void showRelationShipByType(TextView textView, int relationType, FamilyMemberInfo data){
        if (textView == null || relationType < 0 || data == null){
            return;
        }

        if (data.getMemberId().equals(memberId)){
            //自己要显示“本人”
            textView.setText(typeNameArray[relationType]+activity.getString(R.string.myself));
        }else {
            textView.setText(typeNameArray[relationType]);
        }
    }

    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            return;
        }
        FamilyMemberInfo data = (FamilyMemberInfo) holder.data;
//            页面跳转
        if (data != null) {
        }
    }

}
