package com.galaxyschool.app.wawaschool.common;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lqwawa.intleducation.common.utils.UIUtil;

import java.util.HashMap;

public class RecyclerViewSpacesItemDecoration extends RecyclerView.ItemDecoration {

    private HashMap<String, Integer> mSpaceValueMap;
    public static final String TOP_DECORATION = "top_decoration";
    public static final String BOTTOM_DECORATION = "bottom_decoration";
    public static final String LEFT_DECORATION = "left_decoration";
    public static final String RIGHT_DECORATION = "right_decoration";
    private int top;
    private int bottom;
    private int left;
    private int right;

    public RecyclerViewSpacesItemDecoration(HashMap<String, Integer> mSpaceValueMap) {
        this.mSpaceValueMap = mSpaceValueMap;
    }

    public RecyclerViewSpacesItemDecoration(int top, int bottom, int left, int right) {
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
        mSpaceValueMap = new HashMap<>();
        mSpaceValueMap.put(RecyclerViewSpacesItemDecoration.TOP_DECORATION,
                DensityUtils.dp2px(UIUtil.getContext(),top));//top间距
        mSpaceValueMap.put(RecyclerViewSpacesItemDecoration.BOTTOM_DECORATION,
                DensityUtils.dp2px(UIUtil.getContext(),bottom));//底部间距
        mSpaceValueMap.put(RecyclerViewSpacesItemDecoration.LEFT_DECORATION,
                DensityUtils.dp2px(UIUtil.getContext(),left));//左间距
        mSpaceValueMap.put(RecyclerViewSpacesItemDecoration.RIGHT_DECORATION,
                DensityUtils.dp2px(UIUtil.getContext(),right));//右间距
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        if (mSpaceValueMap.get(TOP_DECORATION) != null)
            outRect.top = mSpaceValueMap.get(TOP_DECORATION);
        if (mSpaceValueMap.get(LEFT_DECORATION) != null)
            outRect.left = mSpaceValueMap.get(LEFT_DECORATION);
        if (mSpaceValueMap.get(RIGHT_DECORATION) != null)
            outRect.right = mSpaceValueMap.get(RIGHT_DECORATION);
        if (mSpaceValueMap.get(BOTTOM_DECORATION) != null)
            outRect.bottom = mSpaceValueMap.get(BOTTOM_DECORATION);
    }

}
