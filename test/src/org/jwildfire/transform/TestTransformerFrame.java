package org.jwildfire.transform;

import javax.swing.JFrame;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.Buffer;

public class TestTransformerFrame { // Removed extends JFrame
    public static boolean live = false;
    public SimpleImage si;
    private Buffer buffer;

    public TestTransformerFrame() {
        si = new SimpleImage(500, 500);
        buffer = new Buffer(null, "TestBuffer", si);
    }

    public Buffer getBuffer() {
        return buffer;
    }

    public void pack() {
        // Dummy method to satisfy tests
    }

    public void dispose() {
        // Dummy method to satisfy tests
        if (buffer != null) {
            buffer.flush();
        }
    }
}
