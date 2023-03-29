package Messages;

import Utils.*;

import java.util.ArrayList;

public class QueryResponse extends SuccessResponse{
    private String word;
    private ArrayList<String> meanings;

    public QueryResponse(Operation op, String word, ArrayList<String> meanings) {
        super(op);
        this.word = word;
        this.meanings = meanings;
    }

    public String getWord() {
        return word;
    }

    public ArrayList<String> getMeanings() {
        return meanings;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setMeanings(ArrayList<String> meanings) {
        this.meanings = meanings;
    }

    @Override
    public String toString() {
        return "Operation: " + this.getOp() + ", Status: " + this.getStatus() + ", Message: "
                + this.getMessage() + ", Word: " + this.getWord() + ", Meanings: "
                + this.getMeanings().toString();
    }
}
