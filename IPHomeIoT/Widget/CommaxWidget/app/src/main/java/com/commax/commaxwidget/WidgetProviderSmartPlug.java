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
import com.commax.commaxwidget.wallpad.data.SubSort;


/**
 * Created by OWNER on 2017-02-08.
 */
public class WidgetProviderSmartPlug extends AppWidgetProvider {
    RemoteViews mView;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        AppWidgetManager manager = AppWidgetManager.getInstance(context);

        if (Constants.ACTION_UPDATE_SMART_PLUG_WIDGET.equals(action)) {
            DeviceInfo device = (DeviceInfo) intent.getParcelableExtra(Constants.EXTRA_SMART_PLUG_DEVICES);
            SubDevice elecMeter = (SubDevice) intent.getParcelableExtra(Constants.EXTRA_SMART_PLUG_DEVICES_ELECTRIC_METER);
            SubDevice switchBinary = (SubDevice) intent.getParcelableExtra(Constants.EXTRA_SMART_PLUG_DEVICES_SWITCH_BINARY);

            drawWidgetView(context, manager, manager.getAppWidgetIds(new ComponentName(context, getClass())), device, elecMeter, switchBinary);
        } else if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {
            Intent sendIntent = new Intent(Constants.ACTION_UPDATE_SMART_PLUG_SERVICE);
            context.sendBroadcast(sendIntent);
        } else if (Constants.ACTION_UPDATE_NO_SMART_PLUG_WIDGET.equals(action)) {
            drawWidgetViewEmpty(context, manager, manager.getAppWidgetIds(new ComponentName(context, getClass())));
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    public void drawWidgetView(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, DeviceInfo deviceInfo, SubDevice elecMeter, SubDevice switchBinary) {
        for (int id : appWidgetIds) {
            mView = new RemoteViews(context.getPackageName(), R.layout.widget_smart_plug);
            mView.setTextViewText(R.id.widget_smart_plug_name_text, deviceInfo.nickName);
            mView.setTextViewText(R.id.widget_smart_plug_value_text, elecMeter.value);
            mView.setTextViewText(R.id.widget_smart_plug_value_scale, elecMeter.scale);


            Intent eventIntent = new Intent(Constants.ACTION_UPDATE_SMART_PLUG_REFRESH);
            PendingIntent eventPIntent = PendingIntent.getBroadcast(context, 0, eventIntent, 0);
            mView.setOnClickPendingIntent(R.id.smartplug_update_btn, eventPIntent);


            if (switchBinary.sort.equalsIgnoreCase(SubSort.SWITCH_BINARY)) {
                if (switchBinary.value.equalsIgnoreCase("on")) {
                    mView.setImageViewResource(R.id.widget_smart_plug_icon, R.drawable.ic_widget_smartplug_on);
                } else {
                    mView.setImageViewResource(R.id.widget_smart_plug_icon, R.drawable.ic_widget_smartplug_off);
                }
            }
            appWidgetManager.updateAppWidget(id, mView);
        }
    }

    public void drawWidgetViewEmpty(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int id : appWidgetIds) {
            RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.widget_smart_plug_empty);
            appWidgetManager.updateAppWidget(id, view);
        }
    }
}
