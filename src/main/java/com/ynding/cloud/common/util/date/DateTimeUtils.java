package com.ynding.cloud.common.util.date;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author dyn
 * @version 2019/12/17
 */
public class DateTimeUtils {

    /**
     * 收集起始时间到结束时间之间所有的时间并以字符串集合方式返回
     *
     * @param timeStart
     * @param timeEnd
     * @return
     */
    public static List<String> collectDates(String timeStart, String timeEnd) {
        return collectDates(DateUtil.parse(timeStart), DateUtil.parse(timeEnd));
    }

    /**
     * 收集月份
     *
     * @param timeStart
     * @param timeEnd
     * @return
     */
    public static List<String> collectMonths(String timeStart, String timeEnd) {
        return collectMonths(DateUtil.parse(timeStart), DateUtil.parse(timeEnd));
    }

    /**
     * 收集周
     *
     * @param timeStart
     * @param timeEnd
     * @return
     */
    public static List<Integer> collectWeeks(String timeStart, String timeEnd) {
        return collectWeeks(DateUtil.parse(timeStart), DateUtil.parse(timeEnd));
    }


    public static List<Integer> collectWeeks(Date start, Date end) {
        // 用起始时间作为流的源头，按照每次加一天的方式创建一个无限流
        return Stream.iterate(start, date -> DateUtil.offsetWeek(date, 1))
                // 截断无限流，长度为起始时间和结束时间的差+1个
                .limit(DateUtil.weekOfYear(end) - DateUtil.weekOfYear(start) + 1)
                // 由于最后要的是字符串，所以map转换一下
                .map(e -> {
                    return DateUtil.weekOfYear(e);
                })
                // 把流收集为List
                .collect(Collectors.toList());
    }

    public static List<String> collectDates(Date start, Date end) {
        // 用起始时间作为流的源头，按照每次加一天的方式创建一个无限流
        return Stream.iterate(start, date -> DateUtil.offsetDay(date, 1))
                // 截断无限流，长度为起始时间和结束时间的差+1个
                .limit(DateUtil.between(start, end, DateUnit.DAY) + 1)
                // 由于最后要的是字符串，所以map转换一下
                .map(DateUtil::formatDate)
                // 把流收集为List
                .collect(Collectors.toList());
    }

    public static List<String> collectMonths(Date start, Date end) {
        // 用起始时间作为流的源头，按照每次加一天的方式创建一个无限流
        return Stream.iterate(start, date -> DateUtil.offsetMonth(date, 1))
                // 截断无限流，长度为起始时间和结束时间的差+1个
                .limit(DateUtil.betweenMonth(start, end, true) + 1)
                // 由于最后要的是字符串，所以map转换一下
                .map(e -> {
                    return DateUtil.format(e, "yyyy-MM");
                })
                // 把流收集为List
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        String timeStart = "2018-08-10 00:00:00";
        String timeEnd = "2019-12-11 00:00:00";
        collectMonths(timeStart, timeEnd).stream().forEach(
                e -> {
                    System.out.println(e);
                }
        );
    }
}
