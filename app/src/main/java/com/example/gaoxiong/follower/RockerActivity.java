package com.example.gaoxiong.follower;

import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.orhanobut.logger.Logger;

public class RockerActivity extends AppCompatActivity {
    DisplayMetrics dm2;
    Vibrator vibrator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().hide();
        setContentView(R.layout.activity_rocker);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏 隐藏状态栏
         dm2 = getResources().getDisplayMetrics();
//        View view = getLayoutInflater().inflate(R.layout.activity_main, null);
        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.activity_rocker);
//        relativeLayout.measure(0,0);
//        int width = relativeLayout.getMeasuredWidth();
//        int height = relativeLayout.getMeasuredHeight();
//        Logger.d(">>>x>>>"+width+">>>y>>>"+height);
        init();
        final SmallCircle smallCircle = new SmallCircle(relativeLayout.getContext());

        smallCircle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){

                    vibrator.vibrate(60);
                    Logger.i( "control＝" + "a");
                    Intent intent0= new Intent(BleUUID.CHAIR_CONTROL);
                    intent0.putExtra("control","a");
                    sendBroadcast(intent0);
                }
                if(event.getAction()==MotionEvent.ACTION_UP){
                    vibrator.vibrate(60);
                    smallCircle.x=dm2.widthPixels/2-30;
                    smallCircle.y = dm2.heightPixels/2;
                    smallCircle.invalidate();
                    for(int i=0;i<5;i++){
                        Intent intent =new Intent(BleUUID.CHAIR_CONTROL);
                        intent.putExtra("control","s");
                        Logger.i( "control>>>＝" +i+ "s");
                        sendBroadcast(intent);
                    }
                }

                    smallCircle.x = event.getX();
                    smallCircle.y = event.getY();
                    float d = (float) Math.sqrt((Math.pow(smallCircle.x - dm2.widthPixels / 2 + 30, 2) + Math.pow(smallCircle.y - dm2.heightPixels / 2, 2)));
                    if (d <= 250) {
                        int y1 = (int) (smallCircle.y - dm2.heightPixels / 2 + 250) * 2 / 5 + 30;
                        int x1 = 30 + (250 - (int) (smallCircle.x - dm2.widthPixels / 2 + 30)) * 2 / 5;

                        String y2;
                        if (y1 >= 230) {
                            y2 = 230 + "";
                        } else if (y1 <= 30) {
                            y2 = "0" + "" + 30;
                        } else if (y1 >= 100) {
                            y2 = y1 + "";
                        } else {
                            y2 = "0" + "" + y1;
                        }
                        String x2;
                        if (x1 >= 230) {
                            x2 = 230 + "";
                        } else if (x1 <= 30) {
                            x2 = "0" + "" + 30;
                        } else if (x1 >= 100) {
                            x2 = x1 + "";
                        } else {
                            x2 = "0" + "" + x1;
                        }

                        String control = "#" + y2 + x2;
                        Logger.i("control＝" + control);
                        Intent intent0 = new Intent(BleUUID.CHAIR_CONTROL);
                        intent0.putExtra("control", control);
                        sendBroadcast(intent0);

                    } else {
                        smallCircle.x = (event.getX() - dm2.widthPixels / 2 + 30) * 250 / d + dm2.widthPixels / 2 - 30;
                        smallCircle.y = (event.getY() - dm2.heightPixels / 2) * 250 / d + dm2.heightPixels / 2;

                        int y1 = (int) (smallCircle.y - dm2.heightPixels / 2 + 250) * 2 / 5 + 30;
                        int x1 = 30 + (250 - (int) (smallCircle.x - dm2.widthPixels / 2 + 30)) * 2 / 5;

                        String y2;
                        if (y1 >= 230) {
                            y2 = 230 + "";
                        } else if (y1 <= 30) {
                            y2 = "0" + "" + 30;
                        } else if (y1 >= 100) {
                            y2 = y1 + "";
                        } else {
                            y2 = "0" + "" + y1;
                        }
                        String x2;
                        if (x1 >= 230) {
                            x2 = 230 + "";
                        } else if (x1 <= 30) {
                            x2 = "0" + "" + 30;
                        } else if (x1 >= 100) {
                            x2 = x1 + "";
                        } else {
                            x2 = "0" + "" + x1;
                        }
                        String control = "#" + y2 + x2;
                        Logger.i("control＝" + control);
                        Intent intent0 = new Intent(BleUUID.CHAIR_CONTROL);
                        intent0.putExtra("control", control);
                        sendBroadcast(intent0);

                    }
                if(event.getAction()==MotionEvent.ACTION_UP){
                    vibrator.vibrate(60);
                    smallCircle.x=dm2.widthPixels/2-30;
                    smallCircle.y = dm2.heightPixels/2;
                    smallCircle.invalidate();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for(int i=0;i<10;i++){
                                Intent intent =new Intent(BleUUID.CHAIR_CONTROL);
                                intent.putExtra("control","s");
                                Logger.i( "control>>>＝" +i+ "s");
                                sendBroadcast(intent);
                            }
                        }
                    });

                }
                    Logger.d(">>>x>>>"+smallCircle.x+">>>y>>>"+smallCircle.y);
                    smallCircle.invalidate();
                return true;
            }
        });
       final BigCircle bigCircle = new BigCircle(relativeLayout.getContext());
        relativeLayout.addView(smallCircle);
        relativeLayout.addView(bigCircle);

    }

    void  init(){
        vibrator=(Vibrator)getSystemService(VIBRATOR_SERVICE);


    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent intent0= new Intent(BleUUID.CHAIR_CONTROL);
        intent0.putExtra("control","c");
        sendBroadcast(intent0);
    }
}
