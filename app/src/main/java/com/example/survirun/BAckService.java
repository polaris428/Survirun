package com.example.survirun;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.survirun.activity.MainActivity;

public class BAckService extends Service implements SensorEventListener {

    private static final String TAG = "서비스";
    SensorManager sensorManager;
    Sensor stepCountSensor;

    int currentSteps = 0;


    NotificationCompat.Builder NCBuilder;

    @Override
    public IBinder onBind(Intent intent) {
        // Service 객체와 화면 Activity 사이에서 데이터를 주고받을 때
        // 데이터를 전달할 필요가 없으면 return null;
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "서비스 시작");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PendingIntent mPendingIntent = PendingIntent.getActivity(
                    getApplicationContext(),
                    0, // 보통 default값 0을 삽입
                    new Intent(getApplicationContext(), MainActivity.class),
                    PendingIntent.FLAG_UPDATE_CURRENT
            );


            NotificationManager NoManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("test1", "Service", NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);     //채널에 게시 된 알림에 알림 표시 등을 표시
            channel.setLightColor(Color.RED);

            NoManager.createNotificationChannel(channel);
            NCBuilder = new NotificationCompat.Builder(this, "test1");
            NCBuilder.setSmallIcon(android.R.drawable.ic_menu_search);
            NCBuilder.setContentTitle("서비스 가동");
            NCBuilder.setContentText("서비스가 가동 중입니다" + currentSteps);
            NCBuilder.setOngoing(true);
            NCBuilder.setContentIntent(mPendingIntent);
            NCBuilder.setAutoCancel(true);


            Notification notification = NCBuilder.build();

            //현재 노티피케이션 메시지를 포그라운드 서비스의 메시지로 등록
            startForeground(10, notification);
            // 활동 퍼미션 체크
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {


            }
            // 걸음 센서 연결
            // * 옵션
            // - TYPE_STEP_DETECTOR:  리턴 값이 무조건 1, 앱이 종료되면 다시 0부터 시작
            // - TYPE_STEP_COUNTER : 앱 종료와 관계없이 계속 기존의 값을 가지고 있다가 1씩 증가한 값을 리턴
            //
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

            // 디바이스에 걸음 센서의 존재 여부 체크
            if (stepCountSensor == null) {
                Toast.makeText(this, "No Step Sensor", Toast.LENGTH_SHORT).show();
            }
            if (stepCountSensor != null) {
                // 센서 속도 설정
                // * 옵션
                // - SENSOR_DELAY_NORMAL: 20,000 초 딜레이
                // - SENSOR_DELAY_UI: 6,000 초 딜레이
                // - SENSOR_DELAY_GAME: 20,000 초 딜레이
                // - SENSOR_DELAY_FASTEST: 딜레이 없음
                //
                sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }

        return super.onStartCommand(intent, flags, startId);

    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "서비스 종료");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(5000);
                System.out.println("Thread");

                //완료되면 노티피케이션 메시지 사라지게
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    stopForeground(STOP_FOREGROUND_REMOVE);
                    NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    nm.cancel(10);
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        super.onDestroy();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {

            if (event.values[0] == 1.0f) {
                // 센서 이벤트가 발생할때 마다 걸음수 증가
                currentSteps++;
                NCBuilder.setContentText("걸음수" + currentSteps);
                Notification notification = NCBuilder.build();

                //현재 노티피케이션 메시지를 포그라운드 서비스의 메시지로 등록
                startForeground(10, notification);

            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}

