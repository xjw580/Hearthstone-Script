package club.xiaojiawei.hsscript.utils

import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder

/**
 * @author 肖嘉威
 * @date 2024/9/7 23:49
 */
object CMDUtil {

    fun exec(command: Array<String>): String {
        val sb = StringBuilder()
        val process = Runtime.getRuntime().exec(command)
        return BufferedReader(InputStreamReader(process.inputStream)).use {
            var line: String?
            while ((it.readLine().also { line = it }) != null) {
                sb.append(line).append("\n")
            }
            sb.toString()
        }
    }

}
