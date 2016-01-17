package com.kent.util.recyclerview;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnItemTouchListener;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.kent.util.recyclerview.divider.HorizontalDividerItemDecoration;

public class CustomRecyclerView extends RecyclerView implements
		OnItemTouchListener {
	private Context mContext;
	private ItemDecoration mItemDecoration;

	private String logTag = "CustomRecyclerView";

	private OnItemTouchListener mOnItemTouchListener = this;
	private OnItemClickListener mOnItemClickListener = null;
	private OnItemSelectedListener mOnItemSelectedListener = null;
	private OnItemLongClickListener mOnItemLongClickListener = null;
	// 默认灰色
	private int selectedColor = Color.parseColor("#cdcdcd");
	// 默认白色
	private int unSelectedColor = Color.parseColor("#ffffff");
	
	private boolean setSelectedEffectEnable;
	
	private boolean setClickEffectEnable;

	public CustomRecyclerView(Context context) {
		super(context);
		init(context);
	}

	public CustomRecyclerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CustomRecyclerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	public void setOnItemClickListener(OnItemClickListener listener) {
		this.addOnItemTouchListener(mOnItemTouchListener);
		mOnItemClickListener = listener;
	}

	public void setOnItemSelectedListener(OnItemSelectedListener listener) {
		setOnItemSelectedListener(listener, false);
	}

	public void setOnItemSelectedListener(OnItemSelectedListener listener,
			boolean setSelectedEffect) {
		setOnItemSelectedListener(listener, setSelectedEffect, selectedColor,
				unSelectedColor);
	}

	public void setOnItemSelectedListener(OnItemSelectedListener listener,
			boolean setSelectedEffect, int selectedColor, int unSelectedColor) {
		mOnItemSelectedListener = listener;
		this.setSelectedEffectEnable = setSelectedEffect;
		this.selectedColor = selectedColor;
		this.unSelectedColor = unSelectedColor;
		Adapter adapter = getAdapter();
		if (adapter != null) {
			ReViewAdapter myAdapter = (ReViewAdapter) adapter;
			myAdapter.setOnItemSelectedListener(mOnItemSelectedListener,
					setSelectedEffect, selectedColor, unSelectedColor);
		}
	}

	public void setOnItemLongClickListener(OnItemLongClickListener listener) {
		this.addOnItemTouchListener(mOnItemTouchListener);
		mOnItemLongClickListener = listener;
	}

	public void setOnItemLeftScrollListener(OnItemLeftScrollListener listener) {
		mOnItemLeftScrollListener = listener;
	}

	public void setOnItemRightScrollListener(OnItemRightScrollListener listener) {
		mOnItemRightScrollListener = listener;
	}

	public final OnItemClickListener getOnItemClickListener() {
		return mOnItemClickListener;
	}

	public final OnItemSelectedListener getOnItemSelectedLisenter() {
		return mOnItemSelectedListener;
	}

	public final OnItemLongClickListener getOnItemLongClickListener() {
		return mOnItemLongClickListener;
	}

	/**
	 * 设置Adapter同时传递监听事件
	 */
	@Override
	public void setAdapter(Adapter adapter) {
		super.setAdapter(adapter);
		ReViewAdapter myAdapter = (ReViewAdapter) adapter;
		myAdapter.setOnItemSelectedListener(mOnItemSelectedListener,
				setSelectedEffectEnable, selectedColor, unSelectedColor);
	}

	/**
	 * 设置点击效果
	 */
	public CustomRecyclerView setClickEffectEnable(boolean clickEffect) {
		Adapter adapter = getAdapter();
		if (adapter != null) {
			ReViewAdapter myAdapter = (ReViewAdapter) adapter;
			myAdapter.setClickEffectEnable(clickEffect);
		}
		return this;
	}

	/**
	 * 设置分割线
	 * 
	 * @param color
	 *            分割线颜色
	 * @param size
	 *            分割线大小
	 * @param leftMargin
	 *            左边距
	 * @param rightMargin
	 *            右边距
	 */
	public CustomRecyclerView setItemDecoration(int color, int size,
			int leftMargin, int rightMargin) {
		this.removeItemDecoration(mItemDecoration);
		mItemDecoration = new HorizontalDividerItemDecoration.Builder(mContext)
				.color(color).size(size + 1).margin(leftMargin, rightMargin)
				.build();
		this.addItemDecoration(mItemDecoration);
		return this;
	}

	/**
	 * 点击事件接口
	 */
	public interface OnItemClickListener {
		void onItemClick(RecyclerView parent, View view, int position, long id);
	}

	/**
	 * ItemSelected接口
	 */
	public interface OnItemSelectedListener {
		void onItemSelected(View view, int position, int viewType);

		void onNothingSelected(View lastView, int lastPosition);

		void onItemUnselected(View view, int position, int viewType);
	}

	/**
	 * ItemLongClick接口
	 */
	public interface OnItemLongClickListener {
		void onItemLongClick(RecyclerView parent, View view, int position,
				long id);
	}

	/**
	 * Item左划接口
	 */
	private OnItemLeftScrollListener mOnItemLeftScrollListener;

	public interface OnItemLeftScrollListener {
		void onItemLeftScroll(View view, int position, float touchPonitX,
				float distanceX, float totalDistanceX);
	}

	/**
	 * Item右划接口
	 */
	private OnItemRightScrollListener mOnItemRightScrollListener;

	public interface OnItemRightScrollListener {
		void onItemRightScroll(View view, int position, float touchPonitX,
				float distanceX, float totalDistanceX);
	}

	/**
	 * 设置布局管理器--setLayoutManager(manager) 这里设置为竖直方向排布
	 * 添加Item分隔符--addItemDecoration
	 */
	private void init(Context context) {
		mContext = context;
		LinearLayoutManager manager = new LinearLayoutManager(context,
				LinearLayoutManager.VERTICAL, false);
		this.setLayoutManager(manager);
		mItemDecoration = new HorizontalDividerItemDecoration.Builder(context)
				.color(Color.parseColor("#dcdcdc")).size(2).margin(0, 0)
				.build();
		this.addItemDecoration(mItemDecoration);
	}

	private long getTouchPointId(View view, MotionEvent event) {
		int x = (int) event.getRawX();
		int y = (int) event.getRawY();
		long id = 0;
		if (view instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup) view;
			for (int i = 0; i < vg.getChildCount(); i++) {
				View childView = vg.getChildAt(i);
				int[] location = new int[2];
				childView.getLocationInWindow(location);
				if (location[0] < x && location[0] + childView.getWidth() > x
						&& location[1] < y
						&& location[1] + childView.getHeight() > y) {
					id = getTouchPointId(childView, event);
				}
			}
		} else {
			return view.getId();
		}
		return id;
	}

	private boolean isDirectionConfirm = false;
	private boolean isHorizontalLeftScroll = false;
	private boolean isHorizontalRightScroll = false;
	private GestureDetector mGestureDetector = new GestureDetector(mContext,
			new SimpleOnGestureListener() {

				@Override
				public boolean onSingleTapUp(MotionEvent e) {
					View view = findChildViewUnder(e.getX(), e.getY());
					if (mOnItemClickListener != null) {
						mOnItemClickListener.onItemClick(
								CustomRecyclerView.this, view,
								getChildPosition(view),
								getTouchPointId(view, e));
					}
					return false;
				}

				@Override
				public void onLongPress(MotionEvent e) {
					View view = findChildViewUnder(e.getX(), e.getY());
					super.onLongPress(e);
					if (mOnItemLongClickListener != null) {
						mOnItemLongClickListener.onItemLongClick(
								CustomRecyclerView.this, view,
								getChildPosition(view),
								getTouchPointId(view, e));
					}
				}

				@Override
				public boolean onScroll(MotionEvent e1, MotionEvent e2,
						float distanceX, float distanceY) {
					if (mOnItemLeftScrollListener != null
							|| mOnItemRightScrollListener != null) {
						View view = findChildViewUnder(e1.getX(), e1.getY());
						if (!isDirectionConfirm) {
							if (Math.abs(distanceX) > Math.abs(3 * distanceY)) {
								isHorizontalRightScroll = !(isHorizontalLeftScroll = e2
										.getX() < e1.getX());
							} else {
								isHorizontalLeftScroll = false;
								isHorizontalRightScroll = false;
							}
							isDirectionConfirm = true;
						}
//						Log.d(logTag, "distanceX : " + distanceX);
						if (isHorizontalLeftScroll
								&& mOnItemLeftScrollListener != null) {
							mOnItemLeftScrollListener.onItemLeftScroll(view,
									getChildPosition(view), e1.getRawX(),
									distanceX, e1.getX() - e2.getX());
						} else if (isHorizontalRightScroll
								&& mOnItemRightScrollListener != null) {
							mOnItemRightScrollListener.onItemRightScroll(view,
									getChildPosition(view), e1.getRawX(),
									distanceX, e1.getX() - e2.getX());
						}
					}
					return super.onScroll(e1, e2, distanceX, distanceY);
				}

			});

	@Override
	public boolean onInterceptTouchEvent(RecyclerView recyclerView,
			MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			isDirectionConfirm = false;
			break;
		}
		if (isDirectionConfirm
				&& (isHorizontalLeftScroll || isHorizontalRightScroll)) {
			return true;
		}
		return false;
	}

	@Override
	public void onTouchEvent(RecyclerView recyclerView, MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			isDirectionConfirm = false;
			break;
		}
	}
}
