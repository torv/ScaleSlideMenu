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

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.torv.lijian.slidemenu.R;

/**
 * Created by lijian on 6/3/15.
 */
public class SlideMenu extends FrameLayout {

    private static final String TAG = "SlideMenu";

    private Activity activity;
    private ViewGroup mViewGroupDecor;
    private ViewActivity viewActivity;

    private RelativeLayout mRlMenuConent;

    private DisplayMetrics displayMetrics = new DisplayMetrics();
    private int mScreenWidth;
    private int mScreenHeight;

    private float mScaleValue = 0.6f;

    private boolean isOpened = false;

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
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
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
            }else{
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
            addView(mRlMenuConent);
        }
    }

    private void hideMenuContent() {
        if (mRlMenuConent != null && mRlMenuConent.getParent() == null) {
            removeView(mRlMenuConent);
        }
    }
}
