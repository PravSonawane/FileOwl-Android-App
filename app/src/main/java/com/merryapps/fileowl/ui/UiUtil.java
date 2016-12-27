package com.merryapps.fileowl.ui;

import android.content.Context;
import android.os.Build;

/**
 * //TODO add description here
 *
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */

public final class UiUtil {

    static int getColor(Context context, int colorResId) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            return context.getResources().getColor(colorResId, null);
        } else {
            return context.getResources().getColor(colorResId);
        }
    }
}
