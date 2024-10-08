package com.shineworks.agroinvest;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Item {
    StringProperty tipo;
    StringProperty qnt;
    StringProperty equipamento;

    public Item() {
        this.tipo = new SimpleStringProperty();
        this.qnt = new SimpleStringProperty();
        this.equipamento = new SimpleStringProperty();
    }

    public Item(String tipo, String qnt, String equipamento) {
        this.tipo = new SimpleStringProperty(tipo);
        this.qnt = new SimpleStringProperty(qnt);
        this.equipamento = new SimpleStringProperty(equipamento);
    }

    public Item(StringProperty tipo, StringProperty qnt, StringProperty equipamento) {
        this.tipo = tipo;
        this.qnt = qnt;
        this.equipamento = equipamento;
    }

    public String getTipo() {
        return tipo.get();
    }

    public StringProperty tipoProperty() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo.set(tipo);
    }

    public String getQnt() {
        return qnt.get();
    }

    public StringProperty qntProperty() {
        return qnt;
    }

    public void setQnt(String qnt) {
        this.qnt.set(qnt);
    }

    public String getEquipamento() {
        return equipamento.get();
    }

    public StringProperty equipamentoProperty() {
        return equipamento;
    }

    public void setEquipamento(String equipamento) {
        this.equipamento.set(equipamento);
    }
}
