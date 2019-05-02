package Kaposke.Tasks;

import ch.idsia.benchmark.mario.engine.MarioVisualComponent;
import ch.idsia.benchmark.mario.environments.MarioEnvironment;
import ch.idsia.benchmark.tasks.BasicTask;

import ch.idsia.benchmark.tasks.Task;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.MarioAIOptions;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.Field;
import java.text.DecimalFormat;

public class CustomEvaluationTask extends BasicTask implements Task {
    private static final DecimalFormat df = new DecimalFormat("#.##");
    private EvaluationInfo localEvaluationInfo;
    private int difqualifications = 0;
    private int initialDificulty = 1;
    private int incrementDificulty = 3;

    public CustomEvaluationTask(MarioAIOptions marioAIOptions) {
        super(marioAIOptions);

        localEvaluationInfo = new EvaluationInfo();
        localEvaluationInfo.setTaskName("CustomGamePlayTask");
        localEvaluationInfo.distancePassedCells = 0;
        localEvaluationInfo.distancePassedPhys = 0;
        localEvaluationInfo.flowersDevoured = 0;
        localEvaluationInfo.killsTotal = 0;
        localEvaluationInfo.killsByFire = 0;
        localEvaluationInfo.killsByShell = 0;
        localEvaluationInfo.killsByStomp = 0;
        localEvaluationInfo.marioMode = 0;
        localEvaluationInfo.marioStatus = 0;
        localEvaluationInfo.mushroomsDevoured = 0;
        localEvaluationInfo.coinsGained = 0;
        localEvaluationInfo.timeLeft = 0;
        localEvaluationInfo.timeSpent = 0;
        localEvaluationInfo.hiddenBlocksFound = 0;
        localEvaluationInfo.totalNumberOfCoins = 0;
        localEvaluationInfo.totalNumberOfCreatures = 0;
        localEvaluationInfo.totalNumberOfFlowers = 0;
        localEvaluationInfo.totalNumberOfMushrooms = 0;
        localEvaluationInfo.totalNumberOfHiddenBlocks = 0;
        localEvaluationInfo.collisionsWithCreatures = 0;
        localEvaluationInfo.levelLength = 0;
    }

    private void updateEvaluationInfo(EvaluationInfo evInfo) {
        localEvaluationInfo.distancePassedCells += evInfo.distancePassedCells;
        localEvaluationInfo.distancePassedPhys += evInfo.distancePassedPhys;
        localEvaluationInfo.flowersDevoured += evInfo.flowersDevoured;
        localEvaluationInfo.killsTotal += evInfo.killsTotal;
        localEvaluationInfo.killsByFire += evInfo.killsByFire;
        localEvaluationInfo.killsByShell += evInfo.killsByShell;
        localEvaluationInfo.killsByStomp += evInfo.killsByStomp;
        localEvaluationInfo.marioMode += evInfo.marioMode;
        localEvaluationInfo.marioStatus += evInfo.marioStatus;
        localEvaluationInfo.mushroomsDevoured += evInfo.mushroomsDevoured;
        localEvaluationInfo.coinsGained += evInfo.coinsGained;
        localEvaluationInfo.timeLeft += evInfo.timeLeft;
        localEvaluationInfo.timeSpent += evInfo.timeSpent;
        localEvaluationInfo.hiddenBlocksFound += evInfo.hiddenBlocksFound;
        localEvaluationInfo.totalNumberOfCoins += evInfo.totalNumberOfCoins;
        localEvaluationInfo.totalNumberOfCreatures += evInfo.totalNumberOfCreatures;
        localEvaluationInfo.totalNumberOfFlowers += evInfo.totalNumberOfFlowers;
        localEvaluationInfo.totalNumberOfMushrooms += evInfo.totalNumberOfMushrooms;
        localEvaluationInfo.totalNumberOfHiddenBlocks += evInfo.totalNumberOfHiddenBlocks;
        localEvaluationInfo.collisionsWithCreatures += evInfo.collisionsWithCreatures;
        localEvaluationInfo.levelLength += evInfo.levelLength;
    }

    Thread episodes;

    public void doEpisodes(final int amount, final boolean verbose, final int repetitionsOfSingleEpisode) {
        episodes = new Thread() {
            @Override
            public void run() {
                if(marioFrameRefference == null)
                    setupMarioFrameRefference();
                openFrame();
                for (int i = 0; i < amount; ++i) {
                    options.setLevelLength((200 + (i * 12) + (options.getLevelRandSeed() % (i + 1))) % 512);
                    options.setLevelType(i % 3);
                    options.setLevelRandSeed(options.getLevelRandSeed() + i);
                    options.setLevelDifficulty(initialDificulty + incrementDificulty * i);
                    options.setGapsCount(i % 3 == 0);
                    options.setCannonsCount(i % 3 != 1);
                    options.setCoinsCount(i % 5 != 0);
                    options.setBlocksCount(i % 4 != 0);
                    options.setHiddenBlocksCount(i % 6 != 0);
                    options.setDeadEndsCount(i % 10 == 0);
                    options.setLevelLadder(i % 10 == 2);
                    options.setFrozenCreatures(i % 3 == 1);
                    options.setEnemies(i % 4 == 1 ? "off" : "");
                    options.setScale2X(true);
                    options.setDeadEndsCount(false);
                    reset();
                    if (!runSingleEpisode(repetitionsOfSingleEpisode))
                        difqualifications++;

                    updateEvaluationInfo(environment.getEvaluationInfo());

                    if (verbose)
                        System.out.println(environment.getEvaluationInfoAsString());
                }
                closeFrame();
            }
        };
        episodes.start();
    }

    JFrame marioFrameRefference;

    public void setupMarioFrameRefference() {
        try {
            Field marioFrameField = MarioVisualComponent.class.getDeclaredField("marioComponentFrame");
            marioFrameField.setAccessible(true);
            MarioVisualComponent marioVisualComponentInstance = MarioVisualComponent.getInstance(options, (MarioEnvironment) environment);
            marioFrameRefference = (JFrame) marioFrameField.get(marioVisualComponentInstance);
            marioFrameRefference.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                marioFrameRefference.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        episodes.stop();
                        episodes = new Thread();
                        marioFrameRefference.setVisible(false);
                    }
                });
        } catch (NoSuchFieldException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }

    public void openFrame() {
        marioFrameRefference.setLocationRelativeTo(null);
        marioFrameRefference.setVisible(true);
    }

    public void closeFrame() {
        marioFrameRefference.setVisible(false);
    }

    public EvaluationInfo getEvaluationInfo() {
        return localEvaluationInfo;
    }

    public void printStatistics() {
        System.out.println("\n[MarioAI] ~ Evaluation Results for Task: " + localEvaluationInfo.getTaskName() +
                "\n         Weighted Fitness : " + df.format(localEvaluationInfo.computeWeightedFitness()) +
                "\n             Mario Status : " + localEvaluationInfo.marioStatus +
                "\n               Mario Mode : " + localEvaluationInfo.marioMode +
                "\nCollisions with creatures : " + localEvaluationInfo.collisionsWithCreatures +
                "\n     Passed (Cells, Phys) : " + localEvaluationInfo.distancePassedCells + " of " + localEvaluationInfo.levelLength + ", " + df.format(localEvaluationInfo.distancePassedPhys) + " of " + df.format(localEvaluationInfo.levelLength * 16) + " (" + localEvaluationInfo.distancePassedCells * 100 / localEvaluationInfo.levelLength + "% passed)" +
                "\n Time Spent(marioseconds) : " + localEvaluationInfo.timeSpent +
                "\n  Time Left(marioseconds) : " + localEvaluationInfo.timeLeft +
                "\n             Coins Gained : " + localEvaluationInfo.coinsGained + " of " + localEvaluationInfo.totalNumberOfCoins + " (" + localEvaluationInfo.coinsGained * 100 / (localEvaluationInfo.totalNumberOfCoins == 0 ? 1 : localEvaluationInfo.totalNumberOfCoins) + "% collected)" +
                "\n      Hidden Blocks Found : " + localEvaluationInfo.hiddenBlocksFound + " of " + localEvaluationInfo.totalNumberOfHiddenBlocks + " (" + localEvaluationInfo.hiddenBlocksFound * 100 / (localEvaluationInfo.totalNumberOfHiddenBlocks == 0 ? 1 : localEvaluationInfo.totalNumberOfHiddenBlocks) + "% found)" +
                "\n       Mushrooms Devoured : " + localEvaluationInfo.mushroomsDevoured + " of " + localEvaluationInfo.totalNumberOfMushrooms + " found (" + localEvaluationInfo.mushroomsDevoured * 100 / (localEvaluationInfo.totalNumberOfMushrooms == 0 ? 1 : localEvaluationInfo.totalNumberOfMushrooms) + "% collected)" +
                "\n         Flowers Devoured : " + localEvaluationInfo.flowersDevoured + " of " + localEvaluationInfo.totalNumberOfFlowers + " found (" + localEvaluationInfo.flowersDevoured * 100 / (localEvaluationInfo.totalNumberOfFlowers == 0 ? 1 : localEvaluationInfo.totalNumberOfFlowers) + "% collected)" +
                "\n              kills Total : " + localEvaluationInfo.killsTotal + " of " + localEvaluationInfo.totalNumberOfCreatures + " found (" + localEvaluationInfo.killsTotal * 100 / (localEvaluationInfo.totalNumberOfCreatures == 0 ? 1 : localEvaluationInfo.totalNumberOfCreatures) + "%)" +
                "\n            kills By Fire : " + localEvaluationInfo.killsByFire +
                "\n           kills By Shell : " + localEvaluationInfo.killsByShell +
                "\n           kills By Stomp : " + localEvaluationInfo.killsByStomp +
                "\n        difqualifications : " + difqualifications);
//    System.out.println(localEvaluationInfo.toString());
//    System.out.println("Mario status sum: " + localEvaluationInfo.marioStatus);
//    System.out.println("Mario mode sum: " + localEvaluationInfo.marioMode);
    }

    public void setInitialDificulty(int initialDificulty) {
        this.initialDificulty = initialDificulty;
    }

    public void setIncrementDificulty(int incrementDificulty) {
        this.incrementDificulty = incrementDificulty;
    }
}
