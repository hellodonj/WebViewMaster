package com.lqwawa.lqresviewlib.weike;
import android.os.Bundle;
import android.view.View;

import com.oosic.apps.iemaker.base.PlaybackActivity;

public class SimplePlayBackActivity extends PlaybackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCollectBtn.setVisibility(View.GONE);
        mPraiseBtn.setVisibility(View.GONE);
        mShareBtn.setVisibility(View.GONE);
    }
}
