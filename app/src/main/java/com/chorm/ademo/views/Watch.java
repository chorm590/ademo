package com.chorm.ademo.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.chorm.ademo.tools.Logger;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class Watch extends View {

    private static final String TAG = "Watch";

    /**第一次执行完onDraw方法后会被置为true*/
    private boolean isInitialized;

    private Shape mShape;
    private ColorManager mColorManager;
    private SizeManager mSizeManager;
    private Mathematics mMathematics;
    private Clock mClock;

    /**The point of the view's center.*/
    private Point centerOfPoint;
    /**The 'right-down point' of this view.*/
    private Point rdPoint;

    enum TRIANGLE_POINT{
        POINT_A, //等腰三角形的底边一个点。
        POINT_B, //等腰三角形的顶点。
        POINT_C  //等腰三角形底边的另一个点。
    };

    public Watch(Context context, AttributeSet attrs) {

        super(context, attrs);
        Logger.debug(TAG, "new Watch(Context,attrs)");
        mShape = new Shape();
        mColorManager = new ColorManager();
        centerOfPoint = new Point();
        mSizeManager = new SizeManager();
        mMathematics = new Mathematics();
        mClock = new Clock();
        rdPoint = new Point();

        //set pointer color
        setPointerColor(0xfff0e68c, 0xfff0e68c, 0xfff0e68c);
        setCoverGlassColor(-1);
    }

    /**
     * 设置时分秒针的颜色。
     * */
    private void setPointerColor(int hourp, int minutep, int secondp){
        if(hourp == 0)
            mColorManager.colorIntPointerHour = Color.argb(0xff, 0xff, 0xff, 0xff);
        else
            mColorManager.colorIntPointerHour = hourp;

        if(minutep == 0)
            mColorManager.colorIntPointerMinute = Color.argb(0xff, 0xff, 0xff, 0xff);
        else
            mColorManager.colorIntPointerMinute = minutep;

        if(secondp == 0)
            mColorManager.colorIntPointerSecond = Color.argb(0xff, 0xff, 0xff, 0xff);
        else
            mColorManager.colorIntPointerSecond = secondp;
    }

    /**
     * 设置表层玻璃镜面的颜色。
     * */
    private void setCoverGlassColor(int color){
        if(color < 0)
            mColorManager.colorIntCoverGlass = Color.argb(0x0d, 0x00, 0x00, 0xff);
        else
            mColorManager.colorIntCoverGlass = color;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        //Manual destroy inner class handler.
        for(String key : mClock.whatMap.keySet()){
            mClock.mHandler.removeMessages(mClock.whatMap.get(key));
        }
    }

    // ------------------------------------------------------------------------------------------------------------------------------

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

        //3. logo.
        mShape.drawLogo(canvas);

        //4. date.
        mShape.drawDate(canvas);

        //5. indicator.
        mShape.drawScale(canvas); //尝试绘制点。

        //6. pointer
        mShape.drawPointer(canvas);

        //7. draw center point.
        mShape.drawCenterPoint(canvas);

        //8. glass cover.
        mShape.drawCoverGlass(canvas);

        if(!isInitialized)
            isInitialized = true; //Clock.Handler will keep run.
    }

    private void countCenter() {
        int width = getWidth();
        int height = getHeight();

//        Logger.debug(TAG, "width:" + width + ",height:" + height);
        centerOfPoint.set(width / 2, height / 2);
        rdPoint.set(width, height);
    }

    public void refresh(){
        mShape.secondAngle -= 6;
        if(mShape.secondAngle <= 0)
            mShape.secondAngle = 360;
    }

    /**
     * 手表的外观部分。
     * */
    private class Shape {

        float outBorderRadius;
        float outBorderRadius2;
        float bottleLayerOuterRadius;
        float bottleLayerInnerRadius;
        float logoDownTextY;

        int hourAngle; //给时针用的角度
        float minuteAngle; //给分针用的角度。
        float secondAngle; //给秒针用的角度。

        Shape(){
            Logger.debug(TAG, "new Shape()");
            hourAngle = 330;
            minuteAngle = 60;
            secondAngle = 10;
        }

        void drawZeroPoint(Canvas canvas){
//            Logger.debug(TAG, "draw zero-point,x:0,y:0");
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            canvas.drawCircle(0, 0, 7, paint);
        }

        void drawCenterPoint(Canvas canvas){
//            Logger.debug(TAG, "draw center-point,x:" + centerOfPoint.x + ",y:" + centerOfPoint.y);
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
            canvas.drawRect(0, 0, rdPoint.x, rdPoint.y, paint);
        }

        /**
         * 最外层的边框。
         * */
        void drawOutBorder(Canvas canvas){
//            Logger.debug(TAG, ">>> draw out border <<<");
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
            paint.setColor(Color.argb(0xff, 0xe8, 0xe8, 0xe8)); //I need lightly gray...

            bottleLayerOuterRadius = outBorderRadius2 - 9;
//            Logger.debug(TAG, "bottle layer outer radius:" + bottleLayerOuterRadius);
            canvas.drawCircle(centerOfPoint.x, centerOfPoint.y, bottleLayerOuterRadius, paint);
        }

        /**
         * 手表底盘内圆部分。
         * */
        void drawBottleLayerInner(Canvas canvas){
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.argb(0xff, 0xfa, 0xfa, 0xf0)); //要磨砂的！！！
            bottleLayerInnerRadius = (float) (bottleLayerOuterRadius - 7.5);
//            Logger.debug(TAG, "Bottle layer inner radius:" + bottleLayerInnerRadius);
            canvas.drawCircle(centerOfPoint.x, centerOfPoint.y, bottleLayerInnerRadius, paint);
        }

        /**
         * 刻度
         * */
        void drawScale(Canvas canvas){
//            Logger.debug(TAG, ">>> drawScale <<<");
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
            Mathematics math = new Mathematics();
            PointF pos;
//            Logger.debug(TAG, "scale radius:"+ Watch.this.mShape.bottleLayerOuterRadius);
            for(int i = 1/*Must begin with 1.*/; i < 13; i++){
//                Logger.debug(TAG, "drawing small scale.");
                for(int j = 1; j < 5; j++){
                    pos = math.calPointInSmallDivider(i, j);
                    canvas.drawCircle(pos.x, pos.y, 2, normalPaint);
                }
                pos = math.calPointInDivider(i);
//                Logger.debug(TAG, "draw x:" + pos.x + ",draw y:" + pos.y);
                canvas.drawCircle(pos.x, pos.y, 5, dividePaint);
            }
        }

        /**
         * 上下两个LOGO字样。
         * */
        void drawLogo(Canvas canvas){
//            Logger.debug(TAG, ">>> drawLogo <<<");
            //上方的文字。
            //内底盘高度的0.618位置。
            float x = centerOfPoint.x;
            float y = Watch.this.mShape.bottleLayerInnerRadius * 0.618f;
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setAntiAlias(true);
            paint.setTextSize(36);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            canvas.drawText("ROSSINI", x, y, paint);

            //下方的文字
            logoDownTextY = Watch.this.mShape.bottleLayerInnerRadius * 1.7f;
            Paint downTextPaint = new Paint();
            downTextPaint.setAntiAlias(true);
            downTextPaint.setColor(Color.BLACK);
            downTextPaint.setTextSize(20);
            downTextPaint.setTextAlign(Paint.Align.CENTER);

            canvas.drawText("SAPPHIRE", x, logoDownTextY, downTextPaint);
        }

        /**
         * 日期显示窗口
         * */
        void drawDate(Canvas canvas){
//            Logger.debug(TAG, ">>> drawDate <<<");
            //金色的矩形外框。
            Paint rectPaint = new Paint();
            rectPaint.setColor(Color.argb(0xff, 0xf0, 0xe6, 0x8c));
            rectPaint.setAntiAlias(true);
            rectPaint.setStyle(Paint.Style.STROKE);
            rectPaint.setStrokeWidth(7);

            float left = centerOfPoint.x - 30;
            float top = logoDownTextY + 30;
            float right = centerOfPoint.x + 30;
            float bottom = top + 70;
            canvas.drawRect(left, top, right, bottom, rectPaint);

            //白色的日期显示背景。
            Paint bgPaint = new Paint();
            bgPaint.setColor(Color.WHITE); //最好搞个白色磨砂的。
            bgPaint.setAntiAlias(true);

            canvas.drawRect(left + 3.5f, top + 3.5f, right - 3.5f, bottom - 3.5f, bgPaint);

            //日期数字。
            Paint datePaint = new Paint();
            datePaint.setAntiAlias(true);
            datePaint.setTextSize(40);
            datePaint.setTextAlign(Paint.Align.CENTER);

            Paint.FontMetrics fm = datePaint.getFontMetrics();

            canvas.drawText(String.valueOf(mClock.mTime.iday),
                    left + (right - left) / 2,
                    top + (bottom - top) / 2 - (fm.top + fm.bottom) / 2,
                    datePaint);
        }

        /**
         * 时、分及秒针。
         * */
        void drawPointer(Canvas canvas){
//            Logger.debug(TAG, ">>> drawPointer <<<");
            //hours
            Paint hourPaint = new Paint();
            hourPaint.setColor(mColorManager.colorIntPointerHour);
            hourPaint.setAntiAlias(true);

            //calculate the triangle point.
            Mathematics math = new Mathematics();
            PointF a = math.calHourTriangle(TRIANGLE_POINT.POINT_A, mSizeManager.hourPointerAngle);
            Path hourPointerPath = new Path();
            hourPointerPath.moveTo(a.x, a.y);
            a = math.calHourTriangle(TRIANGLE_POINT.POINT_B, mSizeManager.hourPointerAngle);
            hourPointerPath.lineTo(a.x, a.y);
            a = math.calHourTriangle(TRIANGLE_POINT.POINT_C, mSizeManager.hourPointerAngle);
            hourPointerPath.lineTo(a.x, a.y);
            hourPointerPath.close();
            canvas.drawPath(hourPointerPath, hourPaint);

            //minutes
            Paint minutePaint = new Paint();
            minutePaint.setColor(mColorManager.colorIntPointerMinute);
            minutePaint.setAntiAlias(true);

            //Calculate the minute tirangle point.
            Path minutePath = new Path();
            a = math.calMinuteTriangle(TRIANGLE_POINT.POINT_A, mSizeManager.minutePointerAngle);
            minutePath.moveTo(a.x, a.y);
            a = math.calMinuteTriangle(TRIANGLE_POINT.POINT_B, mSizeManager.minutePointerAngle);
            minutePath.lineTo(a.x, a.y);
            a = math.calMinuteTriangle(TRIANGLE_POINT.POINT_C, mSizeManager.minutePointerAngle);
            minutePath.lineTo(a.x, a.y);
            minutePath.close();
            canvas.drawPath(minutePath, minutePaint);

            //seconds
            Paint secondPaint = new Paint();
            secondPaint.setColor(mColorManager.colorIntPointerSecond);
            secondPaint.setAntiAlias(true);

            //Calculate.
//            Logger.debug(TAG, "second angle:" + secondAngle);
            Path secondPath = new Path();
            a = math.calSecondTriangle(TRIANGLE_POINT.POINT_A, mSizeManager.secondPointerAngle);
            secondPath.moveTo(a.x, a.y);
            a = math.calSecondTriangle(TRIANGLE_POINT.POINT_B, mSizeManager.secondPointerAngle);
            secondPath.lineTo(a.x, a.y);
            a = math.calSecondTriangle(TRIANGLE_POINT.POINT_C, mSizeManager.secondPointerAngle);
            secondPath.lineTo(a.x, a.y);
            secondPath.close();
            canvas.drawPath(secondPath, secondPaint);
        }

        /**
         * 绘制手表最外层玻璃。
         * */
        void drawCoverGlass(Canvas canvas){
            Paint coverGlassPaint = new Paint();
            coverGlassPaint.setColor(mColorManager.colorIntCoverGlass);
            coverGlassPaint.setAntiAlias(true);
            float r = outBorderRadius2;
            canvas.drawCircle(centerOfPoint.x, centerOfPoint.y, r, coverGlassPaint);
        }
    }

    /**
     * 一站式色彩管理方案提供商。
     * */
    private class ColorManager{

        int colorIntPointerHour;
        int colorIntPointerMinute;
        int colorIntPointerSecond;
        int colorIntCoverGlass;

    }

    /**
     * 提供各种数学运算服务。
     * */
    private class Mathematics{

        //将圆分割成12等分，每个等分占的度数。
        final float DEGRESS_IN_DIVIDER = 360 / 12; // 30

        //分的刻度度数。
        final float DEGREE_IN_MINUTE_DIVIDER = 30 / 5; //6

        /**
         * Calculate the twelve points' coordinate on scale.
         * */
        PointF calPointInDivider(int idx){
//            Logger.debug(TAG, "calPointInDivider:>>>> " + idx + " <<<<");
            if(idx > 12){
                throw new IllegalArgumentException("A circle can't be divide more than twelve parts");
            }
            PointF point = new PointF();
            double radians = getRadianInAngle(DEGRESS_IN_DIVIDER * idx);
            //get x length.
            double x = Watch.this.mShape.bottleLayerOuterRadius * Math.sin(radians);
            //get y length.
            double y = Watch.this.mShape.bottleLayerOuterRadius * Math.cos(radians) * -1;
//            Logger.debug(TAG, "cal x:" + x + ",y:" + y);
            point.set(centerOfPoint.x, centerOfPoint.y);
            point.offset(Float.valueOf(String.format(Locale.CHINA, "%.3f", x)),
                    Float.valueOf(String.format(Locale.CHINA, "%.3f", y)));

            return point;
        }

        /**
         * 计算小分钟的刻度坐标。
         * */
        PointF calPointInSmallDivider(int majorIdx, int curIdx){
//            Logger.debug(TAG, "calculate small scale,majorIdx:" + majorIdx + ",curIdx:" + curIdx);
            double radians = getRadianInAngle(curIdx * DEGREE_IN_MINUTE_DIVIDER +((majorIdx - 1) * DEGRESS_IN_DIVIDER));
//            Logger.debug(TAG, "radians:" + radians);
            double x = Watch.this.mShape.bottleLayerOuterRadius * Math.sin(radians);
            double y = Watch.this.mShape.bottleLayerOuterRadius * Math.cos(radians) * -1;
//            Logger.debug(TAG, "cal x:" + x + ",y:" + y);

            PointF pointF = new PointF();
            pointF.set(centerOfPoint.x, centerOfPoint.y);
            pointF.offset(Float.valueOf(String.format(Locale.CHINA, "%.3f", x)),
                    Float.valueOf(String.format(Locale.CHINA, "%.3f", y)));

            return pointF;
        }

        /**
         * 计算时针三角形的坐标。
         * @param point ahhhhhh....
         * */
        PointF calHourTriangle(TRIANGLE_POINT point, float angle){
            PointF pointF = new PointF(centerOfPoint.x, centerOfPoint.y);
            int length;
            double radians;
            if(point == TRIANGLE_POINT.POINT_A){
                radians = getRadianInAngle(angle);
                length = mSizeManager.hourPointerWidth;
            }else if(point == TRIANGLE_POINT.POINT_B){
                // center point of circle.
                length = mSizeManager.hourPointerLength;
                radians = getRadianInAngle(angle + (180 - mSizeManager.hourPointerBackTriangleAngle / 2));
            }else if(point == TRIANGLE_POINT.POINT_C){
                radians = getRadianInAngle(angle - mSizeManager.hourPointerBackTriangleAngle);
                length = mSizeManager.hourPointerWidth;
            }else{
                length = 0;
                radians = 0;
                Logger.debug(TAG, "Calculate back triangle point error with:" + point);
            }

            String xs = String.format(Locale.CHINA, "%.3f", length * Math.sin(radians));
            String ys = String.format(Locale.CHINA, "%.3f", length * Math.cos(radians) * -1);
            pointF.offset(Float.valueOf(xs), Float.valueOf(ys));
//            Logger.debug(TAG, point + "(" + pointF.x + "," + pointF.y + ")");
            return pointF;
        }

        /**
         * 计算分针三角形坐标。
         * */
        PointF calMinuteTriangle(TRIANGLE_POINT point, float angle){
            PointF pointF = new PointF(centerOfPoint.x, centerOfPoint.y);
            double radians;
            float length;
            if(point == TRIANGLE_POINT.POINT_A){
                radians = getRadianInAngle(angle);
                length = mSizeManager.minutePointerWidth;
            }else if(point == TRIANGLE_POINT.POINT_B){
                radians = getRadianInAngle(angle + (180 - mSizeManager.minutePointerBackTriangleAngle / 2));
                length = mSizeManager.minutePointerLength;
            }else if(point == TRIANGLE_POINT.POINT_C){
                radians = getRadianInAngle(angle - mSizeManager.minutePointerBackTriangleAngle);
                length = mSizeManager.minutePointerWidth;
            }else{
                radians = 0;
                length = 0;
                Logger.debug(TAG, "calculate minute point error with:" + point);
            }

            String xs = String.format(Locale.CHINA, "%.3f", length * Math.sin(radians));
            String ys = String.format(Locale.CHINA, "%.3f", length * Math.cos(radians) * -1);
            pointF.offset(Float.valueOf(xs), Float.valueOf(ys));
//            Logger.debug(TAG, point + "(" + pointF.x + "," + pointF.y + ")");

            return pointF;
        }

        /**
         * 计算秒针三角形坐标。
         * */
        PointF calSecondTriangle(TRIANGLE_POINT point, float angle){
            PointF pointF = new PointF();
            pointF.set(centerOfPoint.x, centerOfPoint.y);
            double radians;
            float length;
            if(point == TRIANGLE_POINT.POINT_A){
                radians = getRadianInAngle(angle);
                length = mSizeManager.secondPointerWidth;
            }else if(point == TRIANGLE_POINT.POINT_B){
                radians = getRadianInAngle(angle + (180 - mSizeManager.secondPointerBackTriangleAngle / 2));
                length = mSizeManager.secondPointerLength;
            }else if(point == TRIANGLE_POINT.POINT_C){
                radians = getRadianInAngle(angle - mSizeManager.secondPointerBackTriangleAngle);
                length = mSizeManager.secondPointerWidth;
            }else{
                radians = 0;
                length = 0;
                Logger.debug(TAG, "calculate minute point error with:" + point);
            }

            String xs = String.format(Locale.CHINA, "%.3f", length * Math.sin(radians));
            String ys = String.format(Locale.CHINA, "%.3f", length * Math.cos(radians) * -1);
            pointF.offset(Float.valueOf(xs), Float.valueOf(ys));
//            Logger.debug(TAG, point + "(" + pointF.x + "," + pointF.y + ")");

            return pointF;
        }

        /**
         * 角度转化成弧度。
         * */
        private double getRadianInAngle(float v) {
//            Logger.debug(TAG, "getRadianInAngle:" + v);
            return v*Math.PI/180;
        }
    }


    /**
     * 统一尺寸管理。
     * 本View中所有尺寸均在这里注册。
     * 同时提供尺寸转换服务。
     * */
    private class SizeManager {

        private final String TAG = "Watch.SizeManager";

        //日期部分的尺寸。
        //秒针的后方三角夹角
        float secondPointerBackTriangleAngle = 20; //20 degree
        float minutePointerBackTriangleAngle = 90;
        float hourPointerBackTriangleAngle = 90;
        //为了让指针能正确的指到0度等度数点上，要作一个校正操作。
        float hourPointerAdjustAngle = 180 - hourPointerBackTriangleAngle / 2;
        float minutePointerAdjustAngle = 180 - minutePointerBackTriangleAngle / 2;
        float secondPointerAdjustAngle = 180 - secondPointerBackTriangleAngle / 2;
        //
        float secondPointerAngle;
        float minutePointerAngle;
        float hourPointerAngle;
        //
        final int secondPointerLength = 288;
        final int secondPointerWidth = 50;
        final int minutePointerLength = 248;
        final int minutePointerWidth = 20;
        final int hourPointerLength = 180;
        final int hourPointerWidth = 30;
    }

    /**
     * 统一管理时钟与日期功能。
     */
    private class Clock {

        private final String TAG = "Watch.Clock";

        int daysOfCurrentMonth;

        Map<String, Integer> whatMap = new HashMap<>();
        Time mTime = new Time();

        Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == whatMap.get("REFRESH_TIME")){
                    timeFlowing();
                    applyToView();
                    this.sendEmptyMessageDelayed(whatMap.get("REFRESH_TIME"), 1000/*pray*/);
                }else if(msg.what == whatMap.get("WAIT_FOR_INITIALIZED")){
                    if(isInitialized){
                        Logger.debug(TAG, "Watch View initialized,begin to clocking...");
                        this.removeMessages(whatMap.get("WAIT_FOR_INITIALIZED"));
                        reset();
                        this.sendEmptyMessageDelayed(whatMap.get("REFRESH_TIME"), 990/*I guess*/);
                    }else{
                        this.sendEmptyMessageDelayed(whatMap.get("WAIT_FOR_INITIALIZED"), 128);
                    }
                }
            }
        };

        Clock(){
            Logger.debug(TAG, "new Clock()");
            whatMap.put("REFRESH_TIME", 0);
            whatMap.put("WAIT_FOR_INITIALIZED", 1);

            mHandler.sendEmptyMessage(whatMap.get("WAIT_FOR_INITIALIZED"));
        }

        private void timeFlowing() {
            mTime.isecond++;
            if(mTime.isecond >= 60){
                mTime.isecond = 0;
                mTime.iminute++;
                if(mTime.iminute >= 60){
                    mTime.iminute = 0;
                    mTime.ihour++;
                    if(mTime.ihour >= 24){
                        mTime.ihour = 0;
                        mTime.iday++;
                        if(mTime.iday > daysOfCurrentMonth){
                            mTime.iday = 1;
                            mTime.imonth++;
                            if(mTime.imonth > 12){
                                mTime.imonth = 1;
                                mTime.iyear++;
                            }
                            makeSureDaysOfCurrentMonth();//Must do it.
                        }
                    }
                }
            }
            mTime.refreshDateString();
            mTime.printDate();
        }

        /**
         * 让各指针适应上当前系统时间。
         * */
        void reset(){
            getSystemTime();
            makeSureDaysOfCurrentMonth();
            mTime.printDate();
        }


        /**
         * 确定当前月有多少天。
         * */
        void makeSureDaysOfCurrentMonth(){
            switch (mTime.imonth){
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    daysOfCurrentMonth = 31;
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    daysOfCurrentMonth = 30;
                    break;
                case 2:
                    daysOfCurrentMonth = 28;
                    if(mTime.iyear % 4 == 0){
                        if(mTime.iyear % 100 > 0 || mTime.iyear % 400 == 0){
                            daysOfCurrentMonth++;
                        }
                    }
                    break;
                default:
                    Logger.error(TAG, "Calculate days of current month error...");
                    break;
            }
            Logger.debug(TAG, "daysOfCurrentMonth:" + daysOfCurrentMonth);
        }

        /**
         * 将时间更新到View中显示。
         * */
        void applyToView(){
            //set hour pointer...
            mSizeManager.hourPointerAngle = (mTime.ihour>11?mTime.ihour-12:mTime.ihour) * mMathematics.DEGRESS_IN_DIVIDER;
            mSizeManager.hourPointerAngle += (float)mTime.iminute / 60 * mMathematics.DEGRESS_IN_DIVIDER;
            mSizeManager.hourPointerAngle -= mSizeManager.hourPointerAdjustAngle;

            //set minute pointer...
            mSizeManager.minutePointerAngle = mTime.iminute * mMathematics.DEGREE_IN_MINUTE_DIVIDER;
            mSizeManager.minutePointerAngle += (float)mTime.isecond / 60 * mMathematics.DEGREE_IN_MINUTE_DIVIDER;
            mSizeManager.minutePointerAngle -= mSizeManager.minutePointerAdjustAngle;

            //set second pointer...
            mSizeManager.secondPointerAngle = (float)mTime.isecond * 6;
            mSizeManager.secondPointerAngle -= mSizeManager.secondPointerAdjustAngle;

//            Logger.debug(TAG, "hour:" + mTime.ihour + ",minute:" + mTime.iminute + ",second:" + mTime.isecond);
//            Logger.debug(TAG, "hour:" + mSizeManager.hourPointerAngle + ",minute:" + mSizeManager.minutePointerAngle
//                + ",second:" + mSizeManager.secondPointerAngle);

            invalidate();//refreshing...
        }

        void getSystemTime(){
            Calendar mCalendar = Calendar.getInstance();
            mCalendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
            mTime.iyear = mCalendar.get(Calendar.YEAR);
            mTime.imonth = mCalendar.get(Calendar.MONTH);
            mTime.iday = mCalendar.get(Calendar.DAY_OF_MONTH);
            mTime.ihour = mCalendar.get(Calendar.HOUR_OF_DAY);
            mTime.iminute = mCalendar.get(Calendar.MINUTE);
            mTime.isecond = mCalendar.get(Calendar.SECOND);
            mTime.refreshDateString();
        }

        /**
         * 保存当前时间值。
         * */
        class Time {

            final String TAG = "Watch.Clock.Time";

            int iyear;
            int imonth;
            int iday;
            int ihour;
            int iminute;
            int isecond;

            String dateLong;
            String dateShort;

            /**
             * 将整型类型的日期时间转换为字符串型。
             * */
            void refreshDateString(){
                StringBuilder sb = new StringBuilder();
                sb.append(iyear);
                sb.append("-");
                sb.append(imonth);
                sb.append("-");
                sb.append(iday);
                sb.append(" ");
                sb.append(ihour);
                sb.append(":");
                if(iminute < 10)
                    sb.append("0");
                sb.append(iminute);
                sb.append(":");
                if(isecond < 10)
                    sb.append("0");
                sb.append(isecond);

                try {
                    dateLong = sb.toString();
                    dateShort = sb.toString().split(" ")[1];
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            void printDate(){
                Logger.debug(TAG, mTime.dateLong + "," + mTime.dateShort);
            }
        }

    }

}
