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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.newhorizon.pulsewristband.R;
import com.newhorizon.pulsewristband.conf.ConfiguracaoFirebase;
import com.newhorizon.pulsewristband.helper.Base64CD;
import com.newhorizon.pulsewristband.model.Dado;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;


public class Menu extends AppCompatActivity {
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final static int REQUEST_ENABLE_BT = 1;

    Button conectar;
    Button ler;
    static TextView conectado;

    private InputStream inStream;
    private OutputStream outStream;

    BluetoothSocket socket = null;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference reference = ConfiguracaoFirebase.getFirebaseDatabase();
    DatabaseReference dados;

    Dado dado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        String codeEmail = Base64CD.codeBase64(mAuth.getCurrentUser().getEmail());
        dados = reference.child("usuarios").child(codeEmail).child("dados");


        conectar = findViewById(R.id.b_connect);
        ler = findViewById(R.id.b_ler);
        conectado = findViewById(R.id.connection);

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
                        conectado.setTextColor(0xFF00FF00);
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
                        dado = new Dado(Integer.parseInt(ajustado[0]),Integer.parseInt(ajustado[1]));
                        dados.setValue(dado);
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



