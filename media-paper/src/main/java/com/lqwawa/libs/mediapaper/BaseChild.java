package com.lqwawa.libs.mediapaper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by pp on 15/12/15.
 */
public abstract class BaseChild extends LinearLayout {
    protected boolean mbDelMode = false;
    protected DeleteHandler mDeleteHandler = null;

    public BaseChild(Context context) {
        this(context, null);
    }

    public BaseChild(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseChild(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public abstract void setDeleteMode(boolean bDelMode);

    public void setDeleteHandler(View deleteBtn, DeleteHandler handler) {
        mDeleteHandler = handler;
        if (deleteBtn != null) {
            deleteBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDeleteHandler != null) {
                        mDeleteHandler.delete(BaseChild.this);
                    }
                }
            });
        }

    }

    public interface DeleteHandler {
        public void delete(BaseChild view);
    }
}
