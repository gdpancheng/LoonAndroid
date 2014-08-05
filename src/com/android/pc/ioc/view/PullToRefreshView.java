package com.android.pc.ioc.view;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.pc.ioc.event.EventBus;
import com.android.pc.ioc.inject.InjectView;
import com.android.pc.util.Handler_Bitmap;
import com.android.pc.util.Handler_System;

public class PullToRefreshView extends LinearLayout {

	// private static final String TAG = "PullToRefreshView";
	// refresh states
	private static final int PULL_TO_REFRESH = 2;
	private static final int RELEASE_TO_REFRESH = 3;
	private static final int REFRESHING = 4;
	// pull state
	private static final int PULL_NONE_STATE = -1;
	private static final int PULL_UP_STATE = 0;
	private static final int PULL_DOWN_STATE = 1;

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
	private RelativeLayout mHeaderView;
	/**
	 * footer view
	 */
	private RelativeLayout mFooterView;
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
	private ImageView mHeaderImageView;
	/**
	 * footer view image
	 */
	private ImageView mFooterImageView;
	/**
	 * header tip text
	 */
	private TextView mHeaderTextView;
	/**
	 * footer tip text
	 */
	private TextView mFooterTextView;
	/**
	 * header refresh time
	 */
	private TextView mHeaderUpdateTextView;
	/**
	 * header progress bar
	 */
	private ProgressBar mHeaderProgressBar;
	/**
	 * footer progress bar
	 */
	private ProgressBar mFooterProgressBar;
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
	private int mPullState = PULL_NONE_STATE;
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

	private boolean isGet = false;
	private boolean isHeader = true;
	private boolean isFooter = true;

	private Bitmap downBitmap;
	private Bitmap upBitmap;

	private PullToRefreshManager manager;

	private EventBus eventBus = EventBus.getDefault();

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
	 * @param context
	 */
	private void init() {
		manager = PullToRefreshManager.getInstance();
		// 需要设置成vertical
		setOrientation(LinearLayout.VERTICAL);
		// Load all of the animations we need in code rather than through XML
		mFlipAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mFlipAnimation.setInterpolator(new LinearInterpolator());
		mFlipAnimation.setDuration(250);
		mFlipAnimation.setFillAfter(true);
		mReverseFlipAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
		mReverseFlipAnimation.setDuration(250);
		mReverseFlipAnimation.setFillAfter(true);

		// header view 在此添加,保证是第一个添加到linearlayout的最上端
		addHeaderView();
	}

	@Override
	public void addView(View child) {
		super.addView(child);
	}

	private void addHeaderView() {
		try {
			InputStream down = getResources().getAssets().open("down.png");
			InputStream up = getResources().getAssets().open("up.png");

			downBitmap = BitmapFactory.decodeStream(down);
			upBitmap = BitmapFactory.decodeStream(up);
		} catch (IOException e) {
		}

		float rote = Handler_System.getPadRoate();
		downBitmap = Handler_Bitmap.scaleImg(downBitmap, (int) (downBitmap.getWidth() * rote), (int) (downBitmap.getHeight() * rote));
		upBitmap = Handler_Bitmap.scaleImg(upBitmap, (int) (upBitmap.getWidth() * rote), (int) (upBitmap.getHeight() * rote));

		mHeaderView = new RelativeLayout(getContext());
		LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, (int) (100 * rote));
		layoutParams.bottomMargin = (int) (15 * rote);
		layoutParams.topMargin = (int) (15 * rote);
		layoutParams.gravity = Gravity.CENTER;
		mHeaderView.setLayoutParams(layoutParams);

		mHeaderProgressBar = new ProgressBar(getContext());
		mHeaderProgressBar.setIndeterminate(false);
		mHeaderProgressBar.setVisibility(View.GONE);
		RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams((int) (40 * rote), (int) (40 * rote));
		rl.leftMargin = (int) (30 * rote);
		rl.rightMargin = (int) (20 * rote);
		rl.addRule(RelativeLayout.CENTER_VERTICAL);
		mHeaderProgressBar.setLayoutParams(rl);
		mHeaderView.addView(mHeaderProgressBar);

		mHeaderImageView = new ImageView(getContext());
		rl = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rl.leftMargin = (int) (30 * rote);
		rl.rightMargin = (int) (20 * rote);
		rl.addRule(RelativeLayout.CENTER_VERTICAL);
		mHeaderImageView.setLayoutParams(rl);
		mHeaderImageView.setImageBitmap(downBitmap);
		mHeaderView.addView(mHeaderImageView);

		LinearLayout layout = new LinearLayout(getContext());
		rl = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		rl.addRule(RelativeLayout.CENTER_VERTICAL);
		layout.setOrientation(VERTICAL);
		layout.setGravity(Gravity.CENTER);
		layout.setLayoutParams(rl);

		mHeaderTextView = new TextView(getContext());
		mHeaderTextView.setGravity(Gravity.CENTER);
		mHeaderTextView.setText(manager.getPull_label());
		mHeaderTextView.setTextColor(Color.BLACK);
		mHeaderTextView.setTypeface(Typeface.DEFAULT_BOLD, Typeface.ITALIC);
		rl = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		mHeaderTextView.setLayoutParams(rl);
		layout.addView(mHeaderTextView);

		mHeaderUpdateTextView = new TextView(getContext());
		mHeaderUpdateTextView.setGravity(Gravity.CENTER);
		mHeaderUpdateTextView.setText(manager.getUpdateTime());
		mHeaderUpdateTextView.setTextColor(Color.BLACK);
		mHeaderUpdateTextView.setVisibility(View.GONE);
		mHeaderUpdateTextView.setTextSize(10f);
		rl = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 40);
		mHeaderUpdateTextView.setLayoutParams(rl);
		layout.addView(mHeaderUpdateTextView);
		mHeaderView.addView(layout);
		// header layout
		measureView(mHeaderView);
		mHeaderViewHeight = mHeaderView.getMeasuredHeight();
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, mHeaderViewHeight);
		// 设置topMargin的值为负的header View高度,即将其隐藏在最上方
		params.topMargin = -(mHeaderViewHeight);
		// mHeaderView.setLayoutParams(params1);
		addView(mHeaderView, params);
	}

	private void addFooterView() {

		mFooterView = new RelativeLayout(getContext());
		float rote = Handler_System.getPadRoate();
		LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		layoutParams.bottomMargin = (int) (15 * rote);
		layoutParams.topMargin = (int) (10 * rote);
		layoutParams.gravity = Gravity.CENTER;
		mFooterView.setLayoutParams(layoutParams);

		mFooterProgressBar = new ProgressBar(getContext());
		mFooterProgressBar.setIndeterminate(false);
		mFooterProgressBar.setVisibility(View.GONE);
		RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rl.leftMargin = (int) (30 * rote);
		rl.topMargin = (int) (10 * rote);
		rl.rightMargin = (int) (20 * rote);
		mFooterProgressBar.setLayoutParams(rl);
		mFooterView.addView(mFooterProgressBar);

		mFooterImageView = new ImageView(getContext());
		rl = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rl.leftMargin = (int) (30 * rote);
		rl.rightMargin = (int) (20 * rote);
		mFooterImageView.setLayoutParams(rl);
		mFooterImageView.setImageBitmap(upBitmap);
		mFooterView.addView(mFooterImageView);

		LinearLayout layout = new LinearLayout(getContext());
		rl = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		layout.setOrientation(VERTICAL);
		rl.addRule(RelativeLayout.CENTER_IN_PARENT);
		layout.setGravity(Gravity.CENTER);
		layout.setLayoutParams(rl);

		mFooterTextView = new TextView(getContext());
		mFooterTextView.setGravity(Gravity.CENTER);
		mFooterTextView.setText(manager.getFooter_pull_label());
		mFooterTextView.setTextColor(Color.BLACK);
		mFooterTextView.setTypeface(Typeface.DEFAULT_BOLD, Typeface.ITALIC);
		rl = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		mFooterTextView.setLayoutParams(rl);
		layout.addView(mFooterTextView);

		mFooterView.addView(layout);

		// footer layout
		measureView(mFooterView);
		mFooterViewHeight = mFooterView.getMeasuredHeight();
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, mFooterViewHeight);
		// int top = getHeight();
		// params.topMargin
		// =getHeight();//在这里getHeight()==0,但在onInterceptTouchEvent()方法里getHeight()已经有值了,不再是0;
		// getHeight()什么时候会赋值,稍候再研究一下
		// 由于是线性布局可以直接添加,只要AdapterView的高度是MATCH_PARENT,那么footer view就会被添加到最后,并隐藏
		addView(mFooterView, params);
	}

	public int getmHeaderState() {
		return mHeaderState;
	}

	public void setmHeaderState(int mHeaderState) {
		this.mHeaderState = mHeaderState;
	}

	public int getmFooterState() {
		return mFooterState;
	}

	public void setmFooterState(int mFooterState) {
		this.mFooterState = mFooterState;
	}

	public void onFooter() {
		// footer view 在此添加保证添加到linearlayout中的最后
		addFooterView();
		initContentAdapterView();
		View view = getChildAt(0);
		if (view == null) {
			return;
		}

		ViewTreeObserver vto = this.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				if (isGet) {
					return true;
				}
				isGet = true;
				if (mAdapterView != null) {
					if (LinearLayout.LayoutParams.class.isAssignableFrom(PullToRefreshView.this.getLayoutParams().getClass())) {
						//重置listview的高度
						LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mAdapterView.getLayoutParams();
						layoutParams.height = mAdapterView.getHeight();
						if (layoutParams.height <= 2) {
							layoutParams.height = PullToRefreshView.this.getHeight();
						}
						mAdapterView.setLayoutParams(layoutParams);
						//重置下拉组件的高度
						LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) PullToRefreshView.this.getLayoutParams();
						params.height = layoutParams.height;
						PullToRefreshView.this.setLayoutParams(params);
					}
					if (AbsoluteLayout.LayoutParams.class.isAssignableFrom(PullToRefreshView.this.getLayoutParams().getClass())) {
						//重置listview的高度
						AbsoluteLayout.LayoutParams layoutParams = (AbsoluteLayout.LayoutParams) mAdapterView.getLayoutParams();
						layoutParams.height = mAdapterView.getHeight();
						if (layoutParams.height <= 2) {
							layoutParams.height = PullToRefreshView.this.getHeight();
						}
						mAdapterView.setLayoutParams(layoutParams);
						//重置下拉组件的高度
						AbsoluteLayout.LayoutParams params = (AbsoluteLayout.LayoutParams) PullToRefreshView.this.getLayoutParams();
						params.height = layoutParams.height;
						PullToRefreshView.this.setLayoutParams(params);
					}
					if (RelativeLayout.LayoutParams.class.isAssignableFrom(PullToRefreshView.this.getLayoutParams().getClass())) {
						//重置listview的高度
						RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mAdapterView.getLayoutParams();
						layoutParams.height = mAdapterView.getHeight();
						if (layoutParams.height <= 2) {
							layoutParams.height = PullToRefreshView.this.getHeight();
						}
						mAdapterView.setLayoutParams(layoutParams);
						//重置下拉组件的高度
						RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) PullToRefreshView.this.getLayoutParams();
						params.height = layoutParams.height;
						PullToRefreshView.this.setLayoutParams(params);
					}
					if (FrameLayout.LayoutParams.class.isAssignableFrom(PullToRefreshView.this.getLayoutParams().getClass())) {
						//重置listview的高度
						FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mAdapterView.getLayoutParams();
						layoutParams.height = mAdapterView.getHeight();
						if (layoutParams.height <= 2) {
							layoutParams.height = PullToRefreshView.this.getHeight();
						}
						mAdapterView.setLayoutParams(layoutParams);
						//重置下拉组件的高度
						FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) PullToRefreshView.this.getLayoutParams();
						params.height = layoutParams.height;
						PullToRefreshView.this.setLayoutParams(params);
					}
				}
				return true;
			}
		});
	}

	/**
	 * init AdapterView like ListView,GridView and so on;or init ScrollView
	 * 
	 */
	private void initContentAdapterView() {
		int count = getChildCount();
		if (count < 3) {
			throw new IllegalArgumentException("This layout must contain 3 child views,and AdapterView or ScrollView must in the second position!");
		}
		View view = null;
		for (int i = 0; i < count - 1; ++i) {
			view = getChildAt(i);
			if (view instanceof AdapterView<?>) {
				mAdapterView = (AdapterView<?>) view;
			}
			if (view instanceof ScrollView) {
				// finish later
				mScrollView = (ScrollView) view;
			}
		}
		if (mAdapterView == null && mScrollView == null) {
			throw new IllegalArgumentException("must contain a AdapterView or ScrollView in this layout!");
		}
	}

	@Override
	protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
		super.measureChild(child, parentWidthMeasureSpec, parentHeightMeasureSpec);
	}

	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
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

	/*
	 * 如果在onInterceptTouchEvent()方法中没有拦截(即onInterceptTouchEvent()方法中 return false)则由PullToRefreshView 的子View来处理;否则由下面的方法来处理(即由PullToRefreshView自己来处理)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// if (mLock) {
		// return true;
		// }
		int y = (int) event.getRawY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// onInterceptTouchEvent已经记录
			// mLastMotionY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			int deltaY = y - mLastMotionY;
			if (mPullState == PULL_DOWN_STATE) {// 执行下拉
				headerPrepareToRefresh(deltaY);
				// setHeaderPadding(-mHeaderViewHeight);
			} else if (mPullState == PULL_UP_STATE) {// 执行上拉
				footerPrepareToRefresh(deltaY);
			}
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
				if (Math.abs(topMargin) >= mHeaderViewHeight + mFooterViewHeight) {
					// 开始执行footer 刷新
					footerRefreshing();
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
		// 对于ListView和GridView
		if (mAdapterView != null) {
			// 子view(ListView or GridView)滑动到最顶端
			if (deltaY > 0) {
				if (!isHeader) {
					mPullState = PULL_NONE_STATE;
					return false;
				}
				View child = mAdapterView.getChildAt(0);
				if (child == null) {
					// 如果mAdapterView中没有数据,不拦截
					return false;
				}
				if (mAdapterView.getFirstVisiblePosition() == 0 && child.getTop() == 0) {
					mPullState = PULL_DOWN_STATE;
					return true;
				}
				int top = child.getTop();
				int padding = mAdapterView.getPaddingTop();
				if (mAdapterView.getFirstVisiblePosition() == 0 && Math.abs(top - padding) <= 8) {// 这里之前用3可以判断,但现在不行,还没找到原因
					mPullState = PULL_DOWN_STATE;
					return true;
				}
			} else if (deltaY < 0) {
				if (!isFooter) {
					mPullState = PULL_NONE_STATE;
					return false;
				}
				View lastChild = mAdapterView.getChildAt(mAdapterView.getChildCount() - 1);
				if (lastChild == null) {
					// 如果mAdapterView中没有数据,不拦截
					return false;
				}
				// 最后一个子view的Bottom小于父View的高度说明mAdapterView的数据没有填满父view,
				// 等于父View的高度说明mAdapterView已经滑动到最后
				if (lastChild.getBottom() <= getHeight() && mAdapterView.getLastVisiblePosition() == mAdapterView.getCount() - 1) {
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
				if (!isHeader) {
					mPullState = PULL_NONE_STATE;
					return false;
				}
				mPullState = PULL_DOWN_STATE;
				return true;
			} else if (deltaY < 0 && child.getMeasuredHeight() <= getHeight() + mScrollView.getScrollY()) {
				if (!isFooter) {
					mPullState = PULL_NONE_STATE;
					return false;
				}
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
			mHeaderTextView.setText(manager.getRelease_label());
			mHeaderUpdateTextView.setVisibility(View.VISIBLE);
			mHeaderImageView.clearAnimation();
			mHeaderImageView.startAnimation(mFlipAnimation);
			mHeaderState = RELEASE_TO_REFRESH;
		} else if (newTopMargin < 0 && newTopMargin > -mHeaderViewHeight) {// 拖动时没有释放
			mHeaderImageView.clearAnimation();
			mHeaderImageView.startAnimation(mFlipAnimation);
			// mHeaderImageView.
			mHeaderTextView.setText(manager.getRelease_label());
			mHeaderState = PULL_TO_REFRESH;
		}
	}

	/**
	 * footer 准备刷新,手指移动过程,还没有释放 移动footer view高度同样和移动header view 高度是一样，都是通过修改header view的topmargin的值来达到
	 * 
	 * @param deltaY
	 *            ,手指滑动的距离
	 */
	private void footerPrepareToRefresh(int deltaY) {
		int newTopMargin = changingHeaderViewTopMargin(deltaY);
		// 如果header view topMargin 的绝对值大于或等于header + footer 的高度
		// 说明footer view 完全显示出来了，修改footer view 的提示状态
		if (Math.abs(newTopMargin) >= (mHeaderViewHeight + mFooterViewHeight) && mFooterState != RELEASE_TO_REFRESH) {
			mFooterTextView.setText(manager.getFooter_refreshing_label());
			mFooterImageView.clearAnimation();
			mFooterImageView.startAnimation(mFlipAnimation);
			mFooterState = RELEASE_TO_REFRESH;
		} else if (Math.abs(newTopMargin) < (mHeaderViewHeight + mFooterViewHeight)) {
			mFooterImageView.clearAnimation();
			mFooterImageView.startAnimation(mFlipAnimation);
			mFooterTextView.setText(manager.getFooter_pull_label());
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
		float newTopMargin = params.topMargin + deltaY * 0.5f;
		// 这里对上拉做一下限制,因为当前上拉后然后不释放手指直接下拉,会把下拉刷新给触发了,感谢网友yufengzungzhe的指出
		// 表示如果是在上拉后一段距离,然后直接下拉
		if (deltaY > 0 && mPullState == PULL_UP_STATE && Math.abs(params.topMargin) <= mHeaderViewHeight) {
			return params.topMargin;
		}
		// 同样地,对下拉做一下限制,避免出现跟上拉操作时一样的bug
		if (deltaY < 0 && mPullState == PULL_DOWN_STATE && Math.abs(params.topMargin) >= mHeaderViewHeight) {
			return params.topMargin;
		}
		params.topMargin = (int) newTopMargin;
		mHeaderView.setLayoutParams(params);
		// invalidate();
		return params.topMargin;
	}

	/**
	 * header refreshing
	 * 
	 */
	private void headerRefreshing() {
		mHeaderState = REFRESHING;
		setHeaderTopMargin(0);
		mHeaderImageView.setVisibility(View.GONE);
		mHeaderImageView.clearAnimation();
		mHeaderImageView.setImageDrawable(null);
		mHeaderProgressBar.setVisibility(View.VISIBLE);
		mHeaderTextView.setText(manager.getRefreshing_label());
		if (mOnHeaderRefreshListener != null) {
			mOnHeaderRefreshListener.onHeaderRefresh(this);
		}
	}

	/**
	 * footer refreshing
	 * 
	 */
	private void footerRefreshing() {
		mFooterState = REFRESHING;
		int top = mHeaderViewHeight + mFooterViewHeight;
		setHeaderTopMargin(-top);
		mFooterImageView.setVisibility(View.GONE);
		mFooterImageView.clearAnimation();
		mFooterImageView.setImageDrawable(null);
		mFooterProgressBar.setVisibility(View.VISIBLE);
		mFooterTextView.setText(manager.getRefreshing_label());
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
	private void setHeaderTopMargin(int topMargin) {
		LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
		params.topMargin = topMargin;
		mHeaderView.setLayoutParams(params);
		// invalidate();
	}

	/**
	 * header view 完成更新后恢复初始状态
	 * 
	 */
	public void onHeaderRefreshComplete() {
		setHeaderTopMargin(-mHeaderViewHeight);
		mHeaderImageView.setVisibility(View.VISIBLE);
		mHeaderImageView.setImageBitmap(downBitmap);
		mHeaderTextView.setText(manager.getRelease_label());
		mHeaderProgressBar.setVisibility(View.GONE);
		// mHeaderUpdateTextView.setText("");
		mHeaderState = PULL_TO_REFRESH;
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
		mFooterImageView.setVisibility(View.VISIBLE);
		mFooterImageView.setImageBitmap(upBitmap);
		mFooterTextView.setText(manager.getFooter_pull_label());
		mFooterProgressBar.setVisibility(View.GONE);
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
			mHeaderUpdateTextView.setVisibility(View.VISIBLE);
			mHeaderUpdateTextView.setText(lastUpdated);
		} else {
			mHeaderUpdateTextView.setVisibility(View.GONE);
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

	public void setOnHeaderRefreshListener(OnHeaderRefreshListener headerRefreshListener) {
		mOnHeaderRefreshListener = headerRefreshListener;
	}

	public void setOnFooterRefreshListener(OnFooterRefreshListener footerRefreshListener) {
		mOnFooterRefreshListener = footerRefreshListener;
	}

	/**
	 * Interface definition for a callback to be invoked when list/grid footer view should be refreshed.
	 */
	public interface OnFooterRefreshListener {
		public void onFooterRefresh(PullToRefreshView view);
	}

	/**
	 * Interface definition for a callback to be invoked when list/grid header view should be refreshed.
	 */
	public interface OnHeaderRefreshListener {
		public void onHeaderRefresh(PullToRefreshView view);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		eventBus.unregister(this);
	}

	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);
		switch (visibility) {
		case View.GONE:
			eventBus.unregister(this);
			break;
		case View.INVISIBLE:
			eventBus.unregister(this);
			break;
		case View.VISIBLE:
			eventBus.register(this);
			break;
		}
	}

	public AdapterView<?> getAdapterView() {
		return mAdapterView;
	}

	public boolean isHeader() {
		return isHeader;
	}

	public void setHeader(boolean isHeader) {
		this.isHeader = isHeader;
	}

	public boolean isFooter() {
		return isFooter;
	}

	public void setFooter(boolean isFooter) {
		this.isFooter = isFooter;
	}

	public void onEventMainThread(RefershEntity entity) {
		switch (entity.getType()) {
		case InjectView.PULL_CLOSE:
			setFooter(false);
			break;
		case InjectView.DOWN_CLOSE:
			setHeader(false);
			break;
		case InjectView.PULL_OPEN:
			setFooter(true);
			break;
		case InjectView.DOWN_OPEN:
			setHeader(true);
			break;
		case InjectView.DOWN:
			onHeaderRefreshComplete();
			break;
		case InjectView.PULL:
			onFooterRefreshComplete();
			if (manager.getLimit() == 0) {
				return;
			}
			if (mAdapterView != null) {
				int count = mAdapterView.getAdapter().getCount();
				if (count % manager.getLimit() != 0) {
					setFooter(false);
				} else {
					setFooter(true);
				}
			}
			break;
		}
	}
}
