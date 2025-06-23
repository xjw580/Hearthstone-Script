package club.xiaojiawei.hsscript.utils

import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * @author 肖嘉威
 * @date 2024/9/7 23:49
 */
object CMDUtil {

    data class CommandResult(
        val output: String,
        val exitCode: Int
    )

    fun exec(command: Array<String>): CommandResult {
        val sb = StringBuilder()
        val process = Runtime.getRuntime().exec(command)
//        val process = ProcessBuilder(*command).start()

        // 读取标准输出
        BufferedReader(InputStreamReader(process.inputStream)).use {
            var line: String?
            while ((it.readLine().also { line = it }) != null) {
                sb.append(line).append("\n")
            }
        }

        // 等待进程结束并获取退出码
        val exitCode = process.waitFor()

        return CommandResult(sb.toString(), exitCode)
    }

    fun directExec(command: Array<String>) = Runtime.getRuntime().exec(command)

}
