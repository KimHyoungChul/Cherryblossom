package com.commax.commaxwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.commax.commaxwidget.util.Constants;
import com.commax.commaxwidget.wallpad.data.WeatherInfo;


/**
 * Created by OWNER on 2017-02-08.
 */
public class WidgetProviderWeather_1_1 extends AppWidgetProvider {

    RemoteViews mView;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        AppWidgetManager manager = AppWidgetManager.getInstance(context);

        if (Constants.ACTION_UPDATE_WEATHER_WIDGET.equals(action)) {
            WeatherInfo weatherInfo = new WeatherInfo();
            weatherInfo.arg1 = intent.getExtras().getDouble(Constants.EXTRA_WEATHER_TEMP);
            weatherInfo.arg2 = intent.getExtras().getDouble(Constants.EXTRA_WEATHER_CODE);
            weatherInfo.arg3 = intent.getExtras().getDouble(Constants.EXTRA_WEATHER_HUMID);


            drawWidgetView(context, manager, manager.getAppWidgetIds(new ComponentName(context, getClass())), weatherInfo);
        } else if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {
            Intent sendIntent = new Intent(Constants.ACTION_UPDATE_WEATHER_SERVICE);
            context.sendBroadcast(sendIntent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        Intent sendIntent = new Intent(Constants.ACTION_UPDATE_WEATHER_SERVICE);
        context.sendBroadcast(sendIntent);
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

    public void drawWidgetView(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, WeatherInfo weather) {
        for (int id : appWidgetIds) {
            mView = new RemoteViews(context.getPackageName(), R.layout.widget_weather_1_1);
            mView.setTextViewText(R.id.temp_text, String.valueOf(weather.arg1));
            setIconAndBackimg(weather.arg2);
            appWidgetManager.updateAppWidget(id, mView);
        }
    }

    public void setIconAndBackimg(double value) {
        try {
            int val = (int) value;

            switch (val) {
                case 0:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_tropicalstorm);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_wind_1x1);
                    break;
                case 1:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_tropicalstorm);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_wind_1x1);
                    break;
                case 2:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_tropicalstorm);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_wind_1x1);
                    break;
                case 3:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_thunderstorm);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_wind_1x1);
                    break;
                case 4:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_thunderstorm);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_wind_1x1);
                    break;
                case 5:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_sleet);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_snow_1x1);
                    break;
                case 6:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_sleet);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_snow_1x1);
                    break;
                case 7:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_sleet);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_snow_1x1);
                    break;
                case 8:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_sleet);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_snow_1x1);
                    break;
                case 9:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_drizzle);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_snow_1x1);
                    break;
                case 10:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_sleet);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_snow_1x1);
                    break;
                case 11:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_drizzle);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_snow_1x1);
                    break;
                case 12:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_drizzle);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_snow_1x1);
                    break;
                case 13:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_snow);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_snow_1x1);
                    break;
                case 14:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_sleet);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_snow_1x1);
                    break;
                case 15:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_snow);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_snow_1x1);
                    break;
                case 16:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_snow);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_snow_1x1);
                    break;
                case 17:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_hail);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_snow_1x1);
                    break;
                case 18:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_sleet);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_snow_1x1);
                    break;
                case 19:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_dust);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_smog_1x1);
                    break;
                case 20:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_foggy);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_foggy_1x1);
                    break;
                case 21:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_foggy);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_foggy_1x1);
                    break;
                case 22:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_smoky);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_smog_1x1);
                    break;
                case 23:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_windy);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_wind_1x1);
                    break;
                case 24:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_windy);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_wind_1x1);
                    break;
                case 25:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_clod);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_snow_1x1);
                    break;
                case 26:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_cloudy);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_partlycloudy_1x1);
                    break;
                case 27:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_mostlycloudy);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_mostlycloudy_1x1);
                    break;
                case 28:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_mostlycloudy);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_mostlycloudy_1x1);
                    break;
                case 29:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_cloudy_night);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_partlycloudy_1x1);
                    break;
                case 30:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_cloudy);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_partlycloudy_1x1);
                    break;
                case 31:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_clear);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_sunny_1x1);
                    break;
                case 32:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_sunny);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_sunny_1x1);
                    break;
                case 33:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_clear);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_sunny_1x1);
                    break;
                case 34:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_sunny);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_sunny_1x1);
                    break;
                case 35:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_hail);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_snow_1x1);
                    break;
                case 36:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_hot);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_sunny_1x1);
                    break;
                case 37:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_thunderstorm);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_wind_1x1);
                    break;
                case 38:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_thunderstorm);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_wind_1x1);
                    break;
                case 39:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_thunderstorm);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_wind_1x1);
                    break;
                case 40:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_rain);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_rain_1x1);
                    break;
                case 41:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_heavysnow);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_snow_1x1);
                    break;
                case 42:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_snow);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_snow_1x1);
                    break;
                case 43:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_heavysnow);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_snow_1x1);
                    break;
                case 44:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_mostlycloudy);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_mostlycloudy_1x1);
                    break;
                case 45:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_thunderstorm);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_wind_1x1);
                    break;
                case 46:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_snow);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_snow_1x1);
                    break;
                case 47:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_thunderstorm);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_wind_1x1);
                    break;
                default:
                    mView.setImageViewResource(R.id.condition_image_1_1, R.drawable.ic_widget_cloudy);
                    mView.setInt(R.id.bg_img_1_1, "setBackgroundResource", R.drawable.bg_widget_weather_partlycloudy_1x1);
                    break;
            }
        } catch (Exception e) {

        }
    }
}
