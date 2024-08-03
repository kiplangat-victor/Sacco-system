package emt.sacco.middleware.ATM.ResetPin;

public class CardNotRecognizedException  extends  RuntimeException{


        public CardNotRecognizedException() {
            super();
        }

        public CardNotRecognizedException(String message) {
            super(message);
        }

        public CardNotRecognizedException(String message, Throwable cause) {
            super(message, cause);
        }

        public CardNotRecognizedException(Throwable cause) {
            super(cause);
        }

        protected CardNotRecognizedException(String message, Throwable cause,
                                             boolean enableSuppression,
                                             boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }


}
