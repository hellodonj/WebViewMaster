package com.lqwawa.intleducation.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.common.utils.EmptyUtil;

import net.qiujuer.genius.ui.widget.Button;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 网络错误显示的View
 * @date 2018/05/16 09:44
 * @history v1.0
 * **********************************
 */
public class NetErrorView extends FrameLayout{

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private Button mBtnReload;
    private ImageView mIvFailed;
    private TextView mTvFailed;
    private OnReloadDataListener mListener;

    public NetErrorView(@NonNull Context context) {
        this(context,null);
    }

    public NetErrorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public NetErrorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        initView(attrs,defStyleAttr);
    }

    /**
     * View的初始化
     * @param attrs
     * @param defStyleAttr
     */
    private void initView(@Nullable AttributeSet attrs, int defStyleAttr) {
        mLayoutInflater.inflate(R.layout.holder_net_error_layout,this);
        mBtnReload = (Button) findViewById(R.id.btn_reload);
        mIvFailed = (ImageView) findViewById(R.id.iv_failed);
        mTvFailed = (TextView) findViewById(R.id.tv_failed);

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.NetErrorView, defStyleAttr, 0);

        Drawable mDrawable = a.getDrawable(R.styleable.NetErrorView_net_error_icon);
        CharSequence text = a.getText(R.styleable.NetErrorView_net_error_text);

        // 设置参数
        setNetErrorIcon(mDrawable);
        setNetErrorText(text);

        a.recycle();


        mBtnReload.setOnClickListener(Void ->{
            //  重新加载数据
            if(!EmptyUtil.isEmpty(mListener)){
                mListener.reloadData();
            }
        });
    }

    /**
     * 设置网络错误的图片资源
     * @param drawable 图片资源
     */
    public void setNetErrorIcon(@NonNull Drawable drawable){
        mIvFailed.setImageDrawable(drawable);
    }

    /**
     * 设置网络错误提示文本
     * @param text 文本
     */
    public void setNetErrorText(@NonNull CharSequence text){
        mTvFailed.setText(text);
    }


    /**
     * 添加重新加载事件的监听
     * @param listener 监听对象
     */
    public void setOnReloadDataListener(OnReloadDataListener listener){
        this.mListener = listener;
    }

    /**
     * **********************************
     *
     * @author MrMedici
     * @email mr.medici@foxmail.com
     * @function 当网络发生异常,用户点击重新加载的回调
     * @date 2018/05/16 10:19
     * @history v1.0
     * **********************************
     */
    public interface OnReloadDataListener {
        // 触发重新加载
        void reloadData();
    }

}
