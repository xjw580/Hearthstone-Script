package club.xiaojiawei.hsscript.utils.main

import java.io.*
import java.nio.charset.StandardCharsets

/**
 * 将power.log日志中的不相干信息去除，方便查看
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/8/28 15:45
 */
object PowerLogMain {

    @JvmStatic
    fun main(args: Array<String>) {
        decontamination("S:\\Hearthstone\\Logs\\Hearthstone_2024_11_27_17_38_36", true)
    }

    private fun decontamination(path: String, renew: Boolean) {
        if (renew) {
            try {
                RandomAccessFile(File("$path\\Power.log"), "r").use { accessFile ->
                    FileOutputStream("$path\\Power_renew.log").use { outputStream ->
                        var line: String
                        while ((accessFile.readLine().also { line = it }) != null) {
                            if (line.contains("PowerTaskList")) {
                                outputStream.write(
                                    String(
                                        (line.replace("PowerTaskList.Debug", "") + "\n").toByteArray(
                                            StandardCharsets.ISO_8859_1
                                        )
                                    ).toByteArray(StandardCharsets.UTF_8)
                                )
                            }
                        }
                    }
                }
            } catch (e: FileNotFoundException) {
                throw RuntimeException(e)
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        } else {
            try {
                RandomAccessFile(File("$path\\Power.log"), "rw").use { accessFile ->
                    var line: String
                    var leftPoint = accessFile.filePointer
                    var rightPoint: Long
                    while ((accessFile.readLine().also { line = it }) != null) {
                        rightPoint = accessFile.filePointer
                        if (line.contains("PowerTaskList")) {
                            accessFile.seek(leftPoint)
                            accessFile.write(
                                String(
                                    (line.replace("PowerTaskList.Debug", "") + "\n").toByteArray(
                                        StandardCharsets.ISO_8859_1
                                    )
                                ).toByteArray(StandardCharsets.UTF_8)
                            )
                            leftPoint = accessFile.filePointer
                            accessFile.seek(rightPoint)
                        }
                    }
                    if (leftPoint != 0L) {
                        accessFile.setLength(leftPoint)
                    }
                }
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
    }
}
