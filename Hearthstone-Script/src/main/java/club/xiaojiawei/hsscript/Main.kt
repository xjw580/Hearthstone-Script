package club.xiaojiawei.hsscript

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.joran.JoranConfigurator
import club.xiaojiawei.hsscript.data.IMG_PATH
import club.xiaojiawei.hsscript.data.SCRIPT_NAME
import club.xiaojiawei.hsscript.dll.ZLaunchDll
import club.xiaojiawei.hsscript.utils.SystemUtil
import com.sun.jna.WString
import javafx.application.Application
import org.slf4j.LoggerFactory
import java.awt.Toolkit
import java.io.File
import java.nio.file.Path

/**
 * @author 肖嘉威
 * @date 2024/10/14 17:42
 */
var PROGRAM_ARGS: List<String> = emptyList()

private fun setLogPath() {
    try {
        val logbackConfigFile = File("logback.xml")
        if (logbackConfigFile.exists()) {
            val context = LoggerFactory.getILoggerFactory() as LoggerContext
            val configurator = JoranConfigurator()
            configurator.context = context
            context.reset()
            configurator.doConfigure(logbackConfigFile)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun main(args: Array<String>) {
    System.setProperty("jna.library.path", "lib")

    val scale = Toolkit.getDefaultToolkit().screenResolution / 96.0
    ZLaunchDll.INSTANCE.ShowPage(
        WString(Path.of(IMG_PATH, "startup.jpg").toString()),
        WString(SystemUtil.getProgramIconFile().absolutePath),
        WString(SCRIPT_NAME),
        (558 * scale).toInt(),
        (400 * scale).toInt()
    )

    setLogPath()

    PROGRAM_ARGS = args.toList()

    Application.launch(MainApplication::class.java, *args)
}