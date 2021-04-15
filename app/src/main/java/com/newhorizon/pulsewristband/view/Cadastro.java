package com.newhorizon.pulsewristband.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.newhorizon.pulsewristband.R;
import com.newhorizon.pulsewristband.conf.ConfiguracaoFirebase;
import com.newhorizon.pulsewristband.model.Usuario;

public class Cadastro extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private EditText nome;
    private EditText email;
    private EditText senha;
    private EditText confirmar;
    private RadioGroup grupo;
    private RadioButton escolhido;
    private Button cadastrar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        nome = findViewById(R.id.cadastro_nome);
        email = findViewById(R.id.cadastro_email);
        senha = findViewById(R.id.cadastro_senha);
        grupo = findViewById(R.id.grupo);
        confirmar = findViewById(R.id.cadastro_confirmar);


        cadastrar = findViewById(R.id.bt_cadastrar);

        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // if (!nome.getText().toString().isEmpty() || !email.getText().toString().isEmpty() || !senha.getText().toString().isEmpty()){
                    //if (senha.toString().equals(confirmar.toString())) {
                        Usuario usuario = new Usuario(nome.getText().toString(),email.getText().toString(),senha.getText().toString(),escolhido.getText().toString());
                        cadastrar(usuario);
                   // }
                //}else{
                  //  Toast.makeText(Cadastro.this, "Digite corretamente os dados acima!", Toast.LENGTH_SHORT).show();
                //}
            }


        });

    }

    public void checkedButton(View v){
        int id = grupo.getCheckedRadioButtonId();
        escolhido = findViewById(id);

    }

    private void cadastrar(Usuario u) {
        mAuth = ConfiguracaoFirebase.getFirebaseAuth();
        mAuth.createUserWithEmailAndPassword(u.getEmail() ,u.getSenha()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    finish();
                }else{
                    Toast.makeText(Cadastro.this, "Erro. Tente mais tarde!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}