package Kaposke.UI;

import Kaposke.Models.SettingsModel;
import Kaposke.Utilities.SettingsHandler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

class RecordingSettingsFrame extends JFrame implements ActionListener {

    private JPanel mainPanel;
    private GridBagConstraints gbc;

    private JSpinner colsSpinner;
    private JSpinner rowsSpinner;

    private JComboBox ZLevelSceneCombo;
    private JComboBox ZLevelEnemiesCombo;

    private JCheckBox marioModeCheck;
    private JCheckBox isMarioOnGroundCheck;
    private JCheckBox isMarioAbleToJumpCheck;
    private JCheckBox isMarioAbleToShootCheck;
    private JCheckBox isMarioCarryingCheck;

    private JButton saveButton;

    RecordingSettingsFrame() {
        super("Recording Settings");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 800);

        createSettingsComponents();

        loadSettings();

        pack();
        setVisible(true);
    }

    private void createSettingsComponents() {
        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 10;

        createGridSetupComponent();
        createMarioStatsComponents();

        saveButton = new JButton("Save");
        saveButton.addActionListener(this);

        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 4;
        mainPanel.add(saveButton, gbc);

        add(mainPanel);
    }

    private void createGridSetupComponent() {
        colsSpinner = new JSpinner();
        rowsSpinner = new JSpinner();

        colsSpinner.setValue(20);
        rowsSpinner.setValue(20);

        gbc.gridy = 0;

        gbc.gridx = 0;
        mainPanel.add(new JLabel("Visible Grid Size:"), gbc);

        gbc.gridx = 1;
        gbc.ipadx = 25;
        mainPanel.add(colsSpinner, gbc);

        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(new JLabel("x"), gbc);

        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridx = 3;
        mainPanel.add(rowsSpinner, gbc);

        // ZLevel Dropdowns
        String[] levels = { "0", "1", "2" };
        ZLevelSceneCombo = new JComboBox(levels);
        ZLevelEnemiesCombo = new JComboBox(levels);

        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridx = 0;

        gbc.gridy = 1;
        mainPanel.add(new JLabel("ZLevel Scene"), gbc);
        gbc.gridy = 2;
        mainPanel.add(new JLabel("ZLevel Enemies"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;

        gbc.gridy = 1;
        mainPanel.add(ZLevelSceneCombo, gbc);
        gbc.gridy = 2;
        mainPanel.add(ZLevelEnemiesCombo, gbc);

        gbc.gridwidth = 1;
    }

    private void createMarioStatsComponents() {
        marioModeCheck = new JCheckBox();
        isMarioOnGroundCheck = new JCheckBox();
        isMarioAbleToJumpCheck = new JCheckBox();
        isMarioAbleToShootCheck = new JCheckBox();
        isMarioCarryingCheck = new JCheckBox();

        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridx = 0;

        gbc.gridy = 3;
        mainPanel.add(new JLabel("marioMode"), gbc);
        gbc.gridy = 4;
        mainPanel.add(new JLabel("isMarioOnGround"), gbc);
        gbc.gridy = 5;
        mainPanel.add(new JLabel("isMarioAbleToJump"), gbc);
        gbc.gridy = 6;
        mainPanel.add(new JLabel("isMarioAbleToShoot"), gbc);
        gbc.gridy = 7;
        mainPanel.add(new JLabel("isMarioCarrying"), gbc);

        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.gridx = 3;

        gbc.gridy = 3;
        mainPanel.add(marioModeCheck, gbc);
        gbc.gridy = 4;
        mainPanel.add(isMarioOnGroundCheck, gbc);
        gbc.gridy = 5;
        mainPanel.add(isMarioAbleToJumpCheck, gbc);
        gbc.gridy = 6;
        mainPanel.add(isMarioAbleToShootCheck, gbc);
        gbc.gridy = 7;
        mainPanel.add(isMarioCarryingCheck, gbc);
    }

    private void loadSettings() {

        SettingsModel settings = null;
        try {
            settings = SettingsHandler.loadSettings();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        colsSpinner.setValue(settings.GridWidth);
        rowsSpinner.setValue(settings.GridHeight);

        ZLevelSceneCombo.setSelectedIndex(settings.ZLevelScene);
        ZLevelEnemiesCombo.setSelectedIndex(settings.ZLevelEnemies);

        marioModeCheck.setSelected(settings.marioMode);
        isMarioOnGroundCheck.setSelected(settings.isMarioOnGround);
        isMarioAbleToJumpCheck.setSelected(settings.isMarioAbleToJump);
        isMarioAbleToShootCheck.setSelected(settings.isMarioAbleToShoot);
        isMarioCarryingCheck.setSelected(settings.isMarioCarrying);
    }

    private void saveSettings() {
        SettingsModel settings = new SettingsModel();
        settings.GridWidth = (int) colsSpinner.getValue();
        settings.GridHeight = (int) rowsSpinner.getValue();

        settings.ZLevelScene = (int) ZLevelSceneCombo.getSelectedIndex();
        settings.ZLevelEnemies = (int) ZLevelEnemiesCombo.getSelectedIndex();

        settings.marioMode = marioModeCheck.isSelected();
        settings.isMarioOnGround = isMarioOnGroundCheck.isSelected();
        settings.isMarioAbleToJump = isMarioAbleToJumpCheck.isSelected();
        settings.isMarioAbleToShoot = isMarioAbleToShootCheck.isSelected();
        settings.isMarioCarrying = isMarioCarryingCheck.isSelected();

        try {
            SettingsHandler.saveSettings(settings);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Dispose settings window
        dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveButton) {
            saveSettings();
        }
    }
}