package com.newhorizon.pulsewristband.model;

import com.google.firebase.database.DatabaseReference;
import com.newhorizon.pulsewristband.R;
import com.newhorizon.pulsewristband.conf.ConfiguracaoFirebase;

public class Usuario {

    private String id;
    private String nome;
    private String email;
    private String senha;
    private String publico;
    private Dado dados;

    public Usuario(String nome, String email, String senha, String publico) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.publico = publico;
    }

    public void salvar(){
        DatabaseReference reference = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuario = reference.child("usuarios").child(getId());
        usuario.setValue(this);
    }

    public Dado getDados() {
        return dados;
    }

    public void setDados(Dado dados) {
        this.dados = dados;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getPublico() {
        return publico;
    }

    public void setPublico(String publico) {
        this.publico = publico;
    }
}
