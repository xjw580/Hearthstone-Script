package club.xiaojiawei.utils

import club.xiaojiawei.enums.ConfigEnum
import com.alibaba.fastjson.JSON
import org.ini4j.Config
import org.ini4j.Ini
import java.nio.file.Path


/**
 * @author 肖嘉威
 * @date 2024/9/28 15:35
 */
object ConfigUtil {

    private var CONFIG: Ini = Ini(Path.of("", "config.ini").toFile())

    init {
        val config = Config()
//        todo set path
        CONFIG.config = config
    }

    /* *************************************************************************
     *                                                                         *
     * 公共方法                                                                 *
     *                                                                         *
     **************************************************************************/

    /**
     * 存储字符串
     */
    fun putString(key: ConfigEnum, value: String, store: Boolean = true) {
        CONFIG[key.group]?.put(key.name, value)
        if (store) {
            store()
        }
    }

    /**
     * 读取字符串
     */
    fun getString(key: ConfigEnum): String {
        return CONFIG[key.group]?.get(key.name) ?: key.defaultValue
    }

    /**
     * 存储整型数字
     */
    fun putInt(key: ConfigEnum, value: Int, store: Boolean = true) {
        CONFIG[key.group]?.put(key.name, value)
        if (store) {
            store()
        }
    }

    /**
     * 读取整型数字
     */
    fun getInt(key: ConfigEnum): Int {
        return (CONFIG[key.group]?.get(key.name) ?: key.defaultValue).toInt()
    }

    /**
     * 存储长整型数字
     */
    fun putLong(key: ConfigEnum, value: Long, store: Boolean = true) {
        CONFIG[key.group]?.put(key.name, value)
        if (store) {
            store()
        }
    }

    /**
     * 读取长整型数字
     */
    fun getLong(key: ConfigEnum): Long {
        return (CONFIG[key.group]?.get(key.name) ?: key.defaultValue).toLong()
    }

    /**
     * 存储Float数字
     */
    fun putFloat(key: ConfigEnum, value: Float, store: Boolean = true) {
        CONFIG[key.group]?.put(key.name, value)
        if (store) {
            store()
        }
    }

    /**
     * 读取Float数字
     */
    fun getFloat(key: ConfigEnum): Float {
        return (CONFIG[key.group]?.get(key.name) ?: key.defaultValue).toFloat()
    }

    /**
     * 存储boolean类型数据
     */
    fun putBoolean(key: ConfigEnum, value: Boolean, store: Boolean = true) {
        CONFIG[key.group]?.put(key.name, value)
        if (store) {
            store()
        }
    }

    /**
     * 读取boolean类型数据
     */
    fun getBoolean(key: ConfigEnum): Boolean {
        return (CONFIG[key.group]?.get(key.name) ?: key.defaultValue).toBoolean()
    }

    /**
     * 存储数组类型数据
     */
    fun putArray(key: ConfigEnum, value: List<Any>, store: Boolean = true) {
        CONFIG[key.group]?.put(key.name, JSON.toJSONString(value))
        if (store) {
            store()
        }
    }

    /**
     * 读取数组类型数据
     */
    fun<T> getArray(key: ConfigEnum, clazz: Class<T>): MutableList<T> {
        val value = CONFIG[key.group]?.get(key.name) ?: key.defaultValue
        return JSON.parseArray(value, clazz)
    }

    /**
     * 存储任意类型数据
     */
    fun putObject(key: ConfigEnum, value: Any, store: Boolean = true) {
        CONFIG[key.group]?.put(key.name, JSON.toJSONString(value))
        if (store) {
            store()
        }
    }

    /**
     * 读取任意类型数据
     */
    fun<T> getObject(key: ConfigEnum, clazz: Class<T>): T {
        val value = CONFIG[key.group]?.get(key.name) ?: key.defaultValue
        return JSON.parseObject(value, clazz)
    }

    fun remove(key: ConfigEnum):String {
        return CONFIG[key.group]?.remove(key.name).toString()
    }

    /**
     * 清除数据
     */
    fun clear(store: Boolean = true) {
        CONFIG.clear()
        if (store) {
            store()
        }
    }

    fun store(){
        CONFIG.store()
    }

}
