package club.xiaojiawei.hsscript.utils

import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.data.CONFIG_PATH
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.util.isFalse
import com.alibaba.fastjson.JSON
import org.ini4j.Config
import org.ini4j.Ini
import java.nio.file.Path


/**
 * @author 肖嘉威
 * @date 2024/9/28 15:35
 */
object ConfigUtil {

    private val configFile = Path.of(CONFIG_PATH, "script.ini").toFile()

    private val CONFIG: Ini

    init {
        configFile.exists().isFalse {
            configFile.parentFile.mkdirs()
            configFile.createNewFile()
            log.info { "已创建配置文件【${configFile.absolutePath}】" }
        }
        CONFIG =Ini(configFile)
        val cfg = Config()
        CONFIG.config = cfg
        checkConfig()
    }

    private fun checkConfig() {
        ConfigEnum.values().forEach { entry ->
            if (CONFIG.get(entry.group, entry.name) == null) {
                putString(entry, entry.defaultValue, false)
            }
        }
        store()
        log.info { "配置文件读取完毕：${configFile.absolutePath}" }
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
        CONFIG.put(key.group, key.name, value)
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
        CONFIG.put(key.group, key.name, value)
        if (store) {
            store()
        }
    }

    /**
     * 读取整型数字
     */
    fun getInt(key: ConfigEnum): Int {
        return (CONFIG[key.group]?.get(key.name) ?: key.defaultValue).toIntOrNull() ?: key.defaultValue.toIntOrNull()
        ?: 0
    }

    /**
     * 存储长整型数字
     */
    fun putLong(key: ConfigEnum, value: Long, store: Boolean = true) {
        CONFIG.put(key.group, key.name, value)
        if (store) {
            store()
        }
    }

    /**
     * 读取长整型数字
     */
    fun getLong(key: ConfigEnum): Long {
        return (CONFIG[key.group]?.get(key.name) ?: key.defaultValue).toLongOrNull() ?: key.defaultValue.toLongOrNull()
        ?: 0L
    }

    /**
     * 存储Float数字
     */
    fun putFloat(key: ConfigEnum, value: Float, store: Boolean = true) {
        CONFIG.put(key.group, key.name, value)
        if (store) {
            store()
        }
    }

    /**
     * 读取Float数字
     */
    fun getFloat(key: ConfigEnum): Float {
        return (CONFIG[key.group]?.get(key.name) ?: key.defaultValue).toFloatOrNull()
            ?: key.defaultValue.toFloatOrNull()
            ?: 0.0f
    }

    /**
     * 存储boolean类型数据
     */
    fun putBoolean(key: ConfigEnum, value: Boolean, store: Boolean = true) {
        CONFIG.put(key.group, key.name, value)
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
        CONFIG.put(key.group, key.name, JSON.toJSONString(value))
        if (store) {
            store()
        }
    }

    /**
     * 读取数组类型数据
     */
    fun <T> getArray(key: ConfigEnum, clazz: Class<T>): MutableList<T>? {
        val value = CONFIG[key.group]?.get(key.name) ?: key.defaultValue
        return JSON.parseArray(value, clazz)
    }

    /**
     * 存储任意类型数据
     */
    fun putObject(key: ConfigEnum, value: Any, store: Boolean = true) {
        CONFIG.put(key.group, key.name, JSON.toJSONString(value))
        if (store) {
            store()
        }
    }

    /**
     * 读取任意类型数据
     */
    fun <T> getObject(key: ConfigEnum, clazz: Class<T>): T? {
        val value = CONFIG[key.group]?.get(key.name) ?: key.defaultValue
        return JSON.parseObject(value, clazz)
    }

    fun remove(key: ConfigEnum) {
        CONFIG[key.group]?.remove(key.name)
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

    fun store() {
        CONFIG.store()
    }

}
