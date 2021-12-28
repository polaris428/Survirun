package com.example.survirun.activity.exercise;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.survirun.R;
import com.example.survirun.activity.MainActivity;
import com.example.survirun.data.EnqueueSocketData;
import com.example.survirun.databinding.ActivityMultiMapBinding;
import com.example.survirun.databinding.ActivityQueueBinding;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;

public class QueueActivity extends AppCompatActivity {

    private ActivityQueueBinding binding;
    private Socket mSocket;
    private Gson gson = new Gson();
    SharedPreferences loginSf;
    String token;
    private boolean isEnqueue = false;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQueueBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        dialog = new Dialog(QueueActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog);


        loginSf = getSharedPreferences("Login", MODE_PRIVATE);
        token = loginSf.getString("token", "");
        initAndConnectSocket();
        enqueue();

        final AnimationDrawable drawableCommunicate =
                (AnimationDrawable) binding.communicateImageView.getBackground();
        drawableCommunicate.start();


    }

    private void initAndConnectSocket() {
        try {

            IO.Options opts = new IO.Options();
            mSocket = IO.socket("https://dicon21.2tle.io",opts);
            mSocket.io().on(Manager.EVENT_TRANSPORT, onTransport);
            //mSocket.
            mSocket.connect();
            mSocket.on(Socket.EVENT_CONNECT,onConnect);
            mSocket.on("roomCreated",matched);
        } catch(Exception e) {
            e.printStackTrace();
            Log.e("<Queue>","Something wrong");
        }
    }

    private void enqueue() {
        if(isEnqueue) {
           return;
        }
        isEnqueue = true;
        EnqueueSocketData enqueueSocketData = new EnqueueSocketData();
        GpsTracker tempGPS = new GpsTracker(QueueActivity.this);
        Log.d("QueueCounter >","Press");
        enqueueSocketData.latitude = tempGPS.getLatitude();
        enqueueSocketData.longitude = tempGPS.getLongitude();
        mSocket.emit("enqueue",gson.toJson(enqueueSocketData));
    }

    @Override
    public void onBackPressed() {
        showDialog();
    }

    @Override
    protected void onDestroy() {
        leaveQueue();
        super.onDestroy();

    }

    public void showDialog() {
        Button yesButton = dialog.findViewById(R.id.yes_button);
        Button cancelButton = dialog.findViewById(R.id.cancel_button);
        TextView textView = dialog.findViewById(R.id.explain_textView);
        textView.setText(R.string.disconnect);
        dialog.show();
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        yesButton.setOnClickListener(v -> {
            leaveQueue();
            dialog.dismiss();
            super.onBackPressed();
        });
    }

    // 뒤로가는 경우 반드시 큐 아웃 요청
    private void leaveQueue() {
        mSocket.disconnect();
    }

    /* 여기서부터는 리스너 */
    //헤더 보내기 위해
    private Emitter.Listener onTransport = new Emitter.Listener() {
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

    //연결되었을 때
    Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            enqueue();
        }
    };

    // 큐(매칭) 잡혔을 때
    Emitter.Listener matched = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            Intent mIntent = new Intent(QueueActivity.this, MultiMapActivity.class);
            mIntent.putExtra("jsonString", args[0].toString());
            startActivity(mIntent);

        }
    };



}