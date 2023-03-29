package Utils;

import java.util.ArrayList;
import java.util.Arrays;
import Messages.*;

public class Utils {
    public static final String terminateSignal = "#";

    public static String encodeRequest(Request request) {
        if (request.getOp() == null)
            return terminateSignal;

        StringBuilder encoded = new StringBuilder(request.getOp() + ";" + request.getWord() + ";");

        if (request instanceof AddUpdateRequest)
        {
            for(int i = 0; i < ((AddUpdateRequest) request).getMeanings().size(); i++)
                encoded.append(((AddUpdateRequest) request).getMeanings().get(i)).append(";");
        }

        return encoded.toString();
    }

    public static Request decodeRequest(String encoded)
    {
        String[] code = encoded.split(";");
        ArrayList<String> meanings = new ArrayList<>(Arrays.asList(code).subList(2, code.length));

        if (meanings.size() == 0)
            return new Request(Operation.valueOf(code[0]), code[1]);
        else
            return new AddUpdateRequest(Operation.valueOf(code[0]), code[1], meanings);
    }
}


