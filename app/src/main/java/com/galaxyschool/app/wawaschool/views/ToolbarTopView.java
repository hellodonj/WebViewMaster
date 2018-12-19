package com.galaxyschool.app.wawaschool.views;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.R;

public class ToolbarTopView extends RelativeLayout {

    private ViewGroup layout;
    private Context context;


    public ToolbarTopView(Context context) {
        super(context);
    }

    public ToolbarTopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        LayoutInflater inflater = LayoutInflater.from(context);
        layout = (ViewGroup) inflater.inflate(R.layout.toolbar_top, this, true);
    }

    public ImageView getBackView() {
        return (ImageView) layout.findViewById(R.id.toolbar_top_back_btn);
    }

    public TextView getTitleView() {
        return (TextView) layout.findViewById(R.id.toolbar_top_title);
    }

    public TextView getCommitView() {
        return (TextView) layout.findViewById(R.id.toolbar_top_commit_btn);
    }

}
