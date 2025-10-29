package co.edu.uco.ucochallenge.crosscuting.exception;

public final class DomainException extends UcoChallengeException {

        private static final long serialVersionUID = 1L;

        private DomainException(final String technicalMessage, final String userMessage, final Throwable cause) {
                super(technicalMessage, userMessage, cause);
        }

        public static DomainException build(final String technicalMessage, final String userMessage, final Throwable cause) {
                return new DomainException(technicalMessage, userMessage, cause);
        }

        public static DomainException build(final String technicalMessage, final String userMessage) {
                return new DomainException(technicalMessage, userMessage, null);
        }

        public static DomainException build(final String message) {
                return new DomainException(message, message, null);
        }
}
