package com.countdown.app;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CountdownWidget extends AppWidgetProvider {

    private static final String PREFS_NAME = "CountdownWidgetPrefs";
    private static final String PREF_TARGET_DATE = "targetDate";
    private static final String PREF_GOAL_TITLE = "goalTitle";
    private static final String DEFAULT_TARGET_DATE = "2026-12-12";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String targetDateStr = prefs.getString(PREF_TARGET_DATE, DEFAULT_TARGET_DATE);
        String goalTitle = prefs.getString(PREF_GOAL_TITLE, "倒计时");

        // 计算剩余天数
        long daysRemaining = calculateDaysRemaining(targetDateStr);

        // 创建 RemoteViews
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_countdown);
        views.setTextViewText(R.id.widget_title, goalTitle);
        views.setTextViewText(R.id.widget_days, String.valueOf(daysRemaining));
        views.setTextViewText(R.id.widget_date, "目标: " + targetDateStr);

        // 点击打开应用
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (intent != null) {
            PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static long calculateDaysRemaining(String targetDateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date targetDate = sdf.parse(targetDateStr);
            Date today = new Date();
            
            if (targetDate != null) {
                long diff = targetDate.getTime() - today.getTime();
                return TimeUnit.MILLISECONDS.toDays(diff);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void onEnabled(Context context) {
        // 小组件首次添加时调用
    }

    @Override
    public void onDisabled(Context context) {
        // 所有小组件实例被删除时调用
    }
}
