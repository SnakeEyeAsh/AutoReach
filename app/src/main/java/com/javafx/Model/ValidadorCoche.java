package com.javafx.Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.decoration.GraphicValidationDecoration;

/**
 * Clase optimizada para la validación de formularios de ingreso de coches.
 * Proporciona validación en tiempo real con decoraciones visuales y
 * protección robusta contra campos vacíos o nulos.
 */
public class ValidadorCoche {

    // Validador único que maneja todos los campos
    private ValidationSupport validador;

    // Estilos CSS para los campos
    private static final String ESTILO_ERROR = "-fx-background-color: #ffcccc;"; // Rojo pálido
    private static final String ESTILO_VALIDO = "-fx-background-color: #ccffcc;"; // Verde pálido
    private static final String ESTILO_DEFECTO = ""; // Sin estilo (color por defecto)

    /**
     * Inicializa el validador con todos los campos del formulario de ingreso de coche.
     * Este método debe llamarse una sola vez, preferiblemente en el initialize() del controlador.
     *
     * @param marca Campo de marca
     * @param modelo Campo de modelo
     * @param año Campo de año
     * @param kms Campo de kilómetros
     * @param bastidor Campo de número de bastidor
     * @param precio Campo de precio
     * @param diametroLlantas Campo de diámetro de llantas
     * @param diseñoLlantas Campo de diseño de llantas
     * @param color Campo de color
     * @param matricula Campo de matrícula
     */
    public void inicializarValidadores(Control marca, Control modelo, Control año, Control kms,
            Control bastidor, Control precio, Control diametroLlantas, Control diseñoLlantas,
            Control color, Control matricula) {

        // Crear validador único
        validador = new ValidationSupport();
        validador.setValidationDecorator(new GraphicValidationDecoration());

        // Registrar validadores para cada campo
        registrarValidadorMarca(marca);
        registrarValidadorModelo(modelo);
        registrarValidadorAño(año);
        registrarValidadorKilometraje(kms);
        registrarValidadorBastidor(bastidor);
        registrarValidadorPrecio(precio);
        registrarValidadorDiametroLlantas(diametroLlantas);
        registrarValidadorDiseñoLlantas(diseñoLlantas);
        registrarValidadorColor(color);
        registrarValidadorMatricula(matricula);

        // Aplicar estilos CSS a los campos
        aplicarEstilosDinamicos(marca);
        aplicarEstilosDinamicos(modelo);
        aplicarEstilosDinamicos(año);
        aplicarEstilosDinamicos(kms);
        aplicarEstilosDinamicos(bastidor);
        aplicarEstilosDinamicos(precio);
        aplicarEstilosDinamicos(diametroLlantas);
        aplicarEstilosDinamicos(diseñoLlantas);
        aplicarEstilosDinamicos(color);
        aplicarEstilosDinamicos(matricula);

        // Inicializar decoraciones visuales
        Platform.runLater(() -> validador.initInitialDecoration());
    }

    /**
     * Valida el campo de marca.
     */
    private void registrarValidadorMarca(Control control) {
        validador.registerValidator(control, (Control c, String texto) -> {
            if (esTextoVacio(texto)) {
                return ValidationResult.fromError(c, "La marca es obligatoria");
            }
            if (texto.length() < 2) {
                return ValidationResult.fromError(c, "La marca debe tener al menos 2 caracteres");
            }
            if (texto.length() > 50) {
                return ValidationResult.fromError(c, "La marca no puede exceder 50 caracteres");
            }
            return ValidationResult.fromInfo(c, "");
        });
    }

    /**
     * Valida el campo de modelo.
     */
    private void registrarValidadorModelo(Control control) {
        validador.registerValidator(control, (Control c, String texto) -> {
            if (esTextoVacio(texto)) {
                return ValidationResult.fromError(c, "El modelo es obligatorio");
            }
            if (texto.length() < 1) {
                return ValidationResult.fromError(c, "El modelo debe tener al menos 1 carácter");
            }
            if (texto.length() > 50) {
                return ValidationResult.fromError(c, "El modelo no puede exceder 50 caracteres");
            }
            return ValidationResult.fromInfo(c, "");
        });
    }

    /**
     * Valida el campo de año.
     */
    private void registrarValidadorAño(Control control) {
        validador.registerValidator(control, (Control c, String texto) -> {
            if (esTextoVacio(texto)) {
                return ValidationResult.fromError(c, "El año es obligatorio");
            }

            // Validar que sea un número
            if (!texto.matches("^[0-9]+$")) {
                return ValidationResult.fromError(c, "El año debe ser un número");
            }

            int año;
            try {
                año = Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                return ValidationResult.fromError(c, "Año inválido");
            }

            // Validar rango razonable (desde 1900 hasta el próximo año)
            int añoActual = LocalDate.now().getYear();
            if (año < 1900) {
                return ValidationResult.fromError(c, "El año no puede ser anterior a 1900");
            }
            if (año > añoActual + 1) {
                return ValidationResult.fromError(c, "El año no puede ser futuro");
            }

            return ValidationResult.fromInfo(c, "");
        });
    }

    /**
     * Valida el campo de kilómetros.
     */
    private void registrarValidadorKilometraje(Control control) {
        validador.registerValidator(control, (Control c, String texto) -> {
            if (esTextoVacio(texto)) {
                return ValidationResult.fromError(c, "El kilometraje es obligatorio");
            }

            // Validar que sea un número (puede ser decimal con punto)
            if (!texto.matches("^[0-9]+(\\.[0-9]+)?$")) {
                return ValidationResult.fromError(c, "El kilometraje debe ser un número válido (puede usar punto decimal)");
            }

            double kms;
            try {
                kms = Double.parseDouble(texto);
            } catch (NumberFormatException e) {
                return ValidationResult.fromError(c, "Kilometraje inválido");
            }

            // Validar rango razonable (puede ser 0.0 hasta 1,000,000 km)
            if (kms < 0) {
                return ValidationResult.fromError(c, "El kilometraje no puede ser negativo");
            }
            if (kms > 1000000) {
                return ValidationResult.fromError(c, "El kilometraje parece excesivo (máx. 1,000,000)");
            }

            return ValidationResult.fromInfo(c, "");
        });
    }

    /**
     * Valida el campo de número de bastidor (VIN).
     */
    private void registrarValidadorBastidor(Control control) {
        validador.registerValidator(control, (Control c, String texto) -> {
            if (esTextoVacio(texto)) {
                return ValidationResult.fromError(c, "El número de bastidor es obligatorio");
            }

            String bastidor = texto.trim();

            // El VIN estándar tiene 17 caracteres
            if (bastidor.length() != 17) {
                return ValidationResult.fromError(c, "El número de bastidor debe tener exactamente 17 caracteres");
            }

            // Verificar que todas las letras sean mayúsculas
            if (!bastidor.equals(bastidor.toUpperCase())) {
                return ValidationResult.fromError(c, "El bastidor debe contener solo letras MAYÚSCULAS");
            }

            // Verificar que no contenga las letras O, Q (prohibidas en VIN)
            if (bastidor.contains("O") || bastidor.contains("Q")) {
                return ValidationResult.fromError(c, "El bastidor no puede contener las letras O o Q");
            }

            return ValidationResult.fromInfo(c, "");
        });
    }

    /**
     * Valida el campo de precio.
     */
    private void registrarValidadorPrecio(Control control) {
        validador.registerValidator(control, (Control c, String texto) -> {
            if (esTextoVacio(texto)) {
                return ValidationResult.fromError(c, "El precio es obligatorio");
            }

            // Validar que sea un número (puede tener decimales)
            if (!texto.matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
                return ValidationResult.fromError(c, "El precio debe ser un número válido (ej: 15000.50)");
            }

            double precio;
            try {
                precio = Double.parseDouble(texto);
            } catch (NumberFormatException e) {
                return ValidationResult.fromError(c, "Precio inválido");
            }

            // Validar rango razonable (500 - 10,000,000)
            if (precio < 500) {
                return ValidationResult.fromError(c, "El precio debe ser al menos 500");
            }
            if (precio > 10000000) {
                return ValidationResult.fromError(c, "El precio parece excesivo (máx. 10,000,000)");
            }

            return ValidationResult.fromInfo(c, "");
        });
    }

    /**
     * Valida el campo de diámetro de llantas.
     */
    private void registrarValidadorDiametroLlantas(Control control) {
        validador.registerValidator(control, (Control c, String texto) -> {
            if (esTextoVacio(texto)) {
                return ValidationResult.fromError(c, "El diámetro de llantas es obligatorio");
            }

            // Validar que sea un número
            if (!texto.matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
                return ValidationResult.fromError(c, "El diámetro debe ser un número válido");
            }

            double diametro;
            try {
                diametro = Double.parseDouble(texto);
            } catch (NumberFormatException e) {
                return ValidationResult.fromError(c, "Diámetro inválido");
            }

            // Validar rango razonable (12 - 26 pulgadas para coches comunes)
            if (diametro < 12) {
                return ValidationResult.fromError(c, "El diámetro mínimo es 12 pulgadas");
            }
            if (diametro > 26) {
                return ValidationResult.fromError(c, "El diámetro máximo es 26 pulgadas");
            }

            return ValidationResult.fromInfo(c, "");
        });
    }

    /**
     * Valida el campo de diseño de llantas.
     */
    private void registrarValidadorDiseñoLlantas(Control control) {
        validador.registerValidator(control, (Control c, String texto) -> {
            if (esTextoVacio(texto)) {
                return ValidationResult.fromError(c, "El diseño de llantas es obligatorio");
            }
            if (texto.length() < 2) {
                return ValidationResult.fromError(c, "El diseño debe tener al menos 2 caracteres");
            }
            if (texto.length() > 50) {
                return ValidationResult.fromError(c, "El diseño no puede exceder 50 caracteres");
            }
            return ValidationResult.fromInfo(c, "");
        });
    }

    /**
     * Valida el campo de color.
     */
    private void registrarValidadorColor(Control control) {
        validador.registerValidator(control, (Control c, String texto) -> {
            if (esTextoVacio(texto)) {
                return ValidationResult.fromError(c, "El color es obligatorio");
            }
            if (texto.length() < 3) {
                return ValidationResult.fromError(c, "El color debe tener al menos 3 caracteres");
            }
            if (texto.length() > 30) {
                return ValidationResult.fromError(c, "El color no puede exceder 30 caracteres");
            }
            return ValidationResult.fromInfo(c, "");
        });
    }

    /**
     * Valida el campo de matrícula.
     */
    private void registrarValidadorMatricula(Control control) {
        validador.registerValidator(control, (Control c, String texto) -> {
            if (esTextoVacio(texto)) {
                return ValidationResult.fromError(c, "La matrícula es obligatoria");
            }

            String matricula = texto.trim().toUpperCase();

            // Formato español: 4 números + 3 letras (ej: 1234ABC)
            // También acepta formatos antiguos
            if (!matricula.matches("^[0-9]{4}[A-Z]{3}$") && !matricula.matches("^[A-Z]{1,2}[0-9]{4}[A-Z]{1,2}$")) {
                return ValidationResult.fromError(c, "Matrícula inválida (formato: 1234ABC)");
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
     * Aplica un efecto de shake a los campos vacíos.
     *
     * @return true si todos los campos son válidos, false en caso contrario
     */
    public boolean validarYMostrarErrores() {
        if (validador == null) return false;

        ValidationResult resultado = validador.getValidationResult();

        if (resultado.getErrors().isEmpty()) {
            return true;
        }

        // Aplicar efecto shake a los controles con errores
        resultado.getErrors().forEach(error -> {
            Control control = (Control) error.getTarget();
            aplicarEfectoShake(control);
        });

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
     * Aplica un efecto de shake (movimiento horizontal) a un control.
     *
     * @param control Control al que aplicar el efecto
     */
    private void aplicarEfectoShake(Control control) {
        if (control == null) return;

        TranslateTransition shake = new TranslateTransition(Duration.millis(100), control);
        shake.setFromX(0);
        shake.setByX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();
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

        // Listener para cambios en el texto (para TextField y similares)
        if (control instanceof TextField) {
            TextField textField = (TextField) control;
            textField.textProperty().addListener((obs, oldVal, newVal) -> {
                // Usar Platform.runLater para asegurar que la validación se complete primero
                Platform.runLater(() -> actualizarEstiloCampo(control));
            });

            // Listener adicional para cuando pierde el foco
            textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal) { // Cuando pierde el foco
                    Platform.runLater(() -> actualizarEstiloCampo(control));
                }
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
