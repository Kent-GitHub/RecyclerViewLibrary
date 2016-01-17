package com.kent.util.recyclerview;

import org.androidannotations.annotations.EViewGroup;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
@EViewGroup
public abstract class ItemView<T> extends RelativeLayout implements MyInterface<T>{
	private boolean canvaRestored; 
	private boolean doPressDraw;
	private boolean clickEffectOn;
	public ItemView(Context context) {
		super(context);
		setWillNotDraw(false);
	}
	/**
	 *  设置点击效果 
	 */
	public ItemView<T> setClickEffect(boolean effectOn){
		clickEffectOn=effectOn;
		return this;
	}
	/**
	 * draw点击效果
	 */
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		if (clickEffectOn&&doPressDraw) {
			canvas.save();
			canvas.drawARGB(30, 0, 0, 0);
		}else if (clickEffectOn&&!doPressDraw&&!canvaRestored) {
			canvaRestored=true;
			canvas.restore();
		}
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
		int action=event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			doPressDraw=true;
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			doPressDraw=false;
			invalidate();
			break;
		case MotionEvent.ACTION_CANCEL:
			doPressDraw=false;
			invalidate();
			break;
		}
        return super.onTouchEvent(event);
    }
}

interface MyInterface<T>{
	public void bind(T  data);
}
