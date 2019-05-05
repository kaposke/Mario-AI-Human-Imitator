package Kaposke.UI;

import Kaposke.Tasks.CustomEvaluationTask;
import Kaposke.Utilities.UtilitySingleton;
import Kaposke.Utilities.Utils;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AgentManager implements ActionListener {

    private String agent = "Kaposke.Agents.FakeHumanAgentVerbal";
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

        GridLayout layout = new GridLayout(7,2,5,5);

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
        if(e.getSource() == recordingSettingsButton) {
            new RecordingSettingsFrame();
        } // If recording Agent
        else if (e.getSource() == recordingAgentButton) {
            // Pergunta o nome e salva o caminho
            UtilitySingleton.getInstance().setArffPath(recordingsPath + "/" + JOptionPane.showInputDialog("Insira o seu nome"));
            playAgent("Kaposke.Agents.RecordingAgent");
            try {
                saveOptionsTo(UtilitySingleton.getInstance().getArffPath(), (int)amountOfLevelsSpinner.getValue(), (int)startingDifficultySpinner.getValue(), (int)difficultyIncreaseSpinner.getValue(), levelSeed);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } // If AI Agent
        else if(e.getSource() == AIAgentButton) {

            fileChooser.showOpenDialog(frame);
            File file = fileChooser.getSelectedFile();
            if(!file.exists())
                return;
            if (!file.getName().toLowerCase().endsWith(".arff")) {
                JOptionPane.showMessageDialog(frame, "Selecione um arquivo '.arff'", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            UtilitySingleton.getInstance().setArffPath(file.getPath());
            playAgent(agent);
        }
        if(e.getSource() == useRandomSeedCheckBox) {
            levelSeedSpinner.setEnabled(!useRandomSeedCheckBox.isSelected());
            levelSeedLabel.setEnabled(!useRandomSeedCheckBox.isSelected());

        }
    }

    private void playAgent(String agentPath) {
                Agent agent = AgentsPool.loadAgent(agentPath, false);
                if(!useRandomSeedCheckBox.isSelected()) {
                    levelSeed = (int)levelSeedSpinner.getValue();
                } else {
                    Random r = new Random();
                    levelSeed = r.nextInt();
                }
                evaluateAgent(agent, (int)amountOfLevelsSpinner.getValue(), (int)startingDifficultySpinner.getValue(), (int)difficultyIncreaseSpinner.getValue(), levelSeed);
    }

    private static void evaluateAgent(final Agent agent, int amountOfLevels, int startingDifficulty, int difficultyIncrease, int levelSeed) {

        final CustomEvaluationTask task = new CustomEvaluationTask(marioAIOptions);
        marioAIOptions.setAgent(agent);
        marioAIOptions.setLevelRandSeed(levelSeed);
        task.setOptionsAndReset(marioAIOptions);
        task.setInitialDificulty(startingDifficulty);
        task.setIncrementDificulty(difficultyIncrease);
        System.out.println("Evaluating agent " + agent.getName() + " with seed " + marioAIOptions.getLevelRandSeed());
        task.doEpisodes(amountOfLevels, false, 1);
    }

    private void saveOptionsTo(String path, int amountOfLevels, int startingDifficulty, int difficultyIncrease, int levelSeed ) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(String.valueOf(amountOfLevels));
        lines.add(String.valueOf(startingDifficulty));
        lines.add(String.valueOf(difficultyIncrease));
        lines.add(String.valueOf(levelSeed));
        Utils.createFileWith(path + " Config.txt", lines);
    }

    public static void main(String[] args) {
        new AgentManager();
    }
}
