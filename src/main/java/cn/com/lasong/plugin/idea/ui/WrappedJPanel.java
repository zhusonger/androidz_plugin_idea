package cn.com.lasong.plugin.idea.ui;

import javax.swing.*;
import java.awt.*;

/**
 * 包装的JPanel
 * @param <T>
 */
public class WrappedJPanel<T> extends JPanel {
    private T data;

    public WrappedJPanel(T data) {
        super();
        this.data = data;
    }

    public WrappedJPanel(LayoutManager layout, boolean isDoubleBuffered, T data) {
        super(layout, isDoubleBuffered);
        this.data = data;
    }

    public WrappedJPanel(LayoutManager layout, T data) {
        super(layout);
        this.data = data;
    }

    public WrappedJPanel(boolean isDoubleBuffered, T data) {
        super(isDoubleBuffered);
        this.data = data;
    }

    public T getData() {
        return data;
    }
}
