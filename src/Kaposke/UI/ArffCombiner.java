package Kaposke.UI;


import Kaposke.Utilities.ArffDataFile;
import Kaposke.Utilities.ArffReader;
import weka.core.converters.ArffLoader;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArffCombiner {

    private JFileChooser fileChooser;

    public ArffCombiner() {
        setupFileChooser();
        askForSelection();
        mergeArffIfPossible();
    }

    private void setupFileChooser() {
        fileChooser = new JFileChooser("CollectedData/PlayerRecordings/");
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".arff");
            }

            @Override
            public String getDescription() {
                return null;
            }
        });
        fileChooser.setMultiSelectionEnabled(true);
    }

    private void askForSelection() {
        fileChooser.showOpenDialog(fileChooser);
    }

    private void mergeArffIfPossible() {
        File[] files = fileChooser.getSelectedFiles();
        String[] paths = new String[files.length];

        for (int i = 0; i < paths.length; i++) {
            paths[i] = files[i].getPath();
        }

        try {
            if(!ArffReader.compareAttributes(paths)){
                JOptionPane.showMessageDialog(fileChooser, "File attributes don't match.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String fileName = null;
            do {
                fileName = JOptionPane.showInputDialog("New file name", "");
            } while(fileName == null);

            ArffDataFile arff = new ArffDataFile(fileChooser.getCurrentDirectory() + "/" + fileName + ".arff");

            arff.setRelation(fileName);

            List<String[]> attributes = ArffReader.getAttributes(paths[0]);

            for (String[] attribute : attributes) {
                arff.addAttribute(attribute[0], attribute[1]);
            }

            arff.initializeArffFile();

            for (String path : paths) {
                List<String> data = ArffReader.getData(path);

                for (String line : data) {
                    arff.writeLine(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new ArffCombiner();
    }
}
