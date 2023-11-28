package com.holland.infrastructure.kit.exception;

import cn.hutool.core.util.StrUtil;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

public class AssertKit {
    private AssertKit() {
    }

    /**
     * --------------------------------------------------------
     * 1. 断言对象为null
     * 2. 断言字符串为空字符串
     * 3. 断言集合为空集合
     * 4. 断言数组为空数组
     * 5. 断言Map为空Map
     * --------------------------------------------------------
     **/
    public static void isEmpty(Object obj, String msg) {
        if (obj != null) {
            if (obj instanceof CharSequence) {
                final CharSequence charSequence = (CharSequence) obj;
                if (charSequence.length() == 0)
                    return;
            }

            if (obj instanceof Collection) {
                final Collection<?> collection = (Collection<?>) obj;
                if (collection.isEmpty())
                    return;
            }

            if (obj.getClass().isArray()) {
                if (Array.getLength(obj) == 0)
                    return;
            }

            if (obj instanceof Map) {
                final Map<?, ?> map = (Map<?, ?>) obj;
                if (map.isEmpty())
                    return;
            }
        }
    }

    public static void isNotEmpty(Object obj, String msg) {
        if (null == obj)
            throw new SimpleException(msg);

        if (obj instanceof CharSequence) {
            final CharSequence charSequence = (CharSequence) obj;
            if (charSequence.length() == 0)
                throw new SimpleException(msg);
        }

        if (obj instanceof Collection) {
            final Collection<?> collection = (Collection<?>) obj;
            if (collection.isEmpty())
                throw new SimpleException(msg);
        }

        if (obj.getClass().isArray()) {
            if (Array.getLength(obj) == 0)
                throw new SimpleException(msg);
        }

        if (obj instanceof Map) {
            final Map<?, ?> map = (Map<?, ?>) obj;
            if (map.isEmpty())
                throw new SimpleException(msg);
        }
    }

    /**
     * --------------------------------------------------------
     * 断言null、false,如果为true,报错
     * --------------------------------------------------------
     **/
    public static void isFalse(Boolean boo, String msg) {
        if (null == boo)
            return;
        if (boo)
            throw new SimpleException(msg);
    }

    public static void isTrue(Boolean boo, String msg) {
        if (null == boo)
            throw new SimpleException(msg);
        if (!boo)
            throw new SimpleException(msg);
    }

    /**
     * --------------------------------------------------------
     * 断言两个字符串一致
     * --------------------------------------------------------
     **/
    public static void isEquals(CharSequence s1, CharSequence s2, String msg) {
        if (!StrUtil.equals(s1, s2))
            throw new SimpleException(msg);
    }

    public static void isEqualsIgnoreCase(CharSequence s1, CharSequence s2, String msg) {
        if (!StrUtil.equalsIgnoreCase(s1, s2))
            throw new SimpleException(msg);
    }
}
