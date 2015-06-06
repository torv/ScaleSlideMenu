package com.torv.lijian.slidemenu.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.nio.channels.InterruptibleChannel;

/**
 * Created by lijian on 6/3/15.
 */
public class ViewActivity extends ViewGroup{

    private View mContent;

    private boolean isTouchDisabled = false;

    public ViewActivity(Context context) {
        super(context);
    }

    public ViewActivity(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int width = r - l;
        int height = b - t;
        mContent.layout(0, 0, width, height);
    }

    public void setContent(View v){
        if(mContent != null){
            removeView(mContent);
        }

        mContent = v;
        addView(mContent);
    }

    public View getContent(){
        return mContent;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(width, height);

        int contentWidth = getChildMeasureSpec(widthMeasureSpec, 0, width);
        int contentHeight = getChildMeasureSpec(heightMeasureSpec, 0, height);
        mContent.measure(contentWidth, contentHeight);
    }

    public void setTouchDisable(boolean diableTouch){
        isTouchDisabled = diableTouch;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isTouchDisabled;
    }
}
