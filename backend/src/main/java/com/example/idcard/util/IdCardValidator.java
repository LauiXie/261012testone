package com.example.idcard.util;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Locale;

/**
 * 身份证号基础校验与信息推导（18 位，最后一位可为 X）。
 */
public final class IdCardValidator {

    /**
     * 18 位身份证号基本格式：6 位地区码 + 8 位出生日期 + 3 位顺序码 + 1 位校验位。
     */
    private static final String REGEX =
            "^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[0-9Xx]$";

    private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile(REGEX);

    private IdCardValidator() {
    }

    /** 统一大小写与首尾空白，便于唯一性比较。 */
    public static String normalize(String idNumber) {
        return idNumber == null ? null : idNumber.trim().toUpperCase(Locale.ROOT);
    }

    public static boolean isValidFormat(String idNumber) {
        return idNumber != null && PATTERN.matcher(idNumber.trim()).matches();
    }

    /** 由第 17 位（顺序码末位）推导性别：奇数为男，偶数为女。 */
    public static String deriveGender(String idNumber) {
        if (!isValidFormat(idNumber)) {
            return null;
        }
        int sequence = idNumber.trim().charAt(16) - '0';
        return sequence % 2 == 1 ? "男" : "女";
    }

    /** 由第 7~14 位推导出生日期，非法日期返回 null。 */
    public static LocalDate deriveBirthDate(String idNumber) {
        if (!isValidFormat(idNumber)) {
            return null;
        }
        String value = idNumber.trim();
        int year = Integer.parseInt(value.substring(6, 10));
        int month = Integer.parseInt(value.substring(10, 12));
        int day = Integer.parseInt(value.substring(12, 14));
        try {
            return LocalDate.of(year, month, day);
        } catch (DateTimeException ex) {
            return null;
        }
    }
}
