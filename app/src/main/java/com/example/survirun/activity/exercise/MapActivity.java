package com.example.survirun.activity.exercise;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.survirun.Medel.ZombieModel;
import com.example.survirun.R;
import com.example.survirun.databinding.ActivityMapBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/* 일반 메서드는 맵액티 내에서 사용, 스태틱 메서드는 스태틱 메서드 및 좀비모델에서 사용*/

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int GPS_ENABLE_REQUEST_CODE = 2001; //GPS 권한 요청 코드 상수
    private static final int PERMISSIONS_REQUEST_CODE = 100; //권한 요청 코드 상수
    private static final int DEFAULT_KCAL_WEIGHT = 80; // 기본 칼로리
    public static MediaPlayer mediaPlayer; //사운드 재생할때 쓰는 미디어플레이어
    private static int ZOMBIE_CREATE_MINUTES = 2; // 몇분마다 좀비 생성할건지?
    private static int MAX_ZB_CNT = 0; // 최대 좀비 개수 ( getIntent에서 기본값 3으로 설정됨 )
    public static int HP = 100; // 사용자의 현재 HP
    public static int minusHp = 20; // 좀비에게 물렸을때 HP가 얼마나 깎일껀지
    public static double storyShowKM = 0.2; // 몇 km마다 사용자에게 스토리를 띄울것인가?
    public static int storyCheckCnt = 1;

    public static boolean isZombieCreating = true; //좀비 생성 여부

    public int storyUserSelect = -1; // 유저가 어떤 스토리(버튼)을 클릭했는지 ( Intent )
    public static Context mctx; // static method에서 사용하는 mapactivity의 컨텍스트 ( 좀비에서 맵 참조 )
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}; //요청해야하는 퍼미션 목록
    public static GoogleMap mMap = null; // 환장의 구글맵 ( 초기화 하기 전에 사용되면 터짐 )
    private LocationManager lm; // 로케이션 매니저, 실질적으로 gps 값을 가져올때 쓰는 친구
    public static PolylineOptions polylineOptions = new PolylineOptions(); //플레이 중일때 사용자가 움직인 경로 (폴리라인) 그리는 친구

    private static PolylineOptions pausePolylineOpt = new PolylineOptions(); //일시정지 했을 때 사용자가 움직인 경로 그리는 친구
    private static TextToSpeech tts; //TTS 객체

    private double lastLat = 0.0; // 마지막 위치 ( latitude, 위도 )
    private double lastLng = 0.0; // 마지막 위치 (longitude, 경도)
    public static double currentLat = 0.0; //현재 위치 (lat, 위도 )
    public static double currentLng = 0.0; //현재 위치 (lng, 경도)

    private static double kcal = 0.0; //소모한 칼로리
    private static double walkingDistance = 0; //현재까지 걸은 거리 ( 아마 미터 단위 )
    private static double timeToSec = 0.0; //지금까지 활동한 시간 (아마)

    public static boolean isRunning = true; //일시정지시 false로
    private boolean isFirst = false; // 추측상 폴리라인 그리기 전 최초 여부 판단하는 친구..?
    /*if err remove static*/
    private static Thread timeThread = null; // 시간측정용 타임쓰레드
    public static ActivityMapBinding binding; //바인딩

    private static int kcalMok; //칼로리 목표
    private static int timeMok; //시간 목표
    private static double kmMok;//키로미터 목표
    private static int levelMok; //목표 레벨..?
    private static boolean zombieMode; //좀비모드여부 판단
    public static SupportMapFragment mapFragment; //맵 프래그먼트..

    public static ArrayList<ZombieModel> zombieList = new ArrayList<ZombieModel>(); //좀비 객체 관리용 좀비 리스트
    public static ArrayList<String> itemList = new ArrayList<String>();
    public static ArrayList<Marker> itemMarkerList = new ArrayList<>();
    private int zombieListCurrentPos = 0; // +1 해서 좀데 리스트 요소 개수 ㄱㄴ
    Dialog dialog; //다이얼로그 발생용 다이얼로그
    private static String title; //?

    public static Marker hospitalMarker = null;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);
        mctx = this; //컨텍스트 스태틱 변수에 컨텍스트 넣는 과정 ( 스태틱 메서드에서 사용, 좀비 모델에서 사용 )
        Intent getIntent = getIntent(); // 넘어온 값 관리


        //Log.d(title,title);


        /* 다이얼로그 초기화 */
        dialog = new Dialog(MapActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog);

        /* 이전 액티비티에서 값 가져오는거 */
        title = getIntent.getStringExtra("title");
        zombieMode = getIntent.getBooleanExtra("zombieMode", true);
        kmMok = getIntent().getDoubleExtra("km", 1);
        timeMok = getIntent().getIntExtra("time", 1);
        kcalMok = getIntent().getIntExtra("calorie", 1);
        levelMok = getIntent.getIntExtra("level", 0);
        Log.e(">>>>>", kmMok + " " + timeMok + " " + kcalMok);
        MAX_ZB_CNT = getIntent().getIntExtra("zombieCount", 3);

        //스낵바
        showSnackBar(view);


        binding.dragButton.setOnClickListener(v -> {
            Animation animationDown = AnimationUtils.loadAnimation(binding.layout.getContext(), R.anim.map_sliding_down);
            Animation animationUp = AnimationUtils.loadAnimation(binding.layout.getContext(), R.anim.map_sliding_up);
            Handler handler = new Handler();
            // ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams)binding.group.getLayoutParams();
//                if (lp.height==1){
//                    lp.height=100;
//
//                }else{
//                    lp.height=1;
//                }

            if (binding.group.getVisibility() == View.VISIBLE) {
                binding.layout.startAnimation(animationUp);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.dragButton.setImageResource(R.drawable.ic_down);
                        binding.group.setVisibility(View.GONE);
                    }
                }, 800);
            } else {
                binding.group.setVisibility(View.VISIBLE);
                binding.layout.startAnimation(animationDown);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.dragButton.setImageResource(R.drawable.ic_up);
                    }
                }, 800);
            }
        });

        // 음성 TTS 초기화
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    String strLanguage = Locale.getDefault().getLanguage();
                    if (strLanguage.equals("ko")) {
                        tts.setLanguage(Locale.KOREAN);
                    } else {
                        tts.setLanguage(Locale.ENGLISH);
                    }

                } else Log.d("</>", "system error" + status);
            }
        });

        //GPS Permission Check
        checkGPSPermission();


        try {
            //맵 액티비티 초기화
            init(MapActivity.this);
            //맵 초기화...
            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
            timeThread = new Thread(new timeThread());
            timeThread.start();
        } catch (Exception e) {

        }

        //TTS 출력
        playTTS(getString(R.string.start_tts));

        //위치 변동시 위치값 갱신 및 폴리라인 갱신
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                try {
                    setCurrentLatLng(location.getLatitude(), location.getLongitude());
                    if (lastLat == 0) {
                        exerciseTrackingInit();
                    }
                    if (isRunning) { //달리는중?
                        drawActivePolyline();
                        addMovedDistance();
                        //binding.textviewKm.setText(addMovedDistance());
                        binding.textviewKcal.setText(addUsedKcal());




                    } else {
                        if (isFirst) { //처음이면 초기화!
                            pausePolylineInit();
                        }
                        //폴리라인 그리기
                        drawPausePolyline();
                    }
                    setLastLatLng(currentLat, currentLng);
                } catch (Exception e) {
                }

            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {

            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
        });

        //일지정지 버튼 눌렀다면
        binding.pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //버튼 활성화-비활성화 상태 변경
                btnVisibilityChange(binding.pause);
                btnVisibilityChange(binding.resumeButton);
                btnVisibilityChange(binding.stopButton);
                //텍스트 보이게
                binding.pauseText.setVisibility(View.VISIBLE);
                //좀비들 이동 일시정지
                waitZombie();
                isFirst = true; //?
                isRunning = false; //달리지 않는 상태로

            }
        });

        //재개버튼 눌렀다면
        binding.resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //버튼 활성화-비활성화 상태 변경
                btnVisibilityChange(binding.pause);
                btnVisibilityChange(binding.resumeButton);
                btnVisibilityChange(binding.stopButton);
                //일시정지 버튼 안보이게
                binding.pauseText.setVisibility(View.GONE);
                resumeZombie(); //좀비 다시 달리게
                isRunning = true; //달리는 상태로
                isFirst = false; //?

            }
        });

        //멈추는 버튼 누른다면?
        binding.stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
            } //정지!
        });

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    //좀비 멈춰!
    public static void waitZombie() {
        isZombieCreating = false; //좀비 생성을 잠시 멈춥니다.
        for (ZombieModel z : zombieList) { //반복문을 돌며 좀비가 달리지 않는 상태로 변경
            z.isRun = false;

        }
    }

    @Override
    public void onDestroy() { //여긴 끝나면 초기화진행
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        HP = 100;
        isZombieCreating = true;
        lastLat = 0;
        lastLng = 0;
        currentLat = 0;
        currentLng = 0;
        kcal = 0;
        walkingDistance = 0;
        timeToSec = 0;
        isRunning = true;
        isFirst = false;
        timeThread = null;
        zombieList = new ArrayList<>();
        zombieListCurrentPos = 0;


    }

    public static void resumeZombie() { // 좀비가 다시 달릴 수 있도록 재개
        isZombieCreating = true;
        for (ZombieModel z : zombieList) {
            z.isRun = true;

        }
    }

    public static void playZBS() { //좀비 사운드재생
        mediaPlayer = MediaPlayer.create(mctx, R.raw.zombie4);
        mediaPlayer.setLooping(false);
        mediaPlayer.start();
    }

    @MainThread
    public static void updateMarkerPos(int idx) { // 특정 좀비 마커의 위치 갱신
        //
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                zombieList.get(idx).myMarker.remove(); // 마커 제거
                zombieList.get(idx).myMarker = mMap.addMarker(zombieList.get(idx).options); //마커 이동

            }
        });
    }

    @MainThread
    public static void removeMarker(int idx) { //특정 좀비 마커 제거
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                zombieList.get(idx).myMarker.remove();
            }
        });
    }


    @MainThread
    public static void stopZombie() { //좀비 자체를 멈춤
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                isZombieCreating = false;
                polylineOptions.color(Color.parseColor("#FA1D25"));
                if (zombieList.size() != 0) {
                    for (ZombieModel z : zombieList) {
                        z.myMarker.remove();
                        z.thread.interrupt();
                    }
                }
            }
        });
    }

    public void stop() { //맵 액티 내에서 종료하는 함수
        try {
            timeThread.interrupt();
            if (zombieMode) {
                stopZombie();
            }


            List<LatLng> list = polylineOptions.getPoints();


            if (list.size() > 0) {
                LatLng leftTopLatLng = list.get(0), rightBottomLatLng = list.get(0);
                for (LatLng i : list) {

                    if (leftTopLatLng.latitude > i.latitude) {
                        leftTopLatLng = new LatLng(i.latitude, leftTopLatLng.longitude);
                    }
                    if (leftTopLatLng.longitude < i.longitude) {
                        leftTopLatLng = new LatLng(leftTopLatLng.latitude, i.longitude);
                    }
                    if (rightBottomLatLng.latitude < i.latitude) {
                        rightBottomLatLng = new LatLng(i.latitude, rightBottomLatLng.longitude);
                    }
                    if (rightBottomLatLng.longitude > i.longitude) {
                        rightBottomLatLng = new LatLng(rightBottomLatLng.latitude, i.longitude);
                    }

                }

                LatLng mid = new LatLng((leftTopLatLng.latitude + rightBottomLatLng.latitude) / 2, (leftTopLatLng.longitude + rightBottomLatLng.longitude) / 2);
                Location ltl = new Location("ltl");
                Location rbl = new Location("rbl");
                ltl.setLatitude(leftTopLatLng.latitude);
                ltl.setLongitude(leftTopLatLng.longitude);
                rbl.setLatitude(rightBottomLatLng.latitude);
                rbl.setLongitude(rightBottomLatLng.longitude);

                double d = ltl.distanceTo(rbl);
                Log.d(">", "" + d);
                int zoomLv = getZoomLevelFromMeters(d);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mid, zoomLv));

            }
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                Log.d("ERR", e.getMessage());
            }

            GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {

                @Override
                public void onSnapshotReady(Bitmap snapshot) {
                    //sendDataToFirebase((int) kcal, walkingDistance / 1000, (int) timeToSec, snapshot);


                }
            };


            mMap.snapshot(callback);
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                Log.d("ERR", e.getMessage());
            }

            Intent intent = new Intent(MapActivity.this, ExerciseResultActivity.class); //main말고 다른걸로 변경
            intent.putExtra("kcal", (int) kcal);
            intent.putExtra("walkedDistanceToKm", walkingDistance / 1000);
            intent.putExtra("timeToSec", (int) timeToSec);
            intent.putExtra("title", title);
            intent.putExtra("hp", HP);
            //intent.putExtra()
            //intent.putExtra("title",title);
            intent.putExtra("calorie", kcalMok);
            intent.putExtra("km", kmMok);
            intent.putExtra("time", timeMok);
            intent.putExtra("level", levelMok);
            intent.putExtra("zombieCount", MAX_ZB_CNT);


            polylineOptions = new PolylineOptions();
            pausePolylineOpt = new PolylineOptions();
            mMap.clear();
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
            Log.d("<MapActivity, func stop>", e.getMessage());
        }


    }

    // 외부에서 종료.
    public static void sstop() {
        try {
            timeThread.interrupt();
            List<LatLng> list = polylineOptions.getPoints();

            if (list.size() > 0) {
                LatLng leftTopLatLng = list.get(0), rightBottomLatLng = list.get(0);
                for (LatLng i : list) {

                    if (leftTopLatLng.latitude > i.latitude) {
                        leftTopLatLng = new LatLng(i.latitude, leftTopLatLng.longitude);
                    }
                    if (leftTopLatLng.longitude < i.longitude) {
                        leftTopLatLng = new LatLng(leftTopLatLng.latitude, i.longitude);
                    }
                    if (rightBottomLatLng.latitude < i.latitude) {
                        rightBottomLatLng = new LatLng(i.latitude, rightBottomLatLng.longitude);
                    }
                    if (rightBottomLatLng.longitude > i.longitude) {
                        rightBottomLatLng = new LatLng(rightBottomLatLng.latitude, i.longitude);
                    }

                }

                LatLng mid = new LatLng((leftTopLatLng.latitude + rightBottomLatLng.latitude) / 2, (leftTopLatLng.longitude + rightBottomLatLng.longitude) / 2);
                Location ltl = new Location("ltl");
                Location rbl = new Location("rbl");
                ltl.setLatitude(leftTopLatLng.latitude);
                ltl.setLongitude(leftTopLatLng.longitude);
                rbl.setLatitude(rightBottomLatLng.latitude);
                rbl.setLongitude(rightBottomLatLng.longitude);

                double d = ltl.distanceTo(rbl);
                Log.d(">", "" + d);
                //int zoomLv = getZoomLevelFromMeters(d);
                int dis = 24576000;
                int ret = 21;
                Integer ret1 = ret;
                for (int i = 1; i <= 21; i++) {
                    if (d >= dis) {
                        ret = i;
                        break;
                    } else {
                        dis /= 2;
                    }
                }


                GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {

                    @Override
                    public void onSnapshotReady(Bitmap snapshot) {
                        //sendDataToFirebase((int) kcal, walkingDistance / 1000, (int) timeToSec, snapshot);


                    }
                };


                polylineOptions = new PolylineOptions();
                pausePolylineOpt = new PolylineOptions();

                ((Activity) mctx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mid, ret1));
                        mMap.snapshot(callback);
                        mMap.clear();
                    }
                });


                Intent intent = new Intent(mctx, ExerciseResultActivity.class); //main말고 다른걸로 변경
                intent.putExtra("kcal", (int) kcal);
                intent.putExtra("walkedDistanceToKm", walkingDistance / 1000);
                intent.putExtra("timeToSec", (int) timeToSec);
                intent.putExtra("title", title);
                intent.putExtra("hp", HP);
                intent.putExtra("calorie", kcalMok);
                intent.putExtra("km", kmMok);
                intent.putExtra("time", timeMok);
                intent.putExtra("level", levelMok);
                intent.putExtra("zombieCount", MAX_ZB_CNT);

                mctx.startActivity(intent);


            }

        } catch (Exception e) {
            Log.e("<MapActivity, sstop()>", e.getMessage());
        }
    }

    //맵 사진 찍을 때 지도 줌 레벨 조정
    private int getZoomLevelFromMeters(double distanceTo) {
        int dis = 24576000;
        int ret = 21;
        for (int i = 1; i <= 21; i++) {
            if (distanceTo >= dis) {
                ret = i;
                break;
            } else {
                dis /= 2;
            }
        }
        return ret;

    }

    // 맵 액티 초기화
    public void init(Context ctx) {
        polylineOptions.color(Color.parseColor("#64A3F5"));
        polylineOptions.zIndex(0);
        lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        isRunning = true;
        isFirst = false;
        isZombieCreating = true;

    }

    //gps 펄미션 검사
    private void checkGPSPermission() {
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        } else {
            checkRunTimePermission();
        }
    }

    //운동 추적 초기화?
    public void exerciseTrackingInit() {
        lastLat = currentLat;
        lastLng = currentLng;
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(currentLat, currentLng));
        markerOptions.title(getString(R.string.base_camp));
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.base_camp);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 200, 200, false);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

        mMap.addMarker(markerOptions);

        //mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLat, currentLng), 18));
    }
    //맵 사진 촬영용
    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    //현재위치 설정 ( 변수 업데이트 )
    public void setCurrentLatLng(double lat, double lng) {
        currentLat = lat;
        currentLng = lng;
    }

    //마지막 위치 설정 (변수 업데이트)
    public void setLastLatLng(double lat, double lng) {
        lastLat = lat;
        lastLng = lng;
    }
    //활성화된 폴리라인 그리기
    public void drawActivePolyline() {
        isFirst = false;
        LatLng currentLatLng = new LatLng(currentLat, currentLng);
        polylineOptions.add(currentLatLng);
        mMap.addPolyline(polylineOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18));
    }

    //거리계산후 더하기
    @SuppressLint("DefaultLocale")
    public void addMovedDistance() {
        Location loA = new Location("last");
        loA.setLatitude(lastLat);
        loA.setLongitude(lastLng);
        Location loB = new Location("curr");
        loB.setLatitude(currentLat);
        loB.setLongitude(currentLng);
        walkingDistance = walkingDistance + loA.distanceTo(loB);

        /*story play..*/
        // condition: km check


        binding.textviewKm.setText(String.format("%.2f", walkingDistance / 1000.0));

        if(  walkingDistance > ( storyShowKM * 1000 * storyCheckCnt)) {
            storyCheckCnt++;
            createEvent();
            readStory("TEST STORY", "YES", "NO", 1000);
        }
        if(storyCheckCnt != 1) {
            if(( storyCheckCnt-1) % 5 == 0) { //5
                createItemMarker();
                /*item create*/
                //생성..
            }

            if(( storyCheckCnt-1) % 4 == 0) { //10
                updateHospital();
            }

            if(( storyCheckCnt-1) % 3 == 0) { //3
                Random r = new Random();
                r.setSeed(System.currentTimeMillis());
                int d = r.nextInt(10);

                if(d <= 4) { // 0,1,2,3,4 pass
                    removeItemMarker();
                }

            }

            itemCheck();
            hospitalCheck();


        }






        //return String.format("%.2f", walkingDistance / 1000.0);
    }

    private void updateHospital() {
        MarkerOptions opt = new MarkerOptions();

        BitmapDrawable bitmapdraw=(BitmapDrawable) getResources().getDrawable(R.drawable.hospitalmarkerview);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 150, false);
        opt.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));



        opt.title("Hospital: Heal 20 HP");
        opt.position(createRandomPos(new LatLng(currentLat,currentLng)));
        if(hospitalMarker == null) {
            hospitalMarker = mMap.addMarker(opt);
        }
        else {
            hospitalMarker.remove();
            hospitalMarker = mMap.addMarker(opt);
        }
    }

    public void hospitalCheck() {
        Location human = new Location("human");
        human.setLatitude(currentLat);
        human.setLongitude(currentLng);
        Location item = new Location("Item");
        item.setLatitude(hospitalMarker.getPosition().latitude);
        item.setLongitude(hospitalMarker.getPosition().longitude);

        double distance = Math.round(item.distanceTo(human) * 100) / 100.0; //km
        if(distance <= 5) {
            hospitalMarker.remove();
            hospitalMarker = null;
            plusHP(20);
        }
    }
    public void plusHP(int value) {
        if(HP>=80) HP = 100;
        else HP= HP + value;
        updateHpUI();
    }

    public void itemCheck() {
        Location human = new Location("human");
        human.setLatitude(currentLat);
        human.setLongitude(currentLng);
        for(int m = 0; m<itemMarkerList.size(); m++) {
            Location item = new Location("Item");
            item.setLatitude(itemMarkerList.get(m).getPosition().latitude);
            item.setLongitude(itemMarkerList.get(m).getPosition().longitude);

            double distance = Math.round(item.distanceTo(human) * 100) / 100.0; //km
            if(distance <= 5) {
                getUserItem(m);
            }
        }

    }

    private double latIndiff(int diff) { //단위는 m
        final int earth = 6371000;
        return (diff * 360.0) / (2 * Math.PI * earth);
    }

    public double lonIndiff(double _currentLat, int diff) {
        final int earth = 6371000;
        double ddd = Math.cos(0);
        double ddf = Math.cos(Math.toRadians(_currentLat));
        return (diff * 360.0) / (2 * Math.PI * earth * Math.cos(Math.toRadians(_currentLat)));
    }

    public LatLng createRandomPos(LatLng current) {
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        int d = random.nextInt(3);
        double ta, to;
        double z = 0.0015;
        switch(d) {
            case 0:
                ta = to = z;
                break;
            case 1:
                ta = 0 -z;
                to = z;
                break;
            case 2:
                ta = to = 0 -z;
                break;
            default:
                ta = z;
                to = 0 -z;
                break;
        }
        double diffLat = latIndiff(100);
        double diffLon = lonIndiff(current.latitude, 100);
        LatLng minLatLng = new LatLng(current.latitude - diffLat, current.longitude - diffLon);
        LatLng maxLatLng = new LatLng(current.latitude+ diffLat, current.longitude + diffLon);
        double randomLat = (Math.random() * (maxLatLng.latitude - minLatLng.latitude + ta) + minLatLng.latitude);
        double randomLng = (Math.random() * (maxLatLng.longitude - minLatLng.longitude + to) + minLatLng.longitude);
        return new LatLng(randomLat, randomLng);
    }

    public void createItemMarker() {
        MarkerOptions opt = new MarkerOptions();
        opt.position(createRandomPos(new LatLng(currentLat, currentLng)));
        Random r = new Random();
        r.setSeed(System.currentTimeMillis());
        int d = r.nextInt(5);
        //opt.title("Item"+(d+1));
        switch(d) {
            case 0:
                BitmapDrawable bitmapdraw=(BitmapDrawable) getResources().getDrawable(R.drawable.item_knuckle);
                Bitmap b=bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, 150, 150, false);
                opt.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                opt.title("모자");
                break;
            case 1:
                BitmapDrawable bitmapdraw1 =(BitmapDrawable) getResources().getDrawable(R.drawable.item_cap);
                Bitmap b1 =bitmapdraw1.getBitmap();
                Bitmap smallMarker1 = Bitmap.createScaledBitmap(b1, 150, 150, false);
                opt.icon(BitmapDescriptorFactory.fromBitmap(smallMarker1));
                opt.title("너클");
                break;
            case 2:
                BitmapDrawable bitmapdraw2 =(BitmapDrawable) getResources().getDrawable(R.drawable.bat);
                Bitmap b2 =bitmapdraw2.getBitmap();
                Bitmap smallMarker2 = Bitmap.createScaledBitmap(b2, 150, 150, false);
                opt.icon(BitmapDescriptorFactory.fromBitmap(smallMarker2));
                opt.title("야구빠따");
                break;
            case 3:
                BitmapDrawable bitmapdraw3 =(BitmapDrawable) getResources().getDrawable(R.drawable.alcohol);
                Bitmap b3 =bitmapdraw3.getBitmap();
                Bitmap smallMarker3 = Bitmap.createScaledBitmap(b3, 150, 150, false);
                opt.icon(BitmapDescriptorFactory.fromBitmap(smallMarker3));
                opt.title("화염병");
                break;
            case 4:
                BitmapDrawable bitmapdraw4 =(BitmapDrawable) getResources().getDrawable(R.drawable.knife);
                Bitmap b4 =bitmapdraw4.getBitmap();
                Bitmap smallMarker4 = Bitmap.createScaledBitmap(b4, 150, 150, false);
                opt.icon(BitmapDescriptorFactory.fromBitmap(smallMarker4));
                opt.title("칼");
                break;
            default:
                break;
        }
        Marker cur = mMap.addMarker(opt);
        itemMarkerList.add(cur);

    }

    public void removeItemMarker() {
        itemMarkerList.get(0).remove();
        itemMarkerList.remove(0);
    }

    public void getUserItem(int index) {
        String itemZongRyu = itemMarkerList.get(index).getTitle();
        itemMarkerList.get(index).remove();
        itemMarkerList.remove(index);

        itemList.add(itemZongRyu);

        readStory(itemZongRyu,"확인","OK",20000);
    }

    public void createEvent() {

    }

    //소모한 칼로리 계산후 더하기
    @SuppressLint("DefaultLocale")
    public String addUsedKcal() {
        kcal = DEFAULT_KCAL_WEIGHT * (walkingDistance / 1000.0);
        return String.format("%.0f", kcal);
    }

    //일시정지한 폴리라인 초기화
    public void pausePolylineInit() {
        isFirst = false;
        pausePolylineOpt = new PolylineOptions();
        pausePolylineOpt.zIndex(1);
        pausePolylineOpt.color(Color.parseColor("#979DA6"));
        pausePolylineOpt.add(new LatLng(lastLat, lastLng));
    }

    //일시정지한 폴리라인 그리기
    public void drawPausePolyline() {
        LatLng t = new LatLng(currentLat, currentLng);
        polylineOptions.add(t);
        mMap.addPolyline(polylineOptions);
        pausePolylineOpt.add(t);
        mMap.addPolyline(pausePolylineOpt);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(t, 18));
    }

    // 맵 액티비티 초기화
    @Override
    public void onMapReady(@NonNull final GoogleMap googleMap) { //초기 Init
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
        builder.setTitle(R.string.disable_location);
        builder.setMessage(R.string.use_location_service + "\n"
                + R.string.modify_location);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                Intent intent = new Intent(MapActivity.this, MapActivity.class);
                startActivity(intent);
                finish();

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
        if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED || hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapActivity.this, REQUIRED_PERMISSIONS[0])) {
                Toast.makeText(MapActivity.this, R.string.need_location, Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(MapActivity.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
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
            if (!check_result) {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {
                    //binding.currentLocation.setText("위치 알 수 없음");
                    Toast.makeText(MapActivity.this, R.string.permission_error_re_run, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    //binding.currentLocation.setText("위치 알 수 없음");
                    Toast.makeText(MapActivity.this, R.string.permission_error_allow_setting, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPS_ENABLE_REQUEST_CODE) {//사용자가 GPS 활성 시켰는지 검사
            if (checkLocationServicesStatus()) {
                if (checkLocationServicesStatus()) {
                    Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                    checkRunTimePermission();
                }
            }
        }
        if (requestCode == 1000) { //다이얼로그 띄웠을 때..
            if (resultCode == RESULT_OK) {
                storyUserSelect = data.getIntExtra("result", -1);
            } else {
                storyUserSelect = -1;
            }
            afterReadStory();
        }
        if (requestCode == 20000) {
            if(resultCode == RESULT_OK) {
                //READ CHECK
            }
        }
    }

//    public void sendDataToFirebase(int kcal, double km, int time, Bitmap mapImg) {
//        String uid = FirebaseAuth.getInstance().getUid();
//        ScoreModel scoreModel = new ScoreModel();
//        scoreModel.todayCalorie = kcal;
//        scoreModel.todayKm = km;
//        scoreModel.todayExerciseTime = time;
//        SimpleDateFormat format1 = new SimpleDateFormat( "yyyy-MM-dd/HH:mm:ss");
//        Date t = new Date();
//        String time1 = format1.format(t);
//        StorageReference imgRef = FirebaseStorage.getInstance().getReference().child("exercisePhoto/"+uid+"/"+time1);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        mapImg.compress(Bitmap.CompressFormat.JPEG,50, baos);
//        byte[] d = baos.toByteArray();
//        if (uid != null) {
//            FirebaseDatabase.getInstance().getReference().child("UserProfile").child(uid).setValue(scoreModel);
//            imgRef.putBytes(d);
//        }
//
//
//
//    }

    // 버튼 활성화-비활성화 상태 변경
    public void btnVisibilityChange(Button btn) {
        if (btn.getVisibility() == View.VISIBLE) {
            btn.setVisibility(View.GONE);
        } else {
            btn.setVisibility(View.VISIBLE);
        }
    }

    // tts 재생
    public void playTTS(String text) {
        tts.setPitch(1.0f);
        tts.setSpeechRate(1.0f);
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, text);
    }
    //스태틱 메서드에서 tts 재생
    public static void splayTTS(String text) {
        tts.setPitch(1.0f);
        tts.setSpeechRate(1.0f);
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, text);
    }

    //시간 측정 타이버 ** 시간관련 , 초당 업데이트는 전부 여기서 처리
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @SuppressLint("DefaultLocale")
        @Override
        public void handleMessage(Message msg) {
            try {
                int sec = msg.arg1 % 60;
                int min = msg.arg1 / 60;
                int hour = msg.arg1 / 3600;
                int timeToSec1 = msg.arg1;
                if (isRunning) {
                    timeToSec = msg.arg1;
                    String str = String.format("%02d:%02d:%02d", hour, min, sec);
                    binding.textviewExerciseTime.setText(str);
                    if ((timeToSec1 / 60) % 3 == 0 && timeToSec1 % 60 == 0 && timeToSec1 >= 60) {
                        String d = String.format(getString(R.string.tts_type), (int) kcal, walkingDistance / 1000.0, hour, min, sec);
                        playTTS(d);
                    }
                } else {
                    String str = String.format(getString(R.string.pause_text) + "%02d:%02d:%02d", hour, min, sec);
                    binding.pauseText.setText(str);
                }
                if (zombieMode) {
                    if (isRunning) {
                        if ((timeToSec1 / 60) % ZOMBIE_CREATE_MINUTES == 0 && timeToSec1 % 60 == 0 && isZombieCreating && timeToSec1 >= 60) {
                            if (zombieList.size() < MAX_ZB_CNT) createZombie();
                        }
                    }
                }

                /*목표달성체크*/
                if (kcal >= kcalMok && (walkingDistance / 1000) >= kmMok && timeToSec >= timeMok) {
                    playTTS(getString(R.string.mok_cl));
                    stop();
                }

            } catch (Exception e) {
                Log.e("<MapActivity, Time Handler> : ", e.getMessage());
            }


        }
    };

    // hp 감소
    public static void minusHPAndCheck() {

        vibration();
        updateHpUI();
        splayTTS(mctx.getString(R.string.hitted_zb));
        if (HP <= 0) {

            stopZombie();
            splayTTS(mctx.getString(R.string.died));
            sstop(); //종료하고 싶으면
        }
    }
    //진동발생
    public static void vibration(){
        MapActivity.HP = MapActivity.HP - MapActivity.minusHp;
        Vibrator vibrator = (Vibrator) mctx.getSystemService(Context.VIBRATOR_SERVICE);

        // 0.5초 대기 -> 1초 진동 -> 0.5초 대기 -> 1초 진동
        final long[] vibratePattern = new long[]{500, 1000, 500, 1000,500,1000};
        // 반복 없음
        final int repeat = -1;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(vibratePattern, repeat));
        } else {
            vibrator.vibrate(vibratePattern, repeat);
        }
    }

    //HP ui update
    @MainThread
    public static void updateHpUI() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                binding.progress2.setProgress(HP);
            }
        });
    }

    // 스토리 사용 예정
    private void readStory(String storyBody, String positive, String negative, int reqCode) {

        Intent u = new Intent(MapActivity.this, ExerciseStoryActivity.class);
        u.putExtra("storyBody", storyBody);
        u.putExtra("negative", negative);
        u.putExtra("positive", positive);
        startActivityForResult(u, reqCode);
    }

    private void afterReadStory() {
        //스토리 읽고 난 뒤 진행되는 메서드..
        //positvie btn -> 1
        //negative btn -> 0
        // maybe hardcoding
    }

    // 시간 계산기
    public class timeThread implements Runnable {
        @Override
        public void run() {
            try {
                int runTime = 0;
                int restTime = 0;
                while (true) {
                    Message msg = new Message();
                    if (isRunning) {
                        msg.arg1 = runTime++;
                    } else {
                        msg.arg1 = restTime++;
                    }
                    handler.sendMessage(msg);
                    Thread.sleep(1000);
                }

            } catch (InterruptedException e) {
                //do
            }

        }
    }
    //좀비 생성
    public void createZombie() {
        ZombieModel mZombie = new ZombieModel(new LatLng(currentLat, currentLng), zombieListCurrentPos, MapActivity.this);
        zombieListCurrentPos++;
        zombieList.add(mZombie);
        playTTS(getString(R.string.create_zb));
    }
    // 다이얼로그 생성
    void showDialog() {
        TextView explain = dialog.findViewById(R.id.explain_textView);
        Button cancelButton = dialog.findViewById(R.id.cancel_button);
        Button yesButton = dialog.findViewById(R.id.yes_button);
        explain.setText(R.string.end_exercise);
        //dialog.findViewById(R.id.help_button).setVisibility(View.GONE);
        dialog.show();

        yesButton.setOnClickListener(v -> {
            dialog.dismiss();
            stop();
        });
        cancelButton.setOnClickListener(v -> dialog.dismiss());
    }
    // 백버튼시 다이얼로그 발생
    @Override
    public void onBackPressed() {
        showDialog();
    }

    private void showSnackBar(View v) {
        final Snackbar snackbar = Snackbar.make(v, "", 10000);
        View customSnackView = getLayoutInflater().inflate(R.layout.snackbar, null);
        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbarLayout.setPadding(0, 0, 0, 0);
        customSnackView.findViewById(R.id.gotoWebsiteButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbarLayout.addView(customSnackView, 0);
        snackbar.show();
    }

}
