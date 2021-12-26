package com.example.survirun.server;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class WebSocketService extends AppCompatActivity {
    private Socket mSocket;
    public void socketConnect(){
        try {


            mSocket = IO.socket("https://testserver.iou040428.repl.co");
            mSocket.connect();
            mSocket.on(Socket.EVENT_CONNECT, onConnect);

        } catch (URISyntaxException e) {
            e.printStackTrace();


        }
    }
    private Emitter.Listener onConnect = (args) -> {
        runOnUiThread(() -> {
            Log.i("성공", "connected");

        });
    };

}
