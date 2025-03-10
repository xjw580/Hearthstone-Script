package club.xiaojiawei.hsscript.initializer

import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.data.INSTALL_DRIVE_FILE
import club.xiaojiawei.hsscript.data.MOUSE_DRIVE_PATH
import club.xiaojiawei.hsscript.dll.SystemDll
import club.xiaojiawei.hsscript.enums.MouseControlModeEnum
import club.xiaojiawei.hsscript.utils.ConfigExUtil
import club.xiaojiawei.hsscript.utils.SystemUtil
import club.xiaojiawei.util.isFalse
import club.xiaojiawei.util.isTrue
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

/**
 * @author 肖嘉威
 * @date 2023/7/4 11:33
 */
@Suppress("DEPRECATION")
class DriveInitializer : AbstractInitializer() {

    override fun exec() {
        val mouseControlMode = ConfigExUtil.getMouseControlMode()
        if (mouseControlMode === MouseControlModeEnum.DRIVE) {
            install()
        }
    }

    fun install() {
        val driveFile = File(MOUSE_DRIVE_PATH)
        driveFile.exists().isFalse {
            if (SystemDll.INSTANCE.IsRunAsAdministrator()) {
                SystemUtil.getExeFilePath(INSTALL_DRIVE_FILE)?.let {
                    Runtime.getRuntime().exec("$it /install").waitFor()
                    Thread.sleep(1000)
                    if (driveFile.exists()) {
                        val text = "驱动安装成功，需要重启系统"
                        log.info { text }
                        SystemUtil.messageOk(text)
                    } else {
                        val text = "驱动安装失败"
                        log.error { text }
                        SystemUtil.messageError(text)
                    }
                } ?: let {
                    val text = "找不到${INSTALL_DRIVE_FILE}"
                    log.error { text }
                    SystemUtil.messageError(text)
                }
            } else {
                val text = "安装驱动失败，请以管理员权限重新运行"
                log.error { text }
                SystemUtil.messageError(text)
            }
        }
    }

    fun uninstall() {
        val driveFile = File(MOUSE_DRIVE_PATH)
        driveFile.exists().isTrue {
            if (SystemDll.INSTANCE.IsRunAsAdministrator()) {
                SystemUtil.getExeFilePath(INSTALL_DRIVE_FILE)?.let {
                    val process = Runtime.getRuntime().exec("$it /uninstall")
                    val res = StringBuilder()
                    BufferedReader(InputStreamReader(process.inputStream)).use { reader->
                        var line:String?
                        while (true) {
                            line = reader.readLine()
                            line?:break
                            res.append(line)
                        }
                    }
                    Thread.sleep(1000)
                    if (res.toString().contains("Interception uninstalled")) {
                        val text = "驱动卸载成功，需要重启系统"
                        log.info { text }
                        SystemUtil.messageOk(text)
                    } else {
                        val text = "驱动卸载失败"
                        log.error { text }
                        SystemUtil.messageError(text)
                    }
                } ?: let {
                    val text = "找不到${INSTALL_DRIVE_FILE}"
                    log.error { text }
                    SystemUtil.messageError(text)
                }
            } else {
                val text = "卸载驱动失败，请以管理员权限重新运行"
                log.error { text }
                SystemUtil.messageError(text)
            }
        }
    }

}
