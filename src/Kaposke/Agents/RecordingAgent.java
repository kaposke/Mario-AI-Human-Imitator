package Kaposke.Agents;

import Kaposke.Utilities.ActionDictionary;
import Kaposke.Utilities.ArffDataFile;
import Kaposke.Utilities.UtilitySingleton;
import Kaposke.Utilities.Utils;
import ch.idsia.agents.controllers.human.HumanKeyboardAgent;
import ch.idsia.benchmark.mario.environments.Environment;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecordingAgent extends HumanKeyboardAgent {

    private ArffDataFile arffFile = new ArffDataFile(UtilitySingleton.getInstance().getArffPath());

    private boolean started = false;
    private boolean moved = false;

    @Override
    public void integrateObservation(Environment environment) {
        super.integrateObservation(environment);

        if(!started) {
            started = true;
            setupArffFile();
        }
        if(!moved) {
            moved = !ActionDictionary.buildActionString(getAction()).equals("Idle");
            if(moved) System.out.println("Moved!");
        }
        if(moved) {
            List<String> data = new ArrayList<>();

            for (int i = 0; i < mergedObservation.length; i++) {
                for (int j = 0; j < mergedObservation[0].length; j++) {
                    data.add("" + mergedObservation[i][j]);
                }
            }

            data.add(marioMode == 2 ? "fire" : marioMode == 1 ? "large" : "small");
            data.add(isMarioOnGround ? "true" : "false");
            data.add(isMarioAbleToJump ? "true" : "false");
            data.add(isMarioAbleToShoot ? "true" : "false");
            data.add(isMarioCarrying ? "true" : "false");

//            boolean[] a = new boolean[6];
//            int i = 0;
//            for (boolean action : getAction()) {
//                data.add(action ? "true" : "false");
//                a[i] = action;
//                i++;
//            }

            data.add(ActionDictionary.buildActionString(getAction()));

            try {
                arffFile.writeData(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupArffFile() {
        arffFile.setRelation("marioData");

        // Create an attribute for each visible block. Has to be separated numerics.
        for (int i = 0; i < mergedObservation.length; i++) {
            for (int j = 0; j < mergedObservation[0].length; j++) {
                arffFile.addAttribute("observation" + i + "-" + j, "numeric");
            }
        }
        //arffFile.addAttribute("marioStatus", "numeric");
        arffFile.addAttribute("marioMode", "{ fire, large , small }");
        arffFile.addAttribute("isMarioOnGround", "{ false , true }");
        arffFile.addAttribute("isMarioAbleToJump", "{ false , true }");
        arffFile.addAttribute("isMarioAbleToShoot", "{ false , true }");
        arffFile.addAttribute("isMarioCarrying", "{ false , true }");

//        arffFile.addAttribute("Left", "{ false , true }");
//        arffFile.addAttribute("Right", "{ false , true }");
//        arffFile.addAttribute("Down", "{ false , true }");
//        arffFile.addAttribute("Jump", "{ false , true }");
//        arffFile.addAttribute("Speed", "{ false , true }");
//        arffFile.addAttribute("Up", "{ false , true }");

        List<String> actionList = ActionDictionary.getAllActionsPossibilities();
        String allActions = Utils.stringListToSingleString(actionList, ", ");
        arffFile.addAttribute("MarioAction", "{" + allActions + "}");


        try {
            arffFile.initializeArffFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
