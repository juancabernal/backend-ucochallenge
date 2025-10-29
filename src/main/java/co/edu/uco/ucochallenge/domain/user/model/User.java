package co.edu.uco.ucochallenge.domain.user.model;

import java.util.UUID;
import java.util.regex.Pattern;

import co.edu.uco.ucochallenge.crosscuting.exception.DomainException;
import co.edu.uco.ucochallenge.crosscuting.helper.TextHelper;
import co.edu.uco.ucochallenge.crosscuting.helper.UUIDHelper;

public record User(
                UUID id,
                UUID idType,
                String idNumber,
                String firstName,
                String secondName,
                String firstSurname,
                String secondSurname,
                UUID homeCity,
                String email,
                String mobileNumber,
                boolean emailConfirmed,
                boolean mobileNumberConfirmed) {

        private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$");
        private static final Pattern ID_NUMBER_PATTERN = Pattern.compile("^[0-9]+$");
        private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        private static final Pattern MOBILE_PATTERN = Pattern.compile("^[0-9]{10}$");

        public User {
                id = normalizeId(id);
                idType = validateIdType(idType);
                idNumber = validateIdNumber(idNumber);
                firstName = validateMandatoryName(firstName, "primer nombre");
                secondName = validateOptionalName(secondName, "segundo nombre");
                firstSurname = validateMandatoryName(firstSurname, "primer apellido");
                secondSurname = validateOptionalName(secondSurname, "segundo apellido");
                homeCity = validateHomeCity(homeCity);
                email = validateEmail(email);
                mobileNumber = validateMobileNumber(mobileNumber);
        }

        private static UUID normalizeId(final UUID id) {
                final UUID normalized = UUIDHelper.getDefault(id);
                if (UUIDHelper.getDefault().equals(normalized)) {
                        return UUID.randomUUID();
                }
                return normalized;
        }

        private static UUID validateIdType(final UUID idType) {
                final UUID normalized = UUIDHelper.getDefault(idType);
                if (UUIDHelper.getDefault().equals(normalized)) {
                        throw DomainException.build("idType is mandatory", "El tipo de identificación es obligatorio y debe ser válido.");
                }
                return normalized;
        }

        private static String validateIdNumber(final String idNumber) {
                final String normalized = TextHelper.getDefaultWithTrim(idNumber);
                if (TextHelper.isEmpty(normalized)) {
                        throw DomainException.build("idNumber is empty", "El número de identificación es obligatorio.");
                }
                if (!ID_NUMBER_PATTERN.matcher(normalized).matches()) {
                        throw DomainException.build("idNumber has invalid characters", "El número de identificación solo puede contener dígitos.");
                }
                if (normalized.length() < 5 || normalized.length() > 20) {
                        throw DomainException.build("idNumber length out of range", "El número de identificación debe tener entre 5 y 20 dígitos.");
                }
                return normalized;
        }

        private static String validateMandatoryName(final String name, final String fieldName) {
                final String normalized = TextHelper.getDefaultWithTrim(name);
                if (TextHelper.isEmpty(normalized)) {
                        throw DomainException.build(fieldName + " is empty", "El " + fieldName + " es obligatorio.");
                }
                validateNameFormat(normalized, fieldName);
                return normalized;
        }

        private static String validateOptionalName(final String name, final String fieldName) {
                final String normalized = TextHelper.getDefaultWithTrim(name);
                if (TextHelper.isEmpty(normalized)) {
                        return TextHelper.getDefault();
                }
                validateNameFormat(normalized, fieldName);
                return normalized;
        }

        private static void validateNameFormat(final String value, final String fieldName) {
                if (!NAME_PATTERN.matcher(value).matches()) {
                        throw DomainException.build(fieldName + " has invalid characters", "El " + fieldName + " solo puede contener letras y espacios.");
                }
                if (value.length() < 2 || value.length() > 40) {
                        throw DomainException.build(fieldName + " length out of range", "El " + fieldName + " debe tener entre 2 y 40 caracteres.");
                }
        }

        private static UUID validateHomeCity(final UUID homeCity) {
                final UUID normalized = UUIDHelper.getDefault(homeCity);
                if (UUIDHelper.getDefault().equals(normalized)) {
                        throw DomainException.build("homeCity is mandatory", "La ciudad de residencia es obligatoria y debe ser válida.");
                }
                return normalized;
        }

        private static String validateEmail(final String email) {
                final String normalized = TextHelper.getDefaultWithTrim(email).toLowerCase();
                if (TextHelper.isEmpty(normalized)) {
                        throw DomainException.build("email is empty", "El correo electrónico es obligatorio.");
                }
                if (normalized.length() < 10 || normalized.length() > 100) {
                        throw DomainException.build("email length out of range", "El correo electrónico debe tener entre 10 y 100 caracteres.");
                }
                if (!EMAIL_PATTERN.matcher(normalized).matches()) {
                        throw DomainException.build("email format invalid", "El formato del correo electrónico no es válido.");
                }
                return normalized;
        }

        private static String validateMobileNumber(final String mobileNumber) {
                final String normalized = TextHelper.getDefaultWithTrim(mobileNumber);
                if (TextHelper.isEmpty(normalized)) {
                        throw DomainException.build("mobile number is empty", "El número de teléfono móvil es obligatorio.");
                }
                if (!MOBILE_PATTERN.matcher(normalized).matches()) {
                        throw DomainException.build("mobile number format invalid", "El número de teléfono móvil debe contener exactamente 10 dígitos.");
                }
                return normalized;
        }
}
