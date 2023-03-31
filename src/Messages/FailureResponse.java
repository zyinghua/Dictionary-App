/*
    @Author: Yinghua Zhou
    Student ID: 1308266
 */

package Messages;

import Utils.*;
import Utils.Result;

public class FailureResponse extends Response{
    private String message;

    public FailureResponse(Operation op, String message) {
        super(op, Result.FAILURE);
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
