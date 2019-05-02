package Kaposke.Utilities;

import java.util.ArrayList;
import java.util.List;

public class ActionDictionary {

    public static String buildActionString(boolean[] action) {
        String direction;

        direction = action[0] ? "Left" : "Right";
        if(action[0] == action[1])
            direction = "Idle";
        if(action[2])
            direction = "Down";

        String sprinting = action[4]? "Running" : "Walking";
        if(direction.equals("Idle") || direction.equals("Down"))
            sprinting = "";


        String jumping = action[3]  && !direction.equals("Down") ? "Jump" : "";

        return sprinting + direction + jumping;
    }

    public static String buildActionStringWithoutJump(boolean[] action) {
        String direction;

        direction = action[0] ? "Left" : "Right";
        if(action[0] == action[1])
            direction = "Idle";
        if(action[2])
            direction = "Down";

        String sprinting = action[4]? "Running" : "Walking";
        if(direction.equals("Idle") || direction.equals("Down"))
            sprinting = "";


        String jumping = action[3]  && !direction.equals("Down") ? "Jump" : "";

        return sprinting + direction;
    }

    // Oinc Oinc (BUT WORKS!)
    public static List<String> getAllActionsPossibilities() {
        List<String> possibleActions = new ArrayList<>();
        for (int a = 0; a < 2; a++)
            for (int b = 0; b < 2; b++)
                for (int c = 0; c < 2; c++)
                    for (int d = 0; d < 2; d++)
                        for (int e = 0; e < 2; e++) {
                            boolean[] action = {Utils.intToBoolean(a),
                                    Utils.intToBoolean(b),
                                    Utils.intToBoolean(c),
                                    Utils.intToBoolean(d),
                                    Utils.intToBoolean(e)};
                            String actionString = buildActionString(action);
                            if(!possibleActions.contains(actionString))
                                possibleActions.add(actionString);
                        }

        return possibleActions;
    }

    public static List<String> getAllActionsPossibilitiesButJump() {
        List<String> possibleActions = getAllActionsPossibilities();

        for (int i = possibleActions.size() - 1; i > 0; i--) {
            if(possibleActions.get(i).endsWith("Jump")) {
                possibleActions.remove(possibleActions.get(i));
            }
        }
        return possibleActions;
    }

    public static boolean[] getActionFromString(String actionString) {
        boolean[] action = new boolean[6];
        action[0] = actionString.contains("Left");
        action[1] = actionString.contains("Right");
        action[2] = actionString.contains("Down");
        action[3] = actionString.contains("Jump");
        action[4] = actionString.contains("Sprinting");
        action[5] = false;
        return action;
    }
}
