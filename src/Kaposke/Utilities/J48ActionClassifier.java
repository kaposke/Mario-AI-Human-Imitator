package Kaposke.Utilities;

import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.trees.J48;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import java.io.File;

public class J48ActionClassifier {

    private J48 j48 = new J48();
    private Instances dataSet;
    int num = 362;

    public J48ActionClassifier(Instances dataSet, int classIndex) throws Exception {
        this.dataSet = filterActionsButClass(dataSet, classIndex);
        this.dataSet.setClassIndex(this.dataSet.numAttributes()-1);

        ArffSaver saver = new ArffSaver();
        saver.setInstances(this.dataSet);
        saver.setFile(new File("test/" + classIndex + ".arff"));
        saver.writeBatch();
        buildClassifier();
    }

    //Filters the actions to classify a single one with no dependence on other actions.
    private Instances filterActionsButClass(Instances dataSet, int classIndex) throws Exception {
        String[] options = new String[2];
        options[0] = "-R";

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            if(i + num == classIndex)
                continue;
            stringBuilder.append(i + num);
            if(i != 5)
                stringBuilder.append(", ");
        }

        options[1] = stringBuilder.toString();
        if(options[1].endsWith(", "))
            options[1] = options[1].substring(0, options[1].length() - 2);

        Remove removeFilter = new Remove();
        removeFilter.setOptions(options);
        removeFilter.setInputFormat(dataSet);

        return Filter.useFilter(dataSet, removeFilter);
    }

    public double[] guessProbabilitiesFromInstance(DenseInstance instance) throws Exception {
        instance.setDataset(dataSet);
        return j48.distributionForInstance(instance);
    }

    private void buildClassifier() throws Exception {
        j48.buildClassifier(dataSet);
    }
}
