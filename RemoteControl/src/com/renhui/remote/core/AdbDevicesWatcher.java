package com.renhui.remote.core;

import com.renhui.remote.utils.Utils;

import javax.swing.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * adb设备监听器
 */
public class AdbDevicesWatcher implements Runnable {

    public AdbHelper adbHelper;

    private Thread watchThread; // 执行设备监听的线程

    private List<AdbDevice> previouslyDevices;  // 缓存的链接的设备列表

    public AdbDevicesWatcher(AdbHelper adbHelper) {
        this.adbHelper = adbHelper;
    }

    void startWatch() {
        watchThread = new Thread(this);
        watchThread.start();
    }

    void stopWatch() {
        watchThread.interrupt();
        watchThread = null;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            List<AdbDevice> devices = new LinkedList<>();
            AdbCommandBuilder commandBuilder = adbHelper.getCommandBuilder();
            byte[] data = CommandExec.exec(commandBuilder.buildDevicesCommand());
            if (data != null) {
                String[] lines = new String(data).split("\\n");
                for(String line : lines) {
                    if (!line.startsWith("*") && !line.startsWith("List") && !Utils.isEmpty(line)) {
                        System.out.println("检测到新设备链接, Device Info：" + line);
                        devices.add(new AdbDevice(line));
                    }
                }
            }
            Collections.sort(devices);
            if (!Utils.equalsOrder(devices, previouslyDevices)) {
                previouslyDevices = devices;
                // 因为设备变更，可能需要修改界面，通过此方式进行事件分发
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        adbHelper.onDevicesChanged(devices);
                    }
                });
            }

            // 每隔五秒刷新一次
            Utils.sleep(5000L);
        }
    }

}
