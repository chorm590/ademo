package com.chorm.ademo.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import com.chorm.ademo.tools.Logger;

public class Watch extends View {

    private static final String TAG = "Watch";

    private Shape mShape;

    /**The point of the view's center.*/
    private Point centerOfPoint;
    /**The 'right-down point' of this view.*/
    private Point rdPoint;

    public Watch(Context context, AttributeSet attrs) {
        super(context, attrs);
        Logger.debug(TAG, "new Watch(Context,attrs)");
        mShape = new Shape();
        centerOfPoint = new Point();
        rdPoint = new Point();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //refresh view properties.
        countCenter();
        //set background transparent.
        canvas.drawColor(Color.alpha(0));

        mShape.drawViewBorderRect(canvas);
        mShape.drawZeroPoint(canvas);

        //1. out border.
        mShape.drawOutBorder(canvas);
        mShape.drawOutBorder2(canvas);

        //2. bottle layer.
        mShape.drawBottleLayerOuter(canvas);
        mShape.drawBottleLayerInner(canvas);
        mShape.drawScale(canvas); //尝试绘制点。
        mShape.drawCenterPoint(canvas);

        //3. logo.

        //4. date.

        //5. indicator.

        //6.

        //7. glass cover.

    }

    private void countCenter() {
        int width = getWidth();
        int height = getHeight();

        Logger.debug(TAG, "width:" + width + ",height:" + height);
        centerOfPoint.set(width / 2, height / 2);
        rdPoint.set(width, height);
    }

    /**
     * 手表的外观部分。
     * */
    private class Shape {

        float outBorderRadius;
        float outBorderRadius2;
        float bottleLayerOuterRadius;
        float bottleLayerInnerRadius;

        Shape(){
            Logger.debug(TAG, "new Shape()");
        }

        void drawZeroPoint(Canvas canvas){
            Logger.debug(TAG, "draw zero-point,x:0,y:0");
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            canvas.drawCircle(0, 0, 7, paint);
        }

        void drawCenterPoint(Canvas canvas){
            Logger.debug(TAG, "draw center-point,x:" + centerOfPoint.x + ",y:" + centerOfPoint.y);
            Paint paint = new Paint();
            paint.setColor(Color.argb(0xff, 0x8b, 0x5a, 0x2b));
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2.5f);

            canvas.drawCircle(centerOfPoint.x, centerOfPoint.y, 5.0f, paint);
        }

        /**
         * 整个视图的外边框。用于标示属于视图的范围。四方形的。
         * */
        void drawViewBorderRect(Canvas canvas){
            Paint paint = new Paint();
            paint.setColor(Color.GRAY);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            canvas.drawRect(1, 0, rdPoint.x, rdPoint.y, paint);
        }

        /**
         * 最外层的边框。
         * */
        void drawOutBorder(Canvas canvas){
            Logger.debug(TAG, "draw out border...");
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.argb(0xff, 0xf0, 0xe6, 0x8c));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(17);
            outBorderRadius = rdPoint.x < rdPoint.y ? rdPoint.x : rdPoint.y;
            outBorderRadius = (float) (outBorderRadius * 0.9 / 2);
            canvas.drawCircle(centerOfPoint.x, centerOfPoint.y, outBorderRadius, paint);
        }

        /**
         * 最外层边框与表底盘之间的过渡接缝。
         * */
        void drawOutBorder2(Canvas canvas){
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.argb(0xff, 0xff, 0xf6, 0x8f));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(3);
            outBorderRadius2 = outBorderRadius - 9;
            canvas.drawCircle(centerOfPoint.x, centerOfPoint.y, outBorderRadius2, paint);
        }

        /**
         * 手表底盘外圆部分。
         * */
        void drawBottleLayerOuter(Canvas canvas){
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(15);
            paint.setColor(Color.argb(0xff, 0xdb, 0xdb, 0xdb)); //I need lightly gray...

            bottleLayerOuterRadius = outBorderRadius2 - 9;
            Logger.debug(TAG, "bottle layer outer radius:" + bottleLayerOuterRadius);
            canvas.drawCircle(centerOfPoint.x, centerOfPoint.y, bottleLayerOuterRadius, paint);
        }

        /**
         * 手表底盘内圆部分。
         * */
        void drawBottleLayerInner(Canvas canvas){
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.argb(0xff, 0xfa, 0xfa, 0xd2)); //要磨砂的！！！
            bottleLayerInnerRadius = (float) (bottleLayerOuterRadius - 7.5);
            Logger.debug(TAG, "Bottle layer inner radius:" + bottleLayerInnerRadius);
            canvas.drawCircle(centerOfPoint.x, centerOfPoint.y, bottleLayerInnerRadius, paint);
        }

        /**
         * 刻度
         * */
        void drawScale(Canvas canvas){
            //普通刻度
            Paint normalPaint = new Paint();
            normalPaint.setColor(Color.BLACK);
            normalPaint.setAntiAlias(true);

            //整点时刻刻度
            Paint mainPaint = new Paint();
            mainPaint.setColor(Color.BLACK);

            Paint dividePaint = new Paint();
            dividePaint.setColor(Color.RED);
            dividePaint.setAntiAlias(true);
            Mathematic math = new Mathematic();
            PointF pos;
            Logger.debug(TAG, "scale radius:"+ Watch.this.mShape.bottleLayerOuterRadius);
            for(int i = 1/*Must begin with 1.*/; i < 13; i++){
                Logger.debug(TAG, "drawing small scale.");
                for(int j = 1; j < 5; j++){
                    pos = math.calPointInSmallDivider(i, j);
                    canvas.drawCircle(pos.x, pos.y, 2, normalPaint);
                }

                pos = math.calPointInDivider(i);
                Logger.debug(TAG, "draw x:" + pos.x + ",draw y:" + pos.y);
                canvas.drawCircle(pos.x, pos.y, 5, dividePaint);
            }

        }

    }

    /**
     * 一站式色彩管理方案提供商。
     * */
    private class ColorManager{

    }

    /**
     * 提供各种数学运算服务。
     * */
    private class Mathematic{

        //将圆分割成12等分，每个等分占的度数。
        final float DEGRESS_IN_DIVIDER = 360 / 12; // 30

        //分的刻度度数。
        final float DEGREE_IN_MINUTE_DIVIDER = 30 / 5; //6

        /**
         * Calculate the twelve points' coordinate on scale.
         * */
        PointF calPointInDivider(int idx){
            Logger.debug(TAG, "calPointInDivider:>>>> " + idx + " <<<<");
            if(idx > 12){
                throw new IllegalArgumentException("A circle can't be divide more than twelve parts");
            }
            PointF point = new PointF();
            double angle = getRadianInAngle(180 - DEGRESS_IN_DIVIDER * idx);
            //get x length.
            double x = Watch.this.mShape.bottleLayerOuterRadius * Math.sin(angle);
            //get y length.
            double y = Watch.this.mShape.bottleLayerOuterRadius * Math.cos(angle);
            Logger.debug(TAG, "cal x:" + x + ",y:" + y);
            point.set(centerOfPoint.x, centerOfPoint.y);
            point.offset(Float.valueOf(String.format("%.3f", x)), Float.valueOf(String.format("%.3f", y)));

            return point;
        }

        /**
         * 计算小分钟的刻度坐标。
         * */
        PointF calPointInSmallDivider(int majorIdx, int curIdx){
            Logger.debug(TAG, "calculate small scale,majorIdx:" + majorIdx + ",curIdx:" + curIdx);
            double radians = getRadianInAngle(curIdx * DEGREE_IN_MINUTE_DIVIDER +((majorIdx - 1) * DEGRESS_IN_DIVIDER));
            Logger.debug(TAG, "radians:" + radians);
            double x = Watch.this.mShape.bottleLayerOuterRadius * Math.sin(radians);
            double y = Watch.this.mShape.bottleLayerOuterRadius * Math.cos(radians);
            Logger.debug(TAG, "cal x:" + x + ",y:" + y);

            PointF pointF = new PointF();
            pointF.set(centerOfPoint.x, centerOfPoint.y);
            pointF.offset(Float.valueOf(String.format("%.3f", x)), Float.valueOf(String.format("%.3f", y)));

            return pointF;
        }

        private double getRadianInAngle(float v) {
            Logger.debug(TAG, "getRadianInAngle:" + v);
            return v*Math.PI/180;
        }
    }

}
