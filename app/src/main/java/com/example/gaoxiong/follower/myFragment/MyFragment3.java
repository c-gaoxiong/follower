package com.example.gaoxiong.follower.myFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.gaoxiong.follower.BleUUID;
import com.example.gaoxiong.follower.R;
import com.orhanobut.logger.Logger;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by Jay on 2015/8/28 0028.
 */
public class MyFragment3 extends Fragment  implements SensorEventListener {

    public MyFragment3() {
    }
    View view;
    Context context ;
    Button button4;
    Button button5;
    private boolean bool=false;
    private SensorManager sensorManager;
    public float[] r = new float[9];
    //记录通过getOrientation()计算出来的方位横滚俯仰值
    public float[] values = new float[3];
    public float[] gravity = null;
    public float[] geomagnetic = null;
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(gravity!=null && geomagnetic!=null){
                SensorManager.getRotationMatrix(r, null, gravity, geomagnetic);
                SensorManager.getOrientation(r, values);
                float degree = (float) Math.toDegrees(values[0]);
                Logger.i( "计算出来的方位角＝" + degree);
                float yAngle = (float) Math.toDegrees(values[1]);
                int y = (int) (195-(yAngle+40)*130/70);
                if(y>=195){
                    y=195;
                }else if(y<=65){
                    y=65;
                }
                String y1;
                if(y>=195){
                    y1 = 195+"";
                }else if(y<=65){
                    y1= "0"+""+65;
                    Logger.i( y1);
                }else if(y>=100){
                    y1 = y+"";

                }else {
                    y1= "0"+""+y;
                }
                Logger.i( String.format(getResources().getString(R.string.value),y));
                float zAngle = (float) Math.toDegrees(values[2]);
                int z = (int) (195-(zAngle+50)*130/100);
                String z1;
                if(z>=195){
                    z1 = 195+"";
                }else if(z<=65){
                    z1= "0"+""+65;
                    Logger.i( z1);
                }else if(z>=100){
                    z1 = z+"";
                }else {
                    z1= "0"+z;
                }
                String control = "#"+y1+""+z1;
                Logger.i( "control＝" + control);
                Intent intent0= new Intent(BleUUID.CHAIR_CONTROL);
                intent0.putExtra("control",control);
                context.getApplicationContext().sendBroadcast(intent0);
            }
        }
    };



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.gravity_control_layout,container,false);
        button4 = (Button)view.findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!bool){
                    Intent intent0= new Intent(BleUUID.CHAIR_CONTROL);
                    intent0.putExtra("control","a");
                    context.getApplicationContext().sendBroadcast(intent0);
                    bool = true;
                    button4.setText("停止");
                }else{
                    bool = false;
                    Intent intent0= new Intent(BleUUID.CHAIR_CONTROL);
                    intent0.putExtra("control","c");
                    context.getApplicationContext().sendBroadcast(intent0);
                    button4.setText("启动");
                }

            }
        });

        sensorManager = (SensorManager)context.getSystemService(SENSOR_SERVICE);
        Logger.e("第三个Fragment");

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        //注册加速度传感器监听
        Sensor acceleSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener( this, acceleSensor, SensorManager.SENSOR_DELAY_NORMAL);
        //注册磁场传感器监听
        Sensor magSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener( this, magSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener( this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // 真机上获取触发的传感器类型
        if(bool)
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER: //加速度传感器
                    gravity = event.values;

                    handler.sendEmptyMessage(0);
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD://磁场传感器
                    geomagnetic = event.values;
                    handler.sendEmptyMessage(0);
                    break;
            }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
