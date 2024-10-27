package org.app.utils;

import java.lang.reflect.Field;

public class GenericMapper {

    public static <T> T mapFields(T target, T source) {
        Class<?> sourceClass = source.getClass();
        Class<?> targetClass = target.getClass();

        // Percorre todos os campos declarados na classe source
        for (Field sourceField : sourceClass.getDeclaredFields()) {
            try {

                // Skip the 'id' field to avoid overwriting it
                if (sourceField.getName().equals("id") || sourceField.getName().equals("_id")) {
                    continue;
                }

                // Encontra o mesmo campo na classe target
                Field targetField = targetClass.getDeclaredField(sourceField.getName());

                // Garante que o campo seja acessível (public, private, etc.)
                sourceField.setAccessible(true);
                targetField.setAccessible(true);

                // Atribui o valor do campo source para o campo target
                Object value = sourceField.get(source);
                targetField.set(target, value);

            } catch (NoSuchFieldException | IllegalAccessException e) {
                // Lidar com exceções ou log de erros aqui, se necessário
                e.printStackTrace();
            }
        }

        return target;
    }
}