package com.newhorizon.pulsewristband.view;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.newhorizon.pulsewristband.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    InputStreamReader reader;


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
                leituraDados();
                return;
            }
        });
    }

    private void leituraDados() {
        byte[] buffer = new byte[1025];
        boolean x = true;
        int bytes = 0;
        String a = "";
        while(true){
            try {
//
                if (inStream.available() > 5){
                    bytes = inStream.read(buffer);
                    a = new String(buffer, 0, bytes);
                    System.out.println("print: "+a);
                    if (a.matches("([0-9]+:[0-1]\r\n)")){
                        a = a.replaceAll("\\r\\n", "");
                        String[] ajustado = a.split(":");
                        System.out.println("Regex: "+ajustado[0]);
                    }else{
                        Thread.sleep(200);
                    }
                }


            }catch (Exception e){
                x = false;
            }



        }
    }


}



