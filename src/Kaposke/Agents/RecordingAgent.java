package Kaposke.Agents;

import Kaposke.Models.SettingsModel;
import Kaposke.Utilities.SettingsHandler;
import Kaposke.Utilities.ActionDictionary;
import Kaposke.Utilities.ArffDataFile;
import Kaposke.Utilities.UtilitySingleton;
import Kaposke.Utilities.Utils;
import ch.idsia.agents.controllers.human.HumanKeyboardAgent;
import ch.idsia.benchmark.mario.environments.Environment;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecordingAgent extends HumanKeyboardAgent {

    private ArffDataFile arffFile = new ArffDataFile(UtilitySingleton.getInstance().getArffPath());

    private boolean started = false;
    private boolean moved = false;
    private SettingsModel settings;

    public void initialize() {
        try {
            settings = SettingsHandler.loadSettings();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setupArffFile();
    }

    @Override
    public void integrateObservation(Environment environment) {
        super.integrateObservation(environment);

        if(!started) {
            initialize();
            started = true;
        }

        if (!moved) {
            moved = !ActionDictionary.buildActionString(getAction()).equals("Idle");
            if (moved) System.out.println("Moved!");
        }
        if (moved) {
            List<String> data = new ArrayList<>();

            for (int i = 0; i < mergedObservation.length; i++) {
                for (int j = 0; j < mergedObservation[0].length; j++) {
                    data.add("" + mergedObservation[i][j]);
                }
            }

            if (settings.marioMode)
                data.add(marioMode == 2 ? "fire" : marioMode == 1 ? "large" : "small");
            if (settings.isMarioOnGround)
                data.add(isMarioOnGround ? "true" : "false");
            if (settings.isMarioAbleToJump)
                data.add(isMarioAbleToJump ? "true" : "false");
            if (settings.isMarioAbleToShoot)
                data.add(isMarioAbleToShoot ? "true" : "false");
            if (settings.isMarioCarrying)
                data.add(isMarioCarrying ? "true" : "false");

            data.add(ActionDictionary.buildActionString(getAction()));

//            boolean[] a = new boolean[6];
//            int i = 0;
//            for (boolean action : getAction()) {
//                data.add(action ? "true" : "false");
//                a[i] = action;
//                i++;
//            }

            try {
                arffFile.writeData(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupArffFile() {
        // add settings to header comments so that we can correctly load it later
        arffFile.addHeadComment(new Gson().toJson(settings));

        arffFile.setRelation("marioData");

        // Create an attribute for each visible block. Has to be separated numerics.
        for (int i = 0; i < mergedObservation.length; i++) {
            for (int j = 0; j < mergedObservation[0].length; j++) {
                arffFile.addAttribute("observation" + i + "-" + j, "numeric");
            }
        }

        if (settings.marioMode)
            arffFile.addAttribute("marioMode", "{ fire, large , small }");
        if (settings.isMarioOnGround)
            arffFile.addAttribute("isMarioOnGround", "{ false , true }");
        if (settings.isMarioAbleToJump)
            arffFile.addAttribute("isMarioAbleToJump", "{ false , true }");
        if (settings.isMarioAbleToShoot)
            arffFile.addAttribute("isMarioAbleToShoot", "{ false , true }");
        if (settings.isMarioCarrying)
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
