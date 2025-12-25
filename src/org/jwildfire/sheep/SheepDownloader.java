package org.jwildfire.sheep;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

/**
 * Handles downloading of Electric Sheep genomes (flames).
 */
public class SheepDownloader {
    private static final String SHEEP_SERVER_URL = "https://v2d7c.sheepserver.net/gen/"; // Example URL, needs verification

    public void downloadSheep(String sheepId, String destinationPath) throws IOException {
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
    
    // Placeholder for listing available sheep
    public void listAvailableSheep() {
        // TODO: Implement API call to list sheep
        System.out.println("Listing sheep not implemented yet.");
    }
}
