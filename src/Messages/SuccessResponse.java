package Messages;

import Utils.*;

public class SuccessResponse extends Response{
    private String message;

    public SuccessResponse(Operation op) {
        super(op, Result.SUCCESS);
        this.message = "Success";
    }

    public SuccessResponse(Operation op, String message) {
        super(op, Result.SUCCESS);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Operation: " + this.getOp() + ", Status: " + this.getStatus() + ", Message: " + this.getMessage();
    }
}
