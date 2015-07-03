package com.torv.lijian.slidemenu.menu;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.torv.lijian.slidemenu.R;

/**
 * Created by lijian on 6/3/15.
 */
public class SlideMenu extends FrameLayout {

    private static final String tag = "SlideMenu";

    private static final int SLIDE_MOVE_THRESHOLD = 25;

    private Activity activity;
    private ViewGroup mViewGroupDecor;
    private ViewActivity viewActivity;

    private RelativeLayout mRlMenuConent;

    private DisplayMetrics displayMetrics = new DisplayMetrics();
    private int mScreenWidth;
    private int mScreenHeight;

    private float mScaleValue = 0.5f;

    private boolean isOpened = false;

    private float lastRawX;
    private float lastActionDownX, lastActionDownY;
    private static final int PRESSED_MOVE_HORIZONTAL = 2;
    private static final int PRESSED_DOWN = 3;
    private static final int PRESSED_DONE = 4;
//    private static final int PRESSED_MOVE_VERTICAL = 5;
    private int pressedState = PRESSED_DOWN;


    public SlideMenu(Context context) {
        super(context);
        initViews(context);
    }

    private void initViews(Context context) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.slide_menu, this);

        mRlMenuConent = (RelativeLayout) findViewById(R.id.rl_slidemenu_content);

        ViewGroup parent = (ViewGroup) mRlMenuConent.getParent();
        parent.removeView(mRlMenuConent);
    }

    private SlideMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void attachToActivity(Activity activity) {

        initValue(activity);

        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mScreenWidth = displayMetrics.widthPixels;
        mScreenHeight = displayMetrics.heightPixels;

        mViewGroupDecor.addView(this, 0);
//        viewActivity.setTouchDisable(true);
    }

    private void initValue(Activity activity) {
        this.activity = activity;

        mViewGroupDecor = (ViewGroup) activity.getWindow().getDecorView();
        View mContentView = mViewGroupDecor.getChildAt(0);
        mViewGroupDecor.removeViewAt(0);

        viewActivity = new ViewActivity(this.activity);
        viewActivity.setContent(mContentView);

        addView(viewActivity);
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void openMenu() {

        setPivot();

        isOpened = true;
        AnimatorSet scaleAnima = new AnimatorSet();
        scaleAnima.playTogether(
                ObjectAnimator.ofFloat(viewActivity, "scaleX", mScaleValue),
                ObjectAnimator.ofFloat(viewActivity, "scaleY", mScaleValue));
        scaleAnima.addListener(mAnimatorListener);
        scaleAnima.setDuration(250);
        scaleAnima.start();
    }

    public void closeMenu() {

        isOpened = false;
        AnimatorSet scaleAnima = new AnimatorSet();
        scaleAnima.playTogether(
                ObjectAnimator.ofFloat(viewActivity, "scaleX", 1.0f),
                ObjectAnimator.ofFloat(viewActivity, "scaleY", 1.0f));
        scaleAnima.addListener(mAnimatorListener);
        scaleAnima.setDuration(250);
        scaleAnima.start();
    }

    private void setPivot() {

        ViewHelper.setPivotX(viewActivity, mScreenWidth * 1.5f);
        ViewHelper.setPivotY(viewActivity, mScreenHeight * 0.5f);
    }


    private Animator.AnimatorListener mAnimatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {
            if (isOpened) {

            } else {
                hideMenuContent();
            }
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            if (isOpened) {
                viewActivity.setTouchDisable(true);
                viewActivity.setOnClickListener(viewActivityOnClickListener);

                showMenuContent();
            } else {
                viewActivity.setTouchDisable(false);
                viewActivity.setOnClickListener(null);
            }
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    };

    private OnClickListener viewActivityOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isOpened()) closeMenu();
        }
    };

    private void showMenuContent() {
        if (mRlMenuConent != null && mRlMenuConent.getParent() == null) {
            ViewHelper.setAlpha(mRlMenuConent, 1);
            addView(mRlMenuConent);
        }
    }

    private void hideMenuContent() {
        if (mRlMenuConent != null && mRlMenuConent.getParent() != null) {
            ViewHelper.setAlpha(mRlMenuConent, 0);
            removeView(mRlMenuConent);
        }
    }

    private float getTargetScale(float currentRawX) {
        float scaleFloatX = ((currentRawX - lastRawX) / mScreenWidth) * 0.6f;

        float targetScale = ViewHelper.getScaleX(viewActivity) - scaleFloatX;
        targetScale = targetScale > 1.0f ? 1.0f : targetScale;
        targetScale = targetScale < 0.5f ? 0.5f : targetScale;
        return targetScale;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
Log.e(tag, "dispatchTouchEvent");
        float currentActivityScaleX = ViewHelper.getScaleX(viewActivity);

        switch (ev.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
                Log.e(tag, "ACTION_DOWN, currentActivityScaleX:"+currentActivityScaleX);
                lastActionDownX = ev.getX();
                lastActionDownY = ev.getY();
                pressedState = PRESSED_DOWN;

                break;

            case MotionEvent.ACTION_MOVE:
                Log.w(tag, "ACTION_MOVE, currentActivityScaleX:"+currentActivityScaleX);
                if (pressedState != PRESSED_DOWN && pressedState != PRESSED_MOVE_HORIZONTAL)
                    break;

                int xOffset = (int) (ev.getX() - lastActionDownX);
                int yOffset = (int) (ev.getY() - lastActionDownY);

                if (pressedState == PRESSED_DOWN) {
//                    if (yOffset > 25 || yOffset < -25) {
//                        pressedState = PRESSED_MOVE_VERTICAL;
//                        break;
//                    }
                    if (xOffset < -SLIDE_MOVE_THRESHOLD || xOffset > SLIDE_MOVE_THRESHOLD) {
                        pressedState = PRESSED_MOVE_HORIZONTAL;
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                    }
                } else if (pressedState == PRESSED_MOVE_HORIZONTAL) {

                    if (currentActivityScaleX < 0.95)
                        showMenuContent();

                    float targetScale = getTargetScale(ev.getRawX());
                    ViewHelper.setScaleX(viewActivity, targetScale);
                    ViewHelper.setScaleY(viewActivity, targetScale);
                    ViewHelper.setAlpha(mRlMenuConent, (1 - targetScale) * 2.0f);

                    lastRawX = ev.getRawX();
                    return true;
                }

                break;

            case MotionEvent.ACTION_UP:
                Log.d(tag, "ACTION_UP, currentActivityScaleX:"+currentActivityScaleX);
                if (pressedState != PRESSED_MOVE_HORIZONTAL)
                    break;

                pressedState = PRESSED_DONE;

                if (isOpened()) {
                    if (currentActivityScaleX > 0.56f)
                        closeMenu();
                    else
                        openMenu();
                } else {
                    if (currentActivityScaleX < 0.94f) {
                        openMenu();
                    } else {
                        closeMenu();
                    }
                }

                break;

        }
        return super.dispatchTouchEvent(ev);
    }
}
