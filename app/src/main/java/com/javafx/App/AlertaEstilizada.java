package com.javafx.App;

import atlantafx.base.theme.NordDark;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Clase utilitaria para crear alertas con estilo profesional de AtlantaFX
 * Usa el tema Nord Dark que es elegante y profesional
 * Todas las alertas usan el mismo icono que la ventana principal
 */
public class AlertaEstilizada {

    private static final String TEMA_PROFESIONAL = new NordDark().getUserAgentStylesheet();
    private static final String ICONO_APP = "Assets/auto-limpio.png";

    /**
     * Crea una alerta de información estilizada
     */
    public static Alert crearInformacion(String titulo, String encabezado, String contenido) {
        Alert alerta = new Alert(AlertType.INFORMATION);
        aplicarEstilo(alerta);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(contenido);
        return alerta;
    }

    /**
     * Crea una alerta de advertencia estilizada
     */
    public static Alert crearAdvertencia(String titulo, String encabezado, String contenido) {
        Alert alerta = new Alert(AlertType.WARNING);
        aplicarEstilo(alerta);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(contenido);
        return alerta;
    }

    /**
     * Crea una alerta de error estilizada
     */
    public static Alert crearError(String titulo, String encabezado, String contenido) {
        Alert alerta = new Alert(AlertType.ERROR);
        aplicarEstilo(alerta);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(contenido);
        return alerta;
    }

    /**
     * Crea una alerta de confirmación estilizada
     */
    public static Alert crearConfirmacion(String titulo, String encabezado, String contenido) {
        Alert alerta = new Alert(AlertType.CONFIRMATION);
        aplicarEstilo(alerta);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(contenido);
        return alerta;
    }

    /**
     * Aplica el tema profesional a la alerta
     */
    private static void aplicarEstilo(Alert alerta) {
        // Aplicar tema AtlantaFX
        alerta.getDialogPane().getStylesheets().add(TEMA_PROFESIONAL);
        alerta.getDialogPane().getStyleClass().add("alert");
    }

    /**
     * Muestra una alerta de información y espera
     */
    public static void mostrarInformacion(String titulo, String encabezado, String contenido) {
        Alert alerta = crearInformacion(titulo, encabezado, contenido);
        aplicarIcono(alerta);
        alerta.showAndWait();
    }

    /**
     * Muestra una alerta de advertencia y espera
     */
    public static void mostrarAdvertencia(String titulo, String encabezado, String contenido) {
        Alert alerta = crearAdvertencia(titulo, encabezado, contenido);
        aplicarIcono(alerta);
        alerta.showAndWait();
    }

    /**
     * Muestra una alerta de error y espera
     */
    public static void mostrarError(String titulo, String encabezado, String contenido) {
        Alert alerta = crearError(titulo, encabezado, contenido);
        aplicarIcono(alerta);
        alerta.showAndWait();
    }

    /**
     * Muestra una alerta de confirmación y devuelve true si el usuario confirma
     */
    public static boolean confirmar(String titulo, String encabezado, String contenido) {
        Alert confirmacion = crearConfirmacion(titulo, encabezado, contenido);
        aplicarIcono(confirmacion);
        return confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    /**
     * Aplica el icono de la aplicación a la ventana de la alerta
     */
    private static void aplicarIcono(Alert alerta) {
        try {
            // El stage solo está disponible después de que se crea el DialogPane
            alerta.getDialogPane().getScene().getWindow();
            Stage stage = (Stage) alerta.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(ICONO_APP));
        } catch (Exception e) {
            // Si falla, continuar sin icono
            System.err.println("No se pudo aplicar el icono a la alerta: " + e.getMessage());
        }
    }
}
