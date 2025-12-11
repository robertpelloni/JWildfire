package org.jwildfire.base;
import org.lwjgl.Version;
public class LWJGLSupport {
    public static void verify() {
        System.out.println("LWJGL Version: " + Version.getVersion());
    }
    public static void main(String[] args) {
        verify();
    }
}
