package org.jwildfire.base;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ToolsTest {

    @Test
    public void testAppVersion() {
        String version = Tools.getAppVersion();
        assertNotNull(version, "Version should not be null");
        assertFalse(version.isEmpty(), "Version should not be empty");
        
        // The version format is typically "V9.xx (dd.MM.yyyy)"
        // or just the hardcoded fallback if the resource isn't generated during test execution in IDE
        // But since we use Gradle, the resource might be generated.
        
        System.out.println("Testing Version: " + version);
        
        // Basic sanity check
        assertTrue(version.startsWith("V") || version.matches("\\d+\\.\\d+.*"), "Version should start with V or be a number");
    }

    @Test
    public void testHexStringConversion() {
        String original = "Hello World";
        byte[] bytes = original.getBytes();
        String hex = Tools.byteArrayToHexString(bytes);
        byte[] back = Tools.hexStringToByteArray(hex);
        String result = new String(back);
        
        assertEquals(original, result, "Hex string conversion roundtrip failed");
    }
    
    @Test
    public void testLimitValue() {
        assertEquals(10, Tools.limitValue(15, 0, 10));
        assertEquals(0, Tools.limitValue(-5, 0, 10));
        assertEquals(5, Tools.limitValue(5, 0, 10));
    }
}
