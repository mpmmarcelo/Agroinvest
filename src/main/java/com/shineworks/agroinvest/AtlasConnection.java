package com.shineworks.agroinvest;

import com.mongodb.BasicDBObject;
import com.mongodb.ConnectionString;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;

public class AtlasConnection {

    private MongoCollection<Document> requerimentos, requerentes, imoveis, itens, config;

    private Requerimento requerimento;
    private boolean ativo, requerenteCadastrado;
    private String anoAtual = "2024";

    public AtlasConnection(Requerimento requerimento) {
        this.requerimento = requerimento;
        init();

        FindIterable<Document> documents = config.find(new BasicDBObject().append("_id", new ObjectId("622c9605cf1c99af948a0ec6")));
        Document first = documents.first();
        ativo = first.getBoolean("ativo");
        requerenteCadastrado = false;
        String numeroRequerimento = first.getString("numeroRequerimento");
        this.requerimento.setNumeroRequerimento(numeroRequerimento);
    }

    private void init() {

        ConnectionString connectionString = new ConnectionString(MProp.getString(MProp.Constant.DB_ADMIN));
        MongoClient mongoClient = MongoClients.create(connectionString);
        MongoDatabase database = mongoClient.getDatabase("banco");

        requerimentos = database.getCollection("requerimentos");
        requerentes = database.getCollection("requerimentos.requerentes");
        imoveis = database.getCollection("requerimentos.imoveis");
        itens = database.getCollection("requerimentos.itens");
        config = database.getCollection("config");
    }

    public void reload() {
        init();
        getRequerimento(requerimento.getNumeroRequerimento());
    }

    public void incrementNumeroRequerimento() {
        //https://stackoverflow.com/questions/5197144/convert-1-to-01
        String increment = String.format("%03d", Integer.parseInt(requerimento.getNumeroRequerimento()) + 1);
        //https://www.mongodb.com/community/forums/t/get-object-id-as-primary-key-to-a-return-method-in-java/10233/2
        config.updateOne(Filters.eq("_id", new ObjectId("622c9605cf1c99af948a0ec6")), Updates.set("numeroRequerimento", increment));
    }

    public void salvarRequerimento() {
        requerimentos.insertOne(new Document()
                .append("numeroRequerimento", requerimento.getNumeroRequerimento())
                .append("ano", requerimento.anoProperty().get())
                .append("data", requerimento.dataRequerimentoAsStringProperty().get())
                .append("cpf", requerimento.getRequerente().getCpf())
                .append("numeroCI", requerimento.getNumeroCI())
                .append("numeroProtocolo", requerimento.getNumeroProtocolo())
                .append("isento", requerimento.getIsento())

        );

        if(!requerenteCadastrado){ //se requerente nao existe insira um novo
            requerentes.insertOne(new Document()
                    .append("cpf", requerimento.getRequerente().getCpf())
                    .append("nome", requerimento.getRequerente().getNome().toUpperCase())
                    .append("telefone", requerimento.getRequerente().getTelefone())
                    .append("endereco", requerimento.getRequerente().getEndereco().toUpperCase())
            );
        }

        imoveis.insertOne(new Document()
                .append("numeroRequerimento", requerimento.getNumeroRequerimento())
                .append("ano", requerimento.anoProperty().get())
                .append("denominacao", requerimento.getImovel().getDenominacao().toUpperCase())
                .append("cadpro", requerimento.getImovel().getCadpro())
                .append("amparoLegal", requerimento.getImovel().getAmparoLegal())
                .append("descricaoDoServico", requerimento.getImovel().getDescricaoDoServico().toUpperCase())
        );

        requerimento.getItens().forEach((item) -> {
            itens.insertOne(new Document()
                    .append("numeroRequerimento", requerimento.getNumeroRequerimento())
                    .append("ano", requerimento.anoProperty().get())
                    .append("tipo", item.getTipo())
                    .append("qnt", item.getQnt())
                    .append("equipamento", item.getEquipamento())
            );
        });
    }

    public void getRequerente(String cpf) {
        FindIterable<Document> requerentesCursor = requerentes.find(
                new BasicDBObject().append("cpf", cpf)); //pegar todos os dados pra ver se nao alterou telefone ou endereco e mudar o save pra update no caso
        if (requerentesCursor.first() != null) {
            Document reqt = requerentesCursor.first();
            requerimento.getRequerente().setNome(reqt.getString("nome"));
            requerimento.getRequerente().setTelefone(reqt.getString("telefone"));
            requerimento.getRequerente().setEndereco(reqt.getString("endereco"));

            requerenteCadastrado = true;
        }
    }

    public void getRequerimento(String numeroRequerimento) {
        numeroRequerimento = String.format("%03d", Integer.parseInt(numeroRequerimento));

        FindIterable<Document> requerimentosCursor = requerimentos.find(
                new BasicDBObject().append("numeroRequerimento", numeroRequerimento).append("ano",anoAtual));
        Document req = requerimentosCursor.first();
        String cpf = req.get("cpf").toString(); //pega o cpf para encontrar o requerente
        requerimento.setNumeroRequerimento(req.getString("numeroRequerimento"));
        requerimento.setDataRequerimento(Requerimento.getLocalDateOfString(req.getString("data")));
        requerimento.setNumeroCI(req.getString("numeroCI"));
        requerimento.setNumeroProtocolo(req.getString("numeroProtocolo"));
        requerimento.setIsento(req.getString("isento"));


        FindIterable<Document> requerentesCursor = requerentes.find(
                new BasicDBObject().append("cpf", cpf));
        Document reqt = requerentesCursor.first();
        requerimento.getRequerente().setCpf(reqt.getString("cpf"));
        requerimento.getRequerente().setNome(reqt.getString("nome"));
        requerimento.getRequerente().setTelefone(reqt.getString("telefone"));
        requerimento.getRequerente().setEndereco(reqt.getString("endereco"));

        FindIterable<Document> imoveisCursor = imoveis.find(
                new BasicDBObject().append("numeroRequerimento", numeroRequerimento).append("ano",anoAtual));
        Document imovel = imoveisCursor.first();
        requerimento.getImovel().setDenominacao(imovel.getString("denominacao"));
        requerimento.getImovel().setCadpro(imovel.getString("cadpro"));
        requerimento.getImovel().setAmparoLegal(imovel.getString("amparoLegal"));
        requerimento.getImovel().setDescricaoDoServico(imovel.getString("descricaoDoServico"));

        FindIterable<Document> itensCursor = itens.find(
                new BasicDBObject().append("numeroRequerimento", numeroRequerimento).append("ano",anoAtual));
        MongoCursor<Document> itens = itensCursor.iterator();
        requerimento.getItens().clear(); //limpa os itens antes de carregar os novos
        while (itens.hasNext()) {
            Document item = itens.next();
            requerimento.getItens().add(new Item(item.getString("tipo"), item.getString("qnt"), item.getString("equipamento")));
        }
    }

    public boolean isAtivo() {
        return ativo;
    }

    public boolean isRequerenteCadastrado(){ return requerenteCadastrado;}

    public void printRequerimento(String numeroRequerimento) {
        FindIterable<Document> requerimentosCursor = requerimentos.find(
                new BasicDBObject().append("numeroRequerimento", numeroRequerimento).append("ano",anoAtual));
        String cpf = requerimentosCursor.first().get("cpf").toString(); //pega o cpf para encontrar o requerente
        requerimentosCursor.first().entrySet().forEach((entry) -> {
            System.out.println(entry.getValue());
        });
        System.out.println("---");

        FindIterable<Document> requerentesCursor = requerentes.find(
                new BasicDBObject().append("cpf", cpf));
        requerentesCursor.first().entrySet().forEach((entry) -> {
            System.out.println(entry.getValue());
        });
        System.out.println("---");

        FindIterable<Document> imoveisCursor = imoveis.find(
                new BasicDBObject().append("numeroRequerimento", numeroRequerimento).append("ano",anoAtual));
        imoveisCursor.first().entrySet().forEach((entry) -> {
            System.out.println(entry.getValue());
        });
        System.out.println("---");

        FindIterable<Document> itensCursor = itens.find(
                new BasicDBObject().append("numeroRequerimento", numeroRequerimento).append("ano",anoAtual));
        MongoCursor<Document> iterator = itensCursor.iterator();
        while (iterator.hasNext()) {
            iterator.next().entrySet().forEach((entry) -> {
                System.out.println(entry.getValue());
            });
            System.out.println("");
        }
        System.out.println("---");
    }

    public void updateNumeroCI(String numeroRequerimento, String numeroCI) {
        //www.tutorialspoint.com/how-to-update-an-existing-document-in-mongodb-collection-using-java
       //https://stackoverflow.com/questions/24353580/how-to-find-documents-matching-multiple-criteria
        BasicDBObject criteria = new BasicDBObject("numeroRequerimento", numeroRequerimento).append("ano", anoAtual);
        requerimentos.updateOne(criteria, Updates.set("numeroCI", numeroCI));
    }

    public void updateNumeroProtocolo(String numeroRequerimento, String numeroProtocolo) {
        //www.tutorialspoint.com/how-to-update-an-existing-document-in-mongodb-collection-using-java
        //requerimentos.updateOne(Filters.eq("numeroRequerimento", numeroRequerimento), Updates.set("numeroProtocolo", numeroProtocolo));

        BasicDBObject criteria = new BasicDBObject("numeroRequerimento", numeroRequerimento).append("ano", anoAtual);
        requerimentos.updateOne(criteria, Updates.set("numeroProtocolo", numeroProtocolo));
    }

    public void updateIsento(String numeroRequerimento, String isento) {
        //www.tutorialspoint.com/how-to-update-an-existing-document-in-mongodb-collection-using-java
        //requerimentos.updateOne(Filters.eq("numeroRequerimento", numeroRequerimento), Updates.set("isento", tf));

        BasicDBObject criteria = new BasicDBObject("numeroRequerimento", numeroRequerimento).append("ano", anoAtual);
        requerimentos.updateOne(criteria, Updates.set("isento", isento));
    }

    public void updateConcluido(String numeroRequerimento, boolean tf) {
        //www.tutorialspoint.com/how-to-update-an-existing-document-in-mongodb-collection-using-java
        //requerimentos.updateOne(Filters.eq("numeroRequerimento", numeroRequerimento), Updates.set("concluido", tf));

        //BasicDBObject criteria = new BasicDBObject("numeroRequerimento", numeroRequerimento).append("ano", anoAtual);
        //requerimentos.updateOne(criteria, Updates.set("concluido", tf));
    }
}
