package org.app.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Commons {

    public static <T> boolean isNull(T value) {
        return value == null;
    }

    public static <T> boolean isEmpty(T value) {
        if(value instanceof String val) {
            return val.isEmpty();
        } else if (value instanceof ArrayList<?> a) {
            return a.isEmpty();
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

    public static String extractJsonFromString(String string) {
        // Regex para capturar o conteúdo entre ```json e ```
        Pattern pattern = Pattern.compile("```json\\s*(.*?)\\s*```", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(string);

        if (matcher.find()) {
            return matcher.group(1); // Retorna o JSON capturado
        } else {
            try {
                new ObjectMapper().readTree(string);
                return string; // Retorna a string diretamente se for um JSON válido
            } catch (Exception e) {
                throw new RuntimeException("JSON não encontrado na resposta: " + string);
            }
        }
    }
}
