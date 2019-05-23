package Kaposke.Agents;

import Kaposke.Models.SettingsModel;
import Kaposke.Utilities.*;
import Kaposke.Utilities.Utils;
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
import weka.core.*;
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

        actionClassifier = new NaiveBayes();

        try {
            String filePath = UtilitySingleton.getInstance().getArffPath();

            settings = SettingsHandler.readSettings(filePath);

            if (filePath.endsWith(".arff")) {
                buildClassifier(filePath);
            } else if (filePath.endsWith(".model")) {
                loadClassifier(filePath);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadClassifier(String filePath) throws Exception {
        actionClassifier = (Classifier) SerializationHelper.read(filePath);

        // Might be better and easier to just use the arff

        final List<String> bool = new ArrayList<String>() {
            {
                add("false");
                add("true");
            }
        };

        List<Attribute> observation = new ArrayList<Attribute>() {
            {
                for (int y = 0; y < settings.GridHeight; y++) {
                    for (int x = 0; x < settings.GridWidth; x++) {
                        add(new Attribute("observation" + y + "-" + x));
                    }
                }
            }
        };

        List<String> modes = new ArrayList<String>(){
            {
                add("fire");
                add("large");
                add("small");
            }
        };

        Attribute marioMode = new Attribute("marioMode", modes);
        Attribute isMarioOnGround = new Attribute("isMarioOnGround", bool);
        Attribute isMarioAbleToJump = new Attribute("isMarioAbleToJump", bool);
        Attribute isMarioAbleToShoot = new Attribute("isMarioAbleToShoot", bool);
        Attribute isMarioCarrying = new Attribute("isMarioCarrying", bool);

        final List<String> classes = new ArrayList<String>() {
            {
                addAll(ActionDictionary.getAllActionsPossibilities());
            }
        };

        Attribute marioAction = new Attribute("MarioAction", classes);

        ArrayList<Attribute> attributeList = new ArrayList<Attribute>(2) {
            {
                addAll(observation);
                if (settings.marioMode)
                    add(marioMode);
                if (settings.isMarioOnGround)
                    add(isMarioOnGround);
                if (settings.isMarioAbleToJump)
                    add(isMarioAbleToJump);
                if (settings.isMarioAbleToShoot)
                    add(isMarioAbleToShoot);
                if (settings.isMarioCarrying)
                    add(isMarioCarrying);
                add(marioAction);
            }
        };

        dataSet = new Instances("Instances", attributeList, 1);
        dataSet.setClassIndex(dataSet.numAttributes() - 1);
    }

    private void buildClassifier(String path) throws Exception {
        ConverterUtils.DataSource ds = new ConverterUtils.DataSource(path);
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

        mergedObservation = environment.getMergedObservationZZ(settings.ZLevelScene, settings.ZLevelEnemies);

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
