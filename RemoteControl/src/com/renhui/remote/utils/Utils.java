package com.renhui.remote.utils;

import javax.swing.*;
import java.io.File;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

public class Utils {


    public static String getRunningJarPath() {
        try {
            String path = Utils.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            File classFile = new File(path);
            return classFile.getParent();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isEmpty(String str) {
        char[] arrayOfChar;
        int i;
        byte b;
        for (arrayOfChar = str.toCharArray(), i = arrayOfChar.length, b = 0; b < i; ) {
            Character c = Character.valueOf(arrayOfChar[b]);
            if (Character.isLetterOrDigit(c.charValue()))
                return false;
            b++;
        }
        return true;
    }

    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            System.out.println("Interrupt Screen Update!");
        }
    }

    public static <T> boolean equalsOrder(List<T> c1, List<T> c2) {
        if (c1 == null || c2 == null || c1.size() != c2.size())
            return false;
        for (int i = 0; i < c1.size(); i++) {
            if (!c1.get(i).equals(c2.get(i)))
                return false;
        }
        return true;
    }

    public static void executeOnUiThread(Runnable r) {
        SwingUtilities.invokeLater(r);
    }

    public static String toString(double d) {
        return String.format(Locale.US, "%.3f", new Object[] { Double.valueOf(d) });
    }
}
