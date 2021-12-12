package com.example.survirun;

import static android.widget.Toast.LENGTH_LONG;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ExerciseMulti extends AppCompatActivity {
    private Socket mSocket;
    Gson gson = new Gson();
    String  jsonData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_multi);
        try {
            mSocket = IO.socket("https://testserver.iou040428.repl.co");
            mSocket.connect();
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on("test",test);
            mSocket.on("UpdataCoordinate",UpdataCoordinate);




            // To know if the new user entered the room.
            //mSocket.on("updateChat", onUpdateChat); // To update if someone send a message to chatroom
            //mSocket.on("userLeftChatRoom", onUserLeft); // To know if the user left the chatroom.            Log.d("asdf", "asdf");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.d("안됨", "안됨");
        }

    }
    Emitter.Listener onConnect=new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            // Gson changes data object to Json type.
            mSocket.emit("subscribe", jsonData);





        }
    };
    private  Emitter.Listener test=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("adsf","asdf");
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run()
                {
                    Toast.makeText(getApplicationContext(),args[0].toString(), LENGTH_LONG).show();
                }
            }, 0);


        }
    };

    private  Emitter.Listener UpdataCoordinate=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run()
                {
                    Toast.makeText(getApplicationContext(),args[0].toString(), LENGTH_LONG).show();
                }
            }, 0);

        }
    };






}