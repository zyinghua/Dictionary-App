/*
    @Author: Yinghua Zhou
    Student ID: 1308266
 */

import Messages.FailureResponse;
import Messages.QueryResponse;
import Messages.Response;
import Messages.SuccessResponse;
import Utils.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Dictionary {
    private final String fileName;
    private ConcurrentHashMap<String, ArrayList<String>> dict = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public Dictionary(boolean isFileProvided, String fileName)
    {
        this.fileName = fileName;
        this.objectMapper = new ObjectMapper();

        if (isFileProvided)
        {
            this.loadDictDataFromFile();
        }
    }

    private void loadDictDataFromFile()
    {
        try
        {
            TypeReference<ConcurrentHashMap<String, ArrayList<String>>> typeRef = new TypeReference<>() {};
            dict = this.objectMapper.readValue(new File(this.fileName), typeRef);

        } catch (FileNotFoundException e) {
            System.err.println(e + "\nPlease specify a valid file name. " +
                    "Or leave the file name empty for a default file.");
            System.exit(1);
        }
        catch (IOException e)
        {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public void writeDictDataToFile()
    {
        // convert the map to a JSON string and write it to a file
        try{
            this.objectMapper.writeValue(new File(this.fileName), dict);
        } catch (IOException e)
        {
            System.err.println(e.getMessage());
        }
    }

    public ConcurrentHashMap<String, ArrayList<String>> getDict() {
        return dict;
    }

    public void setDict(ConcurrentHashMap<String, ArrayList<String>> dict) {
        this.dict = dict;
    }

    public Response addAWord(String word, ArrayList<String> meanings) {
        word = word.toLowerCase();

        if (dict.containsKey(word))
        {
            return new FailureResponse(Operation.ADD_WORD, Utils.ERROR_WORD_ALREADY_EXISTS);
        } else if (meanings.size() == 0)
        {
            return new FailureResponse(Operation.ADD_WORD, Utils.ERROR_MEANINGS_EMPTY);
        } else
        {
            dict.put(word, meanings);
            return new SuccessResponse(Operation.ADD_WORD);
        }
    }

    public Response queryAWord(String word) {
        word = word.toLowerCase();

        if (dict.containsKey(word))
        {
            return new QueryResponse(Operation.QUERY_WORD, word, dict.get(word));
        } else
        {
            return new FailureResponse(Operation.QUERY_WORD, Utils.ERROR_WORD_NOT_FOUND);
        }
    }

    public Response removeAWord(String word) {
        word = word.toLowerCase();

        if (dict.containsKey(word))
        {
            dict.remove(word);
            return new SuccessResponse(Operation.REMOVE_WORD);
        }
        else
        {
            return new FailureResponse(Operation.REMOVE_WORD, Utils.ERROR_WORD_NOT_FOUND);
        }
    }

    public Response updateAWord(String word, ArrayList<String> newMeanings) {
        word = word.toLowerCase();

        if (dict.containsKey(word))
        {
            if (newMeanings.size() == 0)
            {
                return new FailureResponse(Operation.UPDATE_WORD, Utils.ERROR_MEANINGS_EMPTY);
            }
            else {
                dict.put(word, newMeanings);
                return new SuccessResponse(Operation.UPDATE_WORD);
            }
        }
        else
        {
            return new FailureResponse(Operation.UPDATE_WORD, Utils.ERROR_WORD_NOT_FOUND);
        }
    }
}
