package emt.sacco.middleware.ATM.ResetPin;

public class PreviousPinIncorrectException extends RuntimeException{

    public PreviousPinIncorrectException() {
        super();
    }

    public PreviousPinIncorrectException(String message) {
        super(message);
    }

    public PreviousPinIncorrectException(String message, Throwable cause) {
        super(message, cause);
    }

    public PreviousPinIncorrectException(Throwable cause) {
        super(cause);
    }

    protected PreviousPinIncorrectException(String message, Throwable cause,
                                            boolean enableSuppression,
                                            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
