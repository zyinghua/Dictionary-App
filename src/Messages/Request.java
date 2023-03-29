package Messages;

import Utils.Operation;

import java.io.Serializable;
import java.util.ArrayList;

public class Request implements Serializable {
    private Operation op;
    private String word;

    public Request()
    {
        this.op = null;
        this.word = null;
    }

    public Request(Operation op, String word)
    {
        this.op = op;
        this.word = word;
    }

    public Operation getOp() {
        return op;
    }

    public String getWord() {
        return word;
    }


    public void setOp(Operation op) {
        this.op = op;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String toString() {
        return "Request{" +
                "operation='" + op + '\'' +
                ", word='" + word + '\'' +
                '}';
    }
}
