package com.holland.infrastructure.kit.kit;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StrKit {
    private StrKit() {
    }

    private static final Pattern COMPILE_MSG = Pattern.compile("\\{}");

    public static String builderMsg(String msg, Object... args) {
        if (null == msg) return null;
        if (args.length == 0) return msg;

        final StringBuilder builder = new StringBuilder();
        final Matcher matcher = COMPILE_MSG.matcher(msg);
        int start = 0, idx = 0;
        while (matcher.find()) {
            builder.append(msg, start, matcher.start());
            builder.append(args[idx++]);
            start = matcher.end();
        }
        builder.append(msg.substring(start));
        if (idx < args.length) {
            Object exception = args[args.length - 1];
            if (exception instanceof Throwable) {
                builder.append(" ").append(exception.getClass().getSimpleName()).append(" :").append(((Throwable) exception).getMessage()).append("\r\n\t");
                builder.append(Arrays.stream(((Throwable) exception).getStackTrace()).map(StackTraceElement::toString).collect(Collectors.joining("\r\n\t")));
            }
        }
        return builder.toString();
    }
}
