package club.xiaojiawei.hsscript

import javafx.application.Application
import java.lang.Exception

/**
 * 启动类
 * @author 肖嘉威
 * @date 2022/11/24 13:34
 */
var PROGRAM_ARGS: List<String> = emptyList()

fun main(args: Array<String>) {
    System.setProperty("jna.library.path", "lib")
    PROGRAM_ARGS = args.toList()

    Application.launch(UIApplication::class.java, *args)
}
