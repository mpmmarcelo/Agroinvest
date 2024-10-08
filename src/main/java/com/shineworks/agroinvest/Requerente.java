package com.shineworks.agroinvest;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Requerente {

    StringProperty cpf, nome, telefone, endereco;

    public Requerente(){
        this.cpf = new SimpleStringProperty();
        this.nome = new SimpleStringProperty();
        this.telefone = new SimpleStringProperty();
        this.endereco = new SimpleStringProperty();
    }

    public Requerente(String cpf, String nome, String telefone, String endereco){
        this.cpf = new SimpleStringProperty(cpf);
        this.nome = new SimpleStringProperty(nome);
        this.telefone = new SimpleStringProperty(telefone);
        this.endereco = new SimpleStringProperty(endereco);
    }

    public Requerente(StringProperty cpf, StringProperty nome, StringProperty telefone, StringProperty endereco){
        this.cpf = cpf;
        this.nome = nome;
        this.telefone = telefone;
        this.endereco = endereco;
    }

    public String getCpf() {
        return cpf.get();
    }

    public StringProperty cpfProperty() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf.set(cpf);
    }

    public String getNome() {
        return nome.get();
    }

    public StringProperty nomeProperty() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome.set(nome);
    }

    public String getTelefone() {
        return telefone.get();
    }

    public StringProperty telefoneProperty() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone.set(telefone);
    }

    public String getEndereco() {
        return endereco.get();
    }

    public StringProperty enderecoProperty() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco.set(endereco);
    }
}
