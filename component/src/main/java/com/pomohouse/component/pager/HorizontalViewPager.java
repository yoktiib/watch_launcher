package com.pomohouse.component.pager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;

/**
 * Created by sirawit on 9/4/16 AD.
 */
public class HorizontalViewPager extends ViewPager {

    private boolean pagingEnabled = true;

    public HorizontalViewPager(Context context) {
        super(context);
    }

    public HorizontalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* constructors omitted */

    public void setPagingEnabled(boolean enabled) {
        pagingEnabled = enabled;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // do not intercept
        return pagingEnabled && super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // do not consume
        return pagingEnabled && super.onTouchEvent(event);
    }
}
