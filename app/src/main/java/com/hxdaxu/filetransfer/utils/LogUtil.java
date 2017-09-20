package com.hxdaxu.filetransfer.utils;

import android.os.Environment;
import android.util.Log;


import com.hxdaxu.filetransfer.env.FileTransferApp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 日志工具类 该类用于控制logcat控制台输出、一般方式文件输出开关， 以及定制独立文件输出
 * 
 * <pre>
 * 独立的日志文件的组织方式：
 * 		此时 FILE_DIR = "/log/backup_restore/"  FILE_NAME = "backup_restore" FILE_SUFFIX = ".log"
 * 
 *  	/log/backup_restore/backup_restore_error.log                    (用于记录ERROR以上级别日志)  
 *  	/log/backup_restore/2012-08-26/backup_restore_2012-08-26.log    (用于记录全级别日志)  
 *  	/log/backup_restore/2012-08-28/backup_restore_2012-08-28.log     (用于记录全级别日志)
 * 		
 * 		标记##，只会存在与产品模式的Log 
 * 		标记**，该集合大小受控制
 * </pre>
 * 
 * 
 * @author Administrator
 * 
 */
public class LogUtil {

    /**
     * 开关用于控制是否启用Log（包括logcat控制台输出、一般方式文件输出）
     */
    public final static boolean logEnable = true;
    /**
     * 开关用于控制是否启用Log（包括独立文件输出），需依赖logEnable置为true，此项配置才能起作用
     */
    private final static boolean logToFileEnable = false;

    /**
     * 当前Log的输出样式
     */
    private static Style style = Style.Style1;

    /**
     * 产品模式，Log的最大集合数量，包括数组,Set,List,Map
     */
    private static final int MAX_COLLECTION_NUM_FOR_P = 10;
    /**
     * 非产品模式，Log的最大集合数量，包括数组,Set,List,Map
     */
    private static final int MAX_COLLECTION_NUM_FOR_UP = 200;
    /**
     * 应用默认TAG
     */
    public static final String APP_TAG = "Amigo_DataGhost";
    /**
     * 日志启用级别，闭区间，大于等于该级别的日志会输出
     */
    private static int logLevel = Log.DEBUG;

    /**
     * 是否为产品模式：产品模式将打印更少的log(集合，数组等 将不输出)
     */
    private static boolean productMode = true;

    private static int collectionNum = MAX_COLLECTION_NUM_FOR_P;

    static {
        try {
            // 检查SD卡上是否创建了："account1234567890test"和类似“gn.com.android.synchronizer.log”文件，如果存在则认为使用非产品模式
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String sdCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                if (new File(sdCardPath + "/" + "account1234567890test").exists()) {

                    productMode = false;
                } else if (new File(sdCardPath + "/" + FileTransferApp.getContext().getPackageName() + ".log")
                        .exists()) {

                    productMode = false;
                } else {

                    productMode = true;
                }
            }
        } catch (Exception e) {
            productMode = true;
        }

        if (productMode) {
            collectionNum = MAX_COLLECTION_NUM_FOR_P;
        } else {
            collectionNum = MAX_COLLECTION_NUM_FOR_UP;
        }
    }

    /**
     * 应用日志文件存放目录
     */
    private final static String FILE_DIR = "/log/backup_restore/";
    /**
     * 应用日志文件名称
     */
    private final static String FILE_NAME = "backup_restore";
    /**
     * 应用日志文件后缀
     */
    private final static String FILE_SUFFIX = ".log";
    /**
     * 应用日志ERROR文件名称
     */
    private final static String ERROR_FILE_NAME = FILE_NAME + "_error";
    /**
     * 该格式用于规范应用日志文件名称中的时间信息
     */
    private final static String FILE_DATE_FORMAT = "yyyy-MM-dd";
    /**
     * 该格式用于规范应用日志文件内具体日志条目时间信息
     */
    private final static String LOG_TIME_FORMAT = "MM-dd HH:mm:ss.SSS";

    /**
     * Log输出的样式
     */
    private enum Style {
        /**
         * 仅输出相关传入字符串信息，而不输出额外信息例如：(Error 08-28 13:56:02.724)[Thread-main: AutoBackUpActivity.java:319
         * onDestroy()]
         */
        Simple,
        /**
         * 输出额外信息，额外信息分行显示
         */
        Style1,
        /**
         * 输出额外信息，额外信息一行显示
         */
        Style2
    }

    public static void v(Object... objs) {
        if (logEnable && logLevel <= Log.VERBOSE) {
            logDetail(buildTag(objs), Log.VERBOSE, objs);
        }
    }


    public static void d(Object... objs) {
        if (logEnable && logLevel <= Log.DEBUG) {
            logDetail(buildTag(objs), Log.DEBUG, objs);
        }
    }


    public static void i(Object... objs) {
        if (logEnable && logLevel <= Log.INFO) {
            logDetail(buildTag(objs), Log.INFO, objs);
        }
    }


    public static void w(Object... objs) {
        if (logEnable && logLevel <= Log.WARN) {
            logDetail(buildTag(objs), Log.WARN, objs);
        }
    }


    public static void e(Object... objs) {
        if (logEnable && logLevel <= Log.ERROR) {
            logDetail(buildTag(objs), Log.ERROR, objs);
        }
    }

    public static void e(Throwable ex) {
        e("", ex);
    }

    public static void e(String summary, Throwable ex) {
        if (logEnable && logLevel <= Log.ERROR) {
            logDetail(buildTag(), Log.ERROR, summary + "\n" + Log.getStackTraceString(ex));
        }
    }

    public static void logStackTrace() {
        e("这是一个打印当前堆栈的调试工具，并非发生了异常", new RuntimeException());
    }

    public static void log(int level, String tag, Object... objs) {
        if (logEnable && logLevel <= level) {
            logDetail(tag, level, objs);
        }
    }

    private static void logDetail(String tag, int level, Object... objs) {
        try {
            String messge = buildMessge(level, objs);
            switch (level) {
                case Log.VERBOSE:
                    Log.v(tag, messge);
                    break;
                case Log.DEBUG:
                    Log.d(tag, messge);
                    break;
                case Log.INFO:
                    Log.i(tag, messge);
                    break;
                case Log.WARN:
                    Log.w(tag, messge);
                    break;
                case Log.ERROR:
                    Log.e(tag, messge);
                    break;
                default:
                    Log.e(tag, "不支持的Log级别" + messge);
            }
            // 判断独立文件输出是否开启
            if (logToFileEnable) {
                // 全级别存储到一般的日志文件
                saveToSDCard(messge, LogFileType.Ordinary);
                if (level >= Log.ERROR) {
                    // Error以上级别日志存储到Error日志文件
                    saveToSDCard(messge, LogFileType.ErrorFile);
                }
            }
        } catch (Throwable e) {
            LogUtil.e(e);
        }
    }

    private static String buildTag(Object... objs) {
        try {
            if (objs == null) {

                return APP_TAG;
            } else if (objs.length <= 1) {

                return APP_TAG;
            } else {
                // 开发模式的log
                if ("##".equals(objs[0])) {

                    if (objs.length >= 2) {
                        if (isTag(objs[1])) {
                            String tag = APP_TAG + "_" + objs[1];
                            objs[1] = null;
                            return tag;
                        }
                    } else {
                        return APP_TAG;
                    }
                } else {

                    if (objs.length >= 1) {
                        if (isTag(objs[0])) {
                            String tag = APP_TAG + "_" + objs[0];
                            objs[0] = null;
                            return tag;
                        } else {
                            return APP_TAG;
                        }
                    } else {
                        return APP_TAG;
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.e(e);
        }
        return APP_TAG;
    }

    /**
     * 构造日志信息
     */
    private static String buildMessge(int level, Object... objs) {
        String message = null;
        if (objs.length == 1) {
            message = objs[0] == null ? "" : getMessage(objs[0]);
        } else if (objs.length == 2) {
            message = (objs[0] == null ? "" : getMessage(objs[0]))
                    + (objs[1] == null ? "" : getMessage(objs[1]));
        } else if (objs.length == 3) {
            message = (objs[0] == null ? "" : getMessage(objs[0]))
                    + (objs[1] == null ? "" : getMessage(objs[1]))
                    + (objs[2] == null ? "" : getMessage(objs[2]));
        } else {
            StringBuilder sb = new StringBuilder();
            for (Object obj : objs) {
                if (obj != null) {
                    sb.append(getMessage(obj));
                }
            }
            message = sb.toString();
        }

        // 是否需要显示详细信息
        if (style == Style.Simple) {
            return message;
        } else {
            String functionInfo = getMethodInfo();
            if (functionInfo == null) {
                functionInfo = "";
            }
            // 构造详细信息（日志级别、时间、方法及线程信息）
            String retString = "(" + getLevelInfo(level) + " " + getTimestamp() + ")" + functionInfo;
            if (style == Style.Style1) {
                return retString + "\n" + message;
            } else if (style == Style.Style2) {
                return message + "      " + retString;
            }
            return message;
        }
    }

    private static String getMessage(Object obj) {
        if (obj == null) {

            return "";
        } else if (obj instanceof Iterable<?>) {

            StringBuilder sb = new StringBuilder();

            Iterable<?> iterable = (Iterable<?>) obj;
            Iterator<?> it = iterable.iterator();

            int i = 0;
            while (it.hasNext()) {
                if (i < collectionNum) {
                    i++;
                    sb.append(it.next());
                } else {
                    sb.append(" ... ");
                    break;
                }
            }

            if (i >= collectionNum) {
                return "(仅保留前" + collectionNum + "项！)" + sb.toString();
            } else {
                return "**" + sb.toString();
            }

        } else if (obj instanceof Map<?, ?>) {

            StringBuilder sb = new StringBuilder();

            Map<?, ?> map = (Map<?, ?>) obj;

            int i = 0;

            for (Entry<?, ?> entry : map.entrySet()) {
                if (i < collectionNum) {
                    i++;
                    sb.append("[Key = " + entry.getKey() + " Value = " + entry.getValue() + "]");
                } else {
                    sb.append(" ... ");
                    break;
                }
            }

            if (i >= collectionNum) {
                return "(仅保留" + collectionNum + "项！)" + sb.toString();
            } else {
                return "**" + sb.toString();
            }

        } else {

            return obj.toString();
        }
    }

    /**
     * 判断当前字符串是否是一个Tag
     */
    private static boolean isTag(Object obj) {
        return obj != null && obj.toString().endsWith("_tag");
    }

    /**
     * 从堆栈中取得必要方法信息等
     */
    private static String getMethodInfo() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts == null) {
            return null;
        }
        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {
                continue;
            }
            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }
            if (st.getClassName().equals(LogUtil.class.getName())) {
                continue;
            }
            return "[" + Thread.currentThread().getName() + ": " + st.getFileName() + ":"
                    + st.getLineNumber() + " " + st.getMethodName() + "()]";
        }
        return null;
    }

    /**
     * 取得日志级别对应的显示字符
     */
    private static String getLevelInfo(int level) {
        switch (level) {
            case Log.VERBOSE:
                return "V";
            case Log.DEBUG:
                return "D";
            case Log.INFO:
                return "I";
            case Log.WARN:
                return "W";
            case Log.ERROR:
                return "E";
            default:
                return "不支持的Log级别";
        }
    }

    /**
     * 获取日志具体时间字符串
     */
    private static String getTimestamp() {
        return new SimpleDateFormat(LOG_TIME_FORMAT).format(new Date());
    }

    /**
     * 存储日志到SD卡文件
     */
    private static void saveToSDCard(String content, LogFileType type) throws Exception {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Writer bw = getDestLogWriter(type);
            bw.write("\n");
            bw.write(content);
            bw.flush();
            // 此处考虑性能，并没有关闭IO流
        }
    }

    // 应用日志文件
    private static File targetFile = null;
    private static Writer targetFileWriter = null;
    // 应用日志当文件天时间段
    private static long targetFileBeginTs = -1;
    private static long targetFileEndTs = -1;
    // 应用日志ERROR文件
    private static File targetErrFile = null;
    private static Writer targetErrFileWriter = null;

    /**
     * 日志文件类型
     */
    private enum LogFileType {
        /**
         * 一般日志文件(各级别全纪录)
         */
        Ordinary,
        /**
         * ERROR文件（记录ERROR以上级别）
         */
        ErrorFile
    }

    private static Writer getDestLogWriter(LogFileType type) throws Exception {
        if (type == LogFileType.ErrorFile) {
            if (targetErrFileWriter == null) {
                targetErrFileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                        getDestLogFile(LogFileType.ErrorFile), true), "UTF-8"));
            }
            return targetErrFileWriter;
        } else {
            // 大于targetFileEndTs即视为新的周期（一天），需重新取得Writer
            long currentTs = System.currentTimeMillis();
            if (targetFileWriter == null || currentTs > targetFileEndTs || currentTs < targetFileBeginTs) {
                targetFileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                        getDestLogFile(LogFileType.Ordinary), true), "UTF-8"));
            }
            return targetFileWriter;
        }
    }

    private static File getDestLogFile(LogFileType type) throws Exception {
        File sdCardDir = Environment.getExternalStorageDirectory();
        String filePath = sdCardDir.getPath() + FILE_DIR;
        if (type == LogFileType.ErrorFile) {
            if (targetErrFile == null) {
                // 构造日志ERROR文件路径
                filePath = filePath + ERROR_FILE_NAME + FILE_SUFFIX;
                targetErrFile = new File(filePath);
                if (!targetErrFile.exists()) {
                    boolean ismkdirsOk = targetErrFile.getParentFile().mkdirs();
                    boolean iscreateOk = targetErrFile.createNewFile();
                    if (!ismkdirsOk || !iscreateOk) {
                        Log.i(APP_TAG, "创建失败");
                    }
                }
            }
            return targetErrFile;
        } else {
            long currentTs = System.currentTimeMillis();
            if (targetFile == null || currentTs > targetFileEndTs || currentTs < targetFileBeginTs) {
                // 构造日志文件路径
                String subDir = new SimpleDateFormat(FILE_DATE_FORMAT).format(new Date(currentTs));
                filePath = filePath + subDir + "/" + FILE_NAME + "_" + subDir + FILE_SUFFIX;
                targetFile = new File(filePath);
                if (!targetFile.exists()) {
                    boolean ismkdirsOk = targetFile.getParentFile().mkdirs();
                    boolean iscreateOk = targetFile.createNewFile();
                    if (!ismkdirsOk || !iscreateOk) {
                        Log.i(APP_TAG, "创建失败");
                    }
                    // TODO:此处用简便方式获取后一天起始值，稍后修改
                    targetFileBeginTs = new SimpleDateFormat("yyyy-MM-dd").parse(subDir).getTime();
                    targetFileEndTs = targetFileBeginTs + 24 * 60 * 60 * 1000 - 1;
                }
            }
            return targetFile;
        }
    }
}