package com.chorm.ademo.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class Watch extends View {

    private static final String TAG = "Watch";

    public Watch(Context context) {
        super(context);

    }

    public Watch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //set background transparent.
        canvas.drawColor(Color.alpha(0));
        //1. out border.

        //2. bottle layer.

        //3. logo.

        //4. date.

        //5. indicator.

        //6.

        //7. glass cover.

    }
}
