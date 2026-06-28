package club.xiaojiawei.hsscriptaistrategy.config

import club.xiaojiawei.hsscriptbase.config.log
import org.ini4j.Ini
import java.io.File
import java.nio.file.Path

object AiConfig {

    private const val SECTION = "ai"
    private const val CONFIG_FILE_NAME = "script.ini"

    private val configFile: File by lazy {
        Path.of(System.getProperty("user.dir", "."), "config", CONFIG_FILE_NAME).toFile()
    }

    private val ini: Ini by lazy {
        val file = configFile
        if (file.exists()) {
            Ini(file)
        } else {
            log.warn { "AI配置文件不存在: ${file.absolutePath}，将使用默认值" }
            Ini()
        }
    }

    private fun get(key: String, default: String): String =
        try {
            ini.get(SECTION, key) ?: default
        } catch (e: Exception) {
            default
        }

    fun isEnabled(): Boolean = get("AI_ENABLED", "false").toBoolean()

    fun baseUrl(): String = get("AI_BASE_URL", "")

    fun provider(): String = get("AI_PROVIDER", "openai")

    fun model(): String = get("AI_MODEL", "")

    fun apiKey(): String = get("AI_API_KEY", "")

    fun timeout(): Int = get("AI_TIMEOUT", "30000").toIntOrNull() ?: 30000

    fun temperature(): Double = get("AI_TEMPERATURE", "0.7").toDoubleOrNull() ?: 0.7

    fun fallbackStrategyId(): String =
        get("AI_FALLBACK_STRATEGY", "e71234fa-1-radical-deck-97e9-1f4e126cd33b")

}
