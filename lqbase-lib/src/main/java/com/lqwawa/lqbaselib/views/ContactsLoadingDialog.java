package com.lqwawa.lqbaselib.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.lqresviewlib.R;


public class ContactsLoadingDialog extends Dialog {

    private Context context;
    private Animation animation;
    private String loadContent;

    public ContactsLoadingDialog(Context context) {
        super(context, R.style.Theme_ContactsLoadingDialog);
        this.context = context;
    }

    private ContactsLoadingDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    public void setContent(String content) {
        this.loadContent = content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_dialog_loading);

        animation = AnimationUtils.loadAnimation(context, R.anim.image_rotate);
        animation.setInterpolator(new LinearInterpolator());

        ImageView imageViewLoading = (ImageView) findViewById(R.id.loading_progress);
        imageViewLoading.startAnimation(animation);

        TextView loadTipTextV = (TextView) findViewById(R.id.loading_tips);
        if (loadTipTextV != null){
            if (!TextUtils.isEmpty(loadContent)){
                loadTipTextV.setText(loadContent);
            }
        }
    }

}
