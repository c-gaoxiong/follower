package com.example.gaoxiong.follower;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.orhanobut.logger.Logger;

/**
 * Created by gaoxiong on 2019/6/12.
 */

public class BigCircle extends View {

    //定义画笔
    Paint paint;
    public float x;
    public float y;

    public BigCircle(Context context) {
        super(context);
        x =context.getResources().getDisplayMetrics().widthPixels/2-30;
        y = context.getResources().getDisplayMetrics().heightPixels / 2;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint = new Paint();//实例化画笔对象
        paint.setColor(Color.RED);//设置画笔颜色
//        paint.setStyle(Paint.Style.FILL);//画笔属性是实心圆
        paint.setStyle(Paint.Style.STROKE);//画笔属性是空心圆
        paint.setStrokeWidth(8);//设置画笔粗细
        Logger.d("x;" + x + "y:" + y);
        /*四个参数：
                参数一：圆心的x坐标
                参数二：圆心的y坐标
                参数三：圆的半径
                参数四：定义好的画笔
                */
        canvas.drawCircle(x,y, 250, paint);

    }

}
