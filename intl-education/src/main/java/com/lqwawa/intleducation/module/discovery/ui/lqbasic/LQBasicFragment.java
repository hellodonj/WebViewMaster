package com.lqwawa.intleducation.module.discovery.ui.lqbasic;

import android.os.Bundle;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.module.discovery.ui.study.OnlineStudyFragment;

/**
 * @author mrmedici
 * @desc 国家课程页面
 */
public class LQBasicFragment extends PresenterFragment<LQBasicConstract.Presenter>
    implements LQBasicConstract.View{

    public static LQBasicFragment newInstance(){
        LQBasicFragment fragment = new LQBasicFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected LQBasicConstract.Presenter initPresenter() {
        return new LQBasicPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_lq_basic;
    }
}
