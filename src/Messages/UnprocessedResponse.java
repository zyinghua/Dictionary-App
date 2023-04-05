package Messages;

import Utils.Operation;
import Utils.Result;

public class UnprocessedResponse extends Response{
    private final String message;

    public UnprocessedResponse(String message) {
        super(Operation.UNKNOWN, Result.UNPROCESSED);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Status: " + this.getStatus() + ", Message: " + this.getMessage() + ".";
    }
}
