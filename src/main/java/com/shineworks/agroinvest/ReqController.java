package com.shineworks.agroinvest;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class ReqController implements Initializable {

    @FXML
    private Label numero, ano, data;

    @FXML
    private TextField nome, endereco, denominacao, descricaoDoServico, quantidade;

    @FXML
    private MNumberField cpf, telefone, cadpro;

    @FXML
    private ChoiceBox<String> amparoLegal, tipo, equipamento;

    @FXML
    private ComboBox isento, selectAno;

    @FXML
    private Button btnAbrir, btnRequerimento, btnCI, btnAutorizacao;

    @FXML
    private TableView<Item> tableView;

    @FXML
    private TableColumn<Item, String> tipoCol, qntCol, equipamentoCol;

    Requerimento requerimento;
    AtlasConnection atlas;

    public ReqController() {

    }

    public ReqController(Requerimento requerimento) {
        this.requerimento = requerimento;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        requerimento = new Requerimento();
        atlas = new AtlasConnection(requerimento);

        initializeNumberFields();
        initializeChoices();
        initializeTableView();
        initializeBinds();
        initializeBtnLock();

        cpf.focusedProperty().addListener((obs, oldVal, newVal) -> { //autocomplete requerente fields
            if (newVal == false) {
                atlas.getRequerente(cpf.getText());
            }
        });

        if (!atlas.isAtivo()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("AGROINVEST");
            alert.setHeaderText("Sua chave de ativação expirou!");
            alert.setContentText("Maiores informações (44) 99949-3354 - Marcelo Perez Maciel");
            alert.showAndWait();
            System.exit(1);
        }
    }

    @FXML
    protected void add() {
        requerimento.getItens().add(new Item(tipo.getValue(), quantidade.getText(), equipamento.getValue()));
    }

    @FXML
    protected void abrirRequerimento() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Abrir Requerimento");
        dialog.setHeaderText("Carrega um Requerimento gerado anteriormente.");
        dialog.setContentText("Numero do Requerimento: ");
        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            lockGerarRequerimento(true);
            atlas.getRequerimento(result.get());
        }
    }

    @FXML
    protected void gerarRequerimento() {
        if (areFieldsValid()) {
            //MReplacer mr = new MReplacer("C:\\Users\\Marcelo\\Desktop\\Requerimento.docx", "C:\\Users\\Marcelo\\Desktop\\Requerimento2.docx");
            MReplacer mr = new MReplacer("input/Requerimento.docx", "output/Requerimento AGROINVEST "
                    + requerimento.getNumeroRequerimento()
                    + "_" + requerimento.anoProperty().get()
                    + " " + requerimento.getRequerente().getNome().toUpperCase()
                    + ".docx");
            if (mr.open()) {
                //header
                mr.replaceAll("@NUMERO", requerimento.getNumeroRequerimento());
                mr.replaceAll("@ANO", requerimento.anoProperty().get());
                mr.replaceAll("@DATA", requerimento.dataRequerimentoAsStringProperty().get());
                //requerente
                mr.replaceAll("@NOME", requerimento.getRequerente().getNome().toUpperCase());
                mr.replaceAll("@CPF", requerimento.getRequerente().getCpf());
                mr.replaceAll("@CADPRO", requerimento.getImovel().getCadpro());
                mr.replaceAll("@ENDERECO", requerimento.getRequerente().getEndereco());
                mr.replaceAll("@TELEFONE", requerimento.getRequerente().getTelefone());
                //imovel tabela
                mr.replaceAllOnTable("@DENOMINACAO", requerimento.getImovel().getDenominacao().toUpperCase());
                mr.replaceAllOnTable("@AMPAROLEGAL", requerimento.getImovel().getAmparoLegal().toUpperCase());
                mr.replaceAllOnTable("@DESCRICAODOSERVICO", requerimento.getImovel().getDescricaoDoServico().toUpperCase());
                //itens
                requerimento.getItens().forEach((item) -> {
                    mr.replaceOnTable("@ITEM", item.getQnt() + " " + placeItemTipo(item.getQnt(),item.getTipo()) + " - " + item.getEquipamento());
                });
                mr.replaceAllOnTable("@ITEM", "");

                //exportar dados para banco
                atlas.salvarRequerimento();
                atlas.incrementNumeroRequerimento();

                //salvar arquivo
                if (mr.save()) {
                    lockGerarRequerimento(true);
                    //abrir pasta
                    try {
                        java.awt.Desktop.getDesktop().open(new File("output"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @FXML
    protected void gerarCI() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Gerar Comunicação Interna");
        dialog.setHeaderText("Gerar uma Comunicação Interna para encaminhar este requerimento.");
        dialog.setContentText("Numero da CI: ");
        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            atlas.updateNumeroCI(requerimento.getNumeroRequerimento(), String.format("%03d", Integer.parseInt(result.get().toString())));
            atlas.reload(); //recarrega os dados do banco

            MReplacer mr = new MReplacer("input/CI.docx", "output/C.I. "
                    + requerimento.getNumeroCI()
                    + "_" + requerimento.anoProperty().get()
                    + " - Encaminha Req. " + requerimento.getNumeroRequerimento()
                    + "-" + requerimento.anoProperty().get()
                    + " - AGROINVEST - " + requerimento.getRequerente().getNome().toUpperCase()
                    + ".docx");
            if (mr.open()) {
                mr.replaceAll("@CI", requerimento.getNumeroCI());
                mr.replaceAll("@ANO", requerimento.anoProperty().get());
                mr.replaceAll("@DATA", requerimento.getDataRequerimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                mr.replaceAll("@NUMERO", requerimento.getNumeroRequerimento());
                mr.replaceAll("@NOME", requerimento.getRequerente().getNome().toUpperCase());
                mr.replaceAll("@AMPAROLEGAL", requerimento.getImovel().getAmparoLegal().toUpperCase());

                if (mr.save()) {
                    //abrir pasta
                    try {
                        java.awt.Desktop.getDesktop().open(new File("output"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @FXML
    protected void gerarAutorizacao() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Gerar Autorização");
        dialog.setHeaderText("Gerar Autorização de Execução de Serviço para este requerimento.");
        dialog.setContentText("Numero do Protocolo: ");
        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            atlas.updateNumeroProtocolo(requerimento.getNumeroRequerimento(), result.get());
            atlas.updateIsento(requerimento.getNumeroRequerimento(), requerimento.getIsento());
            atlas.reload(); //recarrega os dados do banco

            MReplacer mr = new MReplacer("input/Autorizacao.docx", "output/"
                    + requerimento.getNumeroRequerimento()
                    + "_AUTORIZAÇÃO_AGROINVEST REQ. " + requerimento.getNumeroRequerimento()
                    + "_" + requerimento.anoProperty().get()
                    + " - " + requerimento.getRequerente().getNome()
                    + ".docx");
            if (mr.open()) {
                mr.replaceAll("@NOME", requerimento.getRequerente().getNome());
                mr.replaceAll("@PROTOCOLO", requerimento.getNumeroProtocolo());

                switch (requerimento.getIsento()){
                    case "NÃO ISENTO": mr.replaceAll("@ISENTO", "Considerando o recolhimento de taxa (DAM), devidamente juntando ao processo com seu comprovante de quitação;");
                    case "CONSERVAÇÃO DE SOLOS" : mr.replaceAll("@ISENTO", "Considerando o estabelecido na Lei Municipal n° 758/2023 especificamente do PROGRAMA DE CONSERVAÇÃO DE SOLO E APOIO A PRODUÇÃO AGRÍCOLA - SEÇÃO I, ART. 13 §1º, I;");
                    case "MORADIA RURAL" : mr.replaceAll("@ISENTO", "Considerando o estabelecido na Lei Municipal n° 758/2023 especificamente do PROGRAMA DE APOIO À MORADIA RURAL - SEÇÃO VI, ART. 23;");
                    default : mr.replaceAll("@ISENTO", "PEEEEEE DEUUU ERRROOOO");
                }

                mr.replaceAllOnTable("@AMPAROLEGAL", requerimento.getImovel().getAmparoLegal().toUpperCase());
                mr.replaceAllOnTable("@DESCRICAODOSERVICO", requerimento.getImovel().getDescricaoDoServico().toUpperCase());

                requerimento.getItens().forEach((item) -> {
                    mr.replaceOnTable("@ITEM", item.getQnt() + " " + placeItemTipo(item.getQnt(), item.getTipo()) + " - " + item.getEquipamento());
                });
                mr.replaceAllOnTable("@ITEM", "");

                mr.replaceAll("@DATA", requerimento.nowDataAsStringProperty().get()); //data do dia que fez a autorizacao

                if (mr.save()) {
                    //abrir pasta
                    try {
                        java.awt.Desktop.getDesktop().open(new File("output"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public String placeItemTipo(String itemQnt, String itemTipo) {
        double i = Double.parseDouble(itemQnt.replace(",","."));
        if (i > 1.0) {
            return itemTipo.equals("Hora") ? itemTipo.concat("s") : itemTipo.replace("m", "ns");
        }
        return itemTipo;
    }

    public void lockGerarRequerimento(boolean tf) {
        if (tf == true) {
            btnRequerimento.setDisable(true);
            btnCI.setDisable(false);
            btnAutorizacao.setDisable(false);
        } else {
            btnRequerimento.setDisable(false);
            btnCI.setDisable(true);
            btnAutorizacao.setDisable(true);
        }
    }

    public boolean areFieldsValid() {
        try {
            if (!cpf.getText().isEmpty()
                    || !nome.getText().isEmpty()
                    || !telefone.getText().isEmpty()
                    || !endereco.getText().isEmpty()
                    || !denominacao.getText().isEmpty()
                    || !cadpro.getText().isEmpty()
                    || !descricaoDoServico.getText().isEmpty()
                    || !amparoLegal.getValue().isEmpty()
                    || !requerimento.getItens().isEmpty()

            ) return true;
            else throw new Exception("areFieldsValid : campos vazios.");
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("AGROINVEST");
            alert.setHeaderText("Existem campos vazios ou incompletos.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();

            e.printStackTrace();
            return false;
        }
    }

    //----------- INITIALIZATORS -------------------


    private void initializeNumberFields() {
        cpf.installMask(MNumberField.CPF);
        cadpro.installMask(MNumberField.CAD_PRO);
        telefone.installMask(MNumberField.TELEFONE);
    }

    private void initializeChoices() {
        tipo.setItems(FXCollections.observableArrayList("Hora", "Viagem"));
        tipo.getSelectionModel().select(0); //select: "Hora"

        amparoLegal.setItems(FXCollections.observableArrayList(
                "Programa de Conservação de solos e apoio a Produção agrícola",
                "Programa de Trafegabilidade",
                "Programa de apoio à Avicultura",
                "Programa de apoio à Bovinocultura de leite",
                "Programa de apoio à Suinocultura",
                "Programa de apoio à Moradia rural",
                "Programa de apoio a Fruticultura e Olericultura",
                "Programa de apoio à Aquicultura",
                "Programa de apoio à Agricultura orgânica"));

        equipamento.setItems(FXCollections.observableArrayList(
                "Caminhão Basculante 10M³ - FRETE CASCALHO",
                "Caminhão Basculante 10M³ - SERVIÇOS DIVERSOS",
                "Motoniveladora",
                "Pá-Carregadeira",
                "Retroescavadeira",
                "Rolo Compactador",
                "Mini Carregadeira de Pneus"));

        isento.setItems(FXCollections.observableArrayList(
                "NÃO ISENTO",
                "CONSERVAÇÃO DE SOLOS",
                "MORADIA RURAL"
        ));

    }

    private void initializeTableView() {
        //bind tableColumns
        tipoCol.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        qntCol.setCellValueFactory(new PropertyValueFactory<>("qnt"));
        equipamentoCol.setCellValueFactory(new PropertyValueFactory<>("equipamento"));

        //initialize tableView
        tableView.setItems(requerimento.getItens());

        //initialize contextMenu
        MenuItem mi1 = new MenuItem("excluir");
        mi1.setOnAction((ActionEvent) -> {
            requerimento.getItens().remove(tableView.getSelectionModel().getSelectedItem());
        });
        tableView.setContextMenu(new ContextMenu(mi1));
    }

    private void initializeBinds() {
        //requerente
        cpf.textProperty().bindBidirectional(requerimento.getRequerente().cpfProperty());
        nome.textProperty().bindBidirectional(requerimento.getRequerente().nomeProperty());
        telefone.textProperty().bindBidirectional(requerimento.getRequerente().telefoneProperty());
        endereco.textProperty().bindBidirectional(requerimento.getRequerente().enderecoProperty());

        //imovel
        denominacao.textProperty().bindBidirectional(requerimento.getImovel().denominacaoProperty());
        cadpro.textProperty().bindBidirectional(requerimento.getImovel().cadproProperty());
        amparoLegal.valueProperty().bindBidirectional(requerimento.getImovel().amparoLegalProperty());
        descricaoDoServico.textProperty().bindBidirectional(requerimento.getImovel().descricaoDoServicoProperty());

        //others
        numero.textProperty().bindBidirectional(requerimento.numeroRequerimentoProperty());
        ano.textProperty().bindBidirectional(requerimento.anoProperty());
        data.textProperty().bindBidirectional(requerimento.dataRequerimentoAsStringProperty());
        isento.valueProperty().bindBidirectional(requerimento.isentoProperty());


        //https://stackoverflow.com/questions/13726824/javafx-event-triggered-when-selecting-a-check-box
        /*cbIsento.selectedProperty().bindBidirectional(requerimento.isentoProperty());
        cbIsento.selectedProperty().addListener(((observable, oldValue, newValue) -> {
            atlas.updateIsento(requerimento.getNumeroRequerimento(), newValue);
        }));
        cbConcluido.selectedProperty().bindBidirectional(requerimento.concluidoProperty());
        cbConcluido.selectedProperty().addListener((observable, oldValue, newValue) -> {
            atlas.updateConcluido(requerimento.getNumeroRequerimento(), newValue);
        });*/
    }

    private void initializeBtnLock() {
        if (cpf.getText() == null) { //se nao puxar um Requerimento pronto desabilita a CI e Autorizacao
            lockGerarRequerimento(false);
        } else {
            lockGerarRequerimento(true);
        }
    }
}