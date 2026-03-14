package com.itzixi.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * GMT/UTC 时间转换工具类
 * 支持转换为纽约时间、北京时间等
 */
public class GMTDateConverter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * 获取纽约时间字符串
     */
    public static String getNewYorkTime(Date externalDate) {
        return convertGmtToNewYork(externalDate).format(FORMATTER);
    }

    /**
     * 将 GMT 时间转换为纽约时间
     */
    public static LocalDateTime convertGmtToNewYork(Date externalDate) {
        Instant instant = externalDate.toInstant();
        ZonedDateTime gmtTime = instant.atZone(ZoneId.of("GMT"));
        ZonedDateTime newYorkTime = gmtTime.withZoneSameInstant(ZoneId.of("America/New_York"));
        return newYorkTime.toLocalDateTime();
    }

    /**
     * 获取北京时间字符串
     */
    public static String getBeijingTime(Date externalDate) {
        return convertGmtToBeijing(externalDate).format(FORMATTER);
    }

    /**
     * 将 GMT 时间转换为北京时间
     */
    public static LocalDateTime convertGmtToBeijing(Date externalDate) {
        Instant instant = externalDate.toInstant();
        ZonedDateTime gmtTime = instant.atZone(ZoneId.of("GMT"));
        ZonedDateTime beijingTime = gmtTime.withZoneSameInstant(ZoneId.of("Asia/Shanghai"));
        return beijingTime.toLocalDateTime();
    }

    /**
     * 获取 GMT 时间字符串
     */
    public static String getGMTTime(Date externalDate) {
        return convertGmt(externalDate).format(FORMATTER);
    }

    /**
     * 将 Date 转换为 GMT LocalDateTime
     */
    public static LocalDateTime convertGmt(Date externalDate) {
        Instant instant = externalDate.toInstant();
        ZonedDateTime gmtTime = instant.atZone(ZoneId.of("GMT"));
        return gmtTime.toLocalDateTime();
    }

    /**
     * 减去24小时
     */
    public static LocalDateTime minus24Hour(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("时间不能为空");
        }
        return dateTime.minusHours(24);
    }

    /**
     * 减去3天
     */
    public static LocalDateTime minus3Day(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("时间不能为空");
        }
        return dateTime.minusDays(3);
    }

    /**
     * 减去1周
     */
    public static LocalDateTime minus1Week(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("时间不能为空");
        }
        return dateTime.minusWeeks(1);
    }

    /**
     * 加上1分钟
     */
    public static LocalDateTime plus1Minute(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("时间不能为空");
        }
        return dateTime.plusMinutes(1);
    }
}
