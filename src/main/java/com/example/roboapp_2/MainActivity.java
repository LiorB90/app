package com.example.roboapp_2;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Button connectBtn;
    private Button startBtn;
    private Button stopBtn;
    private Button showMapBtn;
    private Button quitBtn;
    private char[][] global_room;

    private BluetoothAdapter BTA;
    private BluetoothDevice BTD;
    private boolean isConnected;
    private boolean isMap;
    BluetoothConnector.BluetoothSocketWrapper socket;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            BTD = device;

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.d(TAG,"ACTION FOUND");
            }
            else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                Log.d(TAG,"ACTION_ACL_CONNECTED");
                Log.d(TAG,"Connected to: " + BTD.getName() + ", Address: " + BTD.getAddress());
                isConnected = true;
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d(TAG,"ACTION_DISCOVERY_FINISHED");
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                Log.d(TAG,"ACTION_ACL_DISCONNECT_REQUESTED");
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                Log.d(TAG,"ACTION_ACL_DISCONNECTED");
                isConnected = false;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG,"On Create");

        connectBtn = (Button) findViewById(R.id.btn_connect);
        startBtn = (Button) findViewById(R.id.btn_start);
        stopBtn = (Button) findViewById(R.id.btn_stop);
        showMapBtn = (Button) findViewById(R.id.btn_map);
        quitBtn = (Button) findViewById(R.id.btn_quit);
        isConnected = false;
        isMap = false;
        BTA = BluetoothAdapter.getDefaultAdapter();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter);

        startBtn.setEnabled(true);
        startBtn.setEnabled(false);
        stopBtn.setEnabled(false);
        showMapBtn.setEnabled(false);


        if (BTA.isEnabled() && isConnected){
            connectBtn.setText("Connected");
            connectBtn.setTextColor(Color.GREEN);
            startBtn.setEnabled(true);
        }
        else{
            connectBtn.setText("Connect");
            connectBtn.setTextColor(Color.RED);
            startBtn.setEnabled(false);
        }

        quitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.finish();
            }
        });

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent BTSettings = new Intent(Intent.ACTION_MAIN, null);
                BTSettings.addCategory(Intent.CATEGORY_LAUNCHER);
                ComponentName cn = new ComponentName("com.android.settings",
                        "com.android.settings.bluetooth.BluetoothSettings");
                BTSettings.setComponent(cn);
                BTSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(BTSettings);
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBtn.setText("Start");
                startBtn.setTextColor(Color.BLACK);
                stopBtn.setEnabled(false);
            }
        });

        showMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBtn.setText("Start");
                startBtn.setTextColor(Color.BLACK);
                stopBtn.setEnabled(false);
                Intent map = new Intent(MainActivity.this, CanvasActivity.class);

                char[] temp = new char[global_room[0].length];
                String t;
                for (int i = 0; i < global_room.length; i++) {
                    map.putExtra("map" + i, global_room[i]);
                }
                map.putExtra("length", global_room.length);
                map.putExtra("width", global_room[0].length);
                startActivity(map);
            }
        });
    }

    public void Start(View v) throws IOException{
        Log.d(TAG,"Start was Clicked");
        startBtn.setText("Running");
        startBtn.setTextColor(Color.GREEN);
        sendBTClass();
    }

    private int qCounter(String s){
        Log.d(TAG,"In q Counter");
        int count = 0;
        for(int i =0; i < s.length(); i++) {
            if (s.charAt(i) == 'q')
                count++;
        }
        return count;
    }

    private int untilQCounter(String s){
        Log.d(TAG,"In Till Q Counter");
        int count = 0;
        for(int i =0; i < s.length(); i++) {
            if (s.charAt(i)!= 'q')
                count++;
            else{
                break;
            }
        }
        return count;
    }

    protected void createMAT(String s){
        Log.d(TAG,"In Create MAT");
        int numOfQ = qCounter(s);
        int length = numOfQ + 1;
        int width = untilQCounter(s);
        int row=0;

        Log.d(TAG,"Length: "+length+" Width: "+width);
        char[][] room = new char[length][width];
        for(int i=0;i<s.length();){
            int jCount = 0;
            for(int j=0;j<width;j++){
                room[row][j] = s.charAt(i+j);
                jCount++;

            }
            i += jCount+1;
            row++;
        }
        global_room = new char[length][width];
        for (int i = 0; i < length; i++)
            for (int j = 0; j < width; j++)
                global_room[i][j] = room[i][j];
        isMap = true;
    }

    private void sendBTClass(){
        try {

            Log.d(TAG, "In SendBTClass");
            ParcelUuid[] parcels = BTD.getUuids();
            List<UUID> uuids = new ArrayList<UUID>();
            for (ParcelUuid parcel : parcels) {
                uuids.add(parcel.getUuid());
            }
            BluetoothConnector BTC = new BluetoothConnector(BTD, false, BTA, uuids);
            Log.d(TAG, "Created Connector");
            socket = BTC.connect();
            Log.d(TAG,"Connected to"+socket.getRemoteDeviceName());
            workerInput.start();
            write("start");
        }catch(Exception e){
            Log.d(TAG,e.getMessage());
        }
    }

    public void write(String s) throws IOException {
        socket.getOutputStream().write(s.getBytes());
        Log.d(TAG,"Write Successful: " + s);
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG,"In Handler");
            Bundle bundle = msg.getData();
            Log.d(TAG,"Got MSG");
            String string = bundle.getString("msg");
            Log.d(TAG,"Converted to String");
            createMAT(string);
            Log.d(TAG,"Mat Created");
            showMapBtn.setEnabled(true);
        }
    };


    Thread workerInput = new Thread(new Runnable(){
        public void run() {
            final int BUFFER_SIZE = 1024;
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytes = 0;
            int b = BUFFER_SIZE;

            while (true) {
                try {
                    bytes = socket.getInputStream().read(buffer);
                    String text = new String(buffer,0,bytes);
                    Message msg = handler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("msg", text);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, e.getMessage());
                }
            }
        }
    });

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
        if (BTA.isEnabled() && isConnected){
            connectBtn.setText("Connected");
            connectBtn.setTextColor(Color.GREEN);
            startBtn.setEnabled(true);
        }
        else{
            connectBtn.setText("Connect");
            connectBtn.setTextColor(Color.RED);
//            startBtn.setEnabled(false);
        }
        if( isMap){
            showMapBtn.setEnabled(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }
}
