package Kaposke.Utilities;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

// This class is not a complete implementation of a ArffReader. It is currently only used for comment retrieval, used in this project
// to load file settings dynamically.
public class ArffReader {

    public static List<String> getHeaderComments(String arffPath) throws IOException {
        File arff = new File(arffPath);

        if(!arff.exists()) {
            throw new FileNotFoundException("Settings file not found.");
        }

        BufferedReader reader = new BufferedReader(new FileReader(arff));

        String line;
        // Read until it finds a comment
        while(!(line = reader.readLine()).startsWith("%"));

        List<String> output = new ArrayList<>();

        do{
            // get line without "%"
            output.add(line.substring(1));
        } while((line = reader.readLine()).startsWith("%"));

        return output;
    }
}
