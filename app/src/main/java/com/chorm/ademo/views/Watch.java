package com.chorm.ademo.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.chorm.ademo.tools.Logger;

public class Watch extends View {

    private static final String TAG = "Watch";

    private Shape mShape;

    /**The point of the view's center.*/
    private Point centerOfPoint;

    public Watch(Context context, AttributeSet attrs) {
        super(context, attrs);
        Logger.debug(TAG, "new Watch(Context,attrs)");
        mShape = new Shape();
        centerOfPoint = new Point();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //set background transparent.
        canvas.drawColor(Color.alpha(0));
        //1. out border.
        mShape.drawOutBorder(canvas);

        //2. bottle layer.

        //3. logo.

        //4. date.

        //5. indicator.

        //6.

        //7. glass cover.

    }

    private class Shape {

        Shape(){
            Logger.debug(TAG, "new Shape()");
        }

        void drawOutBorder(Canvas canvas){
            Logger.debug(TAG, "draw out border...");
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.YELLOW);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);

            canvas.drawCircle(, ,, paint);
        }

    }

}
