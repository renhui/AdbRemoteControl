package com.renhui.remote.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 命令行执行类
 */
public class CommandExec {

    static void execAsync(List<String> command, CallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] data = exec(command);
                if (callBack != null) {
                    callBack.callBack(data);
                }
            }
        }).start();
    }

    static byte[] exec(List<String> command) {
        /** 每个 ProcessBuilder 实例管理一个进程属性集。它的start() 方法利用这些属性创建一个新的 Process 实例。*/
        ProcessBuilder builder = new ProcessBuilder(command);
        /**
         * redirectErrorStream 属性默认值为false，意思是子进程的标准输出和错误输出被发送给两个独立的流，
         * 这些流可以通过 Process.getInputStream() 和 Process.getErrorStream() 方法来访问。
         * 如果将值设置为 true，标准错误将与标准输出合并。这使得关联错误消息和相应的输出变得更容易。
         * 在此情况下，合并的数据可从 Process.getInputStream() 返回的流读取，而从 Process.getErrorStream() 返回的流读取将直接到达文件尾。
         */
        builder.redirectErrorStream(true);

        try {
            /** Process类是一个抽象类（所有的方法均是抽象的），封装了一个进程（即一个执行程序）。
             * Process 类提供了执行从进程输入、执行输出到进程、等待进程完成、检查进程的退出状态以及销毁（杀掉）进程的方法。
             */
            Process process = builder.start();

            InputStream inputStream = process.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] dataChunk = new byte[16384]; // 2的14次方
            int nRead;
            while ((nRead = inputStream.read(dataChunk, 0, dataChunk.length)) != -1)
                buffer.write(dataChunk, 0, nRead);
            buffer.flush();
            byte[] data = buffer.toByteArray();
            buffer.close();
            inputStream.close();
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    interface CallBack {
        void callBack(byte[] paramsOfByte);
    }
}
