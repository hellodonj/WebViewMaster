package com.lqwawa.intleducation.module.learn.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseActivity;
import com.lqwawa.intleducation.base.utils.DateUtils;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.module.learn.vo.NoticeVo;

public class NoticesDetailsActivity extends MyBaseActivity implements View.OnClickListener{

    private TopBar topBar;

    TextView textViewNoticeTitle;
    TextView textViewNoticeData;
    TextView textViewNoticeContent;

    NoticeVo noticeVo;

    public static void start(Activity activity, NoticeVo vo){
        activity.startActivity(new Intent(activity, NoticesDetailsActivity.class)
        .putExtra("notice", vo));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notices_details);
        topBar = (TopBar)findViewById(R.id.top_bar);
        textViewNoticeTitle  = (TextView)findViewById(R.id.notice_title_tv);
        textViewNoticeData  = (TextView)findViewById(R.id.notice_date_tv);
        textViewNoticeContent  = (TextView)findViewById(R.id.notice_content_tv);
        initViews();
    }
    private void initViews(){
        topBar.setTitle(getResources().getString(R.string.notice_details));
        topBar.setBack(true);
        noticeVo = (NoticeVo)getIntent().getSerializableExtra("notice");
        if (noticeVo != null){
            textViewNoticeTitle.setText(noticeVo.getTitle() + "");
            textViewNoticeData.setText(DateUtils.getFormatByStringDate(noticeVo.getCreateTime(),
                    DateUtils.YYYYMMDDHHMM));
            String contentShow = "" + noticeVo.getContent();
            contentShow = contentShow.replace("\u3000\u3000", "");
            contentShow = contentShow.replace(" ", "");
            contentShow = contentShow.replace("\n", "\n\u3000\u3000");
            textViewNoticeContent.setText("\u3000\u3000" + contentShow);
        }
    }

    @Override
    public void onClick(View view) {

    }
}
