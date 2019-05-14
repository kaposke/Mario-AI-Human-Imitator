package Kaposke.Utilities;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// This class is not a complete implementation of a ArffReader. It is currently only used for comment retrieval, used in this project
// to load file settings dynamically.
public class ArffReader {

    public static List<String> getHeaderComments(String arffPath) throws IOException {
        File arff = new File(arffPath);

        if (!arff.exists()) {
            throw new FileNotFoundException("Settings file not found.");
        }

        BufferedReader reader = new BufferedReader(new FileReader(arff));

        String line;
        // Read until it finds a comment
        while (!(line = reader.readLine()).startsWith("%")) ;

        List<String> output = new ArrayList<>();

        do {
            // get line without "%"
            output.add(line.substring(1));
        } while ((line = reader.readLine()).startsWith("%"));

        return output;
    }

    public static List<String[]> getAttributes(String arffPath) throws IOException {
        File arff = new File(arffPath);

        if (!arff.exists()) {
            throw new FileNotFoundException("Settings file not found.");
        }

        BufferedReader reader = new BufferedReader(new FileReader(arff));

        String line;
        // Read until it finds an attribute
        while (!(line = reader.readLine()).startsWith("@attribute")) ;

        List<String[]> output = new ArrayList<>();

        do {
            // remove @attribute
            // '@attribute ' has 11 chars
            line = line.substring(11);


            String[] attr = new String[2];
            // Split into name and type
            attr[0] = line.substring(0, line.indexOf(" ")).trim();
            attr[1] = line.substring(line.indexOf(" ")).trim();

            output.add(attr);

        } while ((line = reader.readLine()).startsWith("@attribute"));

        return output;
    }

    public static boolean compareAttributes(String first, String second) throws IOException {
        List<String[]> firstAttributes = getAttributes(first);
        List<String[]> secondAttributes = getAttributes(second);

        // If they have different sizes, they are different right away
        if (firstAttributes.size() != secondAttributes.size())
            return false;

        for (int i = 0; i < firstAttributes.size(); i++) {


            if(!firstAttributes.get(i)[0].equals(secondAttributes.get(i)[0]) ||
               !firstAttributes.get(i)[1].equals(secondAttributes.get(i)[1]))
                return false;
        }

        return true;
    }

    public static boolean compareAttributes(String[] files) throws IOException {
        for (int i = 1; i < files.length; i++) {
            if(!compareAttributes(files[0], files[i]))
                return false;
        }
        return true;
    }

    public static List<String> getData(String file) throws IOException {
        List<String> lines = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line;

        // Skip to the data
        while (!(line = reader.readLine()).startsWith("@data"));

        while((line = reader.readLine()) != null)
        {
            lines.add(line);
        }

        return lines;
    }
}
