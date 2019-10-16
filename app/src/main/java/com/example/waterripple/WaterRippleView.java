package com.example.waterripple;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;


public class WaterRippleView extends View {

    private Paint centerPaint;
    private int centerRadius = 100;
    private Paint spreadPaint;
    private float centerX;
    private float centerY;
    private int distance = 5;
    private  int maxRadius = 80;
    private int delayMilliseconds = 30;
    private List<Integer> radiusList = new ArrayList<>();
    private List<Integer> alphaList = new ArrayList<>();

    public WaterRippleView(Context context) {
        this(context,null);
    }

    public WaterRippleView(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public WaterRippleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.WaterRippleView,defStyleAttr,0);
        centerRadius = typedArray.getInteger(R.styleable.WaterRippleView_water_ripple_radius,centerRadius);
        maxRadius = typedArray.getInteger(R.styleable.WaterRippleView_water_ripple_spread_max,maxRadius);
        int centerColor = typedArray.getColor(R.styleable.WaterRippleView_water_ripple_center_color,
                ContextCompat.getColor(context,android.R.color.holo_red_dark));
        int spreadColor = typedArray.getColor(R.styleable.WaterRippleView_water_ripple_spread_color,
                ContextCompat.getColor(context,android.R.color.holo_red_light));
        distance = typedArray.getInteger(R.styleable.WaterRippleView_water_ripple_spread_distance,distance);
        delayMilliseconds = typedArray.getInteger(R.styleable.WaterRippleView_water_ripple_spread_delay,delayMilliseconds);
        typedArray.recycle();

        centerPaint = new Paint();
        centerPaint.setColor(centerColor);
        //抗锯齿
        centerPaint.setAntiAlias(true);

        //第一个为不透明
        alphaList.add(255);
        radiusList.add(0);

        spreadPaint = new Paint();
        spreadPaint.setAntiAlias(true);
        //设置填充样式
        spreadPaint.setStyle(Paint.Style.STROKE);
        spreadPaint.setAlpha(255);
        spreadPaint.setColor(spreadColor);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        centerX = w/2;
        centerY = h/2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(centerX,centerY,centerRadius,centerPaint);

        for (int i = 0 ;i<radiusList.size();i++){
            int alpha = alphaList.get(i);
            int width = radiusList.get(i);
            //画外部圆
            canvas.drawCircle(centerX,centerY,centerRadius+width,spreadPaint);

            if (alpha > 0 && width < 300){
                alpha = alpha - distance > 0 ? alpha - distance :1;
                alphaList.set(i,alpha);
                radiusList.set(i,width+distance);
            }
        }

        //适当时机添加一个新的圈圈
        if (radiusList.get(radiusList.size() - 1) > maxRadius){
            radiusList.add(0);
            alphaList.add(255);
        }

        //适当时机 删除第一个圈圈 收支平衡
        if (radiusList.size() >= 6){
            radiusList.remove(0);
            alphaList.remove(0);
        }

        postInvalidateDelayed(delayMilliseconds);

    }
}
