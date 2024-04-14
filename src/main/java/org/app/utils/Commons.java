package org.app.utils;

public class Commons {

    public static <T> boolean isNull(T value) {
        return value == null;
    }

    public static <T> boolean isEmpty(T value) {
        if(value instanceof String val) {
            return val.isEmpty();
        }
        return value == null;
    }

    public static <T> boolean notEmpty(T value) {
        return !isEmpty(value);
    }

}
