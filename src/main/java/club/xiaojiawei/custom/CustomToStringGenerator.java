package club.xiaojiawei.custom;

import club.xiaojiawei.data.ScriptStaticData;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;

/**
 * 自定义 toString方法：跳过值为 null、""、0、false的属性
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/11/9 13:06
 */
@Slf4j
public class CustomToStringGenerator {

    public static String generateToString(Object obj, boolean includeSuperClass) {
        Class<?> clazz = obj.getClass();
        StringBuilder sb = new StringBuilder(clazz.getSimpleName() + "{");
        if (includeSuperClass){
            generateSuperToString(clazz.getSuperclass(), obj, sb);
        }
        appendFields(clazz.getDeclaredFields(), obj, sb);
        if (sb.charAt(sb.length() - 2) == ',') {
            // 去掉后面的逗号和空格
            sb.setLength(sb.length() - 2);
        }
        sb.append("}");
        return sb.toString();
    }

    private static void generateSuperToString(Class<?> clazz, Object obj, StringBuilder sb){
        if (clazz == null){
            return;
        }
        generateSuperToString(clazz.getSuperclass(), obj, sb);
        appendFields(clazz.getDeclaredFields(), obj, sb);
    }

    private static void appendFields(Field[] fields, Object obj, StringBuilder sb){
        for (Field field : fields) {
            if (Objects.equals(field.getName(), ScriptStaticData.LOG_FIELD_NAME)){
                continue;
            }
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (value != null && !isDefaultValue(value)) {
                    sb.append(field.getName()).append("=");
                    sb.append(value).append(", ");
                }
            } catch (IllegalAccessException e) {
                log.error("生成toString失败", e);
            }
        }
    }

    private static boolean isDefaultValue(Object value) {
        if (value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long) {
            return ((Number) value).longValue() == 0;
        } else if (value instanceof Float || value instanceof Double) {
            return ((Number) value).doubleValue() == 0.0;
        } else if (value instanceof Character) {
            return ((Character) value) == '\u0000';
        } else if (value instanceof Boolean) {
            return !((Boolean) value);
        }
        return false;
    }
}
