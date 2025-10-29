package co.edu.uco.ucochallenge.crosscuting.exception;

public final class ApplicationException extends UcoChallengeException {

        private static final long serialVersionUID = 1L;

        private ApplicationException(final String technicalMessage, final String userMessage, final Throwable cause) {
                super(technicalMessage, userMessage, cause);
        }

        public static ApplicationException build(final String technicalMessage, final String userMessage, final Throwable cause) {
                return new ApplicationException(technicalMessage, userMessage, cause);
        }

        public static ApplicationException build(final String technicalMessage, final String userMessage) {
                return new ApplicationException(technicalMessage, userMessage, null);
        }

        public static ApplicationException build(final String message) {
                return new ApplicationException(message, message, null);
        }
}
