package org.jwildfire.sheep;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Prefs;
import org.jwildfire.swing.JWildfire;

public class ElectricSheepInternalFrame extends JInternalFrame {
    private final JWildfire desktop;
    private final SheepDownloader downloader;
    private final SheepRenderer renderer;
    private JList<String> sheepList;
    private DefaultListModel<String> listModel;
    private JTextArea logArea;

    public ElectricSheepInternalFrame(JWildfire desktop) {
        super("Electric Sheep", true, true, true, true);
        this.desktop = desktop;
        this.downloader = new SheepDownloader();
        this.renderer = new SheepRenderer(null); // TODO: Pass actual renderer
        
        initUI();
    }

    private void initUI() {
        setSize(600, 500);
        setLayout(new BorderLayout());

        // Top Panel: Controls
        JPanel topPanel = new JPanel();
        JButton refreshButton = new JButton("Refresh Server");
        JButton downloadButton = new JButton("Download Selected");
        JButton renderButton = new JButton("Render Selected");
        
        refreshButton.addActionListener(e -> refreshSheepList());
        downloadButton.addActionListener(e -> downloadSelectedSheep());
        renderButton.addActionListener(e -> renderSelectedSheep());
        
        topPanel.add(refreshButton);
        topPanel.add(downloadButton);
        topPanel.add(renderButton);
        add(topPanel, BorderLayout.NORTH);

        // Center Panel: Split Pane (List + Log)
        listModel = new DefaultListModel<>();
        sheepList = new JList<>(listModel);
        JScrollPane listScroll = new JScrollPane(sheepList);
        listScroll.setBorder(BorderFactory.createTitledBorder("Available Sheep"));

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder("Log"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, listScroll, logScroll);
        splitPane.setDividerLocation(300);
        add(splitPane, BorderLayout.CENTER);
    }

    private void refreshSheepList() {
        log("Fetching sheep list from server...");
        new Thread(() -> {
            List<String> sheep = downloader.listAvailableSheep();
            SwingUtilities.invokeLater(() -> {
                listModel.clear();
                for (String s : sheep) {
                    listModel.addElement(s);
                }
                log("Found " + sheep.size() + " sheep.");
            });
        }).start();
    }

    private void downloadSelectedSheep() {
        String selected = sheepList.getSelectedValue();
        if (selected == null) return;
        
        log("Downloading " + selected + "...");
        new Thread(() -> {
            try {
                // TODO: Define download path in Prefs
                String path = System.getProperty("java.io.tmpdir") + "/" + selected + ".xml";
                downloader.downloadSheep(selected, path);
                SwingUtilities.invokeLater(() -> log("Downloaded to " + path));
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> log("Error: " + e.getMessage()));
            }
        }).start();
    }

    private void renderSelectedSheep() {
        String selected = sheepList.getSelectedValue();
        if (selected == null) return;
        log("Rendering " + selected + " (Not fully implemented)");
        // renderer.renderSheep(...);
    }

    private void log(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}
