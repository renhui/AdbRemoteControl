package com.renhui.remote.core;

import com.renhui.remote.utils.Utils;

/**
 * Android 设备信息类
 */
public class AdbDevice implements Comparable<AdbDevice> {

    /**
     * 序列号
     */
    String serial;

    /**
     * 产品名
     */
    String product;

    /**
     * 设备名
     */
    String model;

    /**
     * 产品名
     */
    String device;

    /**
     * 是否可用
     */
    boolean available;

    AdbDevice(String line) {
        String[] array = line.split(" ");
        this.serial = array[0];
        for (int i = 1; i < array.length; i++) {
            if (!Utils.isEmpty(array[i])) {
                this.available = "device".equals(array[i]);
                break;
            }
        }
        for (String s : array) {
            if (s.startsWith("product:")) {
                this.product = s.substring("product:".length());
            } else if (s.startsWith("model:")) {
                this.model = s.substring("model:".length());
            } else if (s.startsWith("device:")) {
                this.device = s.substring("device:".length());
            }
        }
        if (!this.available) {
            this.model = this.serial.substring(0, Math.min(this.serial.length(), 4)) + "... (UNAVAILABLE)";
        }
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean equals(Object object) {
        if (object instanceof AdbDevice)
            return (this.serial.equals(((AdbDevice)object).serial) && this.available == ((AdbDevice)object).available);
        return false;
    }

    @Override
    public int compareTo(AdbDevice device) {
        return this.serial.compareTo(device.serial);
    }
}
