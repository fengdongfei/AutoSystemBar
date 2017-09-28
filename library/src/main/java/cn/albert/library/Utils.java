package cn.albert.library;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewParent;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by albert on 2017/9/26.
 *
 */

class Utils {

    private static final String TAG = "Utils";

    private static boolean sIsInitializeStatusBar;
    static int sStatusBarHeight;
    private static boolean sIsInitializeNavigationBar;
    static int sNavigationBarHeight;

    static void initializeStatusBar(View view) {
        if (sIsInitializeStatusBar) {
            return;
        }
        sIsInitializeStatusBar = true;
        Context context = view.getContext();

        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Field field = clazz.getField("status_bar_height");
            int x = Integer.parseInt(field.get(clazz.newInstance()).toString());
            sStatusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Throwable e) {
            Rect outRect = new Rect();
            View decorView = getParentView(view);
            decorView.getWindowVisibleDisplayFrame(outRect);
            sStatusBarHeight = outRect.top;
        }
    }

    private static View getParentView(View view) {
        ViewParent viewParent = view.getParent();
        if (viewParent != null) {
            return getParentView((View) viewParent);
        }
        return view;
    }

    static void initializeNavigationBar(View view) {
        if (sIsInitializeNavigationBar) {
            return;
        }

        sIsInitializeNavigationBar = true;
        Context context = view.getContext();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        try {
            Class<?> displayClass = display.getClass();
            Method method = displayClass.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, displayMetrics);
            sNavigationBarHeight = displayMetrics.heightPixels - getDisplayHeight(display);
        } catch (Exception ignored) {
        }
    }

    private static int getDisplayHeight(Display display) {
        Point point = new Point();
        display.getSize(point);
        return point.y;
    }


    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_EMUI_VERSION_NAME = "ro.build.version.emui";

    private static String getSystemProperty(String key, String defaultValue) {
        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method get = clz.getMethod("get", String.class, String.class);
            return (String) get.invoke(clz, key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }


    public static boolean isEMUI3_1() {
        String property = getSystemProperty(KEY_EMUI_VERSION_NAME, "");
        if ("EmotionUI 3".equals(property) || property.contains("EmotionUI_3.1")) {
            return true;
        }
        return false;
    }

    public static boolean isAfterMIUI_7_7_13(){
        String property = getSystemProperty(KEY_MIUI_VERSION_NAME, "");
        Log.d(TAG, "isAfterMIUI_7_7_13: " + property);
        // TODO: 2017/9/27
        return false;
    }
}
