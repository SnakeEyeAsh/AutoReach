package com.javafx.App;

import java.io.File;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application{

    public static void main(String[] args){
        launch(args);
    }

    private static Stage stage;

     @Override
    public void start(Stage primeraEscena) throws Exception { 

        Parent root = FXMLLoader.load(getClass().getResource("/pantalla1.fxml"));

        stage = primeraEscena;
        Scene scene = new Scene(root);
        primeraEscena.setScene(scene);
        primeraEscena.setTitle("AutoReach");
        primeraEscena.show();
        
        
    }

    static Stage getStage(){

        return stage;

    }

    
}
