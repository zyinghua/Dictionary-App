package Messages;

import Utils.*;
import java.io.Serializable;

public class Response implements Serializable {
    private Result status;

    public Response(Result status) {
        this.status = status;
    }
}
