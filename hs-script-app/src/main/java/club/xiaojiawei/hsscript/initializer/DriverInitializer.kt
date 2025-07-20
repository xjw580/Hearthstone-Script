package club.xiaojiawei.hsscript.initializer

import club.xiaojiawei.hsscriptbase.config.log
import club.xiaojiawei.hsscript.consts.INSTALL_DRIVE_FILE
import club.xiaojiawei.hsscript.consts.MOUSE_DRIVE_PATH
import club.xiaojiawei.hsscript.dll.CSystemDll
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.enums.MouseControlModeEnum
import club.xiaojiawei.hsscript.enums.WindowEnum
import club.xiaojiawei.hsscript.utils.*
import club.xiaojiawei.hsscriptbase.util.isFalse
import club.xiaojiawei.hsscriptbase.util.isTrue
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

/**
 * @author 肖嘉威
 * @date 2023/7/4 11:33
 */

@Suppress("DEPRECATION")
class DriverInitializer : AbstractInitializer() {

    private val driveFile = File(MOUSE_DRIVE_PATH)

    override fun exec() {
        val mouseControlMode = ConfigExUtil.getMouseControlMode()
        if (mouseControlMode === MouseControlModeEnum.DRIVE) {
            ConfigExUtil.storePreventAntiCheat(ConfigUtil.getBoolean(ConfigEnum.PREVENT_AC))
            install(true)
        }
    }

    fun install(silent: Boolean = false) {
        driveFile.exists().isFalse {
            if (CSystemDll.INSTANCE.isRunAsAdministrator()) {
                SystemUtil.getExeFilePath(INSTALL_DRIVE_FILE)?.let {
                    val exec = {
                        val process = Runtime.getRuntime().exec("$it /install")
                        val res = StringBuilder()
                        BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                            var line: String?
                            while (true) {
                                line = reader.readLine()
                                line ?: break
                                res.append(line)
                            }
                        }
                        Thread.sleep(1000)
                        if (res.toString().contains("Interception successfully installed")) {
                            val text = "驱动安装成功，需要重启系统"
                            log.info { text }
                            SystemUtil.messageInfoOk(text)
                        } else {
                            val text = "驱动安装失败"
                            log.error { text }
                            SystemUtil.messageError(text)
                        }
                    }
                    if (silent) {
                        exec()
                    } else {
                        runUI {
                            WindowUtil.createAlert(
                                "${MouseControlModeEnum.DRIVE}模式需要安装驱动（安装时请提前关闭杀毒软件或windows defender）",
                                "是否安装",
                                {
                                    exec()
                                },
                                {
                                },
                                WindowUtil.getStage(WindowEnum.SETTINGS) ?: WindowUtil.getStage(WindowEnum.MAIN),
                                "是",
                                "否"
                            ).show()
                        }
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
        }.isTrue {
            CSystemDll.safeLoadDriver()
        }
    }

    fun uninstall(silent: Boolean = false) {
        CSystemDll.safeReleaseDriver()
        driveFile.exists().isTrue {
            if (CSystemDll.INSTANCE.isRunAsAdministrator()) {
                SystemUtil.getExeFilePath(INSTALL_DRIVE_FILE)?.let {
                    val exec = {
                        val process = Runtime.getRuntime().exec("$it /uninstall")
                        val res = StringBuilder()
                        BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                            var line: String?
                            while (true) {
                                line = reader.readLine()
                                line ?: break
                                res.append(line)
                            }
                        }
                        Thread.sleep(1000)
                        if (res.toString().contains("Interception uninstalled")) {
                            val text = "驱动卸载成功，需要重启系统"
                            log.info { text }
                            SystemUtil.messageInfoOk(text)
                        } else {
                            val text = "驱动卸载失败"
                            log.error { text }
                            SystemUtil.messageError(text)
                        }
                    }
                    if (silent) {
                        exec()
                    }else{
                        runUI {
                            WindowUtil.createAlert(
                                "是否卸载驱动",
                                null,
                                {
                                    exec()
                                },
                                {
                                },
                                WindowUtil.getStage(WindowEnum.SETTINGS) ?: WindowUtil.getStage(WindowEnum.MAIN),
                                "是",
                                "否"
                            ).show()
                        }
                    }
                } ?: let {
                    val text = "找不到${INSTALL_DRIVE_FILE}"
                    log.error { text }
                    SystemUtil.messageError(text)
                }
            }
        }
    }

}
