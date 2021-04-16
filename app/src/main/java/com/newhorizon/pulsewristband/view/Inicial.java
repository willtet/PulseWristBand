package com.newhorizon.pulsewristband.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

    ArrayList<Usuario> usuarios = new ArrayList<>();
    boolean found = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicial);


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
}