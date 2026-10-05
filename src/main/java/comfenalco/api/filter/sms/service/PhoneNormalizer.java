package comfenalco.api.filter.sms.service;

import org.springframework.stereotype.Service;

@Service
public class PhoneNormalizer {

    private static final int PHONE_LENGTH = 10;

    private static final String COUNTRY_CODE = "57";

    public String normalize(String phoneRaw) {

        if (phoneRaw == null) {
            return null;
        }

        String phone = phoneRaw.trim();

        if (phone.isEmpty()) {
            return null;
        }

        /*
         * Manejo de valores que puedan venir como:
         *
         * 3001234567.0
         */
        if (phone.endsWith(".0")) {
            phone = phone.substring(
                    0,
                    phone.length() - 2
            );
        }

        /*
         * Eliminar caracteres no numéricos.
         */
        String digits = phone.replaceAll("\\D", "");

        /*
         * Si viene con código de país:
         *
         * 573001234567
         *
         * se transforma en:
         *
         * 3001234567
         */
        if (digits.length()
                == COUNTRY_CODE.length() + PHONE_LENGTH
                && digits.startsWith(COUNTRY_CODE)) {

            digits = digits.substring(
                    COUNTRY_CODE.length()
            );
        }

        /*
         * Validación de número celular colombiano:
         *
         * - 10 dígitos
         * - comienza por 3
         * - no todos los dígitos iguales
         */
        if (digits.length() == PHONE_LENGTH
                && digits.startsWith("3")
                && digits.chars().distinct().count() > 1) {

            return digits;
        }

        return null;
    }
}
