package org.duder.util;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import org.duder.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class BusyIndicator {

    public static PopupWindow showBusyIndicator(Activity activity, PopupWindow busyIndicator) {
        final View activityView = activity.getWindow().getDecorView();
        if (busyIndicator == null) {
            final int width = activityView.getWidth() - 20;
            final int height = activityView.getHeight() - 20;
            final LayoutInflater inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
            final View popupView = inflater.inflate(R.layout.popup_busy, null);
            busyIndicator = new PopupWindow(popupView, width, height);
            busyIndicator.setElevation(10.0f);
        }
        busyIndicator.showAtLocation(activityView, Gravity.CENTER, 0, 0);
        return busyIndicator;
    }

    public static void hideBusyIndicator(PopupWindow busyIndicator) {
        if (busyIndicator != null && busyIndicator.isShowing()) {
            busyIndicator.dismiss();
        }
    }
}
