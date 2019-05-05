package Kaposke.Agents;

import Kaposke.Utilities.ActionDictionary;
import Kaposke.Utilities.J48ActionClassifier;
import Kaposke.Utilities.UtilitySingleton;
import Kaposke.Utilities.Utils;
import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.benchmark.mario.environments.Environment;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class FakeHumanAgentVerbal extends BasicMarioAIAgent implements Agent {

    private J48 actionClassifier;

    private Instances dataSet;

    private boolean jumpedPreviously = false;

    public FakeHumanAgentVerbal() {
        super(new File(UtilitySingleton.getInstance().getArffPath()).getName());

        actionClassifier = new J48();

        try {
            ConverterUtils.DataSource ds = new ConverterUtils.DataSource(UtilitySingleton.getInstance().getArffPath());
            System.out.println(UtilitySingleton.getInstance().getArffPath());
            dataSet = ds.getDataSet();
            System.out.println("Learning...");
            dataSet.setClassIndex(dataSet.numAttributes() -1);

            actionClassifier.buildClassifier(dataSet);
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
            //System.out.println("Guessed " + ActionDictionary.buildActionString(action));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean[] guessAction() throws Exception {

        DenseInstance instance = new DenseInstance(372);
        instance.setDataset(dataSet);

        fillInstance(instance);

        //System.out.println(actionClassifier.distributionForInstance(instance).length);


        double[] distribution = actionClassifier.distributionForInstance(instance);
        String actionVerb = ActionDictionary.getAllActionsPossibilities().get(Utils.getHighestDoubleIndex(distribution));
        action = ActionDictionary.getActionFromString(actionVerb);

        // Show percentages for all actions
        List<String> possibilities = ActionDictionary.getAllActionsPossibilities();
        System.out.println("----------------------------------");
        for (int i = 0; i < distribution.length; i++) {
            System.out.println(possibilities.get(i) + ": " + String.format("%.2f", distribution[i] * 100) + "%");
        }
        System.out.println("----------------------------------");

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
        //instance.setValue(++index, marioStatus);
        instance.setValue(++index, marioMode == 2 ? "fire" : marioMode == 1 ? "large" : "small");
        instance.setValue(++index, isMarioOnGround ? "true" : "false");
        instance.setValue(++index, isMarioAbleToJump ? "true" : "false");
        instance.setValue(++index, isMarioAbleToShoot ? "true" : "false");
        instance.setValue(++index, isMarioCarrying ? "true" : "false");
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
