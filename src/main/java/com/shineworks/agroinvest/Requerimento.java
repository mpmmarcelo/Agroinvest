package com.shineworks.agroinvest;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Requerimento {
    private StringProperty numeroRequerimento;
    private StringProperty numeroCI;
    private StringProperty numeroProtocolo;
    private Requerente requerente;
    private Imovel imovel;
    private ObservableList<Item> itens;
    private LocalDate dataRequerimento;
    private LocalDate dataCI;
    private LocalDate dataAutorizacao;
    private StringProperty isento;




    public Requerimento() {
        numeroRequerimento = new SimpleStringProperty("");
        numeroCI = new SimpleStringProperty("");
        numeroProtocolo = new SimpleStringProperty("");
        requerente = new Requerente();
        imovel = new Imovel();
        itens = FXCollections.observableArrayList();
        dataRequerimento = LocalDate.now();
        dataCI = LocalDate.now();
        dataAutorizacao = LocalDate.now();
        isento = new SimpleStringProperty("NÃO ISENTO"); //NÃO ISENTO, CONSERVAÇÃO DE SOLOS, MORADIA RURAL
    }

    public String getNumeroRequerimento() {
        return numeroRequerimento.get();
    }

    public StringProperty numeroRequerimentoProperty() {
        return numeroRequerimento;
    }

    public void setNumeroRequerimento(String numeroRequerimento) {
        this.numeroRequerimento.set(numeroRequerimento);
    }

    public String getNumeroCI() {
        return numeroCI.get();
    }

    public StringProperty numeroCIProperty() {
        return numeroCI;
    }

    public void setNumeroCI(String numeroCI) {
        this.numeroCI.set(numeroCI);
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo.get();
    }

    public StringProperty numeroProtocoloProperty() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo.set(numeroProtocolo);
    }

    public String getIsento() {
        return isento.get();
    }

    public StringProperty isentoProperty() {
        return isento;
    }

    public void setIsento(String isento) {
        this.isento.set(isento);
    }

    public Requerente getRequerente() {
        return requerente;
    }

    public void setRequerente(Requerente requerente) {
        this.requerente = requerente;
    }

    public Imovel getImovel() {
        return imovel;
    }

    public void setImovel(Imovel imovel) {
        this.imovel = imovel;
    }

    public ObservableList<Item> getItens() {
        return itens;
    }

    public void setItens(ObservableList<Item> itens) {
        this.itens = itens;
    }

    public LocalDate getDataRequerimento() {
        return dataRequerimento;
    }

    public void setDataRequerimento(LocalDate dataRequerimento) {
        this.dataRequerimento = dataRequerimento;
    }

    public void setData(int dia, int mes, int ano) {
        this.dataRequerimento = LocalDate.of(ano, mes, dia);
    }

    public static LocalDate getLocalDateOfString(String dataAsString) {
        return LocalDate.parse(dataAsString, DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy"));
    }

    public StringProperty anoProperty() {
        return new SimpleStringProperty(dataRequerimento.getYear() + "");
    }

    public StringProperty dataRequerimentoAsStringProperty() {
        return new SimpleStringProperty(dataRequerimento.format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy")));
    }

    public StringProperty dataCIAsStringProperty() {
        return new SimpleStringProperty(dataCI.format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy")));
    }

    public StringProperty dataAutorizacaoAsStringProperty() {
        return new SimpleStringProperty(dataAutorizacao.format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy")));
    }

    public StringProperty nowDataAsStringProperty() {
        return new SimpleStringProperty(LocalDate.now().format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy")));
    }

}
