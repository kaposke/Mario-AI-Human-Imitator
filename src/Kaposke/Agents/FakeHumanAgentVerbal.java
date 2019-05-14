package Kaposke.Agents;

import Kaposke.Models.SettingsModel;
import Kaposke.Utilities.*;
import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.benchmark.mario.environments.Environment;
import com.sun.xml.internal.ws.developer.Serialization;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class FakeHumanAgentVerbal extends BasicMarioAIAgent implements Agent {

    private Classifier actionClassifier;

    private Instances dataSet;

    private boolean jumpedPreviously = false;

    private SettingsModel settings;

    public FakeHumanAgentVerbal() {
        super(new File(UtilitySingleton.getInstance().getArffPath()).getName());

        actionClassifier = new J48();

        try {
            String filePath = UtilitySingleton.getInstance().getArffPath();
            if(filePath.endsWith(".arff")) {
                trainModelAndPlay(filePath);
            } else if(filePath.endsWith(".model")) {
                loadClassifier(filePath);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadClassifier(String filePath) throws Exception {
        actionClassifier = (Classifier)SerializationHelper.read(filePath);
    }

    private void trainModelAndPlay(String filePath) throws Exception {
        // Loads comment from arff file and gets settings used on it.
        List<String> headerComments = ArffReader.getHeaderComments(filePath);

        if(!headerComments.isEmpty())
            settings = SettingsHandler.fromJson(headerComments.get(0));
        else
            settings = new SettingsModel();

        buildClassifier();
    }

    private void buildClassifier() throws Exception {
        ConverterUtils.DataSource ds = new ConverterUtils.DataSource(UtilitySingleton.getInstance().getArffPath());
        System.out.println(UtilitySingleton.getInstance().getArffPath());
        dataSet = ds.getDataSet();

        System.out.println("Learning...");

        // Last attribute must be class
        dataSet.setClassIndex(dataSet.numAttributes() - 1);

        actionClassifier.buildClassifier(dataSet);

        saveModel();
    }

    private void saveModel() throws Exception {
        SerializationHelper.write(UtilitySingleton.getInstance().getArffPath().replace(".arff", ".model"), actionClassifier);
    }

    @Override
    public boolean[] getAction() {
        return action;
    }

    @Override
    public void integrateObservation(Environment environment) {
        super.integrateObservation(environment);

        try {
            action = guessAction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean[] guessAction() throws Exception {

        DenseInstance instance = new DenseInstance(dataSet.numAttributes());
        instance.setDataset(dataSet);

        fillInstance(instance);

        double[] distribution = actionClassifier.distributionForInstance(instance);
        String actionVerb = ActionDictionary.getAllActionsPossibilities().get(Utils.getHighestDoubleIndex(distribution));
        action = ActionDictionary.getActionFromString(actionVerb);

        cleanAmbiguousActions();

        jumpedPreviously = action[3];
        return action;
    }

    private void fillInstance(DenseInstance instance) {
        int index = 0;
        for (int y = 0; y < mergedObservation.length; y++) {
            for (int x = 0; x < mergedObservation[0].length; x++) {
                index = y * mergedObservation[0].length + x;
                instance.setValue(index, mergedObservation[y][x]);
            }
        }

        if (settings.marioMode)
            instance.setValue(++index, marioMode == 2 ? "fire" : marioMode == 1 ? "large" : "small");
        if (settings.isMarioOnGround)
            instance.setValue(++index, isMarioOnGround ? "true" : "false");
        if (settings.isMarioAbleToJump)
            instance.setValue(++index, isMarioAbleToJump ? "true" : "false");
        if (settings.isMarioAbleToShoot)
            instance.setValue(++index, isMarioAbleToShoot ? "true" : "false");
        if (settings.isMarioCarrying)
            instance.setValue(++index, isMarioCarrying ? "true" : "false");
    }

    private void cleanAmbiguousActions() {
        if (action[1])
            action[0] = false;
        if (jumpedPreviously)
            action[3] = false;
    }
}
