package Messages;

import Utils.Result;

public class SuccessResponse extends Response{
    private String message;

    public SuccessResponse() {
        super(Result.SUCCESS);
        this.message = "";
    }

    public SuccessResponse(String message) {
        super(Result.SUCCESS);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
