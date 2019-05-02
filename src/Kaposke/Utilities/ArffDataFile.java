package Kaposke.Utilities;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ArffDataFile {

    private String filePath;

    private File file;
    private BufferedWriter bufferedWriter;

    // ARFF Related tags
    private String relation;
    private List<String> attributes = new ArrayList<>();

    public ArffDataFile(String filePath, String fileName) {
        this.filePath = filePath + "/" + fileName;
    }

    public ArffDataFile(String filePath) {
        this.filePath = filePath;
    }

    public void initializeArffFile() throws IOException {
        file = new File(filePath + ".arff");
        bufferedWriter = new BufferedWriter(new FileWriter(file));
        fillSettings();
        bufferedWriter.flush();
    }

    private void fillSettings() throws IOException {
        bufferedWriter.write("@relation " + relation + "\n");
        bufferedWriter.newLine();
        writeAttributes();
        bufferedWriter.newLine();
        bufferedWriter.write("@data\n");
    }

    private void writeAttributes() throws IOException {
        for (String attribute : attributes) {
            bufferedWriter.write("@attribute " + attribute + "\n");
        }
    }

    public void writeData(List<String> data) throws IOException {
        StringBuilder completeData = new StringBuilder();
        for (int i = 0; i < data.size(); i++) {
            if(attributes.get(i).equals("string"))
                completeData.append("'").append(data.get(i)).append("'");
            else
                completeData.append(data.get(i));
            if(i < data.size() - 1)
                completeData.append(", ");
        }
        bufferedWriter.write(completeData.toString() + "\n");
        bufferedWriter.flush();
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public void addAttribute(String attributeName, String typeSpecification) {
        attributes.add(attributeName + " " + typeSpecification);
    }
}
