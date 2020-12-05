package com.renhui.remote.ui;

import com.renhui.remote.core.AdbHelper;
import com.renhui.remote.utils.Utils;

import javax.imageio.IIOException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 设备屏幕展示组件
 */
public class DeviceScreenComponent extends JComponent implements MouseListener {

    // 默认展示的窗口尺寸
    private static final Dimension DEFAULT_SCREEN_SIZE = new Dimension(540, 1080);

    private final AdbHelper adbHelper;

    private volatile BufferedImage bufferedImage;

    private volatile Thread updateThread; // 渲染屏幕线程

    public DeviceScreenComponent(AdbHelper adbHelper) {
        this.adbHelper = adbHelper;
        setPreferredSize(new Dimension(DEFAULT_SCREEN_SIZE.width, DEFAULT_SCREEN_SIZE.height));
        addMouseListener(this);

        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                super.componentMoved(e);
            }

            @Override
            public void componentShown(ComponentEvent e) {
                super.componentShown(e);
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                super.componentHidden(e);
            }
        });
    }

    boolean isRendering() {
        return (updateThread != null && !updateThread.isInterrupted());
    }

    public void startUpdate() {
        if (isRendering())
            throw new IllegalStateException("Already started");
        this.updateThread = new UpdateThread();
        this.updateThread.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bufferedImage != null) {
            g.drawImage(bufferedImage, 0, 0, getWidth(), getHeight(), null);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            String text = "Nothing to show";
            g.setColor(Color.WHITE);
            int textWidth = g.getFontMetrics().stringWidth(text);
            g.drawString(text, (getWidth() - textWidth) / 2, getHeight() / 2);
        }
    }

    public void stopUpdate() {
        if (!isRendering())
            throw new IllegalStateException("Already stoped");
        updateThread.interrupt();
        updateThread = null;
        bufferedImage = null;
        repaint();
    }

    private double computeTransformValue() {
        return ((bufferedImage == null) ? DEFAULT_SCREEN_SIZE.getWidth() : bufferedImage.getWidth()) / getWidth();
    }

    /****************************** 鼠标键盘事件处理 *******************************/

    private int downX;

    private int downY;

    private long swipeStartTime;


    @Override
    public void mouseClicked(MouseEvent e) {
        double transform = computeTransformValue();
        adbHelper.performClick(e.getX() * transform, e.getY() * transform);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        downX = e.getX();
        downY = e.getY();
        swipeStartTime = System.currentTimeMillis();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int upX = e.getX(), upY = e.getY();
        int dx = Math.abs(downX - upX);
        int dy = Math.abs(downY - upY);
        if (dx > 5 && dy > 5) {
            double transform = computeTransformValue();
            long duration = System.currentTimeMillis() - swipeStartTime;
            adbHelper.performSwipe(downX * transform, downY * transform, upX * transform, upY * transform, duration);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    private class UpdateThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!Thread.interrupted()) {
                if (Thread.interrupted())
                    break;
                try {
                    bufferedImage = adbHelper.retrieveScreenShot();
                    if (Thread.interrupted())
                        bufferedImage = null;
                    if (bufferedImage == null) {
                        abort();
                        continue;
                    }
                    repaintPanel();
                } catch (IIOException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    abort();
                }
            }
        }

        private void abort() {
            interrupt();
            updateThread = null;
            bufferedImage = null;
            Utils.executeOnUiThread(DeviceScreenComponent.this::repaint);
        }
    }

    private void repaintPanel() {
        Utils.executeOnUiThread(DeviceScreenComponent.this::repaint);
    }

}
