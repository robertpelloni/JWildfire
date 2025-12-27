package org.jwildfire.sheep;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

import org.jwildfire.base.Prefs;
import org.jwildfire.swing.JWildfire;

public class ElectricSheepInternalFrame extends JInternalFrame {
    private final JWildfire desktop;
    private final SheepDownloader downloader;
    private SheepRenderer renderer;
    private JList<String> sheepList;
    private DefaultListModel<String> listModel;
    private JTextArea logArea;
    private JPanel renderPanel;

    public ElectricSheepInternalFrame(JWildfire desktop) {
        super("Electric Sheep", true, true, true, true);
        this.desktop = desktop;
        this.downloader = new SheepDownloader();
        
        initUI();
    }

    private void initUI() {
        setSize(900, 600);
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

        // Center: Split Pane (List vs Render)
        listModel = new DefaultListModel<>();
        sheepList = new JList<>(listModel);
        JScrollPane listScroll = new JScrollPane(sheepList);
        listScroll.setBorder(BorderFactory.createTitledBorder("Available Sheep"));
        listScroll.setPreferredSize(new Dimension(250, 0));

        renderPanel = new JPanel();
        renderPanel.setBorder(BorderFactory.createTitledBorder("Preview"));
        renderer = new SheepRenderer(renderPanel);
        
        JSplitPane centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScroll, renderPanel);
        centerSplit.setDividerLocation(250);

        // Bottom: Log
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setRows(6);
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder("Log"));

        JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, centerSplit, logScroll);
        mainSplit.setDividerLocation(450);
        add(mainSplit, BorderLayout.CENTER);
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
        
        String id = parseId(selected);
        
        log("Downloading " + id + "...");
        new Thread(() -> {
            try {
                // TODO: Define download path in Prefs
                String filename = id.equals("RENDER_JOB") ? "render_job.flame" : id + ".xml";
                String path = System.getProperty("java.io.tmpdir") + "/" + filename;
                downloader.downloadSheep(id, path);
                SwingUtilities.invokeLater(() -> log("Downloaded to " + path));
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> log("Error: " + e.getMessage()));
            }
        }).start();
    }

    private String parseId(String displayString) {
        if (displayString.startsWith("RENDER_JOB")) return "RENDER_JOB";
        if (displayString.startsWith("Sheep ")) {
            int start = 6; // "Sheep ".length()
            int end = displayString.indexOf(" ", start);
            if (end > 0) {
                return displayString.substring(start, end);
            }
        }
        return displayString; // Fallback
    }

    private void renderSelectedSheep() {
        String selected = sheepList.getSelectedValue();
        if (selected == null) return;
        
        String id = parseId(selected);
        String filename = id.equals("RENDER_JOB") ? "render_job.flame" : id + ".xml";
        String path = System.getProperty("java.io.tmpdir") + "/" + filename;
        
        File f = new File(path);
        if (!f.exists()) {
            log("File not found: " + path + ". Please download first.");
            return;
        }
        
        log("Rendering " + id + "...");
        renderer.renderSheep(path);
    }

    private void log(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}
