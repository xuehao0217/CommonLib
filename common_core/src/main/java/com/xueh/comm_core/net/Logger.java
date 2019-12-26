package com.xueh.comm_core.net;

import android.util.Log;



public class Logger {

    private static final String TAG = "LOGGER_LOG";


    private static final int NONE = 0;

    private static final int VERBOSE = Log.VERBOSE;

    private static final int DEBUG = Log.DEBUG;

    private static final int INFO = Log.INFO;

    private static final int WARN = Log.WARN;

    private static final int ERROR = Log.ERROR;

    private static final int ALL = 9;


    /**
     * 是否打印LOG,默认DEBUG时打印LOG 正式时不打印LOG
     */
    private static int LEVEL = NONE;

    //暂时都打印
    static {
//        if (DevelopHelper.isDebug()) {
            LEVEL = ALL;
//        } else {
//            LEVEL = ALL;
//        }
    }


    public static boolean getIsLOG() {
        return (LEVEL > 1);
    }

    public static void v(String msg) {
        v(TAG, msg);
    }

    public static void v(String tag, String msg) {
        if ((LEVEL >= VERBOSE)) println(VERBOSE, tag, msg);
    }

    public static void d(String msg) {
        d(TAG, msg);
    }

    public static void d(String tag, String msg) {
        if (LEVEL >= DEBUG) println(DEBUG, tag, msg);
    }

    public static void i(String msg) {
        i(TAG, msg);
    }

    public static void i(String tag, String msg) {
        if (LEVEL >= INFO) println(INFO, tag, msg);
    }

    public static void w(String msg) {
        w(TAG, msg);
    }

    public static void w(String tag, String msg) {
        if (LEVEL >= WARN) println(WARN, tag, msg);
    }

    public static void e(String msg) {
        e(TAG, msg);
    }

    public static void e(String tag, String msg) {
        if (LEVEL >= ERROR) println(ERROR, tag, msg);
    }

    //每行最多打印字符数量
    private static final int CHUNK_SIZE = 4000;

    private static void println(int priority, String tag, String message) {
        if (message == null) {
            message = "log message is null";
        }

        byte[] bytes = message.getBytes();
        int length = bytes.length;

        if (length <= CHUNK_SIZE) {
            Log.println(priority, tag, message);
        } else {
            for (int i = 0; i < length; i += CHUNK_SIZE) {
                int count = Math.min(length - i, CHUNK_SIZE);
                Log.println(priority, tag, new String(bytes, i, count));
            }
        }
    }




    public static class X{

        public static void v(String msg) {
            v(TAG, msg);
        }

        public static void v(String tag, String msg) {
            if ((LEVEL >= VERBOSE)) println(VERBOSE, tag, msg);
        }

        public static void d(String msg) {
            d(TAG, msg);
        }

        public static void d(String tag, String msg) {
            if (LEVEL >= DEBUG) println(DEBUG, tag, msg);
        }

        public static void i(String msg) {
            i(TAG, msg);
        }

        public static void i(String tag, String msg) {
            if (LEVEL >= INFO) println(INFO, tag, msg);
        }

        public static void w(String msg) {
            w(TAG, msg);
        }

        public static void w(String tag, String msg) {
            if (LEVEL >= WARN) println(WARN, tag, msg);
        }

        public static void e(String msg) {
            e(TAG, msg);
        }

        public static void e(String tag, String msg) {
            if (LEVEL >= ERROR) println(ERROR, tag, msg);
        }


        //每行最多打印字符数量
        private static final int CHUNK_SIZE = 4000;

        private static void println(int priority, String tag, String message) {
            if (message == null) {
                message = "log message is null";
            }

            byte[] bytes = message.getBytes();
            int length = bytes.length;

            if (length <= CHUNK_SIZE) {
                xLog(priority,tag,message);
            } else {
                for (int i = 0; i < length; i += CHUNK_SIZE) {
                    int count = Math.min(length - i, CHUNK_SIZE);
                    xLog(priority, tag, new String(bytes, i, count));
                }
            }
        }

        /**
         * 使用xLog
         *
         * */
        private static void xLog(int priority, String tag, String message){
            switch (priority){
//                case VERBOSE:
//                    com.tencent.mars.xlog.Log.v(tag,message);
//                    break;
//                case DEBUG:
//                    com.tencent.mars.xlog.Log.d(tag,message);
//                    break;
//                case INFO:
//                    com.tencent.mars.xlog.Log.i(tag,message);
//                    break;
//                case WARN:
//                    com.tencent.mars.xlog.Log.w(tag,message);
//                    break;
//                case ERROR:
//                    com.tencent.mars.xlog.Log.e(tag,message);
//                    break;
//                default:
//                    com.tencent.mars.xlog.Log.i(tag,message);
//                    break;
            }

        }

    }


}
