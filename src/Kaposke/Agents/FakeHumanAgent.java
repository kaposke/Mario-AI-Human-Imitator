package Kaposke.Agents;

import Kaposke.Utilities.ActionDictionary;
import Kaposke.Utilities.J48ActionClassifier;
import Kaposke.Utilities.UtilitySingleton;
import Kaposke.Utilities.Utils;
import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.benchmark.mario.environments.Environment;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FakeHumanAgent extends BasicMarioAIAgent implements Agent {

    // List of classifiers. One for each action
    List<J48ActionClassifier> actionClassifiers = new ArrayList<>();
    Instances dataSet;
    boolean previouslyOnGround = false;

    int antiAFKCount = 72;

    public FakeHumanAgent() {
        super(new File(UtilitySingleton.getInstance().getArffPath()).getName());

        try {
            ConverterUtils.DataSource ds = new ConverterUtils.DataSource(UtilitySingleton.getInstance().getArffPath());
            dataSet = ds.getDataSet();

            for (int i = 0; i < action.length; i++) {
                System.out.println("Learning how to " + (i == 0 ? "walk left." : i == 1 ? "walk right." : i == 2 ? "crouch." : i == 3 ? "jump." : i == 4 ? "shoot and run." : "climb."));
                actionClassifiers.add(new J48ActionClassifier(dataSet, dataSet.numAttributes() - 5 + i));
            }

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

        DenseInstance instance = new DenseInstance(dataSet.numAttributes());
        instance.setDataset(dataSet);

        fillInstance(instance);

        for (int i = 0; i < 6; i++) {
            double[] distribution = actionClassifiers.get(i).guessProbabilitiesFromInstance(instance);
            action[i] = Utils.getHighestDoubleIndex(distribution) == 1;
        }

        double[] probs = actionClassifiers.get(3).guessProbabilitiesFromInstance(instance);
        System.out.println("Prob to jump: " +  (float)probs[probs.length-1]);

        action[3] = probs[1] >= 0.2f;

        cleanAmbiguousActions();

        //System.out.println(ActionDictionary.buildActionString(action));

        if(ActionDictionary.buildActionString(action).equals("Idle"))
            antiAFKCount--;
        if(antiAFKCount <= 0) {
            action[1] = action[3] = true;
            antiAFKCount--;
            if(antiAFKCount <= -24) {
                antiAFKCount = 72;
                action[3] = false;
                action[1] = false;
            }
        }


        previouslyOnGround = isMarioOnGround;
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
        //instance.setValue(index + 1, marioStatus);
        //instance.setValue(index + 1, marioMode == 2 ? "fire" : marioMode == 1 ? "large" : "small");
        //instance.setValue(++index, isMarioOnGround ? "true" : "false");
        //instance.setValue(++index, isMarioAbleToJump ? "true" : "false");
        //instance.setValue(++index, isMarioAbleToShoot ? "true" : "false");
        //instance.setValue(++index, isMarioCarrying ? "true" : "false");
    }

    private void cleanAmbiguousActions() {
//        // Walk right if no action
//        if(ActionDictionary.buildActionString(action).equals("Idle"))
//            action[1] = true;
        if(action[1])
            action[0] = false;
        if(!previouslyOnGround && isMarioOnGround) {
            action[3] = false;
        }

    }
}
