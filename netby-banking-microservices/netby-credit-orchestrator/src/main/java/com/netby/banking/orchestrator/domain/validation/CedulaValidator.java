package com.netby.banking.orchestrator.domain.validation;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Valida cédulas ecuatorianas usando el algoritmo de Módulo 10.
 * Protección contra inyección: solo acepta exactamente 10 dígitos numéricos.
 */
@ApplicationScoped
public class CedulaValidator {

    public boolean isValid(String cedula) {
        if (cedula == null || !cedula.matches("^[0-9]{10}$")) {
            return false;
        }

        int province = Integer.parseInt(cedula.substring(0, 2));
        if (province < 1 || province > 24) {
            return false;
        }

        int thirdDigit = Character.getNumericValue(cedula.charAt(2));
        if (thirdDigit >= 6) {
            return false;
        }

        int[] coefficients = {2, 1, 2, 1, 2, 1, 2, 1, 2};
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            int digit = Character.getNumericValue(cedula.charAt(i));
            int product = digit * coefficients[i];
            if (product >= 10) {
                product -= 9;
            }
            sum += product;
        }

        int checkDigit = Character.getNumericValue(cedula.charAt(9));
        int mod = sum % 10;
        return (mod == 0) ? checkDigit == 0 : checkDigit == (10 - mod);
    }
}
