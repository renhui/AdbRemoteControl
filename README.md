# AdbRemoteControl
基于Adb技术实现PC远程控制Android设备


准备工作：
1. USB 连接Android设备。
2. Android 设备打开开发者选项和USB调试。


开发逻辑：
1. 使用 adb devices ** 相关命令获取当前连接的设备列表
2. 使用 adb shell screencap ** 相关命令定时获取屏幕的画面并刷新到远程控制页面
3. 使用 adb shell input ** 相关命令实现远程控制


实现方案优点：
1. 免Root  
2. 开发实现相对简单


实现方案缺点：
1. 帧率太低，远程端体验不好
2. 实时性较低，操作感较差

