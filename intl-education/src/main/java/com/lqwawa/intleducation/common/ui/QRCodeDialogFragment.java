package com.lqwawa.intleducation.common.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.image.LQwawaImageUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.helper.CourseHelper;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.apply.CourseApplyForNavigator;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.apply.TutorialCourseApplyForFragment;

public class QRCodeDialogFragment extends DialogFragment {

    private static final String KEY_EXTRA_DIALOG_TITLE = "KEY_EXTRA_DIALOG_TITLE";
    private static final String KEY_EXTRA_DIALOG_DESC = "KEY_EXTRA_DIALOG_DESC";
    private static final String KEY_EXTRA_DIALOG_IMAGE_URL = "KEY_EXTRA_DIALOG_IMAGE_URL";

    private View mRootView;
    private TextView mTvDialogTitle;
    private TextView mTvDialogDesc;
    private ImageView mIvQRCode;
    private TextView mTvSave;

    private OnSaveListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getContext(),R.style.AppTheme_Dialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = mRootView = inflater.inflate(R.layout.fragment_qrcode_dialog,null);
        mTvDialogTitle = (TextView) view.findViewById(R.id.tv_dialog_title);
        mTvDialogDesc = (TextView) view.findViewById(R.id.tv_dialog_desc);
        mIvQRCode = (ImageView) view.findViewById(R.id.iv_qrcode);
        mTvSave = (TextView) view.findViewById(R.id.tv_save);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle arguments = getArguments();
        String dialogTitle = arguments.getString(KEY_EXTRA_DIALOG_TITLE);
        String dialogDesc = arguments.getString(KEY_EXTRA_DIALOG_DESC);
        String url = arguments.getString(KEY_EXTRA_DIALOG_IMAGE_URL);
        StringUtil.fillSafeTextView(mTvDialogTitle,dialogTitle);
        StringUtil.fillSafeTextView(mTvDialogDesc,dialogDesc);
        LQwawaImageUtil.loadCommonIcon(getContext(),mIvQRCode,url);

        mTvSave.setOnClickListener(view->{
            if(EmptyUtil.isNotEmpty(mListener)){
                mListener.onSave();
                dismiss();
            }
        });
    }

    public void setOnSaveListener(@NonNull OnSaveListener listener){
        this.mListener = listener;
    }

    /**
     * 私有的show方法
     * @param manager Fragment管理器
     */
    public static void show(@NonNull FragmentManager manager,
                            @NonNull String dialogTitle,
                            @NonNull String dialogDesc,
                            @NonNull String url,
                            @NonNull OnSaveListener listener) {
        QRCodeDialogFragment fragment = new QRCodeDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_DIALOG_TITLE,dialogTitle);
        bundle.putString(KEY_EXTRA_DIALOG_DESC,dialogDesc);
        bundle.putString(KEY_EXTRA_DIALOG_IMAGE_URL,url);
        fragment.setArguments(bundle);
        fragment.setOnSaveListener(listener);
        fragment.show(manager,QRCodeDialogFragment.class.getName());
    }

    public interface OnSaveListener{
        void onSave();
    }
}
