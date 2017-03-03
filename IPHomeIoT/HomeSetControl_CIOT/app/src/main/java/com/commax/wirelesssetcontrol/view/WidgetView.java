package com.commax.wirelesssetcontrol.view;

import android.appwidget.AppWidgetHostView;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.RemoteViews;

import java.io.IOException;

public class WidgetView extends AppWidgetHostView {
    public WidgetView(Context context, int widgetId) {
        super(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }
}