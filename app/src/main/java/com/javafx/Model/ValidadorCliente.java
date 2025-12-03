package com.javafx.Model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Control;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;

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

    /**
     * Inicializa el validador con todos los campos del formulario.
     * Este método debe llamarse una sola vez, preferiblemente en el initialize() del controlador.
     *
     * @param nombre Campo de nombre
     * @param apellidos Campo de apellidos
     * @param direccion Campo de dirección
     * @param telefono Campo de teléfono
     * @param fechaNac Campo de fecha de nacimiento
     * @param dni Campo de DNI
     * @param email Campo de email
     * @param sexo Menú desplegable de sexo
     */
    public void inicializarValidadores(Control nombre, Control apellidos, Control direccion,
            Control telefono, Control fechaNac, Control dni, Control email, Control sexo) {

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
        registrarValidadorFecha(fechaNac);
        registrarValidadorSexo(sexo);

        // Aplicar estilos CSS a los campos
        aplicarEstilosDinamicos(nombre);
        aplicarEstilosDinamicos(apellidos);
        aplicarEstilosDinamicos(direccion);
        aplicarEstilosDinamicos(telefono);
        aplicarEstilosDinamicos(email);
        aplicarEstilosDinamicos(dni);
        aplicarEstilosDinamicos(fechaNac);
        aplicarEstilosDinamicos(sexo);

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
                if (esTextoVacio((String) t)) return false;
                String telefono = ((String) t).replaceAll("[\\s-]", "");
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
                if (esTextoVacio((String) t)) return false;
                return ((String) t).matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
            },
            "Email inválido (ej: usuario@ejemplo.com)",
            Severity.ERROR
        ));
    }

    /**
     * Valida el campo de DNI.
     */
    private void registrarValidadorDni(Control control) {
        validador.registerValidator(control, (Control c, String texto) -> {
            if (esTextoVacio(texto)) {
                return ValidationResult.fromError(c, "El DNI es obligatorio");
            }

            String dniLimpio = texto.trim().toUpperCase();

            // Validar formato: 8 dígitos seguidos de letra
            if (!dniLimpio.matches("^[0-9]{8}[A-Z]$")) {
                return ValidationResult.fromError(c, "DNI inválido (8 dígitos + letra)");
            }

            // Validar letra correcta del DNI
            String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
            int numeros = Integer.parseInt(dniLimpio.substring(0, 8));
            char letraCalculada = letras.charAt(numeros % 23);
            char letraIntroducida = dniLimpio.charAt(8);

            if (letraCalculada != letraIntroducida) {
                return ValidationResult.fromError(c, "La letra del DNI no es correcta");
            }

            return ValidationResult.fromInfo(c, "");
        });
    }

    /**
     * Valida el campo de fecha de nacimiento.
     */
    private void registrarValidadorFecha(Control control) {
        validador.registerValidator(control, (Control c, String texto) -> {
            if (esTextoVacio(texto)) {
                return ValidationResult.fromError(c, "La fecha de nacimiento es obligatoria");
            }

            LocalDate fecha = parsearFecha(texto);
            if (fecha == null) {
                return ValidationResult.fromError(c, "Fecha inválida (dd/MM/yyyy o yyyy-MM-dd)");
            }

            // Validar que la fecha no sea futura
            if (fecha.isAfter(LocalDate.now())) {
                return ValidationResult.fromError(c, "La fecha no puede ser futura");
            }

            // Validar edad mínima (18 años) y máxima (120 años)
            LocalDate hace18 = LocalDate.now().minusYears(18);
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
     * Valida el campo de sexo.
     */
    private void registrarValidadorSexo(Control control) {
        validador.registerValidator(control, (Control c, SplitMenuButton menu) -> {
            String texto = menu.getText();
            if (esTextoVacio(texto) ||
                texto.equalsIgnoreCase("Sexo") ||
                texto.equalsIgnoreCase("Selecciona") ||
                texto.equalsIgnoreCase("Seleccione")) {
                return ValidationResult.fromError(c, "Debe seleccionar un sexo");
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
                actualizarEstiloCampo(control);
            });
        }
        // Listener para cambios en SplitMenuButton
        else if (control instanceof SplitMenuButton) {
            SplitMenuButton menu = (SplitMenuButton) control;
            menu.textProperty().addListener((obs, oldVal, newVal) -> {
                actualizarEstiloCampo(control);
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
        } else if (control instanceof SplitMenuButton) {
            String texto = ((SplitMenuButton) control).getText();
            estaVacio = esTextoVacio(texto) ||
                        texto.equalsIgnoreCase("Sexo") ||
                        texto.equalsIgnoreCase("Selecciona") ||
                        texto.equalsIgnoreCase("Seleccione");
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