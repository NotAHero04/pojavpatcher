package com.notahero;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SwingWindow {
    private final JLabel headerLabel, statusLabel;
    private final JPanel controlPanel;
    private final JFrame mainFrame;


    public SwingWindow(int width, int height) {
        mainFrame = new JFrame("PojavPatcher");
        mainFrame.setSize(width, height);
        mainFrame.setLayout(new GridLayout(3, 1));
        headerLabel = new JLabel("", JLabel.CENTER);
        statusLabel = new JLabel("", JLabel.CENTER);
        statusLabel.setSize(300, 100);
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
            }
        });
        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        for (JComponent component : new JComponent[]{headerLabel, controlPanel, statusLabel}) {
            mainFrame.add(component);
        }
        mainFrame.setVisible(true);
    }
    public SwingWindow() {
        this(480, 270);
    }

    public void patch(String patchPath, String destPath, String successString, String notInstalledString, String exceptionString) {
        File file = new File(System.getProperty("user.dir"), destPath);
        if (!file.exists()) {
            statusLabel.setText(notInstalledString);
        } else {
            try {
                InputStream inputStream = SwingWindow.this.getClass().getClassLoader().getResourceAsStream(patchPath);
                Files.copy(inputStream, Path.of(file.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
                statusLabel.setText(successString);
            } catch (Exception exception) {
                exception.printStackTrace();
                statusLabel.setText(exceptionString);
            }
        }
    }

    public void patchForgeEvent(JComboBox<Object> parentBox, JButton parentButton) {
        String[] supportedVersions = {"1.3.2-forge-4.3.5.318", "1.4.2-forge-6.0.1.355", "1.4.4-forge-6.3.0.378", "1.4.5-forge-6.4.2.448", "1.4.6-forge-6.5.0.489", "1.4.7-forge-6.6.2.534", "1.5.1-Forge7.7.2.682", "1.5.2-Forge7.8.1.738", "Forge8.9.0.775", "1.6.2-Forge9.10.1.871", "1.6.4-Forge9.11.1.1345", "1.7.2-Forge10.12.2.1161-mc172"};
        String[] mcVersions = {"1.3.2", "1.4.2", "1.4.4", "1.4.5", "1.4.6", "1.4.7", "1.5.1", "1.5.2", "1.6.1", "1.6.2", "1.6.4", "1.7.2"};
        List<String> list = new ArrayList<>();
        for (int i = 0; i < mcVersions.length; i++) {
            String fileName = supportedVersions[i];
            File file = new File(System.getProperty("user.dir"), "versions/" + fileName + "/" + fileName + ".json");
            if (file.exists()) {
                list.add(mcVersions[i]);
            }
        }
        if (list.isEmpty()) {
            statusLabel.setText("Patching Forge failed, reason: no versions to patch");
        } else {
            headerLabel.setText("Choose version to patch.");
            JComboBox<Object> box = new JComboBox<>(list.toArray(new String[0]));
            JButton okButton = new JButton("OK");
            JButton cancelButton = new JButton("Cancel");
            okButton.addActionListener(e -> {
                String version = Objects.requireNonNull(box.getSelectedItem()).toString();
                int index = Arrays.asList(mcVersions).indexOf(version);
                String fileName = supportedVersions[index];
                patch("patches/" + fileName + ".json",
                        "versions/" + fileName + "/" + fileName + ".json",
                        "Patching Forge successful, version: " + version,
                        "",
                        "Patching Forge failed, reason: caught exception"
                        );
            });
            cancelButton.addActionListener(e -> {
                headerLabel.setText("Choose an action.");
                parentBox.setVisible(true);
                parentButton.setVisible(true);
                box.setVisible(false);
                okButton.setVisible(false);
                cancelButton.setVisible(false);
            });
            controlPanel.add(box);
            controlPanel.add(okButton);
            controlPanel.add(cancelButton);
        }
    }
    public void mainEvent() {
        headerLabel.setText("Choose an action.");
        Object[] choices = {"Patch Sodium", "Patch Sodium Extra", "Patch Forge (1.3.2-1.7.2)"};
        JComboBox<Object> box = new JComboBox<>(choices);
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            String choice = Objects.requireNonNull(box.getSelectedItem()).toString();
            switch(choice) {
                case "Patch Sodium" -> patch("patches/sodium-mixins.properties",
                        "config/sodium-mixins.properties",
                        "Patching Sodium successful",
                        "Patching Sodium failed, reason: not installed",
                        "Patching Sodium failed, reason: caught exception");
                case "Patch Sodium Extra" -> patch("patches/sodium-extra.properties",
                        "config/sodium-extra.properties",
                        "Patching Sodium Extra successful",
                        "Patching Sodium Extra failed, reason: not installed",
                        "Patching Sodium Extra failed, reason: caught exception");
                case "Patch Forge (1.3.2-1.7.2)" -> {
                    box.setVisible(false);
                    okButton.setVisible(false);
                    patchForgeEvent(box, okButton);
                }
                default -> throw new IllegalArgumentException("Unexpected choice: " + choice);
            }
        });
        controlPanel.add(box);
        controlPanel.add(okButton);
        okButton.setVisible(true);
        box.setVisible(true);
        mainFrame.setVisible(true);
    }
}
