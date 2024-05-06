package org.app.utils;

import java.util.List;

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

    public static <T> boolean isTheSame(T value, T otherValue) {
        return value.equals(otherValue);
    }

    public static <T> boolean isNotTheSame(T value, T otherValue) {
        return !isTheSame(value, otherValue);
    }

    public static String split(String string, String spliter, int position) {
        String[] stringList;
        if(notEmpty(string)){
            stringList = string.split(spliter);
            return stringList.length >= position ? stringList[1] : null;
        }

        return null;
    }
}
