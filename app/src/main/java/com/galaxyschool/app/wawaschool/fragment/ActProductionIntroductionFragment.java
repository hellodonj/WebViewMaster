package com.galaxyschool.app.wawaschool.fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.pojo.PerformClassList;

public class ActProductionIntroductionFragment extends ContactsListFragment{
    private PerformClassList performList;

    public ActProductionIntroductionFragment(PerformClassList performList) {
        this.performList = performList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_act_production_introduction, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews(){
        TextView actIntro= (TextView) findViewById(R.id.act_introduction);
        TextView actDefaultThumbnail= (TextView) findViewById(R.id.act_default_thumbnail);
        if (actIntro!=null){
            if (performList != null) {
                //根据条件判断 如果Intro为空 显示默认的暂无内容图片
                if (TextUtils.isEmpty(performList.getIntro())){
                    actDefaultThumbnail.setVisibility(View.VISIBLE);
                    actIntro.setVisibility(View.GONE);
                }else {
                    actIntro.setText(performList.getIntro());
                    actDefaultThumbnail.setVisibility(View.GONE);
                    actIntro.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
