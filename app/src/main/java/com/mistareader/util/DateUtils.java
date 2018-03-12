package com.mistareader.util;

import java.text.ChoiceFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.mistareader.util.DateUtils.CustomDate.DAYS;
import static com.mistareader.util.DateUtils.CustomDate.MONTHS;
import static com.mistareader.util.DateUtils.CustomDate.YEARS;

public class DateUtils {
    public static final SimpleDateFormat SDF_D_M_H_M    = new SimpleDateFormat("d MMM H:mm", Locale.getDefault());
    public static final SimpleDateFormat SDF_D_MM_Y     = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());
    public static final SimpleDateFormat SDF_D_MM_Y_H_M = new SimpleDateFormat("d MMMM yyyy HH:mm", Locale.getDefault());

    private static final double[]     limits          = {1, 2, 3};
    private static final ChoiceFormat yearsFormatter  = new ChoiceFormat(limits, new String[]{"год", "года", "лет"});
    private static final ChoiceFormat monthsFormatter = new ChoiceFormat(limits, new String[]{"месяц", "месяца", "месяцев"});
    private static final ChoiceFormat daysFormatter   = new ChoiceFormat(limits, new String[]{"день", "дня", "дней"});

    public static String formatBirth(int year) {
        if (year <= 0) {
            return "";
        }
        int curYear = Calendar.getInstance().get(Calendar.YEAR);
        int years = curYear - year;
        return String.format("%s (~%s)", year, getYearStr(years));
    }

    public static String formatDateRange(Date time) {
        Calendar calUser = Calendar.getInstance();
        Calendar calNow = Calendar.getInstance();
        calUser.setTime(time);
        CustomDate dateUser = new CustomDate(calUser);
        CustomDate dateNow = new CustomDate(calNow);

        long years = dateUser.between(dateNow, YEARS);
        long months = dateUser.plusYears(years).between(dateNow, MONTHS);
        long days = dateUser.plusYears(years).plusMonths(months).between(dateNow, DAYS);

        String res = "";
        if (years > 0) {
            res = getYearStr(years);
        }

        if (months > 0) {
            res += (res.isEmpty() ? "" : " ") + getMonthStr(months);
        }

        if (days > 0) {
            res += (res.isEmpty() ? "" : " ") + getDaysStr(days);
        }

        return res;
    }

    private static String getYearStr(long num) {
        return num + " " + yearsFormatter.format(getType(num));
    }

    private static String getMonthStr(long num) {
        return num + " " + monthsFormatter.format(getType(num));
    }

    private static String getDaysStr(long num) {
        return num + " " + daysFormatter.format(getType(num));
    }

    private static int getType(Long n) {
        if (n == 0) return 0;
        n = Math.abs(n) % 100;
        Long n1 = n % 10;
        if (n > 10 && n < 20) return 3;
        if (n1 > 1 && n1 < 5) return 2;
        if (n1 == 1) return 1;
        return 3;
    }

    static class CustomDate {
        public static final int YEARS  = 0;
        public static final int MONTHS = 1;
        public static final int DAYS   = 2;

        private static final int  DAYS_PER_CYCLE    = 146097;
        private static final long DAYS_0000_TO_1970 = (DAYS_PER_CYCLE * 5L) - (30L * 365L + 7L);

        private final int   year;
        private final short month;
        private final short day;

        public CustomDate(Calendar calendar) {
            year = calendar.get(Calendar.YEAR);
            month = (short) calendar.get(Calendar.MONTH);
            day = (short) calendar.get(Calendar.DAY_OF_MONTH);
        }

        public CustomDate(int year, int month, int day) {
            this.year = year;
            this.month = (short) month;
            this.day = (short) day;
        }

        public long between(CustomDate end, int type) {
            switch (type) {
                case DAYS:
                    return daysUntil(end);
                case MONTHS:
                    return monthsUntil(end);
                case YEARS:
                    return monthsUntil(end) / 12;
            }
            return 0;
        }

        long daysUntil(CustomDate end) {
            return end.toEpochDay() - toEpochDay();  // no overflow
        }

        private long monthsUntil(CustomDate end) {
            long packed1 = getProlepticMonth() * 32L + getDayOfMonth();  // no overflow
            long packed2 = end.getProlepticMonth() * 32L + end.getDayOfMonth();  // no overflow
            return (packed2 - packed1) / 32;
        }

        public int getDayOfMonth() {
            return day;
        }

        private long getProlepticMonth() {
            return (year * 12L + month - 1);
        }

        public CustomDate plusYears(long yearsToAdd) {
            if (yearsToAdd == 0) {
                return this;
            }
            int newYear = checkValidIntValue(year + yearsToAdd);  // safe overflow
            return resolvePreviousValid(newYear, month, day);
        }

        public CustomDate plusMonths(long monthsToAdd) {
            if (monthsToAdd == 0) {
                return this;
            }
            long monthCount = year * 12L + (month - 1);
            long calcMonths = monthCount + monthsToAdd;  // safe overflow
            int newYear = checkValidIntValue(floorDiv(calcMonths, 12));
            int newMonth = (int) floorMod(calcMonths, 12) + 1;
            return resolvePreviousValid(newYear, newMonth, day);
        }

        public static long floorDiv(long x, long y) {
            long r = x / y;
            // if the signs are different and modulo not zero, round down
            if ((x ^ y) < 0 && (r * y != x)) {
                r--;
            }
            return r;
        }

        public static long floorMod(long x, long y) {
            return x - floorDiv(x, y) * y;
        }

        public int checkValidIntValue(long value) {
            if (value > Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            } else if (value < Integer.MIN_VALUE) {
                return Integer.MIN_VALUE;
            }
            return (int) value;
        }

        private static CustomDate resolvePreviousValid(int year, int month, int day) {
            switch (month) {
                case 2:
                    day = Math.min(day, isLeapYear(year) ? 29 : 28);
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    day = Math.min(day, 30);
                    break;
            }
            return new CustomDate(year, month, day);
        }

        public long toEpochDay() {
            long y = year;
            long m = month;
            long total = 0;
            total += 365 * y;
            if (y >= 0) {
                total += (y + 3) / 4 - (y + 99) / 100 + (y + 399) / 400;
            } else {
                total -= y / -4 - y / -100 + y / -400;
            }
            total += ((367 * m - 362) / 12);
            total += day - 1;
            if (m > 2) {
                total--;
                if (!isLeapYear(year)) {
                    total--;
                }
            }
            return total - DAYS_0000_TO_1970;
        }

        public static boolean isLeapYear(long prolepticYear) {
            return ((prolepticYear & 3) == 0) && ((prolepticYear % 100) != 0 || (prolepticYear % 400) == 0);
        }
    }
}
