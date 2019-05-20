package com.lqwawa.intleducation.module.discovery.ui.classcourse.common;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.common.utils.EmptyUtil;

/**
 * @author mrmedici
 * @desc Action Dialog的封装
 */
public class ActionDialogFragment extends DialogFragment implements View.OnClickListener {

    private static final String KEY_EXTRA_ACTION_TEXT_RESOURCE_UP =
            "KEY_EXTRA_ACTION_TEXT_RESOURCE_UP";
    private static final String KEY_EXTRA_ACTION_TEXT_RESOURCE_DOWN =
            "KEY_EXTRA_ACTION_TEXT_RESOURCE_DOWN";


    private Button mBtnActionUp;
    private Button mBtnActionDown;

    private ActionDialogNavigator mNavigator;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        final View view = inflater.inflate(R.layout.dialog_action_layout, null);
        mBtnActionUp = (Button) view.findViewById(R.id.btn_action_up);
        mBtnActionDown = (Button) view.findViewById(R.id.btn_action_down);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle arguments = getArguments();
        int actionResUp = arguments.getInt(KEY_EXTRA_ACTION_TEXT_RESOURCE_UP);
        int actionResDown = arguments.getInt(KEY_EXTRA_ACTION_TEXT_RESOURCE_DOWN);
        mBtnActionUp.setText(actionResUp);
        mBtnActionDown.setText(actionResDown);
        mBtnActionUp.setOnClickListener(this);
        mBtnActionDown.setOnClickListener(this);
    }

    public void setNavigator(@Nullable ActionDialogNavigator navigator) {
        this.mNavigator = navigator;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (EmptyUtil.isNotEmpty(mNavigator)) {
            if (viewId == R.id.btn_action_up) {
                mNavigator.onAction(v, Tag.UP);
                dismiss();
            } else if (viewId == R.id.btn_action_down) {
                mNavigator.onAction(v, Tag.DOWN);
                dismiss();
            }
        }
    }

    /**
     * 私有的show方法
     *
     * @param actionResUp 左边按钮字符串资源
     * @param actionResDown 右边按钮字符串资源
     * @param manager    Fragment管理器
     */
    public static void show(FragmentManager manager,
                            @NonNull @StringRes int actionResUp,
                            @NonNull @StringRes int actionResDown,
                            @NonNull ActionDialogNavigator navigator) {
        // 调用BottomSheetDialogFragment以及准备好的显示方法
        ActionDialogFragment fragment = new ActionDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_EXTRA_ACTION_TEXT_RESOURCE_UP, actionResUp);
        bundle.putInt(KEY_EXTRA_ACTION_TEXT_RESOURCE_DOWN, actionResDown);
        fragment.setArguments(bundle);
        fragment.setNavigator(navigator);
        fragment.show(manager, ActionDialogFragment.class.getName());
    }

    public enum Tag {
        UP, DOWN
    }
}
