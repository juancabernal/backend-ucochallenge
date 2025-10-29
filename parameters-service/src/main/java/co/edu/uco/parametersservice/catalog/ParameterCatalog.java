package co.edu.uco.parametersservice.catalog;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ParameterCatalog {

        private static final Map<String, Parameter> parameters = new ConcurrentHashMap<>();

        static {
                register("notification.admin.email", "admin@uco.edu.co");
                register("notification.duplicated.email.template",
                                "Hola %s, detectamos un intento de registro con su correo electrónico. Si no ha sido usted, por favor contacte al administrador.");
                register("notification.duplicated.mobile.template",
                                "Hola %s, detectamos un intento de registro con su número móvil. Si no ha sido usted, comuníquese con soporte.");
                register("notification.confirmation.email.strategy", "ENVIAR_LINK_CONFIRMACION");
                register("notification.confirmation.mobile.strategy", "ENVIAR_CODIGO_SMS");
                register("notification.email.maxRetries", "3");
                register("validation.code.timeExpiration", "5");

                register("user.idNumber.minLength", "5");
                register("user.idNumber.maxLength", "20");
                register("user.name.minLength", "2");
                register("user.name.maxLength", "40");
                register("user.email.minLength", "10");
                register("user.email.maxLength", "100");
                register("user.mobile.length", "10");
                register("user.name.pattern", "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$");
                register("user.idNumber.pattern", "^\\d+$");
                register("user.email.pattern", "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
                register("user.mobile.pattern", "^\\d{10}$");
        }

        private ParameterCatalog() {
        }

        public static Parameter getParameterValue(final String key) {
                final Parameter parameter = parameters.get(key);
                if (parameter == null) {
                        return null;
                }
                return new Parameter(parameter.getKey(), parameter.getValue());
        }

        public static void synchronizeParameterValue(final Parameter parameter) {
                register(parameter.getKey(), parameter.getValue());
        }

        public static Parameter removeParameter(final String key) {
                return parameters.remove(key);
        }

        public static Map<String, Parameter> getAllParameters() {
                return parameters;
        }

        private static void register(final String key, final String value) {
                parameters.put(key, new Parameter(key, value));
        }

}
