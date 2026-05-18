package com.datrat.tiab.util;

public final class TimeFormat {
    private TimeFormat() {
    }

    public static String formatTicks(int ticks) {
        int seconds = Math.max(0, ticks) / 20;
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }
}
