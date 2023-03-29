package Server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Dictionary {
    private final String fileName;
    private ConcurrentHashMap<String, ArrayList<String>> dict = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;
    private final AutoFileSaver autoFileSaver;

    public Dictionary(String fileName)
    {
        this.fileName = fileName;
        this.objectMapper = new ObjectMapper();
        this.autoFileSaver = new AutoFileSaver(fileName, this);
        this.autoFileSaver.start();
        this.loadDictDataFromFile();
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
            System.err.println(e);
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
            System.err.println(e);
        }
    }

    public ConcurrentHashMap<String, ArrayList<String>> getDict() {
        return dict;
    }

    public void setDict(ConcurrentHashMap<String, ArrayList<String>> dict) {
        this.dict = dict;
    }

    public synchronized String addAWord(String word, ArrayList<String> meanings) {
        /*Simple return: true = success, false otherwise.*/
        if (!dict.containsKey(word))
        {
            dict.put(word, meanings);
            return "success";
        }
        else
        {
            return "failure";
        }
    }

    public ArrayList<String> queryAWord(String word) {
        return dict.getOrDefault(word, null);
    }

    public String removeAWord(String word) {
        if (dict.containsKey(word))
        {
            dict.remove(word);
            return "success";
        }
        else
        {
            return "failure";
        }
    }

    public String updateAWord(String word, ArrayList<String> newMeanings) {
        if (dict.containsKey(word))
        {
            dict.put(word, newMeanings);
            return "success";
        }
        else
        {
            return "failure";
        }
    }

    public void terminate()
    {
        this.autoFileSaver.terminate();
    }

    public static void main(String[] args) {
        Dictionary dict = new Dictionary("dict.json");

    }
}
