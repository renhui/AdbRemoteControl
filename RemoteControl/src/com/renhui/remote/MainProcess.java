package com.renhui.remote;

import java.awt.*;

public class MainProcess {

    public static void main(String[] args) {
        // 执行Adb远程控制设备的任务
        EventQueue.invokeLater(new AdbRemoteScreen());
    }

}
