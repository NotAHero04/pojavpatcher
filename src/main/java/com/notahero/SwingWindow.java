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
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
                if (inputStream != null) {
                    Files.copy(inputStream, Path.of(file.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
                }
                statusLabel.setText(successString);
            } catch (IOException exception) {
                exception.printStackTrace();
                statusLabel.setText(exceptionString);
            }
        }
    }

    public void mainEvent() {
        headerLabel.setText("Choose an action.");
        Object[] choices = {"Patch Sodium", "Patch Sodium Extra"};
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
