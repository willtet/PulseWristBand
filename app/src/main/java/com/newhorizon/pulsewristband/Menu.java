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

    private BluetoothSocket socket;
    private InputStream mmInStream;
    private OutputStream mmOutStream;
    private byte[] mmBuffer; // mmBuffer store for the stream

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        conectar = findViewById(R.id.b_connect);
        ler = findViewById(R.id.b_ler);
        escrever = findViewById(R.id.b_escrever);
        conectado = findViewById(R.id.connection);

        mmBuffer = new byte[0];
        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice("00:20:05:00:05:E1");

        conectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }

                connect = new ConnectThread();
                connect.start();
                if (connect.isConnected){
                    conectado.setText("Connectado");
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
                InputStream inputStream = null;
                try {
                    socket = device.createRfcommSocketToServiceRecord(mUUID);
                    socket.connect();

                    inputStream = socket.getInputStream();
                    inputStream.skip(inputStream.available());

                    for (int i = 0; i < 26; i++) {

                        byte b = (byte) inputStream.read();
                        System.out.println((int) b);

                    }
                    socket.close();

                }catch (Exception e){
                    System.out.println(e);
                }
            }
        });


        escrever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    socket = device.createRfcommSocketToServiceRecord(mUUID);
                    socket.connect();
                    System.out.println("Connected");
                    mmOutStream = socket.getOutputStream();

                    try {
                        mmOutStream.write(mmBuffer);
                        System.out.println(mmBuffer);
                        // Share the sent message with the UI activity.
                        Message writtenMsg = handler.obtainMessage(
                                1, -1, -1, mmBuffer);
                        writtenMsg.sendToTarget();
                        System.out.println(writtenMsg.toString());
                    } catch (IOException e) {
                        Log.e(TAG, "Error occurred when sending data", e);

                        // Send a failure message back to the activity.
                        Message writeErrorMsg =
                                handler.obtainMessage(2);
                        Bundle bundle = new Bundle();
                        bundle.putString("toast",
                                "Couldn't send data to the other device");
                        writeErrorMsg.setData(bundle);
                        handler.sendMessage(writeErrorMsg);
                    }

                    socket.close();


                }catch (Exception e){
                    System.out.println(e);
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
            if(dataString.equals("---N"))
                conectado.setText("Ocorreu um erro durante a conexão D:");
            else if(dataString.equals("---S"))
                conectado.setText("Conectado :D");
            else {
            }

        }
    };



}



