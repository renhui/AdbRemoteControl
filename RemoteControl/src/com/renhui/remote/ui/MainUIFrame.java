package com.renhui.remote.ui;

import com.renhui.remote.core.AdbDevice;
import com.renhui.remote.core.AdbHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;

public class MainUIFrame extends JFrame implements AdbHelper.OnAttachedDevicesChangedListener {

    private int width = 200;

    private int height = 500;

    private JRadioButtonMenu devicesMenu;

    private DeviceScreenComponent screenComponent;

    private AdbHelper adbHelper;

    public MainUIFrame(AdbHelper adbHelper) throws HeadlessException {
        this.adbHelper = adbHelper;

        setSize(width, height);
        // 设置控件显示
        setVisible(true);

        // 不允许用户自行调整窗口大小
        setResizable(false);

        // 用户单击窗口的关闭按钮时程序执行的操作 - 关闭并退出
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // 置窗口相对于指定组件的位置。 如果组件当前未显示或者 c 为 null,则此窗口将置于屏幕的中央。
        setLocationRelativeTo(null);

        initMenu();

        // 创建屏幕展示组件。
        screenComponent = new DeviceScreenComponent(adbHelper);
        // 获取窗口面板，并添加控件。
        getContentPane().add(screenComponent);

        // 设置设备监听
        adbHelper.setOnAttachedDevicesChangedListener(this);

        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                adbHelper.release();
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
    }

    /**
     * 创建菜单列表（设备管理菜单）
     */
    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(buildDevicesMenu());
        setJMenuBar(menuBar);
    }


    @Override
    public void onAttachedDevicesChanged() {
        MenuSelectionManager.defaultManager().clearSelectedPath();
        ButtonGroup buttonGroup = new ButtonGroup();
        devicesMenu.removeAll();
        JMenuItem noneMenuItem = new JRadioButtonMenuItem("None");
        buttonGroup.add(noneMenuItem);
        devicesMenu.add(noneMenuItem);
        noneMenuItem.addActionListener(e -> setCurrentDevice(-1));
        List<AdbDevice> devices = adbHelper.getAttachedDevices();
        for (int i = 0; i < devices.size(); i++) {
            AdbDevice device = devices.get(i);
            JMenuItem menuItem = new JRadioButtonMenuItem(device.getModel());
            menuItem.setEnabled(device.isAvailable());
            buttonGroup.add(menuItem);
            devicesMenu.add(menuItem);
            int deviceIndex = i;
            menuItem.addActionListener(e -> setCurrentDevice(deviceIndex));
        }
        setCurrentDevice(-1);
    }

    private void setCurrentDevice(int deviceIndex) {
        if (deviceIndex == -1) {
            if (screenComponent.isRendering())
                screenComponent.stopUpdate();
            adbHelper.setCurrentDevice(-1);
            devicesMenu.setSelectedIndex(0);
        } else {
            adbHelper.setCurrentDevice(deviceIndex);
            AdbDevice device = adbHelper.getCurrentDevice();
            devicesMenu.setSelectedIndex(deviceIndex + 1);
            this.screenComponent.startUpdate();
        }
    }

    private JMenu buildDevicesMenu() {
        devicesMenu = new JRadioButtonMenu("Devices");
        return devicesMenu;
    }
}
