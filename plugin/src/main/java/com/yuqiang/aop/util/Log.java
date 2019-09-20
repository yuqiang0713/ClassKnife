package com.yuqiang.aop.util;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author yuqiang
 */
public class Log {

    public static final int LOG_LEVEL_VERBOSE = 0;
    public static final int LOG_LEVEL_DEBUG = 1;
    public static final int LOG_LEVEL_INFO = 2;
    public static final int LOG_LEVEL_WARN = 3;
    public static final int LOG_LEVEL_ERROR = 4;

    private static LogImp debugLog = new LogImp() {

        private int level = LOG_LEVEL_INFO;

        @Override
        public void v(final String tag, final String msg, final Object... obj) {
            if (level == LOG_LEVEL_VERBOSE) {
                String log = obj == null ? msg : String.format(msg, obj);
                System.out.println(String.format("[VERBOSE][%s]%s", tag, log));
            }
        }

        @Override
        public void d(final String tag, final String msg, final Object... obj) {
            if (level <= LOG_LEVEL_DEBUG) {
                String log = obj == null ? msg : String.format(msg, obj);
                System.out.println(String.format("[DEBUG][%s]%s", tag, log));
            }
        }

        @Override
        public void i(final String tag, final String msg, final Object... obj) {
            if (level <= LOG_LEVEL_INFO) {
                String log = obj == null ? msg : String.format(msg, obj);
                System.out.println(String.format("\033[36;1m" + "[INFO][%s]%s" + "\033[0m", tag, log));
            }
        }

        @Override
        public void w(final String tag, final String msg, final Object... obj) {
            if (level <= LOG_LEVEL_WARN) {
                String log = obj == null ? msg : String.format(msg, obj);
                System.out.println(String.format("[WARN][%s]%s", tag, log));
            }
        }

        @Override
        public void e(final String tag, final String msg, final Object... obj) {
            if (level <= LOG_LEVEL_ERROR) {
                String log = obj == null ? msg : String.format(msg, obj);
                System.out.println(String.format("\033[31;1m" + "[ERROR][%s]%s" + " \033[0m", tag, log));
            }
        }

        @Override
        public void printErrStackTrace(String tag, Throwable tr, String format, Object... obj) {
            String log = obj == null ? format : String.format(format, obj);
            if (log == null) {
                log = "";
            }
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            tr.printStackTrace(pw);
            log += "  " + sw.toString();
            System.out.println(String.format("[ERROR][%s]%s", tag, log));
        }

        @Override
        public void setLogLevel(int logLevel) {
            this.level = logLevel;
        }
    };

    private static LogImp logImp = debugLog;
    private static int level = LOG_LEVEL_INFO;
    private static File logFile;
    private static FileWriter fileWriter = null;

    private Log() {
    }

    public static void setLogFile(File file) {
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            logFile = file;
            fileWriter = new FileWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void writeMsgToFile(File file, String msg) {
        try {
            fileWriter.write(msg);
            fileWriter.write("\n");
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }

    public static void closeFileWriter() {
        if (fileWriter !=null) {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileWriter != null) {
                    FileUtil.closeQuietly(fileWriter);
                }
            }
        }
    }

    public static void setLogImp(LogImp imp) {
        logImp = imp;
    }

    public static LogImp getImpl() {
        return logImp;
    }

    public static void setLogLevel(String logLevel) {
        if (logLevel.equals("v")) {
            level = LOG_LEVEL_VERBOSE;
        } else if (logLevel.equals("d")) {
            level = LOG_LEVEL_DEBUG;
        } else if (logLevel.equals("i")) {
            level = LOG_LEVEL_INFO;
        } else if (logLevel.equals("w")) {
            level = LOG_LEVEL_WARN;
        } else if (logLevel.equals("e")) {
            level = LOG_LEVEL_ERROR;
        }
        getImpl().setLogLevel(level);
    }

    public static void v(final String tag, final String msg, final Object... obj) {
        if (logImp != null) {
            logImp.v(tag, msg, obj);
        }
        writeMsgToFile(logFile, msg);
    }

    public static void e(final String tag, final String msg, final Object... obj) {
        if (logImp != null) {
            logImp.e(tag, msg, obj);
        }
        writeMsgToFile(logFile, msg);
    }

    public static void w(final String tag, final String msg, final Object... obj) {
        if (logImp != null) {
            logImp.w(tag, msg, obj);
        }
        writeMsgToFile(logFile, msg);
    }

    public static void i(final String tag, final String msg, final Object... obj) {
        if (logImp != null) {
            logImp.i(tag, msg, obj);
        }
        writeMsgToFile(logFile, msg);
    }

    public static void d(final String tag, final String msg, final Object... obj) {
        if (logImp != null) {
            logImp.d(tag, msg, obj);
        }
        writeMsgToFile(logFile, msg);
    }

    public static void printErrStackTrace(String tag, Throwable tr, final String format, final Object... obj) {
        if (logImp != null) {
            logImp.printErrStackTrace(tag, tr, format, obj);
        }
    }

    public interface LogImp {

        void v(final String tag, final String msg, final Object... obj);

        void i(final String tag, final String msg, final Object... obj);

        void w(final String tag, final String msg, final Object... obj);

        void d(final String tag, final String msg, final Object... obj);

        void e(final String tag, final String msg, final Object... obj);

        void printErrStackTrace(String tag, Throwable tr, final String format, final Object... obj);

        void setLogLevel(int logLevel);

    }
}
