package Messages;

public class QueryResponse extends SuccessResponse{
    private String word;
    private String meaning;

    public QueryResponse(String word, String meaning) {
        super("success");
        this.word = word;
        this.meaning = meaning;
    }

    public String getWord() {
        return word;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }
}
