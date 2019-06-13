package com.example.gaoxiong.follower.myFragment;

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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.gaoxiong.follower.BleService;
import com.example.gaoxiong.follower.BleUUID;
import com.example.gaoxiong.follower.R;
import com.example.gaoxiong.follower.RockerActivity;
import com.orhanobut.logger.LogAdapter;
import com.orhanobut.logger.Logger;

import static android.content.Context.SENSOR_SERVICE;
import static android.view.View.*;
import static com.example.gaoxiong.follower.myFragment.MyFragment1.mainActivity;

/**
 * Created by Jay on 2015/8/28 0028.
 */
public class MyFragment3 extends Fragment  implements SensorEventListener {

    public MyFragment3() {
    }
    View view;
    Context context ;
    Button stopButton ;
    Button frontButton ;
    Button backButton ;
    Button leftButton ;
    Button rightButton ;
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
//        button4.setOnTouchListener(clickListener);
        button4.setOnClickListener(clickListener.get());
        button5 = (Button)view.findViewById(R.id.button5);
        button5.setOnClickListener(clickListener.get());
        if(BleUUID.chair_state.equals("停止")){
            button5.setText("停止按钮控制");
            button5.setTextColor(context.getResources().getColor(R.color.color_Green));
        }

        stopButton = (Button)view.findViewById(R.id.stopButton);
        stopButton.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
               Intent intent =new Intent(context.getApplicationContext(), RockerActivity.class);
                startActivity(intent);
                return true;
            }
        });
        frontButton = (Button)view.findViewById(R.id.frontButton);
        backButton = (Button)view.findViewById(R.id.backButton);
        leftButton = (Button)view.findViewById(R.id.leftButton);
        rightButton = (Button)view.findViewById(R.id.rightButton);



        frontButton.setOnTouchListener(touchListener );
        backButton.setOnTouchListener(touchListener );
        leftButton.setOnTouchListener(touchListener );
        rightButton.setOnTouchListener(touchListener );

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
OnTouchListener touchListener = new OnTouchListener(){
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN){

            mainActivity. vibrator.vibrate(60);
            if(BleService.hashMap.get(BleUUID.CHAIR_ADDRESS)!=null) {
                if (BleService.hashMap.get(BleUUID.CHAIR_ADDRESS).getState() == 0) {
                    mainActivity.getViewPager().setCurrentItem(3);
                    Toast.makeText(context.getApplicationContext(),"请连接智能轮椅",Toast.LENGTH_SHORT).show();
                }
            }else {
                mainActivity.getViewPager().setCurrentItem(3);
                Toast.makeText(context.getApplicationContext(),"请连接智能轮椅",Toast.LENGTH_SHORT).show();
            }
            bool = false;
            Logger.d("触摸事件>>>>>>>按下");
            button5.setText(getString(R.string.stop_btn));
            button5.setTextColor(context.getResources().getColor(R.color.color_Green));
            BleUUID.chair_state = "停止";
            switch (v.getId()){
                case R.id.leftButton:
                    String l="l";
                    Logger.e("l");
                    Intent intent3 = new Intent(BleUUID.CHAIR_CONTROL);
                    intent3.putExtra("control",l);
                    context.getApplicationContext().sendBroadcast(intent3);
                    bool = false;
                    button4.setText(context.getString(R.string.start_up_gravity));
                    button4.setTextColor(context.getResources().getColor(R.color.text_yellow));
                    break;
                case R.id.rightButton:
                    String r="r";
                    Intent intent4 = new Intent(BleUUID.CHAIR_CONTROL);
                    Logger.e("r");
                    bool = false;
                    button4.setText(context.getString(R.string.start_up_gravity));
                    intent4.putExtra("control",r);
                    context.getApplicationContext().sendBroadcast(intent4);
                    button4.setTextColor(context.getResources().getColor(R.color.text_yellow));
                    break;
                case R.id.frontButton:
                    String f="f";
                    Intent intent5 = new Intent(BleUUID.CHAIR_CONTROL);
                    Logger.e("f");
                    intent5.putExtra("control",f);
                    context.getApplicationContext().sendBroadcast(intent5);
                    bool = false;
                    button4.setText(context.getString(R.string.start_up_gravity));
                    button4.setTextColor(context.getResources().getColor(R.color.text_yellow));
                    break;
                case R.id.backButton:
                    String b="b";
                    Intent intent7 = new Intent(BleUUID.CHAIR_CONTROL);
                    Logger.e("b");
                    intent7.putExtra("control",b);
                    context.getApplicationContext().sendBroadcast(intent7);
                    bool = false;
                    button4.setText(context.getString(R.string.start_up_gravity));
                    button4.setTextColor(context.getResources().getColor(R.color.text_yellow));
                    break;
                default:break;
            }
        }else if(event.getAction()==MotionEvent.ACTION_UP){
            mainActivity. vibrator.vibrate(60);
            for(int i=0;i<5;i++){
                Intent intent5 = new Intent(BleUUID.CHAIR_CONTROL);
                Logger.e("s");
                intent5.putExtra("control","s");
                context.getApplicationContext().sendBroadcast(intent5);
            }
            BleUUID.chair_state = "停止";
        }
        return true;
    }
};
    final ThreadLocal<OnClickListener> clickListener = new ThreadLocal<OnClickListener>() {
        @Override
        protected OnClickListener initialValue() {
            return new OnClickListener() {

                @Override
                public void onClick(View v) {
                    mainActivity.vibrator.vibrate(60);

                    if (BleService.hashMap.get(BleUUID.CHAIR_ADDRESS) != null) {
                        if (BleService.hashMap.get(BleUUID.CHAIR_ADDRESS).getState() == 2) {

                        } else {
                            mainActivity.getViewPager().setCurrentItem(3);
                            Toast.makeText(context.getApplicationContext(), "请连接智能轮椅", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        mainActivity.getViewPager().setCurrentItem(3);
                        Toast.makeText(context.getApplicationContext(), "请连接智能轮椅", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Logger.d("点击事件>>>>>>>点击");

                    switch (v.getId()) {

                        case R.id.button4:
                            if (!bool) {
                                Intent i1 = new Intent(BleUUID.BTN_CHANGE);
                                i1.putExtra("start", context.getResources().getString(R.string.stop_follower));
                                context.getApplicationContext().sendBroadcast(i1);

                                Intent intent0 = new Intent(BleUUID.CHAIR_CONTROL);
                                intent0.putExtra("control", "a");
                                context.getApplicationContext().sendBroadcast(intent0);

                                bool = true;
                                button4.setText(getString(R.string.stop_gravity));
                                button4.setTextColor(context.getResources().getColor(R.color.color_Green));
                                button5.setTextColor(context.getResources().getColor(R.color.color_Green));
                                BleUUID.chair_state = "停止";
                                button5.setText(getString(R.string.stop_btn));

                            } else {
                                bool = false;
                                Intent intent0 = new Intent(BleUUID.CHAIR_CONTROL);
                                intent0.putExtra("control", "s");
                                context.getApplicationContext().sendBroadcast(intent0);
                                button4.setText(getString(R.string.start_up_gravity));
                                button4.setTextColor(context.getResources().getColor(R.color.text_yellow));

                            }
                            break;
                        case R.id.button5:
                            if (button5.getText().equals(getString(R.string.start_up_btn))) {
                                Intent intent0 = new Intent(BleUUID.CHAIR_CONTROL);
                                intent0.putExtra("control", "a");
                                context.getApplicationContext().sendBroadcast(intent0);
                                button5.setText(getString(R.string.stop_btn));
                                button4.setText(context.getString(R.string.start_up_gravity));
                                button5.setTextColor(context.getResources().getColor(R.color.color_Green));
                                button4.setTextColor(context.getResources().getColor(R.color.text_yellow));
                                BleUUID.chair_state = "停止";
                            } else {
                                Intent intent0 = new Intent(BleUUID.CHAIR_CONTROL);
                                intent0.putExtra("control", "s");
                                context.getApplicationContext().sendBroadcast(intent0);
                                button5.setText(getString(R.string.start_up_btn));
                                button5.setTextColor(context.getResources().getColor(R.color.text_yellow));
                                BleUUID.chair_state = "启动";
                            }
                            bool = false;
                            button4.setText(context.getString(R.string.start_up_gravity));
                            button4.setTextColor(context.getResources().getColor(R.color.text_yellow));
                            break;
                        default:
                            break;
                    }
                }
            };
        }
    };
}
