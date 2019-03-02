package com.icedcap.dubbing.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.icedcap.dubbing.R;
import com.icedcap.dubbing.listener.OnAudioEventListener;

import java.text.DecimalFormat;

import me.zhouzhuo.zzhorizontalprogressbar.ZzHorizontalProgressBar;


public class DubbingItemView extends LinearLayout {

    private RelativeLayout rootLayout;
    private TextView indexTextView;
    private TextView scoreTextView;
    private TextView contentTextView;
    private ZzHorizontalProgressBar zzHorizontalProgressBar;
    private TextView timeTextView;
    private ImageView playBtn;
    private ImageView recordBtn;

    private int max;
    private DecimalFormat decimalFormat;
    private OnAudioEventListener listener;

    public void setOnAudioEventListener(OnAudioEventListener listener) {
        this.listener = listener;
    }

    public DubbingItemView(final Context context) {
        this(context, null);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_dubbing_item, this, true);
        rootLayout = (RelativeLayout) findViewById(R.id.ll_root_layout);
        zzHorizontalProgressBar = (ZzHorizontalProgressBar) findViewById(R.id.hpb_progress_bar);
        contentTextView = (TextView) findViewById(R.id.tv_content);
        indexTextView = (TextView) findViewById(R.id.tv_index);
        playBtn = (ImageView) findViewById(R.id.iv_play);
        recordBtn = (ImageView) findViewById(R.id.iv_record);
        timeTextView = (TextView) findViewById(R.id.tv_time);
        scoreTextView = (TextView) findViewById(R.id.tv_score);

        playBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onAudioPlay();
                }
            }
        });
        recordBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onAudioRecord();
                }
            }
        });
        // 格式化小数
        decimalFormat = new DecimalFormat("0.0");
    }

    public DubbingItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public DubbingItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public void setBackgroundHighLight(boolean isHighLight) {
        rootLayout.getBackground().setLevel(!isHighLight ? 0 : 1);
    }

    public void setIndex(int current, int total) {
        indexTextView.setText(String.format("%s/%s", String.valueOf(current + 1), String.valueOf(total)));
    }

    public void setContent(String content) {
        contentTextView.setText(content);
    }

    public void setProgress(int current) {
        zzHorizontalProgressBar.setProgress(current);
        if (current == max) {
            playBtn.setVisibility(VISIBLE);
        }
    }

    public void setScore(int score) {
        scoreTextView.setVisibility(score >= 0 ? VISIBLE : INVISIBLE);
        scoreTextView.setText(String.valueOf(score));
    }

    public void setProgressMax(int max) {
        this.max = max;
        zzHorizontalProgressBar.setMax(max);
    }

    public void setTime(long time) {
        timeTextView.setText(String.format("%s%s", decimalFormat.format(time / 1000.0), "s"));
    }

    public void clear(int max) {
        this.max = max;
        zzHorizontalProgressBar.setMax(max);
        zzHorizontalProgressBar.setProgress(max);
        playBtn.setVisibility(VISIBLE);
    }

    public ImageView getPlayBtn() {
        return playBtn;
    }

    public ImageView getRecordBtn() {
        return recordBtn;
    }
}
