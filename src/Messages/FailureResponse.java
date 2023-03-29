package Messages;

import Utils.Result;

public class FailureResponse extends Response{
    private String message;

    public FailureResponse(String message) {
        super(Result.FAILURE);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
