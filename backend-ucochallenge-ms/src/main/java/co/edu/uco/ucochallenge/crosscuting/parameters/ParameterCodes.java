package co.edu.uco.ucochallenge.crosscuting.parameters;

public final class ParameterCodes {

        private ParameterCodes() {
        }

        public static final class User {

                private User() {
                }

                public static final String ID_NUMBER_MIN_LENGTH = "user.idNumber.minLength";
                public static final String ID_NUMBER_MAX_LENGTH = "user.idNumber.maxLength";
                public static final String NAME_MIN_LENGTH = "user.name.minLength";
                public static final String NAME_MAX_LENGTH = "user.name.maxLength";
                public static final String EMAIL_MIN_LENGTH = "user.email.minLength";
                public static final String EMAIL_MAX_LENGTH = "user.email.maxLength";
                public static final String MOBILE_LENGTH = "user.mobile.length";
                public static final String NAME_PATTERN = "user.name.pattern";
                public static final String ID_NUMBER_PATTERN = "user.idNumber.pattern";
                public static final String EMAIL_PATTERN = "user.email.pattern";
                public static final String MOBILE_PATTERN = "user.mobile.pattern";
        }
}
