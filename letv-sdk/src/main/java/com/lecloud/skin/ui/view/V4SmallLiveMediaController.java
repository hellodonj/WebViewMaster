package com.lecloud.skin.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import com.example.playerlibrelease.R;
import com.lecloud.skin.ui.base.BaseChgScreenBtn;
import com.lecloud.skin.ui.base.BasePlayBtn;
import com.lecloud.skin.ui.base.IBaseLiveSeekBar;

import java.util.List;

public class V4SmallLiveMediaController extends V4LargeLiveMediaController {
    public V4SmallLiveMediaController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public V4SmallLiveMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public V4SmallLiveMediaController(Context context) {
        super(context);
    }
	
	@Override
	protected void onInitView() {
		mBasePlayBtn = (BasePlayBtn) findViewById(R.id.vnew_play_btn);
		mBaseChgScreenBtn = (BaseChgScreenBtn) findViewById(R.id.vnew_chg_btn);
		mBaseLiveSeekBar = (IBaseLiveSeekBar) findViewById(R.id.vnew_seekbar);
	}
	
	@Override
	public void setRateTypeItems(List<String> ratetypes,String definition) {
		
	}
}
