package club.xiaojiawei.hsscript

import ch.qos.logback.classic.AsyncAppender
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.filter.ThresholdFilter
import ch.qos.logback.classic.joran.JoranConfigurator
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.spi.FilterReply
import club.xiaojiawei.hsscript.consts.SCRIPT_NAME
import club.xiaojiawei.hsscript.utils.ConfigExUtil
import club.xiaojiawei.hsscript.utils.WindowUtil
import javafx.application.Application
import org.slf4j.LoggerFactory
import java.io.File
import java.io.RandomAccessFile
import java.nio.file.Files

/**
 * @author 肖嘉威
 * @date 2024/10/14 17:42
 */
var PROGRAM_ARGS: List<String> = emptyList()

var fileLogLevel = ConfigExUtil.getFileLogLevel().toInt()

private fun setLogPath() {
    try {
        val context = LoggerFactory.getILoggerFactory()
        if (context is LoggerContext) {
            val logbackConfigFile = File("logback.xml")
            if (logbackConfigFile.exists()) {
                val configurator = JoranConfigurator()
                configurator.context = context
                context.reset()
                configurator.doConfigure(logbackConfigFile)
            }

            val appender = context.getLogger("ROOT").getAppender("file_async")
            if (appender is AsyncAppender) {
                for (iteratorForAppender in appender.iteratorForAppenders()) {
                    if (iteratorForAppender.name == "file") {
                        iteratorForAppender.addFilter(object : ThresholdFilter() {
                            override fun decide(iLoggingEvent: ILoggingEvent): FilterReply {
                                return if (iLoggingEvent.level.toInt() >= fileLogLevel) FilterReply.ACCEPT else FilterReply.DENY
                            }
                        })
                        break
                    }
                }
            }
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun main(args: Array<String>) {
    System.setProperty("jna.library.path", "lib")

    val file = File(".pid")
    if (!file.exists()) {
        file.createNewFile()
        Files.setAttribute(file.toPath(), "dos:hidden", true);
    }
    val randomAccessFile = RandomAccessFile(file, "rw")
    if (randomAccessFile.channel.tryLock() == null){
        WindowUtil.hideLaunchPage()
        return
    }

    randomAccessFile.setLength(0)
    randomAccessFile.write(SCRIPT_NAME.toByteArray());

    setLogPath()

    PROGRAM_ARGS = args.toList()

    Application.launch(MainApplication::class.java, *args)
}