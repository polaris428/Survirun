package com.example.survirun.server;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class WebSocketService extends AppCompatActivity {
    private Socket mSocket;
    private Gson gson = new Gson();
    public void socketConnect(){
        try {


            mSocket = IO.socket("https://testserver.iou040428.repl.co");
            mSocket.connect();
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on("UpdataCoordinate",updataCoordinate);
        } catch (URISyntaxException e) {
            e.printStackTrace();


        }
    }

    //사용자 위치 업데이-트
    private Emitter.Listener updataCoordinate = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                JSONObject userObj= new JSONObject(args[0].toString());
            }catch (JSONException err){
                Log.d("Error", err.toString());
            }

            /*유저 찾아서 업데이트*/
        }
    };

    private Emitter.Listener onConnect = (args) -> {
        runOnUiThread(() -> {
            Log.i("성공", "connected");

        });
    };

}
