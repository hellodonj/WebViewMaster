package com.galaxyschool.app.wawaschool.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;

public class ToolbarBottomView extends LinearLayout {

	private static final int TABS_COUNT = 5;
    private ViewGroup layout;
    private Context context;

    private BottomViewClickListener listener;
    private View[] itemViews = new View[TABS_COUNT];
    private TextView[] tipsViews = new TextView[TABS_COUNT];

    public ToolbarBottomView(Context context) {
        super(context);
    }

    public ToolbarBottomView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;

        this.layout = (ViewGroup) LayoutInflater.from(context).inflate(
                R.layout.toolbar_bottom, this, true);

        initViews();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initViews() {
        ViewGroup rootView = (ViewGroup) layout.findViewById(R.id.toolbar_bottom_layout);
        View itemView = null;
        ImageView imageView = null;
        TextView textView = null;
        for (int i = 0; i < itemViews.length; i++) {
            itemView = rootView.getChildAt(i);
            imageView = (ImageView) itemView.findViewById(R.id.item_icon);
            if (imageView != null) {
                if (i == 2){
                    imageView.setVisibility(INVISIBLE);
                } else {
                    imageView.setImageResource(getItemIcon(i));
                }
            }
            textView = (TextView) itemView.findViewById(R.id.item_title);
            if (textView != null) {
                textView.setText(getItemText(i));
            }
            itemView.setTag(i);
            itemView.setOnClickListener(itemViewClickListener);
            tipsViews[i] = (TextView) itemView.findViewById(R.id.item_tips);
            itemViews[i] = itemView;
        }
    }

    private int getItemIcon(int i) {
        switch (i) {
            case 0:
                return R.drawable.sel_tab_lqcourse;
            case 1:
                return R.drawable.sel_tab_international_study;
            case 2:
                return R.drawable.sel_tab_institution;
            case 3:
                return R.drawable.sel_tab_institution;
            case 4:
                return R.drawable.sel_tab_user;
        }
        return 0;
    }

    private int getItemText(int i) {
        switch (i) {
            case 0:
                return R.string.str_tutoring_center;
            case 1:
                return R.string.str_lecture_hall;
            case 2:
                return R.string.my_course;
            case 3:
                return R.string.school_space;
            case 4:
                return R.string.personal_space;
        }
        return 0;
    }

    private OnClickListener itemViewClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Integer index = (Integer)v.getTag();
            if (listener != null) {
                listener.onBottomViewClick(index);
            }
        }
    };

    public void setSelectItemView(int index) {
        View itemView = null;
        ImageView imageView = null;
        TextView textView = null;
        for (int i = 0; i < itemViews.length; i++) {
            itemView = itemViews[i];
            itemView.setSelected(false);
            imageView = (ImageView) itemView.findViewById(R.id.item_icon);
            if (imageView != null) {
                imageView.setSelected(false);
            }
            textView = (TextView) itemView.findViewById(R.id.item_title);
            if (textView != null) {
                textView.setSelected(false);
            }
        }
        if (index >= 0 && index < itemViews.length) {
            itemView = itemViews[index];
            itemViews[index].setSelected(true);
            imageView = (ImageView) itemView.findViewById(R.id.item_icon);
            if (imageView != null) {
                imageView.setSelected(true);
            }
            textView = (TextView) itemView.findViewById(R.id.item_title);
            if (textView != null) {
                textView.setSelected(true);
            }
        }
    }

    public void showBadgeView(int index, int count) {
    	if (count > 0) {
    		tipsViews[index].setVisibility(View.VISIBLE);
//    		String newCountStr = count > AppSettings.MAX_COUNT ? "99+" : String.valueOf(count);
//    		tipsViews[index].setText(newCountStr);
		} else {
			tipsViews[index].setVisibility(View.GONE);
		}
    }

    public void hideBadgeView(int index) {
    	tipsViews[index].setVisibility(View.GONE);
    }

    public void setBottomViewClickListener(BottomViewClickListener listener) {
        this.listener = listener;
    }

    public interface BottomViewClickListener {
        void onBottomViewClick(int index);
    }

}
