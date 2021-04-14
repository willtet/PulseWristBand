package com.newhorizon.pulsewristband;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


public class Menu extends AppCompatActivity {
    ConnectThread connect;

    private static final String TAG = "MY_APP_DEBUG_TAG";
    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final static int REQUEST_ENABLE_BT = 1;


    Button conectar;
    Button ler;
    Button escrever;
    static TextView conectado;
    TextView test;

    private InputStream inStream;
    private OutputStream outStream;
    private byte[] mmBuffer; // mmBuffer store for the stream

    BluetoothSocket socket = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        conectar = findViewById(R.id.b_connect);
        ler = findViewById(R.id.b_ler);
        escrever = findViewById(R.id.b_escrever);
        conectado = findViewById(R.id.connection);
        test = findViewById(R.id.test);

        mmBuffer = new byte[0];
        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice("00:20:05:00:05:E1");



        conectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }

                try {
                    socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                    socket.connect();

                    inStream = socket.getInputStream();
                    outStream = socket.getOutputStream();

                    if (socket.isConnected()){
                        conectado.setText("Connectado");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(1000);
                } catch (Exception E) {
                    E.printStackTrace();
                }

            }
        });

        ler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                byte[] buffer = new byte[1024];
                boolean x = true;
                int bytes;
                int counter = 0;
                int media = 0;
                int parse = 0;
                while(true){
                    try {
                        bytes = inStream.read(buffer);
                        String a = new String(buffer, 0, bytes);

                        if(a.contains("c")){
                            while(x){
                                synchronized (inStream){
                                    bytes = inStream.read(buffer);
                                    Thread.sleep(1500);
                                }
                                a = new String(buffer, 0, bytes);
                                System.out.println(a);
                                if (a.contains("x")){
                                    break;
                                }
                                try {
                                    parse = Integer.parseInt(a);
                                    media += parse;
                                    counter++;
                                }catch (final NumberFormatException e){
                                    parse = 0;
                                }

                            }
                            System.out.println("media: "+media/counter);
                        }
                    }catch (Exception e){
                        x = false;
                    }
                }



            }
        });
    }





}



