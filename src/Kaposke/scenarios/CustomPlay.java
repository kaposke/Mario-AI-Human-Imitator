package Kaposke.scenarios;

import Kaposke.Tasks.CustomEvaluationTask;
import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.MarioVisualComponent;
import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.benchmark.tasks.GamePlayTask;
import ch.idsia.benchmark.tasks.MarioCustomSystemOfValues;
import ch.idsia.benchmark.tasks.Task;
import ch.idsia.tools.MarioAIOptions;

import javax.swing.*;
import java.awt.*;

public class CustomPlay {

    final static int numberOfLevels = 3;
    private static boolean detailedStats = false;
    private static MarioAIOptions marioAIOptions = new MarioAIOptions();

    public static void evaluateAgent(final Agent agent)
    {
        final CustomEvaluationTask task = new CustomEvaluationTask(marioAIOptions);
        //marioAIOptions.setAgent(agent);
        task.setOptionsAndReset(marioAIOptions);
        System.out.println("Evaluating agent " + agent.getName() + " with seed " + marioAIOptions.getLevelRandSeed());
        task.doEpisodes(numberOfLevels, false, 1);
        task.printStatistics();
    }


    public static void main(String[] args)
    {
        marioAIOptions.setArgs(args);
        evaluateAgent(marioAIOptions.getAgent());
        System.exit(0);
    }
}