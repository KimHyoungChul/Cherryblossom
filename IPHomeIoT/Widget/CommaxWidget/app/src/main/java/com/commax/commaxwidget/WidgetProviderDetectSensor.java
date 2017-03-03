package com.commax.commaxwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.commax.commaxwidget.util.Constants;
import com.commax.commaxwidget.wallpad.data.DeviceInfo;
import com.commax.commaxwidget.wallpad.data.SubDevice;


/**
 * Created by OWNER on 2017-02-08.
 */
public class WidgetProviderDetectSensor extends AppWidgetProvider {
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        String action = intent.getAction();
        AppWidgetManager manager = AppWidgetManager.getInstance(context);

        if (Constants.ACTION_UPDATE_DETECT_SENSOR_WIDGET.equals(action)) {
            DeviceInfo device = (DeviceInfo) intent.getParcelableExtra(Constants.EXTRA_CURRENT_DETECT_SENSOR);
            SubDevice temp = (SubDevice) intent.getParcelableExtra(Constants.EXTRA_CURRENT_DETECT_SENSOR_TEMP);
            SubDevice humidity = (SubDevice) intent.getParcelableExtra(Constants.EXTRA_CURRENT_DETECT_SENSOR_HUMIDITY);

            drawWidgetView(context, manager, manager.getAppWidgetIds(new ComponentName(context, getClass())), device, temp, humidity);
        } else if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {
            Intent sendIntent = new Intent(Constants.ACTION_UPDATE_DETECT_SENSOR_SERVICE);
            context.sendBroadcast(sendIntent);
        } else if (Constants.ACTION_UPDATE_NO_DETECT_SENSOR_WIDGET.equals(action)) {
            drawWidgetViewEmpty(context, manager, manager.getAppWidgetIds(new ComponentName(context, getClass())));
        }
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    public void drawWidgetView(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, DeviceInfo deviceInfo, SubDevice temp, SubDevice humidity) {
        for (int id : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_detect_sensor);
            views.setTextViewText(R.id.widget_detect_nickname_text, deviceInfo.nickName);
            views.setTextViewText(R.id.widget_detect_humidity_text, humidity.value);
            views.setTextViewText(R.id.widget_detect_humidity_scale, humidity.scale);
            views.setTextViewText(R.id.widget_detect_temperature_text, temp.value);

            Intent eventIntent = new Intent(Constants.ACTION_UPDATE_DETECT_SENSOR_REFRESH);
            PendingIntent eventPIntent = PendingIntent.getBroadcast(context, 0, eventIntent, 0);
            views.setOnClickPendingIntent(R.id.detect_update_btn, eventPIntent);

            appWidgetManager.updateAppWidget(id, views);
        }
    }

    public void drawWidgetViewEmpty(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int id : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_detect_sensor_empty);
            appWidgetManager.updateAppWidget(id, views);
        }
    }
}
