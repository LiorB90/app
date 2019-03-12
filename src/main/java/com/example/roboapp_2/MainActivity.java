package com.example.roboapp_2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ListView;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Button connectBtn;
    private Button startBtn;
    private Button stopBtn;
    private Button showMapBtn;
    private Button quitBtn;
    private char[][] global_room;

    ///// Test /////
    private BluetoothAdapter BTA;
    private BluetoothDevice BTD;
    //private Set<BluetoothDevice> pairedDevices;
    private boolean isConnected;

    private OutputStream outputStream;
    private InputStream inStream;

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
    ///// End Test //////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG,"On Create");

        global_room = new char[6][6];
        connectBtn = (Button) findViewById(R.id.btn_connect);
        startBtn = (Button) findViewById(R.id.btn_start);
        stopBtn = (Button) findViewById(R.id.btn_stop);
        showMapBtn = (Button) findViewById(R.id.btn_map);
        quitBtn = (Button) findViewById(R.id.btn_quit);
        ///// Test /////
        isConnected = false;
        BTA = BluetoothAdapter.getDefaultAdapter();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter);
        ///// End Test //////

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

//        startBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                ///// Test //////
//                ///// End Test //////
//            }
//        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBtn.setText("Start");
                startBtn.setTextColor(Color.BLACK);
                stopBtn.setEnabled(false);
                //Intent map = new Intent(MainActivity.this, MapActivity.class);
                //startActivity(map);
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
                    t = "map" + i;
                    System.out.println(t);
                    temp = global_room[i];
                    map.putExtra("map" + i, global_room[i]);
                }
                map.putExtra("length", global_room.length);
                map.putExtra("width", global_room[0].length);
                startActivity(map);
            }
        });

    }

///////////////// Test //////////////////////
    public void Start(View v) throws IOException{
        Log.d(TAG,"what is up manniga");
        startBtn.setText("Running");
        startBtn.setTextColor(Color.GREEN);
        stopBtn.setEnabled(true);
//        createMAT();
        showMapBtn.setEnabled(true);
//        sendBT();
        sendBTClass();
    }
///////////////// End Test //////////////////////


//    @Override
//    protected void onStart() {
//        super.onStart();
//        Log.d(TAG,"On Start");
//    }

    protected void createMAT(){
        char[][] room = new char[6][6];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                if (i == 0 || i == 5) {
                    room[i][j] = 'w';
                } else {
                    if (j == 0 || j == 5) {
                        room[i][j] = 'w';
                    } else {
                        room[i][j] = 'k';
                    }
                }
            }
        }
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 6; j++)
                global_room[i][j] = room[i][j];
        System.out.println("done");
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
            BluetoothConnector.BluetoothSocketWrapper socket = BTC.connect();
        }catch(Exception e){
            Log.d(TAG,e.getMessage());
        }

    }

    private void sendBT(){
        try {
            Log.d(TAG,"Start Init");
            init();

            Log.d(TAG,"Start Writing");
            write("s");

            Log.d(TAG,"Ass");
        }catch (Exception e){
            Log.d(TAG,e.getMessage());
        }
    }

    private void init() throws IOException {
        BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
        if (blueAdapter != null) {
            if (blueAdapter.isEnabled()) {
                Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();

                if(bondedDevices.size() > 0) {
                    //Object[] devices = (Object []) bondedDevices.toArray();
                    ParcelUuid[] uuid = BTD.getUuids();
                    BluetoothSocket socket = BTD.createRfcommSocketToServiceRecord(uuid[0].getUuid());
                    socket.connect();
                    outputStream = socket.getOutputStream();
                    inStream = socket.getInputStream();
                }

                Log.e("error", "No appropriate paired devices.");
            } else {
                Log.e("error", "Bluetooth is disabled.");
            }
        }
    }

    public void write(String s) throws IOException {
        outputStream.write(s.getBytes());
    }

    public void run() {
        final int BUFFER_SIZE = 1024;
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytes = 0;
        int b = BUFFER_SIZE;

        while (true) {
            try {
                bytes = inStream.read(buffer, bytes, BUFFER_SIZE - bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
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

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        Log.d(TAG,"onRestart");
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        Log.d(TAG,"onSaveInstanceState");
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        Log.d(TAG,"onRestoreInstanceState");
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }
}
