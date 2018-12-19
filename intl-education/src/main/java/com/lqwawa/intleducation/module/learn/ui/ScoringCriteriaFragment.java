package com.lqwawa.intleducation.module.learn.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseFragment;

/**
 * Created by XChen on 2016/11/29.
 * email:man0fchina@foxmail.com
 * 评分标准
 */

public class ScoringCriteriaFragment extends MyBaseFragment {
    private TextView textViewContent;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.com_multi_line_text, container, false);

        textViewContent  = (TextView)view.findViewById(R.id.content_tv);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void updateContent(String content) {
        textViewContent.setText(content);
    }

}
