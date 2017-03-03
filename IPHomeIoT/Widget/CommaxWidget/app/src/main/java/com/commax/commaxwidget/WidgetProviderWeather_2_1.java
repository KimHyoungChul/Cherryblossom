package com.commax.commaxwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;

import com.commax.commaxwidget.util.Constants;
import com.commax.commaxwidget.wallpad.data.WeatherInfo;

/**
 * Created by OWNER on 2017-02-16.
 */

public class WidgetProviderWeather_2_1 extends AppWidgetProvider {
    private RemoteViews mView;
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        mContext = context;

        String action = intent.getAction();
        AppWidgetManager manager = AppWidgetManager.getInstance(context);

        if (Constants.ACTION_UPDATE_WEATHER_WIDGET.equals(action)) {
            WeatherInfo weatherInfo = new WeatherInfo();

            weatherInfo.arg1 = intent.getExtras().getDouble(Constants.EXTRA_WEATHER_TEMP);
            weatherInfo.arg2 = intent.getExtras().getDouble(Constants.EXTRA_WEATHER_CODE);
            weatherInfo.arg3 = intent.getExtras().getDouble(Constants.EXTRA_WEATHER_HUMID);
            weatherInfo.arg4 = intent.getExtras().getString(Constants.EXTRA_WEATHER_LOCATION);

            drawWidgetView(context, manager, manager.getAppWidgetIds(new ComponentName(context, getClass())), weatherInfo);
        } else if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {
            Intent sendIntent = new Intent(Constants.ACTION_UPDATE_WEATHER_SERVICE);
            context.sendBroadcast(sendIntent);
        }
    }

    public Bitmap getRoundedCornerBitmap(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);
        final float roundPx = 15;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
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
            mView = new RemoteViews(context.getPackageName(), R.layout.widget_weather_2_1);

            mView.setTextViewText(R.id.temp_text, String.valueOf(weather.arg1));
            mView.setTextViewText(R.id.region_text, String.valueOf(weather.arg4));
            setIconAndBackimg(weather.arg2);

            Intent eventIntent = new Intent(Constants.ACTION_UPDATE_WEATHER_REFRESH);
            PendingIntent eventPIntent = PendingIntent.getBroadcast(context, 0, eventIntent, 0);
            mView.setOnClickPendingIntent(R.id.weather_update_btn, eventPIntent);

            appWidgetManager.updateAppWidget(id, mView);
        }
    }

    private void setBackground(int id) {
        BitmapDrawable drawable = (BitmapDrawable) ResourcesCompat.getDrawable(mContext.getResources(), id, null);
        Bitmap bitmap = getRoundedCornerBitmap(drawable.getBitmap());
        mView.setImageViewBitmap(R.id.condition_image_bg, bitmap);
    }

    public void setIconAndBackimg(double value) {
        try {
            int val = (int) value;
            switch (val) {
                case 0:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_tropicalstorm);
                    setBackground(R.drawable.bg_widget_weather_wind_2x1);
                    break;
                case 1:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_tropicalstorm);
                    setBackground(R.drawable.bg_widget_weather_wind_2x1);
                    break;
                case 2:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_tropicalstorm);
                    setBackground(R.drawable.bg_widget_weather_wind_2x1);
                    break;
                case 3:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_thunderstorm);
                    setBackground(R.drawable.bg_widget_weather_wind_2x1);
                    break;
                case 4:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_thunderstorm);
                    setBackground(R.drawable.bg_widget_weather_wind_2x1);
                    break;
                case 5:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_sleet);
                    setBackground(R.drawable.bg_widget_weather_snow_2x1);
                    break;
                case 6:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_sleet);
                    setBackground(R.drawable.bg_widget_weather_snow_2x1);
                    break;
                case 7:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_sleet);
                    setBackground(R.drawable.bg_widget_weather_snow_2x1);
                    break;
                case 8:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_sleet);
                    setBackground(R.drawable.bg_widget_weather_snow_2x1);
                    break;
                case 9:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_drizzle);
                    setBackground(R.drawable.bg_widget_weather_snow_2x1);
                    break;
                case 10:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_sleet);
                    setBackground(R.drawable.bg_widget_weather_snow_2x1);
                    break;
                case 11:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_drizzle);
                    setBackground(R.drawable.bg_widget_weather_snow_2x1);
                    break;
                case 12:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_drizzle);
                    setBackground(R.drawable.bg_widget_weather_snow_2x1);
                    break;
                case 13:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_snow);
                    setBackground(R.drawable.bg_widget_weather_snow_2x1);
                    break;
                case 14:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_sleet);
                    setBackground(R.drawable.bg_widget_weather_snow_2x1);
                    break;
                case 15:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_snow);
                    setBackground(R.drawable.bg_widget_weather_snow_2x1);
                    break;
                case 16:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_snow);
                    setBackground(R.drawable.bg_widget_weather_snow_2x1);
                    break;
                case 17:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_hail);
                    setBackground(R.drawable.bg_widget_weather_snow_2x1);
                    break;
                case 18:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_sleet);
                    setBackground(R.drawable.bg_widget_weather_snow_2x1);
                    break;
                case 19:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_dust);
                    setBackground(R.drawable.bg_widget_weather_smog_2x1);
                    break;
                case 20:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_foggy);
                    setBackground(R.drawable.bg_widget_weather_foggy_2x1);
                    break;
                case 21:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_foggy);
                    setBackground(R.drawable.bg_widget_weather_foggy_2x1);
                    break;
                case 22:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_smoky);
                    setBackground(R.drawable.bg_widget_weather_smog_2x1);
                    break;
                case 23:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_windy);
                    setBackground(R.drawable.bg_widget_weather_wind_2x1);
                    break;
                case 24:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_windy);
                    setBackground(R.drawable.bg_widget_weather_wind_2x1);
                    break;
                case 25:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_clod);
                    setBackground(R.drawable.bg_widget_weather_snow_2x1);
                    break;
                case 26:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_cloudy);
                    setBackground(R.drawable.bg_widget_weather_partlycloudy_2x1);
                    break;
                case 27:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_mostlycloudy);
                    setBackground(R.drawable.bg_widget_weather_mostlycloudy_2x1);
                    break;
                case 28:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_mostlycloudy);
                    setBackground(R.drawable.bg_widget_weather_mostlycloudy_2x1);
                    break;
                case 29:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_cloudy_night);
                    setBackground(R.drawable.bg_widget_weather_partlycloudy_2x1);
                    break;
                case 30:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_cloudy);
                    setBackground(R.drawable.bg_widget_weather_partlycloudy_2x1);
                    break;
                case 31:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_clear);
                    setBackground(R.drawable.bg_widget_weather_sunny_2x1);
                    break;
                case 32:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_sunny);
                    setBackground(R.drawable.bg_widget_weather_sunny_2x1);
                    break;
                case 33:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_clear);
                    setBackground(R.drawable.bg_widget_weather_sunny_2x1);
                    break;
                case 34:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_sunny);
                    setBackground(R.drawable.bg_widget_weather_sunny_2x1);
                    break;
                case 35:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_hail);
                    setBackground(R.drawable.bg_widget_weather_snow_2x1);
                    break;
                case 36:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_hot);
                    setBackground(R.drawable.bg_widget_weather_sunny_2x1);
                    break;
                case 37:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_thunderstorm);
                    setBackground(R.drawable.bg_widget_weather_wind_2x1);
                    break;
                case 38:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_thunderstorm);
                    setBackground(R.drawable.bg_widget_weather_wind_2x1);
                    break;
                case 39:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_thunderstorm);
                    setBackground(R.drawable.bg_widget_weather_wind_2x1);
                    break;
                case 40:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_rain);
                    setBackground(R.drawable.bg_widget_weather_rain_2x1);
                    break;
                case 41:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_heavysnow);
                    setBackground(R.drawable.bg_widget_weather_snow_2x1);
                    break;
                case 42:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_snow);
                    setBackground(R.drawable.bg_widget_weather_snow_2x1);
                    break;
                case 43:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_heavysnow);
                    setBackground(R.drawable.bg_widget_weather_snow_2x1);
                    break;
                case 44:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_mostlycloudy);
                    setBackground(R.drawable.bg_widget_weather_mostlycloudy_2x1);
                    break;
                case 45:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_thunderstorm);
                    setBackground(R.drawable.bg_widget_weather_wind_2x1);
                    break;
                case 46:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_snow);
                    setBackground(R.drawable.bg_widget_weather_snow_2x1);
                    break;
                case 47:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_thunderstorm);
                    setBackground(R.drawable.bg_widget_weather_wind_2x1);
                    break;
                default:
                    mView.setImageViewResource(R.id.condition_image_2_1, R.drawable.ic_widget_cloudy);
                    setBackground(R.drawable.bg_widget_weather_partlycloudy_2x1);
                    break;
            }
        } catch (Exception e) {

        }
    }
}
