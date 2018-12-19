package com.lqwawa.intleducation.base.widgets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.common.utils.ActivityUtils;

/**
 * @author medici
 * @desc 无权限展示的空页面
 */
public class NoPermissionView extends FrameLayout{

    private Context mContext;
    private TextView mDescription;
    private TextView mCallPhone;

    public NoPermissionView(@NonNull Context context) {
        this(context,null);
    }

    public NoPermissionView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public NoPermissionView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    private void initView(){
        final Context context = mContext;
        LayoutInflater.from(context).inflate(R.layout.holder_vip_permissions_layout,this);
        mDescription = (TextView) findViewById(R.id.tv_content);
        mCallPhone = (TextView) findViewById(R.id.tv_phone_number);
        mCallPhone.setOnClickListener(view->{
            // 拨打电话
            ActivityUtils.gotoTelephone(mContext);
        });
    }

    /**
     * 设置提示文本
     * @param description 提示文本字符串
     */
    public void setDescription(@NonNull String description){
        mDescription.setText(description);
    }
}
