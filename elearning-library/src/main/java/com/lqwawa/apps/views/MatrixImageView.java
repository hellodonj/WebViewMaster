package com.lqwawa.apps.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * @ClassName: MatrixImageView
 * @Description: 带放大、缩小、移动效果的ImageView
 */
public class MatrixImageView extends ImageView {

    public final static String TAG = MatrixImageView.class.getSimpleName();

    private GestureDetector mGestureDetector;
    /**
     * 模板Matrix，用以初始化
     */
    private Matrix mMatrix = new Matrix();
    /**
     * 图片长度
     */
    private float mImageWidth;
    /**
     * 图片高度
     */
    private float mImageHeight;
    /**
     * 原始缩放级别
     */
    private float mScale;

    private MatrixDragListener mDragListener;
    private MatrixSingleTapListener mSingleTapListener;

    public MatrixImageView(Context context) {
        this(context, null);
    }

    public MatrixImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        MatrixTouchListener listener = new MatrixTouchListener();
        setOnTouchListener(listener);
        mGestureDetector = new GestureDetector(getContext(), new MatrixGestureListener(listener));
        //背景设置为BLACK
        setBackgroundColor(Color.BLACK);
        //将缩放类型设置为CENTER_INSIDE，表示把图片居中显示，并且宽高最大值为控件宽高
        setScaleType(ScaleType.FIT_CENTER);
    }

    public void setDragListener(MatrixDragListener listener) {
        mDragListener = listener;
    }

    public void setSingleTapListener(MatrixSingleTapListener listener) {
        mSingleTapListener = listener;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);

        //大小为0，表示当前控件大小未测量，设置监听函数，在绘制前赋值
        if (getWidth() == 0) {
            ViewTreeObserver vto = getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    initData();
                    //赋值结束后，移除该监听函数
                    MatrixImageView.this.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
        } else {
            initData();
        }
    }

    /**
     * 初始化模板Matrix和图片的其他数据
     */
    private void initData() {
        //设置完图片后，获取该图片的坐标变换矩阵
        mMatrix.set(getImageMatrix());
        float[] values = new float[9];
        mMatrix.getValues(values);
        //图片宽度为屏幕宽度除缩放倍数
        mImageWidth = getWidth() / values[Matrix.MSCALE_X];
        mImageHeight = (getHeight() - values[Matrix.MTRANS_Y] * 2) / values[Matrix.MSCALE_Y];
        mScale = values[Matrix.MSCALE_X];
    }

    public class MatrixTouchListener implements OnTouchListener {
        /**
         * 拖拉照片模式
         */
        private static final int MODE_DRAG = 1;
        /**
         * 放大缩小照片模式
         */
        private static final int MODE_ZOOM = 2;
        /**
         * 不支持Matrix
         */
        private static final int MODE_DISABLE = 3;
        /**
         * 最大缩放级别
         */
        float mMaxScale = 6;
        /**
         * 双击时的缩放级别
         */
        float mDoubleClickScale = 2;

        private int mMode = 0;

        /**
         * 缩放开始时的手指间距
         */
        private float mStartDistance;
        /**
         * 当前Matrix
         */
        private Matrix mCurrentMatrix = new Matrix();

        /**
         * 和ViewPager交互相关，判断当前是否可以左移、右移
         */
        boolean mLeftDraggable;
        boolean mRightDraggable;
        /**
         * 是否第一次移动
         */
        boolean mFirstMove = false;
        /**
         * 用于记录开始时候的坐标位置
         */
        private PointF mStartPoint = new PointF();

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                //设置拖动模式
                mMode = MODE_DRAG;
                mStartPoint.set(event.getX(), event.getY());
                checkMatrixSupported();
                startDrag();
                checkDraggable();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                resetMatrix();
                stopDrag();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mMode == MODE_ZOOM) {
                    setZoomMatrix(event);
                } else if (mMode == MODE_DRAG) {
                    setDragMatrix(event);
                } else {
                    stopDrag();
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (mMode == MODE_DISABLE) {
                    return true;
                }
                mMode = MODE_ZOOM;
                mStartDistance = distance(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            default:
                break;
            }
            return mGestureDetector.onTouchEvent(event);
        }

        /**
         * 子控件开始进入移动状态，令ViewPager无法拦截对子控件的Touch事件
         */
        private void startDrag() {
            if (mDragListener != null) {
                mDragListener.startDrag();
            }
        }

        /**
         * 子控件开始停止移动状态，ViewPager将拦截对子控件的Touch事件
         */
        private void stopDrag() {
            if (mDragListener != null) {
                mDragListener.stopDrag();
            }
        }

        /**
         * 根据当前图片左右边缘设置可拖拽状态
         */
        private void checkDraggable() {
            mLeftDraggable = true;
            mRightDraggable = true;
            mFirstMove = true;
            float[] values = new float[9];
            getImageMatrix().getValues(values);
            //图片左边缘离开左边界，表示不可右移
            if (values[Matrix.MTRANS_X] >= 0) {
                mRightDraggable = false;
            }
            //图片右边缘离开右边界，表示不可左移
            if ((mImageWidth) * values[Matrix.MSCALE_X] + values[Matrix.MTRANS_X] <= getWidth()) {
                mLeftDraggable = false;
            }
        }

        /**
         * 设置拖拽状态下的Matrix
         *
         * @param event
         */
        public void setDragMatrix(MotionEvent event) {
            if (isZoomChanged()) {
                float dx = event.getX() - mStartPoint.x; // 得到x轴的移动距离
                float dy = event.getY() - mStartPoint.y; // 得到x轴的移动距离
                //避免和双击冲突,大于10f才算是拖动
                if (Math.sqrt(dx * dx + dy * dy) > 10f) {
                    mStartPoint.set(event.getX(), event.getY());
                    //在当前基础上移动
                    mCurrentMatrix.set(getImageMatrix());
                    float[] values = new float[9];
                    mCurrentMatrix.getValues(values);
                    dy = checkDyBound(values, dy);
                    dx = checkDxBound(values, dx, dy);

                    mCurrentMatrix.postTranslate(dx, dy);
                    setImageMatrix(mCurrentMatrix);
                }
            } else {
                stopDrag();
            }
        }

        /**
         * 判断缩放级别是否是改变过
         *
         * @return true表示非初始值, false表示初始值
         */
        private boolean isZoomChanged() {
            float[] values = new float[9];
            getImageMatrix().getValues(values);
            //获取当前X轴缩放级别
            float scale = values[Matrix.MSCALE_X];
            //获取模板的X轴缩放级别，两者做比较
            return scale != mScale;
        }

        /**
         * 和当前矩阵对比，检验dy，使图像移动后不会超出ImageView边界
         *
         * @param values
         * @param dy
         * @return
         */
        private float checkDyBound(float[] values, float dy) {
            float height = getHeight();
            if (mImageHeight * values[Matrix.MSCALE_Y] < height) {
                return 0;
            }
            if (values[Matrix.MTRANS_Y] + dy > 0) {
                dy = -values[Matrix.MTRANS_Y];
            } else if (values[Matrix.MTRANS_Y] + dy < -(mImageHeight * values[Matrix.MSCALE_Y] - height)) {
                dy = -(mImageHeight * values[Matrix.MSCALE_Y] - height) - values[Matrix.MTRANS_Y];
            }
            return dy;
        }

        /**
         * 和当前矩阵对比，检验dx，使图像移动后不会超出ImageView边界
         *
         * @param values
         * @param dx
         * @return
         */
        private float checkDxBound(float[] values, float dx, float dy) {
            float width = getWidth();
            if (!mLeftDraggable && dx < 0) {
                //加入和y轴的对比，表示在监听到垂直方向的手势时不切换Item
                if (Math.abs(dx) * 0.4f > Math.abs(dy) && mFirstMove) {
                    stopDrag();
                }
                return 0;
            }
            if (!mRightDraggable && dx > 0) {
                //加入和y轴的对比，表示在监听到垂直方向的手势时不切换Item
                if (Math.abs(dx) * 0.4f > Math.abs(dy) && mFirstMove) {
                    stopDrag();
                }
                return 0;
            }
            mLeftDraggable = true;
            mRightDraggable = true;
            if (mFirstMove) mFirstMove = false;
            if (mImageWidth * values[Matrix.MSCALE_X] < width) {
                return 0;

            }
            if (values[Matrix.MTRANS_X] + dx > 0) {
                dx = -values[Matrix.MTRANS_X];
            } else if (values[Matrix.MTRANS_X] + dx < -(mImageWidth * values[Matrix.MSCALE_X] - width)) {
                dx = -(mImageWidth * values[Matrix.MSCALE_X] - width) - values[Matrix.MTRANS_X];
            }
            return dx;
        }

        /**
         * 设置缩放Matrix
         *
         * @param event
         */
        private void setZoomMatrix(MotionEvent event) {
            //只有同时触屏两个点的时候才执行
            if (event.getPointerCount() < 2) {
                return;
            }
            float endDis = distance(event);// 结束距离
            if (endDis > 10f) { // 两个手指并拢在一起的时候像素大于10
                float scale = endDis / mStartDistance;// 得到缩放倍数
                mStartDistance = endDis;//重置距离
                mCurrentMatrix.set(getImageMatrix());//初始化Matrix
                float[] values = new float[9];
                mCurrentMatrix.getValues(values);
                scale = checkMaxScale(scale, values);
                PointF centerF = getCenter(scale, values);
                mCurrentMatrix.postScale(scale, scale, centerF.x, centerF.y);
                setImageMatrix(mCurrentMatrix);
            }
        }

        /**
         * 获取缩放的中心点。
         *
         * @param scale
         * @param values
         * @return
         */
        private PointF getCenter(float scale, float[] values) {
            //缩放级别小于原始缩放级别时或者为放大状态时，返回ImageView中心点作为缩放中心点
            if (scale * values[Matrix.MSCALE_X] < mScale || scale >= 1) {
                return new PointF(getWidth() / 2, getHeight() / 2);
            }
            float cx = getWidth() / 2;
            float cy = getHeight() / 2;
            //以ImageView中心点为缩放中心，判断缩放后的图片左边缘是否会离开ImageView左边缘，是的话以左边缘为X轴中心
            if ((getWidth() / 2 - values[Matrix.MTRANS_X]) * scale < getWidth() / 2) {
                cx = 0;
            }
            //判断缩放后的右边缘是否会离开ImageView右边缘，是的话以右边缘为X轴中心
            if ((mImageWidth * values[Matrix.MSCALE_X] + values[Matrix.MTRANS_X]) * scale < getWidth()) {
                cx = getWidth();
            }
            return new PointF(cx, cy);
        }

        /**
         * 检验scale，使图像缩放后不会超出最大倍数
         *
         * @param scale
         * @param values
         * @return
         */
        private float checkMaxScale(float scale, float[] values) {
            if (scale * values[Matrix.MSCALE_X] > mMaxScale) {
                scale = mMaxScale / values[Matrix.MSCALE_X];
            }
            return scale;
        }

        /**
         * 重置Matrix
         */
        private void resetMatrix() {
            if (isMinScaleReached()) {
                mCurrentMatrix.set(mMatrix);
                setImageMatrix(mCurrentMatrix);
            } else {
                //判断Y轴是否需要更正
                float[] values = new float[9];
                getImageMatrix().getValues(values);
                float height = mImageHeight * values[Matrix.MSCALE_Y];
                if (height < getHeight()) {
                    //在图片真实高度小于容器高度时，Y轴居中，Y轴理想偏移量为两者高度差/2，
                    float topMargin = (getHeight() - height) / 2;
                    if (topMargin != values[Matrix.MTRANS_Y]) {
                        mCurrentMatrix.set(getImageMatrix());
                        mCurrentMatrix.postTranslate(0, topMargin - values[Matrix.MTRANS_Y]);
                        setImageMatrix(mCurrentMatrix);
                    }
                }
            }
        }

        /**
         * 判断是否需要重置
         *
         * @return 当前缩放级别小于模板缩放级别时，重置
         */
        private boolean isMinScaleReached() {
            float[] values = new float[9];
            getImageMatrix().getValues(values);
            //获取当前X轴缩放级别
            float scale = values[Matrix.MSCALE_X];
            //获取模板的X轴缩放级别，两者做比较
            return scale < mScale;
        }

        /**
         * 判断是否支持Matrix
         */
        private void checkMatrixSupported() {
            //当加载出错时，不可缩放
            if (getScaleType() != ScaleType.CENTER) {
                setScaleType(ScaleType.MATRIX);
            } else {
                mMode = MODE_DISABLE; //设置为不支持手势
            }
        }

        /**
         * 计算两个手指间的距离
         *
         * @param event
         * @return
         */
        private float distance(MotionEvent event) {
            float dx = event.getX(1) - event.getX(0);
            float dy = event.getY(1) - event.getY(0);
            /** 使用勾股定理返回两点之间的距离 */
            return (float) Math.sqrt(dx * dx + dy * dy);
        }

        /**
         * 双击时触发
         */
        public void onDoubleClick() {
            float scale = isZoomChanged() ? 1 : mDoubleClickScale;
            mCurrentMatrix.set(mMatrix); //初始化Matrix
            mCurrentMatrix.postScale(scale, scale, getWidth() / 2, getHeight() / 2);
            setImageMatrix(mCurrentMatrix);
        }
    }


    private class MatrixGestureListener extends SimpleOnGestureListener {
        private final MatrixTouchListener mTouchListener;

        public MatrixGestureListener(MatrixTouchListener listener) {
            mTouchListener = listener;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            //捕获Down事件
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            //触发双击事件
            mTouchListener.onDoubleClick();
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return super.onSingleTapUp(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public void onShowPress(MotionEvent e) {
            super.onShowPress(e);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return super.onDoubleTapEvent(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (mSingleTapListener != null) {
                mSingleTapListener.onSingleTap();
            }
            return super.onSingleTapConfirmed(e);
        }

    }

    /**
     * @ClassName: MatrixDragListener
     * @Description: MatrixImageView移动监听接口, 用以组织ViewPager对Drag操作的拦截
     */
    public interface MatrixDragListener {
        public void startDrag();

        public void stopDrag();
    }

    /**
     * @ClassName: MatrixSingleTapListener
     * @Description: 监听ViewPager屏幕单击事件，本质是监听子控件MatrixImageView的单击事件
     */
    public interface MatrixSingleTapListener {
        public void onSingleTap();
    }

}
