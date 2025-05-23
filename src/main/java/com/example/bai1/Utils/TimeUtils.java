package com.example.bai1.Utils;

public class TimeUtils {
    public static String getPeriodTime(Integer period) {
        if (period == null) return "Không xác định";
        
        return switch (period) {
            case 1 -> "07:00 - 08:30";
            case 2 -> "08:30 - 10:00";
            case 3 -> "10:00 - 11:30";
            case 4 -> "13:00 - 14:30";
            case 5 -> "14:30 - 16:00";
            case 6 -> "16:00 - 17:30";
            default -> "Không xác định";
        };
    }
} 