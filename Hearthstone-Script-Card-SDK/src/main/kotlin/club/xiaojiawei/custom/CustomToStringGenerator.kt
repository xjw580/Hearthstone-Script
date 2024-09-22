package club.xiaojiawei.custom

import club.xiaojiawei.config.log
import java.lang.reflect.Field

/**
 * 自定义 toString方法：跳过值为 null、""、0、false的属性
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/11/9 13:06
 */
object CustomToStringGenerator {

    @JvmStatic
    fun generateToString(obj: Any, includeSuperClass: Boolean): String {
        val clazz: Class<*> = obj.javaClass
        val sb = StringBuilder(clazz.simpleName + "{")
        if (includeSuperClass) {
            generateSuperToString(clazz.superclass, obj, sb)
        }
        appendFields(clazz.declaredFields, obj, sb)
        if (sb[sb.length - 2] == ',') {
            // 去掉后面的逗号和空格
            sb.setLength(sb.length - 2)
        }
        sb.append("}")
        return sb.toString()
    }

    private fun generateSuperToString(clazz: Class<*>?, obj: Any, sb: StringBuilder) {
        if (clazz == null) {
            return
        }
        generateSuperToString(clazz.superclass, obj, sb)
        appendFields(clazz.declaredFields, obj, sb)
    }

    private fun appendFields(fields: Array<Field>, obj: Any, sb: StringBuilder) {
        for (field in fields) {
            if (field.name == "log") {
                continue
            }
            field.isAccessible = true
            try {
                val value = field[obj]
                if (value != null && !isDefaultValue(value)) {
                    sb.append(field.name).append("=")
                    sb.append(value).append(", ")
                }
            } catch (e: IllegalAccessException) {
                log.error(e) { "生成toString失败" }
            }
        }
    }

    private fun isDefaultValue(value: Any): Boolean {
        when (value) {
            is Byte, is Short, is Int, is Long -> {
                return (value as Number).toLong() == 0L
            }

            is Float, is Double -> {
                return (value as Number).toDouble() == 0.0
            }

            is Char -> {
                return value == '\u0000'
            }

            is Boolean -> {
                return !value
            }

            else -> return false
        }
    }
}
