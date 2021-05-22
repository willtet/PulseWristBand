package com.newhorizon.pulsewristband.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.newhorizon.pulsewristband.R;
import com.newhorizon.pulsewristband.conf.ConfiguracaoFirebase;
import com.newhorizon.pulsewristband.helper.Base64CD;
import com.newhorizon.pulsewristband.model.Dado;
import com.newhorizon.pulsewristband.model.Usuario;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Inicial extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference reference = ConfiguracaoFirebase.getFirebaseDatabase();
    DatabaseReference cuidador;
    DatabaseReference paciente;


    TextView bemvindo;
    EditText campoPesquisa;
    Button pesquisar;
    TextView bpm;
    TextView pacienteNome;
    TextView queda;
    TextView quedaHora;

    int notifyID = 1;
    String CHANNEL_ID = "my_channel_01";

    ArrayList<Usuario> usuarios = new ArrayList<>();
    boolean found = false;
    Date currentTime;
    SimpleDateFormat formater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicial);
        createNotificationChannel();


        bemvindo = findViewById(R.id.tx_nome);
        campoPesquisa = findViewById(R.id.ed_emailPaciente);
        pesquisar = findViewById(R.id.bt_procurarPaciente);

        pacienteNome = findViewById(R.id.id_nomePaciente);
        bpm = findViewById(R.id.tx_batimento);
        queda = findViewById(R.id.text_queda);
        quedaHora = findViewById(R.id.horario_queda);



        String codeEmail = Base64CD.codeBase64(mAuth.getCurrentUser().getEmail());
        cuidador = reference.child("usuarios").child(codeEmail).child("nome");
        cuidador.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bemvindo.setText("Olá "+snapshot.getValue().toString()+",");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        pesquisar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!campoPesquisa.getText().toString().isEmpty()){

                    String codeEmail = Base64CD.codeBase64(campoPesquisa.getText().toString());
                    paciente = reference.child("usuarios");
                    paciente.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dadas: snapshot.getChildren()) {
                                if (dadas.getKey().toString().equals(codeEmail)){
                                    found = true;
                                }
                            }

                            if (found){
                                paciente = reference.child("usuarios").child(codeEmail);
                                paciente.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        bpm.setText(snapshot.child("dados").child("cardio").getValue().toString());
                                        pacienteNome.setText(snapshot.child("nome").getValue().toString());
                                        if(snapshot.child("dados").child("queda").getValue().toString().equals("1")){
                                            alertNotificatite(snapshot.child("nome").getValue().toString(), snapshot.child("dados").child("latitude").getValue().toString(), snapshot.child("dados").child("longitude").getValue().toString());
                                            queda.setTextColor(0xFFFF0000);
                                            queda.setText("Queda Detectada");
                                            quedaHora.setText(snapshot.child("dados").child("horarioQueda").getValue().toString());
                                        }else{
                                            queda.setTextColor(0xFF00FF00);
                                            queda.setText("Não");
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });

    }




    private void alertNotificatite(String pacientes, String latitude, String longitude){
        //Define sound URI
       // Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri gmmIntentUri = Uri.parse("google.streetview:cbll="+latitude+","+longitude);
        Intent intent = new Intent(Intent.ACTION_VIEW,gmmIntentUri);
        intent.setPackage("com.google.android.apps.maps");

        PendingIntent pending = PendingIntent.getActivity(getBaseContext(),100, intent, PendingIntent.FLAG_UPDATE_CURRENT);



        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(),CHANNEL_ID)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_disabled)
                .setContentTitle("Queda detectada!")
                .setContentText("Informamos que houve uma queda do "+pacientes+"! Toque aqui para ver no mapa.")
        //        .setSound(soundUri)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(pending)
                .setAutoCancel(true);



        NotificationManager notificationManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(9000, builder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}