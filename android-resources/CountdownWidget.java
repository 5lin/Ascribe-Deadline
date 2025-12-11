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

    // Capacitor Preferences 使用的 SharedPreferences 名称
    private static final String CAPACITOR_PREFS_NAME = "CapacitorStorage";
    private static final String DEFAULT_TARGET_DATE = "2026-12-12";
    private static final String DEFAULT_START_DATE = "2025-01-01";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        String targetDateStr = DEFAULT_TARGET_DATE;
        String startDateStr = DEFAULT_START_DATE;
        String goalTitle = "倒计时";

        // 从 Capacitor Preferences 读取
        SharedPreferences capacitorPrefs = context.getSharedPreferences(CAPACITOR_PREFS_NAME, Context.MODE_PRIVATE);
        String capTargetDate = capacitorPrefs.getString("widget_targetDate", null);
        String capStartDate = capacitorPrefs.getString("widget_startDate", null);
        String capGoalTitle = capacitorPrefs.getString("widget_goalTitle", null);

        if (capTargetDate != null && !capTargetDate.isEmpty()) {
            targetDateStr = capTargetDate;
        }
        if (capStartDate != null && !capStartDate.isEmpty()) {
            startDateStr = capStartDate;
        }
        if (capGoalTitle != null && !capGoalTitle.isEmpty()) {
            goalTitle = capGoalTitle;
        }

        // 计算剩余天数
        long daysRemaining = calculateDaysRemaining(targetDateStr);
        
        // 计算进度百分比
        int progressPercent = calculateProgress(startDateStr, targetDateStr);

        // 创建 RemoteViews
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_countdown);
        views.setTextViewText(R.id.widget_title, goalTitle);
        views.setTextViewText(R.id.widget_days, String.valueOf(Math.max(0, daysRemaining)));
        views.setTextViewText(R.id.widget_date, "目标: " + targetDateStr);
        
        // 设置进度条
        views.setProgressBar(R.id.widget_progress, 100, progressPercent, false);
        views.setTextViewText(R.id.widget_progress_text, String.format("%.1f%%", (float) progressPercent));

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

    private static int calculateProgress(String startDateStr, String targetDateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date startDate = sdf.parse(startDateStr);
            Date targetDate = sdf.parse(targetDateStr);
            Date today = new Date();
            
            if (startDate != null && targetDate != null) {
                long totalTime = targetDate.getTime() - startDate.getTime();
                long elapsedTime = today.getTime() - startDate.getTime();
                
                if (totalTime > 0) {
                    double progress = (double) elapsedTime / totalTime * 100;
                    return (int) Math.min(Math.max(progress, 0), 100);
                }
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


