package com.example.administrator.customizekeyboarddemo;

import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;

/**
 * 模拟点击屏幕、滑动屏幕等操作
 */
public class CustomTouchEvent {

    /**
     * 模拟向右滑动事件2
     *
     * @param distance 滑动的距离
     * @param view 传进去的活动对象
     */
    public static void setMoveToRight2(int distance, View view) {
        view.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                MotionEvent.ACTION_DOWN, 1, 1, 0));
        view.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                MotionEvent.ACTION_MOVE, 1 + distance, 1, 0));
        view.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                MotionEvent.ACTION_UP, 1 + distance, 1, 0));
    }
}
