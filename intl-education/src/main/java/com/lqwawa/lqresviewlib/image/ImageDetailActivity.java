package com.lqwawa.lqresviewlib.image;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.osastudio.apps.BaseActivity;
import org.xutils.image.ImageOptions;
import org.xutils.x;

/**
 * 图片点击放大预览
 */
public class ImageDetailActivity extends BaseActivity {
    private ZoomImageView image;
    private ImageView backBt;
    private TextView titleTv;
    private ImageOptions imageOptions;
    private ImageView saveBtn;


    public static void showStatic(Activity activity, String imgUrl, String title) {
        activity.startActivity(new Intent(activity, ImageDetailActivity.class)
                .putExtra("image", imgUrl)
                .putExtra("title", title));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);

        image = (ZoomImageView) findViewById(R.id.image);
        image.setPicZoomHeightWidth(4, 0.2f);
        backBt = (ImageView) findViewById(R.id.back_bt);
        titleTv = (TextView) findViewById(R.id.title_tv);
        saveBtn = (ImageView) findViewById(R.id.save_btn);
        saveBtn.setVisibility(View.GONE);
        backBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //}

        titleTv.setText(getIntent().getStringExtra("title"));

        imageOptions = new ImageOptions.Builder()
                .setImageScaleType(ScaleType.FIT_CENTER)
                .setCrop(false)
                .build();

        x.image().bind(image, getIntent().getStringExtra("image"), imageOptions);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


}
