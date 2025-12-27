package org.jwildfire.sheep;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
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

    public void downloadSheep(String sheepId, String destinationPath) throws IOException {
        // Mock implementation for now
        if (sheepId.startsWith("Mock")) {
            System.out.println("Simulating download for " + sheepId);
            try { Thread.sleep(500); } catch (InterruptedException e) {}
            
            // Copy sample file
            File sampleFile = new File("resources/sheep/sample.flame");
            if (sampleFile.exists()) {
                Files.copy(sampleFile.toPath(), new File(destinationPath).toPath(), StandardCopyOption.REPLACE_EXISTING);
            } else {
                // Fallback if resource not found (e.g. running from jar)
                // Create a dummy file
                try (FileOutputStream out = new FileOutputStream(destinationPath)) {
                    String dummyXml = "<flame name='Dummy'></flame>";
                    out.write(dummyXml.getBytes());
                }
            }
            return;
        }

        // TODO: Implement real genome download.
        // Currently, the API for fetching a specific genome by ID is not fully clear.
        // We might need to use the render server's /cgi/get endpoint or similar.
        System.err.println("Real genome download not yet implemented for ID: " + sheepId);
    }
    
    public List<String> listAvailableSheep() {
        try {
            Map<String, String> flock = server.getFlockList();
            List<String> list = new ArrayList<>();
            for (Map.Entry<String, String> entry : flock.entrySet()) {
                list.add("Sheep " + entry.getKey() + " (" + entry.getValue() + ")");
            }
            return list;
        } catch (Exception e) {
            System.err.println("Failed to fetch flock list: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback to mock
            List<String> list = new ArrayList<>();
            list.add("Mock Sheep 1 (Gold)");
            list.add("Mock Sheep 2 (Blue)");
            list.add("Mock Sheep 3 (Fractal)");
            list.add("Error fetching real list: " + e.getMessage());
            return list;
        }
    }
}
