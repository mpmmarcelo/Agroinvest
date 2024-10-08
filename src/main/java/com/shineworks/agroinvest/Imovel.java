package com.shineworks.agroinvest;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Imovel {

    StringProperty denominacao, cadpro, amparoLegal, descricaoDoServico;

    public Imovel() {
        this.denominacao = new SimpleStringProperty();
        this.cadpro = new SimpleStringProperty();
        this.amparoLegal = new SimpleStringProperty();
        this.descricaoDoServico = new SimpleStringProperty();
    }

    public Imovel(String denominacao, String cadpro, String amparoLegal, String descricaoDoServico) {
        this.denominacao = new SimpleStringProperty(denominacao);
        this.cadpro = new SimpleStringProperty(cadpro);
        this.amparoLegal = new SimpleStringProperty(amparoLegal);
        this.descricaoDoServico = new SimpleStringProperty(descricaoDoServico);
    }

    public Imovel(StringProperty denominacao, StringProperty cadpro, StringProperty amparoLegal, StringProperty descricaoDoServico) {
        this.denominacao = denominacao;
        this.cadpro = cadpro;
        this.amparoLegal = amparoLegal;
        this.descricaoDoServico = descricaoDoServico;
    }

    public String getDenominacao() {
        return denominacao.get();
    }

    public StringProperty denominacaoProperty() {
        return denominacao;
    }

    public void setDenominacao(String denominacao) {
        this.denominacao.set(denominacao);
    }

    public String getCadpro() {
        return cadpro.get();
    }

    public StringProperty cadproProperty() {
        return cadpro;
    }

    public void setCadpro(String cadpro) {
        this.cadpro.set(cadpro);
    }

    public String getAmparoLegal() {
        return amparoLegal.get();
    }

    public StringProperty amparoLegalProperty() {
        return amparoLegal;
    }

    public void setAmparoLegal(String amparoLegal) {
        this.amparoLegal.set(amparoLegal);
    }

    public String getDescricaoDoServico() {
        return descricaoDoServico.get();
    }

    public StringProperty descricaoDoServicoProperty() {
        return descricaoDoServico;
    }

    public void setDescricaoDoServico(String descricaoDoServico) {
        this.descricaoDoServico.set(descricaoDoServico);
    }
}
