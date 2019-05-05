package Kaposke.UI;

import Kaposke.Models.SettingsModel;
import Kaposke.Utilities.ArffReader;
import Kaposke.Utilities.SettingsHandler;
import Kaposke.Tasks.CustomEvaluationTask;
import Kaposke.Utilities.UtilitySingleton;
import ch.idsia.agents.Agent;
import ch.idsia.agents.AgentsPool;
import ch.idsia.tools.MarioAIOptions;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class AgentManager implements ActionListener {

    private String aiAgent = "Kaposke.Agents.FakeHumanAgentVerbal";
    private String defaultDirectory = "CollectedData/PlayerRecordings";
    private String recordingsPath = "CollectedData/PlayerRecordings";
    private String settingsPath = "Settings/settings.json";

    private JFrame frame;
    private JPanel panel;
    private JSpinner amountOfLevelsSpinner;
    private JSpinner startingDifficultySpinner;
    private JSpinner difficultyIncreaseSpinner;

    private JCheckBox useRandomSeedCheckBox;

    private JLabel levelSeedLabel;
    private JSpinner levelSeedSpinner;

    private JButton recordingAgentButton;
    private JButton recordingSettingsButton;
    private JButton AIAgentButton;

    private JFileChooser fileChooser;

    private int levelSeed = 1;

    private static MarioAIOptions marioAIOptions = new MarioAIOptions();

    private AgentManager() {
        setupGui();

        UtilitySingleton.getInstance().setSettingsPath(settingsPath);
    }

    private void setupGui() {
        frame = new JFrame("Kaposke's Mario AI Agent Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 450);

        GridLayout layout = new GridLayout(7, 2, 5, 5);

        panel = new JPanel();
        panel.setLayout(layout);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Row 1
        panel.add(new JLabel("Amount of levels:"));

        amountOfLevelsSpinner = new JSpinner();
        amountOfLevelsSpinner.setValue(1);

        panel.add(amountOfLevelsSpinner);

        // Row 2
        panel.add(new JLabel("Starting Difficulty:"));

        startingDifficultySpinner = new JSpinner();
        startingDifficultySpinner.setValue(0);

        panel.add(startingDifficultySpinner);

        // Row 3
        panel.add(new JLabel("Difficulty Increase Rate:"));

        difficultyIncreaseSpinner = new JSpinner();
        difficultyIncreaseSpinner.setValue(0);

        panel.add(difficultyIncreaseSpinner);

        // Row 4
        useRandomSeedCheckBox = new JCheckBox("Use random level seed");
        useRandomSeedCheckBox.setSelected(true);
        useRandomSeedCheckBox.addActionListener(this);

        panel.add(useRandomSeedCheckBox);

        panel.add(new JPanel());

        // Row 5
        levelSeedLabel = new JLabel("LevelSeed");
        levelSeedLabel.setEnabled(!useRandomSeedCheckBox.isSelected());
        panel.add(levelSeedLabel);

        levelSeedSpinner = new JSpinner();
        levelSeedSpinner.setValue(1);
        levelSeedSpinner.setEnabled(!useRandomSeedCheckBox.isSelected());

        panel.add(levelSeedSpinner);

        // Row 6
        recordingSettingsButton = new JButton("Settings");
        recordingSettingsButton.addActionListener(this);
        panel.add(recordingSettingsButton);

        recordingAgentButton = new JButton("Play RecordingAgent");
        recordingAgentButton.addActionListener(this);
        panel.add(recordingAgentButton);

        // Row 7
        AIAgentButton = new JButton("Play AI Agent");
        AIAgentButton.addActionListener(this);
        panel.add(AIAgentButton);

        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(defaultDirectory));
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".arff") || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return null;
            }
        };

        fileChooser.setFileFilter(filter);

        frame.add(panel);

        frame.pack();
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // If settings
        if (e.getSource() == recordingSettingsButton) {
            RecodingSettingsPressed();
        } // If recording Agent
        else if (e.getSource() == recordingAgentButton) {
            RecordingAgentPressed();
        } // If AI Agent
        else if (e.getSource() == AIAgentButton) {
            AIAgentPressed();
        }
        if (e.getSource() == useRandomSeedCheckBox) {
            levelSeedSpinner.setEnabled(!useRandomSeedCheckBox.isSelected());
            levelSeedLabel.setEnabled(!useRandomSeedCheckBox.isSelected());
        }
    }

    private void RecodingSettingsPressed() {
        new RecordingSettingsFrame();
    }

    private void RecordingAgentPressed() {
        // Pergunta o nome e salva o caminho
        String playerName = JOptionPane.showInputDialog("Insira o seu nome", "");

        if(playerName == null || playerName.length() == 0) return;

        UtilitySingleton.getInstance().setArffPath(recordingsPath + "/" + playerName);
        playRecordingAgent();
    }

    private void AIAgentPressed() {
        fileChooser.showOpenDialog(frame);
        File file = fileChooser.getSelectedFile();
        if (!file.exists())
            return;
        if (!file.getName().toLowerCase().endsWith(".arff")) {
            JOptionPane.showMessageDialog(frame, "Selecione um arquivo '.arff'", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        UtilitySingleton.getInstance().setArffPath(file.getPath());
        playFakeHumanAgent();
    }

    private void playAgent(String agentPath) {
        Agent agent = AgentsPool.loadAgent(agentPath, false);
        if (!useRandomSeedCheckBox.isSelected()) {
            levelSeed = (int) levelSeedSpinner.getValue();
        } else {
            Random r = new Random();
            levelSeed = r.nextInt();
        }
        evaluateAgent(agent, (int) amountOfLevelsSpinner.getValue(), (int) startingDifficultySpinner.getValue(), (int) difficultyIncreaseSpinner.getValue(), levelSeed);
    }

    private void playRecordingAgent() {
        Agent agent = AgentsPool.loadAgent("Kaposke.Agents.RecordingAgent", false);

        if (!useRandomSeedCheckBox.isSelected()) {
            levelSeed = (int) levelSeedSpinner.getValue();
        } else {
            Random r = new Random();
            levelSeed = r.nextInt();
        }

        try {
            SettingsModel settings = SettingsHandler.loadSettings();

            marioAIOptions.setReceptiveFieldWidth(settings.GridWidth);
            marioAIOptions.setReceptiveFieldHeight(settings.GridHeight);

            evaluateAgent(agent, (int) amountOfLevelsSpinner.getValue(), (int) startingDifficultySpinner.getValue(), (int) difficultyIncreaseSpinner.getValue(), levelSeed);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playFakeHumanAgent() {
        Agent agent = AgentsPool.loadAgent(aiAgent, false);

        if (!useRandomSeedCheckBox.isSelected()) {
            levelSeed = (int) levelSeedSpinner.getValue();
        } else {
            Random r = new Random();
            levelSeed = r.nextInt();
        }

        try {
            // Loads comment from arff file and gets settings used on it. Smelly stuff, but I've got no time for better ideas.
            List<String> headerComments = ArffReader.getHeaderComments(UtilitySingleton.getInstance().getArffPath());

            if(!headerComments.isEmpty()) {
                SettingsModel settings = SettingsHandler.fromJson(headerComments.get(0));

                marioAIOptions.setReceptiveFieldWidth(settings.GridWidth);
                marioAIOptions.setReceptiveFieldHeight(settings.GridHeight);
            }

            evaluateAgent(agent, (int) amountOfLevelsSpinner.getValue(), (int) startingDifficultySpinner.getValue(), (int) difficultyIncreaseSpinner.getValue(), levelSeed);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void evaluateAgent(final Agent agent, int amountOfLevels, int startingDifficulty, int difficultyIncrease, int levelSeed) {

        marioAIOptions.setAgent(agent);
        marioAIOptions.setLevelRandSeed(levelSeed);

        final CustomEvaluationTask task = new CustomEvaluationTask(marioAIOptions);

        task.setInitialDificulty(startingDifficulty);
        task.setIncrementDificulty(difficultyIncrease);

        System.out.println("Evaluating agent " + agent.getName() + " with seed " + marioAIOptions.getLevelRandSeed());

        task.doEpisodes(amountOfLevels, false, 1);
    }

    public static void main(String[] args) {
        new AgentManager();
    }
}
