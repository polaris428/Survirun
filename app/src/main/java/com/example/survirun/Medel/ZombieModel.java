package com.example.survirun.Medel;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.survirun.Activity.MapActivity;
import com.example.survirun.R;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ZombieModel {
    public int markerImageResourceId;
    public final int LOCATION_DIFF = 100;
    public MarkerOptions options;


    public void updateCurrentZombieLocation(LatLng humanLocation) {
        /* 여기에서 마커위치 업데이트. */


        /* 마지막에 거리 검사 */
        if(getZombieToHumanDistance(humanLocation) <= 10) { //단위가 몇이였더라..
            /* 종료코드 */
        }
    }
    public double getZombieToHumanDistance(LatLng humanLocation) {
        Location h = new Location("h");
        h.setLatitude(humanLocation.latitude);
        h.setLongitude(humanLocation.longitude);
        Location z = new Location("z");
        z.setLatitude(this.options.getPosition().latitude);
        z.setLongitude(this.options.getPosition().longitude);
        return Math.round(h.distanceTo(z)*100)/100.0;
    }

    public void playZombieSound() {

    }

    public ZombieModel(LatLng human) {//, int markerImageResourceId) {
        //this.markerImageResourceId = markerImageResourceId;
        double diffLat = latIndiff(LOCATION_DIFF);
        double diffLon = lonIndiff(human.latitude, LOCATION_DIFF);
        LatLng minLatLng = new LatLng(human.latitude-diffLat, human.longitude-diffLon);
        LatLng maxLatLng = new LatLng(human.latitude+diffLat, human.longitude+diffLon);
        double randomLat = (Math.random() * (maxLatLng.latitude - minLatLng.latitude + 1) + minLatLng.latitude);
        double randomLng = (Math.random() * (maxLatLng.longitude - minLatLng.longitude + 1) + minLatLng.longitude);
        this.options.position(new LatLng(randomLat,randomLng));
        this.options.title("zombie");
        //this.options.icon(BitmapDescriptorFactory.fromResource(markerImageResourceId));
    }


    /* 반경 xm의 위도경도 구하는 func +-로 사용*/
    private double latIndiff(int diff) { //단위는 m
        final int earth = 6371000;
        return (diff * 360.0) / (2*Math.PI*earth);
    }

    public double lonIndiff(double _currentLat, int diff){
        final int earth = 6371000;
        double ddd = Math.cos(0);
        double ddf = Math.cos(Math.toRadians(_currentLat));
        return (diff*360.0) / (2*Math.PI*earth*Math.cos(Math.toRadians(_currentLat)));
    }

    @SuppressLint("HandlerLeak")
    public Handler zombieHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if(MapActivity.isRunning) {
                updateCurrentZombieLocation(new LatLng(MapActivity.currentLat, MapActivity.currentLng));
            }

        }
    };

    public class zombieMoveThread implements Runnable {
        @Override
        public void run() {
            try {
                int runTime = 0; //sec
                while(true) {
                    Message msg = new Message();
                    if(MapActivity.isRunning) {
                        msg.arg1 = runTime++;
                    }
                    zombieHandler.handleMessage(msg);
                    Thread.sleep(1000);

                }
            } catch (InterruptedException e) {
                Log.d(">",e.getMessage());
            }
        }
    }


}
