package com.kent.util.recyclerview;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;

import com.kent.util.recyclerview.CustomRecyclerView.OnItemClickListener;
import com.kent.util.recyclerview.CustomRecyclerView.OnItemLongClickListener;
import com.kent.util.recyclerview.CustomRecyclerView.OnItemSelectedListener;

public  class ReViewAdapter<T> extends RecyclerView.Adapter<ReViewHolder>{
	
	private List<T> mDatas;
	
	private Context mContext;
	
	private View mItemView=null;
	
	private Class mClass;
	
	private OnItemSelectedListener mOnItemSelectedListener;

	//记录选中的View
	private SparseBooleanArray selectedItem;

	private int selectedBackgroundColor;

	private int unSelectedBackgroundColor;

	private boolean selectedEffectEnable;
	
	private boolean clickEffectEnable;

	public ReViewAdapter(Context context){
		mDatas=new ArrayList<T>();
		mContext=context;
		selectedItem=new SparseBooleanArray();
	}
	
	public ReViewAdapter(Context context,List<T> datas){
		mDatas=datas;
		mContext=context;
		selectedItem=new SparseBooleanArray();
	}
	
	public ReViewAdapter(Context context,List<T> datas,Class c){
		this(context, datas);
		mClass=c;
	}
	
	public void setClickEffectEnable(boolean clickEffect){
		clickEffectEnable=clickEffect;
	}

	public void setOnItemSelectedListener(OnItemSelectedListener listener,boolean setEffect,int selectedColor,int unSelectedColor){
		selectedEffectEnable=setEffect;
		this.selectedBackgroundColor=selectedColor;
		this.unSelectedBackgroundColor=unSelectedColor;
		mOnItemSelectedListener=listener;
	}

	@Override
	public ReViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (mClass!=null) {
			try {
				Method build=mClass.getMethod("build",Context.class);
				mItemView=(View) build.invoke(mClass,mContext);
			} catch (Exception e) {
				e.printStackTrace();
			} 
			return new ReViewHolder(mItemView);
		}
		
		if (onCreateItemView(viewType)!=null) {
			mItemView= onCreateItemView(viewType);
			return new ReViewHolder(mItemView);
		}
		
		return new ReViewHolder(mItemView);
	}

	public  View onCreateItemView( int viewType){
		return null;
	}

	@Override
	public void onBindViewHolder(ReViewHolder holder, int position) {
		ItemView view = (ItemView) holder.getView();
		view.bind(mDatas.get(position));
		//绑定监听事件
		if (mOnItemSelectedListener!=null) {
			bindListener(holder, position);
		}
		 //針對ItemSelected事件, 取消ViewHolder複用的影響
		 //Item被选时修改背景色
		if (selectedItem.get(position)) {
			if (selectedEffectEnable) {
				holder.getView().setBackgroundColor(selectedBackgroundColor);
			}
			if (mOnItemSelectedListener!=null) {
				mOnItemSelectedListener.onItemSelected(holder.getView(), position,getItemViewType(position));
			}
		}else {
			if (selectedEffectEnable) {
				holder.getView().setBackgroundColor(unSelectedBackgroundColor);
			}
			if (mOnItemSelectedListener!=null) {
				mOnItemSelectedListener.onItemUnselected(holder.getView(), position,getItemViewType(position));
			}
		}
		 // 设置点击效果
		if (!selectedEffectEnable&&clickEffectEnable) {
			view.setClickEffect(true);
		}else {
			view.setClickEffect(false);
		}
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public int getItemCount() {
		return mDatas.size();
	}

	@Override
	public  int getItemViewType(int position){
		return 0;
	}
	
	/**
	 *在末尾添加数据 
	 */
	public void addData(T t) {
		mDatas.add(t);
		notifyDataSetChanged();
	}
	/**
	 *更换全部数据 
	 */
	public void setDatas(List<T> datas){
		mDatas=datas;
		notifyDataSetChanged();
	}
	
	private void bindListener(final ReViewHolder holder, final int position) {
		holder.itemView.setOnClickListener(new OnClickListener() {
			int viewType=getItemViewType(position);
			@Override
			public void onClick(View v) {
				if (selectedItem.get(position)) {
					if (selectedEffectEnable) {
						v.setBackgroundColor(unSelectedBackgroundColor);
					}
					mOnItemSelectedListener.onItemUnselected(v,position,viewType);
					selectedItem.delete(position);
					if (selectedItem.size()==0) {
						mOnItemSelectedListener.onNothingSelected(v, position);
					}
				}else {
					if (selectedEffectEnable) {
						v.setBackgroundColor(selectedBackgroundColor);
					}
					mOnItemSelectedListener.onItemSelected(v, position,viewType);
					selectedItem.append(position, true);
				}
			}
		});
	}

}

class ReViewHolder  extends ViewHolder{

	private View view;
	
	public ReViewHolder(View view) {
		super(view);
		this.view=view;
	}
	
	public View  getView(){
		return view;
	}

}
