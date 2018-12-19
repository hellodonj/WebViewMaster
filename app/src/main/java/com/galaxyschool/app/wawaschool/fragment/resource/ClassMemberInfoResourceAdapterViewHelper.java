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
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassMemberDetails;
import com.galaxyschool.app.wawaschool.pojo.FamilyMemberInfo;
import com.galaxyschool.app.wawaschool.pojo.RoleType;

/**
 * 班级成员信息Helper
 *
 * @param <T>
 */
public abstract class ClassMemberInfoResourceAdapterViewHelper<T> extends AdapterViewHelper<T> {

    private Activity activity;

    public ClassMemberInfoResourceAdapterViewHelper(Activity activity,
                                                    AdapterView adapterView) {
        this(activity, adapterView,R.layout.item_class_member_info);
    }

    public ClassMemberInfoResourceAdapterViewHelper(Activity activity,
                                                    AdapterView adapterView,int itemViewLayout) {
        super(activity, adapterView, itemViewLayout);
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        final ContactsClassMemberDetails data = (ContactsClassMemberDetails) getDataAdapter()
                .getItem(position);
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
            //点击头像进入个人详情
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //游客之类的memberId为空的不给点击。
                    if (!TextUtils.isEmpty(data.getMemberId())) {
                        ActivityUtils.enterPersonalSpace(activity, data.getMemberId());
                    }
                }
            });

        }


        //账号
        TextView textView = (TextView) view.findViewById(R.id.user_account);
        if (textView != null) {
            textView.setText(data.getNickname());
        }

        //关系描述
         textView = (TextView) view.findViewById(R.id.relation_ship);
        if (textView != null) {
            if (data.getRole() == RoleType.ROLE_TYPE_PARENT) {
                if (data.isFather()) {
                    textView.setText(activity.getString(R.string.whose_parent, data
                            .getStudentName(),
                            activity.getString(R.string.dad)));
                } else if (data.isMother()) {
                    textView.setText(activity.getString(R.string.whose_parent, data
                            .getStudentName(),
                            activity.getString(R.string.mum)));
                } else {
                    textView.setText(activity.getString(R.string.whose_parent, data
                            .getStudentName(),
                            activity.getString(R.string.parent)));
                }
            } else {
                textView.setText(data.getIdentity());
            }
        }

        //姓名
        textView = (TextView) view.findViewById(R.id.real_name);
        if (textView != null) {
            textView.setText(data.getRealName());
        }


        //联系电话
        textView = (TextView) view.findViewById(R.id.telephone_number);
        if (textView != null) {
            textView.setText(data.getTelephone());
        }

        //发送邮件
        textView = (TextView) view.findViewById(R.id.email_address);
        if (textView != null) {
            textView.setText(data.getEmail());
        }

        view.setTag(holder);
        return view;

    }

    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            return;
        }
        ContactsClassMemberDetails data = (ContactsClassMemberDetails) holder.data;
//            页面跳转
        if (data != null) {
        }
    }

}
