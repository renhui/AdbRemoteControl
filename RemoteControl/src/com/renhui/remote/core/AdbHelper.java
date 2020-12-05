package com.renhui.remote.core;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Adb执行帮助类
 */
public class AdbHelper {

    private String adbPath;

    private AdbCommandBuilder commandBuilder;

    private AdbDevicesWatcher adbDevicesWatcher;

    private List<AdbDevice> attachedDevices;  // 当前链接的设备列表

    private int currentDeviceIndex;  // 当前的设备索引

    private OnAttachedDevicesChangedListener onAttachedDevicesChangedListener;

    public AdbHelper(File adbFile) {
        this.adbPath = adbFile.getAbsolutePath();
        this.commandBuilder = new AdbCommandBuilder(this);

        /**
         * 在jvm中增加一个关闭的钩子，当jvm关闭的时候，会执行系统中已经设置的所有通过
         * 方法addShutdownHook添加的钩子，当系统执行完这些钩子后，jvm才会关闭。
         * 所以这些钩子可以在jvm关闭的时候进行内存清理、对象销毁等操作。
         */
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                executeCommand(commandBuilder.buildKillServerCommand());
            }
        }));

        adbDevicesWatcher = new AdbDevicesWatcher(this);
        adbDevicesWatcher.startWatch();
    }

    private void executeCommand(List<String> command) {
        CommandExec.execAsync(command, null);
    }

    public void release() {
        adbDevicesWatcher.stopWatch();
    }

    public AdbCommandBuilder getCommandBuilder() {
        return commandBuilder;
    }

    public String getAdbPath() {
        return adbPath;
    }

    public void setCurrentDevice(int index) {
        currentDeviceIndex = index;
    }

    public AdbDevice getCurrentDevice() {
        return attachedDevices.get(currentDeviceIndex);
    }

    public void setOnAttachedDevicesChangedListener(OnAttachedDevicesChangedListener onAttachedDevicesChangedListener) {
        this.onAttachedDevicesChangedListener = onAttachedDevicesChangedListener;
    }

    // 当设备列表发生变更的时候，会回调此方法
    public void onDevicesChanged(List<AdbDevice> devices) {
        currentDeviceIndex = -1;
        attachedDevices = devices;
        if (onAttachedDevicesChangedListener != null) {
            onAttachedDevicesChangedListener.onAttachedDevicesChanged();
        }
    }

    public List<AdbDevice> getAttachedDevices() {
        return Collections.unmodifiableList(attachedDevices);
    }

    public BufferedImage retrieveScreenShot() throws IOException {
        if (currentDeviceIndex == -1) {
            return null;
        }
        byte[] data = CommandExec.exec(commandBuilder.buildScreencapCommand());
        if (data == null) {
            return null;
        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        return ImageIO.read(inputStream);

    }

    public void performClick(double x, double y) {
        if (currentDeviceIndex == -1)
            return;
        executeCommand(commandBuilder.buildTapCommand(x, y));
    }

    public void performSwipe(double x1, double y1, double x2, double y2, long duration) {
        if (currentDeviceIndex == -1) {
            return;
        }
        executeCommand(commandBuilder.buildSwipeCommand(x1, y1, x2, y2, duration));
    }
    /**
     * 设备列表变更监听
     */
    public interface OnAttachedDevicesChangedListener {

        /**
         * 设备变更回调
         */
        void onAttachedDevicesChanged();
    }
}
