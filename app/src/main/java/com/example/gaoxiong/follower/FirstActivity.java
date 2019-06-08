package com.example.gaoxiong.follower;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.orhanobut.logger.Logger;




public  class FirstActivity extends AppCompatActivity implements SensorEventListener, PermissionInterface{
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
    private boolean bool=false;
    ReceivedReceiver receivedReceiver;
    Button stopButton ;
    Button frontButton ;
    Button backButton ;
    Button leftButton ;
    Button rightButton ;
    Button startButton ;
    Button overButton;
    private PermissionHelper mPermissionHelper;
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
                    startButton.setText(String.format(getResources().getString(R.string.value),yAngle));
                    Logger.i( String.format(getResources().getString(R.string.value),yAngle));
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
                    overButton.setText("指令：" + control);
                    Intent intent0= new Intent(BleUUID.CHAIR_CONTROL);
                    intent0.putExtra("control",control);
                    sendBroadcast(intent0);

                }
            }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first);
        //初始化并发起权限申请
        mPermissionHelper = new PermissionHelper(this, this);
        mPermissionHelper.requestPermissions();
         stopButton = (Button)findViewById(R.id.stopButton);
         frontButton = (Button)findViewById(R.id.frontButton);
         backButton = (Button)findViewById(R.id.backButton);
         leftButton = (Button)findViewById(R.id.leftButton);
         rightButton = (Button)findViewById(R.id.rightButton);
         startButton = (Button)findViewById(R.id.startButton);
         overButton = (Button)findViewById(R.id.overButton);
        stopButton.setOnClickListener(clickListener );
        frontButton.setOnClickListener(clickListener );
        backButton.setOnClickListener(clickListener );
        leftButton.setOnClickListener(clickListener );
        rightButton.setOnClickListener(clickListener );
        startButton.setOnClickListener(clickListener );
        overButton.setOnClickListener(clickListener );
        Intent intent = new Intent(FirstActivity.this, BleService.class);
        startService(intent);
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
         receivedReceiver = new ReceivedReceiver();
        IntentFilter intent1 = new IntentFilter();
        intent1.addAction(BleUUID.RECEIVED);
        registerReceiver(receivedReceiver,intent1);
    }



    View.OnClickListener clickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.overButton :
                    String v1="c";
                    bool = false;
                    Intent intent0= new Intent(BleUUID.CHAIR_CONTROL);
                    Logger.e("c");
                    intent0.putExtra("control",v1);
                    sendBroadcast(intent0);
                    break;
                case R.id.startButton:
                    String a="a";
                    Logger.e("a");
                    bool = true;
                    Intent intent1 = new Intent(BleUUID.CHAIR_CONTROL);
                    intent1.putExtra("control",a);
                    sendBroadcast(intent1);
                    break;
                case R.id.stopButton:
                    String s="s";
                    Logger.e("s");
                    bool = false;
                    Intent intent2= new Intent(BleUUID.CHAIR_CONTROL);
                    intent2.putExtra("control",s);
                    sendBroadcast(intent2);
                    break;
                case R.id.leftButton:
                    String l="l";
                    Logger.e("l");
                    bool = false;
                    Intent intent3 = new Intent(BleUUID.CHAIR_CONTROL);
                    intent3.putExtra("control",l);
                    sendBroadcast(intent3);
                    break;
                case R.id.rightButton:
                    String r="r";
                    bool = false;
                    Intent intent4 = new Intent(BleUUID.CHAIR_CONTROL);
                    Logger.e("r");

                    intent4.putExtra("control",r);
                    sendBroadcast(intent4);
                    break;
                case R.id.frontButton:
                    String f="f";
                    bool = false;
                    Intent intent5 = new Intent(BleUUID.CHAIR_CONTROL);
                    Logger.e("f");
                    intent5.putExtra("control",f);
                    sendBroadcast(intent5);
                    break;
                case R.id.backButton:
                    String b="b";
                    bool = false;
                    Intent intent7 = new Intent(BleUUID.CHAIR_CONTROL);
                    Logger.e("b");
                    intent7.putExtra("control",b);
                    sendBroadcast(intent7);
//                    mBluetoothLeService.sendOrder(b);
                    break;
                default:break;
            }
        }

    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.first, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(FirstActivity.this,BleActivity.class);
            FirstActivity.this.startActivity(intent);
            // intent.putExtra("testIntent","123");
//            intent.setClass(FirstActivity.this,BleActivity.class);
//            FirstActivity.this.startActivity(intent);
        }
        if(id == R.id.action_connect){
            Intent intent = new Intent(FirstActivity.this,ConnectActivity.class);
            FirstActivity.this.startActivity(intent);
        }
        if(id==R.id.action_gravity){

        }
        if(id==R.id.action_quit){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //注册加速度传感器监听
        Sensor acceleSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener( this, acceleSensor, SensorManager.SENSOR_DELAY_NORMAL);
        //注册磁场传感器监听
        Sensor magSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener( this, magSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }


    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent(FirstActivity.this, BleService.class);
        stopService(intent);
        super.onDestroy();
        unregisterReceiver(receivedReceiver);
        sensorManager.unregisterListener( this);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mPermissionHelper.requestPermissionsResult(requestCode, permissions, grantResults)) {
            //权限请求结果，并已经处理了该回调
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    public int getPermissionsRequestCode() {
        return 1000;
    }

    @Override
    public String[] getPermissions() {
        //设置该界面所需的全部权限
        return new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
    }

    @Override
    public void requestPermissionsSuccess() {
//权限请求用户已经全部允许
        initViews();
    }

    @Override
    public void requestPermissionsFail() {
        //权限请求不被用户允许。可以提示并退出或者提示权限的用途并重新发起权限申请。
    }
    private void initViews() {
        //已经拥有所需权限，可以放心操作任何东西了
    }

    public class ReceivedReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        String ctr = intent.getStringExtra("data").trim();

        if(ctr!=null){
            if(ctr.length()<=7){
                Intent i = new Intent(BleUUID.CHAIR_CONTROL);
                i.putExtra("control",ctr);
                sendBroadcast(i);
            }

        }

    }
}

}
