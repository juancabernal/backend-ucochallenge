package co.edu.uco.ucochallenge.crosscuting.exception;

import co.edu.uco.ucochallenge.crosscuting.helper.TextHelper;

public class UcoChallengeException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        private final String technicalMessage;
        private final String userMessage;

        protected UcoChallengeException(final String technicalMessage, final String userMessage, final Throwable cause) {
                super(TextHelper.getDefault(technicalMessage), cause);
                this.technicalMessage = TextHelper.getDefault(technicalMessage);
                this.userMessage = TextHelper.getDefault(userMessage);
        }

        public static UcoChallengeException build(final String technicalMessage, final String userMessage, final Throwable cause) {
                return new UcoChallengeException(technicalMessage, userMessage, cause);
        }

        public static UcoChallengeException build(final String technicalMessage, final String userMessage) {
                return new UcoChallengeException(technicalMessage, userMessage, null);
        }

        public static UcoChallengeException build(final String message) {
                return new UcoChallengeException(message, message, null);
        }

        public String getTechnicalMessage() {
                return technicalMessage;
        }

        public String getUserMessage() {
                return userMessage;
        }
}
