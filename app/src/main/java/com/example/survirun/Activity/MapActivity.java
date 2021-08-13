package com.example.survirun.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.survirun.Medel.ScoreModel;
import com.example.survirun.R;
import com.example.survirun.databinding.ActivityMapBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private GoogleMap mMap = null;
    private LocationManager lm; //= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    private Location location;
    private PolylineOptions polylineOptions = new PolylineOptions();

    private PolylineOptions pausePolylineOpt = new PolylineOptions();
    private TextToSpeech tts;

    private double startLat = 0.0;
    private double startLng = 0.0;
    private double lastLat = 0.0;
    private double lastLng = 0.0;
    private double currentLat = 0.0;
    private double currentLng = 0.0;

    private double kcal = 0.0;
    private double walkingDistance = 0;
    private double timeToSec = 0.0;

    private boolean isRunning = true; //일시정지시 false로
    private boolean isFirst = false;
    private Thread timeThread = null;
    private ActivityMapBinding binding;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        polylineOptions.zIndex(0);
        tts = new TextToSpeech(MapActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS) {
                    int r = tts.setLanguage(Locale.KOREA);
                }

            }
        });

        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        } else {
            checkRunTimePermission();
        }



        //onMapReady(mMap);

        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        timeThread = new Thread(new timeThread());
        timeThread.start();


        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                //location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                currentLat = location.getLatitude();
                currentLng = location.getLongitude();
                if(lastLat == 0) {
                    lastLat = currentLat;
                    lastLng = currentLng;
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(currentLat, currentLng));
                    markerOptions.title("시작");
                    mMap.addMarker(markerOptions);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLat, currentLng), 18));

                }

                if(isRunning) {
                    isFirst = false;
                    LatLng currentLatLng = new LatLng(currentLat, currentLng);
                    Log.d(">>",String.valueOf(currentLat) +' '+ String.valueOf(currentLng));
                    polylineOptions.color(Color.parseColor("#64A3F5"));
                    polylineOptions.add(currentLatLng);
                    //Log.d(">>>",String.valueOf(polylineOptions));
                    mMap.addPolyline(polylineOptions);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18));

                    /* 여기에 칼로리 및 거리 구하는 코드 집어넣기!*/

                    Location loA = new Location("last");
                    loA.setLatitude(lastLat);
                    loA.setLongitude(lastLng);
                    Location loB = new Location("curr");
                    loB.setLatitude(currentLat);
                    loB.setLongitude(currentLng);
                    walkingDistance = walkingDistance + loA.distanceTo(loB);
                    kcal = 80 * (walkingDistance / 1000.0);
                    binding.textviewKcal.setText(String.format("%.0f",kcal));
                    binding.textviewKm.setText(String.format("%.2f",walkingDistance / 1000.0));
                } else { /*
                    LatLng currentLatLng = new LatLng(currentLat, currentLng);
                    Log.d(">>",String.valueOf(currentLat) +' '+ String.valueOf(currentLng));
                    polylineOptions.color(Color.parseColor("#A1A69A"));
                    polylineOptions.add(currentLatLng);

                    //Log.d(">>>",String.valueOf(polylineOptions));
                    mMap.addPolyline(polylineOptions);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18)); */
                    if(isFirst) {
                        isFirst = false;
                        pausePolylineOpt = new PolylineOptions();
                        pausePolylineOpt.zIndex(1);
                        pausePolylineOpt.color(Color.parseColor("#979DA6"));
                        pausePolylineOpt.add(new LatLng(lastLat,lastLng));

                    }
                    LatLng t = new LatLng(currentLat,currentLng);
                    polylineOptions.add(t);
                    mMap.addPolyline(polylineOptions);
                    pausePolylineOpt.add(t);
                    mMap.addPolyline(pausePolylineOpt);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(t, 18));
                }

                lastLat = currentLat;
                lastLng = currentLng;
            }
        });

        binding.pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnVisibilityChange(binding.pause);
                btnVisibilityChange(binding.resume);
                btnVisibilityChange(binding.stop);
                isFirst = true;
                isRunning = false;

            }
        });

        binding.resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnVisibilityChange(binding.pause);
                btnVisibilityChange(binding.resume);
                btnVisibilityChange(binding.stop);
                isRunning = true;
                isFirst = false;

            }
        });

        binding.stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //timeThread.
                timeThread.interrupt();
                sendDataToFirebase((int) kcal, walkingDistance /1000, (int) timeToSec);
                startActivity(new Intent(MapActivity.this, MainActivity.class));
            }
        });

    }




    @Override
    public void onStart() {
        super.onStart();

    }


    @Override
    public void onMapReady(final GoogleMap googleMap) { //초기 Init
        mMap = googleMap;


        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        } else {
            checkRunTimePermission();
        }
        mMap.setMyLocationEnabled(true);
        UiSettings mMapUiSetting = mMap.getUiSettings();
        mMapUiSetting.setZoomControlsEnabled(true);
        mMapUiSetting.setZoomGesturesEnabled(true);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.555201,126.970734), 10));


    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    void checkRunTimePermission() {

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MapActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MapActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음


        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(MapActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MapActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MapActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults);
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if (check_result) {

                //위치 값을 가져올 수 있음
                ;
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    //binding.currentLocation.setText("위치 알 수 없음");
                    Toast.makeText(MapActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();


                } else {
                    //binding.currentLocation.setText("위치 알 수 없음");
                    Toast.makeText(MapActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @SuppressLint("DefaultLocale")
        @Override
        public void handleMessage(Message msg) {
            int sec = (msg.arg1 / 100) % 60;
            int min = (msg.arg1 / 100) / 60;
            int hour = (msg.arg1 / 100) / 3600;
            timeToSec = msg.arg1 / 100.0;
            if(min%5 == 0 && sec == 0 && (min!=0 || hour != 0)) {
                String d = String.format("현재 소비 칼로리는 %d며, 총 %.2f킬로미터 달렸습니다. 지금까지 운동한 시간은 %d시간 %d분 %d초입니다.",(int)kcal, walkingDistance/1000.0,hour,min,sec);
                tts.speak(d,TextToSpeech.QUEUE_FLUSH,null);
            }
            //1000이 1초 1000*60 은 1분 1000*60*10은 10분 1000*60*60은 한시간
            String str = String.format("%02d:%02d:%02d", hour, min, sec);
            binding.textviewExerciseTime.setText(str);
        }
    };

    public class timeThread implements Runnable {
        @Override
        public void run() {
            try {
                int i = 0;
                while(true) {
                    while (isRunning) {
                        Message msg = new Message();
                        msg.arg1 = i++;
                        handler.sendMessage(msg);
                        Thread.sleep(10);
                    }
                }

            } catch(InterruptedException e) {
                Log.d(">",e.getMessage());
            }

        }
    }

    public void sendDataToFirebase(int kcal, double km, int time) {
        //something here..
        Log.d("adfadsf","보내는중");
        String uid= FirebaseAuth.getInstance().getUid();
        ScoreModel scoreModel=new ScoreModel();
        scoreModel.todayCalorie=kcal;
        scoreModel.todayKm=km;
        scoreModel.todayExerciseTime=time;
        FirebaseDatabase.getInstance().getReference().child("UserProfile").child(uid).setValue(scoreModel);
        Log.d("adfadsf","보냄");


    }

    public void btnVisibilityChange(Button btn) {
        if(btn.getVisibility() == View.VISIBLE) {
            btn.setVisibility(View.GONE);
        } else {
            btn.setVisibility(View.VISIBLE);
        }
    }

}