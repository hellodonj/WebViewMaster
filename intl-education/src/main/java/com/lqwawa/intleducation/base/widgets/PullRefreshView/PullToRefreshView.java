package com.lqwawa.intleducation.base.widgets.PullRefreshView;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.common.utils.UIUtil;

import java.util.Date;

public class PullToRefreshView extends LinearLayout {
	// private static final String TAG = "PullToRefreshView";
	// refresh states
	private static final int PULL_TO_REFRESH = 2;
	private static final int RELEASE_TO_REFRESH = 3;
	private static final int REFRESHING = 4;
	// pull state
	public static final int PULL_UP_STATE = 0;
	public static final int PULL_DOWN_STATE = 1;
	/**
	 * last y
	 */
	private int mLastMotionY;
	/**
	 * lock
	 */
	// private boolean mLock;
	/**
	 * header view
	 */
	private View mHeaderView;
	/**
	 * footer view
	 */
	private View mFooterView;
	/**
	 * list or grid
	 */
	private AdapterView<?> mAdapterView;
	/**
	 * scrollview
	 */
	private ScrollView mScrollView;
	/**
	 * header view height
	 */
	private int mHeaderViewHeight;
	/**
	 * footer view height
	 */
	private int mFooterViewHeight;
	/**
	 * header view image
	 */
	private ImageView mRefreshimage_customview_iv;
	/**
	 * footer view image
	 */
	private ImageView mLoadimage_customview_iv;
	/**
	 * header tip text
	 */
	private TextView mRefreshtext_customview_tv;
	/**
	 * footer tip text
	 */
	private TextView mLoadtext_customview_tv;
	/**
	 * header refresh time
	 */
	private TextView mRefreshupdatedtime_customview_tv;
	/**
	 * footer refresh time
	 */
	// private TextView mFooterUpdateTextView;
	/**
	 * header progress bar
	 */
	private ProgressBar mRefreshprogress_customview_pbar;
	/**
	 * footer progress bar
	 */
	private ProgressBar mLoadprogress_customview_pbar;
	/**
	 * layout inflater
	 */
	private LayoutInflater mInflater;
	/**
	 * header view current state
	 */
	private int mHeaderState;
	/**
	 * footer view current state
	 */
	private int mFooterState;
	/**
	 * pull state,pull up or pull down;PULL_UP_STATE or PULL_DOWN_STATE
	 */
	private int mPullState;
	/**
	 * 变为向下的箭头,改变箭头方向
	 */
	private RotateAnimation mFlipAnimation;
	/**
	 * 变为逆向的箭头,旋转
	 */
	private RotateAnimation mReverseFlipAnimation;
	/**
	 * footer refresh listener
	 */
	private OnFooterRefreshListener mOnFooterRefreshListener;
	/**
	 * footer refresh listener
	 */
	private OnHeaderRefreshListener mOnHeaderRefreshListener;
	
	private boolean mEnableRefresh = true;

	private boolean mEnableLoadMore = true;

	/**
	 * last update time
	 */
	// private String mLastUpdateTime;

	public PullToRefreshView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PullToRefreshView(Context context) {
		super(context);
		init();
	}

	/**
	 * init
	 * 
	 */
	private void init() {
		// 需要设置成vertical
		setOrientation(LinearLayout.VERTICAL);
		// Load all of the animations we need in code rather than through XML
		mFlipAnimation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mFlipAnimation.setInterpolator(new LinearInterpolator());
		mFlipAnimation.setDuration(250);
		mFlipAnimation.setFillAfter(true);
		mReverseFlipAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
		mReverseFlipAnimation.setDuration(250);
		mReverseFlipAnimation.setFillAfter(true);

		mInflater = LayoutInflater.from(getContext());
		// header view 在此添加,保证是第一个添加到linearlayout的最上端
		addHeaderView();
	}

	private void addHeaderView() {
		// header view
		mHeaderView = mInflater.inflate(
         R.layout.include_custom_refreshheader,
				this, false);

		mRefreshimage_customview_iv = (ImageView) mHeaderView
				.findViewById(R.id.id_refreshimage_customview_iv);
		mRefreshtext_customview_tv = (TextView) mHeaderView
				.findViewById(R.id.id_refreshtext_customview_tv);
		mRefreshupdatedtime_customview_tv = (TextView) mHeaderView
				.findViewById(R.id.id_refreshupdatedtime_customview_tv);
		mRefreshprogress_customview_pbar = (ProgressBar) mHeaderView
				.findViewById(R.id.id_refreshprogress_customview_pbar);
		// header layout
		measureView(mHeaderView);
		mHeaderViewHeight = mHeaderView.getMeasuredHeight();
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				mHeaderViewHeight);
		// 设置topMargin的值为负的header View高度,即将其隐藏在最上方
		params.topMargin = -(mHeaderViewHeight);
		// mHeaderView.setLayoutParams(params1);
		addView(mHeaderView, params);

	}

	private void addFooterView() {
		// footer view
		mFooterView = mInflater.inflate(R.layout.include_custom_refreshfooter,
				this, false);
		mLoadimage_customview_iv = (ImageView) mFooterView
				.findViewById(R.id.id_loadimage_customview_iv);
		mLoadtext_customview_tv = (TextView) mFooterView
				.findViewById(R.id.id_loadtext_customview_tv);
		mLoadprogress_customview_pbar = (ProgressBar) mFooterView
				.findViewById(R.id.id_loadprogress_customview_pbar);
		// footer layout
		measureView(mFooterView);
		mFooterViewHeight = mFooterView.getMeasuredHeight();
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				mFooterViewHeight);
		// int top = getHeight();
		// params.topMargin
		// =getHeight();//在这里getHeight()==0,但在onInterceptTouchEvent()方法里getHeight()已经有值了,不再是0;
		// getHeight()什么时候会赋值,稍候再研究一下
		// 由于是线性布局可以直接添加,只要AdapterView的高度是MATCH_PARENT,那么footer view就会被添加到最后,并隐藏
		addView(mFooterView, params);
	}

	@Override
	protected void onFinishInflate() { // 加载完xml执行此方法
		super.onFinishInflate();
		// footer view 在此添加保证添加到linearlayout中的最后
		addFooterView();
		initContentAdapterView();
	}

	public void refreshContent(){
		mAdapterView = null;
		mScrollView = null;
		initContentAdapterView();
	}

	/**
	 * init AdapterView like ListView,GridView and so on;or init ScrollView
	 * 
	 */
	private void initContentAdapterView() {
		int count = getChildCount();
		if (count < 3) {
			throw new IllegalArgumentException(
					"This layout must contain 3 child views,and AdapterView or ScrollView must in the second position!");
		}
		View view = null;
		for (int i = 0; i < count - 1; ++i) {
			view = getChildAt(i);
			if (view instanceof AdapterView<?>) {
				if(view.getVisibility() == View.VISIBLE)
				mAdapterView = (AdapterView<?>) view;
			}
			if (view instanceof ScrollView) {
				// finish later
				if(view.getVisibility() == View.VISIBLE)
				mScrollView = (ScrollView) view;
			}
		}
		if (mAdapterView == null && mScrollView == null) {
			throw new IllegalArgumentException(
					"must contain a AdapterView or ScrollView in this layout!");
		}
	}

	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {
		int y = (int) e.getRawY();
		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 首先拦截down事件,记录y坐标
			mLastMotionY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			// deltaY > 0 是向下运动,< 0是向上运动
			int deltaY = y - mLastMotionY;
			if (isRefreshViewScroll(deltaY)) {
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			break;
		}
		return false;
	}

	public void setRefreshEnable(boolean value) {
		mEnableRefresh = value;
	}

	public void setLoadMoreEnable(boolean value){
		mEnableLoadMore = value;
	}
	/*
	 * 如果在onInterceptTouchEvent()方法中没有拦截(即onInterceptTouchEvent()方法中 return
	 * false)则由PullToRefreshView 的子View来处理;否则由下面的方法来处理(即由PullToRefreshView自己来处理)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		 if (!mEnableRefresh) {
			 return true;
		 }
		int y = (int) event.getRawY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// onInterceptTouchEvent已经记录
			// mLastMotionY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			int deltaY = y - mLastMotionY;
			// 更新时可以上划 mutang
			// if (mHeaderState != REFRESHING) {
			if (mPullState == PULL_DOWN_STATE) {// 执行下拉
				headerPrepareToRefresh(deltaY);
				// setHeaderPadding(-mHeaderViewHeight);
			} else if (mPullState == PULL_UP_STATE) {// 执行上拉
				if(mEnableLoadMore) {
					footerPrepareToRefresh(deltaY);
				}
			}
			// } else if (mHeaderState == REFRESHING) {
			// onHeaderRefreshComplete();
			// }
			mLastMotionY = y;
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			int topMargin = getHeaderTopMargin();
			if (mPullState == PULL_DOWN_STATE) {
				if (topMargin >= 0) {
					// 开始刷新
					headerRefreshing();
				} else {
					// 还没有执行刷新，重新隐藏
					setHeaderTopMargin(-mHeaderViewHeight);
				}
			} else if (mPullState == PULL_UP_STATE) {
				if (Math.abs(topMargin) >= 100 * getDisplayMetrics(getContext())) {// 让下拉刷新更容易所以给个小点的值
															// 16 是随便给的
					// if (Math.abs(topMargin) >= mHeaderViewHeight
					// + mFooterViewHeight) {
					// 开始执行footer 刷新
					if (mEnableLoadMore) {
						footerRefreshing();
					}
				} else {
					// 还没有执行刷新，重新隐藏
					setHeaderTopMargin(-mHeaderViewHeight);
				}
			}
			break;
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 是否应该到了父View,即PullToRefreshView滑动
	 * 
	 * @param deltaY
	 *            , deltaY > 0 是向下运动,< 0是向上运动
	 * @return
	 */
	private boolean isRefreshViewScroll(int deltaY) {
		if (mHeaderState == REFRESHING || mFooterState == REFRESHING) {
			return false;
		}
		// 下拉更新时可拦截 mutang
		// if (mFooterState == REFRESHING) {
		// return false;
		// }
		// if (mHeaderState == REFRESHING) {
		// return true;
		// }

		if (Math.abs(deltaY) < 10) {
			return false;
		}
		// 对于ListView和GridView
		if (mAdapterView != null) {
			// 子view(ListView or GridView)滑动到最顶端
			if (deltaY > 0) {

				View child = mAdapterView.getChildAt(0);
				if (child == null) {
					// 如果mAdapterView中没有数据,不拦截
					// 默认return false;
					// 没有数据的时候，改为下面也可以进行下拉数据
					mPullState = PULL_DOWN_STATE;
					return true;
				}
				if (mAdapterView.getFirstVisiblePosition() == 0
						&& child.getTop() == 0) {
					mPullState = PULL_DOWN_STATE;
					return true;
				}
				int top = child.getTop();
				int padding = mAdapterView.getPaddingTop();
				// padding dm 5 1.0 8 1.5 17 3.0 11 2

				if (mAdapterView.getFirstVisiblePosition() == 0
						&& Math.abs(top - padding) <= (getDisplayMetrics(getContext()) * 6 - 1)) {
					// 之前网上分享的都是<=8
					// 而这个8其实是根据分辨率480x800的分辨率的手机密度算出来的，但是放在1280x720或者高分辨率中gridview就无法下拉刷新了，
					// 像1280x720的分辨率的密度为2
					// 而gridview的xml布局中的android:verticalSpacing="10dip"是10，密度2x10/2+1即为11
					// （这里大于11都行），（其实我也这里也不知道是怎么算密度，本人也是菜鸟，是位朋友告诉我的）。
					mPullState = PULL_DOWN_STATE;
					return true;
				}

			} else if (deltaY < 0) {
				View lastChild = mAdapterView.getChildAt(mAdapterView
						.getChildCount() - 1);
				if (lastChild == null) {
					// 如果mAdapterView中没有数据,不拦截
					return false;
				}
				// 最后一个子view的Bottom小于父View的高度说明mAdapterView的数据没有填满父view,
				// 等于父View的高度说明mAdapterView已经滑动到最后
				if (lastChild.getBottom() <= getHeight()
						&& mAdapterView.getLastVisiblePosition() == mAdapterView
								.getCount() - 1) {
					mPullState = PULL_UP_STATE;
					return true;
				}
			}
		}
		// 对于ScrollView
		if (mScrollView != null) {
			// 子scroll view滑动到最顶端
			View child = mScrollView.getChildAt(0);
			if (deltaY > 0 && mScrollView.getScrollY() == 0) {
				mPullState = PULL_DOWN_STATE;
				return true;
			} else if (deltaY < 0
					&& child.getMeasuredHeight() <= getHeight()
							+ mScrollView.getScrollY()) {
				mPullState = PULL_UP_STATE;
				return true;
			}
		}
		return false;
	}

	/**
	 * header 准备刷新,手指移动过程,还没有释放
	 * 
	 * @param deltaY
	 *            ,手指滑动的距离
	 */
	private void headerPrepareToRefresh(int deltaY) {
		int newTopMargin = changingHeaderViewTopMargin(deltaY);
		// 当header view的topMargin>=0时，说明已经完全显示出来了,修改header view 的提示状态
		if (newTopMargin >= 0 && mHeaderState != RELEASE_TO_REFRESH) {
			mRefreshtext_customview_tv
					.setText(R.string.pullToRefreshReleaseLabel_custom_str);
			mRefreshupdatedtime_customview_tv.setVisibility(View.VISIBLE);
			mRefreshimage_customview_iv.clearAnimation();
			mRefreshimage_customview_iv.startAnimation(mFlipAnimation);
			mHeaderState = RELEASE_TO_REFRESH;
		} else if (newTopMargin < 0 && newTopMargin > -mHeaderViewHeight) {// 拖动时没有释放
			mRefreshimage_customview_iv.clearAnimation();
			mRefreshimage_customview_iv.startAnimation(mFlipAnimation);
			// mHeaderImageView.
			mRefreshtext_customview_tv
					.setText(R.string.pullToRefreshPullLabel_custom_str);
			mHeaderState = PULL_TO_REFRESH;
		}
	}

	/**
	 * footer 准备刷新,手指移动过程,还没有释放 移动footer view高度同样和移动header view
	 * 高度是一样，都是通过修改header view的topmargin的值来达到
	 * 
	 * @param deltaY
	 *            ,手指滑动的距离
	 */
	private void footerPrepareToRefresh(int deltaY) {
		int newTopMargin = changingHeaderViewTopMargin(deltaY);
		// 如果header view topMargin 的绝对值大于或等于header + footer 的高度
		// 说明footer view 完全显示出来了，修改footer view 的提示状态
		if (Math.abs(newTopMargin) >= (mHeaderViewHeight + mFooterViewHeight)
				&& mFooterState != RELEASE_TO_REFRESH) {
			mLoadtext_customview_tv
					.setText(R.string.pullToRefreshFooterReleaseLabel_custom_str);
			mLoadimage_customview_iv.clearAnimation();
			mLoadimage_customview_iv.startAnimation(mFlipAnimation);
			mFooterState = RELEASE_TO_REFRESH;
		} else if (Math.abs(newTopMargin) < (mHeaderViewHeight + mFooterViewHeight)) {
			mLoadimage_customview_iv.clearAnimation();
			mLoadimage_customview_iv.startAnimation(mFlipAnimation);
			mLoadtext_customview_tv
					.setText(R.string.pullToRefreshFooterPullLabel_custom_str);
			mFooterState = PULL_TO_REFRESH;
		}
	}

	/**
	 * 修改Header view top margin的值
	 * 
	 * @param deltaY
	 */
	private int changingHeaderViewTopMargin(int deltaY) {
		LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
		float newTopMargin = params.topMargin + deltaY * 0.3f;
		// 这里对上拉做一下限制,因为当前上拉后然后不释放手指直接下拉,会把下拉刷新给触发了,感谢网友yufengzungzhe的指出
		// 表示如果是在上拉后一段距离,然后直接下拉
		if (deltaY > 0 && mPullState == PULL_UP_STATE
				&& Math.abs(params.topMargin) <= mHeaderViewHeight) {
			return params.topMargin;
		}
		// 同样地,对下拉做一下限制,避免出现跟上拉操作时一样的bug
		if (deltaY < 0 && mPullState == PULL_DOWN_STATE
				&& Math.abs(params.topMargin) >= mHeaderViewHeight) {
			return params.topMargin;
		}
		params.topMargin = (int) newTopMargin;
		mHeaderView.setLayoutParams(params);
		invalidate();
		return params.topMargin;
	}

	/**
	 * header refreshing
	 * 
	 */
	private void headerRefreshing() {
		mHeaderState = REFRESHING;
		setHeaderTopMargin(0);
		mRefreshimage_customview_iv.setVisibility(View.GONE);
		mRefreshimage_customview_iv.clearAnimation();
		mRefreshimage_customview_iv.setImageDrawable(null);
		mRefreshprogress_customview_pbar.setVisibility(View.VISIBLE);
		mRefreshtext_customview_tv
				.setText(R.string.pullToRefreshRefreshingLabel_custom_str);
		if (mOnHeaderRefreshListener != null) {
			mOnHeaderRefreshListener.onHeaderRefresh(this);
		}
	}

	/**
	 * 一进入刷新显示
	 */
	public void showRefresh() {
		setHeaderTopMargin(0);
		mRefreshimage_customview_iv.setImageDrawable(null);
		mRefreshprogress_customview_pbar.setVisibility(View.VISIBLE);
		mRefreshtext_customview_tv
				.setText(R.string.pullToRefreshRefreshingLabel_custom_str);
	}

	public void hideRefresh() {
		new Handler().postDelayed(new Runnable() {
			public void run() {
				setHeaderTopMargin(-mHeaderViewHeight);
			}
		}, 1000);

	}

	public void hideFootView(){
		mFooterView.setVisibility(GONE);
	}

	/**
	 * footer refreshing
	 * 
	 */
	private void footerRefreshing() {
		mFooterState = REFRESHING;
		int top = mHeaderViewHeight + mFooterViewHeight;
		setHeaderTopMargin(-top);
		mLoadimage_customview_iv.setVisibility(View.GONE);
		mLoadimage_customview_iv.clearAnimation();
		mLoadimage_customview_iv.setImageDrawable(null);
		mLoadprogress_customview_pbar.setVisibility(View.VISIBLE);
		mLoadtext_customview_tv
				.setText(R.string.pullToRefreshFooterRefreshingLabel_custom_str);
		if (mOnFooterRefreshListener != null) {
			mOnFooterRefreshListener.onFooterRefresh(this);
		}
	}

	/**
	 * 设置header view 的topMargin的值
	 * 
	 * @param topMargin
	 *            ，为0时，说明header view 刚好完全显示出来； 为-mHeaderViewHeight时，说明完全隐藏了
	 */
	public void setHeaderTopMargin(int topMargin) {
		LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
		params.topMargin = topMargin;
		mHeaderView.setLayoutParams(params);
		invalidate();
	}

	/**
	 * header view 完成更新后恢复初始状态
	 * 
	 */
	public void onHeaderRefreshComplete() {
		setHeaderTopMargin(-mHeaderViewHeight);
		mRefreshimage_customview_iv.setVisibility(View.VISIBLE);
		mRefreshimage_customview_iv
				.setImageResource(R.drawable.ic_custom_pulltorefresharrowdown);
		mRefreshtext_customview_tv
				.setText(R.string.pullToRefreshPullLabel_custom_str);
		mRefreshprogress_customview_pbar.setVisibility(View.GONE);
		// mHeaderUpdateTextView.setText("");
		mHeaderState = PULL_TO_REFRESH;
		// 设置更新时间
        String updateTime = UIUtil.getString(R.string.label_update_time);
		setLastUpdated(updateTime + new Date().toLocaleString());
	}

	/**
	 * Resets the list to a normal state after a refresh.
	 * 
	 * @param lastUpdated
	 *            Last updated at.
	 */
	public void onHeaderRefreshComplete(CharSequence lastUpdated) {
		setLastUpdated(lastUpdated);
		onHeaderRefreshComplete();
	}

	/**
	 * footer view 完成更新后恢复初始状态
	 */
	public void onFooterRefreshComplete() {
		setHeaderTopMargin(-mHeaderViewHeight);
		mLoadimage_customview_iv.setVisibility(View.VISIBLE);
		mLoadimage_customview_iv
				.setImageResource(R.drawable.ic_custom_pulltorefresharrowup);
		mLoadtext_customview_tv
				.setText(R.string.pullToRefreshFooterPullLabel_custom_str);
		mLoadprogress_customview_pbar.setVisibility(View.GONE);
		// mHeaderUpdateTextView.setText("");
		mFooterState = PULL_TO_REFRESH;
	}

	/**
	 * Set a text to represent when the list was last updated.
	 * 
	 * @param lastUpdated
	 *            Last updated at.
	 */
	public void setLastUpdated(CharSequence lastUpdated) {
		if (lastUpdated != null) {
			mRefreshupdatedtime_customview_tv.setVisibility(View.VISIBLE);
			mRefreshupdatedtime_customview_tv.setText(lastUpdated);
		} else {
			mRefreshupdatedtime_customview_tv.setVisibility(View.GONE);
		}
	}

	/**
	 * 获取当前header view 的topMargin
	 * 
	 */
	private int getHeaderTopMargin() {
		LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
		return params.topMargin;
	}

	// /**
	// * lock
	// *
	// */
	// private void lock() {
	// mLock = true;
	// }
	//
	// /**
	// * unlock
	// *
	// */
	// private void unlock() {
	// mLock = false;
	// }

	/**
	 * set headerRefreshListener
	 * 
	 * @param headerRefreshListener
	 */
	public void setOnHeaderRefreshListener(
			OnHeaderRefreshListener headerRefreshListener) {
		mOnHeaderRefreshListener = headerRefreshListener;
	}

	public void setOnFooterRefreshListener(
			OnFooterRefreshListener footerRefreshListener) {
		mOnFooterRefreshListener = footerRefreshListener;
	}

	// add by mzgeng 2014-07-23 获取滑动
	public int getPullState() {
		return mPullState;
	}

	/**
	 * Interface definition for a callback to be invoked when list/grid footer
	 * view should be refreshed.
	 */
	public interface OnFooterRefreshListener {
		public void onFooterRefresh(PullToRefreshView view);
	}

	/**
	 * Interface definition for a callback to be invoked when list/grid header
	 * view should be refreshed.
	 */
	public interface OnHeaderRefreshListener {
		public void onHeaderRefresh(PullToRefreshView view);
	}

	public float getDisplayMetrics(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		float density_f = 8.0f; // 默认值
		if (dm != null) {
			density_f = dm.density;
		}
		return density_f;
	}
}