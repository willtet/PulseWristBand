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
    static TextView test;

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
                try {
                    outStream.write("AT+NAME".getBytes());
                    byte[] buffer = new byte[256];
                    int bytes;
                    int bytesRead = -1;

                    int length = inStream.read(buffer);
                        bytes = inStream.read(buffer, 0, length);
                    System.out.println(bytes);
                        System.out.println(new String(buffer, 0,length));
                }catch (Exception e){

                }
            }
        });

        escrever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    System.out.println("AT+NAME".getBytes());
                    outStream.write("AT+NAME".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }



    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            /* Esse método é invocado na Activity principal
                sempre que a thread de conexão Bluetooth recebe
                uma mensagem.
             */
            Bundle bundle = msg.getData();
            byte[] data = bundle.getByteArray("data");
            String dataString= new String(data);

            /* Aqui ocorre a decisão de ação, baseada na string
                recebida. Caso a string corresponda à uma das
                mensagens de status de conexão (iniciadas com --),
                atualizamos o status da conexão conforme o código.
             */
            if(dataString.equals("---N")){
                conectado.setText("Ocorreu um erro durante a conexão D:");
                test.setText(dataString);
            }else if(dataString.equals("---S")) {
                conectado.setText("Conectado :D");
                test.setText(dataString);
            }else {
            }

        }
    };



}



