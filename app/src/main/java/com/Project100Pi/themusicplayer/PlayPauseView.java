package com.Project100Pi.themusicplayer;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

public class PlayPauseView extends FrameLayout {

    private static final Property<PlayPauseView, Integer> COLOR =
            new Property<PlayPauseView, Integer>(Integer.class, "color") {
                @Override
                public Integer get(PlayPauseView v) {
                    return v.getColor();
                }

                @Override
                public void set(PlayPauseView v, Integer value) {
                    v.setColor(value);
                }
            };

    private static final long PLAY_PAUSE_ANIMATION_DURATION = 200;

    private final PlayPauseDrawable mDrawable;
    private final Paint mPaint = new Paint();
    private  int mPauseBackgroundColor;
    private  int mPlayBackgroundColor;

    private AnimatorSet mAnimatorSet;
    private int mBackgroundColor;
    private int mWidth;
    private int mHeight;
    private boolean needShadow = true;
    public boolean isShowPlay = false;

    public PlayPauseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        mBackgroundColor = getResources().getColor(R.color.blue);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mDrawable = new PlayPauseDrawable(context);
        mDrawable.setCallback(this);

        mPauseBackgroundColor = getResources().getColor(R.color.blue);
        mPlayBackgroundColor = getResources().getColor(R.color.pink);
    }
/*
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int size = Math.min(getMeasuredWidth(), getMeasuredHeight());
        setMeasuredDimension(size, size);
    }
*/ 
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
	@Override
    protected void onSizeChanged(final int w, final int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mDrawable.setBounds(0, 0, w, h);
        mWidth = w;
        mHeight = h;

        
        if (needShadow && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0,0, view.getWidth(),view.getHeight());
                }
            });
            setClipToOutline(true);
        }
        
    }
   

    private void setColor(int color) {
        mBackgroundColor = color;
        invalidate();
    }


    private int getColor() {
        return mBackgroundColor;
    }
    
    public boolean isPlay(){
    	return mDrawable.isPlay();
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == mDrawable || super.verifyDrawable(who);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(mBackgroundColor);
        final float radius = Math.min(mWidth, mHeight) / 2f;
        canvas.drawCircle(mWidth / 2f, mHeight / 2f, radius, mPaint);
        mDrawable.draw(canvas);
    }

    public void toggle() {
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
        }

        mAnimatorSet = new AnimatorSet();
        final boolean isPlay = mDrawable.isPlay();
        final ObjectAnimator colorAnim = ObjectAnimator.ofInt(this, COLOR, isPlay ? mPauseBackgroundColor : mPlayBackgroundColor);
        colorAnim.setEvaluator(new ArgbEvaluator());
        final Animator pausePlayAnim = mDrawable.getPausePlayAnimator();
        mAnimatorSet.setInterpolator(new DecelerateInterpolator());
        mAnimatorSet.setDuration(PLAY_PAUSE_ANIMATION_DURATION);
        mAnimatorSet.playTogether(colorAnim, pausePlayAnim);
        mAnimatorSet.start();
        isShowPlay = !isShowPlay;
    }

    public void setPlayBackgroundColor(int color){
            mPlayBackgroundColor = color;
    }

    public void setPauseBackgroundColor(int color){
            mPauseBackgroundColor = color;
    }

    public void setBackgroundColor(int color){
        mBackgroundColor= color;
    }
    public void setNeedShadow(boolean bool){

        needShadow=bool;
    }
}