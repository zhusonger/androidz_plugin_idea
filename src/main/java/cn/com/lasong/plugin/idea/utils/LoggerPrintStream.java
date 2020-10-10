package cn.com.lasong.plugin.idea.utils;

import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LoggerPrintStream extends PrintStream {

    public static final LoggerPrintStream err = new LoggerPrintStream(System.err);
    public static final LoggerPrintStream out = new LoggerPrintStream(System.out);

    private static final Map<ILoggerHandler, Object> sHandlers = new HashMap<>();

    public static void registerHandler(ILoggerHandler handler) {
        sHandlers.put(handler, null);
    }

    public static void unregisterHandler(ILoggerHandler handler) {
        sHandlers.remove(handler);
    }

    private LoggerPrintStream(@NotNull OutputStream out) {
        super(out);
    }

    @Override
    public void write(@NotNull byte[] buf, int off, int len) {
        super.write(buf, off, len);
        String string = new String(buf, off, len, StandardCharsets.UTF_8);
        for (Map.Entry<ILoggerHandler, Object> entry : sHandlers.entrySet()) {
            ILoggerHandler handler = entry.getKey();
            if (null != handler) {
                handler.onMessage(err == this, string);
            }
        }
    }
}