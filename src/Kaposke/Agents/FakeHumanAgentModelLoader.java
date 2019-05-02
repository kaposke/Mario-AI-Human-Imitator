package Kaposke.Agents;

import Kaposke.Utilities.ActionDictionary;
import Kaposke.Utilities.J48ActionClassifier;
import Kaposke.Utilities.UtilitySingleton;
import Kaposke.Utilities.Utils;
import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.benchmark.mario.environments.Environment;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;


public class FakeHumanAgentModelLoader extends BasicMarioAIAgent implements Agent {

    MultilayerPerceptron actionClassifier = new MultilayerPerceptron();

    Instances dataSet;

    boolean jumpedPreviously = false;

    public FakeHumanAgentModelLoader() {
        super(new File(UtilitySingleton.getInstance().getArffPath()).getName());

        try {
            ConverterUtils.DataSource ds = new ConverterUtils.DataSource(UtilitySingleton.getInstance().getArffPath());
            dataSet = ds.getDataSet();
            System.out.println("Learning...");
            dataSet.setClassIndex(dataSet.numAttributes() -1);

            actionClassifier = (MultilayerPerceptron) SerializationHelper.read(new FileInputStream("neuralModel.model"));
        } catch (Exception e) {
            e.printStackTrace();
        }

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
            System.out.println("Guessed " + ActionDictionary.buildActionString(action));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean[] guessAction() throws Exception {

        DenseInstance instance = new DenseInstance(372);
        instance.setDataset(dataSet);

        fillInstance(instance);

        double[] distribution = actionClassifier.distributionForInstance(instance);

        String actionVerb = ActionDictionary.getAllActionsPossibilities().get(Utils.getHighestDoubleIndex(distribution));
        if(actionVerb.equals("Idle")) {
            distribution[0] = 0;
            actionVerb = ActionDictionary.getAllActionsPossibilities().get(Utils.getHighestDoubleIndex(distribution));
        }
        action = ActionDictionary.getActionFromString(actionVerb);

        System.out.println("Guessed: " + actionVerb);
        System.out.println(actionClassifier.distributionForInstance(instance).length);
//        for (int i = 0; i < 6; i++) {
//            action[i] = Utils.getHighestDoubleIndex(actionClassifiers.get(i).guessProbabilitiesFromInstance(instance)) == 1;
//        }

        cleanAmbiguousActions();

        jumpedPreviously = action[3];
        return action;
    }

    private void fillInstance(DenseInstance instance) {
        int index = 0;
        for (int y = 0; y < mergedObservation.length; y++) {
            for (int x = 0; x < mergedObservation[0].length; x++) {
                index = y * mergedObservation[0].length + x;
                instance.setValue(index,mergedObservation[y][x]);
            }
        }
        instance.setValue(index + 1, marioStatus);
        instance.setValue(index + 2, marioMode);
        instance.setValue(index + 3, isMarioOnGround ? "true" : "false");
        instance.setValue(index + 4, isMarioAbleToJump ? "true" : "false");
        instance.setValue(index + 5, isMarioAbleToShoot ? "true" : "false");
        instance.setValue(index + 6, isMarioCarrying ? "true" : "false");
    }

    private void cleanAmbiguousActions() {
//        // Walk right if no action
//        if(ActionDictionary.buildActionString(action).equals("Idle"))
//            action[1] = true;
        if(action[1])
            action[0] = false;
        if(jumpedPreviously)
            action[3] = false;

    }
}
