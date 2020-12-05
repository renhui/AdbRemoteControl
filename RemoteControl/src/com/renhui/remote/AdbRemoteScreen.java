package com.renhui.remote;

import com.renhui.remote.core.AdbHelper;
import com.renhui.remote.ui.MainUIFrame;
import com.renhui.remote.utils.Utils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Adb 远程控制设备 任务
 */
public class AdbRemoteScreen implements Runnable {

    @Override
    public void run() {
        try {
            File adbExecutable = loadInternalAdbExecutable();
            if (adbExecutable == null) {
                System.out.println("Fatal Error, Cannot load adb binary");
                return;
            }
            System.out.println("File adbExecutable path = " + adbExecutable.getAbsolutePath());
            // Adb执行帮助类 对象
            AdbHelper adbHelper = new AdbHelper(adbExecutable);
            MainUIFrame mainUIFrame = new MainUIFrame(adbHelper);
            mainUIFrame.pack(); // 展示界面
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 创建可执行的内部adb文件
    private File loadInternalAdbExecutable() throws MalformedURLException {

        File adbExecutableFile = new File("adb_for_windows");

        URL adbExecutableURL = adbExecutableFile.toURL();

        File tempDirectory = new File(Utils.getRunningJarPath() + File.separator + "Temp");

        if (!tempDirectory.exists()) {
            tempDirectory.mkdirs();
        }

        tempDirectory.deleteOnExit();

        File tempAdbExecutable = new File(tempDirectory, "adb");
        tempAdbExecutable.deleteOnExit();

        try {
            if (!tempAdbExecutable.exists()) {
                tempAdbExecutable.createNewFile();
            }
            createTempExecutable(adbExecutableURL, tempAdbExecutable);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tempAdbExecutable;
    }

    private void createTempExecutable(URL adbSourceExecutableURL, File destFile) throws IOException {
        InputStream is = adbSourceExecutableURL.openStream();
        OutputStream os = new FileOutputStream(destFile);
        byte[] b = new byte[2048];
        int length;
        while ((length = is.read(b)) != -1)
            os.write(b, 0, length);
        is.close();
        os.close();
    }


}
