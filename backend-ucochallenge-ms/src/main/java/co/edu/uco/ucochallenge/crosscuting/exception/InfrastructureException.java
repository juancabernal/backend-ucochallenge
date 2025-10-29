package co.edu.uco.ucochallenge.crosscuting.exception;

public final class InfrastructureException extends UcoChallengeException {

        private static final long serialVersionUID = 1L;

        private InfrastructureException(final String technicalMessage, final String userMessage, final Throwable cause) {
                super(technicalMessage, userMessage, cause);
        }

        public static InfrastructureException build(final String technicalMessage, final String userMessage, final Throwable cause) {
                return new InfrastructureException(technicalMessage, userMessage, cause);
        }

        public static InfrastructureException build(final String technicalMessage, final String userMessage) {
                return new InfrastructureException(technicalMessage, userMessage, null);
        }

        public static InfrastructureException build(final String message) {
                return new InfrastructureException(message, message, null);
        }
}
