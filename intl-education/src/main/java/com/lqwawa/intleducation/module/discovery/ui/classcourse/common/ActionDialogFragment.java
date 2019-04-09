package com.lqwawa.intleducation.module.discovery.ui.classcourse.common;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.pay.PayCourseDialogFragment;

/**
 * @author mrmedici
 * @desc Action Dialog的封装
 */
public class ActionDialogFragment extends DialogFragment implements View.OnClickListener{

    private static final String KEY_EXTRA_DIALOG_CONTENT = "KEY_EXTRA_DIALOG_CONTENT";
    private static final String KEY_EXTRA_ACTION_LEFT_TEXT_RESOURCE = "KEY_EXTRA_ACTION_LEFT_TEXT_RESOURCE";
    private static final String KEY_EXTRA_ACTION_RIGHT_TEXT_RESOURCE = "KEY_EXTRA_ACTION_RIGHT_TEXT_RESOURCE";


    private View mRootView;
    private TextView mTvDialogContent;
    private Button mBtnActionLeft;
    private Button mBtnActionRight;

    private ActionDialogNavigator mNavigator;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getContext(),R.style.AppTheme_Dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = mRootView = inflater.inflate(R.layout.dialog_action_layout,null);
        mTvDialogContent = (TextView) view.findViewById(R.id.tv_dialog_content);
        mBtnActionLeft = (Button) view.findViewById(R.id.btn_action_left);
        mBtnActionRight = (Button) view.findViewById(R.id.btn_action_right);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle arguments = getArguments();
        int leftActionRes = arguments.getInt(KEY_EXTRA_ACTION_LEFT_TEXT_RESOURCE);
        int rightActionRes = arguments.getInt(KEY_EXTRA_ACTION_RIGHT_TEXT_RESOURCE);
        String dialogContent = arguments.getString(KEY_EXTRA_DIALOG_CONTENT);
        mBtnActionLeft.setText(leftActionRes);
        mBtnActionRight.setText(rightActionRes);
        mTvDialogContent.setText(dialogContent);
        mBtnActionLeft.setOnClickListener(this);
        mBtnActionRight.setOnClickListener(this);
    }

    public void setNavigator(@Nullable ActionDialogNavigator navigator){
        this.mNavigator = navigator;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(EmptyUtil.isNotEmpty(mNavigator)) {
            if (viewId == R.id.btn_action_left) {
                mNavigator.onAction(v,Tag.LEFT);
                dismiss();
            } else if (viewId == R.id.btn_action_right) {
                mNavigator.onAction(v,Tag.RIGHT);
                dismiss();
            }
        }
    }

    /**
     * 私有的show方法
     * @param dialogContent Dialog内容
     * @param leftActionRes 左边按钮字符串资源
     * @param rightActionRes 右边按钮字符串资源
     * @param manager Fragment管理器
     */
    public static void show(FragmentManager manager,
                            @NonNull String dialogContent,
                            @NonNull @StringRes int leftActionRes,
                            @NonNull @StringRes int rightActionRes,
                            @NonNull ActionDialogNavigator navigator) {
        // 调用BottomSheetDialogFragment以及准备好的显示方法
        ActionDialogFragment fragment = new ActionDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_EXTRA_ACTION_LEFT_TEXT_RESOURCE,leftActionRes);
        bundle.putInt(KEY_EXTRA_ACTION_RIGHT_TEXT_RESOURCE,rightActionRes);
        bundle.putString(KEY_EXTRA_DIALOG_CONTENT,dialogContent);
        fragment.setArguments(bundle);
        fragment.setNavigator(navigator);
        fragment.show(manager,PayCourseDialogFragment.class.getName());
    }

    public enum Tag{
        LEFT,RIGHT
    }
}
