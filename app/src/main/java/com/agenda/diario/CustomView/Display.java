package com.agenda.diario.CustomView;

        import android.annotation.SuppressLint;
        import android.content.Context;
        import android.graphics.Point;
        import android.graphics.Rect;
        import android.util.Log;
        import android.view.View;
        import android.view.WindowManager;

public class Display {

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static Point getUsableSize(Context mContext) {
        Point displaySize = new Point();
        try {
            WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            if (manager != null) {
                android.view.Display display = manager.getDefaultDisplay();
                if (display != null) {
                    if (android.os.Build.VERSION.SDK_INT < 13) {
                        displaySize.set(display.getWidth(), display.getHeight());
                    } else {
                        display.getSize(displaySize);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("checkDisplaySize", "Error checking display sizes", e);
        }
        return displaySize;
    }

    public static Point getVisibleSize(View view) {
        Point displaySize = new Point();
        Rect r = new Rect();
        view.getWindowVisibleDisplayFrame(r);
        displaySize.x = r.right - r.left;
        displaySize.y = r.bottom - r.top;
        return displaySize;
    }

    public static Point getFullSize(View view) {
        Point displaySize = new Point();
        displaySize.x = view.getRootView().getWidth();
        displaySize.y = view.getRootView().getHeight();
        return displaySize;
    }

    public static int getStatusBarHeight(Context mContext) {
        int result = 0;
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getNavigationBarHeightStandard(Context mContext) {
        int resourceId = mContext.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }


    public static int getNavigationBarHeight(View view) {
        return (getFullSize(view).y - getUsableSize(view.getContext()).y);
    }
}