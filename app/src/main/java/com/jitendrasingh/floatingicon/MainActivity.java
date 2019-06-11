package com.jitendrasingh.floatingicon;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements
        View.OnTouchListener {

    private RelativeLayout parentLayout;
    private ImageView ivFloatingIcon;
    private int parentWidth = 0, parentHeight = 0;
    private int iconWidth = 0, iconHeight = 0;
    private int _xDelta;
    private int _yDelta;
    private long lastTouchDownTime;
    private boolean checkClick = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parentLayout = findViewById(R.id.parentLayout);
        ivFloatingIcon = findViewById(R.id.ivFloatingIcon);
        ivFloatingIcon.setOnTouchListener(this);

        ViewTreeObserver vto = parentLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                parentLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                parentWidth = parentLayout.getMeasuredWidth();
                parentHeight = parentLayout.getMeasuredHeight();
                iconWidth = ivFloatingIcon.getMeasuredWidth();
                iconHeight = ivFloatingIcon.getMeasuredHeight();
            }
        });
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                lastTouchDownTime = System.currentTimeMillis();
                checkClick = true;
                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                _xDelta = X - lParams.leftMargin;
                _yDelta = Y - lParams.topMargin;
                break;
            case MotionEvent.ACTION_UP:
                resetPosition();
                if (checkClick && System.currentTimeMillis() - lastTouchDownTime > 30) {
                    Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (System.currentTimeMillis() - lastTouchDownTime > 200) {
                    checkClick = false;
                    int left = X - _xDelta;
                    int top = Y - _yDelta;
                    if (left < 0
                            || top < 0
                            || left >= (parentWidth - iconWidth)
                            || top >= (parentHeight - iconHeight)) {
                        return true;
                    }
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    layoutParams.leftMargin = X - _xDelta;
                    layoutParams.topMargin = Y - _yDelta;
                    layoutParams.rightMargin = 0;
                    layoutParams.bottomMargin = 0;
                    parentLayout.updateViewLayout(ivFloatingIcon, layoutParams);
                }
                break;
        }
        // parentLayout.invalidate();
        return true;
    }

    public void resetPosition() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ivFloatingIcon.getLayoutParams();
        int left = ivFloatingIcon.getLeft();
        if (left > parentWidth / 2) {
            layoutParams.leftMargin = parentWidth - iconWidth;
        } else {
            layoutParams.leftMargin = 0;
        }
        layoutParams.topMargin = ivFloatingIcon.getTop();
        layoutParams.rightMargin = 0;
        layoutParams.bottomMargin = 0;
        parentLayout.updateViewLayout(ivFloatingIcon, layoutParams);
    }
}
