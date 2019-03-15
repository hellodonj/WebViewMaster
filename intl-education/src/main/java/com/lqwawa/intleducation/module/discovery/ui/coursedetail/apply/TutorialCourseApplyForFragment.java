package com.lqwawa.intleducation.module.discovery.ui.coursedetail.apply;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterDialogFragment;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.pay.PayCourseDialogFragment;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.pay.PayDialogNavigator;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;

import org.angmarch.views.NiceSpinner;

/**
 * @author mrmedici
 * @desc 课程帮辅申请的页面
 */
public class TutorialCourseApplyForFragment extends PresenterDialogFragment<TutorialCourseApplyForContract.Presenter>
    implements TutorialCourseApplyForContract.View{

    private View mRootView;
    // 批阅价格
    private EditText mEtMarkPrice;
    // 省
    private NiceSpinner mProvinceSpinner;
    // 市
    private NiceSpinner mCitySpinner;
    // 区
    private NiceSpinner mDistrictSpinner;

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
        final View view = mRootView = inflater.inflate(R.layout.dialog_pay_course,null);
        mEtMarkPrice = (EditText) view.findViewById(R.id.et_mark_price);
        mProvinceSpinner = (NiceSpinner) view.findViewById(R.id.province_spinner);
        mCitySpinner = (NiceSpinner) view.findViewById(R.id.city_spinner);
        mDistrictSpinner = (NiceSpinner) view.findViewById(R.id.district_spinner);
        return view;
    }

    @Override
    protected TutorialCourseApplyForContract.Presenter initPresenter() {
        return new TutorialCourseApplyForPresenter(this);
    }

    @Override
    public void updateApplyForCourseTutor(boolean result) {

    }

    /**
     * 私有的show方法
     * @param manager Fragment管理器
     */
    public static void show(@NonNull FragmentManager manager) {
        TutorialCourseApplyForFragment fragment = new TutorialCourseApplyForFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        fragment.show(manager,TutorialCourseApplyForFragment.class.getName());
    }
}
