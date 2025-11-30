package models;

public class OperationResult {
    private boolean success;
    private String message;

    public OperationResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Easy helper methods
    public static OperationResult success(String message) {
        return new OperationResult(true, message);
    }

    public static OperationResult error(String message) {
        return new OperationResult(false, message);
    }

    @Override
    public String toString() {
        if (success) {
            return "SUCCESS: " + message;
        } else {
            return "ERROR: " + message;
        }
    }
}