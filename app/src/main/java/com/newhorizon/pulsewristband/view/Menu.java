package com.newhorizon.pulsewristband.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.newhorizon.pulsewristband.R;
import com.newhorizon.pulsewristband.conf.ConfiguracaoFirebase;
import com.newhorizon.pulsewristband.helper.Base64CD;
import com.newhorizon.pulsewristband.model.Dado;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


public class Menu extends AppCompatActivity {
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final static int REQUEST_ENABLE_BT = 1;

    Button localizar;
    Button conectar;
    Button ler;
    static TextView conectado;

    private InputStream inStream;

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


        localizar = findViewById(R.id.b_locate);
        conectar = findViewById(R.id.b_locate);
        ler = findViewById(R.id.b_ler);
        conectado = findViewById(R.id.connection);

        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice("00:20:05:00:05:E1");

        localizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });

        conectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }

                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Menu.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    getCurrentLocation();
                }

                try {
                    socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                    socket.connect();

                    inStream = socket.getInputStream();

                    if (socket.isConnected()) {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permissão Negada", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void getCurrentLocation() {
        LocationRequest location = new LocationRequest();
        location.setInterval(10000);
        location.setFastestInterval(3000);
        location.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return ;
        }
        LocationServices.getFusedLocationProviderClient(Menu.this).requestLocationUpdates(
                location,
                new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(Menu.this).removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int lastLocation = locationResult.getLocations().size() - 1;
                            double latitude = locationResult.getLocations().get(lastLocation).getLatitude();
                            double longitude = locationResult.getLocations().get(lastLocation).getLongitude();

                            System.out.println("longitude: "+ longitude);
                            System.out.println("latitude: "+ latitude);

                        }
                    }
                },
                Looper.getMainLooper());
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
                        dado = new Dado(Integer.parseInt(ajustado[0]),Integer.parseInt(ajustado[1]), 0, 0);
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



