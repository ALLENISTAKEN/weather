package com.example.allen.weather;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * RainView
 * author: Allen
 * time: 2018/5/12 21:52
 */

public class RainView extends View {
    //    private static final int NUM_SNOWFLAKES = 150;
    private static final int NUM_RAINDROPS = 20;
    private static final int DELAY = 5;
//    private static final int DELAY =      20;

    private Raindrop[] raindrops;

    public RainView(Context context) {
        super(context);
    }

    public RainView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RainView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void resize(int width, int height) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        raindrops = new Raindrop[NUM_RAINDROPS];
        for (int i = 0; i < NUM_RAINDROPS; i++) {
            raindrops[i] = Raindrop.create(width, height, paint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw || h != oldh) {
            resize(w, h);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Raindrop Raindrop : raindrops) {
            Raindrop.draw(canvas);
        }
        getHandler().postDelayed(runnable, DELAY);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };
}
