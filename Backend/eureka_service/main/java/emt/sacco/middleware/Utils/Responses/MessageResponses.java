package emt.sacco.middleware.Utils.Responses;

public class MessageResponses {

    private int statusCode;
    private String message;

    public MessageResponses(String message) {
        this.message = message;
    }
    public MessageResponses(int statusCode, String message) {
        this.message = message;
        this.statusCode = statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
