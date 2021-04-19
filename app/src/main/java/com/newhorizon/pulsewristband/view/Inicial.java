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

import java.util.ArrayList;

public class Inicial extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference reference = ConfiguracaoFirebase.getFirebaseDatabase();
    DatabaseReference cuidador;
    DatabaseReference paciente;


    TextView bemvindo;
    EditText campoPesquisa;
    Button pesquisar;
    TextView bpm;
    int[] imagens;
    ImageView imagem ;

    int notifyID = 1;
    String CHANNEL_ID = "my_channel_01";

    ArrayList<Usuario> usuarios = new ArrayList<>();
    boolean found = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicial);
        createNotificationChannel();


        bemvindo = findViewById(R.id.tx_nome);
        campoPesquisa = findViewById(R.id.ed_emailPaciente);
        pesquisar = findViewById(R.id.bt_procurarPaciente);
        bpm = findViewById(R.id.tx_batimento);
        imagens = new int[]{R.drawable.velho,R.drawable.queda};
        imagem = findViewById(R.id.img_queda);



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
                                paciente = reference.child("usuarios").child(codeEmail).child("dados");
                                paciente.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        bpm.setText(snapshot.child("cardio").getValue().toString());
                                        if(snapshot.child("queda").getValue().toString().equals("1")){
                                            imagem.setImageResource(imagens[1]);
                                            alertNotificatite();

                                        }else{
                                            imagem.setImageResource(imagens[0]);
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




    private void alertNotificatite(){
        //Define sound URI
       // Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(this,NotificacaoActivity.class);
        PendingIntent pending = PendingIntent.getActivity(getBaseContext(),100, intent, PendingIntent.FLAG_UPDATE_CURRENT);



        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(),CHANNEL_ID)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_disabled)
                .setContentTitle("Queda detectada!")
                .setContentText("Informamos que houve uma queda do paciente!")
         //       .setSound(soundUri)
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