package com.javafx.Model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.util.Duration;

import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.controlsfx.validation.decoration.GraphicValidationDecoration;

/**
 * Clase optimizada para la validación de formularios de cliente.
 * Proporciona validación en tiempo real con decoraciones visuales y
 * protección robusta contra campos vacíos o nulos.
 */
public class ValidadorCliente {

    // Validador único que maneja todos los campos
    private ValidationSupport validador;

    // Estilos CSS para los campos
    private static final String ESTILO_ERROR = "-fx-background-color: #ffcccc;"; // Rojo pálido
    private static final String ESTILO_VALIDO = "-fx-background-color: #ccffcc;"; // Verde pálido
    private static final String ESTILO_DEFECTO = ""; // Sin estilo (color por defecto)

    // Referencias para validación manual de DatePicker y RadioButtons
    private DatePicker dpFechaNac;
    private ToggleGroup toggleSexo;

    /**
     * Inicializa el validador con todos los campos del formulario.
     * Este método debe llamarse una sola vez, preferiblemente en el initialize() del controlador.
     *
     * @param nombre Campo de nombre
     * @param apellidos Campo de apellidos
     * @param direccion Campo de dirección
     * @param telefono Campo de teléfono
     * @param fechaNac DatePicker de fecha de nacimiento
     * @param dni Campo de DNI
     * @param email Campo de email
     * @param sexo ToggleGroup de sexo (RadioButtons)
     */
    public void inicializarValidadores(Control nombre, Control apellidos, Control direccion,
            Control telefono, DatePicker fechaNac, Control dni, Control email, ToggleGroup sexo) {

        // Guardar referencias para validación manual
        this.dpFechaNac = fechaNac;
        this.toggleSexo = sexo;

        // Crear validador único
        validador = new ValidationSupport();
        validador.setValidationDecorator(new GraphicValidationDecoration());

        // Registrar validadores para cada campo
        registrarValidadorNombre(nombre);
        registrarValidadorApellidos(apellidos);
        registrarValidadorDireccion(direccion);
        registrarValidadorTelefono(telefono);
        registrarValidadorEmail(email);
        registrarValidadorDni(dni);
        registrarValidadorFechaDatePicker(fechaNac);

        // Aplicar estilos CSS a los campos
        aplicarEstilosDinamicos(nombre);
        aplicarEstilosDinamicos(apellidos);
        aplicarEstilosDinamicos(direccion);
        aplicarEstilosDinamicos(telefono);
        aplicarEstilosDinamicos(email);
        aplicarEstilosDinamicos(dni);

        // Inicializar decoraciones visuales
        Platform.runLater(() -> validador.initInitialDecoration());
    }

    /**
     * Valida el campo de nombre.
     */
    private void registrarValidadorNombre(Control control) {
        validador.registerValidator(control, (Control c, String texto) -> {
            if (esTextoVacio(texto)) {
                return ValidationResult.fromError(c, "El nombre es obligatorio");
            }
            if (texto.length() < 2) {
                return ValidationResult.fromError(c, "El nombre debe tener al menos 2 caracteres");
            }
            if (texto.length() > 50) {
                return ValidationResult.fromError(c, "El nombre no puede exceder 50 caracteres");
            }
            if (!texto.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
                return ValidationResult.fromError(c, "El nombre solo puede contener letras");
            }
            return ValidationResult.fromInfo(c, "");
        });
    }

    /**
     * Valida el campo de apellidos.
     */
    private void registrarValidadorApellidos(Control control) {
        validador.registerValidator(control, (Control c, String texto) -> {
            if (esTextoVacio(texto)) {
                return ValidationResult.fromError(c, "Los apellidos son obligatorios");
            }
            if (texto.length() < 2) {
                return ValidationResult.fromError(c, "Los apellidos deben tener al menos 2 caracteres");
            }
            if (texto.length() > 100) {
                return ValidationResult.fromError(c, "Los apellidos no pueden exceder 100 caracteres");
            }
            if (!texto.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
                return ValidationResult.fromError(c, "Los apellidos solo pueden contener letras");
            }
            return ValidationResult.fromInfo(c, "");
        });
    }

    /**
     * Valida el campo de dirección.
     */
    private void registrarValidadorDireccion(Control control) {
        validador.registerValidator(control, (Control c, String texto) -> {
            if (esTextoVacio(texto)) {
                return ValidationResult.fromError(c, "La dirección es obligatoria");
            }
            if (texto.length() < 5) {
                return ValidationResult.fromError(c, "La dirección debe tener al menos 5 caracteres");
            }
            if (texto.length() > 200) {
                return ValidationResult.fromError(c, "La dirección no puede exceder 200 caracteres");
            }
            return ValidationResult.fromInfo(c, "");
        });
    }

    /**
     * Valida el campo de teléfono.
     */
    private void registrarValidadorTelefono(Control control) {
        validador.registerValidator(control, Validator.createPredicateValidator(
            t -> {
                if (!(t instanceof String)) return false;
                String texto = (String) t;
                if (esTextoVacio(texto)) return false;
                String telefono = texto.replaceAll("[\\s-]", "");
                return telefono.matches("^[+]?[0-9]{9,15}$");
            },
            "Teléfono inválido (9-15 dígitos, puede incluir +)",
            Severity.ERROR
        ));
    }

    /**
     * Valida el campo de email.
     */
    private void registrarValidadorEmail(Control control) {
        validador.registerValidator(control, Validator.createPredicateValidator(
            t -> {
                if (!(t instanceof String)) return false;
                String texto = (String) t;
                if (esTextoVacio(texto)) return false;
                return texto.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
            },
            "Email inválido (ej: usuario@ejemplo.com)",
            Severity.ERROR
        ));
    }

    /**
     * Valida el campo de DNI.
     */
    private void registrarValidadorDni(Control control) {
        validador.registerValidator(control, false, (Control c, String texto) -> {
            if (esTextoVacio(texto)) return ValidationResult.fromInfo(c, "");

            String dniLimpio = texto.trim().toUpperCase();
            if (!dniLimpio.matches("^[0-9]{8}[A-Z]$")) {
                return ValidationResult.fromError(c, "DNI inválido (8 dígitos + letra)");
            }
            String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
            int numeros = Integer.parseInt(dniLimpio.substring(0, 8));
            char letraCalculada = letras.charAt(numeros % 23);
            if (letraCalculada != texto.trim().toUpperCase().charAt(8)) {
                return ValidationResult.fromError(c, "La letra del DNI no es correcta");
            }
            return ValidationResult.fromInfo(c, "");
        });
    }

    /**
     * Valida el campo de fecha de nacimiento con DatePicker.
     */
    private void registrarValidadorFechaDatePicker(DatePicker datePicker) {
        validador.registerValidator(datePicker, false, (Control c, LocalDate fecha) -> {
            if (fecha == null) return ValidationResult.fromInfo(c, "");

            if (fecha.isAfter(LocalDate.now())) {
                return ValidationResult.fromError(c, "La fecha no puede ser futura");
            }
            LocalDate hace18  = LocalDate.now().minusYears(18);
            LocalDate hace120 = LocalDate.now().minusYears(120);
            if (fecha.isAfter(hace18)) {
                return ValidationResult.fromError(c, "Debe ser mayor de 18 años");
            }
            if (fecha.isBefore(hace120)) {
                return ValidationResult.fromError(c, "Fecha no válida");
            }
            return ValidationResult.fromInfo(c, "");
        });
    }

    /**
     * Valida que se haya seleccionado un sexo (RadioButton).
     * Se valida manualmente ya que ToggleGroup no es un Control.
     */
    private boolean validarSexoSeleccionado() {
        return toggleSexo != null && toggleSexo.getSelectedToggle() != null;
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
            if (error.getTarget() instanceof Control) {
                Control control = (Control) error.getTarget();
                aplicarEfectoShake(control);
            }
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
     * Intenta parsear una fecha en múltiples formatos.
     *
     * @param texto Texto con la fecha
     * @return LocalDate si es válida, null en caso contrario
     */
    private LocalDate parsearFecha(String texto) {
        if (esTextoVacio(texto)) return null;

        // Intentar formato ISO: yyyy-MM-dd
        try {
            return LocalDate.parse(texto.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e1) {
            // Intentar formato español: dd/MM/yyyy
            try {
                return LocalDate.parse(texto.trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (Exception e2) {
                return null;
            }
        }
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
                // Usar Platform.runLater para asegurar que el validador se actualice primero
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
        actualizarEstiloCampo(control);
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