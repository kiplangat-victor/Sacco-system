package emt.sacco.middleware.Utils.Responses;

public class MessageResponse<T> {
    private T entity;
    private String message;
    private int statusCode;

    // Constructor
    public MessageResponse(T entity, String message, int statusCode) {
        this.entity = entity;
        this.message = message; // Change the type of 'message' to String
        this.statusCode = statusCode;
    }

    public MessageResponse(String message, int statusCode) {
        this.statusCode = statusCode;
        this.message = message;
    }

    // Getters and setters

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
