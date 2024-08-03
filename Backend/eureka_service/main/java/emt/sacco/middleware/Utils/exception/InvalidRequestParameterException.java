package emt.sacco.middleware.Utils.exception;

public class InvalidRequestParameterException extends RuntimeException{

    public InvalidRequestParameterException(String message) {
        super(message);
    }
}
