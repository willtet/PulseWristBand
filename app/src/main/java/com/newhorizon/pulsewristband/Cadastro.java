package com.newhorizon.pulsewristband;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

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
        confirmar = findViewById(R.id.cadastro_confirmar);


        cadastrar = findViewById(R.id.bt_cadastrar);

        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (senha.toString().equals(confirmar.toString())){

                }
            }
        });

    }

    public void checkedButton(View v){
        int id = grupo.getCheckedRadioButtonId();
        escolhido = findViewById(id);

    }
}