package com.galaxyschool.app.wawaschool.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.galaxyschool.app.wawaschool.R;

/**
 * Created by KnIghT on 16-5-12.
 */
public class HalfRoundedImageView extends ImageView {
    /*圆角的半径，依次为左上角xy半径，右上角，右下角，左下角*/

    private float[] rids = new float[8];


    public HalfRoundedImageView(Context context) {
        super(context);
    }

    public HalfRoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HalfRoundedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initDids() {
        float rid = getResources().getDimensionPixelSize(R.dimen.resource_gridview_padding);
        for (int i = 0; i < rids.length; i++) {
            if (i < 4) {
                rids[i] = rid;
            } else {
                rids[i] = 0.0f;
            }
        }
    }

    /**
     * 画图	 * by Hankkin at:2015-08-30 21:15:53
     *
     * @param canvas
     */
    protected void onDraw(Canvas canvas) {
        Path path = new Path();
        int w = this.getWidth();
        int h = this.getHeight();
        /*向路径中添加圆角矩形。radii数组定义圆角矩形的四个圆角的x,y半径。radii长度必须为8*/
        initDids();
        path.addRoundRect(new RectF(0, 0, w, h), rids, Path.Direction.CW);
        canvas.clipPath(path);
        super.onDraw(canvas);
    }
}


