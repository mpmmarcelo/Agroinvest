package com.shineworks.agroinvest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends javafx.application.Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("reqview.fxml"));
        loader.setController(new ReqController()); //controller

        Scene scene = new Scene(loader.load());

        stage.setMinWidth(840);
        stage.setMinHeight(700);
        stage.setTitle("Agroinvest v.1.0.3 - 01/2024 | Marcelo Perez Maciel");
        stage.getIcons().add(new Image(App.class.getResource("icon.png").toString()));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

// jpackage pra fazer exe

// (V) criar campos para data da ci e autorizacao
// (V) colocar campos de texto em CAPS
// ( )

// (V) colocar hora/viagem no plural para quantidades maiores que 1
// ( ) vincular hora/viagem com o tipo do equipamento ex. carga cascalho : viagem
