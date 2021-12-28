package com.example.survirun.server;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.L;
import com.example.survirun.R;
import com.example.survirun.activity.exercise.MultiMapActivity;
import com.example.survirun.data.CoordinateData;
import com.example.survirun.data.LatLngData;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;

public class WebSocketService extends AppCompatActivity {
    public static Socket mSocket;
    public static String roomName;
    private static Gson gson = new Gson();
    static SharedPreferences loginSf;
    static String token;
    public static int tempInt = 0;
    //public static void c() { socketConnect()}
    public static void socketConnect(){
        try {
            loginSf = MultiMapActivity.mctx.getSharedPreferences("Login", MODE_PRIVATE);
            token = loginSf.getString("token", "");


            mSocket = IO.socket("https://dicon21.2tle.io");
            mSocket.io().on(Manager.EVENT_TRANSPORT, onTransport);
            mSocket.connect();
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on("UpdataCoordinate",updataCoordinate);
            mSocket.on("subscribe",subscribe);
            mSocket.on("makeBox",makeBox);
            mSocket.on("getItem",getItem);
            mSocket.on("deleteItem",deleteItem);
        } catch (URISyntaxException e) {
            e.printStackTrace();


        }
    }

    //사용자 위치 업데이-트
    private static Emitter.Listener updataCoordinate = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                JSONObject userObj= new JSONObject(args[0].toString());
                Log.d("<TS1>",userObj.getString("userName")+userObj.getDouble("latitude")+userObj.getDouble("longitude"));
                for(int i=0;i<3;i++) {
                    tempInt = i;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            int idx= tempInt;
                            try {
                                if(MultiMapActivity.userList.get(idx).username.equals(userObj.getString("userName"))) {
                                    MultiMapActivity.markerList.get(idx).remove();
                                    MarkerOptions temp1 = new MarkerOptions();
                                    try {
                                        temp1.position(new LatLng(userObj.getDouble("latitude"), userObj.getDouble("longitude")));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    ((Activity)MultiMapActivity.mctx).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            MultiMapActivity.markerList.set(idx, MultiMapActivity.mMap.addMarker(temp1));
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }

            }catch (JSONException err){
                Log.d("Error", err.toString());
            }

            /*유저 찾아서 업데이트*/
        }
    };

    //헤더 보내기 위해
    private static Emitter.Listener onTransport = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            Transport transport = (Transport)args[0];
            transport.on(Transport.EVENT_REQUEST_HEADERS, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    @SuppressWarnings("unchecked")
                    Map<String, List<String>> headers = (Map<String, List<String>>) args[0];
                    headers.put("x-access-token", Arrays.asList(token));
                }
            }).on(Transport.EVENT_RESPONSE_HEADERS, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                }
            });
        }
    };

    private static Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };

    private static Emitter.Listener getItem = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };

    private static Emitter.Listener deleteItem = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };

    private static Emitter.Listener subscribe=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("실행됨",args[0].toString());
        }
    };

    private static Emitter.Listener makeBox=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run()
                {
                    /*
                    Toast.makeText(getApplicationContext(),args[0].toString(), Toast.LENGTH_SHORT).show();
                    CoordinateData coordinateData= gson.fromJson(args[0].toString(),CoordinateData.class);
                    Log.d("a",coordinateData.latitude+"\n"+coordinateData.longitude);
                    LatLng S = new LatLng(coordinateData.latitude+0.0009, coordinateData.longitude);

                    MarkerOptions markerOptions = new MarkerOptions();         // 마커 생성
                    markerOptions.position(S);
                    markerOptions.title("박스");                         // 마커 제목
                    markerOptions.snippet("아이템");         // 마커 설명
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MultiMapActivity.mMap.addMarker(markerOptions);
                        }
                    });*/
                    //

                    /*아이템 */

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            LatLngData latlngData = gson.fromJson(args[0].toString(), LatLngData.class);
                            MarkerOptions tempMarkerOpt = new MarkerOptions();
                            tempMarkerOpt.position(new LatLng(latlngData.latitude, latlngData.longitude));

                            BitmapDrawable bitmapdraw=(BitmapDrawable)MultiMapActivity.mctx.getResources().getDrawable(R.drawable.box);
                            Bitmap b=bitmapdraw.getBitmap();
                            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 150, false);
                            tempMarkerOpt.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

                            ((Activity)MultiMapActivity.mctx).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MultiMapActivity.itemMarkerList.add(MultiMapActivity.mMap.addMarker(tempMarkerOpt));
                                }
                            });

                        }
                    });

                    /* 아이템 마커뷰 이미지 지정 */
                    //BitmapDrawable bdraw = (BitmapDrawable)MultiMapActivity.mctx.getResources().getDrawable();

                }
            }, 0);

        }
    };

}
