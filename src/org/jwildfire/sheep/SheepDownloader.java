package org.jwildfire.sheep;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Handles downloading of Electric Sheep genomes (flames).
 */
public class SheepDownloader {
    private final SheepServer server;

    public SheepDownloader() {
        this.server = new SheepServer();
    }

    public void setConfig(String nickname, String serverUrl) {
        server.setConfig(nickname, serverUrl);
    }

    public String getNickname() { return server.getNickname(); }
    public String getServerUrl() { return server.getRedirectUrl(); }

    public void downloadSheep(String sheepId, String destinationPath) throws IOException {
        if (sheepId.startsWith("Mock")) {
            // Mock download
            try { Thread.sleep(500); } catch (InterruptedException e) {}
            File sampleFile = new File("resources/sheep/sample.flame");
            if (sampleFile.exists()) {
                Files.copy(sampleFile.toPath(), new File(destinationPath).toPath(), StandardCopyOption.REPLACE_EXISTING);
            } else {
                try (FileOutputStream out = new FileOutputStream(destinationPath)) {
                    String dummyXml = "<flame name='Dummy'></flame>";
                    out.write(dummyXml.getBytes());
                }
            }
            return;
        }

        if (sheepId.trim().equals("RENDER_JOB")) {
            try {
                String xml = server.fetchRenderingJob();
                try (FileOutputStream out = new FileOutputStream(destinationPath)) {
                    out.write(xml.getBytes());
                }
                return;
            } catch (Exception e) {
                throw new IOException("Failed to fetch rendering job", e);
            }
        }

        throw new IOException("Downloading specific sheep by ID '" + sheepId + "' is not supported yet. Please use RENDER_JOB.");
    }
    
    public List<String> listAvailableSheep() {
        try {
            Map<String, String> flock = server.getFlockList();
            List<String> list = new ArrayList<>();
            list.add("RENDER_JOB (Fetch new work)");
            
            for (Map.Entry<String, String> entry : flock.entrySet()) {
                list.add("Sheep " + entry.getKey() + " (" + entry.getValue() + ")");
            }
            return list;
        } catch (Exception e) {
            // Fallback to mock if server fails
            List<String> list = new ArrayList<>();
            list.add("Error: " + e.getMessage());
            list.add("Mock Sheep 1 (Gold)");
            list.add("Mock Sheep 2 (Blue)");
            return list;
        }
    }
}
