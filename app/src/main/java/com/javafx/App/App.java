package com.javafx.App;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class App extends Application {

    // Dimensiones de diseño del sidebar (ToolBar) en el FXML
    private static final double SIDEBAR_W = 110.0;
    private static final double SIDEBAR_H = 534.0;

    // Dimensiones de diseño del panel de contenido (StackPane) en el FXML
    private static final double CONTENT_W = 724.0;
    private static final double CONTENT_H = 578.0;

    public static void main(String[] args) {
        launch(args);
    }

    private static Stage stage;

    @Override
    public void start(Stage primeraEscena) throws Exception {
        HBox fxmlRoot = FXMLLoader.load(getClass().getResource("/pantalla1.fxml"));
        stage = primeraEscena;

        // Extraer sidebar (ToolBar) y contenido (StackPane) del HBox del FXML
        Node sidebar = fxmlRoot.getChildren().remove(0);
        StackPane content = (StackPane) fxmlRoot.getChildren().remove(0);

        // Ambos nodos en un Pane libre para poder posicionarlos manualmente
        Pane wrapper = new Pane(sidebar, content);
        wrapper.setStyle("-fx-background-color: white;");

        Scene scene = new Scene(wrapper);
        primeraEscena.setScene(scene);
        primeraEscena.setTitle("AutoReach");
        primeraEscena.setResizable(true);
        primeraEscena.setMaximized(true);

        Runnable escalar = () -> {
            double w = wrapper.getWidth();
            double h = wrapper.getHeight();
            if (w <= 0 || h <= 0) return;

            // ── Sidebar ──────────────────────────────────────────────────────
            // Escalar para que ocupe toda la altura de la ventana (arriba-izquierda)
            // El pivot del scale está en el centro del nodo, así que compensamos:
            //   visual_left = layoutX + (SIDEBAR_W/2)*(1-ss) = 0  → layoutX = (SIDEBAR_W/2)*(ss-1)
            //   visual_top  = layoutY + (SIDEBAR_H/2)*(1-ss) = 0  → layoutY = (SIDEBAR_H/2)*(ss-1)
            double ss = h / SIDEBAR_H;
            sidebar.setScaleX(ss);
            sidebar.setScaleY(ss);
            sidebar.setLayoutX((SIDEBAR_W / 2.0) * (ss - 1));
            sidebar.setLayoutY((SIDEBAR_H / 2.0) * (ss - 1));

            double sidebarVisualW = SIDEBAR_W * ss;

            // ── Contenido ─────────────────────────────────────────────────────
            // Escalar proporcionalmente al área disponible y centrar
            //   visual_left  = sidebarVisualW + (availW - CONTENT_W*s) / 2
            //                → layoutX = sidebarVisualW + (availW - CONTENT_W) / 2
            //   visual_top   = (h - CONTENT_H*s) / 2
            //                → layoutY = (h - CONTENT_H) / 2
            double availW = w - sidebarVisualW;
            double s = Math.min(availW / CONTENT_W, h / CONTENT_H);
            content.setScaleX(s);
            content.setScaleY(s);
            content.setLayoutX(sidebarVisualW + (availW - CONTENT_W) / 2.0);
            content.setLayoutY((h - CONTENT_H) / 2.0);
        };

        wrapper.widthProperty().addListener(e -> escalar.run());
        wrapper.heightProperty().addListener(e -> escalar.run());

        primeraEscena.show();
    }

    static Stage getStage() {
        return stage;
    }
}
