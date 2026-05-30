package com.javafx.Model;

import javafx.scene.control.Control;

import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.controlsfx.validation.decoration.GraphicValidationDecoration;

public class ValidadorAgente {

    private ValidationSupport validador;

    public void inicializarValidadores(Control username, Control nombre, Control apellidos,
            Control telefono, Control email) {

        validador = new ValidationSupport();
        validador.setValidationDecorator(new GraphicValidationDecoration());

        validador.registerValidator(username, false,
                Validator.createEmptyValidator("El usuario es obligatorio", Severity.ERROR));

        validador.registerValidator(nombre, false,
                Validator.createEmptyValidator("El nombre es obligatorio", Severity.ERROR));

        validador.registerValidator(apellidos, false,
                Validator.createEmptyValidator("Los apellidos son obligatorios", Severity.ERROR));

        validador.registerValidator(telefono, false,
                Validator.createRegexValidator("Teléfono inválido (9-15 dígitos)",
                        "\\d{9,15}", Severity.ERROR));

        validador.registerValidator(email, false,
                Validator.createRegexValidator("Email inválido",
                        "^[\\w.-]+@[\\w.-]+\\.\\w{2,}$", Severity.ERROR));

    }

    public boolean isValid() {
        if (validador == null) return false;
        ValidationResult resultado = validador.getValidationResult();
        return resultado.getErrors().isEmpty();
    }

    public boolean validarYMostrarErrores() {
        if (validador == null) return false;
        return isValid();
    }

}
