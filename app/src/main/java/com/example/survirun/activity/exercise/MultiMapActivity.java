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
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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

import com.example.survirun.Medel.MultiUserModel;
import com.example.survirun.Medel.MultiZombieModel;
import com.example.survirun.R;
import com.example.survirun.databinding.ActivityMapBinding;
import com.example.survirun.databinding.ActivityMultiMapBinding;
import com.example.survirun.server.WebSocketService;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MultiMapActivity  extends AppCompatActivity implements OnMapReadyCallback {
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private static final int DEFAULT_KCAL_WEIGHT = 80;
    public static MediaPlayer mediaPlayer;
    public static int HP = 100;
    public static int minusHp = 20;

    public static boolean isZombieCreating = true;

    public int storyUserSelect = -1;
    public static Context mctx;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    public static GoogleMap mMap = null;
    private LocationManager lm;
    public static PolylineOptions polylineOptions = new PolylineOptions();

    private static PolylineOptions pausePolylineOpt = new PolylineOptions();
    private static TextToSpeech tts;

    private double lastLat = 0.0;
    private double lastLng = 0.0;
    public static double currentLat = 0.0;
    public static double currentLng = 0.0;

    private static double kcal = 0.0;
    private static double walkingDistance = 0;
    private static double timeToSec = 0.0;

    public static boolean isRunning = true; //일시정지시 false로
    private boolean isFirst = false;
    /*if err remove static*/
    private static Thread timeThread = null;
    public static ActivityMultiMapBinding binding;

    private static boolean zombieMode;
    public static SupportMapFragment mapFragment;

    public static ArrayList<MultiZombieModel> zombieList = new ArrayList<MultiZombieModel>();
    private int zombieListCurrentPos = 0; // +1 해서 좀데 리스트 요소 개수 ㄱㄴ
    Dialog dialog;
    private static String title;
    public static ArrayList<MultiUserModel> userList;
    public static ArrayList<Marker> markerList = new ArrayList<>();
    public static Marker labMarker;

    public static ArrayList<Marker> itemMarkerList = new ArrayList<>();



    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityMultiMapBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mctx = this;
        Intent getIntent = getIntent();
        WebSocketService.socketConnect();



        //Log.d(title,title);
        dialog = new Dialog(MultiMapActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog);

        title = getIntent.getStringExtra("title");

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


        checkGPSPermission();
        JSONObject jsonDT;
        try {
            init(MultiMapActivity.this);
            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
            timeThread = new Thread(new MultiMapActivity.timeThread());
            timeThread.start();



        } catch (Exception e) {
            Log.e(">",e.getMessage());
        }


        playTTS(getString(R.string.start_tts));
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                try {
                    setCurrentLatLng(location.getLatitude(), location.getLongitude());
                    sendSocket(); //CurrentLatLng
                    getDistanceBetweenLatLngs(new LatLng(currentLat,currentLng));
                    if (lastLat == 0) {
                        exerciseTrackingInit();
                    }
                    if (isRunning) {
                        drawActivePolyline();
                        binding.textviewKm.setText(addMovedDistance());
                        binding.textviewKcal.setText(addUsedKcal());
                    } else {
                        if (isFirst) {
                            pausePolylineInit();
                        }
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

        binding.pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnVisibilityChange(binding.pause);
                btnVisibilityChange(binding.resumeButton);
                btnVisibilityChange(binding.stopButton);
                binding.pauseText.setVisibility(View.VISIBLE);
                waitZombie();
                isFirst = true;
                isRunning = false;

            }
        });

        binding.resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnVisibilityChange(binding.pause);
                btnVisibilityChange(binding.resumeButton);
                btnVisibilityChange(binding.stopButton);
                binding.pauseText.setVisibility(View.GONE);
                resumeZombie();
                isRunning = true;
                isFirst = false;

            }
        });

        binding.stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
            }
        });

    }

    public void sendSocket() {
        WebSocketService.mSocket.emit("UpdataCoordinate","{\"roomName\":\""+WebSocketService.roomName+"\",\"latitude\":"+currentLat+",\"longitude\":"+currentLng+"}");
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    public static void waitZombie() {
        isZombieCreating = false;
        for (MultiZombieModel z : zombieList) {
            z.isRun = false;

        }
    }

    @Override
    public void onDestroy() {
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

    public static void resumeZombie() {
        isZombieCreating = true;
        for (MultiZombieModel z : zombieList) {
            z.isRun = true;

        }
    }

    public static void playZBS() {
        mediaPlayer = MediaPlayer.create(mctx, R.raw.zombie4);
        mediaPlayer.setLooping(false);
        mediaPlayer.start();
    }

    @MainThread
    public static void updateMarkerPos(int idx) {
        //
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                zombieList.get(idx).myMarker.remove();
                zombieList.get(idx).myMarker = mMap.addMarker(zombieList.get(idx).options);

            }
        });
    }

    @MainThread
    public static void removeMarker(int idx) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                zombieList.get(idx).myMarker.remove();
            }
        });
    }


    @MainThread
    public static void stopZombie() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                isZombieCreating = false;
                polylineOptions.color(Color.parseColor("#FA1D25"));
                if (zombieList.size() != 0) {
                    for (MultiZombieModel z : zombieList) {
                        z.myMarker.remove();
                        z.thread.interrupt();
                    }
                }
            }
        });
    }

    public void stop() {
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

            Intent intent = new Intent(MultiMapActivity.this, ExerciseResultActivity.class); //main말고 다른걸로 변경
            intent.putExtra("kcal", (int) kcal);
            intent.putExtra("walkedDistanceToKm", walkingDistance / 1000);
            intent.putExtra("timeToSec", (int) timeToSec);
            intent.putExtra("title", title);
            intent.putExtra("hp", HP);


            polylineOptions = new PolylineOptions();
            pausePolylineOpt = new PolylineOptions();
            mMap.clear();
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
            Log.d("<MapActivity, func stop>", e.getMessage());
        }


    }


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

                mctx.startActivity(intent);


            }

        } catch (Exception e) {
            Log.e("<MapActivity, sstop()>", e.getMessage());
        }
    }

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


    public void init(Context ctx) {
        polylineOptions.color(Color.parseColor("#64A3F5"));
        polylineOptions.zIndex(0);
        lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        isRunning = true;
        isFirst = false;
        isZombieCreating = true;

    }

    private void checkGPSPermission() {
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        } else {
            checkRunTimePermission();
        }
    }

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

    public void setCurrentLatLng(double lat, double lng) {
        currentLat = lat;
        currentLng = lng;
    }

    public void setLastLatLng(double lat, double lng) {
        lastLat = lat;
        lastLng = lng;
    }

    public void drawActivePolyline() {
        isFirst = false;
        LatLng currentLatLng = new LatLng(currentLat, currentLng);
        polylineOptions.add(currentLatLng);
        mMap.addPolyline(polylineOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18));
    }

    @SuppressLint("DefaultLocale")
    public String addMovedDistance() {
        Location loA = new Location("last");
        loA.setLatitude(lastLat);
        loA.setLongitude(lastLng);
        Location loB = new Location("curr");
        loB.setLatitude(currentLat);
        loB.setLongitude(currentLng);
        walkingDistance = walkingDistance + loA.distanceTo(loB);
        return String.format("%.2f", walkingDistance / 1000.0);
    }

    @SuppressLint("DefaultLocale")
    public String addUsedKcal() {
        kcal = DEFAULT_KCAL_WEIGHT * (walkingDistance / 1000.0);
        return String.format("%.0f", kcal);
    }

    public void pausePolylineInit() {
        isFirst = false;
        pausePolylineOpt = new PolylineOptions();
        pausePolylineOpt.zIndex(1);
        pausePolylineOpt.color(Color.parseColor("#979DA6"));
        pausePolylineOpt.add(new LatLng(lastLat, lastLng));
    }

    public void drawPausePolyline() {
        LatLng t = new LatLng(currentLat, currentLng);
        polylineOptions.add(t);
        mMap.addPolyline(polylineOptions);
        pausePolylineOpt.add(t);
        mMap.addPolyline(pausePolylineOpt);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(t, 18));
    }


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


        JSONObject jsonDT;
        try {
            jsonDT = new JSONObject(getIntent().getStringExtra("jsonString"));
            Log.d("<>",jsonDT.toString());
            WebSocketService.roomName = jsonDT.getString("roomName");
            JSONArray tmp = jsonDT.getJSONArray("users");
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<MultiUserModel>>(){}.getType();
            userList = gson.fromJson(tmp.toString(),type);
            /*for(int i =0; i< tmp.length();i++) {
                MultiUserModel mdl = new MultiUserModel();
                mdl.username = tmp.get(i).getClass()
            }*/
            Log.d("<<>>",userList.toString());
            for(int i =0; i<3;i++) {
                if (i == 0) {
                    MarkerOptions tempOptLab = new MarkerOptions();
                    BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.laboratory);
                    Bitmap b = bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, 200, 200, false);
                    tempOptLab.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));



                    tempOptLab.position(new LatLng(userList.get(i).latitude, userList.get(i).longitude));
                    //BitmapDrawable bitmapdraw=(BitmapDrawable)this.getResources().getDrawable(R.drawable.ic_laboratory);
                    //Bitmap b=bitmapdraw.getBitmap();
                    //Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 150, false);
                    //tempOptLab.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                    labMarker = mMap.addMarker(tempOptLab);
                }
                MarkerOptions tempOpt = new MarkerOptions();
                /*
                * BitmapDrawable bitmapdraw=(BitmapDrawable)this.getResources().getDrawable(R.drawable.ic_laboratory);
                    Bitmap b=bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 150, false);
                    tempOpt.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                *
                * */
                tempOpt.position(new LatLng(userList.get(i).latitude, userList.get(i).longitude));
                Marker tempMk = mMap.addMarker(tempOpt);
                markerList.add(tempMk);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MultiMapActivity.this);
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
                Intent intent = new Intent(MultiMapActivity.this, MultiMapActivity.class);
                startActivity(intent);
                finish();

            }
        });
        builder.create().show();
    }

    void checkRunTimePermission() {
        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MultiMapActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MultiMapActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED || hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MultiMapActivity.this, REQUIRED_PERMISSIONS[0])) {
                Toast.makeText(MultiMapActivity.this, R.string.need_location, Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(MultiMapActivity.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
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
                    Toast.makeText(MultiMapActivity.this, R.string.permission_error_re_run, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    //binding.currentLocation.setText("위치 알 수 없음");
                    Toast.makeText(MultiMapActivity.this, R.string.permission_error_allow_setting, Toast.LENGTH_LONG).show();
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
        if (requestCode == 1000) {
            if (resultCode == RESULT_OK) {
                storyUserSelect = data.getIntExtra("result", -1);
            } else {
                storyUserSelect = -1;
            }
            afterReadStory();
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

    public void btnVisibilityChange(Button btn) {
        if (btn.getVisibility() == View.VISIBLE) {
            btn.setVisibility(View.GONE);
        } else {
            btn.setVisibility(View.VISIBLE);
        }
    }

    public void playTTS(String text) {
        tts.setPitch(1.0f);
        tts.setSpeechRate(1.0f);
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, text);
    }

    public static void splayTTS(String text) {
        tts.setPitch(1.0f);
        tts.setSpeechRate(1.0f);
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, text);
    }

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

                if (isRunning) {
                    if (isZombieCreating && timeToSec1 == 60) {
                        createZombie();
                    }
                }




            } catch (Exception e) {
                Log.e("<MapActivity, Time Handler> : ", e.getMessage());
            }


        }
    };

    public static void minusHPAndCheck() {

        MultiMapActivity.HP = MultiMapActivity.HP - MultiMapActivity.minusHp;
        updateHpUI();
        splayTTS(mctx.getString(R.string.hitted_zb));
        if (HP <= 0) {
            stopZombie();
            splayTTS(mctx.getString(R.string.died));
            sstop(); //종료하고 싶으면
        }
    }

    public void getDistanceBetweenLatLngs(LatLng l1) {
        Location ll1 = new Location("ll1");
        ll1.setLatitude(l1.latitude);
        ll1.setLongitude(l1.longitude);

        for(int i = 0; i<itemMarkerList.size();i++) {
            Location ll2 = new Location("ll2");
            ll2.setLatitude(itemMarkerList.get(i).getPosition().latitude);
            ll2.setLongitude(itemMarkerList.get(i).getPosition().longitude);

            double len = Math.round(ll1.distanceTo(ll2) *100) / 100.0; //단위는 1m
            if(len <= 5) {
                WebSocketService.mSocket.emit("getItem","{\"roomName\":\""+WebSocketService.roomName+"\",\"idx\":"+i+"}");
                itemMarkerList.get(i).remove();
                itemMarkerList.remove(i);
            }
        }
    }




    @MainThread
    public static void updateHpUI() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                binding.progress2.setProgress(HP);
            }
        });
    }

    /* no use
    private void readStory() {

        Intent u = new Intent(MapActivity.this, ExerciseStoryActivity.class);
        u.putExtra("storyBody", "TEST STORY");
        u.putExtra("negative", "NO");
        u.putExtra("positive", "YES");
        startActivityForResult(u, 1000);
    }*/

    private void afterReadStory() {

    }


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

    public void createZombie() {
        MultiZombieModel mZombie = new MultiZombieModel(new LatLng(currentLat, currentLng), zombieListCurrentPos, MultiMapActivity.this);
        zombieListCurrentPos++;
        zombieList.add(mZombie);
        playTTS(getString(R.string.create_zb));
    }

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
