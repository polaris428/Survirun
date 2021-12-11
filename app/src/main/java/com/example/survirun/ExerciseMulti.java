package com.example.survirun;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

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



            // To know if the new user entered the room.
            //mSocket.on("updateChat", onUpdateChat); // To update if someone send a message to chatroom
            //mSocket.on("userLeftChatRoom", onUserLeft); // To know if the user left the chatroom.            Log.d("asdf", "asdf");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.d("안됨", "안됨");
        }

    }



}