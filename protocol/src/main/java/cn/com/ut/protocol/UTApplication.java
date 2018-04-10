package cn.com.ut.protocol;

import android.content.Context;

public class UTApplication {
    private static Context instance;

    /**
     * 获得当前app运行的AppContext
     *
     * @return
     */
    public static Context getInstance() {
        return instance;
    }

    public static void setInstance(Context context) {
        if (instance == null) {
            instance = context;
        }
    }
}