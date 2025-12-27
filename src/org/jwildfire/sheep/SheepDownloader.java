package org.jwildfire.sheep;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Handles downloading of Electric Sheep genomes (flames).
 */
public class SheepDownloader {
    private static final String SHEEP_SERVER_URL = "https://v2d7c.sheepserver.net/gen/"; // Example URL, needs verification

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

        String fileUrl = SHEEP_SERVER_URL + sheepId + ".xml"; // Assuming XML format
        try (BufferedInputStream in = new BufferedInputStream(new URL(fileUrl).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(destinationPath)) {
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        }
    }
    
    public java.util.List<String> listAvailableSheep() {
        // Mock implementation
        java.util.List<String> list = new java.util.ArrayList<>();
        list.add("Mock Sheep 1 (Gold)");
        list.add("Mock Sheep 2 (Blue)");
        list.add("Mock Sheep 3 (Fractal)");
        // In real implementation, parse JSON/XML from server
        return list;
    }
}
