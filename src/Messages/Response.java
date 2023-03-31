/*
    @Author: Yinghua Zhou
    Student ID: 1308266
 */

package Messages;

import Utils.*;
import java.io.Serializable;

public class Response implements Serializable {
    private Operation op;
    private Result status;

    public Response(Operation op, Result status) {
        this.op = op;
        this.status = status;
    }

    public Operation getOp() {
        return op;
    }

    public void setOp(Operation op) {
        this.op = op;
    }

    public Result getStatus() {
        return status;
    }

    public void setStatus(Result status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Operation: " + this.getOp() + ", Status: " + this.getStatus();
    }
}
