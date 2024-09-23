package club.xiaojiawei.config

import club.xiaojiawei.data.SpringData
import club.xiaojiawei.enums.ConfigurationEnum
import jakarta.annotation.Resource
import lombok.extern.slf4j.Slf4j
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.util.*

/**
 * 脚本配置文件
 * @author 肖嘉威
 * @date 2023/7/4 14:12
 */
@Configuration
open class ConfigurationConfig {

    @Resource
    private val springData: SpringData? = null

    /**
     * 脚本配置文件
     */
    @Bean
    open fun scriptConfiguration(): Properties {
        val properties = Properties()
        reloadScriptProperties(properties)
        return properties
    }

    private fun reloadScriptProperties(properties: Properties) {
        val scriptConfigurationFile = File(springData!!.scriptConfigurationFile)
        if (!scriptConfigurationFile.exists()) {
            scriptConfigurationFile.parentFile.mkdirs()
            try {
                FileWriter(scriptConfigurationFile).use { fileWriter ->
                    writeDefaultScriptProperties(fileWriter, properties)
                    log.info{"已创建脚本配置文件，路径：${scriptConfigurationFile.absolutePath}"}
                }
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
        try {
            FileReader(scriptConfigurationFile).use { fileReader ->
                properties.load(fileReader)
                FileWriter(scriptConfigurationFile, true).use { fileWriter ->
                    writeDefaultScriptProperties(fileWriter, properties)
                }
                properties.load(fileReader)
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun writeDefaultScriptProperties(fileWriter: FileWriter, properties: Properties) {
        for (configurationEnum in ConfigurationEnum.entries) {
            if (!properties.containsKey(configurationEnum.key)) {
                try {
                    fileWriter.write(configurationEnum.key + "=" + configurationEnum.defaultValue + "\n")
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }
            }
        }
    }
}
