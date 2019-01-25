package com.lqwawa.intleducation.module.discovery.ui.classcourse.popup;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.module.learn.tool.TaskSliderHelper;

/**
 * @author mrmedici
 * @desc 弹窗是否放弃作业库
 */
public class WorkCartDialogFragment extends AppCompatDialogFragment implements View.OnClickListener {

    private TextView mTvContent;
    private TextView mBtnConfirm;
    private TextView mBtnCancel;

    private ActionCallback mCallback;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getContext(),R.style.Theme_ContactsDialog);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        WindowManager m = getActivity().getWindow().getWindowManager();
        Display display = m.getDefaultDisplay();
        WindowManager.LayoutParams p =  getDialog().getWindow().getAttributes();
        float widthRatio = 0.8f;
        float heightRatio = 0.5f;
        p.width = (int) (display.getWidth() * widthRatio);
        p.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(p);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_work_cart,null);
        mTvContent = (TextView) view.findViewById(R.id.tv_content);
        mBtnConfirm = (TextView) view.findViewById(R.id.btn_confirm);
        mBtnCancel = (TextView) view.findViewById(R.id.btn_cancel);

        mBtnConfirm.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
        return view;
    }

    /**
     * 设置事件回调
     * @param callback 事件回调对象
     */
    public void setCallback(@NonNull ActionCallback callback){
        this.mCallback = callback;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.btn_confirm){
            if(EmptyUtil.isNotEmpty(mCallback)){
                mCallback.onConfirm();
            }
        }else if(viewId == R.id.btn_cancel){
            if(EmptyUtil.isNotEmpty(mCallback)){
                if(EmptyUtil.isNotEmpty(TaskSliderHelper.onWorkCartListener)){
                    TaskSliderHelper.onWorkCartListener.clearCartResource();
                }
                mCallback.onCancel();
            }
        }
    }

    public interface ActionCallback{
        void onConfirm();
        void onCancel();
    }
}
