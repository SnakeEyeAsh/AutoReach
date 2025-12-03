package com.javafx.Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.decoration.GraphicValidationDecoration;

/**
 * Clase optimizada para la validación de formularios de alquiler.
 * Proporciona validación en tiempo real con decoraciones visuales y
 * protección robusta contra campos vacíos o nulos.
 */
public class ValidadorAlquiler {

    // Validador único que maneja todos los campos
    private ValidationSupport validador;

    // Referencias a los DatePickers para validaciones cruzadas
    private DatePicker cldRecogida;
    private DatePicker cldEntrega;

    // Estilos CSS para los campos
    private static final String ESTILO_ERROR = "-fx-background-color: #ffcccc;"; // Rojo pálido
    private static final String ESTILO_VALIDO = "-fx-background-color: #ccffcc;"; // Verde pálido
    private static final String ESTILO_DEFECTO = ""; // Sin estilo (color por defecto)

    /**
     * Inicializa el validador con todos los campos del formulario de alquiler.
     * Este método debe llamarse una sola vez, preferiblemente en el initialize() del controlador.
     *
     * @param cldRecogida DatePicker de fecha de recogida
     * @param cldEntrega DatePicker de fecha de entrega
     * @param txtHoraEntrega Campo de hora de entrega
     */
    public void inicializarValidadores(DatePicker cldRecogida, DatePicker cldEntrega, Control txtHoraEntrega) {

        // Guardar referencias
        this.cldRecogida = cldRecogida;
        this.cldEntrega = cldEntrega;

        // Crear validador único
        validador = new ValidationSupport();
        validador.setValidationDecorator(new GraphicValidationDecoration());

        // Registrar validadores para cada campo
        registrarValidadorFechaRecogida(cldRecogida);
        registrarValidadorFechaEntrega(cldEntrega);
        registrarValidadorHoraEntrega(txtHoraEntrega);

        // Aplicar estilos CSS a los campos
        aplicarEstilosDinamicos(cldRecogida);
        aplicarEstilosDinamicos(cldEntrega);
        aplicarEstilosDinamicos(txtHoraEntrega);

        // Inicializar decoraciones visuales
        Platform.runLater(() -> validador.initInitialDecoration());
    }

    /**
     * Valida el campo de fecha de recogida.
     */
    private void registrarValidadorFechaRecogida(DatePicker control) {
        validador.registerValidator(control, (Control c, LocalDate fecha) -> {
            if (fecha == null) {
                return ValidationResult.fromError(c, "La fecha de recogida es obligatoria");
            }

            // Validar que la fecha de recogida sea menor que la de entrega
            if (cldEntrega != null && cldEntrega.getValue() != null) {
                if (!fecha.isBefore(cldEntrega.getValue())) {
                    return ValidationResult.fromError(c, "La fecha de recogida debe ser anterior a la de entrega");
                }
            }

            return ValidationResult.fromInfo(c, "");
        });
    }

    /**
     * Valida el campo de fecha de entrega.
     */
    private void registrarValidadorFechaEntrega(DatePicker control) {
        validador.registerValidator(control, (Control c, LocalDate fecha) -> {
            if (fecha == null) {
                return ValidationResult.fromError(c, "La fecha de entrega es obligatoria");
            }

            // Validar que la fecha de entrega sea mayor que la de recogida
            if (cldRecogida != null && cldRecogida.getValue() != null) {
                if (!fecha.isAfter(cldRecogida.getValue())) {
                    return ValidationResult.fromError(c, "La fecha de entrega debe ser posterior a la de recogida");
                }
            }

            return ValidationResult.fromInfo(c, "");
        });
    }

    /**
     * Valida el campo de hora de entrega.
     */
    private void registrarValidadorHoraEntrega(Control control) {
        validador.registerValidator(control, (Control c, String texto) -> {
            if (esTextoVacio(texto)) {
                return ValidationResult.fromError(c, "La hora de entrega es obligatoria");
            }

            // Validar formato HH:mm (00:00)
            if (!texto.matches("^([0-1][0-9]|2[0-3]):[0-5][0-9]$")) {
                return ValidationResult.fromError(c, "Formato de hora inválido (debe ser HH:mm, ej: 14:30)");
            }

            return ValidationResult.fromInfo(c, "");
        });
    }

    /**
     * Verifica si todos los campos del formulario son válidos.
     * No muestra alertas, solo retorna el resultado.
     *
     * @return true si todos los campos son válidos, false en caso contrario
     */
    public boolean esFormularioValido() {
        if (validador == null) return false;

        ValidationResult resultado = validador.getValidationResult();
        return resultado.getErrors().isEmpty();
    }

    /**
     * Comprueba las validaciones y muestra una alerta detallada si hay errores.
     * Útil para validar antes de guardar el formulario.
     *
     * @return true si todos los campos son válidos, false en caso contrario
     */
    public boolean validarYMostrarErrores() {
        if (validador == null) return false;

        ValidationResult resultado = validador.getValidationResult();

        if (resultado.getErrors().isEmpty()) {
            return true;
        }

        // Recopilar mensajes de error
        List<String> errores = new ArrayList<>();
        resultado.getErrors().forEach(error -> {
            if (!errores.contains(error.getText())) {
                errores.add(error.getText());
            }
        });

        // Mostrar alerta con los errores
        Alert alerta = new Alert(AlertType.WARNING);
        alerta.setTitle("Errores en el formulario");
        alerta.setHeaderText("Por favor, corrija los siguientes campos:");

        StringBuilder contenido = new StringBuilder();
        for (String error : errores) {
            contenido.append("• ").append(error).append("\n");
        }
        alerta.setContentText(contenido.toString());
        alerta.showAndWait();

        return false;
    }

    /**
     * Obtiene la lista de errores de validación actuales sin mostrar alertas.
     *
     * @return Lista de mensajes de error
     */
    public List<String> obtenerErrores() {
        List<String> errores = new ArrayList<>();

        if (validador != null) {
            ValidationResult resultado = validador.getValidationResult();
            resultado.getErrors().forEach(error -> {
                if (!errores.contains(error.getText())) {
                    errores.add(error.getText());
                }
            });
        }

        return errores;
    }

    /**
     * Limpia todos los validadores y libera recursos.
     * Llamar cuando se destruya el formulario.
     */
    public void limpiar() {
        if (validador != null) {
            validador = null;
        }
        cldRecogida = null;
        cldEntrega = null;
    }

    /**
     * Verifica si un texto está vacío o es nulo.
     *
     * @param texto Texto a verificar
     * @return true si el texto es null, vacío o solo contiene espacios
     */
    private boolean esTextoVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    /**
     * Aplica estilos CSS dinámicos a un control según su estado de validación.
     * - Rojo pálido si hay errores
     * - Verde pálido si es válido y tiene contenido
     * - Color por defecto si está vacío
     *
     * @param control Control al que aplicar los estilos
     */
    private void aplicarEstilosDinamicos(Control control) {
        if (control == null) return;

        // Listener para cambios en TextField
        if (control instanceof TextField) {
            TextField textField = (TextField) control;
            textField.textProperty().addListener((obs, oldVal, newVal) -> {
                Platform.runLater(() -> actualizarEstiloCampo(control));
            });
        }
        // Listener para cambios en DatePicker
        else if (control instanceof DatePicker) {
            DatePicker datePicker = (DatePicker) control;
            datePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
                Platform.runLater(() -> actualizarEstiloCampo(control));
            });
        }

        // Aplicar estilo inicial
        Platform.runLater(() -> actualizarEstiloCampo(control));
    }

    /**
     * Actualiza el estilo de un campo según su estado de validación.
     *
     * @param control Control a actualizar
     */
    private void actualizarEstiloCampo(Control control) {
        if (control == null || validador == null) return;

        // Verificar si el campo tiene contenido
        boolean estaVacio = false;
        if (control instanceof TextField) {
            estaVacio = esTextoVacio(((TextField) control).getText());
        } else if (control instanceof DatePicker) {
            estaVacio = ((DatePicker) control).getValue() == null;
        }

        // Si está vacío, aplicar estilo por defecto
        if (estaVacio) {
            control.setStyle(ESTILO_DEFECTO);
            return;
        }

        // Verificar si hay errores de validación para este control
        ValidationResult resultado = validador.getValidationResult();
        boolean tieneErrores = resultado.getErrors().stream()
                .anyMatch(error -> error.getTarget() == control);

        // Aplicar estilo según el estado
        if (tieneErrores) {
            control.setStyle(ESTILO_ERROR);
        } else {
            control.setStyle(ESTILO_VALIDO);
        }
    }

    /**
     * Obtiene el validador principal.
     * Usar solo si necesitas acceso directo al ValidationSupport.
     *
     * @return Instancia de ValidationSupport
     */
    public ValidationSupport getValidador() {
        return validador;
    }
}
