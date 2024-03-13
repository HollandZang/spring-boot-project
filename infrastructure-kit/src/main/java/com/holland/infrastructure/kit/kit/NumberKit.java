package com.holland.infrastructure.kit.kit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class NumberKit {
    private static final LinkedHashMap<Integer, String> DEFAULT_SIMPLIFIED_CHINESE_DICT = new LinkedHashMap<>();
    private static final LinkedHashMap<Integer, String> DEFAULT_TRADITIONAL_CHINESE_DICT = new LinkedHashMap<>();
    private static final LinkedHashMap<Integer, String> DEFAULT_ENGLISH_DICT = new LinkedHashMap<>();

    static {
        DEFAULT_SIMPLIFIED_CHINESE_DICT.put(9, "亿");
        DEFAULT_SIMPLIFIED_CHINESE_DICT.put(5, "万");

        DEFAULT_TRADITIONAL_CHINESE_DICT.put(9, "億");
        DEFAULT_TRADITIONAL_CHINESE_DICT.put(5, "萬");

        DEFAULT_ENGLISH_DICT.put(9, "billion");
        DEFAULT_ENGLISH_DICT.put(7, "million");
        DEFAULT_ENGLISH_DICT.put(4, "thousand");
        DEFAULT_ENGLISH_DICT.put(3, "hundred");
    }

    private NumberKit() {
    }

    public static String toDescription(Number number, int scale, RoundingMode roundingMode, Locale locale) {
        final DecimalFormat df = new DecimalFormat();
        df.setGroupingUsed(false);

        final String numberStr = df.format(number);

        final String[] split = numberStr.split("\\.", 2);
        final String integerPart = split[0];
        final String fractionalPart = split.length == 2 ? split[1] : "";

        switch (locale.getLanguage()) {
            case "zh":
                switch (locale.getCountry()) {
                    case "TW":
                    case "HK":
                        return assemble(scale, roundingMode, integerPart, fractionalPart, DEFAULT_TRADITIONAL_CHINESE_DICT);
                    default:
                        return assemble(scale, roundingMode, integerPart, fractionalPart, DEFAULT_SIMPLIFIED_CHINESE_DICT);
                }
            case "en":
                return assemble(scale, roundingMode, integerPart, fractionalPart, DEFAULT_ENGLISH_DICT);
            default:
                return numberStr;
        }
    }

    private static String assemble(int scale, RoundingMode roundingMode, String integerPart, String fractionalPart, LinkedHashMap<Integer, String> dict) {
        for (Map.Entry<Integer, String> entry : dict.entrySet()) {
            final Integer places = entry.getKey();
            if (integerPart.length() >= places) {
                return _assemble(scale, roundingMode, integerPart, fractionalPart, places - 1, entry.getValue());
            }
        }
        return _assemble(scale, roundingMode, integerPart, fractionalPart, 0, "");
    }

    private static String _assemble(int scale, RoundingMode roundingMode, String integerPart, String fractionalPart, int stratum, String unit) {
        final String newIntegerPart = integerPart.substring(0, integerPart.length() - stratum);
        final String newFractionalPart = integerPart.substring(integerPart.length() - stratum) + fractionalPart;
        final String fractional = scale == 0
                ? ""
                : newFractionalPart.length() > scale
                ? '.' + new BigDecimal(newFractionalPart.substring(0, scale) + '.' + newFractionalPart.charAt(scale)).setScale(0, roundingMode).stripTrailingZeros().toPlainString()
                : '.' + newFractionalPart;
        return newIntegerPart + fractional + unit;
    }
}
