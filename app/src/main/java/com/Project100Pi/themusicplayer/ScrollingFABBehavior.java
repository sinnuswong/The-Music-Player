package com.Project100Pi.themusicplayer;


import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by BalachandranAR on 8/21/2015.
 */
    public class ScrollingFABBehavior extends FloatingActionButton.Behavior {
        // ...

    public ScrollingFABBehavior(Context context, AttributeSet attrs) {
        super();
    }
        @Override
        public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child,
                                   View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
            super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed,
                    dyUnconsumed);

            if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
                child.hide();
            } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
                child.show();
            }
        }

        // ...
    }

